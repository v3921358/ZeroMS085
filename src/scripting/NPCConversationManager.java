/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc>
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package scripting;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import client.inventory.Equip;
import client.ISkill;
import client.inventory.IItem;
import client.MapleCharacter;
import constants.GameConstants;
import client.inventory.ItemFlag;
import client.MapleClient;
import client.MapleJob;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.SkillFactory;
import client.SkillEntry;
import client.MapleStat;
import client.inventory.Item;
import client.inventory.ItemLoader;
import constants.ServerConfig;
import database.DBConPool;
import database.DatabaseConnection;
import server.MapleCarnivalParty;
import server.Randomizer;
import server.MapleInventoryManipulator;
import server.MapleShopFactory;
import server.MapleSquad;
import server.maps.MapleMap;
import server.maps.Event_DojoAgent;
import server.maps.AramiaFireWorks;
import server.quest.MapleQuest;
import tools.MaplePacketCreator;
import tools.Pair;
import tools.packet.PlayerShopPacket;
import server.MapleItemInformationProvider;
import handling.channel.ChannelServer;
import handling.channel.MapleGuildRanking;
import handling.channel.handler.InterServerHandler;
import handling.channel.handler.InventoryHandler;
import handling.login.LoginServer;
import handling.world.MapleParty;
import handling.world.MaplePartyCharacter;
import handling.world.World;
import handling.world.guild.MapleGuild;
import server.MapleCarnivalChallenge;

import java.util.HashMap;

import handling.world.guild.MapleGuildAlliance;

import java.awt.Point;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import javax.script.Invocable;

import server.MaplePortal;
import server.MapleStatEffect;
import server.MerchItemPackage;
import server.SpeedRunner;
import server.maps.SpeedRunType;
import server.StructPotentialItem;
import server.Timer;
import server.Timer.CloneTimer;
import server.gashapon.Gashapon;
import server.gashapon.GashaponFactory;
import server.life.Element;
import server.life.ElementalEffectiveness;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.MapleMonsterInformationProvider;
import server.life.MonsterDropEntry;
import server.maps.Event_PyramidSubway;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.shops.HiredMerchant;
import tools.FilePrinter;
import tools.FileoutputUtil;
import tools.SearchGenerator;
import tools.StringUtil;

public class NPCConversationManager extends AbstractPlayerInteraction {

    private final MapleClient c;
    private final int npc, questid, mode;
    protected String script;
    private String getText;
    private final byte type; // -1 = NPC, 0 = start quest, 1 = end quest
    private byte lastMsg = -1;
    public boolean pendingDisposal = false;
    private final Invocable iv;

    public NPCConversationManager(MapleClient c, int npc, int questid, int mode, String npcscript, byte type, Invocable iv) {
        super(c);
        this.c = c;
        this.npc = npc;
        this.questid = questid;
        this.mode = mode;
        this.type = type;
        this.iv = iv;
        this.script = npcscript;
    }

    public Invocable getIv() {
        return iv;
    }

    public int getMode() {
        return mode;
    }

    public int getNpc() {
        return npc;
    }

    public int getQuest() {
        return questid;
    }

    public String getScript() {
        return script;
    }

    public byte getType() {
        return type;
    }

    public void safeDispose() {
        pendingDisposal = true;
    }

    public void dispose() {
        NPCScriptManager.getInstance().dispose(c);
    }

    public void askMapSelection(final String sel) {
        if (lastMsg > -1) {
            return;
        }
        c.sendPacket(MaplePacketCreator.getMapSelection(npc, sel));
        lastMsg = 0xE;
    }

    public void sendNext(String text) {
        if (lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) { //sendNext will dc otherwise!
            sendSimple(text);
            return;
        }
        c.sendPacket(MaplePacketCreator.getNPCTalk(npc, (byte) 0, text, "00 01", (byte) 0));
        lastMsg = 0;
    }

    public void sendNextS(String text, byte type) {
        if (lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) { // will dc otherwise!
            sendSimpleS(text, type);
            return;
        }
        c.sendPacket(MaplePacketCreator.getNPCTalk(npc, (byte) 0, text, "00 01", type));
        lastMsg = 0;
    }

    public void sendPrev(String text) {
        if (lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) { // will dc otherwise!
            sendSimple(text);
            return;
        }
        c.sendPacket(MaplePacketCreator.getNPCTalk(npc, (byte) 0, text, "01 00", (byte) 0));
        lastMsg = 0;
    }

    public void sendPrevS(String text, byte type) {
        if (lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) { // will dc otherwise!
            sendSimpleS(text, type);
            return;
        }
        c.sendPacket(MaplePacketCreator.getNPCTalk(npc, (byte) 0, text, "01 00", type));
        lastMsg = 0;
    }

    public void sendNextPrev(String text) {
        if (lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) { // will dc otherwise!
            sendSimple(text);
            return;
        }
        c.sendPacket(MaplePacketCreator.getNPCTalk(npc, (byte) 0, text, "01 01", (byte) 0));
        lastMsg = 0;
    }

    public void PlayerToNpc(String text) {
        sendNextPrevS(text, (byte) 3);
    }

    public void sendNextPrevS(String text) {
        sendNextPrevS(text, (byte) 3);
    }

    public void sendNextPrevS(String text, byte type) {
        if (lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) { // will dc otherwise!
            sendSimpleS(text, type);
            return;
        }
        c.sendPacket(MaplePacketCreator.getNPCTalk(npc, (byte) 0, text, "01 01", type));
        lastMsg = 0;

    }

    public void sendOk(String text) {
        if (lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) { // will dc otherwise!
            sendSimple(text);
            return;
        }
        c.sendPacket(MaplePacketCreator.getNPCTalk(npc, (byte) 0, text, "00 00", (byte) 0));
        lastMsg = 0;
    }

    public void sendOkS(String text, byte type) {
        if (lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) { // will dc otherwise!
            sendSimpleS(text, type);
            return;
        }
        c.sendPacket(MaplePacketCreator.getNPCTalk(npc, (byte) 0, text, "00 00", type));
        lastMsg = 0;
    }

    public void sendYesNo(String text) {
        if (lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) { // will dc otherwise!
            sendSimple(text);
            return;
        }
        c.sendPacket(MaplePacketCreator.getNPCTalk(npc, (byte) 2, text, "", (byte) 0));
        lastMsg = 2;
    }

    public void sendYesNoS(String text, byte type) {
        if (lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) { // will dc otherwise!
            sendSimpleS(text, type);
            return;
        }
        c.sendPacket(MaplePacketCreator.getNPCTalk(npc, (byte) 2, text, "", type));
        lastMsg = 2;
    }

    public void sendAcceptDecline(String text) {
        askAcceptDecline(text);
    }

    public void sendAcceptDeclineNoESC(String text) {
        askAcceptDeclineNoESC(text);
    }

    public void askAcceptDecline(String text) {
        if (lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) { // will dc otherwise!
            sendSimple(text);
            return;
        }
        c.sendPacket(MaplePacketCreator.getNPCTalk(npc, (byte) 0x0C, text, "", (byte) 0));
        lastMsg = 0xC;
    }

    public void askAcceptDeclineNoESC(String text) {
        if (lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) { // will dc otherwise!
            sendSimple(text);
            return;
        }
        c.sendPacket(MaplePacketCreator.getNPCTalk(npc, (byte) 0x0E, text, "", (byte) 0));
        lastMsg = 0xE;
    }

    public void askAvatar(String text, int... args) {
        if (lastMsg > -1) {
            return;
        }
        c.sendPacket(MaplePacketCreator.getNPCTalkStyle(npc, text, args));
        lastMsg = 8;
    }

    public void sendSimple(String text) {
        if (lastMsg > -1) {
            return;
        }
        if (!text.contains("#L")) { //sendSimple will dc otherwise!
            sendNext(text);
            return;
        }
        c.sendPacket(MaplePacketCreator.getNPCTalk(npc, (byte) 5, text, "", (byte) 0));
        lastMsg = 5;
    }

    public void sendSimpleS(String text, byte type) {
        if (lastMsg > -1) {
            return;
        }
        if (!text.contains("#L")) { //sendSimple will dc otherwise!
            sendNextS(text, type);
            return;
        }
        c.sendPacket(MaplePacketCreator.getNPCTalk(npc, (byte) 5, text, "", (byte) type));
        lastMsg = 5;
    }

    public void sendStyle(String text, int styles[]) {
        if (lastMsg > -1) {
            return;
        }
        c.sendPacket(MaplePacketCreator.getNPCTalkStyle(npc, text, styles));
        lastMsg = 8;
    }

    public void sendGetNumber(String text, int def, int min, int max) {
        if (lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) { // will dc otherwise!
            sendSimple(text);
            return;
        }
        c.sendPacket(MaplePacketCreator.getNPCTalkNum(npc, text, def, min, max));
        lastMsg = 4;
    }

    public void sendGetText(String text) {
        if (lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) { // will dc otherwise!
            sendSimple(text);
            return;
        }
        c.sendPacket(MaplePacketCreator.getNPCTalkText(npc, text));
        lastMsg = 3;
    }

    public void setGetText(String text) {
        this.getText = text;
    }

    public String getText() {
        return getText;
    }

    public void setHair(int hair) {
        getPlayer().setHair(hair);
        getPlayer().updateSingleStat(MapleStat.HAIR, hair);
        getPlayer().equipChanged();
    }

    public void setFace(int face) {
        getPlayer().setFace(face);
        getPlayer().updateSingleStat(MapleStat.FACE, face);
        getPlayer().equipChanged();
    }

    public void setSkin(int color) {
        getPlayer().setSkinColor((byte) color);
        getPlayer().updateSingleStat(MapleStat.SKIN, color);
        getPlayer().equipChanged();
    }

    public int setRandomAvatar(int ticket, int... args_all) {
        if (!haveItem(ticket)) {
            return -1;
        }
        gainItem(ticket, (short) -1);

        int args = args_all[Randomizer.nextInt(args_all.length)];
        if (args < 100) {
            c.getPlayer().setSkinColor((byte) args);
            c.getPlayer().updateSingleStat(MapleStat.SKIN, args);
        } else if (args < 30000) {
            c.getPlayer().setFace(args);
            c.getPlayer().updateSingleStat(MapleStat.FACE, args);
        } else {
            c.getPlayer().setHair(args);
            c.getPlayer().updateSingleStat(MapleStat.HAIR, args);
        }
        c.getPlayer().equipChanged();

        return 1;
    }

    public int setAvatar(int ticket, int args) {
        if (!haveItem(ticket)) {
            return -1;
        }
        gainItem(ticket, (short) -1);

        if (args < 100) {
            c.getPlayer().setSkinColor((byte) args);
            c.getPlayer().updateSingleStat(MapleStat.SKIN, args);
        } else if (args < 30000) {
            c.getPlayer().setFace(args);
            c.getPlayer().updateSingleStat(MapleStat.FACE, args);
        } else {
            c.getPlayer().setHair(args);
            c.getPlayer().updateSingleStat(MapleStat.HAIR, args);
        }
        c.getPlayer().equipChanged();

        return 1;
    }

    public void sendStorage() {
        if (getPlayer().hasBlockedInventory2(true)) { //hack
            c.getPlayer().dropMessage(1, "系统错误，请联系管理。");
            c.sendPacket(MaplePacketCreator.enableActions());
            return;
        }

        if (!World.isShutDown) {
            if (!World.isShopShutDown) {
                c.getPlayer().setConversation(4);
                c.getPlayer().getStorage().sendStorage(c, npc);
            } else {
                c.getPlayer().dropMessage(1, "目前不能使用仓库。");
                c.sendPacket(MaplePacketCreator.enableActions());
            }
        } else {
            c.getPlayer().dropMessage(1, "目前不能使用仓库。");
            c.sendPacket(MaplePacketCreator.enableActions());
        }
    }

    public void openShop(int id) {
        MapleShopFactory.getInstance().getShop(id).sendShop(c);
    }

    public void openShopNPC(int shopid) {
        MapleShopFactory.getInstance().getShop(shopid).sendShop(c, npc);
    }

    public int gainGachaponItem(int id, int quantity) {
        return gainGachaponItem(id, quantity, c.getPlayer().getMap().getStreetName() + " - " + c.getPlayer().getMap().getMapName());
    }

    public int gainGachaponItem(int id, int quantity, final String msg) {
        try {
            if (!MapleItemInformationProvider.getInstance().itemExists(id)) {
                return -1;
            }
            final IItem item = MapleInventoryManipulator.addbyId_Gachapon(c, id, (short) quantity);

            if (item == null) {
                return -1;
            }
            if (c.getPlayer().getMapId() == 741000209) {
                World.Broadcast.broadcastMessage(MaplePacketCreator.getGachaponMega("[自由抽奖]", " : 被〖" + c.getPlayer().getName() + "〗,抽到,吓的露西德才吃一口的苹果都掉了", item, (byte) 1));
            } else if (c.getPlayer().getMapId() == 910000007) {
                World.Broadcast.broadcastMessage(MaplePacketCreator.getGachaponMega("[仙境抽奖]", " : 被〖" + c.getPlayer().getName() + "〗,抽到,大家恭喜Ta", item, (byte) 1));


            } else {
                World.Broadcast.broadcastMessage(MaplePacketCreator.getGachaponMega("[抽奖机]", " : 被〖 " + c.getPlayer().getName() + " 〗,抽到,吓的露西德才吃一口的苹果都掉了 ", item, (byte) 1));
            }
            return item.getItemId();
        } catch (Exception e) {
        }
        return -1;
    }

    public int gainGachaponItemTime(int id, int quantity, long period) {
        return gainGachaponItemTime(id, quantity, c.getPlayer().getMap().getStreetName() + " - " + c.getPlayer().getMap().getMapName(), period);
    }

    public int gainGachaponItemTime(int id, int quantity, final String msg, long period) {
        try {
            final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            if (!ii.itemExists(id)) {
                return -1;
            }
            final IItem item = ii.isCash(id) ? MapleInventoryManipulator.addbyId_GachaponTime(c, id, (short) quantity, period) : MapleInventoryManipulator.addbyId_Gachapon(c, id, (short) quantity);

            if (item == null) {
                return -1;
            }
            final byte rareness = GameConstants.gachaponRareItem(item.getItemId());
            if (rareness == 1) {
                if (c.getPlayer().getMapId() == 741000209) {
                    World.Broadcast.broadcastMessage(MaplePacketCreator.getGachaponMega("[转蛋机]", " : 被玩家 〖 " + c.getPlayer().getName() + " 〗,抽到,吓得露西德才吃一口的苹果都掉了 ", item, rareness));
                } else {
                    World.Broadcast.broadcastMessage(MaplePacketCreator.getGachaponMega("[转蛋机]", " : 被玩家 〖 " + c.getPlayer().getName() + " 〗,抽到,吓得露西德才吃一口的苹果都掉了 ", item, rareness));
                }
//World.Broadcast.broadcastMessage(MaplePacketCreator.getGachaponMega("[" + msg + "] " + c.getPlayer().getName(), " : 被他抽到了，大家恭喜他吧！", item, rareness));
            } else if (rareness == 2) {
                World.Broadcast.broadcastMessage(MaplePacketCreator.getGachaponMega("[" + msg + "] " + c.getPlayer().getName(), " : 被他成功转到了，大家恭喜他吧！", item, rareness));
            } else if (rareness > 2) {
                World.Broadcast.broadcastMessage(MaplePacketCreator.getGachaponMega("[" + msg + "] " + c.getPlayer().getName(), " : 被他从枫叶转蛋机转到了，大家恭喜他吧！", item, rareness));
            }
            return item.getItemId();
        } catch (Exception e) {
        }
        return -1;
    }

    public void changeJob(int job) {
        c.getPlayer().changeJob(job);
    }

    public void startQuest(int id) {
        MapleQuest.getInstance(id).start(getPlayer(), npc);
    }

    public void completeQuest() {
        forceCompleteQuest();
    }

    public void completeQuest(int id) {
        MapleQuest.getInstance(id).complete(getPlayer(), npc);
    }

    public void forfeitQuest(int id) {
        MapleQuest.getInstance(id).forfeit(getPlayer());
    }

    public void forceStartQuest() {
        MapleQuest.getInstance(questid).forceStart(getPlayer(), getNpc(), null);
    }

    @Override
    public void forceStartQuest(int id) {
        MapleQuest.getInstance(id).forceStart(getPlayer(), getNpc(), null);
    }

    public void forceStartQuest(String customData) {
        MapleQuest.getInstance(questid).forceStart(getPlayer(), getNpc(), customData);
    }

    public void forceCompleteQuest() {
        MapleQuest.getInstance(questid).forceComplete(getPlayer(), getNpc());
    }

    @Override
    public void forceCompleteQuest(final int id) {
        MapleQuest.getInstance(id).forceComplete(getPlayer(), getNpc());
    }

    public String getQuestCustomData() {
        return c.getPlayer().getQuestNAdd(MapleQuest.getInstance(questid)).getCustomData();
    }

    public void setQuestCustomData(String customData) {
        getPlayer().getQuestNAdd(MapleQuest.getInstance(questid)).setCustomData(customData);
    }

    public int getMeso() {
        return getPlayer().getMeso();
    }

    public void gainAp(final int amount) {
        c.getPlayer().gainAp((short) amount);
    }

    public void expandInventory(byte type, int amt) {
        c.getPlayer().expandInventory(type, amt);
    }

    public void unequipEverything() {
        MapleInventory equipped = getPlayer().getInventory(MapleInventoryType.EQUIPPED);
        MapleInventory equip = getPlayer().getInventory(MapleInventoryType.EQUIP);
        List<Short> ids = new LinkedList<>();
        for (IItem item : equipped.list()) {
            ids.add(item.getPosition());
        }
        for (short id : ids) {
            MapleInventoryManipulator.unequip(getC(), id, equip.getNextFreeSlot());
        }
    }

    public final void clearSkills() {
        Map<ISkill, SkillEntry> skills = getPlayer().getSkills();
        for (Entry<ISkill, SkillEntry> skill : skills.entrySet()) {
            getPlayer().changeSkillLevel(skill.getKey(), (byte) 0, (byte) 0);
        }
    }

    public boolean hasSkill(int skillid) {
        ISkill theSkill = SkillFactory.getSkill(skillid);
        if (theSkill != null) {
            return c.getPlayer().getSkillLevel(theSkill) > 0;
        }
        return false;
    }

    public void showEffect(boolean broadcast, String effect) {
        if (broadcast) {
            c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.showEffect(effect));
        } else {
            c.sendPacket(MaplePacketCreator.showEffect(effect));
        }
    }

    public void playSound(boolean broadcast, String sound) {
        if (broadcast) {
            c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.playSound(sound));
        } else {
            c.sendPacket(MaplePacketCreator.playSound(sound));
        }
    }

    public void environmentChange(boolean broadcast, String env) {
        if (broadcast) {
            c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.environmentChange(env, 2));
        } else {
            c.sendPacket(MaplePacketCreator.environmentChange(env, 2));
        }
    }

    public void updateBuddyCapacity(int capacity) {
        c.getPlayer().setBuddyCapacity((byte) capacity);
    }

    public int getBuddyCapacity() {
        return c.getPlayer().getBuddyCapacity();
    }

    public int partyMembersInMap() {
        int inMap = 0;
        for (MapleCharacter char2 : getPlayer().getMap().getCharactersThreadsafe()) {
            if (char2.getParty() == getPlayer().getParty()) {
                inMap++;
            }
        }
        return inMap;
    }

    public List<MapleCharacter> getPartyMembers() {
        if (getPlayer().getParty() == null) {
            return null;
        }
        List<MapleCharacter> chars = new LinkedList<>(); // creates an empty array full of shit..
        for (MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            for (ChannelServer channel : ChannelServer.getAllInstances()) {
                MapleCharacter ch = channel.getPlayerStorage().getCharacterById(chr.getId());
                if (ch != null) { // double check <3
                    chars.add(ch);
                }
            }
        }
        return chars;
    }

    public void warpPartyWithExp(int mapId, int exp) {
        MapleMap target = getMap(mapId);
        for (MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            MapleCharacter curChar = c.getChannelServer().getPlayerStorage().getCharacterByName(chr.getName());
            if ((curChar.getEventInstance() == null && getPlayer().getEventInstance() == null) || curChar.getEventInstance() == getPlayer().getEventInstance()) {
                curChar.changeMap(target, target.getPortal(0));
                curChar.gainExp(exp, true, false, true);
            }
        }
    }

    public void warpPartyWithExpMeso(int mapId, int exp, int meso) {
        MapleMap target = getMap(mapId);
        for (MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            MapleCharacter curChar = c.getChannelServer().getPlayerStorage().getCharacterByName(chr.getName());
            if ((curChar.getEventInstance() == null && getPlayer().getEventInstance() == null) || curChar.getEventInstance() == getPlayer().getEventInstance()) {
                curChar.changeMap(target, target.getPortal(0));
                curChar.gainExp(exp, true, false, true);
                curChar.gainMeso(meso, true);
            }
        }
    }

    public MapleSquad getSquad(String type) {
        return c.getChannelServer().getMapleSquad(type);
    }

    public int getSquadAvailability(String type) {
        final MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        if (squad == null) {
            return -1;
        }
        return squad.getStatus();
    }

    public boolean registerSquad(String type, int minutes, String startText) {
        if (c.getChannelServer().getMapleSquad(type) == null) {
            final MapleSquad squad = new MapleSquad(c.getChannel(), type, c.getPlayer(), minutes * 60 * 1000, startText);
            final boolean ret = c.getChannelServer().addMapleSquad(squad, type);
            if (ret) {
                final MapleMap map = c.getPlayer().getMap();

                map.broadcastMessage(MaplePacketCreator.getClock(minutes * 60));
                map.broadcastMessage(MaplePacketCreator.serverNotice(6, c.getPlayer().getName() + startText));
            } else {
                squad.clear();
            }
            return ret;
        }
        return false;
    }

    public boolean getSquadList(String type, byte type_) {
        final MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        if (squad == null) {
            return false;
        }
        if (type_ == 0 || type_ == 3) { // Normal viewing
            sendNext(squad.getSquadMemberString(type_));
        } else if (type_ == 1) { // Squad Leader banning, Check out banned participant
            sendSimple(squad.getSquadMemberString(type_));
        } else if (type_ == 2) {
            if (squad.getBannedMemberSize() > 0) {
                sendSimple(squad.getSquadMemberString(type_));
            } else {
                sendNext(squad.getSquadMemberString(type_));
            }
        }
        return true;

    }

    public byte isSquadLeader(String type) {
        final MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        if (squad == null) {
            return -1;
        } else if (squad.getLeader() != null && squad.getLeader().getId() == c.getPlayer().getId()) {
            return 1;
        } else {
            return 0;
        }
    }

    public boolean reAdd(String eim, String squad) {
        EventInstanceManager eimz = getDisconnected(eim);
        MapleSquad squadz = getSquad(squad);
        if (eimz != null && squadz != null) {
            squadz.reAddMember(getPlayer());
            eimz.registerPlayer(getPlayer());
            return true;
        }
        return false;
    }

    public void banMember(String type, int pos) {
        final MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        if (squad != null) {
            squad.banMember(pos);
        }
    }

    public void acceptMember(String type, int pos) {
        final MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        if (squad != null) {
            squad.acceptMember(pos);
        }
    }

    public String getReadableMillis(long startMillis, long endMillis) {
        return StringUtil.getReadableMillis(startMillis, endMillis);
    }

    public int addMember(String type, boolean join) {
        final MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        if (squad != null) {
            return squad.addMember(c.getPlayer(), join);
        }
        return -1;
    }

    public byte isSquadMember(String type) {
        final MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        if (squad == null) {
            return -1;
        } else if (squad.getMembers().contains(c.getPlayer().getName())) {
            return 1;
        } else if (squad.isBanned(c.getPlayer())) {
            return 2;
        } else {
            return 0;
        }
    }

    public void resetReactors() {
        getPlayer().getMap().resetReactors();
    }

    public void genericGuildMessage(int code) {
        c.sendPacket(MaplePacketCreator.genericGuildMessage((byte) code));
    }

    public void disbandGuild() {
        final int gid = c.getPlayer().getGuildId();
        if (gid <= 0 || c.getPlayer().getGuildRank() != 1) {
            return;
        }
        World.Guild.disbandGuild(gid);
    }

    public void increaseGuildCapacity() {
        if (c.getPlayer().getMeso() < 250000) {
            c.sendPacket(MaplePacketCreator.serverNotice(1, "You do not have enough mesos."));
            return;
        }
        final int gid = c.getPlayer().getGuildId();
        if (gid <= 0) {
            return;
        }
        World.Guild.increaseGuildCapacity(gid);
        c.getPlayer().gainMeso(-250000, true, false, true);
    }

    /*
     * 创建家族需要的金币
     */
    public int getCreateGuildCost() {
        return c.getChannelServer().getCreateGuildCost();
    }

    public void displayGuildRanks() {
        c.sendPacket(MaplePacketCreator.showGuildRanks(npc, MapleGuildRanking.getInstance().getGuildRank()));
    }

    public void showlvl() {
        c.sendPacket(MaplePacketCreator.showlevelRanks(npc, MapleGuildRanking.getInstance().getLevelRank()));
    }

    public void showmeso() {
        c.sendPacket(MaplePacketCreator.showmesoRanks(npc, MapleGuildRanking.getInstance().getMesoRank()));
    }

    public boolean removePlayerFromInstance() {
        if (c.getPlayer().getEventInstance() != null) {
            c.getPlayer().getEventInstance().removePlayer(c.getPlayer());
            return true;
        }
        return false;
    }

    public boolean isPlayerInstance() {
        return c.getPlayer().getEventInstance() != null;
    }

    public void changeStat(byte slot, int type, short amount) {
        Equip sel = (Equip) c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(slot);
        switch (type) {
            case 0:
                sel.setStr(amount);
                break;
            case 1:
                sel.setDex(amount);
                break;
            case 2:
                sel.setInt(amount);
                break;
            case 3:
                sel.setLuk(amount);
                break;
            case 4:
                sel.setHp(amount);
                break;
            case 5:
                sel.setMp(amount);
                break;
            case 6:
                sel.setWatk(amount);
                break;
            case 7:
                sel.setMatk(amount);
                break;
            case 8:
                sel.setWdef(amount);
                break;
            case 9:
                sel.setMdef(amount);
                break;
            case 10:
                sel.setAcc(amount);
                break;
            case 11:
                sel.setAvoid(amount);
                break;
            case 12:
                sel.setHands(amount);
                break;
            case 13:
                sel.setSpeed(amount);
                break;
            case 14:
                sel.setJump(amount);
                break;
            case 15:
                sel.setUpgradeSlots((byte) amount);
                break;
            case 16:
                sel.setViciousHammer((byte) amount);
                break;
            case 17:
                sel.setLevel((byte) amount);
                break;
            case 18:
                sel.setEnhance((byte) amount);
                break;
            case 19:
                sel.setPotential1(amount);
                break;
            case 20:
                sel.setPotential2(amount);
                break;
            case 21:
                sel.setPotential3(amount);
                break;
            case 22:
                sel.setOwner(getText());
                break;
            default:
                break;
        }
        c.getPlayer().equipChanged();
    }

    public void cleardrops() {
        MapleMonsterInformationProvider.getInstance().clearDrops();
    }

    public void killAllMonsters() {
        MapleMap map = c.getPlayer().getMap();
        double range = Double.POSITIVE_INFINITY;
        MapleMonster mob;
        for (MapleMapObject monstermo : map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER))) {
            mob = (MapleMonster) monstermo;
            if (mob.getStats().isBoss()) {
                map.killMonster(mob, c.getPlayer(), false, false, (byte) 1);
            }
        }
/*int mapid = c.getPlayer().getMapId();
MapleMap map = c.getChannelServer().getMapFactory().getMap(mapid);
map.killAllMonsters(true); // No drop. */
    }

    public void giveMerchantMesos() {
        long mesos = 0;
        try (Connection con = DBConPool.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = (PreparedStatement) con.prepareStatement("SELECT mesos FROM hiredmerchants WHERE merchantid = ?");
            ps.setInt(1, getPlayer().getId());
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
            } else {
                mesos = rs.getLong("mesos");
            }
            rs.close();
            ps.close();

            ps = (PreparedStatement) con.prepareStatement("UPDATE hiredmerchants SET mesos = 0 WHERE merchantid = ?");
            ps.setInt(1, getPlayer().getId());
            ps.executeUpdate();
            ps.close();

        } catch (SQLException ex) {
            System.err.println("Error gaining mesos in hired merchant" + ex);
            FileoutputUtil.outError("logs/資料庫異常.txt", ex);
        }
        c.getPlayer().gainMeso((int) mesos, true);
    }

    public void dc() {
        MapleCharacter victim = getChannelServer().getPlayerStorage().getCharacterByName(getPlayer().getName());
        victim.getClient().getSession().close();
        victim.getClient().disconnect(true, false);
    }

    public long getMerchantMesos() {
        long mesos = 0;
        try (Connection con = DBConPool.getInstance().getDataSource().getConnection(); PreparedStatement ps = (PreparedStatement) con.prepareStatement("SELECT mesos FROM hiredmerchants WHERE merchantid = ?")) {
            ps.setInt(1, getPlayer().getId());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    rs.close();
                    ps.close();
                } else {
                    mesos = rs.getLong("mesos");
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error gaining mesos in hired merchant" + ex);
            FileoutputUtil.outError("logs/資料庫異常.txt", ex);
        }
        return mesos;
    }

    public void openDuey() {
        c.getPlayer().setConversation(2);
        c.sendPacket(MaplePacketCreator.sendDuey((byte) 9, null));
    }

    public void openMerchantItemStore() {
        if (!World.isShutDown) {
            c.getPlayer().setConversation(3);
            c.sendPacket(PlayerShopPacket.merchItemStore((byte) 0x22));
        } else {
            c.getPlayer().dropMessage(1, "目前不能使用精灵商人领取。");
            c.sendPacket(MaplePacketCreator.enableActions());
        }
    }

    public static String SN取出售(int id) {
        String data = "";
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT Point as DATA FROM character7 WHERE Name = ? && channel = 1");
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data = rs.getString("DATA");
                }
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("获取角色ID取名字出错 - 数据库查询失败：" + Ex);
        }
        if (data == null) {
            data = "匿名人士";
        }
        return data;
    }

    public static String SN取库存(int id) {
        String data = "";
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT Point as DATA FROM character7 WHERE Name = ? &&  channel = 2");
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data = rs.getString("DATA");
                }
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("获取角色ID取名字出错 - 数据库查询失败：" + Ex);
        }
        if (data == null) {
            data = "匿名人士";
        }
        return data;
    }

    public static String SN取折扣(int id) {
        String data = "";
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT Point as DATA FROM character7 WHERE Name = ? &&  channel = 3");
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data = rs.getString("DATA");
                }
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("获取角色ID取名字出错 - 数据库查询失败：" + Ex);
        }
        if (data == null) {
            data = "匿名人士";
        }
        return data;
    }

    public static String SN取限购(int id) {
        String data = "";
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT Point as DATA FROM character7 WHERE Name = ? &&  channel = 4");
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data = rs.getString("DATA");
                }
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("获取角色ID取名字出错 - 数据库查询失败：" + Ex);
        }
        if (data == null) {
            data = "匿名人士";
        }
        return data;
    }

    public static String SN取类型(int id) {
        String data = "";
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT Point as DATA FROM character7 WHERE Name = ? &&  channel = 5");
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data = rs.getString("DATA");
                }
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("获取角色ID取名字出错 - 数据库查询失败：" + Ex);
        }
        if (data == null) {
            data = "匿名人士";
        }
        return data;
    }

    public static int 角色名字取ID(String id) {
        int data = 0;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT id as DATA FROM characters WHERE name = ?");
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data = rs.getInt("DATA");
                }
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("获取角色名字取ID出错 - 数据库查询失败：" + Ex);
        }

        return data;
    }

    public static String 角色ID取名字(int id) {
        String data = "";
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT name as DATA FROM characters WHERE id = ?");
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data = rs.getString("DATA");
                }
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("获取角色ID取名字出错 - 数据库查询失败：" + Ex);
        }
        if (data == null) {
            data = "匿名人士";
        }
        return data;
    }

    public static int 角色名字取账号ID(String id) {
        int data = 0;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT accountid as DATA FROM characters WHERE name = ?");
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data = rs.getInt("DATA");
                }
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("获取角色名字取ID出错 - 数据库查询失败：" + Ex);
        }
        return data;
    }

    public static String IP取账号(String id) {
        String data = "";
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT name as DATA FROM accounts WHERE SessionIP = ?");
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    data = rs.getString("DATA");
                    return data;
                }
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("获取角色ID取名字出错 - 数据库查询失败：" + Ex);
        }
        if (data == null) {
            data = "匿名人士";
        }
        return data;
    }

    public static String MAC取账号(String id) {
        String data = "";
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT name as DATA FROM accounts WHERE macs = ?");
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    data = rs.getString("DATA");
                }
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("获取角色ID取名字出错 - 数据库查询失败：" + Ex);
        }
        if (data == null) {
            data = "匿名人士";
        }
        return data;
    }

    public static String 账号ID取账号(String id) {
        String data = "";
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT name as DATA FROM accounts WHERE id = ?");
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data = rs.getString("DATA");
                }
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("获取角色名字取ID出错 - 数据库查询失败：" + Ex);
        }
        return data;
    }

    public static String 账号ID取在线(int id) {
        String data = "";
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT loggedin as DATA FROM accounts WHERE id = ?");
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data = rs.getString("DATA");
                }
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("获取角色名字取ID出错 - 数据库查询失败：" + Ex);
        }
        return data;
    }

    public static String 角色名字取等级(String id) {
        String data = "";
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT level as DATA FROM characters WHERE name = ?");
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data = rs.getString("DATA");
                }
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("获取角色名字取ID出错 - 数据库查询失败：" + Ex);
        }
        if (data == null) {
            data = "匿名人士";
        }
        return data;
    }

    public static String 物品获取掉落怪物(int itemid) {
        String data = "";
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT dropperid as DATA FROM drop_data WHERE itemid = ?");
            ps.setInt(1, itemid);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    data = rs.getString("DATA");
                }
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("获取物品获取掉落怪物出错 - 数据库查询失败：" + Ex);
        }
        return data;
    }

    public void 对话结束() {
        NPCScriptManager.getInstance().dispose(c);
    }

    public void 说明文字(String text) {
        if (lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) { // will dc otherwise!
            sendSimple(text);
            return;
        }
        c.sendPacket(MaplePacketCreator.getNPCTalk(npc, (byte) 0, text, "00 00", (byte) 0));
        lastMsg = 0;
    }

    public int 随机数(int a) {
        return (int) Math.ceil(Math.random() * a);
    }


    private static MerchItemPackage loadItemFrom_Database(int charid, int accountid) {
        Connection con = DatabaseConnection.getConnection();

        try {
            PreparedStatement ps = con.prepareStatement("SELECT * from hiredmerch where characterid = ? OR accountid = ?");
            ps.setInt(1, charid);
            ps.setInt(2, accountid);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                ps.close();
                rs.close();
                return null;
            }
            int packageid = rs.getInt("PackageId");

            MerchItemPackage pack = new MerchItemPackage();
            pack.setPackageid(packageid);
            pack.setMesos(rs.getInt("Mesos"));
            pack.setSentTime(rs.getLong("time"));

            ps.close();
            rs.close();

            Map<Integer, Pair<IItem, MapleInventoryType>> items = ItemLoader.HIRED_MERCHANT.loadItems_hm(packageid, accountid);
            if (items != null) {
                List<IItem> iters = new ArrayList<>();
                for (Pair<IItem, MapleInventoryType> z : items.values()) {
                    iters.add(z.left);
                }
                pack.setItems(iters);
            }

            return pack;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    public void sendRepairWindow() {
        c.sendPacket(MaplePacketCreator.sendRepairWindow(npc));
    }

    public final int getDojoPoints() {
        return c.getPlayer().getDojo();
    }

    public void setDojoPoints(int point) {
        c.getPlayer().setDojo(c.getPlayer().getDojo() + point);
    }

    public final int getDojoRecord() {
        return c.getPlayer().getDojoRecord();
    }

    public void setDojoRecord(final boolean reset) {
        c.getPlayer().setDojoRecord(reset);
    }

    public boolean start_DojoAgent(final boolean dojo, final boolean party) {
        if (dojo) {
            return Event_DojoAgent.warpStartDojo(c.getPlayer(), party);
        }
        return Event_DojoAgent.warpStartAgent(c.getPlayer(), party);
    }

    public boolean start_PyramidSubway(final int pyramid) {
        if (pyramid >= 0) {
            return Event_PyramidSubway.warpStartPyramid(c.getPlayer(), pyramid);
        }
        return Event_PyramidSubway.warpStartSubway(c.getPlayer());
    }

    public boolean bonus_PyramidSubway(final int pyramid) {
        if (pyramid >= 0) {
            return Event_PyramidSubway.warpBonusPyramid(c.getPlayer(), pyramid);
        }
        return Event_PyramidSubway.warpBonusSubway(c.getPlayer());
    }

    public final short getKegs() {
        return AramiaFireWorks.getInstance().getKegsPercentage();
    }

    public void giveKegs(final int kegs) {
        AramiaFireWorks.getInstance().giveKegs(c.getPlayer(), kegs);
    }

    public final short getSunshines() {
        return AramiaFireWorks.getInstance().getSunsPercentage();
    }

    public final short getSunshines2() {
        return AramiaFireWorks.getInstance().getSunsPercentage2();
    }

    public void addSunshines(final int kegs) {
        AramiaFireWorks.getInstance().giveSuns(c.getPlayer(), kegs);
    }

    public final short getDecorations() {
        return AramiaFireWorks.getInstance().getDecsPercentage();
    }

    public void addDecorations(final int kegs) {
        try {
            AramiaFireWorks.getInstance().giveDecs(c.getPlayer(), kegs);
        } catch (Exception e) {
        }
    }

    public final MapleInventory getInventory(int type) {
        return c.getPlayer().getInventory(MapleInventoryType.getByType((byte) type));
    }

    public final MapleCarnivalParty getCarnivalParty() {
        return c.getPlayer().getCarnivalParty();
    }

    public final MapleCarnivalChallenge getNextCarnivalRequest() {
        return c.getPlayer().getNextCarnivalRequest();
    }

    public final MapleCarnivalChallenge getCarnivalChallenge(MapleCharacter chr) {
        return new MapleCarnivalChallenge(chr);
    }

    public void maxStats() {
        Map<MapleStat, Integer> statup = new EnumMap<>(MapleStat.class);
        c.getPlayer().getStat().setStr((short) 32767);
        c.getPlayer().getStat().setDex((short) 32767);
        c.getPlayer().getStat().setInt((short) 32767);
        c.getPlayer().getStat().setLuk((short) 32767);

        c.getPlayer().getStat().setMaxHp((short) 30000);
        c.getPlayer().getStat().setMaxMp((short) 30000);
        c.getPlayer().getStat().setHp((short) 30000);
        c.getPlayer().getStat().setMp((short) 30000);

        statup.put(MapleStat.STR, 32767);
        statup.put(MapleStat.DEX, 32767);
        statup.put(MapleStat.LUK, 32767);
        statup.put(MapleStat.INT, 32767);
        statup.put(MapleStat.HP, 30000);
        statup.put(MapleStat.MAXHP, 30000);
        statup.put(MapleStat.MP, 30000);
        statup.put(MapleStat.MAXMP, 30000);

        c.sendPacket(MaplePacketCreator.updatePlayerStats(statup, c.getPlayer()));
    }

    public Pair<String, Map<Integer, String>> getSpeedRun(String typ) {
        final SpeedRunType stype = SpeedRunType.valueOf(typ);
        if (SpeedRunner.getInstance().getSpeedRunData(stype) != null) {
            return SpeedRunner.getInstance().getSpeedRunData(stype);
        }
        return new Pair<>("", new HashMap<>());
    }

    public boolean getSR(Pair<String, Map<Integer, String>> ma, int sel) {
        if (ma.getRight().get(sel) == null || ma.getRight().get(sel).length() <= 0) {
            dispose();
            return false;
        }
        sendOk(ma.getRight().get(sel));
        return true;
    }

    public Equip getEquip(int itemid) {
        return (Equip) MapleItemInformationProvider.getInstance().getEquipById(itemid);
    }

    public void setExpiration(Object statsSel, long expire) {
        if (statsSel instanceof Equip) {
            ((Equip) statsSel).setExpiration(System.currentTimeMillis() + (expire * 24 * 60 * 60 * 1000));
        }
    }

    public void setLock(Object statsSel) {
        if (statsSel instanceof Equip) {
            Equip eq = (Equip) statsSel;
            if (eq.getExpiration() == -1) {
                eq.setFlag((byte) (eq.getFlag() | ItemFlag.LOCK.getValue()));
            } else {
                eq.setFlag((byte) (eq.getFlag() | ItemFlag.UNTRADEABLE.getValue()));
            }
        }
    }

    public boolean addFromDrop(Object statsSel) {
        if (statsSel instanceof IItem) {
            final IItem it = (IItem) statsSel;
            return MapleInventoryManipulator.checkSpace(getClient(), it.getItemId(), it.getQuantity(), it.getOwner()) && MapleInventoryManipulator.addFromDrop(getClient(), it, false);
        }
        return false;
    }

    public boolean replaceItem(int slot, int invType, Object statsSel, int offset, String type) {
        return replaceItem(slot, invType, statsSel, offset, type, false);
    }

    public boolean replaceItem(int slot, int invType, Object statsSel, int offset, String type, boolean takeSlot) {
        MapleInventoryType inv = MapleInventoryType.getByType((byte) invType);
        if (inv == null) {
            return false;
        }
        IItem item = getPlayer().getInventory(inv).getItem((byte) slot);
        if (item == null || statsSel instanceof IItem) {
            item = (IItem) statsSel;
        }
        if (offset > 0) {
            if (inv != MapleInventoryType.EQUIP) {
                return false;
            }
            Equip eq = (Equip) item;
            if (takeSlot) {
                if (eq.getUpgradeSlots() < 1) {
                    return false;
                } else {
                    eq.setUpgradeSlots((byte) (eq.getUpgradeSlots() - 1));
                }
            }
            if (type.equalsIgnoreCase("Slots")) {
                eq.setUpgradeSlots((byte) (eq.getUpgradeSlots() + offset));
            } else if (type.equalsIgnoreCase("Level")) {
                eq.setLevel((byte) (eq.getLevel() + offset));
            } else if (type.equalsIgnoreCase("Hammer")) {
                eq.setViciousHammer((byte) (eq.getViciousHammer() + offset));
            } else if (type.equalsIgnoreCase("STR")) {
                eq.setStr((short) (eq.getStr() + offset));
            } else if (type.equalsIgnoreCase("DEX")) {
                eq.setDex((short) (eq.getDex() + offset));
            } else if (type.equalsIgnoreCase("INT")) {
                eq.setInt((short) (eq.getInt() + offset));
            } else if (type.equalsIgnoreCase("LUK")) {
                eq.setLuk((short) (eq.getLuk() + offset));
            } else if (type.equalsIgnoreCase("HP")) {
                eq.setHp((short) (eq.getHp() + offset));
            } else if (type.equalsIgnoreCase("MP")) {
                eq.setMp((short) (eq.getMp() + offset));
            } else if (type.equalsIgnoreCase("WATK")) {
                eq.setWatk((short) (eq.getWatk() + offset));
            } else if (type.equalsIgnoreCase("MATK")) {
                eq.setMatk((short) (eq.getMatk() + offset));
            } else if (type.equalsIgnoreCase("WDEF")) {
                eq.setWdef((short) (eq.getWdef() + offset));
            } else if (type.equalsIgnoreCase("MDEF")) {
                eq.setMdef((short) (eq.getMdef() + offset));
            } else if (type.equalsIgnoreCase("ACC")) {
                eq.setAcc((short) (eq.getAcc() + offset));
            } else if (type.equalsIgnoreCase("Avoid")) {
                eq.setAvoid((short) (eq.getAvoid() + offset));
            } else if (type.equalsIgnoreCase("Hands")) {
                eq.setHands((short) (eq.getHands() + offset));
            } else if (type.equalsIgnoreCase("Speed")) {
                eq.setSpeed((short) (eq.getSpeed() + offset));
            } else if (type.equalsIgnoreCase("Jump")) {
                eq.setJump((short) (eq.getJump() + offset));
            } else if (type.equalsIgnoreCase("ItemEXP")) {
                eq.setItemEXP(eq.getItemEXP() + offset);
            } else if (type.equalsIgnoreCase("Expiration")) {
                eq.setExpiration((long) (eq.getExpiration() + offset));
            } else if (type.equalsIgnoreCase("Flag")) {
                eq.setFlag((byte) (eq.getFlag() + offset));
            }
            if (eq.getExpiration() == -1) {
                eq.setFlag((byte) (eq.getFlag() | ItemFlag.LOCK.getValue()));
            } else {
                eq.setFlag((byte) (eq.getFlag() | ItemFlag.UNTRADEABLE.getValue()));
            }
            item = eq.copy();
        }
        MapleInventoryManipulator.removeFromSlot(getClient(), inv, (short) slot, item.getQuantity(), false);
        return MapleInventoryManipulator.addFromDrop(getClient(), item, false);
    }

    public boolean replaceItem(int slot, int invType, Object statsSel, int upgradeSlots) {
        return replaceItem(slot, invType, statsSel, upgradeSlots, "Slots");
    }

    public boolean isCash(final int itemId) {
        return MapleItemInformationProvider.getInstance().isCash(itemId);
    }

    public void buffGuild(final int buff, final int duration, final String msg) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (ii.getItemEffect(buff) != null && getPlayer().getGuildId() > 0) {
            final MapleStatEffect mse = ii.getItemEffect(buff);
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (MapleCharacter chr : cserv.getPlayerStorage().getAllCharactersThreadSafe()) {
                    if (chr.getGuildId() == getPlayer().getGuildId()) {
                        mse.applyTo(chr, chr, true, null, duration);
                        chr.dropMessage(5, "Your guild has gotten a " + msg + " buff.");
                    }
                }
            }
        }
    }

    public boolean createAlliance(String alliancename) {
        MapleParty pt = c.getPlayer().getParty();
        MapleCharacter otherChar = c.getChannelServer().getPlayerStorage().getCharacterById(pt.getMemberByIndex(1).getId());
        if (otherChar == null || otherChar.getId() == c.getPlayer().getId()) {
            return false;
        }
        try {
            return World.Alliance.createAlliance(alliancename, c.getPlayer().getId(), otherChar.getId(), c.getPlayer().getGuildId(), otherChar.getGuildId());
        } catch (Exception re) {
            return false;
        }
    }

    public boolean addCapacityToAlliance() {
        try {
            final MapleGuild gs = World.Guild.getGuild(c.getPlayer().getGuildId());
            if (gs != null && c.getPlayer().getGuildRank() == 1 && c.getPlayer().getAllianceRank() == 1) {
                if (World.Alliance.getAllianceLeader(gs.getAllianceId()) == c.getPlayer().getId() && World.Alliance.changeAllianceCapacity(gs.getAllianceId())) {
                    gainMeso(-MapleGuildAlliance.CHANGE_CAPACITY_COST);
                    return true;
                }
            }
        } catch (Exception re) {
        }
        return false;
    }

    public boolean disbandAlliance() {
        try {
            final MapleGuild gs = World.Guild.getGuild(c.getPlayer().getGuildId());
            if (gs != null && c.getPlayer().getGuildRank() == 1 && c.getPlayer().getAllianceRank() == 1) {
                if (World.Alliance.getAllianceLeader(gs.getAllianceId()) == c.getPlayer().getId() && World.Alliance.disbandAlliance(gs.getAllianceId())) {
                    return true;
                }
            }
        } catch (Exception re) {
        }
        return false;
    }

    public byte getLastMsg() {
        return lastMsg;
    }

    public final void setLastMsg(final byte last) {
        this.lastMsg = last;
    }

    public void setPartyBossLog(String bossid) {
        MapleParty party = getPlayer().getParty();
        for (MaplePartyCharacter pc : party.getMembers()) {
            MapleCharacter chr = World.getStorage(this.getChannelNumber()).getCharacterById(pc.getId());
            if (chr != null) {
                chr.setBossLog(bossid);
            }
        }
    }

    public final void maxAllSkills() {
        for (ISkill skil : SkillFactory.getAllSkills()) {
            if (GameConstants.isApplicableSkill(skil.getId())) { //no db/additionals/resistance skills
                teachSkill(skil.getId(), skil.getMaxLevel(), skil.getMaxLevel());
            }
        }
    }

    public final void resetStats(int str, int dex, int z, int luk) {
        c.getPlayer().resetStats(str, dex, z, luk);
    }

    public final boolean dropItem(int slot, int invType, int quantity) {
        MapleInventoryType inv = MapleInventoryType.getByType((byte) invType);
        if (inv == null) {
            return false;
        }
        return MapleInventoryManipulator.drop(c, inv, (short) slot, (short) quantity, true);
    }

    public final List<Integer> getAllPotentialInfo() {
        return new ArrayList<>(MapleItemInformationProvider.getInstance().getAllPotentialInfo().keySet());
    }

    public final String getPotentialInfo(final int id) {
        final List<StructPotentialItem> potInfo = MapleItemInformationProvider.getInstance().getPotentialInfo(id);
        final StringBuilder builder = new StringBuilder("#b#ePOTENTIAL INFO FOR ID: ");
        builder.append(id);
        builder.append("#n#k\r\n\r\n");
        int minLevel = 1, maxLevel = 10;
        for (StructPotentialItem item : potInfo) {
            builder.append("#eLevels ");
            builder.append(minLevel);
            builder.append("~");
            builder.append(maxLevel);
            builder.append(": #n");
            builder.append(item.toString());
            minLevel += 10;
            maxLevel += 10;
            builder.append("\r\n");
        }
        return builder.toString();
    }

    public final void sendRPS() {
        c.sendPacket(MaplePacketCreator.getRPSMode((byte) 8, -1, -1, -1));
    }

    public final void setQuestRecord(Object ch, final int questid, final String data) {
        ((MapleCharacter) ch).getQuestNAdd(MapleQuest.getInstance(questid)).setCustomData(data);
    }

    public final void doWeddingEffect(final Object ch) {
        final MapleCharacter chr = (MapleCharacter) ch;
        getMap().broadcastMessage(MaplePacketCreator.yellowChat(chr.getName() + ", 你愿意嫁给 " + getPlayer().getName() + " 吗？无论他将来是富有还是贫穷、或无论他将来身体健康或不适，你都愿意和他永远在一起吗?"));
        CloneTimer.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (chr == null || getPlayer() == null) {
                    warpMap(680000500, 0);
                } else {
                    getMap().broadcastMessage(MaplePacketCreator.yellowChat(getPlayer().getName() + ", 你愿意娶 " + chr.getName() + "为妻吗？无论她将来是富有还是贫穷、或无论她将来身体健康或不适，你都愿意和她永远在一起吗？？"));
                }
            }
        }, 10000);
        CloneTimer.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (chr == null || getPlayer() == null) {
                    if (getPlayer() != null) {
                        setQuestRecord(getPlayer(), 160001, "3");
                        setQuestRecord(getPlayer(), 160002, "0");
                    } else if (chr != null) {
                        setQuestRecord(chr, 160001, "3");
                        setQuestRecord(chr, 160002, "0");
                    }
                    warpMap(680000500, 0);
                } else {
                    setQuestRecord(getPlayer(), 160001, "2");
                    setQuestRecord(chr, 160001, "2");
                    sendNPCText(getPlayer().getName() + " 和 " + chr.getName() + "， 我希望你们两个能在此时此刻永远爱着对方！", 9201002);
                    getMap().startExtendedMapEffect("那么现在请新娘亲吻 " + getPlayer().getName() + "！", 5120006);
                    if (chr.getGuildId() > 0) {
                        World.Guild.guildPacket(chr.getGuildId(), MaplePacketCreator.sendMarriage(false, chr.getName()));
                    }
                    if (chr.getFamilyId() > 0) {
                        World.Family.familyPacket(chr.getFamilyId(), MaplePacketCreator.sendMarriage(true, chr.getName()), chr.getId());
                    }
                    if (getPlayer().getGuildId() > 0) {
                        World.Guild.guildPacket(getPlayer().getGuildId(), MaplePacketCreator.sendMarriage(false, getPlayer().getName()));
                    }
                    if (getPlayer().getFamilyId() > 0) {
                        World.Family.familyPacket(getPlayer().getFamilyId(), MaplePacketCreator.sendMarriage(true, chr.getName()), getPlayer().getId());
                    }
                }
            }
        }, 20000); //10 sec 10 sec
    }

    public void 開啟小鋼珠(int type) {
        c.sendPacket(MaplePacketCreator.openBeans(getPlayer().getBeans(), type));
    }

    public void worldMessage(String text) {
        World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, text));
    }

    public int getBeans() {
        return getClient().getPlayer().getBeans();
    }

    public void warpBack(int mid, final int retmap, final int time) { //時間秒數

        MapleMap warpMap = c.getChannelServer().getMapFactory().getMap(mid);
        c.getPlayer().changeMap(warpMap, warpMap.getPortal(0));
        c.sendPacket(MaplePacketCreator.getClock(time));
        Timer.EventTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                MapleMap warpMap = c.getChannelServer().getMapFactory().getMap(retmap);
                if (c.getPlayer() != null) {
                    c.sendPacket(MaplePacketCreator.stopClock());
                    c.getPlayer().changeMap(warpMap, warpMap.getPortal(0));
                    c.getPlayer().dropMessage(6, "已经到达的地了!");
                }
            }
        }, 1000 * time); //設定時間, (1 秒 = 1000)
    }

    public void ChangeName(String name) {
        getPlayer().setName(name);
        save();
        getPlayer().fakeRelog();
    }

    public String searchData(int type, String search) {
        return SearchGenerator.searchData(type, search);
    }

    public int[] getSearchData(int type, String search) {
        Map<Integer, String> data = SearchGenerator.getSearchData(type, search);
        if (data.isEmpty()) {
            return null;
        }
        int[] searches = new int[data.size()];
        int i = 0;
        for (int key : data.keySet()) {
            searches[i] = key;
            i++;
        }
        return searches;
    }

    public boolean foundData(int type, String search) {
        return SearchGenerator.foundData(type, search);
    }

    public boolean ReceiveMedal() {
        int acid = getPlayer().getAccountID();
        int id = getPlayer().getId();
        String name = getPlayer().getName();
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        int item = 1142475;
        if (!getPlayer().canHold(item)) {
            return false;
        } else if (getPlayer().haveItem(item)) {
            return false;
        }

        try (Connection con = DBConPool.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT id FROM RCmedals WHERE name = ?");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (!rs.first()) {// 角色不存在於勳章表單
                return false;
            }
            ps.close();
            rs.close();
            ps = con.prepareStatement("Update RCmedals set amount = ? Where id = ?");
            ps.setInt(1, 0);
            ps.setInt(2, id);
            ps.execute();
            ps.close();
        } catch (Exception ex) {
            FilePrinter.printError("NPCConversationManager.txt", ex, "ReceiveMedal(" + name + ")");
            FileoutputUtil.outError("logs/資料庫異常.txt", ex);
        }
        IItem toDrop = ii.randomizeStats((Equip) ii.getEquipById(item));
        toDrop.setGMLog(getPlayer().getName() + " 领取勋章");
        MapleInventoryManipulator.addbyItem(c, toDrop);
        FileoutputUtil.logToFile("logs/Data/NPC領取勳章.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號: " + c.getAccountName() + " 玩家: " + c.getPlayer().getName() + " 領取了RC勳章");
        return true;
    }

    public String ShowJobRank(int type) {
        StringBuilder sb = new StringBuilder();
        List<MapleGuildRanking.JobRankingInfo> Ranking = MapleGuildRanking.getInstance().getJobRank(type);
        if (Ranking != null) {
            int num = 0;
            for (MapleGuildRanking.JobRankingInfo info : Ranking) {
                num++;
                sb.append("#n#e#k排名:#r ");
                sb.append(num);
                sb.append("\r\n#n#e#k玩家名称:#d ");
                sb.append(StringUtil.getRightPaddedStr(info.getName(), ' ', 13));
                sb.append("\r\n#n#e#k等级:#e#r ");
                sb.append(StringUtil.getRightPaddedStr(String.valueOf(info.getLevel()), ' ', 3));
                sb.append("\r\n#n#e#k职业:#e#b ");
                sb.append(MapleJob.getName(MapleJob.getById(info.getJob())));
                sb.append("\r\n#n#e#k力量:#e#d ");
                sb.append(StringUtil.getRightPaddedStr(String.valueOf(info.getStr()), ' ', 4));
                sb.append("\r\n#n#e#k敏捷:#e#d ");
                sb.append(StringUtil.getRightPaddedStr(String.valueOf(info.getDex()), ' ', 4));
                sb.append("\r\n#n#e#k智力:#e#d ");
                sb.append(StringUtil.getRightPaddedStr(String.valueOf(info.getInt()), ' ', 4));
                sb.append("\r\n#n#e#k运气:#e#d ");
                sb.append(StringUtil.getRightPaddedStr(String.valueOf(info.getLuk()), ' ', 4));
                sb.append("\r\n");
                sb.append("#n#k======================================================\r\n");
            }
        } else {
            sb.append("#r查询无任何结果");
        }
        return sb.toString();
    }

    public static boolean hairExists(int hair) {
        return MapleItemInformationProvider.hairList.containsKey(hair);
    }

    public int[] getCanHair(int[] hairs) {
        List<Integer> canHair = new ArrayList();
        List<Integer> cantHair = new ArrayList();
        for (int hair : hairs) {
            if (hairExists(hair)) {
                canHair.add(hair);
            } else {
                cantHair.add(hair);
            }
        }
        if (cantHair.size() > 0 && c.getPlayer().isAdmin()) {
            StringBuilder sb = new StringBuilder("正在读取的发型里有");
            sb.append(cantHair.size()).append("个发型客户端不支持显示，已经被清除：");
            for (int i = 0; i < cantHair.size(); i++) {
                sb.append(cantHair.get(i));
                if (i < cantHair.size() - 1) {
                    sb.append(",");
                }
            }
            playerMessage(sb.toString());
        }
        int[] getHair = new int[canHair.size()];
        for (int i = 0; i < canHair.size(); i++) {
            getHair[i] = canHair.get(i);
        }
        return getHair;
    }

    public static boolean faceExists(int face) {
        return MapleItemInformationProvider.faceLists.containsKey(face);
    }

    public int[] getCanFace(int[] faces) {
        List<Integer> canFace = new ArrayList();
        List<Integer> cantFace = new ArrayList();
        for (int face : faces) {
            if (faceExists(face)) {
                canFace.add(face);
            } else {
                cantFace.add(face);
            }
        }
        if (cantFace.size() > 0 && c.getPlayer().isAdmin()) {
            StringBuilder sb = new StringBuilder("正在读取的脸型里有");
            sb.append(cantFace.size()).append("个脸型客户端不支持显示，已经被清除：");
            for (int i = 0; i < cantFace.size(); i++) {
                sb.append(cantFace.get(i));
                if (i < cantFace.size() - 1) {
                    sb.append(",");
                }
            }
            playerMessage(sb.toString());
        }
        int[] getFace = new int[canFace.size()];
        for (int i = 0; i < canFace.size(); i++) {
            getFace[i] = canFace.get(i);
        }
        return getFace;
    }

    public String checkDrop(int mobId) {
        final List<MonsterDropEntry> ranks = MapleMonsterInformationProvider.getInstance().retrieveDrop(mobId);
        if (ranks != null && ranks.size() > 0) {
            int num = 0, itemId = 0, ch = 0;
            MonsterDropEntry de;
            StringBuilder name = new StringBuilder();
            MapleMonster onemob = MapleLifeFactory.getMonster(mobId);
            name.append("#e#d冒险岛怪物详细信息预览：#n#k\r\n\r\n");
            name.append("#d怪物名称 : #b#o" + mobId + "##k\r\n");
            name.append("#d怪物等级 : #b" + onemob.getLevel() + "#k\r\n");
            name.append("#d怪物类型 : #b" + (onemob.getStats().isBoss() ? "Boss怪物" : "普通怪物") + "#k\r\n");
            name.append("#d物理防御 : #b" + (onemob.getStats().getPhysicalDefense()) + "#k\r\n");
            name.append("#d魔法防御 : #b" + (onemob.getStats().getMagicDefense()) + "#k\r\n");
            name.append("#d最大血量 : #b" + (onemob.getMobMaxHp()) + "#k\r\n");
            name.append("--------------------------------------\r\n\r\n");
            name.append("#e#d怪物属性信息：#n#k\r\n\r\n");

            name.append("#d光明 : #b" + ((onemob.getStats().getEffectiveness(Element.HOLY)) == ElementalEffectiveness.IMMUNE ? "免疫" : (onemob.getStats().getEffectiveness(Element.HOLY)) == ElementalEffectiveness.STRONG ? "抗性" : (onemob.getStats().getEffectiveness(Element.HOLY)) == ElementalEffectiveness.WEAK ? "弱点" : "正常") + "#k  ");
            name.append("#d黑暗 : #b" + ((onemob.getStats().getEffectiveness(Element.DARKNESS)) == ElementalEffectiveness.IMMUNE ? "免疫" : (onemob.getStats().getEffectiveness(Element.DARKNESS)) == ElementalEffectiveness.STRONG ? "抗性" : (onemob.getStats().getEffectiveness(Element.DARKNESS)) == ElementalEffectiveness.WEAK ? "弱点" : "正常") + "#k  ");
            name.append("#d雷电 : #b" + ((onemob.getStats().getEffectiveness(Element.LIGHTING)) == ElementalEffectiveness.IMMUNE ? "免疫" : (onemob.getStats().getEffectiveness(Element.LIGHTING)) == ElementalEffectiveness.STRONG ? "抗性" : (onemob.getStats().getEffectiveness(Element.LIGHTING)) == ElementalEffectiveness.WEAK ? "弱点" : "正常") + "#k\r\n");
            name.append("#d冰冻 : #b" + ((onemob.getStats().getEffectiveness(Element.ICE)) == ElementalEffectiveness.IMMUNE ? "免疫" : (onemob.getStats().getEffectiveness(Element.ICE)) == ElementalEffectiveness.STRONG ? "抗性" : (onemob.getStats().getEffectiveness(Element.ICE)) == ElementalEffectiveness.WEAK ? "弱点" : "正常") + "#k  ");
            name.append("#d毒素 : #b" + ((onemob.getStats().getEffectiveness(Element.POISON)) == ElementalEffectiveness.IMMUNE ? "免疫" : (onemob.getStats().getEffectiveness(Element.POISON)) == ElementalEffectiveness.STRONG ? "抗性" : (onemob.getStats().getEffectiveness(Element.POISON)) == ElementalEffectiveness.WEAK ? "弱点" : "正常") + "#k  ");
            name.append("#d火焰 : #b" + ((onemob.getStats().getEffectiveness(Element.FIRE)) == ElementalEffectiveness.IMMUNE ? "免疫" : (onemob.getStats().getEffectiveness(Element.FIRE)) == ElementalEffectiveness.STRONG ? "抗性" : (onemob.getStats().getEffectiveness(Element.FIRE)) == ElementalEffectiveness.WEAK ? "弱点" : "正常") + "#k\r\n");
            name.append("--------------------------------------\r\n\r\n");
            for (int i = 0; i < ranks.size(); i++) {
                de = ranks.get(i);
                if (de.chance > 0 && (de.questid <= 0 || (de.questid > 0 && MapleQuest.getInstance(de.questid).getName().length() > 0))) {
                    itemId = de.itemId;
                    if (num == 0) {

                        name.append("#e#d怪物爆物信息：#n#k\r\n\r\n");
                    }
                    String namez = "#z" + itemId + "#";
                    if (itemId == 0) { //meso
                        itemId = 4031041; //display sack of cash
                        namez = (de.Minimum * getClient().getChannelServer().getMesoRate()) + " 到 " + (de.Maximum * getClient().getChannelServer().getMesoRate()) + " 楓幣";
                    }
                    ch = de.chance * getClient().getChannelServer().getDropRate();
                    name.append((num + 1) + ") #v" + itemId + "#" + namez /*+ " - " + (Integer.valueOf(ch >= 999999 ? 1000000 : ch).doubleValue() / 10000.0) + "% 爆率。 "*/ + (de.questid > 0 && MapleQuest.getInstance(de.questid).getName().length() > 0 ? ("需要接受任務 " + MapleQuest.getInstance(de.questid).getName() + "") : "") + "\r\n");
                    num++;
                }
            }
            if (name.length() > 0) {
                return name.toString();
            }

        }
        return "没有当前怪物的爆物信息。";
    }

    public String checkDrop(MapleCharacter chr, int mobId, boolean GM) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final List<MonsterDropEntry> ranks = MapleMonsterInformationProvider.getInstance().retrieveDrop(mobId);
        if (ranks != null && ranks.size() > 0) {
            int num = 0, itemId = 0, ch = 0;
            MonsterDropEntry de;
            StringBuilder name = new StringBuilder();
            StringBuilder error = new StringBuilder();
            name.append("【#r#o" + mobId + "##k】爆物物品查询列表:#b" + "\r\n");
            for (int i = 0; i < ranks.size(); i++) {
                de = ranks.get(i);
                if (de.chance > 0 && (de.questid <= 0 || (de.questid > 0 && MapleQuest.getInstance(de.questid).getName().length() > 0))) {
                    itemId = de.itemId;
/*   if (num == 0) {
name.append("【#r#o"+ mobId + "##k】掉寶數據列表:#b" +"\r\n");
}*/
                    String namez = "#z" + itemId + "#";
                    if (itemId == 0) { //meso
                        itemId = 4031041; //display sack of cash
                        namez = (de.Minimum * getClient().getChannelServer().getMesoRate()) + " to " + (de.Maximum * getClient().getChannelServer().getMesoRate()) + " #b楓幣#l#k";
                    } else if (itemId != 0 && ii.itemExists(itemId)) {
                        ch = de.chance * getClient().getChannelServer().getDropRate();
                        if (GM == false) {
                            name.append("#k" + (num + 1) + ": #v" + itemId + "# " + namez + ((chr.isGM()) ? "#d  掉落機率：" + (Integer.valueOf(ch >= 999999 ? 1000000 : ch).doubleValue() / 10000.0) + "%\r\n" : "\r\n") + "#b(掉落條件:" + (de.questid > 0 && MapleQuest.getInstance(de.questid).getName().length() > 0 ? ("需要接取任務#r " + MapleQuest.getInstance(de.questid).getName() + " #b)\r\n") : "#r無#b)") + "\r\n");
                        } else {
                            name.append("#L" + itemId + "##k" + (num + 1) + ": #v" + itemId + "# " + namez + ((chr.isGM()) ? "#d  掉落機率：" + (Integer.valueOf(ch >= 999999 ? 1000000 : ch).doubleValue() / 10000.0) + "%(點選更改)\r\n" : "\r\n") + "#b(掉落條件:" + (de.questid > 0 && MapleQuest.getInstance(de.questid).getName().length() > 0 ? ("需要接取任務#r " + MapleQuest.getInstance(de.questid).getName() + " #b)\r\n") : "#r無#b)") + "\r\n");
                        }
//                       name.append("#k" + (num + 1) + ": #v" + itemId + "# " + namez + " #d" +"%\r\n#b(掉落條件:" + (de.questid > 0 && MapleQuest.getInstance(de.questid).getName().length() > 0 ? ("需要接取任務#r " + MapleQuest.getInstance(de.questid).getName() + " #b)\r\n") : "#r無#b)") + ((chr.isGM())?"掉落機率：" + (Integer.valueOf(ch >= 999999 ? 1000000 : ch).doubleValue() / 10000.0) +"\r\n":"\r\n"));

                        num++;
                    } else {
                        error.append(itemId + "\r\n");
                    }
                }
            }
            if (GM == true) {
                name.append("\r\n#L" + 10000 + "##k" + (num + 1) + ": #b我要額外新增掉落物品!");
            }
            if (error.length() > 0) {
                chr.dropMessage(1, "無效的物品ID:\r\n" + error.toString());
            }
            if (name.length() > 0) {
                return name.toString();
            }

        }
        return "該怪物查無任何掉寶數據。";
    }


    public void 刷新地图() {
        boolean custMap = true;
        int mapid = c.getPlayer().getMapId();
        MapleMap map = custMap ? c.getPlayer().getClient().getChannelServer().getMapFactory().getMap(mapid) : c.getPlayer().getMap();
        if (c.getPlayer().getClient().getChannelServer().getMapFactory().destroyMap(mapid)) {
            MapleMap newMap = c.getPlayer().getClient().getChannelServer().getMapFactory().getMap(mapid);
            MaplePortal newPor = newMap.getPortal(0);
            LinkedHashSet<MapleCharacter> mcs = new LinkedHashSet<>(map.getCharacters()); //菱形推断好点
            outerLoop:
            for (MapleCharacter m : mcs) {
                for (int x = 0; x < 5; x++) {
                    try {
                        m.changeMap(newMap, newPor);
                        continue outerLoop;
                    } catch (Throwable t) {
                    }
                }
            }
        }
    }

    public String 查询爆物(int a) {
        StringBuilder name = new StringBuilder();
        try (Connection con = DBConPool.getInstance().getDataSource().getConnection()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM drop_data where itemid =" + a + "")) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    name.append("    #o").append(rs.getInt("dropperid")).append("#\r\n");
                }
                ps.close();
            }
        } catch (SQLException ex) {
        }
        return name.toString();
    }

    public String 显示物品(int a) {
        String data = "";
        data = "#v" + a + "# #b#z" + a + "##k";
        return data;
    }

    public void gainBeans(int s) {
        getPlayer().gainBeans(s);
        c.sendPacket(MaplePacketCreator.updateBeans(this.c.getPlayer()));
    }

    public void openBeans() {//打开豆豆机界面
        c.sendPacket(MaplePacketCreator.openBeans(getPlayer().getBeans(), 0));
        c.getPlayer().dropMessage(5, "按住左右鍵可以调整力道,建议调好角度连续打,不要按暂停若九宮格卡住没反应重新打开豆豆机");
    }

    public void setMonsterRiding(int itemid) {//裝備不能正常裝備的坐騎
        short src = getClient().getPlayer().haveItemPos(itemid);
        if (src == 100) {
            c.getPlayer().dropMessage(5, "你没有当前骑宠。");
        } else {
            MapleInventoryManipulator.equip(c, src, (short) -18);
            c.getPlayer().dropMessage(5, "装备骑宠成功。");
        }
    }

    public int getRandom(int... args_all) {
        int args = args_all[Randomizer.nextInt(args_all.length)];
        return args;
    }

    public void OwlAdv(int point, int itemid) {
        owlse(this.c, point, itemid);
    }

    public static void owlse(MapleClient c, int point, int itemid) {
        int itemSearch = itemid;

        List<HiredMerchant> hms = new ArrayList<HiredMerchant>();
        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
            if (!cserv.searchMerchant(itemSearch).isEmpty()) {
                hms.addAll(cserv.searchMerchant(itemSearch));
            }
        }
        if (hms.size() > 0) {
            if (c.getPlayer().haveItem(5230000, 1)) {
                MapleInventoryManipulator.removeById(c, MapleInventoryType.CASH, 5230000, 1, true, false);
            } else if (c.getPlayer().getCSPoints(point) >= 5) {
                c.getPlayer().modifyCSPoints(point, -5, true);
            } else {
                c.getPlayer().dropMessage(1, "点数不足，无法查询！");
                if (NPCScriptManager.getInstance().getCM(c) != null) {
                    NPCScriptManager.getInstance().dispose(c);
                    c.sendPacket(MaplePacketCreator.enableActions());
                }
            }

            if (NPCScriptManager.getInstance().getCM(c) != null) {
                NPCScriptManager.getInstance().dispose(c);
            }
            c.sendPacket(MaplePacketCreator.getOwlSearched(itemSearch, hms));
        } else {
            if (NPCScriptManager.getInstance().getCM(c) != null) {
                NPCScriptManager.getInstance().dispose(c);
                c.sendPacket(MaplePacketCreator.enableActions());
            }
            c.getPlayer().dropMessage(1, "找不到物品");
        }
    }


    public void checkMobs(MapleCharacter chr) {
        if (this.getMap().getAllMonstersThreadsafe().size() <= 0) {
            sendOk("#地圖上沒有怪物哦!!。");
            dispose();
        }
        String msg = "玩家 #b" + chr.getName() + "#k 此地圖怪物掉寶查詢:\r\n#r(若有任何掉寶問題,請至社團BUG區回報怪物名稱和代碼)\r\n#d";
        Iterator monster = getMap().getAllUniqueMonsters().iterator();
        while (monster.hasNext()) {
            Object monsterid = monster.next();
//            msg += "#L" + monsterid + "##o" + monsterid + "#" + ((chr.isGM()) ? " 代碼:" + monsterid + "#l\r\n" : "(查看)#l\r\n");
            msg += "#L" + monsterid + "##o" + monsterid + "#" + " 代碼:" + monsterid + " (查看)#l\r\n";
        }
        sendOk(msg);
    }

    public void getMobs(int itemid) {
        MapleMonsterInformationProvider mi = MapleMonsterInformationProvider.getInstance();
        final List<Integer> mobs = MapleMonsterInformationProvider.getInstance().getMobByItem(itemid);
        String text = "#d這些怪物會掉落您查詢的物品#k: \r\n\r\n";

        for (int i = 0; i < mobs.size(); i++) {
            int quest = 0;
            if (mi.getDropQuest(mobs.get(i)) > 0) {
                quest = mi.getDropQuest(mobs.get(i));
            }
            int chance = mi.getDropChance(mobs.get(i)) * getClient().getChannelServer().getDropRate();

            text += "#r#o" + mobs.get(i) + "##k " /*+ (Integer.valueOf(chance >= 999999 ? 1000000 : chance).doubleValue() / 10000.0) + "%" */ + (quest > 0 && MapleQuest.getInstance(quest).getName().length() > 0 ? ("#b需要進行 " + MapleQuest.getInstance(quest).getName() + " 任務來取得#k") : "") + "\r\n";

        }
        sendNext(text);
    }

    public static String 获取家族名称(int guildId) {
        String data = "";
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT name as DATA FROM guilds WHERE guildid = ?");
            ps.setInt(1, guildId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data = rs.getString("DATA");
                }
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("获取家族名称出错 - 数据库查询失败：" + Ex);
        }
        return data;
    }

    public static String 获取最高等级玩家名字() {
        String name = "";
        String level = "";
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT `name`, `level` FROM characters WHERE gm = 0 ORDER BY `level` DESC LIMIT 1");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    name = rs.getString("name");
                    level = rs.getString("level");
                }
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("获取家族名称出错 - 数据库查询失败：" + Ex);
        }

        return String.format("%s", name);
    }

    public int 角色ID取雇佣数据(int id) {
        int data = 0;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT cid as DATA FROM hire WHERE cid = ?");
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data = rs.getInt("DATA");
                }
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("角色名字取账号ID、出错");
        }
        return data;
    }

    public static int 角色ID取账号ID(int id) {
        int data = 0;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT accountid as DATA FROM characters WHERE id = ?");
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data = rs.getInt("DATA");
                }
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("角色名字取账号ID、出错");
        }
        return data;
    }


    public Gashapon getGashapon() {
        return GashaponFactory.getInstance().getGashaponByNpcId(this.getNpc());
    }

    public void getGachaponMega(String msg, Item item, int quantity) {
        World.Broadcast.broadcastGashponmega(MaplePacketCreator.getGachaponMega(c.getPlayer().getName(), " : x" + quantity + "恭喜玩家 " + c.getPlayer().getName() + " 在" + msg + "获得！", item, (byte) 1, c.getPlayer().getClient().getChannel()));
    }

    public void getItemMegaphone(String msg, Item item, int quantity) {
        World.Broadcast.broadcastGashponmega(MaplePacketCreator.itemMegaphone(c.getPlayer().getName(), " : x" + quantity + "恭喜玩家 " + c.getPlayer().getName() + " 在" + msg + "获得！", item, c.getPlayer().getClient().getChannel()));
    }

    public void EnterCS(int mod) {
        c.getPlayer().setCsMod(mod);
        InterServerHandler.EnterCashShop(c, c.getPlayer(), false);
    }

    public int[] getSavedFaces() {
        return getPlayer().getSavedFaces();
    }

    public int getSavedFace(int sel) {
        return getPlayer().getSavedFace(sel);
    }

    public void setSavedFace(int sel, int id) {
        getPlayer().setSavedFace(sel, id);
    }

    public int[] getSavedHairs() {
        return getPlayer().getSavedHairs();
    }

    public int getSavedHair(int sel) {
        return getPlayer().getSavedHair(sel);
    }

    public void setSavedHair(int sel, int id) {
        getPlayer().setSavedHair(sel, id);
    }

    public String getServerName() {
//return MapleParty.开服名字;//var MC = cm.etServerName();
        return ServerConfig.SERVERNAME;
    }

    public String 开服名字() {//取服务器名字
        return c.getChannelServer().getServerName();
    }

    /**
     * *****************************************************************************************************************************************************
     * 充值系统
     * *****************************************************************************************************************************************************
     */

    public static int 判断兑换卡是否存在(String id) {
        int data = 0;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT code as DATA FROM nxcodez WHERE code = ?");
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data += 1;
                }
            }
        } catch (SQLException Ex) {
            System.err.println("判断兑换卡是否存在、出错");
        }
        return data;
    }

    public static int 判断兑换卡类型(String code) throws SQLException {
        int item = -1;
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT `leixing` FROM nxcodez WHERE code = ?");
        ps.setString(1, code);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            item = rs.getInt("leixing");
        }
/*rs.close();
ps.close();*/
        return item;
    }

    public static int 判断兑换卡数额(String code) throws SQLException {
        int item = -1;
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT `valid` FROM nxcodez WHERE code = ?");
        ps.setString(1, code);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            item = rs.getInt("valid");
        }
/*rs.close();
ps.close();*/
        return item;
    }

    public static int 判断兑换卡礼包(String code) throws SQLException {
        int item = -1;
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT `itme` FROM nxcodez WHERE code = ?");
        ps.setString(1, code);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            item = rs.getInt("itme");
        }
/*rs.close();
ps.close();*/
        return item;
    }

    //删除兑换卡
    public void Deleteexchangecard(String a) {
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        try {
            ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM nxcodez ");
            rs = ps1.executeQuery();
            while (rs.next()) {
                String sqlstr = " delete from nxcodez where code = '" + a + "' ";
                ps1.executeUpdate(sqlstr);
            }
        } catch (SQLException ex) {
        }
    }

    public int 获取推广人ID() {
        int 推广人ID = 0;
        try {
            int cid = getPlayer().getAccountID();
            Connection con = DatabaseConnection.getConnection();
            ResultSet rs;
            try (PreparedStatement limitCheck = con.prepareStatement("SELECT * FROM accounts WHERE id=" + cid + "")) {
                rs = limitCheck.executeQuery();
                if (rs.next()) {
                    推广人ID = rs.getInt("推广人ID");
                }
            }
            rs.close();
        } catch (SQLException ex) {
            ex.getStackTrace();
        }
        return 推广人ID;
    }

    public void 写入推广人ID(int slot) {
        try {
            int cid = getPlayer().getAccountID();
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE accounts SET 推广人ID = " + slot + " WHERE id = " + cid + "");
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            ex.getStackTrace();
        }
    }

    public int 获取推广值() {
        int 推广值 = 0;
        try {
            int cid = getPlayer().getAccountID();
            Connection con = DatabaseConnection.getConnection();
            ResultSet rs;
            try (PreparedStatement limitCheck = con.prepareStatement("SELECT * FROM accounts WHERE id=" + cid + "")) {
                rs = limitCheck.executeQuery();
                if (rs.next()) {
                    推广值 = rs.getInt("推广值");
                }
            }
            rs.close();
        } catch (SQLException ex) {
            ex.getStackTrace();
        }
        return 推广值;
    }

    public void 写入推广值(int slot) {
        try {
            int cid = 获取推广人ID();
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE accounts SET 推广值 = 推广值 + " + slot + " WHERE id = " + cid + "");
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            ex.getStackTrace();
        }
    }

    public void 更改推广值(int slot) {
        try {
            int cid = getPlayer().getAccountID();
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE accounts SET 推广值 = 推广值+" + slot + " WHERE id = " + cid + "");
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            ex.getStackTrace();
        }
    }

    public void 召唤假人(int a) {
        Map<Integer, Pair<Integer, Integer>> keymap = LoginServer.getChrPos();
        Point Original_Pos = c.getPlayer().getPosition();
        keymap.put(a, new Pair<>(Original_Pos.x, Original_Pos.y));
        setCustomPlayer(a, Original_Pos.x, Original_Pos.y);
    }

    public void 召唤全部假人() {
        Map<Integer, Pair<Integer, Integer>> keymap = LoginServer.getChrPos();
        try (Connection con = DBConPool.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps;
            ps = con.prepareStatement("SELECT * FROM pfake WHERE id  > 0");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                keymap.put(rs.getInt("cid"), new Pair<>(rs.getInt("x"), rs.getInt("y")));
            }
            rs.close();
            ps.close();
        } catch (Exception Ex) {
            FileoutputUtil.outError("logs/数据库异常.txt", Ex);
//   log.error("Error while insert bosslog.", Ex);
        }
    }

    public void setCustomPlayer(int id, int X, int Y) {
        try (Connection con = DBConPool.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps;
            ps = con.prepareStatement("insert into pfake (cid,x,y) values (?,?,?)");
            ps.setInt(1, id);
            ps.setInt(2, X);
            ps.setInt(3, Y);
            ps.executeUpdate();
            ps.close();
        } catch (Exception Ex) {
            FileoutputUtil.outError("logs/数据库异常.txt", Ex);
//   log.error("Error while insert bosslog.", Ex);
        }
    }

    public void 个人存档() {
        c.getPlayer().saveToDB(false, false);
    }

    public int 判断点券() {
        return c.getPlayer().getCSPoints(1);
    }

    public int 判断抵用券() {
        return c.getPlayer().getCSPoints(2);
    }

    public int getWeekOfYear() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        return cal.get(Calendar.WEEK_OF_YEAR);
    }

    public int 记录限时购买(final int id, final short quantity) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("insert into 限时购买记录 (name, itemid, sl) VALUES (?, ?, ?)");
            ps.setString(1, getName());
            ps.setInt(2, id);
            ps.setInt(3, quantity);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            System.out.println("数据库操作错误:" + ex);
        }
        return 0;
    }

    public void 减去限时购买数量(final int id, final short quantity) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE 限时购买物品 SET 物品数量 = 物品数量 - " + quantity + " WHERE 物品ID = " + id + "");
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            ex.getStackTrace();
        }
    }

    public void 限时商城(final int id, final short quantity) {
        减去限时购买数量(id, quantity);
        记录限时购买(id, quantity);
    }

    public String 限时购买玩家购买信息展示() {
        String result = "";

        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("select * from 限时购买记录 order by sl desc limit 0, 20");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result = result + rs.getString("name") + " 购买物品： #v" + rs.getInt("itemid") + "##z" + rs.getInt("itemid") + "# 购买数量:#r#e " + rs.getInt("sl") + "#k#n\r\n";
            }

        } catch (SQLException ex) {
            return "";
        }
        return result;
    }

    public int 限时购买商品(int type) {
        int pay = 0;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("select * from 限时购买物品");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if (type == 1) {//查询物品ID
                    pay = rs.getInt("物品ID");
                } else if (type == 2) {//查询数量
                    pay = rs.getInt("物品数量");
                } else if (type == 3) {//查询玩家ID
                    pay = rs.getInt("购买最大数量");
                } else if (type == 4) {
                    pay = rs.getInt("钻石数量");//该物品数量
                } else {
                    pay = 0;
                }
            }
            ps.close();
            rs.close();
        } catch (SQLException ex) {
            System.out.println("查询限时购买信息错误: " + ex);
        }
        return pay;
    }

    public void gainqjbl(int id, int quantity) {
        String sqltablename = "qjbl";
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE " + sqltablename + " SET cs = cs + " + quantity + " WHERE id = " + id + "");
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            ex.getStackTrace();
        }
//        qjblDao.getInstance().clearQjbls();
    }

    public void setqjbl(int id, int quantity) {
        String sqltablename = "qjbl";
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE " + sqltablename + " SET cs = " + quantity + " WHERE id = " + id + "");
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            ex.getStackTrace();
        }
//        qjblDao.getInstance().clearQjbls();
    }

    public void setqjblname(int id, String name) {
        String sqltablename = "qjbl";
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE " + sqltablename + " SET name = ? WHERE id = ?");
            ps.setString(1, name);
            ps.setInt(2, id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            ex.getStackTrace();
        }
//        qjblDao.getInstance().clearQjbls();
    }

    //    public int getqjbl(int id, int type) {
//        int str = 0;
//        List ranks = new ArrayList(qjblDao.getInstance().getGlobalqjbl());
//        if ((ranks != null) && (ranks.size() > 0)) {
//            for (int i = 0; i < ranks.size(); i++) {
//                qjblModel de = (qjblModel) ranks.get(i);
//                int ids = de.getId();
//                if (ids == id){
//                    str = de.getCs();
//                    break;
//                }
//            }
//        }
//        return str;
//    }
//
//    public String getqjblname(int id) {
//        String str = null;
//        List ranks = new ArrayList(qjblDao.getInstance().getGlobalqjbl());
//        if ((ranks != null) && (ranks.size() > 0)) {
//            for (int i = 0; i < ranks.size(); i++) {
//                qjblModel de = (qjblModel) ranks.get(i);
//                int ids = de.getId();
//                if (ids == id){
//                    str = de.getName();
//                    break;
//                }
//            }
//        }
//        return str;
//    }
    public int getqjbl(int id, int type) {
        String sqltablename = "qjbl";
        int pay = 0;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("select * from " + sqltablename + " WHERE id = " + id + "");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if (type == 1) {//查询剩余
                    pay = rs.getInt("cs");
                } else {
                    pay = 0;
                }
            }
            ps.close();
            rs.close();
        } catch (SQLException ex) {
            System.out.println("查询限时购买信息错误: " + ex);
        }
        return pay;
    }

    public String getqjblname(int id) {
        String sqltablename = "qjbl";
        String name = null;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("select * from " + sqltablename + " WHERE id = " + id + "");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                name = rs.getString("name");
            }
            ps.close();
            rs.close();
        } catch (SQLException ex) {
            System.out.println("查询限时购买信息错误: " + ex);
        }
        return name;
    }

    public void loadmaxlevel() {
        ServerConfig.loadmaxlevel();
    }
}
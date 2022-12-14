package client.messages.commands;

import client.MapleCharacter;
import client.MapleCharacterUtil;
import constants.ServerConstants.PlayerGMRank;
import client.MapleClient;
import client.MapleDisease;
import client.MapleStat;
import client.anticheat.CheatingOffense;
import client.inventory.Equip;
import client.inventory.IItem;
import client.inventory.ItemFlag;
import client.inventory.MapleInventoryIdentifier;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import client.inventory.ModifyInventory;
import client.inventory.OnlyID;
import client.messages.CommandProcessorUtil;
import constants.GameConstants;
import constants.PiPiConfig;
import database.DatabaseConnection;
import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.world.World;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import scripting.EventManager;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MaplePortal;
import server.Timer.EventTimer;
import server.events.MapleEvent;
import server.events.MapleEventType;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.MapleNPC;
import server.life.MobSkillFactory;
import server.life.OverrideMonsterStats;
import server.life.PlayerNPC;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import tools.CPUSampler;
import tools.MaplePacketCreator;
import tools.MockIOSession;
import tools.StringUtil;
import tools.packet.MobPacket;
import java.util.concurrent.ScheduledFuture;
import scripting.NPCScriptManager;
import java.util.LinkedHashSet;
import java.util.ListIterator;
import server.gashapon.GashaponFactory;
import tools.FileoutputUtil;
import tools.HexTool;
import tools.Triple;
import tools.data.MaplePacketLittleEndianWriter;

/**
 *
 * @author Emilyx3
 */
public class AdminCommand {

    public static PlayerGMRank getPlayerLevelRequired() {
        return PlayerGMRank.???????????????;
    }

    public static class GC extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            System.gc();
            System.out.println("????????????????????? ---- " + FileoutputUtil.NowTime());
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!GC - ?????????????????????").toString();
        }
    }

    public static class SavePlayerShops extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                cserv.closeAllMerchant();
            }
            c.getPlayer().dropMessage(6, "????????????????????????.");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!savePlayerShops - ??????????????????").toString();
        }
    }

    public static class ?????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleCharacter player = c.getPlayer();
            if (splitted.length < 2) {
                return false;
            }
            MapleCharacter victim;
            String name = splitted[1];
            int ch = World.Find.findChannel(name);
            if (ch <= 0) {
                return false;
            }
            victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);

            short fame;
            try {
                fame = Short.parseShort(splitted[2]);
            } catch (Exception nfe) {
                c.getPlayer().dropMessage(6, "??????????????????");
                return false;
            }
            if (victim != null && player.allowedToTarget(victim)) {
                victim.addFame(fame);
                victim.updateSingleStat(MapleStat.FAME, victim.getFame());
            } else {
                c.getPlayer().dropMessage(6, "[fame] ???????????????");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!?????? <????????????> <??????> ...  - ??????").toString();
        }
    }

    public static class ?????? extends GodMode {

        @Override
        public String getMessage() {
            return new StringBuilder().append("!?????? - ????????????").toString();
        }
    }

    public static class GodMode extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleCharacter player = c.getPlayer();
            if (player.isInvincible()) {
                player.setInvincible(false);
                player.dropMessage(6, "??????????????????");
            } else {
                player.setInvincible(true);
                player.dropMessage(6, "??????????????????.");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!godmode  - ????????????").toString();
        }
    }

    public static class GainCash extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 3) {
                return false;
            }
            MapleCharacter player;
            int amount = 0;
            String name = "";
            try {
                amount = Integer.parseInt(splitted[1]);
                name = splitted[2];
            } catch (Exception ex) {
                return false;
            }
            int ch = World.Find.findChannel(name);
            if (ch <= 0) {
                c.getPlayer().dropMessage("?????????????????????");
                return true;
            }
            player = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            if (player == null) {
                c.getPlayer().dropMessage("?????????????????????");
                return true;
            }
            player.modifyCSPoints(1, amount, true);
            player.dropMessage("????????????Gash??????" + amount + "???");
            String msg = "[GM ??????] GM " + c.getPlayer().getName() + " ?????? " + player.getName() + " Gash?????? " + amount + "???";
            // World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, msg));
            FileoutputUtil.logToFile("logs/Data/????????????.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " GM " + c.getPlayer().getName() + " ?????? " + player.getName() + " Gash?????? " + amount + "???");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!gaingash <??????> <??????> - ??????Gash??????").toString();
        }
    }

    public static class ????????? extends GainMaplePoint {

        @Override
        public String getMessage() {
            return new StringBuilder().append("!????????? <??????> <??????> - ??????????????????").toString();
        }
    }

    public static class GainMaplePoint extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 3) {
                return false;
            }
            MapleCharacter player;
            int amount = 0;
            String name = "";
            try {
                amount = Integer.parseInt(splitted[1]);
                name = splitted[2];
            } catch (Exception ex) {

            }
            int ch = World.Find.findChannel(name);
            if (ch <= 0) {
                c.getPlayer().dropMessage("?????????????????????");
                return true;
            }
            player = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            if (player == null) {
                c.getPlayer().dropMessage("?????????????????????");
                return true;
            }
            player.modifyCSPoints(2, amount, true);
            String msg = "[GM ??????] GM " + c.getPlayer().getName() + " ?????? " + player.getName() + " ???????????? " + amount + "???";
            World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, msg));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!gainmaplepoint <??????> <??????> - ??????????????????").toString();
        }
    }

    public static class GainPoint extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 3) {
                return false;
            }
            MapleCharacter player;
            int amount = 0;
            String name = "";
            try {
                amount = Integer.parseInt(splitted[1]);
                name = splitted[2];
            } catch (Exception ex) {

            }
            int ch = World.Find.findChannel(name);
            if (ch <= 0) {
                c.getPlayer().dropMessage("?????????????????????");
                return true;
            }
            player = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            if (player == null) {
                c.getPlayer().dropMessage("?????????????????????");
                return true;
            }
            player.setPoints(player.getPoints() + amount);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!gainpoint <??????> <??????> - ??????Point").toString();
        }
    }

    public static class GainVP extends GainPoint {
    }

    public static class ?????????????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length > 2) {
                int itemId = 0;
                try {
                    itemId = Integer.parseInt(splitted[1]);
                } catch (Exception ex) {

                }
                short quantity = (short) CommandProcessorUtil.getOptionalIntArg(splitted, 2, 1);
                MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                int ret = 0;
                for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                    for (MapleCharacter mch : cserv.getPlayerStorage().getAllCharacters()) {
                        if (GameConstants.isPet(itemId)) {
                            MaplePet pet = MaplePet.createPet(itemId, MapleInventoryIdentifier.getInstance());
                            if (pet != null) {
                                MapleInventoryManipulator.addById(mch.getClient(), itemId, (short) 1, "", pet, ii.getPetLife(itemId));
                            }
                        } else if (!ii.itemExists(itemId)) {
                            c.getPlayer().dropMessage(5, itemId + " - ???????????????");
                        } else {
                            IItem item;
                            //byte flag = 0;
                            //flag |= ItemFlag.LOCK.getValue();

                            if (GameConstants.getInventoryType(itemId) == MapleInventoryType.EQUIP) {
                                item = ii.randomizeStats((Equip) ii.getEquipById(itemId));
                                //item.setFlag(flag);
                            } else {
                                item = new client.inventory.Item(itemId, (byte) 0, quantity, (byte) 0);
                                if (GameConstants.getInventoryType(itemId) != MapleInventoryType.USE) {
                                    //item.setFlag(flag);
                                }
                            }
                            //item.setOwner(c.getPlayer().getName());
                            item.setGMLog(c.getPlayer().getName());

                            MapleInventoryManipulator.addbyItem(mch.getClient(), item);
                        }
                        ret++;
                    }
                }
                for (ChannelServer cserv1 : ChannelServer.getAllInstances()) {
                    for (MapleCharacter mch : cserv1.getPlayerStorage().getAllCharacters()) {
                        mch.startMapEffect(c.getPlayer().getName() + "????????????" + quantity + "???" + ii.getName(itemId) + "???????????????????????????????????????????????????", 5121009);
                    }
                }
                c.getPlayer().dropMessage(6, new StringBuilder().append("?????????????????????????????????: ").append(ret).append(" ???????????????: ").append(quantity).append(" ???").append(ii.getName(itemId)).toString());
            } else {
                c.getPlayer().dropMessage(6, "??????: !?????????????????? [??????ID] [??????] ");
            }
            return true;

        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!?????????????????? [??????ID] [??????]").toString();
        }
    }

    public static class ??? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            int itemId = 0;
            try {
                itemId = Integer.parseInt(splitted[1]);
            } catch (Exception ex) {

            }
            short quantity = (short) CommandProcessorUtil.getOptionalIntArg(splitted, 2, 1);

            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            if (GameConstants.isPet(itemId)) {
                MaplePet pet = MaplePet.createPet(itemId, MapleInventoryIdentifier.getInstance());
                if (pet != null) {
                    MapleInventoryManipulator.addById(c, itemId, (short) 1, c.getPlayer().getName(), pet, ii.getPetLife(itemId));
                }
            } else if (!ii.itemExists(itemId)) {
                c.getPlayer().dropMessage(5, itemId + " - ???????????????");
            } else {
                IItem item;
                byte flag = 0;
                flag |= ItemFlag.LOCK.getValue();

                if (GameConstants.getInventoryType(itemId) == MapleInventoryType.EQUIP) {
                    item = ii.randomizeStats((Equip) ii.getEquipById(itemId));
                    item.setFlag(flag);
                } else {
                    item = new client.inventory.Item(itemId, (byte) 0, quantity, (byte) 0);
                    if (GameConstants.getInventoryType(itemId) != MapleInventoryType.USE) {
                        item.setFlag(flag);
                    }
                }
                item.setOwner(c.getPlayer().getName());
                item.setGMLog(c.getPlayer().getName());

                MapleInventoryManipulator.addbyItem(c, item);
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!??? <??????ID> - ????????????").toString();
        }
    }

    public static class serverMsg extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length > 1) {
                StringBuilder sb = new StringBuilder();
                sb.append(StringUtil.joinStringFrom(splitted, 1));
                for (ChannelServer ch : ChannelServer.getAllInstances()) {
                    ch.setServerMessage(sb.toString());
                }
                World.Broadcast.broadcastMessage(MaplePacketCreator.serverMessage(sb.toString()));
            } else {
                return false;
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!servermsg ?????? - ????????????????????????").toString();
        }
    }

    public static class ?????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            for (final MapleMapObject mmo : c.getPlayer().getMap().getAllMonstersThreadsafe()) {
                final MapleMonster monster = (MapleMonster) mmo;
                c.getPlayer().getMap().broadcastMessage(MobPacket.moveMonster(false, 0, 0, monster.getObjectId(), monster.getPosition(), c.getPlayer().getPosition(), c.getPlayer().getLastRes()));
                monster.setPosition(c.getPlayer().getPosition());
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!?????? - ????????????").toString();
        }
    }

    public static class ItemVac extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            boolean ItemVac = c.getPlayer().getItemVac();
            if (ItemVac == false) {
                c.getPlayer().stopItemVac();
                c.getPlayer().startItemVac();
            } else {
                c.getPlayer().stopItemVac();
            }
            c.getPlayer().dropMessage(6, "????????????????????????:" + (ItemVac == false ? "??????" : "??????"));
            return true;

        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!ItemVac - ??????????????????").toString();
        }
    }

    public static class ?????????????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            final EventManager em = c.getChannelServer().getEventSM().getEventManager("AutomatedEvent");
            if (em != null) {
                em.scheduleRandomEvent();
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!?????????????????? - ??????????????????").toString();
        }
    }

    public static class ???????????? extends CommandExecute {

        private static ScheduledFuture<?> ts = null;
        private int min = 1, sec = 0;

        @Override
        public boolean execute(final MapleClient c, String splitted[]) {
            if (c.getChannelServer().getEvent() == c.getPlayer().getMapId()) {
                MapleEvent.setEvent(c.getChannelServer(), false);
                if (c.getPlayer().getMapId() == 109020001) {
                    sec = 10;
                    c.getPlayer().dropMessage(5, "??????????????????????????????????????????????????????");
                    World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, "??????:" + c.getChannel() + "???????????????????????????????????????????????????????????????"));
                    c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.getClock(sec));
                } else {
                    sec = 60;
                    c.getPlayer().dropMessage(5, "??????????????????????????????????????????????????????");
                    World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, "??????:" + c.getChannel() + "???????????????????????????????????????????????????????????????"));
                    c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.getClock(sec));
                }
                ts = EventTimer.getInstance().register(new Runnable() {

                    @Override
                    public void run() {
                        if (min == 0) {
                            MapleEvent.onStartEvent(c.getPlayer());
                            ts.cancel(false);
                            return;
                        }
                        min--;
                    }
                }, sec * 1000);
                return true;
            } else {
                c.getPlayer().dropMessage(5, "?????????????????? !???????????? ????????????????????????????????????????????????????????????????????????");
                return true;
            }
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!???????????? - ????????????").toString();
        }
    }

    public static class ???????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            final MapleEventType type = MapleEventType.getByString(splitted[1]);
            if (type == null) {
                final StringBuilder sb = new StringBuilder("????????????????????????: ");
                for (MapleEventType t : MapleEventType.values()) {
                    sb.append(t.name()).append(",");
                }
                c.getPlayer().dropMessage(5, sb.toString().substring(0, sb.toString().length() - 1));
            }
            final String msg = MapleEvent.scheduleEvent(type, c.getChannelServer());
            if (msg.length() > 0) {
                c.getPlayer().dropMessage(5, msg);
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!???????????? - ?????????????????????????????????????????????????????????0X???????????????????????????").toString();
        }
    }

    public static class LockItem extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 3) {
                return false;
            }
            MapleCharacter chr = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (chr == null) {
                c.getPlayer().dropMessage(6, "?????????????????????");
            } else {
                int itemid = Integer.parseInt(splitted[2]);
                MapleInventoryType type = GameConstants.getInventoryType(itemid);
                for (IItem item : chr.getInventory(type).listById(itemid)) {
                    item.setFlag((byte) (item.getFlag() | ItemFlag.LOCK.getValue()));
                    chr.getClient().sendPacket(MaplePacketCreator.modifyInventory(false, new ModifyInventory(ModifyInventory.Types.UPDATE, item)));
                }
                if (type == MapleInventoryType.EQUIP) {
                    type = MapleInventoryType.EQUIPPED;
                    for (IItem item : chr.getInventory(type).listById(itemid)) {
                        item.setFlag((byte) (item.getFlag() | ItemFlag.LOCK.getValue()));
                        chr.getClient().sendPacket(MaplePacketCreator.modifyInventory(false, new ModifyInventory(ModifyInventory.Types.UPDATE, item)));
                    }
                }
                c.getPlayer().dropMessage(6, "?????? " + splitted[1] + "????????????ID??? " + splitted[2] + " ???????????????????????????");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!lockitem <????????????> <??????ID> - ???????????????????????????").toString();
        }
    }

    public static class KillMap extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            for (MapleCharacter map : c.getPlayer().getMap().getCharactersThreadsafe()) {
                if (map != null) {// && !map.isGM()
                    map.getStat().setHp((short) 0);
                    map.getStat().setMp((short) 0);
                    map.updateSingleStat(MapleStat.HP, 0);
                    map.updateSingleStat(MapleStat.MP, 0);
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!killmap - ??????????????????").toString();
        }
    }

    public static class Disease extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 3) {
                //   c.getPlayer().dropMessage(6, "");
                return false;
            }
            int type;
            MapleDisease dis;
            if (splitted[1].equalsIgnoreCase("SEAL")) {
                type = 120;
            } else if (splitted[1].equalsIgnoreCase("DARKNESS")) {
                type = 121;
            } else if (splitted[1].equalsIgnoreCase("WEAKEN")) {
                type = 122;
            } else if (splitted[1].equalsIgnoreCase("STUN")) {
                type = 123;
            } else if (splitted[1].equalsIgnoreCase("CURSE")) {
                type = 124;
            } else if (splitted[1].equalsIgnoreCase("POISON")) {
                type = 125;
            } else if (splitted[1].equalsIgnoreCase("SLOW")) {
                type = 126;
            } else if (splitted[1].equalsIgnoreCase("SEDUCE")) {
                type = 128;
            } else if (splitted[1].equalsIgnoreCase("REVERSE")) {
                type = 132;
            } else if (splitted[1].equalsIgnoreCase("ZOMBIFY")) {
                type = 133;
            } else if (splitted[1].equalsIgnoreCase("POTION")) {
                type = 134;
            } else if (splitted[1].equalsIgnoreCase("SHADOW")) {
                type = 135;
            } else if (splitted[1].equalsIgnoreCase("BLIND")) {
                type = 136;
            } else if (splitted[1].equalsIgnoreCase("FREEZE")) {
                type = 137;
            } else {
                return false;
            }
            dis = MapleDisease.getByMobSkill(type);
            if (splitted.length == 4) {
                MapleCharacter victim;
                String name = splitted[2];
                int ch = World.Find.findChannel(name);
                if (ch <= 0) {
                    c.getPlayer().dropMessage(6, "??????????????????");
                    return true;
                }
                victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);

                if (victim == null) {
                    c.getPlayer().dropMessage(5, "??????????????????");
                } else {
                    victim.setChair(0);
                    victim.getClient().sendPacket(MaplePacketCreator.cancelChair(-1));
                    victim.getMap().broadcastMessage(victim, MaplePacketCreator.showChair(c.getPlayer().getId(), 0), false);
                    victim.getDiseaseBuff(dis, MobSkillFactory.getMobSkill(type, CommandProcessorUtil.getOptionalIntArg(splitted, 3, 1)));
                }
            } else {
                for (MapleCharacter victim : c.getPlayer().getMap().getCharactersThreadsafe()) {
                    victim.setChair(0);
                    victim.getClient().sendPacket(MaplePacketCreator.cancelChair(-1));
                    victim.getMap().broadcastMessage(victim, MaplePacketCreator.showChair(c.getPlayer().getId(), 0), false);
                    victim.getDiseaseBuff(dis, MobSkillFactory.getMobSkill(type, CommandProcessorUtil.getOptionalIntArg(splitted, 2, 1)));
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!disease <SEAL/DARKNESS/WEAKEN/STUN/CURSE/POISON/SLOW/SEDUCE/REVERSE/ZOMBIFY/POTION/SHADOW/BLIND/FREEZE> [????????????] <????????????> - ????????????????????????").toString();
        }

    }

    public static class SendAllNote extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {

            if (splitted.length >= 1) {
                String text = StringUtil.joinStringFrom(splitted, 1);
                for (MapleCharacter mch : c.getChannelServer().getPlayerStorage().getAllCharactersThreadSafe()) {
                    c.getPlayer().sendNote(mch.getName(), text);
                }
            } else {
                return false;
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!sendallnote <??????> ??????Note???????????????????????????").toString();
        }
    }

    public static class giveMeso extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            MapleCharacter victim;
            String name = splitted[1];
            int gain = Integer.parseInt(splitted[2]);
            int ch = World.Find.findChannel(name);
            if (ch <= 0) {
                c.getPlayer().dropMessage(6, "??????????????????");
                return true;
            }
            victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            if (victim == null) {
                c.getPlayer().dropMessage(5, "????????? '" + name);
            } else {
                victim.gainMeso(gain, false);
                String msg = "[GM ??????] GM " + c.getPlayer().getName() + " ?????? " + victim.getName() + " ?????? " + gain + "???";
                World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, msg));
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!gainmeso <??????> <??????> - ???????????????").toString();
        }
    }

    public static class ?????????????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            int gain = Integer.parseInt(splitted[1]);
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (MapleCharacter mch : cserv.getPlayerStorage().getAllCharactersThreadSafe()) {
                    mch.gainMeso(gain, true);
                }
            }
            String msg = "[GM ??????] GM " + c.getPlayer().getName() + " ?????? ???????????? ?????? " + gain + "???";
            World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, msg));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!?????????????????? <??????> - ?????????????????????").toString();
        }
    }

    public static class ????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().cloneLook();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!????????? - ???????????????").toString();
        }
    }

    public static class ??????????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().dropMessage(6, c.getPlayer().getCloneSize() + "?????????????????????.");
            c.getPlayer().disposeClones();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!??????????????? - ???????????????").toString();
        }
    }

    public static class Monitor extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (target != null) {
                if (target.getClient().isMonitored()) {
                    target.getClient().setMonitored(false);
                    c.getPlayer().dropMessage(5, "Not monitoring " + target.getName() + " anymore.");
                } else {
                    target.getClient().setMonitored(true);
                    c.getPlayer().dropMessage(5, "Monitoring " + target.getName() + ".");
                }
            } else {
                c.getPlayer().dropMessage(5, "??????????????????");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!monitor <??????> - ??????????????????").toString();
        }
    }

    public static class PermWeather extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (c.getPlayer().getMap().getPermanentWeather() > 0) {
                c.getPlayer().getMap().setPermanentWeather(0);
                c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.removeMapEffect());
                c.getPlayer().dropMessage(5, "Map weather has been disabled.");
            } else {
                final int weather = CommandProcessorUtil.getOptionalIntArg(splitted, 1, 5120000);
                if (!MapleItemInformationProvider.getInstance().itemExists(weather) || weather / 10000 != 512) {
                    c.getPlayer().dropMessage(5, "Invalid ID.");
                } else {
                    c.getPlayer().getMap().setPermanentWeather(weather);
                    c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.startMapEffect("", weather, false));
                    c.getPlayer().dropMessage(5, "Map weather has been enabled.");
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!permweather - ????????????").toString();

        }
    }

    public static class Threads extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            Thread[] threads = new Thread[Thread.activeCount()];
            Thread.enumerate(threads);
            String filter = "";
            if (splitted.length > 1) {
                filter = splitted[1];
            }
            for (int i = 0; i < threads.length; i++) {
                String tstring = threads[i].toString();
                if (tstring.toLowerCase().contains(filter.toLowerCase())) {
                    c.getPlayer().dropMessage(6, i + ": " + tstring);
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!threads - ??????Threads??????").toString();

        }
    }

    public static class ShowTrace extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            Thread[] threads = new Thread[Thread.activeCount()];
            Thread.enumerate(threads);
            Thread t = threads[Integer.parseInt(splitted[1])];
            c.getPlayer().dropMessage(6, t.toString() + ":");
            for (StackTraceElement elem : t.getStackTrace()) {
                c.getPlayer().dropMessage(6, elem.toString());
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!showtrace - show trace info").toString();

        }
    }

    public static class ToggleOffense extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }

            try {
                CheatingOffense co = CheatingOffense.valueOf(splitted[1]);
                co.setEnabled(!co.isEnabled());
            } catch (IllegalArgumentException iae) {
                c.getPlayer().dropMessage(6, "Offense " + splitted[1] + " not found");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!toggleoffense <Offense> - ???????????????CheatOffense").toString();

        }
    }

    public static class toggleDrop extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().getMap().toggleDrops();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!toggledrop - ?????????????????????").toString();

        }
    }

    public static class ToggleMegaphone extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            World.toggleMegaphoneMuteState();
            c.getPlayer().dropMessage(6, "?????????????????? : " + (c.getChannelServer().getMegaphoneMuteState() ? "???" : "???"));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!togglemegaphone - ?????????????????????").toString();

        }
    }

    public static class DCAll extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            int range = -1;
            if (splitted.length < 2) {
                return false;
            }
            String input = null;
            try {
                input = splitted[1];
            } catch (Exception ex) {

            }
            switch (splitted[1]) {
                case "m":
                    range = 0;
                    break;
                case "c":
                    range = 1;
                    break;
                case "w":
                default:
                    range = 2;
                    break;
            }
            if (range == -1) {
                range = 1;
            }
            if (range == 0) {
                c.getPlayer().getMap().disconnectAll(c.getPlayer());
            } else if (range == 1) {
                c.getChannelServer().getPlayerStorage().disconnectAll(c.getPlayer());
            } else if (range == 2) {
                for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                    cserv.getPlayerStorage().disconnectAll(true);
                }
            }
            String show = "";
            switch (range) {
                case 0:
                    show = "??????";
                    break;
                case 1:
                    show = "??????";
                    break;
                case 2:
                    show = "??????";
                    break;
            }
            String msg = "[GM ??????] GM " + c.getPlayer().getName() + "  DC ??? " + show + "??????";
            World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, msg));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!dcall [m|c|w] - ??????????????????").toString();

        }
    }

    public static class KillMonsterByOID extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            MapleMap map = c.getPlayer().getMap();
            int targetId = Integer.parseInt(splitted[1]);
            MapleMonster monster = map.getMonsterByOid(targetId);
            if (monster != null) {
                map.killMonster(monster, c.getPlayer(), false, false, (byte) 1);
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!killmonsterbyoid <moboid> - ???????????????????????????").toString();

        }
    }

    public static class HitMonsterByOID extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleMap map = c.getPlayer().getMap();
            int targetId = Integer.parseInt(splitted[1]);
            int damage = Integer.parseInt(splitted[2]);
            MapleMonster monster = map.getMonsterByOid(targetId);
            if (monster != null) {
                map.broadcastMessage(MobPacket.damageMonster(targetId, damage));
                monster.damage(c.getPlayer(), damage, false);
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!hitmonsterbyoid <moboid> <damage> - ???????????????????????????").toString();

        }
    }

    public static class NPC extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            int npcId = 0;
            try {
                npcId = Integer.parseInt(splitted[1]);
            } catch (Exception ex) {

            }
            MapleNPC npc = MapleLifeFactory.getNPC(npcId);
            if (npc != null && !npc.getName().equals("MISSINGNO")) {
                npc.setPosition(c.getPlayer().getPosition());
                npc.setCy(c.getPlayer().getPosition().y);
                npc.setRx0(c.getPlayer().getPosition().x + 50);
                npc.setRx1(c.getPlayer().getPosition().x - 50);
                npc.setFh(c.getPlayer().getMap().getFootholds().findBelow(c.getPlayer().getPosition()).getId());
                npc.setCustom(true);
                c.getPlayer().getMap().addMapObject(npc);
                c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.spawnNPC(npc, true));
            } else {
                c.getPlayer().dropMessage(6, "?????????????????????" + npcId + "???Npc");

            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!npc <npcid> - ?????????NPC").toString();
        }
    }

    public static class MakePNPC extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 3) {
                return false;
            }
            try {
                c.getPlayer().dropMessage(6, "Making playerNPC...");
                MapleCharacter chhr;
                String name = splitted[1];
                int ch = World.Find.findChannel(name);
                if (ch <= 0) {
                    c.getPlayer().dropMessage(6, "??????????????????");
                    return true;
                }
                chhr = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);

                if (chhr == null) {
                    c.getPlayer().dropMessage(6, splitted[1] + " is not online");
                } else {
                    int npcId = Integer.parseInt(splitted[2]);
                    MapleNPC npc_c = MapleLifeFactory.getNPC(npcId);
                    //if (npc_c == null || npc_c.getName().equals("MISSINGNO")) {
                    //c.getPlayer().dropMessage(6, "NPC?????????");
                    //     return true;
                    //  }
                    if (!(npcId >= 9901500 && npcId <= 9901551) || !(npcId >= 9901000 && npcId >= 9901319)) {
                        c.getPlayer().dropMessage(6, "NPC?????????");
                        return true;
                    }
                    PlayerNPC npc = new PlayerNPC(chhr, npcId, c.getPlayer().getMap(), c.getPlayer());
                    npc.addToServer();
                    c.getPlayer().dropMessage(6, "Done");
                }
            } catch (Exception e) {
                c.getPlayer().dropMessage(6, "NPC failed... : " + e.getMessage());

            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!??? <playername> <npcid> - ????????????NPC").toString();
        }
    }

    public static class MakeOfflineP extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            try {
                c.getPlayer().dropMessage(6, "Making playerNPC...");
                MapleClient cs = new MapleClient(null, null, new MockIOSession());
                MapleCharacter chhr = MapleCharacter.loadCharFromDB(MapleCharacterUtil.getIdByName(splitted[1]), cs, false);
                if (chhr == null) {
                    c.getPlayer().dropMessage(6, splitted[1] + " does not exist");

                } else {
                    int npcId = Integer.parseInt(splitted[2]);
                    if (!(npcId >= 9901500 && npcId <= 9901551) || !(npcId >= 9901000 && npcId >= 9901319)) {
                        c.getPlayer().dropMessage(6, "NPC?????????");
                        return true;
                    }
                    PlayerNPC npc = new PlayerNPC(chhr, npcId, c.getPlayer().getMap(), c.getPlayer());
                    npc.addToServer();
                    c.getPlayer().dropMessage(6, "Done");
                }
            } catch (Exception e) {
                c.getPlayer().dropMessage(6, "NPC failed... : " + e.getMessage());
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!deletepnpc <charname> <npcid> - ????????????PNPC").toString();
        }
    }

    public static class ????????????NPC extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            try {
                c.getPlayer().dropMessage(6, "Destroying playerNPC...");
                final MapleNPC npc = c.getPlayer().getMap().getNPCByOid(Integer.parseInt(splitted[1]));
                if (npc instanceof PlayerNPC) {
                    ((PlayerNPC) npc).destroy(true);
                    c.getPlayer().dropMessage(6, "Done");
                } else {
                    c.getPlayer().dropMessage(6, "!destroypnpc [objectid]");
                }
            } catch (Exception e) {
                c.getPlayer().dropMessage(6, "NPC failed... : " + e.getMessage());
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!????????????NPC [objectid] - ??????PNPC").toString();
        }

    }

    public static class ????????????2 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            int mid = 0;
            try {
                mid = Integer.parseInt(splitted[1]);
            } catch (Exception ex) {

            }
            int num = Math.min(CommandProcessorUtil.getOptionalIntArg(splitted, 2, 1), 500);
            if (num > 1000) {
                num = 1000;
            }
            Long hp = CommandProcessorUtil.getNamedLongArg(splitted, 1, "hp");
            Integer mp = CommandProcessorUtil.getNamedIntArg(splitted, 1, "mp");
            Integer exp = CommandProcessorUtil.getNamedIntArg(splitted, 1, "exp");
            Double php = CommandProcessorUtil.getNamedDoubleArg(splitted, 1, "php");
            Double pmp = CommandProcessorUtil.getNamedDoubleArg(splitted, 1, "pmp");
            Double pexp = CommandProcessorUtil.getNamedDoubleArg(splitted, 1, "pexp");
            MapleMonster onemob;
            try {
                onemob = MapleLifeFactory.getMonster(mid);
            } catch (RuntimeException e) {
                c.getPlayer().dropMessage(5, "??????: " + e.getMessage());
                return true;
            }

            long newhp;
            int newexp, newmp;
            if (hp != null) {
                newhp = hp;
            } else if (php != null) {
                newhp = (long) (onemob.getMobMaxHp() * (php / 100));
            } else {
                newhp = onemob.getMobMaxHp();
            }
            if (mp != null) {
                newmp = mp;
            } else if (pmp != null) {
                newmp = (int) (onemob.getMobMaxMp() * (pmp / 100));
            } else {
                newmp = onemob.getMobMaxMp();
            }
            if (exp != null) {
                newexp = exp;
            } else if (pexp != null) {
                newexp = (int) (onemob.getMobExp() * (pexp / 100));
            } else {
                newexp = onemob.getMobExp();
            }
            if (newhp < 1) {
                newhp = 1;
            }

            final OverrideMonsterStats overrideStats = new OverrideMonsterStats(newhp, onemob.getMobMaxMp(), newexp, false);
            for (int i = 0; i < num; i++) {
                MapleMonster mob = MapleLifeFactory.getMonster(mid);
                mob.setHp(newhp);
                mob.setOverrideStats(overrideStats);
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, c.getPlayer().getPosition());
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!????????????2 <??????ID> <hp|exp|php||pexp = ?> - ????????????").toString();
        }
    }

    public static class ?????????????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            try {
                final MapleMap target = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted[1]));
                final MapleMap from = c.getPlayer().getMap();
                for (MapleCharacter chr : from.getCharactersThreadsafe()) {
                    chr.changeMap(target, target.getPortal(0));
                }
            } catch (Exception e) {
                return false; //assume drunk GM
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!?????????????????? <maipid> ????????????????????????????????????").toString();
        }
    }

    public static class ????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            for (ChannelServer CS : ChannelServer.getAllInstances()) {
                for (MapleCharacter mch : CS.getPlayerStorage().getAllCharactersThreadSafe()) {
                    if (mch.getMapId() != c.getPlayer().getMapId()) {
                        mch.changeMap(c.getPlayer().getMap(), c.getPlayer().getPosition());
                    }
                    if (mch.getClient().getChannel() != c.getPlayer().getClient().getChannel()) {
                        mch.changeChannel(c.getPlayer().getClient().getChannel());
                    }
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!????????? ??????????????????????????????").toString();
        }
    }

    public static class LOLCastle extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length != 2) {
                return false;
            }
            MapleMap target = c.getChannelServer().getEventSM().getEventManager("lolcastle").getInstance("lolcastle" + splitted[1]).getMapFactory().getMap(990000300, false, false);
            c.getPlayer().changeMap(target, target.getPortal(0));

            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!lolcastle level (level = 1-5) - ???????????????").toString();
        }

    }

    public static class StartProfiling extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            CPUSampler sampler = CPUSampler.getInstance();
            sampler.addIncluded("client");
            sampler.addIncluded("constants"); //or should we do Packages.constants etc.?
            sampler.addIncluded("database");
            sampler.addIncluded("handling");
            sampler.addIncluded("provider");
            sampler.addIncluded("scripting");
            sampler.addIncluded("server");
            sampler.addIncluded("tools");
            sampler.start();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!startprofiling ????????????JVM??????").toString();
        }
    }

    public static class StopProfiling extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            CPUSampler sampler = CPUSampler.getInstance();
            try {
                String filename = "odinprofile.txt";
                if (splitted.length > 1) {
                    filename = splitted[1];
                }
                File file = new File(filename);
                if (file.exists()) {
                    c.getPlayer().dropMessage(6, "The entered filename already exists, choose a different one");
                    return true;
                }
                sampler.stop();
                try (FileWriter fw = new FileWriter(file)) {
                    sampler.save(fw, 1, 10);
                }
            } catch (IOException e) {
                System.err.println("Error saving profile" + e);
            }
            sampler.reset();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!stopprofiling <filename> - ????????????JVM????????????????????????").toString();
        }
    }

    public static class ???????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            final int mapId = Integer.parseInt(splitted[1]);
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                if (cserv.getMapFactory().isMapLoaded(mapId) && cserv.getMapFactory().getMap(mapId).getCharactersSize() > 0) {
                    c.getPlayer().dropMessage(5, "There exists characters on channel " + cserv.getChannel());
                    return true;
                }
            }
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                if (cserv.getMapFactory().isMapLoaded(mapId)) {
                    cserv.getMapFactory().removeMap(mapId);
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!???????????? <maipid> - ??????????????????").toString();
        }
    }

    public static class Respawn extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().getMap().respawn(true);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!respawn - ??????????????????").toString();
        }
    }

    public static class ResetMap extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().getMap().resetFully();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!respawn - ??????????????????").toString();
        }
    }

    public static class ??????NPC extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {

            int npcId = Integer.parseInt(splitted[1]);
            MapleNPC npc = MapleLifeFactory.getNPC(npcId);
            if (npc != null && !npc.getName().equals("MISSINGNO")) {
                final int xpos = c.getPlayer().getPosition().x;
                final int ypos = c.getPlayer().getPosition().y;
                final int fh = c.getPlayer().getMap().getFootholds().findBelow(c.getPlayer().getPosition()).getId();
                npc.setPosition(c.getPlayer().getPosition());
                npc.setCy(ypos);
                npc.setRx0(xpos);
                npc.setRx1(xpos);
                npc.setFh(fh);
                npc.setCustom(true);
                try (Connection con = DatabaseConnection.getConnection()) {
                    try (PreparedStatement ps = con.prepareStatement("INSERT INTO wz_customlife (dataid, f, hide, fh, cy, rx0, rx1, type, x, y, mid) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                        ps.setInt(1, npcId);
                        ps.setInt(2, 0); // 1 = right , 0 = left
                        ps.setInt(3, 0); // 1 = hide, 0 = show
                        ps.setInt(4, fh);
                        ps.setInt(5, ypos);
                        ps.setInt(6, xpos);
                        ps.setInt(7, xpos);
                        ps.setString(8, "n");
                        ps.setInt(9, xpos);
                        ps.setInt(10, ypos);
                        ps.setInt(11, c.getPlayer().getMapId());
                        ps.executeUpdate();
                    }
                } catch (SQLException e) {
                    c.getPlayer().dropMessage(6, "Failed to save NPC to the database");
                    FileoutputUtil.outError("logs/???????????????.txt", e);
                }
                for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                    cserv.getMapFactory().getMap(c.getPlayer().getMapId()).addMapObject(npc);
                    cserv.getMapFactory().getMap(c.getPlayer().getMapId()).broadcastMessage(MaplePacketCreator.spawnNPC(npc, true));
//                    c.getPlayer().getMap().addMapObject(npc);
//                    c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.spawnNPC(npc, true));
                }
                c.getPlayer().dropMessage(6, "Please do not reload this map or else the NPC will disappear till the next restart.");
            } else {
                c.getPlayer().dropMessage(6, "????????? Npc ");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!??????NPC - ????????????NPC").toString();
        }
    }

    public static class autoban extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            PiPiConfig.setAutoban(!PiPiConfig.getAutoban());
            c.getPlayer().dropMessage("????????????: " + (PiPiConfig.getAutoban() ? "??????" : "??????"));
            System.out.println("????????????: " + (PiPiConfig.getAutoban() ? "??????" : "??????"));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!Autoban - ??????????????????").toString();
        }
    }

    public static class Packet extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            int packetheader = Integer.parseInt(splitted[1]);
            String packet_in = " 00 00 00 00 00 00 00 00 00 ";
            if (splitted.length > 2) {
                packet_in = StringUtil.joinStringFrom(splitted, 2);
            }

            mplew.writeShort(packetheader);
            mplew.write(HexTool.getByteArrayFromHexString(packet_in));
            mplew.writeZeroBytes(20);
            c.sendPacket(mplew.getPacket());
            c.getPlayer().dropMessage(packetheader + "???????????????[" + packetheader + "] : " + mplew.toString());
            return true;
        }

        public String getMessage() {
            return new StringBuilder().append("!Packet - <????????????>").toString();
        }
    }

    public static class ?????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            c.getPlayer().gainMeso(Integer.MAX_VALUE - c.getPlayer().getMeso(), true);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!?????? - ?????????").toString();
        }
    }

    public static class mesos extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                return false;
            }
            int meso = 0;
            try {
                meso = Integer.parseInt(splitted[1]);
            } catch (Exception ex) {
            }
            c.getPlayer().gainMeso(meso, true);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!mesos <???????????????> - ????????????").toString();
        }
    }

    public static class ??? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            int itemId = 0;
            String name = null;
            try {
                itemId = Integer.parseInt(splitted[1]);
                name = splitted[3];
            } catch (Exception ex) {
            }

            final short quantity = (short) CommandProcessorUtil.getOptionalIntArg(splitted, 2, 1);
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            if (GameConstants.isPet(itemId)) {
                c.getPlayer().dropMessage(5, "??????????????????????????????.");
            } else if (!ii.itemExists(itemId)) {
                c.getPlayer().dropMessage(5, itemId + " - ???????????????");
            } else {
                IItem toDrop;
                if (GameConstants.getInventoryType(itemId) == MapleInventoryType.EQUIP) {
                    toDrop = ii.randomizeStats((Equip) ii.getEquipById(itemId));
                } else {
                    toDrop = new client.inventory.Item(itemId, (byte) 0, (short) quantity, (byte) 0);
                }
                //toDrop.setOwner(c.getPlayer().getName());
                toDrop.setGMLog(c.getPlayer().getName());
                if (name != null) {
                    int ch = World.Find.findChannel(name);
                    if (ch > 0) {
                        MapleCharacter victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
                        if (victim != null) {
                            victim.getMap().spawnItemDrop(victim, victim, toDrop, victim.getPosition(), true, true);
                        }
                    } else {
                        c.getPlayer().dropMessage("??????: [" + name + "] ???????????????");
                    }
                } else {
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), toDrop, c.getPlayer().getPosition(), true, true);
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!??? <??????ID> - ????????????").toString();
        }
    }

    public static class ???????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 3) {
                return false;
            }
            int itemId = 0;
            int quantity = 1;
            int Str = 0;
            int Dex = 0;
            int Int = 0;
            int Luk = 0;
            int HP = 0;
            int MP = 0;
            int Watk = 0;
            int Matk = 0;
            int Wdef = 0;
            int Mdef = 0;
            int Scroll = 0;
            int Upg = 0;
            int Acc = 0;
            int Avoid = 0;
            int jump = 0;
            int speed = 0;
            int day = 0;
            try {
                int splitted_count = 1;
                itemId = Integer.parseInt(splitted[splitted_count++]);
                Str = Integer.parseInt(splitted[splitted_count++]);
                Dex = Integer.parseInt(splitted[splitted_count++]);
                Int = Integer.parseInt(splitted[splitted_count++]);
                Luk = Integer.parseInt(splitted[splitted_count++]);
                HP = Integer.parseInt(splitted[splitted_count++]);
                MP = Integer.parseInt(splitted[splitted_count++]);
                Watk = Integer.parseInt(splitted[splitted_count++]);
                Matk = Integer.parseInt(splitted[splitted_count++]);
                Wdef = Integer.parseInt(splitted[splitted_count++]);
                Mdef = Integer.parseInt(splitted[splitted_count++]);
                Upg = Integer.parseInt(splitted[splitted_count++]);
                Acc = Integer.parseInt(splitted[splitted_count++]);
                Avoid = Integer.parseInt(splitted[splitted_count++]);
                speed = Integer.parseInt(splitted[splitted_count++]);
                jump = Integer.parseInt(splitted[splitted_count++]);
                Scroll = Integer.parseInt(splitted[splitted_count++]);
                day = Integer.parseInt(splitted[splitted_count++]);
            } catch (Exception ex) {
                //   ex.printStackTrace();
            }
            boolean Str_check = Str != 0;
            boolean Int_check = Int != 0;
            boolean Dex_check = Dex != 0;
            boolean Luk_check = Luk != 0;
            boolean HP_check = HP != 0;
            boolean MP_check = MP != 0;
            boolean WATK_check = Watk != 0;
            boolean MATK_check = Matk != 0;
            boolean WDEF_check = Wdef != 0;
            boolean MDEF_check = Mdef != 0;
            boolean SCROLL_check = true;
            boolean UPG_check = Upg != 0;
            boolean ACC_check = Acc != 0;
            boolean AVOID_check = Avoid != 0;
            boolean JUMP_check = jump != 0;
            boolean SPEED_check = speed != 0;
            boolean DAY_check = day != 0;
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            if (GameConstants.isPet(itemId)) {
                c.getPlayer().dropMessage(5, "????????????????????????.");
                return true;
            } else if (!ii.itemExists(itemId)) {
                c.getPlayer().dropMessage(5, itemId + " ?????????");
                return true;
            }
            IItem toDrop;
            Equip equip;
            if (GameConstants.getInventoryType(itemId) == MapleInventoryType.EQUIP) {// ?????????????????????
                equip = ii.randomizeStats((Equip) ii.getEquipById(itemId));
                equip.setGMLog(c.getPlayer().getName() + " ?????? !Prodrop");
                if (Str_check) {
                    equip.setStr((short) Str);
                }
                if (Luk_check) {
                    equip.setLuk((short) Luk);
                }
                if (Dex_check) {
                    equip.setDex((short) Dex);
                }
                if (Int_check) {
                    equip.setInt((short) Int);
                }
                if (HP_check) {
                    equip.setHp((short) HP);
                }
                if (MP_check) {
                    equip.setMp((short) MP);
                }
                if (WATK_check) {
                    equip.setWatk((short) Watk);
                }
                if (MATK_check) {
                    equip.setMatk((short) Matk);
                }
                if (WDEF_check) {
                    equip.setWdef((short) Wdef);
                }
                if (MDEF_check) {
                    equip.setMdef((short) Mdef);
                }
                if (ACC_check) {
                    equip.setAcc((short) Acc);
                }
                if (AVOID_check) {
                    equip.setAvoid((short) Avoid);
                }
                if (SCROLL_check) {
                    equip.setUpgradeSlots((byte) Scroll);
                }
                if (UPG_check) {
                    equip.setLevel((byte) Upg);
                }
                if (JUMP_check) {
                    equip.setJump((short) jump);
                }
                if (SPEED_check) {
                    equip.setSpeed((short) speed);
                }
                if (DAY_check) {
                    equip.setExpiration(System.currentTimeMillis() + (day * 24 * 60 * 60 * 1000));
                }
                c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), equip, c.getPlayer().getPosition(), true, true);
            } else {
                toDrop = new client.inventory.Item(itemId, (byte) 0, (short) quantity, (byte) 0);
                toDrop.setGMLog(c.getPlayer().getName() + " ?????? !Prodrop");
                c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), toDrop, c.getPlayer().getPosition(), true, true);
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!???????????? <????????????> (<??????> <??????> <??????> <??????> <HP> <MP> <??????> <??????> <??????> <??????> <??????+x> <??????> <??????> <??????> <??????> <?????????> <??????>)").toString();
        }
    }

    public static class ????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 4) {
                return false;
            }
            String error = null;
            String input = splitted[1];
            String name = splitted[2];
            int nx = 0;
            int gain = 0;
            try {
                switch (input) {
                    case "??????":
                        nx = 1;
                        break;
                    case "??????":
                        nx = 2;
                        break;
                    default:
                        error = "?????????????????????[??????]???[??????] ??????[" + input + "]";
                        break;
                }
                gain = Integer.parseInt(splitted[3]);
            } catch (Exception ex) {
                error = "???????????????????????????????????????2147483647??? " + input + " ?????????: " + ex.toString();
            }
            if (error != null) {
                c.getPlayer().dropMessage(error);
                return true;
            }

            int ch = World.Find.findChannel(name);
            if (ch <= 0) {
                c.getPlayer().dropMessage("??????????????????");
                return true;
            }
            MapleCharacter victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            if (victim == null) {
                c.getPlayer().dropMessage("??????????????????");
            } else {
                c.getPlayer().dropMessage("??????????????????[" + name + "] " + input + " " + gain);
                FileoutputUtil.logToFile("logs/Data/?????????.txt", "\r\n " + FileoutputUtil.NowTime() + " GM " + c.getPlayer().getName() + " ?????? " + victim.getName() + " " + input + " " + gain + "???");
                victim.modifyCSPoints(nx, gain, true);
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!????????? ??????/?????? ???????????? ??????").toString();
        }
    }

    public static class ResetMobs extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().getMap().killAllMonsters(false);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!resetmobs - ???????????????????????????").toString();
        }
    }

    public static class ??????????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MaplePortal portal = c.getPlayer().getMap().findClosestPortal(c.getPlayer().getTruePosition());
            c.getPlayer().dropMessage(-11, portal.getName() + " id: " + portal.getId() + " script: " + portal.getScriptName());
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!??????????????? - ????????????????????????").toString();
        }
    }

    public static class ?????????????????? extends CashEveryone {
    }

    public static class CashEveryone extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length > 2) {
                int type = Integer.parseInt(splitted[1]);
                int quantity = Integer.parseInt(splitted[2]);
                if (type == 1) {
                    type = 1;
                } else if (type == 2) {
                    type = 2;
                } else {
                    c.getPlayer().dropMessage(6, "??????: !?????????????????? [????????????1-2] [????????????] 1???GASH.2???????????????");
                    return true;
                }
                if (quantity > 10000) {
                    quantity = 10000;
                }
                int ret = 0;
                for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                    for (MapleCharacter mch : cserv.getPlayerStorage().getAllCharacters()) {
                        mch.modifyCSPoints(type, quantity);
                        ret++;
                    }
                }
                String show = type == 1 ? "GASH" : "????????????";
                for (ChannelServer cserv1 : ChannelServer.getAllInstances()) {
                    for (MapleCharacter mch : cserv1.getPlayerStorage().getAllCharacters()) {
                        mch.startMapEffect("???????????????" + quantity + show + "???????????????????????????????????????????????????", 5121009);
                    }
                }
                c.getPlayer().dropMessage(6, new StringBuilder().append("?????????????????????????????????: ").append(ret).append(" ???????????????: ").append(quantity).append(" ??????").append(type == 1 ? "GASH " : " ???????????? ").append(" ??????: ").append(ret * quantity).toString());
            } else {
                c.getPlayer().dropMessage(6, "??????: !?????????????????? [????????????1-2] [????????????] 1???GASH.2???????????????");
            }
            return true;

        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!?????????????????? - [????????????1-2] [????????????]").toString();
        }
    }

    public static class ?????????????????? extends CashMap {
    }

    public static class CashMap extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length > 2) {
                int type = Integer.parseInt(splitted[1]);
                int quantity = Integer.parseInt(splitted[2]);
                if (type == 1) {
                    type = 1;
                } else if (type == 2) {
                    type = 2;
                } else {
                    c.getPlayer().dropMessage(6, "??????: !?????????????????? [????????????1-2] [????????????] 1???GASH.2???????????????");
                    return true;
                }
                if (quantity > 10000) {
                    quantity = 10000;
                }
                int ret = 0;

                final MapleMap from = c.getPlayer().getMap();
                for (MapleCharacter chrr : from.getCharactersThreadsafe()) {
                    chrr.modifyCSPoints(type, quantity);
                    ret++;
                }

                String show = type == 1 ? "GASH" : "????????????";
                final MapleMap froma = c.getPlayer().getMap();
                for (MapleCharacter chrrr : froma.getCharactersThreadsafe()) {
                    chrrr.startMapEffect("???????????????" + quantity + show + "???????????????????????????????????????????????????", 5121009);
                }

                c.getPlayer().dropMessage(6, new StringBuilder().append("?????????????????????????????????: ").append(ret).append(" ???????????????: ").append(quantity).append(" ??????").append(type == 1 ? "GASH " : " ???????????? ").append(" ??????: ").append(ret * quantity).toString());
            } else {
                c.getPlayer().dropMessage(6, "??????: !?????????????????? [????????????1-2] [????????????] 1???GASH.2???????????????");
            }
            return true;

        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!?????????????????? - [????????????1-2] [????????????]").toString();
        }
    }

    public static class ?????????????????? extends MesoMap {
    }

    public static class MesoMap extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length > 1) {
                int quantity = Integer.parseInt(splitted[1]);
                int ret = 0;
                final MapleMap from = c.getPlayer().getMap();
                for (MapleCharacter chrr : from.getCharactersThreadsafe()) {
                    chrr.gainMeso(quantity, true);
                    ret++;
                }

                final MapleMap froma = c.getPlayer().getMap();
                for (MapleCharacter chrrr : froma.getCharactersThreadsafe()) {
                    chrrr.startMapEffect("???????????????" + quantity + "?????????????????????????????????????????????????????????", 5121009);
                }

                c.getPlayer().dropMessage(6, new StringBuilder().append("?????????????????????????????????: ").append(ret).append(" ???????????????: ").append(quantity).append(" ??????").append(" ??????: ").append(ret * quantity).toString());
            } else {
                c.getPlayer().dropMessage(6, "??????: !?????????????????? [??????]");
            }
            return true;

        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!?????????????????? - [??????]").toString();
        }
    }
    
    public static class ?????????????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length > 1) {
                int quantity = Integer.parseInt(splitted[1]);
                int ret = 0;
                final MapleMap from = c.getPlayer().getMap();
                for (MapleCharacter chrr : from.getCharactersThreadsafe()) {
                    chrr.modifyCSPoints(3, quantity, true);
                    ret++;
                }

                final MapleMap froma = c.getPlayer().getMap();
                for (MapleCharacter chrrr : froma.getCharactersThreadsafe()) {
                    chrrr.startMapEffect("????????? ??????" + quantity + "???????????????????????????????????????????????????????????????", 5120009);
                }

                c.getPlayer().dropMessage(6, new StringBuilder().append("?????????????????????????????????: ").append(ret).append(" ???????????????: ").append(quantity).append(" ??????").append(" ??????: ").append(ret * quantity).toString());
            } else {
                c.getPlayer().dropMessage(6, "??????: !?????????????????? [??????]");
            }
            return true;

        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!?????????????????? - [??????]").toString();
        }
    }

    public static class ??????????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 3) {
                return false;
            }
            String name = splitted[1];
            int gain = Integer.parseInt(splitted[2]);

            int accid = MapleClient.findAccIdForCharacterName(name);
            int tgjf = MapleClient.getTGJF(accid);

            if (tgjf > 0) {
                c.getPlayer().dropMessage("??????[" + name + "] ?????????????????????0???");
                return true;
            }
            byte ret;
            ret = MapleClient.setTGJF(name, gain);

            if (ret == -2) {
                c.getPlayer().dropMessage(6, "[" + "???????????????" + "] SQL ??????");
            } else if (ret == -1) {
                c.getPlayer().dropMessage(6, "[" + "???????????????" + "] ?????????????????????");
            } else {
                c.getPlayer().dropMessage(6, "[" + "???????????????" + "] ????????????????????????");
            }

            c.getPlayer().dropMessage("??????????????????[" + name + "] " + gain + " " + "????????????");
            FileoutputUtil.logToFile("logs/Data/???????????????.txt", "\r\n " + FileoutputUtil.NowTime() + " GM " + c.getPlayer().getName() + " ?????? " + name + " " + gain + "????????????");

            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!??????????????? ???????????? ??????").toString();
        }
    }

    public static class ???FB?????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 3) {
                return false;
            }
            String name = splitted[1];
            int gain = Integer.parseInt(splitted[2]);

            int accid = MapleClient.findAccIdForCharacterName(name);
            int tjjf = MapleClient.getTJJF(accid);

            if (tjjf > 0) {
                c.getPlayer().dropMessage("??????[" + name + "] FB???????????????0???");
                return true;
            }
            byte ret;
            ret = MapleClient.setTJJF(name, gain);

            if (ret == -2) {
                c.getPlayer().dropMessage(6, "[" + "???FB??????" + "] SQL ??????");
            } else if (ret == -1) {
                c.getPlayer().dropMessage(6, "[" + "???FB??????" + "] ?????????????????????");
            } else {
                c.getPlayer().dropMessage(6, "[" + "???FB??????" + "] ????????????FB??????");
            }

            c.getPlayer().dropMessage("??????????????????[" + name + "] " + gain + " " + "FB??????");
            FileoutputUtil.logToFile("logs/Data/???FB??????.txt", "\r\n " + FileoutputUtil.NowTime() + " GM " + c.getPlayer().getName() + " ?????? " + name + " " + gain + "FB??????");

            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!??????????????? ???????????? ??????").toString();
        }
    }

    public static class ??????????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            int ret = 0;
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (MapleCharacter mch : cserv.getPlayerStorage().getAllCharacters()) {
                    mch.startLieDetector(false);
                    ret++;
                }
            }

            c.getPlayer().dropMessage(6, new StringBuilder().append("?????????????????????????????????: ").append(ret).append(" ??????????????????").toString());
            return true;

        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!??????????????? - [???????????????]").toString();
        }
    }

    public static class ??????????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            GashaponFactory.getInstance().reloadGashapons();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!??????????????? - ?????????????????????").toString();
        }
    }

}

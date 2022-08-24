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
package handling.channel.handler;

import java.util.List;

import client.BuddyEntry;
import client.MapleCharacter;
import client.MapleClient;
import client.MapleQuestStatus;
import client.SkillFactory;
import constants.GameConstants;
import constants.WorldConstants;
import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.login.LoginServer;
import handling.world.CharacterTransfer;
import handling.world.MapleMessenger;
import handling.world.MapleMessengerCharacter;
import handling.world.CharacterIdChannelPair;
import handling.world.MaplePartyCharacter;
import handling.world.PartyOperation;
import handling.world.PlayerBuffStorage;
import handling.world.World;
import handling.world.guild.MapleGuild;
import java.util.Collection;
import scripting.NPCScriptManager;
import static scripting.tools.Exp_main.全服漂浮喇叭;
import server.ServerProperties;
import server.Start;
import server.maps.FieldLimitType;
import tools.FilePrinter;
import tools.FileoutputUtil;
import tools.MaplePacketCreator;
import tools.data.LittleEndianAccessor;
import tools.packet.FamilyPacket;

public class InterServerHandler {

    public static final void EnterCashShop(final MapleClient c, final MapleCharacter chr, final boolean mts) {
        if (c.getCloseSession()) {
            return;
        }
        if (World.isShutDown && chr.isGM() == false) {
            c.sendPacket(MaplePacketCreator.serverBlocked(2));
            c.sendPacket(MaplePacketCreator.enableActions());
            return;
        }
        if (!WorldConstants.CS_ENABLE && chr.isGM() == false || mts) {
            c.sendPacket(MaplePacketCreator.serverBlocked(2));
            c.sendPacket(MaplePacketCreator.enableActions());
            return;
        }
        if ((chr == null) || (chr.getMap() == null) || (chr.getEventInstance() != null) || (c.getChannelServer() == null)) {
            c.sendPacket(MaplePacketCreator.serverBlocked(2));
            c.sendPacket(MaplePacketCreator.enableActions());
            return;
        }

        //if (World.getPendingCharacterSize() >= 80) {
        //    chr.dropMessage(1, "伺服器繁忙，請稍後再試。");
        //    c.sendPacket(MaplePacketCreator.enableActions());
        //    return;
        //}
        if (chr.getAntiMacro().inProgress()) {
            c.getPlayer().dropMessage(1, "被使用测谎仪时无法操作。");
            c.sendPacket(MaplePacketCreator.enableActions());
            return;
        }
        int num = 0;
        for (int i = 1; i <= 5; i++) {
            for (int i1 = 0; i1 <= 96; i1++) {
                if (chr.getInventory(i).getItem((short) i1) != null) {
                    num += chr.getInventory(i).getItem((short) i1).getQuantity();
                }
            }
        }
        if (num >= 80000) {
            c.getPlayer().dropMessage(1, "尊敬的冒险家你好\r\n系统检测到您当前进入商城有崩溃风险\r\n请您清理背包数值小于7万再尝试进入\r\n当前背包数值：" + num);
            c.sendPacket(MaplePacketCreator.enableActions());
            return;
        }

        try {
            //int res = 
            chr.saveToDB(false, false);
            //if (res == 1) {
            //chr.dropMessage(5, "角色保存成功！");
            //}
        } catch (Exception ex) {
            FileoutputUtil.logToFile("logs/進入商城保存數據異常.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號 " + c.getAccountName() + " 帳號ID " + c.getAccID() + " 角色名 " + chr.getName() + " 角色ID " + chr.getId());
            FileoutputUtil.outError("logs/進入商城保存數據異常.txt", ex);
        }
        final ChannelServer ch = ChannelServer.getInstance(c.getChannel());

        //if (chr != null && ch != null) {
        chr.dispelBuff();
        chr.changeRemoval();
        //chr.updateBuffTime();

        if (chr.getMessenger() != null) {
            MapleMessengerCharacter messengerplayer = new MapleMessengerCharacter(chr);
            World.Messenger.leaveMessenger(chr.getMessenger().getId(), messengerplayer);
        }
        PlayerBuffStorage.addBuffsToStorage(chr.getId(), chr.getAllBuffs());
        PlayerBuffStorage.addCooldownsToStorage(chr.getId(), chr.getCooldowns());
        PlayerBuffStorage.addDiseaseToStorage(chr.getId(), chr.getAllDiseases());
        World.channelChangeData(new CharacterTransfer(chr), chr.getId(), mts ? -20 : -10);
        ch.removePlayer(chr);
        c.updateLoginState(MapleClient.CHANGE_CHANNEL, c.getSessionIPAddress());

        chr.getMap().removePlayer(chr);
        c.sendPacket(MaplePacketCreator.getChannelChange(c, Integer.parseInt(CashShopServer.getIP().split(":")[1])));
        c.getPlayer().expirationTask(true, false);
        c.setPlayer(null);
        c.setReceiving(false);
        //} 
    }
 /*   public static final void EnterMTS(final MapleClient c, final MapleCharacter chr) {   
        NPCScriptManager.getInstance().dispose(c);
        if (c.getPlayer().getTrade() != null) {
            c.getPlayer().dropMessage(1, "交易中无法进行其他操作！");
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
                if (c.getPlayer().getLevel() >= 8) {
                    NPCScriptManager.getInstance().start(c, 9900004);//拍卖npc
                 //   c.getSession().write(MaplePacketCreator.enableActions());
                    c.getSession().writeAndFlush(tools.MaplePacketCreator.enableActions());
                } else {
                  //  c.getSession().write(MaplePacketCreator.getNPCTalk(9900004, (byte) 0, "玩家你好.等级不足8级无法使用快捷功能.", "00 00", (byte) 0));
                    c.getPlayer().dropMessage(1, "玩家你好.等级不足8级无法使用快捷功能1");
                    c.getSession().write(MaplePacketCreator.enableActions());
        }
                }*/
    /**
     *
     * @param playerid - 玩家ID
     * @param c - 客戶端 Client
     */
    public static final void LoggedIn(final int playerid, final MapleClient c) {
        //if (c.loadLogGedin(c.getAccID()) == 1 || c.loadLogGedin(c.getAccID()) > 2) {
        //c.getSession().close();
        //return;
        //}
        if (c.getCloseSession()) {
            return;
        }
        final ChannelServer channelServer = c.getChannelServer();
        MapleCharacter player;
        final CharacterTransfer transfer = channelServer.getPlayerStorage().getPendingCharacter(playerid);

        if (transfer == null) { // Player isn't in storage, probably isn't CC

            List<String> charNamesa = c.loadCharacterNamesByCharId(playerid);
            for (ChannelServer cs : ChannelServer.getAllInstances()) {
                for (final String name : charNamesa) {
                    if (cs.getPlayerStorage().getCharacterByName(name) != null) {
                        FileoutputUtil.logToFile("logs/Data/非法登錄.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號 " + c.getAccountName() + "登錄1");
                        World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM 密語系統] 非法登錄 帳號 " + c.getAccountName()));
                        c.getSession().close();
                        return;
                    }
                }
            }
            for (final String name : charNamesa) {
                if (CashShopServer.getPlayerStorage().getCharacterByName(name) != null) {
                    FileoutputUtil.logToFile("logs/Data/非法登錄.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號 " + c.getAccountName() + "登錄1");
                    World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM 密語系統] 非法登錄 帳號 " + c.getAccountName()));
                    c.getSession().close();
                    return;
                }
            }

            List<String> charNames = c.loadCharacterNamesByCharId(playerid);
            for (ChannelServer cs : ChannelServer.getAllInstances()) {
                for (final String name : charNames) {
                    MapleCharacter character = cs.getPlayerStorage().getCharacterByName(name);
                    if (character != null) {
                        //cs.getPlayerStorage().deregisterPlayer(character);
                        //character.getClient().disconnect(false, false, true);
                        FileoutputUtil.logToFile("logs/Data/非法登錄.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號 " + c.getAccountName() + "登錄3");
                        World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM 密語系統] 非法登錄 帳號 " + c.getAccountName()));
                        c.getSession().close();
                        character.getClient().getSession().close();
                    }
                }
            }
            for (final String name : charNames) {
                MapleCharacter charactercs = CashShopServer.getPlayerStorage().getCharacterByName(name);
                if (charactercs != null) {
                    //CashShopServer.getPlayerStorage().deregisterPlayer(charactercs);
                    //charactercs.getClient().disconnect(false, true, true);
                    FileoutputUtil.logToFile("logs/Data/非法登錄.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號 " + c.getAccountName() + "登錄4");
                    World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM 密語系統] 非法登錄 帳號 " + c.getAccountName()));
                    c.getSession().close();
                    charactercs.getClient().getSession().close();
                }
            }

            if (System.getProperty(String.valueOf(playerid)) == null || !System.getProperty(String.valueOf(playerid)).equals("1")) {
                World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM 密語系統] 非法登錄 帳號 " + c.getAccountName()));
                World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM 密語系統] 非法登錄 帳號 " + c.getAccountName()));
                World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM 密語系統] 非法登錄 帳號 " + c.getAccountName()));
                FileoutputUtil.logToFile("logs/Data/非法登錄.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號 " + c.getAccountName());
                c.getSession().close();
                return;
            } else {
                System.setProperty(String.valueOf(playerid), String.valueOf(0));
            }
            LoginServer.removeClient(c);
            player = MapleCharacter.loadCharFromDB(playerid, c, true);
            LoginServer.addEnterGameAgainTime(c.getAccID());
            player.setMrqdTime(System.currentTimeMillis());
        } else {
            player = MapleCharacter.ReconstructChr(transfer, c, true);
        }

        if (!LoginServer.CanLoginKey(player.getLoginKey(), player.getAccountID()) || (LoginServer.getLoginKey(player.getAccountID()) == null && !player.getLoginKey().isEmpty())) {
            FileoutputUtil.logToFile("logs/Data/客戶端登錄KEY異常.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號: " + c.getAccountName() + " 客戶端key：" + LoginServer.getLoginKey(player.getAccountID()) + " 伺服端key：" + player.getLoginKey() + " 進入遊戲1");
            World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM 密語系統] 非法登錄 帳號 " + c.getAccountName()));
            c.getSession().close();
            return;
        }
        if (!LoginServer.CanServerKey(player.getServerKey(), player.getAccountID()) || (LoginServer.getServerKey(player.getAccountID()) == null && !player.getServerKey().isEmpty())) {
            FileoutputUtil.logToFile("logs/Data/客戶端頻道KEY異常.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號: " + c.getAccountName() + " 客戶端key：" + LoginServer.getServerKey(player.getAccountID()) + " 伺服端key：" + player.getServerKey() + " 進入遊戲2");
            World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM 密語系統] 非法登錄 帳號 " + c.getAccountName()));
            c.getSession().close();
            return;
        }
        if (!LoginServer.CanClientKey(player.getClientKey(), player.getAccountID()) || (LoginServer.getClientKey(player.getAccountID()) == null && !player.getClientKey().isEmpty())) {
            FileoutputUtil.logToFile("logs/Data/客戶端進入KEY異常.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號: " + c.getAccountName() + " 客戶端key：" + LoginServer.getClientKey(player.getAccountID()) + " 伺服端key：" + player.getClientKey() + " 進入遊戲3");
            World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM 密語系統] 非法登錄 帳號 " + c.getAccountName()));
            c.getSession().close();
            return;
        }
        
        c.setLastLoginTime(LoginServer.getEnterGameAgainTime(c.getAccID()));
        LoginServer.forceRemoveClient(c, false);
        ChannelServer.forceRemovePlayerByAccId(c, c.getAccID());
        //設置用戶端角色
        c.setPlayer(player);
        //設置用戶端賬號ID
        c.setAccID(player.getAccountID());
        c.setWorld(player.getWorld());

        c.setSecondPassword(player.getAccountSecondPassword());
        if (!c.CheckIPAddress()) { // Remote hack
          /*  c.getSession().close();//支持一线海
            FileoutputUtil.logToFile("logs/Data/進入遊戲掉線.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號: " + c.getAccountName() + " CheckIPAddress");
            return;*/
        }
        final int state = c.getLoginState();
        boolean allowLogin = false;
        if (state == MapleClient.LOGIN_SERVER_TRANSITION || state == MapleClient.CHANGE_CHANNEL || state == MapleClient.LOGIN_NOTLOGGEDIN) {
            allowLogin = !World.isCharacterListConnected(c.loadCharacterNames(c.getWorld()));
        }
        if (!allowLogin) {
            c.setPlayer(null);
            c.getSession().close();
            FileoutputUtil.logToFile("logs/Data/進入遊戲掉線.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號: " + c.getAccountName() + " allowLogin");
            return;
        }
        //更新登入狀態
        c.updateLoginState(MapleClient.LOGIN_LOGGEDIN, c.getSessionIPAddress());
        channelServer.addPlayer(player);
        c.loadVip(player.getAccountID());
        c.sendPacket(MaplePacketCreator.getCharInfo(player));
        if (player.getCharacterNameById2(playerid) == null) {
            FileoutputUtil.logToFile("logs/Data/角色不存在.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號 " + c.getAccountName() + "登錄");
            World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM 密語系統] 非法登錄不存在角色 帳號 " + c.getAccountName()));
            c.getSession().close();
            return;
        }

        if (!LoginServer.CanLoginKey(player.getLoginKey(), player.getAccountID()) || (LoginServer.getLoginKey(player.getAccountID()) == null && !player.getLoginKey().isEmpty())) {
            FileoutputUtil.logToFile("logs/Data/客戶端登錄KEY異常.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號: " + c.getAccountName() + " 客戶端key：" + LoginServer.getLoginKey(player.getAccountID()) + " 伺服端key：" + player.getLoginKey() + " 進入遊戲4");
            World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM 密語系統] 非法登錄 帳號 " + c.getAccountName()));
            c.getSession().close();
            return;
        }
        if (!LoginServer.CanServerKey(player.getServerKey(), player.getAccountID()) || (LoginServer.getServerKey(player.getAccountID()) == null && !player.getServerKey().isEmpty())) {
            FileoutputUtil.logToFile("logs/Data/客戶端頻道KEY異常.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號: " + c.getAccountName() + " 客戶端key：" + LoginServer.getServerKey(player.getAccountID()) + " 伺服端key：" + player.getServerKey() + " 進入遊戲5");
            World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM 密語系統] 非法登錄 帳號 " + c.getAccountName()));
            c.getSession().close();
            return;
        }
        if (!LoginServer.CanClientKey(player.getClientKey(), player.getAccountID()) || (LoginServer.getClientKey(player.getAccountID()) == null && !player.getClientKey().isEmpty())) {
            FileoutputUtil.logToFile("logs/Data/客戶端進入KEY異常.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號: " + c.getAccountName() + " 客戶端key：" + LoginServer.getClientKey(player.getAccountID()) + " 伺服端key：" + player.getClientKey() + " 進入遊戲6");
            World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM 密語系統] 非法登錄 帳號 " + c.getAccountName()));
            c.getSession().close();
            return;
        }

        //管理員上線預設隱藏
        int 管理隐身 = gui.ZeroMS_UI.ConfigValuesMap.get("管理隐身开关");
        if (管理隐身 <= 0) {//管理隐身
        if (player.isGM()) {
            SkillFactory.getSkill(9001004).getEffect(1).applyTo(player);
        int 管理加速 = gui.ZeroMS_UI.ConfigValuesMap.get("管理加速开关");
        if (管理加速 <= 0) {//管理加速
            SkillFactory.getSkill(9001001).getEffect(1).applyTo(player);
            if (GameConstants.isKOC(player.getJob())) {
                SkillFactory.getSkill(10001010).getEffect(1).applyTo(player, 2100000000);
            } else if (GameConstants.isAran(player.getJob())) {
                SkillFactory.getSkill(20001010).getEffect(1).applyTo(player, 2100000000);
            } else {
                SkillFactory.getSkill(1010).getEffect(1).applyTo(player, 2100000000);
            }
        }        }        }

        //player.giveBuffTime();

        //暫存能力值解除
        
        c.sendPacket(MaplePacketCreator.temporaryStats_Reset());
        c.sendPacket(MaplePacketCreator.showCharCash(player));
        player.getMap().addPlayer(player);

        try {
            // BUFF技能
            player.silentGiveBuffs(PlayerBuffStorage.getBuffsFromStorage(player.getId()));
            // 冷卻時間
            player.giveCoolDowns(PlayerBuffStorage.getCooldownsFromStorage(player.getId()));
            // 疾病狀態
            player.giveSilentDebuff(PlayerBuffStorage.getDiseaseFromStorage(player.getId()));

            // 開啟好友列表
            final Collection<Integer> buddyIds = player.getBuddylist().getBuddiesIds();
            World.Buddy.loggedOn(player.getName(), player.getId(), c.getChannel(), buddyIds, player.getGMLevel(), player.isHidden());
            if (player.getParty() != null) {
                //channelServer.getWorldInterface().updateParty(player.getParty().getId(), PartyOperation.LOG_ONOFF, new MaplePartyCharacter(player));
                World.Party.updateParty(player.getParty().getId(), PartyOperation.LOG_ONOFF, new MaplePartyCharacter(player));
            }
            /* 讀取好友 */
            final CharacterIdChannelPair[] onlineBuddies = World.Find.multiBuddyFind(player.getId(), buddyIds);
            for (CharacterIdChannelPair onlineBuddy : onlineBuddies) {
                final BuddyEntry ble = player.getBuddylist().get(onlineBuddy.getCharacterId());
                ble.setChannel(onlineBuddy.getChannel());
                player.getBuddylist().put(ble);
            }
            c.sendPacket(MaplePacketCreator.updateBuddylist(player.getBuddylist().getBuddies()));

            // Messenger
            final MapleMessenger messenger = player.getMessenger();
            if (messenger != null) {
                World.Messenger.silentJoinMessenger(messenger.getId(), new MapleMessengerCharacter(c.getPlayer()));
                World.Messenger.updateMessenger(messenger.getId(), c.getPlayer().getName(), c.getChannel());
            }

            // 開始公會及聯盟
            if (player.getGuildId() > 0) {
                World.Guild.setGuildMemberOnline(player.getMGC(), true, c.getChannel());
                c.sendPacket(MaplePacketCreator.showGuildInfo(player));
                final MapleGuild gs = World.Guild.getGuild(player.getGuildId());
                if (gs != null) {
                    final List<byte[]> packetList = World.Alliance.getAllianceInfo(gs.getAllianceId(), true);
                    if (packetList != null) {
                        for (byte[] pack : packetList) {
                            if (pack != null) {
                                c.sendPacket(pack);
                            }
                        }
                    }
                } else {
                    player.setGuildId(0);
                    player.setGuildRank((byte) 5);
                    player.setAllianceRank((byte) 5);
                    player.saveGuildStatus();
                }
            } else {
                c.sendPacket(MaplePacketCreator.勳章(player));
            }
            //家庭
            if (player.getFamilyId() > 0) {
                World.Family.setFamilyMemberOnline(player.getMFC(), true, c.getChannel());
            }
            c.sendPacket(FamilyPacket.getFamilyInfo(player));
        } catch (Exception e) {
            FilePrinter.printError(FilePrinter.LoginError, e);
        }
        c.sendPacket(FamilyPacket.getFamilyData());

        // 技能組合
        player.sendMacros();
        // 顯示訊息
        player.showNote();
        // 更新組隊成員HP
        player.updatePartyMemberHP();
        // 精靈吊墜計時
        player.startFairySchedule(false);
        // 修復消失技能
        player.baseSkills();
        // 鍵盤設置
        c.sendPacket(MaplePacketCreator.getKeymap(player.getKeyLayout()));
      int p = 0;
        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
            for (MapleCharacter chr : cserv.getPlayerStorage().getAllCharacters()) {
                if (chr != null) {
                    ++p;
                }
            }
        }

        if (c.getPlayer().hasEquipped(1122017)) {
            player.dropMessage(5, "您装备了精灵吊坠！打猎时可以额外获得10%的道具佩戴经验奖励！");
        }
           if (gui.ZeroMS_UI.ConfigValuesMap.get("上线提醒开关") <= 0) {
           if(player.getOneTimeLog("新手礼包") == 0){
             c.getPlayer().dropMessage(5, "背包内的消耗栏新手礼包请自行打开领取，我们给您赠送一份大礼包 !");
             c.getPlayer().dropMessage(5, "背包内的消耗栏新手礼包请自行打开领取，我们给您赠送一份大礼包 !");
             c.getPlayer().dropMessage(5, "背包内的消耗栏新手礼包请自行打开领取，我们给您赠送一份大礼包 !");
             World.Broadcast.broadcastSmega(MaplePacketCreator.serverNotice(0x0C, c.getChannel(), "[萌新驾到] " + player.getName() + " : " +" 欢迎你入驻仙境冒险岛，又一位冒险家的到来！"));
       } else if(c.getPlayer().hasEquipped(1142492) && player.getBossLog("仙君上线提示") == 0){//1-16日新
             全服漂浮喇叭("【仙君上线提示】 " + player.getName() + " ★☆★☆★☆★☆ 闪亮★登场 ★☆★☆★☆★☆", 5120001);
             player.setBossLog("仙君上线提示");
       } else if (c.getPlayer().hasEquipped(1142493)&& player.getBossLog("仙皇上线提示") == 0) {//仙皇 1142494
             全服漂浮喇叭("【仙皇上线提示】 " + player.getName() + " ★☆★☆★☆★☆ 华丽★登场 ★☆★☆★☆★☆", 5120002);
             player.setBossLog("仙皇上线提示");
       } else if (c.getPlayer().hasEquipped(1142494)&& player.getBossLog("仙帝上线提示") == 0) {//检测身上佩戴道具+每天记录
             全服漂浮喇叭("【仙帝上线提示】 " + player.getName() + " ★☆★☆★☆★☆ 闪耀★登场 ★☆★☆★☆★☆", 5120008);
             player.setBossLog("仙帝上线提示");//每天上线记录+1

       } else if (c.getPlayer().hasEquipped(1142746)&& player.getBossLog("3W上线提示") == 0) {//仙帝 1142494
             全服漂浮喇叭("【仙境冒险岛 - 侯爵 上线提示】 " + player.getName() + " ┏☆★☆★☆★☆★☆ 〖华丽登场〗 ☆★☆★☆★☆★☆┓", 5120008);
             player.setBossLog("3W上线提示");
 
       } else if (c.getPlayer().hasEquipped(1142802)&& player.getBossLog("4W上线提示") == 0) {//仙帝 1142494
             全服漂浮喇叭("【仙境冒险岛 - 公爵 上线提示】 " + player.getName() + " ┏☆★☆★☆★☆★☆ 〖闪亮登场〗 ☆★☆★☆★☆★☆┓", 5120008);
             player.setBossLog("4W上线提示");
       
       } else if (c.getPlayer().hasEquipped(1142803)&& player.getBossLog("6W上线提示") == 0) {//仙帝 1142494
             全服漂浮喇叭("【仙境冒险岛 - 王者 上线提示】 " + player.getName() + " ┏☆★☆★☆★☆★☆ 〖闪亮登场〗 ☆★☆★☆★☆★☆┓", 5120008);
             player.setBossLog("6W上线提示");
 
       } else if (c.getPlayer().hasEquipped(1142683)&& player.getBossLog("1W上线提示") == 0) {//仙帝 1142494
             全服漂浮喇叭("【仙境冒险岛 - 伯爵 上线提示】 " + player.getName() + " ┏☆★☆★☆★☆★☆ 〖闪亮登场〗 ☆★☆★☆★☆★☆┓", 5120008);
             player.setBossLog("1W上线提示");
       } else if (player.isGM()) {
//gm不提示
 } else {
 World.Broadcast.broadcastSmega(MaplePacketCreator.serverNotice(0x0C, c.getChannel(), "[仙境冒险岛 - 上线提示] " + player.getName() + " : " +"进入游戏，大家热烈欢迎她吧！！！"));
           }                }
        player.updatePetAuto();
        
        if (gui.ZeroMS_UI.ConfigValuesMap.get("登陆帮助开关") == 0) {
            if (player.getGMLevel() > 0 && player.getBossLog("管理上线提示") == 0) {
                player.dropMessage(5, "指令: [!help] 查看管理员指令");
                player.dropMessage(5, "指令: [@帮助] 查看玩家指令");
            } else if (player.getGMLevel() <= 0 && player.getBossLog("玩家上线提示") == 0) {
                player.dropMessage(5, "指令: [@帮助] 查看玩家指令");
            }
        }
        
        if (gui.ZeroMS_UI.ConfigValuesMap.get("离线挂机开关") == 0 && player.getMapId() == 741000209) {
			long nowTimestamp = System.currentTimeMillis();
			long 奖励时间 = nowTimestamp - player.getLastOfflineTime();
			if(奖励时间 >= 60000){
				int 离线时间 = (int) 奖励时间 / 60000;
				if(离线时间 >= 1440){
					离线时间 = 1440;
					c.getPlayer().dropMessage(5, "您的离线时间超过24小时,离线奖励按照一天算。");
				}
                                final int 点卷数量 = Start.ConfigValuesMap.get("离线泡点点券")* 离线时间;
                                final int 抵用数量 = Start.ConfigValuesMap.get("离线泡点抵用")* 离线时间;
                                final int 豆豆数量 = Start.ConfigValuesMap.get("离线泡点豆豆")* 离线时间;
                                final int 金币数量 = Start.ConfigValuesMap.get("离线泡点金币")* 离线时间;
                                final int 经验数量 = Start.ConfigValuesMap.get("离线泡点经验")* 离线时间;
                                player.gainExp(经验数量, false, false, false);//给固定经验
				player.modifyCSPoints(2, 抵用数量);
				player.modifyCSPoints(1, 点卷数量);
				player.gainBeans(豆豆数量);
                                player.gainMeso((金币数量), true);//给金币
				c.getPlayer().dropMessage(5, "您的离线时间"+离线时间+"分钟,离线获得[" + 经验数量 + "] 经验 ["+金币数量+"] 金币 ["+抵用数量+"] 抵用卷 ["+点卷数量+"] 点卷 ["+豆豆数量+"] 豆豆 !");
				player.updateOfflineTime1();
			}
		}
        
        // 任務狀態
        for (MapleQuestStatus status : player.getStartedQuests()) {
            if (status.hasMobKills()) {
                c.sendPacket(MaplePacketCreator.updateQuestMobKills(status));
            }
        }

        // 好友
        final BuddyEntry pendingBuddyRequest = player.getBuddylist().pollPendingRequest();
        if (pendingBuddyRequest != null) {
            player.getBuddylist().put(new BuddyEntry(pendingBuddyRequest.getName(), pendingBuddyRequest.getCharacterId(), "ETC", -1, false, pendingBuddyRequest.getLevel(), pendingBuddyRequest.getJob()));
            c.sendPacket(MaplePacketCreator.requestBuddylistAdd(pendingBuddyRequest.getCharacterId(), pendingBuddyRequest.getName(), pendingBuddyRequest.getLevel(), pendingBuddyRequest.getJob()));
        }

        // 黑騎士技能 
        if (player.getJob() == 132) {
            player.checkBerserk();
        }
        // 理财 每次上线提示
        //if (player.getGMLevel() == 0) {
          //  NPCScriptManager.getInstance().start(c, 9900005);
        //}
        // 複製人
        player.spawnClones();
        player.set在线时间(player.getGamePoints());
        // 寵物
        player.spawnSavedPets();
        c.sendPacket(MaplePacketCreator.showCharCash(player));
        boolean ChrdangerousIp = player.chrdangerousIp(c.getSession().remoteAddress().toString());
        if (ChrdangerousIp) {
            World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM 密語系統] 危險IP上線" + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號 " + c.getAccountName() + " 帳號ID " + c.getAccID() + " 角色名 " + player.getName() + " 角色ID " + player.getId()));
            World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM 密語系統] 危險IP上線" + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號 " + c.getAccountName() + " 帳號ID " + c.getAccID() + " 角色名 " + player.getName() + " 角色ID " + player.getId()));
            World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM 密語系統] 危險IP上線" + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號 " + c.getAccountName() + " 帳號ID " + c.getAccID() + " 角色名 " + player.getName() + " 角色ID " + player.getId()));
            FileoutputUtil.logToFile("logs/Data/危險IP登錄.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號 " + c.getAccountName() + " 帳號ID " + c.getAccID() + " 角色名 " + player.getName() + " 角色ID " + player.getId());
        }

        boolean ChrdangerousName = player.ChrDangerousAcc(player.getClient().getAccountName());
        if (ChrdangerousName) {
            World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM 密語系統] 危險角色上線" + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號 " + c.getAccountName() + " 帳號ID " + c.getAccID() + " 角色名 " + player.getName() + " 角色ID " + player.getId()));
            World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM 密語系統] 危險角色上線" + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號 " + c.getAccountName() + " 帳號ID " + c.getAccID() + " 角色名 " + player.getName() + " 角色ID " + player.getId()));
            World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM 密語系統] 危險角色上線" + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號 " + c.getAccountName() + " 帳號ID " + c.getAccID() + " 角色名 " + player.getName() + " 角色ID " + player.getId()));
            FileoutputUtil.logToFile("logs/Data/危險帳號登錄.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號 " + c.getAccountName() + " 帳號ID " + c.getAccID() + " 角色名 " + player.getName() + " 角色ID " + player.getId());
        }

    }

    public static final void ChangeChannel(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        if (c.getCloseSession()) {
            return;
        }
        if (chr.hasBlockedInventory(true) || chr.getEventInstance() != null || chr.getMap() == null || FieldLimitType.ChannelSwitch.check(chr.getMap().getFieldLimit())) {
            c.sendPacket(MaplePacketCreator.enableActions());
            return;
        }

        //if (World.getPendingCharacterSize() >= 80) {
        //    chr.dropMessage(1, "伺服器繁忙，請稍後再試。");
        //    c.sendPacket(MaplePacketCreator.enableActions());
        //    return;
        //}
        if (chr.getAntiMacro().inProgress()) {
            chr.dropMessage(5, "被使用测谎仪时无法操作。");
            c.sendPacket(MaplePacketCreator.enableActions());
            return;
        }
        /*if (!LoginServer.CanLoginKey(chr.getLoginKey(), chr.getAccountID()) || (LoginServer.getLoginKey(chr.getAccountID()) == null && !chr.getLoginKey().isEmpty())) {
            FileoutputUtil.logToFile("logs/Data/客戶端登錄KEY異常.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號: " + c.getAccountName() + " 客戶端key：" + LoginServer.getLoginKey(chr.getAccountID()) + " 伺服端key：" + chr.getLoginKey() + " 更換頻道1");
            World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM 密語系統] 非法更換頻道 帳號 " + c.getAccountName()));
            c.getSession().close();
            return;
        }
        if (!LoginServer.CanServerKey(chr.getServerKey(), chr.getAccountID()) || (LoginServer.getServerKey(chr.getAccountID()) == null && !chr.getServerKey().isEmpty())) {
            FileoutputUtil.logToFile("logs/Data/客戶端頻道KEY異常.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號: " + c.getAccountName() + " 客戶端key：" + LoginServer.getServerKey(chr.getAccountID()) + " 伺服端key：" + chr.getServerKey() + " 更換頻道2");
            World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM 密語系統] 非法更換頻道 帳號 " + c.getAccountName()));
            c.getSession().close();
            return;
        }
        if (!LoginServer.CanClientKey(chr.getClientKey(), chr.getAccountID()) || (LoginServer.getClientKey(chr.getAccountID()) == null && !chr.getClientKey().isEmpty())) {
            FileoutputUtil.logToFile("logs/Data/客戶端進入KEY異常.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號: " + c.getAccountName() + " 客戶端key：" + LoginServer.getClientKey(chr.getAccountID()) + " 伺服端key：" + chr.getClientKey() + " 更換頻道3");
            World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM 密語系統] 非法更換頻道 帳號 " + c.getAccountName()));
            c.getSession().close();
            return;
        }*/
        chr.changeChannel(slea.readByte() + 1);
        // 換頻道更新VIP等級
        //c.loadVip(chr.getAccountID());
        //chr.loadVip(chr.getAccountID());
    }

}

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
package handling.login.handler;

import java.util.List;
import java.util.Calendar;

import client.inventory.IItem;
import client.inventory.Item;
import client.MapleClient;
import client.MapleCharacter;
import client.MapleCharacterUtil;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import constants.ServerConfig;
import constants.WorldConstants;
import database.DBConPool;
import database.DatabaseConnection;
import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.login.LoginInformationProvider;
import handling.login.LoginInformationProvider.JobType;
import handling.login.LoginServer;
import handling.login.LoginWorker;
import handling.world.MapleParty;
import handling.world.World;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import server.MapleItemInformationProvider;
import server.ServerProperties;
import server.quest.MapleQuest;
import tools.FileoutputUtil;
import tools.MaplePacketCreator;
import tools.packet.LoginPacket;
import tools.KoreanDateUtil;
import tools.StringUtil;
import tools.data.LittleEndianAccessor;

public class CharLoginHandler {

    private static boolean loginFailCount(final MapleClient c) {
        c.loginAttempt++;
        return c.loginAttempt > 5;
    }

    public static final void handleWelcome(final MapleClient c) {
        c.sendPing();
    }

    public static final void LicenseRequest(final LittleEndianAccessor slea, final MapleClient c) {
        if (slea.readByte() == 1) {
            c.sendPacket(LoginPacket.licenseResult());
            c.updateLoginState(MapleClient.LOGIN_NOTLOGGEDIN, c.getSessionIPAddress());
        } else {
            c.getSession().close();
        }
    }

    public static String RandomString(/*int length*/) {
        /*String str = "abcdefghijklmnopqrstuvwsyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        Random random = new Random();
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int num = random.nextInt(62);
            buf.append(str.charAt(num));
        }
        return buf.toString();*/
        Random random = new Random();
        String sRand = "";

        for (int i = 0; i < 6; i++) {
            String rand = String.valueOf(random.nextInt(10));
            sRand += rand;
        }

        return sRand;
    }

    public static final void handleLogin(final LittleEndianAccessor slea, final MapleClient c) {
        String account = slea.readMapleAsciiString();
        String password = slea.readMapleAsciiString();
        String loginkey = RandomString(/*10*/);
        int loginkeya = (int) ((Math.random() * 9 + 1) * 100000);
        c.setAccountName(account);

        int[] bytes = new int[6];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = slea.readByteAsInt();
        }
        StringBuilder sps = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sps.append(StringUtil.getLeftPaddedStr(Integer.toHexString(bytes[i]).toUpperCase(), '0', 2));
            sps.append("-");
        }
        String macData = sps.toString();
        macData = macData.substring(0, macData.length() - 1);
        //if (!"00-00-00-00-00-00".equals(macData)) {
        //    int MacsCout = c.getMacsCout((byte) 0, macData);
        //    if (MacsCout >= 2) {
        //       c.sendPacket(MaplePacketCreator.serverNotice(1, "?????????????????????????????????????????????"));
        //        c.sendPacket(LoginPacket.getLoginFailed(1));
        //       return;
        //    }
        //}
        final boolean ipBan = c.hasBannedIP();
        final boolean macBan = c.hasBannedMac();
        final boolean ban = ipBan || macBan;

        int loginok = c.login(account, password, ban);
        final Calendar tempbannedTill = c.getTempBanCalendar();
        String errorInfo = null;
        if (c.getLastLoginTime() != 0 && (c.getLastLoginTime() + 5 * 1000) < System.currentTimeMillis()) {
            errorInfo = "????????????????????????!\r\n???????????????.";
            loginok = 1;
        } else if (loginok == 0 && ban && !c.isGm()) {
            //?????????IP???MAC??????GM????????????????????????
            loginok = 3;
            //if (macBan) {
            FileoutputUtil.logToFile("logs/data/" + (macBan ? "MAC" : "IP") + "??????_????????????.txt", "\r\n ?????????[" + FileoutputUtil.NowTime() + "] " + " ??????MAC??????: " + c.getMacs() + " IP??????: " + c.getSession().remoteAddress().toString().split(":")[0] + " ????????????" + account + " ?????????" + password);
            // this is only an ipban o.O" - maybe we should refactor this a bit so it's more readable
            // MapleCharacter.ban(c.getSessionIPAddress(), c.getSession().remoteAddress().toString().split(":")[0], "Enforcing account ban, account " + account, false, 4, false);
            //}
        } else if (loginok == 0 && (c.getGender() == 10 || c.getSecondPassword() == null)) {
            //????????????????????????????????????
//            c.updateLoginState(MapleClient.CHOOSE_GENDER, c.getSessionIPAddress());
            c.sendPacket(LoginPacket.getGenderNeeded(c));
            return;
        } else if (loginok == 5) {
            //???????????????
            if (LoginServer.getAutoReg()) {
                if (password.equalsIgnoreCase("fixlogged")) {
                    errorInfo = "???????????????????????????????????????????????????";
                } else if (account.length() >= 12) {
                    errorInfo = "?????????????????????!\r\n???????????????.";
                } else {
                    AutoRegister.createAccount(account, password, c.getSession().remoteAddress().toString());
                    if (AutoRegister.success && AutoRegister.mac) {
                        c.sendPacket(MaplePacketCreator.serverNotice(1, "<<" + ServerConfig.SERVERNAME + ">>\r\n\r\n????????????????????????????????????????????????????????????"));
                        FileoutputUtil.logToFile("logs/data/????????????.txt", "\r\n ?????????[" + FileoutputUtil.NowTime() + "]" + " IP ?????? : " + c.getSession().remoteAddress().toString().split(":")[0] + " MAC: " + macData + " ????????????" + account + " ?????????" + password);
                    } else if (!AutoRegister.mac) {
                        errorInfo = "???????????????????????????!";
                        AutoRegister.success = false;
                        AutoRegister.mac = true;
                    }
                }
                loginok = 1;
            }
         /**
         * <???????????????????????????>
         */
        //IP????????????
        if (gui.ZeroMS_UI.ConfigValuesMap.get("IP????????????") == 0) {
            if (IP?????????("" + c.getSessionIPAddress() + "") == false) {
                c.sendPacket(MaplePacketCreator.serverNotice(1, "<<" + ServerConfig.SERVERNAME + ">>\r\n\r\n???IP??????????????????????????????"));
                c.sendPacket(LoginPacket.getLoginFailed(1));
                return;
            }
        } else if (IP?????????2("" + c.getSessionIPAddress() + "") >= gui.ZeroMS_UI.ConfigValuesMap.get("IP?????????")) {
            c.sendPacket(MaplePacketCreator.serverNotice(1, "<<" + ServerConfig.SERVERNAME + ">>\r\n\r\n???????????????????????? A01???"));
            c.sendPacket(LoginPacket.getLoginFailed(1));
            return;
        }
        //?????????????????????
        if (gui.ZeroMS_UI.ConfigValuesMap.get("??????????????????") == 0) {
            if (??????????????????("" + macData + "") == false) {
                c.sendPacket(MaplePacketCreator.serverNotice(1, "<<" + ServerConfig.SERVERNAME + ">>\r\n\r\n???????????????????????????????????????"));
                c.sendPacket(LoginPacket.getLoginFailed(1));
                return;
            }
        } else if (??????????????????2("" + macData + "") >= gui.ZeroMS_UI.ConfigValuesMap.get("??????????????????")) {
            c.sendPacket(MaplePacketCreator.serverNotice(1, "<<" + ServerConfig.SERVERNAME + ">>\r\n\r\n???????????????????????? A02???"));
            c.sendPacket(LoginPacket.getLoginFailed(1));
            return;
        }
        
        } else if (!LoginServer.canLoginAgain(c.getAccID())) {// ?????????
            int sec = (int) (((LoginServer.getLoginAgainTime(c.getAccID()) + 50 * 1000) - System.currentTimeMillis()) / 1000);
            c.loginAttempt = 0;
            errorInfo = "??????????????????" + sec + "????????????????????? ??????????????????";
            loginok = 1;
        } else if (!LoginServer.canEnterGameAgain(c.getAccID())) {// ???????????????
            int sec = (int) (((LoginServer.getEnterGameAgainTime(c.getAccID()) + 60 * 1000) - System.currentTimeMillis()) / 1000);
            c.loginAttempt = 0;
            errorInfo = "??????????????????" + sec + "????????????????????? ??????????????????";
            loginok = 1;
        }
        if (loginok != 0) {
            if (!loginFailCount(c)) {
                c.sendPacket(LoginPacket.getLoginFailed(loginok));
                if (errorInfo != null) {
                    c.getSession().writeAndFlush(MaplePacketCreator.serverNotice(1, errorInfo));
                }
            } else {
                c.getSession().close();
            }
        } else if (tempbannedTill.getTimeInMillis() != 0) {
            if (!loginFailCount(c)) {
                c.sendPacket(LoginPacket.getTempBan(KoreanDateUtil.getTempBanTimestamp(tempbannedTill.getTimeInMillis()), c.getBanReason()));
            } else {
                c.getSession().close();
            }
        } else {
            c.loginAttempt = 0;
            LoginServer.RemoveLoginKey(c.getAccID());
            c.updateMacs(macData);
            c.setLoginKey(loginkey);
            c.updateLoginKey(loginkey);
            LoginServer.addLoginKey(loginkey, c.getAccID());
            FileoutputUtil.logToFile("logs/data/????????????.txt", "\r\n ?????????[" + FileoutputUtil.NowTime() + "]" + " IP ?????? : " + c.getSession().remoteAddress().toString().split(":")[0] + " MAC: " + macData + " ????????????" + account + " ?????????" + password);
            c.setLoginKeya(loginkeya);
            LoginWorker.registerClient(c);
        }
    }

    public static final void SetGenderRequest(final LittleEndianAccessor slea, final MapleClient c) {

        byte gender = slea.readByte();
        String username = slea.readMapleAsciiString();
        if (gender != 0 && gender != 1) {
            c.getSession().close();
            return;
        }
        if (c.getAccountName().equals(username)) {
            c.setGender(gender);
            c.updateGender();
            c.setSecondPassword("123456");
            c.updateSecondPassword();
            c.sendPacket(LoginPacket.getGenderChanged(c));
            c.updateLoginState(MapleClient.LOGIN_NOTLOGGEDIN, c.getSessionIPAddress());
        } else {
            c.getSession().close();
        }
    }

    public static final void ServerListRequest(final MapleClient c) {//????????????
        if (gui.ZeroMS_UI.ConfigValuesMap.get("???????????????") == 0) {
            c.sendPacket(LoginPacket.getServerList(0, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
        }
        if (gui.ZeroMS_UI.ConfigValuesMap.get("???????????????") == 0) {
            c.sendPacket(LoginPacket.getServerList(1, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
        }
        if (gui.ZeroMS_UI.ConfigValuesMap.get("???????????????") == 0) {
            c.sendPacket(LoginPacket.getServerList(2, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
        }
        if (gui.ZeroMS_UI.ConfigValuesMap.get("???????????????") == 0) {
            c.sendPacket(LoginPacket.getServerList(3, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
        }
        if (gui.ZeroMS_UI.ConfigValuesMap.get("???????????????") == 0) {
            c.sendPacket(LoginPacket.getServerList(4, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
        }
        if (gui.ZeroMS_UI.ConfigValuesMap.get("???????????????") == 0) {
            c.sendPacket(LoginPacket.getServerList(5, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
        }
        if (gui.ZeroMS_UI.ConfigValuesMap.get("???????????????") == 0) {
            c.sendPacket(LoginPacket.getServerList(6, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
        }
        if (gui.ZeroMS_UI.ConfigValuesMap.get("???????????????") == 0) {
            c.sendPacket(LoginPacket.getServerList(7, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
        }
        if (gui.ZeroMS_UI.ConfigValuesMap.get("???????????????") == 0) {
            c.sendPacket(LoginPacket.getServerList(8, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
        }
        if (gui.ZeroMS_UI.ConfigValuesMap.get("???????????????") == 0) {
            c.sendPacket(LoginPacket.getServerList(9, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
        }
        if (gui.ZeroMS_UI.ConfigValuesMap.get("???????????????") == 0) {
            c.sendPacket(LoginPacket.getServerList(10, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
        }
        if (gui.ZeroMS_UI.ConfigValuesMap.get("???????????????") == 0) {
            c.sendPacket(LoginPacket.getServerList(11, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
        }
        if (gui.ZeroMS_UI.ConfigValuesMap.get("???????????????") == 0) {
            c.sendPacket(LoginPacket.getServerList(12, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
        }
        if (gui.ZeroMS_UI.ConfigValuesMap.get("???????????????") == 0) {
            c.sendPacket(LoginPacket.getServerList(13, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
        }
        if (gui.ZeroMS_UI.ConfigValuesMap.get("???????????????") == 0) {
            c.sendPacket(LoginPacket.getServerList(14, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
        }
        if (gui.ZeroMS_UI.ConfigValuesMap.get("???????????????") == 0) {
            c.sendPacket(LoginPacket.getServerList(15, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
        }
        if (gui.ZeroMS_UI.ConfigValuesMap.get("???????????????") == 0) {
            c.sendPacket(LoginPacket.getServerList(16, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
        }
        if (gui.ZeroMS_UI.ConfigValuesMap.get("???????????????") == 0) {
            c.sendPacket(LoginPacket.getServerList(17, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
        }
        if (gui.ZeroMS_UI.ConfigValuesMap.get("???????????????") == 0) {
            c.sendPacket(LoginPacket.getServerList(18, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
        }
        if (gui.ZeroMS_UI.ConfigValuesMap.get("???????????????") == 0) {
            c.sendPacket(LoginPacket.getServerList(19, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
        }
        c.sendPacket(LoginPacket.getEndOfServerList());
    }
    public static final void ServerStatusRequest(final MapleClient c) {//??????????????????
        if (!c.isCanloginpw()) {
            c.getSession().close();
            return;
        }
        LoginServer.forceRemoveClient(c, false);
        ChannelServer.forceRemovePlayerByCharNameFromDataBase(c, c.loadCharacterNamesByAccId(c.getAccID()));
        // 0 = ????????????
        // 1 = ????????????
        // 2 = ????????????

        final int numPlayer = LoginServer.getUsersOn();
        final int userLimit = WorldConstants.USER_LIMIT;
        if (numPlayer >= userLimit) {
            c.sendPacket(LoginPacket.getServerStatus(2));
        } else if (numPlayer * 2 >= userLimit) {
            c.sendPacket(LoginPacket.getServerStatus(1));
        } else {
            c.sendPacket(LoginPacket.getServerStatus(0));
        }
    }
    //????????????
    public static final void CharlistRequest(final LittleEndianAccessor slea, final MapleClient c) {
        if (!c.isCanloginpw()) {
            c.getSession().close();
            return;
        }
        if (c.getCloseSession()) {
            return;
        }
        if (c.getLoginKeya() == 0) {
            c.sendPacket(MaplePacketCreator.serverNotice(1, "???????????????????????????\r\n???????????????"));
            return;
        }
        ChannelServer.forceRemovePlayerByCharNameFromDataBase(c, c.loadCharacterNamesByAccId(c.getAccID()));
        LoginServer.forceRemoveClient(c, false);
        String serverkey = RandomString(/*10*/);
        if (!LoginServer.CanLoginKey(c.getLoginKey(), c.getAccID()) || (LoginServer.getLoginKey(c.getAccID()) == null && !c.getLoginKey().isEmpty())) {
            FileoutputUtil.logToFile("logs/Data/???????????????KEY??????.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " ??????: " + c.getAccountName() + " ?????????key???" + LoginServer.getLoginKey(c.getAccID()) + " ?????????key???" + c.getLoginKey() + " ????????????");
            return;
        }
        final int server = slea.readByte();
        final int channel = slea.readByte() + 1;
        LoginServer.RemoveServerKey(c.getAccID());
        c.setServerKey(serverkey);
        c.updateServerKey(serverkey);
        LoginServer.addServerKey(serverkey, c.getAccID());
        c.setWorld(server + 1);
        System.err.println("??????????????????  " + c.getSessionIPAddress() + " ????????????????????????: " + server + " ??????: " + channel);
        //System.out.println("Client " + c.getSession().getRemoteAddress().toString().split(":")[0] + " is connecting to server " + server + " channel " + channel + "");
        c.setChannel(channel);
        final List<MapleCharacter> chars = c.loadCharacters(server);
        if (chars != null) {
            c.sendPacket(LoginPacket.getCharList(c.getSecondPassword() != null, chars, c.getCharacterSlots()));
        } else {
            c.getSession().close();
        }
    }
    //??????????????????
    public static final void checkCharName(final String name, final MapleClient c) {
        c.sendPacket(LoginPacket.charNameResponse(name,
                !MapleCharacterUtil.canCreateChar(name) || LoginInformationProvider.getInstance().isForbiddenName(name)));
    }

    public static final void handleCreateCharacter(final LittleEndianAccessor slea, final MapleClient c) {
        final String name = slea.readMapleAsciiString();
        final int job_type = slea.readInt(); // 1 = Adventurer, 0 = Cygnus, 2 = Aran
        if(job_type != 1){
            c.sendPacket(MaplePacketCreator.serverNotice(1, "???????????????????????????\r\n????????????????????????????????????????????????\r\n???????????????????????????\r\n????????????????????????????????????????????????\r\n??????????????????????????????\r\n?????????????????????????????????????????????~"));
            return;
        }
        JobType job = JobType.getByType(job_type);
        if (job == null) {
            System.out.println("New job type found: " + job_type);
            return;
        }
        final short db = slea.readShort(); //whether dual blade = 1 or adventurer = 0

        //if (job_type == 3) {
        //    c.sendPacket(MaplePacketCreator.serverNotice(1, "?????????\r\n?????????????????????\r\n????????????BUG?????????????????????????????????"));
            //    c.sendPacket(MaplePacketCreator.enableActions());
        //    return;
        //}
        //if (db == 1) {
        //    c.sendPacket(MaplePacketCreator.serverNotice(1, "?????????\r\n?????????????????????\r\n????????????BUG?????????????????????????????????"));
            //    c.sendPacket(MaplePacketCreator.enableActions());
       //     return;
       // }

        final int face = slea.readInt();
        final int hair = slea.readInt();
        final int hairColor = 0;
        final byte skinColor = 0;
        final int top = slea.readInt();
        final int bottom = slea.readInt();
        final int shoes = slea.readInt();
        final int weapon = slea.readInt();

        final byte gender = c.getGender();
        final List<MapleCharacter> chars = c.loadCharacters(0);
        if (chars.size() > c.getCharacterSlots()) {
            FileoutputUtil.logToFile("logs/Hack/Ban/??????????????????.txt", "\r\n " + FileoutputUtil.NowTime() + " ?????? " + c.getAccountName());
            c.getSession().close();
            return;
        }

        if (gender != 0 && gender != 1) {
            FileoutputUtil.logToFile("logs/Hack/Ban/????????????.txt", "\r\n " + FileoutputUtil.NowTime() + " ?????? " + c.getAccountName() + " ???????????? " + gender);
            c.getSession().close();
            return;
        }

        if (job_type != 0 && job_type != 1 && job_type != 2 && job_type != 3) {
            FileoutputUtil.logToFile("logs/Data/????????????.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " ?????? " + c.getAccountName() + " ???????????? " + job_type);
            return;
        }
        int index = 0;
        int[] items = new int[]{face, hair, top, bottom, shoes, weapon};
        for (int k : items) {
            if (k > 1000) {
                if (!LoginInformationProvider.getInstance().isEligibleItem(gender, index, /*job == JobType.????????? ? 1 : */ job.id, k)) {
                    System.out.println(gender + " | " + index + " | " + job.type + " | " + k);
                    return;
                }
                index++;
            }
        }

        /*boolean dangerousIp = c.dangerousIp(c.getSession().remoteAddress().toString());
        if (dangerousIp) {
            World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM ????????????] ??????IP???????????? ?????? " + c.getAccountName() + " ?????? " + name + " ???????????? " + JobType + " ?????? " + face + " ?????? " + hair + " ?????? " + top + " ?????? " + bottom + " ?????? " + shoes + " ?????? " + weapon + " ?????? " + gender));
            World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM ????????????] ??????IP???????????? ?????? " + c.getAccountName() + " ?????? " + name + " ???????????? " + JobType + " ?????? " + face + " ?????? " + hair + " ?????? " + top + " ?????? " + bottom + " ?????? " + shoes + " ?????? " + weapon + " ?????? " + gender));
            World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM ????????????] ??????IP???????????? ?????? " + c.getAccountName() + " ?????? " + name + " ???????????? " + JobType + " ?????? " + face + " ?????? " + hair + " ?????? " + top + " ?????? " + bottom + " ?????? " + shoes + " ?????? " + weapon + " ?????? " + gender));
            FileoutputUtil.logToFile("logs/Data/??????IP????????????.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " ?????? " + c.getAccountName() + " ?????? " + name + " ???????????? " + JobType + " ?????? " + face + " ?????? " + hair + " ?????? " + top + " ?????? " + bottom + " ?????? " + shoes + " ?????? " + weapon + " ?????? " + gender);
        }*/
        MapleCharacter newchar = MapleCharacter.getDefault(c, job_type);
        newchar.setWorld((byte) (c.getWorld() - 1));
        newchar.setFace(face);
        newchar.setHair(hair + hairColor);
        newchar.setGender(gender);
        newchar.setName(name);
        newchar.setSkinColor(skinColor);

        MapleInventory equip = newchar.getInventory(MapleInventoryType.EQUIPPED);
        final MapleItemInformationProvider li = MapleItemInformationProvider.getInstance();

        IItem item = li.getEquipById(top);
        item.setPosition((byte) -5);
        equip.addFromDB(item);

        item = li.getEquipById(bottom);
        item.setPosition((byte) -6);
        equip.addFromDB(item);

        item = li.getEquipById(shoes);
        item.setPosition((byte) -7);
        equip.addFromDB(item);

        item = li.getEquipById(weapon);
        item.setPosition((byte) -11);
        equip.addFromDB(item);
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        //IItem gift = ii.getEquipById(gender == 0 ? 1000090 : 1001112);
        //IItem gift2 = ii.getEquipById(gender == 0 ? 1053098 : 1053097);
        //IItem gift3 = ii.getEquipById(gender == 0 ? 1080009 : 1081015);
        //IItem gift4 = ii.getEquipById(1004540);
        //IItem gift5 = ii.getEquipById(1052948);
        //blue/red pots
        switch (job_type) {
            case 0: // Cygnus
                newchar.setQuestAdd(MapleQuest.getInstance(20022), (byte) 1, "1");
                newchar.setQuestAdd(MapleQuest.getInstance(20010), (byte) 1, null); //>_>_>_> ugh

                newchar.setQuestAdd(MapleQuest.getInstance(20000), (byte) 1, null); //>_>_>_> ugh
                newchar.setQuestAdd(MapleQuest.getInstance(20015), (byte) 1, null); //>_>_>_> ugh
                newchar.setQuestAdd(MapleQuest.getInstance(20020), (byte) 1, null); //>_>_>_> ugh
                //newchar.getInventory(MapleInventoryType.EQUIP).addItem(gift, 1);
                //newchar.getInventory(MapleInventoryType.EQUIP).addItem(gift2, 2);
                //newchar.getInventory(MapleInventoryType.EQUIP).addItem(gift3, 3);
                //newchar.getInventory(MapleInventoryType.EQUIP).addItem(gift4, 4);
                //newchar.getInventory(MapleInventoryType.EQUIP).addItem(gift5, 5);
                //????????????????????????  ????????????
                newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000016, (byte) 0, (short) 300, (byte) 0), 1);//????????????
                newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000018, (byte) 0, (short) 300, (byte) 0), 2);//????????????
                newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000016, (byte) 0, (short) 300, (byte) 0), 3);//????????????
                newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000018, (byte) 0, (short) 300, (byte) 0), 4);//????????????
                newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000016, (byte) 0, (short) 300, (byte) 0), 5);//????????????
                newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000018, (byte) 0, (short) 300, (byte) 0), 6);//????????????
               // newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(2022613, (byte) 0, (short) 1, (byte) 0), 1);//????????????
              //  newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161047, (byte) 0, (short) 1, (byte) 0), 1);
                newchar.getInventory(MapleInventoryType.CASH).addItem(new Item(5370000, (byte) 0, (short) 1, (byte) 0), 1);
                break;
            case 1: // Adventurer
                //newchar.getInventory(MapleInventoryType.EQUIP).addItem(gift, 1);
                //newchar.getInventory(MapleInventoryType.EQUIP).addItem(gift2, 2);
                //newchar.getInventory(MapleInventoryType.EQUIP).addItem(gift3, 3);
                //newchar.getInventory(MapleInventoryType.EQUIP).addItem(gift4, 4);
                //newchar.getInventory(MapleInventoryType.EQUIP).addItem(gift5, 5);
                newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000016, (byte) 0, (short) 300, (byte) 0), 1);//????????????
                newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000018, (byte) 0, (short) 300, (byte) 0), 2);//????????????
                newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000016, (byte) 0, (short) 300, (byte) 0), 3);//????????????
                newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000018, (byte) 0, (short) 300, (byte) 0), 4);//????????????
                newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000016, (byte) 0, (short) 300, (byte) 0), 5);//????????????
                newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000018, (byte) 0, (short) 300, (byte) 0), 6);//????????????
               // newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(2022613, (byte) 0, (short) 1, (byte) 0), 1);//????????????
               // newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161001, (byte) 0, (short) 1, (byte) 0), 1);
                newchar.getInventory(MapleInventoryType.CASH).addItem(new Item(5370000, (byte) 0, (short) 1, (byte) 0), 1);
                break;
            case 2: // Aran
                newchar.setSkinColor((byte) 11);
                //newchar.getInventory(MapleInventoryType.EQUIP).addItem(gift, 1);
                //newchar.getInventory(MapleInventoryType.EQUIP).addItem(gift2, 2);
                //newchar.getInventory(MapleInventoryType.EQUIP).addItem(gift3, 3);
                //newchar.getInventory(MapleInventoryType.EQUIP).addItem(gift4, 4);
                //newchar.getInventory(MapleInventoryType.EQUIP).addItem(gift5, 5);
                newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000016, (byte) 0, (short) 300, (byte) 0), 1);//????????????
                newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000018, (byte) 0, (short) 300, (byte) 0), 2);//????????????
                newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000016, (byte) 0, (short) 300, (byte) 0), 3);//????????????
                newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000018, (byte) 0, (short) 300, (byte) 0), 4);//????????????
                newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000016, (byte) 0, (short) 300, (byte) 0), 5);//????????????
                newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000018, (byte) 0, (short) 300, (byte) 0), 6);//????????????
               // newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(2022613, (byte) 0, (short) 1, (byte) 0), 1);//????????????
               // newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161048, (byte) 0, (short) 1, (byte) 0), 1);
                newchar.getInventory(MapleInventoryType.CASH).addItem(new Item(5370000, (byte) 0, (short) 1, (byte) 0), 1);
                break;
            case 3: //Evan
                //newchar.getInventory(MapleInventoryType.EQUIP).addItem(gift, 1);
                //newchar.getInventory(MapleInventoryType.EQUIP).addItem(gift2, 2);
                //newchar.getInventory(MapleInventoryType.EQUIP).addItem(gift3, 3);
                // newchar.getInventory(MapleInventoryType.EQUIP).addItem(gift4, 4);
                //newchar.getInventory(MapleInventoryType.EQUIP).addItem(gift5, 5);
                newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000016, (byte) 0, (short) 300, (byte) 0), 1);//????????????
                newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000018, (byte) 0, (short) 300, (byte) 0), 2);//????????????
                newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000016, (byte) 0, (short) 300, (byte) 0), 3);//????????????
                newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000018, (byte) 0, (short) 300, (byte) 0), 4);//????????????
                newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000016, (byte) 0, (short) 300, (byte) 0), 5);//????????????
                newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000018, (byte) 0, (short) 300, (byte) 0), 6);//????????????
                //newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(2022613, (byte) 0, (short) 1, (byte) 0), 1);//????????????
               // newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161052, (byte) 0, (short) 1, (byte) 0), 1);
                newchar.getInventory(MapleInventoryType.CASH).addItem(new Item(5370000, (byte) 0, (short) 1, (byte) 0), 1);
                break;
        }

        if (MapleCharacterUtil.canCreateChar(name) && !LoginInformationProvider.getInstance().isForbiddenName(name)) {
            MapleCharacter.saveNewCharToDB(newchar, job, db);
            c.sendPacket(LoginPacket.addNewCharEntry(newchar, true));
            c.createdChar(newchar.getId());
        } else {
            c.sendPacket(LoginPacket.addNewCharEntry(newchar, false));
        }
    }

    public static final void handleDeleteCharacter(final LittleEndianAccessor slea, final MapleClient c) {
        if (!LoginServer.CanLoginKey(c.getLoginKey(), c.getAccID()) || (LoginServer.getLoginKey(c.getAccID()) == null && !c.getLoginKey().isEmpty())) {
            FileoutputUtil.logToFile("logs/Data/???????????????KEY??????.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " ??????: " + c.getAccountName() + " ?????????key???" + LoginServer.getLoginKey(c.getAccID()) + " ?????????key???" + c.getLoginKey() + " ????????????");
            return;
        }
        if (!LoginServer.CanServerKey(c.getServerKey(), c.getAccID()) || (LoginServer.getServerKey(c.getAccID()) == null && !c.getServerKey().isEmpty())) {
            FileoutputUtil.logToFile("logs/Data/???????????????KEY??????.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " ??????: " + c.getAccountName() + " ?????????key???" + LoginServer.getServerKey(c.getAccID()) + " ?????????key???" + c.getServerKey() + " ????????????");
            return;
        }
        if (slea.available() < 7) {
            return;
        }
        slea.readByte();

        String _2ndPassword;
        _2ndPassword = slea.readMapleAsciiString();

        final int characterId = slea.readInt();

        /*List<String> charNamesa = c.loadCharacterNamesByCharId(characterId);
        for (ChannelServer cs : ChannelServer.getAllInstances()) {
            for (final String name : charNamesa) {
                if (cs.getPlayerStorage().getCharacterByName(name) != null) {
                    FileoutputUtil.logToFile("logs/Data/??????????????????.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " ?????? " + c.getAccountName()");
                    c.getSession().close();
                    return;
                }
            }
        }
        for (final String name : charNamesa) {
            if (CashShopServer.getPlayerStorage().getCharacterByName(name) != null) {
                FileoutputUtil.logToFile("logs/Data/??????????????????.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " ?????? " + c.getAccountName()");
                c.getSession().close();
                return;
            }
        }*/
        List<String> charNames = c.loadCharacterNamesByCharId(characterId);
        for (ChannelServer cs : ChannelServer.getAllInstances()) {
            for (final String name : charNames) {
                MapleCharacter character = cs.getPlayerStorage().getCharacterByName(name);
                if (character != null) {
                    //cs.getPlayerStorage().deregisterPlayer(character);
                    //character.getClient().disconnect(false, false, true);
                    FileoutputUtil.logToFile("logs/Data/??????????????????.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " ?????? " + c.getAccountName());
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
                FileoutputUtil.logToFile("logs/Data/??????????????????.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " ?????? " + c.getAccountName());
                c.getSession().close();
                charactercs.getClient().getSession().close();
            }
        }
        if (!c.login_Auth(characterId)) {
            c.sendPacket(LoginPacket.secondPwError((byte) 0x14));
            return;
        }
        byte state = 0;

        if (c.getSecondPassword() != null) { // On the server, there's a second password
            if (_2ndPassword == null) { // Client's hacking
                c.getSession().close();
                return;
            } else if (!c.getCheckSecondPassword/*check2ndPassword*/(_2ndPassword)) { // Wrong Password
                //state = 12;
                state = 16;
            }
        }

        if (state == 0) {
            state = (byte) c.deleteCharacter(characterId);
        }

        c.sendPacket(LoginPacket.deleteCharResponse(characterId, state));
    }
    public static boolean IP?????????(String a) {
        boolean ret = true;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM  accounts");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (rs.getString("SessionIP") != null) {
                        if (rs.getString("SessionIP").equals("" + a + "")) {
                            if (rs.getInt("loggedin") > 0) {
                                ret = false;
                            }
                        }
                    }
                }
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("IP??????????????????");
        }
        return ret;
    }

    public static int IP?????????2(String a) {
        int ret = 0;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM  accounts");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (rs.getString("SessionIP") != null) {
                        if (rs.getString("SessionIP").equals("" + a + "")) {
                            if (rs.getInt("loggedin") > 0) {
                                ret += 1;
                            }
                        }
                    }
                }
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("IP?????????2?????????");
        }
        return ret;
    }

    public static boolean ??????????????????(String a) {
        boolean ret = true;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM  accounts");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (rs.getString("macs") != null) {
                        if (rs.getString("macs").equals("" + a + "")) {
                            if (rs.getInt("loggedin") > 0) {
                                ret = false;
                            }
                        }
                    }
                }
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("???????????????????????????");
        }
        return ret;
    }

    public static int ??????????????????2(String a) {
        int ret = 0;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM  accounts");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (rs.getString("macs") != null) {
                        if (rs.getString("macs").equals("" + a + "")) {
                            if (rs.getInt("loggedin") > 0) {
                                ret += 1;
                            }
                        }
                    }
                }
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("??????????????????2?????????");
        }
        return ret;
    }
    public static final void handleSecectCharacter(final LittleEndianAccessor slea, final MapleClient c) {
        if (c.getCloseSession()) {// ????????????
            return;
        }
        if (c.getLoginKeya() == 0) {
            //c.sendPacket(MaplePacketCreator.serverNotice(1, "???????????????????????????\r\n???????????????"));
            return;
        }
        if (!c.isCanloginpw()) {// ???????????????
            //c.getSession().close();
            return;
        }
        //if(c.getLoginKey() == null){
        //    c.loadLoginKey();
        //}
        if (!LoginServer.CanLoginKey(c.getLoginKey(), c.getAccID()) || (LoginServer.getLoginKey(c.getAccID()) == null && !c.getLoginKey().isEmpty())) {
            FileoutputUtil.logToFile("logs/Data/???????????????KEY??????.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " ??????: " + c.getAccountName() + " ?????????key???" + LoginServer.getLoginKey(c.getAccID()) + " ?????????key???" + c.getLoginKey() + " ????????????");
            return;
        }
        //if(c.getLoginKey() == null){
        //    c.loadLoginKey();
        //}
        if (!LoginServer.CanServerKey(c.getServerKey(), c.getAccID()) || (LoginServer.getServerKey(c.getAccID()) == null && !c.getServerKey().isEmpty())) {
            FileoutputUtil.logToFile("logs/Data/???????????????KEY??????.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " ??????: " + c.getAccountName() + " ?????????key???" + LoginServer.getServerKey(c.getAccID()) + " ?????????key???" + c.getServerKey() + " ????????????");
            return;
        }
        LoginServer.RemoveClientKey(c.getAccID());
        String clientkey = RandomString(/*10*/);
        c.updateClientKey(clientkey);
        LoginServer.addClientKey(clientkey, c.getAccID());

        final int charId = slea.readInt();
        //if (c.loadLogGedin(c.getAccID()) == 1 || c.loadLogGedin(c.getAccID()) > 2) {
        //    c.getSession().close();
        //    return;
        //}

        List<String> charNamesa = c.loadCharacterNamesByCharId(charId);
        for (ChannelServer cs : ChannelServer.getAllInstances()) {
            for (final String name : charNamesa) {
                if (cs.getPlayerStorage().getCharacterByName(name) != null) {
                    FileoutputUtil.logToFile("logs/Data/????????????.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " ?????? " + c.getAccountName() + "????????????1");
                    c.getSession().close();
                    return;
                }
            }
        }
        for (final String name : charNamesa) {
            if (CashShopServer.getPlayerStorage().getCharacterByName(name) != null) {
                MapleCharacter victim = CashShopServer.getPlayerStorage().getCharacterByName(name);
                CashShopServer.getPlayerStorage().deregisterPlayer(victim.getId(), victim.getName());
                FileoutputUtil.logToFile("logs/Data/????????????.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " ?????? " + c.getAccountName() + "????????????2");
                c.getSession().close();
                return;
            }
        }

        List<String> charNames = c.loadCharacterNamesByCharId(charId);
        for (ChannelServer cs : ChannelServer.getAllInstances()) {
            for (final String name : charNames) {
                MapleCharacter character = cs.getPlayerStorage().getCharacterByName(name);
                if (character != null) {
                    //cs.getPlayerStorage().deregisterPlayer(character);
                    //character.getClient().disconnect(false, false, true);
                    FileoutputUtil.logToFile("logs/Data/????????????.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " ?????? " + c.getAccountName() + "????????????3");
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
                FileoutputUtil.logToFile("logs/Data/????????????.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " ?????? " + c.getAccountName() + "????????????4");
                c.getSession().close();
                charactercs.getClient().getSession().close();
            }
        }

        try (Connection con = DBConPool.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = null;
            ResultSet rs;
            ps = con.prepareStatement("select accountid from characters where id = ?");
            ps.setInt(1, charId);
            rs = ps.executeQuery();
            if (!rs.next() || rs.getInt("accountid") != c.getAccID()) {
                ps.close();
                rs.close();
                return;
            }
            ps.close();
            rs.close();
        } catch (Exception ex) {
            FileoutputUtil.outError("logs/???????????????.txt", ex);
        }
        if (c.getIdleTask() != null) {
            c.getIdleTask().cancel(true);
        }

        c.updateLoginState(MapleClient.LOGIN_SERVER_TRANSITION, c.getSessionIPAddress());
        c.sendPacket(MaplePacketCreator.getServerIP(c, Integer.parseInt(ChannelServer.getInstance(c.getChannel()).getSocket().split(":")[1]), charId));
        System.setProperty(String.valueOf(charId), "1");
    }

}

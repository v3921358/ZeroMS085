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
        //       c.sendPacket(MaplePacketCreator.serverNotice(1, "一台電腦只允許開啟兩個客戶端。"));
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
            errorInfo = "您登录的速度过快!\r\n请重新输入.";
            loginok = 1;
        } else if (loginok == 0 && ban && !c.isGm()) {
            //被封鎖IP或MAC的非GM角色成功登入處理
            loginok = 3;
            //if (macBan) {
            FileoutputUtil.logToFile("logs/data/" + (macBan ? "MAC" : "IP") + "封鎖_登入帳號.txt", "\r\n 時間　[" + FileoutputUtil.NowTime() + "] " + " 所有MAC位址: " + c.getMacs() + " IP地址: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號：　" + account + " 密碼：" + password);
            // this is only an ipban o.O" - maybe we should refactor this a bit so it's more readable
            // MapleCharacter.ban(c.getSessionIPAddress(), c.getSession().remoteAddress().toString().split(":")[0], "Enforcing account ban, account " + account, false, 4, false);
            //}
        } else if (loginok == 0 && (c.getGender() == 10 || c.getSecondPassword() == null)) {
            //選擇性别並設置第二組密碼
//            c.updateLoginState(MapleClient.CHOOSE_GENDER, c.getSessionIPAddress());
            c.sendPacket(LoginPacket.getGenderNeeded(c));
            return;
        } else if (loginok == 5) {
            //帳號不存在
            if (LoginServer.getAutoReg()) {
                if (password.equalsIgnoreCase("fixlogged")) {
                    errorInfo = "這個密碼是解卡密碼，請換其他密碼。";
                } else if (account.length() >= 12) {
                    errorInfo = "您的帐号太长了!\r\n请重新输入.";
                } else {
                    AutoRegister.createAccount(account, password, c.getSession().remoteAddress().toString());
                    if (AutoRegister.success && AutoRegister.mac) {
                        c.sendPacket(MaplePacketCreator.serverNotice(1, "<<" + ServerConfig.SERVERNAME + ">>\r\n\r\n账号注册成功，请重新登录，即可进入游戏。"));
                        FileoutputUtil.logToFile("logs/data/創建帳號.txt", "\r\n 時間　[" + FileoutputUtil.NowTime() + "]" + " IP 地址 : " + c.getSession().remoteAddress().toString().split(":")[0] + " MAC: " + macData + " 帳號：　" + account + " 密碼：" + password);
                    } else if (!AutoRegister.mac) {
                        errorInfo = "无法注册过多的帐号!";
                        AutoRegister.success = false;
                        AutoRegister.mac = true;
                    }
                }
                loginok = 1;
            }
         /**
         * <登录前清掉登陆信息>
         */
        //IP限制多开
        if (gui.ZeroMS_UI.ConfigValuesMap.get("IP多开开关") == 0) {
            if (IP登陆数("" + c.getSessionIPAddress() + "") == false) {
                c.sendPacket(MaplePacketCreator.serverNotice(1, "<<" + ServerConfig.SERVERNAME + ">>\r\n\r\n该IP下已经有登陆的账号。"));
                c.sendPacket(LoginPacket.getLoginFailed(1));
                return;
            }
        } else if (IP登陆数2("" + c.getSessionIPAddress() + "") >= gui.ZeroMS_UI.ConfigValuesMap.get("IP多开数")) {
            c.sendPacket(MaplePacketCreator.serverNotice(1, "<<" + ServerConfig.SERVERNAME + ">>\r\n\r\n登陆超出多开上限 A01。"));
            c.sendPacket(LoginPacket.getLoginFailed(1));
            return;
        }
        //机器码限制多开
        if (gui.ZeroMS_UI.ConfigValuesMap.get("机器多开开关") == 0) {
            if (机器码登陆数("" + macData + "") == false) {
                c.sendPacket(MaplePacketCreator.serverNotice(1, "<<" + ServerConfig.SERVERNAME + ">>\r\n\r\n该机器下已经有登陆的账号。"));
                c.sendPacket(LoginPacket.getLoginFailed(1));
                return;
            }
        } else if (机器码登陆数2("" + macData + "") >= gui.ZeroMS_UI.ConfigValuesMap.get("机器码多开数")) {
            c.sendPacket(MaplePacketCreator.serverNotice(1, "<<" + ServerConfig.SERVERNAME + ">>\r\n\r\n登陆超出多开上限 A02。"));
            c.sendPacket(LoginPacket.getLoginFailed(1));
            return;
        }
        
        } else if (!LoginServer.canLoginAgain(c.getAccID())) {// 換頻後
            int sec = (int) (((LoginServer.getLoginAgainTime(c.getAccID()) + 50 * 1000) - System.currentTimeMillis()) / 1000);
            c.loginAttempt = 0;
            errorInfo = "游戏帐号将于" + sec + "秒后可以登入， 请耐心等候。";
            loginok = 1;
        } else if (!LoginServer.canEnterGameAgain(c.getAccID())) {// 選擇角色後
            int sec = (int) (((LoginServer.getEnterGameAgainTime(c.getAccID()) + 60 * 1000) - System.currentTimeMillis()) / 1000);
            c.loginAttempt = 0;
            errorInfo = "游戏帐号将于" + sec + "秒后可以登入， 请耐心等候。";
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
            FileoutputUtil.logToFile("logs/data/登入帳號.txt", "\r\n 時間　[" + FileoutputUtil.NowTime() + "]" + " IP 地址 : " + c.getSession().remoteAddress().toString().split(":")[0] + " MAC: " + macData + " 帳號：　" + account + " 密碼：" + password);
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

    public static final void ServerListRequest(final MapleClient c) {//显示频道
        if (gui.ZeroMS_UI.ConfigValuesMap.get("蓝蜗牛开关") == 0) {
            c.sendPacket(LoginPacket.getServerList(0, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
        }
        if (gui.ZeroMS_UI.ConfigValuesMap.get("蘑菇仔开关") == 0) {
            c.sendPacket(LoginPacket.getServerList(1, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
        }
        if (gui.ZeroMS_UI.ConfigValuesMap.get("绿水灵开关") == 0) {
            c.sendPacket(LoginPacket.getServerList(2, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
        }
        if (gui.ZeroMS_UI.ConfigValuesMap.get("漂漂猪开关") == 0) {
            c.sendPacket(LoginPacket.getServerList(3, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
        }
        if (gui.ZeroMS_UI.ConfigValuesMap.get("小青蛇开关") == 0) {
            c.sendPacket(LoginPacket.getServerList(4, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
        }
        if (gui.ZeroMS_UI.ConfigValuesMap.get("红螃蟹开关") == 0) {
            c.sendPacket(LoginPacket.getServerList(5, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
        }
        if (gui.ZeroMS_UI.ConfigValuesMap.get("大海龟开关") == 0) {
            c.sendPacket(LoginPacket.getServerList(6, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
        }
        if (gui.ZeroMS_UI.ConfigValuesMap.get("章鱼怪开关") == 0) {
            c.sendPacket(LoginPacket.getServerList(7, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
        }
        if (gui.ZeroMS_UI.ConfigValuesMap.get("顽皮猴开关") == 0) {
            c.sendPacket(LoginPacket.getServerList(8, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
        }
        if (gui.ZeroMS_UI.ConfigValuesMap.get("星精灵开关") == 0) {
            c.sendPacket(LoginPacket.getServerList(9, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
        }
        if (gui.ZeroMS_UI.ConfigValuesMap.get("胖企鹅开关") == 0) {
            c.sendPacket(LoginPacket.getServerList(10, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
        }
        if (gui.ZeroMS_UI.ConfigValuesMap.get("白雪人开关") == 0) {
            c.sendPacket(LoginPacket.getServerList(11, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
        }
        if (gui.ZeroMS_UI.ConfigValuesMap.get("石头人开关") == 0) {
            c.sendPacket(LoginPacket.getServerList(12, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
        }
        if (gui.ZeroMS_UI.ConfigValuesMap.get("紫色猫开关") == 0) {
            c.sendPacket(LoginPacket.getServerList(13, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
        }
        if (gui.ZeroMS_UI.ConfigValuesMap.get("大灰狼开关") == 0) {
            c.sendPacket(LoginPacket.getServerList(14, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
        }
        if (gui.ZeroMS_UI.ConfigValuesMap.get("小白兔开关") == 0) {
            c.sendPacket(LoginPacket.getServerList(15, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
        }
        if (gui.ZeroMS_UI.ConfigValuesMap.get("喷火龙开关") == 0) {
            c.sendPacket(LoginPacket.getServerList(16, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
        }
        if (gui.ZeroMS_UI.ConfigValuesMap.get("火野猪开关") == 0) {
            c.sendPacket(LoginPacket.getServerList(17, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
        }
        if (gui.ZeroMS_UI.ConfigValuesMap.get("青鳄鱼开关") == 0) {
            c.sendPacket(LoginPacket.getServerList(18, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
        }
        if (gui.ZeroMS_UI.ConfigValuesMap.get("花蘑菇开关") == 0) {
            c.sendPacket(LoginPacket.getServerList(19, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
        }
        c.sendPacket(LoginPacket.getEndOfServerList());
    }
    public static final void ServerStatusRequest(final MapleClient c) {//人数太多提示
        if (!c.isCanloginpw()) {
            c.getSession().close();
            return;
        }
        LoginServer.forceRemoveClient(c, false);
        ChannelServer.forceRemovePlayerByCharNameFromDataBase(c, c.loadCharacterNamesByAccId(c.getAccID()));
        // 0 = 人数正常
        // 1 = 人数拥挤
        // 2 = 人数爆满

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
    //选择频道
    public static final void CharlistRequest(final LittleEndianAccessor slea, final MapleClient c) {
        if (!c.isCanloginpw()) {
            c.getSession().close();
            return;
        }
        if (c.getCloseSession()) {
            return;
        }
        if (c.getLoginKeya() == 0) {
            c.sendPacket(MaplePacketCreator.serverNotice(1, "請不要通過非法手段\r\n進入遊戲。"));
            return;
        }
        ChannelServer.forceRemovePlayerByCharNameFromDataBase(c, c.loadCharacterNamesByAccId(c.getAccID()));
        LoginServer.forceRemoveClient(c, false);
        String serverkey = RandomString(/*10*/);
        if (!LoginServer.CanLoginKey(c.getLoginKey(), c.getAccID()) || (LoginServer.getLoginKey(c.getAccID()) == null && !c.getLoginKey().isEmpty())) {
            FileoutputUtil.logToFile("logs/Data/客戶端登錄KEY異常.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號: " + c.getAccountName() + " 客戶端key：" + LoginServer.getLoginKey(c.getAccID()) + " 伺服端key：" + c.getLoginKey() + " 角色列表");
            return;
        }
        final int server = slea.readByte();
        final int channel = slea.readByte() + 1;
        LoginServer.RemoveServerKey(c.getAccID());
        c.setServerKey(serverkey);
        c.updateServerKey(serverkey);
        LoginServer.addServerKey(serverkey, c.getAccID());
        c.setWorld(server + 1);
        System.err.println("【登录信息】  " + c.getSessionIPAddress() + " 连接到世界服务器: " + server + " 频道: " + channel);
        //System.out.println("Client " + c.getSession().getRemoteAddress().toString().split(":")[0] + " is connecting to server " + server + " channel " + channel + "");
        c.setChannel(channel);
        final List<MapleCharacter> chars = c.loadCharacters(server);
        if (chars != null) {
            c.sendPacket(LoginPacket.getCharList(c.getSecondPassword() != null, chars, c.getCharacterSlots()));
        } else {
            c.getSession().close();
        }
    }
    //判断玩家名字
    public static final void checkCharName(final String name, final MapleClient c) {
        c.sendPacket(LoginPacket.charNameResponse(name,
                !MapleCharacterUtil.canCreateChar(name) || LoginInformationProvider.getInstance().isForbiddenName(name)));
    }

    public static final void handleCreateCharacter(final LittleEndianAccessor slea, final MapleClient c) {
        final String name = slea.readMapleAsciiString();
        final int job_type = slea.readInt(); // 1 = Adventurer, 0 = Cygnus, 2 = Aran
        if(job_type != 1){
            c.sendPacket(MaplePacketCreator.serverNotice(1, "该职业无法直接创建\r\n请返回到输入账号密码界面重新登录\r\n创建职业选择冒险家\r\n想玩的职业均可在拍卖进行快速转职\r\n骑士团关闭创建和转职\r\n希望您在仙境冒险岛玩得开心愉快~"));
            return;
        }
        JobType job = JobType.getByType(job_type);
        if (job == null) {
            System.out.println("New job type found: " + job_type);
            return;
        }
        final short db = slea.readShort(); //whether dual blade = 1 or adventurer = 0

        //if (job_type == 3) {
        //    c.sendPacket(MaplePacketCreator.serverNotice(1, "很抱歉\r\n该职业還未開放\r\n日後如果BUG差不多會開放其他職業。"));
            //    c.sendPacket(MaplePacketCreator.enableActions());
        //    return;
        //}
        //if (db == 1) {
        //    c.sendPacket(MaplePacketCreator.serverNotice(1, "很抱歉\r\n该职业還未開放\r\n日後如果BUG差不多會開放其他職業。"));
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
            FileoutputUtil.logToFile("logs/Hack/Ban/角色數量異常.txt", "\r\n " + FileoutputUtil.NowTime() + " 帳號 " + c.getAccountName());
            c.getSession().close();
            return;
        }

        if (gender != 0 && gender != 1) {
            FileoutputUtil.logToFile("logs/Hack/Ban/修改封包.txt", "\r\n " + FileoutputUtil.NowTime() + " 帳號 " + c.getAccountName() + " 性別類型 " + gender);
            c.getSession().close();
            return;
        }

        if (job_type != 0 && job_type != 1 && job_type != 2 && job_type != 3) {
            FileoutputUtil.logToFile("logs/Data/非法創建.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號 " + c.getAccountName() + " 職業類型 " + job_type);
            return;
        }
        int index = 0;
        int[] items = new int[]{face, hair, top, bottom, shoes, weapon};
        for (int k : items) {
            if (k > 1000) {
                if (!LoginInformationProvider.getInstance().isEligibleItem(gender, index, /*job == JobType.影武者 ? 1 : */ job.id, k)) {
                    System.out.println(gender + " | " + index + " | " + job.type + " | " + k);
                    return;
                }
                index++;
            }
        }

        /*boolean dangerousIp = c.dangerousIp(c.getSession().remoteAddress().toString());
        if (dangerousIp) {
            World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM 密語系統] 危險IP創建角色 帳號 " + c.getAccountName() + " 名字 " + name + " 職業類型 " + JobType + " 臉型 " + face + " 髮型 " + hair + " 衣服 " + top + " 褲子 " + bottom + " 鞋子 " + shoes + " 武器 " + weapon + " 性別 " + gender));
            World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM 密語系統] 危險IP創建角色 帳號 " + c.getAccountName() + " 名字 " + name + " 職業類型 " + JobType + " 臉型 " + face + " 髮型 " + hair + " 衣服 " + top + " 褲子 " + bottom + " 鞋子 " + shoes + " 武器 " + weapon + " 性別 " + gender));
            World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM 密語系統] 危險IP創建角色 帳號 " + c.getAccountName() + " 名字 " + name + " 職業類型 " + JobType + " 臉型 " + face + " 髮型 " + hair + " 衣服 " + top + " 褲子 " + bottom + " 鞋子 " + shoes + " 武器 " + weapon + " 性別 " + gender));
            FileoutputUtil.logToFile("logs/Data/危險IP創建角色.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號 " + c.getAccountName() + " 名字 " + name + " 職業類型 " + JobType + " 臉型 " + face + " 髮型 " + hair + " 衣服 " + top + " 褲子 " + bottom + " 鞋子 " + shoes + " 武器 " + weapon + " 性別 " + gender);
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
                //创建角色赠送道具  新手礼包
                newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000016, (byte) 0, (short) 300, (byte) 0), 1);//白色药水
                newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000018, (byte) 0, (short) 300, (byte) 0), 2);//蓝色药水
                newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000016, (byte) 0, (short) 300, (byte) 0), 3);//白色药水
                newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000018, (byte) 0, (short) 300, (byte) 0), 4);//蓝色药水
                newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000016, (byte) 0, (short) 300, (byte) 0), 5);//白色药水
                newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000018, (byte) 0, (short) 300, (byte) 0), 6);//蓝色药水
               // newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(2022613, (byte) 0, (short) 1, (byte) 0), 1);//新手礼包
              //  newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161047, (byte) 0, (short) 1, (byte) 0), 1);
                newchar.getInventory(MapleInventoryType.CASH).addItem(new Item(5370000, (byte) 0, (short) 1, (byte) 0), 1);
                break;
            case 1: // Adventurer
                //newchar.getInventory(MapleInventoryType.EQUIP).addItem(gift, 1);
                //newchar.getInventory(MapleInventoryType.EQUIP).addItem(gift2, 2);
                //newchar.getInventory(MapleInventoryType.EQUIP).addItem(gift3, 3);
                //newchar.getInventory(MapleInventoryType.EQUIP).addItem(gift4, 4);
                //newchar.getInventory(MapleInventoryType.EQUIP).addItem(gift5, 5);
                newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000016, (byte) 0, (short) 300, (byte) 0), 1);//白色药水
                newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000018, (byte) 0, (short) 300, (byte) 0), 2);//蓝色药水
                newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000016, (byte) 0, (short) 300, (byte) 0), 3);//白色药水
                newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000018, (byte) 0, (short) 300, (byte) 0), 4);//蓝色药水
                newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000016, (byte) 0, (short) 300, (byte) 0), 5);//白色药水
                newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000018, (byte) 0, (short) 300, (byte) 0), 6);//蓝色药水
               // newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(2022613, (byte) 0, (short) 1, (byte) 0), 1);//新手礼包
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
                newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000016, (byte) 0, (short) 300, (byte) 0), 1);//白色药水
                newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000018, (byte) 0, (short) 300, (byte) 0), 2);//蓝色药水
                newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000016, (byte) 0, (short) 300, (byte) 0), 3);//白色药水
                newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000018, (byte) 0, (short) 300, (byte) 0), 4);//蓝色药水
                newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000016, (byte) 0, (short) 300, (byte) 0), 5);//白色药水
                newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000018, (byte) 0, (short) 300, (byte) 0), 6);//蓝色药水
               // newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(2022613, (byte) 0, (short) 1, (byte) 0), 1);//新手礼包
               // newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161048, (byte) 0, (short) 1, (byte) 0), 1);
                newchar.getInventory(MapleInventoryType.CASH).addItem(new Item(5370000, (byte) 0, (short) 1, (byte) 0), 1);
                break;
            case 3: //Evan
                //newchar.getInventory(MapleInventoryType.EQUIP).addItem(gift, 1);
                //newchar.getInventory(MapleInventoryType.EQUIP).addItem(gift2, 2);
                //newchar.getInventory(MapleInventoryType.EQUIP).addItem(gift3, 3);
                // newchar.getInventory(MapleInventoryType.EQUIP).addItem(gift4, 4);
                //newchar.getInventory(MapleInventoryType.EQUIP).addItem(gift5, 5);
                newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000016, (byte) 0, (short) 300, (byte) 0), 1);//白色药水
                newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000018, (byte) 0, (short) 300, (byte) 0), 2);//蓝色药水
                newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000016, (byte) 0, (short) 300, (byte) 0), 3);//白色药水
                newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000018, (byte) 0, (short) 300, (byte) 0), 4);//蓝色药水
                newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000016, (byte) 0, (short) 300, (byte) 0), 5);//白色药水
                newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000018, (byte) 0, (short) 300, (byte) 0), 6);//蓝色药水
                //newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(2022613, (byte) 0, (short) 1, (byte) 0), 1);//新手礼包
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
            FileoutputUtil.logToFile("logs/Data/客戶端登錄KEY異常.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號: " + c.getAccountName() + " 客戶端key：" + LoginServer.getLoginKey(c.getAccID()) + " 伺服端key：" + c.getLoginKey() + " 刪除角色");
            return;
        }
        if (!LoginServer.CanServerKey(c.getServerKey(), c.getAccID()) || (LoginServer.getServerKey(c.getAccID()) == null && !c.getServerKey().isEmpty())) {
            FileoutputUtil.logToFile("logs/Data/客戶端頻道KEY異常.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號: " + c.getAccountName() + " 客戶端key：" + LoginServer.getServerKey(c.getAccID()) + " 伺服端key：" + c.getServerKey() + " 刪除角色");
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
                    FileoutputUtil.logToFile("logs/Data/非法刪除角色.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號 " + c.getAccountName()");
                    c.getSession().close();
                    return;
                }
            }
        }
        for (final String name : charNamesa) {
            if (CashShopServer.getPlayerStorage().getCharacterByName(name) != null) {
                FileoutputUtil.logToFile("logs/Data/非法刪除角色.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號 " + c.getAccountName()");
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
                    FileoutputUtil.logToFile("logs/Data/非法刪除角色.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號 " + c.getAccountName());
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
                FileoutputUtil.logToFile("logs/Data/非法刪除角色.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號 " + c.getAccountName());
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
    public static boolean IP登陆数(String a) {
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
            System.err.println("IP登陆数、出错");
        }
        return ret;
    }

    public static int IP登陆数2(String a) {
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
            System.err.println("IP登陆数2、出错");
        }
        return ret;
    }

    public static boolean 机器码登陆数(String a) {
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
            System.err.println("机器码登陆数、出错");
        }
        return ret;
    }

    public static int 机器码登陆数2(String a) {
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
            System.err.println("机器码登陆数2、出错");
        }
        return ret;
    }
    public static final void handleSecectCharacter(final LittleEndianAccessor slea, final MapleClient c) {
        if (c.getCloseSession()) {// 多重登入
            return;
        }
        if (c.getLoginKeya() == 0) {
            //c.sendPacket(MaplePacketCreator.serverNotice(1, "請不要通過非法手段\r\n進入遊戲。"));
            return;
        }
        if (!c.isCanloginpw()) {// 登入口驗證
            //c.getSession().close();
            return;
        }
        //if(c.getLoginKey() == null){
        //    c.loadLoginKey();
        //}
        if (!LoginServer.CanLoginKey(c.getLoginKey(), c.getAccID()) || (LoginServer.getLoginKey(c.getAccID()) == null && !c.getLoginKey().isEmpty())) {
            FileoutputUtil.logToFile("logs/Data/客戶端登錄KEY異常.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號: " + c.getAccountName() + " 客戶端key：" + LoginServer.getLoginKey(c.getAccID()) + " 伺服端key：" + c.getLoginKey() + " 開始遊戲");
            return;
        }
        //if(c.getLoginKey() == null){
        //    c.loadLoginKey();
        //}
        if (!LoginServer.CanServerKey(c.getServerKey(), c.getAccID()) || (LoginServer.getServerKey(c.getAccID()) == null && !c.getServerKey().isEmpty())) {
            FileoutputUtil.logToFile("logs/Data/客戶端頻道KEY異常.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號: " + c.getAccountName() + " 客戶端key：" + LoginServer.getServerKey(c.getAccID()) + " 伺服端key：" + c.getServerKey() + " 開始遊戲");
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
                    FileoutputUtil.logToFile("logs/Data/非法登錄.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號 " + c.getAccountName() + "開始遊戲1");
                    c.getSession().close();
                    return;
                }
            }
        }
        for (final String name : charNamesa) {
            if (CashShopServer.getPlayerStorage().getCharacterByName(name) != null) {
                MapleCharacter victim = CashShopServer.getPlayerStorage().getCharacterByName(name);
                CashShopServer.getPlayerStorage().deregisterPlayer(victim.getId(), victim.getName());
                FileoutputUtil.logToFile("logs/Data/非法登錄.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號 " + c.getAccountName() + "開始遊戲2");
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
                    FileoutputUtil.logToFile("logs/Data/非法登錄.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號 " + c.getAccountName() + "開始遊戲3");
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
                FileoutputUtil.logToFile("logs/Data/非法登錄.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帳號 " + c.getAccountName() + "開始遊戲4");
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
            FileoutputUtil.outError("logs/資料庫異常.txt", ex);
        }
        if (c.getIdleTask() != null) {
            c.getIdleTask().cancel(true);
        }

        c.updateLoginState(MapleClient.LOGIN_SERVER_TRANSITION, c.getSessionIPAddress());
        c.sendPacket(MaplePacketCreator.getServerIP(c, Integer.parseInt(ChannelServer.getInstance(c.getChannel()).getSocket().split(":")[1]), charId));
        System.setProperty(String.valueOf(charId), "1");
    }

}

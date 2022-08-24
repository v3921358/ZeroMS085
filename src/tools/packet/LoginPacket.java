
package tools.packet;

import java.util.List;
import java.util.Set;

import client.MapleClient;
import client.MapleCharacter;
import constants.Balloon;
import constants.GameConstants;
import constants.ServerConfig;
import constants.ServerConstants;
import constants.WorldConstants;
import handling.SendPacketOpcode;
import handling.channel.ChannelServer;
import handling.login.LoginServer;
import java.util.Map;
import server.Randomizer;
import tools.HexTool;
import tools.MaplePacketCreator;
import tools.data.MaplePacketLittleEndianWriter;

public class LoginPacket {
    /**
     * 发送一个hello packet
     *
     * @参数 mapleversion为客户端版本
     * @参数 sendiv 发送
     * @参数 recviv 接收
     * @参数 testServer
     * @完毕
     */
    public static final byte[] getHello(final short mapleVersion, final byte[] sendIv, final byte[] recvIv) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(16);

        mplew.writeShort(14); // 13 = MSEA, 14 = GlobalMS, 15 = EMS
        mplew.writeShort(mapleVersion);
        mplew.writeMapleAsciiString(ServerConstants.MAPLE_PATCH);
        mplew.write(recvIv);
        mplew.write(sendIv);
        mplew.write(4); // 7 = MSEA, 8 = GlobalMS, 5 = Test Server

        return mplew.getPacket();
    }
    /**
     * 发送ping 包.
     *
     * @返回 数据包
     */
    public static final byte[] getPing() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(16);

        mplew.writeShort(SendPacketOpcode.PING.getValue());

        return mplew.getPacket();
    }

    public static final byte[] StrangeDATA() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(16);

        mplew.writeShort(0x12);
        // 长字符串=生成的静态公钥
        mplew.writeMapleAsciiString("30819F300D06092A864886F70D010101050003818D0030818902818100994F4E66B003A7843C944E67BE4375203DAA203C676908E59839C9BADE95F53E848AAFE61DB9C09E80F48675CA2696F4E897B7F18CCB6398D221C4EC5823D11CA1FB9764A78F84711B8B6FCA9F01B171A51EC66C02CDA9308887CEE8E59C4FF0B146BF71F697EB11EDCEBFCE02FB0101A7076A3FEB64F6F6022C8417EB6B87270203010001");
        //mplew.writeMapleAsciiString("30819D300D06092A864886F70D010101050003818B00308187028181009E68DD55B554E5924BA42CCB2760C30236B66234AFAA420E8E300E74F1FDF27CD22B7FF323C324E714E143D71780C1982E6453AD87749F33E540DB44E9F8C627E6898F915587CD2A7D268471E002D30DF2E214E2774B4D3C58609155A7C79E517CEA332AF96C0161BFF6EDCF1CB44BA21392BED48CBF4BD1622517C6EA788D8D020111");

        return mplew.getPacket();
    }
    /**
     * 发送一个登录失败的数据包。
     *
     * 可能的值 reason:
     * 3: 已被删除或终止的账号，请重新确认。
     * 4: 密码输入错误，请重新输入。
     * 5: 未登陆的账号，请重新确认。
     * 6: 因系统错误，无法登陆，请稍后再试。
     * 7: 目前正在登陆中的账号，请重新确认。
     * 8: 因系统错误，无法登陆，请稍后再试。
     * 9: 因系统错误，无法登陆，请稍后再试。
     * 10: 目前因链接邀请过多，服务器未能处理，请稍后再试。
     * 11: 韩文：只有20岁以上才能连接的频道
     * 13: 目前该IP无法登陆master login，请重新确认。
     * 14: 韩文：一个按钮跳转到（http://www.nexon.com/） 15:
     * 该账号不是有效的盛大通行证。一个按钮跳转到（http://www.nexon.com/）
     * 16: （没有任何反应，登陆按钮不被禁止）
     * 17: （没有任何反应，登陆按钮不被禁止）
     * 21: 提示框（请去盛大主页矫正密宝，再登陆。）
     * 22: 新增盛大协议同意条款
     * 23: 韩文：（禁止连接的IP） 确定后关闭客户端
     * 25: （没有任何反应，登陆按钮不被禁止）
     * 26: （没有任何反应，登陆按钮不被禁止）
     * 27: （没有任何反应，登陆按钮不被禁止） #U-OTP服务是使用一次性密码进行登录的双层安全服务。 QQ上的手机令牌一样
     * 28：韩文：出现一个U-OTP 6位密码输入框//输入后发送封包1B 00 [C1 E6 77 00]
     * 29: 韩文:U-OTP提示
     * 30: 韩文:U-OTP提示
     * 31: 韩文:U-OTP提示
     * 32: （没有任何反应，登陆按钮不被禁止）
     * 33: 韩文:U-OTP提示
     * 34: 服务器用户太多,连接出现延迟.请重新尝试.
     * 35: 韩文:U-OTP提示
     * 36: 韩文:U-OTP提示
     * 37: （没有任何反应，登陆按钮不被禁止）
     * 38: （没有任何反应，登陆按钮不被禁止）
     *
     * @参数 reason 测井在失败的原因。
     * @返回 登录失败的数据包。
     */
    public static final byte[] getLoginFailed(final int reason) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(16);
        mplew.writeShort(SendPacketOpcode.LOGIN_STATUS.getValue());
        mplew.write(reason);
        mplew.writeShort(0);

        return mplew.getPacket();
    }
    /**
     * 发送一个认证服务器的封包
     *
     * @返回 数据包
     */
    public static final byte[] getLoginAUTH() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        /*
         * 1D 00 - 包头
         * 09 00 4D 61 70 4C 6F 67 69 6E 31 - MapLogin1
         * 24 FF EC 77 -2012020516
         */
        mplew.writeShort(SendPacketOpcode.LOGIN_AUTH.getValue());
        mplew.writeMapleAsciiString(ServerConfig.getMapLoginType());
       /* String[] a = {"MapLogin2", "MapLogin2", "MapLogin2"};
        mplew.writeMapleAsciiString(a[((int) (Math.random() * a.length))]);*/
        return mplew.getPacket();
    }

    public static final byte[] getPermBan(final byte reason) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(16);

        mplew.writeShort(SendPacketOpcode.LOGIN_STATUS.getValue());
        mplew.writeShort(2); // 帐户是被禁止的
        mplew.write(0);
        mplew.write(reason);
        mplew.write(HexTool.getByteArrayFromHexString("01 01 01 01 00"));

        return mplew.getPacket();
    }

    public static final byte[] getTempBan(final long timestampTill, final byte reason) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(17);
        /*
         * 0x01 = 您的账号违反用户协议已经被永久封停
         */
        mplew.writeShort(SendPacketOpcode.LOGIN_STATUS.getValue());
        mplew.write(2);
        mplew.write(HexTool.getByteArrayFromHexString("00 00 00 00 00"));
        mplew.write(reason);
        mplew.writeLong(timestampTill); // Tempban 日期处理 -- 64位长, 100 ns的间隔从 1/1/1601.

        return mplew.getPacket();
    }
    /*
     * 发送选择性别成功封包
     * @返回 封包
     */
    public static final byte[] getGenderChanged(final MapleClient client) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GENDER_SET.getValue());
        mplew.write(0);
        mplew.writeMapleAsciiString(client.getAccountName());
        mplew.writeMapleAsciiString(String.valueOf(client.getAccID()));

        return mplew.getPacket();
    }

    public static final byte[] getGenderNeeded(final MapleClient client) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CHOOSE_GENDER.getValue());
        mplew.writeMapleAsciiString(client.getAccountName());

        return mplew.getPacket();
    }
    //登陆游戏后显示的弹窗
    public static final byte[] getAuthSuccessRequest(final MapleClient client) {

        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.LOGIN_STATUS.getValue());
        mplew.write(0);// 是否开启提示登录后的帐号信息 0 = 开启 1 = 关闭
        mplew.writeInt(client.getAccID());// 账号ID
        mplew.write(client.getGender());// 性别
        mplew.write(0); // Admin byte - Commands (v29 >> 5) & 1;
        mplew.write(0); // Admin byte - Commands (v29 >> 4) & 1;
        mplew.writeMapleAsciiString(client.getAccountName());

        mplew.write(HexTool.getByteArrayFromHexString("00 00 00 03 01 00 00 00 E2 ED A3 7A FA C9 01"));
        mplew.write(0);
        mplew.writeLong(0);
        mplew.writeLong(0);
        mplew.writeShort(0); //writeMapleAsciiString  CInPacket::DecodeStr
        mplew.write(1);//屏蔽登录弹出框
        mplew.writeMapleAsciiString(String.valueOf(client.getAccID()));
        mplew.writeMapleAsciiString(client.getAccountName());
        mplew.write(1); //0 = 提示没填身份证
        client.sendPacket(MaplePacketCreator.serverNotice(1, "【 欢迎来到" + ServerConfig.SERVERNAME +" 】\r\n我们拥有完善的技术 稳定的资金\r\n有信心也有实力将游戏持续运营下去\r\n我们以零容忍的态度打击外挂使用者\r\n热心对待每一位支持我们的玩家 \r\n我们一直用心在做\r\n在这里跟曾经的小伙伴一起重温旧梦！"));

        return mplew.getPacket();
    }

    public static final byte[] deleteCharResponse(final int cid, final int state) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.DELETE_CHAR_RESPONSE.getValue());
        mplew.writeInt(cid);
        mplew.write(state);

        return mplew.getPacket();
    }

    public static final byte[] secondPwError(final byte mode) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);

        /*
         * 14 - Invalid password
         * 15 - Second password is incorrect
         */
        mplew.writeShort(SendPacketOpcode.SECONDPW_ERROR.getValue());
        mplew.write(mode);

        return mplew.getPacket();
    }
    
/**
     * 发送数据包的详细介绍了服务器和在线人数
     *
     * @参数 serverId - 服务器ID
     * @参数 channelLoad 负荷的频道-1200似乎是最大
     * @返回 服务器信息包。
     */
    
    public static final byte[] getServerList(final int serverId, final String serverName, final Map<Integer, Integer> channelLoad, final int a) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SERVERLIST.getValue());
        mplew.write(serverId);//serverId // 0 = Aquilla, 1 = bootes, 2 = cass, 3 = delphinus
        mplew.writeMapleAsciiString(serverName);
        mplew.write(a);//mplew.write(LoginServer.getFlag());
        mplew.writeMapleAsciiString(WorldConstants.WORLD_TIP);
        mplew.writeShort(100);
        mplew.writeShort(100);

        int lastChannel = 1;
        Set<Integer> channels = ChannelServer.getAllChannels();
        for (int i = WorldConstants.CHANNEL_COUNT; i > 0; i--) {
            if (channels.contains(i)) {
                lastChannel = i;
                break;
            }
        }
        mplew.write(lastChannel);
        mplew.writeInt(500);

        int load;
        for (int i = 1; i <= lastChannel; i++) {
            if (channels.contains(i)) {
                load = channelLoad.get(i);
            } else {
                load = 1200;
            }
            mplew.writeMapleAsciiString(serverName + "-" + i);
            mplew.writeInt(Math.max(load * 55 / WorldConstants.MAX_CHAR_VIEW, 1));
            mplew.write(serverId);
            mplew.writeShort(i - 1);
        }
        mplew.writeShort(0);
      /*  mplew.writeShort(GameConstants.getBalloons().size());
        for (Balloon balloon : GameConstants.getBalloons()) {
            mplew.writeShort(balloon.nX);
            mplew.writeShort(balloon.nY);
            mplew.writeMapleAsciiString(balloon.sMessage);
        }*/
        //System.err.println(HexTool.toString(mplew.getPacket().getBytes()));

        return mplew.getPacket();
    }
    /**
     * 发送数据包说服务器列表结束
     *
     * @返回 结束服务器列表的数据包
     */
    public static final byte[] getEndOfServerList() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SERVERLIST.getValue());
        mplew.write(0xFF);

        return mplew.getPacket();
    }
    /**
     * 发送数据包详细介绍了服务器的状态信息。
     *
     * @参数 status 服务器状态。
     * @返回 服务器状态数据包。
     */
    public static final byte[] getServerStatus(final int status) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        /*
         * 可能的值 status:
         * 0 - 没有消息
         * 1 - 当前世界连接数量较多，这可能会导致登录游戏时有些困难。
         * 2 - 当前世界上的连接已到达最高限制。请选择别的服务器进行游戏或稍后再试。
         */
        mplew.writeShort(SendPacketOpcode.SERVERSTATUS.getValue());
        mplew.writeShort(status);

        return mplew.getPacket();
    }

    public static final byte[] getCharList(final boolean secondpw, final List<MapleCharacter> chars, int charslots) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CHARLIST.getValue());
        mplew.write(0);
        mplew.writeInt(0);
        mplew.write(chars.size()); // 1

        for (final MapleCharacter chr : chars) {
            addCharEntry(mplew, chr, !chr.isGM() && chr.getLevel() >= 10);
        }
        mplew.writeShort(3); // second pw request
        mplew.writeInt(charslots);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static final byte[] addNewCharEntry(final MapleCharacter chr, final boolean worked) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ADD_NEW_CHAR_ENTRY.getValue());
        mplew.write(worked ? 0 : 1);
        addCharEntry(mplew, chr, false);

        return mplew.getPacket();
    }

    public static final byte[] charNameResponse(final String charname, final boolean nameUsed) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CHAR_NAME_RESPONSE.getValue());
        mplew.writeMapleAsciiString(charname);
        mplew.write(nameUsed ? 1 : 0);

        return mplew.getPacket();
    }

    private static final void addCharEntry(final MaplePacketLittleEndianWriter mplew, final MapleCharacter chr, boolean ranking) {
        PacketHelper.addCharStats(mplew, chr);
        PacketHelper.addCharLook(mplew, chr, true);
        mplew.write(0); //<-- who knows
        //mplew.write(0); //<-- who knows
        // mplew.write(ranking ? 1 : 0);
        // if (ranking) {
        //      mplew.writeInt(chr.getRank());
        //      mplew.writeInt(chr.getRankMove());
        //      mplew.writeInt(chr.getJobRank());
        //      mplew.writeInt(chr.getJobRankMove());
        // }
    }

    public static final byte[] licenseResult() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.LICENSE_RESULT.getValue());
        mplew.write(1);
        return mplew.getPacket();
    }
}

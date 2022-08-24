package constants;

import database.DBConPool;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import server.ServerProperties;

public class ServerConfig {

    public static boolean pvp = true;//pvp开启
    public static int pvpch = 3;//pvp频道
    public static boolean LOG_MRECHANT = true;//精灵商人出售记录
    public static boolean LOG_CSBUY = true;//商城购买记录
    public static boolean LOG_DAMAGE = false;//伤害检测记录
    public static boolean LOG_CHAT = true;//聊天记录
    public static boolean LOG_MEGA = true;//广播记录
    public static boolean LOG_PACKETS = false;
    public static boolean CHRLOG_PACKETS = false;//角色封包输出
    public static boolean AUTO_REGISTER = true;
    public static boolean LOCALHOST = false;//废弃的
    public static boolean Encoder = false;//废弃的
    public static boolean TESPIA = false;//测试服
    public static boolean shieldWardAll = false;//卷轴对装备防爆
    public static boolean DISCOUNTED = false;//栏位扩充
    public static boolean dSoltMax = true;//物品叠加开关
    public static boolean dMapAddMob = false;//自定义地图刷怪开关
    public static boolean 无限BUFF = false;//开启无限BUFF
    public static int dMapAddMobNum = 2;//自定义地图刷怪倍数
    public static String dMapAddMobMapList = "104040000,104040001,100020000,240040510,104010001,100040001,100040002,100040003,100040004,105050000,105050100,105070001,200010000,200020000,200040000,701010000,261020500,300010100,300010200,300020100,300020200,103010001,541010000,541010010,551030100,550000100,550000200,100000003";//自定义怪物倍数地图列表id
    public static String SERVERNAME = "仙境冒险岛";
    public static String version = "【Ver.085 授权版- 仙境冒险岛】";
    public static String TOUDING = "欢迎来到冒险岛Online，祝你游戏愉快！";
    public static String IP = "221.231.130.70";
    public static String wzpath = "WZ";
    private static String EVENTS = null;
    public static boolean DEBUG_MODE = false;//debug模式开关
    public static boolean NMGB = true;//匿名广播开关
    public static boolean PDCS = false;//频道掉线测试
    public static int RSGS = 20;//人物灌水百分比
    public static int 等级上限 = 250;
    public static int dSoltMaxNum = 9999;//物品叠加数
    public static int 开店额外经验 = 30;
    public static int 结婚额外经验 = 30;
    public static int 家族徽章费用 = 100000;
    public static int ExpBound1 = 1;
    public static int ExpBound2 = 1;
    public static int ExpBound3 = 1;
    public static int ExpBound4 = 1;
    public static int ExpBound5 = 1;
    public static int ExpBound6 = 1;
    public static boolean 测谎仪开关 = true;
    public static boolean 锻造系统开关 = true;
    public static boolean 学院系统开关 = true;
    public static boolean 检测外挂自动封号 = false;
    public static int 清理怪物时间 = 60;
    private static String MapLoginType = "MapLogin1";//登录背景
    public static int renew_A = 200;
    public static int renew_S = 50;
    public static int renew_3line = 10;
    public static boolean useAutoPot = true;//快速潜能
    
    public static boolean isPvPChannel(int ch) {
        return pvp && ch == pvpch;
    }

    public static final byte[] Gateway_IP = new byte[]{(byte) 221, (byte) 231, (byte) 130, (byte) 70};
    public static final byte[] Gateway_IP2 = new byte[]{(byte) 221, (byte) 231, (byte) 130, (byte) 70};

    public static String[] getEvents(boolean reLoad) {
        return getEventList(reLoad).split(",");
    }

    public static String getEventList(boolean reLoad) {
        if (EVENTS == null || reLoad) {
            File root = new File("scripts/event");
            File[] files = root.listFiles();
            EVENTS = "";
            for (File file : files) {
                if (!file.isDirectory()) {
                    String[] fileName = file.getName().split("\\.");
                    if (fileName.length > 1 && "js".equals(fileName[fileName.length - 1])) {
                        for (int i = 0; i < fileName.length - 1; i++) {
                            EVENTS += fileName[i];
                        }
                        EVENTS += ",";
                    }
                }
            }
        }
        return EVENTS;
    }

    public static boolean isAutoRegister() {
        return AUTO_REGISTER;
    }

    public static String getVipMedalName(int lv) {
        String medal = "";
        if (SERVERNAME.equals("冒险岛")) {
            switch (lv) {
                case 1:
                    medal = " <普通VIP>";
                    break;
                case 2:
                    medal = " <进阶VIP>";
                    break;
                case 3:
                    medal = " <高級VIP>";
                    break;
                case 4:
                    medal = " <尊贵VIP>";
                    break;
                case 5:
                    medal = " <至尊VIP>";
                    break;
                default:
                    medal = " <VIP" + medal + ">";
                    break;
            }
        } else if (SERVERNAME.equals("冒险岛")) {
            switch (lv) {
                case 1:
                    medal = "☆";
                    break;
                case 2:
                    medal = "☆★";
                    break;
                case 3:
                    medal = "☆★☆";
                    break;
                case 4:
                    medal = "☆★☆★";
                    break;
                case 5:
                    medal = "☆★☆★☆";
                    break;
                case 6:
                    medal = "☆★☆★☆★";
                    break;
                case 7:
                    medal = "☆★☆★☆★☆";
                    break;
                case 8:
                    medal = "☆★☆★☆★☆★";
                    break;
                case 9:
                    medal = "☆★☆★☆★☆★☆";
                    break;
                case 10:
                    medal = "☆★☆★☆★☆★☆★";
                    break;
                case 11:
                    medal = "冒险岛第一土豪";
                    break;
                default:
                    medal = "<VIP" + medal + ">";
                    break;
            }
        }
        return medal;
    }
    
            /*
     * 是否开启防爆卷轴卷轴对所有装备防爆
     *
     * true = 对所有砸卷失败消失防爆
     * false= 只针对强化装备失败防爆
     */
    public boolean isShieldWardAll() {
        return shieldWardAll;
    }

    public void setShieldWardAll(boolean all) {
        this.shieldWardAll = all;
    }
    
    public static String getMapLoginType() {
        return MapLoginType;
    }
    

    public static void setMapLoginType(String s) {
        MapLoginType = s;
    }
    

    public static void loadSetting() {

        LOG_MRECHANT = ServerProperties.getProperty("ZeroMS.merchantLog", LOG_MRECHANT);//精灵商人出售记录
        LOG_MEGA = ServerProperties.getProperty("ZeroMS.megaLog", LOG_MEGA);//广播记录
        LOG_CSBUY = ServerProperties.getProperty("ZeroMS.csLog", LOG_CSBUY);//商城记录
        LOG_DAMAGE = ServerProperties.getProperty("ZeroMS.damLog", LOG_DAMAGE);//伤害记录
        LOG_CHAT = ServerProperties.getProperty("ZeroMS.chatLog", LOG_CHAT);//聊天记录输出
        LOG_PACKETS = ServerProperties.getProperty("ZeroMS.packetLog", LOG_PACKETS);
        IP = ServerProperties.getProperty("ZeroMS.ip.gateway", IP);//商城记录
        AUTO_REGISTER = ServerProperties.getProperty("ZeroMS.autoRegister", AUTO_REGISTER);
        SERVERNAME = ServerProperties.getProperty("ZeroMS.serverName", SERVERNAME);
        wzpath = ServerProperties.getProperty("ZeroMS.wzpath", wzpath);
        DEBUG_MODE = ServerProperties.getProperty("ZeroMS.debug", DEBUG_MODE);
        NMGB = ServerProperties.getProperty("ZeroMS.nmgb", NMGB);//匿名广播开关
        PDCS = ServerProperties.getProperty("ZeroMS.pdcs", PDCS);//频道掉线测试
        CHRLOG_PACKETS = ServerProperties.getProperty("ZeroMS.jsLog", CHRLOG_PACKETS);//角色封包输出
        dMapAddMob = ServerProperties.getProperty("ZeroMS.dMapAddMob", dMapAddMob);//自定义地图刷怪开关
        dMapAddMobNum = ServerProperties.getProperty("ZeroMS.dMapAddMobNum", dMapAddMobNum);//自定义地图刷怪倍数
        dMapAddMobMapList = ServerProperties.getProperty("ZeroMS.dMapAddMobMapList", dMapAddMobMapList);//地图列表id
        shieldWardAll = ServerProperties.getProperty("ZeroMS.shieldWardAll",shieldWardAll); //是否开启防爆卷轴卷轴对所有装备防爆
        RSGS = ServerProperties.getProperty("ZeroMS.rsgs", RSGS);//人物灌水百分比
        dSoltMax = ServerProperties.getProperty("ZeroMS.dSoltMax", dSoltMax);//泡点系统开关
        无限BUFF = ServerProperties.getProperty("ZeroMS.无限BUFF", 无限BUFF);//泡点系统开关
        dSoltMaxNum = ServerProperties.getProperty("ZeroMS.dSoltMaxNum", dSoltMaxNum);//物品叠加数量
        开店额外经验 = ServerProperties.getProperty("ZeroMS.开店额外经验", 开店额外经验);//开店额外经验
        结婚额外经验 = ServerProperties.getProperty("ZeroMS.结婚额外经验", 结婚额外经验);//结婚额外经验
        家族徽章费用 = ServerProperties.getProperty("ZeroMS.createEmblemMoney", 家族徽章费用);//家族徽章费用
        ExpBound1 = ServerProperties.getProperty("ZeroMS.ExpBound1", ExpBound1);//分段式经验
        ExpBound2 = ServerProperties.getProperty("ZeroMS.ExpBound2", ExpBound2);//分段式经验
        ExpBound3 = ServerProperties.getProperty("ZeroMS.ExpBound3", ExpBound3);//分段式经验
        ExpBound4 = ServerProperties.getProperty("ZeroMS.ExpBound4", ExpBound4);//分段式经验
        ExpBound5 = ServerProperties.getProperty("ZeroMS.ExpBound5", ExpBound5);//分段式经验
        ExpBound6 = ServerProperties.getProperty("ZeroMS.ExpBound6", ExpBound6);//分段式经验
        测谎仪开关 = ServerProperties.getProperty("ZeroMS.测谎仪开关", 测谎仪开关);
        锻造系统开关 = ServerProperties.getProperty("ZeroMS.锻造系统开关", 锻造系统开关);
        学院系统开关 = ServerProperties.getProperty("ZeroMS.学院系统开关", 学院系统开关);
        检测外挂自动封号 = ServerProperties.getProperty("ZeroMS.检测外挂自动封号", 检测外挂自动封号);
        清理怪物时间 = ServerProperties.getProperty("ZeroMS.清理怪物时间", 清理怪物时间);//清理无人地图怪物(秒)
        等级上限 = ServerProperties.getProperty("ZeroMS.等级上限", 等级上限);//清理无人地图怪物(秒)
        renew_A = ServerProperties.getProperty("server.settings.renew_A", renew_A);
        renew_S = ServerProperties.getProperty("server.settings.renew_S", renew_S);
        renew_3line = ServerProperties.getProperty("server.settings.renew_3line", renew_3line);
        loadmaxlevel();//等级上线
        
    }
    
    public static void loadmaxlevel() {
        try (Connection con = DBConPool.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("select * from qjbl WHERE name = '等级上限'");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                等级上限 = rs.getInt("cs");
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            System.out.println("全局变量信息错误: " + ex);
        }
    }

    static {
        loadSetting();
    }
}

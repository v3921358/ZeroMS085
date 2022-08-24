package server;

import client.MapleCharacter;
import client.SkillFactory;
import client.inventory.OnlyID;
import constants.GameConstants;
import constants.PiPiConfig;
import constants.ServerConfig;
import constants.ServerConstants;
import constants.WorldConstants;
import database.DBConPool;
import database.DatabaseConnection;
import gui.ZeroMS_UI;
import handling.channel.ChannelServer;
import handling.channel.MapleGuildRanking;
import handling.login.LoginServer;
import handling.cashshop.CashShopServer;
import handling.login.LoginInformationProvider;
import handling.world.MapleParty;
import handling.world.World;
import java.sql.SQLException;
import handling.world.family.MapleFamilyBuff;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import javax.swing.JOptionPane;
import merchant.merchant_main;
import server.Timer.*;
import server.events.MapleOxQuizFactory;
import server.life.MapleLifeFactory;
import server.life.PlayerNPC;
import server.maps.MapleMapFactory;
import server.quest.MapleQuest;
import tools.FileoutputUtil;
import tools.StringUtil;
import server.Timer.BuffTimer;
import server.Timer.CheatTimer;
import server.Timer.CloneTimer;
import server.Timer.EtcTimer;
import server.Timer.EventTimer;
import server.Timer.MapTimer;
import server.Timer.MobTimer;
import server.Timer.WorldTimer;
import server.maps.MapleMap;
import static tools.FileoutputUtil.CurrentReadable_Time;
import tools.MaplePacketCreator;
import tools.packet.UIPacket;

public class Start {
    public static boolean  是否控制台启动 = false;
    public static Map<String, Integer> ConfigValuesMap = new HashMap<>();
    
    private static void resetAllLoginState() {
        
        String name = null;
        int id = 0, vip = 0, size = 0;

      //  try (Connection con = DBConPool.getInstance().getDataSource().getConnection(); PreparedStatement ps = con.prepareStatement("UPDATE accounts SET loggedin = 0")) {
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("UPDATE accounts SET loggedin = 0")) {
            ps.executeUpdate();
        } catch (SQLException ex) {
            FileoutputUtil.outError("logs/数据库异常.txt", ex);
            throw new RuntimeException("【错误】 请确认数据库是否正确连接");
        }
    }
    public final static void start(final String args[]) {
        GetConfigValues();
        ZeroMS_UI.进度条1.setValue(1);
        long startQuestTime = System.currentTimeMillis();
        ZeroMS_UI.进度条1.setValue(5);
      /*  System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        System.setProperty("file.encoding", "utf-8");
        System.setProperty("path", "");
        System.out.println("【当前操作系统】: " + System.getProperty("sun.desktop"));*/
        服务器信息();
        System.out.println("【信息】 盛大游戏版本：" + ServerConstants.MAPLE_VERSION);
        System.out.println("【信息】 盛大补丁版本：" + ServerConstants.MAPLE_PATCH);
        System.out.println("【信息】 世界服务器：" + ServerProperties.getProperty("ZeroMS.serverName"));
        resetAllLoginState();
        ZeroMS_UI.进度条1.setValue(7);
        if (WorldConstants.ADMIN_ONLY) {
            System.out.println("【信息】 管理员模式: 开启");
        } else {
            System.out.println("【信息】 管理员模式: 关闭");
        }

        if (ServerConfig.AUTO_REGISTER) {
            System.out.println("【信息】 自动注册模式: 开启");
        } else {
            System.out.println("【信息】 自动注册模式: 开启");
        }

        if (!WorldConstants.GMITEMS) {
            System.out.println("【信息】 允许玩家使用管理员物品: 开启");
        } else {
            System.out.println("【信息】 允许玩家使用管理员物品: 关闭");
        }
        
        if (Boolean.parseBoolean(ServerProperties.getProperty("ZeroMS.dMapAddMob"))) {
            System.out.println("【信息】 自定义部分地图怪物刷新倍数：已开启");
            System.out.println("【信息】 自定义部分地图怪物刷新倍数值：" + Integer.parseInt(ServerProperties.getProperty("ZeroMS.dMapAddMobNum")) + "倍");
        }
        if (Boolean.parseBoolean(ServerProperties.getProperty("ZeroMS.dSoltMax"))) {
            System.out.println("【信息】 自定义物品最大叠加数：已开启");
            System.out.println("【信息】 自定义物品最大叠加数值：" + Integer.parseInt(ServerProperties.getProperty("ZeroMS.dSoltMaxNum")));
        }
        /* 加载设定 */
        ZeroMS_UI.进度条1.setValue(10);
        World.init();
        System.out.println("【信息】 世界服务器线程");
        /* 加载计时器 */
        WorldTimer.getInstance().start();
        EtcTimer.getInstance().start();
        MapTimer.getInstance().start();
        MobTimer.getInstance().start();
        CloneTimer.getInstance().start();
        EventTimer.getInstance().start();
        BuffTimer.getInstance().start();
        PingTimer.getInstance().start();
        ZeroMS_UI.进度条1.setValue(15);
        /* 读取WZ內禁止使用的名称 */
        LoginInformationProvider.getInstance();
        /* 读取钓鱼 */
        FishingRewardFactory.getInstance();
        /* 加载任务*/
        MapleQuest.initQuests();
        MapleLifeFactory.loadQuestCounts();
        MapleOxQuizFactory.getInstance().initialize();
        ZeroMS_UI.进度条1.setValue(20);
        /* 加载道具信息 */
        System.out.println("【信息】 游戏爆率系统");
        MapleItemInformationProvider.getInstance().load();
      //  MapleItemInformationProvider.loadFaceHair(); //加载脸型发型信息
        PredictCardFactory.getInstance().initialize();
        /* 加载随机奖励 */
        RandomRewards.getInstance();
        /* 加载技能信息 */
        SkillFactory.LoadSkillInformaion();
        /* 加载怪物技能 */
        MapleCarnivalFactory.getInstance();
        ZeroMS_UI.进度条1.setValue(30);
        CashItemFactory.getInstance().initialize();
        System.out.println("【信息】 游戏已上架商店物品数量: " + 服务器游戏商品() + " 个");
        System.out.println("【信息】 游戏已上架商城商品数量: " + 服务器商城商品() + " 个");
        System.out.println("【信息】 游戏注册玩家账号数量: " + 服务器账号() + " 个");
        System.out.println("【信息】 游戏建立玩家角色数量: " + 服务器角色() + " 个");
        System.out.println("【信息】 游戏玩家拥有道具数量: " + 服务器道具() + " 个");
        System.out.println("【信息】 游戏玩家拥有技能数量: " + 服务器技能() + " 个");
        System.out.println("【信息】 自动存档线程");
        System.out.println("【信息】 角色福利泡点线程");
        System.out.println("【信息】 启动记录在线时长线程");
        System.out.println("【信息】 启动服务端内存回收线程");
        System.out.println("【信息】 启动服务端地图回收线程");
        System.out.println("【信息】 处理怪物重生、CD、宠物、坐骑");
        System.out.println("【信息】 玩家NPC");
        System.out.println("【信息】 检测游戏复制道具系统");
        /* 加载排行 */
        ZeroMS_UI.进度条1.setValue(50);
        System.out.println("【信息】 游戏排行榜");
        MapleGuildRanking.getInstance().RankingUpdate();
        MapleGuildRanking.getInstance().getGuildRank();
        MapleGuildRanking.getInstance().getJobRank(1);
        MapleGuildRanking.getInstance().getJobRank(2);
        MapleGuildRanking.getInstance().getJobRank(3);
        MapleGuildRanking.getInstance().getJobRank(4);
        MapleGuildRanking.getInstance().getJobRank(5);
        MapleGuildRanking.getInstance().getJobRank(6);
        ZeroMS_UI.进度条1.setValue(70);
        /* 加载家族Buff */
        MapleFamilyBuff.getBuffEntry();
        ZeroMS_UI.进度条1.setValue(80);
        /* 加载登录服务器 */
        LoginServer.setup();
        ZeroMS_UI.进度条1.setValue(85);
        /* 加载频道服务器*/
        ChannelServer.startAllChannels();
         ZeroMS_UI.进度条1.setValue(87);
        /* 加载商城服务器*/
        CashShopServer.setup();
        ZeroMS_UI.进度条1.setValue(90);
        /* 加载自动封号系统 */
        CheatTimer.getInstance().register(AutobanManager.getInstance(), 60000);
        /* 加载怪物生成 */
        SpeedRunner.getInstance().loadSpeedRuns();
        /* 处理怪物重生、CD、宠物、坐骑 */
        World.registerRespawn();
        /* 加载玩家NPC */
        PlayerNPC.loadAll();// touch - so we see database problems early...
        LoginServer.setOn();
        /* 加载自定义NPC、怪物*/
        MapleMapFactory.loadCustomLife();
        ZeroMS_UI.进度条1.setValue(95);
        World.isShutDown = false;
        OnlyID.getInstance();
        merchant_main.getInstance().load_data();
        ZeroMS_UI.进度条1.setValue(100);
        System.out.println("【贴心提示】 所有游戏数据加载完毕");
        System.out.println("【贴心提示】 服务端已启动完毕，耗时 " + ((System.currentTimeMillis() - startQuestTime) / 1000) + " 秒");
        System.out.println("【贴心提示】 运行中请勿直接关闭本控制台，使用下方关闭服务器按钮来关闭服务端，否则回档自负.");
        System.out.println("【免责声明】 本程序源码均来自互联网,仅作交流学习使用,不得用于任何商业用途");
        System.out.println("【免责声明】 请安装后在24小时内删除,如由此引起的相关法律法规责任,与作者无关");
        自动存档(10);
        在线时间(1);
        回收内存(360);
        回收地图(480);
        福利泡点(1);//泡点时间1分钟一次
    }
   public static void 自动存档(int time) {
        Timer.WorldTimer.getInstance().register(new Runnable() {
            @Override
            public void run() {
                int ppl = 0;
                try {
                    for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                        for (MapleCharacter chr : cserv.getPlayerStorage().getAllCharacters()) {
                            if (chr == null) {
                                continue;
                            }
                            ppl++;
                            chr.saveToDB(false, false);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("自动存档出错：" + e);
                }
                System.out.println("【自动存档】 " + CurrentReadable_Time() + " : 已经将 " + ppl + " 个玩家保存到数据中 √");
            }
        }, 60 * 1000 * time);
    }
   
    /**
     * * <30分钟泡点一次>
     */
    public static int 福利泡点 = 0;
    public static void 福利泡点(final int time) {
       
        Timer.WorldTimer.getInstance().register(new Runnable() {
            @Override
            public void run() {
                
                if (福利泡点 > 0) {
                    try {
                        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                            for (MapleCharacter chr : cserv.getPlayerStorage().getAllCharacters()) {
                                if (chr == null) {
                                    continue;
                                }
                                    if (chr.getMapId() == 741000209) {
                                        int 点券 = 0;
                                        int 经验 = 0;
                                        int 金币 = 0;
                                        int 抵用 = 0;
                                        int 豆豆 = 0;
                                        int 泡点豆豆开关 = Start.ConfigValuesMap.get("泡点豆豆开关");
                                        if (泡点豆豆开关 <= 0) {
                                        int 泡点豆豆 = Start.ConfigValuesMap.get("泡点豆豆");
                                            豆豆 += 泡点豆豆;
                                            chr.gainBeans(豆豆);//给固定豆豆
                                        }
                                        
                                        int 泡点金币开关 = Start.ConfigValuesMap.get("泡点金币开关");
                                        if (泡点金币开关 <= 0) {
                                            int 泡点金币 = Start.ConfigValuesMap.get("泡点金币");
                                            金币 += 泡点金币;
                                            chr.gainMeso(泡点金币, true);//给固定金币
                                        }
                                        int 泡点点券开关 = Start.ConfigValuesMap.get("泡点点券开关");
                                        if (泡点点券开关 <= 0) {
                                            int 泡点点券 = Start.ConfigValuesMap.get("泡点点券");
                                            chr.modifyCSPoints(1, 泡点点券, true);
                                            点券 += 泡点点券;
                                        }
                                        int 泡点抵用开关 = Start.ConfigValuesMap.get("泡点抵用开关");
                                        if (泡点抵用开关 <= 0) {
                                            int 泡点抵用 = Start.ConfigValuesMap.get("泡点抵用");
                                            chr.modifyCSPoints(2, 泡点抵用, true);
                                            抵用 += 泡点抵用;
                                        }
                                        int 泡点经验开关 = Start.ConfigValuesMap.get("泡点经验开关");
                                        if (泡点经验开关 <= 0) {
                                            int 泡点经验 = Start.ConfigValuesMap.get("泡点经验");
                                            经验 += 泡点经验;
                                           
                                           //chr.gainExp((chr.getLevel() * 经验), false, false, false);//给经验乘当前等级
                                         chr.gainExp(经验, false, false, false);//给固定经验
                                        }
                                       
                                        //chr.getClient().getSession().write(MaplePacketCreator.serverNotice(5, "[在线泡点] ：获得 ["+点券+"] 点卷 / ["+抵用+"] 抵用卷 / ["+豆豆+"] 豆豆 / [" + 经验 + "] 经验  [" +金币 + "] 金币"));                                        
                                        chr.getClient().sendPacket(MaplePacketCreator.enableActions());
                                    }
                                }
                            }
                      //  }         
                     //System.err.println("【在线泡点】 " + CurrentReadable_Time() + " : 系统正在发放在线玩家泡点 √");//服务端在线泡点提示
                    } catch (Exception e) {

                      new Thread() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000 * 10);
                                    福利泡点(1);
                                } catch (InterruptedException e) {
                                }
                            }
                        }.start();
                    }
                } else {
                    福利泡点++;
                }
            }
        }, 60 * 1000 * time);
    }

   
      public static void 在线时间(int time) {
        Timer.WorldTimer.getInstance().register(new Runnable() {

            @Override
            public void run() {
                Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);
                if (hour == 0 && minute == 0) {
                    try {
                        Connection con = DatabaseConnection.getConnection();
                        try (PreparedStatement ps = con.prepareStatement("UPDATE accounts_info SET gamePoints = ?, updateTime = CURRENT_TIMESTAMP()")) {
                            ps.setInt(1, 0);
                            ps.executeUpdate();
                            ps.close();
                        }
                    } catch (SQLException Ex) {
                        System.err.println("更新角色帐号的在线时间出现错误 - 数据库更新失败." + Ex);
                    }
                }
                try {
                    for (ChannelServer chan : ChannelServer.getAllInstances()) {
                        for (MapleCharacter chr : chan.getPlayerStorage().getAllCharacters()) {
                            if (chr == null) {
                                continue;
                            }
                            if (hour == 0 && minute == 0) {
                                chr.set在线时间(0);
                                continue;
                            }
                            chr.gainGamePoints(1);
                            if (chr.get在线时间() < 5) {//chr.getGamePoints() < 5
                                chr.resetGamePointsPS();
                                chr.resetGamePointsPD();
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println("在线时间出错:" + e);
                }
            }
        }, 60000 * time);
     }
      
          public static void 服务器信息() {
        try {
            InetAddress addr = InetAddress.getLocalHost();
            String ip = addr.getHostAddress().toString(); //獲取本機ip
            String hostName = addr.getHostName().toString(); //獲取本機計算機名稱
            //System.out.println("本機IP："+ip+"\n本機名稱:"+hostName);
            System.out.println("【信息】 检测服务端运行环境");
            System.out.println("【信息】 服务器名: " + hostName);
            Properties 設定檔 = System.getProperties();
            System.out.println("【信息】 操作系统：" + 設定檔.getProperty("os.name"));
            System.out.println("【信息】 系统框架：" + 設定檔.getProperty("os.arch"));
            System.out.println("【信息】 系统版本：" + 設定檔.getProperty("os.version"));
            System.out.println("【信息】 服务端目录：" + 設定檔.getProperty("user.dir"));
            System.out.println("【信息】 服务端环境检测完成");
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }
      
        public static int 服务器角色() {
        int p = 0;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT id as DATA FROM characters WHERE id >=0");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    p += 1;
                }
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("服务器角色？");
        }
        return p;
    }

    public static int 服务器账号() {
        int p = 0;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT id as DATA FROM accounts WHERE id >=0");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    p += 1;
                }
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("服务器账号？");
        }
        return p;
    }

    public static int 服务器技能() {
        int p = 0;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT id as DATA FROM skills ");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    p += 1;
                }
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("服务器技能？");
        }
        return p;
    }

    public static int 服务器道具() {
        int p = 0;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT inventoryitemid as DATA FROM inventoryitems WHERE inventoryitemid >=0");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    p += 1;
                }
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("服务器道具？");
        }
        return p;
    }

    public static int 服务器商城商品() {
        int p = 0;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT serial as DATA FROM cashshop_modified_items WHERE serial >=0");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    p += 1;
                }
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("服务器商城商品？");
        }
        return p;
    }  
   
        public static int 服务器游戏商品() {
        int p = 0;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT shopitemid as DATA FROM shopitems WHERE shopitemid >=0");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    p += 1;
                }
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("服务器道具游戏商品？");
        }
        return p;
    }
        
           /**
     * * <其他>
     */
    public static void GetConfigValues() {
        //动态数据库连接
        Connection con = DatabaseConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement("SELECT name, val FROM ConfigValues")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("name");
                    int val = rs.getInt("val");
                    ConfigValuesMap.put(name, val);
                }
            }
            ps.close();
        } catch (SQLException ex) {
            System.err.println("读取动态数据库出错：" + ex.getMessage());
        }
    }
    
    /**
     * * <30分钟强制回收一次内存>
     */
    private static int 回收内存 = 0;

    public static void 回收内存(final int time) {
        Timer.WorldTimer.getInstance().register(new Runnable() {
            @Override
            public void run() {
                if (回收内存 > 0) {
                    System.gc();
                    System.err.println("【内存回收】 " + CurrentReadable_Time() + " : 回收服务端内存 √");
                } else {
                    回收内存++;
                }
            }
        }, 60 * 1000 * time);
       }
    /**
     * * <30分钟强制回收一次地图>
     */
    
        public static void 回收地图(int time) {
        Timer.WorldTimer.getInstance().register(new Runnable() {

            public void run() {
                for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                    for (MapleCharacter chr : cserv.getPlayerStorage().getAllCharacters()) {
                        for (int i = 0; i < 6; i++) {
                            int mapidA = 100000000 + (i + 1000000 - 2000000);
                            MapleCharacter player = chr;
                            if (i == 6) {
                                mapidA = 741000209;
                            }
                            int mapid = mapidA;
                            MapleMap map = player.getClient().getChannelServer().getMapFactory().getMap(mapid);
                            if (player.getClient().getChannelServer().getMapFactory().destroyMap(mapid)) {
                                MapleMap newMap = player.getClient().getChannelServer().getMapFactory().getMap(mapid);
                                MaplePortal newPor = newMap.getPortal(0);
                                LinkedHashSet<MapleCharacter> mcs = new LinkedHashSet<MapleCharacter>(map.getCharacters()); // do NOT remove, fixing ConcurrentModificationEx.
                                outerLoop:
                                for (MapleCharacter m : mcs) {
                                    for (int x = 0; x < 5; x++) {
                                        try {
                                            m.changeMap(newMap, newPor);
                                            continue outerLoop;
                                        } catch (Throwable t) {
                                        System.err.println("【地图回收】 " + CurrentReadable_Time() + " : 系统正在回收地图 √");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }, 60000 * time);
    }
    }


package constants;

import server.ServerProperties;

public class WorldConstants {

    public static Option WORLD = WorldOption.菇菇寶貝;
    public static boolean ADMIN_ONLY = false;//管理员模式
    public static boolean JZSD = false;//禁止玩家使用商店
    public static boolean WUYANCHI = false;//无延迟检测
    public static boolean LieDetector = false;//定时测谎
    public static boolean DropItem = true;//丢弃物品信息  
    public static int USER_LIMIT = 10000;
    public static int MAX_CHAR_VIEW = 20;
    public static boolean GMITEMS = false;//允许玩家使用GM物品
    public static boolean CS_ENABLE = true;
    public static int EXP_RATE = 1;
    public static int MESO_RATE = 1;
    public static int maxCharacters = 1;
    public static String eventSM = "HontalePQ,HorntailBattle,cpq2,elevator,Christmas,FireDemon,Amoria,cpq,AutomatedEvent,Flight,English,English0,English1,English2,WuGongPQ,ElementThanatos,4jberserk,4jrush,Trains,Geenie,AirPlane,Boats,OrbisPQ,HenesysPQ,Romeo,Juliet,Pirate,Ellin,DollHouse,BossBalrog_NORMAL,Nibergen,PinkBeanBattle,ZakumBattle,NamelessMagicMonster,Dunas,Dunas2,ZakumPQ,LudiPQ,KerningPQ,ProtectTylus,Vergamot,CoreBlaze,GuildQuest,Aufhaven,Subway,KyrinTrainingGroundC,KyrinTrainingGroundV,ProtectPig,ScarTarBattle,s4resurrection,s4resurrection2,s4nest,s4aWorld,DLPracticeField,BossQuestEASY,BossQuestHARD,BossQuestHELL,BossQuestMed,shaoling,Ravana,MV,BossBalrog,Ghostbaby,MagicQuest1,Francis,Relic,aran3rd,aran3rd2,aran4th,ProtectRichard,SnackBar,Mechanical,chengqiang1,chengqiang2,chengqiang3,chengqiang4,chengqiang5,VonLeonBattle,ChaosZakum,ChaosHorntail";
    public static int DROP_RATE = 1;
    public static byte FLAG = 3;
    public static int CHANNEL_COUNT = 2;
    public static int Pport = 7575;
    public static int Sport = 8600;
    public static int merchantTime = 24;
    public static int statLimit = 999;
    public static int state = 999;
    public static int state1 = 10;
    public static int createGuildCost = 500000;
    public static String dbip = "127.0.0.1";
    public static String dbuser = "root";
    public static String dbpassword = "root";
    public static int dbport = 3306;
    public static int timeout = 300000;
    public static String dbname = "ZeroMS";
    
    public static String WORLD_TIP = "請享受楓之谷的冒險之旅吧!";
    public static String SCROLL_MESSAGE = "";
    public static boolean AVAILABLE = true;
    public static final int gmserver = -1; // -1 = no gm server
    public static final byte recommended = (byte) -1; //-1 = no recommended
    public static final String recommendedmsg = recommended < 0 ? "" : "        Join " + getById(recommended).name() + ",       the newest world! (If youhave friends who play, consider joining their worldinstead. Characters can`t move between worlds.)";

    public static interface Option {

        public int getWorld();

        public String name();
    }

    /**
     *
     * @Warning: World will be duplicated if it's the same as the gm server
     */
    public static enum WorldOption implements Option {

        泰勒熊(16),
        神獸(15),
        皮卡啾(14),
        鯨魚號(13),
        電擊象(12),
        海努斯(11),
        巴洛古(10),
        蝴蝶精(9),
        火獨眼獸(8),
        木妖(7),
        三眼章魚(6),
        綠水靈(5),
        藍寶(4),
        緞帶肥肥(3),
        星光精靈(2),
        菇菇寶貝(1),
        雪吉拉(120);
        private final int world;

        WorldOption(int world) {
            this.world = world;
        }

        @Override
        public int getWorld() {
            return world;
        }
    }

    public static enum TespiaWorldOption implements Option {

        測試機("t0");
        private final int world;
        private final String worldName;

        TespiaWorldOption(String world) {
            this.world = Integer.parseInt(world.replaceAll("t", ""));
            this.worldName = world;
        }

        @Override
        public int getWorld() {
            return world;
        }
    }

    public static Option[] values() {
        return ServerConstants.TESPIA ? TespiaWorldOption.values() : WorldOption.values();
    }

    public static Option valueOf(String name) {
        return ServerConstants.TESPIA ? TespiaWorldOption.valueOf(name) : WorldOption.valueOf(name);
    }

    public static Option getById(int g) {
        for (Option e : values()) {
            if (e.getWorld() == g) {
                return e;
            }
        }
        return null;
    }

    public static boolean isExists(int id) {
        return getById(id) != null;
    }

    public static String getNameById(int serverid) {
        if (getById(serverid) == null) {
            System.err.println("World doesn't exists exception. ID: " + serverid);
            return "";
        }
        return getById(serverid).name();
    }

    public static void loadSetting() {
        ADMIN_ONLY = ServerProperties.getProperty("ZeroMS.admin", ADMIN_ONLY);
        FLAG = ServerProperties.getProperty("ZeroMS.flag", FLAG);
        Pport = ServerProperties.getProperty("ZeroMS.channel.port", Pport);
        Sport = ServerProperties.getProperty("ZeroMS.cashshop.port", Sport);
        merchantTime = ServerProperties.getProperty("ZeroMS.merchantTime", merchantTime);
        statLimit = ServerProperties.getProperty("ZeroMS.statLimit", statLimit);
        state = ServerProperties.getProperty("ZeroMS.state", state);
        state1 = ServerProperties.getProperty("ZeroMS.高级魔方几率", state1);
        dbip = ServerProperties.getProperty("ZeroMS.db.ip", dbip);
        dbuser = ServerProperties.getProperty("ZeroMS.db.user", dbuser);
        dbpassword = ServerProperties.getProperty("ZeroMS.db.password", dbpassword);
        dbport = ServerProperties.getProperty("ZeroMS.db.port", dbport);
        timeout = ServerProperties.getProperty("timeout", timeout);
        dbname = ServerProperties.getProperty("ZeroMS.db.name", dbname);

        
        createGuildCost = ServerProperties.getProperty("ZeroMS.createGuildCost", createGuildCost);
        EXP_RATE = ServerProperties.getProperty("ZeroMS.expRate", EXP_RATE);
        MESO_RATE = ServerProperties.getProperty("ZeroMS.mesoRate", MESO_RATE);
        DROP_RATE = ServerProperties.getProperty("ZeroMS.dropRate", DROP_RATE);
        maxCharacters = ServerProperties.getProperty("ZeroMS.maxCharacters", maxCharacters);
        eventSM = ServerProperties.getProperty("ZeroMS.ZeroMS.events", eventSM);
        WORLD_TIP = ServerProperties.getProperty("ZeroMS.eventMessage", WORLD_TIP);
        SCROLL_MESSAGE = ServerProperties.getProperty("ZeroMS.serverMessage", SCROLL_MESSAGE);
        CHANNEL_COUNT = ServerProperties.getProperty("ZeroMS.channel.count", CHANNEL_COUNT);
        USER_LIMIT = ServerProperties.getProperty("ZeroMS.userlimit", USER_LIMIT);
        MAX_CHAR_VIEW = ServerProperties.getProperty("ZeroMS.maxCharView", MAX_CHAR_VIEW);
        GMITEMS = ServerProperties.getProperty("ZeroMS.gmitems", GMITEMS);
        CS_ENABLE = ServerProperties.getProperty("ZeroMS.cashshop.enable", CS_ENABLE);
        
        JZSD = ServerProperties.getProperty("ZeroMS.jzsd", JZSD);//禁止玩家使用商店
        WUYANCHI = ServerProperties.getProperty("ZeroMS.wuyanchi", WUYANCHI);//无延迟检测
        LieDetector = ServerProperties.getProperty("ZeroMS.LieDetector", LieDetector);//定时测谎
        DropItem = ServerProperties.getProperty("ZeroMS.DropItem", DropItem);//丢弃物品信息  

    }

    static {
        loadSetting();
    }
}

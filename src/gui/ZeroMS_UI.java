package gui;

import com.alee.extended.label.WebHotkeyLabel;
import com.alee.extended.painter.TitledBorderPainter;
import com.alee.extended.panel.GroupPanel;
import com.alee.extended.progress.WebProgressOverlay;
import com.alee.extended.statusbar.WebMemoryBar;
import com.alee.extended.statusbar.WebStatusBar;
import com.alee.global.StyleConstants;
import com.alee.laf.WebLookAndFeel;
import com.alee.laf.button.WebButton;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.label.WebLabel;
import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;
import com.alee.laf.menu.WebMenuItem;
import com.alee.laf.menu.WebPopupMenu;
import com.alee.laf.optionpane.WebOptionPane;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.progressbar.WebProgressBar;
import com.alee.laf.rootpane.WebFrame;
import com.alee.laf.scroll.WebScrollBar;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.separator.WebSeparator;
import com.alee.laf.text.WebTextField;
import com.alee.laf.text.WebTextPane;
import com.alee.utils.ThreadUtils;

import client.LoginCrypto;
import constants.GameConstants;
import constants.PiPiConfig;
import handling.channel.ChannelServer;
import handling.login.handler.AutoRegister;
import client.MapleCharacter;
import client.inventory.Equip;
import client.inventory.ItemFlag;
import client.inventory.MapleInventoryType;
import constants.JobConstants;
import gui.tools.*;
import server.CashItemFactory;
import server.quest.MapleQuest;
import database.DatabaseConnection;
import server.*;
import server.life.MapleMonsterInformationProvider;
import constants.ServerConfig;
import constants.ServerConstants;
import constants.WorldConstants;
import handling.login.LoginServer;
import handling.world.World;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import scripting.ReactorScriptManager;
import scripting.PortalScriptManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.rmi.NotBoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.table.DefaultTableModel;
import tools.MaplePacketCreator;
import scripting.NPCConversationManager;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.TableCellRenderer;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import static server.MapleCarnivalChallenge.getJobNameById;
import static server.Start.GetConfigValues;
import tools.wztosql.*;
import server.Timer.GuiTimer;
import tools.Pair;

/**
 * @author 大白
 */
public class ZeroMS_UI extends javax.swing.JFrame {

    /**
     *
     */
    private static final long serialVersionUID = 6858557462052039707L;
    public static ZeroMS_UI instance = null;
    private final PrintStream printStream;
    private ScheduledFuture<?> shutdownServer, startRunTime;
    public static Map<String, Integer> ConfigValuesMap = new HashMap<>();
    private Map<String, Pair<String, String>> worldProperties = new HashMap<>();
    private Map<String, Boolean> eventsStatus = new HashMap<>();
    private Map<Windows, javax.swing.JFrame> windows = new HashMap<>();
    private final ReentrantReadWriteLock mutex = new ReentrantReadWriteLock();
    private ImageIcon icon = new ImageIcon(this.getClass().getClassLoader().getResource("Image/Icon.png"));
    private final Lock writeLock = mutex.writeLock();
    private Vector<Vector<String>> playerTableRom = new Vector<>();
    private int buffline = 0;
    private ArrayList<Tools> tools = new ArrayList();
    private boolean searchServer = false;
    private static String 服务器状态 = "开启";
    boolean 开启服务端 = false;
    boolean inidrop2 = false;
    
    private boolean dropaDataInitFinished = false;
    private boolean dropaDataGlobalInitFinished = false;
    private boolean shopsInitFinished = false;
    private static boolean MYSQL = false;
    int si = 0;
    
    public static final ZeroMS_UI getInstance() {
        return instance;
    }
    
    public ZeroMS_UI() {
        setIconImage(icon.getImage());
        setTitle("[ZeroMS 服务端]后台管理控制台工具 当前版本:Ver.085.03  区名:" + ServerConfig.SERVERNAME);
        GetConfigValues();
        initComponents();
        resetSetting(false);
        刷新离线挂机开关();
        刷新吸怪检测开关();
        刷新加速检测开关();
        刷新全屏检测开关();
        刷新捡物检测开关();
        刷新段数检测开关();
        刷新群攻检测开关();
        刷新挂机检测开关();
        刷新机器多开();
        刷新IP多开();
        刷新拍卖行开关();
        刷新脚本显码开关();
        刷新游戏仓库开关();
        刷新过图存档时间();
        刷新地图名称开关();
        刷新登陆帮助();
        刷新怪物状态开关();
        刷新越级打怪开关();
        刷新回收地图开关();
        刷新玩家聊天开关();
        刷新滚动公告开关();
        刷新指令通知开关();
        刷新管理隐身开关();
        刷新管理加速开关();
        刷新游戏指令开关();
        刷新游戏喇叭开关();
        刷新丢出金币开关();
        刷新丢出物品开关();
        刷新雇佣商人开关();
        刷新上线提醒开关();
        刷新升级快讯();
        刷新玩家交易开关();
        刷新欢迎弹窗开关();
        刷新禁止登陆开关();
        刷新游戏找人开关();
        刷新蓝蜗牛开关();
        刷新蘑菇仔开关();
        刷新绿水灵开关();
        刷新漂漂猪开关();
        刷新小青蛇开关();
        刷新红螃蟹开关();
        刷新大海龟开关();
        刷新章鱼怪开关();
        刷新顽皮猴开关();
        刷新星精灵开关();
        刷新胖企鹅开关();
        刷新白雪人开关();
        刷新石头人开关();
        刷新紫色猫开关();
        刷新大灰狼开关();
        刷新小白兔开关();
        刷新喷火龙开关();
        刷新火野猪开关();
        刷新青鳄鱼开关();
        刷新花蘑菇开关();
        刷新泡点金币开关();
        刷新泡点点券开关();
        刷新泡点经验开关();
        刷新泡点抵用开关();
        刷新泡点豆豆开关();
        刷新泡点设置();
        刷新离线泡点金币开关();
        刷新离线泡点点券开关();
        刷新离线泡点经验开关();
        刷新离线泡点抵用开关();
        刷新离线泡点豆豆开关();
        刷新离线泡点设置();
        printStream = new PrintStream(new newOutputStream());
        System.setOut(printStream);
        System.setErr(printStream);
        System.out.println("【欢迎使用 ZeroMS服务端 Ver085");
        刷新账号信息();
        刷新角色信息();
        GuiTimer.getInstance().start();//计时器
        InputStream is = null;
        Properties p = new Properties();
        BufferedReader bf = null;
        进度条.setMinimum(0);
        进度条.setMaximum(100);
        // 设置当前进度值
        进度条.setValue(0);
        // 绘制百分比文本（进度条中间显示的百分数）
        进度条.setStringPainted(true);
        进度条1.setMinimum(0);
        进度条1.setMaximum(100);
        // 设置当前进度值
        进度条1.setValue(0);
        // 绘制百分比文本（进度条中间显示的百分数）
        进度条1.setStringPainted(true);

    }
    
        private javax.swing.ComboBoxModel getWorldModel() {
        Vector worldModel = new Vector();
        for (WorldConstants.Option e : WorldConstants.values()) {
            worldModel.add(0, e.name());
        }
        return new DefaultComboBoxModel(worldModel);
    }

        private void resetWorldPanel() {
        //给服务器增加一个默认状态
        InputStream is = null;
//        //开始读取ini内的参数信息
//        String exp = null;
//        String drop = null;
//        String cash = null;
        Properties p = new Properties();
//        try {
//           
//            p.load(new FileInputStream("配置.ini"));
//        } catch (IOException ex) {
//            Logger.getLogger(ZeroMS控制台.class.getName()).log(Level.SEVERE, null, ex);
//        }

        BufferedReader bf = null;

        try {
            is = new FileInputStream("配置.ini");
            //这个要看你dd.properties文件的编码格式，如果编码格式是gbk的要用gbk的InputStreamReader读取，如果utf8的就不用特殊设置了，如果你手工输入的dd的信息应该是gbk的编码
            bf = new BufferedReader(new InputStreamReader(is, "utf-8"));
            p = new Properties();
            p.load(bf);

        } catch (FileNotFoundException e) {
            System.out.println("没有找到文件");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("读取配置文件失败");
            e.printStackTrace();
        }

        经验倍率.setText(String.valueOf(p.getProperty("ZeroMS.expRate")));//经验
        金币爆率.setText(String.valueOf(p.getProperty("ZeroMS.mesoRate")));
        物品爆率.setText(String.valueOf(p.getProperty("ZeroMS.dropRate")));
        加载事件.setText(String.valueOf(p.getProperty("ZeroMS.events")));
        顶部公告.setText(String.valueOf(p.getProperty("ZeroMS.serverMessage")));
        事件名字.setText(String.valueOf(p.getProperty("ZeroMS.eventMessage")));
        最大登录角色数限制.setText(String.valueOf(p.getProperty("ZeroMS.userlimit")));
        频道最大人数.setText(String.valueOf(p.getProperty("ZeroMS.maxCharView")));
        最大创建角色数.setText(String.valueOf(p.getProperty("ZeroMS.maxCharacters")));
        频道总数.setText(String.valueOf(p.getProperty("ZeroMS.channel.count")));
        频道状态.setText(String.valueOf(p.getProperty("ZeroMS.flag")));
        游戏IP.setText(String.valueOf(p.getProperty("ZeroMS.ip.gateway")));
        登录端口.setText(String.valueOf(p.getProperty("ZeroMS.login.port")));
        频道端口.setText(String.valueOf(p.getProperty("ZeroMS.channel.port")));
        商城端口.setText(String.valueOf(p.getProperty("ZeroMS.cashshop.port")));
        雇佣时间.setText(String.valueOf(p.getProperty("ZeroMS.merchantTime", "24")));
        最大能力值.setText(String.valueOf(p.getProperty("ZeroMS.statLimit", "999")));
        潜能几率.setText(String.valueOf(p.getProperty("ZeroMS.state", "4")));
        高级魔方几率.setText(String.valueOf(p.getProperty("ZeroMS.高级魔方几率", "10")));
        家族金币.setText(String.valueOf(p.getProperty("ZeroMS.createGuildCost", "500000")));
        家族徽章费用.setText(String.valueOf(p.getProperty("ZeroMS.createEmblemMoney", "100000")));
        人物灌水比例.setText(String.valueOf(p.getProperty("ZeroMS.rsgs")));
        管理员模式.setSelected(WorldConstants.ADMIN_ONLY);
        允许使用商城.setSelected(WorldConstants.CS_ENABLE);
        允许玩家穿戴GM道具.setSelected(WorldConstants.GMITEMS);
        //数据库
        数据库IP.setText(String.valueOf(p.getProperty("ZeroMS.db.ip")));
        数据库用户名.setText(String.valueOf(p.getProperty("ZeroMS.db.user")));
        数据库密码.setText(String.valueOf(p.getProperty("ZeroMS.db.password")));
        数据库端口.setText(String.valueOf(p.getProperty("ZeroMS.db.port")));
        数据库名.setText(String.valueOf(p.getProperty("ZeroMS.db.name")));
        连接数.setText(String.valueOf(p.getProperty("timeout")));
       //服务端
        游戏名字.setText(String.valueOf(p.getProperty("ZeroMS.serverName")));
        测试机.setSelected(ServerConstants.TESPIA);
        wz路径.setText(String.valueOf(p.getProperty("ZeroMS.wzpath")));
        自动注册.setSelected(ServerConfig.AUTO_REGISTER);
        日志模式.setSelected(ServerConfig.LOG_PACKETS);
        debug模式.setSelected(ServerConfig.DEBUG_MODE);
        检测外挂自动封号.setSelected(ServerConfig.检测外挂自动封号);
        精灵商人出售记录.setSelected(ServerConfig.LOG_MRECHANT);
        广播记录.setSelected(ServerConfig.LOG_MEGA);
        商城购买记录.setSelected(ServerConfig.LOG_CSBUY);
        伤害输出记录.setSelected(ServerConfig.LOG_DAMAGE);
        聊天记录.setSelected(ServerConfig.LOG_CHAT);
        角色封包输出记录.setSelected(ServerConfig.CHRLOG_PACKETS);
        匿名广播.setSelected(ServerConfig.NMGB);
        频道掉线测试.setSelected(ServerConfig.PDCS);
        
        禁止玩家使用商店.setSelected(WorldConstants.JZSD);
        无延迟检测.setSelected(WorldConstants.WUYANCHI);
        定时测谎.setSelected(WorldConstants.LieDetector);
        丢物品信息.setSelected(WorldConstants.DropItem);
        卷轴防爆1.setSelected(ServerConfig.shieldWardAll);
        刷怪开关1.setSelected(ServerConfig.dMapAddMob);
        地图列表ID.setText(String.valueOf(p.getProperty("ZeroMS.dMapAddMobMapList")));
        自定义地图刷怪倍数1.setText(String.valueOf(p.getProperty("ZeroMS.dMapAddMobNum")));
        物品叠加开关.setSelected(ServerConfig.dSoltMax);
        物品叠加数量.setText(String.valueOf(p.getProperty("ZeroMS.dSoltMaxNum")));
        开店额外经验.setText(String.valueOf(p.getProperty("ZeroMS.开店额外经验")));//经验
        结婚额外经验.setText(String.valueOf(p.getProperty("ZeroMS.结婚额外经验")));//经验
        ExpBound1.setText(String.valueOf(p.getProperty("ZeroMS.ExpBound1")));//经验
        ExpBound2.setText(String.valueOf(p.getProperty("ZeroMS.ExpBound2")));//经验
        ExpBound3.setText(String.valueOf(p.getProperty("ZeroMS.ExpBound3")));//经验
        ExpBound4.setText(String.valueOf(p.getProperty("ZeroMS.ExpBound4")));//经验
        ExpBound5.setText(String.valueOf(p.getProperty("ZeroMS.ExpBound5")));//经验
        ExpBound6.setText(String.valueOf(p.getProperty("ZeroMS.ExpBound6")));//经验
        清理怪物时间.setText(String.valueOf(p.getProperty("ZeroMS.清理怪物时间")));//经验
        无限BUFF.setSelected(ServerConfig.无限BUFF);
        测谎仪.setSelected(ServerConfig.测谎仪开关);
        锻造系统.setSelected(ServerConfig.锻造系统开关);
        学院系统.setSelected(ServerConfig.学院系统开关);
    }
    
    
    
    
        public void 按键开关(String a, int b) {
        int 检测开关 = ZeroMS_UI.ConfigValuesMap.get(a);
        PreparedStatement ps = null;
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        if (检测开关 > 0) {
            try {
                ps = DatabaseConnection.getConnection().prepareStatement("UPDATE configvalues SET Val = ? WHERE id = ?");
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM configvalues WHERE id = ?");
                ps1.setInt(1, b);
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlString2 = null;
                    String sqlString3 = null;
                    String sqlString4 = null;
                    sqlString2 = "update configvalues set Val= '0' where id= '" + b + "';";
                    PreparedStatement dropperid = DatabaseConnection.getConnection().prepareStatement(sqlString2);
                    dropperid.executeUpdate(sqlString2);
                }
            } catch (SQLException ex) {
                Logger.getLogger(ZeroMS_UI.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                ps = DatabaseConnection.getConnection().prepareStatement("UPDATE configvalues SET Val = ? WHERE id = ?");
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM configvalues WHERE id = ?");
                ps1.setInt(1, b);
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlString2 = null;
                    String sqlString3 = null;
                    String sqlString4 = null;
                    sqlString2 = "update configvalues set Val= '1' where id='" + b + "';";
                    PreparedStatement dropperid = DatabaseConnection.getConnection().prepareStatement(sqlString2);
                    dropperid.executeUpdate(sqlString2);
                }
            } catch (SQLException ex) {
                Logger.getLogger(ZeroMS_UI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        ZeroMS_UI.GetConfigValues();
    }
        
        
         public void 刷新泡点设置() {
        for (int i = ((DefaultTableModel) (this.在线泡点设置.getModel())).getRowCount() - 1; i >= 0; i--) {
            ((DefaultTableModel) (this.在线泡点设置.getModel())).removeRow(i);
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = null;
            ResultSet rs = null;
            ps = con.prepareStatement("SELECT * FROM configvalues WHERE id = 700 || id = 702 || id = 704 || id = 706 || id = 708 || id = 712");
            rs = ps.executeQuery();
            while (rs.next()) {
                ((DefaultTableModel) 在线泡点设置.getModel()).insertRow(在线泡点设置.getRowCount(), new Object[]{rs.getString("id"), rs.getString("name"), rs.getString("Val")});
            }
        } catch (SQLException ex) {
            Logger.getLogger(ZeroMS_UI.class.getName()).log(Level.SEVERE, null, ex);
        }
        在线泡点设置.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int i = 在线泡点设置.getSelectedRow();
                String a = 在线泡点设置.getValueAt(i, 0).toString();
                String a1 = 在线泡点设置.getValueAt(i, 1).toString();
                String a2 = 在线泡点设置.getValueAt(i, 2).toString();
                泡点序号.setText(a);
                泡点类型.setText(a1);
                泡点值.setText(a2);
            }
        });
    }

    
      private void 刷新泡点金币开关() {
        String 泡点金币开关显示 = "";
        int 泡点金币开关 = ZeroMS_UI.ConfigValuesMap.get("泡点金币开关");
        if (泡点金币开关 <= 0) {
            泡点金币开关显示 = "泡点金币:开启";
        } else {
            泡点金币开关显示 = "泡点金币:关闭";
        }
        泡点金币开关(泡点金币开关显示);
    }

    private void 刷新泡点点券开关() {
        String 泡点点券开关显示 = "";
        int 泡点点券开关 = ZeroMS_UI.ConfigValuesMap.get("泡点点券开关");
        if (泡点点券开关 <= 0) {
            泡点点券开关显示 = "泡点点券:开启";
        } else {
            泡点点券开关显示 = "泡点点券:关闭";
        }
        泡点点券开关(泡点点券开关显示);
    }

    private void 刷新泡点经验开关() {
        String 泡点经验开关显示 = "";
        int 泡点经验开关 = ZeroMS_UI.ConfigValuesMap.get("泡点经验开关");
        if (泡点经验开关 <= 0) {
            泡点经验开关显示 = "泡点经验:开启";
        } else {
            泡点经验开关显示 = "泡点经验:关闭";
        }
        泡点经验开关(泡点经验开关显示);
    }

    private void 刷新泡点抵用开关() {
        String 泡点抵用开关显示 = "";
        int 泡点抵用开关 = ZeroMS_UI.ConfigValuesMap.get("泡点抵用开关");
        if (泡点抵用开关 <= 0) {
            泡点抵用开关显示 = "泡点抵用:开启";
        } else {
            泡点抵用开关显示 = "泡点抵用:关闭";
        }
        泡点抵用开关(泡点抵用开关显示);
    }

    private void 刷新泡点豆豆开关() {
        String 泡点豆豆开关显示 = "";
        int 泡点豆豆开关 = ZeroMS_UI.ConfigValuesMap.get("泡点豆豆开关");
        if (泡点豆豆开关 <= 0) {
            泡点豆豆开关显示 = "泡点豆豆:开启";
        } else {
            泡点豆豆开关显示 = "泡点豆豆:关闭";
        }
        泡点豆豆开关(泡点豆豆开关显示);
    }
     
    private void 泡点点券开关(String str) {
        泡点点券开关.setText(str);
    }

    private void 泡点经验开关(String str) {
        泡点经验开关.setText(str);
    }

    private void 泡点抵用开关(String str) {
        泡点抵用开关.setText(str);
    }
   
    private void 泡点金币开关(String str) {
        泡点金币开关.setText(str);
    }
    
    private void 泡点豆豆开关(String str) {
        泡点豆豆开关.setText(str);
    }
    
             public void 刷新离线泡点设置() {
        for (int i = ((DefaultTableModel) (this.离线泡点设置.getModel())).getRowCount() - 1; i >= 0; i--) {
            ((DefaultTableModel) (this.离线泡点设置.getModel())).removeRow(i);
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = null;
            ResultSet rs = null;
            ps = con.prepareStatement("SELECT * FROM configvalues WHERE id = 714 || id = 716 || id = 718 || id = 720 || id = 722");
            rs = ps.executeQuery();
            while (rs.next()) {
                ((DefaultTableModel) 离线泡点设置.getModel()).insertRow(离线泡点设置.getRowCount(), new Object[]{rs.getString("id"), rs.getString("name"), rs.getString("Val")});
            }
        } catch (SQLException ex) {
            Logger.getLogger(ZeroMS_UI.class.getName()).log(Level.SEVERE, null, ex);
        }
        离线泡点设置.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int i = 离线泡点设置.getSelectedRow();
                String a = 离线泡点设置.getValueAt(i, 0).toString();
                String a1 = 离线泡点设置.getValueAt(i, 1).toString();
                String a2 = 离线泡点设置.getValueAt(i, 2).toString();
                离线泡点序号.setText(a);
                离线泡点类型.setText(a1);
                离线泡点值.setText(a2);
            }
        });
    }

    
      private void 刷新离线泡点金币开关() {
        String 离线泡点金币开关显示 = "";
        int 离线泡点金币开关 = ZeroMS_UI.ConfigValuesMap.get("离线泡点金币开关");
        if (离线泡点金币开关 <= 0) {
            离线泡点金币开关显示 = "泡点金币:开启";
        } else {
            离线泡点金币开关显示 = "泡点金币:关闭";
        }
        离线泡点金币开关(离线泡点金币开关显示);
    }

    private void 刷新离线泡点点券开关() {
        String 离线泡点点券开关显示 = "";
        int 离线泡点点券开关 = ZeroMS_UI.ConfigValuesMap.get("离线泡点点券开关");
        if (离线泡点点券开关 <= 0) {
            离线泡点点券开关显示 = "泡点点券:开启";
        } else {
            离线泡点点券开关显示 = "泡点点券:关闭";
        }
        离线泡点点券开关(离线泡点点券开关显示);
    }

    private void 刷新离线泡点经验开关() {
        String 离线泡点经验开关显示 = "";
        int 离线泡点经验开关 = ZeroMS_UI.ConfigValuesMap.get("离线泡点经验开关");
        if (离线泡点经验开关 <= 0) {
            离线泡点经验开关显示 = "泡点经验:开启";
        } else {
            离线泡点经验开关显示 = "泡点经验:关闭";
        }
        离线泡点经验开关(离线泡点经验开关显示);
    }

    private void 刷新离线泡点抵用开关() {
        String 离线泡点抵用开关显示 = "";
        int 离线泡点抵用开关 = ZeroMS_UI.ConfigValuesMap.get("离线泡点抵用开关");
        if (离线泡点抵用开关 <= 0) {
            离线泡点抵用开关显示 = "泡点抵用:开启";
        } else {
            离线泡点抵用开关显示 = "泡点抵用:关闭";
        }
        离线泡点抵用开关(离线泡点抵用开关显示);
    }

    private void 刷新离线泡点豆豆开关() {
        String 离线泡点豆豆开关显示 = "";
        int 离线泡点豆豆开关 = ZeroMS_UI.ConfigValuesMap.get("离线泡点豆豆开关");
        if (离线泡点豆豆开关 <= 0) {
            离线泡点豆豆开关显示 = "泡点豆豆:开启";
        } else {
            离线泡点豆豆开关显示 = "泡点豆豆:关闭";
        }
        离线泡点豆豆开关(离线泡点豆豆开关显示);
    }
     
    private void 离线泡点点券开关(String str) {
        离线泡点点券开关.setText(str);
    }

    private void 离线泡点经验开关(String str) {
        离线泡点经验开关.setText(str);
    }

    private void 离线泡点抵用开关(String str) {
        离线泡点抵用开关.setText(str);
    }
   
    private void 离线泡点金币开关(String str) {
        离线泡点金币开关.setText(str);
    }
    
    private void 离线泡点豆豆开关(String str) {
        离线泡点豆豆开关.setText(str);
    }
    
    
    private void 离线挂机开关(String str) {
        离线挂机开关.setText(str);
    }
        
        
        private void 刷新全屏检测开关() {
        String 显示 = "";
        int S = ZeroMS_UI.ConfigValuesMap.get("全屏检测开关");
        if (S <= 0) {
            显示 = "全屏检测:开启";
        } else {
            显示 = "全屏检测:关闭";
        }
        全屏检测开关.setText(显示);
    }
        
    private void 刷新离线挂机开关() {
        String 显示 = "";
        int S = ZeroMS_UI.ConfigValuesMap.get("离线挂机开关");
        if (S <= 0) {
            显示 = "离线挂机:开启";
        } else {
            显示 = "离线挂机:关闭";
        }
        离线挂机开关.setText(显示);
    }
    
    private void 刷新吸怪检测开关() {
        String 显示 = "";
        int S = ZeroMS_UI.ConfigValuesMap.get("吸怪检测开关");
        if (S <= 0) {
            显示 = "吸怪检测:开启";
        } else {
            显示 = "吸怪检测:关闭";
        }
        吸怪检测开关.setText(显示);
    }
    
    private void 刷新捡物检测开关() {
        String 显示 = "";
        int S = ZeroMS_UI.ConfigValuesMap.get("捡物检测开关");
        if (S <= 0) {
            显示 = "捡物检测:开启";
        } else {
            显示 = "捡物检测:关闭";
        }
        捡物检测开关.setText(显示);
    }
    
    private void 刷新挂机检测开关() {
        String 显示 = "";
        int S = ZeroMS_UI.ConfigValuesMap.get("挂机检测开关");
        if (S <= 0) {
            显示 = "挂机检测:开启";
        } else {
            显示 = "挂机检测:关闭";
        }
        挂机检测开关.setText(显示);
    }

    private void 刷新群攻检测开关() {
        String 显示 = "";
        int S = ZeroMS_UI.ConfigValuesMap.get("群攻检测开关");
        if (S <= 0) {
            显示 = "群攻检测:开启";
        } else {
            显示 = "群攻检测:关闭";
        }
        群攻检测开关.setText(显示);
    }

    private void 刷新段数检测开关() {
        String 显示 = "";
        int S = ZeroMS_UI.ConfigValuesMap.get("段数检测开关");
        if (S <= 0) {
            显示 = "段数检测:开启";
        } else {
            显示 = "段数检测:关闭";
        }
        段数检测开关.setText(显示);
    }

    private void 刷新加速检测开关() {
        String 显示 = "";
        int S = ZeroMS_UI.ConfigValuesMap.get("加速检测开关");
        if (S <= 0) {
            显示 = "加速检测:开启";
        } else {
            显示 = "加速检测:关闭";
        }
        加速检测开关.setText(显示);
    }
    
    private void 刷新IP多开() {
        String 显示 = "";
        int S = ZeroMS_UI.ConfigValuesMap.get("IP多开开关");
        if (S <= 0) {
            显示 = "IP多开:禁止";
        } else {
            显示 = "IP多开:允许";
        }
        IP多开开关.setText(显示);
    }

    private void 刷新机器多开() {
        String 显示 = "";
        int S = ZeroMS_UI.ConfigValuesMap.get("机器多开开关");
        if (S <= 0) {
            显示 = "机器多开:禁止";
        } else {
            显示 = "机器多开:允许";
        }
        机器多开开关.setText(显示);
    }
 
        private void 游戏找人开关(String str) {
        游戏找人开关.setText(str);
    }
    
        private void 刷新游戏找人开关() {
        String 显示 = "";
        int S = ZeroMS_UI.ConfigValuesMap.get("游戏找人开关");
        if (S <= 0) {
            显示 = "游戏找人:开启";
        } else {
            显示 = "游戏找人:关闭";
        }
        游戏找人开关.setText(显示);
    }
    
        private void 刷新拍卖行开关() {
        String 刷新拍卖行开关显示 = "";
        int 拍卖行开关 = ZeroMS_UI.ConfigValuesMap.get("拍卖行开关");
        if (拍卖行开关 <= 0) {
            刷新拍卖行开关显示 = "游戏拍卖:开启";
        } else {
            刷新拍卖行开关显示 = "游戏拍卖:关闭";
        }
        拍卖行开关(刷新拍卖行开关显示);
    }
    
        private void 拍卖行开关(String str) {
        拍卖行开关.setText(str);
    }
    
        private void 刷新脚本显码开关() {
        String 刷新脚本显码开关显示 = "";
        int 脚本显码开关 = ZeroMS_UI.ConfigValuesMap.get("脚本显码开关");
        if (脚本显码开关 <= 0) {
            刷新脚本显码开关显示 = "脚本显码:开启";
        } else {
            刷新脚本显码开关显示 = "脚本显码:关闭";
        }
        脚本显码开关(刷新脚本显码开关显示);
    }
    
        private void 脚本显码开关(String str) {
        脚本显码开关.setText(str);
    }
    
        private void 刷新游戏仓库开关() {
        String 刷新游戏仓库开关显示 = "";
        int 游戏仓库开关 = ZeroMS_UI.ConfigValuesMap.get("游戏仓库开关");
        if (游戏仓库开关 <= 0) {
            刷新游戏仓库开关显示 = "游戏仓库:开启";
        } else {
            刷新游戏仓库开关显示 = "游戏仓库:关闭";
        }
        游戏仓库开关(刷新游戏仓库开关显示);
    }
    
    private void 游戏仓库开关(String str) {
        游戏仓库开关.setText(str);
    }
    
    private void 刷新指令通知开关() {
        String 刷新指令通知开关显示 = "";
        int 指令通知开关 = ZeroMS_UI.ConfigValuesMap.get("指令通知开关");
        if (指令通知开关 <= 0) {
            刷新指令通知开关显示 = "指令通知:开启";
        } else {
            刷新指令通知开关显示 = "指令通知:关闭";
        }
        指令通知开关(刷新指令通知开关显示);
    }
       
        private void 刷新玩家聊天开关() {
        String 刷新玩家聊天开关显示 = "";
        int 玩家聊天开关 = ZeroMS_UI.ConfigValuesMap.get("玩家聊天开关");
        if (玩家聊天开关 <= 0) {
            刷新玩家聊天开关显示 = "玩家聊天:开启";
        } else {
            刷新玩家聊天开关显示 = "玩家聊天:关闭";
        }
        玩家聊天开关(刷新玩家聊天开关显示);
    }
    
        private void 刷新禁止登陆开关() {
        String 刷新禁止登陆开关显示 = "";
        int 禁止登陆开关 = ZeroMS_UI.ConfigValuesMap.get("禁止登陆开关");
        if (禁止登陆开关 <= 0) {
            刷新禁止登陆开关显示 = "游戏登陆:禁止";
        } else {
            刷新禁止登陆开关显示 = "游戏登陆:通行";
        }
        禁止登陆开关(刷新禁止登陆开关显示);
    }

    
        private void 刷新升级快讯() {
        String 升级快讯显示 = "";
        int 升级快讯 = ZeroMS_UI.ConfigValuesMap.get("升级快讯开关");
        if (升级快讯 <= 0) {
            升级快讯显示 = "升级快讯:开启";
        } else {
            升级快讯显示 = "升级快讯:关闭";
        }
        游戏升级快讯(升级快讯显示);
    }
        
        private void 刷新丢出金币开关() {
        String 刷新丢出金币开关显示 = "";
        int 丢出金币开关 = ZeroMS_UI.ConfigValuesMap.get("丢出金币开关");
        if (丢出金币开关 <= 0) {
            刷新丢出金币开关显示 = "丢出金币:开启";
        } else {
            刷新丢出金币开关显示 = "丢出金币:关闭";
        }
        丢出金币开关(刷新丢出金币开关显示);
    }

    private void 刷新玩家交易开关() {
        String 刷新玩家交易开关显示 = "";
        int 玩家交易开关 = ZeroMS_UI.ConfigValuesMap.get("玩家交易开关");
        if (玩家交易开关 <= 0) {
            刷新玩家交易开关显示 = "玩家交易:开启";
        } else {
            刷新玩家交易开关显示 = "玩家交易:关闭";
        }
        玩家交易开关(刷新玩家交易开关显示);
    }

    private void 刷新丢出物品开关() {
        String 刷新丢出物品开关显示 = "";
        int 丢出物品开关 = ZeroMS_UI.ConfigValuesMap.get("丢出物品开关");
        if (丢出物品开关 <= 0) {
            刷新丢出物品开关显示 = "丢出物品:开启";
        } else {
            刷新丢出物品开关显示 = "丢出物品:关闭";
        }
        丢出物品开关(刷新丢出物品开关显示);
    }
    
        private void 刷新游戏指令开关() {
        String 刷新游戏指令开关显示 = "";
        int 游戏指令开关 = ZeroMS_UI.ConfigValuesMap.get("游戏指令开关");
        if (游戏指令开关 <= 0) {
            刷新游戏指令开关显示 = "游戏指令:关闭";
        } else {
            刷新游戏指令开关显示 = "游戏指令:开启";
        }
        游戏指令开关(刷新游戏指令开关显示);
    }
    
       private void 刷新上线提醒开关() {
        String 刷新上线提醒开关显示 = "";
        int 上线提醒开关 = ZeroMS_UI.ConfigValuesMap.get("上线提醒开关");
        if (上线提醒开关 <= 0) {
            刷新上线提醒开关显示 = "登录公告:开启";
        } else {
            刷新上线提醒开关显示 = "登录公告:关闭";
        }
        上线提醒开关(刷新上线提醒开关显示);
    }
    
       private void 刷新回收地图开关() {
        String 刷新回收地图开关显示 = "";
        int 回收地图开关 = ZeroMS_UI.ConfigValuesMap.get("回收地图开关");
        if (回收地图开关 <= 0) {
            刷新回收地图开关显示 = "回收地图:开启";
        } else {
            刷新回收地图开关显示 = "回收地图:关闭";
        }
        回收地图开关(刷新回收地图开关显示);
    }
    
        private void 刷新管理隐身开关() {
        String 刷新管理隐身开关显示 = "";
        int 管理隐身开关 = ZeroMS_UI.ConfigValuesMap.get("管理隐身开关");
        if (管理隐身开关 <= 0) {
            刷新管理隐身开关显示 = "管理隐身:开启";
        } else {
            刷新管理隐身开关显示 = "管理隐身:关闭";
        }
        管理隐身开关(刷新管理隐身开关显示);
    }

    private void 刷新管理加速开关() {
        String 刷新管理加速开关显示 = "";
        int 管理加速开关 = ZeroMS_UI.ConfigValuesMap.get("管理加速开关");
        if (管理加速开关 <= 0) {
            刷新管理加速开关显示 = "管理加速:开启";
        } else {
            刷新管理加速开关显示 = "管理加速:关闭";
        }
        管理加速开关(刷新管理加速开关显示);
    }
    
        private void 刷新雇佣商人开关() {
        String 刷新雇佣商人开关显示 = "";
        int 雇佣商人开关 = ZeroMS_UI.ConfigValuesMap.get("雇佣商人开关");
        if (雇佣商人开关 <= 0) {
            刷新雇佣商人开关显示 = "雇佣商人:开启";
        } else {
            刷新雇佣商人开关显示 = "雇佣商人:关闭";
        }
        雇佣商人开关(刷新雇佣商人开关显示);
    }
    
        private void 刷新欢迎弹窗开关() {
        String 刷新欢迎弹窗开关显示 = "";
        int 欢迎弹窗开关 = ZeroMS_UI.ConfigValuesMap.get("欢迎弹窗开关");
        if (欢迎弹窗开关 <= 0) {
            刷新欢迎弹窗开关显示 = "欢迎弹窗:开启";
        } else {
            刷新欢迎弹窗开关显示 = "欢迎弹窗:关闭";
        }
        欢迎弹窗开关(刷新欢迎弹窗开关显示);
    }
    
       private void 刷新滚动公告开关() {
        String 刷新滚动公告开关显示 = "";
        int 滚动公告开关 = ZeroMS_UI.ConfigValuesMap.get("滚动公告开关");
        if (滚动公告开关 <= 0) {
            刷新滚动公告开关显示 = "滚动公告:开启";
        } else {
            刷新滚动公告开关显示 = "滚动公告:关闭";
        }
        滚动公告开关(刷新滚动公告开关显示);
    }

    private void 刷新游戏喇叭开关() {
        String 刷新游戏喇叭开关显示 = "";
        int 游戏喇叭开关 = ZeroMS_UI.ConfigValuesMap.get("游戏喇叭开关");
        if (游戏喇叭开关 <= 0) {
            刷新游戏喇叭开关显示 = "游戏喇叭:开启";
        } else {
            刷新游戏喇叭开关显示 = "游戏喇叭:关闭";
        }
        游戏喇叭开关(刷新游戏喇叭开关显示);
    }

    
        private void 滚动公告开关(String str) {
        滚动公告开关.setText(str);
    }

    private void 回收地图开关(String str) {
        回收地图开关.setText(str);
    }
    
        private void 玩家聊天开关(String str) {
        玩家聊天开关.setText(str);
    }
    
        private void 上线提醒开关(String str) {
        上线提醒开关.setText(str);
    }

    private void 指令通知开关(String str) {
        指令通知开关.setText(str);
    }

    private void 雇佣商人开关(String str) {
        雇佣商人开关.setText(str);
    }
    
        private void 欢迎弹窗开关(String str) {
        欢迎弹窗开关.setText(str);
    }
        
        private void 管理隐身开关(String str) {
        管理隐身开关.setText(str);
    }

    private void 管理加速开关(String str) {
        管理加速开关.setText(str);
    }

    private void 游戏指令开关(String str) {
        游戏指令开关.setText(str);
    }

    private void 游戏喇叭开关(String str) {
        游戏喇叭开关.setText(str);
    }

    private void 丢出金币开关(String str) {
        丢出金币开关.setText(str);
    }

    private void 玩家交易开关(String str) {
        玩家交易开关.setText(str);
    }

    private void 丢出物品开关(String str) {
        丢出物品开关.setText(str);
    }

    private void 禁止登陆开关(String str) {
        禁止登陆开关.setText(str);
    }

    
    private void 游戏升级快讯(String str) {
        游戏升级快讯.setText(str);
    }

    
    private void 刷新登陆帮助() {
        String 显示 = "";
        int S = ZeroMS_UI.ConfigValuesMap.get("登陆帮助开关");
        if (S <= 0) {
            显示 = "登陆帮助:开启";
        } else {
            显示 = "登陆帮助:关闭";
        }
        登陆帮助开关.setText(显示);
    }
    
    private void 刷新怪物状态开关() {
        String 显示 = "";
        int S = ZeroMS_UI.ConfigValuesMap.get("怪物状态开关");
        if (S <= 0) {
            显示 = "怪物状态:开启";
        } else {
            显示 = "怪物状态:关闭";
        }
        怪物状态开关.setText(显示);
    }
    
     private void 刷新越级打怪开关() {
        String 显示 = "";
        int S = ZeroMS_UI.ConfigValuesMap.get("越级打怪开关");
        if (S <= 0) {
            显示 = "越级打怪:开启";
        } else {
            显示 = "越级打怪:关闭";
        }
        越级打怪开关.setText(显示);
    }
    
    private void 刷新地图名称开关() {
        String 显示 = "";
        int S = ZeroMS_UI.ConfigValuesMap.get("地图名称开关");
        if (S <= 0) {
            显示 = "地图名称:显示";
        } else {
            显示 = "地图名称:关闭";
        }
        地图名称开关.setText(显示);
    }
    
    
    private void 刷新过图存档时间() {
        String 显示 = "";
        int S = ZeroMS_UI.ConfigValuesMap.get("过图存档开关");
        if (S <= 0) {
            显示 = "过图存档:开启";
        } else {
            显示 = "过图存档:关闭";
        }
        过图存档开关.setText(显示);
    }
    
        
    
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
    
   private void 蓝蜗牛开关(String str) {
        蓝蜗牛开关.setText(str);
    }

    private void 蘑菇仔开关(String str) {
        蘑菇仔开关.setText(str);
    }

    private void 绿水灵开关(String str) {
        绿水灵开关.setText(str);
    }

    private void 漂漂猪开关(String str) {
        漂漂猪开关.setText(str);
    }

    private void 小青蛇开关(String str) {
        小青蛇开关.setText(str);
    }

    private void 红螃蟹开关(String str) {
        红螃蟹开关.setText(str);
    }

    private void 大海龟开关(String str) {
        大海龟开关.setText(str);
    }

    private void 章鱼怪开关(String str) {
        章鱼怪开关.setText(str);
    }

    private void 顽皮猴开关(String str) {
        顽皮猴开关.setText(str);
    }

    private void 星精灵开关(String str) {
        星精灵开关.setText(str);
    }

    private void 胖企鹅开关(String str) {
        胖企鹅开关.setText(str);
    }

    private void 白雪人开关(String str) {
        白雪人开关.setText(str);
    }

    private void 紫色猫开关(String str) {
        紫色猫开关.setText(str);
    }

    private void 大灰狼开关(String str) {
        大灰狼开关.setText(str);
    }

    private void 小白兔开关(String str) {
        小白兔开关.setText(str);
    }

    private void 喷火龙开关(String str) {
        喷火龙开关.setText(str);
    }

    private void 火野猪开关(String str) {
        火野猪开关.setText(str);
    }

    private void 青鳄鱼开关(String str) {
        青鳄鱼开关.setText(str);
    }

    private void 花蘑菇开关(String str) {
        花蘑菇开关.setText(str);
    }

    private void 刷新花蘑菇开关() {
        String 花蘑菇显示 = "";
        int 花蘑菇 = ZeroMS_UI.ConfigValuesMap.get("花蘑菇开关");
        if (花蘑菇 <= 0) {
            花蘑菇显示 = "花蘑菇:开";
        } else {
            花蘑菇显示 = "花蘑菇:关";
        }
        花蘑菇开关(花蘑菇显示);
    }

    private void 刷新火野猪开关() {
        String 火野猪显示 = "";
        int 火野猪 = ZeroMS_UI.ConfigValuesMap.get("火野猪开关");
        if (火野猪 <= 0) {
            火野猪显示 = "火野猪:开";
        } else {
            火野猪显示 = "火野猪:关";
        }
        火野猪开关(火野猪显示);
    }

    private void 刷新青鳄鱼开关() {
        String 青鳄鱼显示 = "";
        int 青鳄鱼 = ZeroMS_UI.ConfigValuesMap.get("青鳄鱼开关");
        if (青鳄鱼 <= 0) {
            青鳄鱼显示 = "青鳄鱼:开";
        } else {
            青鳄鱼显示 = "青鳄鱼:关";
        }
        青鳄鱼开关(青鳄鱼显示);
    }

    private void 刷新喷火龙开关() {
        String 喷火龙显示 = "";
        int 喷火龙 = ZeroMS_UI.ConfigValuesMap.get("喷火龙开关");
        if (喷火龙 <= 0) {
            喷火龙显示 = "喷火龙:开";
        } else {
            喷火龙显示 = "喷火龙:关";
        }
        喷火龙开关(喷火龙显示);
    }

    private void 刷新小白兔开关() {
        String 小白兔显示 = "";
        int 小白兔 = ZeroMS_UI.ConfigValuesMap.get("小白兔开关");
        if (小白兔 <= 0) {
            小白兔显示 = "小白兔:开";
        } else {
            小白兔显示 = "小白兔:关";
        }
        小白兔开关(小白兔显示);
    }

    private void 刷新大灰狼开关() {
        String 大灰狼显示 = "";
        int 大灰狼 = ZeroMS_UI.ConfigValuesMap.get("大灰狼开关");
        if (大灰狼 <= 0) {
            大灰狼显示 = "大灰狼:开";
        } else {
            大灰狼显示 = "大灰狼:关";
        }
        大灰狼开关(大灰狼显示);
    }

    private void 刷新紫色猫开关() {
        String 紫色猫显示 = "";
        int 紫色猫 = ZeroMS_UI.ConfigValuesMap.get("紫色猫开关");
        if (紫色猫 <= 0) {
            紫色猫显示 = "紫色猫:开";
        } else {
            紫色猫显示 = "紫色猫:关";
        }
        紫色猫开关(紫色猫显示);
    }

    private void 石头人开关(String str) {
        石头人开关.setText(str);
    }

    private void 刷新石头人开关() {
        String 石头人显示 = "";
        int 石头人 = ZeroMS_UI.ConfigValuesMap.get("石头人开关");
        if (石头人 <= 0) {
            石头人显示 = "石头人:开";
        } else {
            石头人显示 = "石头人:关";
        }
        石头人开关(石头人显示);
    }

    private void 刷新白雪人开关() {
        String 白雪人显示 = "";
        int 白雪人 = ZeroMS_UI.ConfigValuesMap.get("白雪人开关");
        if (白雪人 <= 0) {
            白雪人显示 = "白雪人:开";
        } else {
            白雪人显示 = "白雪人:关";
        }
        白雪人开关(白雪人显示);
    }

    private void 刷新胖企鹅开关() {
        String 胖企鹅显示 = "";
        int 胖企鹅 = ZeroMS_UI.ConfigValuesMap.get("胖企鹅开关");
        if (胖企鹅 <= 0) {
            胖企鹅显示 = "胖企鹅:开";
        } else {
            胖企鹅显示 = "胖企鹅:关";
        }
        胖企鹅开关(胖企鹅显示);
    }

    private void 刷新星精灵开关() {
        String 星精灵显示 = "";
        int 星精灵 = ZeroMS_UI.ConfigValuesMap.get("星精灵开关");
        if (星精灵 <= 0) {
            星精灵显示 = "星精灵:开";
        } else {
            星精灵显示 = "星精灵:关";
        }
        星精灵开关(星精灵显示);
    }

    private void 刷新顽皮猴开关() {
        String 顽皮猴显示 = "";
        int 顽皮猴 = ZeroMS_UI.ConfigValuesMap.get("顽皮猴开关");
        if (顽皮猴 <= 0) {
            顽皮猴显示 = "顽皮猴:开";
        } else {
            顽皮猴显示 = "顽皮猴:关";
        }
        顽皮猴开关(顽皮猴显示);
    }

    private void 刷新章鱼怪开关() {
        String 章鱼怪显示 = "";
        int 章鱼怪 = ZeroMS_UI.ConfigValuesMap.get("章鱼怪开关");
        if (章鱼怪 <= 0) {
            章鱼怪显示 = "章鱼怪:开";
        } else {
            章鱼怪显示 = "章鱼怪:关";
        }
        章鱼怪开关(章鱼怪显示);
    }

    private void 刷新大海龟开关() {
        String 大海龟显示 = "";
        int 大海龟 = ZeroMS_UI.ConfigValuesMap.get("大海龟开关");
        if (大海龟 <= 0) {
            大海龟显示 = "大海龟:开";
        } else {
            大海龟显示 = "大海龟:关";
        }
        大海龟开关(大海龟显示);
    }

    private void 刷新红螃蟹开关() {
        String 红螃蟹显示 = "";
        int 红螃蟹 = ZeroMS_UI.ConfigValuesMap.get("红螃蟹开关");
        if (红螃蟹 <= 0) {
            红螃蟹显示 = "红螃蟹:开";
        } else {
            红螃蟹显示 = "红螃蟹:关";
        }
        红螃蟹开关(红螃蟹显示);
    }

    private void 刷新小青蛇开关() {
        String 小青蛇显示 = "";
        int 小青蛇 = ZeroMS_UI.ConfigValuesMap.get("小青蛇开关");
        if (小青蛇 <= 0) {
            小青蛇显示 = "小青蛇:开";
        } else {
            小青蛇显示 = "小青蛇:关";
        }
        小青蛇开关(小青蛇显示);
    }

    private void 刷新蓝蜗牛开关() {
        String 蓝蜗牛显示 = "";
        int 蓝蜗牛 = ZeroMS_UI.ConfigValuesMap.get("蓝蜗牛开关");
        if (蓝蜗牛 <= 0) {
            蓝蜗牛显示 = "蓝蜗牛:开";
        } else {
            蓝蜗牛显示 = "蓝蜗牛:关";
        }
        蓝蜗牛开关(蓝蜗牛显示);
    }

    private void 刷新漂漂猪开关() {
        String 漂漂猪显示 = "";
        int 漂漂猪 = ZeroMS_UI.ConfigValuesMap.get("漂漂猪开关");
        if (漂漂猪 <= 0) {
            漂漂猪显示 = "漂漂猪:开";
        } else {
            漂漂猪显示 = "漂漂猪:关";
        }
        漂漂猪开关(漂漂猪显示);
    }

    private void 刷新绿水灵开关() {
        String 绿水灵显示 = "";
        int 绿水灵 = ZeroMS_UI.ConfigValuesMap.get("绿水灵开关");
        if (绿水灵 <= 0) {
            绿水灵显示 = "绿水灵:开";
        } else {
            绿水灵显示 = "绿水灵:关";
        }
        绿水灵开关(绿水灵显示);
    }

    private void 刷新蘑菇仔开关() {
        String 蘑菇仔显示 = "";
        int 蘑菇仔 = ZeroMS_UI.ConfigValuesMap.get("蘑菇仔开关");
        if (蘑菇仔 <= 0) {
            蘑菇仔显示 = "蘑菇仔:开";
        } else {
            蘑菇仔显示 = "蘑菇仔:关";
        }
        蘑菇仔开关(蘑菇仔显示);
    }
    
    
            public void runTool(final Tools tool) {
        if (tools.contains(tool)) {
            JOptionPane.showMessageDialog(null, "工具已在运行。");
        } else {
            tools.add(tool);
            Thread t = new Thread() {
                @Override
                public void run() {
                    switch (tool) {
                        case DumpCashShop:
                            DumpCashShop.main(new String[0]);
                            break;
                        case DumpItems:
                            DumpItems.main(new String[0]);
                            break;
                        case WzStringDumper装备数据:
                    {
                        try {
                            WzStringDumper装备数据.main(new String[0]);
                        } catch (IOException ex) {
                            Logger.getLogger(ZeroMS_UI.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                            break;
                        case DumpMobSkills:
                            DumpMobSkills.main(new String[0]);
                            break;
                        case DumpNpcNames:
                    {
                        try {
                            DumpNpcNames.main(new String[0]);
                        } catch (SQLException ex) {
                            Logger.getLogger(ZeroMS_UI.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                            break;
                        case DumpOxQuizData:
                    {
                        try {
                            DumpOxQuizData.main(new String[0]);
                        } catch (IOException ex) {
                            Logger.getLogger(ZeroMS_UI.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (SQLException ex) {
                            Logger.getLogger(ZeroMS_UI.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                            break;
                        case DumpQuests:
                            DumpQuests.main(new String[0]);
                            break;
                        case MonsterDropCreator:
                    {
                        try {
                            MonsterDropCreator.main(new String[0]);
                        } catch (IOException ex) {
                            Logger.getLogger(ZeroMS_UI.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (NotBoundException ex) {
                            Logger.getLogger(ZeroMS_UI.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (InstanceAlreadyExistsException ex) {
                            Logger.getLogger(ZeroMS_UI.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (MBeanRegistrationException ex) {
                            Logger.getLogger(ZeroMS_UI.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (NotCompliantMBeanException ex) {
                            Logger.getLogger(ZeroMS_UI.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (MalformedObjectNameException ex) {
                            Logger.getLogger(ZeroMS_UI.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                            break;
                            
                    }
                    tools.remove(tool);
                }
            };
            t.start();
        }
    }
    
    
        public void openWindow(final Windows w) {
        if (!windows.containsKey(w)) {
            switch (w) {
                    case 基址计算工具:
                    windows.put(w, new 基址计算工具());
                    break;
                    case 卡密制作工具:
                    windows.put(w, new 卡密制作工具());
                    break;
                    case 包头转换工具:
                    windows.put(w, new 包头转换工具());
                    break;
                case 代码查询工具:
                    windows.put(w, new 代码查询工具());
                    break;
                    case 角色转移工具:
                    windows.put(w, new 角色转移工具());
                    break;
                    case 一键还原:
                    windows.put(w, new 一键还原());
                    break;
                    case 商城管理控制台:
                    windows.put(w, new 商城管理控制台());
                    break;
                    case 商店管理控制台:
                    windows.put(w, new 商店管理控制台());
                    break;
                    case 怪物爆率控制台:
                    windows.put(w, new 怪物爆率控制台());
                    break;
                    case 箱子爆率控制台:
                    windows.put(w, new 箱子爆率控制台());
                    break;
                    case 锻造控制台:
                    windows.put(w, new 锻造控制台());
                    break;
                    case 副本控制台:
                    windows.put(w, new 副本控制台());
                    break;
                    case 删除自添加NPC工具:
                    windows.put(w, new 删除自添加NPC工具());
                    break;
                    case 野外BOSS刷新时间:
                    windows.put(w, new 野外BOSS刷新时间());
                    break;
                    case 药水冷却时间控制台:
                    windows.put(w, new 药水冷却时间控制台());
                    break;
                    case 金锤子成功率控制台:
                    windows.put(w, new 金锤子成功率控制台());
                    break;
                    case 永恒重生装备控制台:
                    windows.put(w, new 永恒重生装备控制台());
                    break;
                    case 游戏抽奖工具:
                    windows.put(w, new 游戏抽奖工具());
                    break;
                    case 椅子控制台:
                    windows.put(w, new 椅子控制台());
                    break;
                    case 鱼来鱼往:
                    windows.put(w, new 鱼来鱼往());
                    break;
                    case 钓鱼控制台:
                    windows.put(w, new 钓鱼控制台());
                    break;
                    case OX答题控制台:
                    windows.put(w, new OX答题控制台());
                    break;
                    case 批量删除工具:
                    windows.put(w, new 批量删除工具());
                    break;
                    case 游戏家族控制台:
                    windows.put(w, new 游戏家族控制台());
                    break;
                    case MACip封禁:
                    windows.put(w, new MACip封禁());
                    break;
                default:
                    return;
            }
            windows.get(w).setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        }
        windows.get(w).setVisible(true);
    }

    private void resetSetting(boolean read) {
        if (read) {
            ServerProperties.loadProperties();
            WorldConstants.loadSetting();
            ServerConstants.loadSetting();
        }
        经验倍率.setText(String.valueOf(WorldConstants.EXP_RATE));           
        金币爆率.setText(String.valueOf(WorldConstants.MESO_RATE));    
        物品爆率.setText(String.valueOf(WorldConstants.DROP_RATE));  
        加载事件.setText(String.valueOf(WorldConstants.eventSM));  
        顶部公告.setText(String.valueOf(WorldConstants.SCROLL_MESSAGE));
        事件名字.setText(String.valueOf(WorldConstants.WORLD_TIP));
        最大登录角色数限制.setText(String.valueOf(WorldConstants.USER_LIMIT));
        频道最大人数.setText(String.valueOf(WorldConstants.MAX_CHAR_VIEW));
        最大创建角色数.setText(String.valueOf(WorldConstants.maxCharacters));
        频道总数.setText(String.valueOf(WorldConstants.CHANNEL_COUNT));
        频道状态.setText(String.valueOf(WorldConstants.FLAG));
        游戏IP.setText(String.valueOf(ServerConfig.IP));
        登录端口.setText(String.valueOf(LoginServer.port));
        频道端口.setText(String.valueOf(WorldConstants.Pport));
        商城端口.setText(String.valueOf(WorldConstants.Sport));
        雇佣时间.setText(String.valueOf(WorldConstants.merchantTime));
        最大能力值.setText(String.valueOf(WorldConstants.statLimit));
        潜能几率.setText(String.valueOf(WorldConstants.state));
        高级魔方几率.setText(String.valueOf(WorldConstants.state1));
        家族金币.setText(String.valueOf(WorldConstants.createGuildCost));
        家族徽章费用.setText(String.valueOf(ServerConfig.家族徽章费用));
        人物灌水比例.setText(String.valueOf(ServerConfig.RSGS));
        管理员模式.setSelected(WorldConstants.ADMIN_ONLY);
        允许使用商城.setSelected(WorldConstants.CS_ENABLE);
        允许玩家穿戴GM道具.setSelected(WorldConstants.GMITEMS);
        //数据库
        数据库IP.setText(String.valueOf(WorldConstants.dbip));
        数据库用户名.setText(String.valueOf(WorldConstants.dbuser));
        数据库密码.setText(String.valueOf(WorldConstants.dbpassword));
        数据库端口.setText(String.valueOf(WorldConstants.dbport));
        数据库名.setText(String.valueOf(WorldConstants.dbname));
        连接数.setText(String.valueOf(WorldConstants.timeout));
       //服务端
        游戏名字.setText(String.valueOf(ServerConfig.SERVERNAME));
        测试机.setSelected(ServerConstants.TESPIA);
        wz路径.setText(String.valueOf(ServerConfig.wzpath));
        自动注册.setSelected(ServerConfig.AUTO_REGISTER);
        日志模式.setSelected(ServerConfig.LOG_PACKETS);
        debug模式.setSelected(ServerConfig.DEBUG_MODE);
        检测外挂自动封号.setSelected(ServerConfig.检测外挂自动封号);
        精灵商人出售记录.setSelected(ServerConfig.LOG_MRECHANT);
        广播记录.setSelected(ServerConfig.LOG_MEGA);
        商城购买记录.setSelected(ServerConfig.LOG_CSBUY);
        伤害输出记录.setSelected(ServerConfig.LOG_DAMAGE);
        聊天记录.setSelected(ServerConfig.LOG_CHAT);
        角色封包输出记录.setSelected(ServerConfig.CHRLOG_PACKETS);
        匿名广播.setSelected(ServerConfig.NMGB);
        频道掉线测试.setSelected(ServerConfig.PDCS);
        
        禁止玩家使用商店.setSelected(WorldConstants.JZSD);
        无延迟检测.setSelected(WorldConstants.WUYANCHI);
        定时测谎.setSelected(WorldConstants.LieDetector);
        丢物品信息.setSelected(WorldConstants.DropItem);
        卷轴防爆1.setSelected(ServerConfig.shieldWardAll);
        刷怪开关1.setSelected(ServerConfig.dMapAddMob);
        地图列表ID.setText(String.valueOf(ServerConfig.dMapAddMobMapList));
        自定义地图刷怪倍数1.setText(String.valueOf(ServerConfig.dMapAddMobNum));
        物品叠加开关.setSelected(ServerConfig.dSoltMax);
        物品叠加数量.setText(String.valueOf(ServerConfig.dSoltMaxNum));
        开店额外经验.setText(String.valueOf(ServerConfig.开店额外经验));
        结婚额外经验.setText(String.valueOf(ServerConfig.结婚额外经验));
        ExpBound1.setText(String.valueOf(ServerConfig.ExpBound1));//经验
        ExpBound2.setText(String.valueOf(ServerConfig.ExpBound2));//经验
        ExpBound3.setText(String.valueOf(ServerConfig.ExpBound3));//经验
        ExpBound4.setText(String.valueOf(ServerConfig.ExpBound4));//经验
        ExpBound5.setText(String.valueOf(ServerConfig.ExpBound5));//经验
        ExpBound6.setText(String.valueOf(ServerConfig.ExpBound6));//经验
        清理怪物时间.setText(String.valueOf(ServerConfig.清理怪物时间));//清理怪物时间
        无限BUFF.setSelected(ServerConfig.无限BUFF);
        测谎仪.setSelected(ServerConfig.测谎仪开关);
        锻造系统.setSelected(ServerConfig.锻造系统开关);
        学院系统.setSelected(ServerConfig.学院系统开关);
    }

    private void updateSetting(boolean save) {
        ServerProperties.setProperty("ZeroMS.expRate", 经验倍率.getText());//经验倍数
        ServerProperties.setProperty("ZeroMS.mesoRate", 金币爆率.getText());//冒险币爆率
        ServerProperties.setProperty("ZeroMS.dropRate", 物品爆率.getText());//物品爆率
        ServerProperties.setProperty("ZeroMS.events", 加载事件.getText());//游戏公告
        ServerProperties.setProperty("ZeroMS.serverMessage", 顶部公告.getText());//脚本公告
        ServerProperties.setProperty("ZeroMS.eventMessage", 事件名字.getText());//脚本公告
        ServerProperties.setProperty("ZeroMS.userlimit", 最大登录角色数限制.getText());//最大登录人数
        ServerProperties.setProperty("ZeroMS.maxCharView", 频道最大人数.getText());//最大登录人数
        ServerProperties.setProperty("ZeroMS.maxCharacters", 最大创建角色数.getText());//最大登录人数
        ServerProperties.setProperty("ZeroMS.channel.count", 频道总数.getText());//频道总数
        ServerProperties.setProperty("ZeroMS.flag", 频道状态.getText());//频道总数
        ServerProperties.setProperty("ZeroMS.ip.gateway", 游戏IP.getText());//游戏IP
        ServerProperties.setProperty("ZeroMS.login.port", 登录端口.getText());//游戏IP
        ServerProperties.setProperty("ZeroMS.channel.port", 频道端口.getText());//游戏IP
        ServerProperties.setProperty("ZeroMS.cashshop.port", 商城端口.getText());//游戏IP
        ServerProperties.setProperty("ZeroMS.admin", String.valueOf(管理员模式.isSelected()));
        ServerProperties.setProperty("ZeroMS.cashshop.enable", String.valueOf(允许使用商城.isSelected()));
        ServerProperties.setProperty("ZeroMS.gmitems", String.valueOf(允许玩家穿戴GM道具.isSelected()));
        //数据库
        ServerProperties.setProperty("ZeroMS.db.ip", 数据库IP.getText());
        ServerProperties.setProperty("ZeroMS.db.user", 数据库用户名.getText());
        ServerProperties.setProperty("ZeroMS.db.password", 数据库密码.getText());
        ServerProperties.setProperty("ZeroMS.db.port", 数据库端口.getText());
        ServerProperties.setProperty("ZeroMS.db.name", 数据库名.getText());
        ServerProperties.setProperty("timeout", 连接数.getText());
       //服务端
        ServerProperties.setProperty("ZeroMS.serverName", 游戏名字.getText());//脚本公告
        
        ServerProperties.setProperty("ZeroMS.tespia", String.valueOf(测试机.isSelected()));
        ServerProperties.setProperty("ZeroMS.wzpath", wz路径.getText());//游戏IP
        ServerProperties.setProperty("ZeroMS.autoRegister", String.valueOf(自动注册.isSelected()));
        ServerProperties.setProperty("ZeroMS.packetLog", String.valueOf(日志模式.isSelected()));
        ServerProperties.setProperty("ZeroMS.debug", String.valueOf(debug模式.isSelected()));
        ServerProperties.setProperty("ZeroMS.检测外挂自动封号", String.valueOf(检测外挂自动封号.isSelected()));
        
        ServerProperties.setProperty("ZeroMS.merchantLog", String.valueOf(精灵商人出售记录.isSelected()));
        ServerProperties.setProperty("ZeroMS.megaLog", String.valueOf(广播记录.isSelected()));
        ServerProperties.setProperty("ZeroMS.csLog", String.valueOf(商城购买记录.isSelected()));
        ServerProperties.setProperty("ZeroMS.damLog", String.valueOf(伤害输出记录.isSelected()));
        ServerProperties.setProperty("ZeroMS.chatLog", String.valueOf(聊天记录.isSelected()));
        ServerProperties.setProperty("ZeroMS.jsLog", String.valueOf(角色封包输出记录.isSelected()));
        
        
        ServerProperties.setProperty("ZeroMS.jzsd", String.valueOf(禁止玩家使用商店.isSelected()));//禁止玩家使用商店
        ServerProperties.setProperty("ZeroMS.wuyanchi", String.valueOf(无延迟检测.isSelected()));//无延迟检测
        ServerProperties.setProperty("ZeroMS.LieDetector", String.valueOf(定时测谎.isSelected()));//定时测谎
        ServerProperties.setProperty("ZeroMS.DropItem", String.valueOf(丢物品信息.isSelected()));//丢弃物品信息 
        
        
        ServerProperties.setProperty("ZeroMS.nmgb", String.valueOf(匿名广播.isSelected()));
        ServerProperties.setProperty("ZeroMS.pdcs", String.valueOf(频道掉线测试.isSelected()));
        ServerProperties.setProperty("ZeroMS.rsgs", 人物灌水比例.getText());
        ServerProperties.setProperty("ZeroMS.merchantTime",雇佣时间.getText());
        ServerProperties.setProperty("ZeroMS.statLimit",最大能力值.getText());
        ServerProperties.setProperty("ZeroMS.state", 潜能几率.getText());
        ServerProperties.setProperty("ZeroMS.高级魔方几率", 高级魔方几率.getText());
        ServerProperties.setProperty("ZeroMS.createGuildCost", 家族金币.getText());
        ServerProperties.setProperty("ZeroMS.createEmblemMoney", 家族徽章费用.getText());
        ServerProperties.setProperty("ZeroMS.shieldWardAll", String.valueOf(卷轴防爆1.isSelected()));
        ServerProperties.setProperty("ZeroMS.dMapAddMob", String.valueOf(刷怪开关1.isSelected()));
        ServerProperties.setProperty("ZeroMS.dMapAddMobMapList", 地图列表ID.getText());
        ServerProperties.setProperty("ZeroMS.dMapAddMobNum", 自定义地图刷怪倍数1.getText());

        ServerProperties.setProperty("ZeroMS.dSoltMax", String.valueOf(物品叠加开关.isSelected()));
        ServerProperties.setProperty("ZeroMS.dSoltMaxNum", 物品叠加数量.getText());
        ServerProperties.setProperty("ZeroMS.开店额外经验", 开店额外经验.getText());
        ServerProperties.setProperty("ZeroMS.结婚额外经验", 结婚额外经验.getText());
        ServerProperties.setProperty("ZeroMS.无限BUFF", String.valueOf(无限BUFF.isSelected()));
        ServerProperties.setProperty("ZeroMS.测谎仪开关", String.valueOf(测谎仪.isSelected()));
        ServerProperties.setProperty("ZeroMS.锻造系统开关", String.valueOf(锻造系统.isSelected()));
        ServerProperties.setProperty("ZeroMS.学院系统开关", String.valueOf(学院系统.isSelected()));
        ServerProperties.setProperty("ZeroMS.ExpBound1", ExpBound1.getText());
        ServerProperties.setProperty("ZeroMS.ExpBound2", ExpBound2.getText());
        ServerProperties.setProperty("ZeroMS.ExpBound3", ExpBound3.getText());
        ServerProperties.setProperty("ZeroMS.ExpBound4", ExpBound4.getText());
        ServerProperties.setProperty("ZeroMS.ExpBound5", ExpBound5.getText());
        ServerProperties.setProperty("ZeroMS.ExpBound6", ExpBound6.getText());
        ServerProperties.setProperty("ZeroMS.清理怪物时间", 清理怪物时间.getText());
        ServerConstants.loadSetting();

        if (save) {
            ServerProperties.saveProperties();
        }
        // resetWorldPanel();

    }
    class MyTableCellRenderer implements TableCellRenderer {

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            ImageIcon ker = new ImageIcon("null");
            JLabel label = new JLabel(ker);
            label.setOpaque(false);
            return label;
        }
    }

    public enum Tools {

        DumpCashShop,
        DumpItems,
        WzStringDumper装备数据,
        DumpMobSkills,
        DumpNpcNames,
        DumpOxQuizData,
        DumpQuests,
        MonsterDropCreator;
    }

    public enum Windows {

        基址计算工具,
        角色转移工具,
        一键还原,
        商城管理控制台,
        商店管理控制台,
        怪物爆率控制台,
        箱子爆率控制台,
        锻造控制台,
        副本控制台,
        卡密制作工具,
        包头转换工具,
        代码查询工具,
        删除自添加NPC工具,
        游戏抽奖工具,
        药水冷却时间控制台,
        金锤子成功率控制台,
        永恒重生装备控制台,
        野外BOSS刷新时间,
        椅子控制台,
        鱼来鱼往,
        钓鱼控制台,
        OX答题控制台,
        批量删除工具,
        游戏家族控制台,
        MACip封禁,
        CashShopItemEditor,
        CashShopItemAdder,
        DropDataAdder,
        DropDataEditor,;

    }
    
        private javax.swing.ComboBoxModel getMapleTypeModel() {
        Vector mapleTypeModel = new Vector();
        mapleTypeModel.add("中国大陆");
//        for (ServerConstants.MapleType e : ServerConstants.MapleType.values()) {
//            if (e == ServerConstants.MapleType.UNKNOWN) {
//                continue;
//            }
//            mapleTypeModel.add(e.name());
//        }
        return new DefaultComboBoxModel(mapleTypeModel);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings({"unchecked", "serial"})
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane = new javax.swing.JTabbedPane();
        jPanel20 = new javax.swing.JPanel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel28 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        consoleInfo = new javax.swing.JTextArea();
        jPanel29 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        output_packet_jTextPane = new javax.swing.JTextPane();
        jPanel30 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        output_notice_jTextPane = new javax.swing.JTextPane();
        jPanel31 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        output_err_jTextPane = new javax.swing.JTextPane();
        jPanel32 = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        output_out_jTextPane = new javax.swing.JTextPane();
        jPanel7 = new javax.swing.JPanel();
        重载副本按钮 = new javax.swing.JButton();
        重载爆率按钮 = new javax.swing.JButton();
        重载传送门按钮 = new javax.swing.JButton();
        重载商店按钮 = new javax.swing.JButton();
        重载包头按钮 = new javax.swing.JButton();
        重载任务 = new javax.swing.JButton();
        重载反应堆按钮 = new javax.swing.JButton();
        重载商城按钮 = new javax.swing.JButton();
        jPanel15 = new javax.swing.JPanel();
        保存雇佣按钮 = new javax.swing.JButton();
        保存数据按钮 = new javax.swing.JButton();
        查询在线玩家人数按钮 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton21 = new javax.swing.JButton();
        进度条 = new javax.swing.JProgressBar();
        状态信息 = new javax.swing.JLabel();
        进度条1 = new javax.swing.JProgressBar();
        jPanel21 = new javax.swing.JPanel();
        jTabbedPane6 = new javax.swing.JTabbedPane();
        jPanel12 = new javax.swing.JPanel();
        jPanel49 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        游戏名字 = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        游戏IP = new javax.swing.JTextField();
        jLabel52 = new javax.swing.JLabel();
        jTextField22 = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel54 = new javax.swing.JLabel();
        测试机 = new javax.swing.JCheckBox();
        日志模式 = new javax.swing.JCheckBox();
        自动注册 = new javax.swing.JCheckBox();
        jLabel31 = new javax.swing.JLabel();
        wz路径 = new javax.swing.JTextField();
        debug模式 = new javax.swing.JCheckBox();
        事件名字 = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        允许使用商城 = new javax.swing.JCheckBox();
        允许玩家穿戴GM道具 = new javax.swing.JCheckBox();
        管理员模式 = new javax.swing.JCheckBox();
        检测外挂自动封号 = new javax.swing.JCheckBox();
        匿名广播 = new javax.swing.JCheckBox();
        频道掉线测试 = new javax.swing.JCheckBox();
        禁止玩家使用商店 = new javax.swing.JCheckBox();
        无延迟检测 = new javax.swing.JCheckBox();
        定时测谎 = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        锻造系统 = new javax.swing.JCheckBox();
        测谎仪 = new javax.swing.JCheckBox();
        无限BUFF = new javax.swing.JCheckBox();
        物品叠加开关 = new javax.swing.JCheckBox();
        卷轴防爆1 = new javax.swing.JCheckBox();
        学院系统 = new javax.swing.JCheckBox();
        jPanel16 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        数据库用户名 = new javax.swing.JTextField();
        数据库密码 = new javax.swing.JTextField();
        数据库端口 = new javax.swing.JTextField();
        jLabel41 = new javax.swing.JLabel();
        数据库名 = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        数据库IP = new javax.swing.JTextField();
        连接数 = new javax.swing.JTextField();
        jLabel58 = new javax.swing.JLabel();
        jPanel50 = new javax.swing.JPanel();
        jButton15 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        角色封包输出记录 = new javax.swing.JCheckBox();
        商城购买记录 = new javax.swing.JCheckBox();
        聊天记录 = new javax.swing.JCheckBox();
        广播记录 = new javax.swing.JCheckBox();
        伤害输出记录 = new javax.swing.JCheckBox();
        丢物品信息 = new javax.swing.JCheckBox();
        精灵商人出售记录 = new javax.swing.JCheckBox();
        jPanel5 = new javax.swing.JPanel();
        物品叠加开关1 = new javax.swing.JCheckBox();
        物品叠加开关2 = new javax.swing.JCheckBox();
        物品叠加开关3 = new javax.swing.JCheckBox();
        物品叠加开关4 = new javax.swing.JCheckBox();
        物品叠加开关5 = new javax.swing.JCheckBox();
        jPanel8 = new javax.swing.JPanel();
        物品叠加开关6 = new javax.swing.JCheckBox();
        物品叠加开关7 = new javax.swing.JCheckBox();
        jPanel14 = new javax.swing.JPanel();
        物品叠加开关8 = new javax.swing.JCheckBox();
        物品叠加开关9 = new javax.swing.JCheckBox();
        物品叠加开关10 = new javax.swing.JCheckBox();
        jPanel51 = new javax.swing.JPanel();
        jPanel52 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        经验倍率 = new javax.swing.JTextField();
        jLabel48 = new javax.swing.JLabel();
        金币爆率 = new javax.swing.JTextField();
        jLabel49 = new javax.swing.JLabel();
        物品爆率 = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        最大登录角色数限制 = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        登录端口 = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        频道端口 = new javax.swing.JTextField();
        jLabel44 = new javax.swing.JLabel();
        顶部公告 = new javax.swing.JTextField();
        商城端口 = new javax.swing.JTextField();
        家族金币 = new javax.swing.JTextField();
        jLabel57 = new javax.swing.JLabel();
        频道最大人数 = new javax.swing.JTextField();
        频道状态 = new javax.swing.JTextField();
        jLabel51 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        人物灌水比例 = new javax.swing.JTextField();
        雇佣时间 = new javax.swing.JTextField();
        jLabel46 = new javax.swing.JLabel();
        潜能几率 = new javax.swing.JTextField();
        最大能力值 = new javax.swing.JTextField();
        jLabel47 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        加载事件 = new javax.swing.JTextField();
        最大创建角色数 = new javax.swing.JTextField();
        人物灌水比例1 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        最大角色 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        频道总数 = new javax.swing.JTextField();
        高级魔方几率 = new javax.swing.JTextField();
        jLabel60 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        物品叠加数量 = new javax.swing.JTextField();
        开店额外经验 = new javax.swing.JTextField();
        jLabel62 = new javax.swing.JLabel();
        结婚额外经验 = new javax.swing.JTextField();
        jLabel63 = new javax.swing.JLabel();
        家族徽章费用 = new javax.swing.JTextField();
        jLabel64 = new javax.swing.JLabel();
        清理怪物时间 = new javax.swing.JTextField();
        jLabel70 = new javax.swing.JLabel();
        jPanel83 = new javax.swing.JPanel();
        jLabel87 = new javax.swing.JLabel();
        jLabel88 = new javax.swing.JLabel();
        jButton10 = new javax.swing.JButton();
        jScrollPane9 = new javax.swing.JScrollPane();
        地图列表ID = new javax.swing.JTextArea();
        刷怪开关1 = new javax.swing.JCheckBox();
        自定义地图刷怪倍数1 = new javax.swing.JTextField();
        jPanel53 = new javax.swing.JPanel();
        jButton17 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel66 = new javax.swing.JLabel();
        ExpBound6 = new javax.swing.JTextField();
        ExpBound5 = new javax.swing.JTextField();
        ExpBound4 = new javax.swing.JTextField();
        ExpBound3 = new javax.swing.JTextField();
        ExpBound2 = new javax.swing.JTextField();
        ExpBound1 = new javax.swing.JTextField();
        jLabel65 = new javax.swing.JLabel();
        jLabel71 = new javax.swing.JLabel();
        jLabel69 = new javax.swing.JLabel();
        jLabel68 = new javax.swing.JLabel();
        jLabel67 = new javax.swing.JLabel();
        jPanel93 = new javax.swing.JPanel();
        jPanel74 = new javax.swing.JPanel();
        蓝蜗牛开关 = new javax.swing.JButton();
        蘑菇仔开关 = new javax.swing.JButton();
        绿水灵开关 = new javax.swing.JButton();
        漂漂猪开关 = new javax.swing.JButton();
        小青蛇开关 = new javax.swing.JButton();
        红螃蟹开关 = new javax.swing.JButton();
        大海龟开关 = new javax.swing.JButton();
        章鱼怪开关 = new javax.swing.JButton();
        顽皮猴开关 = new javax.swing.JButton();
        星精灵开关 = new javax.swing.JButton();
        胖企鹅开关 = new javax.swing.JButton();
        白雪人开关 = new javax.swing.JButton();
        石头人开关 = new javax.swing.JButton();
        紫色猫开关 = new javax.swing.JButton();
        大灰狼开关 = new javax.swing.JButton();
        喷火龙开关 = new javax.swing.JButton();
        火野猪开关 = new javax.swing.JButton();
        小白兔开关 = new javax.swing.JButton();
        青鳄鱼开关 = new javax.swing.JButton();
        花蘑菇开关 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();
        jPanel66 = new javax.swing.JPanel();
        jPanel72 = new javax.swing.JPanel();
        禁止登陆开关 = new javax.swing.JButton();
        滚动公告开关 = new javax.swing.JButton();
        玩家聊天开关 = new javax.swing.JButton();
        游戏升级快讯 = new javax.swing.JButton();
        丢出金币开关 = new javax.swing.JButton();
        丢出物品开关 = new javax.swing.JButton();
        游戏指令开关 = new javax.swing.JButton();
        上线提醒开关 = new javax.swing.JButton();
        回收地图开关 = new javax.swing.JButton();
        管理隐身开关 = new javax.swing.JButton();
        管理加速开关 = new javax.swing.JButton();
        游戏喇叭开关 = new javax.swing.JButton();
        玩家交易开关 = new javax.swing.JButton();
        雇佣商人开关 = new javax.swing.JButton();
        欢迎弹窗开关 = new javax.swing.JButton();
        登陆帮助开关 = new javax.swing.JButton();
        越级打怪开关 = new javax.swing.JButton();
        怪物状态开关 = new javax.swing.JButton();
        地图名称开关 = new javax.swing.JButton();
        过图存档开关 = new javax.swing.JButton();
        指令通知开关 = new javax.swing.JButton();
        游戏仓库开关 = new javax.swing.JButton();
        脚本显码开关 = new javax.swing.JButton();
        拍卖行开关 = new javax.swing.JButton();
        游戏找人开关 = new javax.swing.JButton();
        机器多开开关 = new javax.swing.JButton();
        IP多开开关 = new javax.swing.JButton();
        挂机检测开关 = new javax.swing.JButton();
        全屏检测开关 = new javax.swing.JButton();
        吸怪检测开关 = new javax.swing.JButton();
        段数检测开关 = new javax.swing.JButton();
        群攻检测开关 = new javax.swing.JButton();
        加速检测开关 = new javax.swing.JButton();
        捡物检测开关 = new javax.swing.JButton();
        jPanel23 = new javax.swing.JPanel();
        jPanel68 = new javax.swing.JPanel();
        jPanel69 = new javax.swing.JPanel();
        开启双倍经验 = new javax.swing.JButton();
        双倍经验持续时间 = new javax.swing.JTextField();
        jLabel359 = new javax.swing.JLabel();
        开启双倍爆率 = new javax.swing.JButton();
        双倍爆率持续时间 = new javax.swing.JTextField();
        jLabel360 = new javax.swing.JLabel();
        开启双倍金币 = new javax.swing.JButton();
        双倍金币持续时间 = new javax.swing.JTextField();
        jLabel361 = new javax.swing.JLabel();
        jPanel70 = new javax.swing.JPanel();
        开启三倍经验 = new javax.swing.JButton();
        三倍经验持续时间 = new javax.swing.JTextField();
        jLabel362 = new javax.swing.JLabel();
        开启三倍爆率 = new javax.swing.JButton();
        三倍爆率持续时间 = new javax.swing.JTextField();
        jLabel348 = new javax.swing.JLabel();
        开启三倍金币 = new javax.swing.JButton();
        三倍金币持续时间 = new javax.swing.JTextField();
        jLabel349 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane134 = new javax.swing.JScrollPane();
        在线泡点设置 = new javax.swing.JTable();
        泡点序号 = new javax.swing.JTextField();
        泡点类型 = new javax.swing.JTextField();
        泡点值 = new javax.swing.JTextField();
        泡点值修改 = new javax.swing.JButton();
        jLabel322 = new javax.swing.JLabel();
        jLabel327 = new javax.swing.JLabel();
        jPanel75 = new javax.swing.JPanel();
        泡点金币开关 = new javax.swing.JButton();
        泡点经验开关 = new javax.swing.JButton();
        泡点点券开关 = new javax.swing.JButton();
        泡点抵用开关 = new javax.swing.JButton();
        泡点豆豆开关 = new javax.swing.JButton();
        jLabel328 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jScrollPane135 = new javax.swing.JScrollPane();
        在线泡点设置1 = new javax.swing.JTable();
        泡点序号1 = new javax.swing.JTextField();
        泡点类型1 = new javax.swing.JTextField();
        泡点值1 = new javax.swing.JTextField();
        泡点值修改1 = new javax.swing.JButton();
        jLabel323 = new javax.swing.JLabel();
        jLabel329 = new javax.swing.JLabel();
        jPanel76 = new javax.swing.JPanel();
        泡点金币开关1 = new javax.swing.JButton();
        泡点经验开关1 = new javax.swing.JButton();
        泡点点券开关1 = new javax.swing.JButton();
        泡点抵用开关1 = new javax.swing.JButton();
        泡点豆豆开关1 = new javax.swing.JButton();
        jLabel330 = new javax.swing.JLabel();
        jLabel78 = new javax.swing.JLabel();
        jLabel79 = new javax.swing.JLabel();
        jLabel80 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        jScrollPane136 = new javax.swing.JScrollPane();
        离线泡点设置 = new javax.swing.JTable();
        离线泡点序号 = new javax.swing.JTextField();
        离线泡点类型 = new javax.swing.JTextField();
        离线泡点值 = new javax.swing.JTextField();
        离线泡点值修改 = new javax.swing.JButton();
        jLabel324 = new javax.swing.JLabel();
        jLabel331 = new javax.swing.JLabel();
        jPanel77 = new javax.swing.JPanel();
        离线泡点金币开关 = new javax.swing.JButton();
        离线泡点经验开关 = new javax.swing.JButton();
        离线泡点点券开关 = new javax.swing.JButton();
        离线泡点抵用开关 = new javax.swing.JButton();
        离线泡点豆豆开关 = new javax.swing.JButton();
        离线挂机开关 = new javax.swing.JButton();
        jLabel332 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel22 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        sendNotice = new javax.swing.JButton();
        sendWinNotice = new javax.swing.JButton();
        sendMsgNotice = new javax.swing.JButton();
        sendNpcTalkNotice = new javax.swing.JButton();
        noticeText = new javax.swing.JTextField();
        jLabel117 = new javax.swing.JLabel();
        jLabel118 = new javax.swing.JLabel();
        jLabel119 = new javax.swing.JLabel();
        jLabel106 = new javax.swing.JLabel();
        公告发布喇叭代码 = new javax.swing.JTextField();
        jButton45 = new javax.swing.JButton();
        jLabel259 = new javax.swing.JLabel();
        jPanel25 = new javax.swing.JPanel();
        账号提示语言 = new javax.swing.JLabel();
        显示在线账号 = new javax.swing.JLabel();
        解卡 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        账号信息 = new javax.swing.JTable();
        jPanel38 = new javax.swing.JPanel();
        抵用 = new javax.swing.JTextField();
        账号 = new javax.swing.JTextField();
        点券 = new javax.swing.JTextField();
        jLabel55 = new javax.swing.JLabel();
        jLabel131 = new javax.swing.JLabel();
        修改账号点券抵用 = new javax.swing.JButton();
        账号ID = new javax.swing.JTextField();
        jLabel206 = new javax.swing.JLabel();
        jLabel312 = new javax.swing.JLabel();
        管理1 = new javax.swing.JTextField();
        jLabel353 = new javax.swing.JLabel();
        QQ = new javax.swing.JTextField();
        jLabel357 = new javax.swing.JLabel();
        jButton12 = new javax.swing.JButton();
        刷新账号信息 = new javax.swing.JButton();
        账号操作 = new javax.swing.JTextField();
        离线账号 = new javax.swing.JButton();
        解封 = new javax.swing.JButton();
        jPanel39 = new javax.swing.JPanel();
        注册的账号 = new javax.swing.JTextField();
        注册的密码 = new javax.swing.JTextField();
        jButton35 = new javax.swing.JButton();
        jLabel111 = new javax.swing.JLabel();
        jLabel201 = new javax.swing.JLabel();
        jButton30 = new javax.swing.JButton();
        已封账号 = new javax.swing.JButton();
        在线账号 = new javax.swing.JButton();
        删除账号 = new javax.swing.JButton();
        封锁账号 = new javax.swing.JButton();
        jPanel26 = new javax.swing.JPanel();
        jTabbedPane8 = new javax.swing.JTabbedPane();
        角色信息1 = new javax.swing.JPanel();
        jScrollPane10 = new javax.swing.JScrollPane();
        角色信息 = new javax.swing.JTable();
        刷新角色信息 = new javax.swing.JButton();
        显示管理角色 = new javax.swing.JButton();
        jButton38 = new javax.swing.JButton();
        删除角色 = new javax.swing.JButton();
        角色昵称 = new javax.swing.JTextField();
        等级 = new javax.swing.JTextField();
        力量 = new javax.swing.JTextField();
        敏捷 = new javax.swing.JTextField();
        智力 = new javax.swing.JTextField();
        运气 = new javax.swing.JTextField();
        HP = new javax.swing.JTextField();
        MP = new javax.swing.JTextField();
        金币1 = new javax.swing.JTextField();
        地图 = new javax.swing.JTextField();
        GM = new javax.swing.JTextField();
        jLabel182 = new javax.swing.JLabel();
        jLabel183 = new javax.swing.JLabel();
        jLabel184 = new javax.swing.JLabel();
        jLabel185 = new javax.swing.JLabel();
        jLabel186 = new javax.swing.JLabel();
        jLabel187 = new javax.swing.JLabel();
        jLabel189 = new javax.swing.JLabel();
        jLabel190 = new javax.swing.JLabel();
        jLabel191 = new javax.swing.JLabel();
        jLabel192 = new javax.swing.JLabel();
        jLabel193 = new javax.swing.JLabel();
        角色ID = new javax.swing.JTextField();
        卡号自救1 = new javax.swing.JButton();
        卡号自救2 = new javax.swing.JButton();
        jLabel203 = new javax.swing.JLabel();
        查看技能 = new javax.swing.JButton();
        查看背包 = new javax.swing.JButton();
        卡家族解救 = new javax.swing.JButton();
        脸型 = new javax.swing.JTextField();
        发型 = new javax.swing.JTextField();
        jLabel214 = new javax.swing.JLabel();
        离线角色 = new javax.swing.JButton();
        在线角色 = new javax.swing.JButton();
        显示在线玩家 = new javax.swing.JLabel();
        jLabel194 = new javax.swing.JLabel();
        角色背包 = new javax.swing.JPanel();
        jTabbedPane5 = new javax.swing.JTabbedPane();
        jPanel40 = new javax.swing.JPanel();
        jScrollPane15 = new javax.swing.JScrollPane();
        角色背包穿戴 = new javax.swing.JTable();
        背包物品名字1 = new javax.swing.JTextField();
        身上穿戴序号1 = new javax.swing.JTextField();
        背包物品代码1 = new javax.swing.JTextField();
        jLabel276 = new javax.swing.JLabel();
        jLabel283 = new javax.swing.JLabel();
        jLabel287 = new javax.swing.JLabel();
        删除穿戴装备 = new javax.swing.JButton();
        jPanel41 = new javax.swing.JPanel();
        jScrollPane16 = new javax.swing.JScrollPane();
        角色装备背包 = new javax.swing.JTable();
        装备背包物品名字 = new javax.swing.JTextField();
        装备背包物品序号 = new javax.swing.JTextField();
        装备背包物品代码 = new javax.swing.JTextField();
        jLabel288 = new javax.swing.JLabel();
        jLabel289 = new javax.swing.JLabel();
        jLabel290 = new javax.swing.JLabel();
        删除装备背包 = new javax.swing.JButton();
        jPanel42 = new javax.swing.JPanel();
        jScrollPane17 = new javax.swing.JScrollPane();
        角色消耗背包 = new javax.swing.JTable();
        消耗背包物品名字 = new javax.swing.JTextField();
        消耗背包物品序号 = new javax.swing.JTextField();
        消耗背包物品代码 = new javax.swing.JTextField();
        jLabel291 = new javax.swing.JLabel();
        jLabel292 = new javax.swing.JLabel();
        jLabel293 = new javax.swing.JLabel();
        删除消耗背包 = new javax.swing.JButton();
        jPanel43 = new javax.swing.JPanel();
        jScrollPane18 = new javax.swing.JScrollPane();
        角色设置背包 = new javax.swing.JTable();
        设置背包物品名字 = new javax.swing.JTextField();
        设置背包物品序号 = new javax.swing.JTextField();
        设置背包物品代码 = new javax.swing.JTextField();
        jLabel294 = new javax.swing.JLabel();
        jLabel295 = new javax.swing.JLabel();
        jLabel296 = new javax.swing.JLabel();
        删除设置背包 = new javax.swing.JButton();
        jPanel44 = new javax.swing.JPanel();
        jScrollPane19 = new javax.swing.JScrollPane();
        角色其他背包 = new javax.swing.JTable();
        其他背包物品名字 = new javax.swing.JTextField();
        其他背包物品序号 = new javax.swing.JTextField();
        其他背包物品代码 = new javax.swing.JTextField();
        jLabel297 = new javax.swing.JLabel();
        jLabel298 = new javax.swing.JLabel();
        jLabel299 = new javax.swing.JLabel();
        删除其他背包 = new javax.swing.JButton();
        jPanel45 = new javax.swing.JPanel();
        jScrollPane20 = new javax.swing.JScrollPane();
        角色特殊背包 = new javax.swing.JTable();
        特殊背包物品名字 = new javax.swing.JTextField();
        特殊背包物品序号 = new javax.swing.JTextField();
        特殊背包物品代码 = new javax.swing.JTextField();
        jLabel300 = new javax.swing.JLabel();
        jLabel301 = new javax.swing.JLabel();
        jLabel302 = new javax.swing.JLabel();
        删除特殊背包 = new javax.swing.JButton();
        jPanel46 = new javax.swing.JPanel();
        jScrollPane21 = new javax.swing.JScrollPane();
        角色游戏仓库 = new javax.swing.JTable();
        游戏仓库物品名字 = new javax.swing.JTextField();
        游戏仓库物品序号 = new javax.swing.JTextField();
        游戏仓库物品代码 = new javax.swing.JTextField();
        jLabel303 = new javax.swing.JLabel();
        jLabel304 = new javax.swing.JLabel();
        jLabel305 = new javax.swing.JLabel();
        删除游戏仓库 = new javax.swing.JButton();
        jPanel47 = new javax.swing.JPanel();
        jScrollPane22 = new javax.swing.JScrollPane();
        角色商城仓库 = new javax.swing.JTable();
        商城仓库物品名字 = new javax.swing.JTextField();
        商城仓库物品序号 = new javax.swing.JTextField();
        商城仓库物品代码 = new javax.swing.JTextField();
        jLabel306 = new javax.swing.JLabel();
        jLabel307 = new javax.swing.JLabel();
        jLabel308 = new javax.swing.JLabel();
        删除商城仓库 = new javax.swing.JButton();
        jPanel48 = new javax.swing.JPanel();
        jScrollPane30 = new javax.swing.JScrollPane();
        角色点券拍卖行 = new javax.swing.JTable();
        拍卖行物品名字1 = new javax.swing.JTextField();
        角色点券拍卖行序号 = new javax.swing.JTextField();
        拍卖行物品代码1 = new javax.swing.JTextField();
        jLabel354 = new javax.swing.JLabel();
        jLabel355 = new javax.swing.JLabel();
        jLabel356 = new javax.swing.JLabel();
        删除拍卖行1 = new javax.swing.JButton();
        jPanel56 = new javax.swing.JPanel();
        jScrollPane23 = new javax.swing.JScrollPane();
        角色金币拍卖行 = new javax.swing.JTable();
        拍卖行物品名字 = new javax.swing.JTextField();
        角色金币拍卖行序号 = new javax.swing.JTextField();
        拍卖行物品代码 = new javax.swing.JTextField();
        jLabel309 = new javax.swing.JLabel();
        jLabel310 = new javax.swing.JLabel();
        jLabel311 = new javax.swing.JLabel();
        删除拍卖行 = new javax.swing.JButton();
        技能 = new javax.swing.JPanel();
        jScrollPane14 = new javax.swing.JScrollPane();
        技能信息 = new javax.swing.JTable();
        技能代码 = new javax.swing.JTextField();
        技能目前等级 = new javax.swing.JTextField();
        技能最高等级 = new javax.swing.JTextField();
        技能名字 = new javax.swing.JTextField();
        jLabel86 = new javax.swing.JLabel();
        jLabel89 = new javax.swing.JLabel();
        jLabel107 = new javax.swing.JLabel();
        修改技能 = new javax.swing.JButton();
        技能序号 = new javax.swing.JTextField();
        jLabel188 = new javax.swing.JLabel();
        jLabel204 = new javax.swing.JLabel();
        jLabel205 = new javax.swing.JLabel();
        删除技能 = new javax.swing.JButton();
        修改技能1 = new javax.swing.JButton();
        jPanel27 = new javax.swing.JPanel();
        jPanel57 = new javax.swing.JPanel();
        全服发送物品数量 = new javax.swing.JTextField();
        全服发送物品代码 = new javax.swing.JTextField();
        给予物品1 = new javax.swing.JButton();
        jLabel217 = new javax.swing.JLabel();
        jLabel234 = new javax.swing.JLabel();
        jPanel80 = new javax.swing.JPanel();
        z7 = new javax.swing.JButton();
        z8 = new javax.swing.JButton();
        z9 = new javax.swing.JButton();
        z10 = new javax.swing.JButton();
        z11 = new javax.swing.JButton();
        z12 = new javax.swing.JButton();
        a2 = new javax.swing.JTextField();
        jLabel236 = new javax.swing.JLabel();
        个人发送物品玩家名字1 = new javax.swing.JTextField();
        jLabel59 = new javax.swing.JLabel();
        jPanel60 = new javax.swing.JPanel();
        个人发送物品数量 = new javax.swing.JTextField();
        个人发送物品玩家名字 = new javax.swing.JTextField();
        个人发送物品代码 = new javax.swing.JTextField();
        给予物品 = new javax.swing.JButton();
        jLabel240 = new javax.swing.JLabel();
        jLabel241 = new javax.swing.JLabel();
        jLabel242 = new javax.swing.JLabel();
        jPanel58 = new javax.swing.JPanel();
        全服发送装备装备加卷 = new javax.swing.JTextField();
        全服发送装备装备制作人 = new javax.swing.JTextField();
        全服发送装备装备力量 = new javax.swing.JTextField();
        全服发送装备装备MP = new javax.swing.JTextField();
        全服发送装备装备智力 = new javax.swing.JTextField();
        全服发送装备装备运气 = new javax.swing.JTextField();
        全服发送装备装备HP = new javax.swing.JTextField();
        全服发送装备装备攻击力 = new javax.swing.JTextField();
        全服发送装备装备给予时间 = new javax.swing.JTextField();
        全服发送装备装备可否交易 = new javax.swing.JTextField();
        全服发送装备装备敏捷 = new javax.swing.JTextField();
        全服发送装备物品ID = new javax.swing.JTextField();
        全服发送装备装备魔法力 = new javax.swing.JTextField();
        全服发送装备装备魔法防御 = new javax.swing.JTextField();
        全服发送装备装备物理防御 = new javax.swing.JTextField();
        给予装备1 = new javax.swing.JButton();
        jLabel219 = new javax.swing.JLabel();
        jLabel220 = new javax.swing.JLabel();
        jLabel221 = new javax.swing.JLabel();
        jLabel222 = new javax.swing.JLabel();
        jLabel223 = new javax.swing.JLabel();
        jLabel224 = new javax.swing.JLabel();
        jLabel225 = new javax.swing.JLabel();
        jLabel226 = new javax.swing.JLabel();
        jLabel227 = new javax.swing.JLabel();
        jLabel228 = new javax.swing.JLabel();
        jLabel229 = new javax.swing.JLabel();
        jLabel230 = new javax.swing.JLabel();
        jLabel231 = new javax.swing.JLabel();
        jLabel232 = new javax.swing.JLabel();
        jLabel233 = new javax.swing.JLabel();
        发送装备玩家姓名 = new javax.swing.JTextField();
        给予装备2 = new javax.swing.JButton();
        jLabel246 = new javax.swing.JLabel();
        jLabel244 = new javax.swing.JLabel();
        全服发送装备装备潜能3 = new javax.swing.JTextField();
        全服发送装备装备潜能1 = new javax.swing.JTextField();
        全服发送装备装备潜能2 = new javax.swing.JTextField();
        jLabel72 = new javax.swing.JLabel();
        jLabel73 = new javax.swing.JLabel();
        jLabel74 = new javax.swing.JLabel();
        qiannengdaima = new javax.swing.JButton();
        jPanel59 = new javax.swing.JPanel();
        z2 = new javax.swing.JButton();
        z3 = new javax.swing.JButton();
        z1 = new javax.swing.JButton();
        z4 = new javax.swing.JButton();
        z5 = new javax.swing.JButton();
        z6 = new javax.swing.JButton();
        a1 = new javax.swing.JTextField();
        jLabel235 = new javax.swing.JLabel();
        jPanel35 = new javax.swing.JPanel();
        jPanel54 = new javax.swing.JPanel();
        jButton69 = new javax.swing.JButton();
        jButton70 = new javax.swing.JButton();
        jButton72 = new javax.swing.JButton();
        jButton73 = new javax.swing.JButton();
        jButton74 = new javax.swing.JButton();
        jButton75 = new javax.swing.JButton();
        jPanel55 = new javax.swing.JPanel();
        jButton31 = new javax.swing.JButton();
        jButton22 = new javax.swing.JButton();
        jButton23 = new javax.swing.JButton();
        jButton28 = new javax.swing.JButton();
        jButton29 = new javax.swing.JButton();
        jButton39 = new javax.swing.JButton();
        jButton44 = new javax.swing.JButton();
        jButton51 = new javax.swing.JButton();
        jButton53 = new javax.swing.JButton();
        jPanel67 = new javax.swing.JPanel();
        jButton25 = new javax.swing.JButton();
        jButton32 = new javax.swing.JButton();
        jButton33 = new javax.swing.JButton();
        jButton34 = new javax.swing.JButton();
        jButton40 = new javax.swing.JButton();
        jButton41 = new javax.swing.JButton();
        jButton42 = new javax.swing.JButton();
        jButton43 = new javax.swing.JButton();
        jButton46 = new javax.swing.JButton();
        jButton47 = new javax.swing.JButton();
        jButton48 = new javax.swing.JButton();
        jButton49 = new javax.swing.JButton();
        jButton36 = new javax.swing.JButton();
        jButton37 = new javax.swing.JButton();
        jButton50 = new javax.swing.JButton();
        jButton52 = new javax.swing.JButton();
        jPanel36 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        ActiveThread = new javax.swing.JLabel();
        RunStats = new javax.swing.JLabel();
        RunTime = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jTabbedPane.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        jTabbedPane.setFont(new java.awt.Font("微软雅黑", 0, 18)); // NOI18N

        consoleInfo.setColumns(20);
        consoleInfo.setFont(new java.awt.Font("宋体", 0, 13)); // NOI18N
        consoleInfo.setForeground(new java.awt.Color(51, 51, 51));
        consoleInfo.setLineWrap(true);
        consoleInfo.setToolTipText("");
        consoleInfo.setDoubleBuffered(true);
        consoleInfo.setInheritsPopupMenu(true);
        consoleInfo.setSelectedTextColor(new java.awt.Color(51, 0, 51));
        jScrollPane2.setViewportView(consoleInfo);

        javax.swing.GroupLayout jPanel28Layout = new javax.swing.GroupLayout(jPanel28);
        jPanel28.setLayout(jPanel28Layout);
        jPanel28Layout.setHorizontalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 978, Short.MAX_VALUE)
        );
        jPanel28Layout.setVerticalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE)
        );

        jTabbedPane2.addTab("全部", new javax.swing.ImageIcon(getClass().getResource("/Image/全部日志.png")), jPanel28); // NOI18N

        output_packet_jTextPane.setEditable(false);
        jScrollPane5.setViewportView(output_packet_jTextPane);

        javax.swing.GroupLayout jPanel29Layout = new javax.swing.GroupLayout(jPanel29);
        jPanel29.setLayout(jPanel29Layout);
        jPanel29Layout.setHorizontalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 978, Short.MAX_VALUE)
        );
        jPanel29Layout.setVerticalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE)
        );

        jTabbedPane2.addTab("数据包", new javax.swing.ImageIcon(getClass().getResource("/Image/信息日志.png")), jPanel29); // NOI18N

        output_notice_jTextPane.setEditable(false);
        jScrollPane7.setViewportView(output_notice_jTextPane);

        javax.swing.GroupLayout jPanel30Layout = new javax.swing.GroupLayout(jPanel30);
        jPanel30.setLayout(jPanel30Layout);
        jPanel30Layout.setHorizontalGroup(
            jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 978, Short.MAX_VALUE)
        );
        jPanel30Layout.setVerticalGroup(
            jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE)
        );

        jTabbedPane2.addTab("提示", new javax.swing.ImageIcon(getClass().getResource("/Image/调试日志.png")), jPanel30); // NOI18N

        output_err_jTextPane.setEditable(false);
        jScrollPane6.setViewportView(output_err_jTextPane);

        javax.swing.GroupLayout jPanel31Layout = new javax.swing.GroupLayout(jPanel31);
        jPanel31.setLayout(jPanel31Layout);
        jPanel31Layout.setHorizontalGroup(
            jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 978, Short.MAX_VALUE)
        );
        jPanel31Layout.setVerticalGroup(
            jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE)
        );

        jTabbedPane2.addTab("报错", new javax.swing.ImageIcon(getClass().getResource("/Image/错误日志.png")), jPanel31); // NOI18N

        output_out_jTextPane.setEditable(false);
        jScrollPane8.setViewportView(output_out_jTextPane);

        javax.swing.GroupLayout jPanel32Layout = new javax.swing.GroupLayout(jPanel32);
        jPanel32.setLayout(jPanel32Layout);
        jPanel32Layout.setHorizontalGroup(
            jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 978, Short.MAX_VALUE)
        );
        jPanel32Layout.setVerticalGroup(
            jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE)
        );

        jTabbedPane2.addTab("其他", new javax.swing.ImageIcon(getClass().getResource("/Image/警告日志.png")), jPanel32); // NOI18N

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        重载副本按钮.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        重载副本按钮.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/3801309.png"))); // NOI18N
        重载副本按钮.setText("重载副本");
        重载副本按钮.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                重载副本按钮ActionPerformed(evt);
            }
        });

        重载爆率按钮.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        重载爆率按钮.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/3801310.png"))); // NOI18N
        重载爆率按钮.setText("重载爆率");
        重载爆率按钮.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                重载爆率按钮ActionPerformed(evt);
            }
        });

        重载传送门按钮.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        重载传送门按钮.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/3801312.png"))); // NOI18N
        重载传送门按钮.setText("重载传送");
        重载传送门按钮.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                重载传送门按钮ActionPerformed(evt);
            }
        });

        重载商店按钮.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        重载商店按钮.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/3801315.png"))); // NOI18N
        重载商店按钮.setText("重载商店");
        重载商店按钮.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                重载商店按钮ActionPerformed(evt);
            }
        });

        重载包头按钮.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        重载包头按钮.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/3801314.png"))); // NOI18N
        重载包头按钮.setText("重载包头");
        重载包头按钮.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                重载包头按钮ActionPerformed(evt);
            }
        });

        重载任务.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        重载任务.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/3801313.png"))); // NOI18N
        重载任务.setText("重载任务");
        重载任务.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                重载任务ActionPerformed(evt);
            }
        });

        重载反应堆按钮.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        重载反应堆按钮.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/3801462.png"))); // NOI18N
        重载反应堆按钮.setText("重载反应堆");
        重载反应堆按钮.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                重载反应堆按钮ActionPerformed(evt);
            }
        });

        重载商城按钮.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        重载商城按钮.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/3801316.png"))); // NOI18N
        重载商城按钮.setText("重载商城");
        重载商城按钮.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                重载商城按钮ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(62, 62, 62)
                .addComponent(重载副本按钮, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(重载爆率按钮, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(重载传送门按钮, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(重载任务)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(重载包头按钮)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(重载商店按钮)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(重载商城按钮)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(重载反应堆按钮)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(重载商城按钮, javax.swing.GroupLayout.Alignment.CENTER, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(重载反应堆按钮, javax.swing.GroupLayout.Alignment.CENTER, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(重载任务, javax.swing.GroupLayout.Alignment.CENTER, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(重载包头按钮, javax.swing.GroupLayout.Alignment.CENTER, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(重载商店按钮, javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(重载传送门按钮, javax.swing.GroupLayout.Alignment.CENTER, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(重载爆率按钮, javax.swing.GroupLayout.Alignment.CENTER, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(重载副本按钮, javax.swing.GroupLayout.Alignment.CENTER, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel15.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        保存雇佣按钮.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        保存雇佣按钮.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/123.png"))); // NOI18N
        保存雇佣按钮.setText("保存雇佣");
        保存雇佣按钮.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                保存雇佣按钮ActionPerformed(evt);
            }
        });

        保存数据按钮.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        保存数据按钮.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/123.png"))); // NOI18N
        保存数据按钮.setText("保存数据");
        保存数据按钮.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                保存数据按钮ActionPerformed(evt);
            }
        });

        查询在线玩家人数按钮.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        查询在线玩家人数按钮.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/111.png"))); // NOI18N
        查询在线玩家人数按钮.setText("查询在线玩家人数");
        查询在线玩家人数按钮.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                查询在线玩家人数按钮ActionPerformed(evt);
            }
        });

        jButton1.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 0, 0));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/开启.png"))); // NOI18N
        jButton1.setText("开启服务端");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton21.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jButton21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/关闭服务器.png"))); // NOI18N
        jButton21.setText("关闭服务端");
        jButton21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton21ActionPerformed(evt);
            }
        });

        状态信息.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        状态信息.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/98.png"))); // NOI18N
        状态信息.setText("状态");

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(保存雇佣按钮, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(保存数据按钮)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(查询在线玩家人数按钮)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(进度条, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel15Layout.createSequentialGroup()
                                .addComponent(状态信息)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(12, 12, 12))
                    .addComponent(进度条1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(查询在线玩家人数按钮, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(保存数据按钮, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
                            .addComponent(保存雇佣按钮, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton1)
                            .addComponent(状态信息))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton21)
                            .addComponent(进度条, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(7, 7, 7)
                .addComponent(进度条1, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane2)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel15, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 485, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane.addTab("启动服务", new javax.swing.ImageIcon(getClass().getResource("/Image/Icon.png")), jPanel20); // NOI18N

        jTabbedPane6.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane6.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N

        jPanel49.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "系统设置", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("微软雅黑", 0, 12))); // NOI18N

        jLabel23.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jLabel23.setText("盛大IP地址");

        jLabel19.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jLabel19.setText("游戏名字");

        游戏IP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                游戏IPActionPerformed(evt);
            }
        });

        jLabel52.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jLabel52.setText("服务端版本");

        jTextField22.setEditable(false);
        jTextField22.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jTextField22.setText(String.valueOf(ServerConstants.MAPLE_VERSION) + "." + ServerConstants.MAPLE_PATCH);
        jTextField22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField22ActionPerformed(evt);
            }
        });

        jComboBox1.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jComboBox1.setModel(getMapleTypeModel());
        jComboBox1.setEnabled(false);

        jLabel54.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jLabel54.setText("所在国家/地区");

        测试机.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        测试机.setText("测试机");

        日志模式.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        日志模式.setText("日志模式");

        自动注册.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        自动注册.setText("自动注册");

        jLabel31.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jLabel31.setText("服务端WZ路径");

        debug模式.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        debug模式.setText("Debug模式");
        debug模式.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                debug模式ActionPerformed(evt);
            }
        });

        jLabel26.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jLabel26.setText("事件名字");

        允许使用商城.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        允许使用商城.setText("允许使用商城");

        允许玩家穿戴GM道具.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        允许玩家穿戴GM道具.setText("允许穿戴GM道具");

        管理员模式.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        管理员模式.setText("仅管理员模式");

        检测外挂自动封号.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        检测外挂自动封号.setText("检测外挂自动封号");
        检测外挂自动封号.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                检测外挂自动封号ActionPerformed(evt);
            }
        });

        匿名广播.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        匿名广播.setText("匿名广播");
        匿名广播.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                匿名广播ActionPerformed(evt);
            }
        });

        频道掉线测试.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        频道掉线测试.setText("频道掉线测试");

        禁止玩家使用商店.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        禁止玩家使用商店.setText("禁止使用商店");

        无延迟检测.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        无延迟检测.setText("无延迟检测");

        定时测谎.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        定时测谎.setText("定时测谎");

        javax.swing.GroupLayout jPanel49Layout = new javax.swing.GroupLayout(jPanel49);
        jPanel49.setLayout(jPanel49Layout);
        jPanel49Layout.setHorizontalGroup(
            jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel49Layout.createSequentialGroup()
                .addGroup(jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel49Layout.createSequentialGroup()
                        .addComponent(jLabel52)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField22, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel54)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(游戏名字, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel49Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(禁止玩家使用商店, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(匿名广播, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(日志模式, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(自动注册, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(无延迟检测, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(允许使用商城, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(管理员模式, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(debug模式, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(定时测谎, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel49Layout.createSequentialGroup()
                        .addComponent(jLabel26)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(事件名字, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel23))
                    .addGroup(jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(测试机, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(检测外挂自动封号, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel49Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(频道掉线测试))
                    .addComponent(允许玩家穿戴GM道具)
                    .addGroup(jPanel49Layout.createSequentialGroup()
                        .addComponent(游戏IP, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(wz路径, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel49Layout.setVerticalGroup(
            jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel49Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel52)
                    .addComponent(jTextField22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel54)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(游戏名字, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19)
                    .addComponent(事件名字, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26)
                    .addComponent(jLabel23)
                    .addComponent(游戏IP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31)
                    .addComponent(wz路径, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel49Layout.createSequentialGroup()
                        .addComponent(日志模式, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(匿名广播, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(禁止玩家使用商店, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel49Layout.createSequentialGroup()
                        .addComponent(自动注册, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(允许使用商城, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(无延迟检测, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel49Layout.createSequentialGroup()
                        .addComponent(管理员模式, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(debug模式, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(定时测谎, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel49Layout.createSequentialGroup()
                        .addGroup(jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(测试机, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(允许玩家穿戴GM道具, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(检测外挂自动封号, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(频道掉线测试, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "常用功能", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("微软雅黑", 0, 12))); // NOI18N

        锻造系统.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        锻造系统.setText("锻造系统");
        锻造系统.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                锻造系统ActionPerformed(evt);
            }
        });

        测谎仪.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        测谎仪.setText("测谎仪");
        测谎仪.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                测谎仪ActionPerformed(evt);
            }
        });

        无限BUFF.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        无限BUFF.setText("无限BUFF");
        无限BUFF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                无限BUFFActionPerformed(evt);
            }
        });

        物品叠加开关.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        物品叠加开关.setText("物品叠加");
        物品叠加开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                物品叠加开关ActionPerformed(evt);
            }
        });

        卷轴防爆1.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        卷轴防爆1.setText("卷轴防爆");

        学院系统.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        学院系统.setText("学院系统");
        学院系统.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                学院系统ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(测谎仪, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(物品叠加开关, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(无限BUFF, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(卷轴防爆1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(学院系统)
                    .addComponent(锻造系统))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(物品叠加开关, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(无限BUFF, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(锻造系统, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(测谎仪, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(卷轴防爆1, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(学院系统, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel16.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "数据库设置", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("微软雅黑", 0, 12))); // NOI18N

        jLabel15.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("用戶名");

        jLabel16.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("密码");

        jLabel17.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("端口");

        jLabel41.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jLabel41.setText("数据库");

        jLabel18.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jLabel18.setText("IP");

        jLabel58.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jLabel58.setText("连接数");

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel18)
                .addGap(18, 18, 18)
                .addComponent(数据库IP, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(数据库端口, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel41)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(数据库名, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel58)
                .addGap(13, 13, 13)
                .addComponent(连接数, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(数据库用户名, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(数据库密码, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel18)
                        .addComponent(数据库IP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel17)
                        .addComponent(数据库端口, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel16)
                        .addComponent(数据库密码, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel15)
                        .addComponent(数据库用户名, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel41)
                        .addComponent(数据库名, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(连接数, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel58)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel50.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "保存配置文件", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("微软雅黑", 0, 12))); // NOI18N

        jButton15.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jButton15.setText("保存配置并应用");
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel50Layout = new javax.swing.GroupLayout(jPanel50);
        jPanel50.setLayout(jPanel50Layout);
        jPanel50Layout.setHorizontalGroup(
            jPanel50Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel50Layout.createSequentialGroup()
                .addGap(186, 186, 186)
                .addComponent(jButton15, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel50Layout.setVerticalGroup(
            jPanel50Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel50Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton15, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "日志输出", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("微软雅黑", 0, 12))); // NOI18N

        角色封包输出记录.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        角色封包输出记录.setText("角色封包记录");

        商城购买记录.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        商城购买记录.setText("商城购买记录");

        聊天记录.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        聊天记录.setText("聊天记录");
        聊天记录.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                聊天记录ActionPerformed(evt);
            }
        });

        广播记录.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        广播记录.setText("广播记录");

        伤害输出记录.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        伤害输出记录.setText("伤害输出记录");
        伤害输出记录.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                伤害输出记录ActionPerformed(evt);
            }
        });

        丢物品信息.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        丢物品信息.setText("丢物品信息");

        精灵商人出售记录.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        精灵商人出售记录.setText("精灵商人记录");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(精灵商人出售记录, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(伤害输出记录, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(聊天记录, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(广播记录))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(丢物品信息, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(商城购买记录, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(角色封包输出记录)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(广播记录, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(聊天记录, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(丢物品信息, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(角色封包输出记录, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(商城购买记录, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(精灵商人出售记录, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(伤害输出记录, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "职业开放创建设置", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("微软雅黑", 0, 12))); // NOI18N

        物品叠加开关1.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        物品叠加开关1.setSelected(true);
        物品叠加开关1.setText("冒  险  家");
        物品叠加开关1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                物品叠加开关1ActionPerformed(evt);
            }
        });

        物品叠加开关2.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        物品叠加开关2.setSelected(true);
        物品叠加开关2.setText("暗影双刀");

        物品叠加开关3.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        物品叠加开关3.setSelected(true);
        物品叠加开关3.setText("战  神");

        物品叠加开关4.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        物品叠加开关4.setSelected(true);
        物品叠加开关4.setText("龙  神");

        物品叠加开关5.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        物品叠加开关5.setSelected(true);
        物品叠加开关5.setText("骑 士 团");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(物品叠加开关1)
                .addGap(10, 10, 10)
                .addComponent(物品叠加开关2)
                .addGap(10, 10, 10)
                .addComponent(物品叠加开关3)
                .addGap(10, 10, 10)
                .addComponent(物品叠加开关4)
                .addGap(10, 10, 10)
                .addComponent(物品叠加开关5)
                .addContainerGap(16, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(物品叠加开关1, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(物品叠加开关5)
                    .addComponent(物品叠加开关3)
                    .addComponent(物品叠加开关2)
                    .addComponent(物品叠加开关4))
                .addContainerGap())
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "职业创建顺序界面", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("微软雅黑", 0, 12))); // NOI18N

        物品叠加开关6.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        物品叠加开关6.setSelected(true);
        物品叠加开关6.setText("仿官顺序界面");

        物品叠加开关7.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        物品叠加开关7.setText("特殊顺序界面");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(物品叠加开关6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(物品叠加开关7)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(物品叠加开关6)
                    .addComponent(物品叠加开关7))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel14.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "切换游戏模式", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("微软雅黑", 0, 12))); // NOI18N

        物品叠加开关8.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        物品叠加开关8.setSelected(true);
        物品叠加开关8.setText("自由模式");

        物品叠加开关9.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        物品叠加开关9.setText("故事模式");

        物品叠加开关10.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        物品叠加开关10.setText("战斗模式");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(物品叠加开关8)
                .addGap(18, 18, 18)
                .addComponent(物品叠加开关10)
                .addGap(18, 18, 18)
                .addComponent(物品叠加开关9)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(物品叠加开关8)
                    .addComponent(物品叠加开关9)
                    .addComponent(物品叠加开关10)))
        );

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel50, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel49, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel12Layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel12Layout.createSequentialGroup()
                        .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(721, 721, 721)))
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addComponent(jPanel49, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 61, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, 61, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel50, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(104, 104, 104))
        );

        jTabbedPane6.addTab("系统设置", new javax.swing.ImageIcon(getClass().getResource("/Image/设置.png")), jPanel12); // NOI18N

        jPanel52.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "游戏设置", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("微软雅黑", 0, 12))); // NOI18N

        jLabel22.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jLabel22.setText("经验倍率");

        经验倍率.setEnabled(false);

        jLabel48.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jLabel48.setText("金币爆率");

        jLabel49.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jLabel49.setText("物品爆率");

        jLabel25.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jLabel25.setText("世界服务器最大人数");

        jLabel27.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jLabel27.setText("登录端口");

        jLabel28.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jLabel28.setText("频道端口");

        jLabel44.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jLabel44.setText("商城端口");

        顶部公告.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N

        jLabel57.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jLabel57.setText("创建家族费用");

        jLabel51.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jLabel51.setText("頻道显示最大角色数");

        jLabel24.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jLabel24.setText("頻道总数");

        jLabel46.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jLabel46.setText("雇佣时间");

        jLabel47.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jLabel47.setText("最大属性");

        jLabel53.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jLabel53.setText("潜能几率");

        jLabel50.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jLabel50.setText("频道状态");

        加载事件.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N

        人物灌水比例1.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        人物灌水比例1.setText("人物灌水比例");

        jLabel20.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jLabel20.setText("加载事件");

        最大角色.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        最大角色.setText("最大创建角色数");

        jLabel21.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jLabel21.setText("顶部公告");

        jLabel60.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jLabel60.setText("高级魔方几率");

        jLabel61.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jLabel61.setText("物品叠加数量");

        jLabel62.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jLabel62.setText("开店额外经验");

        jLabel63.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jLabel63.setText("结婚额外经验");

        jLabel64.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jLabel64.setText("家族徽章费用");

        清理怪物时间.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                清理怪物时间ActionPerformed(evt);
            }
        });

        jLabel70.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jLabel70.setText("清理无人地图怪物");

        javax.swing.GroupLayout jPanel52Layout = new javax.swing.GroupLayout(jPanel52);
        jPanel52.setLayout(jPanel52Layout);
        jPanel52Layout.setHorizontalGroup(
            jPanel52Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel52Layout.createSequentialGroup()
                .addGroup(jPanel52Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel52Layout.createSequentialGroup()
                        .addComponent(jLabel22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(经验倍率, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel48)
                        .addGap(8, 8, 8)
                        .addComponent(金币爆率, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel49)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(物品爆率, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel52Layout.createSequentialGroup()
                        .addGroup(jPanel52Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel52Layout.createSequentialGroup()
                                .addComponent(jLabel25)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(最大登录角色数限制, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel52Layout.createSequentialGroup()
                                .addComponent(jLabel51)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(频道最大人数, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel52Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(人物灌水比例1)
                            .addComponent(最大角色))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel52Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(最大创建角色数)
                            .addComponent(人物灌水比例, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel52Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel52Layout.createSequentialGroup()
                                .addComponent(jLabel50)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(频道状态, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel52Layout.createSequentialGroup()
                                .addComponent(jLabel24)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(频道总数, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(0, 106, Short.MAX_VALUE))
            .addGroup(jPanel52Layout.createSequentialGroup()
                .addGroup(jPanel52Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel52Layout.createSequentialGroup()
                        .addGroup(jPanel52Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel52Layout.createSequentialGroup()
                                .addComponent(jLabel28)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(频道端口, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel52Layout.createSequentialGroup()
                                .addComponent(jLabel27)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(登录端口, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel52Layout.createSequentialGroup()
                                .addComponent(jLabel44)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(商城端口, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel52Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel52Layout.createSequentialGroup()
                                .addGroup(jPanel52Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel52Layout.createSequentialGroup()
                                        .addComponent(jLabel46)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(雇佣时间, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel52Layout.createSequentialGroup()
                                        .addComponent(jLabel47)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(最大能力值)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel52Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel52Layout.createSequentialGroup()
                                        .addComponent(jLabel61)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(物品叠加数量, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel52Layout.createSequentialGroup()
                                        .addComponent(jLabel60)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(高级魔方几率, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(jPanel52Layout.createSequentialGroup()
                                .addComponent(jLabel53)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(潜能几率, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel52Layout.createSequentialGroup()
                        .addGroup(jPanel52Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel20))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel52Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(加载事件, javax.swing.GroupLayout.DEFAULT_SIZE, 463, Short.MAX_VALUE)
                            .addComponent(顶部公告)))
                    .addGroup(jPanel52Layout.createSequentialGroup()
                        .addGroup(jPanel52Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel57)
                            .addComponent(jLabel64))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel52Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(家族金币)
                            .addComponent(家族徽章费用, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel52Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel52Layout.createSequentialGroup()
                                .addComponent(jLabel62)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(开店额外经验, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel52Layout.createSequentialGroup()
                                .addComponent(jLabel63)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(结婚额外经验, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel70)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(清理怪物时间, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel52Layout.setVerticalGroup(
            jPanel52Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel52Layout.createSequentialGroup()
                .addGroup(jPanel52Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(经验倍率, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel48)
                    .addComponent(金币爆率, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel49)
                    .addComponent(物品爆率, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel52Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(加载事件, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel52Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(顶部公告, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel52Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel51)
                    .addComponent(频道最大人数, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(人物灌水比例1, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(人物灌水比例, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(频道状态, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel50, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel52Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(最大登录角色数限制, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(最大角色, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(最大创建角色数, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(频道总数, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel52Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27)
                    .addComponent(登录端口, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel46)
                    .addComponent(雇佣时间, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(物品叠加数量, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel61))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel52Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(频道端口, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(最大能力值, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel47)
                    .addComponent(高级魔方几率, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel60))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel52Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel44)
                    .addComponent(商城端口, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel53)
                    .addComponent(潜能几率, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel52Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel52Layout.createSequentialGroup()
                        .addGroup(jPanel52Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel57)
                            .addComponent(家族金币, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(7, 7, 7)
                        .addGroup(jPanel52Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel52Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel70)
                                .addComponent(清理怪物时间, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel52Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(家族徽章费用, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel64)
                                .addComponent(jLabel63)
                                .addComponent(结婚额外经验, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel52Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel62)
                        .addComponent(开店额外经验, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );

        jPanel83.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "自定义地图刷怪设置[自定义怪物倍数地图列表id(逗号隔开)]", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("微软雅黑", 0, 12))); // NOI18N

        jLabel87.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jLabel87.setText("刷怪开关:");

        jLabel88.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        jLabel88.setText("自定义地图刷怪倍数:");

        jButton10.setText("功能说明");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        地图列表ID.setColumns(20);
        地图列表ID.setFont(new java.awt.Font("宋体", 0, 13)); // NOI18N
        地图列表ID.setForeground(new java.awt.Color(51, 51, 51));
        地图列表ID.setLineWrap(true);
        地图列表ID.setToolTipText("");
        地图列表ID.setDoubleBuffered(true);
        地图列表ID.setInheritsPopupMenu(true);
        地图列表ID.setSelectedTextColor(new java.awt.Color(51, 0, 51));
        jScrollPane9.setViewportView(地图列表ID);

        javax.swing.GroupLayout jPanel83Layout = new javax.swing.GroupLayout(jPanel83);
        jPanel83.setLayout(jPanel83Layout);
        jPanel83Layout.setHorizontalGroup(
            jPanel83Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel83Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel83Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane9)
                    .addGroup(jPanel83Layout.createSequentialGroup()
                        .addGap(144, 144, 144)
                        .addComponent(jButton10)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel83Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel87)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(刷怪开关1)
                .addGap(18, 18, 18)
                .addComponent(jLabel88)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(自定义地图刷怪倍数1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(73, 73, 73))
        );
        jPanel83Layout.setVerticalGroup(
            jPanel83Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel83Layout.createSequentialGroup()
                .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel83Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel87, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(刷怪开关1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(自定义地图刷怪倍数1)
                    .addComponent(jLabel88, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton10)
                .addGap(6, 6, 6))
        );

        jPanel53.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "保存配置文件", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("微软雅黑", 0, 12))); // NOI18N

        jButton17.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jButton17.setText("保存配置并应用");
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel53Layout = new javax.swing.GroupLayout(jPanel53);
        jPanel53.setLayout(jPanel53Layout);
        jPanel53Layout.setHorizontalGroup(
            jPanel53Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel53Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton17, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel53Layout.setVerticalGroup(
            jPanel53Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel53Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton17, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "分段式经验倍率", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("微软雅黑", 0, 12))); // NOI18N

        jLabel66.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jLabel66.setText("30级");

        jLabel65.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jLabel65.setText("10级");

        jLabel71.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jLabel71.setText("200级");

        jLabel69.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jLabel69.setText("150级");

        jLabel68.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jLabel68.setText("120级");

        jLabel67.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        jLabel67.setText("70级");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel65)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ExpBound1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel66)
                .addGap(2, 2, 2)
                .addComponent(ExpBound2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel67)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ExpBound3, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel68)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ExpBound4, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel69)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ExpBound5, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel71)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ExpBound6, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel65)
                    .addComponent(ExpBound1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ExpBound2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ExpBound3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ExpBound4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ExpBound5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ExpBound6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel66)
                    .addComponent(jLabel67)
                    .addComponent(jLabel68)
                    .addComponent(jLabel69)
                    .addComponent(jLabel71))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel51Layout = new javax.swing.GroupLayout(jPanel51);
        jPanel51.setLayout(jPanel51Layout);
        jPanel51Layout.setHorizontalGroup(
            jPanel51Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel51Layout.createSequentialGroup()
                .addComponent(jPanel52, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel51Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel83, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(jPanel51Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel53, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(640, 640, 640))
        );
        jPanel51Layout.setVerticalGroup(
            jPanel51Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel51Layout.createSequentialGroup()
                .addComponent(jPanel52, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel51Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel51Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel53, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel83, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(80, Short.MAX_VALUE))
        );

        jTabbedPane6.addTab("游戏设置", new javax.swing.ImageIcon(getClass().getResource("/Image/设置扳手.png")), jPanel51); // NOI18N

        jPanel93.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel74.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "风之大陆", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("宋体", 1, 12))); // NOI18N
        jPanel74.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        蓝蜗牛开关.setText("蓝蜗牛:X");
        蓝蜗牛开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                蓝蜗牛开关ActionPerformed(evt);
            }
        });
        jPanel74.add(蓝蜗牛开关, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 20, 90, -1));

        蘑菇仔开关.setText("蘑菇仔:X");
        蘑菇仔开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                蘑菇仔开关ActionPerformed(evt);
            }
        });
        jPanel74.add(蘑菇仔开关, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 20, 90, -1));

        绿水灵开关.setText("绿水灵:X");
        绿水灵开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                绿水灵开关ActionPerformed(evt);
            }
        });
        jPanel74.add(绿水灵开关, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 20, 90, -1));

        漂漂猪开关.setText("漂漂猪:X");
        漂漂猪开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                漂漂猪开关ActionPerformed(evt);
            }
        });
        jPanel74.add(漂漂猪开关, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 60, 90, -1));

        小青蛇开关.setText("小青蛇:X");
        小青蛇开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                小青蛇开关ActionPerformed(evt);
            }
        });
        jPanel74.add(小青蛇开关, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 60, 90, -1));

        红螃蟹开关.setText("红螃蟹:X");
        红螃蟹开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                红螃蟹开关ActionPerformed(evt);
            }
        });
        jPanel74.add(红螃蟹开关, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 60, 90, -1));

        大海龟开关.setText("大海龟:X");
        大海龟开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                大海龟开关ActionPerformed(evt);
            }
        });
        jPanel74.add(大海龟开关, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 20, 90, -1));

        章鱼怪开关.setText("章鱼怪:X");
        章鱼怪开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                章鱼怪开关ActionPerformed(evt);
            }
        });
        jPanel74.add(章鱼怪开关, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 20, 90, -1));

        顽皮猴开关.setText("顽皮猴:X");
        顽皮猴开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                顽皮猴开关ActionPerformed(evt);
            }
        });
        jPanel74.add(顽皮猴开关, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 60, 90, -1));

        星精灵开关.setText("星精灵:X");
        星精灵开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                星精灵开关ActionPerformed(evt);
            }
        });
        jPanel74.add(星精灵开关, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 20, 90, -1));

        胖企鹅开关.setText("胖企鹅:X");
        胖企鹅开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                胖企鹅开关ActionPerformed(evt);
            }
        });
        jPanel74.add(胖企鹅开关, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 20, 90, -1));

        白雪人开关.setText("白雪人:X");
        白雪人开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                白雪人开关ActionPerformed(evt);
            }
        });
        jPanel74.add(白雪人开关, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 20, 90, -1));

        石头人开关.setText("石头人:X");
        石头人开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                石头人开关ActionPerformed(evt);
            }
        });
        jPanel74.add(石头人开关, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 60, 90, -1));

        紫色猫开关.setText("紫色猫:X");
        紫色猫开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                紫色猫开关ActionPerformed(evt);
            }
        });
        jPanel74.add(紫色猫开关, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 60, 90, -1));

        大灰狼开关.setText("大灰狼:X");
        大灰狼开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                大灰狼开关ActionPerformed(evt);
            }
        });
        jPanel74.add(大灰狼开关, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 60, 90, -1));

        喷火龙开关.setText("喷火龙:X");
        喷火龙开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                喷火龙开关ActionPerformed(evt);
            }
        });
        jPanel74.add(喷火龙开关, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 100, 90, -1));

        火野猪开关.setText("火野猪:X");
        火野猪开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                火野猪开关ActionPerformed(evt);
            }
        });
        jPanel74.add(火野猪开关, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 100, 90, -1));

        小白兔开关.setText("小白兔:X");
        小白兔开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                小白兔开关ActionPerformed(evt);
            }
        });
        jPanel74.add(小白兔开关, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 100, 90, -1));

        青鳄鱼开关.setText("青鳄鱼:X");
        青鳄鱼开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                青鳄鱼开关ActionPerformed(evt);
            }
        });
        jPanel74.add(青鳄鱼开关, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 60, 90, -1));

        花蘑菇开关.setText("花蘑菇:X");
        花蘑菇开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                花蘑菇开关ActionPerformed(evt);
            }
        });
        jPanel74.add(花蘑菇开关, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 100, 90, -1));

        jPanel93.add(jPanel74, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 970, 140));

        jLabel11.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 51, 51));
        jLabel11.setText("注意:请勿全部都开启,会炸客户端的.");
        jPanel93.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 320, -1, -1));

        jLabel56.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
        jLabel56.setForeground(new java.awt.Color(51, 204, 255));
        jLabel56.setText("注意:每个区所建立的角色是不一样的,进入游戏后其他没有变化");
        jPanel93.add(jLabel56, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 370, -1, -1));

        jTabbedPane6.addTab("游戏大区", new javax.swing.ImageIcon(getClass().getResource("/Image/3801296.png")), jPanel93); // NOI18N

        jPanel66.setBorder(javax.swing.BorderFactory.createTitledBorder("特殊功能"));

        jPanel72.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "其他功能设置[注意:本版块功能点击立即生效无需重启服务端]", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("宋体", 0, 12), new java.awt.Color(255, 0, 0))); // NOI18N

        禁止登陆开关.setText("游戏登陆");
        禁止登陆开关.setToolTipText("<html>\n<strong><font color=\"#FF0000\">功能说明</font></strong><br> \n<strong>用于限制玩家登陆游戏<br> <br> <br> ");
        禁止登陆开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                禁止登陆开关ActionPerformed(evt);
            }
        });

        滚动公告开关.setText("滚动公告");
        滚动公告开关.setToolTipText("<html>\n<strong><font color=\"#FF0000\">功能说明</font></strong><br> \n<strong>用于控制游戏顶部滚动公告<br> <br> <br> ");
        滚动公告开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                滚动公告开关ActionPerformed(evt);
            }
        });

        玩家聊天开关.setText("玩家聊天");
        玩家聊天开关.setToolTipText("<html>\n<strong><font color=\"#FF0000\">功能说明</font></strong><br> \n<strong>用于控制游戏内玩家是否可以聊天说话<br> <br> <br> ");
        玩家聊天开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                玩家聊天开关ActionPerformed(evt);
            }
        });

        游戏升级快讯.setText("升级快讯");
        游戏升级快讯.setToolTipText("<html>\n<strong><font color=\"#FF0000\">功能说明</font></strong><br> \n<strong>用于控制玩家升级了刷公告庆祝<br> <br> <br> ");
        游戏升级快讯.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                游戏升级快讯ActionPerformed(evt);
            }
        });

        丢出金币开关.setText("丢出金币");
        丢出金币开关.setToolTipText("<html>\n<strong><font color=\"#FF0000\">功能说明</font></strong><br> \n<strong>用于控制玩家游戏内是否可以丢金币<br> <br> <br> ");
        丢出金币开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                丢出金币开关ActionPerformed(evt);
            }
        });

        丢出物品开关.setText("丢出物品");
        丢出物品开关.setToolTipText("<html>\n<strong><font color=\"#FF0000\">功能说明</font></strong><br> \n<strong>用于控制游戏内玩家是否可以丢物品<br> <br> <br> ");
        丢出物品开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                丢出物品开关ActionPerformed(evt);
            }
        });

        游戏指令开关.setText("游戏指令");
        游戏指令开关.setToolTipText("<html>\n<strong><font color=\"#FF0000\">功能说明</font></strong><br> \n<strong>用于控制GM号是否可以用GM命令<br> <br> <br> ");
        游戏指令开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                游戏指令开关ActionPerformed(evt);
            }
        });

        上线提醒开关.setText("登录公告");
        上线提醒开关.setToolTipText("<html>\n<strong><font color=\"#FF0000\">功能说明</font></strong><br> \n<strong>玩家上线是否提示欢迎公告<br> <br>");
        上线提醒开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                上线提醒开关ActionPerformed(evt);
            }
        });

        回收地图开关.setText("回收地图");
        回收地图开关.setToolTipText("<html>\n<strong><font color=\"#FF0000\">功能说明</font></strong><br> \n<strong>用于游戏地图回收开关<br> <br> <br> ");
        回收地图开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                回收地图开关ActionPerformed(evt);
            }
        });

        管理隐身开关.setText("管理隐身");
        管理隐身开关.setToolTipText("<html>\n<strong><font color=\"#FF0000\">功能说明</font></strong><br> \n<strong>用于管理员号上线默认是否开启隐身BUFF<br> <br> <br> ");
        管理隐身开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                管理隐身开关ActionPerformed(evt);
            }
        });

        管理加速开关.setText("管理加速");
        管理加速开关.setToolTipText("<html>\n<strong><font color=\"#FF0000\">功能说明</font></strong><br> \n<strong>用于管理员号上线默认是否开启轻功BUFF<br> <br> <br> ");
        管理加速开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                管理加速开关ActionPerformed(evt);
            }
        });

        游戏喇叭开关.setText("游戏喇叭");
        游戏喇叭开关.setToolTipText("<html>\n<strong><font color=\"#FF0000\">功能说明</font></strong><br> \n<strong>用于控制是否让玩家使用游戏喇叭功能<br> <br> <br> ");
        游戏喇叭开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                游戏喇叭开关ActionPerformed(evt);
            }
        });

        玩家交易开关.setText("玩家交易");
        玩家交易开关.setToolTipText("<html>\n<strong><font color=\"#FF0000\">功能说明</font></strong><br> \n<strong>用于限制游戏内玩家交易功能<br> <br>");
        玩家交易开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                玩家交易开关ActionPerformed(evt);
            }
        });

        雇佣商人开关.setText("雇佣商人");
        雇佣商人开关.setToolTipText("<html>\n<strong><font color=\"#FF0000\">功能说明</font></strong><br> \n<strong>是否允许玩家在自由摆摊<br> <br>");
        雇佣商人开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                雇佣商人开关ActionPerformed(evt);
            }
        });

        欢迎弹窗开关.setText("欢迎弹窗");
        欢迎弹窗开关.setToolTipText("<html>\n<strong><font color=\"#FF0000\">功能说明</font></strong><br> \n<strong>进入游戏是否弹出欢迎公告<br> <br>");
        欢迎弹窗开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                欢迎弹窗开关ActionPerformed(evt);
            }
        });

        登陆帮助开关.setText("登陆帮助");
        登陆帮助开关.setToolTipText("<html>\n<strong><font color=\"#FF0000\">功能说明</font></strong><br> \n<strong>进游戏是否提示登录帮助<br> <br>");
        登陆帮助开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                登陆帮助开关ActionPerformed(evt);
            }
        });

        越级打怪开关.setText("越级打怪");
        越级打怪开关.setToolTipText("<html>\n<strong><font color=\"#FF0000\">功能说明</font></strong><br> \n<strong>超越本身等级打高级怪物不MISS<br> <br>");
        越级打怪开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                越级打怪开关ActionPerformed(evt);
            }
        });

        怪物状态开关.setText("怪物状态");
        怪物状态开关.setToolTipText("<html>\n<strong><font color=\"#FF0000\">功能说明</font></strong><br> \n<strong>用于游戏内怪物状态释放技能是否提示<br> <br>");
        怪物状态开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                怪物状态开关ActionPerformed(evt);
            }
        });

        地图名称开关.setText("地图名称");
        地图名称开关.setToolTipText("<html>\n<strong><font color=\"#FF0000\">功能说明</font></strong><br> \n<strong>过地图是否提示地图名称<br> <br>");
        地图名称开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                地图名称开关ActionPerformed(evt);
            }
        });

        过图存档开关.setText("过图存档");
        过图存档开关.setToolTipText("<html>\n<strong><font color=\"#FF0000\">功能说明</font></strong><br> \n<strong>是否开启 玩家每过一张图保存当前玩家数据<br> <br>");
        过图存档开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                过图存档开关ActionPerformed(evt);
            }
        });

        指令通知开关.setText("指令通知");
        指令通知开关.setToolTipText("<html>\n<strong><font color=\"#FF0000\">功能说明</font></strong><br> \n<strong>角色上线是否提示命令代码<br> <br> <br> ");
        指令通知开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                指令通知开关ActionPerformed(evt);
            }
        });

        游戏仓库开关.setText("游戏仓库");
        游戏仓库开关.setToolTipText("<html>\n<strong><font color=\"#FF0000\">功能说明</font></strong><br> \n<strong>限制玩家打开游戏内仓库<br> <br>");
        游戏仓库开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                游戏仓库开关ActionPerformed(evt);
            }
        });

        脚本显码开关.setText("脚本显码");
        脚本显码开关.setToolTipText("<html>\n<strong><font color=\"#FF0000\">功能说明</font></strong><br> \n<strong>用于管理员号游戏内提示NPC脚本路径以及对应ID方便GM修复脚本<br> <br>");
        脚本显码开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                脚本显码开关ActionPerformed(evt);
            }
        });

        拍卖行开关.setText("游戏拍卖");
        拍卖行开关.setToolTipText("<html>\n<strong><font color=\"#FF0000\">功能说明</font></strong><br> \n<strong>限制玩家是否可以打开游戏拍卖功能<br> <br>");
        拍卖行开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                拍卖行开关ActionPerformed(evt);
            }
        });

        游戏找人开关.setText("游戏找人");
        游戏找人开关.setToolTipText("");
        游戏找人开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                游戏找人开关ActionPerformed(evt);
            }
        });

        机器多开开关.setText("机器多开");
        机器多开开关.setToolTipText("<html>\n<strong><font color=\"#FF0000\">功能说明</font></strong><br> \n<strong>限制玩家电脑多开<br> <br>");
        机器多开开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                机器多开开关ActionPerformed(evt);
            }
        });

        IP多开开关.setText("IP多开");
        IP多开开关.setToolTipText("<html>\n<strong><font color=\"#FF0000\">功能说明</font></strong><br> \n<strong>限制玩家IP多开<br> <br>");
        IP多开开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                IP多开开关ActionPerformed(evt);
            }
        });

        挂机检测开关.setForeground(new java.awt.Color(255, 51, 51));
        挂机检测开关.setText("挂机检测");
        挂机检测开关.setToolTipText("");
        挂机检测开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                挂机检测开关ActionPerformed(evt);
            }
        });

        全屏检测开关.setForeground(new java.awt.Color(255, 0, 51));
        全屏检测开关.setText("全屏检测");
        全屏检测开关.setToolTipText("");
        全屏检测开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                全屏检测开关ActionPerformed(evt);
            }
        });

        吸怪检测开关.setForeground(new java.awt.Color(255, 0, 51));
        吸怪检测开关.setText("吸怪检测");
        吸怪检测开关.setToolTipText("");
        吸怪检测开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                吸怪检测开关ActionPerformed(evt);
            }
        });

        段数检测开关.setForeground(new java.awt.Color(255, 0, 51));
        段数检测开关.setText("段数检测");
        段数检测开关.setToolTipText("");
        段数检测开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                段数检测开关ActionPerformed(evt);
            }
        });

        群攻检测开关.setForeground(new java.awt.Color(255, 0, 51));
        群攻检测开关.setText("群攻检测");
        群攻检测开关.setToolTipText("");
        群攻检测开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                群攻检测开关ActionPerformed(evt);
            }
        });

        加速检测开关.setForeground(new java.awt.Color(255, 0, 51));
        加速检测开关.setText("加速检测");
        加速检测开关.setToolTipText("");
        加速检测开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                加速检测开关ActionPerformed(evt);
            }
        });

        捡物检测开关.setForeground(new java.awt.Color(255, 0, 51));
        捡物检测开关.setText("捡物检测");
        捡物检测开关.setToolTipText("");
        捡物检测开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                捡物检测开关ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel72Layout = new javax.swing.GroupLayout(jPanel72);
        jPanel72.setLayout(jPanel72Layout);
        jPanel72Layout.setHorizontalGroup(
            jPanel72Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel72Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(jPanel72Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel72Layout.createSequentialGroup()
                        .addComponent(禁止登陆开关, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(滚动公告开关, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(玩家聊天开关, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(游戏升级快讯, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(丢出金币开关, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(丢出物品开关, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel72Layout.createSequentialGroup()
                        .addComponent(游戏指令开关, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(指令通知开关, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(游戏喇叭开关, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(管理隐身开关, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(管理加速开关, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(回收地图开关, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel72Layout.createSequentialGroup()
                        .addComponent(玩家交易开关, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(雇佣商人开关, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(欢迎弹窗开关, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(登陆帮助开关, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(越级打怪开关, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(怪物状态开关, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel72Layout.createSequentialGroup()
                        .addComponent(地图名称开关, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(上线提醒开关, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(过图存档开关, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(游戏仓库开关, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(脚本显码开关, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(游戏找人开关, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel72Layout.createSequentialGroup()
                        .addGroup(jPanel72Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel72Layout.createSequentialGroup()
                                .addComponent(拍卖行开关, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(机器多开开关, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(IP多开开关, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel72Layout.createSequentialGroup()
                                .addComponent(挂机检测开关, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(吸怪检测开关, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(全屏检测开关, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel72Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel72Layout.createSequentialGroup()
                                .addComponent(段数检测开关, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(加速检测开关, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(捡物检测开关, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(群攻检测开关, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel72Layout.setVerticalGroup(
            jPanel72Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel72Layout.createSequentialGroup()
                .addGroup(jPanel72Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(禁止登陆开关, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(滚动公告开关, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(玩家聊天开关, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(游戏升级快讯, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(丢出金币开关, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(丢出物品开关, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel72Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(游戏指令开关, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(回收地图开关, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(管理隐身开关, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(管理加速开关, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(游戏喇叭开关, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(指令通知开关, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel72Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(玩家交易开关, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(雇佣商人开关, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(欢迎弹窗开关, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(登陆帮助开关, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(越级打怪开关, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(怪物状态开关, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel72Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(地图名称开关, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(过图存档开关, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(上线提醒开关, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(游戏仓库开关, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(脚本显码开关, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(游戏找人开关, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel72Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel72Layout.createSequentialGroup()
                        .addGroup(jPanel72Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(拍卖行开关, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel72Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(机器多开开关, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(IP多开开关, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(段数检测开关, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(加速检测开关, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(捡物检测开关, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel72Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(挂机检测开关, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel72Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(吸怪检测开关, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(全屏检测开关, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(群攻检测开关, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 20, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel66Layout = new javax.swing.GroupLayout(jPanel66);
        jPanel66.setLayout(jPanel66Layout);
        jPanel66Layout.setHorizontalGroup(
            jPanel66Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel72, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel66Layout.setVerticalGroup(
            jPanel66Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel66Layout.createSequentialGroup()
                .addComponent(jPanel72, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jTabbedPane6.addTab("特殊功能", new javax.swing.ImageIcon(getClass().getResource("/Image/3801297.png")), jPanel66); // NOI18N

        jPanel23.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N

        jPanel68.setBorder(javax.swing.BorderFactory.createTitledBorder("活动经验"));

        jPanel69.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "2倍率活动", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 24))); // NOI18N
        jPanel69.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        开启双倍经验.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        开启双倍经验.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/经验1.png"))); // NOI18N
        开启双倍经验.setText("开启双倍经验");
        开启双倍经验.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                开启双倍经验ActionPerformed(evt);
            }
        });
        jPanel69.add(开启双倍经验, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 71, 160, 40));
        jPanel69.add(双倍经验持续时间, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 80, 120, -1));

        jLabel359.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel359.setText("持续时间/h；");
        jPanel69.add(jLabel359, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, -1, 20));

        开启双倍爆率.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        开启双倍爆率.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/爆率.png"))); // NOI18N
        开启双倍爆率.setText("开启双倍爆率");
        开启双倍爆率.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                开启双倍爆率ActionPerformed(evt);
            }
        });
        jPanel69.add(开启双倍爆率, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 151, 160, 40));
        jPanel69.add(双倍爆率持续时间, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 160, 120, -1));

        jLabel360.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel360.setText("持续时间/h；");
        jPanel69.add(jLabel360, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 140, -1, 20));

        开启双倍金币.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        开启双倍金币.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/4031040.png"))); // NOI18N
        开启双倍金币.setText("开启双倍金币");
        开启双倍金币.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                开启双倍金币ActionPerformed(evt);
            }
        });
        jPanel69.add(开启双倍金币, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 231, 160, 40));
        jPanel69.add(双倍金币持续时间, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 240, 120, -1));

        jLabel361.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel361.setText("持续时间/h；");
        jPanel69.add(jLabel361, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 220, -1, 20));

        jPanel70.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "3倍率活动", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 24))); // NOI18N
        jPanel70.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        开启三倍经验.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        开启三倍经验.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/2435108.png"))); // NOI18N
        开启三倍经验.setText("开启三倍经验");
        开启三倍经验.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                开启三倍经验ActionPerformed(evt);
            }
        });
        jPanel70.add(开启三倍经验, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 71, 160, 40));
        jPanel70.add(三倍经验持续时间, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 80, 120, -1));

        jLabel362.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel362.setText("持续时间/h；");
        jPanel70.add(jLabel362, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, -1, 20));

        开启三倍爆率.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        开启三倍爆率.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/管理全域掉落.png"))); // NOI18N
        开启三倍爆率.setText("开启三倍爆率");
        开启三倍爆率.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                开启三倍爆率ActionPerformed(evt);
            }
        });
        jPanel70.add(开启三倍爆率, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 151, 160, 40));
        jPanel70.add(三倍爆率持续时间, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 160, 120, -1));

        jLabel348.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel348.setText("持续时间/h；");
        jPanel70.add(jLabel348, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 140, -1, 20));

        开启三倍金币.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        开启三倍金币.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/4031041.png"))); // NOI18N
        开启三倍金币.setText("开启三倍金币");
        开启三倍金币.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                开启三倍金币ActionPerformed(evt);
            }
        });
        jPanel70.add(开启三倍金币, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 231, 160, 40));
        jPanel70.add(三倍金币持续时间, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 240, 120, -1));

        jLabel349.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel349.setText("持续时间/h；");
        jPanel70.add(jLabel349, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 220, -1, 20));

        jLabel1.setFont(new java.awt.Font("宋体", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 51, 51));
        jLabel1.setText("功能说明：本功能无需重启服务端立即生效");

        jLabel3.setFont(new java.awt.Font("宋体", 1, 36)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 0, 51));
        jLabel3.setText("单位换算 h=小时 时间到期自动解除倍率");

        javax.swing.GroupLayout jPanel68Layout = new javax.swing.GroupLayout(jPanel68);
        jPanel68.setLayout(jPanel68Layout);
        jPanel68Layout.setHorizontalGroup(
            jPanel68Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel68Layout.createSequentialGroup()
                .addComponent(jPanel69, javax.swing.GroupLayout.PREFERRED_SIZE, 425, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel70, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel68Layout.createSequentialGroup()
                .addGroup(jPanel68Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel68Layout.createSequentialGroup()
                        .addGap(69, 69, 69)
                        .addComponent(jLabel3))
                    .addGroup(jPanel68Layout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 732, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(172, Short.MAX_VALUE))
        );
        jPanel68Layout.setVerticalGroup(
            jPanel68Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel68Layout.createSequentialGroup()
                .addGroup(jPanel68Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel70, javax.swing.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE)
                    .addComponent(jPanel69, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(50, 50, 50)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(62, 62, 62)
                .addComponent(jLabel3)
                .addContainerGap(90, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel68, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel68, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane6.addTab("活动经验", new javax.swing.ImageIcon(getClass().getResource("/Image/3801294.png")), jPanel23); // NOI18N

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "玩家在线泡点", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 24))); // NOI18N
        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        在线泡点设置.setFont(new java.awt.Font("微软雅黑", 0, 18)); // NOI18N
        在线泡点设置.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "序号", "类型", "数值"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        在线泡点设置.getTableHeader().setReorderingAllowed(false);
        jScrollPane134.setViewportView(在线泡点设置);

        jPanel9.add(jScrollPane134, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 470, 260));

        泡点序号.setEditable(false);
        jPanel9.add(泡点序号, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 330, 70, 30));

        泡点类型.setEditable(false);
        jPanel9.add(泡点类型, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 330, 110, 30));
        jPanel9.add(泡点值, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 330, 120, 30));

        泡点值修改.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        泡点值修改.setForeground(new java.awt.Color(255, 51, 51));
        泡点值修改.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/123.png"))); // NOI18N
        泡点值修改.setText("修改");
        泡点值修改.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                泡点值修改ActionPerformed(evt);
            }
        });
        jPanel9.add(泡点值修改, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 330, 110, 30));

        jLabel322.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel322.setText("类型数值；");
        jPanel9.add(jLabel322, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 310, -1, -1));

        jLabel327.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel327.setText("泡点奖励类型；");
        jPanel9.add(jLabel327, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 310, -1, -1));

        jPanel75.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "在线泡点设置", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 24))); // NOI18N

        泡点金币开关.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        泡点金币开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/3801462.png"))); // NOI18N
        泡点金币开关.setText("泡点金币");
        泡点金币开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                泡点金币开关ActionPerformed(evt);
            }
        });

        泡点经验开关.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        泡点经验开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/3801316.png"))); // NOI18N
        泡点经验开关.setText("泡点经验");
        泡点经验开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                泡点经验开关ActionPerformed(evt);
            }
        });

        泡点点券开关.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        泡点点券开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/3801315.png"))); // NOI18N
        泡点点券开关.setText("泡点点券");
        泡点点券开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                泡点点券开关ActionPerformed(evt);
            }
        });

        泡点抵用开关.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        泡点抵用开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/3801314.png"))); // NOI18N
        泡点抵用开关.setText("泡点抵用");
        泡点抵用开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                泡点抵用开关ActionPerformed(evt);
            }
        });

        泡点豆豆开关.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        泡点豆豆开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/3801309.png"))); // NOI18N
        泡点豆豆开关.setText("泡点豆豆");
        泡点豆豆开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                泡点豆豆开关ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel75Layout = new javax.swing.GroupLayout(jPanel75);
        jPanel75.setLayout(jPanel75Layout);
        jPanel75Layout.setHorizontalGroup(
            jPanel75Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel75Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel75Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(泡点豆豆开关, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel75Layout.createSequentialGroup()
                        .addComponent(泡点金币开关, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(泡点经验开关, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel75Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(泡点点券开关, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(泡点抵用开关, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, 0))
        );
        jPanel75Layout.setVerticalGroup(
            jPanel75Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel75Layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addGroup(jPanel75Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(泡点金币开关, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(泡点经验开关, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addGroup(jPanel75Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(泡点点券开关, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(泡点抵用开关, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addComponent(泡点豆豆开关, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(51, 51, 51))
        );

        jPanel9.add(jPanel75, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 30, 360, 270));

        jLabel328.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel328.setText("序号；");
        jPanel9.add(jLabel328, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 310, -1, -1));

        jLabel4.setFont(new java.awt.Font("微软雅黑", 0, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 51, 51));
        jLabel4.setText("在线泡点必须在自由市场才可获得奖励。金币/经验均按照等级给予");
        jPanel9.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 390, 540, -1));

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "玩家在线泡点", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 24))); // NOI18N
        jPanel10.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        在线泡点设置1.setFont(new java.awt.Font("微软雅黑", 0, 18)); // NOI18N
        在线泡点设置1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "序号", "类型", "数值"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        在线泡点设置1.getTableHeader().setReorderingAllowed(false);
        jScrollPane135.setViewportView(在线泡点设置1);

        jPanel10.add(jScrollPane135, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 470, 260));

        泡点序号1.setEditable(false);
        jPanel10.add(泡点序号1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 380, 70, 30));

        泡点类型1.setEditable(false);
        jPanel10.add(泡点类型1, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 380, 110, 30));
        jPanel10.add(泡点值1, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 380, 120, 30));

        泡点值修改1.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        泡点值修改1.setText("修改");
        泡点值修改1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                泡点值修改1ActionPerformed(evt);
            }
        });
        jPanel10.add(泡点值修改1, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 380, 80, 30));

        jLabel323.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel323.setText("类型数值；");
        jPanel10.add(jLabel323, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 360, -1, -1));

        jLabel329.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel329.setText("泡点奖励类型；");
        jPanel10.add(jLabel329, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 360, -1, -1));

        jPanel76.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "在线泡点设置", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 24))); // NOI18N

        泡点金币开关1.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        泡点金币开关1.setText("泡点金币");
        泡点金币开关1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                泡点金币开关1ActionPerformed(evt);
            }
        });

        泡点经验开关1.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        泡点经验开关1.setText("泡点经验");
        泡点经验开关1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                泡点经验开关1ActionPerformed(evt);
            }
        });

        泡点点券开关1.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        泡点点券开关1.setText("泡点点券");
        泡点点券开关1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                泡点点券开关1ActionPerformed(evt);
            }
        });

        泡点抵用开关1.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        泡点抵用开关1.setText("泡点抵用");
        泡点抵用开关1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                泡点抵用开关1ActionPerformed(evt);
            }
        });

        泡点豆豆开关1.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        泡点豆豆开关1.setText("泡点豆豆");
        泡点豆豆开关1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                泡点豆豆开关1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel76Layout = new javax.swing.GroupLayout(jPanel76);
        jPanel76.setLayout(jPanel76Layout);
        jPanel76Layout.setHorizontalGroup(
            jPanel76Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel76Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel76Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(泡点豆豆开关1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel76Layout.createSequentialGroup()
                        .addComponent(泡点金币开关1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(泡点经验开关1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel76Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(泡点点券开关1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(泡点抵用开关1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, 0))
        );
        jPanel76Layout.setVerticalGroup(
            jPanel76Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel76Layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addGroup(jPanel76Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(泡点金币开关1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(泡点经验开关1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addGroup(jPanel76Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(泡点点券开关1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(泡点抵用开关1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addComponent(泡点豆豆开关1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(51, 51, 51))
        );

        jPanel10.add(jPanel76, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 30, 360, 270));

        jLabel330.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel330.setText("序号；");
        jPanel10.add(jLabel330, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 360, -1, -1));

        jLabel78.setText("金币==数值乘等级 列如：金币数值10，实际泡点所得金币等于10乘当前等级");
        jPanel10.add(jLabel78, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 430, 510, -1));

        jLabel79.setText("经验==数值乘等级 列如：经验数值10，实际泡点所得经验等于10乘当前等级");
        jPanel10.add(jLabel79, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 460, 500, -1));

        jLabel80.setText("其中：点卷/抵用卷/豆豆 这三个数值都是固定数值，设置10泡点所得就是10");
        jPanel10.add(jLabel80, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 490, 520, -1));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, 976, Short.MAX_VALUE)
            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, 976, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, 618, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 618, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jTabbedPane6.addTab("在线泡点", new javax.swing.ImageIcon(getClass().getResource("/Image/3801295.png")), jPanel6); // NOI18N

        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "离线挂机泡点", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 24))); // NOI18N
        jPanel13.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        离线泡点设置.setFont(new java.awt.Font("微软雅黑", 0, 18)); // NOI18N
        离线泡点设置.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "序号", "类型", "数值"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        离线泡点设置.getTableHeader().setReorderingAllowed(false);
        jScrollPane136.setViewportView(离线泡点设置);

        jPanel13.add(jScrollPane136, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 470, 260));

        离线泡点序号.setEditable(false);
        jPanel13.add(离线泡点序号, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 330, 70, 30));

        离线泡点类型.setEditable(false);
        jPanel13.add(离线泡点类型, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 330, 110, 30));
        jPanel13.add(离线泡点值, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 330, 120, 30));

        离线泡点值修改.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        离线泡点值修改.setForeground(new java.awt.Color(255, 0, 0));
        离线泡点值修改.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/123.png"))); // NOI18N
        离线泡点值修改.setText("修改");
        离线泡点值修改.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                离线泡点值修改ActionPerformed(evt);
            }
        });
        jPanel13.add(离线泡点值修改, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 330, 110, 30));

        jLabel324.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel324.setText("类型数值；");
        jPanel13.add(jLabel324, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 310, -1, -1));

        jLabel331.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel331.setText("泡点奖励类型；");
        jPanel13.add(jLabel331, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 310, -1, -1));

        jPanel77.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "离线挂机泡点设置", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 24))); // NOI18N

        离线泡点金币开关.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        离线泡点金币开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/3801462.png"))); // NOI18N
        离线泡点金币开关.setText("泡点金币");
        离线泡点金币开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                离线泡点金币开关ActionPerformed(evt);
            }
        });

        离线泡点经验开关.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        离线泡点经验开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/3801316.png"))); // NOI18N
        离线泡点经验开关.setText("泡点经验");
        离线泡点经验开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                离线泡点经验开关ActionPerformed(evt);
            }
        });

        离线泡点点券开关.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        离线泡点点券开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/3801315.png"))); // NOI18N
        离线泡点点券开关.setText("泡点点券");
        离线泡点点券开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                离线泡点点券开关ActionPerformed(evt);
            }
        });

        离线泡点抵用开关.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        离线泡点抵用开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/3801314.png"))); // NOI18N
        离线泡点抵用开关.setText("泡点抵用");
        离线泡点抵用开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                离线泡点抵用开关ActionPerformed(evt);
            }
        });

        离线泡点豆豆开关.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        离线泡点豆豆开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/3801309.png"))); // NOI18N
        离线泡点豆豆开关.setText("泡点豆豆");
        离线泡点豆豆开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                离线泡点豆豆开关ActionPerformed(evt);
            }
        });

        离线挂机开关.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        离线挂机开关.setForeground(new java.awt.Color(255, 0, 51));
        离线挂机开关.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/3801313.png"))); // NOI18N
        离线挂机开关.setText("离线挂机");
        离线挂机开关.setToolTipText("");
        离线挂机开关.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                离线挂机开关ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel77Layout = new javax.swing.GroupLayout(jPanel77);
        jPanel77.setLayout(jPanel77Layout);
        jPanel77Layout.setHorizontalGroup(
            jPanel77Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel77Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel77Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel77Layout.createSequentialGroup()
                        .addComponent(离线泡点金币开关, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(离线泡点经验开关, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel77Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addGroup(jPanel77Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(离线泡点点券开关, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(离线泡点豆豆开关, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel77Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(离线挂机开关, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(离线泡点抵用开关, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE))))
                .addGap(0, 0, 0))
        );
        jPanel77Layout.setVerticalGroup(
            jPanel77Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel77Layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addGroup(jPanel77Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(离线泡点金币开关, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(离线泡点经验开关, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                .addGroup(jPanel77Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(离线泡点点券开关, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(离线泡点抵用开关, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addGroup(jPanel77Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(离线泡点豆豆开关, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(离线挂机开关, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(51, 51, 51))
        );

        jPanel13.add(jPanel77, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 30, 360, 270));

        jLabel332.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel332.setText("序号；");
        jPanel13.add(jLabel332, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 310, -1, -1));

        jLabel2.setFont(new java.awt.Font("微软雅黑", 0, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 51, 51));
        jLabel2.setText("玩家在自由市场下线后服务端自动启动离线挂机功能,角色将继续在自由市场并统计离线时间,下次上线会发送离线奖励.");
        jPanel13.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 480, 930, -1));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 976, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel13, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 670, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, 621, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 49, Short.MAX_VALUE)))
        );

        jTabbedPane6.addTab("离线挂机", new javax.swing.ImageIcon(getClass().getResource("/Image/3801293.png")), jPanel4); // NOI18N

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addComponent(jTabbedPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 981, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addComponent(jTabbedPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 713, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane.addTab("游戏配置", new javax.swing.ImageIcon(getClass().getResource("/Image/3994610.png")), jPanel21); // NOI18N

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder("游戏公告"));

        sendNotice.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/喇叭1.png"))); // NOI18N
        sendNotice.setText("蓝色提示公告");
        sendNotice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendNoticeActionPerformed(evt);
            }
        });

        sendWinNotice.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/喇叭2.png"))); // NOI18N
        sendWinNotice.setText("顶部滚动公告");
        sendWinNotice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendWinNoticeActionPerformed(evt);
            }
        });

        sendMsgNotice.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/喇叭3.png"))); // NOI18N
        sendMsgNotice.setText("弹窗公告");
        sendMsgNotice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendMsgNoticeActionPerformed(evt);
            }
        });

        sendNpcTalkNotice.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/喇叭4.png"))); // NOI18N
        sendNpcTalkNotice.setText("蓝色公告事项");
        sendNpcTalkNotice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendNpcTalkNoticeActionPerformed(evt);
            }
        });

        noticeText.setFont(new java.awt.Font("宋体", 0, 24)); // NOI18N
        noticeText.setText("游戏即将维护,请安全下线！造成不便请谅解！");

        jLabel117.setFont(new java.awt.Font("幼圆", 0, 24)); // NOI18N
        jLabel117.setText("1、不得散布谣言，扰乱社会秩序，破坏社会稳定的信息 ");

        jLabel118.setFont(new java.awt.Font("幼圆", 0, 24)); // NOI18N
        jLabel118.setText("2、不得散布赌博、暴力、凶杀、恐怖或者教唆犯罪的信息");

        jLabel119.setFont(new java.awt.Font("幼圆", 0, 24)); // NOI18N
        jLabel119.setText("3、不得侮辱或者诽谤他人，侵害他人合法权益");

        jLabel106.setFont(new java.awt.Font("幼圆", 0, 24)); // NOI18N
        jLabel106.setText("4、不得含有法律、行政法规禁止的其他内容");

        公告发布喇叭代码.setText("5120027");
        公告发布喇叭代码.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                公告发布喇叭代码ActionPerformed(evt);
            }
        });

        jButton45.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jButton45.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/喇叭5.png"))); // NOI18N
        jButton45.setText("屏幕正中公告");
        jButton45.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton45ActionPerformed(evt);
            }
        });

        jLabel259.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel259.setText("喇叭代码");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addComponent(sendNotice)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel117, javax.swing.GroupLayout.PREFERRED_SIZE, 680, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel118, javax.swing.GroupLayout.PREFERRED_SIZE, 680, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel119, javax.swing.GroupLayout.PREFERRED_SIZE, 680, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel106, javax.swing.GroupLayout.PREFERRED_SIZE, 680, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(102, 102, 102))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sendWinNotice)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sendMsgNotice)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sendNpcTalkNotice)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton45)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel259)
                            .addComponent(公告发布喇叭代码, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(127, 127, 127))))
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addComponent(noticeText)
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addComponent(noticeText, javax.swing.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel259)
                        .addGap(0, 0, 0)
                        .addComponent(公告发布喇叭代码, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                        .addComponent(sendWinNotice, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(sendMsgNotice, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(sendNotice, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(sendNpcTalkNotice, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton45, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(59, 59, 59)
                .addComponent(jLabel117, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jLabel118, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jLabel119, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jLabel106, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(72, 72, 72))
        );

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane.addTab("游戏公告", new javax.swing.ImageIcon(getClass().getResource("/Image/2630205.png")), jPanel22); // NOI18N

        账号提示语言.setFont(new java.awt.Font("幼圆", 0, 18)); // NOI18N
        账号提示语言.setText("[信息]：");

        显示在线账号.setFont(new java.awt.Font("幼圆", 0, 18)); // NOI18N

        解卡.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        解卡.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/工具.png"))); // NOI18N
        解卡.setText("解卡账号");
        解卡.setToolTipText("<html>\n在文本框<strong><font color=\"#FF0000\">操作的账号</font></strong>中输入账号即可解卡账号<br>");
        解卡.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                解卡ActionPerformed(evt);
            }
        });

        账号信息.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        账号信息.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "账号ID", "账号", "IP地址", "MAC地址", "绑定QQ", "点券", "抵用", "最近上线", "在线", "封号", "GM"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        账号信息.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(账号信息);

        jPanel38.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "账号修改", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 18))); // NOI18N
        jPanel38.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        抵用.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        jPanel38.add(抵用, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 40, 120, 30));

        账号.setEditable(false);
        账号.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        jPanel38.add(账号, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 40, 100, 30));

        点券.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        jPanel38.add(点券, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 40, 120, 30));

        jLabel55.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        jLabel55.setText("抵用：");
        jPanel38.add(jLabel55, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 20, 60, -1));

        jLabel131.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        jLabel131.setText("点券：");
        jPanel38.add(jLabel131, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 20, -1, -1));

        修改账号点券抵用.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        修改账号点券抵用.setForeground(new java.awt.Color(204, 0, 0));
        修改账号点券抵用.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/关闭.png"))); // NOI18N
        修改账号点券抵用.setText("修改");
        修改账号点券抵用.setToolTipText("<html>\n点击账号后可修改账号的<strong><font color=\"#FF0000\">抵用券</font></strong><strong>和<font color=\"#FF0000\">点券</font></strong>");
        修改账号点券抵用.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                修改账号点券抵用ActionPerformed(evt);
            }
        });
        jPanel38.add(修改账号点券抵用, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 40, 100, 30));

        账号ID.setEditable(false);
        账号ID.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        jPanel38.add(账号ID, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 70, 30));

        jLabel206.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        jLabel206.setText("ID：");
        jPanel38.add(jLabel206, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, -1, -1));

        jLabel312.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        jLabel312.setText("管理：");
        jPanel38.add(jLabel312, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 20, -1, -1));

        管理1.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        jPanel38.add(管理1, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 40, 70, 30));

        jLabel353.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        jLabel353.setText("账号：");
        jPanel38.add(jLabel353, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 20, -1, -1));

        QQ.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        jPanel38.add(QQ, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 40, 120, 30));

        jLabel357.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        jLabel357.setText("绑定QQ：");
        jPanel38.add(jLabel357, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 20, 80, -1));

        jButton12.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jButton12.setForeground(new java.awt.Color(51, 0, 255));
        jButton12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/通用设置.png"))); // NOI18N
        jButton12.setText("搜索账号");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        刷新账号信息.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        刷新账号信息.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/全部日志.png"))); // NOI18N
        刷新账号信息.setText("全部账号");
        刷新账号信息.setToolTipText("显示所有玩家账号");
        刷新账号信息.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                刷新账号信息ActionPerformed(evt);
            }
        });

        账号操作.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        账号操作.setText("327321366");

        离线账号.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        离线账号.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/角色管理.png"))); // NOI18N
        离线账号.setText("离线账号");
        离线账号.setToolTipText("显示离线账号");
        离线账号.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                离线账号ActionPerformed(evt);
            }
        });

        解封.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        解封.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/警告日志.png"))); // NOI18N
        解封.setText("解封账号");
        解封.setToolTipText("<html>\n在文本框<strong><font color=\"#FF0000\">操作的账号</font></strong>中输入账号即可解封已经被封禁的账号<br>\n");
        解封.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                解封ActionPerformed(evt);
            }
        });

        jPanel39.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "注册/修改", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 18))); // NOI18N
        jPanel39.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        注册的账号.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        注册的账号.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                注册的账号ActionPerformed(evt);
            }
        });
        jPanel39.add(注册的账号, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 30, 170, 30));

        注册的密码.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        注册的密码.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                注册的密码ActionPerformed(evt);
            }
        });
        jPanel39.add(注册的密码, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 30, 170, 30));

        jButton35.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jButton35.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/任务管理.png"))); // NOI18N
        jButton35.setText("注册");
        jButton35.setToolTipText("<html>\n输入<strong><font color=\"#FF0000\">账号</font></strong><strong>和<strong><font color=\"#FF0000\">密码</font></strong><strong>即可注册账号");
        jButton35.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton35ActionPerformed(evt);
            }
        });
        jPanel39.add(jButton35, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 30, 100, 30));

        jLabel111.setFont(new java.awt.Font("幼圆", 0, 18)); // NOI18N
        jLabel111.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/全部日志.png"))); // NOI18N
        jLabel111.setText("账号：");
        jPanel39.add(jLabel111, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, -1, 30));

        jLabel201.setFont(new java.awt.Font("幼圆", 0, 18)); // NOI18N
        jLabel201.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/调试日志.png"))); // NOI18N
        jLabel201.setText("密码：");
        jPanel39.add(jLabel201, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 30, -1, 30));

        jButton30.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jButton30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/更新.png"))); // NOI18N
        jButton30.setText("改密");
        jButton30.setToolTipText("<html>\n输入账号修改<strong><font color=\"#FF0000\">密码</font></strong><strong>");
        jButton30.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton30ActionPerformed(evt);
            }
        });
        jPanel39.add(jButton30, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 30, 100, 30));

        已封账号.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        已封账号.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/玩家监控.png"))); // NOI18N
        已封账号.setText("已封账号");
        已封账号.setToolTipText("显示已经被封禁的账号");
        已封账号.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                已封账号ActionPerformed(evt);
            }
        });

        在线账号.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        在线账号.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/调试日志.png"))); // NOI18N
        在线账号.setText("在线账号");
        在线账号.setToolTipText("显示在线账号");
        在线账号.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                在线账号ActionPerformed(evt);
            }
        });

        删除账号.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        删除账号.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/3801309.png"))); // NOI18N
        删除账号.setText("删除账号");
        删除账号.setToolTipText("<html>\n在文本框<strong><font color=\"#FF0000\">操作的账号</font></strong>中输入账号即可删除账号<br>");
        删除账号.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                删除账号ActionPerformed(evt);
            }
        });

        封锁账号.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        封锁账号.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/至尊版工具.png"))); // NOI18N
        封锁账号.setText("封锁账号");
        封锁账号.setToolTipText("<html>\n在文本框<strong><font color=\"#FF0000\">操作的账号</font></strong>中输入账号即可封禁账号<br>");
        封锁账号.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                封锁账号ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel25Layout = new javax.swing.GroupLayout(jPanel25);
        jPanel25.setLayout(jPanel25Layout);
        jPanel25Layout.setHorizontalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(账号提示语言, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(显示在线账号, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel25Layout.createSequentialGroup()
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel38, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel25Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel39, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel25Layout.createSequentialGroup()
                        .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(账号操作))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel25Layout.createSequentialGroup()
                        .addComponent(刷新账号信息)
                        .addGap(23, 23, 23)
                        .addComponent(在线账号)
                        .addGap(23, 23, 23)
                        .addComponent(离线账号)
                        .addGap(23, 23, 23)
                        .addComponent(已封账号)
                        .addGap(23, 23, 23)
                        .addComponent(删除账号)
                        .addGap(23, 23, 23)
                        .addComponent(解卡)
                        .addGap(23, 23, 23)
                        .addComponent(封锁账号)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                        .addComponent(解封)))
                .addGap(10, 10, 10))
        );
        jPanel25Layout.setVerticalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(刷新账号信息, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(在线账号, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(离线账号, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(已封账号, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(删除账号, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(解卡, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(封锁账号, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(解封, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(账号操作, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 391, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel38, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jPanel39, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(显示在线账号, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(账号提示语言, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane.addTab("帐户中心", new javax.swing.ImageIcon(getClass().getResource("/Image/3994506.png")), jPanel25); // NOI18N

        jTabbedPane8.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N

        角色信息1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        角色信息.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        角色信息.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        角色信息.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "角色ID", "账号ID", "角色昵称", "职业", "等级", "力量", "敏捷", "智力", "运气", "MaxHP", "MaxMP", "金币", "所在地图", "状态", "GM", "发型", "脸型"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        角色信息.setName(""); // NOI18N
        角色信息.getTableHeader().setReorderingAllowed(false);
        jScrollPane10.setViewportView(角色信息);

        角色信息1.add(jScrollPane10, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 990, 490));

        刷新角色信息.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        刷新角色信息.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/后台.png"))); // NOI18N
        刷新角色信息.setText("刷新列表");
        刷新角色信息.setToolTipText("显示所有角色");
        刷新角色信息.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                刷新角色信息ActionPerformed(evt);
            }
        });
        角色信息1.add(刷新角色信息, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 500, 150, 30));

        显示管理角色.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        显示管理角色.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/调试日志.png"))); // NOI18N
        显示管理角色.setText("管理角色");
        显示管理角色.setToolTipText("显示所有GM管理员");
        显示管理角色.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                显示管理角色ActionPerformed(evt);
            }
        });
        角色信息1.add(显示管理角色, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 530, 150, 30));

        jButton38.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        jButton38.setForeground(new java.awt.Color(255, 51, 51));
        jButton38.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/关闭.png"))); // NOI18N
        jButton38.setText("修改");
        jButton38.setToolTipText("<html>\n修改角色信息<strong><font color=\"#FF0000\">文本框不可留空</font></strong><strong>");
        jButton38.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton38ActionPerformed(evt);
            }
        });
        角色信息1.add(jButton38, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 580, 110, 40));

        删除角色.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        删除角色.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/错误日志.png"))); // NOI18N
        删除角色.setText("删除角色");
        删除角色.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                删除角色ActionPerformed(evt);
            }
        });
        角色信息1.add(删除角色, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 500, 150, 30));

        角色昵称.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        角色昵称.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                角色昵称ActionPerformed(evt);
            }
        });
        角色信息1.add(角色昵称, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 590, 70, 30));

        等级.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        等级.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                等级ActionPerformed(evt);
            }
        });
        角色信息1.add(等级, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 590, 40, 30));

        力量.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        力量.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                力量ActionPerformed(evt);
            }
        });
        角色信息1.add(力量, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 590, 40, 30));

        敏捷.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        敏捷.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                敏捷ActionPerformed(evt);
            }
        });
        角色信息1.add(敏捷, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 590, 40, 30));

        智力.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        智力.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                智力ActionPerformed(evt);
            }
        });
        角色信息1.add(智力, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 590, 40, 30));

        运气.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        运气.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                运气ActionPerformed(evt);
            }
        });
        角色信息1.add(运气, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 590, 40, 30));

        HP.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        HP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                HPActionPerformed(evt);
            }
        });
        角色信息1.add(HP, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 590, 50, 30));

        MP.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        MP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MPActionPerformed(evt);
            }
        });
        角色信息1.add(MP, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 590, 50, 30));

        金币1.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        金币1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                金币1ActionPerformed(evt);
            }
        });
        角色信息1.add(金币1, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 590, 80, 30));

        地图.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        地图.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                地图ActionPerformed(evt);
            }
        });
        角色信息1.add(地图, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 590, 80, 30));

        GM.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        GM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GMActionPerformed(evt);
            }
        });
        角色信息1.add(GM, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 590, 40, 30));

        jLabel182.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        jLabel182.setText("GM等级；");
        角色信息1.add(jLabel182, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 570, -1, -1));

        jLabel183.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        jLabel183.setText("角色ID；");
        角色信息1.add(jLabel183, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 570, -1, -1));

        jLabel184.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        jLabel184.setText("等级；");
        角色信息1.add(jLabel184, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 570, -1, -1));

        jLabel185.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        jLabel185.setText("力量；");
        角色信息1.add(jLabel185, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 570, -1, -1));

        jLabel186.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        jLabel186.setText("敏捷；");
        角色信息1.add(jLabel186, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 570, -1, -1));

        jLabel187.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        jLabel187.setText("智力；");
        角色信息1.add(jLabel187, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 570, -1, -1));

        jLabel189.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        jLabel189.setText("MaxHP；");
        角色信息1.add(jLabel189, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 570, -1, -1));

        jLabel190.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        jLabel190.setText("MaxMP；");
        角色信息1.add(jLabel190, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 570, -1, -1));

        jLabel191.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        jLabel191.setText("金币；");
        角色信息1.add(jLabel191, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 570, -1, -1));

        jLabel192.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        jLabel192.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/01003824.png"))); // NOI18N
        jLabel192.setText("发 型");
        角色信息1.add(jLabel192, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 500, -1, 30));

        jLabel193.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        jLabel193.setText("角色昵称；");
        角色信息1.add(jLabel193, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 570, -1, -1));

        角色ID.setEditable(false);
        角色ID.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        角色ID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                角色IDActionPerformed(evt);
            }
        });
        角色信息1.add(角色ID, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 590, 40, 30));

        卡号自救1.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        卡号自救1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/游戏通知.png"))); // NOI18N
        卡号自救1.setText("卡发/脸型解救");
        卡号自救1.setToolTipText("<html>\n角色卡<strong><font color=\"#FF0000\">发型</font></strong><strong>或者<strong><font color=\"#FF0000\">脸型</font></strong><strong>时候可用此功能\n");
        卡号自救1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                卡号自救1ActionPerformed(evt);
            }
        });
        角色信息1.add(卡号自救1, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 500, 150, 30));

        卡号自救2.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        卡号自救2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/技能魔改.png"))); // NOI18N
        卡号自救2.setText("卡物品解救");
        卡号自救2.setToolTipText("<html>\n次卡号解救会对角色进行<strong><font color=\"#FF0000\">清空物品</font></strong><strong>处理");
        卡号自救2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                卡号自救2ActionPerformed(evt);
            }
        });
        角色信息1.add(卡号自救2, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 530, 150, 30));

        jLabel203.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        jLabel203.setText("运气；");
        角色信息1.add(jLabel203, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 570, -1, -1));

        查看技能.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        查看技能.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/封包日志.png"))); // NOI18N
        查看技能.setText("查看角色技能");
        查看技能.setToolTipText("<html>\n选择角色后，点击此功能，可查看角色所有<strong><font color=\"#FF0000\">技能信息</font></strong><strong>");
        查看技能.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                查看技能ActionPerformed(evt);
            }
        });
        角色信息1.add(查看技能, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 530, 150, 30));

        查看背包.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        查看背包.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/管理背包.png"))); // NOI18N
        查看背包.setText("查看角色背包");
        查看背包.setToolTipText("<html>\n选择角色后，点击此功能，可查看角色所有<strong><font color=\"#FF0000\">物品信息</font></strong><strong>");
        查看背包.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                查看背包ActionPerformed(evt);
            }
        });
        角色信息1.add(查看背包, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 530, 150, 30));

        卡家族解救.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        卡家族解救.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/玩家监控.png"))); // NOI18N
        卡家族解救.setText("卡家族解救");
        卡家族解救.setToolTipText("<html>\n角色卡<strong><font color=\"#FF0000\">发型</font></strong><strong>或者<strong><font color=\"#FF0000\">脸型</font></strong><strong>时候可用此功能\n");
        卡家族解救.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                卡家族解救ActionPerformed(evt);
            }
        });
        角色信息1.add(卡家族解救, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 500, 150, 30));

        脸型.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        脸型.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                脸型ActionPerformed(evt);
            }
        });
        角色信息1.add(脸型, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 530, 90, 25));

        发型.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        发型.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                发型ActionPerformed(evt);
            }
        });
        角色信息1.add(发型, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 500, 90, 25));

        jLabel214.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        jLabel214.setText("所在地图；");
        角色信息1.add(jLabel214, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 570, -1, -1));

        离线角色.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        离线角色.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/3801310.png"))); // NOI18N
        离线角色.setText("离线角色");
        离线角色.setToolTipText("显示所有GM管理员");
        离线角色.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                离线角色ActionPerformed(evt);
            }
        });
        角色信息1.add(离线角色, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 530, 150, 30));

        在线角色.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        在线角色.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/警告日志.png"))); // NOI18N
        在线角色.setText("在线角色");
        在线角色.setToolTipText("显示所有GM管理员");
        在线角色.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                在线角色ActionPerformed(evt);
            }
        });
        角色信息1.add(在线角色, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 500, 150, 30));

        显示在线玩家.setFont(new java.awt.Font("幼圆", 0, 18)); // NOI18N
        角色信息1.add(显示在线玩家, new org.netbeans.lib.awtextra.AbsoluteConstraints(1110, 495, 130, 30));

        jLabel194.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        jLabel194.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/111.png"))); // NOI18N
        jLabel194.setText("脸 型");
        角色信息1.add(jLabel194, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 530, -1, 30));

        jTabbedPane8.addTab("角色信息", new javax.swing.ImageIcon(getClass().getResource("/Image/日志.png")), 角色信息1); // NOI18N

        角色背包.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTabbedPane5.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N

        jPanel40.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "角色穿戴装备信息", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 24))); // NOI18N
        jPanel40.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        角色背包穿戴.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        角色背包穿戴.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "序号", "物品代码", "物品名字"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        角色背包穿戴.getTableHeader().setReorderingAllowed(false);
        jScrollPane15.setViewportView(角色背包穿戴);

        jPanel40.add(jScrollPane15, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 940, 480));

        背包物品名字1.setEditable(false);
        背包物品名字1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                背包物品名字1ActionPerformed(evt);
            }
        });
        jPanel40.add(背包物品名字1, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 550, 150, 30));

        身上穿戴序号1.setEditable(false);
        身上穿戴序号1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                身上穿戴序号1ActionPerformed(evt);
            }
        });
        jPanel40.add(身上穿戴序号1, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 550, 110, 30));

        背包物品代码1.setEditable(false);
        背包物品代码1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                背包物品代码1ActionPerformed(evt);
            }
        });
        jPanel40.add(背包物品代码1, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 550, 110, 30));

        jLabel276.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel276.setText("序号：");
        jPanel40.add(jLabel276, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 530, -1, 20));

        jLabel283.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel283.setText("物品名字：");
        jPanel40.add(jLabel283, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 530, -1, 20));

        jLabel287.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel287.setText("物品代码：");
        jPanel40.add(jLabel287, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 530, -1, 20));

        删除穿戴装备.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        删除穿戴装备.setForeground(new java.awt.Color(255, 51, 51));
        删除穿戴装备.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/关闭服务器.png"))); // NOI18N
        删除穿戴装备.setText("删除");
        删除穿戴装备.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                删除穿戴装备ActionPerformed(evt);
            }
        });
        jPanel40.add(删除穿戴装备, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 550, -1, 30));

        jTabbedPane5.addTab("身上穿戴", new javax.swing.ImageIcon(getClass().getResource("/Image/3801286.png")), jPanel40); // NOI18N

        jPanel41.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "装备背包", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 24))); // NOI18N
        jPanel41.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        角色装备背包.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        角色装备背包.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "序号", "物品代码", "物品名字"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        角色装备背包.getTableHeader().setReorderingAllowed(false);
        jScrollPane16.setViewportView(角色装备背包);

        jPanel41.add(jScrollPane16, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, 930, 480));

        装备背包物品名字.setEditable(false);
        装备背包物品名字.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                装备背包物品名字ActionPerformed(evt);
            }
        });
        jPanel41.add(装备背包物品名字, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 550, 150, 30));

        装备背包物品序号.setEditable(false);
        装备背包物品序号.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                装备背包物品序号ActionPerformed(evt);
            }
        });
        jPanel41.add(装备背包物品序号, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 550, 110, 30));

        装备背包物品代码.setEditable(false);
        装备背包物品代码.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                装备背包物品代码ActionPerformed(evt);
            }
        });
        jPanel41.add(装备背包物品代码, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 550, 110, 30));

        jLabel288.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel288.setText("序号：");
        jPanel41.add(jLabel288, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 530, -1, 20));

        jLabel289.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel289.setText("物品名字：");
        jPanel41.add(jLabel289, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 530, -1, 20));

        jLabel290.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel290.setText("物品代码：");
        jPanel41.add(jLabel290, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 530, -1, 20));

        删除装备背包.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        删除装备背包.setForeground(new java.awt.Color(204, 0, 0));
        删除装备背包.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/关闭服务器.png"))); // NOI18N
        删除装备背包.setText("删除");
        删除装备背包.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                删除装备背包ActionPerformed(evt);
            }
        });
        jPanel41.add(删除装备背包, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 550, -1, 30));

        jTabbedPane5.addTab("装备背包", new javax.swing.ImageIcon(getClass().getResource("/Image/3801287.png")), jPanel41); // NOI18N

        jPanel42.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "消耗背包", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 24))); // NOI18N
        jPanel42.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        角色消耗背包.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        角色消耗背包.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "序号", "物品代码", "物品名字", "物品数量"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        角色消耗背包.getTableHeader().setReorderingAllowed(false);
        jScrollPane17.setViewportView(角色消耗背包);

        jPanel42.add(jScrollPane17, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 940, 490));

        消耗背包物品名字.setEditable(false);
        消耗背包物品名字.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                消耗背包物品名字ActionPerformed(evt);
            }
        });
        jPanel42.add(消耗背包物品名字, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 550, 150, 30));

        消耗背包物品序号.setEditable(false);
        消耗背包物品序号.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                消耗背包物品序号ActionPerformed(evt);
            }
        });
        jPanel42.add(消耗背包物品序号, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 550, 110, 30));

        消耗背包物品代码.setEditable(false);
        消耗背包物品代码.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                消耗背包物品代码ActionPerformed(evt);
            }
        });
        jPanel42.add(消耗背包物品代码, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 550, 110, 30));

        jLabel291.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel291.setText("序号：");
        jPanel42.add(jLabel291, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 530, -1, 20));

        jLabel292.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel292.setText("物品名字：");
        jPanel42.add(jLabel292, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 530, -1, 20));

        jLabel293.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel293.setText("物品代码：");
        jPanel42.add(jLabel293, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 530, -1, 20));

        删除消耗背包.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        删除消耗背包.setForeground(new java.awt.Color(255, 0, 0));
        删除消耗背包.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/关闭服务器.png"))); // NOI18N
        删除消耗背包.setText("删除");
        删除消耗背包.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                删除消耗背包ActionPerformed(evt);
            }
        });
        jPanel42.add(删除消耗背包, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 550, -1, 30));

        jTabbedPane5.addTab("消耗背包", new javax.swing.ImageIcon(getClass().getResource("/Image/3801288.png")), jPanel42); // NOI18N

        jPanel43.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "设置背包", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 24))); // NOI18N
        jPanel43.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        角色设置背包.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        角色设置背包.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "序号", "物品代码", "物品名字", "物品数量"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        角色设置背包.getTableHeader().setReorderingAllowed(false);
        jScrollPane18.setViewportView(角色设置背包);

        jPanel43.add(jScrollPane18, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 940, 490));

        设置背包物品名字.setEditable(false);
        设置背包物品名字.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                设置背包物品名字ActionPerformed(evt);
            }
        });
        jPanel43.add(设置背包物品名字, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 550, 150, 30));

        设置背包物品序号.setEditable(false);
        设置背包物品序号.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                设置背包物品序号ActionPerformed(evt);
            }
        });
        jPanel43.add(设置背包物品序号, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 550, 110, 30));

        设置背包物品代码.setEditable(false);
        设置背包物品代码.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                设置背包物品代码ActionPerformed(evt);
            }
        });
        jPanel43.add(设置背包物品代码, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 550, 110, 30));

        jLabel294.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel294.setText("序号：");
        jPanel43.add(jLabel294, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 530, -1, 20));

        jLabel295.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel295.setText("物品名字：");
        jPanel43.add(jLabel295, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 530, -1, 20));

        jLabel296.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel296.setText("物品代码：");
        jPanel43.add(jLabel296, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 530, -1, 20));

        删除设置背包.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        删除设置背包.setForeground(new java.awt.Color(255, 0, 0));
        删除设置背包.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/关闭服务器.png"))); // NOI18N
        删除设置背包.setText("删除");
        删除设置背包.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                删除设置背包ActionPerformed(evt);
            }
        });
        jPanel43.add(删除设置背包, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 550, -1, 30));

        jTabbedPane5.addTab("设置背包", new javax.swing.ImageIcon(getClass().getResource("/Image/3801289.png")), jPanel43); // NOI18N

        jPanel44.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "其他背包", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 24))); // NOI18N
        jPanel44.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        角色其他背包.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        角色其他背包.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "序号", "物品代码", "物品名字", "物品数量"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        角色其他背包.getTableHeader().setReorderingAllowed(false);
        jScrollPane19.setViewportView(角色其他背包);

        jPanel44.add(jScrollPane19, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 940, 490));

        其他背包物品名字.setEditable(false);
        其他背包物品名字.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                其他背包物品名字ActionPerformed(evt);
            }
        });
        jPanel44.add(其他背包物品名字, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 550, 150, 30));

        其他背包物品序号.setEditable(false);
        其他背包物品序号.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                其他背包物品序号ActionPerformed(evt);
            }
        });
        jPanel44.add(其他背包物品序号, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 550, 110, 30));

        其他背包物品代码.setEditable(false);
        其他背包物品代码.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                其他背包物品代码ActionPerformed(evt);
            }
        });
        jPanel44.add(其他背包物品代码, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 550, 110, 30));

        jLabel297.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel297.setText("序号：");
        jPanel44.add(jLabel297, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 530, -1, 20));

        jLabel298.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel298.setText("物0品名字：");
        jPanel44.add(jLabel298, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 530, -1, 20));

        jLabel299.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel299.setText("物品代码：");
        jPanel44.add(jLabel299, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 530, -1, 20));

        删除其他背包.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        删除其他背包.setForeground(new java.awt.Color(255, 0, 0));
        删除其他背包.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/关闭服务器.png"))); // NOI18N
        删除其他背包.setText("删除");
        删除其他背包.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                删除其他背包ActionPerformed(evt);
            }
        });
        jPanel44.add(删除其他背包, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 550, -1, 30));

        jTabbedPane5.addTab("其他背包", new javax.swing.ImageIcon(getClass().getResource("/Image/3801290.png")), jPanel44); // NOI18N

        jPanel45.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "特殊背包", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 24))); // NOI18N
        jPanel45.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        角色特殊背包.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        角色特殊背包.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "序号", "物品代码", "物品名字", "物品数量"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        角色特殊背包.getTableHeader().setReorderingAllowed(false);
        jScrollPane20.setViewportView(角色特殊背包);

        jPanel45.add(jScrollPane20, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 940, 490));

        特殊背包物品名字.setEditable(false);
        特殊背包物品名字.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                特殊背包物品名字ActionPerformed(evt);
            }
        });
        jPanel45.add(特殊背包物品名字, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 550, 150, 30));

        特殊背包物品序号.setEditable(false);
        特殊背包物品序号.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                特殊背包物品序号ActionPerformed(evt);
            }
        });
        jPanel45.add(特殊背包物品序号, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 550, 110, 30));

        特殊背包物品代码.setEditable(false);
        特殊背包物品代码.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                特殊背包物品代码ActionPerformed(evt);
            }
        });
        jPanel45.add(特殊背包物品代码, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 550, 110, 30));

        jLabel300.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel300.setText("序号：");
        jPanel45.add(jLabel300, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 530, -1, 20));

        jLabel301.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel301.setText("物品名字：");
        jPanel45.add(jLabel301, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 530, -1, 20));

        jLabel302.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel302.setText("物品代码：");
        jPanel45.add(jLabel302, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 530, -1, 20));

        删除特殊背包.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        删除特殊背包.setForeground(new java.awt.Color(255, 0, 0));
        删除特殊背包.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/关闭服务器.png"))); // NOI18N
        删除特殊背包.setText("删除");
        删除特殊背包.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                删除特殊背包ActionPerformed(evt);
            }
        });
        jPanel45.add(删除特殊背包, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 550, -1, 30));

        jTabbedPane5.addTab("特殊背包", new javax.swing.ImageIcon(getClass().getResource("/Image/3801291.png")), jPanel45); // NOI18N

        jPanel46.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "游戏仓库", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 24))); // NOI18N
        jPanel46.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        角色游戏仓库.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        角色游戏仓库.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "序号", "物品代码", "物品名字", "物品数量"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        角色游戏仓库.getTableHeader().setReorderingAllowed(false);
        jScrollPane21.setViewportView(角色游戏仓库);

        jPanel46.add(jScrollPane21, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 940, 490));

        游戏仓库物品名字.setEditable(false);
        游戏仓库物品名字.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                游戏仓库物品名字ActionPerformed(evt);
            }
        });
        jPanel46.add(游戏仓库物品名字, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 550, 150, 30));

        游戏仓库物品序号.setEditable(false);
        游戏仓库物品序号.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                游戏仓库物品序号ActionPerformed(evt);
            }
        });
        jPanel46.add(游戏仓库物品序号, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 550, 110, 30));

        游戏仓库物品代码.setEditable(false);
        游戏仓库物品代码.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                游戏仓库物品代码ActionPerformed(evt);
            }
        });
        jPanel46.add(游戏仓库物品代码, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 550, 110, 30));

        jLabel303.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel303.setText("序号：");
        jPanel46.add(jLabel303, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 530, -1, 20));

        jLabel304.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel304.setText("物品名字：");
        jPanel46.add(jLabel304, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 530, -1, 20));

        jLabel305.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel305.setText("物品代码：");
        jPanel46.add(jLabel305, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 530, -1, 20));

        删除游戏仓库.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        删除游戏仓库.setForeground(new java.awt.Color(255, 0, 0));
        删除游戏仓库.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/关闭服务器.png"))); // NOI18N
        删除游戏仓库.setText("删除");
        删除游戏仓库.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                删除游戏仓库ActionPerformed(evt);
            }
        });
        jPanel46.add(删除游戏仓库, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 550, -1, 30));

        jTabbedPane5.addTab("游戏仓库", new javax.swing.ImageIcon(getClass().getResource("/Image/3801292.png")), jPanel46); // NOI18N

        jPanel47.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "商城仓库", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 24))); // NOI18N
        jPanel47.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        角色商城仓库.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        角色商城仓库.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "序号", "物品代码", "物品名字", "物品数量"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        角色商城仓库.getTableHeader().setReorderingAllowed(false);
        jScrollPane22.setViewportView(角色商城仓库);

        jPanel47.add(jScrollPane22, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 940, 490));

        商城仓库物品名字.setEditable(false);
        商城仓库物品名字.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                商城仓库物品名字ActionPerformed(evt);
            }
        });
        jPanel47.add(商城仓库物品名字, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 550, 150, 30));

        商城仓库物品序号.setEditable(false);
        商城仓库物品序号.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                商城仓库物品序号ActionPerformed(evt);
            }
        });
        jPanel47.add(商城仓库物品序号, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 550, 110, 30));

        商城仓库物品代码.setEditable(false);
        商城仓库物品代码.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                商城仓库物品代码ActionPerformed(evt);
            }
        });
        jPanel47.add(商城仓库物品代码, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 550, 110, 30));

        jLabel306.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel306.setText("序号：");
        jPanel47.add(jLabel306, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 530, -1, 20));

        jLabel307.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel307.setText("物品名字：");
        jPanel47.add(jLabel307, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 530, -1, 20));

        jLabel308.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel308.setText("物品代码：");
        jPanel47.add(jLabel308, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 530, -1, 20));

        删除商城仓库.setForeground(new java.awt.Color(255, 0, 0));
        删除商城仓库.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/关闭服务器.png"))); // NOI18N
        删除商城仓库.setText("删除");
        删除商城仓库.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                删除商城仓库ActionPerformed(evt);
            }
        });
        jPanel47.add(删除商城仓库, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 550, -1, 30));

        jTabbedPane5.addTab("商城仓库", new javax.swing.ImageIcon(getClass().getResource("/Image/3801293.png")), jPanel47); // NOI18N

        jPanel48.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "点券拍卖行", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 24))); // NOI18N
        jPanel48.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        角色点券拍卖行.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        角色点券拍卖行.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "序号", "物品代码", "物品名字", "物品数量"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        角色点券拍卖行.getTableHeader().setReorderingAllowed(false);
        jScrollPane30.setViewportView(角色点券拍卖行);

        jPanel48.add(jScrollPane30, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 940, 490));

        拍卖行物品名字1.setEditable(false);
        拍卖行物品名字1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                拍卖行物品名字1ActionPerformed(evt);
            }
        });
        jPanel48.add(拍卖行物品名字1, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 550, 150, 30));

        角色点券拍卖行序号.setEditable(false);
        角色点券拍卖行序号.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                角色点券拍卖行序号ActionPerformed(evt);
            }
        });
        jPanel48.add(角色点券拍卖行序号, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 550, 110, 30));

        拍卖行物品代码1.setEditable(false);
        拍卖行物品代码1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                拍卖行物品代码1ActionPerformed(evt);
            }
        });
        jPanel48.add(拍卖行物品代码1, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 550, 110, 30));

        jLabel354.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel354.setText("序号：");
        jPanel48.add(jLabel354, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 530, -1, 20));

        jLabel355.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel355.setText("物品名字：");
        jPanel48.add(jLabel355, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 530, -1, 20));

        jLabel356.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel356.setText("物品代码：");
        jPanel48.add(jLabel356, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 530, -1, 20));

        删除拍卖行1.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        删除拍卖行1.setForeground(new java.awt.Color(204, 0, 0));
        删除拍卖行1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/关闭服务器.png"))); // NOI18N
        删除拍卖行1.setText("删除");
        删除拍卖行1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                删除拍卖行1ActionPerformed(evt);
            }
        });
        jPanel48.add(删除拍卖行1, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 550, -1, 30));

        jTabbedPane5.addTab("点券拍卖行", new javax.swing.ImageIcon(getClass().getResource("/Image/3801294.png")), jPanel48); // NOI18N

        jPanel56.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "金币拍卖行", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 24))); // NOI18N
        jPanel56.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        角色金币拍卖行.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        角色金币拍卖行.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "序号", "物品代码", "物品名字"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        角色金币拍卖行.getTableHeader().setReorderingAllowed(false);
        jScrollPane23.setViewportView(角色金币拍卖行);

        jPanel56.add(jScrollPane23, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 950, 490));

        拍卖行物品名字.setEditable(false);
        拍卖行物品名字.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                拍卖行物品名字ActionPerformed(evt);
            }
        });
        jPanel56.add(拍卖行物品名字, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 550, 150, 30));

        角色金币拍卖行序号.setEditable(false);
        角色金币拍卖行序号.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                角色金币拍卖行序号ActionPerformed(evt);
            }
        });
        jPanel56.add(角色金币拍卖行序号, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 550, 110, 30));

        拍卖行物品代码.setEditable(false);
        拍卖行物品代码.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                拍卖行物品代码ActionPerformed(evt);
            }
        });
        jPanel56.add(拍卖行物品代码, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 550, 110, 30));

        jLabel309.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel309.setText("序号：");
        jPanel56.add(jLabel309, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 530, -1, 20));

        jLabel310.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel310.setText("物品名字：");
        jPanel56.add(jLabel310, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 530, -1, 20));

        jLabel311.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel311.setText("物品代码：");
        jPanel56.add(jLabel311, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 530, -1, 20));

        删除拍卖行.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        删除拍卖行.setForeground(new java.awt.Color(255, 0, 0));
        删除拍卖行.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/关闭服务器.png"))); // NOI18N
        删除拍卖行.setText("删除");
        删除拍卖行.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                删除拍卖行ActionPerformed(evt);
            }
        });
        jPanel56.add(删除拍卖行, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 550, -1, 30));

        jTabbedPane5.addTab("金币拍卖行", new javax.swing.ImageIcon(getClass().getResource("/Image/3801296.png")), jPanel56); // NOI18N

        角色背包.add(jTabbedPane5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 970, 620));

        jTabbedPane8.addTab("角色道具信息", new javax.swing.ImageIcon(getClass().getResource("/Image/管理背包.png")), 角色背包); // NOI18N

        技能.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "角色技能", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 24))); // NOI18N
        技能.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        技能信息.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        技能信息.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "序号", "技能名字", "技能代码", "目前等级", "最高等级"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        技能信息.getTableHeader().setReorderingAllowed(false);
        jScrollPane14.setViewportView(技能信息);

        技能.add(jScrollPane14, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 980, 430));

        技能代码.setEditable(false);
        技能代码.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        技能.add(技能代码, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 490, 120, 30));

        技能目前等级.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        技能.add(技能目前等级, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 490, 120, 30));

        技能最高等级.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        技能.add(技能最高等级, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 490, 120, 30));

        技能名字.setEditable(false);
        技能名字.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        技能名字.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                技能名字ActionPerformed(evt);
            }
        });
        技能.add(技能名字, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 490, 120, 30));

        jLabel86.setFont(new java.awt.Font("幼圆", 0, 18)); // NOI18N
        jLabel86.setText("技能代码：");
        技能.add(jLabel86, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 460, -1, -1));

        jLabel89.setFont(new java.awt.Font("幼圆", 0, 18)); // NOI18N
        jLabel89.setText("目前等级：");
        技能.add(jLabel89, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 460, -1, -1));

        jLabel107.setFont(new java.awt.Font("幼圆", 0, 18)); // NOI18N
        jLabel107.setText("最高等级：");
        技能.add(jLabel107, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 460, -1, -1));

        修改技能.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        修改技能.setForeground(new java.awt.Color(255, 0, 0));
        修改技能.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/101.png"))); // NOI18N
        修改技能.setText("修改");
        修改技能.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                修改技能ActionPerformed(evt);
            }
        });
        技能.add(修改技能, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 530, 120, 40));

        技能序号.setEditable(false);
        技能序号.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        技能.add(技能序号, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 490, 80, 30));

        jLabel188.setFont(new java.awt.Font("幼圆", 0, 18)); // NOI18N
        jLabel188.setText("技能名字：");
        技能.add(jLabel188, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 460, -1, -1));

        jLabel204.setFont(new java.awt.Font("幼圆", 0, 18)); // NOI18N
        jLabel204.setText("序号：");
        技能.add(jLabel204, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 460, -1, -1));

        jLabel205.setFont(new java.awt.Font("幼圆", 0, 24)); // NOI18N
        jLabel205.setText("提示;技能无法超出正常范围值。");
        技能.add(jLabel205, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 580, 360, 30));

        删除技能.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        删除技能.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/关闭服务器.png"))); // NOI18N
        删除技能.setText("删除");
        删除技能.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                删除技能ActionPerformed(evt);
            }
        });
        技能.add(删除技能, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 530, 120, 40));

        修改技能1.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        修改技能1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/后台.png"))); // NOI18N
        修改技能1.setText("刷新");
        修改技能1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                修改技能1ActionPerformed(evt);
            }
        });
        技能.add(修改技能1, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 530, 120, 40));

        jTabbedPane8.addTab("角色技能信息", new javax.swing.ImageIcon(getClass().getResource("/Image/问题.png")), 技能); // NOI18N

        javax.swing.GroupLayout jPanel26Layout = new javax.swing.GroupLayout(jPanel26);
        jPanel26.setLayout(jPanel26Layout);
        jPanel26Layout.setHorizontalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addComponent(jTabbedPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 977, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel26Layout.setVerticalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 723, Short.MAX_VALUE)
        );

        jTabbedPane.addTab("角色中心", new javax.swing.ImageIcon(getClass().getResource("/Image/3994264.png")), jPanel26); // NOI18N

        jPanel57.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "全服发送福利", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 12))); // NOI18N
        jPanel57.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        全服发送物品数量.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                全服发送物品数量ActionPerformed(evt);
            }
        });
        jPanel57.add(全服发送物品数量, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 60, 100, 30));

        全服发送物品代码.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                全服发送物品代码ActionPerformed(evt);
            }
        });
        jPanel57.add(全服发送物品代码, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, 110, 30));

        给予物品1.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        给予物品1.setForeground(new java.awt.Color(255, 0, 0));
        给予物品1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/关闭.png"))); // NOI18N
        给予物品1.setText("确认发送");
        给予物品1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                给予物品1ActionPerformed(evt);
            }
        });
        jPanel57.add(给予物品1, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 60, 120, 30));

        jLabel217.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel217.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/发送物品.png"))); // NOI18N
        jLabel217.setText("物品数量");
        jPanel57.add(jLabel217, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 40, -1, -1));

        jLabel234.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel234.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/装备修改.png"))); // NOI18N
        jLabel234.setText("物品代码");
        jPanel57.add(jLabel234, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, -1, -1));

        jPanel80.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "个人发送福利", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 12))); // NOI18N
        jPanel80.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        z7.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        z7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/3801314.png"))); // NOI18N
        z7.setText("发送抵用");
        z7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                z7ActionPerformed(evt);
            }
        });
        jPanel80.add(z7, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 150, 110, 30));

        z8.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        z8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/3801316.png"))); // NOI18N
        z8.setText("发送金币");
        z8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                z8ActionPerformed(evt);
            }
        });
        jPanel80.add(z8, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 200, 110, 30));

        z9.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        z9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/3801312.png"))); // NOI18N
        z9.setText("发送点券");
        z9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                z9ActionPerformed(evt);
            }
        });
        jPanel80.add(z9, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 100, 110, 30));

        z10.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        z10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/3801313.png"))); // NOI18N
        z10.setText("发送经验");
        z10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                z10ActionPerformed(evt);
            }
        });
        jPanel80.add(z10, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 100, 110, 30));

        z11.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        z11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/3801315.png"))); // NOI18N
        z11.setText("发送人气");
        z11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                z11ActionPerformed(evt);
            }
        });
        jPanel80.add(z11, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 150, 110, 30));

        z12.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        z12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/3801462.png"))); // NOI18N
        z12.setText("发送豆豆");
        z12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                z12ActionPerformed(evt);
            }
        });
        jPanel80.add(z12, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 200, 110, 30));

        a2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                a2ActionPerformed(evt);
            }
        });
        jPanel80.add(a2, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 40, 100, 30));

        jLabel236.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel236.setText("数量");
        jPanel80.add(jLabel236, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 40, 40, 30));

        个人发送物品玩家名字1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                个人发送物品玩家名字1ActionPerformed(evt);
            }
        });
        jPanel80.add(个人发送物品玩家名字1, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 40, 120, 30));

        jLabel59.setFont(new java.awt.Font("宋体", 0, 14)); // NOI18N
        jLabel59.setText("玩家名字");
        jPanel80.add(jLabel59, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 46, -1, 20));

        jPanel60.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "个人发送物品", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 12))); // NOI18N
        jPanel60.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        个人发送物品数量.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                个人发送物品数量ActionPerformed(evt);
            }
        });
        jPanel60.add(个人发送物品数量, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 70, 80, 30));

        个人发送物品玩家名字.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                个人发送物品玩家名字ActionPerformed(evt);
            }
        });
        jPanel60.add(个人发送物品玩家名字, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, 130, 30));

        个人发送物品代码.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                个人发送物品代码ActionPerformed(evt);
            }
        });
        jPanel60.add(个人发送物品代码, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 70, 130, 30));

        给予物品.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        给予物品.setForeground(new java.awt.Color(255, 51, 51));
        给予物品.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/关闭.png"))); // NOI18N
        给予物品.setText("确认发送");
        给予物品.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                给予物品ActionPerformed(evt);
            }
        });
        jPanel60.add(给予物品, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 70, 120, 30));

        jLabel240.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel240.setText("物品数量:");
        jPanel60.add(jLabel240, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 50, -1, -1));

        jLabel241.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel241.setText("玩家名字");
        jPanel60.add(jLabel241, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, -1, -1));

        jLabel242.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel242.setText("物品代码");
        jPanel60.add(jLabel242, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 50, -1, -1));

        jPanel58.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "全服发送福利", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 12))); // NOI18N
        jPanel58.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        全服发送装备装备加卷.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                全服发送装备装备加卷ActionPerformed(evt);
            }
        });
        jPanel58.add(全服发送装备装备加卷, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 100, 100, 30));

        全服发送装备装备制作人.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                全服发送装备装备制作人ActionPerformed(evt);
            }
        });
        jPanel58.add(全服发送装备装备制作人, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 220, 100, 30));

        全服发送装备装备力量.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                全服发送装备装备力量ActionPerformed(evt);
            }
        });
        jPanel58.add(全服发送装备装备力量, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 160, 100, 30));

        全服发送装备装备MP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                全服发送装备装备MPActionPerformed(evt);
            }
        });
        jPanel58.add(全服发送装备装备MP, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 100, 100, 30));

        全服发送装备装备智力.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                全服发送装备装备智力ActionPerformed(evt);
            }
        });
        jPanel58.add(全服发送装备装备智力, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 160, 100, 30));

        全服发送装备装备运气.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                全服发送装备装备运气ActionPerformed(evt);
            }
        });
        jPanel58.add(全服发送装备装备运气, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 100, 100, 30));

        全服发送装备装备HP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                全服发送装备装备HPActionPerformed(evt);
            }
        });
        jPanel58.add(全服发送装备装备HP, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 100, 100, 30));

        全服发送装备装备攻击力.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                全服发送装备装备攻击力ActionPerformed(evt);
            }
        });
        jPanel58.add(全服发送装备装备攻击力, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 40, 100, 30));

        全服发送装备装备给予时间.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                全服发送装备装备给予时间ActionPerformed(evt);
            }
        });
        jPanel58.add(全服发送装备装备给予时间, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 220, 100, 30));

        全服发送装备装备可否交易.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                全服发送装备装备可否交易ActionPerformed(evt);
            }
        });
        jPanel58.add(全服发送装备装备可否交易, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 160, 100, 30));

        全服发送装备装备敏捷.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                全服发送装备装备敏捷ActionPerformed(evt);
            }
        });
        jPanel58.add(全服发送装备装备敏捷, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 160, 100, 30));

        全服发送装备物品ID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                全服发送装备物品IDActionPerformed(evt);
            }
        });
        jPanel58.add(全服发送装备物品ID, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 40, 100, 30));

        全服发送装备装备魔法力.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                全服发送装备装备魔法力ActionPerformed(evt);
            }
        });
        jPanel58.add(全服发送装备装备魔法力, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 40, 100, 30));

        全服发送装备装备魔法防御.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                全服发送装备装备魔法防御ActionPerformed(evt);
            }
        });
        jPanel58.add(全服发送装备装备魔法防御, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 220, 100, 30));

        全服发送装备装备物理防御.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                全服发送装备装备物理防御ActionPerformed(evt);
            }
        });
        jPanel58.add(全服发送装备装备物理防御, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 220, 100, 30));

        给予装备1.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        给予装备1.setForeground(new java.awt.Color(255, 0, 0));
        给予装备1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/关闭.png"))); // NOI18N
        给予装备1.setText("个人发送");
        给予装备1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                给予装备1ActionPerformed(evt);
            }
        });
        jPanel58.add(给予装备1, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 330, 130, 30));

        jLabel219.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel219.setText("能否交易填0");
        jPanel58.add(jLabel219, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 140, -1, -1));

        jLabel220.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel220.setText("HP加成");
        jPanel58.add(jLabel220, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 80, -1, -1));

        jLabel221.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel221.setText("魔法攻击力");
        jPanel58.add(jLabel221, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 20, -1, -1));

        jLabel222.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel222.setText("装备代码");
        jPanel58.add(jLabel222, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 20, -1, -1));

        jLabel223.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel223.setText("MP加成");
        jPanel58.add(jLabel223, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, -1, -1));

        jLabel224.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel224.setText("物理攻击力");
        jPanel58.add(jLabel224, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 20, -1, -1));

        jLabel225.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel225.setText("可砸卷次数");
        jPanel58.add(jLabel225, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 80, -1, -1));

        jLabel226.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel226.setText("装备署名");
        jPanel58.add(jLabel226, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 200, -1, -1));

        jLabel227.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel227.setText("装备力量");
        jPanel58.add(jLabel227, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 140, -1, -1));

        jLabel228.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel228.setText("装备敏捷");
        jPanel58.add(jLabel228, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 140, -1, -1));

        jLabel229.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel229.setText("装备智力");
        jPanel58.add(jLabel229, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 140, -1, -1));

        jLabel230.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel230.setText("装备运气");
        jPanel58.add(jLabel230, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 80, -1, -1));

        jLabel231.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel231.setText("魔法防御");
        jPanel58.add(jLabel231, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 200, -1, -1));

        jLabel232.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel232.setText("物理防御");
        jPanel58.add(jLabel232, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 200, -1, -1));

        jLabel233.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel233.setText("限时时间");
        jPanel58.add(jLabel233, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 200, -1, -1));

        发送装备玩家姓名.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                发送装备玩家姓名ActionPerformed(evt);
            }
        });
        jPanel58.add(发送装备玩家姓名, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, 100, 30));

        给予装备2.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        给予装备2.setForeground(new java.awt.Color(255, 0, 0));
        给予装备2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/关闭.png"))); // NOI18N
        给予装备2.setText("全服发送");
        给予装备2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                给予装备2ActionPerformed(evt);
            }
        });
        jPanel58.add(给予装备2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 330, 130, 30));

        jLabel246.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel246.setText("玩家名字");
        jPanel58.add(jLabel246, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, -1));

        jLabel244.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel244.setForeground(new java.awt.Color(255, 51, 51));
        jLabel244.setText("个人发送需要填写名字");
        jPanel58.add(jLabel244, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 340, -1, -1));

        全服发送装备装备潜能3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                全服发送装备装备潜能3ActionPerformed(evt);
            }
        });
        jPanel58.add(全服发送装备装备潜能3, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 280, 100, 30));

        全服发送装备装备潜能1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                全服发送装备装备潜能1ActionPerformed(evt);
            }
        });
        jPanel58.add(全服发送装备装备潜能1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 280, 100, 30));

        全服发送装备装备潜能2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                全服发送装备装备潜能2ActionPerformed(evt);
            }
        });
        jPanel58.add(全服发送装备装备潜能2, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 280, 100, 30));

        jLabel72.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        jLabel72.setText("潜能1");
        jPanel58.add(jLabel72, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 260, -1, -1));

        jLabel73.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        jLabel73.setText("潜能2");
        jPanel58.add(jLabel73, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 260, -1, -1));

        jLabel74.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        jLabel74.setText("潜能3");
        jPanel58.add(jLabel74, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 260, -1, -1));

        qiannengdaima.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/通用设置.png"))); // NOI18N
        qiannengdaima.setText("查看潜能代码");
        qiannengdaima.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                qiannengdaimaActionPerformed(evt);
            }
        });
        jPanel58.add(qiannengdaima, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 273, -1, 40));

        jPanel59.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "全服发送福利", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 12))); // NOI18N
        jPanel59.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        z2.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        z2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/3801314.png"))); // NOI18N
        z2.setText("发送抵用");
        z2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                z2ActionPerformed(evt);
            }
        });
        jPanel59.add(z2, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 150, 110, 30));

        z3.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        z3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/3801316.png"))); // NOI18N
        z3.setText("发送金币");
        z3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                z3ActionPerformed(evt);
            }
        });
        jPanel59.add(z3, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 200, 110, 30));

        z1.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        z1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/3801312.png"))); // NOI18N
        z1.setText("发送点券");
        z1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                z1ActionPerformed(evt);
            }
        });
        jPanel59.add(z1, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 100, 110, 30));

        z4.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        z4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/3801313.png"))); // NOI18N
        z4.setText("发送经验");
        z4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                z4ActionPerformed(evt);
            }
        });
        jPanel59.add(z4, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 100, 110, 30));

        z5.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        z5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/3801315.png"))); // NOI18N
        z5.setText("发送人气");
        z5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                z5ActionPerformed(evt);
            }
        });
        jPanel59.add(z5, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 150, 110, 30));

        z6.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        z6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/3801462.png"))); // NOI18N
        z6.setText("发送豆豆");
        z6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                z6ActionPerformed(evt);
            }
        });
        jPanel59.add(z6, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 200, 110, 30));

        a1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                a1ActionPerformed(evt);
            }
        });
        jPanel59.add(a1, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 40, 100, 30));

        jLabel235.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel235.setText("数量");
        jPanel59.add(jLabel235, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 50, -1, -1));

        javax.swing.GroupLayout jPanel27Layout = new javax.swing.GroupLayout(jPanel27);
        jPanel27.setLayout(jPanel27Layout);
        jPanel27Layout.setHorizontalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel27Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel58, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel57, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel60, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel80, javax.swing.GroupLayout.DEFAULT_SIZE, 447, Short.MAX_VALUE)
                    .addComponent(jPanel59, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel27Layout.setVerticalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel27Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel27Layout.createSequentialGroup()
                        .addComponent(jPanel80, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jPanel59, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel27Layout.createSequentialGroup()
                        .addComponent(jPanel57, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jPanel60, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel58, javax.swing.GroupLayout.DEFAULT_SIZE, 444, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jTabbedPane.addTab("发送装备", new javax.swing.ImageIcon(getClass().getResource("/Image/4031041.png")), jPanel27); // NOI18N

        jPanel54.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "转存数据[非开发者请勿点击下方功能]", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("微软雅黑", 0, 12), new java.awt.Color(255, 0, 0))); // NOI18N

        jButton69.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/错误日志.png"))); // NOI18N
        jButton69.setText("更新物品道具");
        jButton69.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton69ActionPerformed(evt);
            }
        });

        jButton70.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/错误日志.png"))); // NOI18N
        jButton70.setText("导出爆物数据");
        jButton70.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton70ActionPerformed(evt);
            }
        });

        jButton72.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/错误日志.png"))); // NOI18N
        jButton72.setText("更新NPC名称");
        jButton72.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton72ActionPerformed(evt);
            }
        });

        jButton73.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/错误日志.png"))); // NOI18N
        jButton73.setText("更新怪物技能");
        jButton73.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton73ActionPerformed(evt);
            }
        });

        jButton74.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/错误日志.png"))); // NOI18N
        jButton74.setText("更新问答数据");
        jButton74.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton74ActionPerformed(evt);
            }
        });

        jButton75.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/错误日志.png"))); // NOI18N
        jButton75.setText("更新任务数据");
        jButton75.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton75ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel54Layout = new javax.swing.GroupLayout(jPanel54);
        jPanel54.setLayout(jPanel54Layout);
        jPanel54Layout.setHorizontalGroup(
            jPanel54Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel54Layout.createSequentialGroup()
                .addGroup(jPanel54Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel54Layout.createSequentialGroup()
                        .addGap(144, 144, 144)
                        .addComponent(jButton70, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel54Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel54Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel54Layout.createSequentialGroup()
                                .addComponent(jButton73, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton74, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel54Layout.createSequentialGroup()
                                .addComponent(jButton69, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(26, 26, 26)
                                .addComponent(jButton72, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButton75, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(28, Short.MAX_VALUE))
        );
        jPanel54Layout.setVerticalGroup(
            jPanel54Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel54Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(jPanel54Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton69, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton72, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton75, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel54Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton73, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton74, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton70, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(32, Short.MAX_VALUE))
        );

        jPanel55.setBorder(javax.swing.BorderFactory.createTitledBorder("其他工具"));

        jButton31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/3801297.png"))); // NOI18N
        jButton31.setText("代码查询器");
        jButton31.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton31ActionPerformed(evt);
            }
        });

        jButton22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/3801296.png"))); // NOI18N
        jButton22.setText("基址计算工具");
        jButton22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton22ActionPerformed(evt);
            }
        });

        jButton23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/3801295.png"))); // NOI18N
        jButton23.setText("包头转换工具");
        jButton23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton23ActionPerformed(evt);
            }
        });

        jButton28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/3801294.png"))); // NOI18N
        jButton28.setText("角色转移工具");
        jButton28.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton28ActionPerformed(evt);
            }
        });

        jButton29.setForeground(new java.awt.Color(255, 51, 51));
        jButton29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/错误日志.png"))); // NOI18N
        jButton29.setText("一键清空数据库");
        jButton29.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton29ActionPerformed(evt);
            }
        });

        jButton39.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/3801293.png"))); // NOI18N
        jButton39.setText("添加删除NPC");
        jButton39.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton39ActionPerformed(evt);
            }
        });

        jButton44.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/3801292.png"))); // NOI18N
        jButton44.setText("游戏抽奖管理工具");
        jButton44.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton44ActionPerformed(evt);
            }
        });

        jButton51.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/3801291.png"))); // NOI18N
        jButton51.setText("批量删除物品");
        jButton51.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton51ActionPerformed(evt);
            }
        });

        jButton53.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/3801290.png"))); // NOI18N
        jButton53.setText("MACip封禁");
        jButton53.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton53ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel55Layout = new javax.swing.GroupLayout(jPanel55);
        jPanel55.setLayout(jPanel55Layout);
        jPanel55Layout.setHorizontalGroup(
            jPanel55Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel55Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel55Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton31, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton28, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(20, 20, 20)
                .addGroup(jPanel55Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel55Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jButton22, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton39, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton51, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(jPanel55Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton44, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton53, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel55Layout.setVerticalGroup(
            jPanel55Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel55Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(jPanel55Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton31, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton22, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton23, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24)
                .addGroup(jPanel55Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton28, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton39, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton44, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel55Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton29, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                    .addComponent(jButton51, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton53, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton39.getAccessibleContext().setAccessibleName("自添加NPC删除工具");

        jPanel67.setBorder(javax.swing.BorderFactory.createTitledBorder("其他控制台"));

        jButton25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/代码查询器.png"))); // NOI18N
        jButton25.setText("商城CDK卡密控制台");
        jButton25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton25ActionPerformed(evt);
            }
        });

        jButton32.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/商店管理.png"))); // NOI18N
        jButton32.setText("商城管理控制台");
        jButton32.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton32ActionPerformed(evt);
            }
        });

        jButton33.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/5062006.png"))); // NOI18N
        jButton33.setText("锻造控制台");
        jButton33.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton33ActionPerformed(evt);
            }
        });

        jButton34.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/4030011.png"))); // NOI18N
        jButton34.setText("游戏副本控制台");
        jButton34.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton34ActionPerformed(evt);
            }
        });

        jButton40.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/召唤生物.png"))); // NOI18N
        jButton40.setText("野外BOSS刷新时间");
        jButton40.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton40ActionPerformed(evt);
            }
        });

        jButton41.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/2000001.png"))); // NOI18N
        jButton41.setText("药水冷却时间控制台");
        jButton41.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton41ActionPerformed(evt);
            }
        });

        jButton42.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/2470000.png"))); // NOI18N
        jButton42.setText("金锤子成功率控制台");
        jButton42.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton42ActionPerformed(evt);
            }
        });

        jButton43.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/2614075.png"))); // NOI18N
        jButton43.setText("永恒重生装备控制台");
        jButton43.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton43ActionPerformed(evt);
            }
        });

        jButton46.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/提交问题反馈.png"))); // NOI18N
        jButton46.setText("OX答题控制台");
        jButton46.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton46ActionPerformed(evt);
            }
        });

        jButton47.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/钓鱼.png"))); // NOI18N
        jButton47.setText("钓鱼控制台");
        jButton47.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton47ActionPerformed(evt);
            }
        });

        jButton48.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/4031683.png"))); // NOI18N
        jButton48.setText("鱼来鱼往");
        jButton48.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton48ActionPerformed(evt);
            }
        });

        jButton49.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/3010025.png"))); // NOI18N
        jButton49.setText("椅子控制台");
        jButton49.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton49ActionPerformed(evt);
            }
        });

        jButton36.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/2070005.png"))); // NOI18N
        jButton36.setText("商店管理控制台");
        jButton36.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton36ActionPerformed(evt);
            }
        });

        jButton37.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/4110000.png"))); // NOI18N
        jButton37.setText("怪物爆率控制台");
        jButton37.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton37ActionPerformed(evt);
            }
        });

        jButton50.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/9104092.png"))); // NOI18N
        jButton50.setText("箱子爆率控制台");
        jButton50.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton50ActionPerformed(evt);
            }
        });

        jButton52.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Icon.png"))); // NOI18N
        jButton52.setText("游戏家族控制台");
        jButton52.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton52ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel67Layout = new javax.swing.GroupLayout(jPanel67);
        jPanel67.setLayout(jPanel67Layout);
        jPanel67Layout.setHorizontalGroup(
            jPanel67Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel67Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel67Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel67Layout.createSequentialGroup()
                        .addComponent(jButton33, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jButton34, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jButton40, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jButton41, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jButton49, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel67Layout.createSequentialGroup()
                        .addComponent(jButton43, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jButton25, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jButton42, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel67Layout.createSequentialGroup()
                        .addComponent(jButton46, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jButton47, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jButton48, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel67Layout.createSequentialGroup()
                        .addComponent(jButton32, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jButton36, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jButton37, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jButton50, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jButton52, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(64, Short.MAX_VALUE))
        );
        jPanel67Layout.setVerticalGroup(
            jPanel67Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel67Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(jPanel67Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                    .addComponent(jButton32, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton36, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton37, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton50, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton52, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel67Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                    .addComponent(jButton33, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton34, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton40, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton41, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton49, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel67Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton46, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton47, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton48, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel67Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton43, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton25, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton42, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(136, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel35Layout = new javax.swing.GroupLayout(jPanel35);
        jPanel35.setLayout(jPanel35Layout);
        jPanel35Layout.setHorizontalGroup(
            jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel35Layout.createSequentialGroup()
                .addGroup(jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel35Layout.createSequentialGroup()
                        .addComponent(jPanel54, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel55, javax.swing.GroupLayout.PREFERRED_SIZE, 483, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel67, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel35Layout.setVerticalGroup(
            jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel35Layout.createSequentialGroup()
                .addGroup(jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel54, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel55, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel67, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(67, 67, 67))
        );

        jTabbedPane.addTab("更多功能", new javax.swing.ImageIcon(getClass().getResource("/Image/1802034.png")), jPanel35); // NOI18N

        jPanel36.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "关于我们", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("微软雅黑", 0, 12))); // NOI18N

        jLabel9.setText("Ver:085服务端 [正版授权:夜空冒险岛]");

        jLabel12.setText("游戏中遇到BUG请提交到作者");

        jLabel10.setText("未经作者授权一律盗版,后果自负 ");

        javax.swing.GroupLayout jPanel36Layout = new javax.swing.GroupLayout(jPanel36);
        jPanel36.setLayout(jPanel36Layout);
        jPanel36Layout.setHorizontalGroup(
            jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel36Layout.createSequentialGroup()
                .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel36Layout.createSequentialGroup()
                        .addGap(295, 295, 295)
                        .addComponent(jLabel9))
                    .addGroup(jPanel36Layout.createSequentialGroup()
                        .addGap(347, 347, 347)
                        .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addGroup(jPanel36Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jLabel12)))))
                .addContainerGap(437, Short.MAX_VALUE))
        );
        jPanel36Layout.setVerticalGroup(
            jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel36Layout.createSequentialGroup()
                .addGap(61, 61, 61)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12)
                .addContainerGap(484, Short.MAX_VALUE))
        );

        jTabbedPane.addTab("关于我们", new javax.swing.ImageIcon(getClass().getResource("/Image/4310003.png")), jPanel36); // NOI18N

        ActiveThread.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        ActiveThread.setForeground(new java.awt.Color(255, 51, 51));
        ActiveThread.setText("游戏线程:0");

        RunStats.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        RunStats.setText("运行状态:已关闭");
        RunStats.setMaximumSize(new java.awt.Dimension(110, 15));
        RunStats.setMinimumSize(new java.awt.Dimension(110, 15));

        RunTime.setFont(new java.awt.Font("微软雅黑", 0, 12)); // NOI18N
        RunTime.setText("运行时长：0:0:0:0");
        RunTime.setMaximumSize(new java.awt.Dimension(150, 15));
        RunTime.setMinimumSize(new java.awt.Dimension(150, 15));
        RunTime.setName(""); // NOI18N
        RunTime.setPreferredSize(new java.awt.Dimension(200, 15));
        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(ActiveThread)
                .addGap(32, 32, 32)
                .addComponent(RunStats, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(RunTime, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 1107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 664, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ActiveThread)
                    .addComponent(RunStats, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(RunTime, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents
     private void setRunStats(boolean on) {
        jButton1.setEnabled(!on);
     //   ShutdownGameServer.setEnabled(on);
        RunStats.setText("运行状态：" + (on ? "正在运行" : "已关闭"));
    }
    
    private void startRunTime() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                startRunTime = GuiTimer.getInstance().register(new Runnable() {
                    @Override
                    public void run() {
                        RunTime.setText(formatDuring(System.currentTimeMillis() - starttime));
                    }
                }, 1000);
            }
        }).start();
    }
    
    public static final String formatDuring(long mss) {
        long days = mss / (1000 * 60 * 60 * 24);
        long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (mss % (1000 * 60)) / 1000;
        return "运行时长:" + days + "天" + (hours / 10 == 0 ? "0" : "") + hours + "时" + (minutes / 10 == 0 ? "0" : "") + minutes + "分"
                + (seconds / 10 == 0 ? "0" : "") + seconds + "秒";
    }

    public void updateThreadNum() {
        writeLock.lock();
        try {
            GuiTimer.getInstance().register(new Runnable() {
                @Override
                public final void run() {
                    ActiveThread.setText("游戏线程：" + Thread.activeCount() + "");
                }
            }, 1 * 1000);
        } finally {
            writeLock.unlock();
        }
    }
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        int result = JOptionPane.showConfirmDialog(this, "直接关闭服务端又可能不能及时存档,会造成数据丢失或者回档,是否继续?\n" +"正常关闭服务端请在启动服务内点击关闭服务端按钮,等待系统自动关闭.", "温馨提示", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }//GEN-LAST:event_formWindowClosing

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
    }//GEN-LAST:event_formWindowOpened

    private void 重载副本按钮ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_重载副本按钮ActionPerformed
        // TODO add your handling code here:
        for (ChannelServer instance1 : ChannelServer.getAllInstances()) {
            if (instance1 != null) {
                instance1.reloadEvents();
            }
        }
        System.out.println("[重载系统] 副本重载成功。");
        JOptionPane.showMessageDialog(null, "副本重载成功。");
    }//GEN-LAST:event_重载副本按钮ActionPerformed

    private void 重载爆率按钮ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_重载爆率按钮ActionPerformed
        // TODO add your handling code here:
        MapleMonsterInformationProvider.getInstance().clearDrops();
        System.out.println("[重载系统] 爆率重载成功。");
        JOptionPane.showMessageDialog(null, "爆率重载成功。");
    }//GEN-LAST:event_重载爆率按钮ActionPerformed

    private void 重载传送门按钮ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_重载传送门按钮ActionPerformed
        // TODO add your handling code here:
        PortalScriptManager.getInstance().clearScripts();
        System.out.println("[重载系统] 传送门重载成功。");
        JOptionPane.showMessageDialog(null, "传送门重载成功。");
    }//GEN-LAST:event_重载传送门按钮ActionPerformed

    private void 重载商店按钮ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_重载商店按钮ActionPerformed
        // TODO add your handling code here:
        MapleShopFactory.getInstance().clear();
        System.out.println("[重载系统] 商店重载成功。");
        JOptionPane.showMessageDialog(null, "商店重载成功。");
    }//GEN-LAST:event_重载商店按钮ActionPerformed

    private void 重载包头按钮ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_重载包头按钮ActionPerformed
        // TODO add your handling code here:
        //SendPacketOpcode.reloadValues();
        //RecvPacketOpcode.reloadValues();
        System.out.println("[重载系统] 包头重载成功。");
        JOptionPane.showMessageDialog(null, "包头重载成功。");
    }//GEN-LAST:event_重载包头按钮ActionPerformed

    private void 重载任务ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_重载任务ActionPerformed
        // TODO add your handling code here:
        MapleQuest.clearQuests();
        System.out.println("[重载系统] 任务重载成功。");
        JOptionPane.showMessageDialog(null, "任务重载成功。");
    }//GEN-LAST:event_重载任务ActionPerformed

    private void 重载反应堆按钮ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_重载反应堆按钮ActionPerformed
        // TODO add your handling code here:
        ReactorScriptManager.getInstance().clearDrops();
        System.out.println("[重载系统] 反应堆重载成功。");
        JOptionPane.showMessageDialog(null, "反应堆重载成功。");
    }//GEN-LAST:event_重载反应堆按钮ActionPerformed

    private void 重载商城按钮ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_重载商城按钮ActionPerformed
        // TODO add your handling code here:
        CashItemFactory.getInstance().clearCashShop();
        System.out.println("[重载系统] 商城重载成功。");
        JOptionPane.showMessageDialog(null, "商城重载成功。");
    }//GEN-LAST:event_重载商城按钮ActionPerformed

    private void 保存雇佣按钮ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_保存雇佣按钮ActionPerformed
        // TODO add your handling code here:
        int p = 0;
        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
            p++;
            cserv.closeAllMerchants();
        }
        System.out.println("[保存雇佣商人系统] 雇佣商人保存" + p + "个频道成功。");
        JOptionPane.showMessageDialog(null, "雇佣商人保存" + p + "个频道成功。");
    }//GEN-LAST:event_保存雇佣按钮ActionPerformed

    private void 保存数据按钮ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_保存数据按钮ActionPerformed
        // TODO add your handling code here:
        int p = 0;
        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
            for (MapleCharacter chr : cserv.getPlayerStorage().getAllCharacters()) {
                p++;
                chr.saveToDB(true, true);
            }
        }
        System.out.println("[保存数据系统] 保存" + p + "个成功。");
        JOptionPane.showMessageDialog(null, "保存数据成功。");
    }//GEN-LAST:event_保存数据按钮ActionPerformed

    private void 查询在线玩家人数按钮ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_查询在线玩家人数按钮ActionPerformed
        int p = 0;
        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
            for (MapleCharacter chr : cserv.getPlayerStorage().getAllCharacters()) {
                if (chr != null) {
                    ++p;
                }
            }
        }
        JOptionPane.showMessageDialog(this, "当前在线人数：" + p + "人");
    }//GEN-LAST:event_查询在线玩家人数按钮ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        setRunStats(true);
        if (开启服务端 == false) {
            开启服务端 = true;

        }
        new Thread(new Runnable() {
            public void run() {
                Start.是否控制台启动 = true;
                Start.start(null);
                JOptionPane.showMessageDialog(null, "服务器启动完成。");
                jButton1.setText("服务端运行中");
            }
        }).start();
        // 开始服务端启动计时
        startRunTime();
        // 开始统计线程
        updateThreadNum();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton21ActionPerformed

        Runtime runtime = Runtime.getRuntime();
        int p = 0;
        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
            p++;
            cserv.closeAllMerchants();
        }
        System.out.println("[保存雇佣商人系统] 雇佣商人保存" + p + "个频道成功。");
        JOptionPane.showMessageDialog(null, "雇佣商人保存" + p + "个频道成功。");
        int pp = 0;
        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
            for (MapleCharacter chr : cserv.getPlayerStorage().getAllCharacters()) {
                pp++;
                chr.saveToDB(true, true);
            }
        }
        System.out.println("[保存数据系统] 保存" + p + "个成功。");
        JOptionPane.showMessageDialog(null, "保存数据成功。");
        try {

            runtime.exec("taskkill /im java.exe /f");
            runtime.exec("taskkill /im java.exe /f");
            runtime.exec("taskkill /im javax.exe /f");

        } catch (Exception e) {

            System.out.println("Error!");

        }
    }//GEN-LAST:event_jButton21ActionPerformed

    private void 游戏IPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_游戏IPActionPerformed

    }//GEN-LAST:event_游戏IPActionPerformed

    private void jTextField22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField22ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField22ActionPerformed

    private void debug模式ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_debug模式ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_debug模式ActionPerformed

    private void 伤害输出记录ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_伤害输出记录ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_伤害输出记录ActionPerformed

    private void 聊天记录ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_聊天记录ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_聊天记录ActionPerformed

    private void 物品叠加开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_物品叠加开关ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_物品叠加开关ActionPerformed

    private void 无限BUFFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_无限BUFFActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_无限BUFFActionPerformed

    private void 测谎仪ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_测谎仪ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_测谎仪ActionPerformed

    private void 锻造系统ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_锻造系统ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_锻造系统ActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        updateSetting(true);
        WebOptionPane.showMessageDialog(instance, "[信息]:保存全部配置成功.");
        ;
    }//GEN-LAST:event_jButton15ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        WebOptionPane.showMessageDialog(instance, "功能说明\r\n是否开启自定义地图刷怪倍数\r\n如果开启，自定义地图刷怪倍数数值(几倍)\r\n自定义怪物倍数地图列表id(逗号隔开)\r\n此功能开启会增加地图的怪物倍数");
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed
        updateSetting(true);
        WebOptionPane.showMessageDialog(instance, "[信息]:保存全部配置成功.");
        ;
    }//GEN-LAST:event_jButton17ActionPerformed

    private void 禁止登陆开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_禁止登陆开关ActionPerformed
        按键开关("禁止登陆开关", 2013);
        刷新禁止登陆开关();
    }//GEN-LAST:event_禁止登陆开关ActionPerformed

    private void 滚动公告开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_滚动公告开关ActionPerformed
        按键开关("滚动公告开关", 2026);
        刷新滚动公告开关();
    }//GEN-LAST:event_滚动公告开关ActionPerformed

    private void 玩家聊天开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_玩家聊天开关ActionPerformed
        按键开关("玩家聊天开关", 2024);
        刷新玩家聊天开关();
    }//GEN-LAST:event_玩家聊天开关ActionPerformed

    private void 游戏升级快讯ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_游戏升级快讯ActionPerformed
        按键开关("升级快讯开关", 2003);
        刷新升级快讯();
    }//GEN-LAST:event_游戏升级快讯ActionPerformed

    private void 丢出金币开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_丢出金币开关ActionPerformed
        按键开关("丢出金币开关", 2010);
        刷新丢出金币开关();
    }//GEN-LAST:event_丢出金币开关ActionPerformed

    private void 丢出物品开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_丢出物品开关ActionPerformed
        按键开关("丢出物品开关", 2012);
        刷新丢出物品开关();
    }//GEN-LAST:event_丢出物品开关ActionPerformed

    private void 游戏指令开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_游戏指令开关ActionPerformed
        按键开关("游戏指令开关", 2008);
        刷新游戏指令开关();
    }//GEN-LAST:event_游戏指令开关ActionPerformed

    private void 上线提醒开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_上线提醒开关ActionPerformed
        按键开关("上线提醒开关", 2021);
        刷新上线提醒开关();
    }//GEN-LAST:event_上线提醒开关ActionPerformed

    private void 回收地图开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_回收地图开关ActionPerformed
        按键开关("回收地图开关", 2029);
        刷新回收地图开关();
    }//GEN-LAST:event_回收地图开关ActionPerformed

    private void 管理隐身开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_管理隐身开关ActionPerformed
        按键开关("管理隐身开关", 2006);
        刷新管理隐身开关();
    }//GEN-LAST:event_管理隐身开关ActionPerformed

    private void 管理加速开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_管理加速开关ActionPerformed
        按键开关("管理加速开关", 2007);
        刷新管理加速开关();
    }//GEN-LAST:event_管理加速开关ActionPerformed

    private void 游戏喇叭开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_游戏喇叭开关ActionPerformed
        按键开关("游戏喇叭开关", 2009);
        刷新游戏喇叭开关();
    }//GEN-LAST:event_游戏喇叭开关ActionPerformed

    private void 玩家交易开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_玩家交易开关ActionPerformed
        按键开关("玩家交易开关", 2011);
        刷新玩家交易开关();
    }//GEN-LAST:event_玩家交易开关ActionPerformed

    private void 雇佣商人开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_雇佣商人开关ActionPerformed
        按键开关("雇佣商人开关", 2020);
        刷新雇佣商人开关();
    }//GEN-LAST:event_雇佣商人开关ActionPerformed

    private void 欢迎弹窗开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_欢迎弹窗开关ActionPerformed
        按键开关("欢迎弹窗开关", 2015);
        刷新欢迎弹窗开关();
    }//GEN-LAST:event_欢迎弹窗开关ActionPerformed

    private void 登陆帮助开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_登陆帮助开关ActionPerformed
        按键开关("登陆帮助开关", 2058);
        刷新登陆帮助();
    }//GEN-LAST:event_登陆帮助开关ActionPerformed

    private void 越级打怪开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_越级打怪开关ActionPerformed
        按键开关("越级打怪开关", 2125);
        刷新越级打怪开关();
    }//GEN-LAST:event_越级打怪开关ActionPerformed

    private void 怪物状态开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_怪物状态开关ActionPerformed
        按键开关("怪物状态开关", 2061);
        刷新怪物状态开关();
    }//GEN-LAST:event_怪物状态开关ActionPerformed

    private void 地图名称开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_地图名称开关ActionPerformed
        按键开关("地图名称开关", 2136);
        刷新地图名称开关();
    }//GEN-LAST:event_地图名称开关ActionPerformed

    private void 过图存档开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_过图存档开关ActionPerformed
        按键开关("过图存档开关", 2140);
        刷新过图存档时间();
    }//GEN-LAST:event_过图存档开关ActionPerformed

    private void 指令通知开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_指令通知开关ActionPerformed
        按键开关("指令通知开关", 2028);
        刷新指令通知开关();
    }//GEN-LAST:event_指令通知开关ActionPerformed

    private void 游戏仓库开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_游戏仓库开关ActionPerformed
        按键开关("游戏仓库开关", 2017);
        刷新游戏仓库开关();
    }//GEN-LAST:event_游戏仓库开关ActionPerformed

    private void 脚本显码开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_脚本显码开关ActionPerformed
        按键开关("脚本显码开关", 2025);
        刷新脚本显码开关();
    }//GEN-LAST:event_脚本显码开关ActionPerformed

    private void 拍卖行开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_拍卖行开关ActionPerformed
        按键开关("拍卖行开关", 2019);
        刷新拍卖行开关();
    }//GEN-LAST:event_拍卖行开关ActionPerformed

    private void 游戏找人开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_游戏找人开关ActionPerformed
        按键开关("游戏找人开关", 2127);
        刷新游戏找人开关();
    }//GEN-LAST:event_游戏找人开关ActionPerformed

    private void 机器多开开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_机器多开开关ActionPerformed
        按键开关("机器多开开关", 2053);
        刷新机器多开();
    }//GEN-LAST:event_机器多开开关ActionPerformed

    private void IP多开开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_IP多开开关ActionPerformed
        按键开关("IP多开开关", 2054);
        刷新IP多开();
    }//GEN-LAST:event_IP多开开关ActionPerformed

    private void 挂机检测开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_挂机检测开关ActionPerformed
        按键开关("挂机检测开关", 2141);
        刷新挂机检测开关();
    }//GEN-LAST:event_挂机检测开关ActionPerformed

    private void 全屏检测开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_全屏检测开关ActionPerformed
        按键开关("全屏检测开关", 2131);
        刷新全屏检测开关();
    }//GEN-LAST:event_全屏检测开关ActionPerformed

    private void 吸怪检测开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_吸怪检测开关ActionPerformed
        按键开关("吸怪检测开关", 2130);
        刷新吸怪检测开关();
    }//GEN-LAST:event_吸怪检测开关ActionPerformed

    private void 段数检测开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_段数检测开关ActionPerformed
        按键开关("段数检测开关", 2138);
        刷新段数检测开关();
    }//GEN-LAST:event_段数检测开关ActionPerformed

    private void 群攻检测开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_群攻检测开关ActionPerformed
        按键开关("群攻检测开关", 2139);
        刷新群攻检测开关();
    }//GEN-LAST:event_群攻检测开关ActionPerformed

    private void 加速检测开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_加速检测开关ActionPerformed
        按键开关("加速检测开关", 2146);
        刷新加速检测开关();
    }//GEN-LAST:event_加速检测开关ActionPerformed

    private void 捡物检测开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_捡物检测开关ActionPerformed
        按键开关("捡物检测开关", 2148);
        刷新捡物检测开关();
    }//GEN-LAST:event_捡物检测开关ActionPerformed

    private void 蓝蜗牛开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_蓝蜗牛开关ActionPerformed
        按键开关("蓝蜗牛开关", 2200);
        刷新蓝蜗牛开关();
        JOptionPane.showMessageDialog(null, "[信息]:修改成功!");
    }//GEN-LAST:event_蓝蜗牛开关ActionPerformed

    private void 蘑菇仔开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_蘑菇仔开关ActionPerformed
        按键开关("蘑菇仔开关", 2201);
        刷新蘑菇仔开关();
        JOptionPane.showMessageDialog(null, "[信息]:修改成功!");
    }//GEN-LAST:event_蘑菇仔开关ActionPerformed

    private void 绿水灵开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_绿水灵开关ActionPerformed
        按键开关("绿水灵开关", 2202);
        刷新绿水灵开关();
        JOptionPane.showMessageDialog(null, "[信息]:修改成功!");
    }//GEN-LAST:event_绿水灵开关ActionPerformed

    private void 漂漂猪开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_漂漂猪开关ActionPerformed
        按键开关("漂漂猪开关", 2203);
        刷新漂漂猪开关();
        JOptionPane.showMessageDialog(null, "[信息]:修改成功!");
    }//GEN-LAST:event_漂漂猪开关ActionPerformed

    private void 小青蛇开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_小青蛇开关ActionPerformed
        按键开关("小青蛇开关", 2204);
        刷新小青蛇开关();
        JOptionPane.showMessageDialog(null, "[信息]:修改成功!");
    }//GEN-LAST:event_小青蛇开关ActionPerformed

    private void 红螃蟹开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_红螃蟹开关ActionPerformed
        按键开关("红螃蟹开关", 2205);
        刷新红螃蟹开关();
        JOptionPane.showMessageDialog(null, "[信息]:修改成功!");
    }//GEN-LAST:event_红螃蟹开关ActionPerformed

    private void 大海龟开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_大海龟开关ActionPerformed
        按键开关("大海龟开关", 2206);
        刷新大海龟开关();
        JOptionPane.showMessageDialog(null, "[信息]:修改成功!");
    }//GEN-LAST:event_大海龟开关ActionPerformed

    private void 章鱼怪开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_章鱼怪开关ActionPerformed
        按键开关("章鱼怪开关", 2207);
        刷新章鱼怪开关();
        JOptionPane.showMessageDialog(null, "[信息]:修改成功!");
    }//GEN-LAST:event_章鱼怪开关ActionPerformed

    private void 顽皮猴开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_顽皮猴开关ActionPerformed
        按键开关("顽皮猴开关", 2208);
        刷新顽皮猴开关();
        JOptionPane.showMessageDialog(null, "[信息]:修改成功!");
    }//GEN-LAST:event_顽皮猴开关ActionPerformed

    private void 星精灵开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_星精灵开关ActionPerformed
        按键开关("星精灵开关", 2209);
        刷新星精灵开关();
        JOptionPane.showMessageDialog(null, "[信息]:修改成功!");
    }//GEN-LAST:event_星精灵开关ActionPerformed

    private void 胖企鹅开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_胖企鹅开关ActionPerformed
        按键开关("胖企鹅开关", 2210);
        刷新胖企鹅开关();
        JOptionPane.showMessageDialog(null, "[信息]:修改成功!");
    }//GEN-LAST:event_胖企鹅开关ActionPerformed

    private void 白雪人开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_白雪人开关ActionPerformed
        按键开关("白雪人开关", 2211);
        刷新白雪人开关();
        JOptionPane.showMessageDialog(null, "[信息]:修改成功!");
    }//GEN-LAST:event_白雪人开关ActionPerformed

    private void 石头人开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_石头人开关ActionPerformed
        按键开关("石头人开关", 2212);
        刷新石头人开关();
        JOptionPane.showMessageDialog(null, "[信息]:修改成功!");
    }//GEN-LAST:event_石头人开关ActionPerformed

    private void 紫色猫开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_紫色猫开关ActionPerformed
        按键开关("紫色猫开关", 2213);
        刷新紫色猫开关();
        JOptionPane.showMessageDialog(null, "[信息]:修改成功!");
    }//GEN-LAST:event_紫色猫开关ActionPerformed

    private void 大灰狼开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_大灰狼开关ActionPerformed
        按键开关("大灰狼开关", 2214);
        刷新大灰狼开关();
        JOptionPane.showMessageDialog(null, "[信息]:修改成功!");
    }//GEN-LAST:event_大灰狼开关ActionPerformed

    private void 喷火龙开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_喷火龙开关ActionPerformed
        按键开关("喷火龙开关", 2216);
        刷新喷火龙开关();
        JOptionPane.showMessageDialog(null, "[信息]:修改成功!");
    }//GEN-LAST:event_喷火龙开关ActionPerformed

    private void 火野猪开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_火野猪开关ActionPerformed
        按键开关("火野猪开关", 2217);
        刷新火野猪开关();
        JOptionPane.showMessageDialog(null, "[信息]:修改成功!");
    }//GEN-LAST:event_火野猪开关ActionPerformed

    private void 小白兔开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_小白兔开关ActionPerformed
        按键开关("小白兔开关", 2215);
        刷新小白兔开关();
        JOptionPane.showMessageDialog(null, "[信息]:修改成功!");
    }//GEN-LAST:event_小白兔开关ActionPerformed

    private void 青鳄鱼开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_青鳄鱼开关ActionPerformed
        按键开关("青鳄鱼开关", 2218);
        刷新青鳄鱼开关();
        JOptionPane.showMessageDialog(null, "[信息]:修改成功!");
    }//GEN-LAST:event_青鳄鱼开关ActionPerformed

    private void 花蘑菇开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_花蘑菇开关ActionPerformed
        按键开关("花蘑菇开关", 2219);
        刷新花蘑菇开关();
        JOptionPane.showMessageDialog(null, "[信息]:修改成功!");
    }//GEN-LAST:event_花蘑菇开关ActionPerformed

    private void jButton69ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton69ActionPerformed
        runTool(Tools.DumpItems);
    }//GEN-LAST:event_jButton69ActionPerformed

    private void jButton70ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton70ActionPerformed
        runTool(Tools.MonsterDropCreator);
    }//GEN-LAST:event_jButton70ActionPerformed

    private void jButton72ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton72ActionPerformed
        runTool(Tools.DumpNpcNames);
    }//GEN-LAST:event_jButton72ActionPerformed

    private void jButton73ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton73ActionPerformed
        runTool(Tools.DumpMobSkills);
    }//GEN-LAST:event_jButton73ActionPerformed

    private void jButton74ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton74ActionPerformed
        runTool(Tools.DumpOxQuizData);
    }//GEN-LAST:event_jButton74ActionPerformed

    private void jButton75ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton75ActionPerformed
        runTool(Tools.DumpQuests);
    }//GEN-LAST:event_jButton75ActionPerformed

    private void jButton31ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton31ActionPerformed
        openWindow(Windows.代码查询工具);
        if (!LoginServer.isShutdown() || searchServer) {
            return;

        }
    }//GEN-LAST:event_jButton31ActionPerformed

    private void jButton22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton22ActionPerformed
        openWindow(Windows.基址计算工具);
    }//GEN-LAST:event_jButton22ActionPerformed

    private void jButton23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton23ActionPerformed
        openWindow(Windows.包头转换工具);
    }//GEN-LAST:event_jButton23ActionPerformed

    private void jButton28ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton28ActionPerformed
        openWindow(Windows.角色转移工具);
    }//GEN-LAST:event_jButton28ActionPerformed

    private void jButton29ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton29ActionPerformed
        openWindow(Windows.一键还原);
    }//GEN-LAST:event_jButton29ActionPerformed

    private void jButton39ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton39ActionPerformed
        openWindow(Windows.删除自添加NPC工具);
    }//GEN-LAST:event_jButton39ActionPerformed

    private void jButton44ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton44ActionPerformed
        openWindow(Windows.游戏抽奖工具);
    }//GEN-LAST:event_jButton44ActionPerformed

    private void jButton25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton25ActionPerformed
        openWindow(Windows.卡密制作工具);
    }//GEN-LAST:event_jButton25ActionPerformed

    private void jButton32ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton32ActionPerformed
        openWindow(Windows.商城管理控制台);
    }//GEN-LAST:event_jButton32ActionPerformed

    private void jButton33ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton33ActionPerformed
        openWindow(Windows.锻造控制台);
    }//GEN-LAST:event_jButton33ActionPerformed

    private void jButton34ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton34ActionPerformed
        openWindow(Windows.副本控制台);
    }//GEN-LAST:event_jButton34ActionPerformed

    private void jButton40ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton40ActionPerformed
        openWindow(Windows.野外BOSS刷新时间);
    }//GEN-LAST:event_jButton40ActionPerformed

    private void jButton41ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton41ActionPerformed
        openWindow(Windows.药水冷却时间控制台);
    }//GEN-LAST:event_jButton41ActionPerformed

    private void jButton42ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton42ActionPerformed
        openWindow(Windows.金锤子成功率控制台);
    }//GEN-LAST:event_jButton42ActionPerformed

    private void jButton43ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton43ActionPerformed
        openWindow(Windows.永恒重生装备控制台);
    }//GEN-LAST:event_jButton43ActionPerformed

    private void jButton46ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton46ActionPerformed
        openWindow(Windows.OX答题控制台);
    }//GEN-LAST:event_jButton46ActionPerformed

    private void jButton47ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton47ActionPerformed
        openWindow(Windows.钓鱼控制台);
    }//GEN-LAST:event_jButton47ActionPerformed

    private void jButton48ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton48ActionPerformed
        openWindow(Windows.鱼来鱼往);
    }//GEN-LAST:event_jButton48ActionPerformed

    private void jButton49ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton49ActionPerformed
        openWindow(Windows.椅子控制台);
    }//GEN-LAST:event_jButton49ActionPerformed

    private void sendNoticeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendNoticeActionPerformed
        sendNotice(0);
        System.out.println("[公告系统] 发送蓝色公告事项公告成功！");
        JOptionPane.showMessageDialog(null, "发送蓝色公告事项公告成功！");
    }//GEN-LAST:event_sendNoticeActionPerformed

    private void sendWinNoticeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendWinNoticeActionPerformed
        sendNotice(1);
        System.out.println("[公告系统] 发送弹窗公告成功！");
        JOptionPane.showMessageDialog(null, "发送弹窗公告成功！");
    }//GEN-LAST:event_sendWinNoticeActionPerformed

    private void sendMsgNoticeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendMsgNoticeActionPerformed
        sendNotice(2);
        System.out.println("[公告系统] 发送红色提示公告成功！");
        JOptionPane.showMessageDialog(null, "发送红色提示公告成功！");
    }//GEN-LAST:event_sendMsgNoticeActionPerformed

    private void sendNpcTalkNoticeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendNpcTalkNoticeActionPerformed
        sendNotice(3);
        System.out.println("[公告系统] 发送黄色滚动公告成功！");
        JOptionPane.showMessageDialog(null, "发送黄色滚动公告成功！");
    }//GEN-LAST:event_sendNpcTalkNoticeActionPerformed

    private void 公告发布喇叭代码ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_公告发布喇叭代码ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_公告发布喇叭代码ActionPerformed

    private void jButton45ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton45ActionPerformed
        sendNotice(4);
        System.out.println("[公告系统] 发送公告成功！");
        JOptionPane.showMessageDialog(null, "发送公告成功！");
    }//GEN-LAST:event_jButton45ActionPerformed

    private void 全服发送物品数量ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_全服发送物品数量ActionPerformed

    }//GEN-LAST:event_全服发送物品数量ActionPerformed

    private void 全服发送物品代码ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_全服发送物品代码ActionPerformed

    }//GEN-LAST:event_全服发送物品代码ActionPerformed

    private void 给予物品1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_给予物品1ActionPerformed
        刷物品2();    // TODO add your handling code here:
    }//GEN-LAST:event_给予物品1ActionPerformed
    private void 刷物品() {
        try {
            String 名字;
            if ("玩家名字".equals(个人发送物品玩家名字.getText())) {
                名字 = "";
            } else {
                名字 = 个人发送物品玩家名字.getText();
            }
            int 物品ID;
            if ("物品ID".equals(个人发送物品代码.getText())) {
                物品ID = 0;
            } else {
                物品ID = Integer.parseInt(个人发送物品代码.getText());
            }
            int 数量;
            if ("数量".equals(个人发送物品数量.getText())) {
                数量 = 0;
            } else {
                数量 = Integer.parseInt(个人发送物品数量.getText());
            }
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            MapleInventoryType type = GameConstants.getInventoryType(物品ID);
            for (ChannelServer cserv1 : ChannelServer.getAllInstances()) {
                for (MapleCharacter mch : cserv1.getPlayerStorage().getAllCharacters()) {
                    if (mch.getName().equals(名字)) {
                        if (数量 >= 0) {
                            if (!MapleInventoryManipulator.checkSpace(mch.getClient(), 物品ID, 数量, "")) {
                                return;
                            }
                            if (type.equals(MapleInventoryType.EQUIP) && !GameConstants.isThrowingStar(物品ID) && !GameConstants.isBullet(物品ID)
                                    || type.equals(MapleInventoryType.CASH) && 物品ID >= 5000000 && 物品ID <= 5000100) {
                                final Equip item = (Equip) (ii.getEquipById(物品ID));
                                if (ii.isCash(物品ID)) {
                                    item.setUniqueId(1);
                                }
                                final String name = ii.getName(物品ID);
                                if (物品ID / 10000 == 114 && name != null && name.length() > 0) { //medal
                                    final String msg = "你已获得称号 <" + name + ">";
                                    mch.getClient().getPlayer().dropMessage(5, msg);
                                }
                                MapleInventoryManipulator.addbyItem(mch.getClient(), item.copy());
                            } else {
                                MapleInventoryManipulator.addById(mch.getClient(), 物品ID, (short) 数量, "", null, (byte) 0);
                            }
                        } else {
                            MapleInventoryManipulator.removeById(mch.getClient(), GameConstants.getInventoryType(物品ID), 物品ID, -数量, true, false);
                        }
                        mch.getClient().getSession().write(MaplePacketCreator.getShowItemGain(物品ID, (short) 数量, true));

                    }
                }
            }
            个人发送物品玩家名字.setText("");
            个人发送物品代码.setText("");
            个人发送物品数量.setText("");
            JOptionPane.showMessageDialog(null, "[信息]:发送成功。");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "[信息]:错误!" + e);
        }
    }
    private void 刷物品2() {
        try {
            int 数量;
            int 物品ID;
            物品ID = Integer.parseInt(全服发送物品代码.getText());
            数量 = Integer.parseInt(全服发送物品数量.getText());
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            MapleInventoryType type = GameConstants.getInventoryType(物品ID);
            for (ChannelServer cserv1 : ChannelServer.getAllInstances()) {
                for (MapleCharacter mch : cserv1.getPlayerStorage().getAllCharacters()) {
                    if (数量 >= 0) {
                        if (!MapleInventoryManipulator.checkSpace(mch.getClient(), 物品ID, 数量, "")) {
                            return;
                        }
                        if (type.equals(MapleInventoryType.EQUIP) && !GameConstants.isThrowingStar(物品ID) && !GameConstants.isBullet(物品ID)
                                || type.equals(MapleInventoryType.CASH) && 物品ID >= 5000000 && 物品ID <= 5000100) {
                            final Equip item = (Equip) (ii.getEquipById(物品ID));
                            if (ii.isCash(物品ID)) {
                                item.setUniqueId(1);
                            }
                            final String name = ii.getName(物品ID);
                            if (物品ID / 10000 == 114 && name != null && name.length() > 0) { //medal
                                final String msg = "你已获得称号 <" + name + ">";
                                mch.getClient().getPlayer().dropMessage(5, msg);
                            }
                            MapleInventoryManipulator.addbyItem(mch.getClient(), item.copy());
                        } else {
                            MapleInventoryManipulator.addById(mch.getClient(), 物品ID, (short) 数量, "", null, (byte) 0);
                        }
                    } else {
                        MapleInventoryManipulator.removeById(mch.getClient(), GameConstants.getInventoryType(物品ID), 物品ID, -数量, true, false);
                    }
//                    mch.getClient().getSession.write(MaplePacketCreator.getShowItemGain(物品ID, (short) 数量, true));
                    mch.getClient().getSession().write(MaplePacketCreator.getShowItemGain(物品ID, (short) 数量, true));
                }
            }
            全服发送物品代码.setText("");
            全服发送物品数量.setText("");
            JOptionPane.showMessageDialog(null, "[信息]:发送成功。");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "[信息]:错误!" + e);
        }
    }
        private void 个人发送福利(int a) {
   /*         boolean result1 = this.a1.getText().matches("[0-9]+");
        if (result1) {
            int 数量;
            if ("100000000".equals(a1.getText())) {
                数量 = 100;
            } else {
                数量 = Integer.parseInt(a1.getText());
            }
            if (数量 <= 0 || 数量 > 999999999) {
                return;
            }*/
        int 数量 = 0;
        String 类型 = "";
        String name = "";
        数量 = Integer.parseInt(a2.getText());
        name = 个人发送物品玩家名字1.getText();
        for (ChannelServer cserv1 : ChannelServer.getAllInstances()) {
            for (MapleCharacter mch : cserv1.getPlayerStorage().getAllCharacters()) {
                if (mch.getName().equals(name)) {
                    int ch = World.Find.findChannel(name);
                    if (ch <= 0) {
                        JOptionPane.showMessageDialog(null, "该玩家不在线");
                    }
                    switch (a) {
                        case 1:
                        类型 = "点券";
                        mch.modifyCSPoints(1, 数量, true);
                        mch.dropMessage("已经收到点卷" + 数量 + "点");
                        JOptionPane.showMessageDialog(null, "发送成功");
                        break;
                        case 2:
                        类型 = "抵用券";
                        mch.modifyCSPoints(2, 数量, true);
                        mch.dropMessage("已经收到抵用" + 数量 + "点");
                        JOptionPane.showMessageDialog(null, "发送成功");
                        break;
                        case 3:
                        类型 = "金币";
                        mch.gainMeso(数量, true);
                        mch.dropMessage("已经收到金币" + 数量 + "点");
                        JOptionPane.showMessageDialog(null, "发送成功");
                        break;
                        case 4:
                        类型 = "经验";
                        mch.gainExp(数量, true, false, true);
                        mch.dropMessage("已经收到经验" + 数量 + "点");
                        JOptionPane.showMessageDialog(null, "发送成功");
                        break;
                        case 5:
                        类型 = "人气";
                        mch.addFame(数量);
                        mch.dropMessage("已经收到人气" + 数量 + "点");
                        JOptionPane.showMessageDialog(null, "发送成功");
                        break;
                        case 6:
                        类型 = "豆豆";
                        mch.gainBeans(数量);
                        mch.dropMessage("已经收到豆豆" + 数量 + "点");
                        JOptionPane.showMessageDialog(null, "发送成功");
                        break;
                    }
                }
            }
        }
    }
    private void z7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_z7ActionPerformed
        个人发送福利(2);
    }//GEN-LAST:event_z7ActionPerformed

    private void z8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_z8ActionPerformed
        个人发送福利(3);
    }//GEN-LAST:event_z8ActionPerformed

    private void z9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_z9ActionPerformed
        个人发送福利(1);
    }//GEN-LAST:event_z9ActionPerformed

    private void z10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_z10ActionPerformed
        个人发送福利(4);
    }//GEN-LAST:event_z10ActionPerformed

    private void z11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_z11ActionPerformed
        个人发送福利(5);
    }//GEN-LAST:event_z11ActionPerformed

    private void z12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_z12ActionPerformed
        个人发送福利(6);
    }//GEN-LAST:event_z12ActionPerformed

    private void a2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_a2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_a2ActionPerformed

    private void 个人发送物品玩家名字1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_个人发送物品玩家名字1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_个人发送物品玩家名字1ActionPerformed

    private void 个人发送物品数量ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_个人发送物品数量ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_个人发送物品数量ActionPerformed

    private void 个人发送物品玩家名字ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_个人发送物品玩家名字ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_个人发送物品玩家名字ActionPerformed

    private void 个人发送物品代码ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_个人发送物品代码ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_个人发送物品代码ActionPerformed

    private void 给予物品ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_给予物品ActionPerformed
        刷物品();       // TODO add your handling code here:
    }//GEN-LAST:event_给予物品ActionPerformed

    private void 全服发送装备装备加卷ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_全服发送装备装备加卷ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_全服发送装备装备加卷ActionPerformed

    private void 全服发送装备装备制作人ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_全服发送装备装备制作人ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_全服发送装备装备制作人ActionPerformed

    private void 全服发送装备装备力量ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_全服发送装备装备力量ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_全服发送装备装备力量ActionPerformed

    private void 全服发送装备装备MPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_全服发送装备装备MPActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_全服发送装备装备MPActionPerformed

    private void 全服发送装备装备智力ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_全服发送装备装备智力ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_全服发送装备装备智力ActionPerformed

    private void 全服发送装备装备运气ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_全服发送装备装备运气ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_全服发送装备装备运气ActionPerformed

    private void 全服发送装备装备HPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_全服发送装备装备HPActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_全服发送装备装备HPActionPerformed

    private void 全服发送装备装备攻击力ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_全服发送装备装备攻击力ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_全服发送装备装备攻击力ActionPerformed

    private void 全服发送装备装备给予时间ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_全服发送装备装备给予时间ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_全服发送装备装备给予时间ActionPerformed

    private void 全服发送装备装备可否交易ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_全服发送装备装备可否交易ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_全服发送装备装备可否交易ActionPerformed

    private void 全服发送装备装备敏捷ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_全服发送装备装备敏捷ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_全服发送装备装备敏捷ActionPerformed

    private void 全服发送装备物品IDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_全服发送装备物品IDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_全服发送装备物品IDActionPerformed

    private void 全服发送装备装备魔法力ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_全服发送装备装备魔法力ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_全服发送装备装备魔法力ActionPerformed

    private void 全服发送装备装备魔法防御ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_全服发送装备装备魔法防御ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_全服发送装备装备魔法防御ActionPerformed

    private void 全服发送装备装备物理防御ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_全服发送装备装备物理防御ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_全服发送装备装备物理防御ActionPerformed
    private void 刷装备2(int a) {
        try {
            int 物品ID;
            if ("物品ID".equals(全服发送装备物品ID.getText())) {
                物品ID = 0;
            } else {
                物品ID = Integer.parseInt(全服发送装备物品ID.getText());
            }
            int 力量;
            if ("力量".equals(全服发送装备装备力量.getText())) {
                力量 = 0;
            } else {
                力量 = Integer.parseInt(全服发送装备装备力量.getText());
            }
            int 敏捷;
            if ("敏捷".equals(全服发送装备装备敏捷.getText())) {
                敏捷 = 0;
            } else {
                敏捷 = Integer.parseInt(全服发送装备装备敏捷.getText());
            }
            int 智力;
            if ("智力".equals(全服发送装备装备智力.getText())) {
                智力 = 0;
            } else {
                智力 = Integer.parseInt(全服发送装备装备智力.getText());
            }
            int 运气;
            if ("运气".equals(全服发送装备装备运气.getText())) {
                运气 = 0;
            } else {
                运气 = Integer.parseInt(全服发送装备装备运气.getText());
            }
            int HP;
            if ("HP设置".equals(全服发送装备装备HP.getText())) {
                HP = 0;
            } else {
                HP = Integer.parseInt(全服发送装备装备HP.getText());
            }
            int MP;
            if ("MP设置".equals(全服发送装备装备MP.getText())) {
                MP = 0;
            } else {
                MP = Integer.parseInt(全服发送装备装备MP.getText());
            }
            int 可加卷次数;
            if ("加卷次数".equals(全服发送装备装备加卷.getText())) {
                可加卷次数 = 0;
            } else {
                可加卷次数 = Integer.parseInt(全服发送装备装备加卷.getText());
            }

            String 制作人名字;
            if ("制作人".equals(全服发送装备装备制作人.getText())) {
                制作人名字 = "";
            } else {
                制作人名字 = 全服发送装备装备制作人.getText();
            }
            int 给予时间;
            if ("给予物品时间".equals(全服发送装备装备给予时间.getText())) {
                给予时间 = 0;
            } else {
                给予时间 = Integer.parseInt(全服发送装备装备给予时间.getText());
            }
            String 是否可以交易 = 全服发送装备装备可否交易.getText();
            int 攻击力;
            if ("攻击力".equals(全服发送装备装备攻击力.getText())) {
                攻击力 = 0;
            } else {
                攻击力 = Integer.parseInt(全服发送装备装备攻击力.getText());
            }
            int 魔法力;
            if ("魔法力".equals(全服发送装备装备魔法力.getText())) {
                魔法力 = 0;
            } else {
                魔法力 = Integer.parseInt(全服发送装备装备魔法力.getText());
            }
            int 物理防御;
            if ("物理防御".equals(全服发送装备装备物理防御.getText())) {
                物理防御 = 0;
            } else {
                物理防御 = Integer.parseInt(全服发送装备装备物理防御.getText());
            }
            int 魔法防御;
            if ("魔法防御".equals(全服发送装备装备魔法防御.getText())) {
                魔法防御 = 0;
            } else {
                魔法防御 = Integer.parseInt(全服发送装备装备魔法防御.getText());
            }
            int 潜能1;
            if ("潜能1".equals(全服发送装备装备潜能1.getText())) {
                潜能1 = 0;
            } else {
                潜能1 = Integer.parseInt(全服发送装备装备潜能1.getText());
            }
            int 潜能2;
            if ("潜能2".equals(全服发送装备装备潜能2.getText())) {
                潜能2 = 0;
            } else {
                潜能2 = Integer.parseInt(全服发送装备装备潜能2.getText());
            }
            int 潜能3;
            if ("潜能3".equals(全服发送装备装备潜能3.getText())) {
                潜能3 = 0;
            } else {
                潜能3 = Integer.parseInt(全服发送装备装备潜能3.getText());
            }
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            MapleInventoryType type = GameConstants.getInventoryType(物品ID);
            for (ChannelServer cserv1 : ChannelServer.getAllInstances()) {
                for (MapleCharacter mch : cserv1.getPlayerStorage().getAllCharacters()) {
                    if (a == 1) {
                        if (1 >= 0) {
                            if (!MapleInventoryManipulator.checkSpace(mch.getClient(), 物品ID, 1, "")) {
                                return;
                            }
                            if (type.equals(MapleInventoryType.EQUIP) && !GameConstants.isThrowingStar(物品ID) && !GameConstants.isBullet(物品ID)
                                    || type.equals(MapleInventoryType.CASH) && 物品ID >= 5000000 && 物品ID <= 5000100) {
                                final Equip item = (Equip) (ii.getEquipById(物品ID));
                                if (ii.isCash(物品ID)) {
                                    item.setUniqueId(1);
                                }
                                if (力量 > 0 && 力量 <= 32767) {
                                    item.setStr((short) (力量));
                                }
                                if (敏捷 > 0 && 敏捷 <= 32767) {
                                    item.setDex((short) (敏捷));
                                }
                                if (智力 > 0 && 智力 <= 32767) {
                                    item.setInt((short) (智力));
                                }
                                if (运气 > 0 && 运气 <= 32767) {
                                    item.setLuk((short) (运气));
                                }
                                if (攻击力 > 0 && 攻击力 <= 32767) {
                                    item.setWatk((short) (攻击力));
                                }
                                if (魔法力 > 0 && 魔法力 <= 32767) {
                                    item.setMatk((short) (魔法力));
                                }
                                if (物理防御 > 0 && 物理防御 <= 32767) {
                                    item.setWdef((short) (物理防御));
                                }
                                if (魔法防御 > 0 && 魔法防御 <= 32767) {
                                    item.setMdef((short) (魔法防御));
                                }
                                if (HP > 0 && HP <= 30000) {
                                    item.setHp((short) (HP));
                                }
                                if (MP > 0 && MP <= 30000) {
                                    item.setMp((short) (MP));
                                }
                                if (潜能1 > 0 && 潜能1 <= 42767) {
                                    item.setpotential1((short) (潜能1));
                                }
                                if (潜能2 > 0 && 潜能2 <= 42767) {
                                    item.setpotential2((short) (潜能2));
                                }
                                if (潜能3 > 0 && 潜能3 <= 42767) {
                                    item.setpotential3((short) (潜能3));
                                }
                                if ("可以交易".equals(是否可以交易)) {
                                    short flag = item.getFlag();
                                    if (item.getType() == MapleInventoryType.EQUIP.getType()) {
                                        flag |= ItemFlag.KARMA_EQ.getValue();
                                    } else {
                                        flag |= ItemFlag.KARMA_USE.getValue();
                                    }
                                    item.setFlag((byte) flag);
                                }
                                if (给予时间 > 0) {
                                    item.setExpiration(System.currentTimeMillis() + (给予时间 * 24 * 60 * 60 * 1000));
                                }
                                if (可加卷次数 > 0) {
                                    item.setUpgradeSlots((byte) (可加卷次数));
                                }
                                if (制作人名字 != null) {
                                    item.setOwner(制作人名字);
                                }
                                final String name = ii.getName(物品ID);
                                if (物品ID / 10000 == 114 && name != null && name.length() > 0) { //medal
                                    final String msg = "你已获得称号 <" + name + ">";
                                    mch.getClient().getPlayer().dropMessage(5, msg);
                                }
                                MapleInventoryManipulator.addbyItem(mch.getClient(), item.copy());
                            } else {
                           //     MapleInventoryManipulator.addById(mch.getClient(), 物品ID, (short) 1, "", null, 给予时间, "");
                                MapleInventoryManipulator.addById(mch.getClient(), 物品ID, (short) 1, "", null, (byte) 0);

                            }
                        } else {
                            MapleInventoryManipulator.removeById(mch.getClient(), GameConstants.getInventoryType(物品ID), 物品ID, -1, true, false);
                        }
                        mch.getClient().getSession().write(MaplePacketCreator.getShowItemGain(物品ID, (short) 1, true));
                    } else if (mch.getName().equals(发送装备玩家姓名.getText())) {
                        if (1 >= 0) {
                            if (!MapleInventoryManipulator.checkSpace(mch.getClient(), 物品ID, 1, "")) {
                                return;
                            }
                            if (type.equals(MapleInventoryType.EQUIP) && !GameConstants.isThrowingStar(物品ID) && !GameConstants.isBullet(物品ID)
                                    || type.equals(MapleInventoryType.CASH) && 物品ID >= 5000000 && 物品ID <= 5000100) {
                                final Equip item = (Equip) (ii.getEquipById(物品ID));
                                if (ii.isCash(物品ID)) {
                                    item.setUniqueId(1);
                                }
                                if (力量 > 0 && 力量 <= 32767) {
                                    item.setStr((short) (力量));
                                }
                                if (敏捷 > 0 && 敏捷 <= 32767) {
                                    item.setDex((short) (敏捷));
                                }
                                if (智力 > 0 && 智力 <= 32767) {
                                    item.setInt((short) (智力));
                                }
                                if (运气 > 0 && 运气 <= 32767) {
                                    item.setLuk((short) (运气));
                                }
                                if (攻击力 > 0 && 攻击力 <= 32767) {
                                    item.setWatk((short) (攻击力));
                                }
                                if (魔法力 > 0 && 魔法力 <= 32767) {
                                    item.setMatk((short) (魔法力));
                                }
                                if (物理防御 > 0 && 物理防御 <= 32767) {
                                    item.setWdef((short) (物理防御));
                                }
                                if (魔法防御 > 0 && 魔法防御 <= 32767) {
                                    item.setMdef((short) (魔法防御));
                                }
                                if (HP > 0 && HP <= 30000) {
                                    item.setHp((short) (HP));
                                }
                                if (MP > 0 && MP <= 30000) {
                                    item.setMp((short) (MP));
                                }
                                if (潜能1 > 0 && 潜能1 <= 42767) {
                                    item.setpotential1((short) (潜能1));
                                }
                                if (潜能2 > 0 && 潜能2 <= 42767) {
                                    item.setpotential2((short) (潜能2));
                                }
                                if (潜能3 > 0 && 潜能3 <= 42767) {
                                    item.setpotential3((short) (潜能3));
                                }
                                if ("可以交易".equals(是否可以交易)) {
                                    short flag = item.getFlag();
                                    if (item.getType() == MapleInventoryType.EQUIP.getType()) {
                                        flag |= ItemFlag.KARMA_EQ.getValue();
                                    } else {
                                        flag |= ItemFlag.KARMA_USE.getValue();
                                    }
                                    item.setFlag((byte) flag);
                                }
                                if (给予时间 > 0) {
                                    item.setExpiration(System.currentTimeMillis() + (给予时间 * 24 * 60 * 60 * 1000));
                                }
                                if (可加卷次数 > 0) {
                                    item.setUpgradeSlots((byte) (可加卷次数));
                                }
                                if (制作人名字 != null) {
                                    item.setOwner(制作人名字);
                                }
                                final String name = ii.getName(物品ID);
                                if (物品ID / 10000 == 114 && name != null && name.length() > 0) { //medal
                                    final String msg = "你已获得称号 <" + name + ">";
                                    mch.getClient().getPlayer().dropMessage(5, msg);
                                }
                                MapleInventoryManipulator.addbyItem(mch.getClient(), item.copy());
                            } else {
                    //            MapleInventoryManipulator.addById(mch.getClient(), 物品ID, (short) 1, "", null, 给予时间, "");
                                MapleInventoryManipulator.addById(mch.getClient(), 物品ID, (short) 1, "", null, (byte) 0);
                            }
                        } else {
                            MapleInventoryManipulator.removeById(mch.getClient(), GameConstants.getInventoryType(物品ID), 物品ID, -1, true, false);
                        }
                        mch.getClient().getSession().write(MaplePacketCreator.getShowItemGain(物品ID, (short) 1, true));

                    }
                }
            }
            JOptionPane.showMessageDialog(null, "[信息]:发送成功。");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "[信息]:错误!" + e);
        }
    }
    private void 给予装备1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_给予装备1ActionPerformed
        刷装备2(2);        // TODO add your handling code here:
    }//GEN-LAST:event_给予装备1ActionPerformed

    private void 发送装备玩家姓名ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_发送装备玩家姓名ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_发送装备玩家姓名ActionPerformed

    private void 给予装备2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_给予装备2ActionPerformed
        刷装备2(1);
    }//GEN-LAST:event_给予装备2ActionPerformed

    private void 全服发送装备装备潜能3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_全服发送装备装备潜能3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_全服发送装备装备潜能3ActionPerformed

    private void 全服发送装备装备潜能1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_全服发送装备装备潜能1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_全服发送装备装备潜能1ActionPerformed

    private void 全服发送装备装备潜能2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_全服发送装备装备潜能2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_全服发送装备装备潜能2ActionPerformed

    private void qiannengdaimaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_qiannengdaimaActionPerformed
        WebOptionPane.showMessageDialog(instance, "潜能代码如下\r\n30601 boss+20% \r\n30602 boss+30% \r\n30291 无视防御30% \r\n30070 总伤害+9% \r\n30051 攻击力+9% \r\n30052 魔法力+9% \r\n30053 物防+9% \r\n30054 魔防+9% \r\n30041 力量+9% \r\n20041 力量+6%\r\n10041 力量+3%\r\n30042 敏捷+9%\r\n20042 敏捷+6%\r\n10042 敏捷+3% \r\n30043 智力+9%\r\n20043 智力+6%\r\n10043 智力+%3 \r\n30044 运气+9%\r\n20044 运气+6%\r\n10044 运气+3%\r\n30045 HP+9% \r\n30047 命中+9% \r\n30048 回避+9% \r\n20086 所有属性3%\r\n30086 所有属性+6%\r\n\r\n更多的自行测试 例:9%和6%代码第一位减1");
    }//GEN-LAST:event_qiannengdaimaActionPerformed
    private void 发送福利(int a) {
        boolean result1 = this.a1.getText().matches("[0-9]+");
        if (result1) {
            int 数量;
            if ("100000000".equals(a1.getText())) {
                数量 = 100;
            } else {
                数量 = Integer.parseInt(a1.getText());
            }
            if (数量 <= 0 || 数量 > 999999999) {
                return;
            }
            String 类型 = "";
            for (ChannelServer cserv1 : ChannelServer.getAllInstances()) {
                for (MapleCharacter mch : cserv1.getPlayerStorage().getAllCharacters()) {

                    switch (a) {
                        case 1:
                            类型 = "点券";
                            mch.modifyCSPoints(1, 数量, true);
                            break;
                        case 2:
                            类型 = "抵用券";
                            mch.modifyCSPoints(2, 数量, true);
                            break;
                        case 3:
                            类型 = "金币";
                            mch.gainMeso(数量, true);
                            break;
                        case 4:
                            类型 = "经验";
                            mch.gainExp(数量, false, false, false);
                            break;
                        case 5:
                            类型 = "人气";
                            mch.addFame(数量);
                            break;
                        case 6:
                            类型 = "豆豆";
                            mch.gainBeans(数量);
                            break;
                        default:
                            break;
                    }
                    mch.startMapEffect("管理员发放 " + 数量 + " " + 类型 + "给在线的所有玩家！", 5121009);
                }
            }
            JOptionPane.showMessageDialog(null, "[信息]:发放 " + 数量 + " " + 类型 + "给在线的所有玩家。");
            a1.setText("");
            JOptionPane.showMessageDialog(null, "发送成功");
        } else {
            JOptionPane.showMessageDialog(null, "[信息]:请输入要发送数量。");
        }
    }
    private void z2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_z2ActionPerformed
        发送福利(2);
    }//GEN-LAST:event_z2ActionPerformed

    private void z3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_z3ActionPerformed
        发送福利(3);
    }//GEN-LAST:event_z3ActionPerformed

    private void z1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_z1ActionPerformed
        发送福利(1);        // TODO add your handling code here:
    }//GEN-LAST:event_z1ActionPerformed

    private void z4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_z4ActionPerformed
        发送福利(4);
    }//GEN-LAST:event_z4ActionPerformed

    private void z5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_z5ActionPerformed
        发送福利(5);
    }//GEN-LAST:event_z5ActionPerformed

    private void z6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_z6ActionPerformed
        发送福利(6);
    }//GEN-LAST:event_z6ActionPerformed

    private void a1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_a1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_a1ActionPerformed

    private void 解卡ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_解卡ActionPerformed
        显示在线账号.setText("账号在线; " + 在线账号() + "");
        if (账号操作.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "请输入需要解卡的账号 ");
            return;
        }
        String account = 账号操作.getText();
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps;

            ps = con.prepareStatement("Update accounts set loggedin = ? Where name = ?");
            ps.setInt(1, 0);
            ps.setString(2, account);
            ps.execute();
            ps.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "错误!\r\n" + ex);
        }
        账号提示语言.setText("[信息]:解卡账号 " + this.账号操作.getText() + " 成功。");
        刷新账号信息();
    }//GEN-LAST:event_解卡ActionPerformed

    private void 修改账号点券抵用ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_修改账号点券抵用ActionPerformed
        boolean result1 = this.点券.getText().matches("[0-9]+");
        boolean result2 = this.抵用.getText().matches("[0-9]+");
        boolean result3 = this.管理1.getText().matches("[0-9]+");
        boolean result4 = this.QQ.getText().matches("[0-9]+");
        PreparedStatement ps = null;
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        if (result1 && result2 && result3 && result4) {
            try {
                ps = DatabaseConnection.getConnection().prepareStatement("UPDATE accounts SET ACash = ?, mPoints = ?, gm = ?, qq = ? WHERE id = ?");
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM accounts  WHERE id = ? ");
                ps1.setInt(1, Integer.parseInt(this.账号ID.getText()));
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlString2 = null;
                    String sqlString3 = null;
                    String sqlString4 = null;
                    String sqlString5 = null;
                    sqlString2 = "update accounts set ACash=" + Integer.parseInt(this.点券.getText()) + " where id ='" + Integer.parseInt(this.账号ID.getText()) + "';";
                    PreparedStatement priority = DatabaseConnection.getConnection().prepareStatement(sqlString2);
                    priority.executeUpdate(sqlString2);
                    sqlString3 = "update accounts set mPoints=" + Integer.parseInt(this.抵用.getText()) + " where id='" + Integer.parseInt(this.账号ID.getText()) + "';";
                    PreparedStatement period = DatabaseConnection.getConnection().prepareStatement(sqlString3);
                    period.executeUpdate(sqlString3);
                    sqlString4 = "update accounts set gm=" + Integer.parseInt(this.管理1.getText()) + " where id='" + Integer.parseInt(this.账号ID.getText()) + "';";
                    PreparedStatement gm = DatabaseConnection.getConnection().prepareStatement(sqlString4);
                    gm.executeUpdate(sqlString4);
                    sqlString5 = "update accounts set qq=" + Integer.parseInt(this.QQ.getText()) + " where id='" + Integer.parseInt(this.账号ID.getText()) + "';";
                    PreparedStatement qq = DatabaseConnection.getConnection().prepareStatement(sqlString5);
                    qq.executeUpdate(sqlString5);
                    刷新账号信息();
                    账号提示语言.setText("[信息]:修改账号 " + this.账号操作.getText() + " / 点券→" + Integer.parseInt(this.点券.getText()) + " / 抵用券→" + Integer.parseInt(this.抵用.getText()) + " 成功。");
                }
            } catch (SQLException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            账号提示语言.setText("[信息]:请选择要修改的账号,数据不能为空，或者数值填写不对。");
        }
    }//GEN-LAST:event_修改账号点券抵用ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        显示在线账号.setText("账号在线; " + 在线账号() + "");
        查找账号();
    }//GEN-LAST:event_jButton12ActionPerformed

    private void 刷新账号信息ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_刷新账号信息ActionPerformed
        账号提示语言.setText("[信息]:显示游戏所有玩家账号信息。");
        刷新账号信息();
        显示在线账号.setText("账号在线; " + 在线账号() + "");
    }//GEN-LAST:event_刷新账号信息ActionPerformed

    private void 离线账号ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_离线账号ActionPerformed
        显示在线账号.setText("账号在线; " + 在线账号() + "");
        账号提示语言.setText("[信息]:显示游戏所有离线玩家账号信息。");
        for (int i = ((DefaultTableModel) (this.账号信息.getModel())).getRowCount() - 1; i >= 0; i--) {
            ((DefaultTableModel) (this.账号信息.getModel())).removeRow(i);
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = null;
            ResultSet rs = null;
            ps = con.prepareStatement("SELECT * FROM accounts  WHERE loggedin = 0 ");
            rs = ps.executeQuery();
            while (rs.next()) {
                String 封号 = "";
                if (rs.getInt("banned") == 0) {
                    封号 = "正常";
                } else {
                    封号 = "封禁";
                }
                String 在线 = "";
                if (rs.getInt("loggedin") == 0) {
                    在线 = "不在线";
                } else {
                    在线 = "在线";
                }
                String QQ = "";
                if (rs.getString("qq") != null) {
                    QQ = rs.getString("qq");
                } else {
                    QQ = "未绑定QQ";
                }
                ((DefaultTableModel) 账号信息.getModel()).insertRow(账号信息.getRowCount(), new Object[]{
                    rs.getInt("id"), //账号ID
                    rs.getString("name"), //账号
                    rs.getString("SessionIP"), //账号IP地址
                    rs.getString("macs"), ///账号MAC地址
                    QQ,//注册时间
                    rs.getInt("ACash"),//点券
                    rs.getInt("mPoints"),//抵用
                    rs.getString("lastlogin"),//最近上线
                    在线,
                    封号,
                    rs.getInt("gm")
                });

            }
        } catch (SQLException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }
        读取显示账号();
    }//GEN-LAST:event_离线账号ActionPerformed

    private void 解封ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_解封ActionPerformed
        显示在线账号.setText("账号在线; " + 在线账号() + "");
        if (账号操作.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "请输入需要解封的账号 ");
            return;
        }
        String account = 账号操作.getText();
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps;

            ps = con.prepareStatement("Update accounts set banned = ? Where name = ?");
            ps.setInt(1, 0);
            ps.setString(2, account);
            ps.execute();
            ps.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "错误!\r\n" + ex);
        }
        账号提示语言.setText("[信息]:解封账号 " + account + " 成功。");
        //JOptionPane.showMessageDialog(null, "账号解封成功");
        刷新账号信息();
    }//GEN-LAST:event_解封ActionPerformed

    private void 注册的账号ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_注册的账号ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_注册的账号ActionPerformed

    private void 注册的密码ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_注册的密码ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_注册的密码ActionPerformed

    private void jButton35ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton35ActionPerformed
        注册新账号();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton35ActionPerformed

    private void jButton30ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton30ActionPerformed
        ChangePassWord();
    }//GEN-LAST:event_jButton30ActionPerformed

    private void 已封账号ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_已封账号ActionPerformed
        显示在线账号.setText("账号在线; " + 在线账号() + "");
        账号提示语言.setText("[信息]:显示游戏所有已被封禁的玩家账号信息。");
        for (int i = ((DefaultTableModel) (this.账号信息.getModel())).getRowCount() - 1; i >= 0; i--) {
            ((DefaultTableModel) (this.账号信息.getModel())).removeRow(i);
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = null;

            ResultSet rs = null;
            ps = con.prepareStatement("SELECT * FROM accounts WHERE banned > 0 ");
            rs = ps.executeQuery();
            while (rs.next()) {
                String 封号 = "";
                if (rs.getInt("banned") == 0) {
                    封号 = "正常";
                } else {
                    封号 = "封禁";
                }
                String 在线 = "";
                if (rs.getInt("loggedin") == 0) {
                    在线 = "不在线";
                } else {
                    在线 = "在线";
                }
                String QQ = "";
                if (rs.getString("qq") != null) {
                    QQ = rs.getString("qq");
                } else {
                    QQ = "未绑定QQ";
                }
                ((DefaultTableModel) 账号信息.getModel()).insertRow(账号信息.getRowCount(), new Object[]{
                    rs.getInt("id"), //账号ID
                    rs.getString("name"), //账号
                    rs.getString("SessionIP"), //账号IP地址
                    rs.getString("macs"), ///账号MAC地址
                    QQ,//注册时间
                    rs.getInt("ACash"),//点券
                    rs.getInt("mPoints"),//抵用
                    rs.getString("lastlogin"),//最近上线
                    在线,
                    封号,
                    rs.getInt("gm")
                });
            }
        } catch (SQLException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }
        读取显示账号();
    }//GEN-LAST:event_已封账号ActionPerformed

    private void 在线账号ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_在线账号ActionPerformed
        显示在线账号.setText("账号在线; " + 在线账号() + "");
        账号提示语言.setText("[信息]:显示游戏所有在线玩家账号信息。");
        for (int i = ((DefaultTableModel) (this.账号信息.getModel())).getRowCount() - 1; i >= 0; i--) {
            ((DefaultTableModel) (this.账号信息.getModel())).removeRow(i);
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = null;

            ResultSet rs = null;
            ps = con.prepareStatement("SELECT * FROM accounts  WHERE loggedin > 0 ");
            rs = ps.executeQuery();
            while (rs.next()) {
                String 封号 = "";
                if (rs.getInt("banned") == 0) {
                    封号 = "正常";
                } else {
                    封号 = "封禁";
                }
                String 在线 = "";
                if (rs.getInt("loggedin") == 0) {
                    在线 = "不在线";
                } else {
                    在线 = "在线";
                }
                String QQ = "";
                if (rs.getString("qq") != null) {
                    QQ = rs.getString("qq");
                } else {
                    QQ = "未绑定QQ";
                }
                ((DefaultTableModel) 账号信息.getModel()).insertRow(账号信息.getRowCount(), new Object[]{
                    rs.getInt("id"), //账号ID
                    rs.getString("name"), //账号
                    rs.getString("SessionIP"), //账号IP地址
                    rs.getString("macs"), ///账号MAC地址
                    QQ,//注册时间
                    rs.getInt("ACash"),//点券
                    rs.getInt("mPoints"),//抵用
                    rs.getString("lastlogin"),//最近上线
                    在线,
                    封号,
                    rs.getInt("gm")
                });
            }
        } catch (SQLException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }
        读取显示账号();
    }//GEN-LAST:event_在线账号ActionPerformed

    private void 删除账号ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_删除账号ActionPerformed
        显示在线账号.setText("账号在线; " + 在线账号() + "");
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        try {
            int n = JOptionPane.showConfirmDialog(this, "你确定要删除这个账号吗？", "信息", JOptionPane.YES_NO_OPTION);
            if (n == JOptionPane.YES_OPTION) {
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM accounts ");
                //ps1.setInt(1, Integer.parseInt(this.账号信息.getText()));
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlstr = " Delete from accounts where name ='" + this.账号操作.getText() + "'";
                    账号提示语言.setText("[信息]:删除账号 " + this.账号操作.getText() + " 成功。");
                    ps1.executeUpdate(sqlstr);
                    刷新账号信息();
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_删除账号ActionPerformed
    public void 读取显示账号() {
        账号信息.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int i = 账号信息.getSelectedRow();
                String a = 账号信息.getValueAt(i, 0).toString();
                String a1 = 账号信息.getValueAt(i, 1).toString();
                String a2 = 账号信息.getValueAt(i, 5).toString();
                String a3 = 账号信息.getValueAt(i, 6).toString();
                //if (账号信息.getValueAt(i, 4).toString() != null) {
                String a4 = 账号信息.getValueAt(i, 4).toString();
                QQ.setText(a4);
                //}
                String a10 = 账号信息.getValueAt(i, 10).toString();
                账号ID.setText(a);
                账号操作.setText(a1);
                账号.setText(a1);

                点券.setText(a2);
                抵用.setText(a3);
                管理1.setText(a10);
                账号提示语言.setText("[信息]:显示账号 " + 账号.getText() + " 下角色信息。");
                刷新角色信息2();
            }
        });
    }

    public static int 在线账号() {
        int data = 0;
        int p = 0;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT loggedin as DATA FROM accounts WHERE loggedin > 0");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    data = rs.getInt("DATA");
                    p += 1;
                }
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("在线账号、出错");
        }
        return p;
    }
    private void 封锁账号ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_封锁账号ActionPerformed
        显示在线账号.setText("账号在线; " + 在线账号() + "");
        if (账号操作.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "请输入需要封锁的账号 ");
            return;
        }
        String account = 账号操作.getText();
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps;

            ps = con.prepareStatement("Update accounts set banned = ? Where name = ?");
            ps.setInt(1, 1);
            ps.setString(2, account);
            ps.execute();
            ps.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "错误!\r\n" + ex);
        }
        账号提示语言.setText("[信息]:封锁账号 " + this.账号操作.getText() + " 成功。");
        刷新账号信息();
    }//GEN-LAST:event_封锁账号ActionPerformed

    private void 刷新角色信息ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_刷新角色信息ActionPerformed
        JOptionPane.showMessageDialog(null, "[信息]:显示游戏所有玩家角色信息。");
        刷新角色信息();
        显示在线玩家.setText("在线玩家; " + 在线玩家() + "");
    }//GEN-LAST:event_刷新角色信息ActionPerformed
    public static int 在线玩家() {
        int p = 0;
        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
            for (MapleCharacter chr : cserv.getPlayerStorage().getAllCharacters()) {
                if (chr != null) {
                    p++;
                }
            }
        }
        return p;
    }
    private void 显示管理角色ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_显示管理角色ActionPerformed
        显示在线玩家.setText("在线玩家; " + 在线玩家() + "");
        for (int i = ((DefaultTableModel) (this.角色信息.getModel())).getRowCount() - 1; i >= 0; i--) {
            ((DefaultTableModel) (this.角色信息.getModel())).removeRow(i);
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = null;

            ResultSet rs = null;
            ps = con.prepareStatement("SELECT * FROM characters  WHERE gm >0 ");
            rs = ps.executeQuery();
            while (rs.next()) {
                String 在线 = "";
                if (World.Find.findChannel(rs.getString("name")) > 0) {
                    在线 = "在线";
                } else {
                    在线 = "离线";
                }
                ((DefaultTableModel) 角色信息.getModel()).insertRow(角色信息.getRowCount(), new Object[]{
                    rs.getInt("id"),
                    rs.getInt("accountid"),
                    rs.getString("name"),
                    getJobNameById(rs.getInt("job")),
                    rs.getInt("level"),
                    rs.getInt("str"),
                    rs.getInt("dex"),
                    rs.getInt("int"),
                    rs.getInt("luk"),
                    rs.getInt("maxhp"),
                    rs.getInt("maxmp"),
                    rs.getInt("meso"),
                    rs.getInt("map"),
                    在线,
                    rs.getInt("gm"),
                    rs.getInt("hair"),
                    rs.getInt("face"
                    )});
                }
                JOptionPane.showMessageDialog(null, "[信息]:显示游戏所有管理员角色信息。");
            } catch (SQLException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }
    }//GEN-LAST:event_显示管理角色ActionPerformed

    private void jButton38ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton38ActionPerformed
        PreparedStatement ps = null;
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        boolean A = this.等级.getText().matches("[0-9]+");
        boolean B = this.GM.getText().matches("[0-9]+");
        boolean C = this.地图.getText().matches("[0-9]+");
        boolean D = this.金币1.getText().matches("[0-9]+");
        boolean E = this.MP.getText().matches("[0-9]+");
        boolean F = this.HP.getText().matches("[0-9]+");
        boolean G = this.运气.getText().matches("[0-9]+");
        boolean H = this.智力.getText().matches("[0-9]+");
        boolean Y = this.敏捷.getText().matches("[0-9]+");
        boolean J = this.力量.getText().matches("[0-9]+");
        if (角色昵称.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "角色昵称不能留空");
            return;
        }
        if (World.Find.findChannel(角色昵称.getText()) > 0) {
            JOptionPane.showMessageDialog(null, "请先将角色离线后再修改。");
            return;
        }
        int n = JOptionPane.showConfirmDialog(this, "你确定要修改这个角色吗？", "信息", JOptionPane.YES_NO_OPTION);
        if (n != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            ps = DatabaseConnection.getConnection().prepareStatement("UPDATE characters SET (name = ?,level = ?, str = ?, dex = ?, luk = ?,int = ?,  maxhp = ?, maxmp = ?, meso = ?, map = ?, gm = ?, hair = ?, face = ? )WHERE id = ?");
            ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM characters WHERE id = ?");
            ps1.setInt(1, Integer.parseInt(this.角色ID.getText()));
            rs = ps1.executeQuery();
            if (rs.next()) {
                String sqlString1 = null;
                String sqlString2 = null;
                String sqlString3 = null;
                String sqlString4 = null;
                String sqlString5 = null;
                String sqlString6 = null;
                String sqlString7 = null;
                String sqlString8 = null;
                String sqlString9 = null;
                String sqlString10 = null;
                String sqlString11 = null;
                String sqlString12 = null;
                String sqlString13 = null;
                sqlString1 = "update characters set name='" + this.角色昵称.getText() + "' where id=" + this.角色ID.getText() + ";";
                PreparedStatement name = DatabaseConnection.getConnection().prepareStatement(sqlString1);
                name.executeUpdate(sqlString1);
                sqlString2 = "update characters set level='" + this.等级.getText() + "' where id=" + this.角色ID.getText() + ";";
                PreparedStatement level = DatabaseConnection.getConnection().prepareStatement(sqlString2);
                level.executeUpdate(sqlString2);

                sqlString3 = "update characters set str='" + this.力量.getText() + "' where id=" + this.角色ID.getText() + ";";
                PreparedStatement str = DatabaseConnection.getConnection().prepareStatement(sqlString3);
                str.executeUpdate(sqlString3);

                sqlString4 = "update characters set dex='" + this.敏捷.getText() + "' where id=" + this.角色ID.getText() + ";";
                PreparedStatement dex = DatabaseConnection.getConnection().prepareStatement(sqlString4);
                dex.executeUpdate(sqlString4);

                sqlString5 = "update characters set luk='" + this.智力.getText() + "' where id=" + this.角色ID.getText() + ";";
                PreparedStatement luk = DatabaseConnection.getConnection().prepareStatement(sqlString5);
                luk.executeUpdate(sqlString5);

                sqlString6 = "update characters set `int`='" + this.运气.getText() + "' where id=" + this.角色ID.getText() + ";";
                PreparedStatement executeUpdate = DatabaseConnection.getConnection().prepareStatement(sqlString6);
                executeUpdate.executeUpdate(sqlString6);

                sqlString7 = "update characters set maxhp='" + this.HP.getText() + "' where id=" + this.角色ID.getText() + ";";
                PreparedStatement maxhp = DatabaseConnection.getConnection().prepareStatement(sqlString7);
                maxhp.executeUpdate(sqlString7);

                sqlString8 = "update characters set maxmp='" + this.MP.getText() + "' where id=" + this.角色ID.getText() + ";";
                PreparedStatement maxmp = DatabaseConnection.getConnection().prepareStatement(sqlString8);
                maxmp.executeUpdate(sqlString8);

                sqlString9 = "update characters set meso='" + this.金币1.getText() + "' where id=" + this.角色ID.getText() + ";";
                PreparedStatement meso = DatabaseConnection.getConnection().prepareStatement(sqlString9);
                meso.executeUpdate(sqlString9);

                sqlString10 = "update characters set map='" + this.地图.getText() + "' where id=" + this.角色ID.getText() + ";";
                PreparedStatement map = DatabaseConnection.getConnection().prepareStatement(sqlString10);
                map.executeUpdate(sqlString10);

                sqlString11 = "update characters set gm='" + this.GM.getText() + "' where id=" + this.角色ID.getText() + ";";
                PreparedStatement gm = DatabaseConnection.getConnection().prepareStatement(sqlString11);
                gm.executeUpdate(sqlString11);

                sqlString12 = "update characters set hair='" + this.发型.getText() + "' where id=" + this.发型.getText() + ";";
                PreparedStatement hair = DatabaseConnection.getConnection().prepareStatement(sqlString12);
                hair.executeUpdate(sqlString12);

                sqlString13 = "update characters set face='" + this.脸型.getText() + "' where id=" + this.脸型.getText() + ";";
                PreparedStatement face = DatabaseConnection.getConnection().prepareStatement(sqlString13);
                face.executeUpdate(sqlString13);
                JOptionPane.showMessageDialog(null, "[信息]:角色信息修改成功。");
                刷新角色信息();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton38ActionPerformed

    private void 删除角色ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_删除角色ActionPerformed
        String 输出 = "";
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        boolean result1 = this.角色ID.getText().matches("[0-9]+");

        if (result1) {
            int n = JOptionPane.showConfirmDialog(this, "你确定要删除这个角色吗？", "信息", JOptionPane.YES_NO_OPTION);
            if (n == JOptionPane.YES_OPTION) {
                try {
                    ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM characters WHERE id = ?");
                    ps1.setInt(1, Integer.parseInt(this.角色ID.getText()));
                    rs = ps1.executeQuery();
                    if (rs.next()) {
                        String sqlstr = " delete from characters where id =" + Integer.parseInt(this.角色ID.getText()) + "";
                        ps1.executeUpdate(sqlstr);
                        String sqlstr2 = " delete from inventoryitems where characterid =" + Integer.parseInt(this.角色ID.getText()) + "";
                        ps1.executeUpdate(sqlstr2);
                        String sqlstr4 = " delete from csitems where accountid =" + Integer.parseInt(this.角色ID.getText()) + "";
                        ps1.executeUpdate(sqlstr4);
                        String sqlstr5 = " delete from bank_item where cid =" + Integer.parseInt(this.角色ID.getText()) + "";
                        ps1.executeUpdate(sqlstr5);
                        String sqlstr6 = " delete from bossrank where cid =" + Integer.parseInt(this.角色ID.getText()) + "";
                        ps1.executeUpdate(sqlstr6);
                        String sqlstr7 = " delete from skills where characterid =" + Integer.parseInt(this.角色ID.getText()) + "";
                        ps1.executeUpdate(sqlstr7);
                        JOptionPane.showMessageDialog(null, "[信息]:成功删除角色 " + Integer.parseInt(this.角色ID.getText()) + " ，以及所有相关信息。");
                        刷新角色信息();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "[信息]:请选择删除的角色。");
        }
    }//GEN-LAST:event_删除角色ActionPerformed

    private void 角色昵称ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_角色昵称ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_角色昵称ActionPerformed

    private void 等级ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_等级ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_等级ActionPerformed

    private void 力量ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_力量ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_力量ActionPerformed

    private void 敏捷ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_敏捷ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_敏捷ActionPerformed

    private void 智力ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_智力ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_智力ActionPerformed

    private void 运气ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_运气ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_运气ActionPerformed

    private void HPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_HPActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_HPActionPerformed

    private void MPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MPActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_MPActionPerformed

    private void 金币1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_金币1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_金币1ActionPerformed

    private void 地图ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_地图ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_地图ActionPerformed

    private void GMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GMActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_GMActionPerformed

    private void 角色IDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_角色IDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_角色IDActionPerformed

    private void 卡号自救1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_卡号自救1ActionPerformed
        显示在线玩家.setText("在线玩家; " + 在线玩家() + "");
        PreparedStatement ps = null;
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        boolean result1 = this.角色ID.getText().matches("[0-9]+");
        if (result1) {
            int n = JOptionPane.showConfirmDialog(this, "你确定要解卡发型脸型自救这个角色吗？", "信息", JOptionPane.YES_NO_OPTION);
            if (n != JOptionPane.YES_OPTION) {
                return;
            }
            try {
                ps = DatabaseConnection.getConnection().prepareStatement("UPDATE characters SET (hair = ?,face = ?)WHERE id = ?");
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM characters WHERE id = ?");
                ps1.setInt(1, Integer.parseInt(this.角色ID.getText()));
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlString1 = null;
                    String sqlString2 = null;
                    sqlString1 = "update characters set hair='30000' where id=" + this.角色ID.getText() + ";";
                    PreparedStatement hair = DatabaseConnection.getConnection().prepareStatement(sqlString1);
                    hair.executeUpdate(sqlString1);
                    sqlString2 = "update characters set face='20000' where id=" + this.角色ID.getText() + ";";
                    PreparedStatement face = DatabaseConnection.getConnection().prepareStatement(sqlString2);
                    face.executeUpdate(sqlString2);
                    JOptionPane.showMessageDialog(null, "[信息]:解救成功，发型脸型初始化。");
                }
            } catch (SQLException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "[信息]:请选择卡发型脸型的角色。");
        }
    }//GEN-LAST:event_卡号自救1ActionPerformed

    private void 卡号自救2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_卡号自救2ActionPerformed
        显示在线玩家.setText("在线玩家; " + 在线玩家() + "");
        String 输出 = "";
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        boolean result1 = this.角色ID.getText().matches("[0-9]+");
        if (result1) {
            int n = JOptionPane.showConfirmDialog(this, "你确定要解卡物品自救这个角色吗？", "信息", JOptionPane.YES_NO_OPTION);
            if (n != JOptionPane.YES_OPTION) {
                return;
            }
            try {
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM inventoryitems WHERE characterid = ?");
                ps1.setInt(1, Integer.parseInt(this.角色ID.getText()));
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlstr2 = " delete from inventoryitems where characterid =" + Integer.parseInt(this.角色ID.getText()) + "";
                    ps1.executeUpdate(sqlstr2);
                    JOptionPane.showMessageDialog(null, "[信息]:角色已经进行38处理。");
                    刷新角色信息();
                }
            } catch (SQLException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            JOptionPane.showMessageDialog(null, "[信息]:请选择要38处理的角色。");
        }
    }//GEN-LAST:event_卡号自救2ActionPerformed

    private void 查看技能ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_查看技能ActionPerformed

        JOptionPane.showMessageDialog(null, "[信息]:查看玩家技能信息。");
        刷新技能信息();
    }//GEN-LAST:event_查看技能ActionPerformed

    private void 查看背包ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_查看背包ActionPerformed
        显示在线玩家.setText("在线玩家; " + 在线玩家() + "");
        boolean result1 = this.角色ID.getText().matches("[0-9]+");
        if (!result1) {
            JOptionPane.showMessageDialog(null, "[信息]:请选择角色。");
            return;
        }
        if (账号ID.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "[信息]:请先选择账号，再选择账号下的角色，接下来才可以查看游戏仓库。");
            return;
        }
        JOptionPane.showMessageDialog(null, "[信息]:查询速度跟角色信息量有关，请耐心等候。");
        刷新角色背包穿戴();
        刷新角色装备背包();
        刷新角色消耗背包();
        刷新角色设置背包();
        刷新角色其他背包();
        刷新角色特殊背包();
        刷新角色游戏仓库();
        刷新角色商城仓库();
        //        刷新角色金币拍卖行();
        //        刷新角色点券拍卖行();
        JOptionPane.showMessageDialog(null, "[信息]:请转到角色道具信息面板查看。");
    }//GEN-LAST:event_查看背包ActionPerformed

    private void 卡家族解救ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_卡家族解救ActionPerformed
        显示在线玩家.setText("在线玩家; " + 在线玩家() + "");
        PreparedStatement ps = null;
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        boolean result1 = this.角色ID.getText().matches("[0-9]+");
        if (result1) {
            try {
                ps = DatabaseConnection.getConnection().prepareStatement("UPDATE characters SET (guildid = ?,guildrank = ?,allianceRank = ?)WHERE id = ?");
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM characters WHERE id = ?");
                ps1.setInt(1, Integer.parseInt(this.角色ID.getText()));
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlString1 = null;
                    String sqlString2 = null;
                    String sqlString3 = null;
                    sqlString1 = "update characters set guildid='0' where id=" + this.角色ID.getText() + ";";
                    PreparedStatement hair = DatabaseConnection.getConnection().prepareStatement(sqlString1);
                    hair.executeUpdate(sqlString1);
                    sqlString2 = "update characters set guildrank='5' where id=" + this.角色ID.getText() + ";";
                    PreparedStatement face = DatabaseConnection.getConnection().prepareStatement(sqlString2);
                    face.executeUpdate(sqlString2);
                    sqlString3 = "update characters set allianceRank='5' where id=" + this.角色ID.getText() + ";";
                    PreparedStatement allianceRank = DatabaseConnection.getConnection().prepareStatement(sqlString3);
                    allianceRank.executeUpdate(sqlString3);
                    JOptionPane.showMessageDialog(null, "[信息]:解卡家族角色成功。");
                }
            } catch (SQLException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "[信息]:请选择卡家族的角色。");
        }
    }//GEN-LAST:event_卡家族解救ActionPerformed

    private void 脸型ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_脸型ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_脸型ActionPerformed

    private void 发型ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_发型ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_发型ActionPerformed

    private void 离线角色ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_离线角色ActionPerformed
        显示在线玩家.setText("在线玩家; " + 在线玩家() + "");
        for (int i = ((DefaultTableModel) (this.角色信息.getModel())).getRowCount() - 1; i >= 0; i--) {
            ((DefaultTableModel) (this.角色信息.getModel())).removeRow(i);
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = null;

            ResultSet rs = null;
            ps = con.prepareStatement("SELECT * FROM characters order by id desc");
            rs = ps.executeQuery();
            while (rs.next()) {
                if (World.Find.findChannel(rs.getString("name")) <= 0) {
                    ((DefaultTableModel) 角色信息.getModel()).insertRow(角色信息.getRowCount(), new Object[]{
                        rs.getInt("id"),
                        rs.getInt("accountid"),
                        rs.getString("name"),
                        getJobNameById(rs.getInt("job")),
                        rs.getInt("level"),
                        rs.getInt("str"),
                        rs.getInt("dex"),
                        rs.getInt("int"),
                        rs.getInt("luk"),
                        rs.getInt("maxhp"),
                        rs.getInt("maxmp"),
                        rs.getInt("meso"),
                        rs.getInt("map"),
                        "在线",
                        rs.getInt("gm"),
                        rs.getInt("hair"),
                        rs.getInt("face"
                        )});
                    }
                }
                JOptionPane.showMessageDialog(null, "[信息]:显示游戏所有离线角色信息。");

            } catch (SQLException ex) {
                Logger.getLogger(Start.class
                    .getName()).log(Level.SEVERE, null, ex);
            }
    }//GEN-LAST:event_离线角色ActionPerformed

    private void 在线角色ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_在线角色ActionPerformed
        显示在线玩家.setText("在线玩家; " + 在线玩家() + "");
        for (int i = ((DefaultTableModel) (this.角色信息.getModel())).getRowCount() - 1; i >= 0; i--) {
            ((DefaultTableModel) (this.角色信息.getModel())).removeRow(i);
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = null;

            ResultSet rs = null;
            ps = con.prepareStatement("SELECT * FROM characters order by id desc");
            rs = ps.executeQuery();
            while (rs.next()) {
                if (World.Find.findChannel(rs.getString("name")) > 0) {
                    ((DefaultTableModel) 角色信息.getModel()).insertRow(角色信息.getRowCount(), new Object[]{
                        rs.getInt("id"),
                        rs.getInt("accountid"),
                        rs.getString("name"),
                        getJobNameById(rs.getInt("job")),
                        rs.getInt("level"),
                        rs.getInt("str"),
                        rs.getInt("dex"),
                        rs.getInt("int"),
                        rs.getInt("luk"),
                        rs.getInt("maxhp"),
                        rs.getInt("maxmp"),
                        rs.getInt("meso"),
                        rs.getInt("map"),
                        "在线",
                        rs.getInt("gm"),
                        rs.getInt("hair"),
                        rs.getInt("face"
                        )});
                    }
                }
                JOptionPane.showMessageDialog(null, "[信息]:显示游戏所有在线角色信息。");

            } catch (SQLException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }
    }//GEN-LAST:event_在线角色ActionPerformed

    private void 背包物品名字1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_背包物品名字1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_背包物品名字1ActionPerformed

    private void 身上穿戴序号1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_身上穿戴序号1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_身上穿戴序号1ActionPerformed

    private void 背包物品代码1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_背包物品代码1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_背包物品代码1ActionPerformed

    private void 删除穿戴装备ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_删除穿戴装备ActionPerformed
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        boolean result = this.身上穿戴序号1.getText().matches("[0-9]+");
        if (result == true) {
            try {
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM inventoryitems WHERE inventoryitemid = ?");
                ps1.setInt(1, Integer.parseInt(this.身上穿戴序号1.getText()));
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlstr = " delete from inventoryitems where inventoryitemid =" + Integer.parseInt(this.身上穿戴序号1.getText()) + "";
                    ps1.executeUpdate(sqlstr);
                    刷新角色背包穿戴();
                }
            } catch (SQLException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "请选择你要删除的物品");
        }
    }//GEN-LAST:event_删除穿戴装备ActionPerformed

    private void 装备背包物品名字ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_装备背包物品名字ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_装备背包物品名字ActionPerformed

    private void 装备背包物品序号ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_装备背包物品序号ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_装备背包物品序号ActionPerformed

    private void 装备背包物品代码ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_装备背包物品代码ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_装备背包物品代码ActionPerformed

    private void 删除装备背包ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_删除装备背包ActionPerformed
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        boolean result = this.装备背包物品序号.getText().matches("[0-9]+");
        if (result == true) {
            try {
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM inventoryitems WHERE inventoryitemid = ?");
                ps1.setInt(1, Integer.parseInt(this.装备背包物品序号.getText()));
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlstr = " delete from inventoryitems where inventoryitemid =" + Integer.parseInt(this.装备背包物品序号.getText()) + "";
                    ps1.executeUpdate(sqlstr);
                    刷新角色装备背包();
                }
            } catch (SQLException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "请选择你要删除的物品");
        }
    }//GEN-LAST:event_删除装备背包ActionPerformed

    private void 消耗背包物品名字ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_消耗背包物品名字ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_消耗背包物品名字ActionPerformed

    private void 消耗背包物品序号ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_消耗背包物品序号ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_消耗背包物品序号ActionPerformed

    private void 消耗背包物品代码ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_消耗背包物品代码ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_消耗背包物品代码ActionPerformed

    private void 删除消耗背包ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_删除消耗背包ActionPerformed
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        boolean result = this.消耗背包物品序号.getText().matches("[0-9]+");
        if (result == true) {
            try {
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM inventoryitems WHERE inventoryitemid = ?");
                ps1.setInt(1, Integer.parseInt(this.消耗背包物品序号.getText()));
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlstr = " delete from inventoryitems where inventoryitemid =" + Integer.parseInt(this.消耗背包物品序号.getText()) + "";
                    ps1.executeUpdate(sqlstr);
                    刷新角色消耗背包();
                }
            } catch (SQLException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "请选择你要删除的物品");
        }
    }//GEN-LAST:event_删除消耗背包ActionPerformed

    private void 设置背包物品名字ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_设置背包物品名字ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_设置背包物品名字ActionPerformed

    private void 设置背包物品序号ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_设置背包物品序号ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_设置背包物品序号ActionPerformed

    private void 设置背包物品代码ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_设置背包物品代码ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_设置背包物品代码ActionPerformed

    private void 删除设置背包ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_删除设置背包ActionPerformed
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        boolean result = this.设置背包物品序号.getText().matches("[0-9]+");
        if (result == true) {
            try {
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM inventoryitems WHERE inventoryitemid = ?");
                ps1.setInt(1, Integer.parseInt(this.设置背包物品序号.getText()));
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlstr = " delete from inventoryitems where inventoryitemid =" + Integer.parseInt(this.设置背包物品序号.getText()) + "";
                    ps1.executeUpdate(sqlstr);
                    刷新角色设置背包();
                }
            } catch (SQLException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "请选择你要删除的物品");
        }
    }//GEN-LAST:event_删除设置背包ActionPerformed

    private void 其他背包物品名字ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_其他背包物品名字ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_其他背包物品名字ActionPerformed

    private void 其他背包物品序号ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_其他背包物品序号ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_其他背包物品序号ActionPerformed

    private void 其他背包物品代码ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_其他背包物品代码ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_其他背包物品代码ActionPerformed

    private void 删除其他背包ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_删除其他背包ActionPerformed
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        boolean result = this.其他背包物品序号.getText().matches("[0-9]+");
        if (result == true) {
            try {
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM inventoryitems WHERE inventoryitemid = ?");
                ps1.setInt(1, Integer.parseInt(this.其他背包物品序号.getText()));
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlstr = " delete from inventoryitems where inventoryitemid =" + Integer.parseInt(this.其他背包物品序号.getText()) + "";
                    ps1.executeUpdate(sqlstr);
                    刷新角色其他背包();
                }
            } catch (SQLException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "请选择你要删除的物品");
        }
    }//GEN-LAST:event_删除其他背包ActionPerformed

    private void 特殊背包物品名字ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_特殊背包物品名字ActionPerformed

    }//GEN-LAST:event_特殊背包物品名字ActionPerformed

    private void 特殊背包物品序号ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_特殊背包物品序号ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_特殊背包物品序号ActionPerformed

    private void 特殊背包物品代码ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_特殊背包物品代码ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_特殊背包物品代码ActionPerformed

    private void 删除特殊背包ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_删除特殊背包ActionPerformed
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        boolean result = this.特殊背包物品序号.getText().matches("[0-9]+");
        if (result == true) {
            try {
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM inventoryitems WHERE inventoryitemid = ?");
                ps1.setInt(1, Integer.parseInt(this.特殊背包物品序号.getText()));
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlstr = " delete from inventoryitems where inventoryitemid =" + Integer.parseInt(this.特殊背包物品序号.getText()) + "";
                    ps1.executeUpdate(sqlstr);
                    刷新角色特殊背包();
                }
            } catch (SQLException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "请选择你要删除的物品");
        }
    }//GEN-LAST:event_删除特殊背包ActionPerformed

    private void 游戏仓库物品名字ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_游戏仓库物品名字ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_游戏仓库物品名字ActionPerformed

    private void 游戏仓库物品序号ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_游戏仓库物品序号ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_游戏仓库物品序号ActionPerformed

    private void 游戏仓库物品代码ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_游戏仓库物品代码ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_游戏仓库物品代码ActionPerformed

    private void 删除游戏仓库ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_删除游戏仓库ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_删除游戏仓库ActionPerformed

    private void 商城仓库物品名字ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_商城仓库物品名字ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_商城仓库物品名字ActionPerformed

    private void 商城仓库物品序号ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_商城仓库物品序号ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_商城仓库物品序号ActionPerformed

    private void 商城仓库物品代码ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_商城仓库物品代码ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_商城仓库物品代码ActionPerformed

    private void 删除商城仓库ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_删除商城仓库ActionPerformed
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        boolean result = this.商城仓库物品序号.getText().matches("[0-9]+");
        if (result == true) {
            try {
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM csitems WHERE inventoryitemid = ?");
                ps1.setInt(1, Integer.parseInt(this.商城仓库物品序号.getText()));
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlstr = " delete from csitems where inventoryitemid =" + Integer.parseInt(this.商城仓库物品序号.getText()) + "";
                    ps1.executeUpdate(sqlstr);
                    刷新角色商城仓库();
                }
            } catch (SQLException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "请选择你要删除的物品");
        }
    }//GEN-LAST:event_删除商城仓库ActionPerformed

    private void 拍卖行物品名字1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_拍卖行物品名字1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_拍卖行物品名字1ActionPerformed

    private void 角色点券拍卖行序号ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_角色点券拍卖行序号ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_角色点券拍卖行序号ActionPerformed

    private void 拍卖行物品代码1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_拍卖行物品代码1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_拍卖行物品代码1ActionPerformed

    private void 删除拍卖行1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_删除拍卖行1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_删除拍卖行1ActionPerformed

    private void 拍卖行物品名字ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_拍卖行物品名字ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_拍卖行物品名字ActionPerformed

    private void 角色金币拍卖行序号ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_角色金币拍卖行序号ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_角色金币拍卖行序号ActionPerformed

    private void 拍卖行物品代码ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_拍卖行物品代码ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_拍卖行物品代码ActionPerformed

    private void 删除拍卖行ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_删除拍卖行ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_删除拍卖行ActionPerformed

    private void 技能名字ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_技能名字ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_技能名字ActionPerformed

    private void 修改技能ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_修改技能ActionPerformed
        PreparedStatement ps = null;
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        boolean result1 = this.技能序号.getText().matches("[0-9]+");

        if (result1) {
            try {
                ps = DatabaseConnection.getConnection().prepareStatement("UPDATE skills SET skilllevel = ?,masterlevel = ? WHERE id = ?");
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM skills WHERE id = ?");
                ps1.setInt(1, Integer.parseInt(this.技能序号.getText()));
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlString1 = null;
                    String sqlString2 = null;

                    sqlString1 = "update skills set skilllevel='" + this.技能目前等级.getText() + "' where id=" + this.技能序号.getText() + ";";
                    PreparedStatement skilllevel = DatabaseConnection.getConnection().prepareStatement(sqlString1);
                    skilllevel.executeUpdate(sqlString1);

                    sqlString2 = "update skills set masterlevel='" + this.技能最高等级.getText() + "' where id=" + this.技能序号.getText() + ";";
                    PreparedStatement masterlevel = DatabaseConnection.getConnection().prepareStatement(sqlString2);
                    masterlevel.executeUpdate(sqlString2);

                    刷新技能信息();
                }
            } catch (SQLException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "请选择你要修改的技能");
        }// TODO add your handling code here:
    }//GEN-LAST:event_修改技能ActionPerformed

    private void 删除技能ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_删除技能ActionPerformed

        PreparedStatement ps1 = null;
        ResultSet rs = null;
        boolean result1 = this.技能序号.getText().matches("[0-9]+");

        if (result1) {
            if (Integer.parseInt(this.技能序号.getText()) < 0) {
                JOptionPane.showMessageDialog(null, "请填写正确的值");
            }
            try {
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM skills WHERE id = ?");
                ps1.setInt(1, Integer.parseInt(this.技能序号.getText()));
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlstr = " delete from skills where id =" + Integer.parseInt(this.技能序号.getText()) + "";
                    ps1.executeUpdate(sqlstr);
                    刷新技能信息();
                }
            } catch (SQLException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "请选择你要删除的技能");
        }
    }//GEN-LAST:event_删除技能ActionPerformed

    private void 修改技能1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_修改技能1ActionPerformed
        JOptionPane.showMessageDialog(null, "[信息]:查看玩家技能信息。");
        刷新技能信息();
    }//GEN-LAST:event_修改技能1ActionPerformed

    private void jButton36ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton36ActionPerformed
        openWindow(Windows.商店管理控制台);
    }//GEN-LAST:event_jButton36ActionPerformed

    private void jButton37ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton37ActionPerformed
        openWindow(Windows.怪物爆率控制台);
    }//GEN-LAST:event_jButton37ActionPerformed

    private void jButton50ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton50ActionPerformed
        openWindow(Windows.箱子爆率控制台);
    }//GEN-LAST:event_jButton50ActionPerformed

    private void 开启双倍经验ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_开启双倍经验ActionPerformed
        boolean result1 = this.双倍经验持续时间.getText().matches("[0-9]+");
        if (result1) {
            if (双倍经验持续时间.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "持续时间不能为空");
                return;
            }
            int 原始经验 = Integer.parseInt(ServerProperties.getProperty("ZeroMS.expRate"));
            int 双倍经验活动 = 原始经验 * 2;
            int seconds = 0;
            int mins = 0;
            int hours = Integer.parseInt(this.双倍经验持续时间.getText());
            int time = seconds + (mins * 60) + (hours * 60 * 60);
            final String rate = "经验";
            World.scheduleRateDelay(rate, time);
            for (ChannelServer cservs : ChannelServer.getAllInstances()) {
                cservs.setExpRate(双倍经验活动);
            }
            World.Broadcast.broadcastSmega(MaplePacketCreator.serverNotice(9, 20, "[倍率活动] : 游戏开始 2 倍打怪经验活动，将持续 " + hours + " 小时，请各位玩家狂欢吧！"));
            JOptionPane.showMessageDialog(null, "成功开启双倍经验活动，持续 " + hours + " 小时");
        } else {
            JOptionPane.showMessageDialog(null, "持续时间输入不正确");
        }
    }//GEN-LAST:event_开启双倍经验ActionPerformed

    private void 开启双倍爆率ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_开启双倍爆率ActionPerformed
        boolean result1 = this.双倍爆率持续时间.getText().matches("[0-9]+");
        if (result1) {
            if (双倍爆率持续时间.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "持续时间不能为空");
                return;
            }
            int 原始爆率 = Integer.parseInt(ServerProperties.getProperty("ZeroMS.dropRate"));
            int 双倍爆率活动 = 原始爆率 * 2;
            int seconds = 0;
            int mins = 0;
            int hours = Integer.parseInt(this.双倍经验持续时间.getText());
            int time = seconds + (mins * 60) + (hours * 60 * 60);
            final String rate = "爆率";
            World.scheduleRateDelay(rate, time);
            for (ChannelServer cservs : ChannelServer.getAllInstances()) {
                cservs.setExpRate(双倍爆率活动);
            }
            World.Broadcast.broadcastSmega(MaplePacketCreator.serverNotice(9, 20, "[倍率活动] : 游戏开始 2 倍打怪爆率活动，将持续 " + hours + " 小时，请各位玩家狂欢吧！"));
            JOptionPane.showMessageDialog(null, "成功开启双倍爆率活动，持续 " + hours + " 小时");
        } else {
            JOptionPane.showMessageDialog(null, "持续时间输入不正确");
        }
    }//GEN-LAST:event_开启双倍爆率ActionPerformed

    private void 开启双倍金币ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_开启双倍金币ActionPerformed
        boolean result1 = this.双倍金币持续时间.getText().matches("[0-9]+");
        if (result1) {
            if (双倍金币持续时间.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "持续时间不能为空");
                return;
            }
            int 原始金币 = Integer.parseInt(ServerProperties.getProperty("ZeroMS.mesoRate"));
            int 双倍金币活动 = 原始金币 * 2;
            int seconds = 0;
            int mins = 0;
            int hours = Integer.parseInt(this.双倍金币持续时间.getText());
            int time = seconds + (mins * 60) + (hours * 60 * 60);
            final String rate = "金币";
            World.scheduleRateDelay(rate, time);
            for (ChannelServer cservs : ChannelServer.getAllInstances()) {
                cservs.setExpRate(双倍金币活动);
            }
            World.Broadcast.broadcastSmega(MaplePacketCreator.serverNotice(9, 20, "[倍率活动] : 游戏开始 2 倍打怪金币活动，将持续 " + hours + " 小时，请各位玩家狂欢吧！"));
            JOptionPane.showMessageDialog(null, "成功开启双倍金币活动，持续 " + hours + " 小时");
        } else {
            JOptionPane.showMessageDialog(null, "持续时间输入不正确");
        }
    }//GEN-LAST:event_开启双倍金币ActionPerformed

    private void 开启三倍经验ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_开启三倍经验ActionPerformed
        boolean result1 = this.三倍经验持续时间.getText().matches("[0-9]+");
        if (result1) {
            if (三倍经验持续时间.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "持续时间不能为空");
                return;
            }
            int 原始经验 = Integer.parseInt(ServerProperties.getProperty("ZeroMS.expRate"));
            int 三倍经验活动 = 原始经验 * 3;
            int seconds = 0;
            int mins = 0;
            int hours = Integer.parseInt(this.三倍经验持续时间.getText());
            int time = seconds + (mins * 60) + (hours * 60 * 60);
            final String rate = "经验";
            World.scheduleRateDelay(rate, time);
            for (ChannelServer cservs : ChannelServer.getAllInstances()) {
                cservs.setExpRate(三倍经验活动);
            }
            World.Broadcast.broadcastSmega(MaplePacketCreator.serverNotice(9, 20, "[倍率活动] : 游戏开始 3 倍打怪经验活动，将持续 " + hours + " 小时，请各位玩家狂欢吧！"));
            JOptionPane.showMessageDialog(null, "成功开启三倍经验活动，持续 " + hours + " 小时");
        } else {
            JOptionPane.showMessageDialog(null, "持续时间输入不正确");
        }
    }//GEN-LAST:event_开启三倍经验ActionPerformed

    private void 开启三倍爆率ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_开启三倍爆率ActionPerformed
        boolean result1 = this.三倍爆率持续时间.getText().matches("[0-9]+");
        if (result1) {
            if (三倍爆率持续时间.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "持续时间不能为空");
                return;
            }
            int 原始爆率 = Integer.parseInt(ServerProperties.getProperty("ZeroMS.dropRate"));
            int 三倍爆率活动 = 原始爆率 * 3;
            int seconds = 0;
            int mins = 0;
            int hours = Integer.parseInt(this.三倍经验持续时间.getText());
            int time = seconds + (mins * 60) + (hours * 60 * 60);
            final String rate = "爆率";
            World.scheduleRateDelay(rate, time);
            for (ChannelServer cservs : ChannelServer.getAllInstances()) {
                cservs.setExpRate(三倍爆率活动);
            }
            World.Broadcast.broadcastSmega(MaplePacketCreator.serverNotice(9, 20, "[倍率活动] : 游戏开始 3 倍打怪爆率活动，将持续 " + hours + " 小时，请各位玩家狂欢吧！"));
            JOptionPane.showMessageDialog(null, "成功开启三倍爆率活动，持续 " + hours + " 小时");
        } else {
            JOptionPane.showMessageDialog(null, "持续时间输入不正确");
        }
    }//GEN-LAST:event_开启三倍爆率ActionPerformed

    private void 开启三倍金币ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_开启三倍金币ActionPerformed
        boolean result1 = this.三倍金币持续时间.getText().matches("[0-9]+");
        if (result1) {
            if (三倍金币持续时间.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "持续时间不能为空");
                return;
            }
            int 原始金币 = Integer.parseInt(ServerProperties.getProperty("ZeroMS.mesoRate"));
            int 三倍金币活动 = 原始金币 * 3;
            int seconds = 0;
            int mins = 0;
            int hours = Integer.parseInt(this.三倍金币持续时间.getText());
            int time = seconds + (mins * 60) + (hours * 60 * 60);
            final String rate = "金币";
            World.scheduleRateDelay(rate, time);
            for (ChannelServer cservs : ChannelServer.getAllInstances()) {
                cservs.setExpRate(三倍金币活动);
            }
            World.Broadcast.broadcastSmega(MaplePacketCreator.serverNotice(9, 20, "[倍率活动] : 游戏开始 3 倍打怪金币活动，将持续 " + hours + " 小时，请各位玩家狂欢吧！"));
            JOptionPane.showMessageDialog(null, "成功开启三倍金币活动，持续 " + hours + " 小时");
        } else {
            JOptionPane.showMessageDialog(null, "持续时间输入不正确");
        }
    }//GEN-LAST:event_开启三倍金币ActionPerformed

    private void jButton51ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton51ActionPerformed
        openWindow(Windows.批量删除工具);
    }//GEN-LAST:event_jButton51ActionPerformed

    private void jButton52ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton52ActionPerformed
        openWindow(Windows.游戏家族控制台);
    }//GEN-LAST:event_jButton52ActionPerformed

    private void jButton53ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton53ActionPerformed
        openWindow(Windows.MACip封禁);
    }//GEN-LAST:event_jButton53ActionPerformed

    private void 学院系统ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_学院系统ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_学院系统ActionPerformed

    private void 检测外挂自动封号ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_检测外挂自动封号ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_检测外挂自动封号ActionPerformed

    private void 离线挂机开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_离线挂机开关ActionPerformed
        按键开关("离线挂机开关", 77);
        刷新离线挂机开关();
    }//GEN-LAST:event_离线挂机开关ActionPerformed

    private void 泡点值修改1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_泡点值修改1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_泡点值修改1ActionPerformed

    private void 泡点金币开关1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_泡点金币开关1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_泡点金币开关1ActionPerformed

    private void 泡点经验开关1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_泡点经验开关1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_泡点经验开关1ActionPerformed

    private void 泡点点券开关1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_泡点点券开关1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_泡点点券开关1ActionPerformed

    private void 泡点抵用开关1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_泡点抵用开关1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_泡点抵用开关1ActionPerformed

    private void 泡点豆豆开关1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_泡点豆豆开关1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_泡点豆豆开关1ActionPerformed

    private void 离线泡点值修改ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_离线泡点值修改ActionPerformed
        PreparedStatement ps = null;
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        boolean result1 = this.离线泡点值.getText().matches("[0-9]+");
        if (result1) {
            try {
                ps = DatabaseConnection.getConnection().prepareStatement("UPDATE configvalues SET Val = ? WHERE id = ?");

                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM configvalues WHERE id = ?");

                ps1.setInt(1, Integer.parseInt(this.离线泡点序号.getText()));
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlString1 = null;
                    sqlString1 = "update configvalues set Val = '" + this.离线泡点值.getText() + "' where id= " + this.离线泡点序号.getText() + ";";
                    PreparedStatement Val = DatabaseConnection.getConnection().prepareStatement(sqlString1);
                    Val.executeUpdate(sqlString1);
                    刷新离线泡点设置();
                    ZeroMS_UI.GetConfigValues();
                    JOptionPane.showMessageDialog(null, "[信息]:修改成功");
                }
            } catch (SQLException ex) {
                Logger.getLogger(ZeroMS_UI.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "[信息]:请选择你要修改的值。");
        }
    }//GEN-LAST:event_离线泡点值修改ActionPerformed

    private void 离线泡点豆豆开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_离线泡点豆豆开关ActionPerformed
        int 离线泡点豆豆开关 = ZeroMS_UI.ConfigValuesMap.get("离线泡点豆豆开关");
        if (离线泡点豆豆开关 <= 0) {
            按键开关("离线泡点豆豆开关", 721);
            刷新离线泡点豆豆开关();
        } else {
            按键开关("离线泡点豆豆开关", 721);
            刷新离线泡点豆豆开关();
        }
    }//GEN-LAST:event_离线泡点豆豆开关ActionPerformed

    private void 离线泡点金币开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_离线泡点金币开关ActionPerformed
        int 离线泡点金币开关 = ZeroMS_UI.ConfigValuesMap.get("离线泡点金币开关");
        if (离线泡点金币开关 <= 0) {
            按键开关("离线泡点金币开关", 713);
            刷新离线泡点金币开关();
        } else {
            按键开关("离线泡点金币开关", 713);
            刷新离线泡点金币开关();
        }
    }//GEN-LAST:event_离线泡点金币开关ActionPerformed

    private void 离线泡点经验开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_离线泡点经验开关ActionPerformed
        int 离线泡点经验开关 = ZeroMS_UI.ConfigValuesMap.get("离线泡点经验开关");
        if (离线泡点经验开关 <= 0) {
            按键开关("离线泡点经验开关", 717);
            刷新离线泡点经验开关();
        } else {
            按键开关("离线泡点经验开关", 717);
            刷新离线泡点经验开关();
        }
    }//GEN-LAST:event_离线泡点经验开关ActionPerformed

    private void 离线泡点点券开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_离线泡点点券开关ActionPerformed
        int 离线泡点点券开关 = ZeroMS_UI.ConfigValuesMap.get("离线泡点点券开关");
        if (离线泡点点券开关 <= 0) {
            按键开关("离线泡点点券开关", 715);
            刷新离线泡点点券开关();
        } else {
            按键开关("离线泡点点券开关", 715);
            刷新离线泡点点券开关();
        }
    }//GEN-LAST:event_离线泡点点券开关ActionPerformed

    private void 离线泡点抵用开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_离线泡点抵用开关ActionPerformed
        int 离线泡点抵用开关 = ZeroMS_UI.ConfigValuesMap.get("离线泡点抵用开关");
        if (离线泡点抵用开关 <= 0) {
            按键开关("离线泡点抵用开关", 719);
            刷新离线泡点抵用开关();
        } else {
            按键开关("离线泡点抵用开关", 719);
            刷新离线泡点抵用开关();
        }
    }//GEN-LAST:event_离线泡点抵用开关ActionPerformed

    private void 物品叠加开关1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_物品叠加开关1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_物品叠加开关1ActionPerformed

    private void 泡点豆豆开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_泡点豆豆开关ActionPerformed
        int 泡点豆豆开关 = ZeroMS_UI.ConfigValuesMap.get("泡点豆豆开关");
        if (泡点豆豆开关 <= 0) {
            按键开关("泡点豆豆开关", 711);
            刷新泡点豆豆开关();
        } else {
            按键开关("泡点豆豆开关", 711);
            刷新泡点豆豆开关();
        }
    }//GEN-LAST:event_泡点豆豆开关ActionPerformed

    private void 泡点抵用开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_泡点抵用开关ActionPerformed
        int 泡点抵用开关 = ZeroMS_UI.ConfigValuesMap.get("泡点抵用开关");
        if (泡点抵用开关 <= 0) {
            按键开关("泡点抵用开关", 707);
            刷新泡点抵用开关();
        } else {
            按键开关("泡点抵用开关", 707);
            刷新泡点抵用开关();
        }
    }//GEN-LAST:event_泡点抵用开关ActionPerformed

    private void 泡点点券开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_泡点点券开关ActionPerformed
        int 泡点点券开关 = ZeroMS_UI.ConfigValuesMap.get("泡点点券开关");
        if (泡点点券开关 <= 0) {
            按键开关("泡点点券开关", 703);
            刷新泡点点券开关();
        } else {
            按键开关("泡点点券开关", 703);
            刷新泡点点券开关();
        }
    }//GEN-LAST:event_泡点点券开关ActionPerformed

    private void 泡点经验开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_泡点经验开关ActionPerformed

        int 泡点经验开关 = ZeroMS_UI.ConfigValuesMap.get("泡点经验开关");
        if (泡点经验开关 <= 0) {
            按键开关("泡点经验开关", 705);
            刷新泡点经验开关();
        } else {
            按键开关("泡点经验开关", 705);
            刷新泡点经验开关();
        }
    }//GEN-LAST:event_泡点经验开关ActionPerformed

    private void 泡点金币开关ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_泡点金币开关ActionPerformed
        int 泡点金币开关 = ZeroMS_UI.ConfigValuesMap.get("泡点金币开关");
        if (泡点金币开关 <= 0) {
            按键开关("泡点金币开关", 701);
            刷新泡点金币开关();
        } else {
            按键开关("泡点金币开关", 701);
            刷新泡点金币开关();
        }
    }//GEN-LAST:event_泡点金币开关ActionPerformed

    private void 泡点值修改ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_泡点值修改ActionPerformed
        PreparedStatement ps = null;
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        boolean result1 = this.泡点值.getText().matches("[0-9]+");
        if (result1) {
            try {
                ps = DatabaseConnection.getConnection().prepareStatement("UPDATE configvalues SET Val = ? WHERE id = ?");

                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM configvalues WHERE id = ?");

                ps1.setInt(1, Integer.parseInt(this.泡点序号.getText()));
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlString1 = null;
                    sqlString1 = "update configvalues set Val = '" + this.泡点值.getText() + "' where id= " + this.泡点序号.getText() + ";";
                    PreparedStatement Val = DatabaseConnection.getConnection().prepareStatement(sqlString1);
                    Val.executeUpdate(sqlString1);
                    刷新泡点设置();
                    ZeroMS_UI.GetConfigValues();
                    JOptionPane.showMessageDialog(null, "[信息]:修改成功");
                }
            } catch (SQLException ex) {
                Logger.getLogger(ZeroMS_UI.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "[信息]:请选择你要修改的值。");
        }
    }//GEN-LAST:event_泡点值修改ActionPerformed

    private void 匿名广播ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_匿名广播ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_匿名广播ActionPerformed

    private void 清理怪物时间ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_清理怪物时间ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_清理怪物时间ActionPerformed
        private void ChangePassWord() {
        /* boolean result1 = this.jTextField24.getText().matches("[0-9]+");
        boolean result2 = this.jTextField25.getText().matches("[0-9]+");
        if (result1 && result2) {*/
        String account = 注册的账号.getText();
        String password = 注册的密码.getText();

        if (password.length() > 12) {
            账号提示语言.setText("[信息]:修改密码失败，密码过长。");
            return;
        }
        if (!AutoRegister.getAccountExists(account)) {
            账号提示语言.setText("[信息]:修改密码失败，账号不存在。");
            return;
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps;
            ps = con.prepareStatement("Update accounts set password = ? Where name = ?");
            ps.setString(1, LoginCrypto.hexSha1(password));
            ps.setString(2, account);
            ps.execute();
            ps.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "错误!\r\n" + ex);
        }
        账号提示语言.setText("[信息]:修改密码成功。账号: " + account + " 密码: " + password + "");
        //JOptionPane.showMessageDialog(null, "账号: " + account + "\r\n密码: " + password);
        /* } else 
            JOptionPane.showMessageDialog(null, "请填写数据");
            return;
        }*/
    }
    
    public void 注册新账号() {
        boolean result1 = this.注册的账号.getText().matches("[0-9]+");
        boolean result2 = this.注册的密码.getText().matches("[0-9]+");
        if (注册的账号.getText().equals("") || 注册的密码.getText().equals("")) {
            账号提示语言.setText("[信息]:请填写注册的账号密码");
            return;
        } else {
            Connection con;
            String account = 注册的账号.getText();
            String password = 注册的密码.getText();

            if (password.length() > 10) {
                账号提示语言.setText("[信息]:注册失败，密码过长");
                return;
            }
            if (AutoRegister.getAccountExists(account)) {
                账号提示语言.setText("[信息]:注册失败，账号已存在");
                return;
            }
            try {
                con = DatabaseConnection.getConnection();
            } catch (Exception ex) {
                System.out.println(ex);
                return;
            }
            try {
                PreparedStatement ps = con.prepareStatement("INSERT INTO accounts (name, password) VALUES (?,?)");
                ps.setString(1, account);
                ps.setString(2, LoginCrypto.hexSha1(password));
                ps.executeUpdate();
                刷新账号信息();
                账号提示语言.setText("[信息]:注册成功。账号: " + account + " 密码: " + password + "");
            } catch (SQLException ex) {
                System.out.println(ex);
                return;
            }
        }
    }

    private void 刷新账号信息() {
        for (int i = ((DefaultTableModel) (this.账号信息.getModel())).getRowCount() - 1; i >= 0; i--) {
            ((DefaultTableModel) (this.账号信息.getModel())).removeRow(i);
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = null;
            ResultSet rs = null;
            ps = con.prepareStatement("SELECT * FROM accounts order by id desc");
            rs = ps.executeQuery();
            while (rs.next()) {
                String 封号 = "";
                if (rs.getInt("banned") == 0) {
                    封号 = "正常";
                } else {
                    封号 = "封禁";
                }
                String 在线 = "";
                if (rs.getInt("loggedin") == 0) {
                    // Font fnA = new Font("细明本",Font.PLAIN,12);
                    在线 = "不在线";
                } else {
                    在线 = "在线";
                }
                String QQ = "";
                if (rs.getString("qq") != null) {
                    QQ = rs.getString("qq");
                } else {
                    QQ = "未绑定QQ";
                }
                ((DefaultTableModel) 账号信息.getModel()).insertRow(账号信息.getRowCount(), new Object[]{
                    rs.getInt("id"), //账号ID
                    rs.getString("name"), //账号
                    rs.getString("SessionIP"), //账号IP地址
                    rs.getString("macs"), ///账号MAC地址
                    QQ,
                    rs.getInt("ACash"),//点券
                    rs.getInt("mPoints"),//抵用
                    rs.getString("lastlogin"),//最近上线
                    //rs.getInt("loggedin"),//在线
                    //rs.getInt("banned")//封号
                    在线,
                    封号,
                    rs.getInt("gm")
                });
            }
        } catch (SQLException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }
        读取显示账号();

    }

    private void 查找QQ() {

        for (int i = ((DefaultTableModel) (this.账号信息.getModel())).getRowCount() - 1; i >= 0; i--) {
            ((DefaultTableModel) (this.账号信息.getModel())).removeRow(i);
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = null;
            ResultSet rs = null;
            ps = con.prepareStatement("SELECT * FROM accounts WHERE qq =  '" + 账号操作.getText() + " ' ");
            rs = ps.executeQuery();
            while (rs.next()) {
                String 封号 = "";
                if (rs.getInt("banned") == 0) {
                    封号 = "正常";
                } else {
                    封号 = "封禁";
                }
                String 在线 = "";
                if (rs.getInt("loggedin") == 0) {
                    // Font fnA = new Font("细明本",Font.PLAIN,12);
                    在线 = "不在线";
                } else {
                    在线 = "在线";
                }
                String QQ = "";
                if (rs.getString("qq") != null) {
                    QQ = rs.getString("qq");
                } else {
                    QQ = "未绑定QQ";
                }
                ((DefaultTableModel) 账号信息.getModel()).insertRow(账号信息.getRowCount(), new Object[]{
                    rs.getInt("id"), //账号ID
                    rs.getString("name"), //账号
                    rs.getString("SessionIP"), //账号IP地址
                    rs.getString("macs"), ///账号MAC地址
                    QQ,//注册时间
                    rs.getInt("ACash"),//点券
                    rs.getInt("mPoints"),//抵用
                    rs.getString("lastlogin"),//最近上线
                    在线,
                    封号,
                    rs.getInt("gm")
                });
            }
            账号提示语言.setText("[信息]:查找账号 " + this.账号操作.getText() + " 成功。");
        } catch (SQLException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }
        账号信息.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int i = 账号信息.getSelectedRow();
                String a = 账号信息.getValueAt(i, 0).toString();
                String a1 = 账号信息.getValueAt(i, 1).toString();
                String a2 = 账号信息.getValueAt(i, 5).toString();
                String a3 = 账号信息.getValueAt(i, 6).toString();
                账号ID.setText(a);
                账号操作.setText(a1);
                账号.setText(a1);
                点券.setText(a2);
                抵用.setText(a3);
                刷新角色信息2();
            }
        });
    }

    private void 查找账号() {

        for (int i = ((DefaultTableModel) (this.账号信息.getModel())).getRowCount() - 1; i >= 0; i--) {
            ((DefaultTableModel) (this.账号信息.getModel())).removeRow(i);
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = null;
            ResultSet rs = null;
            ps = con.prepareStatement("SELECT * FROM accounts WHERE name =  '" + 账号操作.getText() + "  '");
            rs = ps.executeQuery();
            while (rs.next()) {
                String 封号 = "";
                if (rs.getInt("banned") == 0) {
                    封号 = "正常";
                } else {
                    封号 = "封禁";
                }
                String 在线 = "";
                if (rs.getInt("loggedin") == 0) {
                    // Font fnA = new Font("细明本",Font.PLAIN,12);
                    在线 = "不在线";
                } else {
                    在线 = "在线";
                }
                ((DefaultTableModel) 账号信息.getModel()).insertRow(账号信息.getRowCount(), new Object[]{
                    rs.getInt("id"), //账号ID
                    rs.getString("name"), //账号
                    rs.getString("SessionIP"), //账号IP地址
                    rs.getString("macs"), ///账号MAC地址
                    rs.getString("qq"),//注册时间
                    rs.getInt("ACash"),//点券
                    rs.getInt("mPoints"),//抵用
                    rs.getString("lastlogin"),//最近上线
                    在线,
                    封号,
                    rs.getInt("gm")
                });
            }
            账号提示语言.setText("[信息]:查找账号 " + this.账号操作.getText() + " 成功。");
        } catch (SQLException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }
        账号信息.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int i = 账号信息.getSelectedRow();
                String a = 账号信息.getValueAt(i, 0).toString();
                String a1 = 账号信息.getValueAt(i, 1).toString();
                String a2 = 账号信息.getValueAt(i, 5).toString();
                String a3 = 账号信息.getValueAt(i, 6).toString();
                账号ID.setText(a);
                账号操作.setText(a1);
                账号.setText(a1);
                点券.setText(a2);
                抵用.setText(a3);
                刷新角色信息2();
            }
        });
    }

    private void 刷新技能信息() {
        boolean result1 = this.角色ID.getText().matches("[0-9]+");
        if (result1) {
            for (int i = ((DefaultTableModel) (this.技能信息.getModel())).getRowCount() - 1; i >= 0; i--) {
                ((DefaultTableModel) (this.技能信息.getModel())).removeRow(i);
            }
            try {
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = null;

                ResultSet rs = null;
                ps = con.prepareStatement("SELECT * FROM skills  WHERE characterid =" + this.角色ID.getText() + "");
                rs = ps.executeQuery();
                while (rs.next()) {
                    MapleDataProvider data = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/String.wz"));
                    MapleData itemsData;
                    int itemId;
                    String itemName = "";
                    itemsData = data.getData("Skill.img");
                    for (MapleData itemFolder : itemsData.getChildren()) {
                        itemId = Integer.parseInt(itemFolder.getName());
                        if (rs.getInt("skillid") == itemId) {
                            itemName = MapleDataTool.getString("name", itemFolder, "NO-NAME");
                        }
                    }
                    ((DefaultTableModel) 技能信息.getModel()).insertRow(技能信息.getRowCount(), new Object[]{
                        rs.getInt("id"),
                        itemName,
                        rs.getInt("skillid"),
                        rs.getInt("skilllevel"),
                        rs.getInt("masterlevel")
                    });
                }
            } catch (SQLException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }
            技能信息.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    int i = 技能信息.getSelectedRow();
                    String a = 技能信息.getValueAt(i, 0).toString();
                    // String a1 = 技能信息.getValueAt(i, 1).toString();
                    String a2 = 技能信息.getValueAt(i, 2).toString();
                    String a3 = 技能信息.getValueAt(i, 3).toString();
                    String a4 = 技能信息.getValueAt(i, 4).toString();
                    技能序号.setText(a);
                    // 技能名字.setText(a1);
                    技能代码.setText(a2);
                    技能目前等级.setText(a3);
                    技能最高等级.setText(a4);
                    //出售状态.setText(a8);
                    //jTextField9.setText(a9);
                }
            });
        } else {
            JOptionPane.showMessageDialog(null, "[信息]:请先点击你想查看的角色。");
        }
    }

    private void 刷新角色信息() {
        String 输出 = "";
        for (int i = ((DefaultTableModel) (this.角色信息.getModel())).getRowCount() - 1; i >= 0; i--) {
            ((DefaultTableModel) (this.角色信息.getModel())).removeRow(i);
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = null;

            ResultSet rs = null;
            ps = con.prepareStatement("SELECT * FROM characters order by id desc");
            rs = ps.executeQuery();

            while (rs.next()) {
                String 在线 = "";
                if (World.Find.findChannel(rs.getString("name")) > 0) {
                    在线 = "在线";
                } else {
                    在线 = "离线";
                }
                ((DefaultTableModel) 角色信息.getModel()).insertRow(角色信息.getRowCount(), new Object[]{
                    rs.getInt("id"),
                    rs.getInt("accountid"),
                    rs.getString("name"),
                    getJobNameById(rs.getInt("job")),
                    rs.getInt("level"),
                    rs.getInt("str"),
                    rs.getInt("dex"),
                    rs.getInt("luk"),
                    rs.getInt("int"),
                    rs.getInt("maxhp"),
                    rs.getInt("maxmp"),
                    rs.getInt("meso"),
                    rs.getInt("map"),
                    在线,
                    rs.getInt("gm"),
                    rs.getInt("hair"),
                    rs.getInt("face")
                });

            }
        } catch (SQLException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }
        角色信息.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int i = 角色信息.getSelectedRow();
                String a = 角色信息.getValueAt(i, 0).toString();
                String a1 = 角色信息.getValueAt(i, 2).toString();
                String a2 = 角色信息.getValueAt(i, 4).toString();
                String a3 = 角色信息.getValueAt(i, 5).toString();
                String a4 = 角色信息.getValueAt(i, 6).toString();
                String a5 = 角色信息.getValueAt(i, 7).toString();
                String a6 = 角色信息.getValueAt(i, 8).toString();
                String a7 = 角色信息.getValueAt(i, 9).toString();
                String a8 = 角色信息.getValueAt(i, 10).toString();
                String a9 = 角色信息.getValueAt(i, 11).toString();
                String a10 = 角色信息.getValueAt(i, 12).toString();
                String a11 = 角色信息.getValueAt(i, 14).toString();
                String a12 = 角色信息.getValueAt(i, 15).toString();
                String a13 = 角色信息.getValueAt(i, 16).toString();
                角色ID.setText(a);
                角色昵称.setText(a1);
                等级.setText(a2);
                力量.setText(a3);
                敏捷.setText(a4);
                智力.setText(a5);
                运气.setText(a6);
                HP.setText(a7);
                MP.setText(a8);
                金币1.setText(a9);
                地图.setText(a10);
                GM.setText(a11);
                发型.setText(a12);
                脸型.setText(a13);
                //  个人发送物品玩家名字.setText(a1);
                //  发送装备玩家姓名.setText(a1);
            }
        });
    }

    private void 刷新角色背包穿戴() {
        for (int i = ((DefaultTableModel) (this.角色背包穿戴.getModel())).getRowCount() - 1; i >= 0; i--) {
            ((DefaultTableModel) (this.角色背包穿戴.getModel())).removeRow(i);
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = null;

            ResultSet rs = null;
            ps = con.prepareStatement("SELECT * FROM inventoryitems WHERE characterid =" + this.角色ID.getText() + " && inventorytype = -1");
            rs = ps.executeQuery();
            while (rs.next()) {

                ((DefaultTableModel) 角色背包穿戴.getModel()).insertRow(角色背包穿戴.getRowCount(), new Object[]{
                    rs.getInt("inventoryitemid"),
                    rs.getInt("itemid"),
                    MapleItemInformationProvider.getInstance().getName(rs.getInt("itemid")),
                    rs.getInt("quantity")});
            }
        } catch (SQLException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }
        角色背包穿戴.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int i = 角色背包穿戴.getSelectedRow();
                String a = 角色背包穿戴.getValueAt(i, 0).toString();
                String a1 = 角色背包穿戴.getValueAt(i, 1).toString();
                String a2 = 角色背包穿戴.getValueAt(i, 2).toString();
                身上穿戴序号1.setText(a);
                背包物品代码1.setText(a1);
                背包物品名字1.setText(a2);
            }
        });
    }

    private void 刷新角色装备背包() {
        for (int i = ((DefaultTableModel) (this.角色装备背包.getModel())).getRowCount() - 1; i >= 0; i--) {
            ((DefaultTableModel) (this.角色装备背包.getModel())).removeRow(i);
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = null;

            ResultSet rs = null;
            ps = con.prepareStatement("SELECT * FROM inventoryitems WHERE characterid =" + this.角色ID.getText() + " && inventorytype = 1");
            rs = ps.executeQuery();
            while (rs.next()) {

                ((DefaultTableModel) 角色装备背包.getModel()).insertRow(角色装备背包.getRowCount(), new Object[]{
                    rs.getInt("inventoryitemid"),
                    rs.getInt("itemid"),
                    MapleItemInformationProvider.getInstance().getName(rs.getInt("itemid")),
                    rs.getInt("quantity")});
            }
        } catch (SQLException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }
        角色装备背包.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int i = 角色装备背包.getSelectedRow();
                String a = 角色装备背包.getValueAt(i, 0).toString();
                String a1 = 角色装备背包.getValueAt(i, 1).toString();
                String a2 = 角色装备背包.getValueAt(i, 2).toString();
                装备背包物品序号.setText(a);
                装备背包物品代码.setText(a1);
                装备背包物品名字.setText(a2);
            }
        });
    }

    private void 刷新角色消耗背包() {
        for (int i = ((DefaultTableModel) (this.角色消耗背包.getModel())).getRowCount() - 1; i >= 0; i--) {
            ((DefaultTableModel) (this.角色消耗背包.getModel())).removeRow(i);
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = null;

            ResultSet rs = null;
            ps = con.prepareStatement("SELECT * FROM inventoryitems WHERE characterid =" + this.角色ID.getText() + " && inventorytype = 2");
            rs = ps.executeQuery();
            while (rs.next()) {

                ((DefaultTableModel) 角色消耗背包.getModel()).insertRow(角色消耗背包.getRowCount(), new Object[]{
                    rs.getInt("inventoryitemid"),
                    rs.getInt("itemid"),
                    MapleItemInformationProvider.getInstance().getName(rs.getInt("itemid")),
                    rs.getInt("quantity")});
            }
        } catch (SQLException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }
        角色消耗背包.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int i = 角色消耗背包.getSelectedRow();
                String a = 角色消耗背包.getValueAt(i, 0).toString();
                String a1 = 角色消耗背包.getValueAt(i, 1).toString();
                //String a2 = 角色消耗背包.getValueAt(i, 2).toString();
                消耗背包物品序号.setText(a);
                消耗背包物品代码.setText(a1);
                //消耗背包物品名字.setText(a2);
            }
        });
    }

    private void 刷新角色特殊背包() {
        for (int i = ((DefaultTableModel) (this.角色特殊背包.getModel())).getRowCount() - 1; i >= 0; i--) {
            ((DefaultTableModel) (this.角色特殊背包.getModel())).removeRow(i);
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = null;

            ResultSet rs = null;
            ps = con.prepareStatement("SELECT * FROM inventoryitems WHERE characterid =" + this.角色ID.getText() + " && inventorytype = 5");
            rs = ps.executeQuery();
            while (rs.next()) {
                ((DefaultTableModel) 角色特殊背包.getModel()).insertRow(角色特殊背包.getRowCount(), new Object[]{
                    rs.getInt("inventoryitemid"),
                    rs.getInt("itemid"),
                    MapleItemInformationProvider.getInstance().getName(rs.getInt("itemid")),
                    rs.getInt("quantity")});
            }
        } catch (SQLException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }
        角色特殊背包.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int i = 角色特殊背包.getSelectedRow();
                String a = 角色特殊背包.getValueAt(i, 0).toString();
                String a1 = 角色特殊背包.getValueAt(i, 1).toString();
                //String a2 = 角色特殊背包.getValueAt(i, 2).toString();
                特殊背包物品序号.setText(a);
                特殊背包物品代码.setText(a1);
                //特殊背包物品名字.setText(a2);
            }
        });
    }

    private void 刷新角色游戏仓库() {
        for (int i = ((DefaultTableModel) (this.角色游戏仓库.getModel())).getRowCount() - 1; i >= 0; i--) {
            ((DefaultTableModel) (this.角色游戏仓库.getModel())).removeRow(i);
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = null;

            ResultSet rs = null;
            ps = con.prepareStatement("SELECT * FROM inventoryitems WHERE accountid =" + this.账号ID.getText());
            rs = ps.executeQuery();
            while (rs.next()) {
                ((DefaultTableModel) 角色游戏仓库.getModel()).insertRow(角色游戏仓库.getRowCount(), new Object[]{
                    rs.getInt("inventoryitemid"),
                    rs.getInt("itemid"),
                    MapleItemInformationProvider.getInstance().getName(rs.getInt("itemid")),
                    rs.getInt("quantity")});
            }
        } catch (SQLException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }
        角色游戏仓库.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int i = 角色游戏仓库.getSelectedRow();
                String a = 角色游戏仓库.getValueAt(i, 0).toString();
                String a1 = 角色游戏仓库.getValueAt(i, 1).toString();
                //String a2 = 角色游戏仓库.getValueAt(i, 2).toString();
                游戏仓库物品序号.setText(a);
                游戏仓库物品代码.setText(a1);
                //游戏仓库物品名字.setText(a2);
            }
        });
    }

    private void 刷新角色商城仓库() {
        for (int i = ((DefaultTableModel) (this.角色商城仓库.getModel())).getRowCount() - 1; i >= 0; i--) {
            ((DefaultTableModel) (this.角色商城仓库.getModel())).removeRow(i);
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = null;

            ResultSet rs = null;
            ps = con.prepareStatement("SELECT * FROM csitems WHERE accountid =" + this.账号ID.getText());
            rs = ps.executeQuery();
            while (rs.next()) {
                ((DefaultTableModel) 角色商城仓库.getModel()).insertRow(角色商城仓库.getRowCount(), new Object[]{
                    rs.getInt("inventoryitemid"),
                    rs.getInt("itemid"),
                    MapleItemInformationProvider.getInstance().getName(rs.getInt("itemid")),
                    rs.getInt("quantity")});
            }
        } catch (SQLException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }
        角色商城仓库.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int i = 角色商城仓库.getSelectedRow();
                String a = 角色商城仓库.getValueAt(i, 0).toString();
                String a1 = 角色商城仓库.getValueAt(i, 1).toString();
                //String a2 = 角色商城仓库.getValueAt(i, 2).toString();
                商城仓库物品序号.setText(a);
                商城仓库物品代码.setText(a1);
                //商城仓库物品名字.setText(a2);
            }
        });
    }

    private void 刷新角色其他背包() {
        for (int i = ((DefaultTableModel) (this.角色其他背包.getModel())).getRowCount() - 1; i >= 0; i--) {
            ((DefaultTableModel) (this.角色其他背包.getModel())).removeRow(i);
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = null;

            ResultSet rs = null;
            ps = con.prepareStatement("SELECT * FROM inventoryitems WHERE characterid =" + this.角色ID.getText() + " && inventorytype = 4");
            rs = ps.executeQuery();
            while (rs.next()) {
                ((DefaultTableModel) 角色其他背包.getModel()).insertRow(角色其他背包.getRowCount(), new Object[]{
                    rs.getInt("inventoryitemid"),
                    rs.getInt("itemid"),
                    MapleItemInformationProvider.getInstance().getName(rs.getInt("itemid")),
                    rs.getInt("quantity")});
            }
        } catch (SQLException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }
        角色其他背包.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int i = 角色其他背包.getSelectedRow();
                String a = 角色其他背包.getValueAt(i, 0).toString();
                String a1 = 角色其他背包.getValueAt(i, 1).toString();
                //String a2 = 角色其他背包.getValueAt(i, 2).toString();
                其他背包物品序号.setText(a);
                其他背包物品代码.setText(a1);
                //其他背包物品名字.setText(a2);
            }
        });
    }

    private void 刷新角色设置背包() {
        for (int i = ((DefaultTableModel) (this.角色设置背包.getModel())).getRowCount() - 1; i >= 0; i--) {
            ((DefaultTableModel) (this.角色设置背包.getModel())).removeRow(i);
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = null;

            ResultSet rs = null;
            ps = con.prepareStatement("SELECT * FROM inventoryitems WHERE characterid =" + this.角色ID.getText() + " && inventorytype = 3");
            rs = ps.executeQuery();
            while (rs.next()) {
                ((DefaultTableModel) 角色设置背包.getModel()).insertRow(角色设置背包.getRowCount(), new Object[]{
                    rs.getInt("inventoryitemid"),
                    rs.getInt("itemid"),
                    MapleItemInformationProvider.getInstance().getName(rs.getInt("itemid")),
                    rs.getInt("quantity")});
            }
        } catch (SQLException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }
        角色设置背包.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int i = 角色设置背包.getSelectedRow();
                String a = 角色设置背包.getValueAt(i, 0).toString();
                String a1 = 角色设置背包.getValueAt(i, 1).toString();
                String a2 = 角色设置背包.getValueAt(i, 2).toString();
                设置背包物品序号.setText(a);
                设置背包物品代码.setText(a1);
                设置背包物品名字.setText(a2);
            }
        });
    }

    private void 刷新角色信息2() {
        for (int i = ((DefaultTableModel) (this.角色信息.getModel())).getRowCount() - 1; i >= 0; i--) {
            ((DefaultTableModel) (this.角色信息.getModel())).removeRow(i);
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = null;

            ResultSet rs = null;
            ps = con.prepareStatement("SELECT * FROM characters WHERE accountid =" + this.账号ID.getText() + "");
            rs = ps.executeQuery();

            while (rs.next()) {
                String 在线 = "";
                if (World.Find.findChannel(rs.getString("name")) > 0) {
                    在线 = "在线";
                } else {
                    在线 = "离线";
                }
                ((DefaultTableModel) 角色信息.getModel()).insertRow(角色信息.getRowCount(), new Object[]{
                    rs.getInt("id"),
                    rs.getInt("accountid"),
                    rs.getString("name"),
                    getJobNameById(rs.getInt("job")),
                    rs.getInt("level"),
                    rs.getInt("str"),
                    rs.getInt("dex"),
                    rs.getInt("luk"),
                    rs.getInt("int"),
                    rs.getInt("maxhp"),
                    rs.getInt("maxmp"),
                    rs.getInt("meso"),
                    rs.getInt("map"),
                    在线,
                    rs.getInt("gm"),
                    rs.getInt("hair"),
                    rs.getInt("face")});
            }
        } catch (SQLException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }
        角色信息.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int i = 角色信息.getSelectedRow();
                String a = 角色信息.getValueAt(i, 0).toString();
                String a1 = 角色信息.getValueAt(i, 2).toString();
                String a2 = 角色信息.getValueAt(i, 4).toString();
                String a3 = 角色信息.getValueAt(i, 5).toString();
                String a4 = 角色信息.getValueAt(i, 6).toString();
                String a5 = 角色信息.getValueAt(i, 7).toString();
                String a6 = 角色信息.getValueAt(i, 8).toString();
                String a7 = 角色信息.getValueAt(i, 9).toString();
                String a8 = 角色信息.getValueAt(i, 10).toString();
                String a9 = 角色信息.getValueAt(i, 11).toString();
                String a10 = 角色信息.getValueAt(i, 12).toString();
                String a11 = 角色信息.getValueAt(i, 14).toString();
                String a12 = 角色信息.getValueAt(i, 15).toString();
                String a13 = 角色信息.getValueAt(i, 16).toString();
                角色ID.setText(a);
                角色昵称.setText(a1);
                等级.setText(a2);
                力量.setText(a3);
                敏捷.setText(a4);
                智力.setText(a5);
                运气.setText(a6);
                HP.setText(a7);
                MP.setText(a8);
                金币1.setText(a9);
                地图.setText(a10);
                GM.setText(a11);
                发型.setText(a12);
                脸型.setText(a13);
                //出售状态.setText(a8);
                //jTextField9.setText(a9);
            }
        });
    }
    
    
    private void sendNotice(int a) {
        try {
            String str = noticeText.getText();
            String 输出 = "";
            for (ChannelServer cserv1 : ChannelServer.getAllInstances()) {
                for (MapleCharacter mch : cserv1.getPlayerStorage().getAllCharacters()) {
                    switch (a) {
                        case 0:
                            //顶端公告
                            World.Broadcast.broadcastMessage(MaplePacketCreator.getItemNotice(str.toString()));
                            break;
                        case 1:
                            //顶端公告
                            World.Broadcast.broadcastMessage(MaplePacketCreator.serverMessage(str.toString()));
                            break;
                        case 2:
                            //弹窗公告
                            World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(1, str));
                            break;
                        case 3:
                            //聊天蓝色公告
                            World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(0, str));
                            break;
                        case 4:
                            mch.startMapEffect(str, Integer.parseInt(公告发布喇叭代码.getText()));
                            break;
                        default:
                            break;
                    }
                }
                公告发布喇叭代码.setText("5120027");
            }
        } catch (Exception e) {
        }
    }
    /**
     * @param args the command line arguments
     */
    private static long starttime = 0;

    public static void main(String args[]) throws InterruptedException {
      
        starttime = System.currentTimeMillis();
            EventQueue.invokeLater(new Runnable() {
            public void run() {
        		try {
			org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
			BeautyEyeLNFHelper.frameBorderStyle = BeautyEyeLNFHelper.FrameBorderStyle.translucencySmallShadow;
			org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
			UIManager.put("RootPane.setupButtonVisible", false);
			//BeautyEyeLNFHelper.translucencyAtFrameInactive = true;
              UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
                    e.printStackTrace();
                }
                instance = new ZeroMS_UI();
                instance.setVisible(true);
            }
        });
    }

    public class newOutputStream extends OutputStream {

        @Override
        public void write(int arg) throws IOException {
            // 忽略
        }

        @Override
        public void write(byte data[]) throws IOException {
            consoleInfo.append(new String(data));
        }

        @Override
        public void write(byte data[], int off, int len) throws IOException {
            if (buffline >= 5000) {
                consoleInfo.setText("");
                buffline = 0;
            }
            consoleInfo.append(new String(data, off, len));
            consoleInfo.setCaretPosition(consoleInfo.getText().length());
            buffline++;
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel ActiveThread;
    private javax.swing.JTextField ExpBound1;
    private javax.swing.JTextField ExpBound2;
    private javax.swing.JTextField ExpBound3;
    private javax.swing.JTextField ExpBound4;
    private javax.swing.JTextField ExpBound5;
    private javax.swing.JTextField ExpBound6;
    private javax.swing.JTextField GM;
    private javax.swing.JTextField HP;
    private javax.swing.JButton IP多开开关;
    private javax.swing.JTextField MP;
    private javax.swing.JTextField QQ;
    private javax.swing.JLabel RunStats;
    private javax.swing.JLabel RunTime;
    private javax.swing.JTextField a1;
    private javax.swing.JTextField a2;
    private javax.swing.JTextArea consoleInfo;
    private javax.swing.JCheckBox debug模式;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton22;
    private javax.swing.JButton jButton23;
    private javax.swing.JButton jButton25;
    private javax.swing.JButton jButton28;
    private javax.swing.JButton jButton29;
    private javax.swing.JButton jButton30;
    private javax.swing.JButton jButton31;
    private javax.swing.JButton jButton32;
    private javax.swing.JButton jButton33;
    private javax.swing.JButton jButton34;
    private javax.swing.JButton jButton35;
    private javax.swing.JButton jButton36;
    private javax.swing.JButton jButton37;
    private javax.swing.JButton jButton38;
    private javax.swing.JButton jButton39;
    private javax.swing.JButton jButton40;
    private javax.swing.JButton jButton41;
    private javax.swing.JButton jButton42;
    private javax.swing.JButton jButton43;
    private javax.swing.JButton jButton44;
    private javax.swing.JButton jButton45;
    private javax.swing.JButton jButton46;
    private javax.swing.JButton jButton47;
    private javax.swing.JButton jButton48;
    private javax.swing.JButton jButton49;
    private javax.swing.JButton jButton50;
    private javax.swing.JButton jButton51;
    private javax.swing.JButton jButton52;
    private javax.swing.JButton jButton53;
    private javax.swing.JButton jButton69;
    private javax.swing.JButton jButton70;
    private javax.swing.JButton jButton72;
    private javax.swing.JButton jButton73;
    private javax.swing.JButton jButton74;
    private javax.swing.JButton jButton75;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel106;
    private javax.swing.JLabel jLabel107;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel111;
    private javax.swing.JLabel jLabel117;
    private javax.swing.JLabel jLabel118;
    private javax.swing.JLabel jLabel119;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel131;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel182;
    private javax.swing.JLabel jLabel183;
    private javax.swing.JLabel jLabel184;
    private javax.swing.JLabel jLabel185;
    private javax.swing.JLabel jLabel186;
    private javax.swing.JLabel jLabel187;
    private javax.swing.JLabel jLabel188;
    private javax.swing.JLabel jLabel189;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel190;
    private javax.swing.JLabel jLabel191;
    private javax.swing.JLabel jLabel192;
    private javax.swing.JLabel jLabel193;
    private javax.swing.JLabel jLabel194;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel201;
    private javax.swing.JLabel jLabel203;
    private javax.swing.JLabel jLabel204;
    private javax.swing.JLabel jLabel205;
    private javax.swing.JLabel jLabel206;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel214;
    private javax.swing.JLabel jLabel217;
    private javax.swing.JLabel jLabel219;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel220;
    private javax.swing.JLabel jLabel221;
    private javax.swing.JLabel jLabel222;
    private javax.swing.JLabel jLabel223;
    private javax.swing.JLabel jLabel224;
    private javax.swing.JLabel jLabel225;
    private javax.swing.JLabel jLabel226;
    private javax.swing.JLabel jLabel227;
    private javax.swing.JLabel jLabel228;
    private javax.swing.JLabel jLabel229;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel230;
    private javax.swing.JLabel jLabel231;
    private javax.swing.JLabel jLabel232;
    private javax.swing.JLabel jLabel233;
    private javax.swing.JLabel jLabel234;
    private javax.swing.JLabel jLabel235;
    private javax.swing.JLabel jLabel236;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel240;
    private javax.swing.JLabel jLabel241;
    private javax.swing.JLabel jLabel242;
    private javax.swing.JLabel jLabel244;
    private javax.swing.JLabel jLabel246;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel259;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel276;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel283;
    private javax.swing.JLabel jLabel287;
    private javax.swing.JLabel jLabel288;
    private javax.swing.JLabel jLabel289;
    private javax.swing.JLabel jLabel290;
    private javax.swing.JLabel jLabel291;
    private javax.swing.JLabel jLabel292;
    private javax.swing.JLabel jLabel293;
    private javax.swing.JLabel jLabel294;
    private javax.swing.JLabel jLabel295;
    private javax.swing.JLabel jLabel296;
    private javax.swing.JLabel jLabel297;
    private javax.swing.JLabel jLabel298;
    private javax.swing.JLabel jLabel299;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel300;
    private javax.swing.JLabel jLabel301;
    private javax.swing.JLabel jLabel302;
    private javax.swing.JLabel jLabel303;
    private javax.swing.JLabel jLabel304;
    private javax.swing.JLabel jLabel305;
    private javax.swing.JLabel jLabel306;
    private javax.swing.JLabel jLabel307;
    private javax.swing.JLabel jLabel308;
    private javax.swing.JLabel jLabel309;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel310;
    private javax.swing.JLabel jLabel311;
    private javax.swing.JLabel jLabel312;
    private javax.swing.JLabel jLabel322;
    private javax.swing.JLabel jLabel323;
    private javax.swing.JLabel jLabel324;
    private javax.swing.JLabel jLabel327;
    private javax.swing.JLabel jLabel328;
    private javax.swing.JLabel jLabel329;
    private javax.swing.JLabel jLabel330;
    private javax.swing.JLabel jLabel331;
    private javax.swing.JLabel jLabel332;
    private javax.swing.JLabel jLabel348;
    private javax.swing.JLabel jLabel349;
    private javax.swing.JLabel jLabel353;
    private javax.swing.JLabel jLabel354;
    private javax.swing.JLabel jLabel355;
    private javax.swing.JLabel jLabel356;
    private javax.swing.JLabel jLabel357;
    private javax.swing.JLabel jLabel359;
    private javax.swing.JLabel jLabel360;
    private javax.swing.JLabel jLabel361;
    private javax.swing.JLabel jLabel362;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel87;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel89;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel28;
    private javax.swing.JPanel jPanel29;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel30;
    private javax.swing.JPanel jPanel31;
    private javax.swing.JPanel jPanel32;
    private javax.swing.JPanel jPanel35;
    private javax.swing.JPanel jPanel36;
    private javax.swing.JPanel jPanel38;
    private javax.swing.JPanel jPanel39;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel40;
    private javax.swing.JPanel jPanel41;
    private javax.swing.JPanel jPanel42;
    private javax.swing.JPanel jPanel43;
    private javax.swing.JPanel jPanel44;
    private javax.swing.JPanel jPanel45;
    private javax.swing.JPanel jPanel46;
    private javax.swing.JPanel jPanel47;
    private javax.swing.JPanel jPanel48;
    private javax.swing.JPanel jPanel49;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel50;
    private javax.swing.JPanel jPanel51;
    private javax.swing.JPanel jPanel52;
    private javax.swing.JPanel jPanel53;
    private javax.swing.JPanel jPanel54;
    private javax.swing.JPanel jPanel55;
    private javax.swing.JPanel jPanel56;
    private javax.swing.JPanel jPanel57;
    private javax.swing.JPanel jPanel58;
    private javax.swing.JPanel jPanel59;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel60;
    private javax.swing.JPanel jPanel66;
    private javax.swing.JPanel jPanel67;
    private javax.swing.JPanel jPanel68;
    private javax.swing.JPanel jPanel69;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel70;
    private javax.swing.JPanel jPanel72;
    private javax.swing.JPanel jPanel74;
    private javax.swing.JPanel jPanel75;
    private javax.swing.JPanel jPanel76;
    private javax.swing.JPanel jPanel77;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel80;
    private javax.swing.JPanel jPanel83;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanel93;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane134;
    private javax.swing.JScrollPane jScrollPane135;
    private javax.swing.JScrollPane jScrollPane136;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JScrollPane jScrollPane15;
    private javax.swing.JScrollPane jScrollPane16;
    private javax.swing.JScrollPane jScrollPane17;
    private javax.swing.JScrollPane jScrollPane18;
    private javax.swing.JScrollPane jScrollPane19;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane20;
    private javax.swing.JScrollPane jScrollPane21;
    private javax.swing.JScrollPane jScrollPane22;
    private javax.swing.JScrollPane jScrollPane23;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane30;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTabbedPane jTabbedPane;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTabbedPane jTabbedPane5;
    private javax.swing.JTabbedPane jTabbedPane6;
    private javax.swing.JTabbedPane jTabbedPane8;
    private javax.swing.JTextField jTextField22;
    private javax.swing.JTextField noticeText;
    public static javax.swing.JTextPane output_err_jTextPane;
    public static javax.swing.JTextPane output_notice_jTextPane;
    public static javax.swing.JTextPane output_out_jTextPane;
    public static javax.swing.JTextPane output_packet_jTextPane;
    private javax.swing.JButton qiannengdaima;
    private javax.swing.JButton sendMsgNotice;
    private javax.swing.JButton sendNotice;
    private javax.swing.JButton sendNpcTalkNotice;
    private javax.swing.JButton sendWinNotice;
    private javax.swing.JTextField wz路径;
    private javax.swing.JButton z1;
    private javax.swing.JButton z10;
    private javax.swing.JButton z11;
    private javax.swing.JButton z12;
    private javax.swing.JButton z2;
    private javax.swing.JButton z3;
    private javax.swing.JButton z4;
    private javax.swing.JButton z5;
    private javax.swing.JButton z6;
    private javax.swing.JButton z7;
    private javax.swing.JButton z8;
    private javax.swing.JButton z9;
    private javax.swing.JTextField 三倍爆率持续时间;
    private javax.swing.JTextField 三倍经验持续时间;
    private javax.swing.JTextField 三倍金币持续时间;
    private javax.swing.JButton 上线提醒开关;
    private javax.swing.JButton 丢出物品开关;
    private javax.swing.JButton 丢出金币开关;
    private javax.swing.JCheckBox 丢物品信息;
    private javax.swing.JTextField 个人发送物品代码;
    private javax.swing.JTextField 个人发送物品数量;
    private javax.swing.JTextField 个人发送物品玩家名字;
    private javax.swing.JTextField 个人发送物品玩家名字1;
    private javax.swing.JTextField 事件名字;
    private javax.swing.JTextField 人物灌水比例;
    private javax.swing.JLabel 人物灌水比例1;
    private javax.swing.JCheckBox 伤害输出记录;
    private javax.swing.JButton 保存数据按钮;
    private javax.swing.JButton 保存雇佣按钮;
    private javax.swing.JButton 修改技能;
    private javax.swing.JButton 修改技能1;
    private javax.swing.JButton 修改账号点券抵用;
    private javax.swing.JCheckBox 允许使用商城;
    private javax.swing.JCheckBox 允许玩家穿戴GM道具;
    private javax.swing.JButton 全屏检测开关;
    private javax.swing.JTextField 全服发送物品代码;
    private javax.swing.JTextField 全服发送物品数量;
    private javax.swing.JTextField 全服发送装备物品ID;
    private javax.swing.JTextField 全服发送装备装备HP;
    private javax.swing.JTextField 全服发送装备装备MP;
    private javax.swing.JTextField 全服发送装备装备制作人;
    private javax.swing.JTextField 全服发送装备装备力量;
    private javax.swing.JTextField 全服发送装备装备加卷;
    private javax.swing.JTextField 全服发送装备装备可否交易;
    private javax.swing.JTextField 全服发送装备装备攻击力;
    private javax.swing.JTextField 全服发送装备装备敏捷;
    private javax.swing.JTextField 全服发送装备装备智力;
    private javax.swing.JTextField 全服发送装备装备潜能1;
    private javax.swing.JTextField 全服发送装备装备潜能2;
    private javax.swing.JTextField 全服发送装备装备潜能3;
    private javax.swing.JTextField 全服发送装备装备物理防御;
    private javax.swing.JTextField 全服发送装备装备给予时间;
    private javax.swing.JTextField 全服发送装备装备运气;
    private javax.swing.JTextField 全服发送装备装备魔法力;
    private javax.swing.JTextField 全服发送装备装备魔法防御;
    private javax.swing.JTextField 公告发布喇叭代码;
    private javax.swing.JTextField 其他背包物品代码;
    private javax.swing.JTextField 其他背包物品名字;
    private javax.swing.JTextField 其他背包物品序号;
    private javax.swing.JButton 删除其他背包;
    private javax.swing.JButton 删除商城仓库;
    private javax.swing.JButton 删除技能;
    private javax.swing.JButton 删除拍卖行;
    private javax.swing.JButton 删除拍卖行1;
    private javax.swing.JButton 删除消耗背包;
    private javax.swing.JButton 删除游戏仓库;
    private javax.swing.JButton 删除特殊背包;
    private javax.swing.JButton 删除穿戴装备;
    private javax.swing.JButton 删除装备背包;
    private javax.swing.JButton 删除角色;
    private javax.swing.JButton 删除设置背包;
    private javax.swing.JButton 删除账号;
    private javax.swing.JCheckBox 刷怪开关1;
    private javax.swing.JButton 刷新角色信息;
    private javax.swing.JButton 刷新账号信息;
    private javax.swing.JTextField 力量;
    private javax.swing.JTextField 加载事件;
    private javax.swing.JButton 加速检测开关;
    private javax.swing.JCheckBox 匿名广播;
    private javax.swing.JButton 卡号自救1;
    private javax.swing.JButton 卡号自救2;
    private javax.swing.JButton 卡家族解救;
    private javax.swing.JCheckBox 卷轴防爆1;
    private javax.swing.JTextField 双倍爆率持续时间;
    private javax.swing.JTextField 双倍经验持续时间;
    private javax.swing.JTextField 双倍金币持续时间;
    private javax.swing.JTextField 发型;
    private javax.swing.JTextField 发送装备玩家姓名;
    private javax.swing.JButton 吸怪检测开关;
    private javax.swing.JTextField 商城仓库物品代码;
    private javax.swing.JTextField 商城仓库物品名字;
    private javax.swing.JTextField 商城仓库物品序号;
    private javax.swing.JTextField 商城端口;
    private javax.swing.JCheckBox 商城购买记录;
    private javax.swing.JButton 喷火龙开关;
    private javax.swing.JButton 回收地图开关;
    private javax.swing.JTable 在线泡点设置;
    private javax.swing.JTable 在线泡点设置1;
    private javax.swing.JButton 在线角色;
    private javax.swing.JButton 在线账号;
    private javax.swing.JTextField 地图;
    private javax.swing.JTextArea 地图列表ID;
    private javax.swing.JButton 地图名称开关;
    private javax.swing.JButton 大海龟开关;
    private javax.swing.JButton 大灰狼开关;
    private javax.swing.JCheckBox 学院系统;
    private javax.swing.JCheckBox 定时测谎;
    private javax.swing.JTextField 家族徽章费用;
    private javax.swing.JTextField 家族金币;
    private javax.swing.JButton 封锁账号;
    private javax.swing.JButton 小白兔开关;
    private javax.swing.JButton 小青蛇开关;
    private javax.swing.JButton 已封账号;
    private javax.swing.JCheckBox 广播记录;
    private javax.swing.JButton 开启三倍爆率;
    private javax.swing.JButton 开启三倍经验;
    private javax.swing.JButton 开启三倍金币;
    private javax.swing.JButton 开启双倍爆率;
    private javax.swing.JButton 开启双倍经验;
    private javax.swing.JButton 开启双倍金币;
    private javax.swing.JTextField 开店额外经验;
    private javax.swing.JButton 怪物状态开关;
    private javax.swing.JPanel 技能;
    private javax.swing.JTextField 技能代码;
    private javax.swing.JTable 技能信息;
    private javax.swing.JTextField 技能名字;
    private javax.swing.JTextField 技能序号;
    private javax.swing.JTextField 技能最高等级;
    private javax.swing.JTextField 技能目前等级;
    private javax.swing.JTextField 抵用;
    private javax.swing.JButton 拍卖行开关;
    private javax.swing.JTextField 拍卖行物品代码;
    private javax.swing.JTextField 拍卖行物品代码1;
    private javax.swing.JTextField 拍卖行物品名字;
    private javax.swing.JTextField 拍卖行物品名字1;
    private javax.swing.JButton 挂机检测开关;
    private javax.swing.JButton 指令通知开关;
    private javax.swing.JButton 捡物检测开关;
    private javax.swing.JTextField 敏捷;
    private javax.swing.JTextField 数据库IP;
    private javax.swing.JTextField 数据库名;
    private javax.swing.JTextField 数据库密码;
    private javax.swing.JTextField 数据库用户名;
    private javax.swing.JTextField 数据库端口;
    private javax.swing.JCheckBox 无延迟检测;
    private javax.swing.JCheckBox 无限BUFF;
    private javax.swing.JCheckBox 日志模式;
    private javax.swing.JButton 星精灵开关;
    private javax.swing.JLabel 显示在线玩家;
    private javax.swing.JLabel 显示在线账号;
    private javax.swing.JButton 显示管理角色;
    private javax.swing.JTextField 智力;
    private javax.swing.JTextField 最大创建角色数;
    private javax.swing.JTextField 最大登录角色数限制;
    private javax.swing.JTextField 最大能力值;
    private javax.swing.JLabel 最大角色;
    private javax.swing.JButton 机器多开开关;
    private javax.swing.JButton 查看技能;
    private javax.swing.JButton 查看背包;
    private javax.swing.JButton 查询在线玩家人数按钮;
    private javax.swing.JCheckBox 检测外挂自动封号;
    private javax.swing.JButton 欢迎弹窗开关;
    private javax.swing.JButton 段数检测开关;
    private javax.swing.JTextField 泡点值;
    private javax.swing.JTextField 泡点值1;
    private javax.swing.JButton 泡点值修改;
    private javax.swing.JButton 泡点值修改1;
    private javax.swing.JTextField 泡点序号;
    private javax.swing.JTextField 泡点序号1;
    private javax.swing.JButton 泡点抵用开关;
    private javax.swing.JButton 泡点抵用开关1;
    private javax.swing.JButton 泡点点券开关;
    private javax.swing.JButton 泡点点券开关1;
    private javax.swing.JTextField 泡点类型;
    private javax.swing.JTextField 泡点类型1;
    private javax.swing.JButton 泡点经验开关;
    private javax.swing.JButton 泡点经验开关1;
    private javax.swing.JButton 泡点豆豆开关;
    private javax.swing.JButton 泡点豆豆开关1;
    private javax.swing.JButton 泡点金币开关;
    private javax.swing.JButton 泡点金币开关1;
    private javax.swing.JTextField 注册的密码;
    private javax.swing.JTextField 注册的账号;
    private javax.swing.JCheckBox 测试机;
    private javax.swing.JCheckBox 测谎仪;
    private javax.swing.JTextField 消耗背包物品代码;
    private javax.swing.JTextField 消耗背包物品名字;
    private javax.swing.JTextField 消耗背包物品序号;
    private javax.swing.JTextField 清理怪物时间;
    private javax.swing.JTextField 游戏IP;
    private javax.swing.JButton 游戏仓库开关;
    private javax.swing.JTextField 游戏仓库物品代码;
    private javax.swing.JTextField 游戏仓库物品名字;
    private javax.swing.JTextField 游戏仓库物品序号;
    private javax.swing.JButton 游戏升级快讯;
    private javax.swing.JTextField 游戏名字;
    private javax.swing.JButton 游戏喇叭开关;
    private javax.swing.JButton 游戏找人开关;
    private javax.swing.JButton 游戏指令开关;
    private javax.swing.JButton 滚动公告开关;
    private javax.swing.JButton 漂漂猪开关;
    private javax.swing.JTextField 潜能几率;
    private javax.swing.JButton 火野猪开关;
    private javax.swing.JTextField 点券;
    private javax.swing.JCheckBox 物品叠加开关;
    private javax.swing.JCheckBox 物品叠加开关1;
    private javax.swing.JCheckBox 物品叠加开关10;
    private javax.swing.JCheckBox 物品叠加开关2;
    private javax.swing.JCheckBox 物品叠加开关3;
    private javax.swing.JCheckBox 物品叠加开关4;
    private javax.swing.JCheckBox 物品叠加开关5;
    private javax.swing.JCheckBox 物品叠加开关6;
    private javax.swing.JCheckBox 物品叠加开关7;
    private javax.swing.JCheckBox 物品叠加开关8;
    private javax.swing.JCheckBox 物品叠加开关9;
    private javax.swing.JTextField 物品叠加数量;
    private javax.swing.JTextField 物品爆率;
    private javax.swing.JTextField 特殊背包物品代码;
    private javax.swing.JTextField 特殊背包物品名字;
    private javax.swing.JTextField 特殊背包物品序号;
    public static javax.swing.JLabel 状态信息;
    private javax.swing.JButton 玩家交易开关;
    private javax.swing.JButton 玩家聊天开关;
    private javax.swing.JTextField 登录端口;
    private javax.swing.JButton 登陆帮助开关;
    private javax.swing.JButton 白雪人开关;
    private javax.swing.JButton 石头人开关;
    private javax.swing.JCheckBox 禁止玩家使用商店;
    private javax.swing.JButton 禁止登陆开关;
    private javax.swing.JButton 离线挂机开关;
    private javax.swing.JTextField 离线泡点值;
    private javax.swing.JButton 离线泡点值修改;
    private javax.swing.JTextField 离线泡点序号;
    private javax.swing.JButton 离线泡点抵用开关;
    private javax.swing.JButton 离线泡点点券开关;
    private javax.swing.JTextField 离线泡点类型;
    private javax.swing.JButton 离线泡点经验开关;
    private javax.swing.JTable 离线泡点设置;
    private javax.swing.JButton 离线泡点豆豆开关;
    private javax.swing.JButton 离线泡点金币开关;
    private javax.swing.JButton 离线角色;
    private javax.swing.JButton 离线账号;
    private javax.swing.JButton 章鱼怪开关;
    private javax.swing.JTextField 等级;
    private javax.swing.JTextField 管理1;
    private javax.swing.JButton 管理加速开关;
    private javax.swing.JCheckBox 管理员模式;
    private javax.swing.JButton 管理隐身开关;
    private javax.swing.JCheckBox 精灵商人出售记录;
    private javax.swing.JButton 紫色猫开关;
    private javax.swing.JButton 红螃蟹开关;
    private javax.swing.JTextField 经验倍率;
    private javax.swing.JTextField 结婚额外经验;
    private javax.swing.JButton 给予物品;
    private javax.swing.JButton 给予物品1;
    private javax.swing.JButton 给予装备1;
    private javax.swing.JButton 给予装备2;
    private javax.swing.JButton 绿水灵开关;
    private javax.swing.JButton 群攻检测开关;
    private javax.swing.JCheckBox 聊天记录;
    private javax.swing.JTextField 背包物品代码1;
    private javax.swing.JTextField 背包物品名字1;
    private javax.swing.JButton 胖企鹅开关;
    private javax.swing.JButton 脚本显码开关;
    private javax.swing.JTextField 脸型;
    private javax.swing.JCheckBox 自动注册;
    private javax.swing.JTextField 自定义地图刷怪倍数1;
    private javax.swing.JButton 花蘑菇开关;
    private javax.swing.JButton 蓝蜗牛开关;
    private javax.swing.JButton 蘑菇仔开关;
    private javax.swing.JTextField 装备背包物品代码;
    private javax.swing.JTextField 装备背包物品名字;
    private javax.swing.JTextField 装备背包物品序号;
    private javax.swing.JTextField 角色ID;
    private javax.swing.JTable 角色信息;
    private javax.swing.JPanel 角色信息1;
    private javax.swing.JTable 角色其他背包;
    private javax.swing.JTable 角色商城仓库;
    private javax.swing.JCheckBox 角色封包输出记录;
    private javax.swing.JTextField 角色昵称;
    private javax.swing.JTable 角色消耗背包;
    private javax.swing.JTable 角色游戏仓库;
    private javax.swing.JTable 角色点券拍卖行;
    private javax.swing.JTextField 角色点券拍卖行序号;
    private javax.swing.JTable 角色特殊背包;
    private javax.swing.JPanel 角色背包;
    private javax.swing.JTable 角色背包穿戴;
    private javax.swing.JTable 角色装备背包;
    private javax.swing.JTable 角色设置背包;
    private javax.swing.JTable 角色金币拍卖行;
    private javax.swing.JTextField 角色金币拍卖行序号;
    private javax.swing.JButton 解卡;
    private javax.swing.JButton 解封;
    private javax.swing.JTextField 设置背包物品代码;
    private javax.swing.JTextField 设置背包物品名字;
    private javax.swing.JTextField 设置背包物品序号;
    private javax.swing.JTextField 账号;
    private javax.swing.JTextField 账号ID;
    private javax.swing.JTable 账号信息;
    private javax.swing.JLabel 账号提示语言;
    private javax.swing.JTextField 账号操作;
    private javax.swing.JButton 越级打怪开关;
    private javax.swing.JTextField 身上穿戴序号1;
    private javax.swing.JButton 过图存档开关;
    private javax.swing.JTextField 运气;
    public static javax.swing.JProgressBar 进度条;
    public static javax.swing.JProgressBar 进度条1;
    private javax.swing.JTextField 连接数;
    private javax.swing.JButton 重载任务;
    private javax.swing.JButton 重载传送门按钮;
    private javax.swing.JButton 重载副本按钮;
    private javax.swing.JButton 重载包头按钮;
    private javax.swing.JButton 重载反应堆按钮;
    private javax.swing.JButton 重载商城按钮;
    private javax.swing.JButton 重载商店按钮;
    private javax.swing.JButton 重载爆率按钮;
    private javax.swing.JTextField 金币1;
    private javax.swing.JTextField 金币爆率;
    private javax.swing.JCheckBox 锻造系统;
    private javax.swing.JButton 雇佣商人开关;
    private javax.swing.JTextField 雇佣时间;
    private javax.swing.JButton 青鳄鱼开关;
    private javax.swing.JTextField 顶部公告;
    private javax.swing.JButton 顽皮猴开关;
    private javax.swing.JTextField 频道总数;
    private javax.swing.JCheckBox 频道掉线测试;
    private javax.swing.JTextField 频道最大人数;
    private javax.swing.JTextField 频道状态;
    private javax.swing.JTextField 频道端口;
    private javax.swing.JTextField 高级魔方几率;
    // End of variables declaration//GEN-END:variables

}

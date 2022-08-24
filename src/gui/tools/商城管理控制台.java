/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.tools;

import database.DatabaseConnection;
import gui.ZeroMS_UI;
import handling.channel.ChannelServer;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import scripting.NPCConversationManager;
import server.CashItemFactory;
import server.CashItemInfo;
import server.MapleItemInformationProvider;
import server.Start;
import tools.MaplePacketCreator;

/**
 *
 * @author Administrator
 */
public class 商城管理控制台 extends javax.swing.JFrame {

    /**
     * Creates new form 锻造控制台
     */
    public 商城管理控制台() {
        ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("Image/Icon.png"));
        setIconImage(icon.getImage());
        setTitle("商城管理控制台");
        initComponents();
        刷新商城扩充价格();
    }
    public void 刷新商城扩充价格() {
        for (int i = ((DefaultTableModel) (this.商城扩充价格.getModel())).getRowCount() - 1; i >= 0; i--) {
            ((DefaultTableModel) (this.商城扩充价格.getModel())).removeRow(i);
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = null;
            ResultSet rs = null;
            ps = con.prepareStatement("SELECT * FROM configvalues WHERE id = 999 ");
            rs = ps.executeQuery();
            while (rs.next()) {
                ((DefaultTableModel) 商城扩充价格.getModel()).insertRow(商城扩充价格.getRowCount(), new Object[]{rs.getString("Val")});

            }
        } catch (SQLException ex) {
            Logger.getLogger(ZeroMS_UI.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }    
    

    public void 上架() {

        try {
            int SN_ = Integer.parseInt(String.valueOf(this.charTable.getValueAt(this.charTable.getSelectedRow(), 0)));
            //清楚table数据
            for (int i = ((DefaultTableModel) (this.charTable.getModel())).getRowCount() - 1; i >= 0; i--) {
                ((DefaultTableModel) (this.charTable.getModel())).removeRow(i);
            }
            int OnSale_ = 1;
            CashItemInfo merchandise = new CashItemInfo(SN_, OnSale_);
            int success = update上下架(merchandise);
            if (success == 0) {
                JOptionPane.showMessageDialog(null, "[信息]:上架失败。");
            } else {
                initCharacterPannel();
                JOptionPane.showMessageDialog(null, "[信息]:上架成功。");
            }
        } catch (NumberFormatException e) {
            System.err.println(e);
            JOptionPane.showMessageDialog(null, "[信息]:上架失败，请选中你要上架的道具。");
        }
    }

    public void 下架() {
        try {
            int SN_ = Integer.parseInt(String.valueOf(this.charTable.getValueAt(this.charTable.getSelectedRow(), 0)));
            //清楚table数据
            for (int i = ((DefaultTableModel) (this.charTable.getModel())).getRowCount() - 1; i >= 0; i--) {
                ((DefaultTableModel) (this.charTable.getModel())).removeRow(i);
            }
            int OnSale_ = 0;
            CashItemInfo merchandise = new CashItemInfo(SN_, OnSale_);
            int success = update上下架(merchandise);
            if (success == 0) {
                JOptionPane.showMessageDialog(null, "[信息]:下架失败。");
            } else {
                initCharacterPannel();
                JOptionPane.showMessageDialog(null, "[信息]:下架成功。");
            }
        } catch (NumberFormatException e) {
            System.err.println(e);
            JOptionPane.showMessageDialog(null, "[信息]:下架失败，请选中你要上架的道具。");
        }
    }

    public static int update上下架(CashItemInfo merchandise) {//修改
        PreparedStatement ps = null;
        int resulet = 0;
        Connection conn = DatabaseConnection.getConnection();
        int i = 0;
        try {
            ps = conn.prepareStatement("update cashshop_modified_items set showup = ? where serial = ?");//itemid
            ps.setInt(++i, merchandise.getOnSale());
            ps.setInt(++i, merchandise.getSN());
            resulet = ps.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(Start.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return resulet;
    }

    public void 读取商品(final int a, int b, int c, int d) {
        for (int i = ((DefaultTableModel) (this.charTable.getModel())).getRowCount() - 1; i >= 0; i--) {
            ((DefaultTableModel) (this.charTable.getModel())).removeRow(i);
        }
        商品编码.setText("" + a + "");
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = null;
            PreparedStatement pse;
            ResultSet rs = null;
            ps = con.prepareStatement("SELECT * FROM cashshop_modified_items WHERE serial >= " + a + " && serial < " + b + "");
            rs = ps.executeQuery();
            while (rs.next()) {
                String 上架状态 = "";
                if (rs.getInt("showup") == 0) {
                    上架状态 = "已经下架↓";
                } else {
                    上架状态 = "已经上架↑";
                }
                String 出售状态2 = "";
                switch (rs.getInt("mark")) {
                    case -1:
                        出售状态2 = "无";
                        break;
                    case 0:
                        出售状态2 = "NEW";
                        break;
                    case 1:
                        出售状态2 = "Sale";
                        break;
                    case 2:
                        出售状态2 = "HOT";
                        break;
                    case 3:
                        出售状态2 = "Event";
                        break;
                    default:
                        break;
                }
                String 类型 = "";
                if ("".equals(NPCConversationManager.SN取类型(rs.getInt("serial")))) {
                    类型 = "点券";
                } else {
                    类型 = "点/抵用券";
                }
//                System.out.println( MapleItemInformationProvider.getInstance().getName(1302000));
                ((DefaultTableModel) charTable.getModel()).insertRow(charTable.getRowCount(), new Object[]{
                    rs.getInt("serial"),
                    rs.getInt("itemid"),
                    //itemName,
                    MapleItemInformationProvider.getInstance().getName(rs.getInt("itemid")),
                    rs.getInt("count"),
                    rs.getInt("discount_price"),
                    rs.getInt("period"),
                    出售状态2,
                    上架状态,
                    NPCConversationManager.SN取出售(rs.getInt("serial")),
                    NPCConversationManager.SN取库存(rs.getInt("serial")),
                    NPCConversationManager.SN取折扣(rs.getInt("serial")),
                    NPCConversationManager.SN取限购(rs.getInt("serial")),
                    类型
                });

            }
        } catch (SQLException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT `serial` FROM cashshop_modified_items WHERE serial >= " + a + " && serial <" + b + " ORDER BY `serial` DESC LIMIT 1");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String SN = rs.getString("serial");
                    int sns = Integer.parseInt(SN);
                    sns++;
                    商品编码.setText("" + sns);
                    ps.close();
                }
            }
            ps.close();
        } catch (SQLException ex) {
            System.err.println("出错读取商品：" + ex.getMessage());
        }
        if (c == 1 && d == 1) {
            显示类型.setText("热销产品");
            JOptionPane.showMessageDialog(null, "[信息]:显示热销产品，双击后可在热销产品下添加商品。");
        } else if (c == 1 && d == 2) {
            显示类型.setText("主题馆");
            JOptionPane.showMessageDialog(null, "[信息]:显示主题馆，双击后可在主题馆下添加商品。");
        } else if (c == 1 && d == 3) {
            显示类型.setText("活动");
            JOptionPane.showMessageDialog(null, "[信息]:显示活动，双击后可在活动下添加商品。");
        } else if (c == 2 && d == 1) {
            显示类型.setText("帽子");
            JOptionPane.showMessageDialog(null, "[信息]:显示帽子，双击后可在帽子下添加商品。");
        } else if (c == 2 && d == 2) {
            显示类型.setText("裙裤");
            JOptionPane.showMessageDialog(null, "[信息]:显示裙裤，双击后可在裙裤下添加商品。");
        } else if (c == 2 && d == 3) {
            显示类型.setText("披风");
            JOptionPane.showMessageDialog(null, "[信息]:显示披风，双击后可在披风下添加商品。");
        } else if (c == 2 && d == 4) {
            显示类型.setText("飞镖");
            JOptionPane.showMessageDialog(null, "[信息]:显示飞镖，双击后可在飞镖下添加商品。");
        } else if (c == 2 && d == 5) {
            显示类型.setText("长袍");
            JOptionPane.showMessageDialog(null, "[信息]:显示长袍，双击后可在长袍下添加商品。");
        } else if (c == 2 && d == 6) {
            显示类型.setText("脸饰");
            JOptionPane.showMessageDialog(null, "[信息]:显示脸饰，双击后可在脸饰下添加商品。");
        } else if (c == 2 && d == 7) {
            显示类型.setText("鞋子");
            JOptionPane.showMessageDialog(null, "[信息]:显示鞋子，双击后可在鞋子下添加商品。");
        } else if (c == 2 && d == 8) {
            显示类型.setText("骑宠");
            JOptionPane.showMessageDialog(null, "[信息]:显示骑宠，双击后可在骑宠下添加商品。");
        } else if (c == 2 && d == 9) {
            显示类型.setText("戒指");
            JOptionPane.showMessageDialog(null, "[信息]:显示戒指，双击后可在戒指下添加商品。");
        } else if (c == 2 && d == 10) {
            显示类型.setText("眼饰");
            JOptionPane.showMessageDialog(null, "[信息]:显示眼饰，双击后可在眼饰下添加商品。");
        } else if (c == 2 && d == 11) {
            显示类型.setText("手套");
            JOptionPane.showMessageDialog(null, "[信息]:显示手套，双击后可在手套下添加商品。");
        } else if (c == 2 && d == 12) {
            显示类型.setText("武器");
            JOptionPane.showMessageDialog(null, "[信息]:显示武器，双击后可在武器下添加商品。");
        } else if (c == 2 && d == 13) {
            显示类型.setText("上衣");
            JOptionPane.showMessageDialog(null, "[信息]:显示上衣，双击后可在上衣下添加商品。");
        } else if (c == 3 && d == 1) {
            显示类型.setText("喜庆物品");
            JOptionPane.showMessageDialog(null, "[信息]:显示喜庆物品，双击后可在喜庆物品下添加商品。");
        } else if (c == 3 && d == 2) {
            显示类型.setText("通讯物品");
            JOptionPane.showMessageDialog(null, "[信息]:显示通讯物品，双击后可在通讯物品下添加商品。");
        } else if (c == 3 && d == 3) {
            显示类型.setText("卷轴");
            JOptionPane.showMessageDialog(null, "[信息]:显示卷轴，双击后可在卷轴下添加商品。");
        } else if (c == 4 && d == 1) {
            显示类型.setText("会员卡");
            JOptionPane.showMessageDialog(null, "[信息]:显示会员卡，双击后可在会员卡下添加商品。");
        } else if (c == 4 && d == 2) {
            显示类型.setText("表情");
            JOptionPane.showMessageDialog(null, "[信息]:显示表情，双击后可在表情下添加商品。");
        } else if (c == 4 && d == 3) {
            显示类型.setText("个人商店");
            JOptionPane.showMessageDialog(null, "[信息]:显示个人商店，双击后可在个人商店下添加商品。");
        } else if (c == 4 && d == 4) {
            显示类型.setText("效果");
            JOptionPane.showMessageDialog(null, "[信息]:显示效果，双击后可在效果下添加商品。");
        } else if (c == 4 && d == 5) {
            显示类型.setText("游戏");
            JOptionPane.showMessageDialog(null, "[信息]:显示游戏，双击后可在游戏下添加商品。");
        } else if (c == 4 && d == 6) {
            显示类型.setText("纪念日");
            JOptionPane.showMessageDialog(null, "[信息]:显示纪念日，双击后可在纪念日下添加商品。");
        } else if (c == 5 && d == 1) {
            显示类型.setText("宠物");
            JOptionPane.showMessageDialog(null, "[信息]:显示宠物，双击后可在宠物下添加商品。");
        } else if (c == 5 && d == 2) {
            显示类型.setText("宠物服饰");
            JOptionPane.showMessageDialog(null, "[信息]:显示宠物服饰，双击后可在宠物服饰下添加商品。");
        } else if (c == 5 && d == 3) {
            显示类型.setText("其他");
            JOptionPane.showMessageDialog(null, "[信息]:显示其他，双击后可在其他下添加商品。");
        } else {
            显示类型.setText("XXXX");
            JOptionPane.showMessageDialog(null, "[信息]:XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX。");
        }
    }

    public void 刷新() {
        if ("热销产品".equals(显示类型.getText())) {
            读取商品(10000000, 10100000, 1, 1);
        } else if ("主题馆".equals(显示类型.getText())) {
            读取商品(10100000, 10200000, 1, 2);
        } else if ("活动".equals(显示类型.getText())) {
            读取商品(10200000, 10300000, 1, 3);
        } else if ("帽子".equals(显示类型.getText())) {
            读取商品(20000000, 20100000, 2, 1);
        } else if ("裙裤".equals(显示类型.getText())) {
            读取商品(20500000, 20600000, 2, 2);
        } else if ("披风".equals(显示类型.getText())) {
            读取商品(21100000, 21200000, 2, 3);
        } else if ("飞镖".equals(显示类型.getText())) {
            读取商品(21000000, 21100000, 2, 4);
        } else if ("长袍".equals(显示类型.getText())) {
            读取商品(20300000, 20400000, 2, 5);
        } else if ("脸饰".equals(显示类型.getText())) {
            读取商品(20100000, 20200000, 2, 6);
        } else if ("鞋子".equals(显示类型.getText())) {
            读取商品(20600000, 20700000, 2, 7);
        } else if ("骑宠".equals(显示类型.getText())) {
            读取商品(21200000, 21300000, 2, 8);
        } else if ("戒指".equals(显示类型.getText())) {
            读取商品(20900000, 21000000, 2, 9);
        } else if ("眼饰".equals(显示类型.getText())) {
            读取商品(20200000, 20300000, 2, 10);
        } else if ("手套".equals(显示类型.getText())) {
            读取商品(20700000, 20800000, 2, 11);
        } else if ("武器".equals(显示类型.getText())) {
            读取商品(20800000, 20900000, 2, 12);
        } else if ("上衣".equals(显示类型.getText())) {
            读取商品(20400000, 20500000, 2, 13);
        } else if ("喜庆物品".equals(显示类型.getText())) {
            读取商品(30000000, 30100000, 3, 1);
        } else if ("通讯物品".equals(显示类型.getText())) {
            读取商品(30100000, 30200000, 3, 2);
        } else if ("卷轴".equals(显示类型.getText())) {
            读取商品(30200000, 30300000, 3, 3);
        } else if ("会员卡".equals(显示类型.getText())) {
            读取商品(50000000, 50100000, 4, 1);
        } else if ("表情".equals(显示类型.getText())) {
            读取商品(50100000, 50200000, 4, 2);
        } else if ("个人商店".equals(显示类型.getText())) {
            读取商品(50200000, 50300000, 4, 3);
        } else if ("效果".equals(显示类型.getText())) {
            读取商品(50500000, 50600000, 4, 4);
        } else if ("纪念日".equals(显示类型.getText())) {
            读取商品(50300000, 50400000, 4, 6);
        } else if ("游戏".equals(显示类型.getText())) {
            读取商品(50400000, 50500000, 4, 5);
        } else if ("宠物".equals(显示类型.getText())) {
            读取商品(60000000, 60100000, 5, 1);
        } else if ("宠物服饰".equals(显示类型.getText())) {
            读取商品(60100000, 60200000, 5, 2);
        } else if ("其他".equals(显示类型.getText())) {
            读取商品(60200000, 60300000, 5, 3);
        } else if ("".equals(显示类型.getText())) {
            initCharacterPannel();
        }
    }

    public void initCharacterPannel() {
        long start = System.currentTimeMillis();
        for (int i = ((DefaultTableModel) (this.charTable.getModel())).getRowCount() - 1; i >= 0; i--) {
            ((DefaultTableModel) (this.charTable.getModel())).removeRow(i);
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = null;
            PreparedStatement pse;
            ResultSet rs = null;
            ps = con.prepareStatement("SELECT * FROM cashshop_modified_items ");//WHERE serial > 10000000 && serial < 10100000

            rs = ps.executeQuery();
            while (rs.next()) {
                String itemName = "";
                itemName = MapleItemInformationProvider.getInstance().getName(rs.getInt("itemid"));
                String 上架状态 = "";
                if (rs.getInt("showup") == 0) {
                    上架状态 = "已经下架↓";
                } else {
                    上架状态 = "已经上架↑";
                }
                String 出售状态2 = "";
                switch (rs.getInt("mark")) {
                    case -1:
                        出售状态2 = "无";
                        break;
                    case 0:
                        出售状态2 = "NEW";
                        break;
                    case 1:
                        出售状态2 = "Sale";
                        break;
                    case 2:
                        出售状态2 = "HOT";
                        break;
                    case 3:
                        出售状态2 = "Event";
                        break;
                    default:
                        break;
                }
                ((DefaultTableModel) charTable.getModel()).insertRow(charTable.getRowCount(), new Object[]{
                    rs.getInt("serial"),
                    rs.getInt("itemid"),
                    "非详细分类不显示名称",
                    //itemName,
                    rs.getInt("count"),
                    rs.getInt("discount_price"),
                    rs.getInt("period"),
                    出售状态2,
                    上架状态,
                    NPCConversationManager.SN取出售(rs.getInt("serial")),
                    NPCConversationManager.SN取库存(rs.getInt("serial")),
                    NPCConversationManager.SN取折扣(rs.getInt("serial")),
                    NPCConversationManager.SN取限购(rs.getInt("serial"))
                });
            }
            long now = System.currentTimeMillis() - start;

        } catch (SQLException ex) {
            Logger.getLogger(Start.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        charTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int i = charTable.getSelectedRow();
                String a1 = charTable.getValueAt(i, 0).toString();
                String a2 = charTable.getValueAt(i, 1).toString();
                String a3 = charTable.getValueAt(i, 3).toString();
                String a4 = charTable.getValueAt(i, 4).toString();
                String a5 = charTable.getValueAt(i, 5).toString();
                String a6 = charTable.getValueAt(i, 6).toString();
                String a7 = charTable.getValueAt(i, 7).toString();
                String a8 = charTable.getValueAt(i, 8).toString();
                String a9 = charTable.getValueAt(i, 9).toString();
                String a10 = charTable.getValueAt(i, 10).toString();
                String a11 = charTable.getValueAt(i, 11).toString();

                商品编码.setText(a1);
                商品代码.setText(a2);
                商品数量.setText(a3);
                商品价格.setText(a4);
                商品时间.setText(a5);
                商品库存.setText(a9);
                商品折扣.setText(a10);
                每日限购.setText(a11);

                if (null != charTable.getValueAt(i, 6).toString()) {
                    switch (charTable.getValueAt(i, 6).toString()) {
                        case "无":
                            商品出售状态.setText("-1");
                            break;
                        case "NEW":
                            商品出售状态.setText("0");
                            break;
                        case "Sale":
                            商品出售状态.setText("1");
                            break;
                        case "HOT":
                            商品出售状态.setText("2");
                            break;
                        case "Event":
                            商品出售状态.setText("3");
                            break;
                        default:
                            break;
                    }
                }
            }
        });
    }
    public void 删除SN库存() {
        PreparedStatement ps2 = null;
        ResultSet rs2 = null;
        try {
            ps2 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM character7 WHERE Name = ?");
            ps2.setInt(1, Integer.parseInt(this.商品编码.getText()));
            rs2 = ps2.executeQuery();
            if (rs2.next()) {
                String sqlstr = " delete from character7 where Name =" + Integer.parseInt(this.商品编码.getText()) + ";";
                ps2.executeUpdate(sqlstr);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void 删除SN库存2() {
        PreparedStatement ps2 = null;
        ResultSet rs2 = null;
        try {
            ps2 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM character7 WHERE Name = ?  &&  channel = 2");
            ps2.setInt(1, Integer.parseInt(this.商品编码.getText()));
            rs2 = ps2.executeQuery();
            if (rs2.next()) {
                String sqlstr = " delete from character7 where Name =" + Integer.parseInt(this.商品编码.getText()) + ";";
                ps2.executeUpdate(sqlstr);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void 删除SN库存3() {
        PreparedStatement ps2 = null;
        ResultSet rs2 = null;
        try {
            ps2 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM character7 WHERE Name = ?  &&  channel = 3");
            ps2.setInt(1, Integer.parseInt(this.商品编码.getText()));
            rs2 = ps2.executeQuery();
            if (rs2.next()) {
                String sqlstr = " delete from character7 where Name =" + Integer.parseInt(this.商品编码.getText()) + ";";
                ps2.executeUpdate(sqlstr);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void 删除SN库存4() {
        PreparedStatement ps2 = null;
        ResultSet rs2 = null;
        try {
            ps2 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM character7 WHERE Name = ?  &&  channel = 4");
            ps2.setInt(1, Integer.parseInt(this.商品编码.getText()));
            rs2 = ps2.executeQuery();
            if (rs2.next()) {
                String sqlstr = " delete from character7 where Name =" + Integer.parseInt(this.商品编码.getText()) + ";";
                ps2.executeUpdate(sqlstr);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void Gaincharacter7(String Name, int Channale, int Piot) {
        try {
            int ret = Getcharacter7(Name, Channale);
            if (ret == -1) {
                ret = 0;
                PreparedStatement ps = null;
                try {
                    ps = DatabaseConnection.getConnection().prepareStatement("INSERT INTO character7 (channel, Name,Point) VALUES (?, ?, ?)");
                    ps.setInt(1, Channale);
                    ps.setString(2, Name);
                    ps.setInt(3, ret);
                    ps.execute();
                } catch (SQLException e) {
                    System.out.println("xxxxxxxx:" + e);
                } finally {
                    try {
                        if (ps != null) {
                            ps.close();
                        }
                    } catch (SQLException e) {
                        System.out.println("xxxxxxxxzzzzzzz:" + e);
                    }
                }
            }
            ret += Piot;
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE character7 SET `Point` = ? WHERE Name = ? and channel = ?");
            ps.setInt(1, ret);
            ps.setString(2, Name);
            ps.setInt(3, Channale);
            ps.execute();
            ps.close();
        } catch (SQLException sql) {
            System.err.println("Getcharacter7!!55" + sql);
        }
    }

    public static int Getcharacter7(String Name, int Channale) {
        int ret = -1;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM character7 WHERE channel = ? and Name = ?");
            ps.setInt(1, Channale);
            ps.setString(2, Name);
            ResultSet rs = ps.executeQuery();
            rs.next();
            ret = rs.getInt("Point");
            rs.close();
            ps.close();
        } catch (SQLException ex) {
        }
        return ret;
    }

    public static int Get商城物品() {
        int ret = -1;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM cashshop_modified_items WHERE serial = ?");
            int serial = 0;
            ps.setInt(1, serial);
            ResultSet rs = ps.executeQuery();
            rs.next();
            ret = rs.getInt("meso");
            rs.close();
            ps.close();
        } catch (SQLException ex) {
        }
        return ret;
    }

    public static void Gain商城物品(int Piot, int Piot1) {
        try {
            int ret = Get商城物品();
            if (ret == -1) {
                ret = 0;
                PreparedStatement ps = null;
                try {
                    ps = DatabaseConnection.getConnection().prepareStatement("INSERT INTO cashshop_modified_items (serial,meso) VALUES (?, ?)");
                    int serial = 0;
                    ps.setInt(1, serial);
                    ps.setInt(2, ret);
                    ps.execute();
                } catch (SQLException e) {
                    System.out.println("xxxxxxxx:" + e);
                } finally {
                    try {
                        if (ps != null) {
                            ps.close();
                        }
                    } catch (SQLException e) {
                        System.out.println("xxxxxxxxzzzzzzz:" + e);
                    }
                }
            }
            ret += Piot;
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE cashshop_modified_items SET `meso` = ? WHERE serial = ?");
            ps.setInt(1, ret);
            int serial = 0;
            ps.setInt(2, serial);
            ps.execute();
            ps.close();
        } catch (SQLException sql) {
            System.err.println("獲取錯誤!!55" + sql);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel24 = new javax.swing.JPanel();
        商品数量 = new javax.swing.JTextField();
        商品编码 = new javax.swing.JTextField();
        商品代码 = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        商品价格 = new javax.swing.JTextField();
        商品时间 = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        商品库存 = new javax.swing.JTextField();
        商品折扣 = new javax.swing.JTextField();
        jLabel37 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        每日限购 = new javax.swing.JTextField();
        jLabel38 = new javax.swing.JLabel();
        货币类型 = new javax.swing.JTextField();
        jButton13 = new javax.swing.JButton();
        jButton24 = new javax.swing.JButton();
        jButton27 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        添加 = new javax.swing.JButton();
        修改 = new javax.swing.JButton();
        jScrollPane132 = new javax.swing.JScrollPane();
        商城扩充价格 = new javax.swing.JTable();
        商城扩充价格修改 = new javax.swing.JTextField();
        修改背包扩充价格 = new javax.swing.JButton();
        刷新模式 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        显示类型 = new javax.swing.JTextField();
        商品出售状态 = new javax.swing.JTextField();
        jScrollPane6 = new javax.swing.JScrollPane();
        charTable = new javax.swing.JTable();
        jTabbedPane3 = new javax.swing.JTabbedPane();
        jPanel18 = new javax.swing.JPanel();
        主题馆 = new javax.swing.JButton();
        读取热销产品 = new javax.swing.JButton();
        活动 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jPanel19 = new javax.swing.JPanel();
        帽子 = new javax.swing.JButton();
        脸饰 = new javax.swing.JButton();
        眼饰 = new javax.swing.JButton();
        长袍 = new javax.swing.JButton();
        上衣 = new javax.swing.JButton();
        裙裤 = new javax.swing.JButton();
        鞋子 = new javax.swing.JButton();
        手套 = new javax.swing.JButton();
        武器 = new javax.swing.JButton();
        戒指 = new javax.swing.JButton();
        飞镖 = new javax.swing.JButton();
        披风 = new javax.swing.JButton();
        骑宠 = new javax.swing.JButton();
        jPanel20 = new javax.swing.JPanel();
        喜庆物品 = new javax.swing.JButton();
        通讯物品 = new javax.swing.JButton();
        卷轴 = new javax.swing.JButton();
        jPanel21 = new javax.swing.JPanel();
        会员卡 = new javax.swing.JButton();
        表情 = new javax.swing.JButton();
        个人商店 = new javax.swing.JButton();
        纪念日 = new javax.swing.JButton();
        游戏 = new javax.swing.JButton();
        效果 = new javax.swing.JButton();
        jPanel22 = new javax.swing.JPanel();
        宠物 = new javax.swing.JButton();
        宠物服饰 = new javax.swing.JButton();
        其他 = new javax.swing.JButton();

        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel24.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "添加值", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 18))); // NOI18N
        jPanel24.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        商品数量.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        jPanel24.add(商品数量, new org.netbeans.lib.awtextra.AbsoluteConstraints(235, 55, 65, 20));

        商品编码.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        商品编码.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                商品编码ActionPerformed(evt);
            }
        });
        jPanel24.add(商品编码, new org.netbeans.lib.awtextra.AbsoluteConstraints(84, 25, 65, 20));

        商品代码.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        jPanel24.add(商品代码, new org.netbeans.lib.awtextra.AbsoluteConstraints(84, 55, 65, -1));

        jLabel30.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel30.setText("商品数量；");
        jPanel24.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 50, -1, 30));

        jLabel29.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel29.setText("商品代码；");
        jPanel24.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, -1, 30));

        商品价格.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        jPanel24.add(商品价格, new org.netbeans.lib.awtextra.AbsoluteConstraints(234, 25, 65, -1));

        商品时间.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        jPanel24.add(商品时间, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 55, 65, 20));

        jLabel32.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel32.setText("商品库存；");
        jPanel24.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 20, 90, 30));

        jLabel33.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel33.setText("限时时间；");
        jPanel24.add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 50, -1, 30));

        jLabel34.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel34.setText("商品编码；");
        jPanel24.add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, -1, 30));

        jLabel35.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel35.setText("商品价格；");
        jPanel24.add(jLabel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 20, 90, 30));

        商品库存.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        jPanel24.add(商品库存, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 25, 65, -1));

        商品折扣.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        jPanel24.add(商品折扣, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 25, 65, -1));

        jLabel37.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel37.setText("商品反馈；");
        jPanel24.add(jLabel37, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 20, 90, 30));

        jLabel36.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel36.setText("每日限购；");
        jPanel24.add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 50, 90, 30));

        每日限购.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        jPanel24.add(每日限购, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 55, 65, -1));

        jLabel38.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel38.setText("货币类型；");
        jPanel24.add(jLabel38, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 20, 90, 30));

        货币类型.setEditable(false);
        货币类型.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        jPanel24.add(货币类型, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 25, 65, -1));

        getContentPane().add(jPanel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 450, 740, 110));

        jButton13.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jButton13.setText("上架");
        jButton13.setToolTipText("<html>\n<strong><font color=\"#FF0000\">上架；</font></strong><br>\n1.选择物品<br>\n2.上架/下架<br>");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton13, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 450, -1, -1));

        jButton24.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jButton24.setText("删除");
        jButton24.setToolTipText("<html>\n<strong><font color=\"#FF0000\">删除；</font></strong><br>\n1.选择物品<br>\n2.删除<br>");
        jButton24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton24ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton24, new org.netbeans.lib.awtextra.AbsoluteConstraints(840, 450, -1, -1));

        jButton27.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jButton27.setText("下架");
        jButton27.setToolTipText("<html>\n<strong><font color=\"#FF0000\">下架；</font></strong><br>\n1.选择物品<br>\n2.上架/下架<br>");
        jButton27.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton27ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton27, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 490, -1, -1));

        jButton2.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 0, 255));
        jButton2.setText("刷新");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(840, 490, -1, -1));

        添加.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        添加.setText("添加");
        添加.setToolTipText("<html>\n<strong><font color=\"#FF0000\">添加；</font></strong><br> \n1.选择物品分类<br>\n2.输入商品代码<br>\n3.输入商品数量<br>\n4.输入商品价格<br>\n5.输入限时时间(0代表永久)<br>\n6.选择出售状态<br>");
        添加.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                添加ActionPerformed(evt);
            }
        });
        getContentPane().add(添加, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 530, -1, -1));

        修改.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        修改.setText("修改");
        修改.setToolTipText("<html>\n<strong><font color=\"#FF0000\">修改；</font></strong><br> \n1.在列表中选择需要修改的物品<br>\n2.在文本框中输入修改值<br>\n");
        修改.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                修改ActionPerformed(evt);
            }
        });
        getContentPane().add(修改, new org.netbeans.lib.awtextra.AbsoluteConstraints(840, 530, -1, -1));

        商城扩充价格.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        商城扩充价格.setModel(new DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "背包扩充价格"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        商城扩充价格.getTableHeader().setReorderingAllowed(false);
        jScrollPane132.setViewportView(商城扩充价格);

        getContentPane().add(jScrollPane132, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 570, 130, 50));

        商城扩充价格修改.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        getContentPane().add(商城扩充价格修改, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 580, 50, -1));

        修改背包扩充价格.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        修改背包扩充价格.setText("修改");
        修改背包扩充价格.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                修改背包扩充价格ActionPerformed(evt);
            }
        });
        getContentPane().add(修改背包扩充价格, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 580, -1, -1));

        刷新模式.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        刷新模式.setText("重载商城");
        刷新模式.setToolTipText("<html>\n<strong><font color=\"#FF0000\">重载商城；</font></strong><br>\n在商城控制台中的修改需要重载才能在游戏中生效");
        刷新模式.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                刷新模式ActionPerformed(evt);
            }
        });
        getContentPane().add(刷新模式, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 580, -1, -1));

        jButton3.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jButton3.setText("重载商城");
        jButton3.setToolTipText("<html>\n<strong><font color=\"#FF0000\">重载商城；</font></strong><br>\n在商城控制台中的修改需要重载才能在游戏中生效");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 580, -1, -1));

        显示类型.setEditable(false);
        显示类型.setFont(new java.awt.Font("幼圆", 1, 14)); // NOI18N
        显示类型.setForeground(new java.awt.Color(255, 0, 51));
        显示类型.setText("测试字体");
        显示类型.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                显示类型ActionPerformed(evt);
            }
        });
        getContentPane().add(显示类型, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 580, -1, -1));

        商品出售状态.setEditable(false);
        商品出售状态.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        商品出售状态.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                商品出售状态ActionPerformed(evt);
            }
        });
        getContentPane().add(商品出售状态, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 580, 70, -1));

        charTable.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        charTable.setModel(new DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "商品编码", "物品代码", "道具名称", "数量", "价格", "限时/天", "出售状态", "上/下架", "已售出", "库存", "反馈/%", "每日限购"
            }
        ) {
            Class[] types = new Class [] {
                String.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        charTable.setToolTipText("");
        charTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane6.setViewportView(charTable);

        getContentPane().add(jScrollPane6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 64, 910, 380));

        主题馆.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        主题馆.setText("主题馆");
        主题馆.setToolTipText("<html>\n<strong><font color=\"#FF0000\">点击后；</font></strong><br> \n可在<strong><font color=\"#0000E3\">主题馆</font></strong>分类下添加商品<br> \n显示<strong><font color=\"#0000E3\">主体馆</font></strong>分类下的所有商品<br> ");
        主题馆.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                主题馆ActionPerformed(evt);
            }
        });

        读取热销产品.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        读取热销产品.setText("热销产品");
        读取热销产品.setToolTipText("<html>\n<strong><font color=\"#FF0000\">点击后；</font></strong><br> \n可在<strong><font color=\"#0000E3\">热销产品</font></strong>分类下添加商品<br> \n显示<strong><font color=\"#0000E3\">热销产品</font></strong>分类下的所有商品<br> ");
        读取热销产品.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                读取热销产品ActionPerformed(evt);
            }
        });

        活动.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        活动.setText("活动");
        活动.setToolTipText("<html>\n<strong><font color=\"#FF0000\">点击后；</font></strong><br> \n可在<strong><font color=\"#0000E3\">活动</font></strong>分类下添加商品<br> \n显示<strong><font color=\"#0000E3\">活动</font></strong>分类下的所有商品<br> ");
        活动.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                活动ActionPerformed(evt);
            }
        });

        jButton8.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        jButton8.setText("每日特卖");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addComponent(读取热销产品, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(主题馆, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(活动, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(读取热销产品, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)
            .addComponent(主题馆, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(活动, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jButton8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane3.addTab("热销产品", jPanel18);

        jPanel19.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        帽子.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        帽子.setText("帽子");
        帽子.setToolTipText("<html>\n<strong><font color=\"#FF0000\">点击后；</font></strong><br> \n可在<strong><font color=\"#0000E3\">帽子</font></strong>分类下添加商品<br> \n显示<strong><font color=\"#0000E3\">帽子</font></strong>分类下的所有商品<br> ");
        帽子.setPreferredSize(new java.awt.Dimension(30, 27));
        帽子.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                帽子ActionPerformed(evt);
            }
        });
        jPanel19.add(帽子, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 70, 30));

        脸饰.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        脸饰.setText("脸饰");
        脸饰.setToolTipText("<html>\n<strong><font color=\"#FF0000\">点击后；</font></strong><br> \n可在<strong><font color=\"#0000E3\">脸饰</font></strong>分类下添加商品<br> \n显示<strong><font color=\"#0000E3\">脸饰</font></strong>分类下的所有商品<br> ");
        脸饰.setPreferredSize(new java.awt.Dimension(30, 27));
        脸饰.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                脸饰ActionPerformed(evt);
            }
        });
        jPanel19.add(脸饰, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 0, 60, 30));

        眼饰.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        眼饰.setText("眼饰");
        眼饰.setToolTipText("<html>\n<strong><font color=\"#FF0000\">点击后；</font></strong><br> \n可在<strong><font color=\"#0000E3\">眼饰</font></strong>分类下添加商品<br> \n显示<strong><font color=\"#0000E3\">眼饰</font></strong>分类下的所有商品<br> ");
        眼饰.setPreferredSize(new java.awt.Dimension(30, 27));
        眼饰.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                眼饰ActionPerformed(evt);
            }
        });
        jPanel19.add(眼饰, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 0, 60, 30));

        长袍.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        长袍.setText("长袍");
        长袍.setToolTipText("<html>\n<strong><font color=\"#FF0000\">点击后；</font></strong><br> \n可在<strong><font color=\"#0000E3\">长袍</font></strong>分类下添加商品<br> \n显示<strong><font color=\"#0000E3\">长袍</font></strong>分类下的所有商品<br> ");
        长袍.setPreferredSize(new java.awt.Dimension(30, 27));
        长袍.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                长袍ActionPerformed(evt);
            }
        });
        jPanel19.add(长袍, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 0, 60, 30));

        上衣.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        上衣.setText("上衣");
        上衣.setToolTipText("<html>\n<strong><font color=\"#FF0000\">点击后；</font></strong><br> \n可在<strong><font color=\"#0000E3\">上衣</font></strong>分类下添加商品<br> \n显示<strong><font color=\"#0000E3\">上衣</font></strong>分类下的所有商品<br> ");
        上衣.setPreferredSize(new java.awt.Dimension(30, 27));
        上衣.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                上衣ActionPerformed(evt);
            }
        });
        jPanel19.add(上衣, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 0, 60, 30));

        裙裤.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        裙裤.setText("裙裤");
        裙裤.setToolTipText("<html>\n<strong><font color=\"#FF0000\">点击后；</font></strong><br> \n可在<strong><font color=\"#0000E3\">裙裤</font></strong>分类下添加商品<br> \n显示<strong><font color=\"#0000E3\">裙裤</font></strong>分类下的所有商品<br> ");
        裙裤.setPreferredSize(new java.awt.Dimension(30, 27));
        裙裤.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                裙裤ActionPerformed(evt);
            }
        });
        jPanel19.add(裙裤, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 0, 70, 30));

        鞋子.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        鞋子.setText("鞋子");
        鞋子.setToolTipText("<html>\n<strong><font color=\"#FF0000\">点击后；</font></strong><br> \n可在<strong><font color=\"#0000E3\">鞋子</font></strong>分类下添加商品<br> \n显示<strong><font color=\"#0000E3\">鞋子</font></strong>分类下的所有商品<br> ");
        鞋子.setPreferredSize(new java.awt.Dimension(30, 27));
        鞋子.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                鞋子ActionPerformed(evt);
            }
        });
        jPanel19.add(鞋子, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 0, 60, 30));

        手套.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        手套.setText("手套");
        手套.setToolTipText("<html>\n<strong><font color=\"#FF0000\">点击后；</font></strong><br> \n可在<strong><font color=\"#0000E3\">手套</font></strong>分类下添加商品<br> \n显示<strong><font color=\"#0000E3\">手套</font></strong>分类下的所有商品<br> ");
        手套.setPreferredSize(new java.awt.Dimension(30, 27));
        手套.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                手套ActionPerformed(evt);
            }
        });
        jPanel19.add(手套, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 0, 60, 30));

        武器.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        武器.setText("武器");
        武器.setToolTipText("<html>\n<strong><font color=\"#FF0000\">点击后；</font></strong><br> \n可在<strong><font color=\"#0000E3\">武器</font></strong>分类下添加商品<br> \n显示<strong><font color=\"#0000E3\">武器</font></strong>分类下的所有商品<br> ");
        武器.setPreferredSize(new java.awt.Dimension(30, 27));
        武器.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                武器ActionPerformed(evt);
            }
        });
        jPanel19.add(武器, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 0, 60, 30));

        戒指.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        戒指.setText("戒指");
        戒指.setToolTipText("<html>\n<strong><font color=\"#FF0000\">点击后；</font></strong><br> \n可在<strong><font color=\"#0000E3\">戒指</font></strong>分类下添加商品<br> \n显示<strong><font color=\"#0000E3\">戒指</font></strong>分类下的所有商品<br> ");
        戒指.setPreferredSize(new java.awt.Dimension(30, 27));
        戒指.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                戒指ActionPerformed(evt);
            }
        });
        jPanel19.add(戒指, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 0, 60, 30));

        飞镖.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        飞镖.setText("飞镖");
        飞镖.setToolTipText("<html>\n<strong><font color=\"#FF0000\">点击后；</font></strong><br> \n可在<strong><font color=\"#0000E3\">飞镖</font></strong>分类下添加商品<br> \n显示<strong><font color=\"#0000E3\">飞镖</font></strong>分类下的所有商品<br> ");
        飞镖.setPreferredSize(new java.awt.Dimension(30, 27));
        飞镖.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                飞镖ActionPerformed(evt);
            }
        });
        jPanel19.add(飞镖, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 0, 60, 30));

        披风.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        披风.setText("披风");
        披风.setToolTipText("<html>\n<strong><font color=\"#FF0000\">点击后；</font></strong><br> \n可在<strong><font color=\"#0000E3\">披风</font></strong>分类下添加商品<br> \n显示<strong><font color=\"#0000E3\">披风</font></strong>分类下的所有商品<br> ");
        披风.setPreferredSize(new java.awt.Dimension(30, 27));
        披风.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                披风ActionPerformed(evt);
            }
        });
        jPanel19.add(披风, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 0, 70, 30));

        骑宠.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        骑宠.setText("骑宠");
        骑宠.setToolTipText("<html>\n<strong><font color=\"#FF0000\">点击后；</font></strong><br> \n可在<strong><font color=\"#0000E3\">骑宠</font></strong>分类下添加商品<br> \n显示<strong><font color=\"#0000E3\">骑宠</font></strong>分类下的所有商品<br> ");
        骑宠.setPreferredSize(new java.awt.Dimension(30, 27));
        骑宠.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                骑宠ActionPerformed(evt);
            }
        });
        jPanel19.add(骑宠, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 0, 60, 30));

        jTabbedPane3.addTab("装备", jPanel19);

        喜庆物品.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        喜庆物品.setText("喜庆物品");
        喜庆物品.setToolTipText("<html>\n<strong><font color=\"#FF0000\">点击后；</font></strong><br> \n可在<strong><font color=\"#0000E3\">喜庆物品</font></strong>分类下添加商品<br> \n显示<strong><font color=\"#0000E3\">喜庆物品</font></strong>分类下的所有商品<br> ");
        喜庆物品.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                喜庆物品ActionPerformed(evt);
            }
        });

        通讯物品.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        通讯物品.setText("通讯物品");
        通讯物品.setToolTipText("<html>\n<strong><font color=\"#FF0000\">点击后；</font></strong><br> \n可在<strong><font color=\"#0000E3\">通讯物品</font></strong>分类下添加商品<br> \n显示<strong><font color=\"#0000E3\">通讯物品</font></strong>分类下的所有商品<br> ");
        通讯物品.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                通讯物品ActionPerformed(evt);
            }
        });

        卷轴.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        卷轴.setText("卷轴");
        卷轴.setToolTipText("<html>\n<strong><font color=\"#FF0000\">点击后；</font></strong><br> \n可在<strong><font color=\"#0000E3\">卷轴</font></strong>分类下添加商品<br> \n显示<strong><font color=\"#0000E3\">卷轴</font></strong>分类下的所有商品<br> ");
        卷轴.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                卷轴ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addComponent(喜庆物品, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(通讯物品, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(卷轴, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(喜庆物品, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)
            .addComponent(通讯物品, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(卷轴, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane3.addTab("消耗", jPanel20);

        jPanel21.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        会员卡.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        会员卡.setText("会员卡");
        会员卡.setToolTipText("<html>\n<strong><font color=\"#FF0000\">点击后；</font></strong><br> \n可在<strong><font color=\"#0000E3\">会员卡</font></strong>分类下添加商品<br> \n显示<strong><font color=\"#0000E3\">会员卡</font></strong>分类下的所有商品<br> ");
        会员卡.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                会员卡ActionPerformed(evt);
            }
        });
        jPanel21.add(会员卡, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 100, 30));

        表情.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        表情.setText("表情");
        表情.setToolTipText("<html>\n<strong><font color=\"#FF0000\">点击后；</font></strong><br> \n可在<strong><font color=\"#0000E3\">表情</font></strong>分类下添加商品<br> \n显示<strong><font color=\"#0000E3\">表情</font></strong>分类下的所有商品<br> ");
        表情.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                表情ActionPerformed(evt);
            }
        });
        jPanel21.add(表情, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 0, 100, 30));

        个人商店.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        个人商店.setText("个人商店");
        个人商店.setToolTipText("<html>\n<strong><font color=\"#FF0000\">点击后；</font></strong><br> \n可在<strong><font color=\"#0000E3\">个人商店</font></strong>分类下添加商品<br> \n显示<strong><font color=\"#0000E3\">个人商店</font></strong>分类下的所有商品<br> ");
        个人商店.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                个人商店ActionPerformed(evt);
            }
        });
        jPanel21.add(个人商店, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 0, 100, 30));

        纪念日.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        纪念日.setText("纪念日");
        纪念日.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                纪念日ActionPerformed(evt);
            }
        });
        jPanel21.add(纪念日, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 0, 100, 30));

        游戏.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        游戏.setText("游戏");
        游戏.setToolTipText("<html>\n<strong><font color=\"#FF0000\">点击后；</font></strong><br> \n可在<strong><font color=\"#0000E3\">游戏</font></strong>分类下添加商品<br> \n显示<strong><font color=\"#0000E3\">游戏</font></strong>分类下的所有商品<br> ");
        游戏.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                游戏ActionPerformed(evt);
            }
        });
        jPanel21.add(游戏, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 0, 100, 30));

        效果.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        效果.setText("效果");
        效果.setToolTipText("<html>\n<strong><font color=\"#FF0000\">点击后；</font></strong><br> \n可在<strong><font color=\"#0000E3\">效果</font></strong>分类下添加商品<br> \n显示<strong><font color=\"#0000E3\">效果</font></strong>分类下的所有商品<br> ");
        效果.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                效果ActionPerformed(evt);
            }
        });
        jPanel21.add(效果, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 0, 100, 30));

        jTabbedPane3.addTab("其他", jPanel21);

        宠物.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        宠物.setText("宠物");
        宠物.setToolTipText("<html>\n<strong><font color=\"#FF0000\">点击后；</font></strong><br> \n可在<strong><font color=\"#0000E3\">宠物</font></strong>分类下添加商品<br> \n显示<strong><font color=\"#0000E3\">宠物</font></strong>分类下的所有商品<br> ");
        宠物.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                宠物ActionPerformed(evt);
            }
        });

        宠物服饰.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        宠物服饰.setText("宠物服饰");
        宠物服饰.setToolTipText("<html>\n<strong><font color=\"#FF0000\">点击后；</font></strong><br> \n可在<strong><font color=\"#0000E3\">宠物服饰</font></strong>分类下添加商品<br> \n显示<strong><font color=\"#0000E3\">宠物服饰</font></strong>分类下的所有商品<br> ");
        宠物服饰.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                宠物服饰ActionPerformed(evt);
            }
        });

        其他.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        其他.setText("其他");
        其他.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                其他ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addComponent(宠物, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(宠物服饰, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(其他, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(宠物, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)
            .addComponent(宠物服饰, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(其他, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane3.addTab("宠物", jPanel22);

        getContentPane().add(jTabbedPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void 商品编码ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_商品编码ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_商品编码ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        // TODO add your handling code here:
        int n = JOptionPane.showConfirmDialog(this, "确定为[ " + 商品编码.getText() + " 商品]    上架?", "上架商品提示消息", JOptionPane.YES_NO_OPTION);
        if (n == JOptionPane.YES_OPTION) {
            上架();
            //刷新();
        }
    }//GEN-LAST:event_jButton13ActionPerformed

    private void jButton24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton24ActionPerformed
        String 输出 = "";
        PreparedStatement ps1 = null;
        ResultSet rs1 = null;
        int 商城SN编码 = Integer.parseInt(this.商品编码.getText());
        try {
            ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM cashshop_modified_items WHERE serial = ?");
            ps1.setInt(1, 商城SN编码);
            rs1 = ps1.executeQuery();
            if (rs1.next()) {
                String sqlstr = " delete from cashshop_modified_items where serial =" + 商城SN编码 + ";";
                ps1.executeUpdate(sqlstr);
                JOptionPane.showMessageDialog(null, "[信息]:成功删除商品。");
            } else {
                JOptionPane.showMessageDialog(null, "[信息]:删除商品失败具。");

            }
        } catch (SQLException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }
        删除SN库存();
        int n = JOptionPane.showConfirmDialog(this, "是否刷新？\r\n刷新所耗时间会根据物品数量，服务器配置决定。", "信息", JOptionPane.YES_NO_OPTION);
        if (n == JOptionPane.YES_OPTION) {
            刷新();
        }
    }//GEN-LAST:event_jButton24ActionPerformed

    private void jButton27ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton27ActionPerformed
        // TODO add your handling code here:
        int n = JOptionPane.showConfirmDialog(this, "确定为[ " + 商品编码.getText() + " 商品]    下架?", "上架商品提示消息", JOptionPane.YES_NO_OPTION);
        if (n == JOptionPane.YES_OPTION) {
            下架();
            //刷新();
        }
    }//GEN-LAST:event_jButton27ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        int n = JOptionPane.showConfirmDialog(this, "是否刷新？\r\n刷新所耗时间会根据物品数量，服务器配置决定。", "信息", JOptionPane.YES_NO_OPTION);
        if (n == JOptionPane.YES_OPTION) {
            initCharacterPannel();
        }
        JOptionPane.showMessageDialog(null, "[信息]:刷新商城物品列表。");
    }//GEN-LAST:event_jButton2ActionPerformed

    private void 添加ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_添加ActionPerformed
        boolean result1 = this.商品编码.getText().matches("[0-9]+");
        boolean result2 = this.商品数量.getText().matches("[0-9]+");
        boolean result3 = this.商品价格.getText().matches("[0-9]+");
        boolean result4 = this.商品时间.getText().matches("[0-9]+");
        boolean result5 = this.商品库存.getText().matches("[0-9]+");
        boolean result6 = this.每日限购.getText().matches("[0-9]+");
        boolean result7 = this.商品折扣.getText().matches("[0-9]+");
        boolean result8 = this.商品代码.getText().matches("[0-9]+");

        if (!result1 && !result2 && !result3 && !result4 && !result5 && !result6 && !result7 && !result8) {
            JOptionPane.showMessageDialog(null, "[信息]:请输入正确的数据。");
            return;
        }
        if (商品编码.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "[信息]:请点击商品分类选择添加类型。");
            return;
        }
        if (商品代码.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "[信息]:请输入添加的商品代码。");
            return;
        }
        if (商品价格.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "[信息]:请输入商品价格。");
            return;
        }
        if (Integer.parseInt(this.商品价格.getText()) > 999999999) {
            JOptionPane.showMessageDialog(null, "[信息]:商品数量不能大于999999999。");
            return;
        }
        if (商品时间.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "[信息]:请输入商品的给予时间，0 代表无限制。");
            return;
        }
        if (商品数量.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "[信息]:请输入商品的商品数量。");
            return;
        }
        if (Integer.parseInt(this.商品数量.getText()) > 100) {
            JOptionPane.showMessageDialog(null, "[信息]:商品数量不能大于100。");
            return;
        }
        int 商品出售状态2;
        if (商品出售状态.getText().equals("")) {
            商品出售状态2 = -1;
        } else {
            商品出售状态2 = Integer.parseInt(this.商品出售状态.getText());
        }
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        try {
            ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM cashshop_modified_items WHERE serial = ?");
            ps1.setInt(1, Integer.parseInt(this.商品编码.getText()));
            rs = ps1.executeQuery();
            if (!rs.next()) {
                try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement("INSERT INTO cashshop_modified_items (serial, showup,itemid,priority,period,gender,count,meso,discount_price,mark, unk_1, unk_2, unk_3) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                    ps.setInt(1, Integer.parseInt(this.商品编码.getText()));
                    ps.setInt(2, 1);
                    ps.setInt(3, Integer.parseInt(this.商品代码.getText()));
                    ps.setInt(4, 0);
                    ps.setInt(5, Integer.parseInt(this.商品时间.getText()));
                    ps.setInt(6, 2);
                    ps.setInt(7, Integer.parseInt(this.商品数量.getText()));
                    ps.setInt(8, 0);
                    ps.setInt(9, Integer.parseInt(this.商品价格.getText()));
                    ps.setInt(10, 商品出售状态2);
                    ps.setInt(11, 0);
                    ps.setInt(12, 0);
                    ps.setInt(13, 0);
                    ps.executeUpdate();

                } catch (SQLException ex) {
                    Logger.getLogger(ZeroMS_UI.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (!商品库存.getText().equals("")) {
                    int SN库存 = Getcharacter7("" + Integer.parseInt(this.商品编码.getText()) + "", 2);
                    if (SN库存 == -1) {
                        Gaincharacter7("" + Integer.parseInt(this.商品编码.getText()) + "", 2, -2);
                    }
                    Gaincharacter7("" + Integer.parseInt(this.商品编码.getText()) + "", 2, -SN库存);
                    Gaincharacter7("" + Integer.parseInt(this.商品编码.getText()) + "", 2, Integer.parseInt(this.商品库存.getText()));
                }
                if (!商品折扣.getText().equals("")) {
                    int SN库存 = Getcharacter7("" + Integer.parseInt(this.商品编码.getText()) + "", 3);
                    if (SN库存 == -1) {
                        Gaincharacter7("" + Integer.parseInt(this.商品编码.getText()) + "", 3, -2);
                    }
                    Gaincharacter7("" + Integer.parseInt(this.商品编码.getText()) + "", 3, -SN库存);
                    Gaincharacter7("" + Integer.parseInt(this.商品编码.getText()) + "", 3, Integer.parseInt(this.商品折扣.getText()));
                }

                JOptionPane.showMessageDialog(null, "[信息]:新物品载入成功。");
                int n = JOptionPane.showConfirmDialog(this, "是否刷新？\r\n刷新所耗时间会根据物品数量，服务器配置决定。", "信息", JOptionPane.YES_NO_OPTION);
                if (n == JOptionPane.YES_OPTION) {
                    刷新();
                }
            } else {
                JOptionPane.showMessageDialog(null, "[信息]:已存在的SN编码无法成功载入。");
            }
        } catch (SQLException ex) {
            Logger.getLogger(ZeroMS_UI.class.getName()).log(Level.SEVERE, null, ex);
        }
        /*int 数字 = Integer.parseInt(this.商品代码.getText()) - 1;
        商品代码.setText("" + 数字 + "");
        /*int 数字 = Integer.parseInt(this.商品代码.getText()) - 1;
        商品代码.setText("" + 数字 + "");
        /*int 数字 = Integer.parseInt(this.商品代码.getText()) - 1;
        商品代码.setText("" + 数字 + "");
        /*int 数字 = Integer.parseInt(this.商品代码.getText()) - 1;
        商品代码.setText("" + 数字 + "");
        /*int 数字 = Integer.parseInt(this.商品代码.getText()) - 1;
        商品代码.setText("" + 数字 + "");
        /*int 数字 = Integer.parseInt(this.商品代码.getText()) - 1;
        商品代码.setText("" + 数字 + "");
        /*int 数字 = Integer.parseInt(this.商品代码.getText()) - 1;
        商品代码.setText("" + 数字 + "");
        /*int 数字 = Integer.parseInt(this.商品代码.getText()) - 1;
        商品代码.setText("" + 数字 + "");
        读取商品(60100000, 60200000, 5, 2);*/
    }//GEN-LAST:event_添加ActionPerformed

    private void 修改ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_修改ActionPerformed
        boolean result1 = this.商品编码.getText().matches("[0-9]+");
        boolean result2 = this.商品数量.getText().matches("[0-9]+");
        boolean result3 = this.商品价格.getText().matches("[0-9]+");
        boolean result4 = this.商品时间.getText().matches("[0-9]+");
        boolean result5 = this.商品库存.getText().matches("[0-9]+");
        boolean result6 = this.每日限购.getText().matches("[0-9]+");
        boolean result7 = this.商品折扣.getText().matches("[0-9]+");
        boolean result8 = this.商品代码.getText().matches("[0-9]+");
        if (!result1 && !result2 && !result3 && !result4 && !result5 && !result6 && !result7 && !result8) {
            JOptionPane.showMessageDialog(null, "[信息]:请输入正确的数据。");
            return;
        }
        if (商品编码.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "[信息]:请点击商品分类选择添加类型。");
            return;
        }
        if (商品代码.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "[信息]:请输入添加的商品代码。");
            return;
        }
        if (商品价格.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "[信息]:请输入商品价格。");
            return;
        }
        if (Integer.parseInt(this.商品价格.getText()) > 999999999) {
            JOptionPane.showMessageDialog(null, "[信息]:商品数量不能大于999999999。");
            return;
        }
        if (商品时间.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "[信息]:请输入商品的给予时间，0 代表无限制。");
            return;
        }
        if (商品数量.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "[信息]:请输入商品的商品数量。");
            return;
        }
        if (Integer.parseInt(this.商品数量.getText()) > 100) {
            JOptionPane.showMessageDialog(null, "[信息]:商品数量不能大于100。");
            return;
        }
        int 商品出售状态2;
        if (商品出售状态.getText().equals("")) {
            商品出售状态2 = -1;
        } else {
            商品出售状态2 = Integer.parseInt(this.商品出售状态.getText());
        }
        PreparedStatement ps = null;
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        try {
            //清楚table数据
            for (int i = ((DefaultTableModel) (this.charTable.getModel())).getRowCount() - 1; i >= 0; i--) {
                ((DefaultTableModel) (this.charTable.getModel())).removeRow(i);
            }
            ps = DatabaseConnection.getConnection().prepareStatement("UPDATE cashshop_modified_items SET showup = ?, itemid = ?, priority = ?, period = ?, gender = ?, count = ?, meso = ?, discount_price = ?, mark = ?, unk_1 = ?, unk_2 = ?, unk_3 = ? WHERE serial = ?");
            ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM cashshop_modified_items WHERE serial = ?");
            ps1.setInt(1, Integer.parseInt(this.商品编码.getText()));
            rs = ps1.executeQuery();
            if (rs.next()) {

                String sqlString1 = null;
                String sqlString3 = null;
                String sqlString5 = null;
                String sqlString6 = null;
                String sqlString7 = null;

                sqlString1 = "update cashshop_modified_items set itemid='" + Integer.parseInt(this.商品代码.getText()) + "' where serial=" + Integer.parseInt(this.商品编码.getText()) + ";";
                PreparedStatement itemid = DatabaseConnection.getConnection().prepareStatement(sqlString1);
                itemid.executeUpdate(sqlString1);

                sqlString3 = "update cashshop_modified_items set period='" + Integer.parseInt(this.商品时间.getText()) + "' where serial=" + Integer.parseInt(this.商品编码.getText()) + ";";
                PreparedStatement period = DatabaseConnection.getConnection().prepareStatement(sqlString3);
                period.executeUpdate(sqlString3);

                sqlString5 = "update cashshop_modified_items set count='" + Integer.parseInt(this.商品数量.getText()) + "' where serial=" + Integer.parseInt(this.商品编码.getText()) + ";";
                PreparedStatement count = DatabaseConnection.getConnection().prepareStatement(sqlString5);
                count.executeUpdate(sqlString5);

                sqlString6 = "update cashshop_modified_items set discount_price='" + Integer.parseInt(this.商品价格.getText()) + "' where serial=" + Integer.parseInt(this.商品编码.getText()) + ";";
                PreparedStatement discount_price = DatabaseConnection.getConnection().prepareStatement(sqlString6);
                discount_price.executeUpdate(sqlString6);

                sqlString7 = "update cashshop_modified_items set mark='" + Integer.parseInt(this.商品出售状态.getText()) + "' where serial=" + Integer.parseInt(this.商品编码.getText()) + ";";
                PreparedStatement mark = DatabaseConnection.getConnection().prepareStatement(sqlString7);
                mark.executeUpdate(sqlString7);
                if (!商品库存.getText().equals("")) {
                    int SN库存 = Getcharacter7("" + Integer.parseInt(this.商品编码.getText()) + "", 2);
                    if (SN库存 == -1) {
                        Gaincharacter7("" + Integer.parseInt(this.商品编码.getText()) + "", 2, -2);
                    }
                    Gaincharacter7("" + Integer.parseInt(this.商品编码.getText()) + "", 2, -SN库存);
                    Gaincharacter7("" + Integer.parseInt(this.商品编码.getText()) + "", 2, Integer.parseInt(this.商品库存.getText()));
                } else {
                    删除SN库存2();
                }
                if (!商品折扣.getText().equals("")) {
                    int SN库存 = Getcharacter7("" + Integer.parseInt(this.商品编码.getText()) + "", 3);
                    if (SN库存 == -1) {
                        Gaincharacter7("" + Integer.parseInt(this.商品编码.getText()) + "", 3, -2);
                    }
                    Gaincharacter7("" + Integer.parseInt(this.商品编码.getText()) + "", 3, -SN库存);
                    Gaincharacter7("" + Integer.parseInt(this.商品编码.getText()) + "", 3, Integer.parseInt(this.商品折扣.getText()));
                } else {
                    删除SN库存3();
                }

                if (!每日限购.getText().equals("")) {
                    int SN库存 = Getcharacter7("" + Integer.parseInt(this.商品编码.getText()) + "", 4);
                    if (SN库存 == -1) {
                        Gaincharacter7("" + Integer.parseInt(this.商品编码.getText()) + "", 4, -2);
                    }
                    Gaincharacter7("" + Integer.parseInt(this.商品编码.getText()) + "", 4, -SN库存);
                    Gaincharacter7("" + Integer.parseInt(this.商品编码.getText()) + "", 4, Integer.parseInt(this.每日限购.getText()));
                } else {
                    删除SN库存4();
                }
                JOptionPane.showMessageDialog(null, "[信息]:修改物品载入成功。");
                int n = JOptionPane.showConfirmDialog(this, "是否刷新？\r\n刷新所耗时间会根据物品数量，服务器配置决定。", "信息", JOptionPane.YES_NO_OPTION);
                if (n == JOptionPane.YES_OPTION) {
                    刷新();
                }
            } else {
                JOptionPane.showMessageDialog(null, "[信息]:只是修改！如果需要添加新的SN编码！请点击添加。");

            }
        } catch (SQLException ex) {
            Logger.getLogger(Start.class
                .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_修改ActionPerformed

    private void 修改背包扩充价格ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_修改背包扩充价格ActionPerformed
        boolean result1 = this.商城扩充价格修改.getText().matches("[0-9]+");
        if (result1) {
            if (Integer.parseInt(this.商城扩充价格修改.getText()) < 0) {
                JOptionPane.showMessageDialog(null, "[信息]:请输入正确的修改值。");
                return;
            }
            PreparedStatement ps1 = null;
            ResultSet rs = null;
            try {
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM configvalues WHERE id = ?");
                ps1.setInt(1, 1);
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlstr = " delete from configvalues where id =999";
                    ps1.executeUpdate(sqlstr);

                }
            } catch (SQLException ex) {
                Logger.getLogger(ZeroMS_UI.class
                    .getName()).log(Level.SEVERE, null, ex);
            }
            try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement("INSERT INTO configvalues (id, name,Val) VALUES ( ?, ?, ?)")) {
                ps.setInt(1, 999);
                ps.setString(2, "商城扩充价格");
                ps.setInt(3, Integer.parseInt(this.商城扩充价格修改.getText()));
                ps.executeUpdate();
                刷新商城扩充价格();
                Start.GetConfigValues();
                JOptionPane.showMessageDialog(null, "[信息]:商城扩充背包价格修改成功，已经生效。");

            } catch (SQLException ex) {
                Logger.getLogger(ZeroMS_UI.class
                    .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_修改背包扩充价格ActionPerformed

    private void 刷新模式ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_刷新模式ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_刷新模式ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        JOptionPane.showMessageDialog(null, "[信息]:商城重载开始。");
        //        World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(0, ";商城开始重新载入商品，正在维护，请勿进入。"));        // TODO add your handling code here:
        CashItemFactory.getInstance().clearCashShop();
        //        World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(0, ";商城重新载入商品成功，维护完毕，开放进入。"));
        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
            cserv.broadcastPacket((MaplePacketCreator.serverNotice(0, ";商城重新载入商品成功，维护完毕，开放进入。")));
        }
        JOptionPane.showMessageDialog(null, "[信息]:商城重载成功。");
    }//GEN-LAST:event_jButton3ActionPerformed

    private void 显示类型ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_显示类型ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_显示类型ActionPerformed

    private void 商品出售状态ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_商品出售状态ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_商品出售状态ActionPerformed

    private void 主题馆ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_主题馆ActionPerformed
        读取商品(10100000, 10200000, 1, 2);
    }//GEN-LAST:event_主题馆ActionPerformed

    private void 读取热销产品ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_读取热销产品ActionPerformed
        读取商品(10000000, 10100000, 1, 1);
    }//GEN-LAST:event_读取热销产品ActionPerformed

    private void 活动ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_活动ActionPerformed
        读取商品(10200000, 10300000, 1, 3);
    }//GEN-LAST:event_活动ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        JOptionPane.showMessageDialog(null, "[信息]:未启用。");
        //JOptionPane.showMessageDialog(this, "未启用");  // TODO add your handling code here:
    }//GEN-LAST:event_jButton8ActionPerformed

    private void 帽子ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_帽子ActionPerformed
        读取商品(20000000, 20100000, 2, 1);
    }//GEN-LAST:event_帽子ActionPerformed

    private void 脸饰ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_脸饰ActionPerformed
        读取商品(20100000, 20200000, 2, 6);
    }//GEN-LAST:event_脸饰ActionPerformed

    private void 眼饰ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_眼饰ActionPerformed
        读取商品(20200000, 20300000, 2, 10);
    }//GEN-LAST:event_眼饰ActionPerformed

    private void 长袍ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_长袍ActionPerformed
        读取商品(20300000, 20400000, 2, 5);
    }//GEN-LAST:event_长袍ActionPerformed

    private void 上衣ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_上衣ActionPerformed
        读取商品(20400000, 20500000, 2, 13);
    }//GEN-LAST:event_上衣ActionPerformed

    private void 裙裤ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_裙裤ActionPerformed
        读取商品(20500000, 20600000, 2, 2);
    }//GEN-LAST:event_裙裤ActionPerformed

    private void 鞋子ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_鞋子ActionPerformed
        读取商品(20600000, 20700000, 2, 7);
    }//GEN-LAST:event_鞋子ActionPerformed

    private void 手套ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_手套ActionPerformed
        读取商品(20700000, 20800000, 2, 11);
    }//GEN-LAST:event_手套ActionPerformed

    private void 武器ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_武器ActionPerformed
        读取商品(20800000, 20900000, 2, 12);
    }//GEN-LAST:event_武器ActionPerformed

    private void 戒指ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_戒指ActionPerformed
        读取商品(20900000, 21000000, 2, 9);
    }//GEN-LAST:event_戒指ActionPerformed

    private void 飞镖ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_飞镖ActionPerformed
        读取商品(21000000, 21100000, 2, 4);
    }//GEN-LAST:event_飞镖ActionPerformed

    private void 披风ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_披风ActionPerformed
        读取商品(21100000, 21200000, 2, 3);
    }//GEN-LAST:event_披风ActionPerformed

    private void 骑宠ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_骑宠ActionPerformed
        读取商品(21200000, 21300000, 2, 8);
    }//GEN-LAST:event_骑宠ActionPerformed

    private void 喜庆物品ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_喜庆物品ActionPerformed
        读取商品(30000000, 30100000, 3, 1);
    }//GEN-LAST:event_喜庆物品ActionPerformed

    private void 通讯物品ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_通讯物品ActionPerformed
        读取商品(30100000, 30200000, 3, 2);
    }//GEN-LAST:event_通讯物品ActionPerformed

    private void 卷轴ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_卷轴ActionPerformed
        读取商品(30200000, 30300000, 3, 3);
    }//GEN-LAST:event_卷轴ActionPerformed

    private void 会员卡ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_会员卡ActionPerformed
        读取商品(50000000, 50100000, 4, 1);
    }//GEN-LAST:event_会员卡ActionPerformed

    private void 表情ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_表情ActionPerformed
        读取商品(50100000, 50200000, 4, 2);
    }//GEN-LAST:event_表情ActionPerformed

    private void 个人商店ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_个人商店ActionPerformed
        读取商品(50200000, 50300000, 4, 3);
    }//GEN-LAST:event_个人商店ActionPerformed

    private void 纪念日ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_纪念日ActionPerformed
        读取商品(50300000, 50400000, 4, 6);
    }//GEN-LAST:event_纪念日ActionPerformed

    private void 游戏ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_游戏ActionPerformed
        读取商品(50400000, 50500000, 4, 5);
    }//GEN-LAST:event_游戏ActionPerformed

    private void 效果ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_效果ActionPerformed
        读取商品(50500000, 50600000, 4, 4);
    }//GEN-LAST:event_效果ActionPerformed

    private void 宠物ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_宠物ActionPerformed
        读取商品(60000000, 60100000, 5, 1);
    }//GEN-LAST:event_宠物ActionPerformed

    private void 宠物服饰ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_宠物服饰ActionPerformed
        读取商品(60100000, 60200000, 5, 2);
    }//GEN-LAST:event_宠物服饰ActionPerformed

    private void 其他ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_其他ActionPerformed
        读取商品(60200000, 60300000, 5, 3);
    }//GEN-LAST:event_其他ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable charTable;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton24;
    private javax.swing.JButton jButton27;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton8;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JScrollPane jScrollPane132;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTabbedPane jTabbedPane3;
    private javax.swing.JButton 上衣;
    private javax.swing.JButton 个人商店;
    private javax.swing.JButton 主题馆;
    private javax.swing.JButton 会员卡;
    private javax.swing.JButton 修改;
    private javax.swing.JButton 修改背包扩充价格;
    private javax.swing.JButton 其他;
    private javax.swing.JButton 刷新模式;
    private javax.swing.JButton 卷轴;
    private javax.swing.JTextField 商品代码;
    private javax.swing.JTextField 商品价格;
    private javax.swing.JTextField 商品出售状态;
    private javax.swing.JTextField 商品库存;
    private javax.swing.JTextField 商品折扣;
    private javax.swing.JTextField 商品数量;
    private javax.swing.JTextField 商品时间;
    private javax.swing.JTextField 商品编码;
    private javax.swing.JTable 商城扩充价格;
    private javax.swing.JTextField 商城扩充价格修改;
    private javax.swing.JButton 喜庆物品;
    private javax.swing.JButton 宠物;
    private javax.swing.JButton 宠物服饰;
    private javax.swing.JButton 帽子;
    private javax.swing.JButton 戒指;
    private javax.swing.JButton 手套;
    private javax.swing.JButton 披风;
    private javax.swing.JButton 效果;
    private javax.swing.JTextField 显示类型;
    private javax.swing.JButton 武器;
    private javax.swing.JTextField 每日限购;
    private javax.swing.JButton 活动;
    private javax.swing.JButton 添加;
    private javax.swing.JButton 游戏;
    private javax.swing.JButton 眼饰;
    private javax.swing.JButton 纪念日;
    private javax.swing.JButton 脸饰;
    private javax.swing.JButton 表情;
    private javax.swing.JButton 裙裤;
    private javax.swing.JButton 读取热销产品;
    private javax.swing.JTextField 货币类型;
    private javax.swing.JButton 通讯物品;
    private javax.swing.JButton 长袍;
    private javax.swing.JButton 鞋子;
    private javax.swing.JButton 飞镖;
    private javax.swing.JButton 骑宠;
    // End of variables declaration//GEN-END:variables
}

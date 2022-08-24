package gui.tools;

import database.DatabaseConnection;
import java.awt.Graphics;
import java.awt.Image;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel;

public class 一键还原 extends javax.swing.JFrame {

    public 一键还原() {

        ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("Image/Icon.png"));
        setTitle("一键还原数据库工具");
        Image background = new ImageIcon("gui/1.png").getImage();
        setIconImage(icon.getImage());
        initComponents();
        生成验证码();

    }

    private void 生成验证码() {
        String 生成验证码 = "1234567890aAbBcCdDeEfFgGhHiIjJkKlLmMNnOoPpQqRrSsTtUuVvWwXxYyZz1234567890";
        char 生成1 = 生成验证码.charAt((int) (Math.random() * 62));
        char 生成2 = 生成验证码.charAt((int) (Math.random() * 62));
        char 生成3 = 生成验证码.charAt((int) (Math.random() * 62));
        char 生成4 = 生成验证码.charAt((int) (Math.random() * 62));
        String 输出验证码 = "" + 生成1 + "" + 生成2 + "" + 生成3 + "" + 生成4 + "";
        验证码(输出验证码);
    }

    public void Z(int i) {
        进度条1.setValue(i);
    }

    private void 验证码(String str) {
        验证码.setText(str);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        进度条1 = new javax.swing.JProgressBar();
        还原 = new javax.swing.JButton();
        验证码 = new javax.swing.JLabel();
        输入验证码 = new javax.swing.JTextField();
        验证码1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel1.add(进度条1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 330, 40));

        还原.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        还原.setForeground(new java.awt.Color(255, 51, 102));
        还原.setText("一键还原游戏数据");
        还原.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                还原ActionPerformed(evt);
            }
        });
        jPanel1.add(还原, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 150, 160, 30));

        验证码.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
        验证码.setForeground(new java.awt.Color(51, 255, 51));
        验证码.setText("XXXX");
        jPanel1.add(验证码, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 60, 60, 40));
        jPanel1.add(输入验证码, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 70, 90, -1));

        验证码1.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        验证码1.setText("验证码:");
        jPanel1.add(验证码1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, 60, 40));

        jButton1.setText("刷");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 70, 60, 25));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 350, 540));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void 还原ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_还原ActionPerformed

        if (输入验证码.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "请输入验证码！");
            return;
        }

        if (!输入验证码.getText().equals(验证码.getText())) {
            JOptionPane.showMessageDialog(null, "验证码错误！");
            生成验证码();
            return;
        }
        Delete("accounts", 1);
        Delete("accounts_info", 2);
        Delete("bosslog", 3);
        Delete("buddies", 4);
        Delete("character_slots", 5);
        Delete("character7", 6);
        Delete("characters", 7);
        Delete("csequipment", 8);
        Delete("csitems", 9);
        Delete("famelog", 10);
        Delete("families", 11);
        Delete("fishing_rewards", 12);
        Delete("gmlog", 13);
        Delete("guilds", 14);
        Delete("hiredmerch", 15);
        Delete("hiredmerchequipment", 16);
        Delete("hiredmerchitems", 17);
        Delete("inventoryequipment", 18);
        Delete("inventoryitems", 19);
        Delete("inventorylog", 20);
        Delete("inventoryslot", 21);
        Delete("ipbans", 22);
        Delete("ipcheck", 23);
        Delete("ipvotelog", 24);
        Delete("keymap", 25);
        Delete("macbans", 26);
        Delete("monsterbook", 27);
        Delete("mountdata", 28);
        Delete("mts_cart", 29);
        Delete("notes", 30);
        Delete("nxcode", 31);
        Delete("nxcodez", 32);
        Delete("onetimelog", 33);
        Delete("pets", 34);
        Delete("questinfo", 35);
        Delete("queststatus", 36);
        Delete("queststatusmobs", 37);
    //    Delete("reactordrops", 38);
        Delete("regrocklocations", 39);
        Delete("rings", 40);
        Delete("savedlocations", 41);
        Delete("skillmacros", 42);
        Delete("skills", 43);
        Delete("speedruns", 44);
        Delete("stjflog", 45);
        Delete("stlog", 46);
        Delete("storages", 47);
        Delete("trocklocations", 48);
        Delete("uselog", 49);
        Delete("wishlist", 50);
        Delete("z_pg", 51);
       /* Delete("macbans", 52);
        Delete("mapidban", 53);
        Delete("monsterbook", 54);
        Delete("mountdata", 55);
        Delete("mts_cart", 56);
        Delete("mts_items", 57);
        Delete("mtsequipment", 58);
        Delete("mtsitems", 59);
        Delete("mtstransfer", 60);
        Delete("mtstransferequipment", 61);
        Delete("mulungdojo", 62);
        Delete("notes", 63);
        Delete("nxcode", 64);
        Delete("pets", 65);
        Delete("pnpc", 66);
        Delete("qqlog", 67);
        Delete("qqstem", 68);
        Delete("questactions", 69);
        Delete("questinfo", 70);
        Delete("queststatusmobs", 71);
        Delete("regrocklocations", 72);
        Delete("saiji", 73);
        Delete("skillmacros", 74);
        Delete("skills", 75);
        Delete("skills_cooldowns", 76);
        Delete("speedruns", 77);
        Delete("storages", 78);
        Delete("bossrank8", 79);
        Delete("bossrank8", 80);
        Delete("bossrank8", 81);
        Delete("awarp", 82);
        Delete("bank", 83);
        Delete("mail", 84);
        Delete("jiezoudashi", 85);
        Delete("shouce", 100);*/
        JOptionPane.showMessageDialog(null, "清理完成,请重启服务端生效！");
    }//GEN-LAST:event_还原ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        生成验证码();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void Delete(String a, int b) {
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("Delete from " + a + "");
            ps.executeUpdate();
            ps.close();
            Z(b);
        } catch (SQLException e) {
            System.out.println("Error/" + a + ":" + e);
        }
    }

    private void 清空个人设置() {
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("Delete from characterz");
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }
    }

    private void 清空qqgame() {
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("Delete from qqstem");
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }
    }

    private void 清空qqlog() {
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("Delete from qqlog");
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }
    }

    private void 清空养殖() {

        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("Delete from character7");
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }

    }

    private void 清空B1() {

        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("Delete from bossrank1");
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }

    }

    private void 清空B2() {

        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("Delete from bossrank2");
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }

    }

    private void 清空B3() {

        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("Delete from bossrank3");
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }

    }

    private void 清空B4() {

        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("Delete from bossrank4");
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }

    }

    private void 清空B5() {

        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("Delete from bossrank5");
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }

    }

    private void 清空B6() {

        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("Delete from bossrank6");
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }

    }

    private void 清空B7() {

        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("Delete from bossrank7");
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }

    }

    private void 清空B8() {

        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("Delete from bossrank8");
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }

    }    private void 清空拍卖b1() {
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("Delete from auctionitems1");
            ps.executeUpdate();
            ps.close();
            Z(50);
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }
    }

    private void 清空拍卖b2() {
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("Delete from auctionpoint1");
            ps.executeUpdate();
            ps.close();
            Z(100);
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }
    }
    private void 清除SN(String a, int b) {
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        try {
            ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM " + a + " WHERE SN > " + b + "");
            ps1.setInt(1, b);
            rs = ps1.executeQuery();
            if (rs.next()) {
                String sqlstr = " delete from " + a + " where SN >" + b + "";
                ps1.executeUpdate(sqlstr);
            }
        } catch (SQLException ex) {
        }
    }

    private void 清除商城所有商品() {
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("Delete from cashshop_modified_items");
            ps.executeUpdate();
            ps.close();
            Z(100);
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }
    }

    private void 清空个人随身仓库() {
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("Delete from bank_item");
            ps.executeUpdate();
            ps.close();
            Z(100);
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }
    }

    private void 清空家族随身仓库() {
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("Delete from bank_item1");
            ps.executeUpdate();
            ps.close();
            Z(100);
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }
    }

    private void 清空拍卖a1() {
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("Delete from auctionitems");
            ps.executeUpdate();
            ps.close();
            Z(50);
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }
    }

    private void 清空拍卖a2() {
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("Delete from auctionpoint");
            ps.executeUpdate();
            ps.close();
            Z(100);
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }
    }

    private void 清理雇佣金币() {

        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("Delete from hiredmerch");
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }

    }

    private void 清空记录角色人数() {

        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("Delete from z角色统计");
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }

    }

    private void 清空商城() {

        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("Delete from csitems");
            ps.executeUpdate();
            ps.close();
            Z(56);
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }

    }

    private void 清空技能2() {

        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("Delete from skills");
            ps.executeUpdate();
            ps.close();
            Z(54);
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }

    }

    private void 清空技能1() {

        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("Delete from skillmacros");
            ps.executeUpdate();
            ps.close();
            Z(52);
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }

    }

    private void 清空任务2() {

        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("Delete from queststatusmobs");
            ps.executeUpdate();
            ps.close();
            Z(50);
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }

    }

    private void 清空任务1() {

        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("Delete from queststatus");
            ps.executeUpdate();
            ps.close();
            Z(48);
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }

    }

    private void 清空D() {

        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("Delete from mountdata");
            ps.executeUpdate();
            ps.close();
            Z(46);
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }

    }

    private void 清空C() {

        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("Delete from keymap");
            ps.executeUpdate();
            ps.close();
            Z(44);
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }

    }

    private void 清空B() {

        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("Delete from inventoryslot");
            ps.executeUpdate();
            ps.close();
            Z(42);
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }

    }

    private void 清空论坛1() {

        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("Delete from forum_thread");
            ps.executeUpdate();
            ps.close();
            Z(30);
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }

    }

    private void 清空A() {

        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("Delete from hypay");
            ps.executeUpdate();
            ps.close();
            Z(40);
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }

    }

    private void 清空武器2() {

        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("Delete from inventoryitems");
            ps.executeUpdate();
            ps.close();
            Z(38);
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }

    }

    private void 清空武器1() {

        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("Delete from inventoryequipment");
            ps.executeUpdate();
            ps.close();
            Z(36);
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }

    }

    private void 清空论坛2() {

        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("Delete from forum_section");
            ps.executeUpdate();
            ps.close();
            Z(32);
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }

    }

    private void 清空论坛3() {

        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("Delete from forum_reply");
            ps.executeUpdate();
            ps.close();
            Z(34);
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }

    }

    private void 清空世界爆物() {

        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("Delete from drop_data_global");
            ps.executeUpdate();
            ps.close();
            Z(28);
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }

    }

    private void 清空家族表() {
        Z(6);
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("Delete from guilds");
            ps.executeUpdate();
            ps.close();

        } catch (SQLException e) {
            System.out.println("Error " + e);
        }
        Z(8);
    }

    private void 清空角色表() {
        Z(10);
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("Delete from characters");
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }
        Z(12);
    }

    private void 清空核心数据库() {
        Z(14);
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("Delete from bossrank");
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }
        Z(16);
    }

    private void 清空每日列表() {
        Z(18);
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("Delete from bosslog");
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }
        Z(20);
    }

    private void 清空随身仓库() {

        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("Delete from bank_item");
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }
        Z(22);
    }

    private void 清空拍卖1() {

        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("Delete from auctionitems");
            ps.executeUpdate();
            ps.close();
            Z(24);
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }

    }

    private void 清空拍卖2() {

        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("Delete from auctionpoint");
            ps.executeUpdate();
            ps.close();
            Z(26);
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }

    }

    private void 清空账号() {
        Z(2);
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("Delete from accounts");
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }
        Z(4);
    }

    private void 清空雇佣1() {

        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("Delete from hiredmerch");
            ps.executeUpdate();
            ps.close();
            Z(58);
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }
    }

    private void 清空雇佣2() {

        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("Delete from hiredmerchequipment");
            ps.executeUpdate();
            ps.close();
            Z(60);
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }
    }

    private void 清空雇佣3() {

        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("Delete from hiredmerchitems");
            ps.executeUpdate();
            ps.close();
            Z(62);
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }
    }

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(一键还原.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(一键还原.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(一键还原.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(一键还原.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        一键还原.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.setLookAndFeel(new SubstanceBusinessBlackSteelLookAndFeel());
            // UIManager.setLookAndFeel(new SubstanceBusinessLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new 一键还原().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField 输入验证码;
    private javax.swing.JButton 还原;
    private javax.swing.JProgressBar 进度条1;
    private javax.swing.JLabel 验证码;
    private javax.swing.JLabel 验证码1;
    // End of variables declaration//GEN-END:variables

}

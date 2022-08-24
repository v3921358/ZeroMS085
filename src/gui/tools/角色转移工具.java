/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.tools;

import database.DatabaseConnection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel;

/**
 *
 * @author Administrator
 */
public class 角色转移工具 extends javax.swing.JFrame {

    /**
     * Creates new form 锻造控制台
     */
    public 角色转移工具() {
        ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("Image/Icon.png"));
        setIconImage(icon.getImage());
        setTitle("【角色转移工具，可关闭】");
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel4 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        显示游戏账号 = new javax.swing.JTable();
        显示多角色账号 = new javax.swing.JButton();
        账号ID = new javax.swing.JTextField();
        jLabel315 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        转出角色的账号 = new javax.swing.JTextField();
        jLabel314 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        显示角色1 = new javax.swing.JTable();
        jLabel316 = new javax.swing.JLabel();
        角色ID = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        转入角色的账号 = new javax.swing.JTextField();
        开始转移 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        显示角色2 = new javax.swing.JTable();
        jLabel317 = new javax.swing.JLabel();
        jLabel318 = new javax.swing.JLabel();

        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        显示游戏账号.setFont(new java.awt.Font("幼圆", 0, 18)); // NOI18N
        显示游戏账号.setModel(new DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "账号ID", "游戏账号", "绑定QQ"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(显示游戏账号);
        if (显示游戏账号.getColumnModel().getColumnCount() > 0) {
            显示游戏账号.getColumnModel().getColumn(0).setResizable(false);
            显示游戏账号.getColumnModel().getColumn(0).setPreferredWidth(100);
            显示游戏账号.getColumnModel().getColumn(1).setResizable(false);
            显示游戏账号.getColumnModel().getColumn(1).setPreferredWidth(200);
            显示游戏账号.getColumnModel().getColumn(2).setResizable(false);
            显示游戏账号.getColumnModel().getColumn(2).setPreferredWidth(150);
        }

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 430, 410));

        显示多角色账号.setText("显示多角色账号");
        显示多角色账号.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                显示多角色账号ActionPerformed(evt);
            }
        });
        jPanel1.add(显示多角色账号, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 600, 360, 30));

        账号ID.setEditable(false);
        jPanel1.add(账号ID, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 600, 60, 30));

        jLabel315.setFont(new java.awt.Font("幼圆", 0, 24)); // NOI18N
        jLabel315.setText("选择需要转移角色的账号；");
        jPanel1.add(jLabel315, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        jTabbedPane1.addTab("第一步", jPanel1);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        转出角色的账号.setEditable(false);
        jPanel2.add(转出角色的账号, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, 230, 30));

        jLabel314.setFont(new java.awt.Font("幼圆", 0, 18)); // NOI18N
        jLabel314.setText("双击转移的角色；");
        jPanel2.add(jLabel314, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, -1, -1));

        显示角色1.setFont(new java.awt.Font("幼圆", 0, 24)); // NOI18N
        显示角色1.setModel(new DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "*", "角色"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(显示角色1);
        if (显示角色1.getColumnModel().getColumnCount() > 0) {
            显示角色1.getColumnModel().getColumn(0).setResizable(false);
            显示角色1.getColumnModel().getColumn(0).setPreferredWidth(20);
            显示角色1.getColumnModel().getColumn(1).setResizable(false);
            显示角色1.getColumnModel().getColumn(1).setPreferredWidth(150);
        }

        jPanel2.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 120, 410, 500));

        jLabel316.setFont(new java.awt.Font("幼圆", 0, 18)); // NOI18N
        jLabel316.setText("转出角色的账号；");
        jPanel2.add(jLabel316, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, -1));

        角色ID.setEditable(false);
        jPanel2.add(角色ID, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 630, 60, 30));

        jTabbedPane1.addTab("第二步", jPanel2);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel3.add(转入角色的账号, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, 150, 30));

        开始转移.setText("开始转移");
        开始转移.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                开始转移ActionPerformed(evt);
            }
        });
        jPanel3.add(开始转移, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 40, 100, 30));

        显示角色2.setFont(new java.awt.Font("幼圆", 0, 18)); // NOI18N
        显示角色2.setModel(new DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "*", "角色"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(显示角色2);
        if (显示角色2.getColumnModel().getColumnCount() > 0) {
            显示角色2.getColumnModel().getColumn(0).setResizable(false);
            显示角色2.getColumnModel().getColumn(0).setPreferredWidth(20);
            显示角色2.getColumnModel().getColumn(1).setResizable(false);
            显示角色2.getColumnModel().getColumn(1).setPreferredWidth(150);
        }

        jPanel3.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 160, 420, 500));

        jLabel317.setFont(new java.awt.Font("幼圆", 0, 18)); // NOI18N
        jLabel317.setText("开始转移后刷新该账号下的角色列表；");
        jPanel3.add(jLabel317, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, -1, -1));

        jLabel318.setFont(new java.awt.Font("幼圆", 0, 18)); // NOI18N
        jLabel318.setText("填写转入角色的账号；");
        jPanel3.add(jLabel318, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, -1));

        jTabbedPane1.addTab("第三步", jPanel3);

        jPanel4.add(jTabbedPane1);

        getContentPane().add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 460, 510));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void 显示多角色账号ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_显示多角色账号ActionPerformed
        刷新账号信息();
    }//GEN-LAST:event_显示多角色账号ActionPerformed

    private void 开始转移ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_开始转移ActionPerformed
        if (账号取账号ID(转入角色的账号.getText()) != 0) {
            PreparedStatement ps = null;
            PreparedStatement ps1 = null;
            ResultSet rs = null;
            try {
                ps = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM characters ");
                rs = ps.executeQuery();
                while (rs.next()) {
                    String sqlString2 = null;
                    sqlString2 = "update characters set accountid = " + 账号取账号ID(转入角色的账号.getText()) + " where id ='" + Integer.parseInt(this.角色ID.getText()) + "';";
                    PreparedStatement priority = DatabaseConnection.getConnection().prepareStatement(sqlString2);
                    priority.executeUpdate(sqlString2);
                }
                JOptionPane.showMessageDialog(null, "转移成功。");
            } catch (SQLException ex) {
            }
            刷新账号角色1();
            刷新账号角色2();
        } else {
            JOptionPane.showMessageDialog(null, "没有查询到该账号的ID。");
        }

    }//GEN-LAST:event_开始转移ActionPerformed
    public static int 账号取账号ID(String id) {
        int data = 0;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT id as DATA FROM accounts WHERE name = ?");
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data = rs.getInt("DATA");
                }
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("账号取账号ID、出错");
        }
        return data;
    }

    private void 刷新账号信息() {
        for (int i = ((DefaultTableModel) (this.显示游戏账号.getModel())).getRowCount() - 1; i >= 0; i--) {
            ((DefaultTableModel) (this.显示游戏账号.getModel())).removeRow(i);
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = null;
            ResultSet rs = null;
            ps = con.prepareStatement("SELECT * FROM accounts ");
            rs = ps.executeQuery();
            while (rs.next()) {
                if (多角色(rs.getInt("id")) > 1) {
                    ((DefaultTableModel) 显示游戏账号.getModel()).insertRow(显示游戏账号.getRowCount(), new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("qq")
                    });
                }
            }
        } catch (SQLException ex) {
        }
        显示游戏账号.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int i = 显示游戏账号.getSelectedRow();
                String a = 显示游戏账号.getValueAt(i, 0).toString();
                String a1 = 显示游戏账号.getValueAt(i, 1).toString();
                账号ID.setText(a);
                转出角色的账号.setText(a1);
                刷新账号角色1();
                JOptionPane.showMessageDialog(null, "选择了账号后，请进入第二步吧。");
            }
        });
    }

    private void 刷新账号角色1() {
        for (int i = ((DefaultTableModel) (this.显示角色1.getModel())).getRowCount() - 1; i >= 0; i--) {
            ((DefaultTableModel) (this.显示角色1.getModel())).removeRow(i);
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = null;
            ResultSet rs = null;
            ps = con.prepareStatement("SELECT * FROM characters WHERE accountid = " + 账号ID.getText() + "");
            rs = ps.executeQuery();
            while (rs.next()) {
                ((DefaultTableModel) 显示角色1.getModel()).insertRow(显示角色1.getRowCount(), new Object[]{
                    rs.getString("id"),
                    rs.getString("name")
                });
            }
        } catch (SQLException ex) {
        }
        显示角色1.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int i = 显示角色1.getSelectedRow();
                String a = 显示角色1.getValueAt(i, 0).toString();
                角色ID.setText(a);
                JOptionPane.showMessageDialog(null, "选择了角色后，请进入第三步吧。");
            }
        });
    }

    private void 刷新账号角色2() {
        for (int i = ((DefaultTableModel) (this.显示角色2.getModel())).getRowCount() - 1; i >= 0; i--) {
            ((DefaultTableModel) (this.显示角色2.getModel())).removeRow(i);
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = null;
            ResultSet rs = null;
            ps = con.prepareStatement("SELECT * FROM characters WHERE accountid = " + 账号取账号ID(转入角色的账号.getText()) + " ");
            rs = ps.executeQuery();
            while (rs.next()) {
                ((DefaultTableModel) 显示角色2.getModel()).insertRow(显示角色2.getRowCount(), new Object[]{
                    rs.getString("id"),
                    rs.getString("name")
                });
            }
        } catch (SQLException ex) {
        }

    }

    public static int 多角色(int a) {
        int data = 0;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM characters WHERE accountid = " + a + "");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                data += 1;
            }
        } catch (SQLException ex) {
        }
        return data;
    }

    public static void main(String args[]) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(角色转移工具.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        角色转移工具.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.setLookAndFeel(new SubstanceBusinessBlackSteelLookAndFeel());
            // UIManager.setLookAndFeel(new SubstanceBusinessLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new 角色转移工具().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel314;
    private javax.swing.JLabel jLabel315;
    private javax.swing.JLabel jLabel316;
    private javax.swing.JLabel jLabel317;
    private javax.swing.JLabel jLabel318;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JButton 开始转移;
    private javax.swing.JButton 显示多角色账号;
    private javax.swing.JTable 显示游戏账号;
    private javax.swing.JTable 显示角色1;
    private javax.swing.JTable 显示角色2;
    private javax.swing.JTextField 角色ID;
    private javax.swing.JTextField 账号ID;
    private javax.swing.JTextField 转入角色的账号;
    private javax.swing.JTextField 转出角色的账号;
    // End of variables declaration//GEN-END:variables
}

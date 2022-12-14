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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import server.MapleItemInformationProvider;
import server.life.MapleLifeFactory;

/**
 *
 * @author Administrator
 */
public class 钓鱼控制台 extends javax.swing.JFrame {

    /**
     * Creates new form 锻造控制台
     */
    public 钓鱼控制台() {
        ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("Image/Icon.png"));
        setIconImage(icon.getImage());
        setTitle("钓鱼控制台");
        initComponents();
    }



    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        删除NPC = new javax.swing.JPanel();
        jScrollPane9 = new javax.swing.JScrollPane();
        钓鱼物品 = new javax.swing.JTable();
        jPanel36 = new javax.swing.JPanel();
        钓鱼物品代码 = new javax.swing.JTextField();
        钓鱼物品概率 = new javax.swing.JTextField();
        钓鱼物品序号 = new javax.swing.JTextField();
        jLabel265 = new javax.swing.JLabel();
        jLabel266 = new javax.swing.JLabel();
        jLabel267 = new javax.swing.JLabel();
        jLabel264 = new javax.swing.JLabel();
        钓鱼物品名称 = new javax.swing.JTextField();
        刷新钓鱼物品 = new javax.swing.JButton();
        新增钓鱼物品 = new javax.swing.JButton();
        修改钓鱼物品 = new javax.swing.JButton();
        删除钓鱼物品 = new javax.swing.JButton();

        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        删除NPC.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "钓鱼控制台", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 24))); // NOI18N
        删除NPC.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        钓鱼物品.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        钓鱼物品.setModel(new DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "序号", "代码", "概率", "物品名称"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        钓鱼物品.getTableHeader().setReorderingAllowed(false);
        jScrollPane9.setViewportView(钓鱼物品);

        删除NPC.add(jScrollPane9, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 40, 480, 580));

        jPanel36.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "钓鱼编辑", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 18))); // NOI18N
        jPanel36.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        钓鱼物品代码.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        钓鱼物品代码.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                钓鱼物品代码ActionPerformed(evt);
            }
        });
        jPanel36.add(钓鱼物品代码, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 70, 110, 30));

        钓鱼物品概率.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jPanel36.add(钓鱼物品概率, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 110, 100, 30));

        钓鱼物品序号.setEditable(false);
        钓鱼物品序号.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        钓鱼物品序号.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                钓鱼物品序号ActionPerformed(evt);
            }
        });
        jPanel36.add(钓鱼物品序号, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 30, 80, 30));

        jLabel265.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel265.setText("序列号：");
        jPanel36.add(jLabel265, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, -1, -1));

        jLabel266.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel266.setText("物品代码：");
        jPanel36.add(jLabel266, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, -1, -1));

        jLabel267.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel267.setText("垂钓概率：");
        jPanel36.add(jLabel267, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 120, -1, -1));

        jLabel264.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel264.setText("物品名字：");
        jPanel36.add(jLabel264, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 160, -1, -1));

        钓鱼物品名称.setEditable(false);
        钓鱼物品名称.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jPanel36.add(钓鱼物品名称, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 150, 150, 30));

        刷新钓鱼物品.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        刷新钓鱼物品.setText("刷新钓鱼物品");
        刷新钓鱼物品.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                刷新钓鱼物品ActionPerformed(evt);
            }
        });
        jPanel36.add(刷新钓鱼物品, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 200, -1, 30));

        新增钓鱼物品.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        新增钓鱼物品.setText("新增");
        新增钓鱼物品.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                新增钓鱼物品ActionPerformed(evt);
            }
        });
        jPanel36.add(新增钓鱼物品, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 200, -1, 30));

        修改钓鱼物品.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        修改钓鱼物品.setText("修改");
        修改钓鱼物品.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                修改钓鱼物品ActionPerformed(evt);
            }
        });
        jPanel36.add(修改钓鱼物品, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 200, -1, 30));

        删除钓鱼物品.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        删除钓鱼物品.setText("删除");
        删除钓鱼物品.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                删除钓鱼物品ActionPerformed(evt);
            }
        });
        jPanel36.add(删除钓鱼物品, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 200, -1, 30));

        删除NPC.add(jPanel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 120, 380, 250));

        getContentPane().add(删除NPC, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 950, 630));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void 钓鱼物品代码ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_钓鱼物品代码ActionPerformed

    }//GEN-LAST:event_钓鱼物品代码ActionPerformed

    private void 钓鱼物品序号ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_钓鱼物品序号ActionPerformed

    }//GEN-LAST:event_钓鱼物品序号ActionPerformed

    private void 刷新钓鱼物品ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_刷新钓鱼物品ActionPerformed
        JOptionPane.showMessageDialog(null, "[信息]:刷新钓鱼奖励成功。");
        刷新钓鱼();
    }//GEN-LAST:event_刷新钓鱼物品ActionPerformed

    private void 新增钓鱼物品ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_新增钓鱼物品ActionPerformed
        boolean result1 = this.钓鱼物品代码.getText().matches("[0-9]+");
        boolean result2 = this.钓鱼物品概率.getText().matches("[0-9]+");

        if (result1 && result2) {
            if (Integer.parseInt(this.钓鱼物品代码.getText()) < 0 && Integer.parseInt(this.钓鱼物品概率.getText()) < 0) {
                JOptionPane.showMessageDialog(null, "[信息]:请填写正确的值。");
                return;
            }
            try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement("INSERT INTO 钓鱼物品 (itemid, chance ,expiration) VALUES ( ?, ?, ?)")) {
                ps.setInt(1, Integer.parseInt(this.钓鱼物品代码.getText()));
                ps.setInt(2, Integer.parseInt(this.钓鱼物品概率.getText()));
                ps.setInt(3, 1);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(null, "[信息]:新增钓鱼奖励成功。");
                刷新钓鱼();
            } catch (SQLException ex) {
                Logger.getLogger(钓鱼控制台.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "[信息]:请输入<物品代码><概率>。");
        }
    }//GEN-LAST:event_新增钓鱼物品ActionPerformed

    private void 修改钓鱼物品ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_修改钓鱼物品ActionPerformed
        PreparedStatement ps = null;
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        boolean result1 = this.钓鱼物品序号.getText().matches("[0-9]+");
        if (result1) {
            try {
                ps = DatabaseConnection.getConnection().prepareStatement("UPDATE 钓鱼物品 SET itemid = ?,chance = ?WHERE id = ?");
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM 钓鱼物品 WHERE id = ?");
                ps1.setInt(1, Integer.parseInt(this.钓鱼物品序号.getText()));
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlString1 = null;
                    String sqlString2 = null;
                    sqlString1 = "update 钓鱼物品 set itemid='" + this.钓鱼物品代码.getText() + "' where id=" + this.钓鱼物品序号.getText() + ";";
                    PreparedStatement name = DatabaseConnection.getConnection().prepareStatement(sqlString1);
                    name.executeUpdate(sqlString1);
                    sqlString2 = "update 钓鱼物品 set chance='" + this.钓鱼物品概率.getText() + "' where id=" + this.钓鱼物品序号.getText() + ";";
                    PreparedStatement level = DatabaseConnection.getConnection().prepareStatement(sqlString2);
                    level.executeUpdate(sqlString2);
                    JOptionPane.showMessageDialog(null, "[信息]:修改钓鱼物品成功。");
                    刷新钓鱼();
                }
            } catch (SQLException ex) {
                Logger.getLogger(钓鱼控制台.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "[信息]:输入<物品代码><概率>。");
        }
    }//GEN-LAST:event_修改钓鱼物品ActionPerformed

    private void 删除钓鱼物品ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_删除钓鱼物品ActionPerformed
        String 输出 = "";
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        boolean result1 = this.钓鱼物品序号.getText().matches("[0-9]+");
        if (result1) {
            try {
                //清楚table数据
                for (int i = ((DefaultTableModel) (this.钓鱼物品.getModel())).getRowCount() - 1; i >= 0; i--) {
                    ((DefaultTableModel) (this.钓鱼物品.getModel())).removeRow(i);
                }
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM 钓鱼物品 WHERE id = ?");
                ps1.setInt(1, Integer.parseInt(this.钓鱼物品序号.getText()));
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlstr = " delete from 钓鱼物品 where id =" + Integer.parseInt(this.钓鱼物品序号.getText()) + "";
                    ps1.executeUpdate(sqlstr);
                    JOptionPane.showMessageDialog(null, "[信息]:删除钓鱼奖励物品成功。");
                    刷新钓鱼();
                }
            } catch (SQLException ex) {
                Logger.getLogger(钓鱼控制台.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "[信息]:请选择你要删除的钓鱼物品。");
        }
    }//GEN-LAST:event_删除钓鱼物品ActionPerformed
    private void 刷新钓鱼() {
        for (int i = ((DefaultTableModel) (this.钓鱼物品.getModel())).getRowCount() - 1; i >= 0; i--) {
            ((DefaultTableModel) (this.钓鱼物品.getModel())).removeRow(i);
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = null;

            ResultSet rs = null;
            ps = con.prepareStatement("SELECT * FROM 钓鱼物品");
            rs = ps.executeQuery();
            while (rs.next()) {
                ((DefaultTableModel) 钓鱼物品.getModel()).insertRow(钓鱼物品.getRowCount(), new Object[]{
                    rs.getInt("id"),
                    rs.getInt("itemid"),
                    rs.getInt("chance"),
                    rs.getString("name"),
                    MapleItemInformationProvider.getInstance().getName(rs.getInt("itemid"))
                });
            }
        } catch (SQLException ex) {
            Logger.getLogger(钓鱼控制台.class.getName()).log(Level.SEVERE, null, ex);
        }
        钓鱼物品.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int i = 钓鱼物品.getSelectedRow();
                String a = 钓鱼物品.getValueAt(i, 0).toString();
                String a1 = 钓鱼物品.getValueAt(i, 1).toString();
                String a2 = 钓鱼物品.getValueAt(i, 2).toString();
                String a3 = 钓鱼物品.getValueAt(i, 3).toString();
                钓鱼物品序号.setText(a);
                钓鱼物品代码.setText(a1);
                钓鱼物品概率.setText(a2);
                钓鱼物品名称.setText(a3);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel264;
    private javax.swing.JLabel jLabel265;
    private javax.swing.JLabel jLabel266;
    private javax.swing.JLabel jLabel267;
    private javax.swing.JPanel jPanel36;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JButton 修改钓鱼物品;
    private javax.swing.JPanel 删除NPC;
    private javax.swing.JButton 删除钓鱼物品;
    private javax.swing.JButton 刷新钓鱼物品;
    private javax.swing.JButton 新增钓鱼物品;
    private javax.swing.JTable 钓鱼物品;
    private javax.swing.JTextField 钓鱼物品代码;
    private javax.swing.JTextField 钓鱼物品名称;
    private javax.swing.JTextField 钓鱼物品序号;
    private javax.swing.JTextField 钓鱼物品概率;
    // End of variables declaration//GEN-END:variables
}

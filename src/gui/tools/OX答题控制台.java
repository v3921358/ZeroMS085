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

/**
 *
 * @author Administrator
 */
public class OX答题控制台 extends javax.swing.JFrame {

    /**
     * Creates new form 锻造控制台
     */
    public OX答题控制台() {
        ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("Image/Icon.png"));
        setIconImage(icon.getImage());
        setTitle("OX答题控制台");
        initComponents();
        刷新题库();
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
        题库 = new javax.swing.JPanel();
        jScrollPane110 = new javax.swing.JScrollPane();
        OX答题题库 = new javax.swing.JTable();
        录入问题 = new javax.swing.JTextField();
        录入答案 = new javax.swing.JTextField();
        录入问题按钮 = new javax.swing.JButton();
        修改问题按钮 = new javax.swing.JButton();
        删除问题按钮 = new javax.swing.JButton();
        录入序号 = new javax.swing.JTextField();
        jLabel336 = new javax.swing.JLabel();
        jLabel350 = new javax.swing.JLabel();
        jLabel351 = new javax.swing.JLabel();
        jLabel352 = new javax.swing.JLabel();
        录入问题按钮1 = new javax.swing.JButton();

        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        删除NPC.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "OX答题控制台", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 24))); // NOI18N
        删除NPC.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        题库.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        题库.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        OX答题题库.setFont(new java.awt.Font("幼圆", 0, 18)); // NOI18N
        OX答题题库.setModel(new DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "序号", "问题", "答案"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        OX答题题库.getTableHeader().setReorderingAllowed(false);
        jScrollPane110.setViewportView(OX答题题库);

        题库.add(jScrollPane110, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, 1190, 470));
        题库.add(录入问题, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 540, 390, 30));
        题库.add(录入答案, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 540, 60, 30));

        录入问题按钮.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        录入问题按钮.setText("刷新");
        录入问题按钮.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                录入问题按钮ActionPerformed(evt);
            }
        });
        题库.add(录入问题按钮, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 540, 70, 30));

        修改问题按钮.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        修改问题按钮.setText("修改");
        修改问题按钮.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                修改问题按钮ActionPerformed(evt);
            }
        });
        题库.add(修改问题按钮, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 540, 70, 30));

        删除问题按钮.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        删除问题按钮.setText("删除");
        删除问题按钮.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                删除问题按钮ActionPerformed(evt);
            }
        });
        题库.add(删除问题按钮, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 540, 70, 30));

        录入序号.setEditable(false);
        题库.add(录入序号, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 540, 60, 30));

        jLabel336.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel336.setText("序号；");
        题库.add(jLabel336, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 520, -1, 20));

        jLabel350.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel350.setText("答案；O或者X");
        题库.add(jLabel350, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 520, -1, 20));

        jLabel351.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel351.setText("问题；");
        题库.add(jLabel351, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 520, -1, 20));

        jLabel352.setFont(new java.awt.Font("幼圆", 0, 18)); // NOI18N
        jLabel352.setForeground(new java.awt.Color(255, 51, 0));
        jLabel352.setText("题目目录；   机器人录题格式；*录题<空格>[问题]<空格>[答案]     案例：*录题 冒险岛是不是休闲娱乐的游戏 O");
        题库.add(jLabel352, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 1220, 20));

        录入问题按钮1.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        录入问题按钮1.setText("录入");
        录入问题按钮1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                录入问题按钮1ActionPerformed(evt);
            }
        });
        题库.add(录入问题按钮1, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 540, 70, 30));

        删除NPC.add(题库, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 30, 1230, 610));

        getContentPane().add(删除NPC, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1240, 630));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void 录入问题按钮ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_录入问题按钮ActionPerformed
        JOptionPane.showMessageDialog(null, "[信息]:刷新题库成功。");
        刷新题库();
    }//GEN-LAST:event_录入问题按钮ActionPerformed
    public void 刷新题库() {
        for (int i = ((DefaultTableModel) (this.OX答题题库.getModel())).getRowCount() - 1; i >= 0; i--) {
            ((DefaultTableModel) (this.OX答题题库.getModel())).removeRow(i);
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = null;

            ResultSet rs = null;
            ps = con.prepareStatement("SELECT * FROM oxdt order by a desc");
            rs = ps.executeQuery();
            while (rs.next()) {
                ((DefaultTableModel) OX答题题库.getModel()).insertRow(OX答题题库.getRowCount(), new Object[]{rs.getInt("a"), rs.getString("b"), rs.getString("c")});

            }
        } catch (SQLException ex) {
            Logger.getLogger(OX答题控制台.class.getName()).log(Level.SEVERE, null, ex);
        }
        OX答题题库.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int i = OX答题题库.getSelectedRow();
                String a = OX答题题库.getValueAt(i, 0).toString();
                String a1 = OX答题题库.getValueAt(i, 1).toString();
                String a2 = OX答题题库.getValueAt(i, 2).toString();
                录入序号.setText(a);
                录入问题.setText(a1);
                录入答案.setText(a2);
            }
        });
    }
    private void 修改问题按钮ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_修改问题按钮ActionPerformed
        if (!录入答案.getText().equals("O") && !录入答案.getText().equals("X")) {
            JOptionPane.showMessageDialog(null, "答案只能大写的 O 或者 X");
            return;
        }
        boolean result1 = this.录入序号.getText().matches("[0-9]+");
        PreparedStatement ps = null;
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        if (result1) {
            try {
                ps = DatabaseConnection.getConnection().prepareStatement("UPDATE oxdt SET b = ?, c = ? WHERE a = ?");
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM oxdt WHERE a = ?");
                ps1.setInt(1, Integer.parseInt(this.录入序号.getText()));
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlString2 = null;
                    String sqlString3 = null;
                    sqlString2 = "update oxdt set b='" + this.录入问题.getText() + "' where a=" + this.录入序号.getText() + ";";
                    PreparedStatement priority = DatabaseConnection.getConnection().prepareStatement(sqlString2);
                    priority.executeUpdate(sqlString2);
                    sqlString3 = "update oxdt set c='" + this.录入答案.getText() + "' where a=" + this.录入序号.getText() + ";";
                    PreparedStatement period = DatabaseConnection.getConnection().prepareStatement(sqlString3);
                    period.executeUpdate(sqlString3);
                    刷新题库();

                }
            } catch (SQLException ex) {
            }
        } else {
            JOptionPane.showMessageDialog(null, "请点击你需要修改的问题");
        }
    }//GEN-LAST:event_修改问题按钮ActionPerformed

    private void 删除问题按钮ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_删除问题按钮ActionPerformed
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        boolean result1 = this.录入序号.getText().matches("[0-9]+");
        if (result1) {
            try {
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM oxdt WHERE a = ?");
                ps1.setInt(1, Integer.parseInt(this.录入序号.getText()));
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlstr = " delete from oxdt where a =" + Integer.parseInt(this.录入序号.getText()) + "";
                    ps1.executeUpdate(sqlstr);
                    刷新题库();

                }
            } catch (SQLException ex) {
            }
        } else {
            JOptionPane.showMessageDialog(null, "请选择你要删除的问题 ");
        }
    }//GEN-LAST:event_删除问题按钮ActionPerformed

    private void 录入问题按钮1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_录入问题按钮1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_录入问题按钮1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable OX答题题库;
    private javax.swing.JLabel jLabel336;
    private javax.swing.JLabel jLabel350;
    private javax.swing.JLabel jLabel351;
    private javax.swing.JLabel jLabel352;
    private javax.swing.JScrollPane jScrollPane110;
    private javax.swing.JButton 修改问题按钮;
    private javax.swing.JPanel 删除NPC;
    private javax.swing.JButton 删除问题按钮;
    private javax.swing.JTextField 录入序号;
    private javax.swing.JTextField 录入答案;
    private javax.swing.JTextField 录入问题;
    private javax.swing.JButton 录入问题按钮;
    private javax.swing.JButton 录入问题按钮1;
    private javax.swing.JPanel 题库;
    // End of variables declaration//GEN-END:variables
}

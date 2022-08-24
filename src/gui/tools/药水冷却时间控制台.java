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
public class 药水冷却时间控制台 extends javax.swing.JFrame {

    /**
     * Creates new form 锻造控制台
     */
    public 药水冷却时间控制台() {
        ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("Image/Icon.png"));
        setIconImage(icon.getImage());
        setTitle("药水冷却时间控制台");
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

        jPanel6 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        药水冷却 = new javax.swing.JTable();
        jButton5 = new javax.swing.JButton();
        药水冷却时间 = new javax.swing.JTextField();
        药水序号 = new javax.swing.JTextField();
        药水名字 = new javax.swing.JTextField();
        jLabel285 = new javax.swing.JLabel();
        jLabel286 = new javax.swing.JLabel();
        jLabel287 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();

        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "药水冷却", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 24))); // NOI18N
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        药水冷却.setModel(new DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "序号", "药水", "冷却"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(药水冷却);

        jPanel6.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, 500, 270));

        jButton5.setText("刷新");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jPanel6.add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 270, 100, 30));
        jPanel6.add(药水冷却时间, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 160, 110, -1));

        药水序号.setEditable(false);
        jPanel6.add(药水序号, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 160, 70, -1));

        药水名字.setEditable(false);
        jPanel6.add(药水名字, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 160, 150, -1));

        jLabel285.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel285.setText("冷却；");
        jPanel6.add(jLabel285, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 140, -1, 20));

        jLabel286.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel286.setText("序号；");
        jPanel6.add(jLabel286, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 140, -1, 20));

        jLabel287.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel287.setText("药水；");
        jPanel6.add(jLabel287, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 140, -1, 20));

        jButton6.setText("修改");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        jPanel6.add(jButton6, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 270, 100, 30));

        getContentPane().add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 920, -1));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        刷新药水冷却();
    }//GEN-LAST:event_jButton5ActionPerformed
    private void 刷新药水冷却() {
        for (int i = ((DefaultTableModel) (this.药水冷却.getModel())).getRowCount() - 1; i >= 0; i--) {
            ((DefaultTableModel) (this.药水冷却.getModel())).removeRow(i);
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = null;
            ResultSet rs = null;
            ps = con.prepareStatement("SELECT * FROM configvalues WHERE id >= 30000 && id<= 31000");
            rs = ps.executeQuery();
            while (rs.next()) {
                ((DefaultTableModel) 药水冷却.getModel()).insertRow(药水冷却.getRowCount(), new Object[]{
                    rs.getInt("id"),
                    rs.getString("Name"),
                    rs.getInt("Val")
                });
            }
        } catch (SQLException ex) {
            Logger.getLogger(药水冷却时间控制台.class.getName()).log(Level.SEVERE, null, ex);
        }
        药水冷却.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int i = 药水冷却.getSelectedRow();
                String a = 药水冷却.getValueAt(i, 0).toString();
                String a1 = 药水冷却.getValueAt(i, 1).toString();
                String a2 = 药水冷却.getValueAt(i, 2).toString();
                药水序号.setText(a);
                药水名字.setText(a1);
                药水冷却时间.setText(a2);
            }
        });

    }
    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed

        PreparedStatement ps = null;
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        boolean result1 = this.药水冷却时间.getText().matches("[0-9]+");
        if (result1) {
            try {
                ps = DatabaseConnection.getConnection().prepareStatement("UPDATE configvalues SET Val = ?WHERE id = ?");
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM configvalues WHERE id = ?");
                ps1.setInt(1, Integer.parseInt(this.药水序号.getText()));
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlString1 = null;
                    sqlString1 = "update configvalues set Val='" + this.药水冷却时间.getText() + "' where id=" + this.药水序号.getText() + ";";
                    PreparedStatement name = DatabaseConnection.getConnection().prepareStatement(sqlString1);
                    name.executeUpdate(sqlString1);
                    JOptionPane.showMessageDialog(null, "修改成功!");
                    刷新药水冷却();
                }
            } catch (SQLException ex) {
                Logger.getLogger(药水冷却时间控制台.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jButton6ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel285;
    private javax.swing.JLabel jLabel286;
    private javax.swing.JLabel jLabel287;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable 药水冷却;
    private javax.swing.JTextField 药水冷却时间;
    private javax.swing.JTextField 药水名字;
    private javax.swing.JTextField 药水序号;
    // End of variables declaration//GEN-END:variables
}

package gui.tools;


import org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel;
import database.DatabaseConnection;
import gui.ZeroMS_UI;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import tools.FileoutputUtil;
import static tools.FileoutputUtil.CurrentReadable_Date;

/**
 *
 * @author Administrator
 */
public class 卡密制作工具 extends javax.swing.JFrame {

    /**
     * Creates new form 充值卡后台
     */
    public 卡密制作工具() {
        ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("Image/Icon.png"));
        setIconImage(icon.getImage());
        Properties 設定檔 = System.getProperties();
        setTitle("充值/兑换卡控制台");
        initComponents();
        刷新充值卡信息();
        JOptionPane.showMessageDialog(null, ""
                + "特别友情提示；\r\n"
                + "1.每次尽量不要生成太多充值卡\r\n"
                + "2.生成充值卡后可上架到发卡平台\r\n"
                + "3.请及时删除，避免与之前的卡号弄混\r\n");
        String chars = "1234567890aAbBcCdDeEfFgGhHiIjJkKlLmMNnOoPpQqRrSsTtUuVvWwXxYyZz1234567890";
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        充值卡 = new javax.swing.JPanel();
        jScrollPane81 = new javax.swing.JScrollPane();
        充值卡信息 = new javax.swing.JTable();
        刷新充值卡信息 = new javax.swing.JButton();
        刷新充值卡信息1 = new javax.swing.JButton();
        生成点券充值卡1 = new javax.swing.JButton();
        点券充值卡金额 = new javax.swing.JTextField();
        生成点券充值卡2 = new javax.swing.JButton();
        jLabel221 = new javax.swing.JLabel();
        生成抵用券充值卡1 = new javax.swing.JButton();
        抵用券充值卡金额 = new javax.swing.JTextField();
        jLabel222 = new javax.swing.JLabel();
        生成抵用券充值卡2 = new javax.swing.JButton();
        jLabel223 = new javax.swing.JLabel();
        礼包编号 = new javax.swing.JTextField();
        生成礼包1 = new javax.swing.JButton();
        生成礼包10 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        充值卡信息输出 = new javax.swing.JTextArea();

        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTabbedPane1.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N

        充值卡.setBackground(new java.awt.Color(255, 255, 255));
        充值卡.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        充值卡.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        充值卡信息.setBorder(new javax.swing.border.MatteBorder(null));
        充值卡信息.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
        充值卡信息.setModel(new DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "卡号", "类型", "数额", "礼包"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        充值卡信息.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jScrollPane81.setViewportView(充值卡信息);
        if (充值卡信息.getColumnModel().getColumnCount() > 0) {
            充值卡信息.getColumnModel().getColumn(0).setResizable(false);
            充值卡信息.getColumnModel().getColumn(0).setPreferredWidth(250);
            充值卡信息.getColumnModel().getColumn(1).setResizable(false);
            充值卡信息.getColumnModel().getColumn(1).setPreferredWidth(50);
            充值卡信息.getColumnModel().getColumn(2).setResizable(false);
            充值卡信息.getColumnModel().getColumn(2).setPreferredWidth(50);
            充值卡信息.getColumnModel().getColumn(3).setResizable(false);
        }

        充值卡.add(jScrollPane81, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 630, 510));

        刷新充值卡信息.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        刷新充值卡信息.setText("刷新充值卡信息");
        刷新充值卡信息.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                刷新充值卡信息ActionPerformed(evt);
            }
        });
        充值卡.add(刷新充值卡信息, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 490, 200, -1));

        刷新充值卡信息1.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        刷新充值卡信息1.setText("打开充值卡库存文件夹");
        刷新充值卡信息1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                刷新充值卡信息1ActionPerformed(evt);
            }
        });
        充值卡.add(刷新充值卡信息1, new org.netbeans.lib.awtextra.AbsoluteConstraints(880, 490, 210, -1));

        生成点券充值卡1.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        生成点券充值卡1.setText("生成1张");
        生成点券充值卡1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                生成点券充值卡1ActionPerformed(evt);
            }
        });
        充值卡.add(生成点券充值卡1, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 40, 130, 30));

        点券充值卡金额.setMaximumSize(new java.awt.Dimension(137, 27));
        点券充值卡金额.setMinimumSize(new java.awt.Dimension(137, 27));
        充值卡.add(点券充值卡金额, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 40, 130, 30));

        生成点券充值卡2.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        生成点券充值卡2.setText("生成10张");
        生成点券充值卡2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                生成点券充值卡2ActionPerformed(evt);
            }
        });
        充值卡.add(生成点券充值卡2, new org.netbeans.lib.awtextra.AbsoluteConstraints(960, 40, 130, 30));

        jLabel221.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel221.setText("点券充值卡金额；");
        充值卡.add(jLabel221, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 20, -1, 20));

        生成抵用券充值卡1.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        生成抵用券充值卡1.setText("生成1张");
        生成抵用券充值卡1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                生成抵用券充值卡1ActionPerformed(evt);
            }
        });
        充值卡.add(生成抵用券充值卡1, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 100, 130, 30));

        抵用券充值卡金额.setMaximumSize(new java.awt.Dimension(137, 27));
        抵用券充值卡金额.setMinimumSize(new java.awt.Dimension(137, 27));
        充值卡.add(抵用券充值卡金额, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 100, 130, 30));

        jLabel222.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel222.setText("抵用充值卡金额；");
        充值卡.add(jLabel222, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 80, -1, 20));

        生成抵用券充值卡2.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        生成抵用券充值卡2.setText("生成10张");
        生成抵用券充值卡2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                生成抵用券充值卡2ActionPerformed(evt);
            }
        });
        充值卡.add(生成抵用券充值卡2, new org.netbeans.lib.awtextra.AbsoluteConstraints(960, 100, 130, 30));

        jLabel223.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel223.setText("生成礼包；");
        充值卡.add(jLabel223, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 140, -1, 20));

        礼包编号.setMaximumSize(new java.awt.Dimension(137, 27));
        礼包编号.setMinimumSize(new java.awt.Dimension(137, 27));
        充值卡.add(礼包编号, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 160, 130, 30));

        生成礼包1.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        生成礼包1.setText("生成1张");
        生成礼包1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                生成礼包1ActionPerformed(evt);
            }
        });
        充值卡.add(生成礼包1, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 160, 130, 30));

        生成礼包10.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        生成礼包10.setText("生成10张");
        生成礼包10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                生成礼包10ActionPerformed(evt);
            }
        });
        充值卡.add(生成礼包10, new org.netbeans.lib.awtextra.AbsoluteConstraints(960, 160, 130, 30));

        jTabbedPane1.addTab("充值CDK管理", 充值卡);

        getContentPane().add(jTabbedPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1179, 560));

        充值卡信息输出.setColumns(20);
        充值卡信息输出.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        充值卡信息输出.setRows(5);
        jScrollPane1.setViewportView(充值卡信息输出);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 560, 1180, 200));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void 生成点券充值卡2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_生成点券充值卡2ActionPerformed
        boolean result1 = this.点券充值卡金额.getText().matches("[0-9]+");
        if (点券充值卡金额.getText().equals("") && !result1) {
            return;
        }
        生成自定义充值卡();
        生成自定义充值卡();
        生成自定义充值卡();
        生成自定义充值卡();
        生成自定义充值卡();
        生成自定义充值卡();
        生成自定义充值卡();
        生成自定义充值卡();
        生成自定义充值卡();
        生成自定义充值卡();// TODO add your handling code here:
    }//GEN-LAST:event_生成点券充值卡2ActionPerformed

    private void 生成点券充值卡1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_生成点券充值卡1ActionPerformed

        boolean result1 = this.点券充值卡金额.getText().matches("[0-9]+");
        if (点券充值卡金额.getText().equals("") && !result1) {
            return;
        }
        生成自定义充值卡();
    }//GEN-LAST:event_生成点券充值卡1ActionPerformed

    private void 刷新充值卡信息1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_刷新充值卡信息1ActionPerformed
        打开充值卡库存文件夹();// TODO add your handling code here:
    }//GEN-LAST:event_刷新充值卡信息1ActionPerformed

    private void 刷新充值卡信息ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_刷新充值卡信息ActionPerformed
        刷新充值卡信息();
    }//GEN-LAST:event_刷新充值卡信息ActionPerformed

    private void 生成抵用券充值卡1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_生成抵用券充值卡1ActionPerformed
        boolean result1 = this.抵用券充值卡金额.getText().matches("[0-9]+");
        if (抵用券充值卡金额.getText().equals("") && !result1) {
            return;
        }
        生成自定义充值卡2();
    }//GEN-LAST:event_生成抵用券充值卡1ActionPerformed
    public void 生成自定义充值卡2() {
        int 金额 = Integer.parseInt(抵用券充值卡金额.getText());
        String 输出 = "";
        String chars = "1234567890aAbBcCdDeEfFgGhHiIjJkKlLmMNnOoPpQqRrSsTtUuVvWwXxYyZz1234567890";
        char 生成1 = chars.charAt((int) (Math.random() * 62));
        char 生成2 = chars.charAt((int) (Math.random() * 62));
        char 生成3 = chars.charAt((int) (Math.random() * 62));
        char 生成4 = chars.charAt((int) (Math.random() * 62));
        char 生成5 = chars.charAt((int) (Math.random() * 62));
        char 生成6 = chars.charAt((int) (Math.random() * 62));
        char 生成7 = chars.charAt((int) (Math.random() * 62));
        char 生成8 = chars.charAt((int) (Math.random() * 62));
        char 生成9 = chars.charAt((int) (Math.random() * 62));
        char 生成10 = chars.charAt((int) (Math.random() * 62));
        char 生成11 = chars.charAt((int) (Math.random() * 62));
        char 生成12 = chars.charAt((int) (Math.random() * 62));
        char 生成13 = chars.charAt((int) (Math.random() * 62));
        char 生成14 = chars.charAt((int) (Math.random() * 62));
        char 生成15 = chars.charAt((int) (Math.random() * 62));
        char 生成16 = chars.charAt((int) (Math.random() * 62));
        char 生成17 = chars.charAt((int) (Math.random() * 62));
        char 生成18 = chars.charAt((int) (Math.random() * 62));
        char 生成19 = chars.charAt((int) (Math.random() * 62));
        char 生成20 = chars.charAt((int) (Math.random() * 62));
        char 生成21 = chars.charAt((int) (Math.random() * 62));
        char 生成22 = chars.charAt((int) (Math.random() * 62));
        char 生成23 = chars.charAt((int) (Math.random() * 62));
        char 生成24 = chars.charAt((int) (Math.random() * 62));
        char 生成25 = chars.charAt((int) (Math.random() * 62));
        char 生成26 = chars.charAt((int) (Math.random() * 62));
        char 生成27 = chars.charAt((int) (Math.random() * 62));
        char 生成28 = chars.charAt((int) (Math.random() * 62));
        char 生成29 = chars.charAt((int) (Math.random() * 62));
        char 生成30 = chars.charAt((int) (Math.random() * 62));

        String 充值卡 = "DY" + 生成1 + "" + 生成2 + "" + 生成3 + "" + 生成4 + "" + 生成5 + "" + 生成6 + "" + 生成7 + "" + 生成8 + "" + 生成9 + "" + 生成10 + "" + 生成11 + "" + 生成12 + "" + 生成13 + "" + 生成14 + "" + 生成15 + "" + 生成16 + "" + 生成17 + "" + 生成18 + "" + 生成19 + "" + 生成20 + "" + 生成21 + "" + 生成22 + "" + 生成23 + "" + 生成24 + "" + 生成25 + "" + 生成26 + "" + 生成27 + "" + 生成28 + "" + 生成29 + "" + 生成30 + "";
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement("INSERT INTO nxcodez ( code,leixing,valid) VALUES ( ?, ?, ?)")) {
            ps.setString(1, 充值卡);
            ps.setInt(2, 2);
            ps.setInt(3, 金额);
            ps.executeUpdate();
            FileoutputUtil.logToFile("充值卡后台库存/[" + CurrentReadable_Date() + "]" + 金额 + "充抵用券值卡.txt", "" + 充值卡 + "\r\n");
            刷新充值卡信息();
            输出 = "" + CurrentReadable_Date() + "/生成兑换卡成功，数额为 " + 金额 + " 抵用券，已经存放服务端根目录。";
        } catch (SQLException ex) {
            Logger.getLogger(卡密制作工具.class.getName()).log(Level.SEVERE, null, ex);
        }
        充值卡信息输出(输出);
    }
    private void 生成抵用券充值卡2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_生成抵用券充值卡2ActionPerformed
        boolean result1 = this.抵用券充值卡金额.getText().matches("[0-9]+");
        if (抵用券充值卡金额.getText().equals("") && !result1) {
            return;
        }
        生成自定义充值卡2();
        生成自定义充值卡2();
        生成自定义充值卡2();
        生成自定义充值卡2();
        生成自定义充值卡2();
        生成自定义充值卡2();
        生成自定义充值卡2();
        生成自定义充值卡2();
        生成自定义充值卡2();
        生成自定义充值卡2();
    }//GEN-LAST:event_生成抵用券充值卡2ActionPerformed

    private void 生成礼包1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_生成礼包1ActionPerformed
        boolean result1 = this.礼包编号.getText().matches("[0-9]+");
        if (礼包编号.getText().equals("") && !result1) {
            return;
        }
        生成礼包();
    }//GEN-LAST:event_生成礼包1ActionPerformed

    private void 生成礼包10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_生成礼包10ActionPerformed
        boolean result1 = this.礼包编号.getText().matches("[0-9]+");
        if (礼包编号.getText().equals("") && !result1) {
            return;
        }
        生成礼包();
        生成礼包();
        生成礼包();
        生成礼包();
        生成礼包();
        生成礼包();
        生成礼包();
        生成礼包();
        生成礼包();
        生成礼包();
    }//GEN-LAST:event_生成礼包10ActionPerformed
    public void 生成礼包() {
        int 礼包 = Integer.parseInt(礼包编号.getText());
        String 输出 = "";
        String chars = "1234567890aAbBcCdDeEfFgGhHiIjJkKlLmMNnOoPpQqRrSsTtUuVvWwXxYyZz1234567890";
        char 生成1 = chars.charAt((int) (Math.random() * 62));
        char 生成2 = chars.charAt((int) (Math.random() * 62));
        char 生成3 = chars.charAt((int) (Math.random() * 62));
        char 生成4 = chars.charAt((int) (Math.random() * 62));
        char 生成5 = chars.charAt((int) (Math.random() * 62));
        char 生成6 = chars.charAt((int) (Math.random() * 62));
        char 生成7 = chars.charAt((int) (Math.random() * 62));
        char 生成8 = chars.charAt((int) (Math.random() * 62));
        char 生成9 = chars.charAt((int) (Math.random() * 62));
        char 生成10 = chars.charAt((int) (Math.random() * 62));
        char 生成11 = chars.charAt((int) (Math.random() * 62));
        char 生成12 = chars.charAt((int) (Math.random() * 62));
        char 生成13 = chars.charAt((int) (Math.random() * 62));
        char 生成14 = chars.charAt((int) (Math.random() * 62));
        char 生成15 = chars.charAt((int) (Math.random() * 62));
        char 生成16 = chars.charAt((int) (Math.random() * 62));
        char 生成17 = chars.charAt((int) (Math.random() * 62));
        char 生成18 = chars.charAt((int) (Math.random() * 62));
        char 生成19 = chars.charAt((int) (Math.random() * 62));
        char 生成20 = chars.charAt((int) (Math.random() * 62));
        char 生成21 = chars.charAt((int) (Math.random() * 62));
        char 生成22 = chars.charAt((int) (Math.random() * 62));
        char 生成23 = chars.charAt((int) (Math.random() * 62));
        char 生成24 = chars.charAt((int) (Math.random() * 62));
        char 生成25 = chars.charAt((int) (Math.random() * 62));
        char 生成26 = chars.charAt((int) (Math.random() * 62));
        char 生成27 = chars.charAt((int) (Math.random() * 62));
        char 生成28 = chars.charAt((int) (Math.random() * 62));
        char 生成29 = chars.charAt((int) (Math.random() * 62));
        char 生成30 = chars.charAt((int) (Math.random() * 62));

        String 充值卡 = "LB" + 生成1 + "" + 生成2 + "" + 生成3 + "" + 生成4 + "" + 生成5 + "" + 生成6 + "" + 生成7 + "" + 生成8 + "" + 生成9 + "" + 生成10 + "" + 生成11 + "" + 生成12 + "" + 生成13 + "" + 生成14 + "" + 生成15 + "" + 生成16 + "" + 生成17 + "" + 生成18 + "" + 生成19 + "" + 生成20 + "" + 生成21 + "" + 生成22 + "" + 生成23 + "" + 生成24 + "" + 生成25 + "" + 生成26 + "" + 生成27 + "" + 生成28 + "" + 生成29 + "" + 生成30 + "";
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement("INSERT INTO nxcodez ( code,leixing,valid,itme) VALUES ( ?, ?, ?,?)")) {
            ps.setString(1, 充值卡);
            ps.setInt(2, 5);
            ps.setInt(3, 0);
            ps.setInt(4, 礼包);
            ps.executeUpdate();
            FileoutputUtil.logToFile("充值卡后台库存/[" + CurrentReadable_Date() + "]" + 礼包 + "礼包兑换卡.txt", "" + 充值卡 + "\r\n");
            刷新充值卡信息();
            输出 = "" + CurrentReadable_Date() + "/生成兑换卡成功，礼包为 " + 礼包 + " 号，已经存放服务端根目录。";
        } catch (SQLException ex) {
            Logger.getLogger(卡密制作工具.class.getName()).log(Level.SEVERE, null, ex);
        }
        充值卡信息输出(输出);
    }

    public void 生成自定义充值卡() {
        int 金额 = Integer.parseInt(点券充值卡金额.getText());
        String 输出 = "";
        String chars = "1234567890aAbBcCdDeEfFgGhHiIjJkKlLmMNnOoPpQqRrSsTtUuVvWwXxYyZz1234567890";
        char 生成1 = chars.charAt((int) (Math.random() * 62));
        char 生成2 = chars.charAt((int) (Math.random() * 62));
        char 生成3 = chars.charAt((int) (Math.random() * 62));
        char 生成4 = chars.charAt((int) (Math.random() * 62));
        char 生成5 = chars.charAt((int) (Math.random() * 62));
        char 生成6 = chars.charAt((int) (Math.random() * 62));
        char 生成7 = chars.charAt((int) (Math.random() * 62));
        char 生成8 = chars.charAt((int) (Math.random() * 62));
        char 生成9 = chars.charAt((int) (Math.random() * 62));
        char 生成10 = chars.charAt((int) (Math.random() * 62));
        char 生成11 = chars.charAt((int) (Math.random() * 62));
        char 生成12 = chars.charAt((int) (Math.random() * 62));
        char 生成13 = chars.charAt((int) (Math.random() * 62));
        char 生成14 = chars.charAt((int) (Math.random() * 62));
        char 生成15 = chars.charAt((int) (Math.random() * 62));
        char 生成16 = chars.charAt((int) (Math.random() * 62));
        char 生成17 = chars.charAt((int) (Math.random() * 62));
        char 生成18 = chars.charAt((int) (Math.random() * 62));
        char 生成19 = chars.charAt((int) (Math.random() * 62));
        char 生成20 = chars.charAt((int) (Math.random() * 62));
        char 生成21 = chars.charAt((int) (Math.random() * 62));
        char 生成22 = chars.charAt((int) (Math.random() * 62));
        char 生成23 = chars.charAt((int) (Math.random() * 62));
        char 生成24 = chars.charAt((int) (Math.random() * 62));
        char 生成25 = chars.charAt((int) (Math.random() * 62));
        char 生成26 = chars.charAt((int) (Math.random() * 62));
        char 生成27 = chars.charAt((int) (Math.random() * 62));
        char 生成28 = chars.charAt((int) (Math.random() * 62));
        char 生成29 = chars.charAt((int) (Math.random() * 62));
        char 生成30 = chars.charAt((int) (Math.random() * 62));

        String 充值卡 = "DQ" + 生成1 + "" + 生成2 + "" + 生成3 + "" + 生成4 + "" + 生成5 + "" + 生成6 + "" + 生成7 + "" + 生成8 + "" + 生成9 + "" + 生成10 + "" + 生成11 + "" + 生成12 + "" + 生成13 + "" + 生成14 + "" + 生成15 + "" + 生成16 + "" + 生成17 + "" + 生成18 + "" + 生成19 + "" + 生成20 + "" + 生成21 + "" + 生成22 + "" + 生成23 + "" + 生成24 + "" + 生成25 + "" + 生成26 + "" + 生成27 + "" + 生成28 + "" + 生成29 + "" + 生成30 + "";
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement("INSERT INTO nxcodez ( code,leixing,valid) VALUES ( ?, ?, ?)")) {
            ps.setString(1, 充值卡);
            ps.setInt(2, 1);
            ps.setInt(3, 金额);
            ps.executeUpdate();
            FileoutputUtil.logToFile("充值卡后台库存/[" + CurrentReadable_Date() + "]" + 金额 + "点券充值卡.txt", "" + 充值卡 + "\r\n");
            刷新充值卡信息();
            输出 = "" + CurrentReadable_Date() + "/生成兑换卡成功，数额为 " + 金额 + " 点券，已经存放服务端根目录。";
        } catch (SQLException ex) {
            Logger.getLogger(卡密制作工具.class.getName()).log(Level.SEVERE, null, ex);
        }
        充值卡信息输出(输出);
    }

    private void 充值卡信息输出(String str) {
        充值卡信息输出.setText(充值卡信息输出.getText() + str + "\r\n");
    }

    private static void doDeleteEmptyDir(String dir) {
        boolean success = (new File(dir)).delete();
        if (success) {
            System.out.println("Successfully deleted empty directory: " + dir);
        } else {
            System.out.println("Failed to delete empty directory: " + dir);
        }

    }

    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件  
                flag = true;
            }
        }
        return flag;
    }

    public static void 打开充值卡库存文件夹() {
        final Runtime runtime = Runtime.getRuntime();
        Process process = null;//  
        Properties 設定檔 = System.getProperties();
        final String cmd = "rundll32 url.dll FileProtocolHandler file:" + 設定檔.getProperty("user.dir") + "\\充值卡后台库存";
        try {
            process = runtime.exec(cmd);
        } catch (final Exception e) {
            System.out.println("Error exec!");
        }
    }

    public void 刷新充值卡信息() {
        for (int i = ((DefaultTableModel) (this.充值卡信息.getModel())).getRowCount() - 1; i >= 0; i--) {
            ((DefaultTableModel) (this.充值卡信息.getModel())).removeRow(i);
        }
        PreparedStatement ps1 = null;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = null;
            ResultSet rs = null;
            ps = con.prepareStatement("SELECT * FROM nxcodez");
            rs = ps.executeQuery();
            while (rs.next()) {
                String 类型 = "";
                switch (rs.getInt("leixing")) {
                    case 1:
                        类型 = "点券";
                        break;
                    case 2:
                        类型 = "抵用券";
                        break;
                    case 3:
                        类型 = "金币";
                        break;
                    case 4:
                        类型 = "经验";
                        break;
                    case 5:
                        类型 = "礼包";
                        break;
                    default:
                        break;
                }
                ((DefaultTableModel) 充值卡信息.getModel()).insertRow(充值卡信息.getRowCount(), new Object[]{
                    rs.getString("code"),
                    类型,
                    rs.getInt("valid"),
                    rs.getInt("itme")
                });
            }

        } catch (SQLException ex) {
            Logger.getLogger(ZeroMS_UI.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(卡密制作工具.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(卡密制作工具.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(卡密制作工具.class.getName()).log(Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            Logger.getLogger(卡密制作工具.class.getName()).log(Level.SEVERE, null, ex);
        }
        //</editor-fold>
        卡密制作工具.setDefaultLookAndFeelDecorated(true);
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
                new 卡密制作工具().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel221;
    private javax.swing.JLabel jLabel222;
    private javax.swing.JLabel jLabel223;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane81;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel 充值卡;
    private javax.swing.JTable 充值卡信息;
    private javax.swing.JTextArea 充值卡信息输出;
    private javax.swing.JButton 刷新充值卡信息;
    private javax.swing.JButton 刷新充值卡信息1;
    private javax.swing.JTextField 抵用券充值卡金额;
    private javax.swing.JTextField 点券充值卡金额;
    private javax.swing.JButton 生成抵用券充值卡1;
    private javax.swing.JButton 生成抵用券充值卡2;
    private javax.swing.JButton 生成点券充值卡1;
    private javax.swing.JButton 生成点券充值卡2;
    private javax.swing.JButton 生成礼包1;
    private javax.swing.JButton 生成礼包10;
    private javax.swing.JTextField 礼包编号;
    // End of variables declaration//GEN-END:variables

}

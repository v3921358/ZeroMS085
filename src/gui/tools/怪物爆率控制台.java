/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.tools;

import database.DatabaseConnection;
import gui.ZeroMS_UI;
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
import server.Start;

/**
 *
 * @author Administrator
 */
public class 怪物爆率控制台 extends javax.swing.JFrame {

    /**
     * Creates new form 锻造控制台
     */
    public 怪物爆率控制台() {
        ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("Image/Icon.png"));
        setIconImage(icon.getImage());
        setTitle("怪物爆率控制台");
        initComponents();
        刷新物品掉落持续时间();
        刷新地图物品上限();
        刷新地图刷新频率();
    }



    private void 刷新物品掉落持续时间() {
        int 显示 = ZeroMS_UI.ConfigValuesMap.get("物品掉落持续时间");
        物品掉落持续时间.setText("" + 显示 + "");
    }
    private void 刷新地图物品上限() {
        int 显示 = ZeroMS_UI.ConfigValuesMap.get("地图物品上限");
        地图物品上限.setText("" + 显示 + "");
    }
    private void 刷新地图刷新频率() {
        int 显示 = ZeroMS_UI.ConfigValuesMap.get("地图刷新频率");
        地图刷新频率.setText("" + 显示 + "");
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel26 = new javax.swing.JPanel();
        jPanel27 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        怪物爆物 = new javax.swing.JTable();
        怪物爆物序列号 = new javax.swing.JTextField();
        怪物爆物怪物代码 = new javax.swing.JTextField();
        怪物爆物物品代码 = new javax.swing.JTextField();
        怪物爆物爆率 = new javax.swing.JTextField();
        怪物爆物物品名称 = new javax.swing.JTextField();
        删除怪物爆物 = new javax.swing.JButton();
        添加怪物爆物 = new javax.swing.JButton();
        jLabel120 = new javax.swing.JLabel();
        jLabel211 = new javax.swing.JLabel();
        jLabel212 = new javax.swing.JLabel();
        jLabel213 = new javax.swing.JLabel();
        修改怪物爆物 = new javax.swing.JButton();
        刷新怪物爆物 = new javax.swing.JButton();
        jLabel39 = new javax.swing.JLabel();
        jPanel28 = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        世界爆物 = new javax.swing.JTable();
        世界爆物序列号 = new javax.swing.JTextField();
        世界爆物物品代码 = new javax.swing.JTextField();
        世界爆物爆率 = new javax.swing.JTextField();
        添加世界爆物 = new javax.swing.JButton();
        删除世界爆物 = new javax.swing.JButton();
        jLabel210 = new javax.swing.JLabel();
        jLabel202 = new javax.swing.JLabel();
        jLabel209 = new javax.swing.JLabel();
        世界爆物名称 = new javax.swing.JTextField();
        jLabel40 = new javax.swing.JLabel();
        修改世界爆物 = new javax.swing.JButton();
        刷新世界爆物 = new javax.swing.JButton();
        查询物品掉落 = new javax.swing.JButton();
        查询物品掉落代码 = new javax.swing.JTextField();
        查询怪物掉落代码 = new javax.swing.JTextField();
        删除指定的掉落按键 = new javax.swing.JButton();
        删除指定的掉落 = new javax.swing.JTextField();
        修改物品掉落持续时间 = new javax.swing.JButton();
        物品掉落持续时间 = new javax.swing.JTextField();
        jLabel320 = new javax.swing.JLabel();
        刷新怪物卡片 = new javax.swing.JButton();
        地图物品上限 = new javax.swing.JTextField();
        修改物品掉落持续时间1 = new javax.swing.JButton();
        jLabel319 = new javax.swing.JLabel();
        jLabel324 = new javax.swing.JLabel();
        jLabel325 = new javax.swing.JLabel();
        地图刷新频率 = new javax.swing.JTextField();
        修改物品掉落持续时间2 = new javax.swing.JButton();
        jLabel323 = new javax.swing.JLabel();
        jLabel316 = new javax.swing.JLabel();
        查询怪物掉落 = new javax.swing.JButton();
        jLabel321 = new javax.swing.JLabel();
        查询物品掉落1 = new javax.swing.JButton();
        查询怪物掉落1 = new javax.swing.JButton();
        删除指定的掉落按键1 = new javax.swing.JButton();
        修改物品掉落持续时间3 = new javax.swing.JButton();
        修改物品掉落持续时间4 = new javax.swing.JButton();
        修改物品掉落持续时间5 = new javax.swing.JButton();

        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel27.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "怪物爆物/(10000=1%)", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 18))); // NOI18N
        jPanel27.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        怪物爆物.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        怪物爆物.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "序列号", "怪物代码", "物品代码", "爆率", "物品名字"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        怪物爆物.getTableHeader().setReorderingAllowed(false);
        jScrollPane7.setViewportView(怪物爆物);

        jPanel27.add(jScrollPane7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 460, 380));

        怪物爆物序列号.setEditable(false);
        怪物爆物序列号.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jPanel27.add(怪物爆物序列号, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 440, 40, 30));

        怪物爆物怪物代码.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jPanel27.add(怪物爆物怪物代码, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 440, 60, 30));

        怪物爆物物品代码.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jPanel27.add(怪物爆物物品代码, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 440, 60, 30));

        怪物爆物爆率.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jPanel27.add(怪物爆物爆率, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 440, 70, 30));

        怪物爆物物品名称.setEditable(false);
        怪物爆物物品名称.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jPanel27.add(怪物爆物物品名称, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 440, 90, 30));

        删除怪物爆物.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        删除怪物爆物.setText("删除");
        删除怪物爆物.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                删除怪物爆物ActionPerformed(evt);
            }
        });
        jPanel27.add(删除怪物爆物, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 480, 70, 30));

        添加怪物爆物.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        添加怪物爆物.setText("添加");
        添加怪物爆物.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                添加怪物爆物ActionPerformed(evt);
            }
        });
        jPanel27.add(添加怪物爆物, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 480, 70, 30));

        jLabel120.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel120.setText("怪物代码；");
        jPanel27.add(jLabel120, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 420, -1, -1));

        jLabel211.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel211.setText("物品代码；");
        jPanel27.add(jLabel211, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 420, -1, 20));

        jLabel212.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel212.setText("爆率；");
        jPanel27.add(jLabel212, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 420, -1, -1));

        jLabel213.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel213.setText("序列号；");
        jPanel27.add(jLabel213, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 420, -1, -1));

        修改怪物爆物.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        修改怪物爆物.setText("修改");
        修改怪物爆物.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                修改怪物爆物ActionPerformed(evt);
            }
        });
        jPanel27.add(修改怪物爆物, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 480, 70, 30));

        刷新怪物爆物.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        刷新怪物爆物.setText("刷新怪物爆物");
        刷新怪物爆物.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                刷新怪物爆物ActionPerformed(evt);
            }
        });
        jPanel27.add(刷新怪物爆物, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 480, 140, 30));

        jLabel39.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel39.setText("物品名；");
        jPanel27.add(jLabel39, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 420, -1, -1));

        jPanel28.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "全局爆物/(10000=1%)", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("幼圆", 0, 18))); // NOI18N
        jPanel28.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        世界爆物.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        世界爆物.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "序列号", "物品代码", "爆率", "物品名"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        世界爆物.getTableHeader().setReorderingAllowed(false);
        jScrollPane8.setViewportView(世界爆物);

        jPanel28.add(jScrollPane8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 390, 380));

        世界爆物序列号.setEditable(false);
        世界爆物序列号.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        世界爆物序列号.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                世界爆物序列号ActionPerformed(evt);
            }
        });
        jPanel28.add(世界爆物序列号, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 440, 50, 30));

        世界爆物物品代码.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        世界爆物物品代码.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                世界爆物物品代码ActionPerformed(evt);
            }
        });
        jPanel28.add(世界爆物物品代码, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 440, 70, 30));

        世界爆物爆率.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        世界爆物爆率.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                世界爆物爆率ActionPerformed(evt);
            }
        });
        jPanel28.add(世界爆物爆率, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 440, 50, 30));

        添加世界爆物.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        添加世界爆物.setText("添加");
        添加世界爆物.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                添加世界爆物ActionPerformed(evt);
            }
        });
        jPanel28.add(添加世界爆物, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 480, 70, 30));

        删除世界爆物.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        删除世界爆物.setText("删除");
        删除世界爆物.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                删除世界爆物ActionPerformed(evt);
            }
        });
        jPanel28.add(删除世界爆物, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 480, 70, 30));

        jLabel210.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel210.setText("序列号；");
        jPanel28.add(jLabel210, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 420, -1, -1));

        jLabel202.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel202.setText("物品代码；");
        jPanel28.add(jLabel202, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 420, -1, 20));

        jLabel209.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel209.setText("爆率；");
        jPanel28.add(jLabel209, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 420, -1, -1));

        世界爆物名称.setEditable(false);
        世界爆物名称.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        世界爆物名称.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                世界爆物名称ActionPerformed(evt);
            }
        });
        jPanel28.add(世界爆物名称, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 440, 100, 30));

        jLabel40.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        jLabel40.setText("物品名；");
        jPanel28.add(jLabel40, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 420, -1, -1));

        修改世界爆物.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        修改世界爆物.setText("修改");
        修改世界爆物.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                修改世界爆物ActionPerformed(evt);
            }
        });
        jPanel28.add(修改世界爆物, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 480, 70, 30));

        刷新世界爆物.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        刷新世界爆物.setText("刷新世界爆物");
        刷新世界爆物.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                刷新世界爆物ActionPerformed(evt);
            }
        });
        jPanel28.add(刷新世界爆物, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 480, 140, 30));

        查询物品掉落.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        查询物品掉落.setText("查询物品掉落");
        查询物品掉落.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                查询物品掉落ActionPerformed(evt);
            }
        });

        查询物品掉落代码.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        查询物品掉落代码.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                查询物品掉落代码ActionPerformed(evt);
            }
        });

        查询怪物掉落代码.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N

        删除指定的掉落按键.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        删除指定的掉落按键.setText("删除指定掉落");
        删除指定的掉落按键.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                删除指定的掉落按键ActionPerformed(evt);
            }
        });

        删除指定的掉落.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N

        修改物品掉落持续时间.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        修改物品掉落持续时间.setText("修改确认");
        修改物品掉落持续时间.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                修改物品掉落持续时间ActionPerformed(evt);
            }
        });

        物品掉落持续时间.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N

        jLabel320.setFont(new java.awt.Font("幼圆", 0, 14)); // NOI18N
        jLabel320.setForeground(new java.awt.Color(255, 0, 0));
        jLabel320.setText("需要重启生效，建议 10000 ");

        刷新怪物卡片.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        刷新怪物卡片.setText("刷新卡片");
        刷新怪物卡片.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                刷新怪物卡片ActionPerformed(evt);
            }
        });

        地图物品上限.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N

        修改物品掉落持续时间1.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        修改物品掉落持续时间1.setText("修改确认");
        修改物品掉落持续时间1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                修改物品掉落持续时间1ActionPerformed(evt);
            }
        });

        jLabel319.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        jLabel319.setText("地图物品最多数量");

        jLabel324.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        jLabel324.setText("物品掉落持续时间");

        jLabel325.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        jLabel325.setText("地图刷新频率");

        地图刷新频率.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N

        修改物品掉落持续时间2.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        修改物品掉落持续时间2.setText("修改确认");
        修改物品掉落持续时间2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                修改物品掉落持续时间2ActionPerformed(evt);
            }
        });

        jLabel323.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        jLabel323.setText("指定物品查询掉落");

        jLabel316.setText("指定怪物查物品掉落");

        查询怪物掉落.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        查询怪物掉落.setText("查询怪物掉落");
        查询怪物掉落.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                查询怪物掉落ActionPerformed(evt);
            }
        });

        jLabel321.setFont(new java.awt.Font("幼圆", 0, 12)); // NOI18N
        jLabel321.setText("删除指定物品掉落");

        查询物品掉落1.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        查询物品掉落1.setText("确认修改");
        查询物品掉落1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                查询物品掉落1ActionPerformed(evt);
            }
        });

        查询怪物掉落1.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        查询怪物掉落1.setText("确认修改");
        查询怪物掉落1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                查询怪物掉落1ActionPerformed(evt);
            }
        });

        删除指定的掉落按键1.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        删除指定的掉落按键1.setText("删除物品");
        删除指定的掉落按键1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                删除指定的掉落按键1ActionPerformed(evt);
            }
        });

        修改物品掉落持续时间3.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        修改物品掉落持续时间3.setText("修改确认");
        修改物品掉落持续时间3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                修改物品掉落持续时间3ActionPerformed(evt);
            }
        });

        修改物品掉落持续时间4.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        修改物品掉落持续时间4.setText("修改确认");
        修改物品掉落持续时间4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                修改物品掉落持续时间4ActionPerformed(evt);
            }
        });

        修改物品掉落持续时间5.setFont(new java.awt.Font("幼圆", 0, 15)); // NOI18N
        修改物品掉落持续时间5.setText("修改确认");
        修改物品掉落持续时间5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                修改物品掉落持续时间5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel26Layout = new javax.swing.GroupLayout(jPanel26);
        jPanel26.setLayout(jPanel26Layout);
        jPanel26Layout.setHorizontalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel26Layout.createSequentialGroup()
                    .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel26Layout.createSequentialGroup()
                            .addGap(8, 8, 8)
                            .addComponent(查询物品掉落)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(查询怪物掉落, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel26Layout.createSequentialGroup()
                            .addGap(10, 10, 10)
                            .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel323, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(查询物品掉落代码)
                                .addComponent(查询物品掉落1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel26Layout.createSequentialGroup()
                                    .addGap(30, 30, 30)
                                    .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jLabel316, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(查询怪物掉落代码)))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel26Layout.createSequentialGroup()
                                    .addGap(27, 27, 27)
                                    .addComponent(查询怪物掉落1, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel26Layout.createSequentialGroup()
                            .addGap(10, 10, 10)
                            .addComponent(删除指定的掉落按键)
                            .addGap(22, 22, 22)
                            .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel26Layout.createSequentialGroup()
                                    .addComponent(修改物品掉落持续时间)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(修改物品掉落持续时间1, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(14, 14, 14)
                                    .addComponent(修改物品掉落持续时间2)
                                    .addGap(184, 184, 184))
                                .addGroup(jPanel26Layout.createSequentialGroup()
                                    .addGap(8, 8, 8)
                                    .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel26Layout.createSequentialGroup()
                                                .addComponent(物品掉落持续时间, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(地图物品上限, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(jPanel26Layout.createSequentialGroup()
                                                .addComponent(jLabel324)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabel319)))
                                        .addGroup(jPanel26Layout.createSequentialGroup()
                                            .addComponent(修改物品掉落持续时间3)
                                            .addGap(18, 18, 18)
                                            .addComponent(修改物品掉落持续时间4)))
                                    .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel26Layout.createSequentialGroup()
                                            .addGap(5, 5, 5)
                                            .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addComponent(地图刷新频率)
                                                .addComponent(jLabel325, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(刷新怪物卡片, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(22, 22, 22))
                                        .addGroup(jPanel26Layout.createSequentialGroup()
                                            .addGap(18, 18, 18)
                                            .addComponent(修改物品掉落持续时间5)
                                            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
                        .addGroup(jPanel26Layout.createSequentialGroup()
                            .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel26Layout.createSequentialGroup()
                                    .addGap(10, 10, 10)
                                    .addComponent(删除指定的掉落按键1, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(删除指定的掉落, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel26Layout.createSequentialGroup()
                                        .addGap(12, 12, 12)
                                        .addComponent(jLabel321))))
                            .addContainerGap())))
                .addGroup(jPanel26Layout.createSequentialGroup()
                    .addGap(12, 12, 12)
                    .addComponent(jLabel320)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel26Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel27, javax.swing.GroupLayout.PREFERRED_SIZE, 484, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel28, javax.swing.GroupLayout.PREFERRED_SIZE, 420, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel26Layout.setVerticalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel26Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel27, javax.swing.GroupLayout.PREFERRED_SIZE, 529, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel28, javax.swing.GroupLayout.PREFERRED_SIZE, 524, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel316)
                    .addComponent(jLabel323)
                    .addComponent(jLabel321)
                    .addComponent(jLabel324)
                    .addComponent(jLabel319)
                    .addComponent(jLabel325))
                .addGap(5, 5, 5)
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(查询物品掉落代码, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(查询怪物掉落代码, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(物品掉落持续时间, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(地图物品上限, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(删除指定的掉落, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(地图刷新频率, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(刷新怪物卡片, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(查询物品掉落1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(查询怪物掉落1)
                    .addComponent(删除指定的掉落按键1)
                    .addComponent(修改物品掉落持续时间3)
                    .addComponent(修改物品掉落持续时间4)
                    .addComponent(修改物品掉落持续时间5))
                .addGap(41, 41, 41)
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(查询物品掉落, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(查询怪物掉落)
                    .addComponent(删除指定的掉落按键)
                    .addComponent(修改物品掉落持续时间, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(修改物品掉落持续时间1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(修改物品掉落持续时间2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(90, 90, 90)
                .addComponent(jLabel320)
                .addGap(69, 69, 69))
        );

        getContentPane().add(jPanel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 920, 660));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void 删除怪物爆物ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_删除怪物爆物ActionPerformed
        PreparedStatement ps1 = null;
        ResultSet rs = null;

        boolean result = this.怪物爆物序列号.getText().matches("[0-9]+");
        if (result == true) {
            int 商城SN编码 = Integer.parseInt(this.怪物爆物序列号.getText());

            try {
                //清楚table数据
                for (int i = ((DefaultTableModel) (this.怪物爆物.getModel())).getRowCount() - 1; i >= 0; i--) {
                    ((DefaultTableModel) (this.怪物爆物.getModel())).removeRow(i);
                }
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM drop_data WHERE id = ?");
                ps1.setInt(1, 商城SN编码);
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlstr = " delete from drop_data where id =" + 商城SN编码 + "";
                    ps1.executeUpdate(sqlstr);
                    JOptionPane.showMessageDialog(null, "[信息]:删除爆物成功。");
                    刷新指定怪物爆物();
                }
            } catch (SQLException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_删除怪物爆物ActionPerformed

    private void 添加怪物爆物ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_添加怪物爆物ActionPerformed
        boolean result1 = this.怪物爆物怪物代码.getText().matches("[0-9]+");
        boolean result2 = this.怪物爆物物品代码.getText().matches("[0-9]+");
        boolean result3 = this.怪物爆物爆率.getText().matches("[0-9]+");
        if (result1 && result2 && result3) {
            if (Integer.parseInt(this.怪物爆物怪物代码.getText()) < 0 && Integer.parseInt(this.怪物爆物物品代码.getText()) < 0 && Integer.parseInt(this.怪物爆物爆率.getText()) < 0) {
                JOptionPane.showMessageDialog(null, "[信息]:请填写正确的值。");
                return;
            }
            try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement("INSERT INTO drop_data ( dropperid,itemid,minimum_quantity,maximum_quantity,chance) VALUES ( ?, ?, ?, ?, ?)")) {
                ps.setInt(1, Integer.parseInt(this.怪物爆物怪物代码.getText()));
                ps.setInt(2, Integer.parseInt(this.怪物爆物物品代码.getText()));
                ps.setInt(3, 1);
                ps.setInt(4, 1);
                ps.setInt(5, Integer.parseInt(this.怪物爆物爆率.getText()));
                ps.executeUpdate();
                JOptionPane.showMessageDialog(null, "[信息]:添加成功。");
            } catch (SQLException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "[信息]:请输入<怪物代码><物品代码><物品爆率>的格式来添加。");
        }
    }//GEN-LAST:event_添加怪物爆物ActionPerformed

    private void 修改怪物爆物ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_修改怪物爆物ActionPerformed
        boolean result1 = this.怪物爆物怪物代码.getText().matches("[0-9]+");
        boolean result2 = this.怪物爆物物品代码.getText().matches("[0-9]+");
        boolean result3 = this.怪物爆物爆率.getText().matches("[0-9]+");
        PreparedStatement ps = null;
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        if (result1 && result2 && result3) {
            try {
                ps = DatabaseConnection.getConnection().prepareStatement("UPDATE drop_data SET dropperid = ?, itemid = ?, chance = ? WHERE id = ?");
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM drop_data WHERE id = ?");
                ps1.setInt(1, Integer.parseInt(this.怪物爆物序列号.getText()));
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlString2 = null;
                    String sqlString3 = null;
                    String sqlString4 = null;
                    sqlString2 = "update drop_data set dropperid='" + this.怪物爆物怪物代码.getText() + "' where id=" + this.怪物爆物序列号.getText() + ";";
                    PreparedStatement dropperid = DatabaseConnection.getConnection().prepareStatement(sqlString2);
                    dropperid.executeUpdate(sqlString2);
                    sqlString3 = "update drop_data set itemid='" + this.怪物爆物物品代码.getText() + "' where id=" + this.怪物爆物序列号.getText() + ";";
                    PreparedStatement itemid = DatabaseConnection.getConnection().prepareStatement(sqlString3);
                    itemid.executeUpdate(sqlString3);
                    sqlString4 = "update drop_data set chance='" + this.怪物爆物爆率.getText() + "' where id=" + this.怪物爆物序列号.getText() + ";";
                    PreparedStatement chance = DatabaseConnection.getConnection().prepareStatement(sqlString4);
                    chance.executeUpdate(sqlString4);
                    JOptionPane.showMessageDialog(null, "[信息]:修改成功。");
                }
            } catch (SQLException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "[信息]:请选择你要修改的数据。");
        }
    }//GEN-LAST:event_修改怪物爆物ActionPerformed

    private void 刷新怪物爆物ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_刷新怪物爆物ActionPerformed
        JOptionPane.showMessageDialog(null, "[信息]:刷新怪物物品掉落数据。");
        刷新怪物爆物();
    }//GEN-LAST:event_刷新怪物爆物ActionPerformed

    private void 世界爆物序列号ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_世界爆物序列号ActionPerformed

    }//GEN-LAST:event_世界爆物序列号ActionPerformed

    private void 世界爆物物品代码ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_世界爆物物品代码ActionPerformed

    }//GEN-LAST:event_世界爆物物品代码ActionPerformed

    private void 世界爆物爆率ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_世界爆物爆率ActionPerformed

    }//GEN-LAST:event_世界爆物爆率ActionPerformed

    private void 添加世界爆物ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_添加世界爆物ActionPerformed
        boolean result1 = this.世界爆物物品代码.getText().matches("[0-9]+");
        boolean result2 = this.世界爆物爆率.getText().matches("[0-9]+");
        if (result1 && result2) {
            if (Integer.parseInt(this.世界爆物物品代码.getText()) < 0 && Integer.parseInt(this.世界爆物爆率.getText()) < 0) {
                JOptionPane.showMessageDialog(null, "[信息]:请填写正确的值。");
                return;
            }
            PreparedStatement ps1 = null;
            ResultSet rs = null;
            try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement("INSERT INTO drop_data_global (continent,dropType,itemid,minimum_quantity,maximum_quantity,questid,chance) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                ps.setInt(1, 1);
                ps.setInt(2, 1);
                ps.setInt(3, Integer.parseInt(this.世界爆物物品代码.getText()));
                ps.setInt(4, 1);
                ps.setInt(5, 1);
                ps.setInt(6, 0);
                ps.setInt(7, Integer.parseInt(this.世界爆物爆率.getText()));
                ps.executeUpdate();
                JOptionPane.showMessageDialog(null, "[信息]:世界爆物添加成功。");
                刷新世界爆物();
            } catch (SQLException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "[信息]:请输入<物品代码>，<物品爆率> 。");
        }
    }//GEN-LAST:event_添加世界爆物ActionPerformed

    private void 删除世界爆物ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_删除世界爆物ActionPerformed
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        boolean result = this.世界爆物序列号.getText().matches("[0-9]+");
        if (result == true) {
            int 商城SN编码 = Integer.parseInt(this.世界爆物序列号.getText());
            try {
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM drop_data_global WHERE id = ?");
                ps1.setInt(1, 商城SN编码);
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlstr = " delete from drop_data_global where id =" + 商城SN编码 + "";
                    ps1.executeUpdate(sqlstr);
                    JOptionPane.showMessageDialog(null, "[信息]:删除成功。");
                    刷新世界爆物();
                }
            } catch (SQLException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "[信息]:请选择你要删除的物品。");
        }
    }//GEN-LAST:event_删除世界爆物ActionPerformed

    private void 世界爆物名称ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_世界爆物名称ActionPerformed

    }//GEN-LAST:event_世界爆物名称ActionPerformed

    private void 修改世界爆物ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_修改世界爆物ActionPerformed

        boolean result2 = this.世界爆物物品代码.getText().matches("[0-9]+");
        boolean result3 = this.世界爆物爆率.getText().matches("[0-9]+");
        PreparedStatement ps = null;
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        if (result2 && result3) {
            try {
                ps = DatabaseConnection.getConnection().prepareStatement("UPDATE drop_data_global SET dropperid = ?, itemid = ?, chance = ? WHERE id = ?");
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM drop_data_global WHERE id = ?");
                ps1.setInt(1, Integer.parseInt(this.世界爆物序列号.getText()));
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlString2 = null;
                    String sqlString3 = null;
                    sqlString2 = "update drop_data_global set itemid='" + this.世界爆物物品代码.getText() + "' where id=" + this.世界爆物序列号.getText() + ";";
                    PreparedStatement dropperid = DatabaseConnection.getConnection().prepareStatement(sqlString2);
                    dropperid.executeUpdate(sqlString2);
                    sqlString3 = "update drop_data_global set chance='" + this.世界爆物爆率.getText() + "' where id=" + this.世界爆物序列号.getText() + ";";
                    PreparedStatement itemid = DatabaseConnection.getConnection().prepareStatement(sqlString3);
                    itemid.executeUpdate(sqlString3);
                    JOptionPane.showMessageDialog(null, "[信息]:修改成功。");
                    刷新世界爆物();
                }
            } catch (SQLException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "[信息]:请选择你要修改的数据。");
        }
    }//GEN-LAST:event_修改世界爆物ActionPerformed

    private void 刷新世界爆物ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_刷新世界爆物ActionPerformed
        JOptionPane.showMessageDialog(null, "[信息]:刷新世界物品掉落数据。");
        刷新世界爆物();
    }//GEN-LAST:event_刷新世界爆物ActionPerformed

    private void 查询物品掉落ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_查询物品掉落ActionPerformed
        boolean result = this.查询物品掉落代码.getText().matches("[0-9]+");
        if (result) {
            if (Integer.parseInt(this.查询物品掉落代码.getText()) < 0) {
                JOptionPane.showMessageDialog(null, "[信息]:请填写正确的值。");
                return;
            }
            for (int i = ((DefaultTableModel) (this.怪物爆物.getModel())).getRowCount() - 1; i >= 0; i--) {
                ((DefaultTableModel) (this.怪物爆物.getModel())).removeRow(i);
            }
            try {
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = null;

                ResultSet rs = null;
                ps = con.prepareStatement("SELECT * FROM drop_data WHERE itemid =  " + Integer.parseInt(this.查询物品掉落代码.getText()) + "");
                rs = ps.executeQuery();
                while (rs.next()) {

                    ((DefaultTableModel) 怪物爆物.getModel()).insertRow(怪物爆物.getRowCount(), new Object[]{
                        rs.getInt("id"),
                        rs.getInt("dropperid"),
                        rs.getInt("itemid"),
                        rs.getInt("chance"),
                        MapleItemInformationProvider.getInstance().getName(rs.getInt("itemid"))
                    });
                }
            } catch (SQLException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }
            怪物爆物.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    int i = 怪物爆物.getSelectedRow();
                    String a = 怪物爆物.getValueAt(i, 0).toString();
                    String a1 = 怪物爆物.getValueAt(i, 1).toString();
                    String a2 = 怪物爆物.getValueAt(i, 2).toString();
                    String a3 = 怪物爆物.getValueAt(i, 3).toString();
                    //String a4 = 怪物爆物.getValueAt(i, 4).toString();
                    怪物爆物序列号.setText(a);
                    怪物爆物怪物代码.setText(a1);
                    怪物爆物物品代码.setText(a2);
                    怪物爆物爆率.setText(a3);
                }
            });
        } else {
            JOptionPane.showMessageDialog(null, "[信息]:请输入你要查找的物品代码。");
        }
    }//GEN-LAST:event_查询物品掉落ActionPerformed

    private void 查询物品掉落代码ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_查询物品掉落代码ActionPerformed

    }//GEN-LAST:event_查询物品掉落代码ActionPerformed

    private void 删除指定的掉落按键ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_删除指定的掉落按键ActionPerformed
        String 输出 = "";
        PreparedStatement ps1 = null;
        ResultSet rs = null;

        boolean result = this.删除指定的掉落.getText().matches("[0-9]+");
        if (result == true) {
            int 商城SN编码 = Integer.parseInt(this.删除指定的掉落.getText());
            try {
                // for (int i = ((DefaultTableModel) (this.怪物爆物.getModel())).getRowCount() - 1; i >= 0; i--) {
                    //   ((DefaultTableModel) (this.怪物爆物.getModel())).removeRow(i);
                    //}
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM drop_data WHERE itemid = ?");
                ps1.setInt(1, 商城SN编码);
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlstr = " delete from drop_data where itemid =" + 商城SN编码 + "";
                    ps1.executeUpdate(sqlstr);
                    JOptionPane.showMessageDialog(null, "[信息]:成功删除 " + 商城SN编码 + " 物品。");
                }
            } catch (SQLException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }
            刷新怪物爆物();
        } else {
            JOptionPane.showMessageDialog(null, "[信息]:请输入你要查找的物品代码。");
        }
    }//GEN-LAST:event_删除指定的掉落按键ActionPerformed

    private void 修改物品掉落持续时间ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_修改物品掉落持续时间ActionPerformed
        boolean result2 = this.物品掉落持续时间.getText().matches("[0-9]+");
        PreparedStatement ps = null;
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        if (result2) {
            try {
                ps = DatabaseConnection.getConnection().prepareStatement("UPDATE configvalues SET Val = ? WHERE id = ?");
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM configvalues WHERE id = ?");
                ps1.setInt(1, 998);
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlString2 = null;
                    sqlString2 = "update configvalues set Val='" + this.物品掉落持续时间.getText() + "' where id = 998;";
                    PreparedStatement dropperid = DatabaseConnection.getConnection().prepareStatement(sqlString2);
                    dropperid.executeUpdate(sqlString2);
                    ZeroMS_UI.GetConfigValues();
                    刷新物品掉落持续时间();
                    JOptionPane.showMessageDialog(null, "[信息]:修改成功。");
                }
            } catch (SQLException ex) {
                Logger.getLogger(ZeroMS_UI.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "[信息]:请输入你要修改的数据。");
        }
    }//GEN-LAST:event_修改物品掉落持续时间ActionPerformed

    private void 刷新怪物卡片ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_刷新怪物卡片ActionPerformed
        刷新怪物卡片();
    }//GEN-LAST:event_刷新怪物卡片ActionPerformed

    private void 修改物品掉落持续时间1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_修改物品掉落持续时间1ActionPerformed
        boolean result2 = this.地图物品上限.getText().matches("[0-9]+");
        PreparedStatement ps = null;
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        if (result2) {
            try {
                ps = DatabaseConnection.getConnection().prepareStatement("UPDATE configvalues SET Val = ? WHERE id = ?");
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM configvalues WHERE id = ?");
                ps1.setInt(1, 997);
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlString2 = null;
                    sqlString2 = "update configvalues set Val='" + this.地图物品上限.getText() + "' where id = 997;";
                    PreparedStatement dropperid = DatabaseConnection.getConnection().prepareStatement(sqlString2);
                    dropperid.executeUpdate(sqlString2);
                    ZeroMS_UI.GetConfigValues();
                    刷新地图物品上限();
                    JOptionPane.showMessageDialog(null, "[信息]:修改成功。");
                }
            } catch (SQLException ex) {
                Logger.getLogger(ZeroMS_UI.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "[信息]:请输入你要修改的数据。");
        }
    }//GEN-LAST:event_修改物品掉落持续时间1ActionPerformed

    private void 修改物品掉落持续时间2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_修改物品掉落持续时间2ActionPerformed
        boolean result2 = this.地图刷新频率.getText().matches("[0-9]+");
        PreparedStatement ps = null;
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        if (result2) {
            try {
                ps = DatabaseConnection.getConnection().prepareStatement("UPDATE configvalues SET Val = ? WHERE id = ?");
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM configvalues WHERE id = ?");
                ps1.setInt(1, 996);
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlString2 = null;
                    sqlString2 = "update configvalues set Val='" + this.地图刷新频率.getText() + "' where id = 996;";
                    PreparedStatement dropperid = DatabaseConnection.getConnection().prepareStatement(sqlString2);
                    dropperid.executeUpdate(sqlString2);
                    ZeroMS_UI.GetConfigValues();
                    刷新地图刷新频率();
                    JOptionPane.showMessageDialog(null, "[信息]:修改成功。");
                }
            } catch (SQLException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "[信息]:请输入你要修改的数据。");
        }
    }//GEN-LAST:event_修改物品掉落持续时间2ActionPerformed

    private void 查询怪物掉落ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_查询怪物掉落ActionPerformed
        boolean result = this.查询怪物掉落代码.getText().matches("[0-9]+");
        if (result) {
            if (Integer.parseInt(this.查询怪物掉落代码.getText()) < 0) {
                JOptionPane.showMessageDialog(null, "[信息]:请填写正确的值。");
                return;
            }
            for (int i = ((DefaultTableModel) (this.怪物爆物.getModel())).getRowCount() - 1; i >= 0; i--) {
                ((DefaultTableModel) (this.怪物爆物.getModel())).removeRow(i);
            }
            try {
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = null;

                ResultSet rs = null;
                ps = con.prepareStatement("SELECT * FROM drop_data WHERE dropperid =  " + Integer.parseInt(this.查询怪物掉落代码.getText()) + " && itemid !=0");//&& itemid !=0
                rs = ps.executeQuery();
                while (rs.next()) {

                    ((DefaultTableModel) 怪物爆物.getModel()).insertRow(怪物爆物.getRowCount(), new Object[]{
                        rs.getInt("id"),
                        rs.getInt("dropperid"),
                        rs.getInt("itemid"),
                        rs.getInt("chance"),
                        MapleItemInformationProvider.getInstance().getName(rs.getInt("itemid"))
                    });
                }
            } catch (SQLException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }
            怪物爆物.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    int i = 怪物爆物.getSelectedRow();
                    String a = 怪物爆物.getValueAt(i, 0).toString();
                    String a1 = 怪物爆物.getValueAt(i, 1).toString();
                    String a2 = 怪物爆物.getValueAt(i, 2).toString();
                    String a3 = 怪物爆物.getValueAt(i, 3).toString();
                    //String a4 = 怪物爆物.getValueAt(i, 4).toString();
                    怪物爆物序列号.setText(a);
                    怪物爆物怪物代码.setText(a1);
                    怪物爆物物品代码.setText(a2);
                    怪物爆物爆率.setText(a3);
                    //怪物爆物物品名称.setText(a4);

                }
            });
        } else {
            JOptionPane.showMessageDialog(null, "[信息]:请输入你要查找的怪物代码。");
        }
    }//GEN-LAST:event_查询怪物掉落ActionPerformed

    private void 查询物品掉落1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_查询物品掉落1ActionPerformed
        boolean result = this.查询物品掉落代码.getText().matches("[0-9]+");
        if (result) {
            if (Integer.parseInt(this.查询物品掉落代码.getText()) < 0) {
                JOptionPane.showMessageDialog(null, "[信息]:请填写正确的值。");
                return;
            }
            for (int i = ((DefaultTableModel) (this.怪物爆物.getModel())).getRowCount() - 1; i >= 0; i--) {
                ((DefaultTableModel) (this.怪物爆物.getModel())).removeRow(i);
            }
            try {
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = null;

                ResultSet rs = null;
                ps = con.prepareStatement("SELECT * FROM drop_data WHERE itemid =  " + Integer.parseInt(this.查询物品掉落代码.getText()) + "");
                rs = ps.executeQuery();
                while (rs.next()) {

                    ((DefaultTableModel) 怪物爆物.getModel()).insertRow(怪物爆物.getRowCount(), new Object[]{
                        rs.getInt("id"),
                        rs.getInt("dropperid"),
                        rs.getInt("itemid"),
                        rs.getInt("chance"),
                        MapleItemInformationProvider.getInstance().getName(rs.getInt("itemid"))
                    });
                }
            } catch (SQLException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }
            怪物爆物.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    int i = 怪物爆物.getSelectedRow();
                    String a = 怪物爆物.getValueAt(i, 0).toString();
                    String a1 = 怪物爆物.getValueAt(i, 1).toString();
                    String a2 = 怪物爆物.getValueAt(i, 2).toString();
                    String a3 = 怪物爆物.getValueAt(i, 3).toString();
                    //String a4 = 怪物爆物.getValueAt(i, 4).toString();
                    怪物爆物序列号.setText(a);
                    怪物爆物怪物代码.setText(a1);
                    怪物爆物物品代码.setText(a2);
                    怪物爆物爆率.setText(a3);
                }
            });
        } else {
            JOptionPane.showMessageDialog(null, "[信息]:请输入你要查找的物品代码。");
        }
    }//GEN-LAST:event_查询物品掉落1ActionPerformed

    private void 查询怪物掉落1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_查询怪物掉落1ActionPerformed
        boolean result = this.查询怪物掉落代码.getText().matches("[0-9]+");
        if (result) {
            if (Integer.parseInt(this.查询怪物掉落代码.getText()) < 0) {
                JOptionPane.showMessageDialog(null, "[信息]:请填写正确的值。");
                return;
            }
            for (int i = ((DefaultTableModel) (this.怪物爆物.getModel())).getRowCount() - 1; i >= 0; i--) {
                ((DefaultTableModel) (this.怪物爆物.getModel())).removeRow(i);
            }
            try {
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = null;

                ResultSet rs = null;
                ps = con.prepareStatement("SELECT * FROM drop_data WHERE dropperid =  " + Integer.parseInt(this.查询怪物掉落代码.getText()) + " && itemid !=0");//&& itemid !=0
                rs = ps.executeQuery();
                while (rs.next()) {

                    ((DefaultTableModel) 怪物爆物.getModel()).insertRow(怪物爆物.getRowCount(), new Object[]{
                        rs.getInt("id"),
                        rs.getInt("dropperid"),
                        rs.getInt("itemid"),
                        rs.getInt("chance"),
                        MapleItemInformationProvider.getInstance().getName(rs.getInt("itemid"))
                    });
                }
            } catch (SQLException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }
            怪物爆物.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    int i = 怪物爆物.getSelectedRow();
                    String a = 怪物爆物.getValueAt(i, 0).toString();
                    String a1 = 怪物爆物.getValueAt(i, 1).toString();
                    String a2 = 怪物爆物.getValueAt(i, 2).toString();
                    String a3 = 怪物爆物.getValueAt(i, 3).toString();
                    //String a4 = 怪物爆物.getValueAt(i, 4).toString();
                    怪物爆物序列号.setText(a);
                    怪物爆物怪物代码.setText(a1);
                    怪物爆物物品代码.setText(a2);
                    怪物爆物爆率.setText(a3);
                    //怪物爆物物品名称.setText(a4);

                }
            });
        } else {
            JOptionPane.showMessageDialog(null, "[信息]:请输入你要查找的怪物代码。");
        }
    }//GEN-LAST:event_查询怪物掉落1ActionPerformed

    private void 删除指定的掉落按键1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_删除指定的掉落按键1ActionPerformed
        String 输出 = "";
        PreparedStatement ps1 = null;
        ResultSet rs = null;

        boolean result = this.删除指定的掉落.getText().matches("[0-9]+");
        if (result == true) {
            int 商城SN编码 = Integer.parseInt(this.删除指定的掉落.getText());
            try {
                // for (int i = ((DefaultTableModel) (this.怪物爆物.getModel())).getRowCount() - 1; i >= 0; i--) {
                    //   ((DefaultTableModel) (this.怪物爆物.getModel())).removeRow(i);
                    //}
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM drop_data WHERE itemid = ?");
                ps1.setInt(1, 商城SN编码);
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlstr = " delete from drop_data where itemid =" + 商城SN编码 + "";
                    ps1.executeUpdate(sqlstr);
                    JOptionPane.showMessageDialog(null, "[信息]:成功删除 " + 商城SN编码 + " 物品。");
                }
            } catch (SQLException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }
            刷新怪物爆物();
        } else {
            JOptionPane.showMessageDialog(null, "[信息]:请输入你要查找的物品代码。");
        }
    }//GEN-LAST:event_删除指定的掉落按键1ActionPerformed

    private void 修改物品掉落持续时间3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_修改物品掉落持续时间3ActionPerformed
        boolean result2 = this.物品掉落持续时间.getText().matches("[0-9]+");
        PreparedStatement ps = null;
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        if (result2) {
            try {
                ps = DatabaseConnection.getConnection().prepareStatement("UPDATE configvalues SET Val = ? WHERE id = ?");
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM configvalues WHERE id = ?");
                ps1.setInt(1, 998);
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlString2 = null;
                    sqlString2 = "update configvalues set Val='" + this.物品掉落持续时间.getText() + "' where id = 998;";
                    PreparedStatement dropperid = DatabaseConnection.getConnection().prepareStatement(sqlString2);
                    dropperid.executeUpdate(sqlString2);
                    ZeroMS_UI.GetConfigValues();
                    刷新物品掉落持续时间();
                    JOptionPane.showMessageDialog(null, "[信息]:修改成功。");
                }
            } catch (SQLException ex) {
                Logger.getLogger(ZeroMS_UI.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "[信息]:请输入你要修改的数据。");
        }
    }//GEN-LAST:event_修改物品掉落持续时间3ActionPerformed

    private void 修改物品掉落持续时间4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_修改物品掉落持续时间4ActionPerformed
        boolean result2 = this.地图物品上限.getText().matches("[0-9]+");
        PreparedStatement ps = null;
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        if (result2) {
            try {
                ps = DatabaseConnection.getConnection().prepareStatement("UPDATE configvalues SET Val = ? WHERE id = ?");
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM configvalues WHERE id = ?");
                ps1.setInt(1, 997);
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlString2 = null;
                    sqlString2 = "update configvalues set Val='" + this.地图物品上限.getText() + "' where id = 997;";
                    PreparedStatement dropperid = DatabaseConnection.getConnection().prepareStatement(sqlString2);
                    dropperid.executeUpdate(sqlString2);
                    ZeroMS_UI.GetConfigValues();
                    刷新地图物品上限();
                    JOptionPane.showMessageDialog(null, "[信息]:修改成功。");
                }
            } catch (SQLException ex) {
                Logger.getLogger(ZeroMS_UI.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "[信息]:请输入你要修改的数据。");
        }
    }//GEN-LAST:event_修改物品掉落持续时间4ActionPerformed

    private void 修改物品掉落持续时间5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_修改物品掉落持续时间5ActionPerformed
        boolean result2 = this.地图刷新频率.getText().matches("[0-9]+");
        PreparedStatement ps = null;
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        if (result2) {
            try {
                ps = DatabaseConnection.getConnection().prepareStatement("UPDATE configvalues SET Val = ? WHERE id = ?");
                ps1 = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM configvalues WHERE id = ?");
                ps1.setInt(1, 996);
                rs = ps1.executeQuery();
                if (rs.next()) {
                    String sqlString2 = null;
                    sqlString2 = "update configvalues set Val='" + this.地图刷新频率.getText() + "' where id = 996;";
                    PreparedStatement dropperid = DatabaseConnection.getConnection().prepareStatement(sqlString2);
                    dropperid.executeUpdate(sqlString2);
                    ZeroMS_UI.GetConfigValues();
                    刷新地图刷新频率();
                    JOptionPane.showMessageDialog(null, "[信息]:修改成功。");
                }
            } catch (SQLException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "[信息]:请输入你要修改的数据。");
        }
    }//GEN-LAST:event_修改物品掉落持续时间5ActionPerformed
    public void 刷新世界爆物() {

        for (int i = ((DefaultTableModel) (this.世界爆物.getModel())).getRowCount() - 1; i >= 0; i--) {
            ((DefaultTableModel) (this.世界爆物.getModel())).removeRow(i);
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = null;

            ResultSet rs = null;
            ps = con.prepareStatement("SELECT * FROM drop_data_global WHERE itemid !=0");
            rs = ps.executeQuery();
            while (rs.next()) {

                ((DefaultTableModel) 世界爆物.getModel()).insertRow(世界爆物.getRowCount(), new Object[]{
                    rs.getInt("id"),
                    rs.getInt("itemid"),
                    rs.getString("chance"),
                    MapleItemInformationProvider.getInstance().getName(rs.getInt("itemid"))
                });
            }
            世界爆物.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    int i = 世界爆物.getSelectedRow();
                    String a = 世界爆物.getValueAt(i, 0).toString();
                    String a1 = 世界爆物.getValueAt(i, 1).toString();
                    String a2 = 世界爆物.getValueAt(i, 2).toString();
                    世界爆物序列号.setText(a);
                    世界爆物物品代码.setText(a1);
                    世界爆物爆率.setText(a2);
                }
            });

        } catch (SQLException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void 刷新指定怪物爆物() {
        boolean result = this.查询怪物掉落代码.getText().matches("[0-9]+");
        if (result) {
            if (Integer.parseInt(this.查询怪物掉落代码.getText()) < 0) {
                JOptionPane.showMessageDialog(null, "请填写正确的值");
            }
            for (int i = ((DefaultTableModel) (this.怪物爆物.getModel())).getRowCount() - 1; i >= 0; i--) {
                ((DefaultTableModel) (this.怪物爆物.getModel())).removeRow(i);
            }
            try {
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = null;

                ResultSet rs = null;
                ps = con.prepareStatement("SELECT * FROM drop_data WHERE dropperid =  " + Integer.parseInt(this.怪物爆物怪物代码.getText()) + "");
                rs = ps.executeQuery();
                while (rs.next()) {
                    ((DefaultTableModel) 怪物爆物.getModel()).insertRow(怪物爆物.getRowCount(), new Object[]{rs.getInt("id"), rs.getInt("dropperid"), rs.getInt("itemid"), rs.getInt("chance"), MapleItemInformationProvider.getInstance().getName(rs.getInt("itemid"))});
                }
            } catch (SQLException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }
            怪物爆物.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    int i = 怪物爆物.getSelectedRow();
                    String a = 怪物爆物.getValueAt(i, 0).toString();
                    String a1 = 怪物爆物.getValueAt(i, 1).toString();
                    String a2 = 怪物爆物.getValueAt(i, 2).toString();
                    String a3 = 怪物爆物.getValueAt(i, 3).toString();
                    String a4 = 怪物爆物.getValueAt(i, 4).toString();
                    怪物爆物序列号.setText(a);
                    怪物爆物怪物代码.setText(a1);
                    怪物爆物物品代码.setText(a2);
                    怪物爆物爆率.setText(a3);
                    怪物爆物物品名称.setText(a4);
                }
            });
        } else {
            JOptionPane.showMessageDialog(null, "请输入要查询的怪物代码 ");
        }
    }

    public void 刷新怪物爆物() {
        for (int i = ((DefaultTableModel) (this.怪物爆物.getModel())).getRowCount() - 1; i >= 0; i--) {
            ((DefaultTableModel) (this.怪物爆物.getModel())).removeRow(i);
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = null;
            ResultSet rs = null;
            ps = con.prepareStatement("SELECT * FROM drop_data WHERE itemid !=0");
            rs = ps.executeQuery();
            while (rs.next()) {
                ((DefaultTableModel) 怪物爆物.getModel()).insertRow(怪物爆物.getRowCount(), new Object[]{
                    rs.getInt("id"),
                    rs.getInt("dropperid"),
                    //MapleLifeFactory.getMonster(rs.getInt("dropperid")),
                    rs.getInt("itemid"),
                    rs.getInt("chance"),
                    MapleItemInformationProvider.getInstance().getName(rs.getInt("itemid"))
                });
            }
        } catch (SQLException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }
        怪物爆物.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int i = 怪物爆物.getSelectedRow();
                String a = 怪物爆物.getValueAt(i, 0).toString();
                String a1 = 怪物爆物.getValueAt(i, 1).toString();
                String a2 = 怪物爆物.getValueAt(i, 2).toString();
                String a3 = 怪物爆物.getValueAt(i, 3).toString();
                //String a4 = 怪物爆物.getValueAt(i, 4).toString();
                怪物爆物序列号.setText(a);
                怪物爆物怪物代码.setText(a1);
                怪物爆物物品代码.setText(a2);
                怪物爆物爆率.setText(a3);
                //怪物爆物物品名称.setText(a4);

            }
        });
    }

    public void 刷新怪物卡片() {
        for (int i = ((DefaultTableModel) (this.怪物爆物.getModel())).getRowCount() - 1; i >= 0; i--) {
            ((DefaultTableModel) (this.怪物爆物.getModel())).removeRow(i);
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = null;
            ResultSet rs = null;
            ps = con.prepareStatement("SELECT * FROM drop_data WHERE itemid >=2380000&& itemid <2390000");
            rs = ps.executeQuery();
            while (rs.next()) {
                ((DefaultTableModel) 怪物爆物.getModel()).insertRow(怪物爆物.getRowCount(), new Object[]{
                    rs.getInt("id"),
                    rs.getInt("dropperid"),
                    //MapleLifeFactory.getMonster(rs.getInt("dropperid")),
                    rs.getInt("itemid"),
                    rs.getInt("chance"),
                    MapleItemInformationProvider.getInstance().getName(rs.getInt("itemid"))
                });
            }
        } catch (SQLException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }
        怪物爆物.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int i = 怪物爆物.getSelectedRow();
                String a = 怪物爆物.getValueAt(i, 0).toString();
                String a1 = 怪物爆物.getValueAt(i, 1).toString();
                String a2 = 怪物爆物.getValueAt(i, 2).toString();
                String a3 = 怪物爆物.getValueAt(i, 3).toString();
                //String a4 = 怪物爆物.getValueAt(i, 4).toString();
                怪物爆物序列号.setText(a);
                怪物爆物怪物代码.setText(a1);
                怪物爆物物品代码.setText(a2);
                怪物爆物爆率.setText(a3);
                //怪物爆物物品名称.setText(a4);
            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel120;
    private javax.swing.JLabel jLabel202;
    private javax.swing.JLabel jLabel209;
    private javax.swing.JLabel jLabel210;
    private javax.swing.JLabel jLabel211;
    private javax.swing.JLabel jLabel212;
    private javax.swing.JLabel jLabel213;
    private javax.swing.JLabel jLabel316;
    private javax.swing.JLabel jLabel319;
    private javax.swing.JLabel jLabel320;
    private javax.swing.JLabel jLabel321;
    private javax.swing.JLabel jLabel323;
    private javax.swing.JLabel jLabel324;
    private javax.swing.JLabel jLabel325;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel28;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JTable 世界爆物;
    private javax.swing.JTextField 世界爆物名称;
    private javax.swing.JTextField 世界爆物序列号;
    private javax.swing.JTextField 世界爆物爆率;
    private javax.swing.JTextField 世界爆物物品代码;
    private javax.swing.JButton 修改世界爆物;
    private javax.swing.JButton 修改怪物爆物;
    private javax.swing.JButton 修改物品掉落持续时间;
    private javax.swing.JButton 修改物品掉落持续时间1;
    private javax.swing.JButton 修改物品掉落持续时间2;
    private javax.swing.JButton 修改物品掉落持续时间3;
    private javax.swing.JButton 修改物品掉落持续时间4;
    private javax.swing.JButton 修改物品掉落持续时间5;
    private javax.swing.JButton 删除世界爆物;
    private javax.swing.JButton 删除怪物爆物;
    private javax.swing.JTextField 删除指定的掉落;
    private javax.swing.JButton 删除指定的掉落按键;
    private javax.swing.JButton 删除指定的掉落按键1;
    private javax.swing.JButton 刷新世界爆物;
    private javax.swing.JButton 刷新怪物卡片;
    private javax.swing.JButton 刷新怪物爆物;
    private javax.swing.JTextField 地图刷新频率;
    private javax.swing.JTextField 地图物品上限;
    private javax.swing.JTable 怪物爆物;
    private javax.swing.JTextField 怪物爆物序列号;
    private javax.swing.JTextField 怪物爆物怪物代码;
    private javax.swing.JTextField 怪物爆物爆率;
    private javax.swing.JTextField 怪物爆物物品代码;
    private javax.swing.JTextField 怪物爆物物品名称;
    private javax.swing.JButton 查询怪物掉落;
    private javax.swing.JButton 查询怪物掉落1;
    private javax.swing.JTextField 查询怪物掉落代码;
    private javax.swing.JButton 查询物品掉落;
    private javax.swing.JButton 查询物品掉落1;
    private javax.swing.JTextField 查询物品掉落代码;
    private javax.swing.JButton 添加世界爆物;
    private javax.swing.JButton 添加怪物爆物;
    private javax.swing.JTextField 物品掉落持续时间;
    // End of variables declaration//GEN-END:variables
}

package merchant;

import client.MapleCharacter;
import database.DBConPool;
import tools.FileoutputUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class merchant_main {
    private static merchant_main instance = new merchant_main();
    private List<goods_model> goods_list = new LinkedList<goods_model>();
    private List<eqs_model> eq_list = new LinkedList<eqs_model>();
    private boolean close = false;

    public static merchant_main getInstance(){
        return instance;
    }

    public void add_good(MapleCharacter chr, int good_id, int good_num, int good_price, long createData){
        goods_list.add(new goods_model(chr.getAccountID(), good_id, good_num, good_price, chr.getName(), createData));
        FileoutputUtil.logToFile( "日志/商人交易系统/上架物品_goods.txt", " 上架物品ID： " + good_id + " 上架数量： " + good_num + " 价格： " + good_price + "");
    }

    public void add_eq(MapleCharacter chr, int upgradeslots, int level, int str, int dex, int int_, int luk, int hp, int mp, int watk, int matk, int wdef, int mdef, int acc, int avoid, int hands, int speed, int jump, byte viciousHammer, int itemEXP, int durability, byte enhance, short potential1, short potential2, short potential3, short hpR, short mpR, int good_id, int good_price) {
        eqs_model add_eq = new eqs_model(upgradeslots, level, str, dex, int_, luk, hp, mp, watk, matk, wdef, mdef, acc, avoid, hands, speed, jump, viciousHammer, itemEXP, durability, enhance, potential1, potential2, potential3, hpR, mpR, chr.getAccountID(), good_id, good_price, chr.getName(), System.currentTimeMillis(), 1);
        eq_list.add(add_eq);
        FileoutputUtil.logToFile( "日志/商人交易系统/上架物品_eqs.txt", add_eq.toString());
    }

//    public boolean gain_good_num(MapleCharacter chr, int goods_list_index, int good_num){
//        if (goods_list_index < 0 || goods_list_index > goods_list.size()){
//            // index不合法
//            chr.dropMessage(5, "goods_list查询index非法,请联系管理员处理!");
//            return false;
//        }
//        goods_model good = goods_list.get(goods_list_index);
//        int update_num = good.getGood_num() + good_num;
//        if (update_num < 0){
//            // 更改数量不合法
//            chr.dropMessage(5, "货物数量修改失败,请重新尝试!");
//            return false;
//        }
//        good.setGood_num(update_num);
//        goods_list.set(goods_list_index, good);
//        return true;
//    }

    public List<goods_model> getGoods_list() {
        return goods_list;
    }

    public List<eqs_model> getEqs_list() {
        return eq_list;
    }

    public void save_data(){
        try (Connection con = DBConPool.getInstance().getDataSource().getConnection()){
            PreparedStatement ps = con.prepareStatement("Truncate Table merchant");
            ps.executeUpdate();
            for (int i = 0; i < goods_list.size(); i++) {
                if (goods_list.get(i).getGood_num() == 0){
                    continue;
                }
                ps = con.prepareStatement("insert into merchant (acc_id, good_id, good_num, good_price, characters_name, createdata) VALUES (?, ?, ?, ?, ?, ?)");
                ps.setInt(1, goods_list.get(i).getAcc_id());
                ps.setInt(2, goods_list.get(i).getGood_id());
                ps.setInt(3, goods_list.get(i).getGood_num());
                ps.setInt(4, goods_list.get(i).getGood_price());
                ps.setString(5, goods_list.get(i).getCharacters_name());
                ps.setLong(6, goods_list.get(i).getCreateData());
                ps.executeUpdate();
            }
            ps.close();
            goods_list.clear();
            PreparedStatement ps1 = con.prepareStatement("Truncate Table merchantEquip");
            ps1.executeUpdate();
            for (int i = 0; i < goods_list.size(); i++) {
                if (goods_list.get(i).getGood_num() == 0){
                    continue;
                }
                ps1 = con.prepareStatement("insert into merchantEquip ('upgradeslots', 'level', 'str', 'dex', 'int', 'luk', 'hp', 'mp', 'watk', 'matk', 'wdef', 'mdef', 'acc', 'avoid', 'hands', 'speed', 'jump', 'ViciousHammer', 'itemEXP', 'durability', 'enhance', 'potential1', 'potential2', 'potential3', 'hpR', 'mpR', 'acc_id', 'good_id', 'good_price', 'characters_name', 'createData', 'good_num') VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                ps1.setInt(1, eq_list.get(i).getUpgradeslots());
                ps1.setInt(2, eq_list.get(i).getLevel());
                ps1.setInt(3, eq_list.get(i).getStr());
                ps1.setInt(4, eq_list.get(i).getDex());
                ps1.setInt(5, eq_list.get(i).getInt_());
                ps1.setInt(6, eq_list.get(i).getLuk());
                ps1.setInt(7, eq_list.get(i).getHp());
                ps1.setInt(8, eq_list.get(i).getMp());
                ps1.setInt(9, eq_list.get(i).getWatk());
                ps1.setInt(10, eq_list.get(i).getMatk());
                ps1.setInt(11, eq_list.get(i).getWdef());
                ps1.setInt(12, eq_list.get(i).getMdef());
                ps1.setInt(13, eq_list.get(i).getAcc());
                ps1.setInt(14, eq_list.get(i).getAvoid());
                ps1.setInt(15, eq_list.get(i).getHands());
                ps1.setInt(16, eq_list.get(i).getSpeed());
                ps1.setInt(17, eq_list.get(i).getJump());
                ps1.setByte(18, eq_list.get(i).getViciousHammer());
                ps1.setInt(19, eq_list.get(i).getItemEXP());
                ps1.setInt(20, eq_list.get(i).getDurability());
                ps1.setByte(21, eq_list.get(i).getEnhance());
                ps1.setShort(22, eq_list.get(i).getPotential1());
                ps1.setShort(23, eq_list.get(i).getPotential2());
                ps1.setShort(24, eq_list.get(i).getPotential3());
                ps1.setShort(25, eq_list.get(i).getHpR());
                ps1.setShort(26, eq_list.get(i).getMpR());
                ps1.setInt(27, eq_list.get(i).getAcc_id());
                ps1.setInt(28, eq_list.get(i).getGood_id());
                ps1.setInt(29, eq_list.get(i).getGood_price());
                ps1.setString(30, eq_list.get(i).getCharacters_name());
                ps1.setLong(31, eq_list.get(i).getCreateData());
                ps1.setLong(32, eq_list.get(i).getGood_num());
                ps1.executeUpdate();
            }
            ps1.close();
            eq_list.clear();
            setClose(true);
        } catch (SQLException ex) {
            System.err.println("数据库操作错误:" + ex);
        }
    }

    public void load_data(){
        try (Connection con = DBConPool.getInstance().getDataSource().getConnection()){
            PreparedStatement ps = con.prepareStatement("select * from merchant");
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                goods_list.add(new goods_model(rs.getInt("acc_id"), rs.getInt("good_id"), rs.getInt("good_num"), rs.getInt("good_price"), rs.getString("characters_name"), rs.getLong("createdata")));
            }
            rs.close();
            ps.close();
            PreparedStatement ps1 = con.prepareStatement("select * from merchantEquip");
            ResultSet rs1 = ps1.executeQuery();
            while(rs1.next()){
                eq_list.add(new eqs_model(rs.getInt("'upgradeslots'"), rs.getInt("'level'"), rs.getInt("'str'"), rs.getInt("'dex'"), rs.getInt("'int'"), rs.getInt("'luk'"), rs.getInt("'hp'"), rs.getInt("'mp'"), rs.getInt("'watk'"), rs.getInt("'matk'"), rs.getInt("'wdef'"), rs.getInt("'mdef'"), rs.getInt("'acc'"), rs.getInt("'avoid'"), rs.getInt("'hands'"), rs.getInt("'speed'"), rs.getInt("'jump'"), rs.getByte("'ViciousHammer'"), rs.getInt("'itemEXP'"), rs.getInt("'durability'"), rs.getByte("'enhance'"), rs.getShort("'potential1'"), rs.getShort("'potential2'"), rs.getShort("'potential3'"), rs.getShort("'hpR'"), rs.getShort("'mpR'"), rs.getInt("'acc_id'"), rs.getInt("'good_id'"), rs.getInt("'good_price'"), rs.getString("'characters_name'"), rs.getLong("'createData'"), rs.getInt("'good_num'")));
            }
            rs1.close();
            ps1.close();
        } catch (SQLException ex) {
            System.err.println("数据库操作错误:" + ex);
        }
    }

    public boolean isClose() {
        return close;
    }

    public void setClose(boolean close) {
        this.close = close;
    }

    public List<Integer> getOnlygoods_list(){
        List<goods_model> w_list = goods_list;
        ArrayList only_list = new ArrayList();
        for (int i = 0; i < w_list.size(); i++) {
            if (w_list.get(i).getGood_num() <= 0){
                w_list.remove(i);
            }
        }
        for (int i = 0; i < w_list.size()-1; i++) {
            for (int j = i + 1; j < w_list.size(); j++) {
                if(w_list.get(i).getGood_id() > w_list.get(j).getGood_id()){
                    goods_model temp = w_list.get(i);
                    w_list.set(i, w_list.get(j));
                    w_list.set(j, temp);
                }
            }
        }
        int t_tmp = 0;
        for (int i = 0; i < w_list.size(); i++) {
            if(w_list.get(i).getGood_id() != t_tmp){
                only_list.add(w_list.get(i).getGood_id());
                t_tmp = w_list.get(i).getGood_id();
            }
        }
        return only_list;
    }

    public List<Integer> getOnlyeq_list(){
        List<eqs_model> w_list = eq_list;
        ArrayList only_list = new ArrayList();
        for (int i = 0; i < w_list.size(); i++) {
            if (w_list.get(i).getGood_num() <= 0){
                w_list.remove(i);
            }
        }
        for (int i = 0; i < w_list.size()-1; i++) {
            for (int j = i + 1; j < w_list.size(); j++) {
                if(w_list.get(i).getGood_id() > w_list.get(j).getGood_id()){
                    eqs_model temp = w_list.get(i);
                    w_list.set(i, w_list.get(j));
                    w_list.set(j, temp);
                }
            }
        }
        int t_tmp = 0;
        for (int i = 0; i < w_list.size(); i++) {
            if(w_list.get(i).getGood_id() != t_tmp){
                only_list.add(w_list.get(i).getGood_id());
                t_tmp = w_list.get(i).getGood_id();
            }
        }
        return only_list;
    }
}

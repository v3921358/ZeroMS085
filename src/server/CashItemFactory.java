/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import database.DatabaseConnection;
import gui.ZeroMS_UI;
import java.sql.SQLException;
import java.sql.Connection;
import handling.cashshop.CashShopServer;
import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import server.CashItemInfo.CashModInfo;
import tools.FileoutputUtil;
import java.util.*;

/**
 *
 * @author user
 */
public class CashItemFactory {

    private final static CashItemFactory instance = new CashItemFactory();
    private final static int[] bestItems = new int[]{50100010, 50100010, 50100010, 50100010, 50100010};
    private boolean initialized = false;
    private final Map<Integer, List<Integer>> openBox = new HashMap();//箱子道具物品
    private final Map<Integer, CashItemInfo> itemStats = new HashMap<>();//商城道具状态
    private final Map<Integer, List<CashItemInfo>> itemPackage = new HashMap<>();//礼包信息
    private Map<Integer, List<Integer>> itemPackage2 = new HashMap<Integer, List<Integer>>(); //礼包信息
    private final Map<Integer, CashModInfo> itemMods = new HashMap<>();
    private final Map<Integer, Integer> itemIdToSN = new HashMap<>();
    private final Map<Integer, Integer> itemIdToSn = new HashMap<>();
    private final Map<Integer, Integer> idLookup = new HashMap();//商城道具的SN集合
    private Map<Integer, Boolean> blockCashItemId = new HashMap<Integer, Boolean>(); //禁止购买的商城道具ID
    private Map<Integer, Boolean> blockCashSnId = new HashMap<Integer, Boolean>(); //禁止购买的SNid
    private List<Integer> blockRefundableItemId = new LinkedList<Integer>(); //禁止使用回购的道具 也就是有些道具有多个SN信息 而每个SN下的价格又不一样
    private final MapleDataProvider data = MapleDataProviderFactory.getDataProvider("Etc.wz");

    public static final CashItemFactory getInstance() {
        return instance;
    }

    protected CashItemFactory() {
    }

    public void initialize() {
        blockRefundableItemId.clear();
        int ii = 0;
        int max = 0;
        Map<Integer, Integer> fixId = new HashMap<Integer, Integer>(); //检测WZ中是否有重复价格的道具 [SN] [itemId]
        System.out.println("【信息】 商城系统耐心等待");

        final List<Integer> itemids = new ArrayList<>();
        for (MapleData field : data.getData("Commodity.img").getChildren()) {
            max++;
            final int itemId = MapleDataTool.getIntConvert("ItemId", field, 0);
            final int SN = MapleDataTool.getIntConvert("SN", field, 0);

            final CashItemInfo stats = new CashItemInfo(itemId,
                    MapleDataTool.getIntConvert("Count", field, 1),
                    MapleDataTool.getIntConvert("Price", field, 0), SN,
                    MapleDataTool.getIntConvert("Period", field, 0),
                    MapleDataTool.getIntConvert("Gender", field, 2),
                    MapleDataTool.getIntConvert("OnSale", field, 0) > 0,0);

            if (SN > 0) {
                itemStats.put(SN, stats);
                itemIdToSN.put(stats.getId(), SN);
            }

            if (itemId > 0) {
                itemids.add(itemId);
            }
        }
                System.out.println("【信息】 WZ商城道具: " + itemStats.size() + " 个");
        
       //加载商城礼包的信息
        MapleData packageData = data.getData("CashPackage.img");
        for (MapleData root : packageData.getChildren()) {
            if (root.getChildByPath("SN") == null) {
                continue;
            }
            List<Integer> packageItems2 = new ArrayList<Integer>();
            for (MapleData dat : root.getChildByPath("SN").getChildren()) {
                packageItems2.add(MapleDataTool.getIntConvert(dat));
            }
            itemPackage2.put(Integer.parseInt(root.getName()), packageItems2);
        }
        System.out.println("【信息】 现金商城礼包: " + itemPackage2.size() + " 个");
                
        loadRandomItemInfo();//加载箱子
        
        java.text.NumberFormat numberFormat = java.text.NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(0); // 保留小数点后0位
        for (int i : itemids) {
            getPackageItems(i);
            ii++;
            String result = numberFormat.format((float) ii / (float) max * 100);

             }
        refreshAllModInfo();
        for (int i : itemids) {
            getPackageItems(i);
        }
        for (int i : itemStats.keySet()) {
            getModInfo(i);
            //init the modinfo's citem
            getItem(i);
        }

        initialized = true;
    }
    


        
         public void loadRandomItemInfo() {
        openBox.clear();
        List<Integer> boxItems = new LinkedList<Integer>();
        boxItems.add(50400438);
        boxItems.add(50400439);
        boxItems.add(50400440);
        boxItems.add(50400441);
        boxItems.add(50400442);
        boxItems.add(50400443);
        boxItems.add(50400444);
        boxItems.add(50400445);
        openBox.put(5533027, boxItems);

        boxItems = new LinkedList<Integer>();
        boxItems.add(20000485);
        boxItems.add(20000486);
        boxItems.add(20000487);
        boxItems.add(20000488);
        boxItems.add(20000489);
        boxItems.add(20000490);
        boxItems.add(20000491);
        openBox.put(5533003, boxItems);

        boxItems = new LinkedList<Integer>();
        boxItems.add(20000687);
        boxItems.add(20000688);
        boxItems.add(20000689);
        boxItems.add(20000690);
        boxItems.add(20000691);
        openBox.put(5533011, boxItems);

        boxItems = new LinkedList<Integer>();
        boxItems.add(50500061);
        boxItems.add(50100026);
        boxItems.add(50100027);
        boxItems.add(50100028);
        boxItems.add(50500046);
        openBox.put(5533019, boxItems);

        boxItems = new LinkedList<Integer>();
        boxItems.add(20800316);
        boxItems.add(20800317);
        boxItems.add(20800318);
        boxItems.add(20800319);
        boxItems.add(20800320);
        openBox.put(5533004, boxItems);

        boxItems = new LinkedList<Integer>();
        boxItems.add(21100152);
        boxItems.add(21100153);
        boxItems.add(21100154);
        boxItems.add(21100155);
        openBox.put(5533012, boxItems);

        boxItems = new LinkedList<Integer>();
        boxItems.add(20000849);
        boxItems.add(20000850);
        boxItems.add(20000851);
        boxItems.add(20000852);
        boxItems.add(20000853);
        boxItems.add(20000854);
        boxItems.add(20400302);
        boxItems.add(20400303);
        boxItems.add(20400304);
        openBox.put(5533013, boxItems);

        boxItems = new LinkedList<Integer>();
        boxItems.add(20000547);
        boxItems.add(20000533);
        boxItems.add(20000391);
        boxItems.add(20000550);
        boxItems.add(20000476);
        openBox.put(5533006, boxItems);

        boxItems = new LinkedList<Integer>();
        boxItems.add(20000462);
        boxItems.add(20000463);
        boxItems.add(20000464);
        boxItems.add(20000465);
        boxItems.add(20000466);
        boxItems.add(20000467);
        boxItems.add(20000468);
        boxItems.add(20000469);
        openBox.put(5533014, boxItems);

        boxItems = new LinkedList<Integer>();
        boxItems.add(140200224);
        boxItems.add(140200218);
        boxItems.add(140200225);
        boxItems.add(140200226);
        boxItems.add(140200227);
        boxItems.add(140200228);
        openBox.put(5533007, boxItems);

        boxItems = new LinkedList<Integer>();
        boxItems.add(20000625);
        boxItems.add(20000626);
        boxItems.add(20000627);
        boxItems.add(20000628);
        boxItems.add(20000629);
        openBox.put(5533023, boxItems);

        boxItems = new LinkedList<Integer>();
        boxItems.add(20000621);
        boxItems.add(20000622);
        boxItems.add(20000623);
        boxItems.add(20000624);
        openBox.put(5533024, boxItems);

        boxItems = new LinkedList<Integer>();
        boxItems.add(20000740);
        boxItems.add(20000741);
        boxItems.add(20000742);
        boxItems.add(20000743);
        boxItems.add(20000744);
        boxItems.add(20000745);
        boxItems.add(20000746);
        boxItems.add(20000747);
        openBox.put(5533000, boxItems);

        boxItems = new LinkedList<Integer>();
        boxItems.add(140800248);
        boxItems.add(140800249);
        boxItems.add(140800250);
        boxItems.add(140800251);
        boxItems.add(140800252);
        openBox.put(5533032, boxItems);

        boxItems = new LinkedList<Integer>();
        boxItems.add(21100149);
        boxItems.add(21100150);
        boxItems.add(21100151);
        openBox.put(5533008, boxItems);

        boxItems = new LinkedList<Integer>();
        boxItems.add(20800259);
        boxItems.add(20800260);
        boxItems.add(20800263);
        boxItems.add(20800264);
        boxItems.add(20800265);
        boxItems.add(20800267);
        openBox.put(5533001, boxItems);

        boxItems = new LinkedList<Integer>();
        boxItems.add(130000498);
        boxItems.add(130000499);
        boxItems.add(130000391);
        boxItems.add(130000500);
        boxItems.add(130000390);
        openBox.put(5533025, boxItems);

        boxItems = new LinkedList<Integer>();
        boxItems.add(140100764);
        boxItems.add(140100765);
        boxItems.add(140100766);
        boxItems.add(140100767);
        boxItems.add(140100768);
        boxItems.add(140100769);
        boxItems.add(140100770);
        boxItems.add(140100771);
        boxItems.add(140100772);
        boxItems.add(140100773);
        boxItems.add(140100774);
        boxItems.add(140100775);
        boxItems.add(140100776);
        boxItems.add(140100777);
        openBox.put(5533033, boxItems);

        boxItems = new LinkedList<Integer>();
        boxItems.add(20000543);
        boxItems.add(20000544);
        boxItems.add(20000545);
        boxItems.add(20000546);
        boxItems.add(20000547);
        openBox.put(5533009, boxItems);

        boxItems = new LinkedList<Integer>();
        boxItems.add(10002766);
        boxItems.add(10002767);
        boxItems.add(10002768);
        boxItems.add(10002769);
        openBox.put(5533017, boxItems);

        boxItems = new LinkedList<Integer>();
        boxItems.add(140100547);
        boxItems.add(140100548);
        boxItems.add(140100549);
        boxItems.add(140100550);
        boxItems.add(140100551);
        boxItems.add(140100552);
        openBox.put(5533026, boxItems);

        boxItems = new LinkedList<Integer>();
        boxItems.add(20800297);
        boxItems.add(20800298);
        boxItems.add(20800299);
        boxItems.add(20800300);
        boxItems.add(20800301);
        openBox.put(5533002, boxItems);

        boxItems = new LinkedList<Integer>();
        boxItems.add(50500061);
        boxItems.add(50100026);
        boxItems.add(50100027);
        boxItems.add(50100028);
        boxItems.add(50500046);
        openBox.put(5533018, boxItems);

        System.out.println("【信息】 WZ商城随机箱子: " + openBox.size() + " 个");
    }
    
    public boolean isOnSalePackage(int snId) {
        return snId >= 170200002 && snId <= 170200013;
    }
    
    public final int getSnByItemItd(int itemid) {
        int sn = itemIdToSN.get(itemid);
        return sn;
    }

    public final int getSnByItemItd2(int itemid) {
        int sn = itemIdToSn.get(itemid);
        return sn;
    }

    public final CashItemInfo getItem(int sn) {
        final CashItemInfo stats = itemStats.get(Integer.valueOf(sn));
        final CashModInfo z = getModInfo(sn);
        if (z != null && z.showUp) {
            return z.toCItem(stats); //null doesnt matter
        }
        if (stats == null || !stats.onSale()) {
            return null;
        }
        //hmm
        return stats;
    }

    public final Set<Integer> getAllItemSNs() {
        return itemStats.keySet();
    }

    public final List<CashItemInfo> getAllItems() {
        return new ArrayList<>(itemStats.values());
    }

    public final List<CashItemInfo> getPackageItems(int itemId) {
        if (itemPackage.get(itemId) != null) {
            return itemPackage.get(itemId);
        }
        final List<CashItemInfo> packageItems = new ArrayList<>();

        final MapleData b = data.getData("CashPackage.img");
        if (b == null || b.getChildByPath(itemId + "/SN") == null) {
            return null;
        }
        for (MapleData d : b.getChildByPath(itemId + "/SN").getChildren()) {
            packageItems.add(itemStats.get(MapleDataTool.getIntConvert(d)));
        }
        itemPackage.put(itemId, packageItems);
        return packageItems;
    }

    public final Map<Integer, List<Integer>> getRandomItemInfo() {
        return this.openBox;
    }

    public final CashModInfo getModInfo(int sn) {
        CashModInfo ret = itemMods.get(sn);

        if (ret == null) {
            if (initialized) {
                return null;
            }
              try {{
                    Connection con = DatabaseConnection.getConnection();
                    PreparedStatement ps = con.prepareStatement("SELECT * FROM cashshop_modified_items WHERE serial = ?");
                    ps.setInt(1, sn);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        ret = new CashModInfo(sn, rs.getInt("discount_price"), rs.getInt("mark"), rs.getInt("showup") > 0, rs.getInt("itemid"), rs.getInt("priority"), rs.getInt("package") > 0, rs.getInt("period"), rs.getInt("gender"), rs.getInt("count"), rs.getInt("meso"), rs.getInt("unk_1"), rs.getInt("unk_2"), rs.getInt("unk_3"), rs.getInt("extra_flags"), rs.getInt("mod"));
                        itemMods.put(sn, ret);

                    }
                    rs.close();
                    ps.close();
                }
            } catch (Exception e) {
                FileoutputUtil.outError("logs/資料庫異常.txt", e);
                e.printStackTrace();
            }
        }
        return ret;
    }

    private void refreshAllModInfo() {
        itemMods.clear();
        itemIdToSn.clear();
        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM cashshop_modified_items");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Integer sn = rs.getInt("serial");
                CashModInfo ret = new CashModInfo(sn, rs.getInt("discount_price"), rs.getInt("mark"), rs.getInt("showup") > 0, rs.getInt("itemid"), rs.getInt("priority"), rs.getInt("package") > 0, rs.getInt("period"), rs.getInt("gender"), rs.getInt("count"), rs.getInt("meso"), rs.getInt("unk_1"), rs.getInt("unk_2"), rs.getInt("unk_3"), rs.getInt("extra_flags"), rs.getInt("mod"));
                itemMods.put(sn, ret);
                itemIdToSn.put(ret.itemid, sn);
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            FileoutputUtil.outError("logs/資料庫異常.txt", e);
            e.printStackTrace();
        }
    }

    public final Collection<CashModInfo> getAllModInfo() {
        if (itemMods.isEmpty()) {
            refreshAllModInfo();
        }
        return itemMods.values();
    }

    public final int[] getBestItems() {
        return bestItems;
    }

    public void clearItems() {
        refreshAllModInfo();
    }
    public void clearCashShop() {
        itemStats.clear();
        itemPackage.clear();
        itemMods.clear();
        idLookup.clear();
        initialized = false;
        initialize();
    }
    public final int getItemSN(int itemid) {
        for (Entry<Integer, CashItemInfo> ci : itemStats.entrySet()) {
            if (ci.getValue().getId() == itemid) {
                return ci.getValue().getSN();
            }
        }
        return 0;
    }
}

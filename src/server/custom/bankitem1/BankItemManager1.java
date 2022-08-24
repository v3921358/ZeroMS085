package server.custom.bankitem1;

import client.MapleCharacter;
import client.inventory.IItem;
import client.inventory.ItemFlag;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import tools.MaplePacketCreator;

/**
 *
 * @author ZEV1
 */
public class BankItemManager1 {

    private static class InstanceHolder {

        public static final BankItemManager1 instance = new BankItemManager1();
    }

    public static BankItemManager1 getInstance() {
        return InstanceHolder.instance;
    }

    private BankItemManager1() {
    }

    public int saveItem(MapleCharacter player, byte type, short slot, short count) {
        
        
        
        int ret = 1;
        if (!(type == 2 || type == 3 || type == 4)) {
            return -2;//只能存消耗设置其他的道具
        }
        if (slot < 0) {
            return -3;//道具位置非法
        }
        final MapleInventoryType itemtype = MapleInventoryType.getByType(type);
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final IItem source = player.getInventory(itemtype).getItem(slot);
        return saveItem(player, source, count);
    }

    public int saveItem(MapleCharacter player, IItem source, short count) {
        int ret = 1;
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

        byte type = (byte) (source.getItemId() / 1000000);
        if (!(type == 2 || type == 3 || type == 4)) {
            return -2;//只能存消耗设置其他的道具
        }
        if (source == null) {
            return -4;//存的道具不存在
        }
        if (ii.isCash(source.getItemId()) || source.getExpiration() > 0) {
            return -5;//点装或限时道具不能存
        }
        final byte flag = source.getFlag();
        if (count > source.getQuantity() || count < 1) {
            return -6;//存的数量大于存在数量
        }
//         if (ItemFlag.LOCK.check(flag) || (count != 1 && itemtype == MapleInventoryType.EQUIP)) { // hack
//            return -7;//锁定道具或一栏多个装备
//        }
        ret = add(player.getguildid(), source.getItemId(), count);
        if (ret < 1) {
            return -8;//存库失败
        }
        //System.out.println("存入道具:[itemid:"+source.getItemId()+"][type:"+source.getType()+"][position:"+source.getPosition()+"][Quantity:"+source.getQuantity()+"][count:"+count+"]");

        final MapleInventoryType itemtype = MapleInventoryType.getByType(type);

        if (GameConstants.isThrowingStar(source.getItemId())||GameConstants.isBullet(source.getItemId())) {
            count = source.getQuantity();
        }
        MapleInventoryManipulator.removeFromSlot(player.getClient(), itemtype, source.getPosition(), count, false);
//        if (count < source.getQuantity() && !GameConstants.isRechargable(source.getItemId())) {
//            source.setQuantity((short) (source.getQuantity() - count));
//            player.getClient().sendPacket(MaplePacketCreator.dropInventoryItemUpdate(itemtype, source));
//        } else {
//            player.getInventory(itemtype).removeSlot(source.getPosition());
//            player.getClient().sendPacket(MaplePacketCreator.dropInventoryItem((source.getPosition() < 0 ? MapleInventoryType.EQUIP : itemtype), source.getPosition()));
//        }
        return ret;
    }

    public List<BankItem1> getItems(int cid) {
        List<BankItem1> items = new ArrayList<>();
        Connection con1 = DatabaseConnection.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con1.prepareStatement("select * from bank_item1 where cid = ?");
            ps.setInt(1, cid);
            rs = ps.executeQuery();
            while (rs.next()) {
                BankItem1 info = new BankItem1();
                info.setId(rs.getLong("id"));
                info.setItemid(rs.getInt("itemid"));
                info.setCid(rs.getInt("cid"));
                info.setCount(rs.getInt("count"));
                items.add(info);
            }
        } catch (Exception Ex) {
            Ex.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return items;
    }

    public int add(int cid, int itemid, int count) {
        BankItem1 item = new BankItem1();
        item.setCid(cid);
        item.setItemid(itemid);
        item.setCount(count);
        return add(item);
    }

    public int add(BankItem1 item) {
        int ret = -1;
        if (item == null) {
            return -1;
        }
        Connection con1 = DatabaseConnection.getConnection();
        PreparedStatement ps = null;
        try {
            ps = con1.prepareStatement("insert into bank_item1 (id,cid,itemid,count) values (?,?,?,?)");
            ps.setLong(1, item.getId());
            ps.setInt(2, item.getCid());
            ps.setInt(3, item.getItemid());
            ps.setInt(4, item.getCount());
            ret = ps.executeUpdate();
        } catch (Exception Ex) {
            Ex.printStackTrace();
            return ret;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return ret;
    }

    public int delete(long id) {
        int ret = -1;
        Connection con1 = DatabaseConnection.getConnection();
        PreparedStatement ps = null;
        try {
            ps = con1.prepareStatement("delete from bank_item1 where id = ?");
            ps.setLong(1, id);
            ret = ps.executeUpdate();
        } catch (Exception Ex) {
            Ex.printStackTrace();
            return ret;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return ret;
    }

    public int update(BankItem1 bankItem) {
        int ret = -1;
        Connection con1 = DatabaseConnection.getConnection();
        PreparedStatement ps = null;
        try {
            ps = con1.prepareStatement("update bank_item1 set cid = ? ,itemid = ? ,count = ? where id = ?");
            ps.setInt(1, bankItem.getCid());
            ps.setInt(2, bankItem.getItemid());
            ps.setInt(3, bankItem.getCount());
            ps.setLong(4, bankItem.getId());
            ret = ps.executeUpdate();
        } catch (Exception Ex) {
            Ex.printStackTrace();
            return ret;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return ret;
    }
}

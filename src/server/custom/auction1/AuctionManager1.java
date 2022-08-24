package server.custom.auction1;

import abc.拍卖行限制;
import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Equip;
import client.inventory.IEquip;
import client.inventory.IItem;
import client.inventory.Item;
import client.inventory.ItemFlag;
import client.inventory.MapleInventoryIdentifier;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import client.inventory.MapleRing;
import constants.GameConstants;
import database.DatabaseConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import tools.MaplePacketCreator;

public class AuctionManager1 {

    private static class InstanceHolder {

        public static final AuctionManager1 instance = new AuctionManager1();
    }

    public static AuctionManager1 getInstance() {
        return InstanceHolder.instance;
    }

    private AuctionManager1() {
    }

    public final void gainItem(final IItem item, short quantity, final MapleClient cg) {
        if (quantity >= 0) {
            final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            final MapleInventoryType type = GameConstants.getInventoryType(item.getItemId());

            if (!MapleInventoryManipulator.checkSpace(cg, item.getItemId(), quantity, "")) {
                return;
            }
            if (type.equals(MapleInventoryType.EQUIP) && !GameConstants.isThrowingStar(item.getItemId()) && !GameConstants.isBullet(item.getItemId())) {
                final Equip equip = (Equip) item;

                final String name = ii.getName(item.getItemId());
                if (item.getItemId() / 10000 == 114 && name != null && name.length() > 0) {
                    final String msg = "你已获得称号 <" + name + ">";
                    cg.getPlayer().dropMessage(5, msg);
                    cg.getPlayer().dropMessage(5, msg);
                }
                MapleInventoryManipulator.addbyItem(cg, equip.copy());
            } else {
                MapleInventoryManipulator.addbyItem(cg, item.copy());
            }
        } else {
            MapleInventoryManipulator.removeById(cg, GameConstants.getInventoryType(item.getItemId()), item.getItemId(), -quantity, true, false);
        }
        cg.sendPacket(MaplePacketCreator.getShowItemGain(item.getItemId(), quantity, true));
    }

    public int putInt(MapleCharacter player, IItem source, short quantity) {
        int ret = 1;
        if (player == null || source == null) {
            return -4;
        }
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (source.getExpiration() > 0) {
            return -5;
        }
        final byte flag = source.getFlag();
        if (quantity > source.getQuantity() || quantity < 1) {
            return -6;
        }
        //时装
        if (ii.isCash(source.getItemId())) {
            return -8;
        }
     
        if (source.getFlag() != 0) {
            return -8;
        }

        final MapleInventoryType itemtype = getItemTypeByItemId(source.getItemId());
        //LOCK鎖定道具 UNTRADEABLE不可交易的道具(固有道具等)
        if (ItemFlag.LOCK.check(flag) || ItemFlag.UNTRADEABLE.check(flag) || (quantity != 1 && itemtype == MapleInventoryType.EQUIP)) {
            // if (ItemFlag.LOCK.check(flag)|| (quantity != 1 && itemtype == MapleInventoryType.EQUIP)) {
            // if (ItemFlag.LOCK.check(flag)||ItemFlag.UNTRADEABLE.check(flag)          ) {
            return -7;
        }

        拍卖行限制 itemjc2 = new 拍卖行限制();
        String items2 = itemjc2.getPM();
        if (items2.contains("," + source.getItemId() + ",")) {
            return -8;
        }

        AuctionItem1 auctionItem = new AuctionItem1();
        auctionItem.setAuctionState1(AuctionState1.下架);
        auctionItem.setCharacterid(player.getId());
        auctionItem.setCharacterName(player.getName());
        auctionItem.setQuantity(quantity);
        auctionItem.setItem(source.copy());
        int id = add(auctionItem);
        if (id < 1) {
            return id;
        }
        auctionItem.setId(id);
        if (GameConstants.isThrowingStar(source.getItemId()) || GameConstants.isBullet(source.getItemId())) {
            quantity = source.getQuantity();
        }
        MapleInventoryManipulator.removeFromSlot(player.getClient(), itemtype, source.getPosition(), quantity, false);
        return ret;
    }

    public int takeOutAuctionItem1(MapleCharacter player, long id) {
        AuctionItem1 auctionItem = this.findById(id);
        if (auctionItem == null) {
            return -5;
        }
        return takeOutAuctionItem1(player, id, (short) auctionItem.getQuantity());
    }

    public int takeOutAuctionItem1(MapleCharacter player, long id, short count) {
        AuctionItem1 auctionItem = this.findById(id);
        if (auctionItem == null) {
            return -5;
        }
        return takeOutAuctionItem1(player, auctionItem, count);
    }

    public int takeOutAuctionItem1(MapleCharacter player, AuctionItem1 auctionItem, short count) {
        if (auctionItem == null) {
            return -5;
        }
        if (auctionItem.getCharacterid() != player.getId()) {
            return -6;
        }
        if (AuctionState1.下架 != auctionItem.getAuctionState1()) {
            return -7;
        }
        if (count > auctionItem.getQuantity() || count < 1) {
            return -8;
        }
        if (!MapleInventoryManipulator.checkSpace(player.getClient(), auctionItem.getItem().getItemId(), count, "")) {
            return -9;
        }
        int ret = 1;
        if (count < auctionItem.getQuantity() && !GameConstants.isThrowingStar(auctionItem.getItem().getItemId()) && !GameConstants.isBullet(auctionItem.getItem().getItemId())) {
            auctionItem.setQuantity(auctionItem.getQuantity() - count);
            ret = AuctionManager1.getInstance().update(auctionItem);
        } else {
            ret = AuctionManager1.getInstance().deleteById(auctionItem.getId());
        }
        if (ret > 0) {
            this.gainItem(auctionItem.getItem(), count, player.getClient());
        }
        return ret;
    }

    public int buy(MapleCharacter player, long id) {
        AuctionItem1 auctionItem = this.findById(id);
        if (auctionItem == null) {
            return -5;
        }
        return buy(player, auctionItem);
    }

    public int buy(MapleCharacter player, AuctionItem1 auctionItem) {
        int ret = -1;
        if (auctionItem == null) {
            return -5;
        }
        if (AuctionState1.上架 != auctionItem.getAuctionState1()) {
            return -6;
        }
        AuctionPoint1 auctionPoint1 = this.getAuctionPoint1(player.getId());
        if (auctionPoint1 == null) {
            return -7;
        }
        if (auctionPoint1.getPoint() < auctionItem.getPrice()) {
            return -8;
        }
        if (!MapleInventoryManipulator.checkSpace(player.getClient(), auctionItem.getItem().getItemId(), auctionItem.getQuantity(), "")) {
            return -9;
        }
        auctionItem.setAuctionState1(AuctionState1.已售);
        auctionItem.setBuyer(player.getId());
        auctionItem.setBuyerName(player.getName());
        ret = update(auctionItem);
        if (ret > 0) {
            int addpc = addPoint(player.getId(), -auctionItem.getPrice());
            if (addpc > 0) {
                int addp = addPoint(auctionItem.getCharacterid(), auctionItem.getPrice());
                if (addp > 0) {
                    if (auctionItem.getCharacterid() != player.getId()) {
                        addPointSell(auctionItem.getCharacterid(), auctionItem.getPrice());
                        addPointBuy(player.getId(), auctionItem.getPrice());
                    }
                    this.gainItem(auctionItem.getItem(), (short) auctionItem.getQuantity(), player.getClient());
                }
            }
        }
        return ret;
    }

    public int setPutaway(long id, int price) {
        AuctionItem1 auctionItem = this.findById(id);
        if (auctionItem == null) {
            return -5;
        }
        auctionItem.setPrice(price);
        return setPutaway(auctionItem);
    }

    public int setPutaway(AuctionItem1 auctionItem) {
        if (AuctionState1.下架 != auctionItem.getAuctionState1()) {
            return -6;
        }
        if (auctionItem.getPrice() < 1) {
            return -7;
        }
        auctionItem.setAuctionState1(AuctionState1.上架);
        return update(auctionItem);
    }

    public int soldOut(long id) {
        AuctionItem1 auctionItem = this.findById(id);
        if (auctionItem == null) {
            return -5;
        }
        return soldOut(auctionItem);
    }

    public int soldOut(AuctionItem1 auctionItem) {
        if (AuctionState1.上架 != auctionItem.getAuctionState1()) {
            return -6;
        }
        auctionItem.setAuctionState1(AuctionState1.下架);
        auctionItem.setPrice(0);
        return update(auctionItem);
    }

    public AuctionPoint1 getAuctionPoint1(int characterid) {
        AuctionPoint1 auctionPoint1 = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = DatabaseConnection.getConnection().prepareStatement("select * from auctionPoint1 where characterid = ?");
            ps.setInt(1, characterid);
            rs = ps.executeQuery();
            if (rs.next()) {
                auctionPoint1 = new AuctionPoint1();
                auctionPoint1.setCharacterid(rs.getInt("characterid"));
                auctionPoint1.setPoint(rs.getLong("point"));
                auctionPoint1.setPoint_sell(rs.getLong("point_sell"));
                auctionPoint1.setPoint_buy(rs.getLong("point_buy"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(AuctionManager1.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(AuctionManager1.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }
        return auctionPoint1;
    }

    public int addPoint(int characterid, long point) {
        return addPoint(characterid, point, 1);
    }

    public int addPointSell(int characterid, long point) {
        return addPoint(characterid, point, 2);
    }

    public int addPointBuy(int characterid, long point) {
        return addPoint(characterid, point, 3);
    }

    public int addPoint(int characterid, long point, int type) {
        int ret = -1;
        boolean update = false;
        AuctionPoint1 dbPoint = getAuctionPoint1(characterid);
        if (dbPoint == null) {
            dbPoint = new AuctionPoint1();
            dbPoint.setCharacterid(characterid);
        } else {
            update = true;
        }
        switch (type) {
            case 1:
                dbPoint.setPoint(dbPoint.getPoint() + point);
                break;
            case 2:
                dbPoint.setPoint_sell(dbPoint.getPoint_sell() + point);
                break;
            case 3:
                dbPoint.setPoint_buy(dbPoint.getPoint_buy() + point);
                break;
        }

        PreparedStatement ps = null;
        try {
            if (update) {
                ps = DatabaseConnection.getConnection().prepareStatement("UPDATE `auctionPoint1` SET point = ? , point_sell = ? , point_buy = ? WHERE characterid = ? ");
                ps.setLong(1, dbPoint.getPoint());
                ps.setLong(2, dbPoint.getPoint_sell());
                ps.setLong(3, dbPoint.getPoint_buy());
                ps.setInt(4, dbPoint.getCharacterid());
            } else {
                ps = DatabaseConnection.getConnection().prepareStatement("INSERT INTO `auctionPoint1` VALUES (?, ?, ?, ?)");
                ps.setInt(1, dbPoint.getCharacterid());
                ps.setLong(2, dbPoint.getPoint());
                ps.setLong(3, dbPoint.getPoint_sell());
                ps.setLong(4, dbPoint.getPoint_buy());
            }

            ret = ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(AuctionManager1.class.getName()).log(Level.SEVERE, null, ex);
            return -2;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(AuctionManager1.class.getName()).log(Level.SEVERE, null, ex);
                return -2;
            }
        }
        return ret;
    }

    public int add(AuctionItem1 auctionItem) {
        int ret = -1;
        MapleInventoryType itemtype = getItemTypeByItemId(auctionItem.getItem().getItemId());
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            if (itemtype.equals(MapleInventoryType.EQUIP) || itemtype.equals(MapleInventoryType.EQUIPPED)) {
                ps = DatabaseConnection.getConnection().prepareStatement("INSERT INTO `auctionitems1` VALUES (null, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            } else {
                ps = DatabaseConnection.getConnection().prepareStatement("INSERT INTO `auctionitems1` (characterid,characterName,auctionState,buyer,buyerName,price,itemid,inventorytype,quantity,owner,GM_Log,uniqueid,flag,expiredate,sender) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            }
            mapSavePs(ps, auctionItem);
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs != null && rs.next()) {
                ret = rs.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(AuctionManager1.class.getName()).log(Level.SEVERE, null, ex);
            return -2;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(AuctionManager1.class.getName()).log(Level.SEVERE, null, ex);
                return -2;
            }
        }
        return ret;
    }

    public int update(AuctionItem1 auctionItem) {
        int ret = -1;
        MapleInventoryType itemtype = getItemTypeByItemId(auctionItem.getItem().getItemId());
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            if (itemtype.equals(MapleInventoryType.EQUIP) || itemtype.equals(MapleInventoryType.EQUIPPED)) {
                ps = DatabaseConnection.getConnection().prepareStatement("UPDATE `auctionitems1` SET characterid = ? ,characterName = ? ,auctionState = ?,buyer = ?,buyerName = ? ,price = ?,itemid = ?,inventorytype = ?,quantity = ?,owner = ?,GM_Log = ?,uniqueid = ?,flag = ?,expiredate = ?,sender = ?,upgradeslots = ?,level = ?,str = ?,dex = ?,_int = ?,luk = ?,hp = ?,mp = ?,watk = ?,matk = ?,wdef = ?,mdef = ?,acc = ?,avoid = ?,hands = ?,speed = ?,jump = ?,ViciousHammer = ?,itemEXP = ?,durability = ?,enhance = ?,potential1 = ?,potential2 = ?,potential3 = ?,hpR = ?,mpR = ?,itemlevel = ? where id = ?");
            } else {
                ps = DatabaseConnection.getConnection().prepareStatement("UPDATE `auctionitems1` SET characterid = ?, characterName = ? ,auctionState = ?,buyer = ?,buyerName = ? ,price = ?,itemid = ?,inventorytype = ?,quantity = ?,owner = ?,GM_Log = ?,uniqueid = ?,flag = ?,expiredate = ?,sender = ? where id = ?");
            }
            mapSavePs(ps, auctionItem);
            if (itemtype.equals(MapleInventoryType.EQUIP) || itemtype.equals(MapleInventoryType.EQUIPPED)) {
                ps.setLong(43, auctionItem.getId());
            } else {
                ps.setLong(16, auctionItem.getId());
            }
            ret = ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(AuctionManager1.class.getName()).log(Level.SEVERE, null, ex);
            return -2;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(AuctionManager1.class.getName()).log(Level.SEVERE, null, ex);
                return -2;
            }
        }
        return ret;
    }

    public int deleteById(long id) {
        int ret = -1;
        PreparedStatement ps_del = null;
        try {
            ps_del = DatabaseConnection.getConnection().prepareStatement("DELETE  FROM auctionItems1 where id = ?");
            ps_del.setLong(1, id);
            ret = ps_del.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(AuctionManager1.class.getName()).log(Level.SEVERE, null, ex);
            return -2;
        } finally {
            try {
                if (ps_del != null) {
                    ps_del.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(AuctionManager1.class.getName()).log(Level.SEVERE, null, ex);
                return -2;
            }
        }
        return ret;
    }

    public int deletePlayerSold(int characterid) {
        int ret = -1;
        PreparedStatement ps_del = null;
        try {
            ps_del = DatabaseConnection.getConnection().prepareStatement("DELETE  FROM auctionItems1 where characterid = ? and auctionState = ?");
            ps_del.setInt(1, characterid);
            ps_del.setInt(2, AuctionState1.已售.getState());
            ret = ps_del.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(AuctionManager1.class.getName()).log(Level.SEVERE, null, ex);
            return -2;
        } finally {
            try {
                if (ps_del != null) {
                    ps_del.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(AuctionManager1.class.getName()).log(Level.SEVERE, null, ex);
                return -2;
            }
        }
        return ret;
    }

    public AuctionItem1 findById(long id) {
        AuctionItem1 auctionItem = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = DatabaseConnection.getConnection().prepareStatement("select * from auctionitems1 where id = ?");
            ps.setLong(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                auctionItem = mapLoadRs(rs);
            }
        } catch (SQLException ex) {
            Logger.getLogger(AuctionManager1.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(AuctionManager1.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return auctionItem;
    }

    public List<AuctionItem1> findByCharacterId(int characterid) {
        List<AuctionItem1> ret = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = DatabaseConnection.getConnection().prepareStatement("select * from auctionitems1 where characterid = ?");
            ps.setInt(1, characterid);
            rs = ps.executeQuery();
            while (rs.next()) {
                AuctionItem1 auctionItem = mapLoadRs(rs);
                if (auctionItem != null) {
                    ret.add(auctionItem);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(AuctionManager1.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(AuctionManager1.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return ret;
    }

    public List<AuctionItem1> findByItemType(int inventorytype) {
        List<AuctionItem1> ret = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = DatabaseConnection.getConnection().prepareStatement("select * from auctionitems1 where inventorytype = ? and auctionState = 1 order by itemid desc");
            ps.setInt(1, inventorytype);
            rs = ps.executeQuery();
            while (rs.next()) {
                AuctionItem1 auctionItem = mapLoadRs(rs);
                if (auctionItem != null) {
                    ret.add(auctionItem);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(AuctionManager1.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(AuctionManager1.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return ret;
    }

    public MapleInventoryType getItemTypeByItemId(int itemid) {
        return MapleInventoryType.getByType((byte) (itemid / 1000000));
    }

    public void mapSavePs(PreparedStatement ps, AuctionItem1 auctionItem) throws SQLException {
        MapleInventoryType itemtype = getItemTypeByItemId(auctionItem.getItem().getItemId());
        IItem item = auctionItem.getItem();
        ps.setInt(1, auctionItem.getCharacterid());
        ps.setString(2, auctionItem.getCharacterName());
        ps.setInt(3, auctionItem.getAuctionState1().getState());
        ps.setInt(4, auctionItem.getBuyer());
        ps.setString(5, auctionItem.getBuyerName());
        ps.setInt(6, auctionItem.getPrice());
        ps.setInt(7, item.getItemId());
        ps.setInt(8, itemtype.getType());
        ps.setInt(9, auctionItem.getQuantity());
        ps.setString(10, item.getOwner());
        ps.setString(11, item.getGMLog());
        ps.setInt(12, item.getUniqueId());
        ps.setByte(13, item.getFlag());
        ps.setLong(14, item.getExpiration());
        ps.setString(15, item.getGiftFrom());
        if (itemtype.equals(MapleInventoryType.EQUIP) || itemtype.equals(MapleInventoryType.EQUIPPED)) {
            IEquip equip = (IEquip) item;
            ps.setInt(16, equip.getUpgradeSlots());
            ps.setInt(17, equip.getLevel());
            ps.setInt(18, equip.getStr());
            ps.setInt(19, equip.getDex());
            ps.setInt(20, equip.getInt());
            ps.setInt(21, equip.getLuk());
            ps.setInt(22, equip.getHp());
            ps.setInt(23, equip.getMp());
            ps.setInt(24, equip.getWatk());
            ps.setInt(25, equip.getMatk());
            ps.setInt(26, equip.getWdef());
            ps.setInt(27, equip.getMdef());
            ps.setInt(28, equip.getAcc());
            ps.setInt(29, equip.getAvoid());
            ps.setInt(30, equip.getHands());
            ps.setInt(31, equip.getSpeed());
            ps.setInt(32, equip.getJump());
            ps.setInt(33, equip.getViciousHammer());
            ps.setInt(34, equip.getItemEXP());
            ps.setInt(35, equip.getDurability());
            ps.setByte(36, equip.getEnhance());
            ps.setInt(37, equip.getPotential1());
            ps.setInt(38, equip.getPotential2());
            ps.setInt(39, equip.getPotential3());
            ps.setInt(40, equip.getHpR());
            ps.setInt(41, equip.getMpR());
            ps.setByte(42, (byte) equip.getEquipLevel());
  //          ps.setString(43, equip.getDaKongFuMo());
        }
    }

    public AuctionItem1 mapLoadRs(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        int characterid = rs.getInt("characterid");
        String characterName = rs.getString("characterName");
        Byte auctionState = rs.getByte("auctionState");
        int buyer = rs.getInt("buyer");
        String buyerName = rs.getString("buyerName");
        int price = rs.getInt("price");
        int itemid = rs.getInt("itemid");
        int inventorytype = rs.getInt("inventorytype");
        short quantity = rs.getShort("quantity");
        String owner = rs.getString("owner");
        String GM_Log = rs.getString("GM_Log");
        int uniqueid = rs.getInt("uniqueid");
        byte flag = rs.getByte("flag");
        long expiredate = rs.getLong("expiredate");
        String sender = rs.getString("sender");
        byte upgradeslots = rs.getByte("upgradeslots");
        byte level = rs.getByte("level");
        short str = rs.getShort("str");
        short dex = rs.getShort("dex");
        short _int = rs.getShort("_int");
        short luk = rs.getShort("luk");
        short hp = rs.getShort("hp");
        short mp = rs.getShort("mp");
        short watk = rs.getShort("watk");
        short matk = rs.getShort("matk");
        short wdef = rs.getShort("wdef");
        short mdef = rs.getShort("mdef");
        short acc = rs.getShort("acc");
        short avoid = rs.getShort("avoid");
        short hands = rs.getShort("hands");
        short speed = rs.getShort("speed");
        short jump = rs.getShort("jump");
        byte ViciousHammer = rs.getByte("ViciousHammer");
        int itemEXP = rs.getInt("itemEXP");
        int durability = rs.getInt("durability");
        byte enhance = rs.getByte("enhance");
        short potential1 = rs.getShort("potential1");
        short potential2 = rs.getShort("potential2");
        short potential3 = rs.getShort("potential3");
        short hpR = rs.getShort("hpR");
        short mpR = rs.getShort("mpR");
        byte itemlevel = rs.getByte("itemlevel");

        MapleInventoryType mit = MapleInventoryType.getByType((byte) inventorytype);
        if (characterid < 1 || mit == null) {
            return null;
        }

        AuctionItem1 auctionItem = new AuctionItem1();
        auctionItem.setId(id);
        auctionItem.setAuctionState1(AuctionState1.getState(auctionState));
        auctionItem.setPrice(price);
        auctionItem.setBuyer(buyer);
        auctionItem.setBuyerName(buyerName);
        auctionItem.setCharacterid(characterid);
        auctionItem.setCharacterName(characterName);
        auctionItem.setQuantity(quantity);
        if (mit.equals(MapleInventoryType.EQUIP) || mit.equals(MapleInventoryType.EQUIPPED)) {
            Equip equip = new Equip(itemid, (short) 0, uniqueid, flag);
            equip.setQuantity((short) 1);
            equip.setOwner(owner);
            equip.setExpiration(expiredate);
            equip.setUpgradeSlots(upgradeslots);
            equip.setLevel(level);
            equip.setStr(str);
            equip.setDex(dex);
            equip.setInt(_int);
            equip.setLuk(luk);
            equip.setHp(hp);
            equip.setMp(mp);
            equip.setWatk(watk);
            equip.setMatk(matk);
            equip.setWdef(wdef);
            equip.setMdef(mdef);
            equip.setAcc(acc);
            equip.setAvoid(avoid);
            equip.setHands(hands);
            equip.setSpeed(speed);
            equip.setJump(jump);
            equip.setViciousHammer(ViciousHammer);
            equip.setItemEXP(itemEXP);
            equip.setGMLog(GM_Log);
            equip.setDurability(durability);
            equip.setEnhance(enhance);
            equip.setPotential1(potential1);
            equip.setPotential2(potential2);
            equip.setPotential3(potential3);
            equip.setHpR(hpR);
            equip.setMpR(mpR);
            equip.setGiftFrom(sender);
            equip.setEquipLevel(itemlevel);
            if (equip.getUniqueId() > -1) {
                if (GameConstants.isEffectRing(itemid)) {
                    MapleRing ring = MapleRing.loadFromDb(equip.getUniqueId(), mit.equals(MapleInventoryType.EQUIPPED));
                    if (ring != null) {
                        equip.setRing(ring);
                    }
                }
            }
            auctionItem.setItem(equip.copy());
        } else {
            Item item = new Item(itemid, (short) 0, quantity, flag);
            item.setUniqueId(uniqueid);
            item.setOwner(owner);
            item.setExpiration(expiredate);
            item.setGMLog(GM_Log);
            item.setGiftFrom(sender);
            if (GameConstants.isPet(item.getItemId())) {
                if (item.getUniqueId() > -1) {
                    MaplePet pet = MaplePet.loadFromDb(item.getItemId(), item.getUniqueId(), item.getPosition());
                    if (pet != null) {
                        item.setPet(pet);
                    }
                } else {
                    final int new_unique = MapleInventoryIdentifier.getInstance();
                    item.setUniqueId(new_unique);
                    item.setPet(MaplePet.createPet(item.getItemId(), new_unique));
                }
            }
            auctionItem.setItem(item.copy());
        }
        return auctionItem;
    }

}

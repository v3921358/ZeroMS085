/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License version 3
 as published by the Free Software Foundation. You may not use, modify
 or distribute this program under any other version of the
 GNU Affero General Public License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package client.inventory;

import client.MapleClient;
import constants.GameConstants;
import constants.ServerConfig;
import handling.world.World;
import java.io.Serializable;
import java.util.List;
import server.MapleItemInformationProvider;
import server.Randomizer;
import server.ServerProperties;
import tools.MaplePacketCreator;
import tools.Pair;

public class Equip extends Item implements IEquip, Serializable {

    private byte upgradeSlots = 0;
    private byte level = 0, vicioushammer = 0, enhance = 0, state = 0;
    private short str = 0, dex = 0, _int = 0, luk = 0, hp = 0, mp = 0, watk = 0, matk = 0, wdef = 0, mdef = 0, acc = 0, avoid = 0, hands = 0, speed = 0, jump = 0, potential1 = 0, potential2 = 0, potential3 = 0, hpR = 0, mpR = 0, charmExp = 0, pvpDamage = 0;
    private int itemEXP = 0, durability = 0, incSkill = -1, hpRR = 0, mpRR = 0;
    private byte itemLevel;
    public Equip(int id, short position, byte flag) {
        super(id, position, (short) 1, flag);
    }

    public Equip(int id, short position, int uniqueid, byte flag) {
        super(id, position, (short) 1, flag, uniqueid);
    }

    @Override
    public IItem copy() {
        Equip ret = new Equip(getItemId(), getPosition(), getUniqueId(), getFlag());
        ret.str = str;//??????
        ret.dex = dex;//??????
        ret._int = _int;//??????
        ret.luk = luk;//??????
        ret.hp = hp;//Hp
        ret.mp = mp;//Mp
        ret.matk = matk;//????????????
        ret.mdef = mdef;//????????????
        ret.watk = watk;//????????????
        ret.wdef = wdef;//????????????
        ret.acc = acc;//?????????
        ret.avoid = avoid;//?????????
        ret.hands = hands;//??????
        ret.speed = speed;//????????????
        ret.jump = jump;//?????????
        ret.state = state; //????????????
        ret.enhance = enhance;//??????
        ret.upgradeSlots = upgradeSlots;//???????????????
        ret.level = level;//???????????????
        ret.itemEXP = itemEXP;
        ret.durability = durability;//?????????
        ret.vicioushammer = vicioushammer;//?????????
        ret.potential1 = potential1;//??????1
        ret.potential2 = potential2;//??????2
        ret.potential3 = potential3;//??????3
        ret.charmExp = charmExp;//????????????
        ret.pvpDamage = pvpDamage;//??????????????????
        ret.hpR = hpR;
        ret.mpR = mpR;
        ret.hpRR = hpRR;
        ret.mpRR = mpRR;
        ret.incSkill = incSkill;
        ret.setGiftFrom(getGiftFrom());
        ret.setOwner(getOwner());
        ret.setQuantity(getQuantity());
        ret.setExpiration(getExpiration());
        ret.setInventoryId(getInventoryId());
        ret.setEquipOnlyId(getEquipOnlyId());
        return ret;
    }

    @Override
    public byte getType() {
        return 1;
    }

    @Override
    public byte getUpgradeSlots() {
        return upgradeSlots;
    }

    public void setEquipLevel(byte gf) {
        this.itemLevel = gf;
    }

    @Override
    public short getStr() {
        return str;
    }

    @Override
    public short getDex() {
        return dex;
    }

    @Override
    public short getInt() {
        return _int;
    }

    @Override
    public short getLuk() {
        return luk;
    }

    @Override
    public short getHp() {
        return hp;
    }

    @Override
    public short getMp() {
        return mp;
    }

    @Override
    public short getWatk() {
        return watk;
    }

    @Override
    public short getMatk() {
        return matk;
    }

    @Override
    public short getWdef() {
        return wdef;
    }

    @Override
    public short getMdef() {
        return mdef;
    }

    @Override
    public short getAcc() {
        return acc;
    }

    @Override
    public short getAvoid() {
        return avoid;
    }

    @Override
    public short getHands() {
        return hands;
    }

    @Override
    public short getSpeed() {
        return speed;
    }

    @Override
    public short getJump() {
        return jump;
    }

    public void setStr(short str) {
        if (str < 0) {
            str = 0;
        }
        this.str = str;
    }

    public void setDex(short dex) {
        if (dex < 0) {
            dex = 0;
        }
        this.dex = dex;
    }

    public void setInt(short _int) {
        if (_int < 0) {
            _int = 0;
        }
        this._int = _int;
    }

    public void setLuk(short luk) {
        if (luk < 0) {
            luk = 0;
        }
        this.luk = luk;
    }

    public void setHp(short hp) {
        if (hp < 0) {
            hp = 0;
        }
        this.hp = hp;
    }

    public void setMp(short mp) {
        if (mp < 0) {
            mp = 0;
        }
        this.mp = mp;
    }

    public void setWatk(short watk) {
        if (watk < 0) {
            watk = 0;
        }
        this.watk = watk;
    }

    public void setMatk(short matk) {
        if (matk < 0) {
            matk = 0;
        }
        this.matk = matk;
    }

    public void setWdef(short wdef) {
        if (wdef < 0) {
            wdef = 0;
        }
        this.wdef = wdef;
    }

    public void setMdef(short mdef) {
        if (mdef < 0) {
            mdef = 0;
        }
        this.mdef = mdef;
    }
    
    public void setpotential1(short potential1) {
        if (potential1 < 0) {
            potential1 = 0;
        }
        this.potential1 = potential1;
    }
    public void setpotential2(short potential2) {
        if (potential2 < 0) {
            potential2 = 0;
        }
        this.potential2 = potential2;
    }
    public void setpotential3(short potential3) {
        if (potential3 < 0) {
            potential3 = 0;
        }
        this.potential3 = potential3;
    }
    public void setAcc(short acc) {
        if (acc < 0) {
            acc = 0;
        }
        this.acc = acc;
    }

    public void setAvoid(short avoid) {
        if (avoid < 0) {
            avoid = 0;
        }
        this.avoid = avoid;
    }

    public void setHands(short hands) {
        if (hands < 0) {
            hands = 0;
        }
        this.hands = hands;
    }

    public void setSpeed(short speed) {
        if (speed < 0) {
            speed = 0;
        }
        this.speed = speed;
    }

    public void setJump(short jump) {
        if (jump < 0) {
            jump = 0;
        }
        this.jump = jump;
    }

    public void setUpgradeSlots(byte upgradeSlots) {
        this.upgradeSlots = upgradeSlots;
    }

    @Override
    public byte getLevel() {
        return level;
    }

    public void setLevel(byte level) {
        this.level = level;
    }

    @Override
    public byte getViciousHammer() {
        return vicioushammer;
    }

    public void setViciousHammer(byte ham) {
        vicioushammer = ham;
    }

    @Override
    public int getItemEXP() {
        return itemEXP;
    }

    public void setItemEXP(int itemEXP) {
        if (itemEXP < 0) {
            itemEXP = 0;
        }
        this.itemEXP = itemEXP;
    }

    @Override
    public int getEquipExp() {
        if (itemEXP <= 0) {
            return 0;
        }
        //aproximate value
        if (GameConstants.isWeapon(getItemId())) {
            return itemEXP / IEquip.WEAPON_RATIO;
        } else {
            return itemEXP / IEquip.ARMOR_RATIO;
        }
    }

    @Override
    public int getEquipExpForLevel() {
        if (getEquipExp() <= 0) {
            return 0;
        }
        int expz = getEquipExp();
        for (int i = getBaseLevel(); i <= GameConstants.getMaxLevel(getItemId()); i++) {
            if (expz >= GameConstants.getExpForLevel(i, getItemId())) {
                expz -= GameConstants.getExpForLevel(i, getItemId());
            } else { //for 0, dont continue;
                break;
            }
        }
        return expz;
    }

    @Override
    public int getExpPercentage() {
        if (getEquipLevel() < getBaseLevel() || getEquipLevel() > GameConstants.getMaxLevel(getItemId()) || GameConstants.getExpForLevel(getEquipLevel(), getItemId()) <= 0) {
            return 0;
        }
        return getEquipExpForLevel() * 100 / GameConstants.getExpForLevel(getEquipLevel(), getItemId());
    }

    @Override
    public int getEquipLevel() {
        if (GameConstants.getMaxLevel(getItemId()) <= 0) {
            return 0;
        } else if (getEquipExp() <= 0) {
            return getBaseLevel();
        }
        int levelz = getBaseLevel();
        int expz = getEquipExp();
        for (int i = levelz; (GameConstants.getStatFromWeapon(getItemId()) == null ? (i <= GameConstants.getMaxLevel(getItemId())) : (i < GameConstants.getMaxLevel(getItemId()))); i++) {
            if (expz >= GameConstants.getExpForLevel(i, getItemId())) {
                levelz++;
                expz -= GameConstants.getExpForLevel(i, getItemId());
            } else { //for 0, dont continue;
                break;
            }
        }
        return levelz;
    }

    @Override
    public int getBaseLevel() {
        return (GameConstants.getStatFromWeapon(getItemId()) == null ? 1 : 0);
    }

    @Override
    public void setQuantity(short quantity) {
        if (quantity < 0 || quantity > 1) {
            throw new RuntimeException("Setting the quantity to " + quantity + " on an equip (itemid: " + getItemId() + ")");
        }
        super.setQuantity(quantity);
    }

    /*
     * ????????????????????????
     */
    public int getDurability() {
        return durability;
    }

    public void setDurability(final int dur) {
        this.durability = dur;
    }

    /*
     * ??????
     */
    public byte getEnhance() {
        return enhance;
    }

    public void setEnhance(final byte en) {
        this.enhance = en;
    }

    /*
     * ????????????1
     */
    public short getPotential1() {
        return potential1;
    }

    public void setPotential1(final short en) {
        this.potential1 = en;
    }

    /*
     * ????????????2
     */
    public short getPotential2() {
        return potential2;
    }

    public void setPotential2(final short en) {
        this.potential2 = en;
    }

    /*
     * ????????????3
     */
    public short getPotential3() {
        return potential3;
    }

    public void setPotential3(final short en) {
        this.potential3 = en;
    }

    /*
     * ???????????????
     * 15 = ????????? 16?????? 20?????????????????????
     * 16 = C???
     * 17 = B???
     * 18 = A???
     * 19 = S???
     * 20 = SS???
     */
    public byte getState() {
        final int pots = potential1 + potential2 + potential3;
        if (potential1 >= 30000 || potential2 >= 30000 || potential3 >= 30000) {
            return 7;
        } else if (potential1 >= 20000 || potential2 >= 20000 || potential3 >= 20000) {
            return 6;
        } else if (pots >= 1) {
            return 5;
        } else if (pots < 0) {
            return 1;//???????????????
        }
        return 0;//??????C?????????
    }
  
    public void resetPotential() { //equip first receive
        //0.04% chance unique, 4% chance epic, else rare
        final int rank = Randomizer.nextInt(100) < 4 ? (Randomizer.nextInt(100) < 4 ? -7 : -6) : -5;
        setPotential1((short) rank);
        setPotential2((short) (Randomizer.nextInt(100) < ServerConfig.renew_3line ? rank : 0)); //1/10 chance of 3 line ???????????????????????????
        setPotential3((short) 0); //just set it theoretically
    }
     /*
     * 0 = 5062000 - ????????????
     * 1 = 5062100 - ???????????? 5062001 - ??????????????????
     * 2 = A????????????
     * 3 = 5062002 - ??????????????????
     * 4 = S????????????
     */
    public void renewPotential() {
        //4% chance upgrade
        //final int rank = Randomizer.nextInt(100) < 4 && getState() != 7 ? -(getState() + 1) : -(getState());
        int rank = 0;
        if (getState() == 5) {
            rank = Randomizer.nextInt(10000) < ServerConfig.renew_A && getState() != 7 ? -(getState() + 1) : -(getState());
            //A
        } else {
            rank = Randomizer.nextInt(10000) < ServerConfig.renew_S && getState() != 7 ? -(getState() + 1) : -(getState());
            //S
        }
        if (rank == -8) {
            rank = -7;
        }
        setPotential1((short) rank);
        setPotential2((short) (getPotential3() > 0 ? rank : 0)); //1/10 chance of 3 line
        setPotential3((short) 0); //just set it theoretically
    }
    
        public void renewPotentialauto() {
        int rank = 0;
        switch (getState()) {
            case 5:
                rank = Randomizer.nextInt(10000) < 500 && getState() != 7 ? -(getState() + 1) : -(getState());
                break;
            case 6:
                rank = Randomizer.nextInt(10000) < 100 && getState() != 7 ? -(getState() + 1) : -(getState());
                break;
            default:
                rank = Randomizer.nextInt(10000) < 1 && getState() != 7 ? -(getState() + 1) : -(getState());
                break;
        }
        if (rank == -8) {
            rank = -7;
        }
        setPotential1((short) rank);
        setPotential2((short) (getPotential3() > 0 ? rank : 0)); //1/10 chance of 3 line
        setPotential3((short) 0); //just set it theoretically
    }
    public void renewPotentialSuper() {
        int rank = 0;
        switch (getState()) {
            case 5:
                rank = Randomizer.nextInt(10000) < 500 && getState() != 7 ? -(getState() + 1) : -(getState());
                break;
            case 6:
                rank = Randomizer.nextInt(10000) < 100 && getState() != 7 ? -(getState() + 1) : -(getState());
                break;
            default:
                rank = Randomizer.nextInt(10000) < 1 && getState() != 7 ? -(getState() + 1) : -(getState());
                break;
        }
        if (rank == -8) {
            rank = -7;
        }
        setPotential1((short) rank);
        setPotential2((short) (getPotential3() > 0  || Randomizer.nextInt(1000) < 20 ? rank : 0)); //1/10 chance of 3 line 
        setPotential3((short) 0); //just set it theoretically
    }
    @Override
    public short getHpR() {
        return hpR;
    }

    public void setHpR(final short hp) {
        this.hpR = hp;
    }

    @Override
    public short getMpR() {
        return mpR;
    }

    public void setMpR(final short mp) {
        this.mpR = mp;
    }
    
    @Override
    public int getHpRR() {
        return hpRR;
    }

    public void setHpRR(final int hp) {
        this.hpRR = hp;
    }

    @Override
    public int getMpRR() {
        return mpRR;
    }

    public void setMpRR(final int mp) {
        this.mpRR = mp;
    }

    public int getIncSkill() {
        return incSkill;
    }

    public void setIncSkill(int inc) {
        this.incSkill = inc;
    }

    public short getCharmEXP() {
        return charmExp;
    }

    public void setCharmEXP(short s) {
        this.charmExp = s;
    }

    public short getPVPDamage() {
        return pvpDamage;
    }

    public void setPVPDamage(short p) {
        this.pvpDamage = p;
    }

     /**<??????????????????>*/
    public void renewPotential_super() {
        // 10% is too high....8% enough
        final int rank = Randomizer.nextInt(100) < Integer.parseInt(ServerProperties.getProperty("ZeroMS.??????????????????")) && getState() != 8 ? -(getState() + 1) : -(getState());
        setPotential1((short) rank);
        setPotential2((short) (getPotential3() > 0 ? rank : 0));
        setPotential3((short) 0); //just set it theoretically
    }
    }
    
   

    
   
    


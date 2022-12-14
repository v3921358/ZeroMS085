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
package handling.world;

import database.DatabaseConnection;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class MapleParty implements Serializable {

    private static final long serialVersionUID = 9179541993413738569L;

    public static String 取椅子备注(int a) {
        String data = "";
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM qqstem");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getInt("channel") == a) {
                    data = rs.getString("Name");
                }
            }
            ps.close();
        } catch (SQLException ex) {
        }
        return data;
    }
    
    private MaplePartyCharacter leader;
    private final List<MaplePartyCharacter> members = new LinkedList<>();
    private int id;
    public static int 通缉地图 = 0;
    public static int 通缉BOSS = 0;
    public static int 互相伤害 = 0;
    private int averageLevel = 0;
    public static int 活动安排 = 0;
    public static int 雪球赛 = 0;
    public static String 椅子3010025 = 取椅子备注(3010025);
    public static String 椅子3010100 = 取椅子备注(3010100);
    public static String 椅子3012002 = 取椅子备注(3012002);

    private void calculateAverageLevel() {
        int value = 0;
        for (MaplePartyCharacter chr : members) {
            value += chr.getLevel();
        }
        value = (int) ((double) averageLevel / (double) members.size());
        this.averageLevel = value;
    }

    public MapleParty(int id, MaplePartyCharacter chrfor) {
        this.leader = chrfor;
        this.members.add(this.leader);
        this.id = id;
        this.averageLevel = 0;
    }

    public boolean containsMembers(MaplePartyCharacter member) {
        return members.contains(member);
    }

    public void addMember(MaplePartyCharacter member) {
        members.add(member);
        calculateAverageLevel();
    }

    public void removeMember(MaplePartyCharacter member) {
        members.remove(member);
        calculateAverageLevel();
    }

    public void updateMember(MaplePartyCharacter member) {
        for (int i = 0; i < members.size(); i++) {
            MaplePartyCharacter chr = members.get(i);
            if (chr.equals(member)) {
                members.set(i, member);
            }
        }
        calculateAverageLevel();
    }

    public MaplePartyCharacter getMemberById(int id) {
        for (MaplePartyCharacter chr : members) {
            if (chr.getId() == id) {
                return chr;
            }
        }
        return null;
    }

    public MaplePartyCharacter getMemberByIndex(int index) {
        return members.get(index);
    }

    public Collection<MaplePartyCharacter> getMembers() {
        return new LinkedList<>(members);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MaplePartyCharacter getLeader() {
        return leader;
    }

    public void setLeader(MaplePartyCharacter nLeader) {
        leader = nLeader;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MapleParty other = (MapleParty) obj;
        return id == other.id;
    }

    public int getAverageLevel() {
        return this.averageLevel;
    }
}

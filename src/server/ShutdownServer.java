package server;

import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.login.LoginServer;
import handling.world.World;
import java.util.Set;
import server.Timer.*;

public class ShutdownServer implements Runnable, ShutdownServerMBean {

    private static final ShutdownServer instance = new ShutdownServer();
    public static boolean running = false;

    public static ShutdownServer getInstance() {
        return instance;
    }

    @Override
    public void run() {
        synchronized (this) {
            if (running) { //Run once!
                return;
            }
            running = true;
        }
        World.isShutDown = true;
        int ret = 0;
        for (handling.channel.ChannelServer cserv : handling.channel.ChannelServer.getAllInstances()) {
            ret += cserv.closeAllMerchant();
        }
        System.out.println("共保存了 " + ret + " 个雇佣商店");
        ret = 0;
        for (handling.channel.ChannelServer cserv : handling.channel.ChannelServer.getAllInstances()) {
            ret += cserv.closeAllPlayerShop();
        }
        System.out.println("共保存了 " + ret + " 个人执照商店");
        World.Guild.save();
        System.out.println("公会资料保存完毕");
        World.Alliance.save();
        System.out.println("联盟资料保存完毕");
        World.Family.save();
        System.out.println("家族资料保存完毕");
        EventTimer.getInstance().stop();
        WorldTimer.getInstance().stop();
        MapTimer.getInstance().stop();
        MobTimer.getInstance().stop();
        BuffTimer.getInstance().stop();
        CloneTimer.getInstance().stop();
        EtcTimer.getInstance().stop();
        PingTimer.getInstance().stop();
        System.out.println("计时器关闭完成");
        
        
        Set<Integer> channels = ChannelServer.getAllChannels();

        for (Integer channel : channels) {
            try {
                ChannelServer cs = ChannelServer.getInstance(channel);
                cs.saveAll();
                cs.setPrepareShutdown();
                cs.shutdown();
            } catch (Exception e) {
                System.out.println("频道" + String.valueOf(channel) + " 关闭失败.");
            }
        }

        try {
            LoginServer.shutdown();
            System.out.println("登录服务器关闭完成.");
        } catch (Exception e) {
            System.out.println("登录服务器关闭失败");
        }
        try {
            CashShopServer.shutdown();
            System.out.println("商城服务器关闭完成.");
        } catch (Exception e) {
            System.out.println("商城服务器关闭失败");
        }
//        try {
//            Thread.sleep(5000);
//        } catch (Exception e) {
//
//        }
//        System.exit(0);
    }

    @Override
    public void shutdown() {
        this.run();
    }
}

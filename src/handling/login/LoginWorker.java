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
package handling.login;

import client.MapleClient;
import constants.WorldConstants;
import handling.channel.ChannelServer;
import handling.world.World;
import java.util.Map;
import java.util.Map.Entry;
import server.ServerProperties;
import server.Timer.PingTimer;
import tools.MaplePacketCreator;
import tools.packet.LoginPacket;

public class LoginWorker {

    private static long lastUpdate = 0;

    public static void registerClient(final MapleClient c) {
        int 禁止登陆开关 = gui.ZeroMS_UI.ConfigValuesMap.get("禁止登陆开关");
        if (!c.isGm()) {
            if (禁止登陆开关 <= 0) {
            c.sendPacket(MaplePacketCreator.serverNotice(1, "服务器目前正在维修中."));
            c.sendPacket(LoginPacket.getLoginFailed(1));
            return;
        }
        if (!c.isGm() && (c.hasBannedMac() || c.hasBannedIP())) {
            c.sendPacket(LoginPacket.getLoginFailed(3)); //
            return;
        }
        if (System.currentTimeMillis() - lastUpdate > 600000) { // Update once every 10 minutes
            lastUpdate = System.currentTimeMillis();
            final Map<Integer, Integer> load = ChannelServer.getChannelLoad();
            int usersOn = 0;
            if (load == null || load.size() <= 0) { // In an unfortunate event that client logged in before load
                lastUpdate = 0;
                c.sendPacket(LoginPacket.getLoginFailed(7));
                return;
            }
            final double loadFactor = 1200 / ((double) WorldConstants.USER_LIMIT / load.size());
            for (Entry<Integer, Integer> entry : load.entrySet()) {
                usersOn += entry.getValue();
                load.put(entry.getKey(), Math.min(1200, (int) (entry.getValue() * loadFactor)));
            }
            LoginServer.setLoad(load, usersOn);
            lastUpdate = System.currentTimeMillis();
        }

        if (c.finishLogin() == 0) {
            if (c.getSecondPassword() == null) {
                c.sendPacket(LoginPacket.getGenderNeeded(c));
            } else {
                World.clearChannelChangeDataByAccountId(c.getAccID());
                LoginServer.forceRemoveClient(c);
                ChannelServer.forceRemovePlayerByAccId(c, c.getAccID());
                LoginServer.getClientStorage().registerAccount(c);
                c.sendPacket(LoginPacket.getAuthSuccessRequest(c));
                if (gui.ZeroMS_UI.ConfigValuesMap.get("蓝蜗牛开关") == 0) {
                    c.sendPacket(LoginPacket.getServerList(0, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
                }
                if (gui.ZeroMS_UI.ConfigValuesMap.get("蘑菇仔开关") == 0) {
                    c.sendPacket(LoginPacket.getServerList(1, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
                }
                if (gui.ZeroMS_UI.ConfigValuesMap.get("绿水灵开关") == 0) {
                    c.sendPacket(LoginPacket.getServerList(2, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
                }
                if (gui.ZeroMS_UI.ConfigValuesMap.get("漂漂猪开关") == 0) {
                    c.sendPacket(LoginPacket.getServerList(3, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
                }
                if (gui.ZeroMS_UI.ConfigValuesMap.get("小青蛇开关") == 0) {
                    c.sendPacket(LoginPacket.getServerList(4, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
                }
                if (gui.ZeroMS_UI.ConfigValuesMap.get("红螃蟹开关") == 0) {
                    c.sendPacket(LoginPacket.getServerList(5, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
                }
                if (gui.ZeroMS_UI.ConfigValuesMap.get("大海龟开关") == 0) {
                    c.sendPacket(LoginPacket.getServerList(6, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
                }
                if (gui.ZeroMS_UI.ConfigValuesMap.get("章鱼怪开关") == 0) {
                    c.sendPacket(LoginPacket.getServerList(7, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
                }
                if (gui.ZeroMS_UI.ConfigValuesMap.get("顽皮猴开关") == 0) {
                    c.sendPacket(LoginPacket.getServerList(8, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
                }
                if (gui.ZeroMS_UI.ConfigValuesMap.get("星精灵开关") == 0) {
                    c.sendPacket(LoginPacket.getServerList(9, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
                }
                if (gui.ZeroMS_UI.ConfigValuesMap.get("胖企鹅开关") == 0) {
                    c.sendPacket(LoginPacket.getServerList(10, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
                }
                if (gui.ZeroMS_UI.ConfigValuesMap.get("白雪人开关") == 0) {
                    c.sendPacket(LoginPacket.getServerList(11, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
                }
                if (gui.ZeroMS_UI.ConfigValuesMap.get("石头人开关") == 0) {
                    c.sendPacket(LoginPacket.getServerList(12, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
                }
                if (gui.ZeroMS_UI.ConfigValuesMap.get("紫色猫开关") == 0) {
                    c.sendPacket(LoginPacket.getServerList(13, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
                }
                if (gui.ZeroMS_UI.ConfigValuesMap.get("大灰狼开关") == 0) {
                    c.sendPacket(LoginPacket.getServerList(14, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
                }
                if (gui.ZeroMS_UI.ConfigValuesMap.get("小白兔开关") == 0) {
                    c.sendPacket(LoginPacket.getServerList(15, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
                }
                if (gui.ZeroMS_UI.ConfigValuesMap.get("喷火龙开关") == 0) {
                    c.sendPacket(LoginPacket.getServerList(16, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
                }
                if (gui.ZeroMS_UI.ConfigValuesMap.get("火野猪开关") == 0) {
                    c.sendPacket(LoginPacket.getServerList(17, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
                }
                if (gui.ZeroMS_UI.ConfigValuesMap.get("青鳄鱼开关") == 0) {
                    c.sendPacket(LoginPacket.getServerList(18, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
                }
                if (gui.ZeroMS_UI.ConfigValuesMap.get("花蘑菇开关") == 0) {
                    c.sendPacket(LoginPacket.getServerList(19, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
                }
                c.sendPacket(LoginPacket.getEndOfServerList());
            }
            c.setIdleTask(PingTimer.getInstance().schedule(new Runnable() {

                @Override
                public void run() {
                    c.getSession().close();
                }
            }, 10 * 60 * 10000));
        } else if (c.getGender() == 10) {
            c.sendPacket(LoginPacket.getGenderNeeded(c));

        } else {
            c.sendPacket(LoginPacket.getAuthSuccessRequest(c));
            if (gui.ZeroMS_UI.ConfigValuesMap.get("蓝蜗牛开关") == 0) {
                c.sendPacket(LoginPacket.getServerList(0, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
            }
            if (gui.ZeroMS_UI.ConfigValuesMap.get("蘑菇仔开关") == 0) {
                c.sendPacket(LoginPacket.getServerList(1, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
            }
            if (gui.ZeroMS_UI.ConfigValuesMap.get("绿水灵开关") == 0) {
                c.sendPacket(LoginPacket.getServerList(2, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
            }
            if (gui.ZeroMS_UI.ConfigValuesMap.get("漂漂猪开关") == 0) {
                c.sendPacket(LoginPacket.getServerList(3, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
            }
            if (gui.ZeroMS_UI.ConfigValuesMap.get("小青蛇开关") == 0) {
                c.sendPacket(LoginPacket.getServerList(4, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
            }
            if (gui.ZeroMS_UI.ConfigValuesMap.get("红螃蟹开关") == 0) {
                c.sendPacket(LoginPacket.getServerList(5, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
            }
            if (gui.ZeroMS_UI.ConfigValuesMap.get("大海龟开关") == 0) {
                c.sendPacket(LoginPacket.getServerList(6, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
            }
            if (gui.ZeroMS_UI.ConfigValuesMap.get("章鱼怪开关") == 0) {
                c.sendPacket(LoginPacket.getServerList(7, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
            }
            if (gui.ZeroMS_UI.ConfigValuesMap.get("顽皮猴开关") == 0) {
                c.sendPacket(LoginPacket.getServerList(8, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
            }
            if (gui.ZeroMS_UI.ConfigValuesMap.get("星精灵开关") == 0) {
                c.sendPacket(LoginPacket.getServerList(9, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
            }
            if (gui.ZeroMS_UI.ConfigValuesMap.get("胖企鹅开关") == 0) {
                c.sendPacket(LoginPacket.getServerList(10, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
            }
            if (gui.ZeroMS_UI.ConfigValuesMap.get("白雪人开关") == 0) {
                c.sendPacket(LoginPacket.getServerList(11, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
            }
            if (gui.ZeroMS_UI.ConfigValuesMap.get("石头人开关") == 0) {
                c.sendPacket(LoginPacket.getServerList(12, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
            }
            if (gui.ZeroMS_UI.ConfigValuesMap.get("紫色猫开关") == 0) {
                c.sendPacket(LoginPacket.getServerList(13, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
            }
            if (gui.ZeroMS_UI.ConfigValuesMap.get("大灰狼开关") == 0) {
                c.sendPacket(LoginPacket.getServerList(14, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
            }
            if (gui.ZeroMS_UI.ConfigValuesMap.get("小白兔开关") == 0) {
                c.sendPacket(LoginPacket.getServerList(15, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
            }
            if (gui.ZeroMS_UI.ConfigValuesMap.get("喷火龙开关") == 0) {
                c.sendPacket(LoginPacket.getServerList(16, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
            }
            if (gui.ZeroMS_UI.ConfigValuesMap.get("火野猪开关") == 0) {
                c.sendPacket(LoginPacket.getServerList(17, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
            }
            if (gui.ZeroMS_UI.ConfigValuesMap.get("青鳄鱼开关") == 0) {
                c.sendPacket(LoginPacket.getServerList(18, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
            }
            if (gui.ZeroMS_UI.ConfigValuesMap.get("花蘑菇开关") == 0) {
                c.sendPacket(LoginPacket.getServerList(19, LoginServer.getServerName(), LoginServer.getLoad(), Integer.parseInt(ServerProperties.getProperty("ZeroMS.flag"))));
            }
            c.sendPacket(LoginPacket.getEndOfServerList());
        }

    }
}}

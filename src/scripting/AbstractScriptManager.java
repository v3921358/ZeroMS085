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
package scripting;

import java.io.File;

import java.io.IOException;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import client.MapleClient;
import static client.messages.commands.PlayerCommand.ea.getDayOfWeek;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import javax.script.ScriptException;
import tools.EncodingDetect;
import tools.FilePrinter;
import tools.MaplePacketCreator;
import tools.StringUtil;

/**
 *
 * @author Matze
 */
public abstract class AbstractScriptManager {

    private static final ScriptEngineManager sem = new ScriptEngineManager();

    protected Invocable getInvocable(String path, MapleClient c) {
        return getInvocable(path, c, false);
    }

    protected Invocable getInvocable(String path, MapleClient c, boolean npc) {
        path = "scripts/" + path;
        ScriptEngine engine = null;

        if (c != null) {
            engine = c.getScriptEngine(path);
        }
        if (engine == null) {
            File scriptFile = new File(path);
            if (!scriptFile.exists()) {
                return null;
            }
            if (c != null && c.getPlayer() != null) {
                if (c.getPlayer().getDebugMessage()) {
                    c.getPlayer().dropMessage("getInvocable - Part1");
                }
            }
            engine = sem.getEngineByName("javascript");
            if (c != null && c.getPlayer() != null) {
                if (c.getPlayer().getDebugMessage()) {
                    c.getPlayer().dropMessage("getInvocable - Part2");
                }
            }
            if (c != null) {
                c.setScriptEngine(path, engine);
                if (c != null && c.getPlayer() != null) {
                    if (c.getPlayer().getDebugMessage()) {
                        c.getPlayer().dropMessage("getInvocable - Part3");
                    }
                }
            }
            InputStream in = null;
            try {
                in = new FileInputStream(scriptFile);
                if (c != null && c.getPlayer() != null) {
                    if (c.getPlayer().getDebugMessage()) {
                        c.getPlayer().dropMessage("getInvocable - Part4");
                    }
                }
                BufferedReader bf = new BufferedReader(new InputStreamReader(in, EncodingDetect.getJavaEncode(scriptFile)));
                String lines = "load('nashorn:mozilla_compat.js');" + bf.lines().collect(Collectors.joining(System.lineSeparator()));
                engine.eval(lines);
                if (c != null && c.getPlayer() != null) {
                    if (c.getPlayer().getDebugMessage()) {
                        c.getPlayer().dropMessage("getInvocable - Part5");
                    }
                }
            } catch (ScriptException | IOException e) {
                FilePrinter.printError("AbstractScriptManager.txt", "Error executing script. Path: " + path + "\nException " + e);
                return null;
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException ignore) {
                }
            }
        } else if (c != null && npc) {
                 //c.getPlayer().dropMessage(5, "悠着点.不要点太快哟");
            //  c.getPlayer().dropMessage(5, "            chr.dropMessage(5, \"悠着点.不要点太快哟\");");
           // c.removeClickedNPC();
          //  NPCScriptManager.getInstance().dispose(c);
          //  c.sendPacket(MaplePacketCreator.enableActions());
         //   c.sendPacket(MaplePacketCreator.sendHint(
             //       "\r\n"
             //       + "当前系统时间" + FilePrinter.getLocalDateString() + " 星期" + getDayOfWeek() + "\r\n"
               //     + "经验值倍率 " + ((Math.round(c.getPlayer().getEXPMod()) * c.getPlayer().getExpm() * 100 * c.getChannelServer().getExpRate()) * Math.round(c.getPlayer().getStat().expBuff / 100.0) + (c.getPlayer().getStat().equippedFairy ? c.getPlayer().getFairyExp() : 0)) + "%, 爆率 " + Math.round(c.getPlayer().getDropMod() * c.getPlayer().getDropm() * (c.getPlayer().getStat().dropBuff / 100.0) * 100 * c.getChannelServer().getDropRate()) + "%, 金币倍率 " + Math.round((c.getPlayer().getStat().mesoBuff / 100.0) * 100 * c.getChannelServer().getMesoRate()) + "% \r\n "
          //          + "VIP经验掉宝加成：" + (c.getPlayer().getVipExpRate()) + "%\r\n"
                 //   + "目前剩余 " + c.getPlayer().getCSPoints(1) + " 点券 " + c.getPlayer().getCSPoints(2) + " 抵用券 \r\n版权:仙境冒险岛 QQ群:1014032303\r\n"
          //          + "赞助红利 " + c.getPlayer().getCSPoints(3) + "  \r\n"
                         //
              //              + "当前延迟 " + c.getPlayer().getClient().getLatency() + " 毫秒", 350, 5));
                  //   + "当前延迟 " + c.getPlayer().getClient().getLatency() + " 毫秒", 350, 5));
        }
        return (Invocable) engine;
    }
}

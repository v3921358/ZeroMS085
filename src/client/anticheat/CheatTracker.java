package client.anticheat;

import client.MapleBuffStat;
import java.awt.Point;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import constants.GameConstants;
import client.MapleCharacter;
import client.MapleCharacterUtil;
import client.SkillFactory;
import constants.PiPiConfig;
import constants.WorldConstants;
import handling.world.World;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import server.AutobanManager;
import server.Timer.CheatTimer;
import tools.FileoutputUtil;
import tools.MaplePacketCreator;
import tools.StringUtil;

public class CheatTracker {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock rL = lock.readLock(), wL = lock.writeLock();
    private final Map<CheatingOffense, CheatingOffenseEntry> offenses = new LinkedHashMap<>();
    private final WeakReference<MapleCharacter> chr;
    // For keeping track of speed attack hack.
    private long lastAttackTime = 0;
    private int inMapIimeCount = 0;
    private int lastAttackTickCount = 0;
    private byte Attack_tickResetCount = 0;
    private long Server_ClientAtkTickDiff = 0;
    private long lastDamage = 0;
    private long takingDamageSince;
    private int numSequentialDamage = 0;
    private long lastDamageTakenTime = 0;
    private byte numZeroDamageTaken = 0;
    private int numSequentialSummonAttack = 0;
    private long summonSummonTime = 0;
    private int numSameDamage = 0;
    private Point lastMonsterMove;
    private int monsterMoveCount;
    private int attacksWithoutHit = 0;
    private byte dropsPerSecond = 0;
    private long lastDropTime = 0;
    private byte msgsPerSecond = 0;
    private long lastMsgTime = 0;
    private ScheduledFuture<?> invalidationTask;
    private int gm_message = 100;
    private int lastTickCount = 0, tickSame = 0;
    private long lastASmegaTime = 0;
    private long[] lastTime = new long[6];
    private long lastSaveTime = 0;
    private long lastLieDetectorTime = 0;
    private long lastLieTime = 0;

    public CheatTracker(final MapleCharacter chr) {
        this.chr = new WeakReference<>(chr);
        invalidationTask = CheatTimer.getInstance().register(new InvalidationTask(), 60000);
        takingDamageSince = System.currentTimeMillis();
    }

    public final void checkAttack(final int skillId, final int tickcount) {
        short AtkDelay = GameConstants.getAttackDelay(skillId);
        if (chr.get().getBuffedValue(MapleBuffStat.BODY_PRESSURE) != null) {
            AtkDelay /= 6;// ?????????Buff?????? tickcount - lastAttackTickCount ?????????0...
        }
        // ????????????
        if (chr.get().getBuffedValue(MapleBuffStat.BOOSTER) != null) {
            AtkDelay /= 1.5;
        }
        // ????????????
        if (chr.get().getBuffedValue(MapleBuffStat.SPEED_INFUSION) != null) {
            AtkDelay /= 1.35;
        }
        // ??????
        if (GameConstants.isAran(chr.get().getJob())) {
            AtkDelay /= 1.4;// 407
        }
        // ???????????????
        if (chr.get().getJob() >= 500 && chr.get().getJob() <= 512) {
            AtkDelay = 0;// 407
        }
        // ????????????
        if (skillId == 21101003 || skillId == 5110001) {
            AtkDelay = 0;
        }
        if ((tickcount - lastAttackTickCount) < AtkDelay) {
            /*if (chr.get().get??????() >= 100) {
                if (!chr.get().hasGmLevel(1)) {
                    chr.get().ban(chr.get().getName() + "??????????????????????????????" + skillId, true, true, false);
                    chr.get().getClient().getSession().close();
                    String reason = "????????????????????????";
                    World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, "[????????????] " + chr.get().getName() + " ??????" + reason + "??????????????????????????????"));
                    World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM??????] " + chr.get().getName() + " ???????????????????????????! "));
                } else {
                    chr.get().dropMessage("????????????????????????");
                }
                FileoutputUtil.logToFile("logs/Hack/Ban/????????????.txt", "\r\n " + FileoutputUtil.NowTime() + " ?????????" + chr.get().getName() + " ??????:" + chr.get().getJob() + " ??????: " + skillId + " check: " + (tickcount - lastAttackTickCount) + " " + "AtkDelay: " + AtkDelay);
                return;
            }*/
            if (GameConstants.getWuYanChi(skillId)) {
                FileoutputUtil.logToFile("logs/Hack/??????????????????.txt", "\r\n " + FileoutputUtil.NowTime() + " ?????????" + chr.get().getName() + " ??????:" + chr.get().getJob() + "?????????: " + skillId + "(" + SkillFactory.getSkillName(skillId) + ")" + " check: " + (tickcount - lastAttackTickCount) + " " + "AtkDelay: " + AtkDelay);

                //chr.get().add??????();
                //registerOffense(CheatingOffense.FASTATTACK, "???????????????????????????: " + skillId + " check: " + (tickcount - lastAttackTickCount) + " " + "AtkDelay: " + AtkDelay);
                if (WorldConstants.WUYANCHI) {
                    World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM??????] " + " ID " + chr.get().getId() + " " + chr.get().getName() + " ???????????????????????????: " + skillId + "(" + SkillFactory.getSkillName(skillId) + ")"));
                }
            }
        }

        if (chr.get().getDebugMessage()) {
            chr.get().dropMessage("Delay [" + skillId + "] = " + (tickcount - lastAttackTickCount) + ", " + (AtkDelay));
        }

        if (WorldConstants.LieDetector) {
            this.lastAttackTime = System.currentTimeMillis();
            if ((this.chr.get() != null) && (this.lastAttackTime - ((MapleCharacter) this.chr.get()).getChangeTime() > 60000)) {
                ((MapleCharacter) this.chr.get()).setChangeTime(false);

                if ((!GameConstants.isBossMap(chr.get().getMapId())) && (((MapleCharacter) this.chr.get()).getEventInstance() == null) && (((MapleCharacter) this.chr.get()).getMap().getMobsSize() >= 1)) {
                    this.inMapIimeCount += 1;
                    if (this.inMapIimeCount >= 30) {
                        World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM??????] " + " ID " + chr.get().getId() + " " + chr.get().getName() + " ?????????????????? 30 ???????????????????????????????????? "));
                    }
                    if (this.inMapIimeCount >= 30) {
                        this.inMapIimeCount = 0;
                        ((MapleCharacter) this.chr.get()).startLieDetector(false);
                        World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM??????] " + " ID " + chr.get().getId() + " " + chr.get().getName() + " ?????????????????? 30 ??????????????????????????????????????? "));
                    }
                }
            }
        }
        final long STime_TC = System.currentTimeMillis() - tickcount; // hack = - more
        if (Server_ClientAtkTickDiff - STime_TC > 1000) { // 250 is the ping, TODO
            if (GameConstants.getWuYanChi(skillId)) {
                if (WorldConstants.WUYANCHI) {
                    World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM??????] " + " ID " + chr.get().getId() + " " + chr.get().getName() + " ???????????????????????????: " + skillId + "(" + SkillFactory.getSkillName(skillId) + ")"));
                }
                //registerOffense(CheatingOffense.FASTATTACK2, "???????????????????????????: " + skillId + " check: " + (tickcount - lastAttackTickCount) + " " + "AtkDelay: " + AtkDelay);
            }
        }

        Server_ClientAtkTickDiff = STime_TC;

//	System.out.println("Delay [" + skillId + "] = " + (tickcount - lastAttackTickCount) + ", " + (Server_ClientAtkTickDiff - STime_TC));
//
        chr.get().updateTick(tickcount);
        lastAttackTickCount = tickcount;
    }

    public final void checkTakeDamage(final int damage) {
        numSequentialDamage++;
        lastDamageTakenTime = System.currentTimeMillis();

        // System.out.println("tb" + timeBetweenDamage);
        // System.out.println("ns" + numSequentialDamage);
        // System.out.println(timeBetweenDamage / 1500 + "(" + timeBetweenDamage / numSequentialDamage + ")");
        if (lastDamageTakenTime - takingDamageSince / 500 < numSequentialDamage) {
//            registerOffense(CheatingOffense.FAST_TAKE_DAMAGE);
        }
        if (lastDamageTakenTime - takingDamageSince > 4500) {
            takingDamageSince = lastDamageTakenTime;
            numSequentialDamage = 0;
        }
        /*	(non-thieves)
         Min Miss Rate: 2%
         Max Miss Rate: 80%
         (thieves)
         Min Miss Rate: 5%
         Max Miss Rate: 95%*/
        if (damage == 0) {
            numZeroDamageTaken++;
            if (numZeroDamageTaken >= 35) { // Num count MSEA a/b players
                numZeroDamageTaken = 0;
                registerOffense(CheatingOffense.HIGH_AVOID, "??????????????? ");
            }
        } else if (damage != -1) {
            numZeroDamageTaken = 0;
        }
    }

    public final void checkSameDamage(final int dmg, final double expected) {
        if (dmg > 2000 && lastDamage == dmg && chr.get() != null && (chr.get().getLevel() < 175 || dmg > expected * 2)) {
            numSameDamage++;

            if (numSameDamage > 5) {
                numSameDamage = 0;
                registerOffense(CheatingOffense.SAME_DAMAGE, numSameDamage + " ???, ????????????: " + dmg + ", ????????????: " + expected + " [??????: " + chr.get().getLevel() + ", ??????: " + chr.get().getJob() + "]");
            }
        } else {
            lastDamage = dmg;
            numSameDamage = 0;
        }
    }

    public final void checkMoveMonster(final Point pos) {
        if (pos == lastMonsterMove) {
            monsterMoveCount++;
            if (monsterMoveCount > 10) {
                //registerOffense(CheatingOffense.MOVE_MONSTERS, "Position: " + pos.x + ", " + pos.y);
                World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM??????] " + chr.get().getName() + " (??????: " + chr.get().getId() + ")????????????(" + chr.get().get??????() + ")! - ??????:" + chr.get().getMapId() + "(" + chr.get().getMap().getMapName() + ")"), true);
                monsterMoveCount = 0;
            }
        } else {
            lastMonsterMove = pos;
            monsterMoveCount = 1;
        }
    }

    public final void resetSummonAttack() {
        summonSummonTime = System.currentTimeMillis();
        numSequentialSummonAttack = 0;
    }

    public final boolean checkSummonAttack() {
        numSequentialSummonAttack++;
        //estimated
        // System.out.println(numMPRegens + "/" + allowedRegens);
        // long time = (System.currentTimeMillis() - summonSummonTime) / (2000 + 1) + 3l;
        //  if (time < numSequentialSummonAttack) {
        //        registerOffense(CheatingOffense.FAST_SUMMON_ATTACK, chr.get().getName() + "????????????????????? " + time + " < " + numSequentialSummonAttack);
        //      return false;
        //  }
        return true;
    }

    public final void checkDrop() {
        checkDrop(false);
    }

    public final void checkDrop(final boolean dc) {
        if ((System.currentTimeMillis() - lastDropTime) < 1000) {
            dropsPerSecond++;
            if (dropsPerSecond >= (dc ? 32 : 16) && chr.get() != null && !chr.get().isGM()) {
                if (dc) {
                    chr.get().getClient().getSession().close();
                } else {
                    chr.get().getClient().setMonitored(true);
                }
            }
        } else {
            dropsPerSecond = 0;
        }
        lastDropTime = System.currentTimeMillis();
    }

    public boolean canAvatarSmega2() {
        long time = 10 * 1000;
        if (chr.get() != null) {
            if (chr.get().getId() == 845 || chr.get().getId() == 5247 || chr.get().getId() == 12048) {
                time = 20 * 1000;
            }
            if (lastASmegaTime + time > System.currentTimeMillis() && !chr.get().isGM()) {
                return false;
            }
        }
        lastASmegaTime = System.currentTimeMillis();
        return true;
    }

    public synchronized boolean GMSpam(int limit, int type) {
        if (type < 0 || lastTime.length < type) {
            type = 1; // default xD
        }
        if (System.currentTimeMillis() < limit + lastTime[type]) {
            return true;
        }
        lastTime[type] = System.currentTimeMillis();
        return false;
    }

    public final void checkMsg() { //ALL types of msg. caution with number of  msgsPerSecond
        if ((System.currentTimeMillis() - lastMsgTime) < 1000) { //luckily maplestory has auto-check for too much msging
            msgsPerSecond++;
            /*            if (msgsPerSecond > 10 && chr.get() != null) {
             chr.get().getClient().getSession().close();
             }*/
        } else {
            msgsPerSecond = 0;
        }
        lastMsgTime = System.currentTimeMillis();
    }

    public final int getAttacksWithoutHit() {
        return attacksWithoutHit;
    }

    public final void setAttacksWithoutHit(final boolean increase) {
        if (increase) {
            this.attacksWithoutHit++;
        } else {
            this.attacksWithoutHit = 0;
        }
    }

    public final void registerOffense(final CheatingOffense offense) {
        registerOffense(offense, null);
    }

    public final void registerOffense(final CheatingOffense offense, final String param) {
        final MapleCharacter chrhardref = chr.get();
        //if (chrhardref == null || !offense.isEnabled() || chrhardref.isClone()) {
        if (chrhardref == null || !offense.isEnabled() || chrhardref.isClone() || chrhardref.isGM()) {
            return;
        }
        //HP??????????????????
       // if (chr.get().hasGmLevel(5)) {
       //     chr.get().dropMessage("?????????" + offense + " ?????????" + param);
       // }
        CheatingOffenseEntry entry = null;
        rL.lock();
        try {
            entry = offenses.get(offense);
        } finally {
            rL.unlock();
        }
        if (entry != null && entry.isExpired()) {
            expireEntry(entry);
            entry = null;
        }
        if (entry == null) {
            entry = new CheatingOffenseEntry(offense, chrhardref.getId());
        }
        if (param != null) {
            entry.setParam(param);
        }
        entry.incrementCount();
        if (offense.shouldAutoban(entry.getCount())) {
            final byte type = offense.getBanType();
            String outputFileName;
            if (type == 1) {
                AutobanManager.getInstance().autoban(chrhardref.getClient(), StringUtil.makeEnumHumanReadable(offense.name()));
            } else if (type == 2) {
                outputFileName = "??????";
                World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM??????] " + chrhardref.getName() + " ???????????? ??????: " + offense.toString() + " ??????: " + (param == null ? "" : (" - " + param))));
                FileoutputUtil.logToFile("logs/Hack/" + outputFileName + ".txt", "\r\n " + FileoutputUtil.NowTime() + " ?????????" + chr.get().getName() + " ?????????" + offense.toString() + " ????????? " + (param == null ? "" : (" - " + param)));
                chrhardref.getClient().getSession().close();
            } else if (type == 3) {
                boolean ban = false;
                outputFileName = "??????";
                String show = "????????????????????????";
                String real = "";
                if (offense.toString() == "ITEMVAC_SERVER") {
                    outputFileName = "????????????";
                    real = "??????????????????";
                    if (!PiPiConfig.getAutoban()) {
                        ban = false;
                    }
                } else if (offense.toString() == "FAST_SUMMON_ATTACK") {
                    outputFileName = "??????????????????";
                    real = "??????????????????????????????";
                } else if (offense.toString() == "MOB_VAC") {
                    outputFileName = "??????";
                    real = "????????????";
                    if (!PiPiConfig.getAutoban()) {
                        ban = false;
                    }
                } else if (offense.toString() == "ATTACK_FARAWAY_MONSTER_BAN") {
                    outputFileName = "?????????";
                    real = "???????????????";
                    if (!PiPiConfig.getAutoban()) {
                        ban = false;
                    }
                } else {
                    ban = false;
                    World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM??????] " + MapleCharacterUtil.makeMapleReadable(chrhardref.getName()) + " (??????: " + chrhardref.getId() + " )????????????! " + StringUtil.makeEnumHumanReadable(offense.name()) + (param == null ? "" : (" - " + param))));
                }

                if (chr.get().hasGmLevel(1)) {
                    chr.get().dropMessage("????????????: " + real + " param: " + (param == null ? "" : (" - " + param)));
                } else if (ban) {
                    FileoutputUtil.logToFile("logs/Hack/Ban/" + outputFileName + ".txt", "\r\n " + FileoutputUtil.NowTime() + " ?????????" + chr.get().getName() + " ?????????" + offense.toString() + " ????????? " + (param == null ? "" : (" - " + param)));
                    World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, "[????????????] " + chrhardref.getName() + " ??????" + show + "??????????????????????????????"));
                    World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM??????] " + chrhardref.getName() + " " + real + "????????????! "));
                    chrhardref.ban(chrhardref.getName() + real, false, false, false);
                    chrhardref.getClient().getSession().close();
                } else {
                    FileoutputUtil.logToFile("logs/Hack/" + outputFileName + ".txt", "\r\n " + FileoutputUtil.NowTime() + " ?????????" + chr.get().getName() + " ?????????" + offense.toString() + " ????????? " + (param == null ? "" : (" - " + param)));
                }
            }
            gm_message = 100;
            return;
        }

        wL.lock();

        try {
            offenses.put(offense, entry);
        } finally {
            wL.unlock();
        }
        switch (offense) {
            case FAST_SUMMON_ATTACK:
            case ITEMVAC_SERVER:
            case MOB_VAC:
            case HIGH_DAMAGE_MAGIC:
            case HIGH_DAMAGE_MAGIC_2:
            case HIGH_DAMAGE:
            case HIGH_DAMAGE_2:
            case ATTACK_FARAWAY_MONSTER:
            //case ATTACK_FARAWAY_MONSTER_SUMMON:
            case SAME_DAMAGE:
                gm_message--;
                boolean log = false;
                String out_log = "";
                String show = offense.name();
                switch (show) {
                    case "ATTACK_FARAWAY_MONSTER":
                        show = "?????????";
                        out_log = "??????????????????";
                        log = true;
                        break;
                    case "MOB_VAC":
                        show = "????????????";
                        out_log = "??????";
                        log = true;
                        break;
                }
                if (gm_message % 5 == 0) {
                    World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM??????] " + chrhardref.getName() + " (??????:" + chrhardref.getId() + ")????????????! " + show + (param == null ? "" : (" - " + param))));
                    if (log) {
                        FileoutputUtil.logToFile("logs/Hack/" + out_log + ".txt", "\r\n" + FileoutputUtil.NowTime() + " " + chrhardref.getName() + " (??????:" + chrhardref.getId() + ")????????????! " + show + (param == null ? "" : (" - " + param)));
                    }
                }
                if (gm_message == 0) {
                    World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[????????????] " + chrhardref.getName() + " (??????: " + chrhardref.getId() + " )???????????????" + show + (param == null ? "" : (" - " + param))));
                    AutobanManager.getInstance().autoban(chrhardref.getClient(), StringUtil.makeEnumHumanReadable(offense.name()));
                    gm_message = 100;
                }
                break;
        }
        CheatingOffensePersister.getInstance().persistEntry(entry);
    }

    public void updateTick(int newTick) {
        if (newTick == lastTickCount) { //definitely packet spamming
/*	    if (tickSame >= 5) {
             chr.get().getClient().getSession().close(); //i could also add a check for less than, but i'm not too worried at the moment :)
             } else {*/
            tickSame++;
//	    }
        } else {
            tickSame = 0;
        }
        lastTickCount = newTick;
    }

    public final void expireEntry(final CheatingOffenseEntry coe) {
        wL.lock();
        try {
            offenses.remove(coe.getOffense());
        } finally {
            wL.unlock();
        }
    }

    public final int getPoints() {
        int ret = 0;
        CheatingOffenseEntry[] offenses_copy;
        rL.lock();
        try {
            offenses_copy = offenses.values().toArray(new CheatingOffenseEntry[offenses.size()]);
        } finally {
            rL.unlock();
        }
        for (final CheatingOffenseEntry entry : offenses_copy) {
            if (entry.isExpired()) {
                expireEntry(entry);
            } else {
                ret += entry.getPoints();
            }
        }
        return ret;
    }

    public final Map<CheatingOffense, CheatingOffenseEntry> getOffenses() {
        return Collections.unmodifiableMap(offenses);
    }

    public final String getSummary() {
        final StringBuilder ret = new StringBuilder();
        final List<CheatingOffenseEntry> offenseList = new ArrayList<>();
        rL.lock();
        try {
            for (final CheatingOffenseEntry entry : offenses.values()) {
                if (!entry.isExpired()) {
                    offenseList.add(entry);
                }
            }
        } finally {
            rL.unlock();
        }
        Collections.sort(offenseList, new Comparator<CheatingOffenseEntry>() {

            @Override
            public final int compare(final CheatingOffenseEntry o1, final CheatingOffenseEntry o2) {
                final int thisVal = o1.getPoints();
                final int anotherVal = o2.getPoints();
                return (thisVal < anotherVal ? 1 : (thisVal == anotherVal ? 0 : -1));
            }
        });
        final int to = Math.min(offenseList.size(), 4);
        for (int x = 0; x < to; x++) {
            ret.append(StringUtil.makeEnumHumanReadable(offenseList.get(x).getOffense().name()));
            ret.append(": ");
            ret.append(offenseList.get(x).getCount());
            if (x != to - 1) {
                ret.append(" ");
            }
        }
        return ret.toString();
    }

    public final void dispose() {
        if (invalidationTask != null) {
            invalidationTask.cancel(false);
        }
        invalidationTask = null;

    }

    private final class InvalidationTask implements Runnable {

        @Override
        public final void run() {
            CheatingOffenseEntry[] offenses_copy;
            rL.lock();
            try {
                offenses_copy = offenses.values().toArray(new CheatingOffenseEntry[offenses.size()]);
            } finally {
                rL.unlock();
            }
            for (CheatingOffenseEntry offense : offenses_copy) {
                if (offense.isExpired()) {
                    expireEntry(offense);
                }
            }
            if (chr.get() == null) {
                dispose();
            }
        }
    }

    public boolean canSaveDB() {
        if ((System.currentTimeMillis() - lastSaveTime < 5 * 60 * 1000)) {
            return false;
        }
        this.lastSaveTime = System.currentTimeMillis();
        return true;
    }

    public int getlastSaveTime() {
        return (int) ((System.currentTimeMillis() - this.lastSaveTime) / 1000);
    }

    public int getlastLieTime() {
        return (int) ((System.currentTimeMillis() - this.lastLieTime) / 1000);
    }

    public boolean canLieDetector() {
        if ((this.lastLieDetectorTime + 300000 > System.currentTimeMillis()) && (this.chr.get() != null)) {
            return false;
        }
        this.lastLieDetectorTime = System.currentTimeMillis();
        return true;
    }

    public void resetInMapIimeCount() {
        this.inMapIimeCount = 0;
    }
}

package server;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import constants.GameConstants;
import constants.ServerConstants;
import database.DBConPool;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class RandomRewards {

    private final static RandomRewards instance = new RandomRewards();
    private List<Integer> compiledGold = null;
    private List<Integer> compiledJxbox = null;
    private List<Integer> compiledSilver = null;
    private List<Integer> compiledFishing = null;
    private List<Integer> compiledEvent = null;
    private List<Integer> compiledEventC = null;
    private List<Integer> compiledEventB = null;
    private List<Integer> compiledEventA = null;
    public Map<Integer, String> FishingRewardItemNameMap = null;

    public static RandomRewards getInstance() {
        return instance;
    }

    protected RandomRewards() {
        System.out.println("【信息】 随机奖励系统");
        // Gold Box
        List<Integer> returnArray = new ArrayList<>();

        processRewards(returnArray, GameConstants.goldrewards);

        compiledGold = returnArray;

        // 惊新年禮盒
        returnArray = new ArrayList<>();

        processRewards(returnArray, GameConstants.Jxboxrewards);

        compiledJxbox = returnArray;

        // Silver Box
        returnArray = new ArrayList<>();

        processRewards(returnArray, GameConstants.silverrewards);

        compiledSilver = returnArray;

        // Fishing Rewards
         getFishingRewards();
        //returnArray = new ArrayList<>();

        //processRewards(returnArray, GameConstants.fishingReward);

        //compiledFishing = returnArray;

        // Event Rewards
        returnArray = new ArrayList<>();

        processRewards(returnArray, GameConstants.eventCommonReward);

        compiledEventC = returnArray;

        returnArray = new ArrayList<>();

        processRewards(returnArray, GameConstants.eventUncommonReward);

        compiledEventB = returnArray;

        returnArray = new ArrayList<>();

        processRewards(returnArray, GameConstants.eventRareReward);

        compiledEventA = returnArray;

        returnArray = new ArrayList<>();

        processRewards(returnArray, GameConstants.eventSuperReward);

        compiledEvent = returnArray;
    }

    private void processRewards(final List<Integer> returnArray, final int[] list) {
        int lastitem = 0;
        for (int i = 0; i < list.length; i++) {
            if (i % 2 == 0) { // Even
                lastitem = list[i];
            } else { // Odd
                for (int j = 0; j < list[i]; j++) {
                    returnArray.add(lastitem);
                }
            }
        }
        Collections.shuffle(returnArray);
    }

    public final int getGoldBoxReward() {
        return compiledGold.get(Randomizer.nextInt(compiledGold.size()));
    }
    
    public final int getJxBoxReward() {
        return compiledJxbox.get(Randomizer.nextInt(compiledJxbox.size()));
    }

    public final int getSilverBoxReward() {
        return compiledSilver.get(Randomizer.nextInt(compiledSilver.size()));
    }

    public final int getFishingReward() {
        return compiledFishing.get(Randomizer.nextInt(compiledFishing.size()));
    }

    public final int getEventReward() {
        final int chance = Randomizer.nextInt(100);
        if (chance < 50) {
            return compiledEventC.get(Randomizer.nextInt(compiledEventC.size()));
        } else if (chance < 80) {
            return compiledEventB.get(Randomizer.nextInt(compiledEventB.size()));
        } else if (chance < 95) {
            return compiledEventA.get(Randomizer.nextInt(compiledEventA.size()));
        } else {
            return compiledEvent.get(Randomizer.nextInt(compiledEvent.size()));
        }
    }

    public int[] loadFishingRewardFromDb() {
        List<Integer> data = new ArrayList<Integer>();

        try (Connection con = DBConPool.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT itemid, chance FROM 钓鱼物品 WHERE expiration = 1");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                data.add(rs.getInt("itemid"));
                data.add(rs.getInt("chance"));
            }

            rs.close();
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("查询钓鱼奖励数据出错 - 数据库查询失败：" + Ex);
        }

        return toIntArray(data);
    }

    private Map<Integer, String> loadFishingRewardsMapFromDb() {
        Map<Integer, String> data = new HashMap<Integer, String>();

        try (Connection con = DBConPool.getInstance().getDataSource().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT itemid, name FROM 钓鱼物品 WHERE expiration = 1");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                data.put(rs.getInt("itemid"), rs.getString("name"));
            }

            rs.close();
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("查询钓鱼奖励数据出错2 - 数据库查询失败：" + Ex);
        }

        return data;
    }

    public static int[] toIntArray(List<Integer> list) {
        int[] ret = new int[list.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = list.get(i);
        }
        return ret;
    }
    
    private void getFishingRewards() {
        FishingRewardItemNameMap = loadFishingRewardsMapFromDb();
        
        List<Integer> list = new ArrayList<Integer>();
        processRewards(list, loadFishingRewardFromDb());
        compiledFishing = list;
       
    }
    public void refreshFishingRewards() {
        FishingRewardItemNameMap.clear();
        compiledFishing.clear();
        getFishingRewards();
        System.out.println("    -> 已加载钓鱼随机奖励物品 " + compiledFishing.size());
    }
    
}

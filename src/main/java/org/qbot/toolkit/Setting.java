package org.qbot.toolkit;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

/**
 * Setting class
 *
 * @author 649953543@qq.com
 * @date 2021/09/22
 */

public class Setting {

    public static long getQq() {
        return main().getLongValue("QQ");
    }

    public static String getAppId() {
        return main().getJSONObject("BaiDuAPI").getString("APP_ID");
    }

    public static String getApiKey() {
        return main().getJSONObject("BaiDuAPI").getString("API_KEY");
    }

    public static String getSecretKey() {
        return main().getJSONObject("BaiDuAPI").getString("SECRET_KEY");
    }

    public static boolean getAgreeFriend() {
        return main().getBooleanValue("AgreeFriend");
    }

    public static boolean getAgreeGroup() {
        return main().getBooleanValue("AgreeGroup");
    }

    public static boolean getAi() {
        return main().getBooleanValue("AI");
    }

    public static boolean getAutoFortune() {
        return main().getJSONObject("Auto").getBooleanValue("AutoFortune");
    }

    public static boolean getAutoNews() {
        return main().getJSONObject("Auto").getBooleanValue("AutoNews");
    }

    public static boolean getAutoTips() {
        return main().getJSONObject("Auto").getBooleanValue("AutoTips");
    }

    public static JSONArray getGroup() {
        return main().getJSONObject("Auto").getJSONArray("Group");
    }

    public static boolean getGroupManagement() {
        return main().getJSONObject("GroupManagement").getBooleanValue("Open");
    }

    public static long getAdminQQ() {
        return main().getJSONObject("GroupManagement").getLongValue("AdminQQ");
    }

    private static final String VERSION = "Version";
    private static final String VERSION_NUM = "2.1";

    public void getVersion() {
        boolean isUpdate = false;
        try {
            if (!main().getString(VERSION).equals(VERSION_NUM)) {
                isUpdate = true;
            }
        } catch (Exception e) {
            isUpdate = true;
        } finally {
            if (isUpdate) {
                System.out.println("正在更新配置文件");
                SetSetting.setFile(VERSION_NUM, getQq(), getAppId(), getApiKey(), getSecretKey(), getAgreeFriend(), getAgreeGroup(), getAi(), getAutoFortune(), getAutoNews(), getAutoTips(), getGroup(), false, 0);
            }
        }

    }


    /**
     * 动态更新配置文件
     */

    public static boolean updateVersion(boolean options, long value) {
        System.out.println("正在更新配置文件");
        JSONArray jsonArray = getGroup();
        if (options) {
            for (int i = 0; i < jsonArray.size(); i++) {
                long longValue = jsonArray.getLongValue(i);
                if (longValue == value) {
                    return false;
                }
            }
            jsonArray.set(jsonArray.size(), value);
        } else {
            for (int i = 0; i < jsonArray.size(); i++) {
                long longValue = jsonArray.getLongValue(i);
                if (longValue == value) {
                    jsonArray.remove(i);
                }
            }
        }
        SetSetting.setFile(VERSION_NUM, getQq(), getAppId(), getApiKey(), getSecretKey(), getAgreeFriend(), getAgreeGroup(), getAi(), getAutoFortune(), getAutoNews(), getAutoTips(), jsonArray, getGroupManagement(), getAdminQQ());
        return true;
    }

    public static boolean updateVersion(String options, boolean value) {
        System.out.println("正在更新配置文件");
        switch (options) {
            case "Friend":
                SetSetting.setFile(VERSION_NUM, getQq(), getAppId(), getApiKey(), getSecretKey(), value, getAgreeGroup(), getAi(), getAutoFortune(), getAutoNews(), getAutoTips(), getGroup(), getGroupManagement(), getAdminQQ());
                return true;
            case "Group":
                SetSetting.setFile(VERSION_NUM, getQq(), getAppId(), getApiKey(), getSecretKey(), getAgreeFriend(), value, getAi(), getAutoFortune(), getAutoNews(), getAutoTips(), getGroup(), getGroupManagement(), getAdminQQ());
                return true;
            case "AI":
                SetSetting.setFile(VERSION_NUM, getQq(), getAppId(), getApiKey(), getSecretKey(), getAgreeFriend(), getAgreeGroup(), value, getAutoFortune(), getAutoNews(), getAutoTips(), getGroup(), getGroupManagement(), getAdminQQ());
                return true;
            case "Fortune":
                SetSetting.setFile(VERSION_NUM, getQq(), getAppId(), getApiKey(), getSecretKey(), getAgreeFriend(), getAgreeGroup(), getAi(), value, getAutoNews(), getAutoTips(), getGroup(), getGroupManagement(), getAdminQQ());
                return true;
            case "News":
                SetSetting.setFile(VERSION_NUM, getQq(), getAppId(), getApiKey(), getSecretKey(), getAgreeFriend(), getAgreeGroup(), getAi(), getAutoFortune(), value, getAutoTips(), getGroup(), getGroupManagement(), getAdminQQ());
                return true;
            case "Tips":
                SetSetting.setFile(VERSION_NUM, getQq(), getAppId(), getApiKey(), getSecretKey(), getAgreeFriend(), getAgreeGroup(), getAi(), getAutoFortune(), getAutoNews(), value, getGroup(), getGroupManagement(), getAdminQQ());
                return true;
            case "GroupManagement":
                SetSetting.setFile(VERSION_NUM, getQq(), getAppId(), getApiKey(), getSecretKey(), getAgreeFriend(), getAgreeGroup(), getAi(), getAutoFortune(), getAutoNews(), getAutoTips(), getGroup(), value, getAdminQQ());
                return true;
            default:
                System.out.println("更新失败");
                return false;
        }
    }

    public static JSONObject main() {
        String filePath = Utils.getPluginsPath() + "/setting.yml";
        Yaml yaml = new Yaml();
        Map<String, Object> map = null;
        try {
            map = yaml.load(new FileInputStream(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        assert map != null;
        return new JSONObject(map);
    }

}

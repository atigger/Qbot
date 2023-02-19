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
        try {
            return main().getLongValue("QQ");
        } catch (Exception e) {
            return 0;
        }
    }

    public static String getAppId() {
        try {
            if (main().getJSONObject("BaiDuAPI").getString("APP_ID") == null) {
                return "";
            }
            return main().getJSONObject("BaiDuAPI").getString("APP_ID");
        } catch (Exception e) {
            return "";
        }
    }

    public static String getApiKey() {
        try {
            if (main().getJSONObject("BaiDuAPI").getString("API_KEY") == null) {
                return "";
            }
            return main().getJSONObject("BaiDuAPI").getString("API_KEY");
        } catch (Exception e) {
            return "";
        }
    }

    public static String getSecretKey() {
        try {
            if (main().getJSONObject("BaiDuAPI").getString("SECRET_KEY") == null) {
                return "";
            }
            return main().getJSONObject("BaiDuAPI").getString("SECRET_KEY");
        } catch (Exception e) {
            return "";
        }
    }

    public static int getImageNum() {
        try {
            return main().getIntValue("ImageNum");
        } catch (Exception e) {
            return 1;
        }
    }

    public static int getImageRecall() {
        try {
            return main().getIntValue("ImageRecall");
        } catch (Exception e) {
            return 119;
        }
    }

    public static boolean getAgreeFriend() {
        try {
            return main().getBooleanValue("AgreeFriend");
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean getAgreeGroup() {
        try {
            return main().getBooleanValue("AgreeGroup");
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean getOldAi() {
        try {
            return main().getBooleanValue("AI");
        } catch (Exception e) {
            return getAi();
        }
    }

    public static boolean getAi() {
        try {
            return main().getJSONObject("AI").getBooleanValue("Open");
        } catch (Exception e) {
            return false;
        }
    }

    public static String getAiApiKey() {
        try {
            if (main().getJSONObject("AI").getString("Api_Key") == null) {
                return "";
            }
            return main().getJSONObject("AI").getString("Api_Key");
        } catch (Exception e) {
            return "";
        }
    }

    public static String getAiApiSecret() {
        try {
            if (main().getJSONObject("AI").getString("Api_Secret") == null) {
                return "";
            }
            return main().getJSONObject("AI").getString("Api_Secret");
        } catch (Exception e) {
            return "";
        }
    }

    public static boolean getAutoFortune() {
        try {
            return main().getJSONObject("Auto").getBooleanValue("AutoFortune");
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean getAutoNews() {
        try {
            return main().getJSONObject("Auto").getBooleanValue("AutoNews");
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean getAutoTips() {
        try {
            return main().getJSONObject("Auto").getBooleanValue("AutoTips");
        } catch (Exception e) {
            return false;
        }
    }

    public static JSONArray getGroup() {
        try {
            return main().getJSONObject("Auto").getJSONArray("Group");
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    public static boolean getGroupManagement() {
        try {
            return main().getJSONObject("GroupManagement").getBooleanValue("Open");
        } catch (Exception e) {
            return false;
        }
    }

    public static long getAdminQQ() {
        try {
            return main().getJSONObject("GroupManagement").getLongValue("AdminQQ");
        } catch (Exception e) {
            return 0;
        }
    }

    public static String getMusicAPIURL() {
        try {
            if (main().getString("MusicAPIURL") == null) {
                return "";
            }
            return main().getString("MusicAPIURL");
        } catch (Exception e) {
            return "";
        }
    }

    private static final String VERSION = "Version";
    public static final String VERSION_NUM = "3.2";

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
                SetSetting.setFile(VERSION_NUM, getQq(), getAppId(), getApiKey(), getSecretKey(), getImageNum(), getImageRecall(), getAgreeFriend(), getAgreeGroup(), getOldAi(), getAiApiKey(), getAiApiSecret(), getAutoFortune(), getAutoNews(), getAutoTips(), getGroup(), getGroupManagement(), getAdminQQ(), "");
                System.out.println("更新配置文件成功");
            }
        }

    }


    /**
     * 动态更新配置文件
     */

    public static boolean updateConfig(boolean options, long value) {
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
        SetSetting.setFile(VERSION_NUM, getQq(), getAppId(), getApiKey(), getSecretKey(), getImageNum(),
                getImageRecall(), getAgreeFriend(), getAgreeGroup(), getAi(), getAiApiKey(), getAiApiSecret(),
                getAutoFortune(), getAutoNews(), getAutoTips(), jsonArray, getGroupManagement(), getAdminQQ(), getMusicAPIURL());
        return true;
    }

    public static boolean updateConfig(String options, boolean value) {
        System.out.println("正在更新配置文件");
        switch (options) {
            case "Friend":
                SetSetting.setFile(VERSION_NUM, getQq(), getAppId(), getApiKey(), getSecretKey(), getImageNum(), getImageRecall(), value, getAgreeGroup(), getAi(), getAiApiKey(), getAiApiSecret(), getAutoFortune(), getAutoNews(), getAutoTips(), getGroup(), getGroupManagement(), getAdminQQ(), getMusicAPIURL());
                return true;
            case "Group":
                SetSetting.setFile(VERSION_NUM, getQq(), getAppId(), getApiKey(), getSecretKey(), getImageNum(), getImageRecall(), getAgreeFriend(), value, getAi(), getAiApiKey(), getAiApiSecret(), getAutoFortune(), getAutoNews(), getAutoTips(), getGroup(), getGroupManagement(), getAdminQQ(), getMusicAPIURL());
                return true;
            case "AI":
                SetSetting.setFile(VERSION_NUM, getQq(), getAppId(), getApiKey(), getSecretKey(), getImageNum(), getImageRecall(), getAgreeFriend(), getAgreeGroup(), value, getAiApiKey(), getAiApiSecret(), getAutoFortune(), getAutoNews(), getAutoTips(), getGroup(), getGroupManagement(), getAdminQQ(), getMusicAPIURL());
                return true;
            case "Fortune":
                SetSetting.setFile(VERSION_NUM, getQq(), getAppId(), getApiKey(), getSecretKey(), getImageNum(), getImageRecall(), getAgreeFriend(), getAgreeGroup(), getAi(), getAiApiKey(), getAiApiSecret(), value, getAutoNews(), getAutoTips(), getGroup(), getGroupManagement(), getAdminQQ(), getMusicAPIURL());
                return true;
            case "News":
                SetSetting.setFile(VERSION_NUM, getQq(), getAppId(), getApiKey(), getSecretKey(), getImageNum(), getImageRecall(), getAgreeFriend(), getAgreeGroup(), getAi(), getAiApiKey(), getAiApiSecret(), getAutoFortune(), value, getAutoTips(), getGroup(), getGroupManagement(), getAdminQQ(), getMusicAPIURL());
                return true;
            case "Tips":
                SetSetting.setFile(VERSION_NUM, getQq(), getAppId(), getApiKey(), getSecretKey(), getImageNum(), getImageRecall(), getAgreeFriend(), getAgreeGroup(), getAi(), getAiApiKey(), getAiApiSecret(), getAutoFortune(), getAutoNews(), value, getGroup(), getGroupManagement(), getAdminQQ(), getMusicAPIURL());
                return true;
            case "GroupManagement":
                SetSetting.setFile(VERSION_NUM, getQq(), getAppId(), getApiKey(), getSecretKey(), getImageNum(), getImageRecall(), getAgreeFriend(), getAgreeGroup(), getAi(), getAiApiKey(), getAiApiSecret(), getAutoFortune(), getAutoNews(), getAutoTips(), getGroup(), value, getAdminQQ(), getMusicAPIURL());
                return true;
            default:
                System.out.println("更新失败");
                return false;
        }
    }

    public static boolean updateConfig(int options, int value) {
        try {
            switch (options) {
                case 1:
                    SetSetting.setFile(VERSION_NUM, getQq(), getAppId(), getApiKey(), getSecretKey(), value, getImageRecall(), getAgreeFriend(), getAgreeGroup(), getAi(), getAiApiKey(), getAiApiSecret(), getAutoFortune(), getAutoNews(), getAutoTips(), getGroup(), getGroupManagement(), getAdminQQ(), getMusicAPIURL());
                    break;
                case 2:
                    SetSetting.setFile(VERSION_NUM, getQq(), getAppId(), getApiKey(), getSecretKey(), getImageNum(), value, getAgreeFriend(), getAgreeGroup(), getAi(), getAiApiKey(), getAiApiSecret(), getAutoFortune(), getAutoNews(), getAutoTips(), getGroup(), getGroupManagement(), getAdminQQ(), getMusicAPIURL());
                    break;
                default:
                    System.out.println("更新失败");
                    return false;
            }
            return true;
        } catch (Exception e) {
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
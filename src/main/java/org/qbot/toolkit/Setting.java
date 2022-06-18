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

    public long getQq() {
        return main().getLongValue("QQ");
    }

    public String getAppId() {
        return main().getJSONObject("BaiDuAPI").getString("APP_ID");
    }

    public String getApiKey() {
        return main().getJSONObject("BaiDuAPI").getString("API_KEY");
    }

    public String getSecretKey() {
        return main().getJSONObject("BaiDuAPI").getString("SECRET_KEY");
    }

    public boolean getAgreeFriend() {
        return main().getBooleanValue("AgreeFriend");
    }

    public boolean getAgreeGroup() {
        return main().getBooleanValue("AgreeGroup");
    }

    public boolean getAi() {
        return main().getBooleanValue("AI");
    }

    public boolean getAutoFortune() {
        return main().getJSONObject("Auto").getBooleanValue("AutoFortune");
    }

    public boolean getAutoNews() {
        return main().getJSONObject("Auto").getBooleanValue("AutoNews");
    }

    public boolean getAutoTips() {
        return main().getJSONObject("Auto").getBooleanValue("AutoTips");
    }

    public JSONArray getGroup() {
        return main().getJSONObject("Auto").getJSONArray("Group");
    }

    public boolean getGroupManagement() {
        return main().getJSONObject("GroupManagement").getBooleanValue("Open");
    }

    public long getAdminQQ() {
        return main().getJSONObject("GroupManagement").getLongValue("AdminQQ");
    }

    private static final String VERSION = "Version";
    private static final String VERSION_NUM = "2.1";

    public void getVersion() {
        SetSetting setSetting = new SetSetting();
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
                setSetting.setFile(VERSION_NUM, getQq(), getAppId(), getApiKey(), getSecretKey(), getAgreeFriend(), getAgreeGroup(), getAi(), getAutoFortune(), getAutoNews(), getAutoTips(), getGroup(), false, 0);
            }
        }

    }

    public JSONObject main() {
        Utils utils = new Utils();
        String filePath = utils.getPluginsPath() + "/setting.yml";
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

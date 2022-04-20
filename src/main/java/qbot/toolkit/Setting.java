package org.qbot.toolkit;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
        return main().getLong("QQ");
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

    public boolean getRane() {
        return main().getBoolean("Rane");
    }

    public boolean getAgreeFriend() {
        return main().getBoolean("AgreeFriend");
    }

    public boolean getAgreeGroup() {
        return main().getBoolean("AgreeGroup");
    }

    public boolean getAi() {
        return main().getBoolean("AI");
    }

    public boolean getAutoFortune() {
        return main().getJSONObject("Auto").getBoolean("AutoFortune");
    }

    public boolean getAutoNews() {
        return main().getJSONObject("Auto").getBoolean("AutoNews");
    }

    public boolean getAutoTips() {
        return main().getJSONObject("Auto").getBoolean("AutoTips");
    }

    public JSONArray getGroup() {
        return main().getJSONObject("Auto").getJSONArray("Group");
    }

    public boolean getGroupManagement() {
        return main().getJSONObject("GroupManagement").getBoolean("Open");
    }

    public long getAdminQQ() {
        return main().getJSONObject("GroupManagement").getLong("AdminQQ");
    }

    public void getVersion() {
        SetSetting setSetting = new SetSetting();
        boolean isUpdate = false;
        try {
            if (!main().getString("Version").equals("2.0")) {
                isUpdate = true;
            }
        } catch (Exception e) {
            isUpdate = true;
        } finally {
            if (isUpdate) {
                System.out.println("正在更新配置文件");
                setSetting.setFile("2.0", getQq(), getAppId(), getApiKey(), getSecretKey(), getRane(), getAgreeFriend(), getAgreeGroup(), getAi(), getAutoFortune(), getAutoNews(), getAutoTips(), getGroup(), false, 0);
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

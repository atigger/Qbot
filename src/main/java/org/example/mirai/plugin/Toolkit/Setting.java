package org.example.mirai.plugin.Toolkit;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

public class Setting {
    private long QQ;
    private String APP_ID;
    private String API_KEY;
    private String SECRET_KEY;
    private boolean Rane;
    private boolean AgreeIngroup;
    private boolean AgreeFriend;
    private boolean AgreeGroup;
    private boolean AI;
    private boolean AutoFortune;
    private boolean AutoNews;
    private boolean AutoTips;
    private long[] Group;


    public long getQQ() {
        return main().getLong("QQ");
    }

    public String getAPP_ID() {
        return main().getJSONObject("BaiDuAPI").getString("APP_ID");
    }

    public String getAPI_KEY() {
        return main().getJSONObject("BaiDuAPI").getString("API_KEY");
    }

    public String getSECRET_KEY() {
        return main().getJSONObject("BaiDuAPI").getString("SECRET_KEY");
    }

    public boolean getRane() {
        return main().getBoolean("Rane");
    }

    public boolean getAgreeIngroup() {
        return main().getBoolean("AgreeIngroup");
    }

    public boolean getAgreeFriend() {
        return main().getBoolean("AgreeFriend");
    }

    public boolean getAgreeGroup() {
        return main().getBoolean("AgreeGroup");
    }

    public boolean getAI() {
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

    public JSONObject main() {
        Utils utils = new Utils();
        String file_path = utils.get_plugins_path() + "/setting.yml";
        Yaml yaml = new Yaml();
        Map<String, Object> map = null;
        try {
            map = yaml.load(new FileInputStream(file_path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = new JSONObject(map);
        return jsonObject;
    }

}

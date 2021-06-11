package org.example.mirai.plugin.Toolkit;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class SetSetting {
    String config = "#机器人QQ\n" +
            "QQ: 0\n" +
            "#百度语音API\n" +
            "BaiDuAPI:\n" +
            "  APP_ID: \"\"\n" +
            "  API_KEY: \"\"\n" +
            "  SECRET_KEY: \"\"\n" +
            "#燃鹅操作开关\n" +
            "Rane: false\n" +
            "#自动同意别人入群\n" +
            "AgreeIngroup: false\n" +
            "#自动同意好友请求\n" +
            "AgreeFriend: false\n" +
            "#自动同意邀请入群请求\n" +
            "AgreeGroup: false\n" +
            "#智能聊天开关\n" +
            "AI: false\n" +
            "#自动化操作\n" +
            "Auto:\n" +
            "  #自动发送运势信息\n" +
            "  AutoFortune: false\n" +
            "  #自动每日新闻信息\n" +
            "  AutoNews: false\n" +
            "  #自动发送饮茶小提示\n" +
            "  AutoTips: false\n" +
            "  #需要自动发送的群列表 用,隔开\n" +
            "  Group: []";

    public void set_file() {
        Utils utils = new Utils();
        File file = new File(utils.get_plugins_path() + "/setting.yml");
        try {
            FileOutputStream writerStream = new FileOutputStream(file);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(writerStream, StandardCharsets.UTF_8));
            writer.write(config);
            writer.close();
        } catch (IOException e) {
        }
    }

}

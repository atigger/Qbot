package org.qbot.toolkit;

import com.alibaba.fastjson.JSONArray;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * SetSetting class
 *
 * @author 649953543@qq.com
 * @date 2021/09/22
 */

public class SetSetting {
    public void setFile(String version, long qq, String appid, String appKey, String secretKey, boolean rane, boolean agreeFriend,
                        boolean agreeGroup, boolean AI, boolean autoFortune, boolean autoNews, boolean autoTips, JSONArray groupList,
                        boolean groupManagement, long AdminQQ) {
        String config = "#配置文件版本\n" +
                "Version: " + version + "\n" +
                "#机器人QQ\n" +
                "QQ: " + qq + "\n" +
                "#百度语音API\n" +
                "BaiDuAPI:\n" +
                "  APP_ID: \"" + appid + "\"\n" +
                "  API_KEY: \"" + appKey + "\"\n" +
                "  SECRET_KEY: \"" + secretKey + "\"\n" +
                "#燃鹅操作开关\n" +
                "Rane: " + rane + "\n" +
                "#自动同意好友请求\n" +
                "AgreeFriend: " + agreeFriend + "\n" +
                "#自动同意邀请入群请求\n" +
                "AgreeGroup: " + agreeGroup + "\n" +
                "#智能聊天开关\n" +
                "AI: " + AI + "\n" +
                "#自动化操作\n" +
                "Auto:\n" +
                "  #自动发送运势信息\n" +
                "  AutoFortune: " + autoFortune + "\n" +
                "  #自动每日新闻信息\n" +
                "  AutoNews: " + autoNews + "\n" +
                "  #自动发送摸鱼小提示\n" +
                "  AutoTips: " + autoTips + "\n" +
                "  #需要自动发送的群列表 用,隔开\n" +
                "  Group: " + groupList + "\n" +
                "GroupManagement:\n" +
                "  #开启群管系统\n" +
                "  Open: " + groupManagement + "\n" +
                "  #群管系统管理员QQ\n" +
                "  AdminQQ: " + AdminQQ;

        Utils utils = new Utils();
        File file = new File(utils.getPluginsPath() + "/setting.yml");
        try {
            FileOutputStream writerStream = new FileOutputStream(file);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(writerStream, StandardCharsets.UTF_8));
            writer.write(config);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

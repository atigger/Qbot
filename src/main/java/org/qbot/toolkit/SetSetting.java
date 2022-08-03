package org.qbot.toolkit;

import com.alibaba.fastjson2.JSONArray;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * SetSetting class
 *
 * @author 649953543@qq.com
 * @date 2021/09/22
 */

public class SetSetting {
    public static void setFile(String version, long qq, String appid, String apiKey, String secretKey, int imageNum,
                               int imageRecall, boolean agreeFriend,
                               boolean agreeGroup, boolean AI, String aiApiKey, String aiApiSecret, boolean autoFortune, boolean autoNews, boolean autoTips, JSONArray groupList,
                               boolean groupManagement, long adminQQ) {
        String config = "#配置文件版本\n" +
                "Version: " + version + "\n" +
                "#机器人QQ\n" +
                "QQ: " + qq + "\n" +
                "#百度语音API\n" +
                "BaiDuAPI:\n" +
                "  APP_ID: \"" + appid + "\"\n" +
                "  API_KEY: \"" + apiKey + "\"\n" +
                "  SECRET_KEY: \"" + secretKey + "\"\n" +
                "#发送图片数量\n" +
                "ImageNum: " + imageNum + "\n" +
                "#图片自动撤回时间（0为不撤回,单位秒）\n" +
                "ImageRecall: " + imageRecall + "\n" +
                "#自动同意好友请求\n" +
                "AgreeFriend: " + agreeFriend + "\n" +
                "#自动同意邀请入群请求\n" +
                "AgreeGroup: " + agreeGroup + "\n" +
                "#智能聊天开关\n" +
                "AI:\n" +
                "  Open: " + AI + "\n" +
                "  Api_Key: \"" + aiApiKey + "\"\n" +
                "  Api_Secret: \"" + aiApiSecret + "\"\n" +
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
                "  AdminQQ: " + adminQQ;

        File file = new File(Utils.getPluginsPath() + "/setting.yml");
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

package org.qbot.group;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import org.qbot.toolkit.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * GroupManagementUtil class
 *
 * @author 649953543@qq.com
 * @date 2022/4/6
 */
public class GroupManagementSetting {
    Utils utils = new Utils();
    Path dataFolderPath = utils.getPluginsDataPath();
    File groupManagementDirectory = new File(dataFolderPath + "/groupManagement");

    /**
     * 判断是否是管理员
     *
     * @param group
     * @return
     */
    public boolean Authority(Group group) {
        if (group.getBotPermission().getLevel() == 0) {
            MessageChain chain = new MessageChainBuilder().append(new PlainText("当前不是管理员，无法使用此功能")).build();
            group.sendMessage(chain);
            return false;
        }
        return true;
    }

    /**
     * 设置管理员
     *
     * @param group
     * @return
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public int setAdmin(long group, long qq) throws IOException {
        File file = new File(groupManagementDirectory + "/" + group + ".txt");
        if (!file.exists()) {
            creatEmptyTemplate(file);
        }
        JSONObject jsonObject = utils.readFile(file);
        JSONArray adminQQ;
        try {
            adminQQ = jsonObject.getJSONArray("AdminQQ");
        } catch (Exception ignored) {
            adminQQ = new JSONArray();
        }
        for (int i = 0; i < adminQQ.size(); i++) {
            if (adminQQ.getLong(i) == qq) {
                return -1;
            }
        }
        adminQQ.add(qq);
        jsonObject.put("AdminQQ", adminQQ);
        if (utils.writeFile(file, jsonObject.toJSONString())) {
            return 1;
        }
        return 0;

    }

    /**
     * 删除管理员
     *
     * @param group
     * @param qq
     * @return
     * @throws IOException
     */
    public int delAdmin(long group, long qq) throws IOException {
        File file = new File(groupManagementDirectory + "/" + group + ".txt");
        if (!file.exists()) {
            creatEmptyTemplate(file);
        }
        JSONObject jsonObject = utils.readFile(file);
        JSONArray adminQQ;
        try {
            adminQQ = jsonObject.getJSONArray("AdminQQ");
        } catch (Exception ignored) {
            adminQQ = new JSONArray();
        }
        for (int i = 0; i < adminQQ.size(); i++) {
            if (adminQQ.getLong(i) == qq) {
                adminQQ.remove(i);
                jsonObject.put("AdminQQ", adminQQ);
                if (utils.writeFile(file, jsonObject.toJSONString())) {
                    return 1;
                }
                return 0;
            }
        }
        return -1;
    }

    /**
     * 设置禁言关键字
     *
     * @param group
     * @param keywords
     * @return
     */
    public int setMuteKeywords(long group, String keywords) throws IOException {
        File file = new File(groupManagementDirectory + "/" + group + ".txt");
        if (!file.exists()) {
            creatEmptyTemplate(file);
        }
        JSONObject jsonObject = utils.readFile(file);
        JSONArray muteKeywords;
        try {
            muteKeywords = jsonObject.getJSONArray("MuteKeywords");
        } catch (Exception ignored) {
            muteKeywords = new JSONArray();
        }
        for (int i = 0; i < muteKeywords.size(); i++) {
            if (muteKeywords.getString(i).equals(keywords)) {
                return -1;
            }
        }
        muteKeywords.add(keywords);
        jsonObject.put("MuteKeywords", muteKeywords);
        if (utils.writeFile(file, jsonObject.toJSONString())) {
            return 1;
        }
        return 0;
    }

    /**
     * 删除禁言关键字
     *
     * @param group
     * @param keywords
     * @return
     */
    public int delMuteKeywords(long group, String keywords) throws IOException {
        File file = new File(groupManagementDirectory + "/" + group + ".txt");
        if (!file.exists()) {
            creatEmptyTemplate(file);
        }
        JSONObject jsonObject = utils.readFile(file);
        JSONArray muteKeywords;
        try {
            muteKeywords = jsonObject.getJSONArray("MuteKeywords");
        } catch (Exception ignored) {
            muteKeywords = new JSONArray();
        }
        for (int i = 0; i < muteKeywords.size(); i++) {
            if (muteKeywords.getString(i).equals(keywords)) {
                muteKeywords.remove(i);
                jsonObject.put("MuteKeywords", muteKeywords);
                if (utils.writeFile(file, jsonObject.toJSONString())) {
                    return 1;
                }
                return 0;
            }
        }
        return -1;
    }

    /**
     * 清空禁言关键字
     *
     * @param group
     * @return
     */
    public int delAllMuteKeywords(long group) throws IOException {
        File file = new File(groupManagementDirectory + "/" + group + ".txt");
        if (!file.exists()) {
            creatEmptyTemplate(file);
        }
        JSONObject jsonObject = utils.readFile(file);
        jsonObject.put("MuteKeywords", new JSONArray());
        if (utils.writeFile(file, jsonObject.toJSONString())) {
            return 1;
        }
        return 0;
    }

    /**
     * 设置禁言时间
     *
     * @param group
     * @param min
     * @return
     */
    public int setMuteTime(long group, int min) throws IOException {
        File file = new File(groupManagementDirectory + "/" + group + ".txt");
        if (!file.exists()) {
            creatEmptyTemplate(file);
        }
        if (min > 43200) {
            return -1;
        }
        JSONObject jsonObject = utils.readFile(file);
        jsonObject.put("MuteTime", min);
        if (utils.writeFile(file, jsonObject.toJSONString())) {
            return 1;
        }
        return 0;
    }

    /**
     * 设置撤回关键字
     *
     * @param group
     * @param keywords
     * @return
     */
    public int setRecallWords(long group, String keywords) throws IOException {
        File file = new File(groupManagementDirectory + "/" + group + ".txt");
        if (!file.exists()) {
            creatEmptyTemplate(file);
        }
        JSONObject jsonObject = utils.readFile(file);
        JSONArray RecallWords;
        try {
            RecallWords = jsonObject.getJSONArray("RecallKeywords");
        } catch (Exception ignored) {
            RecallWords = new JSONArray();
        }
        for (int i = 0; i < RecallWords.size(); i++) {
            if (RecallWords.getString(i).equals(keywords)) {
                return -1;
            }
        }
        RecallWords.add(keywords);
        jsonObject.put("RecallKeywords", RecallWords);
        if (utils.writeFile(file, jsonObject.toJSONString())) {
            return 1;
        }
        return 0;
    }

    /**
     * 删除撤回关键字
     *
     * @param group
     * @param keywords
     * @return
     */
    public int delRecallWords(long group, String keywords) throws IOException {
        File file = new File(groupManagementDirectory + "/" + group + ".txt");
        if (!file.exists()) {
            creatEmptyTemplate(file);
        }
        JSONObject jsonObject = utils.readFile(file);
        JSONArray RecallWords;
        try {
            RecallWords = jsonObject.getJSONArray("RecallKeywords");
        } catch (Exception ignored) {
            RecallWords = new JSONArray();
        }
        for (int i = 0; i < RecallWords.size(); i++) {
            if (RecallWords.getString(i).equals(keywords)) {
                RecallWords.remove(i);
                jsonObject.put("RecallKeywords", RecallWords);
                if (utils.writeFile(file, jsonObject.toJSONString())) {
                    return 1;
                }
                return 0;
            }
        }
        return -1;
    }

    /**
     * 清空撤回关键字
     *
     * @param group
     * @return
     */
    public int delAllRecallWords(long group) throws IOException {
        File file = new File(groupManagementDirectory + "/" + group + ".txt");
        if (!file.exists()) {
            creatEmptyTemplate(file);
        }
        JSONObject jsonObject = utils.readFile(file);
        jsonObject.put("RecallKeywords", new JSONArray());
        if (utils.writeFile(file, jsonObject.toJSONString())) {
            return 1;
        }
        return 0;
    }

    /**
     * 设置自动同意群申请
     *
     * @param group
     * @return
     */
    public int setAutoAgreeApplication(long group) throws IOException {
        File file = new File(groupManagementDirectory + "/" + group + ".txt");
        if (!file.exists()) {
            creatEmptyTemplate(file);
        }
        JSONObject jsonObject = utils.readFile(file);
        jsonObject.put("AutoAgreeApplication", true);
        if (utils.writeFile(file, jsonObject.toJSONString())) {
            return 1;
        }
        return 0;
    }

    /**
     * 关闭自动同意群申请
     *
     * @param group
     * @return
     */
    public int delAutoAgreeApplication(long group) throws IOException {
        File file = new File(groupManagementDirectory + "/" + group + ".txt");
        if (!file.exists()) {
            creatEmptyTemplate(file);
        }
        JSONObject jsonObject = utils.readFile(file);
        jsonObject.put("AutoAgreeApplication", false);
        if (utils.writeFile(file, jsonObject.toJSONString())) {
            return 1;
        }
        return 0;
    }

    /**
     * 设置自动同意关键字
     *
     * @param group
     * @param keywords
     * @return
     */
    public int setAutoAgreeKeyWords(long group, String keywords) throws IOException {
        File file = new File(groupManagementDirectory + "/" + group + ".txt");
        if (!file.exists()) {
            creatEmptyTemplate(file);
        }
        JSONObject jsonObject = utils.readFile(file);
        JSONArray AutoAgreeKeyWords;
        try {
            AutoAgreeKeyWords = jsonObject.getJSONArray("AgreeKeywords");
        } catch (Exception ignored) {
            AutoAgreeKeyWords = new JSONArray();
        }
        for (int i = 0; i < AutoAgreeKeyWords.size(); i++) {
            if (AutoAgreeKeyWords.getString(i).equals(keywords)) {
                return -1;
            }
        }
        AutoAgreeKeyWords.add(keywords);
        jsonObject.put("AgreeKeywords", AutoAgreeKeyWords);
        if (utils.writeFile(file, jsonObject.toJSONString())) {
            return 1;
        }
        return 0;
    }

    /**
     * 删除自动同意关键字
     *
     * @param group
     * @param keywords
     * @return
     */
    public int delAutoAgreeKeyWords(long group, String keywords) throws IOException {
        File file = new File(groupManagementDirectory + "/" + group + ".txt");
        if (!file.exists()) {
            creatEmptyTemplate(file);
        }
        JSONObject jsonObject = utils.readFile(file);
        JSONArray AutoAgreeKeyWords;
        try {
            AutoAgreeKeyWords = jsonObject.getJSONArray("AgreeKeywords");
        } catch (Exception ignored) {
            AutoAgreeKeyWords = new JSONArray();
        }
        for (int i = 0; i < AutoAgreeKeyWords.size(); i++) {
            if (AutoAgreeKeyWords.getString(i).equals(keywords)) {
                AutoAgreeKeyWords.remove(i);
                jsonObject.put("AgreeKeywords", AutoAgreeKeyWords);
                if (utils.writeFile(file, jsonObject.toJSONString())) {
                    return 1;
                }
                return 0;
            }
        }
        return -1;
    }


    public int delAllAutoAgreeKeyWords(long group) throws IOException {
        File file = new File(groupManagementDirectory + "/" + group + ".txt");
        if (!file.exists()) {
            creatEmptyTemplate(file);
        }
        JSONObject jsonObject = utils.readFile(file);
        jsonObject.put("AgreeKeywords", new JSONArray());
        if (utils.writeFile(file, jsonObject.toJSONString())) {
            return 1;
        }
        return 0;
    }

    /**
     * 创建空白模板
     *
     * @param file
     * @return
     */
    public boolean creatEmptyTemplate(File file) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("AdminQQ", new JSONArray());
        jsonObject.put("MuteKeywords", new JSONArray());
        jsonObject.put("MuteTime", 0);
        jsonObject.put("RecallKeywords", new JSONArray());
        jsonObject.put("AutoAgreeApplication", false);
        jsonObject.put("AgreeKeywords", new JSONArray());
        jsonObject.put("RejectKeywords", new JSONArray());
        jsonObject.put("WarnKeywords", new JSONArray());
        return utils.writeFile(file, jsonObject.toJSONString());
    }


}

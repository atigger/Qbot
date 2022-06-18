package org.qbot.group;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
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
@SuppressWarnings("unchecked")
public class GroupManagementSetting {
    final Utils utils = new Utils();
    final Path dataFolderPath = utils.getPluginsDataPath();
    final File groupManagementDirectory = new File(dataFolderPath + "/groupManagement");

    /**
     * 判断是否是管理员
     *
     * @param group 群对象
     * @return 管理员返回true，非管理员返回false
     */
    public boolean authority(Group group) {
        if (group.getBotPermission().getLevel() == 0) {
            MessageChain chain = new MessageChainBuilder().append(new PlainText("当前不是管理员，无法使用此功能")).build();
            group.sendMessage(chain);
            return true;
        }
        return false;
    }

    /**
     * 设置管理员
     *
     * @param group 群对象
     * @return 设置成功返回1，失败返回0、-1
     */
    public int setAdmin(long group, long qq) throws IOException {
        File file = new File(groupManagementDirectory + "/" + group + ".txt");
        if (!file.exists()) {
            creatEmptyTemplate(file);
        }
        JSONObject jsonObject = utils.readFile(file);
        JSONArray adminQq;
        try {
            adminQq = jsonObject.getJSONArray("AdminQQ");
        } catch (Exception ignored) {
            adminQq = new JSONArray();
        }
        for (int i = 0; i < adminQq.size(); i++) {
            if (adminQq.getLongValue(i) == qq) {
                return -1;
            }
        }
        adminQq.add(qq);
        jsonObject.put("AdminQQ", adminQq);
        if (utils.writeFile(file, jsonObject.toJSONString())) {
            return 1;
        }
        return 0;

    }

    /**
     * 删除管理员
     *
     * @param group 群对象
     * @param qq    QQ号
     * @return 设置成功返回1，失败返回0、-1
     * @throws IOException IOException
     */
    public int delAdmin(long group, long qq) throws IOException {
        File file = new File(groupManagementDirectory + "/" + group + ".txt");
        if (!file.exists()) {
            creatEmptyTemplate(file);
        }
        JSONObject jsonObject = utils.readFile(file);
        JSONArray adminQq;
        try {
            adminQq = jsonObject.getJSONArray("AdminQQ");
        } catch (Exception ignored) {
            adminQq = new JSONArray();
        }
        for (int i = 0; i < adminQq.size(); i++) {
            if (adminQq.getLongValue(i) == qq) {
                adminQq.remove(i);
                jsonObject.put("AdminQQ", adminQq);
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
     * @param group    群对象
     * @param keywords 关键字
     * @return 设置成功返回1，失败返回0、-1
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
     * @param group    群对象
     * @param keywords 关键字
     * @return 设置成功返回1，失败返回0、-1
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
     * @param group 群对象
     * @return 设置成功返回1，失败返回0、-1
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

    private static final int MUTE_TIME = 43200;

    /**
     * 设置禁言时间
     *
     * @param group 群对象
     * @param min   分钟
     * @return 设置成功返回1，失败返回0、-1
     */
    public int setMuteTime(long group, int min) throws IOException {
        File file = new File(groupManagementDirectory + "/" + group + ".txt");
        if (!file.exists()) {
            creatEmptyTemplate(file);
        }
        if (min > MUTE_TIME) {
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
     * @param group    群对象
     * @param keywords 关键字
     * @return 设置成功返回1，失败返回0、-1
     */
    public int setRecallWords(long group, String keywords) throws IOException {
        File file = new File(groupManagementDirectory + "/" + group + ".txt");
        if (!file.exists()) {
            creatEmptyTemplate(file);
        }
        JSONObject jsonObject = utils.readFile(file);
        JSONArray recallWords;
        try {
            recallWords = jsonObject.getJSONArray("RecallKeywords");
        } catch (Exception ignored) {
            recallWords = new JSONArray();
        }
        for (int i = 0; i < recallWords.size(); i++) {
            if (recallWords.getString(i).equals(keywords)) {
                return -1;
            }
        }
        recallWords.add(keywords);
        jsonObject.put("RecallKeywords", recallWords);
        if (utils.writeFile(file, jsonObject.toJSONString())) {
            return 1;
        }
        return 0;
    }

    /**
     * 删除撤回关键字
     *
     * @param group    群对象
     * @param keywords 关键字
     * @return 设置成功返回1，失败返回0、-1
     */
    public int delRecallWords(long group, String keywords) throws IOException {
        File file = new File(groupManagementDirectory + "/" + group + ".txt");
        if (!file.exists()) {
            creatEmptyTemplate(file);
        }
        JSONObject jsonObject = utils.readFile(file);
        JSONArray recallWords;
        try {
            recallWords = jsonObject.getJSONArray("RecallKeywords");
        } catch (Exception ignored) {
            recallWords = new JSONArray();
        }
        for (int i = 0; i < recallWords.size(); i++) {
            if (recallWords.getString(i).equals(keywords)) {
                recallWords.remove(i);
                jsonObject.put("RecallKeywords", recallWords);
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
     * @param group 群对象
     * @return 设置成功返回1，失败返回0、-1
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
     * @param group 群对象
     * @return 设置成功返回1，失败返回0、-1
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
     * @param group 群对象
     * @return 设置成功返回1，失败返回0、-1
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
     * @param group    群对象
     * @param keywords 关键字
     * @return 设置成功返回1，失败返回0、-1
     */
    public int setAutoAgreeKeyWords(long group, String keywords) throws IOException {
        File file = new File(groupManagementDirectory + "/" + group + ".txt");
        if (!file.exists()) {
            creatEmptyTemplate(file);
        }
        JSONObject jsonObject = utils.readFile(file);
        JSONArray autoAgreeKeyWords;
        try {
            autoAgreeKeyWords = jsonObject.getJSONArray("AgreeKeywords");
        } catch (Exception ignored) {
            autoAgreeKeyWords = new JSONArray();
        }
        for (int i = 0; i < autoAgreeKeyWords.size(); i++) {
            if (autoAgreeKeyWords.getString(i).equals(keywords)) {
                return -1;
            }
        }
        autoAgreeKeyWords.add(keywords);
        jsonObject.put("AgreeKeywords", autoAgreeKeyWords);
        if (utils.writeFile(file, jsonObject.toJSONString())) {
            return 1;
        }
        return 0;
    }

    /**
     * 删除自动同意关键字
     *
     * @param group    群对象
     * @param keywords 关键字
     * @return 设置成功返回1，失败返回0、-1
     */
    public int delAutoAgreeKeyWords(long group, String keywords) throws IOException {
        File file = new File(groupManagementDirectory + "/" + group + ".txt");
        if (!file.exists()) {
            creatEmptyTemplate(file);
        }
        JSONObject jsonObject = utils.readFile(file);
        JSONArray autoAgreeKeyWords;
        try {
            autoAgreeKeyWords = jsonObject.getJSONArray("AgreeKeywords");
        } catch (Exception ignored) {
            autoAgreeKeyWords = new JSONArray();
        }
        for (int i = 0; i < autoAgreeKeyWords.size(); i++) {
            if (autoAgreeKeyWords.getString(i).equals(keywords)) {
                autoAgreeKeyWords.remove(i);
                jsonObject.put("AgreeKeywords", autoAgreeKeyWords);
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
     * @param file 文件对象
     */
    public void creatEmptyTemplate(File file) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("AdminQQ", new JSONArray());
        jsonObject.put("MuteKeywords", new JSONArray());
        jsonObject.put("MuteTime", 0);
        jsonObject.put("RecallKeywords", new JSONArray());
        jsonObject.put("AutoAgreeApplication", false);
        jsonObject.put("AgreeKeywords", new JSONArray());
        jsonObject.put("RejectKeywords", new JSONArray());
        jsonObject.put("WarnKeywords", new JSONArray());
        utils.writeFile(file, jsonObject.toJSONString());
    }


}

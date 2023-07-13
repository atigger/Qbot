package org.qbot.music;

import com.alibaba.fastjson2.JSONObject;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.message.MessageReceipt;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.utils.ExternalResource;
import org.qbot.toolkit.Setting;
import org.qbot.toolkit.Utils;

import java.io.File;
import java.io.IOException;

/**
 * MusicMessageDeal class
 *
 * @author 649953543@qq.com
 * @date 2023/2/18
 */
public class MusicMessageDeal {

    NeteaseCloudMusicTask neteaseCloudMusicTask = new NeteaseCloudMusicTask();
    Utils utils = new Utils();
    private static final String STRING_LOGIN = "网易云登录";
    private static final String STRING_SIGN = "网易云签到";
    private static final String STRING_INFO = "我的网易云";
    private static final String EXPIRED = "已过期";
    private static final String STRING_START = "开始刷歌";
    private static final String STRING_STOP = "停止刷歌";
    private static final String STRING_STOP1 = "强制停止刷歌";
    private static final String STRING_LOGIN_OUT = "退出登录";

    public boolean msgDel(Friend frind, String msg) throws IOException, InterruptedException {
        Setting.getBot().getLogger().info("收到消息:" + msg);

        long senderId = frind.getId();

        MessageChain chain;

        if (STRING_LOGIN.equals(msg)) {
            chain = new MessageChainBuilder().append(new PlainText("正在获取二维码,请稍后")).build();
            MessageReceipt<Friend> messageReceipts = frind.sendMessage(chain);
            int randomNum = Utils.getRandomNum(0, 10000);
            String filename = Utils.getTime() + randomNum + ".jpg";
            try {
                String qrkey = neteaseCloudMusicTask.getQrKey();
                String orCodeBase64 = neteaseCloudMusicTask.getQrCode(qrkey).replace("data:image/png;base64,", "");
                utils.GenerateImage(orCodeBase64, filename);
                ExternalResource img = ExternalResource.create(new File(Utils.getPluginsDataPath() + "/cache/image/" + filename));
                Image image = frind.uploadImage(img);
                img.close();
                chain = new MessageChainBuilder().append(image).append(new PlainText("请在100秒内登录")).build();
                messageReceipts.recall();
                MessageReceipt<Friend> messageReceipts1 = frind.sendMessage(chain);
                String cookie = neteaseCloudMusicTask.getCheckLogin(qrkey);
                if (cookie.contains(EXPIRED)) {
                    chain = new MessageChainBuilder().append(new PlainText(cookie)).build();
                    frind.sendMessage(chain);
                } else {
                    setFile(senderId, "Cookie", cookie);
                    chain = new MessageChainBuilder().append(new PlainText("登录成功")).build();
                    frind.sendMessage(chain);
                    setFile(senderId, "Name", neteaseCloudMusicTask.getUserInfo(cookie).getString("nickname"));
                    setFile(senderId, "UserID", neteaseCloudMusicTask.getUserInfo(cookie).getString("userId"));
                    setFile(senderId, "LastLoginDate", utils.getTime4());
                    chain = new MessageChainBuilder().append(new PlainText(getInfo(senderId))).build();
                    frind.sendMessage(chain);
                }
                messageReceipts1.recall();
            } catch (Exception e) {
                chain = new MessageChainBuilder().append(new PlainText("获取失败,请重试")).build();
                frind.sendMessage(chain);
                e.printStackTrace();
            }
            return true;
        }

        if (STRING_SIGN.equals(msg)) {
            chain = new MessageChainBuilder().append(new PlainText("正在签到,请稍后")).build();
            MessageReceipt<Friend> messageReceipts = frind.sendMessage(chain);
            String cookie = getFile(senderId, "Cookie");
            if (cookie == null || "".equals(cookie)) {
                chain = new MessageChainBuilder().append(new PlainText("请先登录")).build();
                frind.sendMessage(chain);
                messageReceipts.recall();
                return true;
            }
            chain = new MessageChainBuilder().append(new PlainText(neteaseCloudMusicTask.yunbeiSign(cookie))).build();
            frind.sendMessage(chain);
            setFile(senderId, "Sign", "已签到");
            messageReceipts.recall();
            return true;
        }

        if (STRING_INFO.equals(msg)) {
            chain = new MessageChainBuilder().append(new PlainText(getInfo(senderId))).build();
            frind.sendMessage(chain);
            return true;
        }


        if (STRING_START.equals(msg)) {
            chain = new MessageChainBuilder().append(new PlainText("正在启动刷歌,请稍后")).build();
            MessageReceipt<Friend> messageReceipts = frind.sendMessage(chain);
            String cookie = getFile(senderId, "Cookie");
            if (cookie == null || "".equals(cookie)) {
                chain = new MessageChainBuilder().append(new PlainText("请先登录")).build();
                frind.sendMessage(chain);
                messageReceipts.recall();
                return true;
            }
            if ("已启动".equals(getFile(senderId, "Lock"))) {
                chain = new MessageChainBuilder().append(new PlainText("刷歌正在运行中,无需重新启动")).build();
                frind.sendMessage(chain);
                messageReceipts.recall();
                return true;
            }
            setFile(senderId, "Lock", "已启动");
            if (!utils.getTime3().equals(getFile(senderId, "LastDate"))) {
                setFile(senderId, "NumberOfBrushes", "0");
            }
            setFile(senderId, "LastDate", utils.getTime3());
            chain = new MessageChainBuilder().append(new PlainText("刷歌已启动")).build();
            frind.sendMessage(chain);
            messageReceipts.recall();
            String txt;
            try {
                txt = neteaseCloudMusicTask.brushMusic(senderId, cookie, getFile(senderId, "UserID"));
            } catch (Exception e) {
                setFile(senderId, "Lock", "未启动");
                setFile(senderId, "ExitTag", "false");
                e.printStackTrace();
                txt = "刷歌错误";
            }
            chain = new MessageChainBuilder().append(new PlainText(txt)).build();
            frind.sendMessage(chain);
            return true;
        }

        if (STRING_STOP.equals(msg)) {
            chain = new MessageChainBuilder().append(new PlainText("正在停止刷歌,请稍后")).build();
            MessageReceipt<Friend> messageReceipts = frind.sendMessage(chain);
            String cookie = getFile(senderId, "Cookie");
            if (cookie == null || "".equals(cookie)) {
                chain = new MessageChainBuilder().append(new PlainText("请先登录")).build();
                frind.sendMessage(chain);
                messageReceipts.recall();
                return true;
            }
            if ("未启动".equals(getFile(senderId, "Lock"))) {
                chain = new MessageChainBuilder().append(new PlainText("刷歌未启动,无需停止")).build();
                frind.sendMessage(chain);
                messageReceipts.recall();
                return true;
            }
            setFile(senderId, "ExitTag", "true");
            return true;
        }

        if (STRING_STOP1.equals(msg)) {
            chain = new MessageChainBuilder().append(new PlainText("正在强制停止刷歌,请稍后")).build();
            MessageReceipt<Friend> messageReceipts = frind.sendMessage(chain);
            String cookie = getFile(senderId, "Cookie");
            if (cookie == null || "".equals(cookie)) {
                chain = new MessageChainBuilder().append(new PlainText("请先登录")).build();
                frind.sendMessage(chain);
                messageReceipts.recall();
                return true;
            }
            setFile(senderId, "Lock", "未启动");
            setFile(senderId, "ExitTag", "false");
            chain = new MessageChainBuilder().append(new PlainText("已停止")).build();
            frind.sendMessage(chain);
            return true;
        }

        if (STRING_LOGIN_OUT.equals(msg)) {
            chain = new MessageChainBuilder().append(new PlainText("正在退出登录,请稍后")).build();
            MessageReceipt<Friend> messageReceipts = frind.sendMessage(chain);
            String cookie = getFile(senderId, "Cookie");
            if (cookie == null || "".equals(cookie)) {
                chain = new MessageChainBuilder().append(new PlainText("请先登录")).build();
                frind.sendMessage(chain);
                messageReceipts.recall();
                return true;
            }
            setFile(senderId, "Cookie", "");
            setFile(senderId, "Lock", "未启动");
            setFile(senderId, "Sign", "未知");
            setFile(senderId, "Name", "未知");
            setFile(senderId, "LastDate", "未知");
            setFile(senderId, "NumberOfBrushes", "0");
            setFile(senderId, "UserID", "0");
            setFile(senderId, "LastLoginDate", utils.getTime4());
            setFile(senderId, "ExitTag", "false");
            chain = new MessageChainBuilder().append(new PlainText("已退出登录")).build();
            frind.sendMessage(chain);
            messageReceipts.recall();
            return true;
        }

        return false;
    }


    /**
     * 设置缓存文件
     *
     * @return boolean 是否成功
     */
    public static boolean setFile(long senderId, String key, String value) throws IOException {
        Utils utils1 = new Utils();
        String filePath = Utils.getPluginsDataPath() + "/cache/music/" + senderId + ".txt";
        File file = new File(filePath);
        JSONObject jsonObject = new JSONObject();
        if (!file.exists()) {
            jsonObject.put("Cookie", "");
            jsonObject.put("Lock", "未启动");
            jsonObject.put("Sign", "未知");
            jsonObject.put("Name", "未知");
            jsonObject.put("LastDate", "未知");
            jsonObject.put("NumberOfBrushes", "0");
            jsonObject.put("UserID", "0");
            jsonObject.put("LastLoginDate", utils1.getTime4());
            jsonObject.put("ExitTag", "false");
            jsonObject.put(key, value);
        } else {
            jsonObject = utils1.readFile(file);
            jsonObject.put(key, value);
        }
        return utils1.writeFile(file, jsonObject.toJSONString());
    }

    /**
     * 获取缓存文件
     */
    public static String getFile(long senderId, String key) {
        Utils utils1 = new Utils();
        String filePath = Utils.getPluginsDataPath() + "/cache/music/" + senderId + ".txt";
        File file = new File(filePath);
        if (file.exists()) {
            try {
                JSONObject jsonObject = utils1.readFile(file);
                return jsonObject.getString(key);
            } catch (IOException ignored) {
            }
        }
        return null;
    }

    /**
     * 获取个人信息
     */
    public String getInfo(long senderId) throws IOException {
        String filePath = Utils.getPluginsDataPath() + "/cache/music/" + senderId + ".txt";
        File file = new File(filePath);
        JSONObject jsonObject = utils.readFile(file);

        String sign = jsonObject.getString("Sign");
        String numberOfBrushes = jsonObject.getString("NumberOfBrushes");
        String lastDate = jsonObject.getString("LastDate");
        String lock = jsonObject.getString("Lock");
        if (!lastDate.equals(utils.getTime3())) {
            sign = "未签到";
            numberOfBrushes = "0";
            lock = "未启动";
            setFile(senderId, "Sign", sign);
            setFile(senderId, "NumberOfBrushes", numberOfBrushes);
            setFile(senderId, "Lock", lock);
            setFile(senderId, "ExitTag", "false");
        }

        return "昵称:" + jsonObject.getString("Name") + "\n" +
                "签到状态:" + sign + "\n" +
                "已刷歌次数:" + numberOfBrushes + "\n" +
                "上次刷歌时间:\n" + lastDate + "\n" +
                "刷歌状态:" + lock + "\n" +
                "上次登录时间:\n" + jsonObject.getString("LastLoginDate");
    }

}

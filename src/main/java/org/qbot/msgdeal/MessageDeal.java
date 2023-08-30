package org.qbot.msgdeal;

import com.alibaba.fastjson2.JSONObject;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.file.AbsoluteFile;
import net.mamoe.mirai.message.MessageReceipt;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.utils.ExternalResource;
import org.qbot.PluginVersion;
import org.qbot.toolkit.PluginUtil;
import org.qbot.toolkit.Setting;
import org.qbot.toolkit.Utils;

import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * MessageDeal class
 *
 * @author 649953543@qq.com
 * @date 2021/09/22
 */

@SuppressWarnings({"ALL", "AlibabaMethodTooLong"})
public class MessageDeal {
    PluginUtil pluginUtil = new PluginUtil();
    Utils utils = new Utils();
    Group group = null;

    Path dataFolderPath = Utils.getPluginsDataPath();

    private static final String STRING_LISTENTOMUSIC = "听歌";
    private static final String STRING_QQ = "qq";
    private static final String STRING_SAY = "说";
    private static final String STRING_WE_CHAT = "微信";
    private static final String STRING_HOROSCOPE = "星座运势";
    private static final String STRING_COMBAT_POWER_QUERY = "战力查询";
    private static final String STRING_MUSIC_SYSTEM = "音乐系统";
    private static final String STRING_FAIL = "失败";
    private static final String STRING_FORTUNE = "运势";
    private static final String STRING_TODAY_FORTUNE = "今日运势";
    private static final String STRING_GET_FAILED = "获取失败";
    private static final String STRING_NEWS = "新闻";
    private static final String STRING_TODAY_NEWS = "今日新闻";
    private static final String STRING_BEAUTY = "美女";
    private static final String STRING_BEAUTY_PICTURES = "美女图片";
    private static final String STRING_GET_SIGN = "求签";
    private static final String STRING_GUANYIN_GET_SIGN = "诸葛神签";
    private static final String STRING_MENU = "菜单";
    private static final String STRING_WEATHER = "天气";
    private static final String STRING_HELP = "帮助";
    private static final String STRING_FISH = "摸鱼办";
    private static final String STRING_BEAUTY_VIDEO = "美女视频";
    private static final String STRING_GOOD_NEWS = "喜报";
    private static final String STRING_BAD_NEWS = "悲报";
    private static final String NUM_ONE = "1";
    private static final JSONObject TWELVE_HOROSCOPE = JSONObject.parseObject("{'白羊':'aries','金牛':'taurus','双子':'gemini','巨蟹':'cancer','狮子':'leo','处女':'virgo','天秤':'libra','天蝎':'scorpio','射手':'sagittarius','摩羯':'capricorn','水瓶':'aquarius','双鱼':'pisces'}");
    private static final JSONObject HOT_LIST = JSONObject.parseObject("{'微博热搜':'wbHot'}");
    ExecutorService executorService = Executors.newCachedThreadPool();

    @SuppressWarnings("AlibabaMethodTooLong")
    public void msgDel(Long senderId, String senderName, Group group, String msg, MessageChain msgchains) throws IOException,
            InterruptedException,
            ScriptException, NoSuchMethodException, ParseException {
        MessageChain chain;
        this.group = group;
        Setting.getBot().getLogger().info("收到的消息:" + msg + " 消息长度:" + msg.length());
        if (STRING_FISH.equals(msg)) {
//            chain = new MessageChainBuilder().append(new PlainText(pluginUtil.moFish())).build();
//            group.sendMessage(chain);
//            return;
            String mofishImgUrl = pluginUtil.moFishNew();
            if (!mofishImgUrl.contains(STRING_FAIL)) {
                Image image = getImageAdd(mofishImgUrl);
                chain = new MessageChainBuilder().append(image).build();
            } else {
                chain = new At(senderId).plus(new PlainText("\n获取失败"));
            }
            group.sendMessage(chain);
            return;
        }


        if ("".equals(msg)) {
            String ImgUrl = pluginUtil.generatePicture("crawl", java.net.URLEncoder.encode("http://q.qlogo.cn/g?b=qq&nk=" + senderId + "&s=640", "UTF-8"), "img");
            if (!ImgUrl.contains(STRING_FAIL)) {
                Image image = getImageAdd(ImgUrl);
                chain = new MessageChainBuilder().append(image).append("\n发送<菜单>命令可获取菜单\n发送<帮助>命令可获取帮助文档").build();
            } else {
                chain = new At(senderId).plus(new PlainText("\n发送<菜单>命令可获取菜单\n发送<帮助>命令可获取帮助文档"));
            }
            group.sendMessage(chain);
            return;
        }


        if (msg.contains(STRING_BEAUTY_VIDEO)) {
            try {
                MessageSource.recall(msgchains);
            } catch (Exception e) {
                Setting.getBot().getLogger().info("撤回失败1");
            }
            MessageReceipt<Group> messageReceipts = group.sendMessage("正在上传，请稍后...");
            String filePath = pluginUtil.getVideo();
            File video = new File(filePath);
            try (ExternalResource resource = ExternalResource.create(video)) {
                AbsoluteFile uploadFile = group.getFiles().getRoot().uploadNewFile("/" + video.getName(), resource);
                messageReceipts.recall();
                int recallTimes = Setting.getImageRecall();
                try {
                    if (recallTimes != 0) {
                        Thread.sleep(recallTimes * 1000);
                        uploadFile.delete();
                    }
                } catch (Exception e) {
                    Setting.getBot().getLogger().info("撤回失败");
                }
            } catch (Exception e) {
                messageReceipts.recall();
                group.sendMessage("发送失败,请重试");
            }
            return;
        }

        if (msg.startsWith(STRING_LISTENTOMUSIC)) {
            msg = msg.replace(STRING_LISTENTOMUSIC, "");
            if ("".equals(msg)) {
                chain = new PlainText("？ \n你总得告诉我要听什么吧？").plus(new Face(244));
                group.sendMessage(chain);
                return;
            }
            PluginUtil pluginUtil = new PluginUtil();
            MusicShare musicShare = pluginUtil.getMusic(msg);
            group.sendMessage(musicShare);
            return;
        }

        if (msg.startsWith(STRING_SAY)) {
            msg = msg.replaceFirst(STRING_SAY, "");
            if ("".equals(msg)) {
                chain = new PlainText("？ \n你总得告诉我要说什么吧？").plus(new Face(244));
                group.sendMessage(chain);
                return;
            }
            PluginUtil pluginUtil = new PluginUtil();
            ExternalResource audio = pluginUtil.getVoice(msg);
            if (audio == null) {
                group.sendMessage("语音系统未配置，请联系管理员");
                return;
            }
            Audio audio1 = group.uploadAudio(audio);
            group.sendMessage(audio1);
            audio.close();
            return;
        }

        if (msg.startsWith(STRING_QQ)) {
            msg = msg.replace(STRING_QQ, "");
            msg = msg.replace(" ", "");
            JSONObject jsonObject = pluginUtil.getPower(msg, "qq");
            if (jsonObject == null) {
                group.sendMessage("获取失败");
                return;
            }
            String heroInfo = jsonObject.getString("data");
            String heroImg = jsonObject.getString("img");
            Image image = getImageAdd(heroImg);
            chain = new MessageChainBuilder().append(new At(senderId)).append(image).append(new PlainText("\n" + heroInfo)).build();
            group.sendMessage(chain);
            return;
        }

        if (msg.startsWith(STRING_WE_CHAT)) {
            msg = msg.replace(STRING_WE_CHAT, "");
            msg = msg.replace(" ", "");
            JSONObject jsonObject = pluginUtil.getPower(msg, "wx");
            if (jsonObject == null) {
                group.sendMessage("获取失败");
                return;
            }
            String heroInfo = jsonObject.getString("data");
            String heroImg = jsonObject.getString("img");
            Image image = getImageAdd(heroImg);
            chain = new MessageChainBuilder().append(new At(senderId)).append(image).append(new PlainText("\n" + heroInfo)).build();
            group.sendMessage(chain);
            return;
        }

        if (STRING_HOROSCOPE.equals(msg)) {
            chain = new MessageChainBuilder().append(new At(senderId)).append(new PlainText("\n请@我并发送星座名\n例如 <@运势小助手 白羊>")).build();
            group.sendMessage(chain);
            return;
        }

        if (STRING_COMBAT_POWER_QUERY.equals(msg)) {
            chain = new MessageChainBuilder().append(new At(senderId)).append(new PlainText("\n请@我并发送qq/微信+英雄名\n例如 <@运势小助手 qq 伽罗>")).build();
            group.sendMessage(chain);
            return;
        }

        if (STRING_MUSIC_SYSTEM.equals(msg)) {
            chain = new MessageChainBuilder().append(new At(senderId)).append(new PlainText("\n请@我并发送听歌+音乐名\n注歌曲源来自网易云\n例如 <@运势小助手 听歌稻香>")).build();
            group.sendMessage(chain);
            return;
        }

        if (STRING_FORTUNE.equals(msg) || STRING_TODAY_FORTUNE.equals(msg)) {
            String replaceMsg = pluginUtil.getFortune();
            chain = new At(senderId).plus(new PlainText("\n" + replaceMsg));
            group.sendMessage(chain);
            return;
        }

        if (STRING_NEWS.equals(msg) || STRING_TODAY_NEWS.equals(msg)) {
            String newsImgUrl = pluginUtil.getNews();
            if (!newsImgUrl.contains(STRING_FAIL)) {
                Image image = getImageAdd(newsImgUrl);
                chain = new MessageChainBuilder().append(image).build();
            } else {
                chain = new At(senderId).plus(new PlainText("\n获取失败"));
            }
            group.sendMessage(chain);
            return;
        }

        if (STRING_BEAUTY.equals(msg) || STRING_BEAUTY_PICTURES.equals(msg)) {
            ForwardMessageBuilder builder1 = new ForwardMessageBuilder(group);
            ForwardMessageBuilder builder = new ForwardMessageBuilder(group);

            builder.add(2119517658L, "一位不愿透露姓名的群友", getImageAdd(dataFolderPath + "/image_qr_code.png"));
            builder.add(2119517658L, "一位不愿透露姓名的群友", new PlainText("注意\n由于tx限制\n请按照以下提示操作\n使用浏览器打开").plus(getImageAdd(dataFolderPath + "/image_tips.png")));
            builder.add(2119517658L, "一位不愿透露姓名的群友",
                    new PlainText("注意\n刷新即可切换图片").plus(new Face(271)));
            ForwardMessage forward = builder.build();
            ForwardMessage forward1 = builder1.add(2119517658L, "一位不愿透露姓名的群友", forward).build();
            MessageReceipt<Group> messageReceipts = group.sendMessage(forward1);
            int recallTimes = Setting.getImageRecall();
            try {
                MessageSource.recall(msgchains);
            } catch (Exception e) {
                Setting.getBot().getLogger().info("撤回失败1");
            }
            try {
                if (recallTimes != 0) {
                    Thread.sleep(recallTimes * 1000);
                    messageReceipts.recall();
                }
            } catch (Exception e) {
                Setting.getBot().getLogger().info("撤回失败");
            }
            return;
        }

        //获取星座运势
        for (String s : TWELVE_HOROSCOPE.keySet()) {
            if (msg.equals(s) || msg.equals(s + "座") || msg.equals(s + "座运势")) {
                chain = new MessageChainBuilder().append(new At(senderId)).append(new PlainText("\n" + utils.getHoroscope(TWELVE_HOROSCOPE.getString(s)))).build();
                group.sendMessage(chain);
                return;
            }
        }

        //获取热点
        for (String s : HOT_LIST.keySet()) {
            if (msg.equals(s)) {
                chain = new MessageChainBuilder().append(s + ":\n" + utils.getHotList(HOT_LIST.getString(s))).build();
                group.sendMessage(chain);
                return;
            }
        }

        if (msg.contains(STRING_HELP)) {
            chain = new MessageChainBuilder().append(new At(senderId)).append(new PlainText("\n帮助文档:\nhttps://www" +
                    ".miraiqbot.top/\n当前版本：" + PluginVersion.VERSION_NUM)).build();
            group.sendMessage(chain);
            return;
        }

        if (STRING_GET_SIGN.equals(msg) || STRING_GUANYIN_GET_SIGN.equals(msg)) {
            File qqFile = new File(utils.getPluginsDataPath() + "/cache/qq/" + senderId + ".cache");
            String qian;
            if (qqFile.exists()) {
                qian = utils.readData(qqFile);
                if (NUM_ONE.equals(qian)) {
                    qian = pluginUtil.getCq();
                    utils.rewriteData(qqFile, qian);
                }
            } else {
                qian = pluginUtil.getCq();
                utils.rewriteData(qqFile, qian);
            }
            chain = new MessageChainBuilder().append(new At(senderId)).append(new PlainText("\n" + qian)).build();
            group.sendMessage(chain);
            return;
        }

        if (STRING_MENU.equals(msg)) {
            group.sendMessage(getMenuTxt());
            return;
        }

        if (msg.contains(STRING_GOOD_NEWS)) {
            msg = msg.replace("喜报", "");
            msg = msg.replace(" ", "");
            String ImgUrl = pluginUtil.generatePicture("certificate", msg, "txt");
            if (!ImgUrl.contains(STRING_FAIL)) {
                Image image = getImageAdd(ImgUrl);
                chain = new MessageChainBuilder().append(image).build();
            } else {
                chain = new At(senderId).plus(new PlainText("\n获取失败"));
            }
            group.sendMessage(chain);
            return;
        }

        if (msg.contains(STRING_BAD_NEWS)) {
            msg = msg.replace("悲报", "");
            msg = msg.replace(" ", "");
            String ImgUrl = pluginUtil.generatePicture("sad_news", msg, "txt");
            if (!ImgUrl.contains(STRING_FAIL)) {
                Image image = getImageAdd(ImgUrl);
                chain = new MessageChainBuilder().append(image).build();
            } else {
                chain = new At(senderId).plus(new PlainText("\n获取失败"));
            }
            group.sendMessage(chain);
            return;
        }

        if (Setting.getAi()) {
            String reply = pluginUtil.aiReply(senderId, senderName, group.getId(), msg);
            if (reply.equals("你干嘛！哎哟~")) {
                String ImgUrl = pluginUtil.generatePicture("crawl", java.net.URLEncoder.encode("http://q.qlogo.cn/g?b=qq&nk=" + senderId + "&s=640", "UTF-8"), "img");
                if (!ImgUrl.contains(STRING_FAIL)) {
                    Image image = getImageAdd(ImgUrl);
                    chain = new MessageChainBuilder().append(image).append("\n发送<菜单>命令可获取菜单\n发送<帮助>命令可获取帮助文档").build();
                } else {
                    chain = new At(senderId).plus(new PlainText("\n发送<菜单>命令可获取菜单\n发送<帮助>命令可获取帮助文档"));
                }
            } else {
                chain = new MessageChainBuilder().append(new PlainText(reply)).build();
            }
            group.sendMessage(chain);
            return;
        }

    }

    public void nudgeDel(Long formId, Long groupId) throws IOException {
        MessageChain chain;
        List<String> keyList = Arrays.asList("breakdown", "bite", "cast", "crawl", "dont_touch", "eat", "hammer", "knock", "pat", "petpet", "play", "pound", "roll", "suck", "tear", "thump", "tightly");
        String ImgUrl = pluginUtil.generatePicture(keyList.get(Utils.getRandomNum(0, 16)), java.net.URLEncoder.encode("http://q.qlogo.cn/g?b=qq&nk=" + formId + "&s=640", "UTF-8"), "gif");
        this.group = Setting.getBot().getGroup(groupId);
        if (!ImgUrl.contains(STRING_FAIL)) {
            Image image = getImageAdd(ImgUrl);
            chain = new MessageChainBuilder().append(image).build();
        } else {
            chain = new At(formId).plus(new PlainText("\n你戳我干啥"));
        }
        this.group.sendMessage(chain);
        return;
    }

    /**
     * 菜单
     */
    public MessageChain getMenuTxt() {
        return new MessageChainBuilder().append(new Face(147)).append(new PlainText("           菜单          ")).append(new Face(147)).append(new PlainText("\n◇━━━━━━━━◇\n")).append(new Face(190)).append(new PlainText("今日运势  今日新闻")).append(new Face(190)).append(new PlainText("\n")).append(new Face(190)).append(new PlainText("星座运势  诸葛神签")).append(new Face(190)).append(new PlainText("\n")).append(new Face(190)).append(new PlainText("音乐系统  语音系统")).append(new Face(190)).append(new PlainText("\n")).append(new Face(190)).append(new PlainText("美女图片  美女视频")).append(new Face(190)).append(new PlainText("\n")).append(new Face(190)).append(new PlainText("战力查询  帮助文档")).append(new Face(190)).append(new PlainText("\n")).append(new Face(190)).append(new PlainText("喜报悲报  敬请期待")).append(new Face(190)).append(new PlainText("\n◇━━━━━━━━◇\nPS:@我并发相应文字查看指令\n当前版本：" + PluginVersion.VERSION_NUM)).build();
    }


    public Image getImageAdd(String filepath) throws IOException {
        ExternalResource img = ExternalResource.create(new File(filepath));
        Image image = this.group.uploadImage(img);
        img.close();
        return image;
    }


}

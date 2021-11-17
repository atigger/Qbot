package org.example.mirai.plugin;

import com.alibaba.fastjson.JSONObject;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.utils.ExternalResource;
import org.example.mirai.plugin.rane.RaneBase;
import org.example.mirai.plugin.rane.RaneUtil;
import org.example.mirai.plugin.toolkit.Plugin;
import org.example.mirai.plugin.toolkit.Setting;
import org.example.mirai.plugin.toolkit.Utils;

import javax.script.ScriptException;
import java.io.*;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * MessageDeal class
 *
 * @author 649953543@qq.com
 * @date 2021/09/22
 */

public class MessageDeal {
    Plugin plugin = new Plugin();
    Utils utils = new Utils();
    Group group = null;
    RaneUtil raneUtil = new RaneUtil();
    RaneBase raneBase = new RaneBase();
    Setting setting = new Setting();

    String stringListentomusic = "听歌";
    String stringQq = "qq";
    String stringSay = "说";
    String stringWeChat = "微信";
    String stringHoroscope = "星座运势";
    String stringCombatPowerQuery = "战力查询";
    String stringMusicSystem = "音乐系统";
    String stringRaneMenu = "燃鹅菜单";
    String stringAuthorization = "授权";
    String stringFail = "失败";
    String stringTimeOut = "超时";
    String stringLogin = "登录";
    String stringSign = "签到";
    String stringReissue = "补签";
    String stringLotteryRecord = "抽奖记录";
    String stringSuperLottery = "超级抽奖";
    String stringNormalLottery = "普通抽奖";
    String stringMineRane = "我的燃鹅";
    String stringFortune = "运势";
    String stringTodayFortune = "今日运势";
    String stringGetFailed = "获取失败";
    String stringNews = "新闻";
    String stringTodayNews = "今日新闻";
    String stringBeauty = "美女";
    String stringBeautyPictures = "美女图片";
    String stringGetSign = "求签";
    String stringGuanyinGetSign = "观音求签";
    String stringMenu = "菜单";
    String stringWeather = "天气";

    String numOne = "1";
    int numEleven = 11;


    public void msgDel(Long senderId, Group group, String msg) throws IOException, InterruptedException, ScriptException, NoSuchMethodException, ParseException {
        MessageChain chain;
        ArrayList<String> twelveHoroscop = getTwelveHoroscopeList();
        this.group = group;
        System.out.println("收到的消息:" + msg + " 消息长度:" + msg.length());

        if ("my".equals(msg)) {
            chain = new MessageChainBuilder()
                    .append(new PlainText(plugin.moFish()))
                    .build();
            group.sendMessage(chain);
            return;
        }


        if ("".equals(msg)) {
            chain = new MessageChainBuilder()
                    .append(new At(senderId))
                    .append(new PlainText("\n你没得事情干唛？@我作甚么？"))
                    .append(new Face(312))
                    .build();
            group.sendMessage(chain);
            return;
        }

        if (msg.indexOf(stringListentomusic) == 0) {
            msg = msg.replace(stringListentomusic, "");
            if ("".equals(msg)) {
                chain = new PlainText("？ \n你总得告诉我要听什么吧？").plus(new Face(244));
                group.sendMessage(chain);
                return;
            }
            Plugin plugin = new Plugin();
            MusicShare musicShare = plugin.getMusic(msg);
            group.sendMessage(musicShare);
            return;
        }

        if (msg.indexOf(stringSay) == 0) {
            msg = msg.replaceFirst(stringSay, "");
            if ("".equals(msg)) {
                chain = new PlainText("？ \n你总得告诉我要说什么吧？").plus(new Face(244));
                group.sendMessage(chain);
                return;
            }
            Plugin plugin = new Plugin();
            ExternalResource audio = plugin.getVoice(msg);
            if (audio == null) {
                group.sendMessage("语音系统未配置，请联系管理员");
                return;
            }
            Audio audio1 = group.uploadAudio(audio);
            group.sendMessage(audio1);
            audio.close();
            return;
        }

        if (msg.contains(stringQq)) {
            msg = msg.replace(stringQq, "");
            msg = msg.replace(" ", "");
            JSONObject jsonObject = plugin.getPower(msg, "qq");
            if(jsonObject==null){
                group.sendMessage("获取失败");
                return;
            }
            String heroInfo = jsonObject.getString("data");
            String heroImg = jsonObject.getString("img");
            Image image = getImageAdd(heroImg);
            chain = new MessageChainBuilder()
                    .append(new At(senderId))
                    .append(image)
                    .append(new PlainText("\n" + heroInfo))
                    .build();
            group.sendMessage(chain);
            return;
        }

        if (msg.contains(stringWeChat)) {
            msg = msg.replace(stringWeChat, "");
            msg = msg.replace(" ", "");
            JSONObject jsonObject = plugin.getPower(msg, "wx");
            if(jsonObject==null){
                group.sendMessage("获取失败");
                return;
            }
            String heroInfo = jsonObject.getString("data");
            String heroImg = jsonObject.getString("img");
            Image image = getImageAdd(heroImg);
            chain = new MessageChainBuilder()
                    .append(new At(senderId))
                    .append(image)
                    .append(new PlainText("\n" + heroInfo))
                    .build();
            group.sendMessage(chain);
            return;
        }

        if (stringHoroscope.equals(msg)) {
            chain = new MessageChainBuilder()
                    .append(new At(senderId))
                    .append(new PlainText("\n请@我并发送星座名\n例如@XXX 白羊"))
                    .build();
            group.sendMessage(chain);
            return;
        }

        if (stringCombatPowerQuery.equals(msg)) {
            chain = new MessageChainBuilder()
                    .append(new At(senderId))
                    .append(new PlainText("\n请@我并发送qq/微信+英雄名\n例如@XXX qq 伽罗"))
                    .build();
            group.sendMessage(chain);
            return;
        }

        if (stringMusicSystem.equals(msg)) {
            chain = new MessageChainBuilder()
                    .append(new At(senderId))
                    .append(new PlainText("\n请@我并发送听歌+音乐名\n例如@XXX 听歌稻香"))
                    .build();
            group.sendMessage(chain);
            return;
        }

        if (setting.getRane()) {
            //燃鹅相关
            if (stringRaneMenu.equals(msg)) {
                chain = new MessageChainBuilder()
                        .append(new PlainText("授权燃鹅命令：授权\n登录燃鹅命令：登录\n每日签到命令：签到\n一键补签命令：补签\n燃鹅信息命令：我的燃鹅\n抽奖命令：超级抽奖/普通抽奖+抽奖数\n抽奖记录命令：抽奖记录\nPS:请不要频繁操作，如果出现无权限或者时序异常请重新发送登录命令，且授权命令和登录命令绑定，授权完后无需登录\n"))
                        .build();
                group.sendMessage(chain);
                return;
            }

            if (stringAuthorization.equals(msg)) {
                String codes = raneUtil.getLoginLink();
                if (!codes.contains(stringFail)) {
                    String loginUrl = "https://h5.qzone.qq.com/qqq/code/" + codes + "?_proxy=1&from=ide";
                    String qrcode = utils.getCode(loginUrl);
                    ExternalResource img = ExternalResource.create(new File(qrcode));
                    Image image = group.uploadImage(img);
                    chain = new At(senderId).plus(new PlainText("\n请在1分钟内授权").plus(image));
                    group.sendMessage(chain);
                    String msg1 = raneUtil.getLoginTicket(codes);
                    if (!msg1.contains(stringTimeOut)) {
                        group.sendMessage(msg1);
                        group.sendMessage(new At(senderId).plus(new PlainText("\n正在登录，请稍后")));
                        group.sendMessage(raneUtil.login(String.valueOf(senderId)));
                    } else {
                        group.sendMessage(new At(senderId).plus(new PlainText("\n授权超时")));
                    }
                } else {
                    group.sendMessage(new At(senderId).plus(new PlainText("\n获取授权链接失败，请稍后再试")));
                }
                return;
            }

            if (stringLogin.equals(msg)) {
                group.sendMessage(new At(senderId).plus(new PlainText("\n正在登录，请稍后")));
                msg = raneUtil.login(String.valueOf(senderId));
                chain = new At(senderId).plus(new PlainText("\n" + msg));
                group.sendMessage(chain);
                return;
            }

            if (stringSign.equals(msg)) {
                group.sendMessage(new At(senderId).plus(new PlainText("\n正在签到，请稍后")));
                msg = raneUtil.getSign(String.valueOf(senderId));
                chain = new At(senderId).plus(new PlainText("\n" + msg));
                group.sendMessage(chain);
                Calendar calendar = Calendar.getInstance();
                int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
                if (week == 0) {
                    group.sendMessage(new At(senderId).plus(new PlainText("\n正在超级签到，请稍后")));
                    Thread.sleep(5000);
                    msg = raneUtil.getSuperSign(String.valueOf(senderId));
                    chain = new At(senderId).plus(new PlainText("\n" + msg));
                    group.sendMessage(chain);
                }
                return;
            }

            if (stringReissue.equals(msg)) {
                group.sendMessage(new At(senderId).plus(new PlainText("\n正在补签，请稍后")));
                String[] msglist1 = raneUtil.getReissueSign(String.valueOf(senderId));
                if (msglist1 != null) {
                    StringBuilder msgBuilder = new StringBuilder(msg);
                    for (int i = 0; i < msglist1.length; i++) {
                        int i1 = i + 1;
                        msgBuilder.append("\nday").append(i1).append(":").append(msglist1[i]);
                    }
                    msg = msgBuilder.toString();
                    chain = new At(senderId).plus(new PlainText(msg));
                    group.sendMessage(chain);
                } else {
                    group.sendMessage(new At(senderId).plus(new PlainText("\n请先授权")));
                }
                return;
            }

            if (stringLotteryRecord.equals(msg)) {
                group.sendMessage(new At(senderId).plus(new PlainText("\n正在查询，请稍后")));
                String[] msglist2 = raneUtil.getGiftList(String.valueOf(senderId));
                if (msglist2 != null) {
                    StringBuilder msgBuilder = new StringBuilder(msg);
                    for (String s : msglist2) {
                        msgBuilder.append("\n").append(s);
                    }
                    msg = msgBuilder.toString();
                    chain = new At(senderId).plus(new PlainText(msg));
                    group.sendMessage(chain);
                } else {
                    group.sendMessage(new At(senderId).plus(new PlainText("\n请先授权登录")));
                }
                return;
            }

            if (msg.contains(stringSuperLottery)) {
                msg = msg.replaceAll("[^0-9]", "");
                if (!"".equals(msg)) {
                    int cs = Integer.parseInt(msg);
                    System.out.println("将要进行" + msg + "次抽奖");
                    if (cs < numEleven) {
                        group.sendMessage(new At(senderId).plus(new PlainText("\n正在超级抽奖，请稍后")));
                        StringBuilder msg1 = new StringBuilder();
                        for (int i = 0; i < cs; i++) {
                            msg1.append("\n超级抽奖:").append(raneUtil.superLottery(String.valueOf(senderId)));
                            if (msg1.toString().contains("时序") || msg1.toString().contains("奖券") || msg1.toString().contains("失败")) {
                                break;
                            }
                            Thread.sleep(5000);
                        }
                        group.sendMessage(new At(senderId).plus(new PlainText(msg1.toString())));
                    } else {
                        group.sendMessage(new At(senderId).plus(new PlainText("\n超级抽奖次数限制在10次以内")));
                    }
                } else {
                    group.sendMessage(new At(senderId).plus(new PlainText("\n请输入抽奖次数")));
                }
                return;
            }

            if (msg.contains(stringNormalLottery)) {
                msg = msg.replaceAll("[^0-9]", "");
                if (!"".equals(msg)) {
                    int cs = Integer.parseInt(msg);
                    System.out.println("将要进行" + msg + "次抽奖");
                    if (cs < numEleven) {
                        group.sendMessage(new At(senderId).plus(new PlainText("\n正在普通抽奖，请稍后")));
                        StringBuilder msg1 = new StringBuilder();
                        for (int i = 0; i < cs; i++) {
                            msg1.append("\n普通抽奖:").append(raneUtil.normalLottery(String.valueOf(senderId)));
                            if (msg1.toString().contains("时序") || msg1.toString().contains("奖券") || msg1.toString().contains("失败")) {
                                break;
                            }
                            Thread.sleep(5000);
                        }
                        group.sendMessage(new At(senderId).plus(new PlainText(msg1.toString())));
                    } else {
                        group.sendMessage(new At(senderId).plus(new PlainText("\n普通抽奖次数限制在10次以内")));
                    }
                } else {
                    group.sendMessage(new At(senderId).plus(new PlainText("\n请输入抽奖次数")));
                }
                return;
            }

            if (stringMineRane.equals(msg)) {
                JSONObject jsondata = raneBase.readUserFile(String.valueOf(senderId));
                String money = jsondata.getString("金币");
                String gfc = jsondata.getString("奖券");
                msg = "\nQQ:" + senderId + "\n金币:" + money + "\n奖券:" + gfc;
                group.sendMessage(new At(senderId).plus(new PlainText(msg)));
                return;
            }
        }

        if (stringFortune.equals(msg) || stringTodayFortune.equals(msg)) {
            String replaceMsg = plugin.getFortune();
            if (!replaceMsg.contains(stringGetFailed)) {
                chain = new At(senderId).plus(new PlainText("\n" + replaceMsg));
            } else {
                chain = new At(senderId).plus(new PlainText("\n" + replaceMsg).plus(new Face(263)));
            }
            group.sendMessage(chain);
            return;
        }

        if (stringNews.equals(msg) || stringTodayNews.equals(msg)) {
            String newsImgUrl = plugin.getNews();
            if (!newsImgUrl.contains(stringFail)) {
                Image image = getImageAdd(newsImgUrl);
                chain = new MessageChainBuilder()
                        .append(image)
                        .build();
            } else {
                chain = new At(senderId).plus(new PlainText("\n获取失败"));
            }
            group.sendMessage(chain);
            return;
        }

        if (stringBeauty.equals(msg) || stringBeautyPictures.equals(msg)) {
            String filepath = plugin.getImage();
            if (filepath.contains(stringFail)) {
                group.sendMessage(filepath);
                return;
            }
            Image image = getImageAdd(filepath);
            FlashImage flashImage = FlashImage.from(image);
            group.sendMessage(flashImage);
            return;
        }

        //获取星座运势
        for (String s : twelveHoroscop) {
            if (msg.equals(s) || msg.equals(s + "座")) {
                chain = new MessageChainBuilder()
                        .append(new At(senderId))
                        .append(new PlainText("\n" + s + "座运势\n" + plugin.getHoroscope(s)))
                        .build();
                group.sendMessage(chain);
                return;
            }
        }

        if (stringGetSign.equals(msg) || stringGuanyinGetSign.equals(msg)) {
            File qqFile = new File(utils.getPluginsDataPath() + "/cache/qq/" + senderId + ".cache");
            String qian;
            if (qqFile.exists()) {
                qian = utils.readData(qqFile);
                if (numOne.equals(qian)) {
                    qian = plugin.getCq();
                    utils.rewriteData(qqFile, qian);
                }
            } else {
                qian = plugin.getCq();
                utils.rewriteData(qqFile, qian);
            }
            chain = new MessageChainBuilder()
                    .append(new At(senderId))
                    .append(new PlainText("\n" + qian))
                    .build();
            group.sendMessage(chain);
            return;
        }

        if (stringMenu.equals(msg)) {
            group.sendMessage(getMenuTxt());
        } else if (setting.getAi()) {
            String url = "http://api.qingyunke.com/api.php?key=free&appid=0&msg=" + msg;
            String data = utils.okHttpClientGet(url);
            JSONObject jsonObject = JSONObject.parseObject(data);
            String txt = jsonObject.getString("content");
            txt = txt.replace("{br}", "\n");
            if (!msg.contains(stringWeather)) {
                chain = new MessageChainBuilder()
                        .append(new PlainText(txt))
                        .build();
                group.sendMessage(chain);
            }
        }

    }

    /**
     * 更新新闻
     */
    public String friendMsgDel(String imgUrl) {

        Path newsFilePath = utils.getPluginsDataPath();
        File newsfile = new File(newsFilePath + "/cache/news.cache");
        // 格式化时间
        SimpleDateFormat sdf = new SimpleDateFormat();
        // a为am/pm的标记
        sdf.applyPattern("MMdd");
        // 获取当前时间
        Date date = new Date();
        String nowData = sdf.format(date);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", nowData);
        jsonObject.put("url", imgUrl);

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(String.valueOf(newsfile)));
            out.write(String.valueOf(jsonObject));
            out.close();
            return "修改成功";
        } catch (IOException e) {
            return "修改失败";
        }


    }

    /**
     * 菜单
     */
    public MessageChain getMenuTxt() {
        return new MessageChainBuilder()
                .append(new Face(147))
                .append(new PlainText("           菜单          "))
                .append(new Face(147))
                .append(new PlainText("\n◇━━━━━━━━◇\n"))
                .append(new Face(190))
                .append(new PlainText("今日运势  今日新闻"))
                .append(new Face(190))
                .append(new PlainText("\n"))
                .append(new Face(190))
                .append(new PlainText("星座运势  观音求签"))
                .append(new Face(190))
                .append(new PlainText("\n"))
                .append(new Face(190))
                .append(new PlainText("音乐系统  语音系统"))
                .append(new Face(190))
                .append(new PlainText("\n"))
                .append(new Face(190))
                .append(new PlainText("美女图片  战力查询"))
                .append(new Face(190))
                .append(new PlainText("\n"))
                .append(new Face(190))
                .append(new PlainText("燃鹅菜单  敬请期待"))
                .append(new Face(190))
                .append(new PlainText("\n◇━━━━━━━━◇\nPS:@我并发相应文字查看指令"))
                .build();
    }

    public ArrayList<String> getTwelveHoroscopeList() {

        ArrayList<String> twelveHoroscope = new ArrayList<>();
        twelveHoroscope.add("白羊");
        twelveHoroscope.add("金牛");
        twelveHoroscope.add("双子");
        twelveHoroscope.add("巨蟹");
        twelveHoroscope.add("狮子");
        twelveHoroscope.add("处女");
        twelveHoroscope.add("天秤");
        twelveHoroscope.add("天蝎");
        twelveHoroscope.add("射手");
        twelveHoroscope.add("摩羯");
        twelveHoroscope.add("水瓶");
        twelveHoroscope.add("双鱼");

        return twelveHoroscope;
    }

    public Image getImageAdd(String filepath) throws IOException {
        ExternalResource img = ExternalResource.create(new File(filepath));
        Image image = this.group.uploadImage(img);
        img.close();
        return image;
    }
}

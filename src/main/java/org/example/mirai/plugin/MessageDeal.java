package org.example.mirai.plugin;

import com.alibaba.fastjson.JSONObject;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.utils.ExternalResource;
import org.example.mirai.plugin.Rane.RaneBase;
import org.example.mirai.plugin.Rane.RaneUtil;
import org.example.mirai.plugin.Toolkit.Plugin;
import org.example.mirai.plugin.Toolkit.Setting;
import org.example.mirai.plugin.Toolkit.Utils;

import javax.script.ScriptException;
import java.awt.*;
import java.io.*;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class MessageDeal {
    Plugin plugin = new Plugin();
    Utils utils = new Utils();
    Group group = null;
    RaneUtil raneUtil = new RaneUtil();
    RaneBase raneBase = new RaneBase();
    Setting setting = new Setting();

    public void msg_del(Long sender_id, Group group, String msg) throws IOException, InterruptedException, ScriptException, NoSuchMethodException {
        MessageChain chain;
        ArrayList<String> twelveHoroscop = get_twelveHoroscope_list();
        this.group = group;
        System.out.println("收到的消息:" + msg + " 消息长度:" + msg.length());

        if (msg.equals("")) {
            chain = new MessageChainBuilder()
                    .append(new At(sender_id))
                    .append(new PlainText("\n你没得事情干唛？@我作甚么？"))
                    .append(new Face(312))
                    .build();
            group.sendMessage(chain);
            return;
        }

        if (msg.indexOf("听歌") == 0) {
            msg = msg.replace("听歌", "");
            if (msg.equals("")) {
                chain = new PlainText("？ \n你总得告诉我要听什么吧？").plus(new Face(244));
                group.sendMessage(chain);
                return;
            }
            Plugin plugin = new Plugin();
            MusicShare musicShare = plugin.get_music(msg);
            group.sendMessage(musicShare);
            return;
        }

        if (msg.indexOf("说") == 0) {
            msg = msg.replaceFirst("说", "");
            if (msg.equals("")) {
                chain = new PlainText("？ \n你总得告诉我要说什么吧？").plus(new Face(244));
                group.sendMessage(chain);
                return;
            }
            Plugin plugin = new Plugin();
            ExternalResource voice = plugin.get_voice(group, msg);
            if (voice == null) {
                group.sendMessage("语音系统未配置，请联系管理员");
                return;
            }
            Voice voice1 = group.uploadVoice(voice);
            group.sendMessage(voice1);
            voice.close();
            return;
        }

        if (msg.contains("qq")) {
            msg = msg.replace("qq", "");
            msg = msg.replace(" ", "");
            chain = new MessageChainBuilder()
                    .append(new At(sender_id))
                    .append(new PlainText("\n" + plugin.get_power(msg, "qq")))
                    .build();
            group.sendMessage(chain);
            return;
        }

        if (msg.contains("微信")) {
            msg = msg.replace("微信", "");
            msg = msg.replace(" ", "");
            chain = new MessageChainBuilder()
                    .append(new At(sender_id))
                    .append(new PlainText("\n" + plugin.get_power(msg, "wx")))
                    .build();
            group.sendMessage(chain);
            return;
        }

        if (msg.equals("星座运势")) {
            chain = new MessageChainBuilder()
                    .append(new At(sender_id))
                    .append(new PlainText("\n请@我并发送星座名\n例如@XXX 白羊"))
                    .build();
            group.sendMessage(chain);
            return;
        }

        if (msg.equals("战力查询")) {
            chain = new MessageChainBuilder()
                    .append(new At(sender_id))
                    .append(new PlainText("\n请@我并发送qq/微信+英雄名\n例如@XXX qq 伽罗"))
                    .build();
            group.sendMessage(chain);
            return;
        }

        if (msg.equals("音乐系统")) {
            chain = new MessageChainBuilder()
                    .append(new At(sender_id))
                    .append(new PlainText("\n请@我并发送听歌+音乐名\n例如@XXX 听歌稻香"))
                    .build();
            group.sendMessage(chain);
            return;
        }

        if (setting.getRane()) {
            //燃鹅相关
            if (msg.equals("燃鹅菜单")) {
                chain = new MessageChainBuilder()
                        .append(new PlainText("授权燃鹅命令：授权\n登录燃鹅命令：登录\n每日签到命令：签到\n一键补签命令：补签\n燃鹅信息命令：我的燃鹅\n抽奖命令：超级抽奖/普通抽奖+抽奖数\n抽奖记录命令：抽奖记录\nPS:请不要频繁操作，如果出现无权限或者时序异常请重新发送登录命令，且授权命令和登录命令绑定，授权完后无需登录\n"))
                        .build();
                group.sendMessage(chain);
                return;
            }

            if (msg.equals("授权")) {
                String codes = raneUtil.get_login_link();
                if (!codes.contains("失败")) {
                    String login_url = "https://h5.qzone.qq.com/qqq/code/" + codes + "?_proxy=1&from=ide";
                    String qrcode = utils.get_code(login_url);
                    ExternalResource img = ExternalResource.create(new File(qrcode));
                    Image image = group.uploadImage(img);
                    chain = new At(sender_id).plus(new PlainText("\n请在1分钟内授权").plus(image));
                    group.sendMessage(chain);
                    String msg1 = raneUtil.get_login_ticket(codes);
                    if (!msg1.contains("超时")) {
                        group.sendMessage(msg1);
                        group.sendMessage(new At(sender_id).plus(new PlainText("\n正在登录，请稍后")));
                        group.sendMessage(raneUtil.login(String.valueOf(sender_id)));
                    } else {
                        group.sendMessage(new At(sender_id).plus(new PlainText("\n授权超时")));
                    }
                } else {
                    group.sendMessage(new At(sender_id).plus(new PlainText("\n获取授权链接失败，请稍后再试")));
                }
                return;
            }

            if (msg.equals("登录")) {
                group.sendMessage(new At(sender_id).plus(new PlainText("\n正在登录，请稍后")));
                msg = raneUtil.login(String.valueOf(sender_id));
                chain = new At(sender_id).plus(new PlainText("\n" + msg));
                group.sendMessage(chain);
                return;
            }

            if (msg.equals("签到")) {
                group.sendMessage(new At(sender_id).plus(new PlainText("\n正在签到，请稍后")));
                msg = raneUtil.get_sign(String.valueOf(sender_id));
                chain = new At(sender_id).plus(new PlainText("\n" + msg));
                group.sendMessage(chain);
                Calendar calendar = Calendar.getInstance();
                int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
                if (week == 0) {
                    group.sendMessage(new At(sender_id).plus(new PlainText("\n正在超级签到，请稍后")));
                    Thread.sleep(5000);
                    msg = raneUtil.get_super_sign(String.valueOf(sender_id));
                    chain = new At(sender_id).plus(new PlainText("\n" + msg));
                    group.sendMessage(chain);
                }
                return;
            }

            if (msg.equals("补签")) {
                group.sendMessage(new At(sender_id).plus(new PlainText("\n正在补签，请稍后")));
                String[] msglist1 = raneUtil.get_reissue_sign(String.valueOf(sender_id));
                if (msglist1 != null) {
                    for (int i = 0; i < msglist1.length; i++) {
                        int i1 = i + 1;
                        msg = msg + "\nday" + i1 + ":" + msglist1[i];
                    }
                    chain = new At(sender_id).plus(new PlainText(msg));
                    group.sendMessage(chain);
                } else {
                    group.sendMessage(new At(sender_id).plus(new PlainText("\n请先授权")));
                }
                return;
            }

            if (msg.equals("抽奖记录")) {
                group.sendMessage(new At(sender_id).plus(new PlainText("\n正在查询，请稍后")));
                String[] msglist2 = raneUtil.get_gift_list(String.valueOf(sender_id));
                if (msglist2 != null) {
                    for (int i = 0; i < msglist2.length; i++) {
                        int i1 = i + 1;
                        msg = msg + "\n" + msglist2[i];
                    }
                    chain = new At(sender_id).plus(new PlainText(msg));
                    group.sendMessage(chain);
                } else {
                    group.sendMessage(new At(sender_id).plus(new PlainText("\n请先授权登录")));
                }
                return;
            }

            if (msg.contains("超级抽奖")) {
                msg = msg.replaceAll("[^0-9]", "");
                if (!msg.equals("")) {
                    int cs = Integer.parseInt(msg);
                    System.out.println("将要进行" + msg + "次抽奖");
                    if (cs < 11) {
                        group.sendMessage(new At(sender_id).plus(new PlainText("\n正在超级抽奖，请稍后")));
                        String msg1 = "";
                        for (int i = 0; i < cs; i++) {
                            msg1 = msg1 + "\n超级抽奖:" + raneUtil.super_lottery(String.valueOf(sender_id));
                            if (msg1.contains("时序") || msg1.contains("奖券") || msg1.contains("失败")) {
                                break;
                            }
                            Thread.sleep(5000);
                        }
                        group.sendMessage(new At(sender_id).plus(new PlainText(msg1)));
                    } else {
                        group.sendMessage(new At(sender_id).plus(new PlainText("\n超级抽奖次数限制在10次以内")));
                    }
                } else {
                    group.sendMessage(new At(sender_id).plus(new PlainText("\n请输入抽奖次数")));
                }
                return;
            }

            if (msg.contains("普通抽奖")) {
                msg = msg.replaceAll("[^0-9]", "");
                if (!msg.equals("")) {
                    int cs = Integer.parseInt(msg);
                    System.out.println("将要进行" + msg + "次抽奖");
                    if (cs < 11) {
                        group.sendMessage(new At(sender_id).plus(new PlainText("\n正在普通抽奖，请稍后")));
                        String msg1 = "";
                        for (int i = 0; i < cs; i++) {
                            msg1 = msg1 + "\n普通抽奖:" + raneUtil.normal_lottery(String.valueOf(sender_id));
                            if (msg1.contains("时序") || msg1.contains("奖券") || msg1.contains("失败")) {
                                break;
                            }
                            Thread.sleep(5000);
                        }
                        group.sendMessage(new At(sender_id).plus(new PlainText(msg1)));
                    } else {
                        group.sendMessage(new At(sender_id).plus(new PlainText("\n普通抽奖次数限制在10次以内")));
                    }
                } else {
                    group.sendMessage(new At(sender_id).plus(new PlainText("\n请输入抽奖次数")));
                }
                return;
            }

            if (msg.equals("我的燃鹅")) {
                JSONObject jsondata = raneBase.read_user_file(String.valueOf(sender_id));
                String money = jsondata.getString("金币");
                String gfc = jsondata.getString("奖券");
                msg = "\nQQ:" + sender_id + "\n金币:" + money + "\n奖券:" + gfc;
                group.sendMessage(new At(sender_id).plus(new PlainText(msg)));
                return;
            }
        }

        if (msg.equals("奥运") || msg.contains("奥运排行")) {
            String message = plugin.medal_rank();
            if (message != null) {
                group.sendMessage(message);
            }else {
                group.sendMessage("获取失败");
            }

            return;
        }

        if (msg.equals("运势") || msg.equals("今日运势")) {
            String replace_msg = plugin.get_fortune();
            if (replace_msg.indexOf("获取失败") == -1) {
                chain = new At(sender_id).plus(new PlainText("\n" + replace_msg));
            } else {
                chain = new At(sender_id).plus(new PlainText("\n" + replace_msg).plus(new Face(263)));
            }
            group.sendMessage(chain);
            return;
        }

        if (msg.equals("新闻") || msg.equals("今日新闻")) {
            String news_img_url = plugin.get_news();
            if (!news_img_url.contains("失败")) {
                Image image = get_image_add(news_img_url);
                chain = new MessageChainBuilder()
                        .append(image)
                        .build();
            } else {
                chain = new At(sender_id).plus(new PlainText("\n获取失败"));
            }
            group.sendMessage(chain);
            return;
        }

        if (msg.equals("美女") || msg.equals("美女图片")) {
            String filepath = plugin.get_image();
            Image image = get_image_add(filepath);
            FlashImage flashImage = FlashImage.from(image);
            group.sendMessage(flashImage);
            return;
        }

        //获取星座运势
        for (
                int i = 0; i < twelveHoroscop.size(); i++) {
            if (msg.equals(twelveHoroscop.get(i)) || msg.equals(twelveHoroscop.get(i) + "座")) {
                chain = new MessageChainBuilder()
                        .append(new At(sender_id))
                        .append(new PlainText("\n" + twelveHoroscop.get(i) + "座运势\n" + plugin.get_horoscope(twelveHoroscop.get(i))))
                        .build();
                group.sendMessage(chain);
                return;
            }
        }

        if (msg.equals("求签") || msg.equals("观音求签")) {
            File qq_file = new File(utils.get_plugins_data_path() + "/cache/qq/" + sender_id + ".cache");
            String qian = null;
            if (qq_file.exists()) {
                qian = utils.read_data(qq_file);
                if (qian.equals("1")) {
                    qian = plugin.get_cq();
                    utils.rewrite_data(qq_file, qian);
                }
            } else {
                qian = plugin.get_cq();
                utils.rewrite_data(qq_file, qian);
            }
            chain = new MessageChainBuilder()
                    .append(new At(sender_id))
                    .append(new PlainText("\n" + qian))
                    .build();
            group.sendMessage(chain);
            return;
        }

        if (msg.equals("菜单")) {
            group.sendMessage(get_menu_txt());
            return;
        }

        if (msg.equals("福利") || msg.equals("羊毛") || msg.equals("羊毛福利")) {
            try {
                utils.gift();
                Image image = get_image_add(utils.get_plugins_data_path() + "/fl.jpg");
                chain = new MessageChainBuilder()
                        .append(new At(sender_id))
                        .append(new PlainText("\n请输入想查看的福利序号\n例：序号1"))
                        .append(image)
                        .build();
                group.sendMessage(chain);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        if (msg.indexOf("序号") == 0) {
            msg = msg.replace("序号", "");
            try {
                int i = Integer.parseInt(msg);
                if (i > 10) {
                    group.sendMessage("请正确输入");
                    return;
                }
                String filepath = utils.get_gift(i);
                Image image = get_image_add(filepath);
                group.sendMessage(image);
                return;
            } catch (Exception e) {
                group.sendMessage("请正确输入");
                return;
            }
        }

        if (msg.equals("关于") || msg.equals("关于作者")) {
            String filepath = utils.get_plugins_data_path() + "/qrcode.png";
            Image image = get_image_add(filepath);
            chain = new MessageChainBuilder()
                    .append(new At(sender_id))
                    .append(new PlainText("\n当前版本1.0.7\n更新日期2021年8月20日\n本项目已开源\n戳↓↓↓↓↓"))
                    .append(image)
                    .build();
            group.sendMessage(chain);
            return;
        } else if (setting.getAI()) {
            String URL = "http://api.qingyunke.com/api.php?key=free&appid=0&msg=" + msg;
            String data = utils.okHttpClient_get(URL);
            JSONObject jsonObject = JSONObject.parseObject(data);
            String txt = jsonObject.getString("content");
            txt = txt.replace("{br}", "\n");
            if (!msg.contains("天气")) {
                chain = new MessageChainBuilder()
                        .append(new PlainText(txt))
                        .build();
                group.sendMessage(chain);
            }
        }

    }

    //更新新闻
    public String friend_msg_del(String img_url) {

        Path news_file_path = utils.get_plugins_data_path();
        File newsfile = new File(String.valueOf(news_file_path) + "/cache/news.cache");

        SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
        sdf.applyPattern("MMdd");// a为am/pm的标记
        Date date = new Date();// 获取当前时间
        String now_data = sdf.format(date);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", now_data);
        jsonObject.put("url", img_url);

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(String.valueOf(newsfile)));
            out.write(String.valueOf(jsonObject));
            out.close();
            return "修改成功";
        } catch (IOException e) {
            return "修改失败";
        }


    }

    //菜单
    public MessageChain get_menu_txt() {
        MessageChain chain = new MessageChainBuilder()
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
                .append(new PlainText("羊毛福利  奥运排行"))
                .append(new Face(190))
                .append(new PlainText("\n"))
                .append(new Face(190))
                .append(new PlainText("燃鹅菜单  关于作者"))
                .append(new Face(190))
                .append(new PlainText("\n◇━━━━━━━━◇\nPS:@我并发相应文字查看指令"))
                .build();
        return chain;
    }

    public ArrayList<String> get_twelveHoroscope_list() {

        ArrayList<String> TwelveHoroscope = new ArrayList<>();
        TwelveHoroscope.add("白羊");
        TwelveHoroscope.add("金牛");
        TwelveHoroscope.add("双子");
        TwelveHoroscope.add("巨蟹");
        TwelveHoroscope.add("狮子");
        TwelveHoroscope.add("处女");
        TwelveHoroscope.add("天秤");
        TwelveHoroscope.add("天蝎");
        TwelveHoroscope.add("射手");
        TwelveHoroscope.add("摩羯");
        TwelveHoroscope.add("水瓶");
        TwelveHoroscope.add("双鱼");

        return TwelveHoroscope;
    }

    public Image get_image_add(String filepath) throws IOException {
        ExternalResource img = ExternalResource.create(new File(filepath));
        Image image = this.group.uploadImage(img);
        img.close();
        return image;
    }
}

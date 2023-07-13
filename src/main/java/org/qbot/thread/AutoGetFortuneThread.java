package org.qbot.thread;

import com.alibaba.fastjson2.JSONArray;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.Face;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import org.qbot.toolkit.PluginUtil;
import org.qbot.toolkit.Setting;
import org.qbot.toolkit.Utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;

/**
 * AutoGetFortuneThread class
 *
 * @author 649953543@qq.com
 * @date 2021/09/22
 */

public class AutoGetFortuneThread extends Thread {
    @Override
    @SuppressWarnings("InfiniteLoopStatement")
    public void run() {
        super.run();
        String filePath = Utils.getPluginsDataPath() + "/cache/week.cache";
        PluginUtil pluginUtil = new PluginUtil();
        Utils utils = new Utils();
        Setting.getBot().getLogger().info("开启自动获取运势线程成功！");
        try {
            while (true) {
                Bot bot = Bot.getInstance(Setting.getQq());
                Calendar calendar = Calendar.getInstance();
                int week1 = calendar.get(Calendar.DAY_OF_WEEK);
                BufferedReader in = new BufferedReader(new FileReader(filePath));
                int week = Integer.parseInt(in.readLine());
                JSONArray groupList = Setting.getGroup();
                if (week != week1) {
                    String txt = pluginUtil.getFortune();
                    if (!txt.contains("失败")) {
                        Setting.getBot().getLogger().info("正在发送运势");
                        for (int i = 0; i < groupList.size(); i++) {
                            try {
                                Group group = bot.getGroup(groupList.getLongValue(i));
                                MessageChain chain = new MessageChainBuilder()
                                        .append(new PlainText("滴滴滴"))
                                        .append(new Face(307))
                                        .append(new PlainText("\n" + txt))
                                        .build();
                                assert group != null;
                                group.sendMessage(chain);
                                Thread.sleep(1000);
                            } catch (Exception e) {
                                Setting.getBot().getLogger().info("发送运势失败");
                            }
                        }
                        Utils.rewrite(week1);
                    }
                }
                if (!bot.isOnline()) {
                    try {
                        utils.sendMsgToWeChat("机器人:" + bot.getId() + "\n已离线\n" + utils.getTime4());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    Setting.getBot().getLogger().info("机器人已离线");
                }
                Thread.sleep(600000);
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}


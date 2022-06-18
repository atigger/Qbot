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
        Utils utils = new Utils();
        String filePath = utils.getPluginsDataPath() + "/cache/week.cache";
        PluginUtil pluginUtil = new PluginUtil();
        System.out.println("开启自动获取运势线程成功！");
        while (true) {
            try {
                Setting setting = new Setting();
                Calendar calendar = Calendar.getInstance();
                int week1 = calendar.get(Calendar.DAY_OF_WEEK);

                BufferedReader in = new BufferedReader(new FileReader(filePath));
                int week = Integer.parseInt(in.readLine());

                long botqq = setting.getQq();
                JSONArray groupList = setting.getGroup();
                Bot bot = Bot.getInstance(botqq);
                if (week != week1) {
                    String txt = pluginUtil.getFortune();
                    if (!txt.contains("失败")) {
                        System.out.println("正在发送运势");
                        for (int i = 0; i < groupList.size(); i++) {
                            Group group = bot.getGroup(groupList.getLongValue(i));
                            MessageChain chain = new MessageChainBuilder()
                                    .append(new PlainText("滴滴滴"))
                                    .append(new Face(307))
                                    .append(new PlainText("\n" + txt))
                                    .build();
                            assert group != null;
                            group.sendMessage(chain);
                            Thread.sleep(1000);
                        }
                        utils.rewrite(week1);
                    }
                }
                Thread.sleep(600000);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}


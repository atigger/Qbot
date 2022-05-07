package org.qbot.thread;

import com.alibaba.fastjson2.JSONArray;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.utils.ExternalResource;
import org.qbot.toolkit.PluginUtil;
import org.qbot.toolkit.Setting;
import org.qbot.toolkit.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;

/**
 * AutoThread class
 *
 * @author 649953543@qq.com
 * @date 2021/09/22
 */

public class AutoThread extends Thread {
    @Override
    @SuppressWarnings("InfiniteLoopStatement")
    public void run() {
        Utils utils = new Utils();
        Setting setting = new Setting();
        System.out.println("开启自动发送小提醒线程成功！");
        Path dataFolderPath = utils.getPluginsDataPath();
        PluginUtil pluginUtil = new PluginUtil();
        File file1 = new File(dataFolderPath + "/yc.jpg");
        long botQq = setting.getQq();
        JSONArray groupList = setting.getGroup();

        Bot bot = Bot.getInstance(botQq);
        while (true) {
            try {
                boolean autoTips = setting.getAutoTips();
                boolean autoNews = setting.getAutoNews();
                if ("08:00".equals(utils.getNowTime()) && autoNews) {
                    for (int i = 0; i < groupList.size(); i++) {
                        Group group = bot.getGroup((long) groupList.get(i));
                        String newsImgUrl = pluginUtil.getNews();
                        if (!newsImgUrl.contains("失败")) {
                            ExternalResource img = ExternalResource.create(new File(newsImgUrl));
                            assert group != null;
                            Image image = group.uploadImage(img);
                            img.close();
                            MessageChain chain = new MessageChainBuilder()
                                    .append(new PlainText("今日新闻"))
                                    .append(image)
                                    .build();
                            group.sendMessage(chain);
                        }
                    }
                    sleep(60000);
                } else if ("15:00".equals(utils.getNowTime()) && autoTips) {
                    for (int i = 0; i < groupList.size(); i++) {
                        Group group = bot.getGroup((long) groupList.get(i));
                        ExternalResource img = ExternalResource.create(new File(String.valueOf(file1)));
                        assert group != null;
                        Image image = group.uploadImage(img);
                        MessageChain chain = new MessageChainBuilder()
                                .append(new PlainText(pluginUtil.moFish()))
                                .build();
                        group.sendMessage(chain);
                    }
                    sleep(60000);
                }
                sleep(30000);
            } catch (InterruptedException | IOException | ParseException e) {
                e.printStackTrace();
            }

        }
    }
}

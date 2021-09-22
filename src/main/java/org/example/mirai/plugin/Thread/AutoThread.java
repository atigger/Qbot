package org.example.mirai.plugin.thread;

import com.alibaba.fastjson.JSONArray;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.utils.ExternalResource;
import org.example.mirai.plugin.toolkit.Plugin;
import org.example.mirai.plugin.toolkit.Setting;
import org.example.mirai.plugin.toolkit.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

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
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Path dataFolderPath = utils.getPluginsDataPath();
        Plugin plugin = new Plugin();
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
                        Group group = bot.getGroup(groupList.getLong(i));
                        String newsImgUrl = plugin.getNews();
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
                        Group group = bot.getGroup(groupList.getLong(i));
                        ExternalResource img = ExternalResource.create(new File(String.valueOf(file1)));
                        assert group != null;
                        Image image = group.uploadImage(img);
                        MessageChain chain = new MessageChainBuilder()
                                .append(new PlainText("喂！3点几嚟！饮茶时间到！"))
                                .append(new Face(229))
                                .append(image)
                                .build();
                        group.sendMessage(chain);
                    }
                    sleep(60000);
                }
                sleep(30000);
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }

        }
    }
}

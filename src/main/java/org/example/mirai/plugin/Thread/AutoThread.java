package org.example.mirai.plugin.Thread;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.utils.ExternalResource;
import org.example.mirai.plugin.Toolkit.Plugin;
import org.example.mirai.plugin.Toolkit.Setting;
import org.example.mirai.plugin.Toolkit.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

public class AutoThread extends Thread {
    @Override
    public void run() {
        Utils utils = new Utils();
        Setting setting = new Setting();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Path dataFolderPath = utils.get_plugins_data_path();
        Plugin plugin = new Plugin();
        File file1 = new File(String.valueOf(dataFolderPath) + "/yc.jpg");
        long BOTQQ = setting.getQQ();
        JSONArray group_list = setting.getGroup();

        Bot bot = Bot.getInstance(BOTQQ);
        while (true) {
            try {
                boolean AutoTips = setting.getAutoTips();
                boolean AutoNews = setting.getAutoNews();
                if (utils.get_now_time().equals("11:35") && AutoNews) {
                    for (int i = 0; i < group_list.size(); i++) {
                        Group group = bot.getGroup(group_list.getLong(i));
                        String news_img_url = plugin.get_news();
                        if (!news_img_url.contains("失败")) {
                            ExternalResource img = ExternalResource.create(new File(news_img_url));
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
                } else if (utils.get_now_time().equals("15:00") && AutoTips) {
                    for (int i = 0; i < group_list.size(); i++) {
                        Group group = bot.getGroup(group_list.getLong(i));
                        ExternalResource img = ExternalResource.create(new File(String.valueOf(file1)));
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

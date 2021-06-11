package org.example.mirai.plugin.Thread;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.*;
import org.example.mirai.plugin.Toolkit.Plugin;
import org.example.mirai.plugin.Toolkit.Setting;
import org.example.mirai.plugin.Toolkit.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Calendar;

public class AutoGetFortuneThread extends Thread {
    @Override
    public void run() {
        Utils utils = new Utils();
        String file_path = utils.get_plugins_data_path() + "/cache/week.cache";
        Plugin plugin = new Plugin();

        try {
            sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while (true) {
            System.out.println("正在获取运势...");
            try {
                Setting setting = new Setting();
                Calendar calendar = Calendar.getInstance();
                int week1 = calendar.get(Calendar.DAY_OF_WEEK);

                BufferedReader in = new BufferedReader(new FileReader(file_path));
                int week = Integer.parseInt(in.readLine());

                long BOTQQ = setting.getQQ();
                JSONArray group_list = setting.getGroup();
                Bot bot = Bot.getInstance(BOTQQ);
                if (week != week1) {
                    String txt = plugin.get_fortune();
                    if (!txt.contains("失败")) {
                        for (int i = 0; i < group_list.size(); i++) {
                            Group group = bot.getGroup(group_list.getLong(i));
                            MessageChain chain = new MessageChainBuilder()
                                    .append(new PlainText("滴滴滴"))
                                    .append(new Face(307))
                                    .append(new PlainText("\n" + txt))
                                    .build();
                            group.sendMessage(chain);
                            Thread.sleep(1000);
                        }
                        utils.rewrite(week1);
                    }
                }
                sleep(600000);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}


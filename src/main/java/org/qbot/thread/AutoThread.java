package org.qbot.thread;

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
        Setting.getBot().getLogger().info("开启自动发送小提醒线程成功！");
        PluginUtil pluginUtil = new PluginUtil();
        while (true) {
            try {
                Bot bot = Bot.getInstance(Setting.getQq());
                Thread.sleep(1000);
                boolean autoTips = Setting.getAutoTips();
                boolean autoNews = Setting.getAutoNews();
                if ("08:30".equals(utils.getNowTime()) && autoNews) {
                    for (int i = 0; i < Setting.getGroup().size(); i++) {
                        try {
                            Group group = bot.getGroup(Setting.getGroup().getLongValue(i));
                            String newsImgUrl = pluginUtil.getNews();
                            if (!newsImgUrl.contains("失败")) {
                                ExternalResource img = ExternalResource.create(new File(newsImgUrl));
                                assert group != null;
                                Image image = group.uploadImage(img);
                                img.close();
                                MessageChain chain = new MessageChainBuilder().append(new PlainText("今日新闻")).append(image).append(new PlainText(pluginUtil.getOne())).build();
                                group.sendMessage(chain);
                            }
                        } catch (Exception e) {
                            bot.getLogger().info("发送小提醒失败");
                        }
                    }
                    sleep(60000);
                } else if ("15:00".equals(utils.getNowTime()) && autoTips) {
                    for (int i = 0; i < Setting.getGroup().size(); i++) {
//                        try {
//                            Group group = bot.getGroup(Setting.getGroup().getLongValue(i));
//                            assert group != null;
//                            MessageChain chain = new MessageChainBuilder()
//                                    .append(new PlainText(pluginUtil.moFish()))
//                                    .build();
//                            group.sendMessage(chain);
//                        } catch (Exception e) {
//                            bot.getLogger().info("发送小提醒失败");
//                        }
                        try {
                            Group group = bot.getGroup(Setting.getGroup().getLongValue(i));
                            String mofishImgUrl = pluginUtil.moFishNew();
                            if (!mofishImgUrl.contains("失败")) {
                                ExternalResource img = ExternalResource.create(new File(mofishImgUrl));
                                assert group != null;
                                Image image = group.uploadImage(img);
                                img.close();
                                MessageChain chain = new MessageChainBuilder().append(image).build();
                                group.sendMessage(chain);
                            }
                        } catch (Exception e) {
                            bot.getLogger().info("发送小提醒失败");
                        }
                    }
                    sleep(60000);
                }
                sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

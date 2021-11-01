package org.example.mirai.plugin;

import net.mamoe.mirai.console.command.CommandManager;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.*;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import org.example.mirai.plugin.command.CloseMaintainCommand;
import org.example.mirai.plugin.command.OnMaintainCommand;

import org.example.mirai.plugin.thread.StartThread;
import org.example.mirai.plugin.toolkit.*;

import javax.script.ScriptException;
import java.io.*;
import java.nio.file.Path;

/*
使用java请把
src/main/resources/META-INF.services/net.mamoe.mirai.console.plugin.jvm.JvmPlugin
文件内容改成"org.example.mirai.plugin.JavaPluginMain"也就是当前主类
使用java可以把kotlin文件夹删除不会对项目有影响

在settings.gradle.kts里改生成的插件.jar名称
build.gradle.kts里改依赖库和插件版本
在主类下的JvmPluginDescription改插件名称，id和版本
用runmiraikt这个配置可以在ide里运行，不用复制到mcl或其他启动器
 */

/**
 * JavaPluginMain class
 *
 * @author 649953543@qq.com
 * @date 2021/09/22
 */

public final class JavaPluginMain extends JavaPlugin {
    public static final JavaPluginMain INSTANCE = new JavaPluginMain();

    public JavaPluginMain() {
        super(new JvmPluginDescriptionBuilder("org.qbot.plugin", "1.1.1")
                .info("EG")
                .build());
    }

    @Override
    public void onEnable() {
        CommandManager.INSTANCE.registerCommand(OnMaintainCommand.INSTANCE, true);
        CommandManager.INSTANCE.registerCommand(CloseMaintainCommand.INSTANCE, true);
        getLogger().info("启动中。。。");
        Path configFolderPath = getConfigFolderPath();

        CreateFile createFile = new CreateFile();
        createFile.createFile();

        Setting setting = new Setting();
        MessageDeal messagedeal = new MessageDeal();
        GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, g -> {
            //监听群消息
            String groupMsg = g.getMessage().contentToString();//获取消息
            long senderId = g.getSender().getId(); //获取发送者QQ
            Group group = g.getGroup(); //获取群对象
            Long botQq = setting.getQq();
            if (groupMsg.contains(String.valueOf(botQq))) {
                File file = new File(configFolderPath + "/wh.wh");
                if (!file.exists()) {
                    groupMsg = groupMsg.replace("@" + botQq, "");
                    groupMsg = groupMsg.replace("[图片]", "");
                    groupMsg = groupMsg.replace(" ", "");
                    try {
                        messagedeal.msgDel(senderId, group, groupMsg);
                    } catch (IOException | ScriptException | InterruptedException | NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                } else {
                    MessageChain chain = new MessageChainBuilder()
                            .append(new At(senderId))
                            .append(new PlainText("\n系统升级中，请稍后再试"))
                            .build();
                    group.sendMessage(chain);
                }
            }
        });

        GlobalEventChannel.INSTANCE.subscribeAlways(FriendMessageEvent.class, f -> {
            //监听好友消息
            String friendMsg = f.getMessage().serializeToMiraiCode();
            if (friendMsg.contains("新闻")) {
                friendMsg = friendMsg.replace("[mirai:image:", "");
                friendMsg = friendMsg.replace("]", "");
                friendMsg = friendMsg.replace("新闻", "");
                friendMsg = friendMsg.replace("\\n", "");
                String newsUrl = friendMsg;
                getLogger().info(newsUrl);
                MessageDeal messageDeal = new MessageDeal();
                String msg = messageDeal.friendMsgDel(newsUrl);
                f.getSender().sendMessage(msg);
            }
            if (friendMsg.contains("运势")) {
                Plugin plugin = new Plugin();
                String chain = plugin.getFortune();
                f.getSender().sendMessage(chain);
                getLogger().info(chain);
            }
        });

        GlobalEventChannel.INSTANCE.subscribeAlways(GroupTempMessageEvent.class, f -> {
            //监听临时消息
            String friendMsg = f.getMessage().contentToString();
            if (friendMsg.contains("运势")) {
                Plugin plugin = new Plugin();
                String chain = plugin.getFortune();
                f.getSender().sendMessage(chain);
                getLogger().info(chain);
            }
        });

        GlobalEventChannel.INSTANCE.subscribeAlways(NewFriendRequestEvent.class, a -> {
            //监听添加好友申请
            boolean accept1 = setting.getAgreeFriend();
            if (accept1) {
                getLogger().info("昵称:" + a.getFromNick() + " QQ:" + a.getFromId() + " 请求添加好友，已同意");
                a.accept();
            }
        });


        GlobalEventChannel.INSTANCE.subscribeAlways(MemberJoinRequestEvent.class, a -> {
            //监听入群消息
            boolean finalAutoJoinRequestEvent = setting.getAgreeIngroup();
            File file = new File(configFolderPath + "/wh.wh");
            if (!file.exists()) {
                if (finalAutoJoinRequestEvent) {
                    String yzMessage = a.getMessage();
                    long groupId = a.getGroupId();
                    if (groupId == 1132747000) {
                        if (yzMessage.contains("毓") || yzMessage.contains("秀") || yzMessage.contains("迎") || yzMessage.contains("曦") || yzMessage.contains("北")) {
                            a.accept();
                        } else if (yzMessage.contains("邀请")){
                            getLogger().info("被邀请");
                        } else {
                            a.reject(false, "请确认答案是否正确");
                        }
                        getLogger().info(yzMessage);
                    }

                }
            }
        });

        GlobalEventChannel.INSTANCE.subscribeAlways(BotInvitedJoinGroupRequestEvent.class, a -> {
            //监听被邀请入群消息
            boolean finalAutoInvited = setting.getAgreeGroup();
            if (finalAutoInvited) {
                a.accept();
            }
        });

        StartThread startThread = new StartThread();
        startThread.start();
    }


}
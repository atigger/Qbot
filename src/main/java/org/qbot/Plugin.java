package org.qbot;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.*;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.MessageSource;
import net.mamoe.mirai.message.data.PlainText;
import org.qbot.group.GroupManagement;
import org.qbot.group.GroupManagementSetting;
import org.qbot.group.GroupManagementUtil;
import org.qbot.msgdeal.AdminMessageDeal;
import org.qbot.msgdeal.MessageDeal;
import org.qbot.music.MusicMessageDeal;
import org.qbot.music.NeteaseCloudMusicTask;
import org.qbot.thread.StartThread;
import org.qbot.toolkit.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * @author 64995
 */
@SuppressWarnings({"AlibabaMethodTooLong"})
public final class Plugin extends JavaPlugin {
    public static final Plugin INSTANCE = new Plugin();

    public Plugin() {
        super(new JvmPluginDescriptionBuilder("org.qbot.plugin", PluginVersion.VERSION_NUM)
                .name("Qbot")
                .author("649953543@qq.com")
                .build());
    }

    @SuppressWarnings("AlibabaMethodTooLong")
    @Override
    public void onEnable() {
        getLogger().info("启动中。。。");
        CreateFile createFile = new CreateFile();
        createFile.createFile();
        Utils utils = new Utils();

        GroupManagementSetting groupManagementSetting = new GroupManagementSetting();
        GroupManagementUtil groupManagementUtil = new GroupManagementUtil();
        MessageDeal messagedeal = new MessageDeal();
        GroupManagement groupManagement = new GroupManagement();
        Long botQq = Setting.getQq();
        long superAdmin = Setting.getAdminQQ();
        Path dataFolderPath = Utils.getPluginsDataPath();
        File groupManagementDirectory = new File(dataFolderPath + "/groupManagement");

        GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, g -> {
            //监听群消息
            String groupMsg = g.getMessage().contentToString();//获取消息
            long senderId = g.getSender().getId(); //获取发送者QQ
            String senderName = g.getSenderName(); //获取发送者名字
            Group group = g.getGroup(); //获取群对象
            MessageChain groupMessageChain = g.getMessage(); //获取消息来源

            boolean botTag = false;
            if (groupMsg.contains(String.valueOf(botQq))) {
                botTag = true;
                groupMsg = groupMsg.replace("@" + botQq, "");
                groupMsg = groupMsg.replace("[图片]", "");
                groupMsg = groupMsg.replace(" ", "");
            }
            try {
                //检测是否开启群管理
                if (Setting.getGroupManagement()) {
                    File file = new File(groupManagementDirectory + "/" + group.getId() + ".txt");
                    if (!file.exists()) {
                        groupManagementSetting.creatEmptyTemplate(file);
                    }
                    //判断是否为超级管理员
                    if (senderId == superAdmin) {
                        if (groupManagement.msgDel(group, groupMsg, true)) {
                            return;
                        }
                        if (groupManagementUtil.msgDel(group, groupMsg)) {
                            return;
                        }
                    }
                    JSONObject jsonObject = utils.readFile(file);
                    JSONArray adminQqArray = jsonObject.getJSONArray("AdminQQ");
                    //判断是否设置了普通管理员
                    if (adminQqArray.size() > 0) {
                        //遍历管理员
                        for (int i = 0; i < adminQqArray.size(); i++) {
                            long adminQq = adminQqArray.getLongValue(i);
                            if (senderId == adminQq) {
                                if (groupManagement.msgDel(group, groupMsg, false)) {
                                    return;
                                }
                                if (groupManagementUtil.msgDel(group, groupMsg)) {
                                    return;
                                }
                            }
                        }
                    }
                    //获取禁言词汇列表
                    JSONArray muteKeyWordsArray = jsonObject.getJSONArray("MuteKeywords");
                    int muteTime = jsonObject.getIntValue("MuteTime");
                    if (muteTime == 0) {
                        muteTime = 10;
                    }
                    if (muteKeyWordsArray.size() > 0) {
                        //遍历禁言词汇
                        for (int i = 0; i < muteKeyWordsArray.size(); i++) {
                            String muteKeyWords = muteKeyWordsArray.getString(i);
                            if (groupMsg.contains(muteKeyWords)) {
                                groupManagementUtil.muteGroup(group, senderId, muteTime, groupMessageChain);
                                return;
                            }
                        }
                    }
                    //获取撤回词汇列表
                    JSONArray recallKeyWordsArray = jsonObject.getJSONArray("RecallKeywords");
                    if (recallKeyWordsArray.size() > 0) {
                        //遍历撤回词汇
                        for (int i = 0; i < recallKeyWordsArray.size(); i++) {
                            String recallKeyWords = recallKeyWordsArray.getString(i);
                            if (groupMsg.contains(recallKeyWords)) {
                                try {
                                    MessageSource.recall(groupMessageChain);
                                } catch (Exception e) {
                                    MessageChain chain = new MessageChainBuilder()
                                            .append(new PlainText("撤回失败,请检查是否有管理员权限"))
                                            .build();
                                    group.sendMessage(chain);
                                }
                                return;
                            }
                        }
                    }
                }
                if (botTag) {
                    messagedeal.msgDel(senderId, senderName, group, groupMsg, groupMessageChain);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        //监听好友消息
        GlobalEventChannel.INSTANCE.subscribeAlways(FriendMessageEvent.class, f -> {
            long senderQq = f.getSender().getId();
            String msg = f.getMessage().contentToString();
            MusicMessageDeal musicMessageDeal = new MusicMessageDeal();
            try {
                if (!musicMessageDeal.msgDel(f.getFriend(), msg)) {
                    if (senderQq == superAdmin) {
                        System.out.println("收到管理员消息:" + msg);
                        try {
                            AdminMessageDeal.msgDel(msg, f.getSender());
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            } catch (IOException | InterruptedException e) {
                System.out.printf(String.valueOf(e));
                throw new RuntimeException(e);
            }
        });

        GlobalEventChannel.INSTANCE.subscribeAlways(GroupTempMessageEvent.class, f -> {
            //监听临时消息
        });

        GlobalEventChannel.INSTANCE.subscribeAlways(NewFriendRequestEvent.class, a -> {
            //监听添加好友申请
            boolean accept1 = Setting.getAgreeFriend();
            if (accept1) {
                getLogger().info("昵称:" + a.getFromNick() + " QQ:" + a.getFromId() + " 请求添加好友，已同意");
                a.accept();
            }
        });

        //监听入群消息
        GlobalEventChannel.INSTANCE.subscribeAlways(MemberJoinRequestEvent.class, a -> {
            try {
                File file = new File(groupManagementDirectory + "/" + a.getGroupId() + ".txt");
                String msg = a.getMessage();
                JSONObject jsonObject = utils.readFile(file);
                boolean accept1 = jsonObject.getBooleanValue("AutoAgreeApplication");
                JSONArray acceptArray = jsonObject.getJSONArray("AgreeKeywords");
                JSONArray refuseArray = jsonObject.getJSONArray("RejectKeywords");
                if (accept1) {
                    if (acceptArray.size() > 0) {
                        for (int i = 0; i < acceptArray.size(); i++) {
                            String acceptKeyWords = acceptArray.getString(i);
                            if (msg.contains(acceptKeyWords)) {
                                a.accept();
                            }
                        }
                    }
                    if (refuseArray.size() > 0) {
                        for (int i = 0; i < refuseArray.size(); i++) {
                            String refuseKeyWords = refuseArray.getString(i);
                            if (msg.contains(refuseKeyWords)) {
                                a.reject();
                            }
                        }
                    }
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        GlobalEventChannel.INSTANCE.subscribeAlways(BotInvitedJoinGroupRequestEvent.class, a -> {
            //监听被邀请入群消息
            boolean finalAutoInvited = Setting.getAgreeGroup();
            if (finalAutoInvited) {
                a.accept();
            }
        });

        StartThread startThread = new StartThread();
        startThread.start();
    }

}
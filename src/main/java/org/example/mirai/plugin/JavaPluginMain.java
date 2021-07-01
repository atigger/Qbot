package org.example.mirai.plugin;

import com.alibaba.fastjson.JSONObject;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.*;
import net.mamoe.mirai.message.data.*;
import org.example.mirai.plugin.Dg.DgUtils;
import org.example.mirai.plugin.Thread.AutoGetFortuneThread;
import org.example.mirai.plugin.Thread.AutoThread;

import org.example.mirai.plugin.Toolkit.*;
import org.yaml.snakeyaml.Yaml;

import javax.script.ScriptException;
import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

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

public final class JavaPluginMain extends JavaPlugin {
    public static final JavaPluginMain INSTANCE = new JavaPluginMain();

    public JavaPluginMain() {
        super(new JvmPluginDescriptionBuilder("org.qbot.plugin", "0.1.0")
                .info("EG")
                .build());
    }

    @Override
    public void onEnable() {
        getLogger().info("启动中。。。");
        Path configFolderPath = getConfigFolderPath();
        Path dataFolderPath = getDataFolderPath();

        CreateFile createFile = new CreateFile();
        createFile.create_file();

        Setting setting = new Setting();
        MessageDeal messagedeal = new MessageDeal();
        Utils utils = new Utils();
        GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, g -> {
            //监听群消息
            String group_msg = g.getMessage().contentToString();//获取消息
            Long sender_id = g.getSender().getId(); //获取发送者QQ
            Group group = g.getGroup(); //获取群对象
            Long BOT_QQ = setting.getQQ();
            if (group_msg.contains(String.valueOf(BOT_QQ))) {
                group_msg = group_msg.replace("@" + BOT_QQ, "");
                group_msg = group_msg.replace("[图片]", "");
                group_msg = group_msg.replace(" ", "");
                try {
                    messagedeal.msg_del(sender_id, group, group_msg);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ScriptException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        });

        GlobalEventChannel.INSTANCE.subscribeAlways(FriendMessageEvent.class, f -> {
            //监听好友消息
            String friend_msg = f.getMessage().serializeToMiraiCode();
            if (friend_msg.indexOf("新闻") != -1) {
                friend_msg = friend_msg.replace("[mirai:image:", "");
                friend_msg = friend_msg.replace("]", "");
                friend_msg = friend_msg.replace("新闻", "");
                friend_msg = friend_msg.replace("\\n", "");
                String news_url = friend_msg;
                getLogger().info(news_url);
                MessageDeal messageDeal = new MessageDeal();
                String msg = messageDeal.friend_msg_del(news_url);
                f.getSender().sendMessage(msg);
            }
            if (friend_msg.indexOf("运势") != -1) {
                Plugin plugin = new Plugin();
                String chain = plugin.get_fortune();
                f.getSender().sendMessage(chain);
                getLogger().info(String.valueOf(chain));
            }
            if (friend_msg.contains("修改密码")) {
                DgUtils dgUtils = new DgUtils();
                friend_msg = friend_msg.replace("修改密码", "");
                friend_msg = friend_msg.replace(" ", "");
                friend_msg = friend_msg.replace("\n", "");
                String msg = dgUtils.get_pass(f.getSender().getId(), friend_msg);
                f.getSender().sendMessage(msg);
            }
            MessageChain chain;
            if (friend_msg.indexOf("补挂") != -1) {
                DgUtils dgUtils = new DgUtils();
                String rane_msg = dgUtils.get_bugua(f.getSender().getId());
                chain = new MessageChainBuilder()
                        .append(rane_msg)
                        .build();
                f.getSender().sendMessage(chain);
            }

            if (friend_msg.indexOf("状态查询") != -1) {
                DgUtils dgUtils = new DgUtils();
                String rane_msg = dgUtils.get_zt(f.getSender().getId());
                chain = new MessageChainBuilder()
                        .append(rane_msg)
                        .build();
                f.getSender().sendMessage(chain);
            }
        });

        GlobalEventChannel.INSTANCE.subscribeAlways(GroupTempMessageEvent.class, f -> {
            //监听临时消息
            String friend_msg = f.getMessage().contentToString();
            if (friend_msg.indexOf("运势") != -1) {
                Plugin plugin = new Plugin();
                String chain = plugin.get_fortune();
                f.getSender().sendMessage(chain);
                getLogger().info(String.valueOf(chain));
            }
            if (friend_msg.contains("修改密码")) {
                DgUtils dgUtils = new DgUtils();
                friend_msg = friend_msg.replace("修改密码", "");
                friend_msg = friend_msg.replace(" ", "");
                friend_msg = friend_msg.replace("\n", "");
                String msg = dgUtils.get_pass(f.getSender().getId(), friend_msg);
                f.getSender().sendMessage(msg);
            }

            MessageChain chain;
            if (friend_msg.indexOf("补挂") != -1) {
                DgUtils dgUtils = new DgUtils();
                String rane_msg = dgUtils.get_bugua(f.getSender().getId());
                chain = new MessageChainBuilder()
                        .append(rane_msg)
                        .build();
                f.getSender().sendMessage(chain);
            }

            if (friend_msg.indexOf("状态查询") != -1) {
                DgUtils dgUtils = new DgUtils();
                String rane_msg = dgUtils.get_zt(f.getSender().getId());
                chain = new MessageChainBuilder()
                        .append(rane_msg)
                        .build();
                f.getSender().sendMessage(chain);
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
            if (finalAutoJoinRequestEvent) {
                String yz_message = a.getMessage();
                Long group_id = a.getGroupId();
                if (group_id == 1132747000) {
                    if (yz_message.indexOf("毓") != -1 || yz_message.indexOf("秀") != -1 || yz_message.indexOf("迎") != -1 || yz_message.indexOf("曦") != -1) {
                        a.accept();
                    } else {
                        a.reject(false, "请确认答案是否正确");
                    }
                }
                getLogger().info(yz_message);
            }
        });

        GlobalEventChannel.INSTANCE.subscribeAlways(BotInvitedJoinGroupRequestEvent.class, a -> {
            //监听被邀请入群消息
            boolean finalAutoInvited = setting.getAgreeGroup();
            if (finalAutoInvited) {
                a.accept();
            }
        });

        //自动化线程
        boolean AutoFortune = setting.getAutoFortune();
        if (AutoFortune) {
            getLogger().info("开启自动获取运势线程成功！");
            AutoGetFortuneThread autoGetFortuneThread = new AutoGetFortuneThread();
            autoGetFortuneThread.start();
        }
        boolean AutoTips = setting.getAutoTips();
        boolean AutoNews = setting.getAutoNews();
        if (AutoTips || AutoNews) {
            getLogger().info("开启自动发送小提醒线程成功！");
            AutoThread autoThread = new AutoThread();
            autoThread.start();
        }

    }


}
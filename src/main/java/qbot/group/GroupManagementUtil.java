package org.qbot.group;


import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.MessageSource;
import net.mamoe.mirai.message.data.PlainText;
import org.qbot.toolkit.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * GroupManagementUtil class
 *
 * @author 649953543@qq.com
 * @date 2022/4/7
 */
public class GroupManagementUtil {
    GroupManagementSetting groupManagementSetting = new GroupManagementSetting();
    Utils utils = new Utils();
    Path dataFolderPath = utils.getPluginsDataPath();
    File groupManagementDirectory = new File(dataFolderPath + "/groupManagement");


    public boolean msgDel(Group group, String msg) throws IOException {


        String unMute = "解除禁言";
        if (msg.contains(unMute)) {
            if (!groupManagementSetting.Authority(group)) {
                return true;
            }
            msg = msg.replace(unMute, "");
            msg = msg.replace(" ", "");
            msg = msg.replace("@", "");
            Long muteQq = Long.valueOf(msg);
            group.get(muteQq).unmute();
            return true;
        }

        String mute = "禁言";
        if (msg.contains(mute)) {
            if (!groupManagementSetting.Authority(group)) {
                return true;
            }
            msg = msg.replace(mute, "");
            msg = msg.replace("@", "");
            Long muteQq = Long.valueOf(msg.substring(0, msg.indexOf(" ")));
            int time = Integer.parseInt(msg.substring(msg.indexOf(" ") + 1));
            time = time * 60;
            group.get(muteQq).mute(time);
            return true;
        }
        return false;
    }

    public void muteGroup(Group group, long qq, int time, MessageChain groupMessageChain) throws IOException {
        try {
            time = time * 60;
            group.get(qq).mute(time);
            MessageSource.recall(groupMessageChain);
        } catch (Exception e) {
            MessageChain chain = new MessageChainBuilder()
                    .append(new PlainText("禁言失败,请检查是否有管理员权限"))
                    .build();
            group.sendMessage(chain);
        }
    }
}

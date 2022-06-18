package org.qbot.group;


import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.MessageSource;
import net.mamoe.mirai.message.data.PlainText;
import java.util.Objects;

/**
 * GroupManagementUtil class
 *
 * @author 649953543@qq.com
 * @date 2022/4/7
 */
public class GroupManagementUtil {
    final GroupManagementSetting groupManagementSetting = new GroupManagementSetting();


    public boolean msgDel(Group group, String msg) {


        String unMute = "解除禁言";
        if (msg.contains(unMute)) {
            if (groupManagementSetting.authority(group)) {
                return true;
            }
            msg = msg.replace(unMute, "");
            msg = msg.replace(" ", "");
            msg = msg.replace("@", "");
            long muteQq = Long.parseLong(msg);
            Objects.requireNonNull(group.get(muteQq)).unmute();
            return true;
        }

        String mute = "禁言";
        if (msg.contains(mute)) {
            if (groupManagementSetting.authority(group)) {
                return true;
            }
            msg = msg.replace(mute, "");
            msg = msg.replace("@", "");
            long muteQq = Long.parseLong(msg.substring(0, msg.indexOf(" ")));
            int time = Integer.parseInt(msg.substring(msg.indexOf(" ") + 1));
            time = time * 60;
            Objects.requireNonNull(group.get(muteQq)).mute(time);
            return true;
        }
        return false;
    }

    public void muteGroup(Group group, long qq, int time, MessageChain groupMessageChain) {
        try {
            time = time * 60;
            Objects.requireNonNull(group.get(qq)).mute(time);
            MessageSource.recall(groupMessageChain);
        } catch (Exception e) {
            MessageChain chain = new MessageChainBuilder()
                    .append(new PlainText("禁言失败,请检查是否有管理员权限"))
                    .build();
            group.sendMessage(chain);
        }
    }
}

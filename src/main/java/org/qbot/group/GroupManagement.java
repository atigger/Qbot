package org.qbot.group;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;

import java.io.IOException;

/**
 * GroupManagement class
 *
 * @author 649953543@qq.com
 * @date 2021/11/26
 */
@SuppressWarnings({"ALL", "AlibabaMethodTooLong"})
public class GroupManagement {

    GroupManagementSetting GroupManagementSetting = new GroupManagementSetting();

    @SuppressWarnings("AlibabaMethodTooLong")
    public boolean msgDel(Group group, String msg, boolean superAdmin) throws IOException {
        MessageChain chain;
        String setGroupManagement = "设置本群管理";
        long groupId = group.getId();
        if (msg.contains(setGroupManagement)) {
            if (GroupManagementSetting.authority(group)) {
                return true;
            }
            if (!superAdmin) {
                return false;
            }
            long setQQ = newMsg(setGroupManagement, msg);
            if (GroupManagementSetting.setAdmin(groupId, setQQ) == 1) {
                chain = new MessageChainBuilder()
                        .append(new PlainText("设置成功"))
                        .build();
            } else if (GroupManagementSetting.setAdmin(groupId, setQQ) == -1) {
                chain = new MessageChainBuilder()
                        .append(new At(setQQ))
                        .append(new PlainText("已经是管理员了"))
                        .build();
            } else {
                chain = new MessageChainBuilder()
                        .append(new PlainText("设置失败"))
                        .build();
            }
            group.sendMessage(chain);
            return true;
        }

        String delGroupManagement = "取消本群管理";
        if (msg.contains(delGroupManagement)) {
            if (GroupManagementSetting.authority(group)) {
                return true;
            }
            if (!superAdmin) {
                return false;
            }
            long delQQ = newMsg(delGroupManagement, msg);
            if (GroupManagementSetting.delAdmin(groupId, delQQ) == 1) {
                chain = new MessageChainBuilder()
                        .append(new PlainText("取消成功"))
                        .build();

            } else if (GroupManagementSetting.delAdmin(groupId, delQQ) == -1) {
                chain = new MessageChainBuilder()
                        .append(new At(delQQ))
                        .append(new PlainText("已经不是管理员了"))
                        .build();
            } else {
                chain = new MessageChainBuilder()
                        .append(new PlainText("取消失败"))
                        .build();
            }
            group.sendMessage(chain);
            return true;
        }

        String setMuteKeywords = "设置禁言关键字";
        if (msg.contains(setMuteKeywords)) {
            if (GroupManagementSetting.authority(group)) {
                return true;
            }
            msg = msg.replace(setMuteKeywords, "");
            String setMuteKeywordsStr = msg.replace(" ", "");
            if (GroupManagementSetting.setMuteKeywords(groupId, setMuteKeywordsStr) == 1) {
                chain = new MessageChainBuilder()
                        .append(new PlainText("设置成功"))
                        .build();
            } else if (GroupManagementSetting.setMuteKeywords(groupId, setMuteKeywordsStr) == -1) {
                chain = new MessageChainBuilder()
                        .append(new PlainText("已经设置过了"))
                        .build();
            } else {
                chain = new MessageChainBuilder()
                        .append(new PlainText("设置失败"))
                        .build();

            }
            group.sendMessage(chain);
            return true;
        }

        String delMuteKeywords = "删除禁言关键字";
        if (msg.contains(delMuteKeywords)) {
            if (GroupManagementSetting.authority(group)) {
                return true;
            }
            msg = msg.replace(delMuteKeywords, "");
            String delMuteKeywordsStr = msg.replace(" ", "");
            if (GroupManagementSetting.delMuteKeywords(groupId, delMuteKeywordsStr) == 1) {
                chain = new MessageChainBuilder()
                        .append(new PlainText("删除成功"))
                        .build();
            } else if (GroupManagementSetting.delMuteKeywords(groupId, delMuteKeywordsStr) == -1) {
                chain = new MessageChainBuilder()
                        .append(new PlainText("已经不是禁言词汇了"))
                        .build();
            } else {
                chain = new MessageChainBuilder()
                        .append(new PlainText("取消失败"))
                        .build();
            }
            group.sendMessage(chain);
            return true;

        }

        String delAllMuteKeywords = "清空禁言关键字";
        if (msg.contains(delAllMuteKeywords)) {
            if (GroupManagementSetting.authority(group)) {
                return true;
            }
            if (GroupManagementSetting.delAllMuteKeywords(groupId) == 1) {
                chain = new MessageChainBuilder()
                        .append(new PlainText("清空成功"))
                        .build();
            } else {
                chain = new MessageChainBuilder()
                        .append(new PlainText("清空失败"))
                        .build();
            }
            group.sendMessage(chain);
            return true;
        }

        String setMuteTime = "设置禁言时间";
        if (msg.contains(setMuteTime)) {
            if (GroupManagementSetting.authority(group)) {
                return true;
            }
            msg = msg.replace(setMuteTime, "");
            int setMuteTimeStr = Integer.parseInt(msg.replace(" ", ""));
            if (GroupManagementSetting.setMuteTime(groupId, setMuteTimeStr) == 1) {
                chain = new MessageChainBuilder()
                        .append(new PlainText("设置成功"))
                        .build();
            } else if (GroupManagementSetting.setMuteTime(groupId, setMuteTimeStr) == -1) {
                chain = new MessageChainBuilder()
                        .append(new PlainText("禁言时间不能超过30天"))
                        .build();
            } else {
                chain = new MessageChainBuilder()
                        .append(new PlainText("设置失败"))
                        .build();
            }
            group.sendMessage(chain);
            return true;
        }

        String setRecallWords = "设置撤回关键字";
        if (msg.contains(setRecallWords)) {
            if (GroupManagementSetting.authority(group)) {
                return true;
            }
            msg = msg.replace(setRecallWords, "");
            String setRecallWordsStr = msg.replace(" ", "");
            if (GroupManagementSetting.setRecallWords(groupId, setRecallWordsStr) == 1) {
                chain = new MessageChainBuilder()
                        .append(new PlainText("设置成功"))
                        .build();

            } else if (GroupManagementSetting.setRecallWords(groupId, setRecallWordsStr) == -1) {
                chain = new MessageChainBuilder()
                        .append(new PlainText("已经设置过了"))
                        .build();

            } else {
                chain = new MessageChainBuilder()
                        .append(new PlainText("设置失败"))
                        .build();

            }
            group.sendMessage(chain);
            return true;
        }

        String delRecallWords = "删除撤回关键字";
        if (msg.contains(delRecallWords)) {
            if (GroupManagementSetting.authority(group)) {
                return true;
            }
            msg = msg.replace(delRecallWords, "");
            String delRecallWordsStr = msg.replace(" ", "");
            if (GroupManagementSetting.delRecallWords(groupId, delRecallWordsStr) == 1) {
                chain = new MessageChainBuilder()
                        .append(new PlainText("删除成功"))
                        .build();

            } else if (GroupManagementSetting.delRecallWords(groupId, delRecallWordsStr) == -1) {
                chain = new MessageChainBuilder()
                        .append(new PlainText("没有设置过"))
                        .build();
            } else {
                chain = new MessageChainBuilder()
                        .append(new PlainText("取消失败"))
                        .build();
            }
            group.sendMessage(chain);
            return true;
        }

        String delAllRecallWords = "清空撤回关键字";
        if (msg.contains(delAllRecallWords)) {
            if (GroupManagementSetting.authority(group)) {
                return true;
            }
            if (GroupManagementSetting.delAllRecallWords(groupId) == 1) {
                chain = new MessageChainBuilder()
                        .append(new PlainText("清空成功"))
                        .build();
            } else {
                chain = new MessageChainBuilder()
                        .append(new PlainText("清空失败"))
                        .build();
            }
            group.sendMessage(chain);
            return true;

        }

        String setAutoAgreeApplication = "设置自动同意群申请";
        if (msg.contains(setAutoAgreeApplication)) {
            if (GroupManagementSetting.authority(group)) {
                return true;
            }
            if (GroupManagementSetting.setAutoAgreeApplication(groupId) == 1) {
                chain = new MessageChainBuilder()
                        .append(new PlainText("设置成功"))
                        .build();
            } else {
                chain = new MessageChainBuilder()
                        .append(new PlainText("设置失败"))
                        .build();
            }
            group.sendMessage(chain);
            return true;

        }

        String delAutoAgreeApplication = "关闭自动同意群申请";
        if (msg.contains(delAutoAgreeApplication)) {
            if (GroupManagementSetting.authority(group)) {
                return true;
            }
            if (GroupManagementSetting.delAutoAgreeApplication(groupId) == 1) {
                chain = new MessageChainBuilder()
                        .append(new PlainText("关闭成功"))
                        .build();
            } else {
                chain = new MessageChainBuilder()
                        .append(new PlainText("关闭失败"))
                        .build();
            }
            group.sendMessage(chain);
            return true;
        }

        String setAutoAgreeKeyWords = "设置自动同意关键字";
        if (msg.contains(setAutoAgreeKeyWords)) {
            if (GroupManagementSetting.authority(group)) {
                return true;
            }
            msg = msg.replace(setAutoAgreeKeyWords, "");
            String setAutoAgreeKeyWordsStr = msg.replace(" ", "");
            if (GroupManagementSetting.setAutoAgreeKeyWords(groupId, setAutoAgreeKeyWordsStr) == 1) {
                chain = new MessageChainBuilder()
                        .append(new PlainText("设置成功"))
                        .build();

            } else if (GroupManagementSetting.setAutoAgreeKeyWords(groupId, setAutoAgreeKeyWordsStr) == -1) {
                chain = new MessageChainBuilder()
                        .append(new PlainText("已经设置过了"))
                        .build();

            } else {
                chain = new MessageChainBuilder()
                        .append(new PlainText("设置失败"))
                        .build();
            }
            group.sendMessage(chain);
            return true;
        }

        String delAutoAgreeKeyWords = "删除自动同意关键字";
        if (msg.contains(delAutoAgreeKeyWords)) {
            if (GroupManagementSetting.authority(group)) {
                return true;
            }
            msg = msg.replace(delAutoAgreeKeyWords, "");
            String delAutoAgreeKeyWordsStr = msg.replace(" ", "");
            if (GroupManagementSetting.delAutoAgreeKeyWords(groupId, delAutoAgreeKeyWordsStr) == 1) {
                chain = new MessageChainBuilder()
                        .append(new PlainText("删除成功"))
                        .build();
            } else if (GroupManagementSetting.delAutoAgreeKeyWords(groupId, delAutoAgreeKeyWordsStr) == -1) {
                chain = new MessageChainBuilder()
                        .append(new PlainText("没有设置过"))
                        .build();
            } else {
                chain = new MessageChainBuilder()
                        .append(new PlainText("删除失败"))
                        .build();
            }
            group.sendMessage(chain);
            return true;
        }

        String delAllAutoAgreeKeyWords = "清空自动同意关键字";
        if (msg.contains(delAllAutoAgreeKeyWords)) {
            if (GroupManagementSetting.authority(group)) {
                return true;
            }
            if (GroupManagementSetting.delAllAutoAgreeKeyWords(groupId) == 1) {
                chain = new MessageChainBuilder()
                        .append(new PlainText("清空成功"))
                        .build();
            } else {
                chain = new MessageChainBuilder()
                        .append(new PlainText("清空失败"))
                        .build();
            }
            group.sendMessage(chain);
            return true;
        }

        String managementMenu = "管理菜单";
        if (msg.contains(managementMenu)) {
            chain = new MessageChainBuilder()
                    .append(new PlainText("管理菜单"))
                    .build();
            group.sendMessage(chain);
            return true;
        }
        return false;
    }

    public long newMsg(String replaceStr, String msg) {
        msg = msg.replace(replaceStr, "");
        msg = msg.replace("@", "");
        msg = msg.replace(" ", "");
        return Long.parseLong(msg);
    }

}

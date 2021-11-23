package org.example.mirai.plugin.command;

import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.java.JSimpleCommand;
import org.example.mirai.plugin.JavaPluginMain;
import org.example.mirai.plugin.toolkit.Utils;

import java.io.File;
import java.nio.file.Path;

/**
 * CloseMaintainCommand class
 *
 * @author 649953543@qq.com
 * @date 2021/09/22
 */

public final class CloseMaintainCommand extends JSimpleCommand {

    public static final CloseMaintainCommand INSTANCE = new CloseMaintainCommand();

    private CloseMaintainCommand() {
        super(JavaPluginMain.INSTANCE, "closewh", new String[]{"s"}, JavaPluginMain.INSTANCE.getParentPermission());
        this.setDescription("关闭维护模式");
    }

    @Handler
    public void onCommand(CommandSender sender) {
        Utils utils = new Utils();
        Path configFolderPath = utils.getPluginsPath();
        File file = new File(configFolderPath + "/wh.wh");
        if (!file.exists()) {
            System.out.println("未开启维护模式！");
        } else {
            if (file.delete()) {
                System.out.println("维护模式已关闭！");
            }
        }
    }
}
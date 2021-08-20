package org.example.mirai.plugin.Command;

import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.java.JSimpleCommand;
import org.example.mirai.plugin.JavaPluginMain;
import org.example.mirai.plugin.Toolkit.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public final class CloseMaintainCommand extends JSimpleCommand {

    public static final CloseMaintainCommand INSTANCE = new CloseMaintainCommand();

    private CloseMaintainCommand() {
        super(JavaPluginMain.INSTANCE, "closewh", new String[]{"s"}, JavaPluginMain.INSTANCE.getParentPermission());
        this.setDescription("关闭维护模式");
    }

    @Handler
    public void onCommand(CommandSender sender) {
        Utils utils = new Utils();
        Path configFolderPath = utils.get_plugins_path();
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
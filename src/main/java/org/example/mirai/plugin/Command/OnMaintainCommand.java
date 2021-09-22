package org.example.mirai.plugin.command;

import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.java.JSimpleCommand;
import org.example.mirai.plugin.JavaPluginMain;
import org.example.mirai.plugin.toolkit.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * OnMaintainCommand class
 *
 * @author 649953543@qq.com
 * @date 2021/09/22
 */

public final class OnMaintainCommand extends JSimpleCommand {

    public static final OnMaintainCommand INSTANCE = new OnMaintainCommand();

    private OnMaintainCommand() {
        super(JavaPluginMain.INSTANCE, "onwh", new String[]{"s"}, JavaPluginMain.INSTANCE.getParentPermission());
        this.setDescription("开启维护模式");
    }
    @Handler
    public void onCommand(CommandSender sender) throws IOException {
        Utils utils = new Utils();
        Path configFolderPath = utils.getPluginsPath();
        File file = new File(configFolderPath + "/wh.wh");
        if(!file.exists()){
            if(file.createNewFile()){
                System.out.println("开启维护模式成功！");
            }else {
                System.out.println("开启维护模式失败！");
            }
        }else {
            System.out.println("维护模式已开启，无需重新开启！");
        }

    }
}
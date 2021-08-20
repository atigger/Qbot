package org.example.mirai.plugin.Command;

import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.java.JSimpleCommand;
import org.example.mirai.plugin.JavaPluginMain;
import org.example.mirai.plugin.Toolkit.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;


public final class OnMaintainCommand extends JSimpleCommand {

    public static final OnMaintainCommand INSTANCE = new OnMaintainCommand();

    private OnMaintainCommand() {
        super(JavaPluginMain.INSTANCE, "onwh", new String[]{"s"}, JavaPluginMain.INSTANCE.getParentPermission());
        this.setDescription("开启维护模式");
    }
    @Handler
    public void onCommand(CommandSender sender) throws IOException {
        Utils utils = new Utils();
        Path configFolderPath = utils.get_plugins_path();
        File file = new File(configFolderPath + "/wh.wh");
        if(!file.exists()){
            file.createNewFile();
            System.out.println("开启维护模式成功！");
        }else {
            System.out.println("维护模式已开启，无需重新开启！");
        }

    }
}
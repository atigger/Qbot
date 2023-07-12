package org.qbot.toolkit;

import com.alibaba.fastjson2.JSONArray;
import org.qbot.api.API;

import java.io.*;
import java.nio.file.Path;

/**
 * CreateFile class
 *
 * @author 649953543@qq.com
 * @date 2021/09/22
 */

public class CreateFile {
    Utils utils = new Utils();

    public void createFile() {
        Path dataFolderPath = Utils.getPluginsDataPath();
        Path configFolderPath = Utils.getPluginsPath();

        File dataFolderDirectory = new File(String.valueOf(dataFolderPath));
        dataFolderDirectory.mkdir();

        File configFolderDirectory = new File(String.valueOf(configFolderPath));
        configFolderDirectory.mkdir();

        File cacheDirectory = new File(dataFolderPath + "/cache");
        cacheDirectory.mkdir();

        File imageDirectory = new File(dataFolderPath + "/cache/image");
        imageDirectory.mkdir();

        File heroHeadDirectory = new File(dataFolderPath + "/cache/hero");
        heroHeadDirectory.mkdir();

        File newsDirectory = new File(dataFolderPath + "/cache/news");
        newsDirectory.mkdir();

        File videoDirectory = new File(dataFolderPath + "/cache/video");
        videoDirectory.mkdir();

        File voiceDirectory = new File(dataFolderPath + "/cache/voice");
        voiceDirectory.mkdir();

        File tqDirectory = new File(dataFolderPath + "/cache/tq");
        tqDirectory.mkdir();

        File qqDirectory = new File(dataFolderPath + "/cache/qq");
        qqDirectory.mkdir();

        File musicDirectory = new File(dataFolderPath + "/cache/music");
        musicDirectory.mkdir();

        File mofishDirectory = new File(dataFolderPath + "/cache/mofish");
        mofishDirectory.mkdir();

        File groupManagementDirectory = new File(dataFolderPath + "/groupManagement");
        groupManagementDirectory.mkdir();

        File file = new File(configFolderPath + "/setting.yml");

        if (!file.exists()) {
            System.out.println("检测到配置文件不存在，生成中");
            SetSetting.setFile(Setting.VERSION_NUM, 0, "", "", "", 1, 119, false, false, false, "", "", false, false,
                    false,
                    new JSONArray(), false, 0, "", "");
        } else {
            System.out.println("配置文件存在");
            Setting setting1 = new Setting();
            setting1.getVersion();
        }
        File newsFile = new File(dataFolderPath + "/cache/news.cache");
        File weekcacheFile = new File(dataFolderPath + "/cache/week.cache");
        try {
            if (!newsFile.exists()) {
                newsFile.createNewFile();
            }
            if (!weekcacheFile.exists()) {
                newsFile.createNewFile();
                BufferedWriter out = new BufferedWriter(new FileWriter(weekcacheFile));
                out.write("0");
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        File cqFile = new File(dataFolderPath + "/诸葛神签.txt");
        if (!cqFile.exists()) {
            System.out.println("正在下载资源文件:" + cqFile.getName());
            if (utils.downloadFile(API.ZHU_GE_URL, cqFile.getAbsolutePath())) {
                System.out.println("下载完成");
            } else {
                System.out.println("下载失败");
            }
        }
        //提示文件
        File tipsFile = new File(dataFolderPath + "/image_tips.png");
        if (!tipsFile.exists()) {
            System.out.println("正在下载资源文件:" + tipsFile.getName());
            if (utils.downloadFile(API.IMAGE_TIPS_URL, tipsFile.getAbsolutePath())) {
                System.out.println("下载完成");
            } else {
                System.out.println("下载失败");
            }
        }
        //二维码
        File ewmFile = new File(dataFolderPath + "/image_qr_code.png");
        if (!ewmFile.exists()) {
            System.out.println("正在下载资源文件:" + ewmFile.getName());
            if (utils.downloadFile(API.IMAGE_URL, ewmFile.getAbsolutePath())) {
                System.out.println("下载完成");
            } else {
                System.out.println("下载失败");
            }
        }
    }

}

package org.example.mirai.plugin.toolkit;

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
        Path dataFolderPath = utils.getPluginsDataPath();
        Path configFolderPath = utils.getPluginsPath();

        File heroHeadDirectory = new File(dataFolderPath + "/cache/hero");
        heroHeadDirectory.mkdir();

        File NewsDirectory = new File(dataFolderPath + "/cache/news");
        NewsDirectory.mkdir();

        File voiceDirectory = new File(dataFolderPath + "/cache/voice");
        voiceDirectory.mkdir();

        File tqDirectory = new File(dataFolderPath + "/cache/tq");
        tqDirectory.mkdir();

        File qqDirectory = new File(dataFolderPath + "/cache/qq");
        qqDirectory.mkdir();

        File imageDirectory = new File(dataFolderPath + "/image");
        imageDirectory.mkdir();

        File raneDirectory = new File(dataFolderPath + "/rane");
        raneDirectory.mkdir();

        File cacheDirectory = new File(dataFolderPath + "/cache");
        cacheDirectory.mkdir();

        File file = new File(configFolderPath + "/setting.yml");
        if (!file.exists()) {
            System.out.println("检测到配置文件不存在，生成中");
            SetSetting setting = new SetSetting();
            setting.setFile();
        } else {
            System.out.println("配置文件存在");
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
        File cqFile = new File(dataFolderPath + "/cq.txt");
        if (!cqFile.exists()) {
            try {
                createDataFile(cqFile,"cq.txt");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        File codeFile = new File(dataFolderPath + "/rane/codes.js");
        if (!codeFile.exists()) {
            try {
                createDataFile(codeFile,"codes.js");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void createDataFile(File cq, String fileName) throws IOException {
        int bytesum = 0;
        int byteread;
        InputStream in = this.getClass().getResourceAsStream("../../../../../" + fileName);
        FileOutputStream fs = new FileOutputStream(cq);
        byte[] buffer = new byte[1444];
        if (in != null) {
            while ((byteread = in.read(buffer)) != -1) {
                bytesum += byteread;
                fs.write(buffer, 0, byteread);
            }
            in.close();
        }
        fs.close();
    }

}

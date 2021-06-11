package org.example.mirai.plugin.Toolkit;

import com.alibaba.fastjson.JSONObject;
import com.baidu.aip.speech.AipSpeech;
import com.baidu.aip.speech.TtsResponse;
import com.baidu.aip.util.Util;
import net.mamoe.mirai.console.plugin.PluginManager;
import net.mamoe.mirai.console.plugin.jvm.JvmPlugin;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.example.mirai.plugin.JavaPluginMain;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Utils {
    //okhttp get请求
    public String okHttpClient_get(String url) {
        OkHttpClient httpClient = new OkHttpClient();
        Request getRequest = new Request.Builder()
                .url(url)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36")
                .get()
                .build();
        Call call = httpClient.newCall(getRequest);
        try {
            Response response = call.execute();
            String html = response.body().string();
            return html;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    //获取插件配置文件目录
    public Path get_plugins_path() {
        JvmPlugin jvmPlugin = new JavaPluginMain();
        Path news_file_path = PluginManager.INSTANCE.getPluginsConfigPath().resolve(jvmPlugin.getDescription().getName());
        return news_file_path;
    }

    //获取插件数据文件目录
    public Path get_plugins_data_path() {
        JvmPlugin jvmPlugin = new JavaPluginMain();
        Path news_file_path = PluginManager.INSTANCE.getPluginsDataPath().resolve(jvmPlugin.getDescription().getName());
        return news_file_path;
    }

    //获取星期
    public String getWeek() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("E", Locale.ENGLISH);
        String week = formatter.format(date);
        return week;
    }

    //获取时间
    public String get_time() {
        SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
        sdf.applyPattern("MMddHHmmss");// a为am/pm的标记
        Date date = new Date();// 获取当前时间
        String now_data = sdf.format(date);
        return now_data;

    }

    public String format_text(String text) {
        text = text.replace("<table class=\"tb\"> \n", "");
        text = text.replace(" <tbody>\n", "");
        text = text.replace("  <tr> \n", "");
        text = text.replace("   <td class=\"tb_td1\">", "");
        text = text.replace("</td> \n   <td>", ":");
        text = text.replace("</td> ", "");
        text = text.replace("  </tr> \n", "");
        text = text.replace("\n   <td colspan=\"3\">", "");
        text = text.replace("精评", "");
        text = text.replace("\n   <td colspan=\"3\" style=\"line-height: 25px;\">", ":");
        text = text.replace("今日重要天象", "今日重要天象:");
        text = text.replace(" </tbody>\n", "");
        text = text.replace("</table>", "");
        text = text.replace("：", ":");
        return text;
    }

    public String get_horoscope_text(String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            Elements html = doc.select("table");
            String html1 = String.valueOf(html);
            String text = format_text(html1);
            return text;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    //获取范围随机数
    public int get_random_num(int min, int max) {
        Random random = new Random();
        int s = random.nextInt(max) % (max - min + 1) + min;
        return s;
    }

    //获取文件行数
    public int get_file_len(String path) {
        try {
            File file = new File(path);
            if (file.exists()) {
                FileReader fr = new FileReader(file);
                LineNumberReader lnr = new LineNumberReader(fr);
                int linenumber = 0;
                while (lnr.readLine() != null) {
                    linenumber++;
                }
                lnr.close();
                return linenumber;
            }
            return 1;
        } catch (IOException e) {
            e.printStackTrace();
            return 1;
        }
    }

    //获取音乐信息
    public JSONObject get_music_info(String music_name) {
        String url = "http://music.163.com/api/search/get/web?s=" + music_name + "&type=1&limit=1";
        String html = okHttpClient_get(url);
        JSONObject json = JSONObject.parseObject(html);
        Object music_data_json = json.getJSONObject("result").getJSONArray("songs").get(0);
        JSONObject music_info_json = (JSONObject) music_data_json;
        JSONObject artists = (JSONObject) music_info_json.getJSONArray("artists").get(0);

        String song_name = music_info_json.getString("name");
        String songer_name = artists.getString("name");
        String cover_url = music_info_json.getJSONObject("album").getJSONObject("artist").getString("img1v1Url");
        int song_id = music_info_json.getInteger("id");

        JSONObject music_info = new JSONObject();
        music_info.put("song_name", song_name);
        music_info.put("songer_name", songer_name);
        music_info.put("cover_url", cover_url);
        music_info.put("song_id", song_id);
        return music_info;
    }

    //获取二维码路径
    public String get_code(String url) {
        QRCodeUtil qrCodeUtil = new QRCodeUtil();
        String code_path = String.valueOf(get_plugins_data_path()) + "/code.png";
        // 生成二维码
        qrCodeUtil.encodeQRCode(url, code_path);
        return code_path;
    }


    //生成语音文件并返回目录
    public String make_voice(String txt) {
        Setting setting = new Setting();

        String APP_ID = setting.getAPP_ID();
        String API_KEY = setting.getAPI_KEY();
        String SECRET_KEY = setting.getSECRET_KEY();

        if (APP_ID.equals("")) {
            return "1";
        }
        JvmPlugin jvmPlugin = new JavaPluginMain();
        Path news_file_path = PluginManager.INSTANCE.getPluginsDataPath().resolve(jvmPlugin.getDescription().getName());
        int random_num = get_random_num(0, 10000);
        String filename = get_time() + random_num + ".mp3";
        // 初始化一个AipSpeech
        AipSpeech client = new AipSpeech(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);

        // 调用接口
        TtsResponse res = client.synthesis(txt, "zh", 1, null);
        byte[] data = res.getData();
        org.json.JSONObject res1 = res.getResult();
        if (data != null) {
            try {
                String filepath = String.valueOf(news_file_path) + "/cache/voice/" + filename;
                Util.writeBytesToFileSystem(data, filepath);
                return filepath;
            } catch (IOException e) {
                e.printStackTrace();
                return "0";
            }
        }
        if (res1 != null) {
            System.out.println(res1.toString(2));
            return "0";
        }
        return "0";
    }

    //获取当前小时分钟
    public String get_now_time() {
        SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
        sdf.applyPattern("HH:mm");// a为am/pm的标记
        Date date = new Date();// 获取当前时间
        String now_time = sdf.format(date); // 输出已经格式化的现在时间（24小时制）
        return now_time;
    }


    //获取时间
    public String get_time1() {
        SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
        sdf.applyPattern("MMdd");// a为am/pm的标记
        Date date = new Date();// 获取当前时间
        String now_data = sdf.format(date);
        return now_data;

    }

    public void rewrite(int week) {
        try {
            File weekcacheFile = new File(get_plugins_data_path() + "/cache/week.cache");
            BufferedWriter out = new BufferedWriter(new FileWriter(weekcacheFile));
            out.write(String.valueOf(week));
            out.close();
        } catch (IOException e) {
        }
    }


    public void rewrite_data(File file) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            out.write(get_time1());
            out.close();
        } catch (IOException e) {
        }
    }

    public boolean read_data(File file) {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));
            String time = in.readLine();
            if (time.equals(get_time1())) {
                return false;
            } else {
                rewrite_data(file);
                return true;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


}


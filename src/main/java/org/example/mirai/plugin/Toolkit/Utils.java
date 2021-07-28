package org.example.mirai.plugin.Toolkit;

import com.alibaba.fastjson.JSONObject;
import com.baidu.aip.speech.AipSpeech;
import com.baidu.aip.speech.TtsResponse;
import com.baidu.aip.util.Util;
import net.mamoe.mirai.console.plugin.PluginManager;
import net.mamoe.mirai.console.plugin.jvm.JvmPlugin;
import okhttp3.*;
import org.example.mirai.plugin.JavaPluginMain;
import org.jetbrains.annotations.NotNull;
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

    //获取日期
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
        Path news_file_path = get_plugins_data_path();
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


    public void rewrite_data(File file, String qian) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("date", get_time1());
            jsonObject.put("qian", qian);
            out.write(String.valueOf(jsonObject));
            out.close();
        } catch (IOException e) {
        }
    }

    public String read_data(File file) {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));
            String data = in.readLine();
            JSONObject jsonObject = JSONObject.parseObject(data);
            String time = jsonObject.getString("date");
            String qian = jsonObject.getString("qian");
            if (time.equals(get_time1())) {
                return qian;
            } else {
                return "1";
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "0";
    }

    public void gift() throws Exception {
        String url = "https://www.jishuqq.com";
        Document doc = Jsoup.connect(url).timeout(10000).get();
        Elements url_list = doc.getElementsByClass("home-last half-50");
        int size = url_list.size();
        if (size > 10) {
            size = 10;
        }
        String message = "";
        for (int i = 0; i < size; i++) {
            String url_1 = url + url_list.get(i).getElementsByTag("a").attr("href");
            String title = url_list.get(i).getElementsByTag("a").attr("title");
            String a = url_list.get(i).getElementsByTag("span").text();
            message = message + (i + 1) + "." + title + " " + a + "\n\n";
            System.out.println(title + " " + url_1 + " " + a);
        }
        String[] strArr = message.split("\n");
        int image_height = 700; // 每张图片的高度
        int line_height = 20; // 每行或者每个文字的高度
        int every_line = image_height / line_height; // 每张图片有多少行文字
        createImage(strArr, new Font("黑体", Font.PLAIN, 20), 800, image_height, every_line, line_height);
    }

    public String get_gift(int i) throws IOException {
        i = i - 1;
        String url = "https://www.jishuqq.com";
        Document doc = Jsoup.connect(url).timeout(10000).get();
        Elements url_list = doc.getElementsByClass("home-last half-50");
        String url_1 = url + url_list.get(i).getElementsByTag("a").attr("href");
        QRCodeUtil qrCodeUtil = new QRCodeUtil();
        String code_path = String.valueOf(get_plugins_data_path()) + "/code1.png";
        // 生成二维码
        qrCodeUtil.encodeQRCode(url_1, code_path);
        return code_path;
    }

    public void createImage(@NotNull String[] strArr, Font font,
                            int width, int image_height, int every_line, int line_height) throws Exception {
        FontMetrics fm = sun.font.FontDesignMetrics.getMetrics(font);
        int stringWidth = fm.charWidth('字');// 标点符号也算一个字
        int line_string_num = width % stringWidth == 0 ? (width / stringWidth) : (width / stringWidth) + 1;
        line_string_num = line_string_num + 7;
        System.out.println("每行=" + line_string_num);

        java.util.List<String> listStr = new ArrayList<String>();
        List<String> newList = new ArrayList<String>();
        for (int h = 0; h < strArr.length; h++) {
            listStr.add(strArr[h]);
        }
        for (int j = 0; j < listStr.size(); j++) {
            if (listStr.get(j).length() > line_string_num) {
                newList.add(listStr.get(j).substring(0, line_string_num));
                listStr.add(j + 1, listStr.get(j).substring(line_string_num));
                listStr.set(j, listStr.get(j).substring(0, line_string_num));
            } else {
                newList.add(listStr.get(j));
            }
        }

        int a = newList.size();
        int b = every_line;
        int imgNum = a % b == 0 ? (a / b) : (a / b) + 1;
        image_height = a * 22;
        for (int m = 0; m < imgNum; m++) {
            String filePath = get_plugins_data_path() + "/fl.jpg";
            File outFile = new File(filePath);
// 创建图片
            BufferedImage image = new BufferedImage(width, image_height,
                    BufferedImage.TYPE_INT_BGR);
            Graphics g = image.getGraphics();
            g.setClip(0, 0, width, image_height);
            g.setColor(Color.white); // 背景色白色
            g.fillRect(0, 0, width, image_height);
            g.setColor(Color.black);// 字体颜色黑色
            g.setFont(font);// 设置画笔字体
// 每张多少行，当到最后一张时判断是否填充满
            for (int i = 0; i < every_line; i++) {
                int index = i + m * every_line;
                if (newList.size() - 1 >= index) {
                    System.out.println("每行实际=" + newList.get(index).length());
                    g.drawString(newList.get(index), 0, line_height * (i + 1));
                }
            }
            g.dispose();
            ImageIO.write(image, "jpg", outFile);// 输出png图片
        }
    }

    public boolean getnews() {
        String url = "http://api.03c3.cn/zb";
        OkHttpClient okHttpClient = new OkHttpClient();
        Request getRequest = new Request.Builder()
                .url(url)
                .get()
                .build();
        Call call = okHttpClient.newCall(getRequest);
        try {
            Response response = call.execute();
            ResponseBody body = response.body();
            //获取流
            InputStream in = body.byteStream();
            //转化为bitmap
            FileOutputStream fo = new FileOutputStream(new File(get_plugins_data_path() + "/cache/" + get_time1() + ".jpg"));
            byte[] buf = new byte[1024];
            int length = 0;
            while ((length = in.read(buf, 0, buf.length)) != -1) {
                fo.write(buf, 0, length);
            }
            in.close();
            fo.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}


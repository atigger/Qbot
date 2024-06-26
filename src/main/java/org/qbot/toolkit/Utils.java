package org.qbot.toolkit;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baidu.aip.speech.AipSpeech;
import com.baidu.aip.speech.TtsResponse;
import com.baidu.aip.util.Util;
import net.mamoe.mirai.console.plugin.PluginManager;
import net.mamoe.mirai.console.plugin.jvm.JvmPlugin;
import okhttp3.*;
import org.qbot.Plugin;
import org.qbot.api.API;

import java.io.*;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Utils class
 *
 * @author 649953543@qq.com
 * @date 2021/09/22
 */

public class Utils {

    /**
     * okhttp get请求
     */
    public String okHttpClientGet(String url) {
        OkHttpClient httpClient = new OkHttpClient();
        Request getRequest = new Request.Builder()
                .url(url)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36")
                .get()
                .build();
        Call call = httpClient.newCall(getRequest);
        try {
            Response response = call.execute();
            return Objects.requireNonNull(response.body()).string();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * okhttp get请求,X-Real-Ip
     */
    public String okHttpClientGet(String url, String ip) {
        OkHttpClient httpClient = new OkHttpClient();
        Request getRequest = new Request.Builder()
                .url(url)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36")
                .addHeader("X-Real-Ip", ip)
                .get()
                .build();
        Call call = httpClient.newCall(getRequest);
        try {
            Response response = call.execute();
            return Objects.requireNonNull(response.body()).string();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * okhttp get请求
     */
    public String okHttpClientGetMusic(String url, String cookie) {
        OkHttpClient httpClient = new OkHttpClient();
        Request getRequest = new Request.Builder()
                .url(url)
                .addHeader("Cookie", cookie)
                .get()
                .build();
        Call call = httpClient.newCall(getRequest);
        try {
            Response response = call.execute();
            return Objects.requireNonNull(response.body()).string();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 获取插件配置文件目录
     */
    public static Path getPluginsPath() {
        JvmPlugin jvmPlugin = new Plugin();
        return PluginManager.INSTANCE.getPluginsConfigPath().resolve(jvmPlugin.getDescription().getId());
    }

    /**
     * 获取插件数据文件目录
     */
    public static Path getPluginsDataPath() {
        JvmPlugin jvmPlugin = new Plugin();
        return PluginManager.INSTANCE.getPluginsDataPath().resolve(jvmPlugin.getDescription().getId());
    }

    /**
     * 获取星期
     */
    public String getWeek() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("E", Locale.ENGLISH);
        return formatter.format(date);
    }

    /**
     * 获取日期
     */
    public static String getTime() {
        // 格式化时间
        SimpleDateFormat sdf = new SimpleDateFormat();
        // a为am/pm的标记
        sdf.applyPattern("MMddHHmmss");
        // 获取当前时间
        Date date = new Date();
        return sdf.format(date);

    }

    public String formatText(String text) {
        text = text.replace("<table class=\"tb\">\n" +
                " <tbody>\n" +
                "  <tr>\n" +
                "   <td class=\"tb_td1\">", "");
        text = text.replace("</td>\n" +
                "   <td>", ":");
        text = text.replace("</td>\n" +
                "   <td class=\"tb_td1\">", "\n");
        text = text.replace("</td>\n" +
                "  </tr>\n" +
                "  <tr>\n" +
                "   <td class=\"tb_td1\">", "\n");
        text = text.replace("</td>\n" +
                "   <td colspan=\"3\">", ":");
        text = text.replace("精评:", "");
        text = text.replace("</td>\n" +
                "   <td colspan=\"3\" style=\"line-height: 25px;\">", ":");
        text = text.replace("</td>\n" +
                "  </tr>\n" +
                " </tbody>\n" +
                "</table>", "");
        text = text.replace("。", "");
        return text;
    }

    /**
     * 获取星座运势
     *
     * @param msg
     * @return
     */
    public String getHoroscope(String msg) {
        String url = API.HOROSCOPE_URL + "?type=" + msg + "&time=today";
        try {
            JSONObject jsonObject = JSONObject.parseObject(okHttpClientGet(url)).getJSONObject("data");
            String title = jsonObject.getString("title") + jsonObject.getString("type") + "\n";
            String todo = "宜:" + jsonObject.getJSONObject("todo").getString("yi") + "\n忌:" + jsonObject.getJSONObject("todo").getString("ji") + "\n";
            String fortune = "总运势:" + jsonObject.getJSONObject("index").getString("all") + "\n" + jsonObject.getJSONObject("fortunetext").getString("all") + "\n" +
                    "爱情运势:" + jsonObject.getJSONObject("index").getString("love") + "\n" + jsonObject.getJSONObject("fortunetext").getString("love") + "\n" +
                    "财富运势:" + jsonObject.getJSONObject("index").getString("money") + "\n" + jsonObject.getJSONObject("fortunetext").getString("money") + "\n" +
                    "工作运势:" + jsonObject.getJSONObject("index").getString("work") + "\n" + jsonObject.getJSONObject("fortunetext").getString("work") + "\n" +
                    "健康运势:" + jsonObject.getJSONObject("index").getString("health") + "\n" + jsonObject.getJSONObject("fortunetext").getString("health") + "\n";
            String luckyNumber = "幸运数字:" + jsonObject.getString("luckynumber") + "\n";
            String luckyColor = "幸运颜色:" + jsonObject.getString("luckycolor") + "\n";
            String shortComment = "短评:" + jsonObject.getString("shortcomment");
            return title + todo + luckyNumber + luckyColor + todo + fortune + shortComment;
        } catch (Exception e) {
            return "\n获取失败";
        }
    }


    /**
     * 获取热点
     *
     * @param msg
     * @return
     */
    public String getHotList(String msg) {
        String url = API.HOT_LIST_URL + "?type=" + msg;
        try {
            JSONArray jsonArray = JSONObject.parseObject(okHttpClientGet(url)).getJSONArray("data");
            StringBuilder data = new StringBuilder();
            for (int i = 0; i < 10; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int num = i + 1;
                data.append(num).append(" ").append(jsonObject.getString("title")).append("\n");
            }
            return data.toString();
        } catch (Exception e) {
            return "\n获取失败";
        }
    }

    /**
     * 获取范围随机数
     */
    public static int getRandomNum(int min, int max) {
        Random random = new Random();
        return random.nextInt(max) % (max - min + 1) + min;
    }

    /**
     * 获取文件行数
     */
    public int getFileLen(String path) {
        try {
            File file = new File(path);
            if (file.exists()) {
                FileReader fr = new FileReader(file);
                LineNumberReader lnr = new LineNumberReader(fr);
                int linenumber = 0;
                while (lnr.readLine() != null) {
                    linenumber++;
                }
                fr.close();
                lnr.close();
                return linenumber;
            }
            return 1;
        } catch (IOException e) {
            e.printStackTrace();
            return 1;
        }
    }

    /**
     * 生成随机ip
     */
    public String generateRandomChinaIP() {
        Random random = new Random();
        // China IP address ranges from 58.16.0.0 to 58.25.255.255
        int firstPart = 58;
        int secondPart = 16 + random.nextInt(10); // 16 to 25
        int thirdPart = random.nextInt(256); // 0 to 255
        int fourthPart = random.nextInt(256); // 0 to 255
        return firstPart + "." + secondPart + "." + thirdPart + "." + fourthPart;
    }

    /**
     * 获取音乐信息
     */
    public JSONObject getMusicInfo(String musicName) {
        String url = "https://music.163.com/api/search/get/web?s=" + musicName + "&type=1&limit=1";
        String html = okHttpClientGet(url, generateRandomChinaIP());
        JSONObject json = JSONObject.parseObject(html);
        JSONObject musicInfoJson = json.getJSONObject("result").getJSONArray("songs").getJSONObject(0);
        JSONObject artists = musicInfoJson.getJSONArray("artists").getJSONObject(0);
        String songName = musicInfoJson.getString("name");
        String songerName = artists.getString("name");
        String coverUrl = musicInfoJson.getJSONObject("album").getJSONObject("artist").getString("img1v1Url");
        int songId = musicInfoJson.getInteger("id");

        JSONObject musicInfo = new JSONObject();
        musicInfo.put("song_name", songName);
        musicInfo.put("songer_name", songerName);
        musicInfo.put("cover_url", coverUrl);
        musicInfo.put("song_id", songId);
        return musicInfo;
    }

    /**
     * 获取二维码路径
     */
    public String getCode(String url) {
        String codePath = getPluginsDataPath() + "/code.png";
        // 生成二维码
        QrCodeUtil.encodeQrCode(url, codePath);
        return codePath;
    }


    /**
     * 生成语音文件并返回目录
     */
    public String makeVoice(String txt) {
        String appId = Setting.getAppId();
        String apiKey = Setting.getApiKey();
        String secretKey = Setting.getSecretKey();

        if ("".equals(appId)) {
            return "1";
        }
        Path newsFilePath = getPluginsDataPath();
        int randomNum = getRandomNum(0, 10000);
        String filename = getTime() + randomNum + ".mp3";
        // 初始化一个AipSpeech
        AipSpeech client = new AipSpeech(appId, apiKey, secretKey);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);

        // 调用接口
        TtsResponse res = client.synthesis(txt, "zh", 1, null);
        byte[] data = res.getData();
        org.json.JSONObject res1 = res.getResult();
        if (data != null) {
            try {
                String filepath = newsFilePath + "/cache/voice/" + filename;
                Util.writeBytesToFileSystem(data, filepath);
                return filepath;
            } catch (IOException e) {
                e.printStackTrace();
                return "0";
            }
        }
        if (res1 != null) {
            Setting.getBot().getLogger().info(res1.toString(2));
            return "0";
        }
        return "0";
    }

    /**
     * 获取当前小时分钟
     */
    public String getNowTime() {
        // 格式化时间
        SimpleDateFormat sdf = new SimpleDateFormat();
        // a为am/pm的标记
        sdf.applyPattern("HH:mm");
        // 获取当前时间
        Date date = new Date();
        // 输出已经格式化的现在时间（24小时制）
        return sdf.format(date);
    }

    /**
     * 获取当前小时分钟1
     */
    public String getNowTime1() {
        // 格式化时间
        SimpleDateFormat sdf = new SimpleDateFormat();
        // a为am/pm的标记
        sdf.applyPattern("HH");
        // 获取当前时间
        Date date = new Date();
        // 输出已经格式化的现在时间（24小时制）
        return sdf.format(date);
    }


    /**
     * 获取时间
     */
    public String getTime1() {
        // 格式化时间
        SimpleDateFormat sdf = new SimpleDateFormat();
        // a为am/pm的标记
        sdf.applyPattern("MMdd");
        // 获取当前时间
        Date date = new Date();
        return sdf.format(date);

    }

    /**
     * 获取时间2
     */
    public String getTime2() {
        // 格式化时间
        SimpleDateFormat sdf = new SimpleDateFormat();
        // a为am/pm的标记
        sdf.applyPattern("MM月dd日");
        // 获取当前时间
        Date date = new Date();
        return sdf.format(date);

    }

    /**
     * 获取时间3
     */
    public String getTime3() {
        // 格式化时间
        SimpleDateFormat sdf = new SimpleDateFormat();
        // a为am/pm的标记
        sdf.applyPattern("yyyy-MM-dd");
        // 获取当前时间
        Date date = new Date();
        return sdf.format(date);
    }

    /**
     * 获取时间4
     */
    public String getTime4() {
        // 格式化时间
        SimpleDateFormat sdf = new SimpleDateFormat();
        // a为am/pm的标记
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
        // 获取当前时间
        Date date = new Date();
        return sdf.format(date);
    }


    /**
     * 重写星期文件
     */
    public static void rewrite(int week) {
        try {
            File weekcacheFile = new File(getPluginsDataPath() + "/cache/week.cache");
            BufferedWriter out = new BufferedWriter(new FileWriter(weekcacheFile));
            out.write(String.valueOf(week));
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 重写日期文件
     */
    public void rewriteData(File file, String qian) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("date", getTime1());
            jsonObject.put("qian", qian);
            out.write(String.valueOf(jsonObject));
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取已抽签文件
     */
    public String readData(File file) {
        BufferedReader in;
        try {
            in = new BufferedReader(new FileReader(file));
            String data = in.readLine();
            JSONObject jsonObject = JSON.parseObject(data);
            String time = jsonObject.getString("date");
            String qian = jsonObject.getString("qian");
            in.close();
            if (time.equals(getTime1())) {
                return qian;
            } else {
                return "1";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "0";
    }

    public boolean downloadFile(String url, String filePath) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request getRequest = new Request.Builder()
                .url(url)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36")
                .get()
                .build();
        Call call = okHttpClient.newCall(getRequest);
        try {
            Response response = call.execute();
            ResponseBody body = response.body();
            //获取流
            assert body != null;
            InputStream in = body.byteStream();
            //转化为bitmap
            FileOutputStream fo = new FileOutputStream(filePath);
            byte[] buf = new byte[1024];
            int length;
            while ((length = in.read(buf, 0, buf.length)) != -1) {
                fo.write(buf, 0, length);
            }
            in.close();
            fo.close();
            //检测下载是否成功
            File file = new File(filePath);
            if (file.exists()) {
                //基本上没那么小的文件
                if (file.length() < 1000) {
                    file.delete();
                    return false;
                }
            } else {
                return false;
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 取得两个日期之间的相差多少天
     **/
    public int daysBetween(Date early, Date late) {

        Calendar calst = Calendar.getInstance();
        Calendar caled = Calendar.getInstance();
        calst.setTime(early);
        caled.setTime(late);
        //设置时间为0时
        calst.set(Calendar.HOUR_OF_DAY, 0);
        calst.set(Calendar.MINUTE, 0);
        calst.set(Calendar.SECOND, 0);
        caled.set(Calendar.HOUR_OF_DAY, 0);
        caled.set(Calendar.MINUTE, 0);
        caled.set(Calendar.SECOND, 0);
        //得到两个日期相差的天数
        int days = ((int) (caled.getTime().getTime() / 1000) - (int) (calst
                .getTime().getTime() / 1000)) / 3600 / 24;

        return days;
    }

    public JSONObject readFile(File file) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(file));
        String data = in.readLine();
        JSONObject jsonObject = JSON.parseObject(data);
        in.close();
        return jsonObject;
    }

    public boolean writeFile(File file, String data) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            out.write(data);
            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void GenerateImage(String imgStr, String img_name) { //对字节数组字符串进行Base64解码并生成图片
        Base64.Decoder decoder = Base64.getDecoder();
        try {
            //Base64解码
            byte[] b = decoder.decode(imgStr);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {//调整异常数据
                    b[i] += 256;
                }
            }
            //生成jpeg图片

            String imgFilePath = getPluginsDataPath() + "/cache/image/" + img_name;//新生成的图片
            OutputStream out = new FileOutputStream(imgFilePath);
            out.write(b);
            out.flush();
            out.close();
        } catch (Exception e) {
        }
    }

    public void sendMsgToWeChat(String msg) throws Exception {
        String botKeyUrl = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=" + Setting.getQYWXKEY();
        JSONObject text = new JSONObject();
        text.put("content", msg);
        JSONObject reqBody = new JSONObject();
        reqBody.put("msgtype", "text");
        reqBody.put("text", text);
        reqBody.put("safe", 0);

        MediaType contentType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(contentType, reqBody.toString());
        String respMsg = okHttp(body, botKeyUrl);
        if ("0".equals(respMsg.substring(11, 12))) {
            Setting.getBot().getLogger().info("发送消息成功！");
        } else {
            Setting.getBot().getLogger().info("请求失败！");
            Setting.getBot().getLogger().info("群机器人推送消息失败，错误信息：\n" + respMsg);
        }
    }

    public String okHttp(RequestBody body, String url) throws Exception {
        // 构造和配置OkHttpClient
        OkHttpClient client;

        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS) // 设置连接超时时间
                .readTimeout(20, TimeUnit.SECONDS) // 设置读取超时时间
                .build();


        // 构造Request对象
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("cache-control", "no-cache") // 响应消息不缓存
                .build();

        // 构建Call对象，通过Call对象的execute()方法提交异步请求
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 请求结果处理
        byte[] datas = response.body().bytes();
        String respMsg = new String(datas);

        return respMsg;
    }

}


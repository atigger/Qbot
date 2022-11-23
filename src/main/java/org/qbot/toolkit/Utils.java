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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.qbot.Plugin;

import java.io.*;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

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
     * Coc鱼情解析
     */
    public static String CocFishGet() {
        OkHttpClient httpClient = new OkHttpClient();
        Request getRequest = new Request.Builder()
                .url("http://www.clashofclansforecaster.com/STATS.json")
                .get()
                .build();
        Call call = httpClient.newCall(getRequest);
        try {
            Response response = call.execute();
            String html = Objects.requireNonNull(response.body()).string();
            JSONObject jsonObject = JSONObject.parseObject(html);
            String lootIndexString = jsonObject.getString("lootIndexString");
            String forecastMessages = jsonObject.getJSONObject("forecastMessages").getString("chinese-simp");
            JSONArray regionStats = jsonObject.getJSONArray("regionStats");
            String date = "";
            int alluser = 0;
            int offline = 0;
            int shielded = 0;
            int attackable = 0;
            for (Object obj : regionStats) {
                JSONArray jsonArray = JSONArray.parseArray(obj.toString());
                if ("China".equals(jsonArray.getString(1))) {
                    date = jsonArray.getString(3);
                    alluser = jsonArray.getInteger(5);
                    offline = jsonArray.getInteger(6);
                    shielded = jsonArray.getInteger(10);
                    attackable = jsonArray.getInteger(12);
                }
            }
            int online = alluser - offline;
            double onlineRate = online / (double) alluser * 100;
            double offlineRate = offline / (double) alluser * 100;
            double shieldedRate = shielded / (double) alluser * 100;
            double attackableRate = attackable / (double) alluser * 100;
            DecimalFormat df = new DecimalFormat("#.00");
            forecastMessages = forecastMessages.replace("现在有效地战利品是 <b>", "当前鱼情为:");
            forecastMessages = forecastMessages.replace("</b> 现在.  这将持续到", "\n当前鱼情将持续:");
            forecastMessages = forecastMessages.substring(0, forecastMessages.indexOf("分钟") + 2);
            return "查询时间:" + date + "\n" +
                    "鱼情指数:" + lootIndexString + "\n" +
                    forecastMessages + "\n" +
                    "在线玩家:" + online + " " + df.format(onlineRate) + "%\n" +
                    "离线玩家:" + offline + " " + df.format(offlineRate) + "%\n" +
                    "护盾玩家:" + shielded + " " + df.format(shieldedRate) + "%\n" +
                    "可被攻击玩家:" + attackable + " " + df.format(attackableRate) + "%";
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 统计
     */
    public static void statisticsGet(int index) {
        try {
            OkHttpClient httpClient = new OkHttpClient();
            Request getRequest = new Request.Builder()
                    .url("https://image.globaldxmfi.com/statistics.php?statistics=" + index)
                    .get()
                    .build();
            Call call = httpClient.newCall(getRequest);
            Response response = call.execute();
            Objects.requireNonNull(response.body()).string();
        } catch (IOException e) {
            e.printStackTrace();
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


    /**
     * 获取日期
     */
    public static String getTimeForWorld() {
        // 格式化时间
        SimpleDateFormat sdf = new SimpleDateFormat();
        // a为am/pm的标记
        sdf.applyPattern("yyyyMMdd");
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

    public String getHoroscopeText(String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            Elements html = doc.select("table");
            String html1 = String.valueOf(html);
            return formatText(html1);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
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
     * 获取音乐信息
     */
    public JSONObject getMusicInfo(String musicName) {
        String url = "http://music.163.com/api/search/get/web?s=" + musicName + "&type=1&limit=1";
        String html = okHttpClientGet(url, "220.181.108.104");
        JSONObject json = JSONObject.parseObject(html);
        System.out.println(json);
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
            System.out.println(res1.toString(2));
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

    public boolean downloadImg(String url, String filePath) {
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
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static String getImage() {
        int num = getRandomNum(1, 7735);
        try {
            File file = new File(getPluginsDataPath() + "/cache/image/" + num + ".jpg");
            if (!file.exists()) {
                String url = "https://image.miraiqbot.xyz/image/" + num + ".jpg";
                OkHttpClient okHttpClient = new OkHttpClient();
                Request getRequest = new Request.Builder()
                        .url(url)
                        .addHeader("User-Agent", "miraiqbot")
                        .get()
                        .build();
                Call call = okHttpClient.newCall(getRequest);
                Response response = call.execute();
                if (response.code() == 200) {
                    ResponseBody body = response.body();
                    //获取流
                    assert body != null;
                    InputStream in = body.byteStream();
                    //转化为bitmap
                    FileOutputStream fo = new FileOutputStream(getPluginsDataPath() + "/cache/image/" + num + ".jpg");
                    byte[] buf = new byte[1024];
                    int length;
                    while ((length = in.read(buf, 0, buf.length)) != -1) {
                        fo.write(buf, 0, length);
                    }
                    in.close();
                    fo.close();
                    return file.toString();
                }
            } else {
                return file.toString();
            }
            return "获取失败";
        } catch (Exception e) {
            return "获取失败";
        }
    }

    public static String getTipsImage() {
        try {
            File file = new File(getPluginsDataPath() + "/cache/image/ts.jpg");
            if (!file.exists()) {
                String url = "https://image.miraiqbot.xyz/ts.jpg";
                OkHttpClient okHttpClient = new OkHttpClient();
                Request getRequest = new Request.Builder()
                        .url(url)
                        .addHeader("User-Agent", "miraiqbot")
                        .get()
                        .build();
                Call call = okHttpClient.newCall(getRequest);
                Response response = call.execute();
                if (response.code() == 200) {
                    ResponseBody body = response.body();
                    //获取流
                    assert body != null;
                    InputStream in = body.byteStream();
                    //转化为bitmap
                    FileOutputStream fo = new FileOutputStream(getPluginsDataPath() + "/cache/image/ts.jpg");
                    byte[] buf = new byte[1024];
                    int length;
                    while ((length = in.read(buf, 0, buf.length)) != -1) {
                        fo.write(buf, 0, length);
                    }
                    in.close();
                    fo.close();
                    return file.toString();
                }
            } else {
                return file.toString();
            }
            return "获取失败";
        } catch (Exception e) {
            return "获取失败";
        }
    }

    public static String getToken() {
        try {
            String url = "https://image.globaldxmfi.com/token.php";
            OkHttpClient okHttpClient = new OkHttpClient();
            Request getRequest = new Request.Builder()
                    .url(url)
                    .addHeader("User-Agent", "miraiqbot")
                    .get()
                    .build();
            Call call = okHttpClient.newCall(getRequest);
            Response response = call.execute();
            if (response.code() == 200) {
                return Objects.requireNonNull(response.body()).string();
            } else {
                return "获取失败";
            }
        } catch (Exception e) {
            return "获取失败";
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

    /**
     * 世界杯赛程
     *
     * @return
     */
    public static String getWorldCup() {
        try {
            StringBuilder returnData = new StringBuilder();
            returnData.append("【世界杯赛程】");
            String url = "https://sport.zijieapi.com/vertical/sport/go/world_cup/match_info";
            OkHttpClient okHttpClient = new OkHttpClient();
            Request getRequest = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            Call call = okHttpClient.newCall(getRequest);
            Response response = call.execute();
            if (response.code() == 200) {
                JSONObject jsonObject = JSONObject.parseObject(Objects.requireNonNull(response.body()).string());
                int date = Integer.parseInt(getTimeForWorld());
                for (int j = 0; j < 2; j++) {
                    int date1 = date + j;
                    JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONObject("match_infos").getJSONArray(String.valueOf(date1));
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        if (jsonObject1.getString("group_name") != null) {
                            //主队名称
                            String hostTeamName = jsonObject1.getJSONObject("host_team").getString("cn_name");
                            //客队名称
                            String guestTeamName = jsonObject1.getJSONObject("guest_team").getString("cn_name");
                            //主队得分
                            int hostScore = jsonObject1.getInteger("host_score");
                            //客队得分
                            int guestScore = jsonObject1.getInteger("guest_score");
                            //比赛时间
                            String matchTime = jsonObject1.getString("match_round_desc");
                            String data = "\n\n" + matchTime + "\n" + hostTeamName + " " + hostScore + " : " + guestScore + " " + guestTeamName;
                            returnData.append(data);
                        }
                    }
                }
                return returnData.toString();
            } else {
                return "获取失败";
            }
        } catch (Exception e) {
            return "获取失败";
        }
    }

    /**
     * 获取小组积分
     *
     * @param group 小组代号
     * @return
     */
    public static String getWorldCupGroup(String group) {
        try {
            StringBuilder returnData = new StringBuilder();
            returnData.append("【").append(group).append("组】\n");
            String temp =
                    "\n" +
                            "%cn1% \n" +
                            "场次%play1% \n" +
                            "%win1%胜/%draw1%平/%loss1%负 \n" +
                            "%goals_for1%进/%goals_against1%失 \n" +
                            "积分%points1%\n" +
                            "\n" +
                            "%cn2% \n" +
                            "场次%play2% \n" +
                            "%win2%胜/%draw2%平/%loss2%负 \n" +
                            "%goals_for2%进/%goals_against2%失 \n" +
                            "积分%points2%\n" +
                            "\n" +
                            "%cn3% \n" +
                            "场次%play3% \n" +
                            "%win3%胜/%draw3%平/%loss3%负 \n" +
                            "%goals_for3%进/%goals_against3%失 \n" +
                            "积分%points3%\n" +
                            "\n" +
                            "%cn4% \n" +
                            "场次%play4% \n" +
                            "%win4%胜/%draw4%平/%loss4%负 \n" +
                            "%goals_for4%进/%goals_against4%失 \n" +
                            "积分%points4%";
            String url = "https://sport.zijieapi.com/vertical/sport/go/world_cup/group_ranking";
            OkHttpClient okHttpClient = new OkHttpClient();
            Request getRequest = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            Call call = okHttpClient.newCall(getRequest);
            Response response = call.execute();
            if (response.code() == 200) {
                JSONObject jsonObject = JSONObject.parseObject(Objects.requireNonNull(response.body()).string());
                JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("groups");
                for (int i = 0; i < 8; i++) {
                    if (jsonArray.getJSONObject(i).getString("group_name").contains(group)) {
                        JSONArray jsonArray1 = jsonArray.getJSONObject(i).getJSONArray("team_list");
                        for (int j = 0; j < 4; j++) {
                            JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                            String cn = jsonObject1.getString("cn_name");
                            int play = jsonObject1.getJSONObject("group_data").getInteger("games_played");
                            int win = jsonObject1.getJSONObject("group_data").getInteger("wins");
                            int draw = jsonObject1.getJSONObject("group_data").getInteger("draws");
                            int loss = jsonObject1.getJSONObject("group_data").getInteger("losses");
                            int goals_for = jsonObject1.getJSONObject("group_data").getInteger("goals_for");
                            int goals_against = jsonObject1.getJSONObject("group_data").getInteger("goals_against");
                            int points = jsonObject1.getJSONObject("group_data").getInteger("points");
                            temp = temp.replace("%cn" + (j + 1) + "%", cn)
                                    .replace("%play" + (j + 1) + "%", String.valueOf(play))
                                    .replace("%win" + (j + 1) + "%", String.valueOf(win))
                                    .replace("%draw" + (j + 1) + "%", String.valueOf(draw))
                                    .replace("%loss" + (j + 1) + "%", String.valueOf(loss))
                                    .replace("%goals_for" + (j + 1) + "%", String.valueOf(goals_for))
                                    .replace("%goals_against" + (j + 1) + "%", String.valueOf(goals_against))
                                    .replace("%points" + (j + 1) + "%", String.valueOf(points));

                        }
                        returnData.append(temp);
                    }
                }
                return returnData.toString();
            } else {
                return "获取失败";
            }
        } catch (Exception e) {
            return "获取失败";
        }
    }
}


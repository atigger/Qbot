package org.example.mirai.plugin.Toolkit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import net.mamoe.mirai.console.plugin.PluginManager;
import net.mamoe.mirai.console.plugin.jvm.JvmPlugin;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.MusicKind;
import net.mamoe.mirai.message.data.MusicShare;
import net.mamoe.mirai.utils.ExternalResource;
import org.example.mirai.plugin.JavaPluginMain;
import org.example.mirai.plugin.Toolkit.Utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Plugin {
    Utils utils = new Utils();

    //获取运势
    public String get_fortune() {
        Utils utils = new Utils();
        String Url = "https://m.weibo.cn/api/container/getIndex?type=uid&value=7230522444&containerid=1076037230522444";
        String html = utils.okHttpClient_get(Url);
        if(html==null){
            return "获取失败";
        }
        JSONObject json;
        json = JSONObject.parseObject(html);
        json = JSONObject.parseObject(String.valueOf(json.getJSONObject("data").getJSONArray("cards").get(0)));
        String txt = String.valueOf(json.getJSONObject("mblog").getString("text"));
        String wb_time = String.valueOf(json.getJSONObject("mblog").getString("created_at"));
        String now_week = utils.getWeek();
        if (wb_time.indexOf(now_week) != -1) {
            if (txt.indexOf("日运播报") != -1) {
                txt = txt.replace("<br />", "\n");
            } else {
                txt = "获取失败，获取到广告了";
            }
        } else {
            txt = "获取失败，今日还未发运势";
        }
        return txt;
    }

    //获取新闻
    public String get_news() {
        String now_day = utils.get_time1();
        File news_file = new File(utils.get_plugins_data_path() + "/cache/" + now_day + ".jpg");
        if (news_file.exists()) {
            return news_file.getPath();
        } else {
            if (utils.getnews()) {
                return news_file.getPath();
            } else {
                return "失败";
            }
        }
    }

    //星座运势
    public String get_horoscope(String name) {
        String url = "";
        switch (name) {

            case "白羊":
                url = "http://astro.sina.com.cn/fate_day_Aries/";
                return utils.get_horoscope_text(url);
            case "金牛":
                url = "http://astro.sina.com.cn/fate_day_Taurus/";
                return utils.get_horoscope_text(url);
            case "双子":
                url = "http://astro.sina.com.cn/fate_day_Gemini/";
                return utils.get_horoscope_text(url);
            case "巨蟹":
                url = "http://astro.sina.com.cn/fate_day_Cancer/";
                return utils.get_horoscope_text(url);
            case "狮子":
                url = "http://astro.sina.com.cn/fate_day_leo/";
                return utils.get_horoscope_text(url);
            case "处女":
                url = "http://astro.sina.com.cn/fate_day_Virgo/";
                return utils.get_horoscope_text(url);
            case "天秤":
                url = "http://astro.sina.com.cn/fate_day_Libra/";
                return utils.get_horoscope_text(url);
            case "天蝎":
                url = "http://astro.sina.com.cn/fate_day_Scorpio/";
                return utils.get_horoscope_text(url);
            case "射手":
                url = "http://astro.sina.com.cn/fate_day_Sagittarius/";
                return utils.get_horoscope_text(url);
            case "摩羯":
                url = "http://astro.sina.com.cn/fate_day_Capricorn/";
                return utils.get_horoscope_text(url);
            case "水瓶":
                url = "http://astro.sina.com.cn/fate_day_Aquarius/";
                return utils.get_horoscope_text(url);
            case "双鱼":
                url = "http://astro.sina.com.cn/fate_day_Pisces/";
                return utils.get_horoscope_text(url);
        }
        return null;
    }

    //求签
    public String get_cq() {
        String cq_txt = "";
        String file_path = String.valueOf(utils.get_plugins_data_path()) + "/cq.txt";
        int max_num = utils.get_file_len(file_path);
        int random_num = utils.get_random_num(1, max_num);
        try {
            cq_txt = Files.readAllLines(Paths.get(file_path)).get(random_num);
        } catch (IOException e) {
            e.printStackTrace();
        }
        cq_txt = cq_txt.replace("【解签】", "\n【解签】");
        return cq_txt;
    }

    //音乐分享
    public MusicShare get_music(String music_name) {
        JSONObject music_info = utils.get_music_info(music_name);

        MusicShare musicShare = new MusicShare(MusicKind.NeteaseCloudMusic, music_info.getString("song_name"),
                music_info.getString("songer_name"),
                "http://music.163.com/song/" + music_info.getString("song_id") + "/?userid=380034310",
                music_info.getString("cover_url"),
                "http://music.163.com/song/media/outer/url?id=" + music_info.getString("song_id") + "&userid=380034310"
        );
        return musicShare;
    }

    //发语音
    public ExternalResource get_voice(Group group, String txt) {
        String filepath = utils.make_voice(txt);
        if (filepath.equals("1")) {
            return null;
        } else {
            ExternalResource voice_file = ExternalResource.create(new File(filepath));
            return voice_file;
        }
    }

    //战力查询
    public String get_power(String hero, String qu) {
        String hero_url = "https://www.sapi.run/hero/select.php?hero=" + hero + "&type=" + qu;
        String data = utils.okHttpClient_get(hero_url);
        JSONObject json_data = JSONObject.parseObject(data);
        int code = json_data.getInteger("code");
        if (code == 200) {
            String heroName = json_data.getJSONObject("data").getString("heroName");
            String area = json_data.getJSONObject("data").getString("area");
            String areaPower = json_data.getJSONObject("data").getString("areaPower");
            String city = json_data.getJSONObject("data").getString("city");
            String cityPower = json_data.getJSONObject("data").getString("cityPower");
            String province = json_data.getJSONObject("data").getString("province");
            String provincePower = json_data.getJSONObject("data").getString("provincePower");
            return "英雄名:" + heroName + "\n最低县标:" + area + "\n县标战力:" + areaPower + "\n最低市标:" + city + "\n市标战力:" + cityPower + "\n最低省标:" + province + "\n省标战力:" + provincePower;
        }
        return "获取失败";
    }

    //获取随机图片
    public String get_image() {
        JvmPlugin jvmPlugin = new JavaPluginMain();
        Path news_file_path = PluginManager.INSTANCE.getPluginsDataPath().resolve(jvmPlugin.getDescription().getName());
        String path = news_file_path + "/image";
        File file = new File(path);        //获取其file对象
        File[] fs = file.listFiles();    //遍历path下的文件和目录，放在File数组中
        int max_file = fs.length;
        if (max_file == 0) {
            return "获取失败，请联系管理员添加照片";
        }
        int random_num = utils.get_random_num(0, max_file - 1);
        String file_path = String.valueOf(fs[random_num]);
        return file_path;
    }

    //奥运排名
    public String medal_rank() {
        String url = "https://act.e.mi.com/olympic/medal_rank";
        String html = utils.okHttpClient_get(url);
        JSONObject jsonObject = JSON.parseObject(html);
        JSONArray json_data = jsonObject.getJSONArray("data");
        String paihang = "";
        for (int i = 0; i < 5; i++) {
            String country = json_data.getJSONObject(i).getString("country_name");
            String rank = json_data.getJSONObject(i).getString("rank");
            String medal_gold_count = json_data.getJSONObject(i).getString("medal_gold_count");
            String medal_silver_count = json_data.getJSONObject(i).getString("medal_silver_count");
            String medal_bronze_count = json_data.getJSONObject(i).getString("medal_bronze_count");
            String medal_sum_count = json_data.getJSONObject(i).getString("medal_sum_count");
            String list1 = "第" + rank + "名:" + country + "\n金牌:" + medal_gold_count + " 银牌:" + medal_silver_count + " 铜牌:" + medal_bronze_count + " 总数:" + medal_sum_count + "\n";
            paihang = paihang + list1;
        }
        return paihang;
    }
}
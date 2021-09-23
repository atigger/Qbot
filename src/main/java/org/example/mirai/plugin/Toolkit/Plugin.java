package org.example.mirai.plugin.toolkit;

import com.alibaba.fastjson.JSONObject;
import net.mamoe.mirai.console.plugin.PluginManager;
import net.mamoe.mirai.console.plugin.jvm.JvmPlugin;
import net.mamoe.mirai.message.data.MusicKind;
import net.mamoe.mirai.message.data.MusicShare;
import net.mamoe.mirai.utils.ExternalResource;
import org.example.mirai.plugin.JavaPluginMain;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Plugin class
 *
 * @author 649953543@qq.com
 * @date 2021/09/22
 */

public class Plugin {
    Utils utils = new Utils();
    String broadcast = "播报";
    String one = "1";
    int statuscode = 200;

    /**
     * 获取运势
     */
    public String getFortune() {
        Utils utils = new Utils();
        String url = "https://m.weibo.cn/api/container/getIndex?type=uid&value=7230522444&containerid=1076037230522444";
        String html = utils.okHttpClientGet(url);
        if (html == null) {
            return "获取失败";
        }
        JSONObject json;
        json = JSONObject.parseObject(html);
        json = JSONObject.parseObject(String.valueOf(json.getJSONObject("data").getJSONArray("cards").get(0)));
        String txt = String.valueOf(json.getJSONObject("mblog").getString("text"));
        String wbTime = String.valueOf(json.getJSONObject("mblog").getString("created_at"));
        String nowWeek = utils.getWeek();
        if (wbTime.contains(nowWeek)) {
            if (txt.contains(broadcast)) {
                txt = txt.replace("<br />", "\n");
            } else {
                txt = "获取失败，获取到广告了";
            }
        } else {
            txt = "获取失败，今日还未发运势";
        }
        return txt;
    }

    /**
     * 获取新闻
     */
    public String getNews() {
        String nowDay = utils.getTime1();
        File newsFile = new File(utils.getPluginsDataPath() + "/cache/news/" + nowDay + ".jpg");
        String filePath = utils.getPluginsDataPath() + "/cache/news/" + utils.getTime1() + ".jpg";
        String url = "http://api.03c3.cn/zb";
        if (newsFile.exists()) {
            return newsFile.getPath();
        } else {
            if (utils.downloadImg(url, filePath)) {
                return newsFile.getPath();
            } else {
                return "失败";
            }
        }
    }

    /**
     * 星座运势
     */
    public String getHoroscope(String name) {
        String url;
        switch (name) {
            case "白羊":
                url = "http://astro.sina.com.cn/fate_day_Aries/";
                return utils.getHoroscopeText(url);
            case "金牛":
                url = "http://astro.sina.com.cn/fate_day_Taurus/";
                return utils.getHoroscopeText(url);
            case "双子":
                url = "http://astro.sina.com.cn/fate_day_Gemini/";
                return utils.getHoroscopeText(url);
            case "巨蟹":
                url = "http://astro.sina.com.cn/fate_day_Cancer/";
                return utils.getHoroscopeText(url);
            case "狮子":
                url = "http://astro.sina.com.cn/fate_day_leo/";
                return utils.getHoroscopeText(url);
            case "处女":
                url = "http://astro.sina.com.cn/fate_day_Virgo/";
                return utils.getHoroscopeText(url);
            case "天秤":
                url = "http://astro.sina.com.cn/fate_day_Libra/";
                return utils.getHoroscopeText(url);
            case "天蝎":
                url = "http://astro.sina.com.cn/fate_day_Scorpio/";
                return utils.getHoroscopeText(url);
            case "射手":
                url = "http://astro.sina.com.cn/fate_day_Sagittarius/";
                return utils.getHoroscopeText(url);
            case "摩羯":
                url = "http://astro.sina.com.cn/fate_day_Capricorn/";
                return utils.getHoroscopeText(url);
            case "水瓶":
                url = "http://astro.sina.com.cn/fate_day_Aquarius/";
                return utils.getHoroscopeText(url);
            case "双鱼":
                url = "http://astro.sina.com.cn/fate_day_Pisces/";
                return utils.getHoroscopeText(url);
            default:
                return null;
        }
    }

    /**
     * 求签
     */
    public String getCq() {
        String cqTxt = "";
        String filePath = utils.getPluginsDataPath() + "/cq.txt";
        int maxNum = utils.getFileLen(filePath);
        int randomNum = utils.getRandomNum(1, maxNum);
        try {
            cqTxt = Files.readAllLines(Paths.get(filePath)).get(randomNum);
        } catch (IOException e) {
            e.printStackTrace();
        }
        cqTxt = cqTxt.replace("【解签】", "\n【解签】");
        return cqTxt;
    }

    /**
     * 音乐分享
     */
    public MusicShare getMusic(String musicName) {
        JSONObject musicInfo = utils.getMusicInfo(musicName);

        return new MusicShare(MusicKind.NeteaseCloudMusic, musicInfo.getString("song_name"),
                musicInfo.getString("songer_name"),
                "http://music.163.com/song/" + musicInfo.getString("song_id") + "/?userid=380034310",
                musicInfo.getString("cover_url"),
                "http://music.163.com/song/media/outer/url?id=" + musicInfo.getString("song_id") + "&userid=380034310"
        );
    }

    /**
     * 发语音
     */

    public ExternalResource getVoice(String txt) {
        String filepath = utils.makeVoice(txt);
        if (one.equals(filepath)) {
            return null;
        } else {
            return ExternalResource.create(new File(filepath));
        }
    }

    /**
     * 战力查询
     */
    public JSONObject getPower(String hero, String qu) {
        String heroUrl = "https://www.sapi.run/hero/select.php?hero=" + hero + "&type=" + qu;
        String data = utils.okHttpClientGet(heroUrl);
        JSONObject jsonData = JSONObject.parseObject(data);
        int code = jsonData.getInteger("code");
        if (code == statuscode) {
            String heroName = jsonData.getJSONObject("data").getString("name");
            String area = jsonData.getJSONObject("data").getString("area");
            String areaPower = jsonData.getJSONObject("data").getString("areaPower");
            String city = jsonData.getJSONObject("data").getString("city");
            String cityPower = jsonData.getJSONObject("data").getString("cityPower");
            String province = jsonData.getJSONObject("data").getString("province");
            String provincePower = jsonData.getJSONObject("data").getString("provincePower");
            String heroPhoto = jsonData.getJSONObject("data").getString("photo");
            String heroPower = "英雄名:" + heroName + "\n最低县标:" + area + "\n县标战力:" + areaPower + "\n最低市标:" + city + "\n市标战力:" + cityPower + "\n最低省标:" + province + "\n省标战力:" + provincePower;
            JSONObject heroInfo = new JSONObject();
            heroInfo.put("data", heroPower);
            String filePath = utils.getPluginsDataPath() + "/cache/hero/" + heroName + ".jpg";
            File heroImgPath = new File(filePath);
            if(heroImgPath.exists()){
                heroInfo.put("img", heroImgPath.toString());
                return heroInfo;
            }else{
                if (utils.downloadImg(heroPhoto,filePath)){
                    heroInfo.put("img", heroImgPath.toString());
                    return heroInfo;
                }
                return null;
            }

        }
        return null;
    }

    /**
     * 获取随机图片
     */
    public String getImage() {
        JvmPlugin jvmPlugin = new JavaPluginMain();
        Path newsFilePath = PluginManager.INSTANCE.getPluginsDataPath().resolve(jvmPlugin.getDescription().getName());
        String path = newsFilePath + "/image";
        //获取其file对象
        File file = new File(path);
        //遍历path下的文件和目录，放在File数组中
        File[] fs = file.listFiles();
        assert fs != null;
        int maxFile = fs.length;
        if (maxFile == 0) {
            return "获取失败，请联系管理员添加照片";
        } else if (maxFile == 1) {
            return String.valueOf(fs[0]);
        }
        int randomNum = utils.getRandomNum(0, maxFile - 1);
        return String.valueOf(fs[randomNum]);
    }

}
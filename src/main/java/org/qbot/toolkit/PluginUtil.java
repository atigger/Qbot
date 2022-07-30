package org.qbot.toolkit;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import net.mamoe.mirai.console.plugin.PluginManager;
import net.mamoe.mirai.console.plugin.jvm.JvmPlugin;
import net.mamoe.mirai.message.data.MusicKind;
import net.mamoe.mirai.message.data.MusicShare;
import net.mamoe.mirai.utils.ExternalResource;
import org.qbot.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Plugin class
 *
 * @author 649953543@qq.com
 * @date 2021/09/22
 */

@SuppressWarnings({"ALL", "AlibabaMethodTooLong"})
public class PluginUtil {
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
        try {
            JSONObject json;
            json = JSON.parseObject(html);
            json = JSON.parseObject(String.valueOf(json.getJSONObject("data").getJSONArray("cards").get(0)));
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
        } catch (Exception e) {
            return "获取失败";
        }

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
        JSONObject jsonData = JSON.parseObject(data);
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
            if (heroImgPath.exists()) {
                heroInfo.put("img", heroImgPath.toString());
                return heroInfo;
            } else {
                if (utils.downloadImg(heroPhoto, filePath)) {
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
        JvmPlugin jvmPlugin = new Plugin();
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

    /**
     * 摸鱼办
     */
    @SuppressWarnings("AlibabaMethodTooLong")
    public String moFish() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date nowdate = sdf.parse(utils.getTime3());
        String txt = "";

        String sjd = "";
        int hh = Integer.parseInt(utils.getNowTime1());
        if (hh < 6) {
            sjd = "凌晨";
        }
        if (hh >= 6 && hh < 9) {
            sjd = "早上";
        }
        if (hh >= 9 && hh < 11) {
            sjd = "上午";
        }
        if (hh >= 11 && hh < 14) {
            sjd = "中午";
        }
        if (hh >= 14 && hh < 19) {
            sjd = "下午";
        }
        if (hh >= 19) {
            sjd = "晚上";
        }
        String statementAtTheBeginning = "\n工作再累，一定不要忘记摸鱼哦！有事没事起身去茶水间，去厕所，去廊道走走别老在工位上坐着，钱是老板的,但命是自己的！";
        String statementAtTheEnd = "\n上班是帮老板赚钱，摸鱼是赚老板的钱！最后，祝愿天下所有摸鱼人，都能愉快的渡过每一天！";
        String superThursday = "\n今天星期四，明天星期五，再坚持一天，然后星期天！";
        String theWeekendToRemind = "\n不是吧不是吧，不会有人还在996、997吧？";

        Date cjdate = sdf.parse("2023-01-22");
        int cj = utils.daysBetween(nowdate, cjdate);
        Date qmjdate = sdf.parse("2023-04-05");
        int qmj = utils.daysBetween(nowdate, qmjdate);
        Date ndjdate = sdf.parse("2023-05-01");
        int ndj = utils.daysBetween(nowdate, ndjdate);
        Date dwjdate = sdf.parse("2023-06-22");
        int dwj = utils.daysBetween(nowdate, dwjdate);
        Date zqjdate = sdf.parse("2022-09-10");
        int zqj = utils.daysBetween(nowdate, zqjdate);
        Date gqjdate = sdf.parse("2022-10-01");
        int gqj = utils.daysBetween(nowdate, gqjdate);
        Date ydjdate = sdf.parse("2023-01-01");
        int ydj = utils.daysBetween(nowdate, ydjdate);

        String title = "【摸鱼办】提醒您：\n" + utils.getTime2() + sjd + "好，摸鱼人！";
        txt = txt + title;
        Calendar calendar = Calendar.getInstance();
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        if (cj == 0 || qmj == 0 || ndj == 0 || dwj == 0 || zqj == 0 || gqj == 0 || ydj == 0) {
            if (cj == 0) {
                txt = txt + "\n今天是春节哦，祝大家春节快乐！";
            }
            if (qmj == 0) {
                txt = txt + "\n今天是清明节哦，祝大家....算了...";
            }
            if (ndj == 0) {
                txt = txt + "\n今天是劳动节哦，祝大家劳动节快乐！";
            }
            if (dwj == 0) {
                txt = txt + "\n今天是端午节哦，祝大家端午节快乐！";
            }
            if (zqj == 0) {
                txt = txt + "\n今天是中秋节哦，祝大家中秋节快乐！";
            }
            if (gqj == 0) {
                txt = txt + "\n今天是国庆节哦，祝大家国庆节快乐！";
            }
            if (ydj == 0) {
                txt = txt + "\n今天是元旦节哦，祝大家元旦节快乐！";
            }
        } else {
            String[] stringWeek = {"周日","周一","周二","周三","周四","周五","周六"};
            if (week == 5) {
                txt = txt + superThursday;
            } else if (week == 7 || week == 1) {
                txt = txt + theWeekendToRemind;
            } else {
                txt = txt + statementAtTheBeginning;
            }
            int day = week - 1;
            txt = txt + "\n今天【"+stringWeek[day]+"】";
            if (week > 1 && week < 7) {
                int jg = 7 - week;
                txt = txt + "\n距离【周末】还有:" + jg + "天";
            }
        }

        if (zqj > 0) {
            txt = txt + "\n距离【中秋】还有:" + zqj + "天";
        }
        if (gqj > 0) {
            txt = txt + "\n距离【国庆】还有:" + gqj + "天";
        }
        if (ydj > 0) {
            txt = txt + "\n距离【元旦】还有:" + ydj + "天";
        }
        if (cj > 0) {
            txt = txt + "\n距离【春节】还有:" + cj + "天";
        }
        if (qmj > 0) {
            txt = txt + "\n距离【清明】还有:" + qmj + "天";
        }
        if (ndj > 0) {
            txt = txt + "\n距离【劳动】还有:" + ndj + "天";
        }
        if (dwj > 0) {
            txt = txt + "\n距离【端午】还有:" + dwj + "天";
        }
        txt = txt + statementAtTheEnd;
        return txt;
    }

}
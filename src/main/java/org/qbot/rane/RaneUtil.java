package org.qbot.rane;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.qbot.toolkit.Utils;

import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

/**
 * RaneUtil class
 *
 * @author 649953543@qq.com
 * @date 2021/09/22
 */

public class RaneUtil {
    Utils utils = new Utils();
    final String urlGetCode = "https://q.qq.com/ide/devtoolAuth/GetLoginCode";
    final String dataPath = utils.getPluginsDataPath() + "/rane/";
    final String errorCode_1 = "-1";
    final String errCode = "errCode";
    final String codeIs_1 = "{\"code\":-1}";
    final String fail = "失败";
    RaneBase raneBase = new RaneBase();

    /**
     * 获取授权链接
     */
    public String getLoginLink() {
        JSONObject jsonData = JSONObject.parseObject(raneBase.okHttpClientGet(urlGetCode), JSONObject.class);
        int code = jsonData.getInteger("code");
        String codes = jsonData.getJSONObject("data").getString("code");
        if (code == 0) {
            return codes;
        } else {
            return "获取codes失败";
        }
    }

    /**
     * 获取登录要用的ticket
     */
    public String getLoginTicket(String codes) throws InterruptedException, IOException {
        if (!codes.contains(fail)) {
            String loginUrl = "https://h5.qzone.qq.com/qqq/code/" + codes + "?_proxy=1&from=ide";
            System.out.println(loginUrl);
            String getLoginStateUrl = "https://q.qq.com/ide/devtoolAuth/syncScanSateGetTicket?code=" + codes;
            int num = 60;
            for (int i = 0; i < num; i++) {
                String data = raneBase.okHttpClientGet(getLoginStateUrl);
                if (data.contains("ok")) {
                    JSONObject jsonData = JSONObject.parseObject(data, JSONObject.class);
                    String ticket = jsonData.getJSONObject("data").getString("ticket");
                    String uin = jsonData.getJSONObject("data").getString("uin");

                    String qqFilePath = dataPath + uin + ".txt";
                    File file = new File(qqFilePath);
                    if (!file.exists()) {
                        if (file.createNewFile()) {
                            raneBase.writeUserFile(ticket, uin, "", "", 0, 0, 1);
                        } else {
                            return null;
                        }
                    } else {
                        raneBase.writeUserFile(ticket, uin, "", "", 0, 0, 1);
                    }
                    return "授权成功";
                }
                Thread.sleep(1000);
            }
            return "授权超时";
        } else {
            return "获取链接失败";
        }
    }

    /**
     * 获取登录code
     */
    public String getLoginCode(String qq) throws IOException {
        String qqFilePath = dataPath + qq + ".txt";
        File file = new File(qqFilePath);
        if (file.exists()) {
            JSONObject jsonData = raneBase.readUserFile(qq);
            String ticket = jsonData.getString("ticket");

            JSONObject dataJson = new JSONObject();
            dataJson.put("ticket", ticket);
            dataJson.put("appid", "1110797565");

            String url = "https://q.qq.com/ide/login";
            JSONObject jsonDataReplace = JSONObject.parseObject(raneBase.okHttpClientLoginPost(url, String.valueOf(dataJson)), JSONObject.class);
            String codes = jsonDataReplace.getString("code");
            if (!codes.contains(errorCode_1)) {
                return codes;
            } else {
                return null;
            }

        } else {
            return null;
        }

    }

    /**
     * 登录
     */
    public String login(String qq) throws IOException, ScriptException, NoSuchMethodException {
        String qqFilePath = dataPath + qq + ".txt";
        File file = new File(qqFilePath);
        if (file.exists()) {
            String codes = getLoginCode(qq);
            String sign = raneBase.getMd5("2014" + codes + "472770f9e581cffb09349f422af57c5d");
            JSONObject obj = new JSONObject();
            obj.put("gid", "201");
            obj.put("sdk", 4);
            obj.put("uid", codes);
            obj.put("ios", false);
            obj.put("sign", sign);

            int rant = raneBase.getRandomNum(10000000, 99999999) + 99999999;
            String ranv = raneBase.encode(rant, String.valueOf(obj));

            String url = "https://rane.jwetech.com:8080/login/login";
            String getData = raneBase.okHttpClientPost(url, rant, ranv);
            if (!getData.contains(codeIs_1)) {
                JSONObject jsonDataReplace = JSONObject.parseObject(getData, JSONObject.class);
                int t = jsonDataReplace.getInteger("t");
                String v = jsonDataReplace.getString("v");
                String data1 = raneBase.decode(t, v);
                JSONObject jsonData = JSONObject.parseObject(data1, JSONObject.class);
                if (!jsonData.toString().contains(fail)) {
                    String uid = jsonData.getString("uid");
                    int money = jsonData.getJSONObject("data").getInteger("money");
                    int gfc = jsonData.getJSONObject("data").getInteger("gfc");
                    String token = jsonData.getString("token");
                    raneBase.rewriteLoginCode(qq, uid, token, money, gfc);
                    return "登录成功\nQQ:" + qq + "\n金币:" + money + "\n奖券:" + gfc;
                } else {
                    return "登录失败,请尝试重新登录";
                }
            } else {
                return "登录失败,请尝试重新登录";
            }
        } else {
            return "未找到授权记录，请先授权";
        }
    }

    /**
     * 签到
     */
    public String getSign(String qq) throws IOException, ScriptException, NoSuchMethodException {
        String url = "https://rane.jwetech.com:8080/game/sign";

        String qqFilePath = dataPath + qq + ".txt";
        File file = new File(qqFilePath);
        if (file.exists()) {
            JSONObject jsonData = raneBase.readUserFile(qq);
            String uid = jsonData.getString("uid");
            String token = jsonData.getString("token");
            int ts = jsonData.getInteger("时序");
            Calendar calendar = Calendar.getInstance();
            int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            if (week == 0) {
                week = 7;
            }
            JSONObject jsonSign = new JSONObject();
            jsonSign.put("day", week);
            jsonSign.put("pid", 0);
            jsonSign.put("video", true);
            jsonSign.put("t", 0);

            String data1 = sendRequest(qq, uid, token, ts, url, jsonSign);
            JSONObject data2 = JSONObject.parseObject(data1, JSONObject.class);
            if (!data1.contains(errCode)) {
                return "签到成功";
            } else {
                return data2.getString("errMsg");
            }
        } else {
            return "未找到授权记录，请先授权";
        }
    }

    /**
     * 超级签到
     */
    public String getSuperSign(String qq) throws IOException, ScriptException, NoSuchMethodException {
        String url = "https://rane.jwetech.com:8080/game/sign";

        String qqFilePath = dataPath + qq + ".txt";
        File file = new File(qqFilePath);
        if (file.exists()) {
            JSONObject jsonData = raneBase.readUserFile(qq);
            String uid = jsonData.getString("uid");
            String token = jsonData.getString("token");
            int ts = jsonData.getInteger("时序");
            JSONObject jsonSign = new JSONObject();
            jsonSign.put("day", 8);
            jsonSign.put("video", true);
            jsonSign.put("t", 2);
            String data1 = sendRequest(qq, uid, token, ts, url, jsonSign);
            JSONObject data2 = JSONObject.parseObject(data1, JSONObject.class);
            if (!data1.contains(errCode)) {
                return "超级签到成功";
            } else {
                return data2.getString("errMsg");
            }
        } else {
            return "未找到授权记录，请先授权";
        }
    }

    /**
     * 补签
     */
    public String[] getReissueSign(String qq) throws IOException, ScriptException, NoSuchMethodException, InterruptedException {
        String url = "https://rane.jwetech.com:8080/game/sign";

        String qqFilePath = dataPath + qq + ".txt";
        File file = new File(qqFilePath);
        if (file.exists()) {
            JSONObject jsonData = raneBase.readUserFile(qq);
            String uid = jsonData.getString("uid");
            String token = jsonData.getString("token");
            int ts = jsonData.getInteger("时序");
            Calendar calendar = Calendar.getInstance();
            int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            if (week == 0) {
                week = 7;
            }
            if (week == 1) {
                return new String[]{"今天是周一，不用补签"};
            }
            String[] list = new String[week - 1];

            for (int ii = 0; ii < week - 1; ii++) {
                int i = ii + 1;
                JSONObject jsonSign = new JSONObject();
                jsonSign.put("day", i);
                jsonSign.put("video", false);
                jsonSign.put("t", 1);
                String data1 = sendRequest(qq, uid, token, ts, url, jsonSign);
                JSONObject data2 = JSONObject.parseObject(data1, JSONObject.class);
                if (!data1.contains(errCode)) {
                    list[ii] = "补签成功";
                } else {
                    String a = data2.getString("errMsg");
                    list[ii] = a;
                    if (a.contains("时序")) {
                        break;
                    }
                }
                Thread.sleep(5000);
                ts++;
            }
            return list;
        } else {
            return null;
        }
    }

    /**
     * 抽奖记录
     */
    public String[] getGiftList(String qq) throws IOException, ScriptException, NoSuchMethodException {
        String url = "https://rane.jwetech.com:8080/game/myLottery";

        String qqFilePath = dataPath + qq + ".txt";
        File file = new File(qqFilePath);
        if (file.exists()) {
            JSONObject jsonData = raneBase.readUserFile(qq);
            String uid = jsonData.getString("uid");
            String token = jsonData.getString("token");
            int ts = jsonData.getInteger("时序");

            String jsonSign1 = "{}";
            JSONObject jsonSign = JSON.parseObject(jsonSign1);
            String data1 = sendRequest(qq, uid, token, ts, url, jsonSign);
            JSONObject data2 = JSONObject.parseObject(data1, JSONObject.class);
            if (!data1.contains(errCode)) {
                String[] list = new String[6];
                JSONArray jsonArry = data2.getJSONArray("l");
                for (int i = 0; i < jsonArry.size(); i++) {
                    list[i] = JSONObject.parseObject(String.valueOf(jsonArry.get(i)), JSONObject.class).getString("n");
                    if (i == 5) {
                        break;
                    }
                }
                return list;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 超级抽奖
     */
    public String superLottery(String qq) throws IOException, ScriptException, NoSuchMethodException, InterruptedException {
        String urlStart = "https://rane.jwetech.com:8080/game/lotteryStart";
        String qqFilePath = dataPath + qq + ".txt";
        File file = new File(qqFilePath);
        if (file.exists()) {
            JSONObject jsonData = raneBase.readUserFile(qq);
            String uid = jsonData.getString("uid");
            String token = jsonData.getString("token");
            int ts = jsonData.getInteger("时序");


            JSONObject jsonSign = new JSONObject();
            jsonSign.put("t", 1);
            jsonSign.put("ios", false);
            jsonSign.put("video", false);
            String data1 = sendRequest(qq, uid, token, ts, urlStart, jsonSign);
            JSONObject data2 = JSONObject.parseObject(data1, JSONObject.class);
            System.out.println(data1);
            if (!data1.contains(errCode)) {
                String jp = getGift(data2.getString("id"));
                System.out.println(jp);
                Thread.sleep(5000);
                if (getEnable(qq)) {
                    return jp;
                } else {
                    return "确认失败";
                }
            } else {
                return data2.getString("errMsg");
            }
        } else {
            return "请先登录";
        }
    }

    /**
     * 普通抽奖
     */
    public String normalLottery(String qq) throws IOException, ScriptException, NoSuchMethodException, InterruptedException {
        String urlStart = "https://rane.jwetech.com:8080/game/lotteryStart";
        String qqFilePath = dataPath + qq + ".txt";
        File file = new File(qqFilePath);
        if (file.exists()) {
            JSONObject jsonData = raneBase.readUserFile(qq);
            String uid = jsonData.getString("uid");
            String token = jsonData.getString("token");
            int ts = jsonData.getInteger("时序");


            JSONObject jsonSign = new JSONObject();
            jsonSign.put("t", 0);
            jsonSign.put("ios", false);
            jsonSign.put("video", false);
            String data1 = sendRequest(qq, uid, token, ts, urlStart, jsonSign);
            JSONObject data2 = JSONObject.parseObject(data1, JSONObject.class);
            if (!data1.contains(errCode)) {
                String jp = getGift(data2.getString("id"));
                System.out.println(jp);
                Thread.sleep(5000);
                if (getEnable(qq)) {
                    return jp;
                } else {
                    return "确认失败";
                }
            } else {
                return data2.getString("errMsg");
            }
        } else {
            return "请先登录";
        }
    }

    /**
     * 确认抽奖
     */
    public boolean getEnable(String qq) throws IOException, ScriptException, NoSuchMethodException {
        String urlEnd = "https://rane.jwetech.com:8080/game/lotteryEnd";
        String qqFilePath = dataPath + qq + ".txt";
        File file = new File(qqFilePath);
        if (file.exists()) {
            JSONObject jsonData = raneBase.readUserFile(qq);
            String uid = jsonData.getString("uid");
            String token = jsonData.getString("token");
            int ts = jsonData.getInteger("时序");

            JSONObject jsonSign = new JSONObject();
            jsonSign.put("ios", false);
            sendRequest(qq, uid, token, ts, urlEnd, jsonSign);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 奖品列表
     */
    String giftCode1 = "160";
    String giftCode2 = "151";
    String giftCode3 = "157";
    String giftCode4 = "153";
    String giftCode5 = "156";
    String giftCode6 = "159";
    String giftCode7 = "154";
    String giftCode8 = "158";
    String giftCode9 = "152";
    String giftCode10 = "155";
    String giftCode11 = "";
    String giftCode12 = "101";
    String giftCode13 = "107";
    String giftCode14 = "103";
    String giftCode15 = "109";
    String giftCode16 = "104";
    String giftCode17 = "105";
    String giftCode18 = "110";
    String giftCode19 = "106";
    String giftCode20 = "102";
    String giftCode21 = "108";
    public String getGift(String id) {
        if (giftCode1.equals(id)) {
            return "一年大会员";
        }
        if (giftCode2.equals(id)) {
            return "5成长值";
        }
        if (giftCode3.equals(id)) {
            return "1月豪华黄钻";
        }
        if (giftCode4.equals(id)) {
            return "200成长值";
        }
        if (giftCode5.equals(id)) {
            return "1年超级会员";
        }
        if (giftCode6.equals(id)) {
            return "1月大会员";
        }
        if (giftCode7.equals(id)) {
            return "1天超级会员";
        }
        if (giftCode8.equals(id)) {
            return "1年豪华黄钻";
        }
        if (giftCode9.equals(id)) {
            return "100成长值";
        }
        if (giftCode10.equals(id)) {
            return "3个月超级会员";
        }
        if (giftCode11.equals(id)) {
            return "'未中奖'";
        }
        if (giftCode12.equals(id)) {
            return "未中奖";
        }
        if (giftCode13.equals(id)) {
            return "14天超级会员";
        }
        if (giftCode14.equals(id)) {
            return "10成长值";
        }
        if (giftCode15.equals(id)) {
            return "未中奖";
        }
        if (giftCode16.equals(id)) {
            return "20成长值";
        }
        if (giftCode17.equals(id)) {
            return "1天超级会员";
        }
        if (giftCode18.equals(id)) {
            return "未中奖";
        }
        if (giftCode19.equals(id)) {
            return "7天超级会员";
        }
        if (giftCode20.equals(id)) {
            return "5成长值";
        }
        if (giftCode21.equals(id)) {
            return "1个月超级会员";
        }
        return null;
    }

    /**
     * 发送请求
     */
    public String sendRequest(String qq, String uid, String token, int ts, String url, JSONObject jsonSign) throws ScriptException, IOException, NoSuchMethodException {
        String jsonSign1 = jsonSign.toString();
        String sign = raneBase.getMd5(uid + token + ts + jsonSign1 + "472770f9e581cffb09349f422af57c5d");

        JSONObject obj = new JSONObject();
        obj.put("uid", uid);
        obj.put("ts", ts);
        obj.put("token", token);
        obj.put("params", jsonSign1);
        obj.put("sign", sign);
        String obj1 = obj.toString();

        System.out.println(sign);
        System.out.println(obj1);

        int rant = raneBase.getRandomNum(10000000, 99999999) + 99999999;
        String ranv = raneBase.encode(rant, obj1);

        raneBase.rewriteTs(qq, ts);
        String getData = raneBase.okHttpClientPost(url, rant, ranv);
        JSONObject jsonDataReplace = JSONObject.parseObject(getData, JSONObject.class);
        int t = jsonDataReplace.getInteger("t");
        String v = jsonDataReplace.getString("v");
        String data1 = raneBase.decode(t, v);
        System.out.println(data1);
        return data1;
    }
}



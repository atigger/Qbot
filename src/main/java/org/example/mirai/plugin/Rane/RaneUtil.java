package org.example.mirai.plugin.Rane;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.example.mirai.plugin.Toolkit.Utils;

import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public class RaneUtil {
    Utils utils = new Utils();
    final String url_get_code = "https://q.qq.com/ide/devtoolAuth/GetLoginCode";
    final String data_path = utils.get_plugins_data_path() + "/rane/";
    RaneBase raneBase = new RaneBase();

    //获取授权链接
    public String get_login_link() {
        JSONObject json_data = JSONObject.parseObject(raneBase.okHttpClient_get(url_get_code));
        int code = json_data.getInteger("code");
        String codes = json_data.getJSONObject("data").getString("code");
        if (code == 0) {
            return codes;
        } else {
            return "获取codes失败";
        }
    }

    //获取登录要用的ticket
    public String get_login_ticket(String codes) throws InterruptedException, IOException {
        if (!codes.contains("失败")) {
            String login_url = "https://h5.qzone.qq.com/qqq/code/" + codes + "?_proxy=1&from=ide";
            System.out.println(login_url);
            String get_login_state_url = "https://q.qq.com/ide/devtoolAuth/syncScanSateGetTicket?code=" + codes;
            for (int i = 0; i < 60; i++) {
                String data = raneBase.okHttpClient_get(get_login_state_url);
                if (data.contains("ok")) {
                    JSONObject json_data = JSONObject.parseObject(data);
                    String ticket = json_data.getJSONObject("data").getString("ticket");
                    String uin = json_data.getJSONObject("data").getString("uin");

                    String qq_file_path = data_path + uin + ".txt";
                    File file = new File(qq_file_path);
                    if (!file.exists()) {
                        file.createNewFile();
                        raneBase.write_user_file(ticket, uin, "", "", 0, 0, 1);
                    } else {
                        raneBase.write_user_file(ticket, uin, "", "", 0, 0, 1);
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

    //获取登录code
    public String get_login_code(String qq) throws IOException {
        String qq_file_path = data_path + qq + ".txt";
        File file = new File(qq_file_path);
        if (file.exists()) {
            JSONObject json_data = raneBase.read_user_file(qq);
            String ticket = json_data.getString("ticket");

            JSONObject data_json = new JSONObject();
            data_json.put("ticket", ticket);
            data_json.put("appid", "1110797565");

            String url = "https://q.qq.com/ide/login";
            JSONObject json_data_replace = JSONObject.parseObject(raneBase.okHttpClient_login_post(url, String.valueOf(data_json)));
            String codes = json_data_replace.getString("code");
            if (!codes.contains("-1")) {
                return codes;
            } else {
                return null;
            }

        } else {
            return null;
        }

    }

    //登录
    public String login(String qq) throws IOException, ScriptException, NoSuchMethodException {
        String qq_file_path = data_path + qq + ".txt";
        File file = new File(qq_file_path);
        if (file.exists()) {
            String codes = get_login_code(qq);
            String sign = raneBase.getMD5("2014" + codes + "472770f9e581cffb09349f422af57c5d");
            JSONObject obj = new JSONObject();
            obj.put("gid", "201");
            obj.put("sdk", 4);
            obj.put("uid", codes);
            obj.put("ios", false);
            obj.put("sign", sign);

            int rant = raneBase.get_random_num(10000000, 99999999) + 99999999;
            String ranv = raneBase.encode(rant, String.valueOf(obj));

            String url = "https://rane.jwetech.com:8080/login/login";
            String get_data = raneBase.okHttpClient_post(url, rant, ranv);
            if (!get_data.contains("{\"code\":-1}")) {
                JSONObject json_data_replace = JSONObject.parseObject(get_data);
                int t = json_data_replace.getInteger("t");
                String v = json_data_replace.getString("v");
                String data1 = raneBase.decode(t, v);
                JSONObject json_data = JSONObject.parseObject(data1);
                if (!json_data.toString().contains("失败")) {
                    String uid = json_data.getString("uid");
                    int money = json_data.getJSONObject("data").getInteger("money");
                    int gfc = json_data.getJSONObject("data").getInteger("gfc");
                    String token = json_data.getString("token");
                    raneBase.rewrite_login_code(qq, uid, token, money, gfc);
                    return "登录成功\nQQ:" + qq + "\n金币:" + money + "\n奖券:" + gfc;
                } else {
                    return "登录失败,请尝试重新登录";
                }
            } else {
                return "登录失败,请尝试重新登录";
            }
        } else {
            return "请先授权";
        }
    }

    //签到
    public String get_sign(String qq) throws IOException, ScriptException, NoSuchMethodException {
        String url = "https://rane.jwetech.com:8080/game/sign";

        String qq_file_path = data_path + qq + ".txt";
        File file = new File(qq_file_path);
        if (file.exists()) {
            JSONObject json_data = raneBase.read_user_file(qq);
            String uid = json_data.getString("uid");
            String token = json_data.getString("token");
            int ts = json_data.getInteger("时序");
            Calendar calendar = Calendar.getInstance();
            int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            if (week == 0) {
                week = 7;
            }
            JSONObject json_sign = new JSONObject();
            json_sign.put("day", week);
            json_sign.put("pid", 0);
            json_sign.put("video", true);
            json_sign.put("t", 0);
            String json_sign1 = json_sign.toString();
            String sign = raneBase.getMD5(uid + token + ts + json_sign1 + "472770f9e581cffb09349f422af57c5d");

            JSONObject obj = new JSONObject();
            obj.put("uid", uid);
            obj.put("ts", ts);
            obj.put("token", token);
            obj.put("params", json_sign1);
            obj.put("sign", sign);
            String obj1 = obj.toString();

            System.out.println(sign);
            System.out.println(obj1);

            int rant = raneBase.get_random_num(10000000, 99999999) + 99999999;
            String ranv = raneBase.encode(rant, obj1);

            raneBase.rewrite_ts(qq, ts);
            String get_data = raneBase.okHttpClient_post(url, rant, ranv);
            JSONObject json_data_replace = JSONObject.parseObject(get_data);
            int t = json_data_replace.getInteger("t");
            String v = json_data_replace.getString("v");
            String data1 = raneBase.decode(t, v);
            JSONObject data2 = JSONObject.parseObject(data1);
            System.out.println(data1);
            if (!data1.contains("errCode")) {
                return "签到成功";
            } else {
                return data2.getString("errMsg");
            }
        } else {
            return "请先登录";
        }
    }

    //超级签到
    public String get_super_sign(String qq) throws IOException, ScriptException, NoSuchMethodException {
        String url = "https://rane.jwetech.com:8080/game/sign";

        String qq_file_path = data_path + qq + ".txt";
        File file = new File(qq_file_path);
        if (file.exists()) {
            JSONObject json_data = raneBase.read_user_file(qq);
            String uid = json_data.getString("uid");
            String token = json_data.getString("token");
            int ts = json_data.getInteger("时序");
            JSONObject json_sign = new JSONObject();
            json_sign.put("day", 8);
            json_sign.put("video", true);
            json_sign.put("t", 2);
            String json_sign1 = json_sign.toString();
            String sign = raneBase.getMD5(uid + token + ts + json_sign1 + "472770f9e581cffb09349f422af57c5d");

            JSONObject obj = new JSONObject();
            obj.put("uid", uid);
            obj.put("ts", ts);
            obj.put("token", token);
            obj.put("params", json_sign1);
            obj.put("sign", sign);
            String obj1 = obj.toString();

            System.out.println(sign);
            System.out.println(obj1);

            int rant = raneBase.get_random_num(10000000, 99999999) + 99999999;
            String ranv = raneBase.encode(rant, obj1);

            raneBase.rewrite_ts(qq, ts);
            String get_data = raneBase.okHttpClient_post(url, rant, ranv);
            JSONObject json_data_replace = JSONObject.parseObject(get_data);
            int t = json_data_replace.getInteger("t");
            String v = json_data_replace.getString("v");
            String data1 = raneBase.decode(t, v);
            JSONObject data2 = JSONObject.parseObject(data1);
            System.out.println(data1);
            if (!data1.contains("errCode")) {
                return "超级签到成功";
            } else {
                return data2.getString("errMsg");
            }
        } else {
            return "请先登录";
        }
    }

    //补签
    public String[] get_reissue_sign(String qq) throws IOException, ScriptException, NoSuchMethodException, InterruptedException {
        String url = "https://rane.jwetech.com:8080/game/sign";

        String qq_file_path = data_path + qq + ".txt";
        File file = new File(qq_file_path);
        if (file.exists()) {
            JSONObject json_data = raneBase.read_user_file(qq);
            String uid = json_data.getString("uid");
            String token = json_data.getString("token");
            int ts = json_data.getInteger("时序");
            Calendar calendar = Calendar.getInstance();
            int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            if (week == 0) {
                week = 7;
            }
            if (week == 1) {
                String[] a = new String[]{"今天是周一，不用补签"};
                return a;
            }
            String[] list = new String[week - 1];

            for (int ii = 0; ii < week - 1; ii++) {
                int i = ii + 1;
                JSONObject json_sign = new JSONObject();
                json_sign.put("day", i);
                json_sign.put("video", false);
                json_sign.put("t", 1);
                String json_sign1 = json_sign.toString();
                String sign = raneBase.getMD5(uid + token + ts + json_sign1 + "472770f9e581cffb09349f422af57c5d");

                JSONObject obj = new JSONObject();
                obj.put("uid", uid);
                obj.put("ts", ts);
                obj.put("token", token);
                obj.put("params", json_sign1);
                obj.put("sign", sign);
                String obj1 = obj.toString();

                System.out.println(sign);
                System.out.println(obj1);

                int rant = raneBase.get_random_num(10000000, 99999999) + 99999999;
                String ranv = raneBase.encode(rant, obj1);

                raneBase.rewrite_ts(qq, ts);
                String get_data = raneBase.okHttpClient_post(url, rant, ranv);
                JSONObject json_data_replace = JSONObject.parseObject(get_data);
                int t = json_data_replace.getInteger("t");
                String v = json_data_replace.getString("v");
                String data1 = raneBase.decode(t, v);
                JSONObject data2 = JSONObject.parseObject(data1);
                System.out.println(data1);
                if (!data1.contains("errCode")) {
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

    //抽奖记录
    public String[] get_gift_list(String qq) throws IOException, ScriptException, NoSuchMethodException {
        String url = "https://rane.jwetech.com:8080/game/myLottery";

        String qq_file_path = data_path + qq + ".txt";
        File file = new File(qq_file_path);
        if (file.exists()) {
            JSONObject json_data = raneBase.read_user_file(qq);
            String uid = json_data.getString("uid");
            String token = json_data.getString("token");
            int ts = json_data.getInteger("时序");

            String json_sign1 = "{}";
            String sign = raneBase.getMD5(uid + token + ts + json_sign1 + "472770f9e581cffb09349f422af57c5d");

            JSONObject obj = new JSONObject();
            obj.put("uid", uid);
            obj.put("ts", ts);
            obj.put("token", token);
            obj.put("params", json_sign1);
            obj.put("sign", sign);
            String obj1 = obj.toString();

            System.out.println(sign);
            System.out.println(obj1);

            int rant = raneBase.get_random_num(10000000, 99999999) + 99999999;
            String ranv = raneBase.encode(rant, obj1);

            raneBase.rewrite_ts(qq, ts);
            String get_data = raneBase.okHttpClient_post(url, rant, ranv);
            JSONObject json_data_replace = JSONObject.parseObject(get_data);
            int t = json_data_replace.getInteger("t");
            String v = json_data_replace.getString("v");
            String data1 = raneBase.decode(t, v);
            JSONObject data2 = JSONObject.parseObject(data1);
            System.out.println(data1);
            if (!data1.contains("errCode")) {
                String[] list = new String[6];
                JSONArray json_arry = data2.getJSONArray("l");
                for (int i = 0; i < json_arry.size(); i++) {
                    list[i] = JSONObject.parseObject(String.valueOf(json_arry.get(i))).getString("n");
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

    //超级抽奖
    public String super_lottery(String qq) throws IOException, ScriptException, NoSuchMethodException, InterruptedException {
        String url_start = "https://rane.jwetech.com:8080/game/lotteryStart";
        String qq_file_path = data_path + qq + ".txt";
        File file = new File(qq_file_path);
        if (file.exists()) {
            JSONObject json_data = raneBase.read_user_file(qq);
            String uid = json_data.getString("uid");
            String token = json_data.getString("token");
            int ts = json_data.getInteger("时序");


            JSONObject json_sign = new JSONObject();
            json_sign.put("t", 1);
            json_sign.put("ios", false);
            json_sign.put("video", false);
            String json_sign1 = json_sign.toString();
            String sign = raneBase.getMD5(uid + token + ts + json_sign1 + "472770f9e581cffb09349f422af57c5d");

            JSONObject obj = new JSONObject();
            obj.put("uid", uid);
            obj.put("ts", ts);
            obj.put("token", token);
            obj.put("params", json_sign1);
            obj.put("sign", sign);
            String obj1 = obj.toString();

            System.out.println(sign);
            System.out.println(obj1);

            int rant = raneBase.get_random_num(10000000, 99999999) + 99999999;
            String ranv = raneBase.encode(rant, obj1);
            raneBase.rewrite_ts(qq, ts);
            String get_data = raneBase.okHttpClient_post(url_start, rant, ranv);
            JSONObject json_data_replace = JSONObject.parseObject(get_data);
            int t = json_data_replace.getInteger("t");
            String v = json_data_replace.getString("v");
            String data1 = raneBase.decode(t, v);
            JSONObject data2 = JSONObject.parseObject(data1);
            System.out.println(data1);
            if (!data1.contains("errCode")) {
                String jp = get_gift(data2.getString("id"));
                System.out.println(jp);
                Thread.sleep(5000);
                if (get_enable(qq)) {
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

    //普通抽奖
    public String normal_lottery(String qq) throws IOException, ScriptException, NoSuchMethodException, InterruptedException {
        String url_start = "https://rane.jwetech.com:8080/game/lotteryStart";
        String qq_file_path = data_path + qq + ".txt";
        File file = new File(qq_file_path);
        if (file.exists()) {
            JSONObject json_data = raneBase.read_user_file(qq);
            String uid = json_data.getString("uid");
            String token = json_data.getString("token");
            int ts = json_data.getInteger("时序");


            JSONObject json_sign = new JSONObject();
            json_sign.put("t", 0);
            json_sign.put("ios", false);
            json_sign.put("video", false);
            String json_sign1 = json_sign.toString();
            String sign = raneBase.getMD5(uid + token + ts + json_sign1 + "472770f9e581cffb09349f422af57c5d");

            JSONObject obj = new JSONObject();
            obj.put("uid", uid);
            obj.put("ts", ts);
            obj.put("token", token);
            obj.put("params", json_sign1);
            obj.put("sign", sign);
            String obj1 = obj.toString();

            System.out.println(sign);
            System.out.println(obj1);

            int rant = raneBase.get_random_num(10000000, 99999999) + 99999999;
            String ranv = raneBase.encode(rant, obj1);
            raneBase.rewrite_ts(qq, ts);
            String get_data = raneBase.okHttpClient_post(url_start, rant, ranv);
            JSONObject json_data_replace = JSONObject.parseObject(get_data);
            int t = json_data_replace.getInteger("t");
            String v = json_data_replace.getString("v");
            String data1 = raneBase.decode(t, v);
            JSONObject data2 = JSONObject.parseObject(data1);
            if (!data1.contains("errCode")) {
                String jp = get_gift(data2.getString("id"));
                System.out.println(jp);
                Thread.sleep(5000);
                if (get_enable(qq)) {
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

    //确认抽奖
    public boolean get_enable(String qq) throws IOException, ScriptException, NoSuchMethodException {
        String url_end = "https://rane.jwetech.com:8080/game/lotteryEnd";
        String qq_file_path = data_path + qq + ".txt";
        File file = new File(qq_file_path);
        if (file.exists()) {
            JSONObject json_data = raneBase.read_user_file(qq);
            String uid = json_data.getString("uid");
            String token = json_data.getString("token");
            int ts = json_data.getInteger("时序");

            JSONObject json_sign = new JSONObject();
            json_sign.put("ios", false);
            String json_sign1 = json_sign.toString();
            String sign = raneBase.getMD5(uid + token + ts + json_sign1 + "472770f9e581cffb09349f422af57c5d");

            JSONObject obj = new JSONObject();
            obj.put("uid", uid);
            obj.put("ts", ts);
            obj.put("token", token);
            obj.put("params", json_sign1);
            obj.put("sign", sign);
            String obj1 = obj.toString();

            System.out.println(sign);
            System.out.println(obj1);

            int rant = raneBase.get_random_num(10000000, 99999999) + 99999999;
            String ranv = raneBase.encode(rant, obj1);
            raneBase.rewrite_ts(qq, ts);
            String get_data = raneBase.okHttpClient_post(url_end, rant, ranv);
            JSONObject json_data_replace = JSONObject.parseObject(get_data);
            int t = json_data_replace.getInteger("t");
            String v = json_data_replace.getString("v");
            String data1 = raneBase.decode(t, v);
            JSONObject data2 = JSONObject.parseObject(data1);
            System.out.println(data1);
            return true;
        } else {
            return false;
        }
    }

    //奖品列表
    public String get_gift(String id) {
        if (id.equals("160")) {
            return "一年大会员";
        }
        if (id.equals("151")) {
            return "5成长值";
        }
        if (id.equals("157")) {
            return "1月豪华黄钻";
        }
        if (id.equals("153")) {
            return "200成长值";
        }
        if (id.equals("156")) {
            return "1年超级会员";
        }
        if (id.equals("159")) {
            return "1月大会员";
        }
        if (id.equals("154")) {
            return "1天超级会员";
        }
        if (id.equals("158")) {
            return "1年豪华黄钻";
        }
        if (id.equals("152")) {
            return "100成长值";
        }
        if (id.equals("155")) {
            return "3个月超级会员";
        }
        if (id.equals("")) {
            return "'未中奖'";
        }
        if (id.equals("101")) {
            return "未中奖";
        }
        if (id.equals("107")) {
            return "14天超级会员";
        }
        if (id.equals("103")) {
            return "10成长值";
        }
        if (id.equals("109")) {
            return "未中奖";
        }
        if (id.equals("104")) {
            return "20成长值";
        }
        if (id.equals("105")) {
            return "1天超级会员";
        }
        if (id.equals("110")) {
            return "未中奖";
        }
        if (id.equals("106")) {
            return "7天超级会员";
        }
        if (id.equals("102")) {
            return "5成长值";
        }
        if (id.equals("108")) {
            return "1个月超级会员";
        }
        return null;
    }
}



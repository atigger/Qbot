package org.example.mirai.plugin.Rane;

import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.example.mirai.plugin.Toolkit.Utils;

import javax.script.*;
import java.io.*;
import java.security.MessageDigest;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class RaneBase {
    Utils utils = new Utils();
    final String js_path = utils.get_plugins_data_path() + "/rane/codes.js";
    final String data_path = utils.get_plugins_data_path() + "/rane/";

    //okhttp get请求
    public String okHttpClient_get(String url) {
        OkHttpClient httpClient = new OkHttpClient();
        //请求超时设置
        httpClient.newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS).build();
        Request getRequest = new Request.Builder()
                .url(url)
                .get()
                .build();
        Call call = httpClient.newCall(getRequest);
        try {
            Response response = call.execute();
            String html = response.body().string();
            return html;
        } catch (IOException e) {
            e.printStackTrace();
            return "{\"code\":-1}";
        }

    }

    //okhttp 获取codes post请求
    public String okHttpClient_login_post(String url, String json) {
        OkHttpClient httpClient = new OkHttpClient();
        //请求超时设置
        httpClient.newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS).build();

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), json);

        Request getRequest = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = httpClient.newCall(getRequest);
        try {
            Response response = call.execute();
            String html = response.body().string();
            return html;
        } catch (IOException e) {
            e.printStackTrace();
            return "{\"code\":\"-1\"}";
        }

    }

    //okhttp 燃鹅操作 post请求
    public String okHttpClient_post(String url, int t, String v) {
        OkHttpClient httpClient = new OkHttpClient();
        FormBody formBody = new FormBody.Builder()
                .add("t", String.valueOf(t))
                .add("v", v)
                .build();
        //请求超时设置
        httpClient.newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS).build();


        Request getRequest = new Request.Builder()
                .url(url)
                .addHeader("referer", "https://appservice.qq.com/1110797565/1.1.5/page-frame.html")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("User-Agent", "Mozilla/5.0(Linux)AppleWebKit/537.36(KHTML;likeGecko)Version/4.0Chrome/86.0.4240.185MobileSafari/537.36QQ/8.5.5.5105V1_AND_SQ_8.5.5_1630_YYB_DQQ/MiniApp")
                .addHeader("Accept-Encoding", "gzip")
                .post(formBody)
                .build();
        Call call = httpClient.newCall(getRequest);
        try {
            Response response = call.execute();
            if (response.code() == 200) {
                String html = response.body().string();
                return html;
            } else {
                return "{\"code\":-1}";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "{\"code\":-1}";
        }

    }

    //写文件
    public void write_user_file(String ticket, String uin, String uid, String token, int money, int gfc, int sx) throws IOException {
        String qq_file_path = data_path + uin + ".txt";
        JSONObject json = new JSONObject();
        json.put("ticket", ticket);
        json.put("qq", uin);
        json.put("uid", uid);
        json.put("token", token);
        json.put("金币", money);
        json.put("奖券", gfc);
        json.put("时序", sx);
        BufferedWriter out = new BufferedWriter(new FileWriter(qq_file_path));
        out.write(String.valueOf(json));
        out.close();
    }

    //覆盖写入登录code
    public void rewrite_login_code(String qq, String uid, String token, int money, int gfc) throws IOException {
        JSONObject strat_file_data = read_user_file(qq);
        strat_file_data.put("uid", uid);
        strat_file_data.put("token", token);
        strat_file_data.put("金币", money);
        strat_file_data.put("奖券", gfc);
        strat_file_data.put("时序", 1);
        strat_file_data.put("跑分状态", 0);
        String qq_file_path = data_path + qq + ".txt";
        BufferedWriter out = new BufferedWriter(new FileWriter(qq_file_path));
        out.write(String.valueOf(strat_file_data));
        out.close();
    }

    //覆盖写入时序
    public void rewrite_ts(String qq, int ts) throws IOException {
        ts++;
        JSONObject strat_file_data = read_user_file(qq);
        strat_file_data.put("时序", ts);
        String qq_file_path = data_path + qq + ".txt";
        BufferedWriter out = new BufferedWriter(new FileWriter(qq_file_path));
        out.write(String.valueOf(strat_file_data));
        out.close();
    }

    //读文件
    public JSONObject read_user_file(String qq) throws IOException {
        String qq_file_path = data_path + qq + ".txt";
        BufferedReader in = new BufferedReader(new FileReader(qq_file_path));
        String str;
        String to_json_str = null;
        while ((str = in.readLine()) != null) {
            to_json_str = str;
        }
        JSONObject json_data = JSONObject.parseObject(to_json_str);
        return json_data;
    }

    //获取随机数
    public int get_random_num(int min, int max) {
        Random random = new Random();
        int s = random.nextInt(max) % (max - min + 1) + min;
        return s;
    }

    //加密
    public String encode(int t, String v) throws FileNotFoundException, ScriptException, NoSuchMethodException {
        // 获取JS执行引擎
        ScriptEngine se = new ScriptEngineManager().getEngineByName("javascript");
        // 获取变量
//        Bindings bindings = se.createBindings();
//        bindings.put("number", 3);
//        se.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
        se.eval(new FileReader(js_path));
        // 是否可调用
        if (se instanceof Invocable) {
            Invocable in = (Invocable) se;
            String result = (String) in.invokeFunction("encode", t, v);
            return result;
        }
        return null;
    }

    //解密
    public String decode(int t, String v) throws FileNotFoundException, ScriptException, NoSuchMethodException {
        // 获取JS执行引擎
        ScriptEngine se = new ScriptEngineManager().getEngineByName("javascript");
        // 获取变量
        Bindings bindings = se.createBindings();
        bindings.put("number", 3);
        se.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
        se.eval(new FileReader(js_path));
        // 是否可调用
        if (se instanceof Invocable) {
            Invocable in = (Invocable) se;
            String result = (String) in.invokeFunction("decode", t, v);
            return result;
        }
        return null;
    }

    //获取MD5
    public String getMD5(String dataStr) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(dataStr.getBytes("UTF8"));
            byte s[] = m.digest();
            String result = "";
            for (int i = 0; i < s.length; i++) {
                result += Integer.toHexString((0x000000FF & s[i]) | 0xFFFFFF00).substring(6);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}

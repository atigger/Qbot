package org.example.mirai.plugin.rane;

import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.example.mirai.plugin.toolkit.Utils;

import javax.script.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * RaneBase class
 *
 * @author 649953543@qq.com
 * @date 2021/09/22
 */

public class RaneBase {
    Utils utils = new Utils();
    final String js_path = utils.getPluginsDataPath() + "/rane/codes.js";
    final String data_path = utils.getPluginsDataPath() + "/rane/";

    /**
     *
     * okhttp get请求
     *
     */
    public String okHttpClientGet(String url) {
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
            return Objects.requireNonNull(response.body()).string();
        } catch (IOException e) {
            e.printStackTrace();
            return "{\"code\":-1}";
        }

    }


    /**
     *
     * okhttp 获取codes post请求
     *
     */
    public String okHttpClientLoginPost(String url, String json) {
        OkHttpClient httpClient = new OkHttpClient();
        //请求超时设置
        httpClient.newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS).build();


        RequestBody body = RequestBody.create(json,
                MediaType.parse("application/json"));

        Request getRequest = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = httpClient.newCall(getRequest);
        try {
            Response response = call.execute();
            return Objects.requireNonNull(response.body()).string();
        } catch (IOException e) {
            e.printStackTrace();
            return "{\"code\":\"-1\"}";
        }

    }

    /**
     *
     * okhttp 燃鹅操作 post请求
     *
     */
    public String okHttpClientPost(String url, int t, String v) {
        int statusCode = 200;
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
            if (response.code() == statusCode) {
                return Objects.requireNonNull(response.body()).string();
            } else {
                return "{\"code\":-1}";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "{\"code\":-1}";
        }

    }

    /**
     *
     * 写文件
     *
     */
    public void writeUserFile(String ticket, String uin, String uid, String token, int money, int gfc, int sx) throws IOException {
        String qqFilePath = data_path + uin + ".txt";
        JSONObject json = new JSONObject();
        json.put("ticket", ticket);
        json.put("qq", uin);
        json.put("uid", uid);
        json.put("token", token);
        json.put("金币", money);
        json.put("奖券", gfc);
        json.put("时序", sx);
        BufferedWriter out = new BufferedWriter(new FileWriter(qqFilePath));
        out.write(String.valueOf(json));
        out.close();
    }

    /**
     *
     * 覆盖写入登录code
     *
     */
    public void rewriteLoginCode(String qq, String uid, String token, int money, int gfc) throws IOException {
        JSONObject startFileData = readUserFile(qq);
        startFileData.put("uid", uid);
        startFileData.put("token", token);
        startFileData.put("金币", money);
        startFileData.put("奖券", gfc);
        startFileData.put("时序", 1);
        startFileData.put("跑分状态", 0);
        String qqFilePath = data_path + qq + ".txt";
        BufferedWriter out = new BufferedWriter(new FileWriter(qqFilePath));
        out.write(String.valueOf(startFileData));
        out.close();
    }

    /**
     *
     * 覆盖写入时序
     *
     */
    public void rewriteTs(String qq, int ts) throws IOException {
        ts++;
        JSONObject startFileData = readUserFile(qq);
        startFileData.put("时序", ts);
        String qqFilePath = data_path + qq + ".txt";
        BufferedWriter out = new BufferedWriter(new FileWriter(qqFilePath));
        out.write(String.valueOf(startFileData));
        out.close();
    }

    /**
     *
     * 读文件
     *
     */
    public JSONObject readUserFile(String qq) throws IOException {
        String qqFilePath = data_path + qq + ".txt";
        BufferedReader in = new BufferedReader(new FileReader(qqFilePath));
        String str;
        String toJsonStr = null;
        while ((str = in.readLine()) != null) {
            toJsonStr = str;
        }
        return JSONObject.parseObject(toJsonStr);
    }

    /**
     *
     * 获取随机数
     *
     */
    public int getRandomNum(int min, int max) {
        Random random = new Random();
        return random.nextInt(max) % (max - min + 1) + min;
    }

    /**
     *
     * 加密
     *
     */
    public String encode(int t, String v) throws FileNotFoundException, ScriptException, NoSuchMethodException {
        // 获取JS执行引擎
        ScriptEngine se = new ScriptEngineManager().getEngineByName("javascript");
        // 获取变量
        se.eval(new FileReader(js_path));
        // 是否可调用
        if (se instanceof Invocable) {
            Invocable in = (Invocable) se;
            return (String) in.invokeFunction("encode", t, v);
        }
        return null;
    }

    /**
     *
     * 解密
     *
     */
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
            return (String) in.invokeFunction("decode", t, v);
        }
        return null;
    }

    /**
     *
     * 获取MD5
     *
     */
    public String getMd5(String dataStr) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(dataStr.getBytes(StandardCharsets.UTF_8));
            byte[] s = m.digest();
            StringBuilder result = new StringBuilder();
            for (byte b : s) {
                result.append(Integer.toHexString((0x000000FF & b) | 0xFFFFFF00).substring(6));
            }
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}

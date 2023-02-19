package org.qbot.music;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.qbot.toolkit.Setting;
import org.qbot.toolkit.Utils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * NeteaseCloudMusicTask class
 *
 * @author 649953543@qq.com
 * @date 2023/2/18
 */
public class NeteaseCloudMusicTask {
    private final Utils utils = new Utils();

    private final String baseUrl = Setting.getMusicAPIURL();

    private String _csrf = "";

    private String MUSIC_U = "";

    private String musicCookie = "";

    /**
     * 超时时间
     */
    private final int TIMEOUT = 100;

    /**
     * 听歌次数
     */
    private final int LISTEN_COUNT = 350;

    /**
     * 成功状态码
     */
    private final int SUCCESS_CODE = 200;

    /**
     * 获取二维码key
     */
    public String getQrKey() {
        return JSONObject.parseObject(utils.okHttpClientGetMusic(baseUrl + "/login/qr/key?timerstamp=" + System.currentTimeMillis(), "")).getJSONObject("data").getString("unikey");
    }

    /**
     * 生成二维码
     */
    public String getQrCode(String qrKey) {
        return JSONObject.parseObject(utils.okHttpClientGetMusic(baseUrl + "/login/qr/create?key=" + qrKey + "&qrimg=true&timerstamp=" + System.currentTimeMillis(), "")).getJSONObject("data").getString("qrimg");
    }

    /**
     * 获取登录状态
     */
    public String getCheckLogin(String qrKey) {
        try {
            for (int i = 0; i < TIMEOUT; i++) {
                JSONObject jsonObject = JSONObject.parseObject(utils.okHttpClientGetMusic(baseUrl + "/login/qr/check?key=" + qrKey + "&timerstamp=" + System.currentTimeMillis(), ""));
                int code = jsonObject.getIntValue("code");
                System.out.println("" + jsonObject);
                if (code != 801) {
                    if (code == 803) {
                        String cookie = jsonObject.getString("cookie");
                        _csrf = cookie.substring(cookie.indexOf("_csrf=") + 6, cookie.indexOf(";", cookie.indexOf("_csrf=") + 6));
                        MUSIC_U = cookie.substring(cookie.indexOf("MUSIC_U=") + 8, cookie.indexOf(";", cookie.indexOf("MUSIC_U=") + 8));
                        musicCookie = "__csrf=" + _csrf + "; MUSIC_U=" + MUSIC_U;
                        return musicCookie;
                    }
                    if (code == 800) {
                        return jsonObject.getString("message");
                    }
                    if (code == 802) {
                        continue;
                    }
                    break;
                }
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            System.out.println(e + "");
        }
        return "已过期请重新获取1";
    }

    /**
     * 获取我喜欢列表
     *
     * @param cookie
     * @return
     */
    public JSONArray getLikeList(String cookie) {
        String url = baseUrl + "/likelist?timerstamp=" + System.currentTimeMillis();
        JSONObject jsonresult = JSONObject.parseObject(utils.okHttpClientGetMusic(url, cookie));
        if (jsonresult.getInteger("code") == SUCCESS_CODE) {
            return jsonresult.getJSONArray("ids");
        }
        return null;
    }

    /**
     * 获取歌单ID
     */
    public String getPlaylistId(String cookie, String userId) {
        String url = baseUrl + "/user/playlist?uid=" + userId + "&timerstamp=" + System.currentTimeMillis();
        JSONObject jsonresult = JSONObject.parseObject(utils.okHttpClientGetMusic(url, cookie));
        if (jsonresult.getInteger("code") == SUCCESS_CODE) {
            return jsonresult.getJSONArray("playlist").getJSONObject(0).getString("id");
        }
        return null;
    }

    /**
     * 获取歌单歌曲ID
     */
    public String getPlaylistSongId(String cookie, String Id) {
        String url = baseUrl + "/playlist/track/all?id=" + Id + "&limit=10&offset=0&timerstamp=" + System.currentTimeMillis();
        JSONObject jsonresult = JSONObject.parseObject(utils.okHttpClientGetMusic(url, cookie));
        if (jsonresult.getInteger("code") == SUCCESS_CODE) {
            return jsonresult.getJSONArray("privileges").getJSONObject(0).getString("id");
        }
        return null;
    }

    /**
     * 获取心动模式歌曲
     */
    public JSONArray getIntelligenceList(String cookie, String SongId, String ListId) {
        String url = baseUrl + "/playmode/intelligence/list?id=" + SongId + "&pid=" + ListId + "&timerstamp=" + System.currentTimeMillis();
        JSONObject jsonresult = JSONObject.parseObject(utils.okHttpClientGetMusic(url, cookie));
        if (jsonresult.getInteger("code") == SUCCESS_CODE) {
            return jsonresult.getJSONArray("data");
        }
        return null;
    }


    /**
     * 刷音乐
     *
     * @param cookie
     * @return
     */
    public String brushMusic(long senderId, String cookie, String userId) throws InterruptedException, IOException {
        String likeListId = getPlaylistId(cookie, userId);
        if (likeListId == null) {
            MusicMessageDeal.setFile(senderId, "ExitTag", "false");
            MusicMessageDeal.setFile(senderId, "Lock", "未启动");
            return "获取歌单ID失败";
        } else {
            System.out.println("QQ:" + senderId + "----获取歌单ID成功:" + likeListId);
        }
        String songId1 = getPlaylistSongId(cookie, likeListId);
        if (songId1 == null) {
            MusicMessageDeal.setFile(senderId, "ExitTag", "false");
            MusicMessageDeal.setFile(senderId, "Lock", "未启动");
            return "获取歌曲ID失败";
        } else {
            System.out.println("QQ:" + senderId + "----获取歌曲ID成功:" + songId1);
        }
        JSONArray likeListArray = getIntelligenceList(cookie, songId1, likeListId);
        if (likeListArray == null) {
            MusicMessageDeal.setFile(senderId, "ExitTag", "false");
            MusicMessageDeal.setFile(senderId, "Lock", "未启动");
            return "获取心动模式歌曲失败";
        } else {
            System.out.println("QQ:" + senderId + "----获取心动模式歌曲成功,歌曲数:" + likeListArray.size());
        }
        Set<String> count = new HashSet<>();
        int tag = 0;
        System.out.println("QQ:" + senderId + "----刷歌次数:" + LISTEN_COUNT);
        int num = Integer.parseInt(Objects.requireNonNull(MusicMessageDeal.getFile(senderId, "NumberOfBrushes")));
        while (count.size() < LISTEN_COUNT) {
            String songId = songId1;
            for (int i = 0; i < likeListArray.size(); i++) {
                if (Objects.equals(MusicMessageDeal.getFile(senderId, "ExitTag"), "true")) {
                    MusicMessageDeal.setFile(senderId, "NumberOfBrushes", String.valueOf((num + count.size())));
                    MusicMessageDeal.setFile(senderId, "ExitTag", "false");
                    MusicMessageDeal.setFile(senderId, "Lock", "未启动");
                    return "刷歌完成：共刷歌" + count.size() + "首";
                }
                songId = likeListArray.getJSONObject(i).getString("id");
                JSONObject result = listenMusic(cookie, songId);
                try {
                    if ("success".equals(result.getString("data"))) {
                        count.add(songId);
                        if (count.size() > LISTEN_COUNT) {
                            break;
                        }
                        MusicMessageDeal.setFile(senderId, "NumberOfBrushes", String.valueOf((num + count.size())));
                    }
                } catch (Exception ignored) {
                }
                System.out.println("QQ:" + senderId + "----第" + count.size() + "首----歌曲ID:" + songId + "----结果:" + result);
                Thread.sleep(Utils.getRandomNum(1000, 10000));
                //防止死循环
                if (tag > LISTEN_COUNT * 2) {
                    MusicMessageDeal.setFile(senderId, "ExitTag", "false");
                    MusicMessageDeal.setFile(senderId, "Lock", "未启动");
                    return "刷歌完成：共刷歌" + count.size() + "首";
                }
                tag++;
            }
            likeListArray = getIntelligenceList(cookie, songId, likeListId);
            if (likeListArray == null) {
                MusicMessageDeal.setFile(senderId, "ExitTag", "false");
                MusicMessageDeal.setFile(senderId, "Lock", "未启动");
                return "更新心动模式歌曲失败,刷歌完成：共刷歌" + count.size() + "首";
            } else {
                System.out.println("QQ:" + senderId + "----更新心动模式歌曲成功,歌曲数:" + likeListArray.size());
            }
        }
        MusicMessageDeal.setFile(senderId, "ExitTag", "false");
        MusicMessageDeal.setFile(senderId, "Lock", "未启动");
        return "刷歌完成：共刷歌" + count.size() + "首";
    }

    /**
     * 听歌打卡
     */
    public JSONObject listenMusic(String cookie, String songId) {
        int songTime = getSongDetailTime(cookie, songId) - 5;
        if (songTime != 0) {
            try {
                return JSONObject.parseObject(utils.okHttpClientGetMusic(baseUrl + "/scrobble?id=" + songId + "&time=" + songTime, cookie));
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    /**
     * 获取歌曲时长
     */
    public int getSongDetailTime(String cookie, String songId) {
        try {
            JSONObject jsonObject = JSONObject.parseObject(utils.okHttpClientGetMusic(baseUrl + "/song/detail?ids=" + songId, cookie)).getJSONArray("songs").getJSONObject(0);
            System.out.println("歌曲ID：" + songId + "--歌曲名:" + jsonObject.getString("name") + "--时长:" + jsonObject.getInteger("dt") / 1000);
            return jsonObject.getInteger("dt") / 1000;
        } catch (Exception e) {
            System.out.println("歌曲ID：" + songId + "--获取歌曲时长失败");
            return 5;
        }
    }


    /**
     * 签到
     */
    public String sign(String cookie) {
        try {
            //手机签到
            JSONObject phoneJsonObject = JSONObject.parseObject(utils.okHttpClientGetMusic(baseUrl + "/daily_signin?type=0", cookie));
            String msg;
            if (phoneJsonObject.getInteger("code") == SUCCESS_CODE) {
                msg = "手机签到成功，获得积分:" + phoneJsonObject.getInteger("point") + "\n";
            } else {
                msg = "手机签到失败:" + phoneJsonObject.getString("msg") + "\n";
            }
            //PC签到
            JSONObject pCJsonObject = JSONObject.parseObject(utils.okHttpClientGetMusic(baseUrl + "/daily_signin?type=1", cookie));
            if (pCJsonObject.getInteger("code") == SUCCESS_CODE) {
                msg += "PC签到成功，获得积分:" + pCJsonObject.getInteger("point");
            } else {
                msg += "PC签到失败:" + pCJsonObject.getString("msg");
            }
            System.out.println(msg);
            return msg;
        } catch (Exception e) {
            return "签到失败";
        }
    }

    /**
     * 云贝签到
     */
    public String yunbeiSign(String cookie) {
        try {
            JSONObject jsonObject = JSONObject.parseObject(utils.okHttpClientGetMusic(baseUrl + "/yunbei/sign?timerstamp=" + System.currentTimeMillis(), cookie));
            String msg;
            if (jsonObject.getInteger("code") == SUCCESS_CODE) {
                msg = "云贝签到成功，获得云贝:" + jsonObject.getInteger("point");
            } else {
                msg = "云贝签到失败:" + jsonObject.getString("msg");
            }
            System.out.println(msg);
            return msg;
        } catch (Exception e) {
            return "云贝签到失败";
        }
    }

    /**
     * 领取云贝
     */
    public void yunbeiGet(String cookie) {
        JSONObject jsonObject = JSONObject.parseObject(utils.okHttpClientGetMusic(baseUrl + "/yunbei/get?timerstamp=" + System.currentTimeMillis(), cookie));
        if (jsonObject.getInteger("code") == SUCCESS_CODE) {
            System.out.println("领取云贝成功，获得云贝:" + jsonObject.getJSONObject("data").getInteger("point"));
        } else {
            System.out.println("领取云贝失败:" + jsonObject.getString("msg"));
        }

    }

    /**
     * 完成云贝任务
     */
    public void yunbeiTask(String cookie) {
        JSONObject jsonObject = JSONObject.parseObject(utils.okHttpClientGetMusic(baseUrl + "/yunbei/task?timerstamp=" + System.currentTimeMillis(), cookie));
        if (jsonObject.getInteger("code") == SUCCESS_CODE) {
            System.out.println("完成云贝任务成功，获得云贝:" + jsonObject.getJSONObject("data").getInteger("point"));
        } else {
            System.out.println("完成云贝任务失败:" + jsonObject.getString("msg"));
        }
    }

    /**
     * 云贝推歌
     */
    public void yunbeiPush(String cookie, String songId) {
        JSONObject jsonObject = JSONObject.parseObject(utils.okHttpClientGetMusic(baseUrl + "/yunbei/rcmd/song?id=" + songId, cookie));
        if (jsonObject.getInteger("code") == SUCCESS_CODE) {
            System.out.println("云贝推歌成功");
        } else {
            System.out.println("云贝推歌失败:" + jsonObject.getString("message"));
        }
    }

    /**
     * 获取用户信息
     */
    public JSONObject getUserInfo(String cookie) {
        JSONObject jsonObject = JSONObject.parseObject(utils.okHttpClientGetMusic(baseUrl + "/user/account?timerstamp=" + System.currentTimeMillis(), cookie));
        if (jsonObject.getInteger("code") == SUCCESS_CODE) {
            String nickname = jsonObject.getJSONObject("profile").getString("nickname");
            String userid = jsonObject.getJSONObject("profile").getString("userId");
            System.out.println("用户信息:用户名:" + nickname + " 用户ID:" + userid);
            jsonObject.put("nickname", nickname);
            jsonObject.put("userId", userid);
            return jsonObject;
        } else {
            System.out.println("获取用户信息失败:" + jsonObject.getString("msg"));
        }
        return null;
    }
}

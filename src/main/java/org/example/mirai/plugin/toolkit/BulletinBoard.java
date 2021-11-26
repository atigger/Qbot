package org.example.mirai.plugin.toolkit;

/**
 * BulletinBoard class
 *
 * @author 649953543@qq.com
 * @date 2021/11/26
 */
public class BulletinBoard {
    Utils utils = new Utils();

    public BulletinBoard() {
        String url = "http://47.240.4.177/gg.php";
        System.out.println(utils.okHttpClientGet(url));

    }
}

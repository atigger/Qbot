package org.qbot.toolkit;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * QRCodeUtil class
 *
 * @author 649953543@qq.com
 * @date 2021/09/22
 */

public class QrCodeUtil {

    private static final Logger log = LoggerFactory.getLogger(QrCodeUtil.class);

    /**
     * 生成二维码
     *
     * @param text 内容，可以是链接或者文本
     * @param path 生成的二维码位置
     */
    public static boolean encodeQrCode(String text, String path) {
       return encodeQrCode(text, path, null, null, null);
    }

    /**
     * 生成二维码
     *
     * @param text   内容，可以是链接或者文本
     * @param path   生成的二维码位置
     * @param width  宽度，默认300
     * @param height 高度，默认300
     * @param format 生成的二维码格式，默认png
     */
    public static boolean encodeQrCode(String text, String path, Integer width, Integer height, String format) {
        try {

            // 得到文件对象
            File file = new File(path);
            // 判断目标文件所在的目录是否存在
            if (!file.getParentFile().exists()) {
                // 如果目标文件所在的目录不存在，则创建父目录
                log.info("目标文件所在目录不存在，准备创建它！");
                if (!file.getParentFile().mkdirs()) {
                    log.info("创建目标文件所在目录失败！");
                    return false;
                }
            }

            // 宽
            if (width == null) {
                width = 450;
            }
            // 高
            if (height == null) {
                height = 450;
            }
            // 图片格式
            if (format == null) {
                format = "png";
            }

            // 设置字符集编码
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            // 生成二维码矩阵
            BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hints);
            // 二维码路径
            Path outputPath = Paths.get(path);
            // 写入文件
            MatrixToImageWriter.writeToPath(bitMatrix, format, outputPath);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }
}

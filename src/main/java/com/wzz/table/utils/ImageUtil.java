package com.wzz.table.utils;

import net.coobird.thumbnailator.Thumbnails;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

@Component
public class ImageUtil {

    private static final Logger log = LogManager.getLogger(ImageUtil.class);

    /**
     * 【原始方法】处理上传的 MultipartFile
     */
    public byte[] cropImage(MultipartFile imageFile, int targetHeight) throws IOException {
        BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(imageFile.getBytes()));
        // 从文件名中获取原始格式，例如 "png", "jpg"
        String formatName = imageFile.getOriginalFilename().substring(imageFile.getOriginalFilename().lastIndexOf(".") + 1);
        return crop(originalImage, targetHeight, formatName);
    }

    /**
     * 【新增重载方法】处理本地的 File 对象
     */
    public byte[] cropImage(File imageFile, int targetHeight) throws IOException {
        if (!imageFile.exists()) {
            throw new IOException("文件不存在: " + imageFile.getAbsolutePath());
        }
        BufferedImage originalImage = ImageIO.read(imageFile);
        String formatName = imageFile.getName().substring(imageFile.getName().lastIndexOf(".") + 1);
        return crop(originalImage, targetHeight, formatName);
    }

    /**
     * 【新增重载方法】处理本地的文件路径 (String)
     */
    public byte[] cropImage(String imagePath, int targetHeight) throws IOException {
        File imageFile = new File(imagePath);
        return cropImage(imageFile, targetHeight);
    }

    /**
     * 【私有核心方法】封装通用的裁剪逻辑
     * @param originalImage 原始图片
     * @param targetHeight  目标高度
     * @param formatName    输出格式 (如 "png", "jpg")
     * @return 裁剪后的图片字节数组
     */
    private byte[] crop(BufferedImage originalImage, int targetHeight, String formatName) throws IOException {
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        if (targetHeight <= 0 || targetHeight > originalHeight) {
            throw new IllegalArgumentException("指定的高度无效。必须大于0且小于等于原始高度 (" + originalHeight + ")。");
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // 使用 Thumbnailator 进行裁剪
        Thumbnails.of(originalImage)
                // sourceRegion(x, y, width, height) 定义裁剪区域
                .sourceRegion(0, 0, originalWidth, targetHeight)
                // size(width, height) 定义输出图像大小，这里保持原尺寸
                .size(originalWidth, targetHeight)
                // 指定输出格式，非常重要！
                .outputFormat(formatName)
                .toOutputStream(baos);
        Base64.Encoder encoder = Base64.getEncoder();


        //log.info("<UNK>" +new String( encoder.encode(baos.toByteArray())));
        return baos.toByteArray();
    }
}
package com.yslc.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.graphics.Bitmap;
import android.os.Environment;
import android.os.StatFs;

/**
 * File工具类
 *
 * @author HH
 */
public class FileUtil {
    /**
     * 检测SD卡是否可用
     *
     * @return true为可用，否则为不可用
     */
    public static boolean sdCardIsAvailable() {
        String status = Environment.getExternalStorageState();
        return status.equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取SD卡路径
     *
     * @return
     */
    public static String getSdCardPath() {
        return Environment.getExternalStorageDirectory().toString();
    }

    /**
     * 根据文件路径创建文件夹和文件
     * <p>
     * eg: /userImg/userImg.jpg
     * 先依据/拆分为字符串数组，然后逐级创建文件夹和文件
     */
    public static File creatMoreFiles(String path) {
        String sdPath = getSdCardPath();
        StringBuilder sb = new StringBuilder(sdPath);
        String[] filesArr = path.split("/");
        File files;

        //逐级创建文件夹
        for (String filesName : filesArr) {
            if (filesArr.length < 1 || filesName.contains(".")) {
                continue;
            }

            sb.append(File.separator).append(filesName);
            files = new File(sb.toString());
            if (!files.exists()) {
                files.mkdirs();
            }
        }

        //创建文件
        return creatFile(path);
    }

    /**
     * 创建文件
     *
     * @param fileName
     */
    public static File creatFile(String fileName) {
        String path = getSdCardPath() + fileName;
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * 删除某个文件夹下全部文件
     *
     * @param filePath 文件夹路径
     */
    public static void deleteAllFile(String filePath) {
        File file = new File(getSdCardPath() + File.separator + filePath);
        File[] fileArr = file.listFiles();
        if(fileArr == null){
            return;
        }
        for (File files : fileArr) {
            files.delete();
        }
    }

    /**
     * 获取SD卡剩余大小
     *
     * @return 使用大小
     */
    public static long sdRemainSize() {
        File file = new File(getSdCardPath());
        StatFs statFs = new StatFs(file.getPath());
        // 获取单个数据块的大小(Byte)
        long blockSize = statFs.getBlockSize();
        // 空闲的数据块的数量
        long freeBlocks = statFs.getAvailableBlocks();
        // 返回SD卡空闲大小
        return (blockSize * freeBlocks) / 1024 / 1024; // 单位KB
    }

    /**
     * 将Bitmap写入SD卡中
     *
     * @param bm
     * @param filesName 文件名称
     */
    public static void saveFile(Bitmap bm, String filesName) {
        saveBitmapToCard(filesName, bm);
    }

    public static void saveBitmapToCard(String fileName,
                                        Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        inputstreamToFile(is, fileName);
    }

    /**
     * InputStream to file
     *
     * @param ins
     * @param fileName
     */
    public static File inputstreamToFile(InputStream ins, String fileName) {
        creatMoreFiles(fileName);
        OutputStream os;
        try {
            os = new FileOutputStream(getSdCardPath() + fileName);
            int bytesRead;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return creatFile(fileName);
    }

    /**
     * 获取照片InputStream
     */
    public static InputStream crieImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return new ByteArrayInputStream(baos.toByteArray());
    }
}

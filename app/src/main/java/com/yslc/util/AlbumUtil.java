package com.yslc.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.Images.Media;

import com.yslc.bean.AlbumModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlbumUtil {
    private static final int IMAGE_MIN_SIZE = 10 * 1024;
    private ContentResolver resolver;

    public AlbumUtil(Context context) {
        resolver = context.getContentResolver();
    }

    /**
     * 获取最近照片列表
     */
    public List<String> getCurrent() {
        Cursor cursor = resolver.query(Media.EXTERNAL_CONTENT_URI, new String[]{ImageColumns.DATA,
                ImageColumns.DATE_ADDED, ImageColumns.SIZE}, null, null, ImageColumns.DATE_ADDED);
        if (cursor == null || !cursor.moveToNext())
            return new ArrayList<>();
        List<String> photos = new ArrayList<>();
        while (cursor.moveToNext()) {
            if (cursor.getLong(cursor.getColumnIndex(ImageColumns.SIZE)) > IMAGE_MIN_SIZE) {
                photos.add(cursor.getString(cursor.getColumnIndex(ImageColumns.DATA)));
            }
        }
        cursor.close();
        return photos;
    }

    /**
     * 获取所有相册列表
     */
    public List<AlbumModel> getAlbums() {
        List<AlbumModel> albums = new ArrayList<>();
        Map<String, AlbumModel> map = new HashMap<>();
        Cursor cursor = resolver.query(Media.EXTERNAL_CONTENT_URI, new String[]{ImageColumns.DATA,
                ImageColumns.BUCKET_DISPLAY_NAME, ImageColumns.SIZE}, null, null, null);
        if (cursor == null || !cursor.moveToNext())
            return new ArrayList<>();
        cursor.moveToLast();
        AlbumModel current = new AlbumModel("最近照片", 0, cursor.getString(cursor.getColumnIndex(ImageColumns.DATA)), true); // "最近照片"相册
        albums.add(current);
        do {
            if (cursor.getInt(cursor.getColumnIndex(ImageColumns.SIZE)) < 1024 * 10)
                continue;

            current.increaseCount();
            String name = cursor.getString(cursor.getColumnIndex(ImageColumns.BUCKET_DISPLAY_NAME));
            if (map.keySet().contains(name))
                map.get(name).increaseCount();
            else {
                AlbumModel album = new AlbumModel(name, 1, cursor.getString(cursor.getColumnIndex(ImageColumns.DATA)));
                map.put(name, album);
                albums.add(album);
            }
        } while (cursor.moveToPrevious());

        cursor.close();
        return albums;
    }

    /**
     * 获取对应相册下的照片
     */
    public List<String> getAlbum(String name) {
        if (name.equals("最近照片")) {
            return getCurrent();
        }

        Cursor cursor = resolver.query(Media.EXTERNAL_CONTENT_URI, new String[]{ImageColumns.BUCKET_DISPLAY_NAME,
                        ImageColumns.DATA, ImageColumns.DATE_ADDED, ImageColumns.SIZE}, "bucket_display_name = ?",
                new String[]{name}, ImageColumns.DATE_ADDED);
        if (cursor == null || !cursor.moveToNext())
            return new ArrayList<>();
        List<String> photos = new ArrayList<>();
        cursor.moveToLast();
        do {
            if (cursor.getLong(cursor.getColumnIndex(ImageColumns.SIZE)) > IMAGE_MIN_SIZE) {
                photos.add(cursor.getString(cursor.getColumnIndex(ImageColumns.DATA)));
            }
        } while (cursor.moveToPrevious());

        cursor.close();
        return photos;
    }
}

package com.easing.commons.android.media;

import android.webkit.MimeTypeMap;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MediaType {

    public static final Map<String, String> MIME_TYPE_MAP = new HashMap();
    public static final String TYPE_ALL = "*/*";
    public static final String TYPE_TEXT = "text/*";
    public static final String TYPE_IMAGE = "image/*";

    static {
        MIME_TYPE_MAP.put("all", "*/*");
        MIME_TYPE_MAP.put("text", "text/*");
        MIME_TYPE_MAP.put("image", "image/*");
        MIME_TYPE_MAP.put("audio", "audio/*");
        MIME_TYPE_MAP.put("video", "video/*");
        MIME_TYPE_MAP.put("app", "application/*");
    }

    public static String getMIMEType(String path) {
        String name = new File(path).getName().toLowerCase();
        int index = name.lastIndexOf(".");
        if (index > 0) {
            String ext = name.substring(index + 1);
            if (!ext.equals("")) {
                MimeTypeMap mime_map = MimeTypeMap.getSingleton();
                if (mime_map.hasExtension(ext))
                    return mime_map.getMimeTypeFromExtension(ext);
            }
        }
        return "*/*";
    }

    public static boolean isImage(String path) {
        if (path.toLowerCase().endsWith(".png"))
            return true;
        if (path.toLowerCase().endsWith(".bmp"))
            return true;
        if (path.toLowerCase().endsWith(".jpg"))
            return true;
        if (path.toLowerCase().endsWith(".jpeg"))
            return true;
        if (path.toLowerCase().endsWith(".gif"))
            return true;
        return false;
    }

    public static boolean isAudio(String path) {
        if (path.toLowerCase().endsWith(".mp3"))
            return true;
        if (path.toLowerCase().endsWith(".wav"))
            return true;
        if (path.toLowerCase().endsWith(".aac"))
            return true;
        if (path.toLowerCase().endsWith(".flac"))
            return true;
        if (path.toLowerCase().endsWith(".ape"))
            return true;
        if (path.toLowerCase().endsWith(".m4a"))
            return true;
        return false;
    }

    public static boolean isVideo(String path) {
        if (path.toLowerCase().endsWith(".mp4"))
            return true;
        if (path.toLowerCase().endsWith(".avi"))
            return true;
        if (path.toLowerCase().endsWith(".flv"))
            return true;
        if (path.toLowerCase().endsWith(".rmvb"))
            return true;
        if (path.toLowerCase().endsWith(".mkv"))
            return true;
        return false;
    }

    public static boolean isText(String path) {
        if (path.toLowerCase().endsWith(".txt"))
            return true;
        if (path.toLowerCase().endsWith(".ini"))
            return true;
        if (path.toLowerCase().endsWith(".info"))
            return true;
        if (path.toLowerCase().endsWith(".info"))
            return true;
        if (path.toLowerCase().endsWith(".error"))
            return true;
        if (path.toLowerCase().endsWith(".xml"))
            return true;
        if (path.toLowerCase().endsWith(".json"))
            return true;
        if (path.toLowerCase().endsWith(".html"))
            return true;
        if (path.toLowerCase().endsWith(".css"))
            return true;
        if (path.toLowerCase().endsWith(".js"))
            return true;
        return false;
    }

    public static boolean isDocument(String path) {
        if (path.toLowerCase().endsWith(".doc"))
            return true;
        if (path.toLowerCase().endsWith(".docx"))
            return true;
        return false;
    }

    public static boolean isWebResource(String path) {
        if (path.startsWith("http://"))
            return true;
        if (path.startsWith("https://"))
            return true;
        return false;
    }
}

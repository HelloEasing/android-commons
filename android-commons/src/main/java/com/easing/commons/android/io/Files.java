package com.easing.commons.android.io;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Environment;

import com.easing.commons.android.helper.callback.Filter;
import com.easing.commons.android.helper.data.Result;
import com.easing.commons.android.helper.exception.BizException;
import com.easing.commons.android.manager.Uris;
import com.easing.commons.android.media.MediaType;
import com.easing.commons.android.app.CommonApplication;
import com.easing.commons.android.struct.Collections;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import lombok.SneakyThrows;

public class Files {

    public static boolean exist(String path) {
        return new File(path).exists();
    }

    public static boolean isFileSystem(String path) {
        try {
            new File(path).getName();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean isFile(String path) {
        if (!new File(path).exists())
            throw BizException.of("file not exists");
        return new File(path).isFile();
    }

    public static String getFileName(String path) {
        return new File(path).getName().toLowerCase();
    }

    public static boolean isSameFile(String f1, String f2) {
        return new File(f1).equals(new File(f2));
    }

    public static boolean isSameFile(File f1, File f2) {
        return f1.equals(f2);
    }

    public static boolean isAncestorFile(String parent, String child) {
        while (child != null) {
            if (Files.isSameFile(parent, child))
                return true;
            child = new File(child).getParent();
        }
        return false;
    }

    public static boolean isParentFile(String parent, String child) {
        return Files.isParentFile(new File(parent), new File(child));
    }

    public static boolean isParentFile(File parent, File child) {
        return Files.isSameFile(parent, child.getParentFile());
    }

    @SneakyThrows
    public static void createFile(String path) {
        File file = new File(path);
        if (file.exists() && file.isDirectory())
            Files.deleteFile(file);
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        if (!file.exists())
            file.createNewFile();
    }

    @SneakyThrows
    public static File getFile(String path) {
        return new File(path);
    }

    //创建目录
    @SneakyThrows
    public static File createFolder(String path) {
        File directory = new File(path);
        if (directory.exists() && !directory.isDirectory())
            Files.deleteFile(directory);
        if (!directory.exists())
            directory.mkdirs();
        return directory;
    }

    /**
     * 删除文件
     **/
    @SneakyThrows
    public static void deleteFile(String file) {
        Files.deleteFile(new File(file));
    }

    /**
     * 删除文件
     **/
    @SneakyThrows
    public static void deleteFile(File file) {
        if (!file.exists())
            return;

        if (file.isDirectory()) {
            File[] child_files = file.listFiles();
            for (File child_file : child_files)
                Files.deleteFile(child_file.getAbsolutePath());
        }
        file.delete();
    }

    /**
     * 清空文件夹
     **/
    public static void clearFolder(String path) {
        Files.deleteFile(path);
        Files.createFolder(path);
    }

    @SneakyThrows
    public static void copyFile(String source, String target) {
        copyFile(new File(source), new File(target));
    }

    /**
     * 复制文件
     *
     * @param source 数据源 可以是文件，或者文件夹
     * @param target 目标文件夹
     **/
    @SneakyThrows
    public static void copyFile(File source, File target) {
        if (!target.exists())
            target.mkdirs();

        if (source.isDirectory()) {
            target = new File(target, source.getName());
            target.mkdir();
            if (Files.isSameFile(source, target))
                return;

            File[] child_files = source.listFiles();
            for (File old_file : child_files) {
                File new_file = new File(target, old_file.getName());
                Files.copyFile(old_file, target);
            }
        } else {
            target = new File(target, source.getName());
            target.createNewFile();
            if (Files.isSameFile(source, target))
                return;

            FileInputStream fis = new FileInputStream(source);
            BufferedInputStream bis = new BufferedInputStream(fis);
            FileOutputStream fos = new FileOutputStream(target);
            BufferedOutputStream bos = new BufferedOutputStream(fos);

            byte[] buffer = new byte[1024 * 1024];
            int len;
            while ((len = bis.read(buffer)) != -1)
                bos.write(buffer, 0, len);
            bos.flush();

            bis.close();
            bos.close();
            fis.close();
            fos.close();
        }
    }

    /**
     * 复制文件结构，但不复制数据
     **/
    public static void copyFileWithNodata(File source, File target) throws Exception {
        if (!target.getParentFile().exists())
            target.getParentFile().mkdirs();

        if (source.isDirectory()) {
            if (target.isFile())
                Files.deleteFile(target.getAbsolutePath());
            if (!target.exists())
                target.mkdirs();

            File[] child_files = source.listFiles();
            for (File old_file : child_files) {
                File new_file = new File(target, old_file.getName());
                Files.copyFile(old_file, new_file);
            }
        } else {
            Files.deleteFile(target.getAbsolutePath());
            target.createNewFile();
        }
    }

    /**
     * 剪切文件
     **/
    public static void cutFile(String source, String target) {
        Files.cutFile(new File(source), new File(target));
    }

    /**
     * 剪切文件
     **/
    @SneakyThrows
    public static void cutFile(File source, File target) {
        Files.copyFile(source, target);
        Files.deleteFile(source.getAbsolutePath());
    }

    //重命名文件
    @SneakyThrows
    public static void renameFile(String source, String target) {
        Files.renameFile(new File(source), new File(target));
    }

    //重命名文件
    @SneakyThrows
    public static void renameFile(File source, File target) {
        source.renameTo(target);
    }

    //获取文件扩展名
    public static String getExtensionName(String file) {
        return Files.getExtensionName(new File(file));
    }

    //获取文件扩展名
    public static String getExtensionName(File file) {
        String name = file.getName().toLowerCase();
        if (name.lastIndexOf('.') == -1)
            return "";
        else
            return name.substring(name.lastIndexOf('.') + 1);
    }

    public static List<String> sortFiles(List<String> files) {
        Collections.sort(files, (left, right) -> {
            String left_name = left.toLowerCase();
            String right_name = right.toLowerCase();

            if (new File(left).isDirectory() && new File(right).isFile())
                return -1;

            if (new File(left).isFile() && new File(right).isDirectory())
                return 1;

            if (left_name.startsWith("#") && !right_name.startsWith("#"))
                return -1;

            if (left_name.startsWith("_") && !right_name.startsWith("_"))
                return -1;

            if (!left_name.startsWith(".") && right_name.startsWith("."))
                return -1;

            if (!left_name.startsWith("#") && right_name.startsWith("#"))
                return 1;

            if (!left_name.startsWith("_") && right_name.startsWith("_"))
                return 1;

            if (left_name.startsWith(".") && !right_name.startsWith("."))
                return 1;

            return left_name.compareTo(right_name);
        });
        return files;
    }

    /**
     * 对子文件进行排序
     **/
    public static List<File> listSortedChildFile(File dir) {
        if (!dir.exists() || !dir.isDirectory())
            throw new RuntimeException("dir not exists, or not a directory");

        List<File> child_files = Arrays.asList(dir.listFiles());
        Collections.sort(child_files, (left, right) -> {
            String left_name = left.getAbsolutePath().toLowerCase();
            String right_name = right.getAbsolutePath().toLowerCase();

            if (left.isDirectory() && right.isFile())
                return -1;

            if (left.isFile() && right.isDirectory())
                return 1;

            if (left_name.startsWith("#") && !right_name.startsWith("#"))
                return -1;

            if (left_name.startsWith("_") && !right_name.startsWith("_"))
                return -1;

            if (!left_name.startsWith(".") && right_name.startsWith("."))
                return -1;

            if (!left_name.startsWith("#") && right_name.startsWith("#"))
                return 1;

            if (!left_name.startsWith("_") && right_name.startsWith("_"))
                return 1;

            if (left_name.startsWith(".") && !right_name.startsWith("."))
                return 1;

            return left_name.compareTo(right_name);
        });
        return child_files;
    }

    public static List<String> getChildNames(String dir) {
        List<File> child_files = Files.listSortedChildFile(new File(dir));
        List<String> child_names = Collections.emptyList();
        for (File file : child_files)
            child_names.add(file.getName());
        return child_names;
    }

    /**
     * 关闭文件相关资源
     **/
    @SneakyThrows
    public static void close(InputStream is, OutputStream os) {
        if (os != null)
            os.close();
        if (is != null)
            is.close();
    }

    /**
     * 从文件输入流中读取字符串
     **/
    @SneakyThrows
    public static String streamToString(FileInputStream fis, String encode) {
        if (encode == null)
            encode = "UTF-8";
        byte[] buffer = new byte[fis.available()];
        fis.read(buffer);
        Files.close(fis, null);
        return new String(buffer, encode);
    }

    /**
     * 按行读入文件中的字符串
     **/
    @SneakyThrows
    public static List<String> readLines(File file) {
        ArrayList list = new ArrayList();

        FileReader reader = new FileReader(file);
        BufferedReader bf = new BufferedReader(reader);
        String line = bf.readLine();
        while (line != null) {
            list.add(line);
            bf.readLine();
        }
        bf.close();
        return list;
    }

    //按行读取需要的字符串，检查通过，则返回该行
    @SneakyThrows
    public static String readLine(File file, Filter<String> filter) {
        FileReader reader = new FileReader(file);
        BufferedReader bf = new BufferedReader(reader);
        String line = bf.readLine();
        while (line != null) {
            if (filter.keep(line))
                break;
            else
                bf.readLine();
        }
        bf.close();
        reader.close();
        return line;
    }

    //将字节集写入文件
    @SneakyThrows
    public static void writeToFile(String path, byte[] bytes) {
        Files.createFile(path);
        FileOutputStream os = new FileOutputStream(path);
        os.write(bytes);
        os.flush();
        os.close();
    }

    //将输入流写入文件
    @SneakyThrows
    public static void writeToFile(String path, InputStream is) {
        Files.createFile(path);
        FileOutputStream os = new FileOutputStream(path);
        byte[] buffer = new byte[1024 * 1024];
        for (int len = -1; is.read(buffer) != -1; )
            os.write(buffer, 0, len);
        os.flush();
        os.close();
        is.close();
    }

    //将字符串写入文件
    @SneakyThrows
    public static void writeToFile(File file, String content) {
        Files.createFile(file.getAbsolutePath());
        FileWriter writer = new FileWriter(file);
        writer.write(content);
        writer.flush();
        writer.close();
    }

    //将字符串写入文件
    @SneakyThrows
    public static void writeToFile(String file, String content) {
        Files.createFile(file);
        FileWriter writer = new FileWriter(file);
        writer.write(content);
        writer.flush();
        writer.close();
    }

    /**
     * 将字符串写入文件结尾
     **/
    @SneakyThrows
    public static void appendToFile(String path, String content) {
        File file = new File(path);
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        raf.seek(file.length());
        raf.writeBytes(content);
        raf.close();
    }

    public static String getAndroidExternalFile(String path) {
        return getAndroidExternalPath(path, false);
    }

    public static String getAndroidExternalFolder(String path) {
        return getAndroidExternalPath(path, true);
    }

    public static String getAndroidExternalPath(String path, boolean isFolder) {
        if (path == null || path.equals(""))
            return Environment.getExternalStorageDirectory().getPath();
        String file_path = Environment.getExternalStorageDirectory().getPath() + "/" + path;
        if (isFolder)
            Files.createFolder(file_path);
        else
            Files.createFile(file_path);
        return file_path;
    }

    public static String getAndroidPrivatePath(Context context, String path) {
        return context.getExternalFilesDir(path).getPath();
    }

    public static String getAndroidPrivateCachePath(Context context, String path) {
        if (path == null)
            return context.getExternalCacheDir().getPath();
        String file_path = context.getExternalCacheDir().getPath() + "/" + path;
        Files.createFile(file_path);
        return file_path;
    }

    public static String getAndroidInternalPath(Context context, String path) {
        if (path == null)
            return context.getFilesDir().getPath();
        return context.getFilesDir().getPath() + "/" + path;
    }

    public static String getAndroidInternalCachePath(Context context, String path) {
        if (path == null)
            return context.getCacheDir().getPath();
        return context.getCacheDir().getPath() + "/" + path;
    }

    public static File getAndroidInternalDatabase(Context context, String name) {
        return context.getDatabasePath(name);
    }

    public static SharedPreferences getAndroidSharedPreferences(Context context, String name) {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public static String getAndroidPublicPictureDirectory() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath();
    }

    public static String getAndroidPublicMusicDirectory() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getPath();
    }

    public static String getAndroidPublicMovieDirectory() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getPath();
    }

    public static String getAndroidPublicDocumentDirectory() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath();
    }

    public static String getAndroidPublicAlbumDirectory() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath();
    }

    public static String getAndroidPublicDownloadDirectory() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
    }

    public static List<String> listChildFiles(String dir) {
        List<String> paths = new ArrayList();
        if (new File(dir).listFiles() != null)
            for (File file : new File(dir).listFiles())
                paths.add(file.getPath());
        return paths;
    }

    public static List<String> listSortedChildFiles(String dir) {
        List<File> files = Files.listSortedChildFile(new File(dir));
        List<String> paths = new ArrayList();
        for (File file : files)
            paths.add(file.getPath());
        return paths;
    }

    public static List<String> listVisibleFiles(String dir) {
        List<File> files = Files.listSortedChildFile(new File(dir));
        List<String> paths = new ArrayList();
        for (File file : files)
            if (!file.isHidden())
                paths.add(file.getPath());
        return paths;
    }

    public static List<String> listAndroidPictures(String dir) {
        List<String> paths = Files.listChildFiles(dir);
        List<String> filter_paths = new ArrayList();
        for (String path : paths)
            if (path.indexOf('.') >= 0) {
                int index = path.lastIndexOf('.');
                String suffix = path.substring(index).toLowerCase();
                if (suffix.equals(".jpg") || suffix.equals(".jpeg") || suffix.equals(".bmp") || suffix.equals(".png") || suffix.equals(".gif"))
                    filter_paths.add(path);
            }
        return filter_paths;
    }

    public static List<String> listAndroidAudios(String dir) {
        List<String> paths = Files.listChildFiles(dir);
        List<String> filteredPaths = new ArrayList();
        for (String path : paths)
            if (path.indexOf('.') >= 0) {
                int index = path.lastIndexOf('.');
                String suffix = path.substring(index).toLowerCase();
                if (suffix.equals(".mp3") || suffix.equals(".mp4") || suffix.equals(".flac"))
                    filteredPaths.add(path);
            }
        return filteredPaths;
    }

    public static List<String> listAndroidVideos(String dir) {
        List<String> paths = Files.listChildFiles(dir);
        List<String> filter_paths = new ArrayList();
        for (String path : paths)
            if (path.indexOf('.') >= 0) {
                int index = path.lastIndexOf('.');
                String suffix = path.substring(index).toLowerCase();
                if (suffix.equals(".mp4") || suffix.equals(".avi") || suffix.equals(".mov"))
                    filter_paths.add(path);
            }
        return filter_paths;
    }

    public static void openFile(Context context, String path, String authority) {
        Uri data = Uris.fromFile(context, path, authority);
        String type = MediaType.getMIMEType(path);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(data, type);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            intent.setDataAndType(data, MediaType.TYPE_ALL);
            context.startActivity(intent);
        }
    }

    public static void openWebFile(Context context, String path) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse(path));
        context.startActivity(intent);
    }

    public static Result<String> openFile(String path) {
        try {
            Files.openFile(CommonApplication.ctx, path, Uris.AUTHORITY_FILE_PROVIDER);
            return Result.ok();
        } catch (Exception e) {
            String extension = Files.getExtensionName(path);
            return Result.fail(Result.CODE_FAIL, "不支持该文件类型", extension);
        }
    }

    @SneakyThrows
    public static void writeStream(InputStream is, String dest) {
        Files.createFile(dest);
        writeStream(is, new FileOutputStream(dest));
    }

    @SneakyThrows
    private static void writeStream(InputStream is, OutputStream os) {
        byte[] buffer = new byte[10 * 1024];
        int len = 0;
        while ((len = is.read(buffer)) != -1)
            os.write(buffer, 0, len);
        os.flush();
        os.close();
        is.close();
    }

    @SneakyThrows
    public static void writeStreamWithCallback(InputStream is, OutputStream os, ByteWriteCallback callback) {
        double total = is.available();
        double written = 0;
        byte[] buffer = new byte[10 * 1024];
        int len = 0;
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
            written += len;
            if (callback != null)
                callback.onByteWrite(total, written);
        }
        os.flush();
        os.close();
        is.close();
    }

    @SneakyThrows
    private static void zip(ZipOutputStream zos, File source, String base) {
        // 如果是文件夹，创建目录并遍历子文件
        if (source.isDirectory()) {
            File[] childs = source.listFiles();
            // 空文件夹，则写入一个目录
            if (childs.length == 0)
                zos.putNextEntry(new ZipEntry(base + "/"));
                // 非空文件夹，则递归遍历子文件
            else
                for (File child : childs)
                    zip(zos, child, base + "/" + child.getName());
            return;
        }

        // 如果是文件，直接写数据
        zos.putNextEntry(new ZipEntry(base));
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(source));
        byte[] buffer = new byte[1024 * 1024];
        for (int len; (len = bis.read(buffer)) != -1; )
            zos.write(buffer, 0, len);
        bis.close();
    }

    @SneakyThrows
    public static void zip(String source, String dest) {
        Files.deleteFile(dest);
        Files.createFolder(dest);
        ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(dest)));
        zip(zos, new File(source), new File(source).getName());
        zos.flush();
        zos.close();
    }

    @SneakyThrows
    public static String zipHere(String source) {
        String dest = new File(source).getParent() + "/" + new File(source).getName() + ".zip";
        Files.zip(source, dest);
        return dest;
    }

    @SneakyThrows
    public static void unzip(String source, String dest) {
        Files.createFolder(dest);
        ZipFile zipFile = new ZipFile(source);
        Enumeration entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            String file = dest + "/" + entry.getName();
            if (entry.isDirectory())
                Files.createFolder(file);
            else {
                Files.createFile(file);
                InputStream is = zipFile.getInputStream(entry);
                BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(file));
                byte[] buffer = new byte[1024 * 1024];
                for (int len; (len = is.read(buffer)) != -1; )
                    os.write(buffer, 0, len);
                os.flush();
                os.close();
                is.close();
            }
        }
        zipFile.close();
    }

    @SneakyThrows
    public static void unzipHere(String source) {
        File sourceFile = Files.getFile(source);
        String name = sourceFile.getName();
        // int index = name.lastIndexOf(".zip");
        // String suffix = (index < 0) ? name : name.substring(0, index);
        // String dest = sourceFile.getParent() + "/" + suffix;
        Files.unzip(source, sourceFile.getParent());
    }

    //拷贝asset资源到存储卡
    @SneakyThrows
    public static void copyFromAsset(Context ctx, String source, String dest) {
        AssetManager manager = ctx.getAssets();
        InputStream is = manager.open(source);
        String to = dest + "/" + source;
        FileOutputStream os = new FileOutputStream(to);
        Files.writeStream(is, os);
    }

    //拷贝asset资源到存储卡
    @SneakyThrows
    public static void copyAssets(Context ctx, String[] sources, String dest) {
        Files.createFolder(dest);
        for (String source : sources)
            Files.copyFromAsset(ctx, source, dest);
    }

    //获取文件长度
    public static long lenth(String path) {
        return new File(path).length();
    }

    //获取文件后缀
    public String getFileSuffix(String path, boolean withDot) {
        String[] splits = path.split("[.]");
        if (splits.length == 0)
            return "";
        String suffix = splits[splits.length - 1];
        return withDot ? "." + suffix : suffix;
    }

    public interface ByteWriteCallback {
        void onByteWrite(double total, double written);
    }

}

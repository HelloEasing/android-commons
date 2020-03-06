package com.easing.commons.android.format;

import com.easing.commons.android.code.Console;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

//文本工具
//有空优化排序一下
public class Texts {

    private static final HanyuPinyinOutputFormat defaultFormat;

    static {
        defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
    }

    public static String random() {
        return UUID.randomUUID().toString();
    }

    public static String random(boolean withConcatChar, boolean toUpperCase) {
        String uuid = UUID.randomUUID().toString();
        if (!withConcatChar)
            uuid = uuid.replaceAll("-", "");
        if (toUpperCase)
            uuid = uuid.toUpperCase();
        return uuid;
    }

    public static String[] collectionToStringArray(Collection collection) {
        String[] array = new String[collection.size()];
        int index = 0;
        for (Object item : collection)
            array[index++] = (item == null) ? "NULL" : item.toString();
        return array;
    }

    public static String arrayToString(Object... array) {
        if (array == null) return "NULL";
        else if (array.length == 0) return "[]";

        StringBuffer buffer = new StringBuffer();
        buffer.append("[");
        buffer.append(array[0] == null ? "NULL" : array[0].toString());
        for (int i = 1; i < array.length; i++) {
            buffer.append(", ");
            buffer.append(array[i] == null ? "NULL" : array[i].toString());
        }
        buffer.append("]");
        return buffer.toString();
    }

    public static String arrayToString2(Object[][] array) {
        if (array == null) return "NULL";
        else if (array.length == 0) return "[]";

        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < array.length; i++) {
            buffer.append(arrayToString(array[i]));
            if (i != array.length - 1) buffer.append("\n");
        }
        return buffer.toString();
    }

    public static String arrayToStringWithSplit(String tag, Object... array) {
        if (array == null) return "NULL";
        else if (array.length == 0) return "[]";

        StringBuffer sb = new StringBuffer();
        sb.append(array[0] == null ? "NULL" : array[0].toString());
        for (int i = 1; i < array.length; i++) {
            sb.append(tag);
            sb.append(array[i] == null ? "NULL" : array[i].toString());
        }
        return sb.toString();
    }

    public static String listToString(List objs, String split) {
        if (objs == null || objs.size() == 0) return "";
        StringBuffer buffer = new StringBuffer();
        buffer.append(objs.get(0).toString());
        for (int i = 1; i < objs.size(); i++)
            buffer.append(split).append(objs.get(i));
        return buffer.toString();
    }

    public static String[][] sortArray2(String[][] datas) {
        ArrayList<ArrayList<String>> lists = new ArrayList();
        for (int i = 0; i < datas.length; i++)
            lists.add(new ArrayList(Arrays.asList(datas[i])));
        Collections.sort(lists, (l, r) -> {
            for (int j = 0; j < l.size(); j++)
                if (l.get(j).compareTo(r.get(j)) != 0) return l.get(j).compareTo(r.get(j));
            return 0;
        });

        String[][] newDatas = new String[datas.length][datas.length > 0 ? datas[0].length : 0];
        for (int i = 0; i < newDatas.length; i++)
            for (int j = 0; j < newDatas[i].length; j++)
                newDatas[i][j] = lists.get(i).get(j);

        return newDatas;
    }

    public static String getPinyin(Object o) {
        try {
            String pinyin = PinyinHelper.toHanYuPinyinString(o.toString(), defaultFormat, "", true);
            return pinyin.toLowerCase();
        } catch (Exception e) {
            Console.error(e);
            return "";
        }
    }

    //首字母简拼
    public static String getSimplePinyin(Object o) {
        try {
            String pinyin = "";
            String str = o.toString();
            for (int i = 0; i < str.length(); i++)
                pinyin = pinyin + PinyinHelper.toHanYuPinyinString(str.charAt(i) + "", defaultFormat, "", true).charAt(0);
            return pinyin.toLowerCase();
        } catch (Exception e) {
            Console.error(e);
            return "";
        }
    }

    public static int comparePinyin(Object o1, Object o2) {
        return getPinyin(o1).compareTo(getPinyin(o2));
    }

    public static String unicodeToString(String str) {
        StringBuilder sb = new StringBuilder();
        int index = str.indexOf("\\u");
        while (index != -1) {
            sb.append(unicodeToChar(str.substring(index, index + 6)));
            index = str.indexOf("\\u", index + 6);
        }
        return sb.toString();
    }

    public static char unicodeToChar(String str) {
        if (str.length() != 6) throw new RuntimeException("not a unicode string");
        if (!"\\u".equals(str.substring(0, 2))) throw new RuntimeException("not a unicode string");

        int high = Integer.parseInt(str.substring(2, 4), 16) << 8; // 高8位
        int low = Integer.parseInt(str.substring(4, 6), 16); // 低8位
        return (char) (high + low);
    }

    //在已有字符串基础上，连续拼接若干个相同的字符
    public static String append(String base, Object extra, int times) {
        for (int i = 0; i < times; i++)
            base = base + extra.toString();
        return base;
    }

    public static String trim(String str, char c) {
        if (isEmpty(str)) return "";
        char[] chars = str.toCharArray();
        int start = 0;
        int end = chars.length;
        while (chars[start] == c)
            start++;
        while (chars[end - 1] == c)
            end--;
        String substring = str.substring(start, end);
        return substring;
    }

    public static String trimLeft(String str, char c) {
        if (isEmpty(str)) return "";
        char[] chars = str.toCharArray();
        int start = 0;
        int end = chars.length;
        while (chars[start] == c)
            start++;
        String substring = str.substring(start, end);
        return substring;
    }

    public static String trimRight(String str, char c) {
        if (isEmpty(str)) return "";
        char[] chars = str.toCharArray();
        int start = 0;
        int end = chars.length;
        while (chars[end - 1] == c)
            end--;
        String substring = str.substring(start, end);
        return substring;
    }

    //判断字符串是否为空
    public static boolean isEmpty(String text) {
        return text == null || text.isEmpty();
    }

    //判断是不是http网址
    public static boolean isHttpUrl(String url) {
        String regex = "^([hH][tT]{2}[pP]:/*|[hH][tT]{2}[pP][sS]:/*|[fF][tT][pP]:/*)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+(\\?{0,1}(([A-Za-z0-9-~]+\\={0,1})([A-Za-z0-9-~]*)\\&{0,1})*)$";
        Pattern pattern = Pattern.compile(regex.trim());
        Matcher matcher = pattern.matcher(url.toLowerCase().trim());
        return matcher.matches();
    }

    //将异常的字符串转化为整数
    public static Integer abnormalTextToInt(String text, Integer defaultValue) {
        try {
            return Integer.parseInt(Pattern.compile("[^0-9]").matcher(text).replaceAll("").trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }
}

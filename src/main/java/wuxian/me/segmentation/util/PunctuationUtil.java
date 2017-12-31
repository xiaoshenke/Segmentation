package wuxian.me.segmentation.util;

import org.slf4j.LoggerFactory;

import java.util.*;

//判断一个字符是否是标点符号
public class PunctuationUtil {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(PunctuationUtil.class);
    private static char[] chars = null;
    private static boolean isLoaded = false;

    static {
        loadPunctuation();
    }

    //Call this function first
    public static void loadPunctuation() {

        if (isLoaded) {
            return;
        }
        isLoaded = true;

        List<String> punctuations = WordsLoader.loadPunctuations();
        LOGGER.info("初始化标点符号");
        Set<Character> set = new HashSet<>();
        for (String line : punctuations) {
            if (line.length() == 1) {
                set.add(line.charAt(0));
            } else {
                LOGGER.warn("长度不为一的标点符号：" + line);
            }
        }
        //增加空白字符
        set.add(' ');
        set.add('　');
        set.add('\t');
        set.add('\n');
        set.add('\r');
        List<Character> list = new ArrayList<>();
        list.addAll(set);
        Collections.sort(list);
        int len = list.size();
        chars = new char[len];
        for (int i = 0; i < len; i++) {
            chars[i] = list.get(i);
        }
        set.clear();
        list.clear();
        LOGGER.info("标点符号初始化完毕，标点符号个数：" + chars.length);
    }

    //判断文本中是否包含标点符号
    public static boolean has(String text) throws RuntimeException {

        if (!isLoaded) {
            throw new RuntimeException("you should call loadPunctuation first!");
        }

        for (char c : text.toCharArray()) {
            if (is(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 将一段文本根据标点符号分割为多个不包含标点符号的文本
     * 可指定要保留那些标点符号
     *
     * @param text            文本
     * @param withPunctuation 是否保留标点符号
     * @param reserve         保留的标点符号列表
     * @return 文本列表
     */
    public static List<String> seg(String text, boolean withPunctuation, char... reserve) {
        if (!isLoaded) {
            throw new RuntimeException("you should call loadPunctuation first!");
        }

        List<String> list = new ArrayList<>();
        int start = 0;
        char[] array = text.toCharArray();
        int len = array.length;
        outer:
        for (int i = 0; i < len; i++) {
            char c = array[i];
            for (char t : reserve) {
                if (c == t) {
                    //保留的标点符号
                    continue outer;
                }
            }
            if (PunctuationUtil.is(c)) {
                if (i > start) {
                    list.add(text.substring(start, i));
                    //下一句开始索引
                    start = i + 1;
                } else {
                    //跳过标点符号
                    start++;
                }
                if (withPunctuation) {
                    list.add(Character.toString(c));
                }
            }
        }
        if (len - start > 0) {
            list.add(text.substring(start, len));
        }
        return list;
    }

    //判断一个字符是否是标点符号
    public static boolean is(char _char) {
        if (!isLoaded) {
            throw new RuntimeException("you should call loadPunctuation first!");
        }

        int index = Arrays.binarySearch(chars, _char);
        return index >= 0;
    }

}
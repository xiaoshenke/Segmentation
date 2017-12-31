package wuxian.me.segmentation.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class StopWordUtil {

    private StopWordUtil() {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(StopWordUtil.class);
    private static final Set<String> stopwords = new HashSet<>();

    private static boolean inited = false;

    public static void initWithDefaultWords() {
        if (inited) {
            return;
        }

        inited = true;
        List<String> lines = WordsLoader.loadStopWord();
        setStopwords(lines);
    }

    public static void setStopwords(List<String> lines) {
        stopwords.clear();
        for (String line : lines) {
            if (!isStopChar(line)) {
                stopwords.add(line);
            }
        }
    }

    //如果词的长度为一且不是中文字符和数字，则认定为停用词
    private static boolean isStopChar(String word) {
        if (word.length() == 1) {
            char _char = word.charAt(0);
            if (_char < 48) {
                return true;
            }
            if (_char > 57 && _char < 19968) {
                return true;
            }
            if (_char > 40869) {
                return true;
            }
        }
        return false;
    }

    //判断一个词是否是停用词
    public static boolean is(String word) {
        if (word == null) {
            return false;
        }
        word = word.trim();
        return isStopChar(word) || stopwords.contains(word);
    }

    /**
     * 停用词过滤，删除输入列表中的停用词
     *
     * @param words 词列表
     */
    public static void filterStopWords(List<String> words) {
        Iterator<String> iter = words.iterator();
        while (iter.hasNext()) {
            String word = iter.next();
            if (is(word)) {
                //去除停用词
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("去除停用词：" + word);
                }
                iter.remove();
            }
        }
    }

}
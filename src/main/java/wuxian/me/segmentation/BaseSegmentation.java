package wuxian.me.segmentation;

import wuxian.me.segmentation.core.Dictionary;
import wuxian.me.segmentation.util.PunctuationUtil;
import wuxian.me.segmentation.util.StopWordUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by wuxian on 16/12/2017.
 */
public abstract class BaseSegmentation implements Segmentation {

    private boolean keepWhitespace = false;
    private boolean caseSensetive = false;
    private boolean keepPunctutaion = false;
    private int maxLength = 6;

    private Dictionary dictionary = null;
    private boolean hasStopword = true;

    public BaseSegmentation(boolean hasStopword) {
        this.hasStopword = hasStopword;

        StopWordUtil.initWithDefaultWords();
    }

    public void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    /**
     * 获取词典操作接口
     *
     * @return 词典操作接口
     */
    public Dictionary getDictionary() {
        return dictionary;
    }

    protected abstract List<String> segImpl(String text);

    //分词时截取的字符串的最大长度
    public int getInterceptLength() {
        /*
        if (getDictionary().getMaxLength() > maxLength) {  //这样会严重影响效率
            return getDictionary().getMaxLength();
        }
        */
        return maxLength;
    }

    public List<String> seg(String text) {
        if (text == null || text.length() == 0) {
            return new ArrayList<>(0);
        }
        List<String> sentences = PunctuationUtil.seg(text, keepPunctutaion); //1:先按标点进行句子分割。
        if (sentences.size() == 1) {
            return segSingleSentence(sentences.get(0));
        }

        List<String> resultList = new ArrayList<>(text.length() / 3);
        for (String sentence : sentences) {
            List<String> words = segSingleSentence(sentence);
            if (words != null && !words.isEmpty()) {
                resultList.addAll(words);
            }
        }
        sentences.clear();
        sentences = null;

        if (!hasStopword) {
            StopWordUtil.filterStopWords(resultList);
        }
        return resultList;
    }

    private List<String> segSingleSentence(final String sentence) {
        if (sentence.length() == 1) {
            if (keepWhitespace) {
                List<String> result = new ArrayList<>(1);
                result.add(new String(caseSensetive ? sentence : sentence.toLowerCase()));
                return result;
            } else {
                if (!Character.isWhitespace(sentence.charAt(0))) {
                    List<String> result = new ArrayList<>(1);
                    result.add(new String(caseSensetive ? sentence : sentence.toLowerCase()));
                    return result;
                }
            }
        }

        List<String> list = segImpl(sentence);
        return list;
    }

    protected void addToCuttedList(List<String> result, String text, int start, int len) {
        String String = getWord(text, start, len);
        if (String != null) {
            result.add(String);
        }
    }

    protected void addToCuttedList(Stack<String> result, String text, int start, int len) {
        String String = getWord(text, start, len);
        if (String != null) {
            result.push(String);
        }
    }

    protected String getWord(String text, int start, int len) {
        if (len < 1) {
            return null;
        }
        if (start < 0) {
            return null;
        }
        if (text == null) {
            return null;
        }
        if (start + len > text.length()) {
            return null;
        }
        String wordText = null;
        if (caseSensetive) {
            wordText = text.substring(start, start + len);
        } else {
            wordText = text.substring(start, start + len).toLowerCase();
        }
        String String = new String(wordText);
        //方便编译器优化
        if (keepWhitespace) {
            //保留空白字符
            return String;
        } else {
            //忽略空白字符
            if (len > 1) {
                //长度大于1，不会是空白字符
                return String;
            } else {
                //长度为1，只要非空白字符
                if (!Character.isWhitespace(text.charAt(start))) {
                    //不是空白字符，保留
                    return String;
                }
            }
        }
        return null;
    }


}

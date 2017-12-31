package wuxian.me.segmentation;


import wuxian.me.segmentation.util.RecognitionUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuxian on 16/12/2017.
 */
public class MaxLengthMatching extends BaseSegmentation {

    public MaxLengthMatching() {
        this(true);
    }

    public MaxLengthMatching(boolean b) {
        super(b);
    }

    @Override
    //影响以下算法的效率的
    //1 @getinterceptLength
    protected List<String> segImpl(String text) {

        List<String> result = new ArrayList<>();
        final int textLen = text.length();
        int len = getInterceptLength();
        int start = 0;
        while (start < textLen) {
            if (len > textLen - start) {
                len = textLen - start;
            }
            while (!getDictionary().contains(text, start, len) && !RecognitionUtil.recog(text, start, len)) {
                if (len == 1) {
                    break;
                }
                len--;
            }
            len = getLen(text, start, len, getInterceptLength());
            addToCuttedList(result, text, start, len);
            start += len;
            len = getInterceptLength();
        }
        return result;
    }

    //若originLen=interceptLen,那么往len变大的方向尝试 看看能不能继续以更大的len进行分词 解决了单词长度最大为@getInterceptLength()的bug
    private int getLen(String text, int start, int originLen, int interceptLen) {
        if (originLen < interceptLen) {
            return originLen;
        }

        int nextLen = originLen + 1;
        while (true) {

            if (nextLen + start >= text.length()) {
                return originLen;
            }

            if (getDictionary().contains(text, start, nextLen) || RecognitionUtil.recog(text, start, nextLen)) {
                originLen = nextLen;
                nextLen = originLen + 1;
                continue;
            }

            return originLen;
        }

    }
}

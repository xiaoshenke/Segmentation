package wuxian.me.segmentation.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wuxian on 10/1/2018.
 */
public class EnglishRecogUtil {

    private static final String HTTP_REG = "https?://[\\d\\w\\-?=/.]+";
    private static final Pattern HTTP_PATTREN = Pattern.compile(HTTP_REG);

    private static final String ENG_REG = "[\\w']+";
    private static final Pattern ENG_PATTERN = Pattern.compile(ENG_REG);

    private EnglishRecogUtil() {
    }

    //-1表示识别失败
    public static int recLen(String origin, int begin) {

        if (origin == null || origin.length() == 0 || begin >= origin.length()) {
            return -1;
        }

        if (!RecognitionUtil.isEnglish(origin.charAt(begin))) {
            return -1;
        }

        Matcher matcher = HTTP_PATTREN.matcher(origin.subSequence(begin, origin.length()));
        if (matcher.find()) {
            return matcher.group().length();
        }

        matcher = ENG_PATTERN.matcher(origin.subSequence(begin, origin.length()));
        if (matcher.find()) {
            return matcher.group().length();
        }

        return -1;
    }
}

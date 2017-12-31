package wuxian.me.segmentation.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 数量词识别
 *
 * @author 杨尚川
 */
public class QuantifierUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuantifierUtil.class);
    private static final Set<Character> quantifiers = new HashSet<>();

    public static boolean inited = false;

    public static void init() {
        if (inited) {
            return;
        }

        inited = true;

        List<String> lines = WordsLoader.loadStopWord();
        for (String line : lines) {
            if (line.length() == 1) {
                char _char = line.charAt(0);
                if (quantifiers.contains(_char)) {
                    LOGGER.info("配置文件有重复项：" + line);
                } else {
                    quantifiers.add(_char);
                }
            } else {
                LOGGER.info("忽略不合法数量词：" + line);
            }
        }
        LOGGER.info("数量词初始化完毕，数量词个数：" + quantifiers.size());

    }


    public static boolean is(char _char) {
        return quantifiers.contains(_char);
    }

    public static void main(String[] args) {
        QuantifierUtil.init();
        int i = 1;
        for (char quantifier : quantifiers) {
            LOGGER.info((i++) + " : " + quantifier);
        }
    }
}
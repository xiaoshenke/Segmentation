package wuxian.me.segmentation.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by wuxian on 10/1/2018.
 */
public class EnglishRecogUtilTest {

    @Test
    public void testReg1() {
        String s = "链接：https://www.zhihu.com/question/265247240/answer/291519351\n" +
                "来源：知乎";

        System.out.println(EnglishRecogUtil.recLen(s, 0));
        System.out.println(EnglishRecogUtil.recLen(s, 3));
        int len = EnglishRecogUtil.recLen(s, 3);
        System.out.println(s.substring(3, 3 + len));
    }

    @Test
    public void testReg2() {
        String s = "Gina Carano.\n" +
                "\n" +
                "You don't want to mess with them.\n" +
                "\n" +
                "总结";

        System.out.println(EnglishRecogUtil.recLen(s, 0));

        int len = EnglishRecogUtil.recLen(s, 0);
        System.out.println(s.substring(0, 0 + len));
    }
}
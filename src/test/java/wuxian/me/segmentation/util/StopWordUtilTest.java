package wuxian.me.segmentation.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuxian on 24/12/2017.
 */
public class StopWordUtilTest {

    @Test
    public void testStopwords() {
        List<String> stopList = new ArrayList<>();
        stopList.add("我们");
        stopList.add("是");

        List<String> originList = new ArrayList<>();
        originList.add("今天");
        originList.add("是");
        originList.add("圣诞节");

        StopWordUtil.setStopwords(stopList);
        StopWordUtil.filterStopWords(originList);
        System.out.println(originList.toString());
    }

}
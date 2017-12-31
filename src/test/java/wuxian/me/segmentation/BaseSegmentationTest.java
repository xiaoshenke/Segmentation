package wuxian.me.segmentation;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuxian on 24/12/2017.
 */
public class BaseSegmentationTest {

    @Test
    public void testSegmentation() {
        Segmentation englishSegmentation = new BaseSegmentation(true) {
            @Override
            public List<String> segImpl(String text) {
                List<String> words = new ArrayList<>();
                for (String String : text.split("\\s+")) {
                    words.add(new String(String));
                }
                return words;
            }
        };
        System.out.println(englishSegmentation.seg("i love programming"));
    }

}
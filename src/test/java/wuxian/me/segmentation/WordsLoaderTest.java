package wuxian.me.segmentation;

import org.junit.Test;
import wuxian.me.segmentation.util.WordsLoader;

/**
 * Created by wuxian on 17/12/2017.
 */
public class WordsLoaderTest {

    @Test
    public void testLoad() {
        WordsLoader.loadByPaths("classpath:dic.txt");
    }

}
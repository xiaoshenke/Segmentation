package wuxian.me.segmentation;

import org.apache.log4j.varia.NullAppender;
import org.junit.Before;
import org.junit.Test;
import wuxian.me.segmentation.core.DictionaryTrie;
import wuxian.me.segmentation.util.FileUtil;

/**
 * Created by wuxian on 17/12/2017.
 */
public class MaxLengthMatchingTest {

    static {
        org.apache.log4j.BasicConfigurator.configure(new NullAppender());
    }

    DictionaryTrie trie = DictionaryTrie.getIns();

    @Before
    public void setup() {
        trie.add("中国");
        trie.add("实行");
        trie.add("的");
        trie.add("是");
        trie.add("全国");
        trie.add("人民");
        trie.add("当家做主");
        trie.add("政治");
        trie.add("制度");
    }

    String test = "中国实行的是全国人民当家做主的政治制度";

    @Test
    public void noramlSegment() {
        Segmentation seg = new MaxLengthMatching(true);
        seg.setDictionary(trie);
        System.out.println(seg.seg(test));
    }

    @Test
    public void fullSegment() {
        Segmentation segmentation = new MaxLengthMatching(true);
        long cur = System.currentTimeMillis();
        trie.initWithDefaultWords();
        System.out.println("load dictionary cost " + (System.currentTimeMillis() - cur) + " millis");
        segmentation.setDictionary(trie);

        cur = System.currentTimeMillis();
        System.out.println(segmentation.seg(FileUtil.readFromFile(FileUtil.getCurrentPath() + "/article/test1.txt")));

        System.out.println("segmentation cost " + (System.currentTimeMillis() - cur) + " millis");
    }

    @Test
    public void testExeceedMaxLength() {

        System.out.println(trie.contains("全国人民当家做主"));
        BaseSegmentation seg = new MaxLengthMatching(true);

        seg.setMaxLength(6);
        trie.add("全国人民当家");  //max length是一个词的时候 才会继续向前+length

        trie.add("全国人民当家做主");

        seg.setDictionary(trie);
        System.out.println(seg.seg(test));
    }

}
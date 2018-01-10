package wuxian.me.segmentation;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wuxian.me.segmentation.core.DictionaryTrie;
import wuxian.me.segmentation.util.FileUtil;

import java.util.List;

/**
 * Created by wuxian on 17/12/2017.
 */
public class MaxLengthMatchingTest {

    static Logger LOGGER = LoggerFactory.getLogger(MaxLengthMatching.class);

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

        LOGGER.info(test);
    }

    @Test
    public void fullSegment() {
        Segmentation segmentation = new MaxLengthMatching(true);
        long cur = System.currentTimeMillis();
        trie.initWithDefaultWords();
        System.out.println("load dictionary cost " + (System.currentTimeMillis() - cur) + " millis");
        segmentation.setDictionary(trie);

        cur = System.currentTimeMillis();
        List<String> segged = segmentation.seg(FileUtil.readFromFile(FileUtil.getCurrentPath() + "/article/test1.txt"));
        System.out.println(segged);
        System.out.println("segmentation cost " + (System.currentTimeMillis() - cur) + " millis");

        StringBuilder builder = new StringBuilder("");
        for (String s : segged) {
            builder.append(s);
            builder.append(" ");
        }
        FileUtil.writeToFile(FileUtil.getCurrentPath() + "/article/test1_seg.txt", builder.toString());
    }

    @Test
    public void test1() throws Exception {
        String input = "/Users/wuxian/Desktop/in.txt";
        String output = "/Users/wuxian/Desktop/out.txt";
        fullSegment(input, output);
    }

    @Test
    public void test2() throws Exception {

        Segmentation segmentation = new MaxLengthMatching(true);
        long cur = System.currentTimeMillis();
        trie.initWithDefaultWords();
        System.out.println("load dictionary cost " + (System.currentTimeMillis() - cur) + " millis");
        segmentation.setDictionary(trie);

        cur = System.currentTimeMillis();
        List<String> segged = segmentation.seg(FileUtil.readFromFile(FileUtil.getCurrentPath() + "/article/test2.txt"));
        System.out.println(segged);
        System.out.println("segmentation cost " + (System.currentTimeMillis() - cur) + " millis");


    }


    private void fullSegment(String inputPath, String outputPath) {

        Segmentation segmentation = new MaxLengthMatching(true);
        long cur = System.currentTimeMillis();
        trie.initWithDefaultWords();
        System.out.println("load dictionary cost " + (System.currentTimeMillis() - cur) + " millis");
        segmentation.setDictionary(trie);

        cur = System.currentTimeMillis();
        List<String> segged = segmentation.seg(FileUtil.readFromFile(inputPath));
        System.out.println(segged);
        System.out.println("segmentation cost " + (System.currentTimeMillis() - cur) + " millis");

        StringBuilder builder = new StringBuilder("");
        for (String s : segged) {
            builder.append(s);
            builder.append(" ");
        }
        FileUtil.writeToFile(outputPath, builder.toString());

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
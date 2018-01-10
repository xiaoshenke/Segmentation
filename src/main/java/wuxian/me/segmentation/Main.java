package wuxian.me.segmentation;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wuxian.me.segmentation.core.DictionaryTrie;
import wuxian.me.segmentation.util.FileUtil;

import java.io.File;
import java.util.List;
import java.util.Properties;

/**
 * Created by wuxian on 31/12/2017.
 */
public class Main {
    static Logger LOGGER = LoggerFactory.getLogger(Main.class);

    //若路径/conf/log4j.properties存在,那么重新初始化log4j
    private static void mayReinitLog4j() {
        File file = new File(FileUtil.getCurrentPath() + "/conf/log4j.properties");
        if (file.exists() && file.isFile()) {
            PropertyConfigurator.configure(file.getAbsolutePath());
        }
    }

    public static void main(String[] args) throws Exception {
        mayReinitLog4j();

        if (args == null || args.length < 1) {
            throw new IllegalArgumentException(
                    "must give these args: inputString or inputPath | outputPath");
        }

        String str = String.valueOf(args[0]);
        if (str.length() == 0) {
            throw new IllegalArgumentException("first argument can't be empty");
        }

        String output = null;
        if (args.length >= 2) {
            output = String.valueOf(args[1]);
        }

        boolean isFile = false;
        File file = null;
        file = new File(str);

        if (file.exists() && file.isFile()) {
            isFile = true;
        }

        if (!isFile) {
            file = new File(FileUtil.getCurrentPath() + "/" + str);
            if (!file.exists() || !file.isFile()) {
                isFile = true;
            }
        }

        String content = isFile ? FileUtil.readFromFile(file.getAbsolutePath()) : str;

        Segmentation segmentation = new MaxLengthMatching(true);
        long cur = System.nanoTime();
        DictionaryTrie trie = DictionaryTrie.getIns();
        trie.initWithDefaultWords();
        //trie.show();
        LOGGER.info("load dictionary cost " + (System.nanoTime() - cur) / 1000000 + " millis");
        segmentation.setDictionary(trie);

        cur = System.nanoTime();
        List<String> segged = segmentation.seg(content);
        LOGGER.info("segmentation cost " + (System.nanoTime() - cur) / 1000000 + " millis");

        StringBuilder builder = new StringBuilder("");
        for (String s : segged) {
            builder.append(s);
            builder.append(" ");
        }

        if (output != null) {
            FileUtil.writeToFile(output, builder.toString());
        } else {
            LOGGER.info("--------------------- result ---------------------");
            LOGGER.info(builder.toString());
        }

    }
}

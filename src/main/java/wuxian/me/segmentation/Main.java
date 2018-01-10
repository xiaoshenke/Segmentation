package wuxian.me.segmentation;

import wuxian.me.segmentation.core.DictionaryTrie;
import wuxian.me.segmentation.util.FileUtil;

import java.io.File;
import java.util.List;

/**
 * Created by wuxian on 31/12/2017.
 */
public class Main {

    public static void main(String[] args) throws Exception {

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
        System.out.println("load dictionary cost " + (System.nanoTime() - cur) / 1000000 + " millis");
        segmentation.setDictionary(trie);

        cur = System.nanoTime();
        List<String> segged = segmentation.seg(content);
        System.out.println("segmentation cost " + (System.nanoTime() - cur) / 1000000 + " millis");

        StringBuilder builder = new StringBuilder("");
        for (String s : segged) {
            builder.append(s);
            builder.append(" ");
        }

        if (output != null) {
            FileUtil.writeToFile(output, builder.toString());
        } else {
            System.out.println("--------------------- result ---------------------");
            System.out.println(builder.toString());
        }

    }
}

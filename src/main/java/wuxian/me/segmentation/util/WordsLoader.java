package wuxian.me.segmentation.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by wuxian on 17/12/2017.
 */
public class WordsLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(WordsLoader.class);

    private WordsLoader() {
    }

    //当前只支持所有resource包下的txt文件
    public static List<String> loadWords() {
        return loadByPaths("classpath:dic.txt");
    }

    public static List<String> loadPunctuations() {
        return loadByPaths("classpath:punctuation.txt");
    }

    public static List<String> loadStopWord() {
        return loadByPaths("classpath:stopword.txt");
    }

    //resourcePaths 多个资源路径，用逗号分隔
    public static List<String> loadByPaths(String resourcePaths) {
        resourcePaths = resourcePaths.trim();
        if ("".equals(resourcePaths)) {
            LOGGER.info("没有资源可以加载");
            return new ArrayList<>(0);
        }
        LOGGER.info("开始加载资源");
        LOGGER.info(resourcePaths);
        long start = System.currentTimeMillis();
        List<String> result = new ArrayList<>();
        for (String resource : resourcePaths.split("[,，]")) {
            try {
                resource = resource.trim();
                if (resource.startsWith("classpath:")) {
                    result.addAll(loadClasspathResource(resource.replace("classpath:", "")));
                }
            } catch (Exception e) {
                LOGGER.error("加载资源失败：" + resource, e);
            }
        }
        LOGGER.info("加载资源 " + result.size() + " 行");
        //调用自定义加载逻辑
        long cost = System.currentTimeMillis() - start;
        LOGGER.info("完成加载资源，耗时" + cost + " 毫秒");
        return result;
    }


    private static List<String> loadClasspathResource(String resource) throws IOException {
        List<String> result = new ArrayList<>();
        LOGGER.info("类路径资源：" + resource);
        Enumeration<URL> ps = WordsLoader.class.getClassLoader().getResources(resource);
        while (ps.hasMoreElements()) {
            URL url = ps.nextElement();
            LOGGER.info("类路径资源URL：" + url);
            if (url.getFile().contains(".jar!")) {
                continue;
            }
            File file = new File(url.getFile());
            boolean dir = file.isDirectory();
            if (dir) {
                continue;
            } else {
                //处理文件
                result.addAll(load(file.getAbsolutePath()));
            }
        }
        return result;
    }

    //注意path只需要文件名即可
    //不加载以#开头的单词
    private static List<String> load(String path) {
        List<String> result = new ArrayList<>();
        try {
            InputStream in = null;
            LOGGER.info("加载资源：" + path);
            if (path.startsWith("classpath:")) {
                in = WordsLoader.class.getClassLoader().getResourceAsStream(path.replace("classpath:", ""));
            } else {
                in = new FileInputStream(path);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf-8"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if ("".equals(line) || line.startsWith("#")) {
                        continue;
                    }
                    result.add(line);
                }
            }
        } catch (Exception e) {
            LOGGER.error("加载资源失败：" + path, e);
        }
        return result;
    }

}

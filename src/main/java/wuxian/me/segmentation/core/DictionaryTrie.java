package wuxian.me.segmentation.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wuxian.me.segmentation.util.WordsLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//thread safe implementation
public class DictionaryTrie implements Dictionary {
    private static final Logger LOGGER = LoggerFactory.getLogger(DictionaryTrie.class);
    //词表的首字母数量在一个可控范围内，默认值为24000
    private static final int INDEX_LENGTH = 24000;
    private final TrieNode[] ROOT_NODES_INDEX = new TrieNode[INDEX_LENGTH];
    private int maxLength;

    private static DictionaryTrie dictionaryTrie = null;

    public static DictionaryTrie getIns() {
        if (dictionaryTrie == null) {
            synchronized (DictionaryTrie.class) {
                if (dictionaryTrie == null) {
                    dictionaryTrie = new DictionaryTrie();
                }
            }
        }

        return dictionaryTrie;
    }

    public void initWithDefaultWords() {
        addAll(WordsLoader.loadWords());
    }

    private DictionaryTrie() {
        LOGGER.info("初始化词典：" + this.getClass().getName());
    }

    @Override
    public synchronized void clear() {
        for (int i = 0; i < INDEX_LENGTH; i++) {
            ROOT_NODES_INDEX[i] = null;
        }
    }

    /**
     * 统计根节点冲突情况及预分配的数组空间利用情况
     */
    public synchronized void showConflict() {
        int emptySlot = 0;
        //key:冲突长度 value:冲突个数
        Map<Integer, Integer> map = new HashMap<>();
        for (TrieNode node : ROOT_NODES_INDEX) {
            if (node == null) {
                emptySlot++;
            } else {
                int i = 0;
                while ((node = node.getSibling()) != null) {
                    i++;
                }
                if (i > 0) {
                    Integer count = map.get(i);
                    if (count == null) {
                        count = 1;
                    } else {
                        count++;
                    }
                    map.put(i, count);
                }
            }
        }
        int count = 0;
        for (int key : map.keySet()) {
            int value = map.get(key);
            count += key * value;
            LOGGER.info("冲突次数为：" + key + " 的元素个数：" + value);
        }
        LOGGER.info("冲突次数：" + count);
        LOGGER.info("总槽数：" + INDEX_LENGTH);
        LOGGER.info("用槽数：" + (INDEX_LENGTH - emptySlot));
        LOGGER.info("使用率：" + (float) (INDEX_LENGTH - emptySlot) / INDEX_LENGTH * 100 + "%");
        LOGGER.info("剩槽数：" + emptySlot);
    }

    /**
     * 获取字符对应的根节点
     * 如果节点不存在
     * 则增加根节点后返回新增的节点
     *
     * @param character 字符
     * @return 字符对应的根节点
     */
    private synchronized TrieNode getRootNodeIfNotExistThenCreate(char character) {
        TrieNode trieNode = getRootNode(character);
        if (trieNode == null) {
            trieNode = new TrieNode(character);
            addRootNode(trieNode);
        }
        return trieNode;
    }

    /**
     * 新增一个根节点
     *
     * @param rootNode 根节点
     */
    private synchronized void addRootNode(TrieNode rootNode) {
        //计算节点的存储索引
        int index = rootNode.getCharacter() % INDEX_LENGTH;
        //检查索引是否和其他节点冲突
        TrieNode existTrieNode = ROOT_NODES_INDEX[index];
        if (existTrieNode != null) {
            //有冲突，将冲突节点附加到当前节点之后
            rootNode.setSibling(existTrieNode);
        }
        //新增的节点总是在最前
        ROOT_NODES_INDEX[index] = rootNode;
    }

    /**
     * 获取字符对应的根节点
     * 如果不存在，则返回NULL
     *
     * @param character 字符
     * @return 字符对应的根节点
     */
    private synchronized TrieNode getRootNode(char character) {
        //计算节点的存储索引
        int index = character % INDEX_LENGTH;
        TrieNode trieNode = ROOT_NODES_INDEX[index];
        while (trieNode != null && character != trieNode.getCharacter()) {
            //如果节点和其他节点冲突，则需要链式查找
            trieNode = trieNode.getSibling();
        }
        return trieNode;
    }

    public synchronized List<String> prefix(String prefix) {
        List<String> result = new ArrayList<>();
        //去掉首尾空白字符
        prefix = prefix.trim();
        int len = prefix.length();
        if (len < 1) {
            return result;
        }
        //从根节点开始查找
        //获取根节点
        TrieNode node = getRootNode(prefix.charAt(0));
        if (node == null) {
            //不存在根节点，结束查找
            return result;
        }
        //存在根节点，继续查找
        for (int i = 1; i < len; i++) {
            char character = prefix.charAt(i);
            TrieNode child = node.getChild(character);
            if (child == null) {
                //未找到匹配节点
                return result;
            } else {
                //找到节点，继续往下找
                node = child;
            }
        }
        for (TrieNode item : node.getChildren()) {
            result.add(prefix + item.getCharacter());
        }
        return result;
    }

    @Override
    public synchronized boolean contains(String item) {
        return contains(item, 0, item.length());
    }

    @Override
    public synchronized boolean contains(String item, int start, int length) {
        if (start < 0 || length < 1) {
            return false;
        }
        if (item == null || item.length() < length) {
            return false;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("开始查词典：{}", item.substring(start, start + length));
        }
        //从根节点开始查找
        //获取根节点
        TrieNode node = getRootNode(item.charAt(start));
        if (node == null) {
            //不存在根节点，结束查找
            return false;
        }
        //存在根节点，继续查找
        for (int i = 1; i < length; i++) {
            char character = item.charAt(i + start);
            TrieNode child = node.getChild(character);
            if (child == null) {
                //未找到匹配节点
                return false;
            } else {
                //找到节点，继续往下找
                node = child;
            }
        }
        if (node.isTerminal()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("在词典中查到词：{}", item.substring(start, start + length));
            }
            return true;
        }
        return false;
    }

    @Override
    public synchronized void removeAll(List<String> items) {
        for (String item : items) {
            remove(item);
        }
    }

    @Override
    public synchronized void remove(String item) {
        if (item == null || item.isEmpty()) {
            return;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("从词典中移除词：{}", item);
        }
        //从根节点开始查找
        //获取根节点
        TrieNode node = getRootNode(item.charAt(0));
        if (node == null) {
            //不存在根节点，结束查找
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("词不存在：{}", item);
            }
            return;
        }
        int length = item.length();
        //存在根节点，继续查找
        for (int i = 1; i < length; i++) {
            char character = item.charAt(i);
            TrieNode child = node.getChild(character);
            if (child == null) {
                //未找到匹配节点
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("词不存在：{}", item);
                }
                return;
            } else {
                //找到节点，继续往下找
                node = child;
            }
        }
        if (node.isTerminal()) {
            //设置为非叶子节点，效果相当于从词典中移除词
            node.setTerminal(false);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("成功从词典中移除词：{}", item);
            }
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("词不存在：{}", item);
            }
        }
    }

    @Override
    public synchronized void addAll(List<String> items) {
        for (String item : items) {
            add(item);
        }
    }

    @Override
    public synchronized void add(String item) {
        //去掉首尾空白字符
        item = item.trim();
        int len = item.length();
        if (len < 1) {
            //长度小于1则忽略
            return;
        }
        if (len > maxLength) {
            maxLength = len;
        }
        //从根节点开始添加
        //获取根节点
        TrieNode node = getRootNodeIfNotExistThenCreate(item.charAt(0));
        for (int i = 1; i < len; i++) {
            char character = item.charAt(i);
            TrieNode child = node.getChildIfNotExistThenCreate(character);
            //改变顶级节点
            node = child;
        }
        //设置终结字符，表示从根节点遍历到此是一个合法的词
        node.setTerminal(true);
    }

    @Override
    public int getMaxLength() {
        return maxLength;
    }

    public synchronized void show(char character) {
        show(getRootNode(character), "");
    }

    public synchronized void show() {
        for (TrieNode node : ROOT_NODES_INDEX) {
            if (node != null) {
                show(node, "");
            }
        }
    }

    private synchronized void show(TrieNode node, String indent) {
        if (node.isTerminal()) {
            LOGGER.info(indent + node.getCharacter() + "(T)");
        } else {
            LOGGER.info(indent + node.getCharacter());
        }
        for (TrieNode item : node.getChildren()) {
            show(item, indent + "\t");
        }
    }

}
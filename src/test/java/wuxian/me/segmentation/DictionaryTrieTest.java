package wuxian.me.segmentation;

import org.junit.Before;
import org.junit.Test;
import wuxian.me.segmentation.core.DictionaryTrie;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class DictionaryTrieTest {
    private DictionaryTrie trie = null;

    @Before
    public void setUp() {
        trie = DictionaryTrie.getIns();
        trie.add("今年");
        trie.add("马上");
        trie.add("迎来");
        trie.add("结尾");
        trie.add("明年");
        trie.add("今天");
        trie.add("上学");
        trie.add("上学校");
        trie.add("上海");
    }

    @Test
    public void testPrefix() {
        String prefix = "上";
        List<String> result = trie.prefix(prefix);
        assertTrue(result.contains("上学"));
        assertTrue(result.contains("上海"));

        prefix = "上学";
        result = trie.prefix(prefix);
        assertTrue(result.contains("上学校"));
    }

    @Test
    public void testContains() {
        assertTrue(trie.contains("上学"));
    }
}
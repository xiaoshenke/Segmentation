package wuxian.me.segmentation;


import wuxian.me.segmentation.core.Dictionary;

import java.util.List;

/**
 * Created by wuxian on 16/12/2017.
 */
public interface Segmentation {

    List<String> seg(String text);

    void setDictionary(Dictionary dictionary);

    Dictionary getDictionary();
}

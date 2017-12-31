package wuxian.me.segmentation.core;

import java.util.List;

/**
 * Created by wuxian on 16/12/2017.
 */
public interface Dictionary {

    int getMaxLength();

    boolean contains(String item, int start, int length);

    boolean contains(String item);

    void addAll(List<String> items);

    void add(String item);

    void removeAll(List<String> items);

    void remove(String item);

    void clear();
}

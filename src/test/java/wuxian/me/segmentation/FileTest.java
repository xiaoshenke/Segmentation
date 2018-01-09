package wuxian.me.segmentation;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Created by wuxian on 9/1/2018.
 */
public class FileTest {

    @Test
    public void testFile() {
        File file = new File("hello world");
        System.out.println(file.isFile());
        System.out.println(file.exists());
    }

}
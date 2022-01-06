package com.shdata.oip.test;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.junit.Test;

/**
 * @author wangwj
 * @version 1.0
 * @date 2022/1/5
 */

public class IdWorkerTest {

    @Test
    public void longIdTest() {
        System.out.println(IdWorker.getId());
    }
}

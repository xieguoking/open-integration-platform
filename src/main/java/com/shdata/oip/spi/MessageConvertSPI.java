package com.shdata.oip.spi;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * 通讯格式的转换
 *
 * @author xieguojun
 * @author (2021 / 12 / 17 add by xieguojun)
 * @version 1.0
 * @since 1.0
 */
public interface MessageConvertSPI {

    /**
     * 消息格式转换
     *
     * @param input  输入消息内容
     * @param config 转换配置，可以配置映射，转换策略
     * @return
     */
    OutputStream convert(InputStream input, MessageConvertConfig config);


}

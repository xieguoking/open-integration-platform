package com.shdata.osp.spi;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * 定义通讯协议转换：按照指定通讯协议访问远端服务
 *
 * @author xieguojun
 * @author (2021 / 12 / 17 add by xieguojun)
 * @version 1.0
 * @since 1.0
 */
public interface ProtocolProxySPI {

    /**
     * 执行远程访问
     *
     * @param request 待发送给远端的流信息
     * @return
     */
    OutputStream invoke(InputStream request);


}

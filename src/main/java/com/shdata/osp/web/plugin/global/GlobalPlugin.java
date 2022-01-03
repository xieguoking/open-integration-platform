package com.shdata.osp.web.plugin.global;

import com.shdata.osp.web.plugin.OspPlugin;
import com.shdata.osp.web.plugin.OspPluginChain;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author wangwj
 * @version 1.0
 * @date 2022/1/3
 */

public class GlobalPlugin implements OspPlugin {

    @Override
    public String named() {
        return null;
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public boolean skip(HttpServletRequest httpServletRequest) {
        return true;
    }

    @Override
    public void execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, OspPluginChain ospPluginChain) throws IOException {

    }
}

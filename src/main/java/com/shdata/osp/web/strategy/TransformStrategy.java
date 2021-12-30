package com.shdata.osp.web.strategy;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface TransformStrategy {

    String named();

    Map<String, String> resolve(HttpServletRequest httpServletRequest);
}

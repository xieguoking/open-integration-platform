package com.shdata.oip.modular.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shdata.oip.modular.dao.ApisMapper;
import com.shdata.oip.modular.model.po.Apis;
import com.shdata.oip.modular.service.IApisService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wangwj
 * @since 2022-01-05
 */
@Service
public class ApisServiceImpl extends ServiceImpl<ApisMapper, Apis> implements IApisService {

}

package com.shdata.oip.modular.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shdata.oip.core.common.OipConstants;
import com.shdata.oip.core.spi.VirtualService;
import com.shdata.oip.modular.dao.VirtualServiceRegitryMapper;
import com.shdata.oip.modular.model.po.VirtualServiceRegistry;
import com.shdata.oip.modular.service.IVirtualServiceRegistryService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wangwj
 * @since 2022-01-05
 */
@Service
public class VirtualServiceRegistryServiceImpl extends ServiceImpl<VirtualServiceRegitryMapper, VirtualServiceRegistry> implements IVirtualServiceRegistryService {


    @Override
    public void VirtualServiceUp(VirtualService virtualService) {
        VirtualServiceRegistry virtualServiceRegistry = this.getOne(new QueryWrapper<VirtualServiceRegistry>().lambda().eq(VirtualServiceRegistry::getServiceID, virtualService.getService()));
        if (Objects.isNull(virtualServiceRegistry)) {
            virtualServiceRegistry = new VirtualServiceRegistry();

        }

        virtualServiceRegistry.setServiceName(virtualService.getService());
        virtualServiceRegistry.setServiceType(virtualService.getMetadata().get(OipConstants.KEY_SERVICE_TYPE));
        virtualServiceRegistry.setIp(virtualService.getIp());
        virtualServiceRegistry.setPort(virtualService.getPort());
        virtualServiceRegistry.setMetadata(JSONUtil.toJsonStr(virtualService.getMetadata()));
        virtualServiceRegistry.setRegTime(DateUtil.parseDateTime(virtualService.getMetadata().get(OipConstants.KEY_SERVICE_REG_TIME)));
        virtualServiceRegistry.setLastTime(new Date());
        this.saveOrUpdate(virtualServiceRegistry);
    }
}

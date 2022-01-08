package com.shdata.oip.core.dubbo;


import com.shdata.oip.core.dubbo.po.MetaData;

public interface DubboMetaDataManager {

    /**
     * key = interfaceName : version : group
     */
    MetaData getMetaData(String key);

}

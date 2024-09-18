package api.development.platform.service.impl.inner;

import api.development.apiplatform_interface.model.entity.InterfaceInfo;
import api.development.apiplatform_interface.service.InnerInterfaceInfoService;
import api.development.platform.common.ErrorCode;
import api.development.platform.exception.BusinessException;
import api.development.platform.mapper.InterfaceInfoMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * 内部接口服务实现类
 * 注册到dubbo，提供远程调用
 */
@DubboService
public class  InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {

    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;

    @Override
    public InterfaceInfo getInterfaceInfo(String url, String method) {
        if (StringUtils.isAnyBlank(url, method)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("interfaceUrl", url);
        queryWrapper.eq("interfaceType", method);
        return interfaceInfoMapper.selectOne(queryWrapper);
    }
}


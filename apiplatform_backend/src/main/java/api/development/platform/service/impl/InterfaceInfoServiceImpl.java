package api.development.platform.service.impl;

import api.development.apiplatform_interface.model.entity.InterfaceInfo;
import api.development.platform.common.ErrorCode;
import api.development.platform.exception.BusinessException;
import api.development.platform.mapper.InterfaceInfoMapper;
import api.development.platform.service.InterfaceInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
* @author poise
* @description 针对表【interface_info(接口信息表)】的数据库操作Service实现
* @createDate 2024-05-26 19:48:30
*/
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
    implements InterfaceInfoService{

    /**
     * 接口参数校验
     * @param interfaceInfo
     * @param add
     */
    @Override
    public void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add) {
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String interfaceName = interfaceInfo.getInterfaceName();
        // 创建时，参数不能为空
        if (add) {
            if (StringUtils.isAnyBlank(interfaceName)){
                throw  new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(interfaceName) && interfaceName.length()    > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
    }
}





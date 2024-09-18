package api.development.platform.service.impl;

import api.development.apiplatform_interface.model.entity.InterfaceInfo;
import api.development.apiplatform_interface.model.entity.User;
import api.development.apiplatform_interface.model.entity.UserInterfaceInfo;
import api.development.platform.common.ErrorCode;
import api.development.platform.exception.BusinessException;
import api.development.platform.service.InterfaceInfoService;
import api.development.platform.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import api.development.platform.service.UserInterfaceInfoService;
import api.development.platform.mapper.UserInterfaceInfoMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
* @author poise
* @description 针对表【user_interface_info(`userInterfaceInfo`)】的数据库操作Service实现
* @createDate 2024-07-03 17:43:09
*/
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
    implements UserInterfaceInfoService{
    @Resource
    private InterfaceInfoService interfaceInfoService;



    @Override
    public void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add) {
        if (userInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 创建时，所有参数必须非空
        if (add) {
            if (userInterfaceInfo.getInterfaceInfoId() <= 0 || userInterfaceInfo.getUserId() <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口或用户不存在");
            }
        }
        if (userInterfaceInfo.getLeftNum() < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "剩余次数不能小于 0");
        }
    }

    /**
     * 接口调用成功，修改用户接口信息表的数据
     * @param interfaceId
     * @param userId
     * @return
     */
    @Override
    public boolean  invokeCount(long interfaceId, long userId) {
        if (interfaceId <= 0 || userId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 用户的调用次数减少
        UpdateWrapper<UserInterfaceInfo> updateInvokeCount = new UpdateWrapper<>();
        updateInvokeCount.eq("interfaceInfoId", interfaceId);
        updateInvokeCount.eq("userId",userId);
        updateInvokeCount.setSql("leftNum = leftNum - 1, totalNum = totalNum + 1");

        return this.update(updateInvokeCount);
    }
}





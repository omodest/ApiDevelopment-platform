package api.development.platform.service.impl;

import api.development.apiplatform_interface.model.entity.InterfaceInfo;
import api.development.apiplatform_interface.model.entity.User;
import api.development.apiplatform_interface.model.entity.UserInterfaceInfo;
import api.development.platform.common.ErrorCode;
import api.development.platform.exception.BusinessException;
import api.development.platform.mapper.UserInterfaceInfoMapper;
import api.development.platform.service.InterfaceInfoService;
import api.development.platform.service.UserInterfaceInfoService;
import api.development.platform.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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

    @Resource
    private UserService userService;

    @Override
    public void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add) {
        User user = userService.getById(userInterfaceInfo.getUserId());
        int kunCoin = user.getKunCoin();
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
            if (kunCoin <= 0){
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"用户剩余调用次数不足");
            }
            try {
                kunCoin--;
                user.setKunCoin(kunCoin);
                userService.updateById(user);
            } catch (Exception e) {
                // 处理保存失败的情况
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"钱不够");
            }
        }
    }

    /**
     * 接口调用成功，修改用户接口信息表的数据（用户的调用次数减少）
     * @param interfaceId
     * @param userId
     * @return
     */
    @Override
    public boolean invokeCount(long interfaceId, long userId) {
        // 参数校验
        if (interfaceId <= 0 || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 查询用户接口信息
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId).eq("interfaceId", interfaceId);

        UserInterfaceInfo userInterfaceInfo = this.getOne(queryWrapper);

        // 检查用户接口信息是否存在
        if (userInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "没有找到接口数据"); // 或者抛出一个业务异常，表示没有找到该记录
        }

        // 判断剩余调用次数
        if (userInterfaceInfo.getLeftNum() <= 0) {
            // 调用次数不足，尝试扣除积分
            return reducePoint(userId);
        }

        // 更新调用次数
        userInterfaceInfo.setLeftNum(userInterfaceInfo.getLeftNum() - 1);
        userInterfaceInfo.setTotalNum(userInterfaceInfo.getTotalNum() + 1);

        return this.updateById(userInterfaceInfo);
    }


    public boolean reducePoint(long userId){
        User byId = userService.getById(userId);
        if (byId.getKunCoin() <= 0){
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "积分不足");
        }
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("id", userId);
        return userService.update(userQueryWrapper);
    }
}





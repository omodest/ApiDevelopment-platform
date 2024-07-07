package api.development.platform.service;

import api.development.apiplatform_interface.model.entity.UserInterfaceInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author poise
* @description 针对表【user_interface_info(`userInterfaceInfo`)】的数据库操作Service
* @createDate 2024-07-03 17:43:09
*/
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {
    /**
     * 校验
     *
     * @param userInterfaceInfo
     * @param add 需要调用时传的所有参数都为空
     */
    void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add);

    /**
     * 接口调用次数加 1 (数据库中有以 interfaceId 和 userId为联合主键的数据时，才调用这个函数，否则执行userInterfaceInfo add函数就行)
     * @param interfaceId
     * @param userId
     * @return
     */
    boolean invokeCount(long interfaceId,long userId);
}

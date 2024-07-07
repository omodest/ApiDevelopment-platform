package api.development.platform.mapper;

import api.development.apiplatform_interface.model.entity.UserInterfaceInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author poise
* @description 针对表【user_interface_info(`userInterfaceInfo`)】的数据库操作Mapper
* @createDate 2024-07-03 17:43:09
* @Entity api.development.platform.model.entity.UserInterfaceInfo
*/
public interface UserInterfaceInfoMapper extends BaseMapper<UserInterfaceInfo> {
    List<UserInterfaceInfo> listTopInvokeInterfaceInfo(int limit);
}





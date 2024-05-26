package api.development.platform.service;


import api.development.platform.model.entity.InterfaceInfo;
import api.development.platform.model.entity.Post;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author poise
* @description 针对表【interface_info(接口信息表)】的数据库操作Service
* @createDate 2024-05-26 19:48:30
*/
public interface InterfaceInfoService extends IService<InterfaceInfo> {
    /**
     * 校验
     *
     * @param interfaceInfo
     * @param add
     */
    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);
}

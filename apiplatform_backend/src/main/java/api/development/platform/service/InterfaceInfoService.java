package api.development.platform.service;


import api.development.apiplatform_interface.model.entity.InterfaceInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

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

    /**
     * 给接口信息添加一个 调用次数字段
     * @param interfaceInfoList
     * @return
     */
    List<InterfaceInfo> InterfaceAddTotalInvoke(List<InterfaceInfo> interfaceInfoList);

    /**
     * 给接口信息添加一个 调用次数字段
     * @param interfaceInfoList
     * @return
     */
    Page<InterfaceInfo> PageInterfaceAddTotalInvoke(Page<InterfaceInfo> interfaceInfoList, int currentPage, int pageSize);
}

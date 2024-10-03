package api.development.platform.service.impl;

import api.development.apiplatform_interface.model.entity.InterfaceInfo;
import api.development.apiplatform_interface.model.entity.UserInterfaceInfo;
import api.development.platform.common.ErrorCode;
import api.development.platform.exception.BusinessException;
import api.development.platform.mapper.InterfaceInfoMapper;
import api.development.platform.mapper.UserInterfaceInfoMapper;
import api.development.platform.service.InterfaceInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @author poise
* @description 针对表【interface_info(接口信息表)】的数据库操作Service实现
* @createDate 2024-05-26 19:48:30
*/
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
    implements InterfaceInfoService{

    @Resource
    private UserInterfaceInfoMapper  userInterfaceInfoMapper;

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

    @Override
    public List<InterfaceInfo> InterfaceAddTotalInvoke(List<InterfaceInfo> interfaceInfoList) {
        if (interfaceInfoList.isEmpty()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口列表为空");
        }

        // 用来保存最终的结果
        List<InterfaceInfo> resultList = new ArrayList<>();

        // 获取接口ID列表
        List<Long> interfaceIds = interfaceInfoList.stream()
                .map(InterfaceInfo::getId)
                .collect(Collectors.toList());

        // 使用 QueryWrapper 来构造查询
        QueryWrapper<UserInterfaceInfo> userInterfaceInfoQueryWrapper = new QueryWrapper<>();
        userInterfaceInfoQueryWrapper.select("interfaceInfoId, SUM(totalNum) as total_invokes")
                .in("interfaceInfoId", interfaceIds)
                .groupBy("interfaceInfoId");

        // 执行查询，获取结果
        List<Map<String, Object>> totalInvokesList = userInterfaceInfoMapper.selectMaps(userInterfaceInfoQueryWrapper);

        // 创建一个 Map 加速查找
        Map<Long, Long> totalInvokesMap = totalInvokesList.stream()
                .collect(Collectors.toMap(
                        map -> ((Number) map.get("interfaceInfoId")).longValue(),  // 获取 interfaceInfoId
                        map -> ((Number) map.get("total_invokes")).longValue() // 获取对应的调用总次数
                ));

        // 遍历原来的 InterfaceInfo 列表，并设置 totalInvokes
        for (InterfaceInfo interfaceInfo : interfaceInfoList) {
            Long totalInvokes = totalInvokesMap.get(interfaceInfo.getId());
            if (totalInvokes != null) {
                interfaceInfo.setTotalInvokes(Math.toIntExact(totalInvokes));  // 假设 InterfaceInfo 类有 setTotalInvokes 方法
            } else {
                interfaceInfo.setTotalInvokes(0); // 如果没有对应的调用次数，设置为 0
            }
            resultList.add(interfaceInfo); // 添加到结果列表中
        }

        return resultList;
    }
    @Override
    public Page<InterfaceInfo> PageInterfaceAddTotalInvoke(Page<InterfaceInfo> interfaceInfoList, int currentPage, int pageSize) {
        // 检查接口列表是否为空
        if (ObjectUtils.isEmpty(interfaceInfoList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口列表为空");
        }

        // 获取接口ID列表
        List<Long> interfaceIds = interfaceInfoList.getRecords().stream()
                .map(InterfaceInfo::getId)
                .collect(Collectors.toList());

        // 使用 QueryWrapper 来构造查询
        QueryWrapper<UserInterfaceInfo> userInterfaceInfoQueryWrapper = new QueryWrapper<>();
        userInterfaceInfoQueryWrapper.select("interfaceInfoId, SUM(totalNum) as total_invokes")
                .in("interfaceInfoId", interfaceIds)
                .groupBy("interfaceInfoId");

        // 执行查询，获取结果
        List<Map<String, Object>> totalInvokesList = userInterfaceInfoMapper.selectMaps(userInterfaceInfoQueryWrapper);

        // 创建一个 Map 加速查找
        Map<Long, Long> totalInvokesMap = totalInvokesList.stream()
                .collect(Collectors.toMap(
                        map -> ((Number) map.get("interfaceInfoId")).longValue(),  // 获取 interfaceInfoId
                        map -> ((Number) map.get("total_invokes")).longValue() // 获取对应的调用总次数
                ));

        // 更新 InterfaceInfo 列表并设置 totalInvokes，使用流式编程提高可读性
        List<InterfaceInfo> updatedInterfaceInfoList = interfaceInfoList.getRecords().stream()
                .map(interfaceInfo -> {
                    Long totalInvokes = totalInvokesMap.get(interfaceInfo.getId());
                    interfaceInfo.setTotalInvokes(totalInvokes != null ? Math.toIntExact(totalInvokes) : 0);
                    return interfaceInfo;
                })
                .collect(Collectors.toList());

        // 计算总数
        long totalCount = updatedInterfaceInfoList.size();

        // 进行分页处理
        long start = (currentPage - 1) * pageSize;
        long end = Math.min(start + pageSize, totalCount);

        // 安全处理边界情况
        if (start >= totalCount) {
            return new Page<>(); // 返回空结果
        }

        List<InterfaceInfo> pagedResult = updatedInterfaceInfoList.subList((int) start, (int) end);

        // 构造并设置分页信息
        Page<InterfaceInfo> page = new Page<>(currentPage, pageSize);
        page.setRecords(pagedResult);
        page.setTotal(totalCount);

        return page;
    }
}





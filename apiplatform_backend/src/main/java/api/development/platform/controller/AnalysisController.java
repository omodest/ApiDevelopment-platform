package api.development.platform.controller;

import api.development.apiplatform_interface.model.entity.InterfaceInfo;
import api.development.apiplatform_interface.model.entity.UserInterfaceInfo;
import api.development.platform.annotation.AuthCheck;
import api.development.platform.common.BaseResponse;
import api.development.platform.common.ErrorCode;
import api.development.platform.common.ResultUtils;
import api.development.platform.exception.BusinessException;
import api.development.platform.mapper.UserInterfaceInfoMapper;
import api.development.platform.model.vo.InterfaceInfoVO;
import api.development.platform.service.InterfaceInfoService;
import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分析控制器
 */
@RestController
@RequestMapping("/analysis")
@Slf4j
public class AnalysisController {

    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @GetMapping("/top/interface/invoke")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<List<InterfaceInfoVO>> listTopInvokeInterfaceInfo() {
        // 查询userInterfaceInfo 调用次数最高的三个接口，封装到List
        List<UserInterfaceInfo> userInterfaceInfoList = userInterfaceInfoMapper.listTopInvokeInterfaceInfo(3);
        // 对userInterfaceInfo 分组，作用就是用来计数（这里的键是id，值就是interfaceInfoId）
        Map<Long, List<UserInterfaceInfo>> interfaceInfoIdObjMap = userInterfaceInfoList.stream()
                .collect(Collectors.groupingBy(UserInterfaceInfo::getInterfaceInfoId));
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", interfaceInfoIdObjMap.keySet());
        // 按照 id查询，这里的list 用来给返回前端的vo对象赋值
        List<InterfaceInfo> list = interfaceInfoService.list(queryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        // 使用流特性给返回给前端的vo对象设置：totalNum(调用总次数：用来前端绘制图表)；
        List<InterfaceInfoVO> interfaceInfoVOList = list.stream().map(interfaceInfo -> {
            InterfaceInfoVO interfaceInfoVO = new InterfaceInfoVO();
            BeanUtils.copyProperties(interfaceInfo, interfaceInfoVO);
            int totalNum = interfaceInfoIdObjMap.get(interfaceInfo.getId()).get(0).getTotalNum();
            interfaceInfoVO.setTotalNum(totalNum);
            return interfaceInfoVO;
        }).collect(Collectors.toList());

        return ResultUtils.success(interfaceInfoVOList);
    }
}


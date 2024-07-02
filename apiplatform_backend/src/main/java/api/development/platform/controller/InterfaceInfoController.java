package api.development.platform.controller;

import api.development.apiplatform_client_sdk.client.NameClient;
import api.development.platform.annotation.AuthCheck;
import api.development.platform.common.*;
import api.development.platform.constant.CommonConstant;
import api.development.platform.constant.UserConstant;
import api.development.platform.exception.BusinessException;
import api.development.platform.exception.ThrowUtils;
import api.development.platform.model.dto.InterfaceInfo.InterfaceInfoAddRequest;
import api.development.platform.model.dto.InterfaceInfo.InterfaceInfoQueryRequest;
import api.development.platform.model.dto.InterfaceInfo.InterfaceInfoUpdateRequest;
import api.development.platform.model.entity.InterfaceInfo;
import api.development.platform.model.entity.User;
import api.development.platform.model.enums.InterfaceStatusEnum;
import api.development.platform.service.InterfaceInfoService;
import api.development.platform.service.UserService;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 接口信息
 */
@RestController // RESTful控制器的定义
@RequestMapping("/interfaceinfo")
@Slf4j
public class InterfaceInfoController {

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private UserService userService;

    @Resource
    private NameClient nameClient;

    // region 增删改查

    /**
     * 创建
     * @param interfaceInfoAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addInterfaceInfo(@RequestBody InterfaceInfoAddRequest interfaceInfoAddRequest, HttpServletRequest request) {
        if (interfaceInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoAddRequest, interfaceInfo); // 将 第一个javabean中的属性复制到第二个javabean
        interfaceInfoService.validInterfaceInfo(interfaceInfo, false);
        User loginUser = userService.getLoginUser(request);
        interfaceInfo.setUserId(loginUser.getId()); // 设置用户id
        boolean result = interfaceInfoService.save(interfaceInfo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR); // 自定义异常类
        long newInterfaceInfoId = interfaceInfo.getId();
        return ResultUtils.success(newInterfaceInfoId);
    }

    /**
     * 删除
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteInterfaceInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = interfaceInfoService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param interfaceInfoUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateInterfaceInfo(@RequestBody InterfaceInfoUpdateRequest interfaceInfoUpdateRequest,HttpServletRequest httpServletRequest) {
        if (interfaceInfoUpdateRequest == null || interfaceInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoUpdateRequest, interfaceInfo); //
        // 参数校验
        interfaceInfoService.validInterfaceInfo(interfaceInfo, true);
        long id = interfaceInfoUpdateRequest.getId();
        User user = userService.getLoginUser(httpServletRequest);
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅限管理员或创建者才能删除
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(httpServletRequest) ){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR,"仅限管理员或创建者才能删除");
        }
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 接口发布
     * @param idRequest
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/online")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE) // aop 自定义注解，检验是否是管理员
    public BaseResponse<Boolean> onlineInterfaceInfo(@RequestBody IdRequest idRequest, HttpServletRequest httpServletRequest){
        // 参数校验
        long id = idRequest.getId();
        if (id <= 0 || idRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 查询数据
        InterfaceInfo interfaceInfoServiceById = interfaceInfoService.getById(id);
        if (interfaceInfoServiceById == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 使用interface项目中的方法，验证接口是否能访问同
        // todo 目前这里仅是使用了测试接口，后续将接口改成数据库中相应数据
        api.development.apiplatform_client_sdk.model.User user = new api.development.apiplatform_client_sdk.model.User();
        user.setName("roqweqeot1"); // 这里不能出现中文，会乱码，导致在自定义sdk中不匹配，出现无权限错误
        String userNameByPost = nameClient.getUserNameByPost(user);
        if (StringUtils.isAnyBlank(userNameByPost) || StringUtils.contains(userNameByPost,"无权限")){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "接口验证错误");
        }
        // 修改状态为上线
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(id);
        interfaceInfo.setInterfaceStatus(InterfaceStatusEnum.ONLINE.getValue());
        boolean updateById = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(updateById);
    }

    /**
     * 下线
     * @param idRequest
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/offline")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE) // aop 自定义注解，检验是否是管理员
    public BaseResponse<Boolean> offlineInterfaceInfo(@RequestBody IdRequest idRequest, HttpServletRequest httpServletRequest){
        // 参数校验
        long id = idRequest.getId();
        if (id <= 0 || idRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 查询数据
        InterfaceInfo interfaceInfoServiceById = interfaceInfoService.getById(id);
        if (interfaceInfoServiceById == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        // 修改状态为上线
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(id);
        interfaceInfo.setInterfaceStatus(InterfaceStatusEnum.OFFLINE.getValue());
        boolean updateById = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(updateById);
    }


    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<InterfaceInfo> getInterfaceInfoVOById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(id);
        return ResultUtils.success(interfaceInfo);
    }

    /**
     * 分页获取列表（仅管理员）
     * 这里的page对象使用，需要对mybatis plus的分页功能进行配置（ mybatisPlusInterceptor中已配置）
     * @param interfaceInfoQueryRequest
     * @return
     */
    @GetMapping("/list")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<List<InterfaceInfo>> listInterfaceInfoByPage(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
        if (interfaceInfoQueryRequest != null) {
            BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceInfoQuery);
        List<InterfaceInfo> interfaceInfoList = interfaceInfoService.list(queryWrapper);
        return ResultUtils.success(interfaceInfoList);

    }

    /**
     * 分页获取列表（封装类）
     *
     * @param interfaceInfoQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<InterfaceInfo>> listInterfaceInfoVOByPage(@RequestBody InterfaceInfoQueryRequest interfaceInfoQueryRequest,
            HttpServletRequest request) {
        if (interfaceInfoQueryRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoQueryRequest,interfaceInfo);
        long current = interfaceInfoQueryRequest.getCurrent();
        long size = interfaceInfoQueryRequest.getPageSize();
        String sortField = interfaceInfoQueryRequest.getSortField();
        String sortOrder = interfaceInfoQueryRequest.getSortOrder();
        String description = interfaceInfo.getInterfaceDescript();
        // description 需支持模糊搜索
        interfaceInfo.setInterfaceDescript(null);
        // 限制爬虫
        ThrowUtils.throwIf(size > 50,ErrorCode.PARAMS_ERROR);
        // 拼接查询条件
        QueryWrapper queryWrapper = new QueryWrapper<>(interfaceInfo);
        queryWrapper.like(StringUtils.isNotBlank(interfaceInfo.getInterfaceDescript()),"description",description);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),sortOrder.equals(CommonConstant.SORT_ORDER_ASC),sortField);
        // 获取分页查询结果
        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.page(new Page<>(current, size),
                queryWrapper);
        return ResultUtils.success(interfaceInfoPage);
    }
}

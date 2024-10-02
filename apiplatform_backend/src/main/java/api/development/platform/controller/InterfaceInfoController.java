package api.development.platform.controller;

import api.development.apiplatform_client_sdk.client.NameClient;
import api.development.apiplatform_client_sdk.model.UserSignature;
import api.development.apiplatform_client_sdk.model.request.CurrencyRequest;
import api.development.apiplatform_client_sdk.model.response.ResultResponse;
import api.development.apiplatform_client_sdk.services.ApiServices;
import api.development.apiplatform_interface.model.entity.InterfaceInfo;
import api.development.apiplatform_interface.model.entity.User;
import api.development.apiplatform_interface.model.entity.UserInterfaceInfo;
import api.development.platform.annotation.AuthCheck;
import api.development.platform.common.*;
import api.development.platform.constant.CommonConstant;
import api.development.platform.constant.UserConstant;
import api.development.platform.exception.BusinessException;
import api.development.platform.exception.ThrowUtils;
import api.development.platform.mapper.UserInterfaceInfoMapper;
import api.development.platform.model.dto.InterfaceInfo.InterfaceInfoAddRequest;
import api.development.platform.model.dto.InterfaceInfo.InterfaceInfoInvokeRequest;
import api.development.platform.model.dto.InterfaceInfo.InterfaceInfoQueryRequest;
import api.development.platform.model.dto.InterfaceInfo.InterfaceInfoUpdateRequest;
import api.development.platform.model.enums.InterfaceStatusEnum;
import api.development.platform.service.InterfaceInfoService;
import api.development.platform.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

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
    private UserInterfaceInfoMapper userInterfaceInfoMapper;

    @Resource
    private ApiServices apiServices;

    /**
     * 接口远程调用 Client
     */
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
        // 将 第一个javabean中的属性复制到第二个javabean
        BeanUtils.copyProperties(interfaceInfoAddRequest, interfaceInfo);
        // 接口校验
        interfaceInfoService.validInterfaceInfo(interfaceInfo, true);
        User loginUser = userService.getLoginUser(request);
        // 设置用户id
        interfaceInfo.setUserId(loginUser.getId());
        boolean result = interfaceInfoService.save(interfaceInfo);
        // 自定义异常类
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
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
        // 仅本人或管理员可删除(即不是本人，又不是管理员，抛异常)
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = interfaceInfoService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     * @param interfaceInfoUpdateRequest 修改请求
     * @return 操作结果
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateInterfaceInfo(@RequestBody InterfaceInfoUpdateRequest interfaceInfoUpdateRequest,HttpServletRequest httpServletRequest) {
        if (interfaceInfoUpdateRequest == null || interfaceInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoUpdateRequest, interfaceInfo);
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
     * 接口发布（这里添加接口）
     * @param idRequest 接口id
     * @return 操作结果
     */
    @PostMapping("/online")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE) // aop 自定义注解，检验是否是管理员
    public BaseResponse<Boolean> onlineInterfaceInfo(@RequestBody IdRequest idRequest){
        // 参数校验
        long id = idRequest.getId();
        if (id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 查询数据
        InterfaceInfo interfaceInfoServiceById = interfaceInfoService.getById(id);
        if (interfaceInfoServiceById == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        // 使用interface项目模块中的方法，验证接口是否能访问同
//        api.development.apiplatform_client_sdk.model.params.NameParams user = new api.development.apiplatform_client_sdk.model.params.NameParams();
//        user.setName("roqweqeot1"); // 这里不能出现中文，会乱码，导致在自定义sdk中不匹配，出现无权限错误
//        String userNameByPost = nameClient.getUserNameByPost(user);

//        if (StringUtils.isAnyBlank(userNameByPost) || StringUtils.contains(userNameByPost,"无权限")){
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "接口验证错误");
//        }

        // 修改接口状态为上线
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(id);
        interfaceInfo.setInterfaceStatus(InterfaceStatusEnum.ONLINE.getValue());
        boolean updateById = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(updateById);
    }
    /**
     * 接口 在线调用
     * @param interfaceInfoInvokeRequest 调用接口请求
     * @param httpServletRequest 客户端请求
     * @return
     */
    @PostMapping("/invoke")
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<Object> invokeInterfaceInfo(@RequestBody InterfaceInfoInvokeRequest interfaceInfoInvokeRequest, HttpServletRequest httpServletRequest){
        // 1. 参数校验
        Long id = interfaceInfoInvokeRequest.getId();
        if (ObjectUtils.isEmpty(interfaceInfoInvokeRequest.getRequestParams()) || id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 查询数据是否存在
        InterfaceInfo oldInterfaceInfo  = interfaceInfoService.getById(id);
        if (oldInterfaceInfo  == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 校验接口状态
        if (oldInterfaceInfo.getInterfaceStatus().equals(InterfaceStatusEnum.OFFLINE)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"接口已关闭");
        }
        // 查询调用接口的用户,校验用户是否有调用次数是否大于0
        User loginUser = userService.getLoginUser(httpServletRequest);
        int kunCoin = loginUser.getKunCoin();

        String accessKey = loginUser.getAccessKey();
        String secretKey = loginUser.getSecretKey();
        Long loginUserId = loginUser.getId();
        QueryWrapper<UserInterfaceInfo> userInterfaceInfoQueryWrapper = new QueryWrapper<>();
        userInterfaceInfoQueryWrapper.eq("interfaceInfoId", id);
        userInterfaceInfoQueryWrapper.eq("userId",loginUserId);
        UserInterfaceInfo userInterfaceInfo = userInterfaceInfoMapper.selectOne(userInterfaceInfoQueryWrapper);
        if (userInterfaceInfo.getLeftNum() <= 0){
            if (kunCoin <= 0){
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"用户剩余调用次数不足");
            }
            try {
                kunCoin--;
                loginUser.setKunCoin(kunCoin);
                userService.updateById(loginUser);
            } catch (Exception e) {
                // 处理保存失败的情况
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"钱不够");
            }

        }
        // 2. 构建查询参数
        Gson gson = new Gson();
        List<InterfaceInfoInvokeRequest.Field> fieldList = interfaceInfoInvokeRequest.getRequestParams();
        String requestParams = "{}";
        if (fieldList != null && fieldList.size() > 0) {
            JsonObject jsonObject = new JsonObject();
            for (InterfaceInfoInvokeRequest.Field field : fieldList) { //
                if (StringUtils.isNotBlank(field.getFieldName())){
                    jsonObject.addProperty(field.getFieldName(), field.getValue());
                }
            }
            requestParams = gson.toJson(jsonObject);
        }
        Map<String, Object> params = new Gson().fromJson(requestParams, new TypeToken<Map<String, Object>>() {
        }.getType());
        try {
            // 确保 requestParams 是一个有效的 JSON 字符串

            UserSignature qiApiClient = new UserSignature(accessKey, secretKey);
            CurrencyRequest currencyRequest = new CurrencyRequest();
            currencyRequest.setMethod(oldInterfaceInfo.getInterfaceType());
            currencyRequest.setPath(oldInterfaceInfo.getInterfaceUrl());
            currencyRequest.setRequestParams(params);
            ResultResponse response = apiServices.request(qiApiClient, currencyRequest);
            return ResultUtils.success(response.getData());
        } catch (JsonSyntaxException e) {
            // 处理 JSON 解析异常
            System.err.println("Failed to parse JSON: " + e.getMessage());
        } catch (Exception e) {
            // 处理其他可能的异常
            System.err.println("An error occurred: " + e.getMessage());
        }
        return null;
    }

    /**
     * 下线
     * @param idRequest 接口id
     * @return 操作结果
     */
    @PostMapping("/offline")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE) // aop 自定义注解，检验是否是管理员
    public BaseResponse<Boolean> offlineInterfaceInfo(@RequestBody IdRequest idRequest){
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

        // 修改状态为下线
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(id);
        interfaceInfo.setInterfaceStatus(InterfaceStatusEnum.OFFLINE.getValue());
        boolean updateById = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(updateById);
    }


    /**
     * 根据 id 获取
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
     * 分页获取列表
     * 这里的page对象使用，需要对mybatis plus的分页功能进行配置（ mybatisPlusInterceptor中已配置）
     * @param interfaceInfoQueryRequest 查询请求
     * @return 返回所有接口数据
     */
    @GetMapping("/list")
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
     * 分页获取列表
     * @param interfaceInfoQueryRequest 查询请求
     * @return 查询结果
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<InterfaceInfo>> listInterfaceInfoVOByPage(@RequestBody InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
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
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceInfo);
        queryWrapper.like(StringUtils.isNotBlank(interfaceInfo.getInterfaceDescript()),"description",description);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),sortOrder.equals(CommonConstant.SORT_ORDER_ASC),sortField);
        // 获取分页查询结果
        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.page(new Page<>(current, size),
                queryWrapper);
        return ResultUtils.success(interfaceInfoPage);
    }
}

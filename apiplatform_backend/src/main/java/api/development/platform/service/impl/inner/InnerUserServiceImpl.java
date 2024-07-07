package api.development.platform.service.impl.inner;

import api.development.apiplatform_interface.model.entity.User;
import api.development.apiplatform_interface.service.InnerUserService;
import api.development.platform.common.ErrorCode;
import api.development.platform.exception.BusinessException;
import api.development.platform.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * 内部用户服务实现类
 *

 */
@DubboService
@Slf4j
public class InnerUserServiceImpl implements InnerUserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public User getInvokeUser(String accessKey) {
        if (StringUtils.isAnyBlank(accessKey)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("accessKey", accessKey);

        // 添加日志输出
        log.info("Executing query: {}", queryWrapper.getSqlSegment());

        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            log.warn("User with accessKey '{}' not found in the database.", accessKey);
        } else {
            log.info("User found: {}", user);
        }

        return user;
    }

}


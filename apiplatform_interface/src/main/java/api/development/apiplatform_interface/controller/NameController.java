/*
 * Copyright 2013-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package api.development.apiplatform_interface.controller;


import api.development.apiplatform_client_sdk.model.User;
import api.development.apiplatform_client_sdk.utils.SignUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


/**
 * @author <a href="mailto:chenxilzx1@gmail.com">theonefx</a>
 */
@RestController
@RequestMapping("/name")
public class NameController {

    @GetMapping("/get")
    public String getNameByGet(String name) {
        return "Get name Hello: " + name;
    }
    @PostMapping("/post")
    public String getNameByPost(@RequestParam String name) {
        return "Post name Hello：" + name;
    }
    @PostMapping("/user")
    public String getUserNameByPost(@RequestBody User user, HttpServletRequest httpServletRequest) {
        // 这五个可以一步一步去做校验,比如 accesskey 我们先去数据库中查一下
        String accessKey = httpServletRequest.getHeader("accessKey");
        String body = httpServletRequest.getHeader("body");
        String nonce = httpServletRequest.getHeader("nonce");
        String timestamp = httpServletRequest.getHeader("timestamp");
        String sign = httpServletRequest.getHeader("sign");
        // todo 这里写死，后续可以完善，根据用户信息去查数据库
        if (!accessKey.equals("poise.admin")){
            throw new RuntimeException("无权限");
        }
        // todo 后续可根据自己的想法去完善
        // 后端存储用hashmap或redis都可以
        if(Long.parseLong(nonce)>10000){ // 校验随机数,模拟一下,直接判断nonce是否大于10000
            throw new RuntimeException("无权限");
        }
        // todo 时间戳校验，时间和当前时间差不能少于五分钟
        // todo 签名校验，sercrtKey是从库中查询出来的
        String signCheck = SignUtils.getSignUtils(body, "admin.admin");
        if (!sign.equals(signCheck)){
            throw new RuntimeException("无权限");
        }
        String result = "Post username Hello " + user.getName();
        // todo 正常的话是在这个位置执行统计次数加1的逻辑
        return result;

    }


}

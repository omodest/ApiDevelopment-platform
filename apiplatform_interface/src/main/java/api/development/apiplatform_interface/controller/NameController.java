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
        String result = "POST 用户名字是" + user.getName();
        return result;
    }


}

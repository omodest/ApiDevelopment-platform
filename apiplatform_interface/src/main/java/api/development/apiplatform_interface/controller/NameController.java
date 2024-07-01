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

import api.development.apiplatform_interface.model.User;
import org.springframework.web.bind.annotation.*;


/**
 * @author <a href="mailto:chenxilzx1@gmail.com">theonefx</a>
 */
@RestController
@RequestMapping("name")
public class NameController {

    @GetMapping("/")
    public String getNameByGet(String name) {
        return "Get name Hello: " + name;
    }
    @PostMapping("/")
    public String getNameByPost(@RequestParam String name) {
        return "Post name Hello：" + name;
    }
    @PostMapping("/JSON")
    public String getUserNameByPost(@RequestBody User user) {
        return "Post username Hello " + user.getName();
    }


}

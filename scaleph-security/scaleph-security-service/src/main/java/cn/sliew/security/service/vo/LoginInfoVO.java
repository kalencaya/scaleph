/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.sliew.security.service.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @author gleiyu
 */
@Data
@Schema(name = "登录信息对象", description = "登录信息对象")
public class LoginInfoVO {

    @NotBlank
    @Length(min = 5, max = 30)
    @Pattern(regexp = "\\w+$")
    private String userName;

    @NotBlank
    @Length(min = 6, max = 32)
    private String password;

    /**
     * 验证码信息
     */
    @NotBlank
    @Length(max = 5)
    private String authCode;

    @NotBlank
    private String uuid;

    private Boolean remember;

    public Boolean getRemember() {
        return remember != null && remember;
    }

    public void setRemember(Boolean remember) {
        this.remember = remember;
    }
}

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

package cn.sliew.security.service.param;

import cn.sliew.scaleph.common.dict.security.ResourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode
public class SecResourceWebUpdateParam {

    @NotNull
    @Schema(description = "id")
    private Long id;

    @NotNull
    @Schema(description = "资源类型。导航，菜单，页面，按钮")
    private ResourceType type;

    @Schema(description = "上级资源id")
    private Long pid;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "前端目录")
    private String menuName;

    @Schema(description = "前端路由路径")
    private String path;

    @Schema(description = "前端重定向路径")
    private String redirect;

    @Schema(description = "前端全局布局显示。只在一级生效")
    private Boolean layout;

    @Schema(description = "前端 icon")
    private String icon;

    @Schema(description = "前端组件")
    private String component;

    @Schema(description = "备注")
    private String remark;
}

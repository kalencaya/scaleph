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

package cn.sliew.security.service;

import java.io.Serializable;
import java.util.List;

import cn.sliew.security.service.dto.SecUserRoleDTO;

/**
 * <p>
 * 用户角色关联表 服务类
 * </p>
 *
 * @author liyu
 * @since 2021-08-01
 */
public interface SecUserRoleService {
    /**
     * 新增用户角色关系
     *
     * @param secUserRoleDTO userrole
     * @return int
     */
    int insert(SecUserRoleDTO secUserRoleDTO);

    /**
     * 根据角色删除
     *
     * @param roleId roleid
     * @return int
     */
    int deleteByRoleId(Serializable roleId);

    /**
     * 删除用户角色关系
     *
     * @param secUserRoleDTO userrole
     * @return int
     */
    int delete(SecUserRoleDTO secUserRoleDTO);

    /**
     * 查询角色相关用户列表
     *
     * @param roleId role
     * @return list
     */
    List<SecUserRoleDTO> listByRoleId(Serializable roleId);
}

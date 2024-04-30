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

import cn.sliew.security.service.dto.SecDeptRoleDTO;

/**
 * <p>
 * 部门角色关联表 服务类
 * </p>
 *
 * @author liyu
 * @since 2021-08-01
 */
public interface SecDeptRoleService {
    /**
     * 根据部门删除
     *
     * @param deptId deptid
     * @return int
     */
    int deleteByDeptId(Serializable deptId);

    /**
     * 根据角色删除
     *
     * @param roleId role id
     * @return int
     */
    int deleteByRoleId(Serializable roleId);

    /**
     * 授权
     *
     * @param secDeptRoleDTO dept role
     * @return int
     */
    int insert(SecDeptRoleDTO secDeptRoleDTO);

    /**
     * 删除授权
     *
     * @param secDeptRoleDTO dept role
     * @return int
     */
    int delete(SecDeptRoleDTO secDeptRoleDTO);
}

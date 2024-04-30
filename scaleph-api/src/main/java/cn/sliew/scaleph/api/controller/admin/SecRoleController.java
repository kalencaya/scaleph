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

package cn.sliew.scaleph.api.controller.admin;

import cn.hutool.json.JSONUtil;
import cn.sliew.scaleph.dao.DataSourceConstants;
import cn.sliew.scaleph.log.annotation.Logging;
import cn.sliew.scaleph.security.web.service.OnlineUserService;
import cn.sliew.scaleph.system.model.ResponseVO;
import cn.sliew.security.service.SecDeptRoleService;
import cn.sliew.security.service.SecRoleService;
import cn.sliew.security.service.SecUserRoleService;
import cn.sliew.security.service.SecUserService;
import cn.sliew.security.service.dto.SecDeptRoleDTO;
import cn.sliew.security.service.dto.SecRoleDTO;
import cn.sliew.security.service.dto.SecUserDTO;
import cn.sliew.security.service.dto.SecUserRoleDTO;
import cn.sliew.security.service.param.SecRoleAddParam;
import cn.sliew.security.service.param.SecRoleListParam;
import cn.sliew.security.service.param.SecRoleUpdateParam;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 角色表 前端控制器
 * </p>
 *
 * @author liyu
 */
@RestController
@RequestMapping("/api/admin/role")
@Tag(name = "系统管理-角色管理")
public class SecRoleController {

    @Autowired
    private SecRoleService secRoleService;
    @Autowired
    private SecUserRoleService secUserRoleService;
    @Autowired
    private SecDeptRoleService secDeptRoleService;
    @Autowired
    private OnlineUserService onlineUserService;
    @Autowired
    private SecUserService secUserService;

    @Logging
    @GetMapping("list")
    @Operation(summary = "查询角色列表", description = "查询全部角色信息")
    @PreAuthorize("@svs.validate(T(cn.sliew.scaleph.common.constant.PagePrivilege).ADMIN_ROLE_SHOW)")
    public ResponseEntity<Page<SecRoleDTO>> listByPage(@Validated SecRoleListParam param) {
        Page<SecRoleDTO> result = this.secRoleService.listByPage(param);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Logging
    @GetMapping
    @Operation(summary = "查询角色列表", description = "查询全部角色信息")
    @PreAuthorize("@svs.validate(T(cn.sliew.scaleph.common.constant.PagePrivilege).ADMIN_ROLE_SHOW)")
    public ResponseEntity<List<SecRoleDTO>> listAll() {
        List<SecRoleDTO> list = this.secRoleService.listAll();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @Logging
    @PostMapping
    @Operation(summary = "新增角色", description = "新增角色")
    @PreAuthorize("@svs.validate(T(cn.sliew.scaleph.common.constant.ButtonPrivilege).ADMIN_ROLE_ADD)")
    public ResponseEntity<ResponseVO> addRole(@Validated @RequestBody SecRoleAddParam param) {
        this.secRoleService.insert(param);
        return new ResponseEntity<>(ResponseVO.success(), HttpStatus.CREATED);
    }

    @Logging
    @PutMapping
    @Operation(summary = "修改角色", description = "修改角色")
    @PreAuthorize("@svs.validate(T(cn.sliew.scaleph.common.constant.ButtonPrivilege).ADMIN_ROLE_EDIT)")
    public ResponseEntity<ResponseVO> editRole(@Validated @RequestBody SecRoleUpdateParam param) {
        this.secRoleService.update(param);
        return new ResponseEntity<>(ResponseVO.success(), HttpStatus.CREATED);
    }

    @Logging
    @DeleteMapping("/{id}")
    @Operation(summary = "删除角色", description = "删除角色")
    @PreAuthorize("@svs.validate(T(cn.sliew.scaleph.common.constant.ButtonPrivilege).ADMIN_ROLE_DELETE)")
    public ResponseEntity<ResponseVO> deleteRole(@PathVariable("id") Long id) {
        this.secRoleService.deleteById(id);
        this.onlineUserService.disableOnlineCacheRole(id);
        return new ResponseEntity<>(ResponseVO.success(), HttpStatus.OK);
    }

    @Logging
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除角色", description = "批量删除角色")
    @PreAuthorize("@svs.validate(T(cn.sliew.scaleph.common.constant.ButtonPrivilege).ADMIN_ROLE_DELETE)")
    public ResponseEntity<ResponseVO> deleteBatch(@RequestBody List<Long> ids) {
        secRoleService.deleteBatch(ids);
        for (Long id : ids) {
            this.onlineUserService.disableOnlineCacheRole(id);
        }
        return new ResponseEntity<>(ResponseVO.success(), HttpStatus.OK);
    }

    @Logging
    @PostMapping(path = "/grant")
    @Operation(summary = "用户授权角色", description = "用户授权角色")
    @Transactional(rollbackFor = Exception.class, transactionManager = DataSourceConstants.MASTER_TRANSACTION_MANAGER_FACTORY)
    @PreAuthorize("@svs.validate(T(cn.sliew.scaleph.common.constant.ButtonPrivilege).ADMIN_ROLE_AUTHORIZE)")
    public ResponseEntity<ResponseVO> grantRole(@NotNull Long roleId, @NotNull String userIds) {
        List<Long> userList = JSONUtil.toList(userIds, Long.class);
        List<SecUserRoleDTO> oldUserList = this.secUserRoleService.listByRoleId(roleId);
        List<Long> tmpList = new ArrayList<>(userList.size());
        tmpList.addAll(userList);
        //grant new user
        tmpList.removeAll(
                oldUserList.stream().map(SecUserRoleDTO::getUserId).collect(Collectors.toList()));
        for (Long userId : tmpList) {
            SecUserRoleDTO userRole = new SecUserRoleDTO();
            userRole.setRoleId(roleId);
            userRole.setUserId(userId);
            this.secUserRoleService.insert(userRole);
        }
        //revoke removed user
        for (SecUserRoleDTO userRole : oldUserList) {
            if (!userList.contains(userRole.getUserId())) {
                this.secUserRoleService.delete(userRole);
            }
        }
        userList.forEach(d -> {
            SecUserDTO user = this.secUserService.selectOne(d);
            this.onlineUserService.disableOnlineCacheUser(user.getUserName());
        });
        return new ResponseEntity<>(ResponseVO.success(), HttpStatus.OK);
    }

    @Logging
    @GetMapping("/dept")
    @Operation(summary = "查询部门对应角色列表", description = "查询部门对应角色列表")
    @PreAuthorize("@svs.validate(T(cn.sliew.scaleph.common.constant.ButtonPrivilege).ADMIN_DEPT_SELECT)")
    public ResponseEntity<List<SecRoleDTO>> listRoleByDept(String grant, @NotNull Long deptId) {
        List<SecRoleDTO> list = this.secRoleService.selectRoleByDept(grant, deptId);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @Logging
    @GetMapping("/dept/grant")
    @Operation(summary = "部门角色授权", description = "部门角色授权")
    @PreAuthorize("@svs.validate(T(cn.sliew.scaleph.common.constant.ButtonPrivilege).ADMIN_DEPT_AUTHORIZE)")
    public ResponseEntity<ResponseVO> grantDeptRole(@NotNull SecDeptRoleDTO deptRole) {
        this.secDeptRoleService.insert(deptRole);
        List<SecUserDTO> userList = this.secUserService.listByDept(deptRole.getDeptId(), "", "1");
        userList.forEach(user -> {
            this.onlineUserService.disableOnlineCacheUser(user.getUserName());
        });
        return new ResponseEntity<>(ResponseVO.success(), HttpStatus.OK);
    }

    @Logging
    @GetMapping("/dept/revoke")
    @Operation(summary = "回收部门角色权限", description = "回收部门角色权限")
    @PreAuthorize("@svs.validate(T(cn.sliew.scaleph.common.constant.ButtonPrivilege).ADMIN_DEPT_UNAUTHORIZE)")
    public ResponseEntity<ResponseVO> revokeDeptRole(@NotNull SecDeptRoleDTO deptRole) {
        this.secDeptRoleService.delete(deptRole);
        List<SecUserDTO> userList = this.secUserService.listByDept(deptRole.getDeptId(), "", "1");
        userList.forEach(user -> {
            this.onlineUserService.disableOnlineCacheUser(user.getUserName());
        });
        return new ResponseEntity<>(ResponseVO.success(), HttpStatus.OK);
    }
}

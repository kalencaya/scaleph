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

package cn.sliew.scaleph.security.web.authentication;

import cn.sliew.scaleph.common.dict.security.UserStatus;
import cn.sliew.scaleph.common.util.I18nUtil;
import cn.sliew.security.service.SecUserService;
import cn.sliew.security.service.dto.SecPrivilegeDTO;
import cn.sliew.security.service.dto.SecRoleDTO;
import cn.sliew.security.service.dto.SecUserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * @author gleiyu
 */
@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private SecUserService secUserService;

    /**
     * 根据用户名查询登录用户信息
     *
     * @param userName 用户名
     * @return 用户信息
     * @throws UsernameNotFoundException UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        try {
            SecUserDTO secUserDTO = secUserService.selectOne(userName);
            if (secUserDTO == null) {
                throw new BadCredentialsException(I18nUtil.get("response.error.login.password"));
            }

            if (secUserDTO.getStatus() != UserStatus.ENABLED) {
                throw new BadCredentialsException(I18nUtil.get("response.error.login.disable"));
            }

            UserDetailInfo user = new UserDetailInfo();
            user.setUser(secUserDTO);
            //查询用户角色权限信息
            List<SecRoleDTO> privileges = secUserService.getAllPrivilegeByUserName(userName);
            user.setAuthorities(toGrantedAuthority(privileges));
            return user;
        } catch (Exception e) {
            log.error("获取用户信息异常! userName: {}", userName, e);
            throw new UsernameNotFoundException(e.getMessage(), e);
        }
    }

    private List<GrantedAuthority> toGrantedAuthority(List<SecRoleDTO> roles) {
        if (CollectionUtils.isEmpty(roles)) {
            return Collections.emptyList();
        }
        String[] privileges = roles.stream()
                .flatMap(role -> role.getPrivileges().stream())
                .map(SecPrivilegeDTO::getPrivilegeCode)
                .toArray(length -> new String[length]);
        return AuthorityUtils.createAuthorityList(privileges);
    }
}

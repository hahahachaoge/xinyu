package com.example.web.service.impl;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.example.web.common.CurrentUserDto;
import com.example.web.dto.SysUserDto;
import com.example.web.entity.SysUser;
import com.example.web.enums.RoleTypeEnum;
import com.example.web.exception.CustomException;
import com.example.web.mapper.SysUserMapper;
import com.example.web.utils.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AccountSecurityTest {
    SysUserMapper mapper; SysUserServiceImpl service;
    @BeforeEach void setup() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""),SysUser.class);
        mapper=mock(SysUserMapper.class); service=new SysUserServiceImpl();
        ReflectionTestUtils.setField(service,"sysUserMpper",mapper);
        var user=new CurrentUserDto(); user.setUserId(7); user.setRoleType(RoleTypeEnum.用户); BaseContext.setCurrentUserDto(user);
        var context=new StaticApplicationContext();
        context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("test",Map.of("security.jwt.secret","unit-test-only-signing-key-never-use-in-production")));
        BaseContext.setApplicationContext(context);
    }
    @AfterEach void cleanup() { BaseContext.clear(); BaseContext.setApplicationContext(null); }
    SysUser account() { var user=new SysUser(); user.setId(7);user.setUserName("test-user");user.setRoleType(2);user.setPassWord(Extension.md5Encrypt("old-test-password"));return user; }
    @Test void profileUpdateCannotChangeRolePasswordOrUsername() {
        var stored=account();when(mapper.selectById(7)).thenReturn(stored);
        var input=new SysUserDto();input.setId(7);input.setRoleType(1);input.setUserName("admin");input.setPassWord("attacker-password");input.setNickName("updated");
        service.addOrEdit(input);
        assertEquals(2,stored.getRoleType());assertEquals("test-user",stored.getUserName());
        assertTrue(PasswordUtils.matches("old-test-password",stored.getPassWord()));assertEquals("updated",stored.getNickName());
    }
    @Test void cannotEditAnotherAccount() {
        var input=new SysUserDto();input.setId(8);
        assertEquals(403,assertThrows(ResponseStatusException.class,()->service.addOrEdit(input)).getStatusCode().value());
        verifyNoInteractions(mapper);
    }
    @Test void registrationAlwaysCreatesOrdinaryUserWithNewIdAndBcrypt() {
        BaseContext.clear();var input=new SysUserDto();input.setId(7);input.setRoleType(1);input.setUserName("new-user");input.setPassWord("new-test-password");
        service.register(input);
        var captor=ArgumentCaptor.forClass(SysUser.class);verify(mapper).insert(captor.capture());var saved=captor.getValue();
        assertNull(saved.getId());assertEquals(2,saved.getRoleType());assertTrue(saved.getPassWord().startsWith("$2"));
        assertTrue(PasswordUtils.matches("new-test-password",saved.getPassWord()));verify(mapper,never()).updateById(any(SysUser.class));
    }
    @Test void loginRequiresPassword() {
        var input=new SysUserDto();input.setUserName("test-user");
        assertThrows(CustomException.class,()->service.signIn(input));verifyNoInteractions(mapper);
    }
    @Test void legacyPasswordIsUpgradedOnlyAfterSuccessfulLogin() {
        var stored=account();when(mapper.selectList(any())).thenReturn(List.of(stored));
        var input=new SysUserDto();input.setUserName("test-user");input.setPassWord("wrong-password");
        assertThrows(CustomException.class,()->service.signIn(input));verify(mapper,never()).updateById(any(SysUser.class));
        input.setPassWord("old-test-password");assertNotNull(service.signIn(input));
        assertTrue(stored.getPassWord().startsWith("$2"));verify(mapper).updateById(stored);
    }
    @Test void passwordAndOriginalPasswordNeverAppearInJson() throws Exception {
        var input=new SysUserDto();input.setPassWord("test-secret");input.setOrginPassword("old-secret");
        String result=new ObjectMapper().writeValueAsString(input);
        assertFalse(result.contains("passWord"));assertFalse(result.contains("orginPassword"));
    }
    @Test void unverifiedPasswordRecoveryIsDisabled() {
        assertThrows(CustomException.class,()->service.forgetPassword(new SysUserDto()));verifyNoInteractions(mapper);
    }
}

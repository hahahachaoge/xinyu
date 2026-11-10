package com.example.web.config;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.example.web.entity.SysUser;
import com.example.web.mapper.SysUserMapper;
import com.example.web.utils.PasswordUtils;
import java.util.List;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.springframework.context.annotation.Profile;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DemoAccountInitializerTest {
    SysUserMapper users;
    DemoAccountInitializer initializer;
    @BeforeEach void setup() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), SysUser.class);
        users = mock(SysUserMapper.class);
        initializer = new DemoAccountInitializer(users, "12345678");
    }
    @Test void createsBothAccountsWithCorrectRolesAndHashedPasswords() {
        when(users.selectList(any())).thenReturn(List.of());
        initializer.run(null);
        var captured = ArgumentCaptor.forClass(SysUser.class);
        verify(users, times(2)).insert(captured.capture());
        var accounts = captured.getAllValues();
        assertEquals("user", accounts.get(0).getUserName()); assertEquals(2, accounts.get(0).getRoleType());
        assertEquals("admin", accounts.get(1).getUserName()); assertEquals(1, accounts.get(1).getRoleType());
        for (var account : accounts) {
            assertTrue(account.getPassWord().startsWith("$2"));
            assertTrue(PasswordUtils.matches("12345678", account.getPassWord()));
        }
    }
    @Test void repeatedStartupKeepsExistingIdsPasswordsAndData() {
        var user = account("user",2); var admin = account("admin",1);
        when(users.selectList(any())).thenReturn(List.of(user),List.of(admin),List.of(user),List.of(admin));
        initializer.run(null); initializer.run(null);
        verify(users,never()).insert(any(SysUser.class)); verify(users,never()).updateById(any(SysUser.class));
        assertEquals(100,user.getId()); assertEquals("existing nickname",user.getNickName());
    }
    @Test void demoStartupRestoresChangedCredentialsAndRoles() {
        var user=account("user",1);user.setPassWord(PasswordUtils.encode("other-password"));
        var admin=account("admin",2);
        when(users.selectList(any())).thenReturn(List.of(user),List.of(admin));
        initializer.run(null);
        assertEquals(2,user.getRoleType());assertEquals(1,admin.getRoleType());
        assertTrue(PasswordUtils.matches("12345678",user.getPassWord()));
        assertEquals(100,user.getId());assertEquals("existing nickname",user.getNickName());
        verify(users,times(2)).updateById(any(SysUser.class));
    }
    @Test void onlyRunsInExplicitDemoProfile() {
        assertArrayEquals(new String[]{"demo"},DemoAccountInitializer.class.getAnnotation(Profile.class).value());
    }
    @Test void duplicateNamesFailWithoutChangingAccounts() {
        when(users.selectList(any())).thenReturn(List.of(account("user",2),account("user",2)));
        assertThrows(IllegalStateException.class,()->initializer.run(null));
        verify(users,never()).insert(any(SysUser.class));verify(users,never()).updateById(any(SysUser.class));
    }
    private SysUser account(String name,int role) {
        var account=new SysUser();account.setId(100);account.setUserName(name);account.setRoleType(role);
        account.setPassWord(PasswordUtils.encode("12345678"));account.setNickName("existing nickname");return account;
    }
}

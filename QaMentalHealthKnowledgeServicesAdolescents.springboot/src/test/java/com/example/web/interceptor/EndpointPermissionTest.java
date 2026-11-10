package com.example.web.interceptor;

import com.example.web.SysConst;
import com.example.web.advice.*;
import com.example.web.controller.SysUserController;
import com.example.web.entity.SysUser;
import com.example.web.mapper.SysUserMapper;
import com.example.web.service.SysUserService;
import com.example.web.utils.*;
import java.util.Map;
import org.junit.jupiter.api.*;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class EndpointPermissionTest {
    SysUserMapper accounts;SysUserService service;MockMvc mvc;SysUser account;String token;
    @BeforeEach void setup() {
        accounts=mock(SysUserMapper.class);service=mock(SysUserService.class);
        var controller=new SysUserController();ReflectionTestUtils.setField(controller,"sysUserService",service);
        mvc=MockMvcBuilders.standaloneSetup(controller).addInterceptors(new CurrentUserInterceptor(accounts))
            .setControllerAdvice(new GlobalExceptionHandler(),new GlobalResponseAdvice()).build();
        var context=new StaticApplicationContext();context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("test",
            Map.of("security.jwt.secret","unit-test-only-signing-key-never-use-in-production")));BaseContext.setApplicationContext(context);
        account=new SysUser();account.setId(7);account.setRoleType(2);account.setPassWord("unit-test-stored-password-hash");when(accounts.selectById(7)).thenReturn(account);
        token=JWTUtils.getToken(Map.of(SysConst.UserIdClaim,"7",SysConst.RoleTypeClaim,"1", JWTUtils.CREDENTIAL_CLAIM,JWTUtils.credentialVersion(account.getPassWord())));
    }
    @AfterEach void cleanup(){BaseContext.clear();BaseContext.setApplicationContext(null);}
    @Test void anonymousCannotAccessManagement() throws Exception {
        mvc.perform(post("/sysUser/list").contentType("application/json").content("{}"))
            .andExpect(status().isUnauthorized()).andExpect(jsonPath("$.success").value(false));verifyNoInteractions(service);
    }
    @Test void staleAdminClaimDoesNotOverrideCurrentOrdinaryRole() throws Exception {
        mvc.perform(post("/sysUser/list").header("Authorization",token).contentType("application/json").content("{}"))
            .andExpect(status().isForbidden()).andExpect(jsonPath("$.code").value("403"));verifyNoInteractions(service);
    }
    @Test void currentAdministratorCanAccessManagement() throws Exception {
        account.setRoleType(1);
        mvc.perform(post("/sysUser/list").header("Authorization","Bearer "+token).contentType("application/json").content("{}"))
            .andExpect(status().isOk());verify(service).list(any(com.example.web.dto.query.SysUserPagedInput.class));
        Assertions.assertNull(BaseContext.getCurrentUserDto());
    }
    @Test void deletedAccountTokenIsRejected() throws Exception {
        when(accounts.selectById(7)).thenReturn(null);
        mvc.perform(post("/sysUser/list").header("Authorization",token).contentType("application/json").content("{}"))
            .andExpect(status().isUnauthorized());verifyNoInteractions(service);
    }
    @Test void passwordResetInvalidatesExistingToken() throws Exception {
        account.setPassWord("changed-password-hash");
        mvc.perform(post("/sysUser/list").header("Authorization",token).contentType("application/json").content("{}"))
            .andExpect(status().isUnauthorized());verifyNoInteractions(service);
    }
    @Test void staleTokenDoesNotBlockNewLogin() throws Exception {
        mvc.perform(post("/sysUser/signIn").header("Authorization","expired-token").contentType("application/json").content("{}"))
            .andExpect(status().isOk());verify(service).signIn(any());
    }
    @Test void sortCannotInjectSqlSyntax() throws Exception {
        account.setRoleType(1);
        mvc.perform(post("/sysUser/list").header("Authorization",token).contentType("application/json")
            .content("{\"sortItem\":{\"fieldName\":\"id; DELETE FROM sys_user\",\"isAsc\":true}}"))
            .andExpect(status().isBadRequest());verifyNoInteractions(service);
    }
    @Test void anonymousRegistrationRemainsAvailable() throws Exception {
        mvc.perform(post("/sysUser/register").contentType("application/json").content("{}"))
            .andExpect(status().isOk());verify(service).register(any());
    }
}

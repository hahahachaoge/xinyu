package com.example.web.config;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.web.entity.SysUser;
import com.example.web.enums.RoleTypeEnum;
import com.example.web.mapper.SysUserMapper;
import com.example.web.utils.PasswordUtils;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Explicit demo mode maintains the two public judging accounts. */
@Component
@Profile("demo")
public class DemoAccountInitializer implements ApplicationRunner {
    private final SysUserMapper users;
    private final String password;

    public DemoAccountInitializer(SysUserMapper users, @Value("${demo.accounts.password}") String password) {
        this.users = users;
        this.password = password;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        ensureAccount("user", RoleTypeEnum.用户.index(), "测试用户");
        ensureAccount("admin", RoleTypeEnum.管理员.index(), "测试管理员");
    }

    private void ensureAccount(String name, int role, String nickname) {
        var matches = users.selectList(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUserName, name));
        if (matches.size() > 1) throw new IllegalStateException("演示账号重名，请先清理重复账号：" + name);
        if (matches.isEmpty()) {
            SysUser account = new SysUser();
            account.setUserName(name);
            account.setPassWord(PasswordUtils.encode(password));
            account.setRoleType(role);
            account.setNickName(nickname);
            account.setCreationTime(LocalDateTime.now());
            users.insert(account);
            return;
        }
        SysUser account = matches.get(0);
        boolean passwordChanged = !PasswordUtils.matches(password, account.getPassWord())
                || PasswordUtils.needsUpgrade(account.getPassWord());
        if (passwordChanged || !Integer.valueOf(role).equals(account.getRoleType())) {
            if (passwordChanged) account.setPassWord(PasswordUtils.encode(password));
            account.setRoleType(role);
            users.updateById(account);
        }
    }
}

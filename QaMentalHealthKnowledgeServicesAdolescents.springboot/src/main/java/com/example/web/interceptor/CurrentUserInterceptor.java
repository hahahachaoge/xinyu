package com.example.web.interceptor;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.web.SysConst;
import com.example.web.common.CurrentUserDto;
import com.example.web.entity.SysUser;
import com.example.web.enums.RoleTypeEnum;
import com.example.web.mapper.SysUserMapper;
import com.example.web.utils.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;

/** Public actions are explicit. Unlisted controller actions require an administrator. */
@Component
public class CurrentUserInterceptor implements HandlerInterceptor {
    private final SysUserMapper users;
    private static final Set<String> PUBLIC = Set.of(
            "SysUserController#signIn", "SysUserController#register", "SysUserController#forgetPassword",
            "SelectController#roleType", "SelectController#genderEnum",
            "ArticleController#list", "ArticleController#findById", "ArticleController#view", "ArticleController#recommend",
            "ArticleTypeController#list", "ArticleTypeController#findById",
            "ArticleCommentController#list", "ArticleCommentController#findById",
            "BannerController#list", "BannerController#findById",
            "NoticeController#list", "NoticeController#findById", "NoticeTypeController#list", "NoticeTypeController#findById",
            "ScaleInfoController#list", "ScaleInfoController#findById",
            "ScaleQuestionController#list", "ScaleQuestionController#findById",
            "ScoreRuleController#list", "ScoreRuleController#findById", "AiChatController#hotQuestions");
    private static final Set<String> PERSONAL = Set.of(
            "SysUserController#getByToken", "SysUserController#findById", "SysUserController#addOrEdit", "SysUserController#changePassword",
            "AiChatController#createSession", "AiChatController#chat", "AiChatController#chatStream",
            "AiChatController#mySessions", "AiChatController#getMessages", "AiChatController#deleteSession",
            "AiChatController#feedback", "AiChatController#getChunkDetails",
            "AssessmentRecordController#list", "AssessmentRecordController#findById", "AssessmentRecordController#submit",
            "AssessmentRecordController#deleteById", "AssessmentRecordController#batchDeleteByIds",
            "FavoriteController#list", "FavoriteController#findById", "FavoriteController#toggle", "FavoriteController#cancel",
            "FavoriteController#checkFavorited", "ArticleCommentController#addOrEdit", "ArticleCommentController#deleteById",
            "FileController#batchUpload");

    public CurrentUserInterceptor(SysUserMapper users) { this.users = users; }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        BaseContext.clear();
        if ("OPTIONS".equals(request.getMethod()) || !(handler instanceof HandlerMethod method)) return true;
        String action = method.getBeanType().getSimpleName() + "#" + method.getMethod().getName();
        if (action.equals("SysUserController#signIn") || action.equals("SysUserController#register")
                || action.equals("SysUserController#forgetPassword")) return true;
        String token = request.getHeader("Authorization");
        if (token != null && !token.isBlank()) {
            try {
                DecodedJWT jwt = JWTUtils.getTokenInfo(token.startsWith("Bearer ") ? token.substring(7) : token);
                Integer id = Integer.valueOf(jwt.getClaim(SysConst.UserIdClaim).asString());
                SysUser account = users.selectById(id);
                // Current database role takes precedence over stale JWT claims.
                if (account == null || (!Integer.valueOf(1).equals(account.getRoleType()) && !Integer.valueOf(2).equals(account.getRoleType())))
                    throw new IllegalArgumentException("Account unavailable");
                if (account.getPassWord() == null || !JWTUtils.credentialVersion(account.getPassWord())
                        .equals(jwt.getClaim(JWTUtils.CREDENTIAL_CLAIM).asString()))
                    throw new IllegalArgumentException("Credentials changed");
                CurrentUserDto user = new CurrentUserDto();
                user.setUserId(account.getId());
                user.setRoleType(RoleTypeEnum.fromIndex(account.getRoleType()));
                BaseContext.setCurrentUserDto(user);
            } catch (Exception invalidToken) {
                BaseContext.clear();
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "登录已失效，请重新登录");
            }
        }
        if (PUBLIC.contains(action)) return true;
        try {
            AccessControl.requireUser();
            if (!PERSONAL.contains(action)) AccessControl.requireAdmin();
        } catch (ResponseStatusException denied) {
            BaseContext.clear();
            throw denied;
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        BaseContext.clear();
    }
}

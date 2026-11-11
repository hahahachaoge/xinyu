# 协作与代码规范

本规范记录当前项目的组织方式，供后续维护使用；不要求为了格式统一重写现有业务代码。

## 目录与命名

- 前端沿用 Vue 单文件组件，页面放在 `src/views`，公共组件放在 `src/components`，复用现有路由、状态管理及请求工具。
- 后端保留 `controller → service → mapper` 的职责分层，实体放在 `entity`，接口对象放在 `dto`，配置放在 `config`。
- Java 类使用 PascalCase，方法和变量使用 camelCase，常量使用 UPPER_SNAKE_CASE；修改时优先与相邻代码保持一致。
- 单个改动围绕一个具体问题，不随手更改量表、评分规则、知识内容和数据库结构。

## 有效注释

注释解释业务目的、限制条件和不直观的行为，避免逐行重复代码。修改逻辑时同步修改相关注释。

可供维护者定位的现有注释与实现：

- 后端 `service/impl/AiChatServiceImpl.java`：RAG 问答服务与会话流程。
- 后端 `config/LangChainConfig.java`：嵌入模型及向量存储配置。
- 后端 `config/DemoAccountInitializer.java`：显式演示模式下的固定账号维护。
- 后端 `utils/JWTUtils.java`：Token 生成及验证。
- 前端 `vite.config.js`：生产构建本地预演的 `/api` 代理说明。

## 接口与配置

- 前端使用现有请求封装；接口入参与返回格式沿用项目现有约定。
- 登录、管理员权限及用户记录归属由服务端校验，不能只依赖前端按钮或路由。
- 不在代码、日志、截图、提交消息或前端环境变量中写入真实密码、Token 和模型 Key。
- 新增环境变量时同步维护 `.env.example` 和 `docs/environment.md`；模板只包含空值或非敏感默认值。
- 修改 AI 流式链路时检查 SSE 事件、错误处理和代理缓冲，不能只验证普通 HTTP 请求。

## Git 提交

保留已有真实历史，不补造开发时间、不为改标题而重写历史。后续按改动内容提交：

```text
fix(chat): correct message input spacing
feat(article): add an article feature
docs(readme): complete setup instructions
chore(build): update build configuration
```

使用 `type(scope): description` 格式；`fix` 表示修复，`feat` 表示新增功能，`docs` 表示文档，`chore` 表示维护。同一次提交只放与任务相关的文件，不上传本地配置、依赖缓存、构建产物或数据库备份。

## 验证

提交前检查差异及空白错误：

```cmd
git diff --check
git diff --stat
git status --short
```

前端功能或样式改动按需要构建与浏览器检查；后端逻辑改动执行相关测试。文档修改检查本地链接、端口、命令和实际配置是否一致。完整测试与构建命令见 [README](../README.md)。

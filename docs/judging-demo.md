# 评委运行说明

## 固定演示账号

| 账号 | 密码 | 登录时选择的角色 |
| --- | --- | --- |
| `user` | `12345678` | 用户 |
| `admin` | `12345678` | 管理员 |

这是项目所有者指定公开的演示凭据。数据库内保存 BCrypt 密码哈希；真实数据库连接密码、JWT 密钥和模型 API Key 不随演示账号公开。

## 首次运行

1. 安装 README 中列出的 Java、Maven、Node.js、MySQL、PostgreSQL 和 pgvector。
2. 在项目根目录将 `.env.example` 复制为 `.env.properties`，填写评审环境自己的数据库、JWT 和模型服务配置。已有本地配置文件时不要覆盖。
3. 按 README 导入 MySQL 初始化 SQL，创建 PostgreSQL 数据库并启用 vector 扩展。
4. 在项目根目录打开 CMD，启动后端演示模式：

```cmd
cd QaMentalHealthKnowledgeServicesAdolescents.springboot
mvn.cmd spring-boot:run "-Dmaven.repo.local=.maven-repository" "-Dspring-boot.run.profiles=demo"
```

5. 另开 CMD，从项目根目录启动前端：

```cmd
cd QaMentalHealthKnowledgeServicesAdolescents.elementui
npm.cmd ci --cache .npm-cache
npm.cmd run dev
```

6. 打开 http://localhost:8000，使用上表账号登录。后端接口为 http://localhost:7406。无需先注册，也无需执行授予管理员权限的 SQL。

## 重启与数据保留

后端启用 `demo` 配置时，会自动创建缺失的账号；已存在同名账号时保留 ID、昵称、资料和关联业务数据，并确保角色与演示密码一致。正常重启不会重复创建账号，也不会清空数据库。

在演示模式内修改这两个账号的密码或角色，下一次启动会恢复上表设置。不要删除这两个账号；如果删除后再启动，会重新创建账号，ID 可能变化。删除数据库后需重新导入结构和示例数据。存在多个同名账号时启动会报错，需先清理重复数据。

若部署者设置了 `DEMO_ACCOUNT_PASSWORD` 环境变量，将覆盖表中的默认演示密码。默认不设置此变量。

## 建议验收

1. 使用 `user` 登录，完成测评并查看结果，测试收藏、头像上传与聊天。
2. 使用 `admin` 登录，查看用户、量表、评分规则和知识库管理，测试文件上传与导出。
3. 用另一个自行注册的普通账号验证数据隔离：看不到 `user` 的测评、收藏和聊天。
4. 重启后端演示模式，确认两个账号仍可用，原测试数据仍保留。

AI 功能依赖有效的模型服务配置和知识库向量数据，固定账号不替代这些配置。首次导入 SQL 后，在知识文档管理中点击“重建全部分块向量”，再刷新索引检查，确认真实向量与分块均匹配。详见 [AI 与知识库验收](deployment-step-2-ai-rag.md)。

## 正式部署

正式部署不启用 `demo` 配置。若生产数据库曾用于演示，请先删除公开演示账号或更换其密码，再对外提供服务。

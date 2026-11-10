# 青少年心理健康知识服务平台

基于 Vue 3 和 Spring Boot 的心理健康知识服务应用，包含 AI 问答、知识来源查看、知识文档管理、心理测评、科普文章及后台统计。

AI 问答使用 DeepSeek；知识库通过 LangChain4j 调用嵌入模型，并使用 PostgreSQL 的 pgvector 扩展存储向量。业务数据存储在 MySQL 中。

## 评委演示账号

完成下文的环境配置和数据库初始化后，在后端目录用 CMD 启动演示模式：

```cmd
mvn.cmd spring-boot:run "-Dmaven.repo.local=.maven-repository" "-Dspring-boot.run.profiles=demo"
```

| 登录账号 | 登录密码 | 登录角色 |
| --- | --- | --- |
| `user` | `12345678` | 用户 |
| `admin` | `12345678` | 管理员 |

演示模式自动创建这两个账号，重启保留账号 ID 与已有业务数据，并确保密码及角色与上表一致，无需手动注册或执行管理员 SQL。生产部署不启用 `demo` 配置；数据库密码、JWT 密钥和 AI Key 仍由评委或部署者自行配置，不随演示账号公开。详见 [评委运行说明](docs/judging-demo.md)。

## 项目目录

```text
QaMentalHealthKnowledgeServicesAdolescents.elementui/   Vue 前端
QaMentalHealthKnowledgeServicesAdolescents.springboot/ Spring Boot 后端
qa_mental_health_knowledge_services_adolescents.sql     MySQL 初始化数据
.env.example                                          本地配置模板
```

## 环境准备

- Node.js 18 及以上版本、npm
- JDK 17 或兼容版本、Maven 3.9
- MySQL 8
- PostgreSQL 及 pgvector 扩展
- DeepSeek API Key、嵌入模型服务的 API Key（当前配置使用硅基流动）

后端配置位于 `QaMentalHealthKnowledgeServicesAdolescents.springboot/src/main/resources/application.yml`。默认 MySQL 端口为 `3306`，PostgreSQL 端口为 `5432`，数据库名称均为 `qa_mental_health_knowledge_services_adolescents`。

## 本地配置

以下命令在 Windows CMD 中执行。从项目根目录复制配置模板：

```cmd
copy .env.example .env.properties
```

填写 `.env.properties` 中的数据库密码及两个服务的 API Key。已有该文件时请直接编辑，不要覆盖本地配置。Spring Boot 会自动读取该文件，也可通过系统环境变量提供同名配置。密钥和密码不要填写到前端代码中。

模型名称和 API 地址在 `application.yml` 中配置，请按服务账号实际可用的模型调整。

## 数据库初始化

在项目根目录执行下面的 MySQL 命令（使用自己的数据库账号）：

```cmd
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS qa_mental_health_knowledge_services_adolescents CHARACTER SET utf8mb4;"
mysql -u root -p --default-character-set=utf8mb4 qa_mental_health_knowledge_services_adolescents < qa_mental_health_knowledge_services_adolescents.sql
```

注意：SQL 包含重建表操作，仅用于新建开发或演示数据库，已有数据请先备份。

PostgreSQL 初始化：

```cmd
psql -U postgres -h localhost -d postgres -c "CREATE DATABASE qa_mental_health_knowledge_services_adolescents;"
psql -U postgres -h localhost -d qa_mental_health_knowledge_services_adolescents -c "CREATE EXTENSION IF NOT EXISTS vector;"
```

如果数据库已存在，跳过创建步骤。MySQL 示例数据不包含 PostgreSQL 向量；配置有效的嵌入模型 API 后，需要在后台对知识文档执行向量化。

## 启动前后端

在项目根目录分别打开两个 CMD 窗口，保持窗口运行。

窗口 1，启动后端：

```cmd
cd QaMentalHealthKnowledgeServicesAdolescents.springboot
mvn.cmd spring-boot:run "-Dmaven.repo.local=.maven-repository"
```

窗口 2，启动前端：

```cmd
cd QaMentalHealthKnowledgeServicesAdolescents.elementui
npm.cmd ci --cache .npm-cache
npm.cmd run dev
```

- 前端页面：<http://localhost:8000>
- 后端接口：<http://localhost:7406>

前端已配置连接本地 `7406` 端口。启动前请确保 MySQL 和 PostgreSQL 服务运行。端口被占用时，先确认是否已经启动同一服务。

## 构建与测试

前端目录执行：

```cmd
npm.cmd run build
```

后端目录执行：

```cmd
mvn.cmd test "-Dmaven.repo.local=.maven-repository"
```

## 配置与使用说明

本地密钥文件、依赖、缓存、日志、构建产物和 IDE 配置均不纳入版本管理。`external-resources` 包含项目已有的演示素材；后续上传真实用户文件前应检查其内容和使用权限。

AI 回答和测评结果仅供知识参考和自我了解，不替代专业诊断与治疗。

JWT_SECRET 请使用随机生成的长字符串。SQL 不提供账户密码；普通模式需自行注册，评审演示模式自动设置本文列出的两个固定账号。

权限、后端计分、首次管理员设置及后续上线顺序见 [上线第 1 步](docs/deployment-step-1.md)。更新后需要重启后端并重新登录，前后端应同时更新。

# 环境配置与依赖清单

文档复核日期：2026-11-14。本说明对应仓库现有配置，不修改数据结构、量表或知识内容。本地操作使用 Windows CMD，启动入口见 [README](../README.md)。

## 工具与服务

| 工具 / 服务 | 版本或条件 | 检查命令（CMD） |
| --- | --- | --- |
| Git | 支持 HTTPS 克隆 | `git --version` |
| Java | JDK 17 或兼容版本 | `java -version`、`javac -version` |
| Maven | 3.9.x，使用正确的 JAVA_HOME | `mvn.cmd -version` |
| Node.js / npm | Node 18+，建议 22 LTS | `node -v`、`npm.cmd -v` |
| MySQL | 8.x | `mysql --version` |
| PostgreSQL | 建议 16；数据库服务已启动 | `psql --version` |
| pgvector | 已安装到上述 PostgreSQL，能启用 vector 扩展 | 按 README 在新库执行 `CREATE EXTENSION IF NOT EXISTS vector;` |
| DeepSeek | 可用 Key、模型权限和额度 | 通过服务端配置后验收问答 |
| 硅基流动 | 可用 Embedding Key、模型权限和额度 | 首次新环境建立向量索引后验收 |

`mysql --version` / `psql --version` 只能检查客户端，不能证明数据库服务已启动。若命令不识别，把对应安装目录的 `bin` 加入 PATH，重新打开 CMD。Windows 上安装 pgvector 时，扩展文件必须匹配 PostgreSQL 的版本及架构。

## 本地配置文件与加载方式

在项目根目录执行：

```cmd
if not exist .env.properties copy .env.example .env.properties
notepad .env.properties
```

在文件中填写自己的配置，不要删除或覆盖已可用的本地配置。该文件使用 `名称=值`，不要加 `export`、`set` 或不必要的引号。

Spring Boot 配置导入 `../.env.properties` 与 `.env.properties`，路径相对于进程工作目录。按 README 进入后端目录后启动，可以读取项目根目录的文件；同名系统环境变量也可以提供配置。生产发布包从 `backend/` 目录启动，私密文件放在其上一层。

### 必填配置

| 变量 | 用途 | 默认 / 填写说明 |
| --- | --- | --- |
| `MYSQL_USERNAME` | MySQL 用户名 | 默认 `root`，按自己的数据库填写 |
| `MYSQL_PASSWORD` | MySQL 密码 | 模板留空，填写自己的密码 |
| `PGVECTOR_USER` | PostgreSQL 用户名 | 默认 `postgres`，按自己的数据库填写 |
| `PGVECTOR_PASSWORD` | PostgreSQL 密码 | 模板留空，填写自己的密码 |
| `JWT_SECRET` | 登录 Token 签名 | 无可用公开值，生成私有随机长字符串，建议至少 32 字符 |
| `DEEPSEEK_API_KEY` | AI 文本回答 | 模板留空，填写自己的 DeepSeek Key |
| `EMBEDDING_API_KEY` | 文档及问题向量生成 | 模板留空，填写自己的硅基流动 Key |

两个 API Key 属于不同服务，不能互相替代。缺少模型配置时，不能据此验收完整 AI 链路；拿到普通文本回复也不能单独证明向量检索成功。

### 可选配置

这些变量在模板中以注释提供。需要覆盖默认值时再取消注释，不需要时保持注释；**不要把带默认值的变量设置为空值**。

| 变量 | 默认行为 | 使用场景 |
| --- | --- | --- |
| `MYSQL_URL` | 连接本机 `3306`、项目同名数据库 | 修改 MySQL 地址、端口或库名，使用完整 JDBC URL |
| `PGVECTOR_HOST` | `localhost` | 修改向量数据库主机 |
| `PGVECTOR_PORT` | `5432` | 修改向量数据库端口 |
| `PGVECTOR_DATABASE` | `qa_mental_health_knowledge_services_adolescents` | 修改 PostgreSQL 库名 |
| `PUBLIC_API_URL` | 开发为 `http://localhost:7406`；prod 为 `/api` | 生成公开资源 URL；同站生产部署保持未设置或 `/api` |
| `SERVER_ADDRESS` | prod 为 `127.0.0.1` | 后端监听地址；同机 Nginx 部署保持默认 |
| `DEMO_ACCOUNT_PASSWORD` | demo 为 `12345678` | 仅演示模式覆盖固定测试账号密码；按评委说明运行时保持未设置 |

## 模型、向量和端口

模型配置位于后端 `src/main/resources/application.yml`：

| 配置 | 当前值 |
| --- | --- |
| DeepSeek 地址 | `https://api.deepseek.com` |
| DeepSeek 模型 | `deepseek-flash` |
| 嵌入地址 | `https://api.siliconflow.cn/v1` |
| 嵌入模型 | `BAAI/bge-large-zh-v1.5` |
| 向量维度 | `1024` |
| 向量表 | `qa_mental_health_knowledge_services_adolescents` |

以上是仓库当前配置值，服务账号须支持对应模型。若服务商调整模型可用性，需按实际账号核对后配置；更换嵌入模型或维度涉及索引兼容性，不能只改 Key。

| 服务 | 本地端口 | 说明 |
| --- | --- | --- |
| Web 前端 | **8000** | 开发和本地 preview 均使用此端口 |
| Spring Boot | **7406** | 后端接口，不是用户网页端口 |
| MySQL | 3306 | 业务数据库 |
| PostgreSQL | 5432 | 向量数据库 |

前端 `.env.development` 仅包含公开接口地址；`.env.production` 使用同站 `/api`，由 Nginx 转发到后端。`VITE_` 变量会进入浏览器构建产物，不能保存密码或 API Key。`npm run preview` 用于本地检查构建结果，正式部署使用 Nginx。

## 前端依赖

准确清单以 [package.json](../QaMentalHealthKnowledgeServicesAdolescents.elementui/package.json) 为准；完整解析版本及间接依赖由 [package-lock.json](../QaMentalHealthKnowledgeServicesAdolescents.elementui/package-lock.json) 记录。评审者使用 `npm.cmd ci --cache .npm-cache` 安装，不需要复制 `node_modules`。

| 类别 | 主要依赖及清单版本 |
| --- | --- |
| UI 与路由 | Vue `3.5.13`、Element Plus `2.5.1`、Vue Router `4.2.5` |
| 状态管理 | Pinia `2.1.7`、pinia-plugin-persistedstate `3.2.1` |
| 请求与工具 | Axios `1.6.2`、crypto-js `4.0.0`、js-cookie `3.0.5`、Moment `2.29.4`、nprogress `^0.2.0` |
| 图表与编辑器 | ECharts `5.5.1`、echarts-wordcloud `^2.1.0`、AiEditor `1.3.3` |
| 构建 | Vite `5.0.0`、plugin-vue `4.5.0`、Sass / sass-embedded `1.77.5` |
| 开发辅助 | ESLint `8.54.0`、eslint-plugin-vue `9.18.1`、unplugin-auto-import `19.3.0`、unplugin-vue-components `28.0.0` |

## 后端依赖

完整直接依赖以 [pom.xml](../QaMentalHealthKnowledgeServicesAdolescents.springboot/pom.xml) 为准。Spring Boot 父 POM `3.3.1` 管理未显式指定版本的组件，Java 编译目标为 `17`。

| 类别 | 主要依赖及清单版本 |
| --- | --- |
| Web / JDBC / 测试 | spring-boot-starter-web、jdbc、test、devtools，由 Boot 管理 |
| 安全与辅助 | spring-security-crypto、Lombok、jackson-datatype-jsr310，由 Boot 管理 |
| 数据访问 | mybatis-plus-spring-boot3-starter `3.5.7`、mysql-connector-j `8.0.33` |
| 登录与对象转换 | java-jwt `3.19.2`、commons-beanutils `1.9.0` |
| 文档与 HTTP | POI / poi-ooxml `4.1.0`、httpclient `4.5.3` |
| RAG | LangChain4j 核心、open-ai、pgvector、Tika 文档解析器、all-minilm-l6-v2，均为 `0.34.0` |
| 中文与其他 | HanLP `portable-1.8.4`、os-spring-boot-starter（POM 当前使用 `LATEST`） |

`os-spring-boot-starter` 的版本声明为动态 `LATEST`，并非锁定版本；既有本地环境记录解析为 `1.2.0`，本次未重新下载依赖，其他环境以实际 Maven 解析结果为准。本次仅复核文档，不更换依赖或改业务代码。可在后端目录查看实际依赖树：

```cmd
mvn.cmd dependency:tree "-Dmaven.repo.local=.maven-repository"
```

## 常见问题

| 现象 | 检查方法 |
| --- | --- |
| 后端连接数据库失败 | 确认服务运行，账号、密码、端口与库名正确；不要重新导入已有数据库 |
| 找不到 vector 扩展 | 先安装与 PostgreSQL 匹配的 pgvector，再在目标库启用扩展 |
| 登录失败或 Token 无效 | 确认 JWT_SECRET 非空、后端已重启；切换密钥后重新登录 |
| user / admin 不存在 | 仅新建评审环境使用 `demo` profile 启动，普通 / prod 模式不会自动创建 |
| AI 鉴权失败 / 余额不足 | 检查对应服务的 Key、模型权限及额度，不要把 Key 放进前端 |
| AI 能回复但检索不完整 | 检查嵌入服务和向量库；新环境须建立真实向量，不能只看 MySQL 的 vector_id |
| 8000 或 7406 被占用 | 用 `netstat -ano` 查看是否已有同一服务运行，避免重复启动 |
| 依赖下载失败 | 检查网络和 Maven / npm 配置，修复下载错误后重试 |

本文件仅描述配置和运行方法，不代表已在另一台干净电脑或公网服务器完成验收。

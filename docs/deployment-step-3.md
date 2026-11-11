# 上线第 3 步：部署文件与本地预演

当前没有服务器，本阶段仅准备可部署文件和验证配置，不表示已经公网部署。部署方案为同一台服务器上的 Nginx、Java 17+、MySQL 8、PostgreSQL/pgvector；日后选择 Docker 时需另配容器网络，当前后端默认仅监听本机回环地址。

## 已提供的配置

- 前端生产构建访问同站 `/api`；开发模式仍为 `http://localhost:8000` 连接 `7406`。
- 后端 `prod` 配置默认监听 `127.0.0.1:7406`，上传返回 `/api/...` 地址，不写死公网域名。数据库连接支持环境变量覆盖。
- `deploy/nginx.conf` 提供 SPA 页面回退、API 路径转发、SSE 禁用缓冲、180 秒读取超时、API 限流和 50MB 请求上限。单文件限 20MB，单批限 10 个文件。
- 上传保存路径兼容 Windows/Linux，拒绝路径分隔符和不支持的扩展名；支持图片、视频、PDF、TXT、MD、Office 文档。上传文件仍为公开资源，勿上传私人聊天、病历等资料。
- 文档解析、摘要和向量化均读取本地上传文件，兼容旧链接、HTTPS、`/api` 前缀及中文编码文件名，不回退为下载任意网址。

## 在当前电脑构建（CMD）

从项目根目录执行，保持命令失败时先处理错误：

```cmd
cd QaMentalHealthKnowledgeServicesAdolescents.springboot
mvn.cmd package "-Dmaven.repo.local=.maven-repository"
cd ..\QaMentalHealthKnowledgeServicesAdolescents.elementui
npm.cmd ci --cache .npm-cache
npm.cmd run build
cd ..
scripts\package-production.cmd
```

脚本需要 Git 和 Windows 自带的 `tar.exe`，生成独立的 `release\build-随机数-随机数` 文件夹，不覆盖已有发布包。包含：`backend/app.jar`、版本库中已有的演示素材、`html/` 前端、启动脚本、Nginx 模板、MySQL 初始 SQL、空密钥模板和本文。实际密码、Key、数据库备份及未提交的上传文件均不打包。

打包脚本使用已存在的 JAR 和 dist，不自行重建；代码更新后应重新执行上面的构建命令。

## 本地预演生产模式（CMD）

先停止占用 7406、8000 的旧开发进程；MySQL、PostgreSQL 保持运行。项目根目录已有 `.env.properties` 时，不要覆盖它。

窗口一，在后端目录运行：

```cmd
java -jar target\web-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

窗口二，在前端目录运行：

```cmd
npm.cmd run preview
```

打开 `http://localhost:8000`。Vite preview 仅用于本地预演，已配置 `/api` 代理；线上使用 Nginx。`prod` 不自动创建演示账号，当前本地数据库已有的 `user`、`admin` 不会被删除或改密。首次评审数据库可先按评委说明初始化演示账号；真实用户生产数据库应独立维护，不与公开演示账号共用。

## 将来放到服务器的步骤

1. 创建独立的 MySQL、PostgreSQL 数据库和服务账号，启用 vector 扩展。首次新库可导入发布包中的 MySQL SQL；它会重建表，不能用于覆盖已有生产数据。
2. 在发布包根目录复制 `.env.example` 为 `.env.properties`，填写数据库密码、JWT、DeepSeek、嵌入服务 Key。必要时填写 `MYSQL_URL`、`PGVECTOR_HOST` 等地址；本方案的 `PUBLIC_API_URL` 保持未设置，或明确设为 `/api`，不要填写空值或 localhost 地址。配置文件放服务器本地，不提交 Git，也不放 Nginx 的 `html/` 中。
3. 进入 `backend/`：Windows 执行 `start-prod.cmd`；Linux 执行 `sh start-prod.sh`。进程工作目录须保持为 `backend/`，才能读取上传素材和上一层配置文件。再配置系统的进程托管与重启。
4. 将 `html/` 的内容放进 Nginx 的静态目录。`nginx.conf` 是完整独立模板，`mime.types` 使用 Nginx 自带文件，`root html` 相对于 Nginx 前缀目录。测试配置 `nginx -t` 后再加载；不要直接覆盖服务器上已有其他站点配置。
5. 当前 Nginx 模板仅提供 HTTP 冒烟检查。确定域名后需补域名、证书与 HTTPS 跳转。只公开站点端口；后端 7406 和数据库保持内网访问。按环境安排 MySQL、PostgreSQL 和整个 `backend/external-resources/` 的备份及恢复演练。
6. 首次初始化后，在知识文档后台执行“重建全部分块向量”。本地向量不在发布包内；嵌入模型与维度必须一致。原文档 ID 4 仍需补传。
7. 验收登录、普通用户隔离、测评、AI 流式输出、来源打开、刷新和重启后的历史、图片上传及索引检查。确认通过后再公开站点。

正式上线仍待服务器、域名/证书、进程托管及备份配置。本地接口测试不能代替公网浏览器与手机验收。

## 2026-11-11 本地验收记录

| 项目 | 结果 |
| --- | --- |
| 后端测试与 JAR 打包 | 63 项通过，打包成功 |
| 前端 SSE 测试及生产构建 | 4 项通过，构建成功 |
| `prod` JAR 启动 | 仅本机 7406，使用现有私密配置成功启动 |
| Nginx 代理与页面 | 本机 8000 页面、登录、API 转发成功 |
| 权限与索引 | 游客管理接口 401、普通用户 403；40/40 向量关联完整 |
| 文件链路 | 旧文档链接转换为 `/api` 后可读取，新上传文件内容往返一致 |
| 真实 AI 经 Nginx 流式回复 | `deepseek-flash`，578 Token，向量检索命中 3 个来源，历史保存成功 |
| 发布脚本 | CMD 实际执行成功，29 个演示素材逐一校验一致，无私密配置或日志 |

以上 AI 测试用量来自真实服务响应。Nginx 原始模板的语法检查通过，但当前 Windows 禁止绑定 80；本地使用同一模板修改监听地址为 `127.0.0.1:8000`、静态根目录为构建输出后，完整 `nginx -t` 及实际代理验证通过。正式服务器仍需重新检查端口、配置及 TLS。

浏览器自动化再次因工具无法识别地址而终止；没有声称完成页面点击、刷新或手机适配验收。上述页面资源及交互接口由 HTTP 联调验证。

配置依据：[Nginx proxy 模块](https://nginx.org/en/docs/http/ngx_http_proxy_module.html)、[Spring Boot 3.3 代理头配置](https://docs.spring.io/spring-boot/3.3/how-to/webserver.html)。

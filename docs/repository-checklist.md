# 代码仓库与文档交付检查

文档版本：2026-11-16 整合版。所有当前材料由 [最新版入口](delivery.md) 导航，技术 PDF、Word、文字版使用同一份正文。

| 要求 | 当前提供 |
| --- | --- |
| 托管平台 | [hahahachaoge/xinyu](https://github.com/hahahachaoge/xinyu)，Public，main 分支 |
| 代码规范 | 前后端分目录、后端分层、关键注释及 [协作规范](code-conventions.md) |
| README | 项目、技术架构、CMD 安装运行、团队及前置文档入口 |
| Git 记录 | 完整真实提交历史，后续采用 feat、fix、docs 等类型 |
| 环境配置 | .env.example、依赖清单、锁文件及 [环境说明](environment.md) |
| 技术材料 | [正式 PDF](submission/technical-plan.pdf)、[Word](submission/technical-plan.docx) 与 [文字版](submission/technical-plan.md) |

## 评审访问

评委无需登录 GitHub 即可查看公开源码、文档和历史。网站入口为 [心屿](https://xinyu-care.xyz/#/front/home)，已上线；本地复现按 README 配置自己的服务凭据。

应用测试账号 user / admin 与 GitHub 账号无关。完整 Git 历史须使用 git clone 获取，网页下载 ZIP 不携带 .git：

```cmd
git clone https://github.com/hahahachaoge/xinyu.git
cd xinyu
git log --oneline --graph --all
```

## 统一信息

学校为华南师范大学，队伍为“不知名队伍-”，队长黄彪骐，队员蓝俊豪、林佳逸，指导教师刘寿强；职责见 [团队信息](../README.md#团队信息)。项目此前未发表、未获奖，非基于其他项目优化改进。

本地网页端口为 8000、后端为 7406；生产使用 HTTPS 与同站 /api。生成模型为仓库配置的 deepseek-flash，嵌入模型为 BAAI/bge-large-zh-v1.5，向量为 1024 维。当前聊天页不显示片段编号与来源标签。

## 环境与公开文件

- 真实数据库密码、JWT 密钥和模型 Key 由部署者配置，不放入前端或公开材料。
- .env.properties、.local/、release/、output/、tmp/、缓存、日志和构建目录不作为源码交付；正式报告仅从 docs/submission/ 有选择地公开。
- 应用内固定测试密码是所有者指定公开的演示凭据，不能当作数据库或模型密码。
- 向量不随 MySQL SQL 提供；新评审环境自行建立索引，已有数据库不重复导入带重建表的主 SQL。
- 依赖与模型配置以代码为准；动态 LATEST 依赖的限制见环境说明，不把文档整理表述为另一台机器已复现成功。

本次文件、链接及敏感信息核对结果在提交前复查；业务和历史验证范围统一见 [验证记录](verification.md)。

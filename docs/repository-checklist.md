# 代码仓库交付检查

本文件仅对应“代码仓库”五项要求。检查日期：2026-11-11；不涉及本次修改量表、知识库、数据库或网页功能。

| 要求 | 已提供的内容 | 状态 |
| --- | --- | --- |
| 托管平台 | [GitHub 仓库](https://github.com/hahahachaoge/xinyu)，主分支 main | 已设为 Public，评委无需登录即可查看 |
| 代码规范 | 前后端分目录；后端分层；关键流程与配置注释；[代码规范](code-conventions.md) | 已检查现有结构并补充规范说明，未做全项目格式重写 |
| README | 项目介绍、功能、架构图、技术栈、CMD 安装运行、测试与团队栏 | 已补齐文档结构；真实团队信息待所有者填写 |
| Git 记录 | 保留从首次导入到实际功能和修复的提交，后续使用 Conventional Commits | 原有真实提交全部保留，文档补充另行提交，不伪造或改写历史 |
| 环境配置 | `.env.example`、[变量及依赖说明](environment.md)、前端 package.json / lock、后端 pom.xml | 已提供；真实密码和 Key 保留在本地 |

## 评审访问

所有者已明确选择 Public，并完成 GitHub 身份验证；2026-11-11 仓库设置已确认显示公开。评委可直接打开仓库链接查看代码与提交历史，不需要单独邀请，也不需要拥有 GitHub 账号。

公开源码不等于网页已经公网部署。评委本地运行仍需按 README 配置自己的数据库与模型服务凭据。

仓库账号与网站账号相互独立：`user` / `admin` 是应用内测试账号，不能用于登录 GitHub。

## README 团队信息

按所有者此前要求，“学校 / 单位、团队名称、指导教师、成员姓名及分工、联系方式”先保留为“待填写”。提交前应替换为真实信息；占位不等于已经完成正式团队材料。

## 环境与隐私

- 评审者从模板创建自己的 `.env.properties`，配置数据库、JWT 和两个模型服务 Key。
- `.env.properties`、`.local/`、`release/`、依赖缓存及构建目录由 `.gitignore` 排除；不上传私人数据库备份。
- 应用内固定演示密码由所有者指定公开；真实数据库密码、JWT 和 API Key 不属于演示凭据。
- GitHub 源码不包含本地 PostgreSQL 向量。新建评审环境按 README 初始化数据库、维护演示账号并建立向量；已有数据不重复导入 SQL。
- 依赖清单已提供，但后端有一项动态 `LATEST` 依赖，细节见环境说明；未把文档核对描述成干净环境部署成功。

## Git 历史查看

评审者在 CMD 中执行：

```cmd
git clone https://github.com/hahahachaoge/xinyu.git
cd xinyu
git log --oneline --graph --all
```

不要使用 `--depth 1` 获取评审副本；GitHub 下载 ZIP 不携带提交历史。后续新功能用 `feat(scope): ...`，修复用 `fix(scope): ...`，本次仅文档改动用 `docs(scope): ...`。

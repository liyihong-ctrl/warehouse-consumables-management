# 仓库消耗品管理系统

软件工程课程小组项目，提供仓库消耗品、领用与报废等业务管理功能。代码按课程项目结构保留前后端与数据库表结构，项目中原有第三方凭据、真实邮箱及演示用户数据已从此副本移除。

## 技术栈

- 后端：Java 8、Spring Boot、MyBatis、MySQL
- 前端：Vue 2、Vue Router、Vuex、Element UI、Axios

## 目录

- `后端代码_frame/`：Spring Boot 服务端
- `前端代码_Web_frame/`：Vue 前端
- `数据库脚本/crush_database.sql`：MySQL 表结构（不包含原始演示数据）

## 本地运行提示

1. 安装 Java 8、Maven、Node.js、MySQL。
2. 创建 MySQL 数据库 `crush`，按需导入 `数据库脚本/crush_database.sql`。
3. 设置 `DB_URL`、`DB_USERNAME`、`DB_PASSWORD` 环境变量后启动后端：在 `后端代码_frame/` 运行 `mvn spring-boot:run`。
4. 在 `前端代码_Web_frame/` 安装依赖并运行 `npm run serve`。前端默认请求 `http://127.0.0.1:8888`，可按需调整 `src/utils/base.js`。
5. 邮件、七牛云上传等可选功能需要自行设置 `MAIL_*`、`QINIU_*` 环境变量。不要将密钥写入源码或提交到仓库。

> 这是课程项目整理副本。提交前请确认小组成员同意代码托管，并在简历中如实填写个人负责模块与项目成果。

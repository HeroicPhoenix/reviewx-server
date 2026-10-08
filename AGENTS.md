# ReviewX 开发入口

开始工作时先阅读 `docs/coding-standards.md`、`docs/项目概览.md` 和 `docs/开发进度.md`，再根据 `docs/源码索引.md` 定位模块。前端位于 `/Users/mack/Documents/WorkSpace/WebStormProject/reviewx-front`，跨端修改同时阅读其开发记录。

遵守开发规范中的分层、参数校验、日志、异常和 SQL 复用要求。规范内的 datax 包名是模板，本项目使用 `com.lvwyh.reviewx.web`。

`ANSWER_RECORD` 只新增和查询；`QUESTION_ANALYSIS` 不得删除，仅允许按 `(USER_ID, QUESTION_ID)` 精确新增或覆盖。不得通过导入、题目清空或用户管理破坏历史数据。

结构发生变化才同步修改 `docs/ddl.sql`，保持非破坏性初始化。涉及题库时运行 `mvn test`，包含永久数据保护测试。测试使用隔离数据，不把业务数据库当测试库。

每次完成开发后更新两端 `docs/开发进度.md`，说明行为、接口、验证结果和待办；架构变化同步项目概览。保持历史记录，不把本地验证写成线上验收。

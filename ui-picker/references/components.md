# components（组件语义）

## 登记四层

| 层 | 要声明 |
| --- | --- |
| 来源 | shadcn / 自有 / 业务定制 |
| 语义角色 | 状态 / 分类 / 动作 / 容器 / 导航 / 反馈 |
| 变体与状态 | size、variant、loading、disabled… |
| 组合边界 | 允许/禁止嵌套与替代 |

## 易混

| 对 | 差别 |
| --- | --- |
| Badge / Tag | 状态·计数 vs 分类·可选·可移除 |
| Modal / Dialog / Drawer | 打断程度、信息密度、退出方式 |
| Tabs / Tabs-Switch | 同空间视图 vs 模式/口径 |
| Dropdown / Menu / Command | 局部选择 / 动作集 / 搜索式操作 |

## Illustrative mapping (agent-ops list row)

Generic example — adapt per product; not a fixed template.

| Datum | Role | Component |
| --- | --- | --- |
| running / failed | run status | Badge |
| high-risk | risk | RiskBadge (+ domain) |
| instance type / environment | category | Tag |
| view log | inline action | Link/Button |
| retry | recovery action | Button |

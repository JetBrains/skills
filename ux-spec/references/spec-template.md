<!-- spec-schema: 2 -->

# [功能名] 交互设计 Spec

schema 2 adds the L2-L5 structured field blocks (per-page duty table, path table, per-page five-state matrix) consumed by the deepened G1 gate. Legacy schema-1 specs are not re-checked; new runs author against this template.

## L1 定位与意图
- 用户可见目标：
- 目标用户：
- 场景清单：
- 非目标：
- 行为边界：始终 / 询问后 / 永不

（成形会话投影义务：五个字段一一对应契约 `l1.goal / l1.target_user / l1.scenes / l1.non_goals / l1.boundaries`；假设值显式标注「假设」并指向契约字段路径。）

## L2 信息架构
- 空间区域定义：
- 区域边界规则：
- 内容生长规则：

### Page duties

| Page | Duty |
| --- | --- |
| <page-id> | <one owner duty per page — what this page alone is for> |

## L3 核心链路
- 状态清单：
- 主链路：
- 分支链路：

### Paths

| Path | Steps |
| --- | --- |
| P1 | <page/decision points in order — primary path; structural alternatives go through CP-B> |

## L4 组件功能细节
- 组件定位与功能清单
- 默认 / 悬停 / 加载 / 禁用 / 错误 等状态
- L4 declares control behavior only; reuse / no-internal-change constraints must name exceptions (for example, allow a minimal patch when they conflict with L5).

## L5 边界条件
- 空态：
- 加载态：
- 错误态：
- 权限降级：

### Five-state matrix

| Page | initial | loading | success | failure | empty |
| --- | --- | --- | --- | --- | --- |
| <page-id> | <value or n/a (reason)> | <value> | <value> | <value> | <value> |

## L6 验收标准
- 每条验收是一个顶层列表项，按序显式包含 `Given` → `When` → `Then`（顺序固定），并写明该条的必备证据，且以 `(path: P<n>)` 引用 L3 路径表中一条可达路径
  - 必备证据：规划声明覆盖 / 目标视口渲染 / 交互记录或自动化检查 / 相关 test、type、lint、build（按任务适用项选择）
  - 证据为运行时状态时，命名 capture seed（要捕获的状态 + 捕获类型，如 "error-state screenshot"）；不写 selector/URL/actions
- 设计完成定义：

---

## Worked snippet (illustrative)

For an agent-ops list: a failed item must show cause + retry (L3/L4); no-data shows a non-blank empty state (L5); without permission the dangerous action is disabled with a reason (L5); acceptance ticks each of these (L6) and names the path that exercises it. Adapt to the actual product; this is not a fixed domain.

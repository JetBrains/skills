# domain（领域语义，可按产品替换）

## 任务状态

| 状态 | 表达 |
| --- | --- |
| 排队 | 中性，不示风险 |
| 运行中 | 正常进行 |
| 完成 | 成功 |
| 失败 | 必须提供恢复动作 |
| 超时 | 原因 + 后续操作 |

## 风险色（示例 token 角色）

- 高危 → `var(--warning-high)`
- 可疑 → `var(--warning-medium)`
- 低风险 → `var(--warning-low)` 或 `var(--info)`

## Data safety

- Secrets, credentials, account IDs, host IPs default to masked
- Plaintext requires explicit click; reveal action may be audited

## 危险操作

关闭防护、批量删除、解除高危屏蔽等：二次确认 + 写清后果，禁止「确定吗？」空确认。

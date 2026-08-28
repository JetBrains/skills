# design.md（视觉系统执行约束）

## 意图（默认）

- CJK-first；控制台密度优先；品牌色克制；中性色承层级。

## 角色示例

- `--brand` 主 CTA
- `--brand-surface` 选中行/软徽章
- `--foreground-link` 正文链接
- `--warning-high` 高风险
- `--chart-1..12` 多系列图

## 执行三律

1. 所有视觉值走 `var(--*)`
2. hover/active/disabled/selected 从基础 token 派生
3. 找不到 token：记 `gaps.log` + 合法 fallback 或拒生成该细节

禁止裸写 hex、随意 px/ms/cubic-bezier 字面量绕过系统。

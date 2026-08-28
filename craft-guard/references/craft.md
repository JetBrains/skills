# craft 细则

## 状态反馈

等待分层归 `SKILL.md` loading tiers；此处只管失败与降级：

- 失败：原因 + 可恢复动作（重试/忽略/查看日志）
- 权限不足：禁用 + 需要的权限说明

## 工艺

- 嵌套圆角与间距分层，忌全站同一圆角同一阴影
- 阴影档位少而稳；层级靠 surface 不靠彩虹边
- 中英混排：中文行高与标点优先

## 交互 affordance（L4 交互区，grill v0.3 Q3.4）

每个 L4 声明的交互区（行、卡片、按钮组、可点单元）须有有意的 motion/hover affordance，且在 craft review 里说明用途：

- 默认 hover/active 有过渡（opacity / transform / background 之一，~120ms），用途写在 craft 报告（如「行可点 → 提示可进入详情」「行只读 → 不加 hover」）。
- 静态 throwaway 也得体现 affordance 目标：要么给出 hover，要么显式声明「此区只读，无 hover」——不允许「交互区既无 hover 又无声明」的静默 PASS。
- 数据表/list 的 ledger 行尤其易漏：扫描密集时 hover 是可扫描性 affordance，不是装饰。

**Done when：** 每个 L4 交互区都有 motion/hover purpose（给出或显式声明只读）；无静默漏 hover 的交互区。

## 图表

- 分类色稳定可复述；风险色回 `domain`
- 容器与坐标可读，不为「炫」牺牲扫描

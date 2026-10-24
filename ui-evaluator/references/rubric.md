# evaluator rubric

## 维度如何选

- **落地页**：设计质量 / 原创性 / 工艺 / 功能
- **控制台**：设计质量 / 可用性 / 信息密度 / 工艺 / 一致性

维度从场景读出，引擎不写死；**判据必须能回到声明**。

## 回流示例

| 发现 | 表层说法 | 回指 |
| --- | --- | --- |
| 失败无重试 | 交互不完整 | `spec` 状态流 |
| 高危灰标签 | 颜色不醒目 | `domain` + `components` |
| 按钮硬编码 hex（如 `#4f46e5`） | 代码不规范 | `design` token |
| 列表变卡片墙 | 版式不对 | `template` |

## 禁止

- 「整体还可以优化」无指征
- 只改 CSS 不回到声明
- 用新审美词覆盖未写清的 L5

## preview seam 健康（supporting，ADR-0008）

若该 run 跑了 `preview*`（`.scratch/<run>/preview/log.md` + `confirm-round-*.json` 存在），把它列为 supporting finding：

- 读 `preview/log.md` + confirm json：反馈是否驱动了 revision，还是空/无关锚点滑过结构 floor。`decision-round-*.json` 仅用于审计/恢复，不是确认权威或第二份语义输入（ADR-0013）。
- 结构 floor（adapter，G5）只挡空反馈/无注释锚点；**语义**问题（如「安师大」这种与被批注元素无关的合法字符串）floor 挡不住，靠这里兜。
- `source` 归 `preview* seam`（orchestrator 的 preview 步骤契约），不是 UI source —— 当缺陷在 adapter loop 契约而非生成 UI 本身时用这个。
- 过程缺口（seam 契约）与产品 findings（UI）分开记；不混在回流闭包 trail 里。

## observe* mirror surface（supporting）

若 `evidence/manifest.jsonl` 任一条 capture 声明 **`surface: mirror`**（或等价 note），必须有 finding：

```text
issue:    observe used semantic mirror, not live Fill host
source:   observe* seam
fix:      re-capture on live host URL when available; keep surface: mirror until then
severity: S1
```

Do not treat G6 artifact presence alone as proof the Fill tree was runtime-verified.

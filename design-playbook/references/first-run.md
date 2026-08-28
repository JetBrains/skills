# First-run guide (greenfield)

Operational route for a first-time greenfield product UI ask. Existing-product baseline onboarding is out of scope until a dedicated dogfood run exists.

## Route

1. `/design-playbook:design-io <ask>` (or host equivalent)
2. Skip `design-baseline` unless the router returned `requires_baseline` (greenfield: no existing product UI)
3. Skip `reference-intake` unless the router returned `requires_reference_contract`
4. `ux-spec` → six-layer `spec.md` (**pause** if L1 boundaries need a user decision)
5. `plan.md` handoff
6. `ui-picker` decision report (**pause** if platform/native route unclear)
7. `preview*` only if adapter present (**pause** for HITL confirm)
8. Fill
9. `craft-guard`
10. `observe*` only if adapter present
11. `ui-evaluator` + verdict (**pause** on Recirculate)

## Pause points

| Pause | Why | Resume with |
| --- | --- | --- |
| L1 always/ask/never | Authority / scope | User answer recorded in L1 |
| Platform unclear | Native vs Web route | One clarifying answer |
| preview* HITL | User confirmation | confirm-round with floor_pass |
| Recirculate verdict | Blocking findings | Smallest owning declaration fix |

## Stuck?

```text
python <plugin>/scripts/run_status.py .scratch/<run>
python <plugin>/scripts/doctor.py
```

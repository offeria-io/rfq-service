# Offeria Development Guide

## Working model

GitHub is the technical source of truth. Work is issue-driven and merged through reviewed pull requests.

## Branches

Use short-lived branches tied to the work item.

Examples:

- `feat/23-rfq-items`
- `fix/31-offer-number-sequence`
- `docs/1-platform-contracts`
- `chore/42-version-alignment`

Do not mix unrelated local WIP into an issue branch.

## Commits

Use Conventional Commits:

- `feat(rfq): add RFQ item lifecycle`
- `fix(pricing): make calculation idempotent`
- `docs(architecture): document platform contracts`
- `test(material): cover alias promotion`
- `chore(build): align Spring baseline`

## Pull requests

A PR should:

1. link its GitHub issue;
2. explain scope and non-goals;
3. include validation evidence;
4. update API/event/config/docs when contracts change;
5. avoid unrelated refactors;
6. pass tests/build checks before merge.

Prefer squash merge for a clean issue-oriented history unless preserving commits has a specific value.

## Testing expectations

Depending on the change:

- unit tests for domain rules;
- repository/integration tests for persistence;
- controller/API tests for contracts;
- Kafka integration tests for producers/consumers;
- Testcontainers for infrastructure-dependent integration where practical;
- end-to-end tests for critical RFQ-to-delivery paths.

## Database changes

Persistent schema changes require Flyway migrations under version control. Never rely on production `ddl-auto=update`.

## API/event changes

Breaking API or event changes require explicit versioning and dependent-service review.

## Definition of Done

Implementation is Done when code, tests, migrations/configuration, documentation, PR evidence and required integration verification agree with the acceptance criteria. A Trello card is moved to Done only after the GitHub evidence is complete.

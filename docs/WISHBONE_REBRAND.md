# Wishbone — independent rebranding workstream

This branch is a separate development track based on `Projekt2_Gruppe_6` / Ønskeskyen.

## Guardrails
- Keep the original group's `main` branch unchanged.
- Do not merge Wishbone changes into the group's `main` without explicit agreement.
- Deploy Wishbone as a **separate Railway service/project and separate database**. Do not point it at the group's production database.
- Remove hardcoded database credentials from the new application's configuration before deploying. Use environment variables and rotate any previously exposed credentials.
- Preserve attribution and review the project's licensing and contributors' rights before publication or commercialization.

## Rebranding roadmap
1. Rename display labels and page titles from Ønskeskyen to Wishbone, without unnecessarily renaming Java packages at first.
2. Redesign mobile layouts: single-column cards, responsive typography, image sizing, clear action hierarchy and accessible tap targets.
3. Apply Gestalt principles and Shneiderman's eight golden rules.
4. Review validation, error messages, forms, create/edit/delete flows, and accessibility.
5. Introduce Wishbone-specific configuration and database schema initialization/migrations.
6. Run automated tests and a manual mobile regression checklist before a separate deployment.

## Status
- Independent branch created.
- No runtime application code changed yet.

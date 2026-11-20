[日本語](./README.md) | [English](./README_en-US.md)

# <img src="Logo/Sansa2023.png" width="24"> What is Project Sansa?

Project Sansa (hereinafter referred to as Sansa) began as an xR open platform—roughly speaking, “something I’d want to use if it existed”—and has evolved into a cross-platform xR system concept spanning VR, AR, MR, and non-XR domains.  
The project name “Sansa” was chosen to evoke the image of a three-way intersection where the “real world,” “virtual world,” and “ideal” converge.  
Although it started as a personal project, anyone who finds it interesting is welcome to join.
(Sorry, but we’re currently only Japanese speakers.)

[Discord](https://discord.gg/wN67tdzrCT)

---

## 1. Our Goals

This is a rather ambitious goal, but ultimately, we aim to create a new personal device and service platform to replace the smartphone.  
Mainly because I want to use it myself.

In Sansa, we treat VR, AR, MR, and non-XR as a single service.

- Non-XR: Calls and communication
- AR: Overlaying information onto the real world
- VR: Virtual space social network
- MR: Fusion of the real and virtual worlds

Users will be able to switch between these modes with simple controls, aiming to naturally bridge the real and virtual worlds.
Additionally, we aim for an open architecture that allows Sansa to connect and integrate not only on its own but also with other VRSNS platforms and services.  
Furthermore, to support the growth of UGC (User-Generated Content), we intend to incorporate mechanisms related to rights management and economic ecosystems.

- [Initial Ideas and Use Cases](./docs/ja-JP/01_初期アイデアと利用シーン.md) (Japanese)

---

## 2. Purpose of This Repository

This repository serves to introduce the overall vision of Project Sansa and the Sansa series, as well as to provide guidance on how to navigate them.  
Additionally, common understandings regarding the Sansa series, particularly terminology, will be shared under the management of this repository.  
ProjectSansa provides common terminology, operational guidelines, and LLM integration policies for the Sansa series.  
We recommend that each Sansa series operate by referring to these guidelines.

For details, please refer to the following documents.  
Ideas for use cases are also listed here.

- [ProjectSansa Table of Contents](./docs/en-US/index.md)

## 3. Introduction to Each Sansa Series

The following overview reflects each repository's `develop` branch README, design materials, code and test structure, checked on 2026-11-20. Status descriptions are summaries, not claims of production readiness or completed releases. Consult each repository's specifications, tests and worklogs for details.

<table>

<tr><td>

### [SansaSphere](https://github.com/mayusaki3/SansaSphere)

Shared ecosystem services for accounts, authentication, rights, provenance, auditing, logging, economy and federation. The design also covers separation into service repositories and federated trust.

### Current Status
- Architecture and infrastructure design

`Authentication` `Rights` `Provenance` `Audit` `Economy` `Federation`
</td></tr>

<tr><td>

### [SansaVRM](https://github.com/mayusaki3/SansaVRM)

A structured model foundation for humanoid and non-humanoid assets. Development includes Rust core APIs, validators and format adapters, with tests for models, modules and related metadata.

### Current Status
- Design, initial implementation and validation

`3D Models` `Rust` `glTF` `VRM` `Modules` `Validation`
</td></tr>

<tr><td>

### [SansaVRM Studio AI](https://github.com/mayusaki3/SansaVRM-Studio-AI)

A creation, conversion, validation and AI preprocessing environment built around SansaVRM. PoCs include project workspaces, workflow management, dependency graphs, a local server and a minimal validation UI. The core is intended to work without AI, with local AI preferred for preprocessing.

### Current Status
- Architecture stabilization and PoC implementation

`3D Authoring` `AI Preprocessing` `Local AI` `Python` `Workflow` `PoC`
</td></tr>

<tr><td>

### [SansaXR](https://github.com/mayusaki3/SansaXR)

XR runtime, OpenXR tracking, multiplayer, synchronization and interaction. Demo 1 compares native implementations in O3DE and Godot against shared requirements before defining common engine boundaries.

### Current Status
- Design and XR runtime technology validation

`XRSNS` `OpenXR` `O3DE` `Godot` `Multiplayer` `Validation`
</td></tr>

<tr><td>

### [SansaXR-WorldCore](https://github.com/mayusaki3/SansaXR-WorldCore)

SansaXR world management: fog-covered undefined regions, geographic cells, photo-based 3D reconstruction, Sandbox-to-Live publication, access policies and world/instance transitions. Worklogs also explore separate performer/audience instances for live events and distribution and resynchronization of stage gimmicks.

### Current Status
- World-management design (pre-implementation)

`World Management` `Fog World` `Spatial Cells` `Instances` `Access Control` `Gimmick Sync`
</td></tr>

<tr><td>

### [SansaCloth](https://github.com/mayusaki3/SansaCloth)

An SDF-based clothing anti-clipping and surface-response foundation. Rust reference code and tests exist alongside validation tools for Godot, Unity and O3DE; these do not imply a production release.

### Current Status
- Reference implementation and engine validation

`Cloth` `Anti-clipping` `SDF` `Rust` `Godot` `Unity` `O3DE`
</td></tr>

<tr><td>

### [SansaVRM MuJoCo Adapter](https://github.com/mayusaki3/SansaVRM-MuJoCo-Adapter)

An adapter connecting SansaVRM with MuJoCo, MJCF and controller configuration. MuJoCo-specific conversion, actuator mapping and diagnostics are separated from SansaVRM; Python modules and tests are under development.

### Current Status
- Design, foundational implementation and tests

`SansaVRM` `MuJoCo` `MJCF` `Python` `Simulation` `Adapter`
</td></tr>

</table>

---

## 4. Documentation convention: HLDocS

ProjectSansa and related repositories use [HLDocS (Human–LLM Document Roles Specification)](https://github.com/mayusaki3/HLDocS) as a common convention for creating, validating and reconstructing documentation. HLDocS assigns requirements, meaning and design decisions to humans, while LLMs help maintain document structures, identifiers and traceability.

It covers traceability between specifications, design and tests; structured Markdown; and `LLM_WORKSPACE` workflows (Request / Worklog / Handover) for long-running work. Adoption and directory naming can differ by repository; consult each repository's own conventions.

- **Stable operational line:** HLDocS `main` (v0.6.1 series)
- **Next-specification evaluation:** HLDocS `develop` (v0.7.0 reconstruction in progress; not yet complete or stable)
- [HLDocS repository and usage guidance](https://github.com/mayusaki3/HLDocS)

---
© 2020 ProjectSansa

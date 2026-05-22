[日本語](./README.md) | [English](./README_en-US.md)

# <img src="docs/ja-JP/ロゴマーク/Project%20Sansa%20logo%202023.png" width="36"> What is Project Sansa?

Project Sansa (hereinafter “Sansa”) began as an xR open platform based on the idea, “If something like this existed, I’d want to use it,” and has evolved into a vision for an xR system that spans VR, AR, MR, and non-xR domains.  
The project name “Sansa” was chosen to evoke the image of a three-way intersection where the “real world,” the “virtual world,” and “ideals” converge.  
Although it started as a personal project, anyone who finds it interesting is welcome to join.  
(Sorry, but we’re currently only Japanese speakers.)

[Discord](https://discord.gg/wN67tdzrCT)

---

## 1. What We Want to Achieve

This might sound like a tall order, but ultimately, we aim to create a new personal device and service platform to replace the smartphone.  
Mainly because I want to use it myself.

At Sansa, we treat VR, AR, MR, and non-XR as a single service.

- Non-XR: Calls and communication
- AR: Overlaying information onto the real world
- VR: Virtual space social networking
- MR: Fusion of the real and virtual worlds

Users can switch between these modes with simple controls, aiming to naturally bridge the real and virtual worlds.
Furthermore, we aim for an open architecture that allows Sansa to connect and integrate not only on its own but also with other VRSNS platforms and services.  
Furthermore, to support the growth of UGC (User-Generated Content), we intend to incorporate mechanisms related to rights management and the economic ecosystem.

---

## 2. Purpose of This Repository

This repository serves as an introduction to the overall vision of ProjectSansa and the Sansa series, as well as a hub for related resources.  
It also serves as a central repository for sharing common understandings regarding the Sansa series, particularly terminology.  
ProjectSansa provides common terminology, operational guidelines, and LLM integration policies for the Sansa series.  
We recommend that each Sansa series operate in accordance with these guidelines.

For details, please refer to the following documents.  
Ideas for use cases are also listed here.

- [ProjectSansa Table of Contents](./docs/en-US/index.md)

## 3. Introduction to Each Sansa Series

### 3.1 SansaSphere

SansaSphere handles the foundational aspects of Project Sansa, including user authentication, rights management, provenance information, verification, and the economic ecosystem.  
It also serves as a portal site.

- [SansaSphere Repository](https://github.com/mayusaki3/SansaSphere)

---

### 3.2 SansaVRM

VRM stands for Virtual Reality Models.
It is the content data format used by ProjectSansa.

Multiple models can be stored within a single format, and in addition to rights information, it can also include provenance data, terms of use, and information regarding compensation.  
Since it is merely a format, tampering cannot be prevented; however, when distributing content, registering it with SansaSphere is expected to enable tamper detection and compensation evaluation (such as usage status and what percentage of the total revenue is allocated) by querying SansaSphere.

- [SansaVRM Repository](https://github.com/mayusaki3/SansaVRM)

---

### 3.3 SansaVRM Studio AI

This is the development environment for creating SansaVRM content.  
In addition to basic editing functions, it enables AI-assisted creation.  
Regarding AI support, the plan is not for Project Sansa to provide AI services, but rather for each individual to build and use their own local AI.  
The philosophy is that creators should be able to create anything—whether it’s erotic or gory—at the time of creation, and that compliance with policies is determined only when the content is ready for distribution.

- [SansaVRM-Studio-AI Repository](https://github.com/mayusaki3/SansaVRM-Studio-AI)

### 3.4 SansaXR

This is a so-called XRSNS system where SansaVRM content is available.
Most of what Project Sansa aims to achieve is concentrated here.

- [SansaXR Repository](https://github.com/mayusaki3/SansaXR)

### 3.5 SansaCloth

A shader for SansaXR, primarily aimed at improving the rendering of clothing and similar items.  
It processes effects that would normally require mesh deformation directly within the shader.  
We plan to port this to various environments.

- [SansaCloth Repository](https://github.com/mayusaki3/SansaCloth)

### 3.6 SansaVRM MuJoCo Adapter

This is an adapter for converting data from the SansaVRM format into a format compatible with MuJoCo, an external physics simulator.  

- [SansaVRM-MuJoCo-Adapter Repository](https://github.com/mayusaki3/SansaVRM-MuJoCo-Adapter)

---
© 2020 ProjectSansa

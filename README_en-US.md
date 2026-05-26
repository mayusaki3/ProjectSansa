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

<table>

<tr><td>

### [SansaSphere](https://github.com/mayusaki3/SansaSphere)

It handles the foundational aspects of Project Sansa, including user authentication, rights management, provenance information, verification, and the economic ecosystem.  
It also functions as a portal site.

### Current Status
- Under design

`Portal Site` `Account` `Authentication` `Rights Management` `Provenance Information` `Verification` `Economic Ecosystem`
</td></tr>

<tr><td>

### [SansaVRM](https://github.com/mayusaki3/SansaVRM)

VRM stands for Virtual Reality Models.  
It is the content data format used by ProjectSansa, handling avatars, world objects, and more.
Multiple models can be stored within the format, and in addition to rights information, it can also include provenance information, terms of use, and information regarding compensation.  
Since it is merely a format, tampering cannot be prevented; however, when distributing content, registering it with SansaSphere is expected to enable tamper detection and compensation evaluation (such as usage status and what percentage of the total constitutes the share) by querying SansaSphere.

### Current Status
- Under development

`Format` `glTF` `Licensing Information` `Provenance Information` `Economic Ecosystem`
</td></tr>

<tr><td>

### [SansaVRM Studio AI](https://github.com/mayusaki3/SansaVRM-Studio-AI)

This is the development environment for SansaVRM content.  
In addition to basic editing functions, it enables AI-assisted creation.  
Regarding AI support, the plan is not for Project Sansa to provide AI services, but rather for individuals to build and use their own local AI.  
The philosophy is that creators should be able to create anything—whether it’s erotic, gory, or otherwise—at the time of creation, and that compliance with policies is determined only when the content is ready for distribution.

### Current Status
- Under design

`SansaVRM` `AI Generation` `Editing` `Conversion` `Assembly` `Licensing Information` `History Information` `Economic Ecosystem`
</td></tr>

<tr><td>

### [SansaXR](https://github.com/mayusaki3/SansaXR)

A so-called XRSNS system where SansaVRM content is available.
Most of what Project Sansa aims to achieve is concentrated here.

### Current Status
- Under design

`XRSNS` `OpenXR` `O3DE` `SansaVRM`
</td></tr>

<tr><td>

### [SansaCloth](https://github.com/mayusaki3/SansaCloth)

A shader for SansaXR, primarily aimed at improving the rendering of clothing and similar items.  
It processes effects that would normally require mesh deformation directly within the shader.  
We plan to port this to various platforms.

### Current Status
- Not yet started

`SansaVRM` `Shader` `PC` `Android` `iOS`
</td></tr>

<tr><td>

### [SansaVRM MuJoCo Adapter](https://github.com/mayusaki3/SansaVRM-MuJoCo-Adapter)

This is an adapter for converting from the SansaVRM format to a format compatible with MuJoCo, an external physics simulator.  

### Current Status
- Under design

`SansaVRM` `MuJoCo`
</td></tr>

</table>

---
© 2020 ProjectSansa

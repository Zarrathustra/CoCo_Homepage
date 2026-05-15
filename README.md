# CoCo Project Homepage

CoCo Project Homepage 是 CoCo 项目的官方网站首页，用于介绍 CoCo 作为 cryo-EM 工作流统一 GUI 的定位，并说明 CryoSieve、CryoPROS 和 CTFA 三个模块与 CoCo 的关系。

页面当前以静态前端为主，也提供 Spring Boot 可运行 JAR 的打包入口，便于部署为一个独立 Web 服务。

## 项目定位

CoCo 是面向结构生物学实验室和 cryo-EM 研究者的工作流 copilot。首页重点传达三点：

- CoCo 是统一 GUI 入口。
- CryoSieve、CryoPROS 和 CTFA 是 CoCo 内部包装的分析模块。
- 页面主要转化目标是 Download CoCo，而不是分别下载各模块。

## 模块概览

| 模块 | 状态 | 说明 |
| --- | --- | --- |
| CryoSieve | 已展示 | 单颗粒 cryo-EM 粒子排序与筛选模块，用于移除不必要粒子并提升重构质量。 |
| CryoPROS | 已展示 | 使用 AI 生成辅助粒子并进行 co-refinement 的 preferred-orientation correction 框架。 |
| CTFA | TBD | 规划中的模块，当前仅作为路线图占位展示。 |

## 页面结构

首页是一个零前端依赖的单页应用，所有交互逻辑位于 `index.html` 内部脚本中。

```text
index.html
  header                 固定导航栏
  main[data-view=home]   首页内容
  div[data-view=cryosieve]
  div[data-view=cryopros]
  div[data-view=ctfa]
  footer                 页脚与备案信息
```

主要功能：

- 首页与模块子页之间使用 hash 路由切换。
- 支持 `#cryosieve`、`#cryopros`、`#ctfa` 深链接。
- 支持浏览器前进、后退。
- 支持键盘访问模块卡片和 SVG 节点。
- 支持 reduced motion 用户偏好。

## 目录结构

```text
.
├── index.html
├── styles.css
├── coco_logo.png
├── img/
│   ├── cryosieve_featured_image.jpg
│   ├── cryosieve_main_figure.jpg
│   ├── cryopros_featured_image.png
│   ├── cryopros_main_figure.png
│   └── ga.jpg
├── pom.xml
└── src/
    └── main/
        ├── java/org/cocoproject/homepage/CocoHomepageApplication.java
        └── resources/application.properties
```

## 本地预览

### 方式一：直接打开静态页面

直接用浏览器打开：

```bash
open index.html
```

### 方式二：使用本地静态服务器

```bash
python3 -m http.server 8000
```

然后访问：

```text
http://localhost:8000
```

### 方式三：使用 Spring Boot 运行

需要 Java 17 和 Maven。

```bash
mvn spring-boot:run
```

默认访问：

```text
http://localhost:8080
```

## 打包为可运行 JAR

```bash
mvn clean package
```

运行打包产物：

```bash
java -jar target/coco-homepage-0.1.0.jar
```

`pom.xml` 会将以下静态资源打包到 Spring Boot 的 `static` 目录中：

- `index.html`
- `styles.css`
- `coco_logo.png`
- `img/**`

## 编辑说明

- 页面内容主要在 `index.html` 中维护。
- 样式集中在 `styles.css`，使用 OKLCH 色彩变量和 CSS cascade layers。
- 当前没有 npm、前端框架或构建工具。
- 下载按钮仍是占位状态，等待正式 CoCo installer 链接。
- CTFA 当前为 TBD，不应作为独立下载入口展示。

## 设计方向

当前视觉方向为 Academic Workbench：

- 温和、专业、偏学术的页面语气。
- CoCo 作为中心 GUI hub。
- CryoSieve、CryoPROS、CTFA 围绕 CoCo 作为模块能力展示。
- 使用柔和的矿物绿、蓝色和实验室纸张质感。

## 参考链接

- CryoSieve GitHub: https://github.com/mxhulab/cryosieve
- CryoPROS GitHub: https://github.com/mxhulab/cryopros
- CryoSieve Nature Communications paper: https://www.nature.com/articles/s41467-023-43555-x
- CryoPROS Nature Communications paper: https://doi.org/10.1038/s41467-025-59797-w

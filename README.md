# CoCo Project Homepage

CoCo Project Homepage 是 CoCo 项目的官方网站首页，用于介绍 CoCo 作为 cryo-EM 工作流统一 GUI 与 workflow copilot 的定位，并说明 CryoSieve、CryoPROS 和 CTFA 三个模块与 CoCo 的关系。

页面当前以静态前端为主，没有 npm、前端框架或前端构建流程；同时提供 Spring Boot 可运行 JAR 的打包入口，便于部署为独立 Web 服务。

## 项目定位

CoCo 面向结构生物学实验室、cryo-EM 研究者、研究生、PI 和科学软件用户。首页重点传达三点：

- CoCo 是统一 GUI 入口。
- CryoSieve、CryoPROS 和 CTFA 是 CoCo 内部包装的分析模块。
- 页面主要转化目标是 Download CoCo，而不是分别下载各模块。

当前发布版本为 `v0.1.0`，首页主按钮下载根目录静态资源 `coco.tar.gz`。完整安装说明以内嵌网页视图 `#install` 展示，不直接向用户暴露 raw `INSTALL.md`。

## 模块概览

| 模块 | 当前状态 | 页面内容 |
| --- | --- | --- |
| CryoSieve | 已展示，`v1.3.0` | 单颗粒 cryo-EM 粒子排序与筛选模块，用于移除不必要粒子并提升重构质量；包含 GitHub、论文、featured image、main figure 和 EMPIAR demo 表格。 |
| CryoPROS | 已展示，`v1.0.1` | 使用 AI 生成辅助粒子并进行 co-refinement 的 preferred-orientation correction 框架；包含 GitHub、论文和 featured image。 |
| CTFA | TBD | 规划中的模块，当前仅作为路线图占位展示；没有独立下载、GitHub 链接或公开 release 信息。 |

## 页面结构

首页是一个零前端依赖的单页应用，主要交互逻辑位于 `index.html` 内部脚本中。

```text
index.html
  header                    sticky navigation
  main[data-view=home]      homepage
  div[data-view=cryosieve]  CryoSieve sub-page
  div[data-view=cryopros]   CryoPROS sub-page
  div[data-view=ctfa]       CTFA sub-page
  div[data-view=install]    CoCo v0.1.0 installation guide
  footer                    legal, ICP, contact information
```

首页主要区块：

1. Hero：CoCo 定位、主 CTA、模块入口 CTA、事实列表。
2. Relationship panel：用步骤说明 CoCo GUI hub 与三个模块的关系。
3. CoCo GUI preview：展示 `North_Star` light/dark 两张界面图，并提供明暗模式切换按钮。
4. Modules：三行 ledger 式模块入口。
5. Download：唯一主下载目标，提供 `Download CoCo v0.1.0` 与安装指南入口。
6. References：CryoSieve 和 CryoPROS 的 GitHub 与论文引用。
7. Install guide：`#install` 视图，展示完整发布包安装、配置、运行和排错说明。
8. Footer：版权、备案、公安备案、电话、地址与邮箱。

主要交互：

- 首页、模块子页和安装指南之间使用 hash 路由切换。
- 支持 `#cryosieve`、`#cryopros`、`#ctfa`、`#install` 深链接。
- 安装指南内部使用 `#install-*` 锚点目录。
- 支持浏览器前进、后退。
- 导航栏 Modules 下拉菜单支持点击、hover/focus-within 和 Escape 关闭。
- CoCo GUI preview 支持 light/dark 图片切换，并同步 `aria-pressed` 与按钮标签。
- 模块 ledger 行支持鼠标点击与键盘 Enter/Space 激活。
- 从模块页点击首页锚点时，会先切回 home view，再滚动到目标区块。
- 支持 `prefers-reduced-motion: reduce`。

## 目录结构

```text
.
├── CLAUDE.md
├── DESIGN.md
├── PRODUCT.md
├── PROGRESS.md
├── README.md
├── index.html
├── styles.css
├── coco_logo.png
├── coco.tar.gz
├── img/
│   ├── North_Star_dark_mode.png
│   ├── North_Star_light_mode.png
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

安装指南视图：

```text
http://localhost:8000/#install
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

## 测试与打包

运行测试：

```bash
mvn test
```

打包为可运行 JAR：

```bash
mvn package
```

运行打包产物：

```bash
java -jar target/coco-homepage-0.1.0.jar
```

`pom.xml` 会将以下静态资源打包到 Spring Boot 的 `static` 目录中：

- `index.html`
- `styles.css`
- `coco_logo.png`
- `coco.tar.gz`
- `img/**`

## 发布包安装说明

用户在首页点击 `Installation guide` 会进入 `#install` 视图。该视图展示完整安装说明，包括：

- Java 8+、CryoSPARC、RELION 前置要求。
- `coco.tar.gz` 解包结构。
- `bin/coco-install-runtime.sh` 管理运行时安装。
- CoCo 后端启动方式和默认端口 `8078`。
- auth、Environment Check、SLURM lanes、内置模块验证、runtime workflow checks 和 troubleshooting。

安装说明是网页界面的一部分，不通过 raw `INSTALL.md` 对外发布。

## 编辑说明

- 页面内容主要在 `index.html` 中维护。
- 样式集中在 `styles.css`，使用 OKLCH 色彩变量和 CSS cascade layers：`reset`、`base`、`components`、`layout`、`responsive`。
- Spring Boot 只负责提供可运行 JAR 外壳；通常不需要修改 Java 代码。
- 根目录静态资源若需要打包进 JAR，必须同步更新 `pom.xml` 的 resources includes。放在 `img/` 下的资源会自动随 `img/**` 打包。
- `coco.tar.gz` 是 CoCo `v0.1.0` 的唯一主下载目标。
- CTFA 当前为 TBD，不应作为独立下载入口展示。

## 设计方向

当前视觉方向为 `DESIGN.md` 中的 Coconut Atlas / Academic Workbench：

- 温和、专业、偏学术的页面语气。
- Warm paper / research atlas 视觉风格。
- CoCo 作为中心 GUI hub。
- CryoSieve、CryoPROS、CTFA 围绕 CoCo 作为模块能力展示。
- 使用柔和的 coconut、leaf、slate、CTFA lavender 与 warm ink 色彩体系。
- 保持 Download CoCo 作为唯一主行动，不让模块链接、GitHub 或论文引用与它竞争。

## 可访问性

当前页面包含以下可访问性设计，后续修改应保留：

- Skip link。
- 语义化 landmark 和标题层级。
- 有意义图片的 alt 文本，装饰图片使用空 alt 或 `aria-hidden`。
- 键盘可访问的导航、下拉菜单、模块 ledger 行、GUI preview toggle 和安装指南目录。
- 安装命令使用可选择的 `<pre><code>` 文本块，并在小屏上横向滚动。
- 明显的 `:focus-visible` 焦点状态。
- reduced motion 支持。

## 参考链接

- CryoSieve GitHub: https://github.com/mxhulab/cryosieve
- CryoPROS GitHub: https://github.com/mxhulab/cryopros
- CryoSieve Nature Communications paper: https://www.nature.com/articles/s41467-023-43555-x
- CryoPROS Nature Communications paper: https://doi.org/10.1038/s41467-025-59797-w

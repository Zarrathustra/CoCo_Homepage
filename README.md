# CoCo Project Homepage

CoCo Project Homepage 是 CoCo 项目的官方网站首页，用于介绍 CoCo 作为 cryo-EM 数据准备、重构与分析的统一工作区，并重点说明 CryoSieve、CryoPROS 和 EModelG 在工作流中的作用。

页面前端没有 npm、框架或构建流程；生产环境由 Spring Boot 提供静态页面、外部发布包和长期统计服务。纯静态服务器仍可用于布局预览，但不会记录访问或下载统计。

## 项目定位

CoCo 面向结构生物学实验室、cryo-EM 研究者、研究生、PI 和科学软件用户。首页重点传达三点：

- CoCo 是统一 GUI 入口。
- CryoSieve、CryoPROS 和 EModelG 是重点介绍的分析模块；CoCo 还包含数据导入、粒子挑选与提取、分类、重构和 CryoSPARC 导出等任务。
- 页面主要转化目标是 `Download CoCo v0.3.5`，而不是分别下载各模块。

当前发布版本为 `v0.3.5`，首页主按钮通过 `/downloads/coco-0.3.5.tar.gz` 下载外部发布包。`#install-v0-3-5` 展示该版完整安装指南；`#changelog` 按 v0.3.5 brochure 展示完整更新日志。`#install` 保留为最新指南兼容入口，`#changelogs` 列出所有日志；v0.3.2、v0.2.0 的完整日志分别保留在 `#changelog-v0-3-2`、`#changelog-v0-2-0`。历史安装指南保留 v0.3.2、v0.2.0、v0.1.0，每个指南顶部均可切换版本。

## 模块概览

| 模块 | 当前状态 | 页面内容 |
| --- | --- | --- |
| CryoSieve | 已展示，`v1.3.0` | 单颗粒 cryo-EM 粒子排序与筛选模块，用于移除不必要粒子并提升重构质量；包含 GitHub、论文、featured image、main figure 和 EMPIAR demo 表格。 |
| CryoPROS | 已展示，`v1.0.1` | 使用 AI 生成辅助粒子并进行 co-refinement 的 preferred-orientation correction 框架；包含 GitHub、论文和 featured image。 |
| CTFA | 暂不公开 | 因 CoCo 界面中尚不可见，菜单、首页介绍和详情路由暂时隐藏；既有文案保留在惰性 template 中。 |
| EModelG | CoCo 内置模块 | 从 cryo-EM 密度自动构建糖链与蛋白主链模型；包含两条建模流程、三张科研实例图、指标评价条件、三种模式、使用步骤和 Science 论文引用，没有独立下载或单独版本号。 |

## 页面结构

首页是一个零前端依赖的单页应用，主要交互逻辑位于 `index.html` 内部脚本中。

```text
index.html
  header                    sticky navigation
  main[data-view=home]      homepage
  div[data-view=cryosieve]  CryoSieve sub-page
  div[data-view=cryopros]   CryoPROS sub-page
  template#deferred-ctfa    deferred CTFA content, outside active routing
  div[data-view=emodelg]    EModelG sub-page
  div[data-view=install-v0-3-5]  CoCo v0.3.5 installation guide
  div[data-view=install-v0-3-2]  archived v0.3.2 guide
  div[data-view=install-v0-2-0]  archived v0.2.0 guide
  div[data-view=changelog]  CoCo v0.3.5 change log
  div[data-view=changelog-v0-3-2]  archived v0.3.2 change log
  div[data-view=changelogs]  all change logs, newest first
  div[data-view=changelog-v0-2-0]  archived v0.2.0 change log
  div[data-view=install-v0-1-0]  archived v0.1.0 guide
  footer                    legal, ICP, contact information
```

首页主要区块：

1. Hero：CoCo 定位、主 CTA、模块入口 CTA、事实列表。
2. Relationship panel：用步骤说明 CoCo 工作区、任务依赖、重点模块和下游处理的关系。
3. CoCo GUI preview：展示 Light/Dark 与 Structure/DAG/2D/EModelG 的八种界面组合，默认 Light + Structure。初始化时预加载全部八张原图，默认图优先，其余七张低优先级预加载；主题和视图独立切换，始终使用单张图片展示，避免同时解码八张 5K 图片。
4. Modules：三行 ledger 式重点模块入口，不将其表述为 CoCo 的全部能力。
5. Download：唯一主下载目标，提供 `Download CoCo v0.3.5`、安装指南与轻量 Change Log 入口。
6. Publications：CryoSieve、CryoPROS 和 EModelG 的三篇主要论文，首页不再列出 Code 栏。模块详情页中的既有 GitHub 链接继续保留。
7. Change Log：`#changelog` 展示 v0.3.5 的新增任务、工作流复用、查看与配置改进，`#changelogs` 提供倒序版本目录。`#changelog-v0-3-2` 保留任务自动接续、批量操作、DFR／SGD 重构和独立 CryoSPARC 导出的完整内容及两张配图；`#changelog-v0-2-0` 保留原正文及两张配图。所有日志详情均链接当前下载及对应版本指南。
8. Install guides：v0.3.5 提供完整指南，v0.3.2、v0.2.0、v0.1.0 保留历史正文与版本章节锚点；所有指南顶部可切换版本。
9. Footer：版权、备案、公安备案、电话、地址与邮箱。

主要交互：

- 首页、模块子页、Change Log 和四个版本的安装页面之间使用 hash 路由切换。
- 支持三个公开模块、当前日志、日志目录、历史日志和四个安装版本的 hash 深链接。
- 各安装指南使用版本前缀的内部锚点，例如 `#install-v0-2-0-runtime`。旧 `#install` 与 `#install-*` 章节链接兼容映射到最新指南；最新指南没有对应章节时定位页首，同页操作也适用。未知版本不会被误判为最新指南。
- 支持浏览器前进、后退。
- Header 仅保留一个 `Download CoCo v0.3.5` 下载入口。手机导航保留两行，窄屏可换行，触控目标至少 48px。
- Modules 使用点击、触控或键盘明确展开，支持再次点击、点击外部、焦点移出和 Escape 关闭；隐藏菜单不会进入 Tab 顺序，Escape 返回触发按钮。
- Header 实际高度通过 `ResizeObserver` 同步到页面滚动偏移，主页与各版本安装章节均预留 16px 间距，适应换页与横竖屏变化。
- CoCo GUI preview 支持 Light/Dark 和 Structure/DAG/2D/EModelG 独立切换，同步各组按钮的 `aria-pressed` 状态与预览描述；小于 36rem 时视图按钮排列为 2×2。
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
├── img/
│   ├── Structure_Light.png
│   ├── Structure_Dark.png
│   ├── DAG_Light.png
│   ├── DAG_Dark.png
│   ├── 2D_Reconstruction_Light.png
│   ├── 2D_Reconstruction_Dark.png
│   ├── EModelG_Light.png
│   ├── EModelG_Dark.png
│   ├── coco-v0.2.0-job-graph.png
│   ├── coco-v0.2.0-user-management.png
│   ├── coco-v0.3.2-batch-operations.png
│   ├── coco-v0.3.2-deferred-job.png
│   ├── cryosieve_featured_image.jpg
│   ├── cryosieve_main_figure.jpg
│   ├── cryopros_featured_image.png
│   ├── cryopros_main_figure.png
│   ├── emodelg-glycan-example.png
│   ├── emodelg-protein-example.png
│   ├── emodelg-structural-examples.png
│   └── ga.jpg
├── pom.xml
└── src/
    └── main/
        ├── java/org/cocoproject/homepage/
        │   ├── CocoHomepageApplication.java
        │   ├── DownloadResourceConfiguration.java
        │   └── metrics/              SQLite persistence, counting, and APIs
        └── resources/application.properties
```

`coco-0.3.5.tar.gz` 是外部部署资源，不属于源码或 JAR 内容。默认从进程工作目录下的 `downloads/` 读取，也可以通过 `COCO_DOWNLOAD_DIR` 指定其他目录。统计数据库同样位于 JAR 之外，默认使用 `./data/coco-metrics.db`。

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
http://localhost:8000/#install-v0-3-5
http://localhost:8000/#install-v0-3-2
http://localhost:8000/#install-v0-2-0
```

Change Log 与归档指南：

```text
http://localhost:8000/#changelog
http://localhost:8000/#changelogs
http://localhost:8000/#changelog-v0-3-2
http://localhost:8000/#changelog-v0-2-0
http://localhost:8000/#install-v0-1-0
```

静态服务器不会提供 `/api/**` 统计接口。页面会静默隐藏下载量，适合视觉检查，但不能用于生产统计。

### 方式三：使用 Spring Boot 运行

需要 Java 17 和 Maven。

```bash
COCO_DOWNLOAD_DIR=/path/to/downloads \
COCO_METRICS_DB=/path/to/data/coco-metrics.db \
COCO_METRICS_ADMIN_TOKEN=replace-with-a-random-secret \
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

浏览器回归入口为 `.impeccable-artifacts/inspect-homepage.mjs`。它启动仅监听本机的静态服务，用 WebKit / Chromium 检查全部 12 个公开视图、菜单触控和键盘操作、八张预览图的提前加载与切换、图片加载失败后的交互、锚点与浏览器历史。覆盖 iPhone 15 Pro（竖屏、横屏、393×852 可用视口）、iPhone SE、Pro Max、iPad Mini、Galaxy S24、Pixel 7，以及 1280/1440px 桌面，共 15 个配置；这是设备仿真，不代表真机测试。

Playwright 可单独安装在临时目录，无需为网站增加 npm 构建流程。使用匹配版本的浏览器：

```bash
npm install --prefix /tmp/coco-browser-check playwright@1.58.0
node /tmp/coco-browser-check/node_modules/playwright/cli.js install chromium webkit
PLAYWRIGHT_MODULE=/tmp/coco-browser-check/node_modules/playwright \
  node .impeccable-artifacts/inspect-homepage.mjs
```

已有可被 Node 解析的 Playwright 时可直接运行 `node .impeccable-artifacts/inspect-homepage.mjs`。脚本默认在系统临时目录保存截图和 `results.json`，失败返回非零退出码；`COCO_TEST_OUTPUT_DIR` 可指定报告目录，`COCO_TEST_PROFILE=iphone-15-pro` 可单独重跑该配置。测试只提供网页资产，不访问发布包或生产统计数据。

打包为可运行 JAR：

```bash
mvn package
```

运行打包产物：

```bash
java -jar target/coco-homepage-0.3.5.jar
```

`pom.xml` 会将以下静态资源打包到 Spring Boot 的 `static` 目录中：

- `index.html`
- `styles.css`
- `coco_logo.png`
- `img/**`

CoCo 发布包和辅助资源包不会进入 JAR。Spring Boot 将 `/downloads/**` 映射到外部下载目录，并将 `/emodelg/**` 兼容映射到该目录的 `emodelg/` 子目录：

```text
/opt/coco-homepage/
├── coco-homepage-0.3.5.jar
├── data/
│   └── coco-metrics.db
└── downloads/
    ├── coco-0.2.0.tar.gz
    ├── coco-0.3.2.tar.gz
    ├── coco-0.3.5.tar.gz
    └── emodelg/
        ├── emodelg-so3-2.2.0-20261011.tar.gz
        └── emodelg-models-2.2.0-20261011.tar.gz
```

生产环境可以这样启动：

```bash
COCO_DOWNLOAD_DIR=/opt/coco-homepage/downloads \
COCO_METRICS_DB=/opt/coco-homepage/data/coco-metrics.db \
COCO_METRICS_TIME_ZONE=Asia/Shanghai \
COCO_METRICS_ADMIN_TOKEN=replace-with-a-random-secret \
java -jar /opt/coco-homepage/coco-homepage-0.3.5.jar
```

不设置 `COCO_DOWNLOAD_DIR` 时，默认目录是进程工作目录下的 `./downloads`。数据库默认写入 `./data/coco-metrics.db`；网页或 JAR 更新必须复用原数据库路径，才能延续历史统计。

页面及静态资源返回 `Cache-Control: no-cache`，让浏览器每次访问时向服务器校验缓存，未变化的文件仍可返回 `304`。反向代理应保留此响应头。`index.html` 的样式链接带有 `styles.css` SHA-256 的前 12 位作为 `v` 参数；修改样式时同步更新该值，以避开旧版本已有的浏览器缓存。首次从未设置缓存策略的旧版本升级后，如仍显示旧界面，需强制刷新一次。

### 辅助资源下载路由

CoCo 的 Environment Check 使用 `https://cocoproject.org.cn/emodelg/` 下载资源。服务器文件仍放在 `COCO_DOWNLOAD_DIR/emodelg/`，无需复制到第二个目录。每个文件同时支持 `/emodelg/<文件名>` 和 `/downloads/emodelg/<文件名>`，直接返回原始文件，并支持 `GET`、`HEAD` 和字节范围请求。这些资源不注册为 CoCo release，也不计入首页下载总量。

兼容路由由 Java 提供，因此上线时需替换网站 JAR 并重启应用，仅更新 HTML 不会加载该路由。反向代理应将 `/emodelg/` 和 `/downloads/` 请求交给该应用，或将两者映射到上述相同的实际文件。部署后检查两个 CoCo 使用的地址：

```bash
curl --fail --head https://cocoproject.org.cn/emodelg/emodelg-so3-2.2.0-20261011.tar.gz
curl --fail --head https://cocoproject.org.cn/emodelg/emodelg-models-2.2.0-20261011.tar.gz
```

应返回 `200`，对应 `Content-Length` 分别为 `395653505` 和 `1115143034`。网站已展示 EModelG 产品介绍；v0.3.5 安装指南包含资源下载与 Phenix 配置说明。上述资源路由与 CoCo 主下载分别维护。

## 访问与下载统计

- 下载量按 CoCo 版本分别累计，同时保留所有版本总计。v0.2.0 从统计功能上线时的 `0` 开始。
- 下载开始时计数；同一匿名访客对同一版本在 30 秒内的重复请求只计一次。`HEAD`、未知文件、缺失文件、辅助资源包和非零字节断点续传不计数。
- 访问统计只保存每个页面视图的每日匿名 UV，不保存 PV、原始 IP 或原始 Cookie。安装指南按版本视图分别计数，既有 `install` 历史数据继续保留。
- 首页以灰色小字公开所有 CoCo 版本的累计下载量。内部接口同时提供每日 UV、各版本每日/累计下载量和所有版本总计。
- 匿名访客使用一年有效的第一方 Cookie。SQLite 中只保存其 SHA-256，并定期清理用于去重的短期记录。

公共下载统计：

```bash
curl http://localhost:8080/api/stats/releases/v0.3.5
```

内部统计默认查询最近 30 天，单次查询最多 366 天：

```bash
curl \
  -H "Authorization: Bearer ${COCO_METRICS_ADMIN_TOKEN}" \
  "http://localhost:8080/api/internal/metrics?from=2026-07-01&to=2026-07-31"
```

未设置 `COCO_METRICS_ADMIN_TOKEN` 时，内部接口保持关闭。令牌只通过部署环境提供，不写入仓库。

生产环境只应通过 HTTPS 或受控内网访问内部统计接口。正式公开部署前，应在站点适用的隐私说明中披露 `coco_vid` 的匿名统计用途、一年有效期、哈希存储方式和短期去重数据保留周期。

### 数据备份与升级

- `data/` 已被 Git 忽略，SQLite 数据库不会进入源码或 Maven JAR。
- `downloads/` 同样被 Git 忽略，外部 CoCo 发布包不会被误提交。
- 每日使用 SQLite online backup 生成一致性快照，并检查备份完整性：

```bash
mkdir -p /opt/coco-homepage/backups
sqlite3 /opt/coco-homepage/data/coco-metrics.db \
  ".backup '/opt/coco-homepage/backups/coco-metrics-2026-07-29.db'"
sqlite3 /opt/coco-homepage/backups/coco-metrics-2026-07-29.db \
  "PRAGMA integrity_check;"
```

- 恢复时先停止应用，将当前数据库及可能存在的 `-wal`、`-shm` sidecar 移到保留目录，再复制已验证的备份并重启。不要在应用运行时直接覆盖数据库文件。

```bash
mkdir -p /opt/coco-homepage/backups/before-restore
mv /opt/coco-homepage/data/coco-metrics.db \
  /opt/coco-homepage/backups/before-restore/
cp /opt/coco-homepage/backups/coco-metrics-2026-07-29.db \
  /opt/coco-homepage/data/coco-metrics.db
COCO_DOWNLOAD_DIR=/opt/coco-homepage/downloads \
COCO_METRICS_DB=/opt/coco-homepage/data/coco-metrics.db \
COCO_METRICS_TIME_ZONE=Asia/Shanghai \
COCO_METRICS_ADMIN_TOKEN="${COCO_METRICS_ADMIN_TOKEN}" \
java -jar /opt/coco-homepage/coco-homepage-0.3.5.jar
```

若同目录仍有 `coco-metrics.db-wal` 或 `coco-metrics.db-shm`，也应在复制备份前移入 `before-restore/`。重启后通过公共接口和带令牌的内部接口核对版本总数、全版本总数和最近每日 UV。
- 更新首页或 JAR 前先备份数据库，部署后继续指向同一个 `COCO_METRICS_DB`。
- 新增 CoCo 版本时，在 `application.properties` 追加下一组 `coco.releases[n].version` 和 `coco.releases[n].file-name`，并保留所有旧映射；随后放置外部压缩包并更新页面链接。该过程不需要修改统计 schema。当前保留 v0.2.0、v0.3.2，并追加 v0.3.5：

```properties
coco.releases[0].version=v0.2.0
coco.releases[0].file-name=coco-0.2.0.tar.gz
coco.releases[1].version=v0.3.2
coco.releases[1].file-name=coco-0.3.2.tar.gz
coco.releases[2].version=v0.3.5
coco.releases[2].file-name=coco-0.3.5.tar.gz
```

## 发布包安装说明

用户在首页点击 `Installation guide` 会进入完整的 `#install-v0-3-5` 指南。指南沿用历史页的四步快速安装、侧边目录、版本切换和命令块样式，内容依据 CoCo GitHub 仓库的 v0.3.5 源码快照核对。当前更新日志依据 v0.3.5 brochure 提供完整功能说明。

- [v0.3.5 安装文档](https://github.com/mxhulab/CoCo/blob/11bab54eaaa8514589de9314d92be2ff5d607e6f/INSTALL.md)：固定提交 `11bab54eaaa8514589de9314d92be2ff5d607e6f`，该提交的 `backend/pom.xml` 版本为 0.3.5；不将其称为发布 tag。交叉核对 `PACK.md`、打包脚本、Settings 和 EModelG 源码。涵盖 Linux x86_64 / glibc 2.17+、Java 8+、主机 Python 3、版本运行时、认证、CryoSPARC/WIT、Local GPU、SLURM、LSF、EModelG 资源与 Phenix，以及持久状态备份升级。

v0.3.2、v0.2.0 历史指南分别依据 CoCo 对应 release tag 的 `INSTALL.md`，并核对打包与安装脚本、后端 Maven 配置、前端路由和 Settings 界面。维护时使用以下固定提交，不用 main 的新内容覆盖历史版本：

- [v0.3.2 安装文档](https://github.com/mxhulab/CoCo/blob/a38abf5353224f177a55fe0c9273c13d37efe54d/INSTALL.md)：Linux x86_64、Java 8+、主机 Python 3；GPU 工作流要求兼容 CUDA 12.1 的驱动。运行时包为 `coco-runtime-0.3.2.tar.gz`，环境目录为 `runtime/envs/coco-runtime-0.3.2`，配套 CoCoShed、CryoPROS 和 pySymStat。使用 `/login`、`/account/password`、`/projects` 和 `/api/v1/managed-runtimes/current`。CryoSPARC 可通过 4/5 安装路径或 5 远程 URL 接入；不要求 RELION 可执行程序。指南包含 SLURM、LSF 的部署条件与独立 Export to CryoSPARC 检查。
- [v0.2.0 安装文档](https://github.com/mxhulab/CoCo/blob/c833bb9fc88b3951000642e47814f0bc1050bf5b/INSTALL.md)：保留旧版未加版本号的 `coco-runtime.tar.gz` 和 `runtime/envs/coco-runtime`、CryoSPARC/RELION 配置、SLURM 单节点资源示例、`/login-verification`、`/change-password`、`/home` 及 `/api/open/settings/managed-runtime`。CryoSPARC 导出在 CryoPROS Generate 内启用。旧文档称发布包为 `coco.tar.gz`，网页命令采用本站下载名 `coco-0.2.0.tar.gz`；旧版 Conda 运行时包需要匹配目标系统和架构。

两版均使用 `bin/coco-install-runtime.sh`，默认端口为 `8078`。启动命令采用各 tag 中 `backend/pom.xml` 对应的 `coco-0.3.2.jar` 或 `coco-0.2.0.jar`。网页指南的核对不执行 CoCo 安装，也不检查或解包下载归档。

安装说明是网页界面的一部分，不通过 raw `INSTALL.md` 对外发布。v0.3.2、v0.2.0、v0.1.0 的指南分别保留在独立版本页面，并通过指南顶部的版本切换访问。各版本 Change Log 的安装按钮明确标注并链接对应指南，面板不再单列 v0.1.0 归档入口。

## 编辑说明

- 首页和 v0.3.5 安装指南展示 CryoSieve、CryoPROS 和 EModelG；CTFA 因产品界面暂不可见而隐藏。CryoSieve 使用内置 DFR 重构及后处理，CryoPROS 采用 Prep / Train / Generate 与独立导出任务。主下载为 v0.3.5，更新日志按 brochure 编写；历史安装说明及更新日志保持对应版本内容。
- EModelG 和 v0.3.5 更新日志以 `/Users/hmx/Git/Documents/Lab_Managing/CoCo_Project/v0.3.5/CoCo_v0.3.5_brochure.pdf` / `.tex` 为正文依据，英文重组内容并沿用网站样式。三张科研实例图来自 brochure 的 `assets/`；两个算法流程用可访问的 HTML 步骤表示。正式引用保留 Science DOI `10.1126/science.aef4958`。
- 功能范围依据同目录 `版本差异核对.md`：v0.3.2 标签提交 `a38abf5353224f177a55fe0c9273c13d37efe54d` 与 v0.3.5 beta 快照 `87e70ed7770eb7d8837a18f54700255f76c275ba`；不将后者称为发布 tag。新增六种可见任务。工作流保存仅涵盖符合条件且完成的颗粒导入、二维 DFR、Select 2D、Average Spectrum 流程；载入生成草稿，选类需重新审核。
- 研究指标保持评价范围：0.643 Å 平均全原子 RMSD 属于另一个无蛋白糖链案例，0.891 AUPRC 使用 4 Å 位点匹配阈值，PLL-1 的 2.87 Å 为实验密度图分辨率、0.87 TM-score 的比较对象为 AlphaFold 模型；不表示版本间性能提升。研究来源详见 brochure 的核对文件。
- 页面内容主要在 `index.html` 中维护。
- 样式集中在 `styles.css`，使用 OKLCH 色彩变量和 CSS cascade layers：`reset`、`base`、`components`、`layout`、`responsive`。
- Spring Boot 提供静态资源、外部下载映射、SQLite 长期统计和统计 API；统计行为变更需要同步修改 Java 与测试。
- 根目录静态资源若需要打包进 JAR，必须同步更新 `pom.xml` 的 resources includes。放在 `img/` 下的资源会自动随 `img/**` 打包。
- `coco-0.3.5.tar.gz` 是 CoCo `v0.3.5` 的唯一主下载目标，但应部署在 `COCO_DOWNLOAD_DIR` 指向的外部目录中，不应加入 Maven resources。
- `data/` 与发布压缩包都属于部署数据，不应提交或打包进 JAR。
- CTFA 暂不公开展示；`#ctfa` 不打开详情，历史 `ctfa` 访问统计仍保留。

## 设计方向

当前视觉方向为 `DESIGN.md` 中的 Coconut Atlas / Academic Workbench：

- 温和、专业、偏学术的页面语气。
- Warm paper / research atlas 视觉风格。
- CoCo 作为中心 GUI hub。
- CryoSieve、CryoPROS 和 EModelG 围绕 CoCo 作为模块能力展示。
- 使用柔和的 coconut、leaf、slate 与 warm ink 色彩体系；首页三个模块使用相同的中性配色，EModelG 详情页沿用 CryoPROS 的绿色，不使用独立紫色。
- 保持 `Download CoCo v0.3.5` 作为唯一主行动，不让模块链接、GitHub 或论文引用与它竞争。

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
- EModelG Science paper: https://doi.org/10.1126/science.aef4958
- EModelG research examples (CryoSeek preprint): https://doi.org/10.65215/bkvrt910

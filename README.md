# CoCo Project Homepage

CoCo Project Homepage 是 CoCo 项目的官方网站首页，用于介绍 CoCo 作为 cryo-EM 数据准备、重构与分析的统一工作区，并重点说明 CryoSieve、CryoPROS 和 CTFA 在工作流中的作用。

页面前端没有 npm、框架或构建流程；生产环境由 Spring Boot 提供静态页面、外部发布包和长期统计服务。纯静态服务器仍可用于布局预览，但不会记录访问或下载统计。

## 项目定位

CoCo 面向结构生物学实验室、cryo-EM 研究者、研究生、PI 和科学软件用户。首页重点传达三点：

- CoCo 是统一 GUI 入口。
- CryoSieve、CryoPROS 和 CTFA 是重点介绍的分析模块；CoCo 还包含数据导入、粒子挑选与提取、分类、重构和 CryoSPARC 导出等任务。
- 页面主要转化目标是 `Download CoCo v0.3.2`，而不是分别下载各模块。

当前发布版本为 `v0.3.2`，首页主按钮通过 `/downloads/coco-0.3.2.tar.gz` 下载外部发布包。当前安装说明以内嵌网页视图 `#install-v0-3-2` 展示，`#install` 保留为兼容入口，`#changelog` 展示 v0.3.2 更新日志，`#changelogs` 列出所有日志，`#changelog-v0-2-0` 保留完整 v0.2.0 日志；旧版安装指南分别保留为 `#install-v0-2-0` 和 `#install-v0-1-0`，每个指南顶部均可切换版本。

## 模块概览

| 模块 | 当前状态 | 页面内容 |
| --- | --- | --- |
| CryoSieve | 已展示，`v1.3.0` | 单颗粒 cryo-EM 粒子排序与筛选模块，用于移除不必要粒子并提升重构质量；包含 GitHub、论文、featured image、main figure 和 EMPIAR demo 表格。 |
| CryoPROS | 已展示，`v1.0.1` | 使用 AI 生成辅助粒子并进行 co-refinement 的 preferred-orientation correction 框架；包含 GitHub、论文和 featured image。 |
| CTFA | CoCo 内置模块 | 微图 CTF 估计、拟合参数与 Thon-ring 检查，输出可供后续粒子挑选与提取使用；没有独立下载、GitHub 链接或单独版本号。 |

## 页面结构

首页是一个零前端依赖的单页应用，主要交互逻辑位于 `index.html` 内部脚本中。

```text
index.html
  header                    sticky navigation
  main[data-view=home]      homepage
  div[data-view=cryosieve]  CryoSieve sub-page
  div[data-view=cryopros]   CryoPROS sub-page
  div[data-view=ctfa]       CTFA sub-page
  div[data-view=install-v0-3-2]  CoCo v0.3.2 installation guide
  div[data-view=install-v0-2-0]  archived v0.2.0 guide
  div[data-view=changelog]  CoCo v0.3.2 change log
  div[data-view=changelogs]  all change logs, newest first
  div[data-view=changelog-v0-2-0]  archived v0.2.0 change log
  div[data-view=install-v0-1-0]  archived v0.1.0 guide
  footer                    legal, ICP, contact information
```

首页主要区块：

1. Hero：CoCo 定位、主 CTA、模块入口 CTA、事实列表。
2. Relationship panel：用步骤说明 CoCo 工作区、任务依赖、重点模块和下游处理的关系。
3. CoCo GUI preview：展示 `North_Star` 的 Light/Dark 与 Cards/DAG 四种界面组合，并提供主题和布局两组切换控件。
4. Modules：三行 ledger 式重点模块入口，不将其表述为 CoCo 的全部能力。
5. Download：唯一主下载目标，提供 `Download CoCo v0.3.2`、安装指南与轻量 Change Log 入口。
6. References：CryoSieve 和 CryoPROS 的 GitHub 与论文引用。
7. Change Log：`#changelog` 展示 v0.3.2 的任务自动接续、批量操作、DFR／SGD 重构入口和 CryoSPARC 独立导出任务，Workflow continuity 区块配有 `coco-v0.3.2-deferred-job.png`，批量操作区使用发布文档图 1，不使用文档原图 2。`#changelogs` 提供版本目录，`#changelog-v0-2-0` 保留旧版正文及两张配图；两个详情页的 Ready to update 面板均提供 All change logs 入口。
8. Install guides：v0.3.2、v0.2.0、v0.1.0 各有独立页面及章节锚点，顶部可切换版本；各版本 Change Log 指向对应指南。
9. Footer：版权、备案、公安备案、电话、地址与邮箱。

主要交互：

- 首页、模块子页、Change Log 和三个安装指南之间使用 hash 路由切换。
- 支持 `#cryosieve`、`#cryopros`、`#ctfa`、`#changelog`、`#changelogs`、`#changelog-v0-2-0`、`#install-v0-3-2`、`#install-v0-2-0` 和 `#install-v0-1-0` 深链接。
- 各安装指南使用版本前缀的内部锚点，例如 `#install-v0-2-0-runtime`。旧 `#install` 与 `#install-*` 章节链接兼容映射到最新指南；未知版本不会被误判为最新指南。
- 支持浏览器前进、后退。
- 导航栏 Modules 下拉菜单支持点击、hover/focus-within 和 Escape 关闭。
- CoCo GUI preview 支持 Light/Dark 和 Cards/DAG 独立切换，并同步各组按钮的 `aria-pressed` 状态。
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
│   ├── North_Star_dark_mode.png
│   ├── North_Star_dark_mode_DAG.png
│   ├── North_Star_light_mode.png
│   ├── North_Star_light_mode_DAG.png
│   ├── coco-v0.2.0-job-graph.png
│   ├── coco-v0.2.0-user-management.png
│   ├── coco-v0.3.2-batch-operations.png
│   ├── coco-v0.3.2-deferred-job.png
│   ├── cryosieve_featured_image.jpg
│   ├── cryosieve_main_figure.jpg
│   ├── cryopros_featured_image.png
│   ├── cryopros_main_figure.png
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

`coco-0.3.2.tar.gz` 是外部部署资源，不属于源码或 JAR 内容。默认从进程工作目录下的 `downloads/` 读取，也可以通过 `COCO_DOWNLOAD_DIR` 指定其他目录。统计数据库同样位于 JAR 之外，默认使用 `./data/coco-metrics.db`。

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
http://localhost:8000/#install-v0-3-2
http://localhost:8000/#install-v0-2-0
```

Change Log 与归档指南：

```text
http://localhost:8000/#changelog
http://localhost:8000/#changelogs
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

打包为可运行 JAR：

```bash
mvn package
```

运行打包产物：

```bash
java -jar target/coco-homepage-0.3.2.jar
```

`pom.xml` 会将以下静态资源打包到 Spring Boot 的 `static` 目录中：

- `index.html`
- `styles.css`
- `coco_logo.png`
- `img/**`

CoCo 发布包和辅助资源包不会进入 JAR。Spring Boot 将 `/downloads/**` 映射到外部下载目录，并将 `/emodelg/**` 兼容映射到该目录的 `emodelg/` 子目录：

```text
/opt/coco-homepage/
├── coco-homepage-0.3.2.jar
├── data/
│   └── coco-metrics.db
└── downloads/
    ├── coco-0.3.2.tar.gz
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
java -jar /opt/coco-homepage/coco-homepage-0.3.2.jar
```

不设置 `COCO_DOWNLOAD_DIR` 时，默认目录是进程工作目录下的 `./downloads`。数据库默认写入 `./data/coco-metrics.db`；网页或 JAR 更新必须复用原数据库路径，才能延续历史统计。

### 辅助资源下载路由

CoCo 的 Environment Check 使用 `https://cocoproject.org.cn/emodelg/` 下载资源。服务器文件仍放在 `COCO_DOWNLOAD_DIR/emodelg/`，无需复制到第二个目录。每个文件同时支持 `/emodelg/<文件名>` 和 `/downloads/emodelg/<文件名>`，直接返回原始文件，并支持 `GET`、`HEAD` 和字节范围请求。这些资源不注册为 CoCo release，也不计入首页下载总量。

兼容路由由 Java 提供，因此上线时需替换网站 JAR 并重启应用，仅更新 HTML 不会加载该路由。反向代理应将 `/emodelg/` 和 `/downloads/` 请求交给该应用，或将两者映射到上述相同的实际文件。部署后检查两个 CoCo 使用的地址：

```bash
curl --fail --head https://cocoproject.org.cn/emodelg/emodelg-so3-2.2.0-20261011.tar.gz
curl --fail --head https://cocoproject.org.cn/emodelg/emodelg-models-2.2.0-20261011.tar.gz
```

应返回 `200`，对应 `Content-Length` 分别为 `395653505` 和 `1115143034`。网站暂不展示 EModelG 产品介绍或环境配置指南；上述内容仅用于维护服务器下载路由。

## 访问与下载统计

- 下载量按 CoCo 版本分别累计，同时保留所有版本总计。v0.2.0 从统计功能上线时的 `0` 开始。
- 下载开始时计数；同一匿名访客对同一版本在 30 秒内的重复请求只计一次。`HEAD`、未知文件、缺失文件、辅助资源包和非零字节断点续传不计数。
- 访问统计只保存每个页面视图的每日匿名 UV，不保存 PV、原始 IP 或原始 Cookie。安装指南按版本视图分别计数，既有 `install` 历史数据继续保留。
- 首页以灰色小字公开所有 CoCo 版本的累计下载量。内部接口同时提供每日 UV、各版本每日/累计下载量和所有版本总计。
- 匿名访客使用一年有效的第一方 Cookie。SQLite 中只保存其 SHA-256，并定期清理用于去重的短期记录。

公共下载统计：

```bash
curl http://localhost:8080/api/stats/releases/v0.3.2
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
java -jar /opt/coco-homepage/coco-homepage-0.3.2.jar
```

若同目录仍有 `coco-metrics.db-wal` 或 `coco-metrics.db-shm`，也应在复制备份前移入 `before-restore/`。重启后通过公共接口和带令牌的内部接口核对版本总数、全版本总数和最近每日 UV。
- 更新首页或 JAR 前先备份数据库，部署后继续指向同一个 `COCO_METRICS_DB`。
- 新增 CoCo 版本时，在 `application.properties` 追加下一组 `coco.releases[n].version` 和 `coco.releases[n].file-name`，并保留所有旧映射；随后放置外部压缩包并更新页面链接。该过程不需要修改统计 schema。当前保留 v0.2.0，并追加 v0.3.2：

```properties
coco.releases[0].version=v0.2.0
coco.releases[0].file-name=coco-0.2.0.tar.gz
coco.releases[1].version=v0.3.2
coco.releases[1].file-name=coco-0.3.2.tar.gz
```

## 发布包安装说明

用户在首页点击 `Installation guide` 会进入当前 `#install-v0-3-2` 视图。两版指南分别依据 CoCo 对应 release tag 的 `INSTALL.md`，并核对打包与安装脚本、后端 Maven 配置、前端路由和 Settings 界面。维护时使用以下固定提交，不用 main 的新内容覆盖历史版本：

- [v0.3.2 安装文档](https://github.com/mxhulab/CoCo/blob/a38abf5353224f177a55fe0c9273c13d37efe54d/INSTALL.md)：Linux x86_64、Java 8+、主机 Python 3；GPU 工作流要求兼容 CUDA 12.1 的驱动。运行时包为 `coco-runtime-0.3.2.tar.gz`，环境目录为 `runtime/envs/coco-runtime-0.3.2`，配套 CoCoShed、CryoPROS 和 pySymStat。使用 `/login`、`/account/password`、`/projects` 和 `/api/v1/managed-runtimes/current`。CryoSPARC 可通过 4/5 安装路径或 5 远程 URL 接入；不要求 RELION 可执行程序。指南包含 SLURM、LSF 的部署条件与独立 Export to CryoSPARC 检查。
- [v0.2.0 安装文档](https://github.com/mxhulab/CoCo/blob/c833bb9fc88b3951000642e47814f0bc1050bf5b/INSTALL.md)：保留旧版未加版本号的 `coco-runtime.tar.gz` 和 `runtime/envs/coco-runtime`、CryoSPARC/RELION 配置、SLURM 单节点资源示例、`/login-verification`、`/change-password`、`/home` 及 `/api/open/settings/managed-runtime`。CryoSPARC 导出在 CryoPROS Generate 内启用。旧文档称发布包为 `coco.tar.gz`，网页命令采用本站下载名 `coco-0.2.0.tar.gz`；旧版 Conda 运行时包需要匹配目标系统和架构。

两版均使用 `bin/coco-install-runtime.sh`，默认端口为 `8078`。启动命令采用各 tag 中 `backend/pom.xml` 对应的 `coco-0.3.2.jar` 或 `coco-0.2.0.jar`。网页指南的核对不执行 CoCo 安装，也不检查或解包下载归档。

安装说明是网页界面的一部分，不通过 raw `INSTALL.md` 对外发布。v0.2.0、v0.1.0 的指南分别保留在独立版本页面，并通过指南顶部的版本切换访问。各版本 Change Log 的安装按钮明确标注并链接对应指南，面板不再单列 v0.1.0 归档入口。

## 编辑说明

- 首页与模块介绍已对照 CoCo v0.3.2 的 `backend/src/main/resources/jobTypeMapping.json`、`INSTALL.md` 和当前源码核对：CTFA 已提供 CTF 估计与结果检查，CryoSieve 使用内置 DFR 重构及后处理，CryoPROS 采用 Prep / Train / Generate 与独立导出任务。主下载仍为 v0.3.2，历史安装说明及更新日志保持对应版本内容。
- 页面内容主要在 `index.html` 中维护。
- 样式集中在 `styles.css`，使用 OKLCH 色彩变量和 CSS cascade layers：`reset`、`base`、`components`、`layout`、`responsive`。
- Spring Boot 提供静态资源、外部下载映射、SQLite 长期统计和统计 API；统计行为变更需要同步修改 Java 与测试。
- 根目录静态资源若需要打包进 JAR，必须同步更新 `pom.xml` 的 resources includes。放在 `img/` 下的资源会自动随 `img/**` 打包。
- `coco-0.3.2.tar.gz` 是 CoCo `v0.3.2` 的唯一主下载目标，但应部署在 `COCO_DOWNLOAD_DIR` 指向的外部目录中，不应加入 Maven resources。
- `data/` 与发布压缩包都属于部署数据，不应提交或打包进 JAR。
- CTFA 作为 CoCo 内置分析模块展示；不提供独立下载、未经确认的 GitHub 链接或单独版本号。

## 设计方向

当前视觉方向为 `DESIGN.md` 中的 Coconut Atlas / Academic Workbench：

- 温和、专业、偏学术的页面语气。
- Warm paper / research atlas 视觉风格。
- CoCo 作为中心 GUI hub。
- CryoSieve、CryoPROS、CTFA 围绕 CoCo 作为模块能力展示。
- 使用柔和的 coconut、leaf、slate 与 warm ink 色彩体系；首页三个模块使用相同的中性配色，CTFA 详情页沿用 CryoPROS 的绿色，不使用独立紫色。
- 保持 `Download CoCo v0.3.2` 作为唯一主行动，不让模块链接、GitHub 或论文引用与它竞争。

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

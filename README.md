# CoCo Project Homepage

CoCo Project Homepage 是 CoCo 项目的官方网站首页，用于介绍 CoCo 作为 cryo-EM 工作流统一 GUI 与 workflow copilot 的定位，并说明 CryoSieve、CryoPROS 和 CTFA 三个模块与 CoCo 的关系。

页面前端没有 npm、框架或构建流程；生产环境由 Spring Boot 提供静态页面、外部发布包和长期统计服务。纯静态服务器仍可用于布局预览，但不会记录访问或下载统计。

## 项目定位

CoCo 面向结构生物学实验室、cryo-EM 研究者、研究生、PI 和科学软件用户。首页重点传达三点：

- CoCo 是统一 GUI 入口。
- CryoSieve、CryoPROS 和 CTFA 是 CoCo 内部包装的分析模块。
- 页面主要转化目标是 `Download CoCo v0.2.0`，而不是分别下载各模块。

当前发布版本为 `v0.2.0`，首页主按钮通过 `/downloads/coco-0.2.6.tar.gz` 下载外部发布包。当前安装说明以内嵌网页视图 `#install` 展示，`#changelog` 说明相对 v0.1.0 的主要变化；旧版指南保留为 `#install-v0-1-0` 归档视图。

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
  div[data-view=install]    CoCo v0.2.0 installation guide
  div[data-view=changelog]  CoCo v0.2.0 change log
  div[data-view=install-v0-1-0]  archived v0.1.0 guide
  footer                    legal, ICP, contact information
```

首页主要区块：

1. Hero：CoCo 定位、主 CTA、模块入口 CTA、事实列表。
2. Relationship panel：用步骤说明 CoCo GUI hub 与三个模块的关系。
3. CoCo GUI preview：展示 `North_Star` 的 Light/Dark 与 Cards/DAG 四种界面组合，并提供主题和布局两组切换控件。
4. Modules：三行 ledger 式模块入口。
5. Download：唯一主下载目标，提供 `Download CoCo v0.2.0`、安装指南与轻量 Change Log 入口。
6. References：CryoSieve 和 CryoPROS 的 GitHub 与论文引用。
7. Change Log：`#changelog` 视图，展示 v0.2.0 的 data job graph、多用户管理和其他运行体验升级。
8. Install guides：`#install` 展示当前指南，`#install-v0-1-0` 保留历史指南。
9. Footer：版权、备案、公安备案、电话、地址与邮箱。

主要交互：

- 首页、模块子页、Change Log 和两个安装指南之间使用 hash 路由切换。
- 支持 `#cryosieve`、`#cryopros`、`#ctfa`、`#changelog`、`#install` 和 `#install-v0-1-0` 深链接。
- 当前安装指南使用 `#install-*` 内部锚点，归档指南使用 `#install-v0-1-0-*`。
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

`coco-0.2.6.tar.gz` 是外部部署资源，不属于源码或 JAR 内容。默认从进程工作目录下的 `downloads/` 读取，也可以通过 `COCO_DOWNLOAD_DIR` 指定其他目录。统计数据库同样位于 JAR 之外，默认使用 `./data/coco-metrics.db`。

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

Change Log 与归档指南：

```text
http://localhost:8000/#changelog
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
java -jar target/coco-homepage-0.2.0.jar
```

`pom.xml` 会将以下静态资源打包到 Spring Boot 的 `static` 目录中：

- `index.html`
- `styles.css`
- `coco_logo.png`
- `img/**`

CoCo 发布包不会进入 JAR。Spring Boot 将 `/downloads/**` 映射到外部下载目录：

```text
/opt/coco-homepage/
├── coco-homepage-0.2.0.jar
├── data/
│   └── coco-metrics.db
└── downloads/
    └── coco-0.2.6.tar.gz
```

生产环境可以这样启动：

```bash
COCO_DOWNLOAD_DIR=/opt/coco-homepage/downloads \
COCO_METRICS_DB=/opt/coco-homepage/data/coco-metrics.db \
COCO_METRICS_TIME_ZONE=Asia/Shanghai \
COCO_METRICS_ADMIN_TOKEN=replace-with-a-random-secret \
java -jar /opt/coco-homepage/coco-homepage-0.2.0.jar
```

不设置 `COCO_DOWNLOAD_DIR` 时，默认目录是进程工作目录下的 `./downloads`。数据库默认写入 `./data/coco-metrics.db`；网页或 JAR 更新必须复用原数据库路径，才能延续历史统计。

## 访问与下载统计

- 下载量按 CoCo 版本分别累计，同时保留所有版本总计。v0.2.0 从统计功能上线时的 `0` 开始。
- 下载开始时计数；同一匿名访客对同一版本在 30 秒内的重复请求只计一次。`HEAD`、未知文件、缺失文件和非零字节断点续传不计数。
- 访问统计只保存每个页面视图的每日匿名 UV，不保存 PV、原始 IP 或原始 Cookie。
- 首页以灰色小字公开所有 CoCo 版本的累计下载量。内部接口同时提供每日 UV、各版本每日/累计下载量和所有版本总计。
- 匿名访客使用一年有效的第一方 Cookie。SQLite 中只保存其 SHA-256，并定期清理用于去重的短期记录。

公共下载统计：

```bash
curl http://localhost:8080/api/stats/releases/v0.2.0
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
java -jar /opt/coco-homepage/coco-homepage-0.2.0.jar
```

若同目录仍有 `coco-metrics.db-wal` 或 `coco-metrics.db-shm`，也应在复制备份前移入 `before-restore/`。重启后通过公共接口和带令牌的内部接口核对版本总数、全版本总数和最近每日 UV。
- 更新首页或 JAR 前先备份数据库，部署后继续指向同一个 `COCO_METRICS_DB`。
- 新增 CoCo 版本时，在 `application.properties` 追加下一组 `coco.releases[n].version` 和 `coco.releases[n].file-name`，并保留所有旧映射；随后放置外部压缩包并更新页面链接。该过程不需要修改统计 schema。例如：

```properties
coco.releases[1].version=v0.3.0
coco.releases[1].file-name=coco-0.3.0.tar.gz
```

## 发布包安装说明

用户在首页点击 `Installation guide` 会进入当前 `#install` 视图。该视图根据 CoCo 仓库当前 `INSTALL.md` 整理，展示：

- Java 8+、CryoSPARC、RELION 前置要求。
- `coco-0.2.6.tar.gz` 解包结构。
- `bin/coco-install-runtime.sh` 管理运行时安装。
- CoCo 后端启动方式和默认端口 `8078`。
- auth、Environment Check、SLURM lanes、内置模块验证、runtime workflow checks 和 troubleshooting。

安装说明是网页界面的一部分，不通过 raw `INSTALL.md` 对外发布。旧 v0.1.0 指南保留在 `#install-v0-1-0`，仅从 Change Log 进入，不再提供旧版下载入口。

## 编辑说明

- 页面内容主要在 `index.html` 中维护。
- 样式集中在 `styles.css`，使用 OKLCH 色彩变量和 CSS cascade layers：`reset`、`base`、`components`、`layout`、`responsive`。
- Spring Boot 提供静态资源、外部下载映射、SQLite 长期统计和统计 API；统计行为变更需要同步修改 Java 与测试。
- 根目录静态资源若需要打包进 JAR，必须同步更新 `pom.xml` 的 resources includes。放在 `img/` 下的资源会自动随 `img/**` 打包。
- `coco-0.2.6.tar.gz` 是 CoCo `v0.2.0` 的唯一主下载目标，但应部署在 `COCO_DOWNLOAD_DIR` 指向的外部目录中，不应加入 Maven resources。
- `data/` 与发布压缩包都属于部署数据，不应提交或打包进 JAR。
- CTFA 当前为 TBD，不应作为独立下载入口展示。

## 设计方向

当前视觉方向为 `DESIGN.md` 中的 Coconut Atlas / Academic Workbench：

- 温和、专业、偏学术的页面语气。
- Warm paper / research atlas 视觉风格。
- CoCo 作为中心 GUI hub。
- CryoSieve、CryoPROS、CTFA 围绕 CoCo 作为模块能力展示。
- 使用柔和的 coconut、leaf、slate、CTFA lavender 与 warm ink 色彩体系。
- 保持 `Download CoCo v0.2.0` 作为唯一主行动，不让模块链接、GitHub 或论文引用与它竞争。

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

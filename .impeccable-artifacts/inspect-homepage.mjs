import assert from 'node:assert/strict';
import { createReadStream } from 'node:fs';
import { mkdir, mkdtemp, stat, writeFile } from 'node:fs/promises';
import { createServer } from 'node:http';
import { createRequire } from 'node:module';
import { tmpdir } from 'node:os';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const require = createRequire(import.meta.url);
const { chromium, webkit, devices } = require(process.env.PLAYWRIGHT_MODULE || 'playwright');
const root = path.dirname(path.dirname(fileURLToPath(import.meta.url)));
const output = process.env.COCO_TEST_OUTPUT_DIR || await mkdtemp(path.join(tmpdir(), 'coco-browser-check-'));
await mkdir(output, { recursive: true });
const views = ['home', 'cryosieve', 'cryopros', 'emodelg', 'changelog', 'changelogs',
  'changelog-v0-3-2', 'changelog-v0-2-0', 'install-v0-3-5', 'install-v0-3-2', 'install-v0-2-0', 'install-v0-1-0'];
const previews = {
  structure: ['Structure', 'Structure view'], dag: ['DAG', 'DAG workflow'],
  '2d': ['2D_Reconstruction', '2D reconstruction'], emodelg: ['EModelG', 'EModelG modeling'],
};
const previewFiles = Object.values(previews).flatMap(([stem]) => [`${stem}_Light.png`, `${stem}_Dark.png`]);
const profiles = [
  { name: 'iphone-15-pro', engine: 'webkit', ...devices['iPhone 15 Pro'] },
  { name: 'iphone-15-pro-full-height', engine: 'webkit', ...devices['iPhone 15 Pro'], viewport: { width: 393, height: 852 } },
  { name: 'iphone-15-pro-landscape', engine: 'webkit', ...devices['iPhone 15 Pro landscape'] },
  { name: 'iphone-se-3', engine: 'webkit', ...devices['iPhone SE (3rd gen)'] },
  { name: 'iphone-se-320', engine: 'webkit', ...devices['iPhone SE'] },
  { name: 'iphone-15-pro-max', engine: 'webkit', ...devices['iPhone 15 Pro Max'] },
  { name: 'ipad-mini', engine: 'webkit', ...devices['iPad Mini'] },
  { name: 'galaxy-s24', engine: 'chromium', ...devices['Galaxy S24'] },
  { name: 'galaxy-s24-landscape', engine: 'chromium', ...devices['Galaxy S24 landscape'] },
  { name: 'pixel-7', engine: 'chromium', ...devices['Pixel 7'] },
  { name: 'pixel-7-landscape', engine: 'chromium', ...devices['Pixel 7 landscape'] },
  ...['webkit', 'chromium'].flatMap(engine => [1280, 1440].map(width => ({
    name: `${engine}-desktop-${width}`, engine, viewport: { width, height: 1000 },
  }))),
];
const selected = profiles.filter(p => !process.env.COCO_TEST_PROFILE || p.name === process.env.COCO_TEST_PROFILE);
assert(selected.length, 'COCO_TEST_PROFILE must name one of the configured device profiles');

// Serve only site assets, with the production cache policy. Never serve release archives or metrics data.
const server = createServer(async (req, res) => {
  try {
    let pathname = decodeURIComponent(new URL(req.url, 'http://localhost').pathname);
    if (pathname === '/') pathname = '/index.html';
    if (!/^\/(index\.html|styles\.css|coco_logo\.png|img\/[^/]+)$/.test(pathname)) {
      res.writeHead(404).end();
      return;
    }
    const filename = path.join(root, pathname);
    const info = await stat(filename);
    if (!info.isFile()) { res.writeHead(404).end(); return; }
    const etag = `"${info.size}-${Math.floor(info.mtimeMs)}"`;
    res.setHeader('Cache-Control', 'no-cache');
    res.setHeader('ETag', etag);
    if (req.headers['if-none-match'] === etag) { res.writeHead(304).end(); return; }
    const types = { '.html': 'text/html', '.css': 'text/css', '.png': 'image/png', '.jpg': 'image/jpeg', '.svg': 'image/svg+xml' };
    res.setHeader('Content-Type', types[path.extname(filename)] || 'application/octet-stream');
    res.setHeader('Content-Length', info.size);
    if (req.method === 'HEAD') res.end();
    else createReadStream(filename).on('error', () => res.destroy()).pipe(res);
  } catch { res.writeHead(404).end(); }
});
await new Promise(resolve => server.listen(0, '127.0.0.1', resolve));
const baseUrl = `http://127.0.0.1:${server.address().port}/`;
const results = [];
const browsers = new Map();
const touchSessions = new WeakMap();

async function settle(page) {
  await page.evaluate(async () => {
    await document.fonts.ready;
    await new Promise(resolve => requestAnimationFrame(() => requestAnimationFrame(resolve)));
  });
}

async function captureScreenshot(page, options) {
  const hadTouch = await page.evaluate(() => navigator.maxTouchPoints > 0);
  await page.screenshot(options);
  // Chromium 145 on macOS can reset touch emulation after a full-page capture.
  // Restore the original input configuration before testing the next view.
  if (hadTouch && page.context().browser().browserType().name() === 'chromium'
      && !await page.evaluate(() => navigator.maxTouchPoints > 0)) {
    let session = touchSessions.get(page);
    if (!session) {
      session = await page.context().newCDPSession(page);
      touchSessions.set(page, session);
    }
    await session.send('Emulation.setTouchEmulationEnabled', { enabled: true, maxTouchPoints: 1 });
    await settle(page);
  }
}

async function activeView(page, view) {
  await page.waitForFunction(view => {
    const el = document.querySelector(`[data-view="${view}"]`);
    return el && !el.hidden && !el.classList.contains('view-enter');
  }, view);
  await settle(page);
}

async function navigate(page, hash = '') {
  await page.goto(baseUrl + (hash ? '#' + hash : ''), { waitUntil: 'load' });
  await settle(page);
}

async function activate(page, selector, touch) {
  const locator = typeof selector === 'string' ? page.locator(selector) : selector;
  if (touch) await locator.tap();
  else await locator.click();
  await settle(page);
}

async function menuState(page, open) {
  await page.waitForFunction(open => {
    const menu = document.querySelector('.nav-dropdown-menu');
    return menu.hidden === !open && document.querySelector('.nav-dropdown-trigger').getAttribute('aria-expanded') === String(open);
  }, open);
}

async function checkMenu(page, touch, screenshotName) {
  const trigger = '.nav-dropdown-trigger';
  // WebKit's macOS default uses Option+Tab to include links in keyboard traversal.
  const tab = process.platform === 'darwin' && page.context().browser().browserType().name() === 'webkit' ? 'Alt+Tab' : 'Tab';
  await menuState(page, false);
  await activate(page, trigger, touch);
  await menuState(page, true);
  const links = await page.locator('.nav-dropdown-menu a').evaluateAll(links => links.map(link => {
    const rect = link.getBoundingClientRect();
    const hit = document.elementFromPoint(rect.x + rect.width / 2, rect.y + rect.height / 2);
    return { text: link.innerText, hit: link.contains(hit), height: rect.height,
      inViewport: rect.left >= 0 && rect.right <= innerWidth && rect.top >= 0 && rect.bottom <= innerHeight };
  }));
  assert.equal(links.length, 3);
  assert(links.every(l => l.hit && l.inViewport && (!touch || l.height >= 48)), JSON.stringify(links));
  if (screenshotName) await captureScreenshot(page, { path: path.join(output, screenshotName), scale: 'css' });
  await activate(page, trigger, touch);
  await menuState(page, false);
  await activate(page, trigger, touch);
  // The dropdown covers the hero title; use the header's empty outer margin.
  if (touch) await page.touchscreen.tap(2, 2);
  else await page.mouse.click(2, 2);
  await menuState(page, false);

  await page.locator(trigger).focus();
  await page.keyboard.press('Enter');
  await menuState(page, true);
  await page.keyboard.press(tab);
  assert(await page.locator('.nav-dropdown-menu a').first().evaluate(el => el === document.activeElement),
    await page.evaluate(() => document.activeElement.outerHTML.slice(0, 250)));
  await page.keyboard.press('Escape');
  await menuState(page, false);
  assert(await page.locator(trigger).evaluate(el => el === document.activeElement),
    await page.evaluate(() => document.activeElement.outerHTML.slice(0, 250)));
  await page.keyboard.press(tab);
  assert.equal(await page.evaluate(() => document.activeElement.getAttribute('href')), '#references');
  await page.locator(trigger).focus();
  await page.keyboard.press('Space');
  await menuState(page, true);
  for (let i = 0; i < 4; i++) await page.keyboard.press(tab);
  await menuState(page, false);
  assert.equal(await page.evaluate(() => document.activeElement.getAttribute('href')), '#references');
}

async function checkPreview(page, theme, layout) {
  await page.locator('.coco-gui-image').evaluate(img => img.decode());
  const expected = `img/${previews[layout][0]}_${theme === 'dark' ? 'Dark' : 'Light'}.png`;
  assert.equal(await page.locator('.coco-gui-image').getAttribute('src'), expected);
  assert.equal(await page.locator('.coco-gui-figure').getAttribute('aria-label'), `CoCo GUI preview: ${previews[layout][1]} in ${theme} mode`);
  assert.equal(await page.locator('[data-coco-gui-theme-option][aria-pressed="true"]').getAttribute('data-coco-gui-theme-option'), theme);
  assert.equal(await page.locator('[data-coco-gui-layout-option][aria-pressed="true"]').getAttribute('data-coco-gui-layout-option'), layout);
  assert.equal(await page.locator('[data-coco-gui-theme-option][aria-pressed="true"]').count(), 1);
  assert.equal(await page.locator('[data-coco-gui-layout-option][aria-pressed="true"]').count(), 1);
  const box = await page.locator('.coco-gui-figure').boundingBox();
  assert(Math.abs(box.width / box.height - 16 / 9) < 0.02);
}

async function checkPreviews(page, touch) {
  // Resource completion is observed before any preview control is touched.
  await page.waitForFunction(files => files.every(file => performance.getEntriesByType('resource')
    .some(resource => resource.name.endsWith('/' + file))), previewFiles);
  for (const theme of ['light', 'dark']) {
    await activate(page, `[data-coco-gui-theme-option="${theme}"]`, touch);
    for (const layout of Object.keys(previews)) {
      await activate(page, `[data-coco-gui-layout-option="${layout}"]`, touch);
      await checkPreview(page, theme, layout);
    }
  }
  await page.evaluate(() => {
    for (const [kind, value] of [['theme', 'light'], ['layout', 'dag'], ['theme', 'dark'], ['layout', '2d'], ['layout', 'emodelg']]) {
      document.querySelector(`[data-coco-gui-${kind}-option="${value}"]`).click();
    }
  });
  await checkPreview(page, 'dark', 'emodelg');
}

async function checkAnchor(page, id) {
  await page.waitForFunction(id => {
    const target = document.getElementById(id).getBoundingClientRect();
    const header = document.querySelector('.site-header').getBoundingClientRect();
    return target.top >= header.bottom + 14 && target.top <= header.bottom + 19;
  }, id);
}

async function checkNavigation(page, touch) {
  await navigate(page);
  for (const view of ['cryosieve', 'cryopros', 'emodelg']) {
    await activate(page, '.nav-dropdown-trigger', touch);
    await activate(page, `.nav-dropdown-menu [data-nav="${view}"]`, touch);
    await activeView(page, view);
    await menuState(page, false);
    assert.equal(new URL(page.url()).hash, '#' + view);
  }
  await activate(page, '.nav-cta', touch);
  await activeView(page, 'home');
  await checkAnchor(page, 'download');
  await page.goBack();
  await activeView(page, 'emodelg');
  await page.goForward();
  await activeView(page, 'home');
  await checkAnchor(page, 'download');
  for (const id of ['overview', 'references']) {
    await activate(page, `.nav-links > a[href="#${id}"]`, touch);
    await checkAnchor(page, id);
    await navigate(page, id);
    await checkAnchor(page, id);
  }
  for (const version of ['0-3-5', '0-3-2', '0-2-0', '0-1-0']) {
    const view = `install-v${version}`;
    await navigate(page, `${view}-runtime`);
    await checkAnchor(page, `${view}-runtime`);
    await activate(page, `[data-view="${view}"] .install-toc a[href="#${view}-start"]`, touch);
    await checkAnchor(page, `${view}-start`);
  }
  for (const [hash, target] of [['install', 'install-v0-3-5-top'], ['install-runtime', 'install-v0-3-5-runtime'], ['install-login', 'install-v0-3-5-top']]) {
    await navigate(page, hash);
    await activeView(page, 'install-v0-3-5');
    await checkAnchor(page, target);
  }
}

async function checkLayout(page, view, touch) {
  await activeView(page, view);
  for (const img of await page.locator(`[data-view="${view}"] img`).all()) {
    if (await img.isVisible()) {
      await img.scrollIntoViewIfNeeded();
      await img.evaluate(img => img.decode());
    }
  }
  await page.evaluate(() => scrollTo({ top: 0, behavior: 'instant' }));
  await settle(page);
  const layout = await page.evaluate(() => {
    const doc = document.documentElement;
    const visible = e => e.getBoundingClientRect().width && e.getBoundingClientRect().height;
    const rect = e => e.getBoundingClientRect().toJSON();
    const headerLinks = [...document.querySelectorAll('.nav-links > a, .nav-dropdown-trigger, .nav-cta')].filter(visible);
    const overflow = [...document.querySelectorAll('body *')].filter(visible).filter(e => {
      if (e.closest('.demo-table-wrap, .install-command, .install-hero-facts, .install-toc-list')) return false;
      const r = e.getBoundingClientRect();
      return r.right > doc.clientWidth + 1 || r.left < -1;
    }).slice(0,8).map(e => ({ tag: e.tagName, class: e.className, text: e.textContent.trim().slice(0, 60) }));
    return { width: doc.clientWidth, scrollWidth: Math.max(doc.scrollWidth, document.body.scrollWidth),
      coarsePointer: matchMedia('(pointer: coarse)').matches, maxTouchPoints: navigator.maxTouchPoints,
      headerHeight: rect(document.querySelector('.site-header')).height,
      downloads: document.querySelectorAll('.site-header a[href="#download"]').length,
      links: headerLinks.map(e => ({ text: e.innerText, ...rect(e) })),
      brand: rect(document.querySelector('.brand-mark')), cta: rect(document.querySelector('.nav-cta')), overflow };
  });
  assert(layout.scrollWidth <= layout.width + 1, `${view}: document overflow ${JSON.stringify(layout)}`);
  assert.deepEqual(layout.overflow, [], `${view}: content outside viewport`);
  assert.equal(layout.downloads, 1);
  assert(layout.brand.right <= layout.cta.left, `${view}: brand overlaps download button`);
  for (const link of layout.links) {
    assert(link.left >= 0 && link.right <= layout.width, `${view}: clipped ${link.text}`);
    if (touch) assert(link.height >= 48, `${view}: touch target ${link.text} is ${link.height}px (coarse=${layout.coarsePointer}, maxTouchPoints=${layout.maxTouchPoints})`);
  }
  if (view === 'home' && layout.width < 576) {
    const buttons = await page.locator('[data-coco-gui-layout-option]').evaluateAll(es => es.map(e => e.getBoundingClientRect().toJSON()));
    assert.equal(buttons.length, 4);
    assert.equal(buttons[0].top, buttons[1].top);
    assert.equal(buttons[2].top, buttons[3].top);
    assert(buttons[2].top >= buttons[0].bottom && buttons[1].left >= buttons[0].right);
    if (touch) assert(buttons.every(button => button.height >= 48));
  }
  return { view, width: layout.width, scrollWidth: layout.scrollWidth, headerHeight: layout.headerHeight };
}

async function checkLoadingFailure(browser, contextOptions) {
  const context = await browser.newContext(contextOptions);
  try {
    await context.route('**/DAG_Dark.png', route => route.abort());
    const page = await context.newPage();
    page.setDefaultTimeout(8000);
    const errors = [];
    page.on('pageerror', e => errors.push(e.message));
    await navigate(page);
    await activate(page, '[data-coco-gui-theme-option="dark"]', contextOptions.hasTouch);
    await activate(page, '[data-coco-gui-layout-option="dag"]', contextOptions.hasTouch);
    await page.waitForFunction(() => {
      const img = document.querySelector('.coco-gui-image');
      return img.complete && img.naturalWidth === 0;
    });
    await activate(page, '[data-coco-gui-layout-option="emodelg"]', contextOptions.hasTouch);
    await checkPreview(page, 'dark', 'emodelg');
    await activate(page, '.nav-dropdown-trigger', contextOptions.hasTouch);
    await activate(page, '.nav-dropdown-menu [data-nav="cryosieve"]', contextOptions.hasTouch);
    await activeView(page, 'cryosieve');
    assert.deepEqual(errors, []);
  } finally { await context.close(); }
}

try {
  console.log(`Artifacts: ${output}`);
  for (const { name, engine, defaultBrowserType, ...options } of selected) {
    const result = { profile: name, engine, viewport: options.viewport, status: 'passed', views: [] };
    let page, context;
    try {
      if (!browsers.has(engine)) browsers.set(engine, await ({ chromium, webkit }[engine]).launch({ headless: true }));
      const browser = browsers.get(engine);
      result.browserVersion = browser.version();
      context = await browser.newContext({ ...options, reducedMotion: 'reduce' });
      page = await context.newPage();
      page.setDefaultTimeout(8000);
      const errors = [];
      page.on('pageerror', e => errors.push(e.message));
      await navigate(page);
      await checkPreviews(page, options.hasTouch);
      result.preloadedImages = previewFiles.length;
      await page.evaluate(() => scrollTo({ top: 0, behavior: 'instant' }));
      await checkMenu(page, options.hasTouch, `${name}-menu.png`);
      await checkNavigation(page, options.hasTouch);
      for (const view of views) {
        await navigate(page, view === 'home' ? '' : view);
        result.views.push(await checkLayout(page, view, options.hasTouch));
        await captureScreenshot(page, { path: path.join(output, `${name}-${view}.png`), fullPage: true, scale: 'css' });
      }
      // Resize the same page across the navigation breakpoint to exercise ResizeObserver.
      await navigate(page);
      await page.setViewportSize(options.viewport.width < 672 ? { width: 734, height: 343 } : { width: 393, height: 852 });
      await settle(page);
      await activate(page, '.nav-cta', options.hasTouch);
      await checkAnchor(page, 'download');
      await page.setViewportSize(options.viewport);
      await settle(page);
      await activate(page, '.nav-links > a[href="#overview"]', options.hasTouch);
      await checkAnchor(page, 'overview');
      // Also exercise the normal animation timings on every device.
      await page.emulateMedia({ reducedMotion: 'no-preference' });
      await navigate(page);
      await activate(page, '.nav-dropdown-trigger', options.hasTouch);
      await activate(page, '.nav-dropdown-menu [data-nav="emodelg"]', options.hasTouch);
      await activeView(page, 'emodelg');
      await activate(page, '.nav-cta', options.hasTouch);
      await activeView(page, 'home');
      await checkAnchor(page, 'download');
      assert.deepEqual(errors, []);
      await checkLoadingFailure(browser, { ...options, reducedMotion: 'reduce' });
    } catch (error) {
      result.status = 'failed';
      result.error = error.stack;
      if (page && !page.isClosed()) {
        result.url = page.url();
        await captureScreenshot(page, { path: path.join(output, `${name}-failure.png`), scale: 'css' }).catch(() => {});
      }
      process.exitCode = 1;
    } finally {
      if (context) await context.close();
    }
    results.push(result);
    await writeFile(path.join(output, 'results.json'), JSON.stringify(results, null, 2) + '\n');
    console.log(`${result.status.toUpperCase()} ${name}: ${result.views.length}/${views.length} views${result.error ? '\n' + result.error : ''}`);
  }
} finally {
  for (const browser of browsers.values()) await browser.close();
  await new Promise(resolve => server.close(resolve));
}

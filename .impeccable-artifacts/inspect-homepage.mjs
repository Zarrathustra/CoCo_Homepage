import { chromium } from 'playwright';
import { fileURLToPath, pathToFileURL } from 'node:url';
import path from 'node:path';

const root = path.dirname(path.dirname(fileURLToPath(import.meta.url)));
const pageUrl = pathToFileURL(path.join(root, 'index.html')).href;
const browser = await chromium.launch({ headless: true });
const page = await browser.newPage();

const viewports = [
  { name: 'mobile', width: 390, height: 844 },
  { name: 'tablet', width: 820, height: 1180 },
  { name: 'desktop', width: 1440, height: 1100 },
];

const results = [];
for (const viewport of viewports) {
  await page.setViewportSize({ width: viewport.width, height: viewport.height });
  await page.goto(pageUrl, { waitUntil: 'load' });
  await page.screenshot({ path: path.join(root, '.impeccable-artifacts', `homepage-${viewport.name}.png`), fullPage: true });

  const metrics = await page.evaluate(() => {
    const doc = document.documentElement;
    const body = document.body;
    const overflow = Math.max(body.scrollWidth, doc.scrollWidth) - doc.clientWidth;
    const navRect = document.querySelector('.site-header')?.getBoundingClientRect();
    const heroRect = document.querySelector('.hero-section')?.getBoundingClientRect();
    const mapRect = document.querySelector('.workbench-card')?.getBoundingClientRect();
    const downloadButton = document.querySelector('.hero-actions .button-primary')?.getBoundingClientRect();
    const visibleText = document.body.innerText;
    return {
      title: document.title,
      viewportWidth: doc.clientWidth,
      scrollWidth: Math.max(body.scrollWidth, doc.scrollWidth),
      horizontalOverflow: overflow,
      navHeight: navRect?.height ?? null,
      heroHeight: heroRect?.height ?? null,
      mapWidth: mapRect?.width ?? null,
      mapHeight: mapRect?.height ?? null,
      primaryButtonWidth: downloadButton?.width ?? null,
      primaryButtonHeight: downloadButton?.height ?? null,
      downloadMentions: (visibleText.match(/Download CoCo/g) || []).length,
      ctfaTbd: visibleText.includes('CTFA') && visibleText.includes('TBD'),
    };
  });
  results.push({ viewport, metrics });
}

await browser.close();
console.log(JSON.stringify(results, null, 2));

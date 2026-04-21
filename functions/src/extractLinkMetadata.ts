import { onCall, HttpsError } from "firebase-functions/v2/https";
import * as logger from "firebase-functions/logger";
import * as cheerio from "cheerio";

type Field<T> = {
  value: T | null;
  source: string | null;
};

type JsonLdResult = {
  product: Field<string>;
  imageUrl: Field<string>;
  price: Field<number>;
  description: Field<string>;
  brand: Field<string>;
};

function firstNonNull<T>(
  candidates: Array<[T | null | undefined, string]>
): [T | null, string | null] {
  for (const [value, source] of candidates) {
    const isEmptyArray = Array.isArray(value) && value.length === 0;
    const isEmptyObject =
      value != null &&
      typeof value === "object" &&
      !Array.isArray(value) &&
      Object.keys(value as Record<string, unknown>).length === 0;

    if (
      value !== null &&
      value !== undefined &&
      value !== "" &&
      !isEmptyArray &&
      !isEmptyObject
    ) {
      return [value, source];
    }
  }

  return [null, null];
}

function normalizeStore(
  url: string,
  ogSiteName: string | null
): string | null {
  if (ogSiteName) {
    return ogSiteName.trim();
  }

  let hostname = "";
  try {
    hostname = new URL(url).hostname
      .replace(/^www\./, "")
      .replace(/^www2\./, "");
  } catch {
    return null;
  }

  const known: Record<string, string> = {
    "amazon.es": "Amazon",
    "amazon.com": "Amazon",
    "nike.com": "Nike",
    "zara.com": "Zara",
    "hm.com": "H&M",
    "pccomponentes.com": "PcComponentes",
  };

  if (known[hostname]) {
    return known[hostname];
  }

  if (!hostname) {
    return null;
  }

  const main = hostname.split(".")[0] ?? "";
  return main ? `${main[0]!.toUpperCase()}${main.slice(1)}` : null;
}

function cleanTitle(
  title: string | null,
  store: string | null
): string | null {
  if (!title) {
    return null;
  }

  let cleaned = title.trim();

  if (store) {
    const patterns = [
      ` | ${store}`,
      ` - ${store}`,
      ` – ${store}`,
      ` · ${store}`,
    ];

    for (const pattern of patterns) {
      if (cleaned.endsWith(pattern)) {
        cleaned = cleaned.slice(0, -pattern.length).trim();
      }
    }
  }

  return cleaned;
}

function parsePriceValue(value: unknown): number | null {
  if (value === null || value === undefined) {
    return null;
  }

  if (typeof value === "number") {
    return Number.isNaN(value) ? null : value;
  }

  let text = String(value).trim();
  text = text.replace(/\u00a0/g, " ").replace(/€/g, "").trim();

  if (text.includes(",") && text.includes(".")) {
    if (text.lastIndexOf(",") > text.lastIndexOf(".")) {
      text = text.replace(/\./g, "").replace(",", ".");
    } else {
      text = text.replace(/,/g, "");
    }
  } else {
    text = text.replace(",", ".");
  }

  const parsed = Number.parseFloat(text);
  return Number.isNaN(parsed) ? null : parsed;
}

function extractJsonLdObjects(raw: unknown): unknown[] {
  const objects: unknown[] = [];

  function walk(value: unknown): void {
    if (Array.isArray(value)) {
      for (const item of value) {
        walk(item);
      }
      return;
    }

    if (value && typeof value === "object") {
      objects.push(value);

      const graph = (value as Record<string, unknown>)["@graph"];
      if (graph !== undefined) {
        walk(graph);
      }
    }
  }

  walk(raw);
  return objects;
}

function isProductType(typeValue: unknown): boolean {
  if (typeof typeValue === "string") {
    return typeValue.toLowerCase() === "product";
  }

  if (Array.isArray(typeValue)) {
    return typeValue.some(
      (v) => typeof v === "string" && v.toLowerCase() === "product"
    );
  }

  return false;
}

function resolveUrl(baseUrl: string, candidate: string | null): string | null {
  if (!candidate) {
    return null;
  }

  try {
    return new URL(candidate, baseUrl).toString();
  } catch {
    return candidate;
  }
}

function extractFromJsonLd(
  $: cheerio.CheerioAPI,
  baseUrl: string
): JsonLdResult {
  const result: JsonLdResult = {
    product: { value: null, source: null },
    imageUrl: { value: null, source: null },
    price: { value: null, source: null },
    description: { value: null, source: null },
    brand: { value: null, source: null },
  };

  const scripts = $('script[type="application/ld+json"]');

  for (let i = 0; i < scripts.length; i++) {
    try {
      const content = $(scripts[i]).html();
      if (!content || !content.trim()) {
        continue;
      }

      const data = JSON.parse(content);
      const objects = extractJsonLdObjects(data);

      for (const obj of objects) {
        if (!obj || typeof obj !== "object" || Array.isArray(obj)) {
          continue;
        }

        const dict = obj as Record<string, unknown>;

        if (!isProductType(dict["@type"])) {
          continue;
        }

        if (result.product.value === null && dict["name"]) {
          result.product = {
            value: String(dict["name"]).trim(),
            source: "json-ld:name",
          };
        }

        const imageRaw = dict["image"];
        if (result.imageUrl.value === null && imageRaw) {
          let image: unknown = imageRaw;

          if (Array.isArray(image) && image.length > 0) {
            image = image[0];
          }

          if (
            image &&
            typeof image === "object" &&
            !Array.isArray(image) &&
            "url" in image
          ) {
            image = (image as Record<string, unknown>)["url"];
          }

          if (image) {
            result.imageUrl = {
              value: resolveUrl(baseUrl, String(image).trim()),
              source: "json-ld:image",
            };
          }
        }

        if (result.description.value === null && dict["description"]) {
          result.description = {
            value: String(dict["description"]).trim(),
            source: "json-ld:description",
          };
        }

        const brandRaw = dict["brand"];
        if (result.brand.value === null && brandRaw) {
          let brand: unknown = brandRaw;

          if (
            brand &&
            typeof brand === "object" &&
            !Array.isArray(brand) &&
            "name" in brand
          ) {
            brand = (brand as Record<string, unknown>)["name"];
          }

          if (brand) {
            result.brand = {
              value: String(brand).trim(),
              source: "json-ld:brand",
            };
          }
        }

        const offersRaw = dict["offers"];
        const offersCandidates = Array.isArray(offersRaw)
          ? offersRaw
          : [offersRaw];

        for (const offer of offersCandidates) {
          if (!offer || typeof offer !== "object" || Array.isArray(offer)) {
            continue;
          }

          const offerDict = offer as Record<string, unknown>;
          const price = parsePriceValue(offerDict["price"]);
          if (price !== null) {
            result.price = {
              value: price,
              source: "json-ld:offers.price",
            };
            break;
          }
        }

        return result;
      }
    } catch {
      continue;
    }
  }

  return result;
}

function extractRegexPrice(bodyText: string): number | null {
  const match = bodyText.match(
    /(\d{1,3}(?:[.\s]\d{3})*[.,]\d{2})\s?€|€\s?(\d{1,3}(?:[.\s]\d{3})*[.,]\d{2})/
  );

  if (!match) {
    return null;
  }

  const raw = match[1] ?? match[2];
  return parsePriceValue(raw);
}

export const extractLinkMetadata = onCall(
  {
    region: "europe-west1",
    maxInstances: 1,
    timeoutSeconds: 15,
    memory: "256MiB",
  },
  async (request) => {
    const url = request.data?.url as string | undefined;

    if (!url) {
      throw new HttpsError("invalid-argument", "URL is required");
    }

    const headers: Record<string, string> = {
      "User-Agent":
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36",
      Accept:
        "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8",
      "Accept-Language": "es-ES,es;q=0.9,en;q=0.8",
      "Cache-Control": "no-cache",
      Pragma: "no-cache",
      "Upgrade-Insecure-Requests": "1",
      "Sec-Fetch-Dest": "document",
      "Sec-Fetch-Mode": "navigate",
      "Sec-Fetch-Site": "none",
      "Sec-Fetch-User": "?1",
    };

    try {
      const response = await fetch(url, {
        headers,
        redirect: "follow",
        signal: AbortSignal.timeout(8000),
      });

      const finalUrl = response.url || url;
      const html = await response.text();
      const statusCode = response.status;
      const contentType = response.headers.get("content-type");

      if (!response.ok) {
        return {
          product: { value: null, source: null },
          imageUrl: { value: null, source: null },
          price: { value: null, source: null },
          store: {
            value: normalizeStore(finalUrl, null),
            source: "hostname",
          },
          description: { value: null, source: null },
          brand: { value: null, source: null },
          link: finalUrl,
          debug: {
            statusCode,
            finalUrl,
            contentType,
            htmlLength: html.length,
            blocked: true,
          },
        };
      }

      const $ = cheerio.load(html);

      const meta = (prop: string): string | null => {
        const value =
          $(`meta[property="${prop}"]`).attr("content") ??
          $(`meta[name="${prop}"]`).attr("content") ??
          null;

        return typeof value === "string" ? value.trim() : null;
      };

      const jsonLd = extractFromJsonLd($, finalUrl);

      const ogSiteName = meta("og:site_name");
      const store = normalizeStore(finalUrl, ogSiteName);
      const storeSource = ogSiteName ? "og:site_name" : "hostname";

      const titleTag = $("title").first().text()?.trim() || null;

      let [product, productSource] = firstNonNull<string>([
        [jsonLd.product.value, jsonLd.product.source ?? "json-ld:name"],
        [meta("og:title"), "og:title"],
        [meta("twitter:title"), "twitter:title"],
        [titleTag, "title"],
      ]);
      product = cleanTitle(product, store);

      const [description, descriptionSource] = firstNonNull<string>([
        [
          jsonLd.description.value,
          jsonLd.description.source ?? "json-ld:description",
        ],
        [meta("og:description"), "og:description"],
        [meta("twitter:description"), "twitter:description"],
        [meta("description"), "meta:description"],
      ]);

      let [image, imageSource] = firstNonNull<string>([
        [jsonLd.imageUrl.value, jsonLd.imageUrl.source ?? "json-ld:image"],
        [meta("og:image"), "og:image"],
        [meta("twitter:image"), "twitter:image"],
      ]);
      image = resolveUrl(finalUrl, image);

      let [price, priceSource] = firstNonNull<number>([
        [jsonLd.price.value, jsonLd.price.source ?? "json-ld:offers.price"],
        [parsePriceValue(meta("product:price:amount")), "meta:product:price:amount"],
        [
          parsePriceValue(meta("product:sale_price:amount")),
          "meta:product:sale_price:amount",
        ],
        [parsePriceValue(meta("og:price:amount")), "meta:og:price:amount"],
        [parsePriceValue(meta("price")), "meta:price"],
      ]);

      if (price === null) {
        const bodyText = $("body").text().replace(/\s+/g, " ").trim();
        const regexPrice = extractRegexPrice(bodyText);
        if (regexPrice !== null) {
          price = regexPrice;
          priceSource = "regex";
        }
      }

      return {
        product: {
          value: product,
          source: productSource,
        },
        imageUrl: {
          value: image,
          source: imageSource,
        },
        price: {
          value: price,
          source: priceSource,
        },
        store: {
          value: store,
          source: storeSource,
        },
        description: {
          value: description,
          source: descriptionSource,
        },
        brand: jsonLd.brand,
        link: finalUrl,
        debug: {
          statusCode,
          finalUrl,
          contentType,
          htmlLength: html.length,
        },
      };
    } catch (error) {
      logger.error("extractLinkMetadata failed", error);
      throw new HttpsError("internal", "Failed to extract metadata");
    }
  }
);
# cloud-itonami-assoc-2910-chn-caam

Industry self-regulatory rule catalog for **中国汽车工业协会 (China
Association of Automobile Manufacturers, CAAM)** — the **first CHN member
of the `cloud-itonami-assoc-*` family**, and the third aligned to ISIC 2910
(manufacture of motor vehicles) alongside
[`cloud-itonami-assoc-2910-deu-vda`](https://github.com/cloud-itonami/cloud-itonami-assoc-2910-deu-vda)
and
[`cloud-itonami-assoc-2910-gbr-smmt`](https://github.com/cloud-itonami/cloud-itonami-assoc-2910-gbr-smmt).
Part of the [`cloud-itonami`](https://github.com/cloud-itonami)
compliance-fact family (ADR-2607141700,
`cloud-itonami-compliance-fact-federation`, in `com-junkawasaki/root`).

## Why this repo exists

Before it, the 106 `cloud-itonami-assoc-*` repos covered 38 `usa`, 18
`jpn`, 6 `deu`, 4 `gbr`, 4 `fra` … and **0 `chn`**. Recorded as a coverage
gap in superproject ADR-2607277000 alongside the China marketing vertical.

## Scope

A **read-only reference/archive** catalog — not an Advisor⊣Governor
actuation actor. It proposes or executes nothing on CAAM's behalf, and it
is not CAAM.

Coverage is reported through bounded `entry-count`, `association-covered?`
and `by-topic-*` operations. An unknown association has no spec-basis.

## Data

- `src/association_facts.kotoba` — the sole production source; every
  present field and ordered topic is exposed through bounded count/index
  access.
- `schema/association-rule.edn` — DataScript schema (identical to every
  sibling, so the federated query joins across all of them unchanged).
- `data/datascript-tx.edn` — derived DataScript tx-data (query alongside
  the other `cloud-itonami`/`etzhayyim` compliance-fact sources via
  `com-junkawasaki/root`'s `scripts/compliance-fact-query.cljs`).

## Verification

Everything recorded here comes from CAAM's own **协会简介** page
(<http://www.caam.org.cn/chn/2/cate_7/con_5223237.html>, page 发布时间
2023-06-12), read on **2026-07-27**. That page states the founding date
(1990-01-01), the 民政部 approval and 社会团体法人 status, the Beijing
headquarters, the 3300+ 会员单位 figure, OICA standing-council membership,
and the twelve-item 业务范围 list in which **行业自律** (industry
self-discipline) appears.

**Two honest limitations, recorded rather than papered over:**

1. Both entries cite the **same URL**, because that page was the only CAAM
   source reachable from the authoring network. Unlike the VDA sibling,
   **no separate self-regulatory-code document is claimed** — CAAM's
   self-discipline mandate is recorded as a `self-regulatory-mandate`
   stated on the profile page, not as a code whose text was read. The
   `coverage-note` says so, and a test asserts that it does.
2. A second intended CHN entry, 中国银行业协会 (China Banking Association,
   ISIC 6419), was attempted and **abandoned**: `china-cba.net` was
   unreachable over both HTTP and HTTPS from the authoring network, and
   this family does not accept secondary sources for an
   `official-association-site` provenance. It appears nowhere in this
   repo's data.

The site is HTTP-only; it was read over plain HTTP and the `url` field
records the `http://` scheme it actually serves rather than an `https://`
URL that does not resolve.

## License

AGPL-3.0-or-later (matches the `cloud-itonami-iso3166-*` /
`-municipality-*` / `-assoc-*` / `-lei-*` convention). Document text
itself remains CAAM's; this repo stores only citation metadata
(id/title/url/dates), not full text.

## Running it

Run `kbb -M:test` and `kbb -M:lint`. Qualification covers reference
semantics, restricted JavaScript, and instantiated typed WebAssembly. The
JVM is a compiler/test host only.

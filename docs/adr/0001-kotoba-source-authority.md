# ADR 0001: Kotoba is the CAAM catalog source authority

- Status: Accepted
- Date: 2026-07-27

## Decision

`src/association_facts.kotoba` is the sole production source. Both
citations retain every present scalar field. The first citation carries the
full `1990-01-01` established date; the second **omits** an established
date entirely rather than inheriting the association's founding year,
because the self-discipline mandate is a scope statement, not a dated
instrument. Topic count plus indexed access preserves the ordered
governance/standards pair and the self-discipline singleton. Unknown values
and indexes return zero or typed option-none; no effects are declared.

CI executes reference semantics, restricted JavaScript, instantiated typed
WebAssembly, and production source-authority checks. Clojure and the JVM
are compiler/test hosts only.

## Sourcing decisions specific to this repo

- **Both entries share one URL.** CAAM's 协会简介 page was the only CAAM
  source reachable from the authoring network on 2026-07-27. Rather than
  fabricate a second document URL to make the two entries look
  independently sourced, both cite the page that actually states them, and
  `coverage-note` records that this is why. A test asserts the note says
  so, so removing the caveat breaks the build.
- **The second entry is `:self-regulatory-mandate`, not
  `:self-regulatory-code`.** The VDA sibling's first entry is a code whose
  PDF was read; here the page states that 行业自律 is one of twelve
  业务范围 items, which is a mandate, not a code text. Using the sibling's
  `kind` would have overstated what was verified.
- **`url` keeps the `http://` scheme.** caam.org.cn does not serve HTTPS;
  recording an `https://` URL would produce a citation that does not
  resolve.
- **中国银行业协会 (ISIC 6419) was attempted and abandoned.**
  `china-cba.net` was unreachable over both schemes, and this family
  requires `official-association-site` provenance. No CBA entry exists in
  any file here — the gap stays open rather than being filled from
  secondary sources.

## Consequences

- Missing dates remain absent instead of being guessed.
- Multi-topic entries remain complete without host sets.
- The CHN row of the assoc family is opened at 1 association; 6419 and the
  other ISIC codes with existing multi-country coverage remain uncovered
  for CHN and are recorded as such in superproject ADR-2607277000.

# Fabric Guidebook

A generic Fabric guidebook mod for Markdown content supplied by mods or resource packs.

## Resource Convention

Define a guide in:

```text
assets/<namespace>/guidebook_guides/<guide_id>.json
```

Example:

```json
{
  "title": "Example Guide",
  "folder": "guidebook/main",
  "landing_page": "index.md"
}
```

Pages are loaded from:

```text
assets/<namespace>/<folder>/**/*.md
```

For the example above:

```text
assets/<namespace>/guidebook/main/index.md
```

## API

```java
Guide guide = Guide.builder(new Identifier("modid", "guide"))
    .folder("guidebook/main")
    .title("My Guide")
    .landingPage("index.md")
    .build();

Guidebooks.register(guide);
Guidebooks.open(new Identifier("modid", "guide"), "index.md");
Guidebooks.openForItem(stack);
```

The bundled generic item is:

```text
fabric_guidebook:guide
```

Give a guide-bound item:

```text
/give @p fabric_guidebook:guide{GuideId:"modid:guide"}
```

## License Notice

This project includes relocated guidebook and Markdown parsing code from Applied
Energistics 2 (`appeng.client.guidebook` relocated to
`appengx.client.guidebook`, and `appeng.libs` relocated to `appengx.libs`). See
`NOTICE`
and `LICENSE-LGPL-3.0`.

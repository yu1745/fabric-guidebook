---
navigation:
  title: Fabric Guidebook
  position: 0
  icon: minecraft:book
---

# Fabric Guidebook

Fabric Guidebook renders Markdown guides provided by mods and resource packs.

Use this guide as a reference while authoring another mod's guide:

- [Authoring examples](authoring.md)

## Guide definition

Create a guide definition JSON:

```json
{
  "title": "My Guide",
  "folder": "guidebook/main",
  "landing_page": "index.md",
  "starter_item": true
}
```

Place it at:

`assets/{namespace}/guidebook_guides/{guide_id}.json`

Then place Markdown pages and guide assets under the configured folder:

`assets/{namespace}/guidebook/main/`

The default landing page is `index.md`. Links between pages are normal Markdown links:

```md
[Machines](machines/index.md)
```

Starter items are named from the guide namespace's mod name, formatted with Fabric Guidebook's language keys. `title_key` is optional and can override this for a custom starter item name.

`starter_item` controls whether players receive a `fabric_guidebook:guide` item for this guide when they join a world. It defaults to `true`; set it to `false` for guides that should only be opened by command or custom integration.

Localized pages can override default pages by using the same relative path under an `i18n/{language}` folder:

`assets/{namespace}/guidebook/main/i18n/zh_cn/index.md`

## Localization

Guide pages use the current client language. Put translated Markdown files under:

`assets/{namespace}/{guide_folder}/i18n/{language}/`

Use the same relative path as the default page. If a translated page is missing, Fabric Guidebook falls back to the default page.

Normal Minecraft language files are still used for item names and UI text:

`assets/{namespace}/lang/zh_cn.json`

Starter book names are formatted by Fabric Guidebook, using the guide namespace's mod name:

```json
{
  "item.fabric_guidebook.starter_guide": "%s Guide"
}
```

If a guide needs a completely custom starter book name, add `title_key` to the guide definition and provide that key in lang files.

## Guide item

The generic guide item stores its target guide id in NBT:

```mcfunction
/give @p fabric_guidebook:guide{GuideId:"example:main"}
```

## Starter books

On the server, Fabric Guidebook scans every loaded mod for guide definitions:

`assets/{namespace}/guidebook_guides/{guide_id}.json`

When a player joins, each guide with `starter_item` enabled gives one `fabric_guidebook:guide` item. The item stores the guide id in `GuideId`, so books from different mods stay independent.

The server records delivery with a player tag:

`fabric_guidebook.given.{namespace}.{guide_path}`

For example, `ic2_120:main` uses:

`fabric_guidebook.given.ic2_120.main`

To make the server give that starter book again, remove the tag and let the player rejoin:

```mcfunction
/tag @p remove fabric_guidebook.given.ic2_120.main
```

You can also give a guide directly without touching the starter flag:

```mcfunction
/give @p fabric_guidebook:guide{GuideId:"ic2_120:main"}
```

Open a guide directly with a client command:

```mcfunction
/fabricguidebook open example:main
```

Open a specific page:

```mcfunction
/fabricguidebook open example:main machines/index.md
```

## Item pages

Add `item_ids` to a page frontmatter:

```md
---
navigation:
  title: Generator
  icon: example:generator
item_ids:
  - example:generator
---
```

Players can hold the open-guide key while hovering or holding a bound item to open its page.

## Development watch

During development, start the game with the usual `runClient` task, then enable live guide reloading from the client command line:

```mcfunction
/fabricguidebook dev watch example:main /absolute/path/to/src/main/resources/assets/example/guidebook/main
```

The watched folder should be the guide content folder that contains `index.md`, not the `assets` root.

Use `watchns` when the page namespace should be different from the guide id namespace:

```mcfunction
/fabricguidebook dev watchns example:main example /absolute/path/to/guidebook/main
```

Stop watching with:

```mcfunction
/fabricguidebook dev stop example:main
```

Markdown changes update the guide pages and item index. Asset changes such as `.snbt` structures or images reload the currently open page.

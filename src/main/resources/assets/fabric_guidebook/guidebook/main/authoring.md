---
navigation:
  title: Authoring Examples
  parent: index.md
  position: 10
  icon: minecraft:writable_book
categories:
  - authoring
item_ids:
  - minecraft:writable_book
---

# Authoring Examples

This page shows common Markdown and MDX snippets for guide pages.

## Page frontmatter

Every navigable page should start with YAML frontmatter:

```md
---
navigation:
  title: Generator
  parent: machines/index.md
  position: 10
  icon: example:generator
item_ids:
  - example:generator
categories:
  - machines
---
```

`navigation` adds the page to the left navigation tree. `item_ids` binds items to this page, so the open-guide key can jump from an item stack to the page. `categories` can be rendered elsewhere with `<CategoryIndex />`.

## Links

Use normal Markdown links for page navigation:

```md
[Machines](machines/index.md)
[Cable page](../cables/copper_cable.md)
[Jump to recipes](#recipes)
```

Inline item links render the item name, tooltip, and jump to the page bound through `item_ids` when one exists:

```md
Craft with <ItemLink id="minecraft:crafting_table" />.
```

Craft with <ItemLink id="minecraft:crafting_table" />.

## Item and block images

Use images when a page needs to show the actual item or block:

```md
<ItemImage id="minecraft:diamond_pickaxe" scale="2" />

<BlockImage id="minecraft:furnace" scale="2" />

<BlockImage id="minecraft:oak_log" p:axis="y" scale="2" />
```

<ItemImage id="minecraft:diamond_pickaxe" scale="2" />

<BlockImage id="minecraft:furnace" scale="2" />

## Recipes

Use `<Recipe />` when you know the exact recipe id:

```md
<Recipe id="minecraft:crafting_table" />
```

<Recipe id="minecraft:crafting_table" />

Use `<RecipeFor />` when you only care about the output item. Fabric Guidebook first tries known recipe layouts, then falls back to a generic ingredients/result layout for other `Recipe<?>` types:

```md
<RecipeFor id="minecraft:furnace" />
```

<RecipeFor id="minecraft:furnace" />

## Item grids

Use an item grid for variant lists:

```md
<ItemGrid>
  <ItemIcon id="minecraft:oak_planks" />
  <ItemIcon id="minecraft:spruce_planks" />
  <ItemIcon id="minecraft:birch_planks" />
  <ItemIcon id="minecraft:jungle_planks" />
</ItemGrid>
```

<ItemGrid>
  <ItemIcon id="minecraft:oak_planks" />
  <ItemIcon id="minecraft:spruce_planks" />
  <ItemIcon id="minecraft:birch_planks" />
  <ItemIcon id="minecraft:jungle_planks" />
</ItemGrid>

## Category index

Pages can opt into categories in frontmatter:

```md
---
categories:
  - machines
---
```

Render all pages in a category with:

```md
<CategoryIndex category="machines" />
```

This page is in the `authoring` category:

<CategoryIndex category="authoring" />

## 3D scenes

Use `<GameScene>` for small block scenes. Boolean attributes must use MDX expression syntax: `{true}` or `{false}`.

```md
<GameScene zoom="6" interactive={true} fullWidth={true}>
  <Block id="minecraft:stone" x="0" y="0" z="0" />
  <Block id="minecraft:crafting_table" x="1" y="0" z="0" />
  <Block id="minecraft:furnace" x="2" y="0" z="0" p:facing="north" />
</GameScene>
```

<GameScene zoom="6" interactive={true} fullWidth={true}>
  <Block id="minecraft:stone" x="0" y="0" z="0" />
  <Block id="minecraft:crafting_table" x="1" y="0" z="0" />
  <Block id="minecraft:furnace" x="2" y="0" z="0" p:facing="north" />
</GameScene>

For larger scenes, export an `.snbt` structure and import it:

```md
<GameScene zoom="7" interactive={true} fullWidth={true}>
  <ImportStructure src="machines/generator.snbt" />
</GameScene>
```

Structure authoring commands:

```mcfunction
/fabricguidebookstructure structuretool
/fabricguidebookstructure exportstructure
/fabricguidebookstructure clearstructureselection
/fabricguidebookstructure importstructure ~ ~ ~
/fabricguidebookstructure exportstructure ~ ~ ~ ~4 ~3 ~4
/fabricguidebookstructure exportstructure ~ ~ ~ size 5 4 5
```

`structuretool` gives a special stick. Left-click a block to set the start corner, right-click a block to set the end corner, then run `/fabricguidebookstructure exportstructure` to save that selected box. The selected region is rendered as a cyan wireframe in the world.

`clearstructureselection` clears the current selection.

`importstructure` places an `.snbt` or `.nbt` file into a singleplayer world. The coordinate form of `exportstructure` takes two inclusive corners, matching the selection tool. Use the `size` form when you want to type the origin and size manually.

## Development loop

Enable live reload for a guide folder:

```mcfunction
/fabricguidebook dev watch example:main /absolute/path/to/src/main/resources/assets/example/guidebook/main
```

Open a guide directly:

```mcfunction
/fabricguidebook open example:main
/fabricguidebook open example:main machines/index.md
```

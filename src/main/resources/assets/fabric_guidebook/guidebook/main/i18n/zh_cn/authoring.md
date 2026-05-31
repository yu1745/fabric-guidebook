---
navigation:
  title: 写作示例
  parent: index.md
  position: 10
  icon: minecraft:writable_book
categories:
  - authoring
item_ids:
  - minecraft:writable_book
---

# 写作示例

这里列出常用的 Markdown 和 MDX 写法。

## 页面 frontmatter

建议每个需要出现在目录中的页面都以 YAML frontmatter 开头：

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

`navigation` 会把页面加入左侧目录。`item_ids` 会把物品绑定到当前页面，玩家悬停或手持对应物品时可以用打开指南键跳转。`categories` 可以配合 `<CategoryIndex />` 在其他页面生成分类索引。

## 跳转链接

页面跳转使用普通 Markdown 链接：

```md
[机器](machines/index.md)
[铜导线](../cables/copper_cable.md)
[跳到配方](#配方)
```

行内物品链接会显示物品名和 tooltip；如果该物品通过 `item_ids` 绑定了页面，还会跳转到对应页面：

```md
使用 <ItemLink id="minecraft:crafting_table" /> 合成。
```

使用 <ItemLink id="minecraft:crafting_table" /> 合成。

## 物品和方块图片

需要展示物品或方块时可以使用：

```md
<ItemImage id="minecraft:diamond_pickaxe" scale="2" />

<BlockImage id="minecraft:furnace" scale="2" />

<BlockImage id="minecraft:oak_log" p:axis="y" scale="2" />
```

<ItemImage id="minecraft:diamond_pickaxe" scale="2" />

<BlockImage id="minecraft:furnace" scale="2" />

## 配方

知道明确配方 id 时使用 `<Recipe />`：

```md
<Recipe id="minecraft:crafting_table" />
```

<Recipe id="minecraft:crafting_table" />

只关心输出物品时使用 `<RecipeFor />`。Fabric Guidebook 会先尝试已知配方布局，再对其他 `Recipe<?>` 使用通用 ingredients/result 布局：

```md
<RecipeFor id="minecraft:furnace" />
```

<RecipeFor id="minecraft:furnace" />

## 物品网格

展示一组变体时可以使用物品网格：

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

## 分类索引

页面可以在 frontmatter 里声明分类：

```md
---
categories:
  - machines
---
```

用下面的写法渲染某个分类下的全部页面：

```md
<CategoryIndex category="machines" />
```

当前页面属于 `authoring` 分类：

<CategoryIndex category="authoring" />

## 3D 场景

小型方块展示可以直接写 `<GameScene>`。布尔属性必须使用 MDX 表达式语法：`{true}` 或 `{false}`。

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

大型场景建议导出 `.snbt` 结构，然后在页面中导入：

```md
<GameScene zoom="7" interactive={true} fullWidth={true}>
  <ImportStructure src="machines/generator.snbt" />
</GameScene>
```

结构制作命令：

```mcfunction
/fabricguidebookstructure structuretool
/fabricguidebookstructure exportstructure
/fabricguidebookstructure clearstructureselection
/fabricguidebookstructure importstructure ~ ~ ~
/fabricguidebookstructure exportstructure ~ ~ ~ ~4 ~3 ~4
/fabricguidebookstructure exportstructure ~ ~ ~ size 5 4 5
```

`structuretool` 会给一根特殊木棍。左键方块设置起点，右键方块设置终点，然后执行 `/fabricguidebookstructure exportstructure` 就会导出当前选区。选中的区域会在世界中实时渲染成青色线框。

`clearstructureselection` 会清除当前选区。

`importstructure` 会把 `.snbt` 或 `.nbt` 文件放进单人世界。`exportstructure` 的坐标形式使用两个包含端点的角点，和选区工具一致。如果需要手动输入起点和尺寸，可以使用 `size` 形式。

## 开发流程

对指南目录启用实时重载：

```mcfunction
/fabricguidebook dev watch example:main /absolute/path/to/src/main/resources/assets/example/guidebook/main
```

直接打开指南：

```mcfunction
/fabricguidebook open example:main
/fabricguidebook open example:main machines/index.md
```

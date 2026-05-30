---
navigation:
  title: Fabric Guidebook
  position: 0
  icon: minecraft:book
---

# Fabric Guidebook

Fabric Guidebook 用于渲染由 mod 和资源包提供的 Markdown 指南。

编写其他 mod 的指南时，可以直接参考这些页面：

- [写作示例](authoring.md)

## 指南定义

创建一个指南定义 JSON：

```json
{
  "title": "My Guide",
  "folder": "guidebook/main",
  "landing_page": "index.md",
  "starter_item": true
}
```

把它放在：

`assets/{namespace}/guidebook_guides/{guide_id}.json`

然后把 Markdown 页面和指南资源放到配置的目录下：

`assets/{namespace}/guidebook/main/`

默认首页是 `index.md`。页面之间可以使用普通 Markdown 链接：

```md
[机器](machines/index.md)
```

开局发放的指南书会使用指南 namespace 对应的 mod 名称，并通过 Fabric Guidebook 的语言键格式化。`title_key` 是可选字段，可以用来覆盖默认书名。

`starter_item` 控制玩家进服时是否收到 `fabric_guidebook:guide` 物品。默认值是 `true`；如果指南只应该通过命令或自定义集成打开，可以设为 `false`。

本地化页面可以放在 `i18n/{language}` 目录下，并使用和默认页面相同的相对路径：

`assets/{namespace}/guidebook/main/i18n/zh_cn/index.md`

## 本地化

指南页面会使用当前客户端语言。翻译后的 Markdown 文件放在：

`assets/{namespace}/{guide_folder}/i18n/{language}/`

翻译页面需要和默认页面使用相同的相对路径。如果某个翻译页面不存在，Fabric Guidebook 会回退到默认页面。

物品名和 UI 文本仍然使用普通 Minecraft 语言文件：

`assets/{namespace}/lang/zh_cn.json`

开局指南书的名称由 Fabric Guidebook 格式化，并使用指南 namespace 对应的 mod 名称：

```json
{
  "item.fabric_guidebook.starter_guide": "%s 指南"
}
```

如果某个指南需要完全自定义开局书名，可以在指南定义里添加 `title_key`，然后在 lang 文件中提供对应翻译。

## 指南物品

通用指南物品通过 NBT 保存目标 guide id：

```mcfunction
/give @p fabric_guidebook:guide{GuideId:"example:main"}
```

## 开局发书

在服务器端，Fabric Guidebook 会扫描所有已加载 mod 的指南定义：

`assets/{namespace}/guidebook_guides/{guide_id}.json`

玩家进服时，每个启用了 `starter_item` 的指南都会发一本 `fabric_guidebook:guide`。物品通过 `GuideId` 保存目标指南，所以不同 mod 的指南书相互独立。

服务器会用玩家 tag 记录是否已经发过：

`fabric_guidebook.given.{namespace}.{guide_path}`

例如 `ic2_120:main` 对应：

`fabric_guidebook.given.ic2_120.main`

如果想让服务器再次给某个玩家发这本开局书，移除这个 tag，然后让玩家重新进服：

```mcfunction
/tag @p remove fabric_guidebook.given.ic2_120.main
```

也可以不改发放记录，直接用命令给一本指南：

```mcfunction
/give @p fabric_guidebook:guide{GuideId:"ic2_120:main"}
```

也可以用客户端命令直接打开指南：

```mcfunction
/fabricguidebook open example:main
```

打开指定页面：

```mcfunction
/fabricguidebook open example:main machines/index.md
```

## 物品页面

在页面 frontmatter 中添加 `item_ids`：

```md
---
navigation:
  title: Generator
  icon: example:generator
item_ids:
  - example:generator
---
```

玩家悬停或手持绑定物品时，按住打开指南键即可跳转到对应页面。

## 开发监听

开发时先用常规 `runClient` 任务启动游戏，然后在客户端命令行启用实时指南重载：

```mcfunction
/fabricguidebook dev watch example:main /absolute/path/to/src/main/resources/assets/example/guidebook/main
```

监听目录应该是包含 `index.md` 的指南内容目录，而不是 `assets` 根目录。

当页面 namespace 和 guide id namespace 不一致时，使用 `watchns`：

```mcfunction
/fabricguidebook dev watchns example:main example /absolute/path/to/guidebook/main
```

停止监听：

```mcfunction
/fabricguidebook dev stop example:main
```

Markdown 变更会更新指南页面和物品索引。`.snbt` 结构、图片等资源变更会重新加载当前打开的页面。

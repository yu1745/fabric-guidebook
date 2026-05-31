# Fabric Guidebook

> **[English](README.md)**

一个通用的 Fabric 指南书 Mod，支持通过 Mod 或资源包提供 Markdown 内容。

## 资源定义

在以下路径定义指南：

```text
assets/<namespace>/guidebook_guides/<guide_id>.json
```

示例：

```json
{
  "title": "示例指南",
  "folder": "guidebook/main",
  "landing_page": "index.md"
}
```

页面从以下目录加载：

```text
assets/<namespace>/<folder>/**/*.md
```

以上面示例为例：

```text
assets/<namespace>/guidebook/main/index.md
```

## API

```java
Guide guide = Guide.builder(new Identifier("modid", "guide"))
    .folder("guidebook/main")
    .title("我的指南")
    .landingPage("index.md")
    .build();

Guidebooks.register(guide);
Guidebooks.open(new Identifier("modid", "guide"), "index.md");
Guidebooks.openForItem(stack);
```

内置通用物品：

```text
fabric_guidebook:guide
```

获取绑定了特定指南的物品：

```text
/give @p fabric_guidebook:guide{GuideId:"modid:guide"}
```

## 快速上手

本 Mod 自带了一本指南书。在游戏中打开它：

1. 执行 `/give @p fabric_guidebook:guide{GuideId:"fabric_guidebook:main"}` 获取物品
2. 按 **G** 键（默认键位，可在控制选项中修改）打开

## 实际案例

[IC2 Fabric Mod](https://github.com/yu1745/ic2-fabric) 是一个在生产环境中集成 fabric-guidebook 的参考项目。

## 许可声明

本项目包含从 Applied Energistics 2 迁移的指南书和 Markdown 解析代码（`appeng.client.guidebook` 迁移至 `appengx.client.guidebook`，`appeng.libs` 迁移至 `appengx.libs`）。详见 `NOTICE` 和 `LICENSE-LGPL-3.0`。

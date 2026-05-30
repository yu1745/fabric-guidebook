---
navigation:
  title: 3D Scene
  parent: index.md
  position: 20
  icon: minecraft:stone
item_ids:
  - minecraft:stone
---

# 3D Scene

This page renders a structure loaded from SNBT.

<GameScene zoom={1.2} interactive={true} fullWidth={true}>
  <ImportStructure src="scene_stone.snbt" />
  <IsometricCamera yaw={35} pitch={25} />
</GameScene>

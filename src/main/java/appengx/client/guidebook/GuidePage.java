package appengx.client.guidebook;

import net.minecraft.resources.ResourceLocation;

import appengx.client.guidebook.document.block.LytDocument;

public record GuidePage(String sourcePack, ResourceLocation id, LytDocument document) {
}

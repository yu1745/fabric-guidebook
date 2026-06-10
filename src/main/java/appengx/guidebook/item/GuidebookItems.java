package appengx.guidebook.item;

import appengx.guidebook.FabricGuidebook;
import appengx.guidebook.GuideStarterBooks;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

public final class GuidebookItems {
    public static final Item GUIDE = new GuideBookItem(new FabricItemSettings().maxCount(1));

    private GuidebookItems() {
    }

    public static void register() {
        Registry.register(BuiltInRegistries.ITEM, FabricGuidebook.id("guide"), GUIDE);
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
                .register(GuideStarterBooks::addToCreativeTab);
    }
}

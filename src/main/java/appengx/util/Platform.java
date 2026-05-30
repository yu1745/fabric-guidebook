package appengx.util;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.crafting.RecipeManager;

public final class Platform {
    public static RecipeManager fallbackClientRecipeManager;
    public static RegistryAccess fallbackClientRegistryAccess;

    private Platform() {
    }

    public static RecipeManager getClientRecipeManager() {
        var connection = net.minecraft.client.Minecraft.getInstance().getConnection();
        if (connection != null) {
            return connection.getRecipeManager();
        }
        return fallbackClientRecipeManager;
    }

    public static RegistryAccess getClientRegistryAccess() {
        ClientPacketListener connection = net.minecraft.client.Minecraft.getInstance().getConnection();
        if (connection != null) {
            return connection.registryAccess();
        }
        return fallbackClientRegistryAccess;
    }
}

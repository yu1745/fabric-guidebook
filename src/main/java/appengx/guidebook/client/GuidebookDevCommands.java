package appengx.guidebook.client;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import java.nio.file.Paths;

import com.mojang.brigadier.arguments.StringArgumentType;

import appengx.guidebook.api.Guidebooks;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class GuidebookDevCommands {
    private GuidebookDevCommands() {
    }

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            var root = literal("fabricguidebook");

            var openCommand = literal("open");
            openCommand.then(argument("guide", ResourceLocationArgument.id())
                    .executes(ctx -> open(ctx.getSource(), ctx.getArgument("guide", ResourceLocation.class), null))
                    .then(argument("page", StringArgumentType.greedyString())
                            .executes(ctx -> open(
                                    ctx.getSource(),
                                    ctx.getArgument("guide", ResourceLocation.class),
                                    StringArgumentType.getString(ctx, "page")))));
            root.then(openCommand);

            var devCommand = literal("dev");
            devCommand.then(literal("watch")
                    .then(argument("guide", ResourceLocationArgument.id())
                            .then(argument("sources", StringArgumentType.greedyString())
                                    .executes(ctx -> watch(
                                            ctx.getSource(),
                                            ctx.getArgument("guide", ResourceLocation.class),
                                            null,
                                            StringArgumentType.getString(ctx, "sources"))))));
            devCommand.then(literal("watchns")
                    .then(argument("guide", ResourceLocationArgument.id())
                            .then(argument("namespace", StringArgumentType.word())
                                    .then(argument("sources", StringArgumentType.greedyString())
                                            .executes(ctx -> watch(
                                                    ctx.getSource(),
                                                    ctx.getArgument("guide", ResourceLocation.class),
                                                    StringArgumentType.getString(ctx, "namespace"),
                                                    StringArgumentType.getString(ctx, "sources")))))));
            devCommand.then(literal("stop")
                    .then(argument("guide", ResourceLocationArgument.id())
                            .executes(ctx -> stop(ctx.getSource(), ctx.getArgument("guide", ResourceLocation.class)))));
            root.then(devCommand);

            dispatcher.register(root);
        });
    }

    private static int open(FabricClientCommandSource source, ResourceLocation guideId, String pageText) {
        var guide = Guidebooks.get(guideId).orElse(null);
        if (guide == null) {
            source.sendError(Component.literal("Unknown guide: " + guideId));
            return 0;
        }

        var page = pageText != null ? pageText : guide.landingPage();
        Guidebooks.open(guideId, page);
        source.sendFeedback(Component.literal("Opening guide " + guideId + " page " + page));
        return 1;
    }

    private static int watch(FabricClientCommandSource source,
            ResourceLocation guideId,
            String namespaceText,
            String sourcesText) {
        var guide = Guidebooks.get(guideId).orElse(null);
        if (guide == null) {
            source.sendError(Component.literal("Unknown guide: " + guideId));
            return 0;
        }

        var namespace = namespaceText != null ? namespaceText : guideId.getNamespace();
        if (!ResourceLocation.isValidResourceLocation(namespace + ":dummy")) {
            source.sendError(Component.literal("Invalid namespace: " + namespace));
            return 0;
        }

        try {
            var sources = Paths.get(sourcesText).toAbsolutePath().normalize();
            guide.ae2Guide().enableDevelopmentSources(sources, namespace);
            source.sendFeedback(Component.literal("Watching guide sources for " + guideId + " in " + sources));
            return 1;
        } catch (RuntimeException e) {
            source.sendError(Component.literal("Failed to watch guide sources: " + e.getMessage()));
            return 0;
        }
    }

    private static int stop(FabricClientCommandSource source, ResourceLocation guideId) {
        var guide = Guidebooks.get(guideId).orElse(null);
        if (guide == null) {
            source.sendError(Component.literal("Unknown guide: " + guideId));
            return 0;
        }

        guide.ae2Guide().disableDevelopmentSources();
        source.sendFeedback(Component.literal("Stopped watching guide sources for " + guideId));
        return 1;
    }
}

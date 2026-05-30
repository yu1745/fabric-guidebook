package appengx.core.localization;

import net.minecraft.network.chat.Component;

public interface LocalizationEnum {
    String getTranslationKey();

    String getEnglishText();

    default Component text(Object... args) {
        return Component.translatableWithFallback(getTranslationKey(), getEnglishText(), args);
    }
}

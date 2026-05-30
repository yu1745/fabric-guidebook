package appengx.core;

public final class AEConfig {
    private static final AEConfig INSTANCE = new AEConfig();

    private AEConfig() {
    }

    public static AEConfig instance() {
        return INSTANCE;
    }

    public boolean isShowDebugGuiOverlays() {
        return false;
    }

    public boolean isGuideHotkeyEnabled() {
        return false;
    }

    public long getCrystalResonanceGeneratorRate() {
        return 0;
    }
}

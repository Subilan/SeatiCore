package cc.seati.fabric.PlayerTracer;

import net.fabricmc.api.ModInitializer;

import cc.seati.PlayerTracer.Main;

public final class MainFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        Main.init();
    }
}

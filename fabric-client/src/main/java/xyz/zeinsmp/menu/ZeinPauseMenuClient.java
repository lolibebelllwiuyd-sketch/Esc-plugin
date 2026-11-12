package xyz.zeinsmp.menu;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;

public final class ZeinPauseMenuClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.screen instanceof PauseScreen
                    && !(client.screen instanceof ZeinPauseScreen)) {
                client.setScreen(new ZeinPauseScreen());
            }
        });
    }
}

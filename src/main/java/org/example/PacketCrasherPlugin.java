package org.example;

import org.rusherhack.client.api.plugin.Plugin;
import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.core.logging.ILogger;

public class PacketCrasherPlugin extends Plugin {
    @Override
    public void onLoad() {
        getLogger().info("Rusher Utils plugin loaded!");
        RusherHackAPI.getModuleManager().registerFeature(new HighJump());
    }

    @Override
    public void onUnload() {
        getLogger().info("Rusher Utils plugin unloaded!");
    }
}

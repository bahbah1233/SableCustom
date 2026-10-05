package com.bahbah.shipautopilot.event;

import com.bahbah.shipautopilot.core.AutopilotPhysicsController;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod.EventBusSubscriber(modid = "shipautopilot", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.DEDICATED_SERVER)
public class ServerTickHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger("shipautopilot");

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        // Update all active autopilots once per server tick
        AutopilotPhysicsController.updateAllAutopilots();
    }
}

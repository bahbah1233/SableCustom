package com.bahbah.shipautopilot.event;

import com.bahbah.shipautopilot.core.AutopilotPhysicsController;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EventBusSubscriber(modid = "shipautopilot", bus = EventBusSubscriber.Bus.GAME, value = Dist.DEDICATED_SERVER)
public class ServerTickHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger("shipautopilot");

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        // Update all active autopilots once per server tick
        AutopilotPhysicsController.updateAllAutopilots();
    }
}

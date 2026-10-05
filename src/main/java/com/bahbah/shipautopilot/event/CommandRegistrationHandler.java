package com.bahbah.shipautopilot.event;

import com.bahbah.shipautopilot.command.ShipAutopilotCommand;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod.EventBusSubscriber(modid = "shipautopilot", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.DEDICATED_SERVER)
public class CommandRegistrationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger("shipautopilot");

    @SubscribeEvent
    public static void onCommandsRegister(RegisterCommandsEvent event) {
        LOGGER.info("Registering Ship Autopilot commands");
        ShipAutopilotCommand.register(event.getDispatcher());
    }
}

package com.bahbah.shipautopilot.event;

import com.bahbah.shipautopilot.command.ShipAutopilotCommand;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EventBusSubscriber(modid = "shipautopilot", bus = EventBusSubscriber.Bus.GAME, value = Dist.DEDICATED_SERVER)
public class CommandRegistrationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger("shipautopilot");

    @SubscribeEvent
    public static void onCommandsRegister(RegisterCommandsEvent event) {
        LOGGER.info("Registering Ship Autopilot commands");
        ShipAutopilotCommand.register(event.getDispatcher());
    }
}

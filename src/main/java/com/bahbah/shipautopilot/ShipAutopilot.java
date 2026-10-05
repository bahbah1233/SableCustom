package com.bahbah.shipautopilot;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(ShipAutopilot.MODID)
public class ShipAutopilot {
    public static final String MODID = "shipautopilot";
    private static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public ShipAutopilot(IEventBus modEventBus, ModLoadingContext context) {
        LOGGER.info("Initializing Ship Autopilot mod");
        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.register(this);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("Ship Autopilot common setup complete");
    }
}

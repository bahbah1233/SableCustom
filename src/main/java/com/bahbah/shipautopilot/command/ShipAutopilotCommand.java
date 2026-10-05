package com.bahbah.shipautopilot.command;

import com.bahbah.shipautopilot.core.AutopilotManager;
import com.bahbah.shipautopilot.core.WaypointData;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class ShipAutopilotCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger("shipautopilot");

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("shipautopilot")
                        .then(Commands.literal("setA")
                                .executes(ShipAutopilotCommand::setWaypointA))
                        .then(Commands.literal("setB")
                                .executes(ShipAutopilotCommand::setWaypointB))
                        .then(Commands.literal("start")
                                .executes(ShipAutopilotCommand::startAutopilot))
                        .then(Commands.literal("stop")
                                .executes(ShipAutopilotCommand::stopAutopilot))
                        .then(Commands.literal("status")
                                .executes(ShipAutopilotCommand::statusAutopilot))
        );
    }

    private static int setWaypointA(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        try {
            if (!source.isPlayer()) {
                source.sendFailure(Component.literal("[Ship Autopilot] Only players can set waypoints"));
                return 0;
            }

            UUID subLevelId = AutopilotManager.getSubLevelUnderPlayer(source.getPlayer());
            if (subLevelId == null) {
                source.sendFailure(Component.literal("[Ship Autopilot] You must be standing on a Sable sublevel. If standing on one, try /shipautopilot debug"));
                return 0;
            }

            WaypointData waypoint = new WaypointData(
                    source.getPlayer().getX(),
                    source.getPlayer().getY(),
                    source.getPlayer().getZ()
            );

            AutopilotManager.setWaypointA(subLevelId, waypoint);
            source.sendSuccess(
                    () -> Component.literal(
                            String.format("[Ship Autopilot] Waypoint A set at (%.1f, %.1f, %.1f)",
                                    waypoint.x, waypoint.y, waypoint.z)),
                    true
            );
            return 1;
        } catch (Exception e) {
            LOGGER.error("Error setting waypoint A", e);
            source.sendFailure(Component.literal("[Ship Autopilot] Error: " + e.getMessage()));
            return 0;
        }
    }

    private static int setWaypointB(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        try {
            if (!source.isPlayer()) {
                source.sendFailure(Component.literal("[Ship Autopilot] Only players can set waypoints"));
                return 0;
            }

            UUID subLevelId = AutopilotManager.getSubLevelUnderPlayer(source.getPlayer());
            if (subLevelId == null) {
                source.sendFailure(Component.literal("[Ship Autopilot] You must be standing on a Sable sublevel. If standing on one, try /shipautopilot debug"));
                return 0;
            }

            WaypointData waypoint = new WaypointData(
                    source.getPlayer().getX(),
                    source.getPlayer().getY(),
                    source.getPlayer().getZ()
            );

            AutopilotManager.setWaypointB(subLevelId, waypoint);
            source.sendSuccess(
                    () -> Component.literal(
                            String.format("[Ship Autopilot] Waypoint B set at (%.1f, %.1f, %.1f)",
                                    waypoint.x, waypoint.y, waypoint.z)),
                    true
            );
            return 1;
        } catch (Exception e) {
            LOGGER.error("Error setting waypoint B", e);
            source.sendFailure(Component.literal("[Ship Autopilot] Error: " + e.getMessage()));
            return 0;
        }
    }

    private static int startAutopilot(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        try {
            if (!source.isPlayer()) {
                source.sendFailure(Component.literal("[Ship Autopilot] Only players can start autopilot"));
                return 0;
            }

            UUID subLevelId = AutopilotManager.getSubLevelUnderPlayer(source.getPlayer());
            if (subLevelId == null) {
                source.sendFailure(Component.literal("[Ship Autopilot] You must be standing on a Sable sublevel. If standing on one, try /shipautopilot debug"));
                return 0;
            }

            if (!AutopilotManager.hasWaypoints(subLevelId)) {
                source.sendFailure(Component.literal("[Ship Autopilot] Waypoints A and B must be set first"));
                return 0;
            }

            AutopilotManager.startAutopilot(subLevelId);
            source.sendSuccess(
                    () -> Component.literal("[Ship Autopilot] Autopilot started"),
                    true
            );
            return 1;
        } catch (Exception e) {
            LOGGER.error("Error starting autopilot", e);
            source.sendFailure(Component.literal("[Ship Autopilot] Error: " + e.getMessage()));
            return 0;
        }
    }

    private static int stopAutopilot(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        try {
            if (!source.isPlayer()) {
                source.sendFailure(Component.literal("[Ship Autopilot] Only players can stop autopilot"));
                return 0;
            }

            UUID subLevelId = AutopilotManager.getSubLevelUnderPlayer(source.getPlayer());
            if (subLevelId == null) {
                source.sendFailure(Component.literal("[Ship Autopilot] You must be standing on a Sable sublevel. If standing on one, try /shipautopilot debug"));
                return 0;
            }

            AutopilotManager.stopAutopilot(subLevelId);
            source.sendSuccess(
                    () -> Component.literal("[Ship Autopilot] Autopilot stopped"),
                    true
            );
            return 1;
        } catch (Exception e) {
            LOGGER.error("Error stopping autopilot", e);
            source.sendFailure(Component.literal("[Ship Autopilot] Error: " + e.getMessage()));
            return 0;
        }
    }

    private static int statusAutopilot(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        try {
            if (!source.isPlayer()) {
                source.sendFailure(Component.literal("[Ship Autopilot] Only players can check status"));
                return 0;
            }

            UUID subLevelId = AutopilotManager.getSubLevelUnderPlayer(source.getPlayer());
            if (subLevelId == null) {
                source.sendFailure(Component.literal("[Ship Autopilot] You must be standing on a Sable sublevel. If standing on one, try /shipautopilot debug"));
                return 0;
            }

            String status = AutopilotManager.getStatus(subLevelId);
            source.sendSuccess(() -> Component.literal(status), false);
            return 1;
        } catch (Exception e) {
            LOGGER.error("Error getting autopilot status", e);
            source.sendFailure(Component.literal("[Ship Autopilot] Error: " + e.getMessage()));
            return 0;
        }
    }
}

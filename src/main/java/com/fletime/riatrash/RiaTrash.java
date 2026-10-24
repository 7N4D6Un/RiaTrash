package com.fletime.riatrash;

import java.util.Locale;
import java.util.Map;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class RiaTrash implements ClientModInitializer {
	public static final String MOD_ID = "riatrash";
	private static final Map<String, String> SERVER_COMMANDS = Map.of(
			"auto-zth.ria.red", "dispose",
			"zth.ria.red", "dispose",
			"zth.spare.ria.red", "dispose",
			"124.222.156.11", "dispose",
			"mc.hammer.moe", "trash");

	private static final Identifier TRASH_SPRITE = Identifier.fromNamespaceAndPath(MOD_ID, "trash");
	private static final int BUTTON_SIZE = 16;
	private static final int BUTTON_OFFSET_X = 77;
	private static final int BUTTON_OFFSET_Y = 43;
	private static final int INVENTORY_WIDTH = 176;
	private static final int INVENTORY_HEIGHT = 166;

	@Override
	public void onInitializeClient() {
		ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			if (!(screen instanceof InventoryScreen inventoryScreen)) {
				return;
			}
			String command = getTrashCommand(client);
			if (command == null) {
				return;
			}
			int leftPos = (inventoryScreen.width - INVENTORY_WIDTH) / 2;
			int topPos = (inventoryScreen.height - INVENTORY_HEIGHT) / 2;
			ImageButton trashButton = new ImageButton(
					leftPos + BUTTON_OFFSET_X,
					topPos + BUTTON_OFFSET_Y,
					BUTTON_SIZE,
					BUTTON_SIZE,
					new WidgetSprites(TRASH_SPRITE),
					button -> client.getConnection().sendCommand(command),
					Component.translatable("gui.riatrash.trash_button"));
			trashButton.setTooltip(Tooltip.create(Component.translatable("gui.riatrash.trash_button")));
			Screens.getWidgets(inventoryScreen).add(trashButton);
		});
	}

	private static String getTrashCommand(Minecraft client) {
		if (client.getCurrentServer() == null) {
			return null;
		}
		String address = client.getCurrentServer().ip;
		if (address == null) {
			return null;
		}
		int portIndex = address.indexOf(':');
		if (portIndex != -1) {
			address = address.substring(0, portIndex);
		}
		return SERVER_COMMANDS.get(address.toLowerCase(Locale.ROOT));
	}
}

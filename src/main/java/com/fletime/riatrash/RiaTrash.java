package com.fletime.riatrash;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class RiaTrash implements ClientModInitializer {
	public static final String MOD_ID = "riatrash";

	private static final Map<String, String> DEFAULT_SERVERS = createDefaultServers();
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private static Map<String, String> serverCommands = DEFAULT_SERVERS;

	private static final Identifier TRASH_SPRITE = Identifier.fromNamespaceAndPath(MOD_ID, "trash");
	private static final int BUTTON_SIZE = 16;
	private static final int BUTTON_OFFSET_X = 77;
	private static final int BUTTON_OFFSET_Y = 43;
	private static final int INVENTORY_WIDTH = 176;
	private static final int INVENTORY_HEIGHT = 166;

	@Override
	public void onInitializeClient() {
		serverCommands = loadServers();
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

	private static Map<String, String> createDefaultServers() {
		Map<String, String> servers = new LinkedHashMap<>();
		servers.put("auto-zth.ria.red", "dispose");
		servers.put("zth.ria.red", "dispose");
		servers.put("zth.spare.ria.red", "dispose");
		servers.put("124.222.156.11", "dispose");
		servers.put("mc.hammer.moe", "trash");
		return servers;
	}

	private static Map<String, String> loadServers() {
		Path path = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID + ".json");
		if (Files.exists(path)) {
			try {
				JsonObject servers = JsonParser.parseString(Files.readString(path)).getAsJsonObject();
				Map<String, String> result = new LinkedHashMap<>();
				for (String key : servers.keySet()) {
					result.put(key, servers.get(key).getAsString());
				}
				return result;
			} catch (IOException | RuntimeException ignored) {
			}
		}
		writeDefaultConfig(path);
		return DEFAULT_SERVERS;
	}

	private static void writeDefaultConfig(Path path) {
		JsonObject servers = new JsonObject();
		DEFAULT_SERVERS.forEach(servers::addProperty);
		try {
			Files.createDirectories(path.getParent());
			Files.writeString(path, GSON.toJson(servers));
		} catch (IOException ignored) {
		}
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
		return serverCommands.get(address.toLowerCase(Locale.ROOT));
	}
}

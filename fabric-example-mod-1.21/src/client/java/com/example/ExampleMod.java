package com.example;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.Minecraft;

public class ExampleMod implements ClientModInitializer {

	private static final String WEBHOOK_URL = "discord webhook here";

	@Override
	public void onInitializeClient() {

		ClientReceiveMessageEvents.CHAT.register((message, signedMessage, sender, params, timestamp) -> {
			handleIncoming(message, "PLAYER_CHAT");
		});

		ClientSendMessageEvents.COMMAND.register(message -> {
			if (message != null && !message.trim().isEmpty()) {
				handleSentCommand(message.trim());
			}
		});
	}
	private String getPlayerName() {
		Minecraft client = Minecraft.getInstance();
		if (client.player != null) {
			return client.player.getName().getString();
		}
		return "UnknownPlayer";
	}

	private void handleSentCommand(String command) {
		Minecraft client = Minecraft.getInstance();

		String serverInfo = client.getCurrentServer() != null
				? client.getCurrentServer().ip
				: "Singleplayer";

		String playerName = getPlayerName();

		String log = "[" + serverInfo + "] [" + playerName + "] [COMMAND] " + command;

		new Thread(() -> sendToDiscord(command, serverInfo, "COMMAND", playerName)).start();
	}
	private void handleIncoming(Object rawMessage, String type) {
		try {
			String text;
			try {
				text = (String) rawMessage.getClass().getMethod("getString").invoke(rawMessage);
			} catch (Exception e) {
				text = rawMessage.toString();
			}

			text = text.trim();
			if (text.isEmpty() || text.equals("null")) return;

			Minecraft client = Minecraft.getInstance();

			String serverInfo = client.getCurrentServer() != null
					? client.getCurrentServer().ip
					: "Singleplayer";

			String playerName = getPlayerName();

			String log = "[" + serverInfo + "] [" + playerName + "] [" + type + "] " + text;
			System.out.println("[ChatLogger] " + log);

			final String finalText = text;
			final String finalServer = serverInfo;
			final String finalType = type;
			final String finalPlayer = playerName;

			new Thread(() -> sendToDiscord(finalText, finalServer, finalType, finalPlayer)).start();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void sendToDiscord(String message, String server, String type, String playerName) {
		try {

			java.net.URL url = new java.net.URL(WEBHOOK_URL);
			java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("User-Agent", "Minecraft-ChatLogger/1.0");
			conn.setDoOutput(true);
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);

			String json = "{\"content\": \"["
					+ server.replace("\"", "\\\"") + "] ["
					+ playerName.replace("\"", "\\\"") + "] ["
					+ type + "] "
					+ message.replace("\"", "\\\"").replace("\n", "\\n") + "\"}";

			try (java.io.OutputStream os = conn.getOutputStream()) {
				os.write(json.getBytes(java.nio.charset.StandardCharsets.UTF_8));
			}

			int code = conn.getResponseCode();

			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
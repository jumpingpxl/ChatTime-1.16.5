package de.jumpingpxl.labymod.chattime.util;

import com.google.gson.JsonObject;
import de.jumpingpxl.labymod.chattime.ChatTime;
import net.labymod.settings.elements.BooleanElement;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.HeaderElement;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.settings.elements.StringElement;
import net.labymod.utils.Material;

import java.util.List;

public class Settings {

	private final ChatTime chatTime;

	private String chatTimeFormat = "HH:mm:ss";
	private String chatTimeStyle = "&7[&6%time%&7] &r";
	private boolean enabledChatTime = true;
	private boolean beforeMessage = true;

	public Settings(ChatTime chatTime) {
		this.chatTime = chatTime;
	}

	public void loadConfig() {
		chatTimeFormat = getConfig().has("format") ? getConfig().get("format").getAsString()
				: chatTimeFormat;
		chatTimeStyle = getConfig().has("prefix") ? getConfig().get("prefix").getAsString()
				: chatTimeStyle;
		enabledChatTime = !getConfig().has("enabled") || getConfig().get("enabled").getAsBoolean();
		beforeMessage = !getConfig().has("before") || getConfig().get("before").getAsBoolean();
	}

	public void fillSettings(List<SettingsElement> settingsElements) {
		settingsElements.add(new HeaderElement("§eChatTime v" + chatTime.getVersion()));
		settingsElements.add(new HeaderElement("§6General"));
		settingsElements.add(
				new BooleanElement("§6Enabled", new ControlElement.IconData(Material.LEVER), enabled -> {
					enabledChatTime = enabled;
					getConfig().addProperty("enabled", enabled);
					saveConfig();
				}, enabledChatTime));
		settingsElements.add(
				new StringElement("§6Style", new ControlElement.IconData(Material.MAP), chatTimeStyle,
						string -> {
							chatTimeStyle = string;
							getConfig().addProperty("prefix", string);
							saveConfig();
						}));
		settingsElements.add(
				new StringElement("§6Time-Formatting", new ControlElement.IconData(Material.MAP),
						chatTimeFormat, string -> {
					chatTimeFormat = string;
					getConfig().addProperty("format", string);
					saveConfig();
				}));
		settingsElements.add(
				new BooleanElement("§6Before Message", new ControlElement.IconData(Material.LEVER),
						enabled -> {
							beforeMessage = enabled;
							getConfig().addProperty("before", enabled);
							saveConfig();
						}, beforeMessage));
	}

	public String getChatTimeFormat() {
		return chatTimeFormat;
	}

	public String getChatTimeStyle() {
		return chatTimeStyle;
	}

	public boolean isEnabledChatTime() {
		return enabledChatTime;
	}

	public boolean isBeforeMessage() {
		return beforeMessage;
	}

	private JsonObject getConfig() {
		return chatTime.getConfig();
	}

	private void saveConfig() {
		chatTime.saveConfig();
	}
}

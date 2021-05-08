package de.jumpingpxl.labymod.chattime.listener;

import de.jumpingpxl.labymod.chattime.util.Settings;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.events.client.chat.MessageModifyEvent;
import net.minecraft.util.text.StringTextComponent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class MessageModifyListener {

	private final Settings settings;

	public MessageModifyListener(Settings settings) {
		this.settings = settings;
	}

	@Subscribe
	public void onMessageModify(MessageModifyEvent event) {
		if (!settings.isEnabledChatTime()) {
			return;
		}

		String time = "";
		try {
			time = new SimpleDateFormat(stripColor('&', settings.getChatTimeFormat())).format(
					new Date(System.currentTimeMillis()));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}

		StringTextComponent textComponent = new StringTextComponent(
				translateColorCodes(settings.getChatTimeStyle()).replace("%time%", time));
		if (settings.isBeforeMessage()) {
			event.setComponent(textComponent.append(event.getComponent()));
		} else {
			event.setComponent(new StringTextComponent("").append(event.getComponent())
					.append(textComponent));
		}
	}

	private String translateColorCodes(String textToTranslate) {
		char[] array = textToTranslate.toCharArray();
		for (int i = 0; i < array.length - 1; i++) {
			if (array[i] == '&' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(array[i + 1]) > -1) {
				array[i] = "§".toCharArray()[0];
				array[i + 1] = Character.toLowerCase(array[i + 1]);
			}
		}
		return new String(array);
	}

	private String stripColor(char colorChar, String input) {
		if (input == null) {
			return null;
		}
		return Pattern.compile("(?i)" + colorChar + "[0-9A-FK-OR]").matcher(input).replaceAll("");
	}
}

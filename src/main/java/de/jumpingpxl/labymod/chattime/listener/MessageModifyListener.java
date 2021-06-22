package de.jumpingpxl.labymod.chattime.listener;

import de.jumpingpxl.labymod.chattime.util.Settings;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.events.client.chat.MessageModifyEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

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

		StringTextComponent textComponent = (StringTextComponent) settings.getStyle().deepCopy();
		for (int i = 0; i < textComponent.getSiblings().size(); i++) {
			ITextComponent sibling = textComponent.getSiblings().get(i);
			if (sibling.getString().contains("%time%")) {
				StringTextComponent newSibling = new StringTextComponent(
						sibling.getString().replace("%time%", getTime()));
				newSibling.setStyle(sibling.getStyle());
				textComponent.getSiblings().remove(sibling);
				textComponent.getSiblings().add(i, newSibling);
			}
		}

		if (settings.isBeforeMessage()) {
			event.setComponent(textComponent.append(event.getComponent()));
		} else {
			event.setComponent(new StringTextComponent("").append(event.getComponent())
					.append(textComponent));
		}
	}

	private String getTime() {
		try {
			return settings.getDateFormat().format(System.currentTimeMillis());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return "§cERROR";
		}
	}
}

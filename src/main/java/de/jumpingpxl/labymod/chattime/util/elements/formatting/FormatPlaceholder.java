package de.jumpingpxl.labymod.chattime.util.elements.formatting;

import de.jumpingpxl.labymod.chattime.util.elements.PlaceholderButton;
import de.jumpingpxl.labymod.chattime.util.elements.TextField;
import net.minecraft.client.gui.FontRenderer;

import java.text.SimpleDateFormat;

public class FormatPlaceholder {

	private final SimpleDateFormat dateFormat;
	private final String format;
	private final String description;

	public FormatPlaceholder(String format, String description) {
		this.format = format;
		this.description = description;

		dateFormat = new SimpleDateFormat(format);
	}

	public String getFormat() {
		return format;
	}

	public String getDescription() {
		return description;
	}

	public PlaceholderButton createButton(FontRenderer fontRenderer, TextField textField, int x,
	                                      int y) {
		PlaceholderButton placeholderButton = new PlaceholderButton(fontRenderer, format, x, y,
				textField::writeText);
		placeholderButton.setOnHover(
				string -> new String[]{"§6§lWhat Does §c§l" + format + " §6§lDisplay?",
						" §7» §e" + description, " ", "§6§lCurrent State:", " §7» §a" + dateFormat.format(
						System.currentTimeMillis())});
		return placeholderButton;
	}
}

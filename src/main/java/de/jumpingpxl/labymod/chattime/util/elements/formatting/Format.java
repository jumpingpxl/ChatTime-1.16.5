package de.jumpingpxl.labymod.chattime.util.elements.formatting;

import com.google.common.collect.Maps;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public enum Format {
	HOUR_MINUTE_SECOND("Default", "HH:mm:ss", false),
	HOUR_MINUTE_SECOND_A("American", "hh:mm:ss a", false),
	CUSTOM("Custom", null, true);

	public static final Format[] VALUES = values();
	private static final Map<Format, SimpleDateFormat> FORMAT_MAP = Maps.newHashMap();
	private final String name;
	private final String formatting;
	private final boolean highlighted;

	Format(String name, String formatting, boolean highlighted) {
		this.name = name;
		this.formatting = formatting;
		this.highlighted = highlighted;
	}

	public static Optional<Format> getFormatByName(String name) {
		for (Format format : VALUES) {
			if (format.name().equalsIgnoreCase(name)) {
				return Optional.of(format);
			}
		}

		return Optional.empty();
	}

	public static Optional<Format> getFormatByFormatting(String formatting) {
		for (Format format : VALUES) {
			if (Objects.nonNull(format.getFormatting()) && format.getFormatting().equals(formatting)) {
				return Optional.of(format);
			}
		}

		return Optional.empty();
	}

	public String getName() {
		return name;
	}

	public String getFormatting() {
		return formatting;
	}

	public boolean isHighlighted() {
		return highlighted;
	}

	public SimpleDateFormat getDateFormat() {
		return FORMAT_MAP.computeIfAbsent(this, absent -> new SimpleDateFormat(formatting));
	}
}

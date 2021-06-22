package de.jumpingpxl.labymod.chattime.util.elements.formatting;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.labymod.main.LabyMod;
import net.labymod.utils.Consumer;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Objects;

public class FormatButton {

	private static final DrawUtils DRAW_UTILS = LabyMod.getInstance().getDrawUtils();
	private static final int BACKGROUND_COLOR = ModColor.toRGB(50, 50, 50, 160);
	private static final int BACKGROUND_COLOR_HOVER = ModColor.toRGB(100, 100, 100, 160);
	private static final int BACKGROUND_COLOR_SELECTED = ModColor.toRGB(0, 180, 0, 160);
	private static final int BACKGROUND_COLOR_SELECTED_HOVER = ModColor.toRGB(0, 255, 0, 160);

	private final Consumer<Format> onClick;
	private final Format format;
	private final int x;
	private final int y;
	private final int maxX;
	private final int maxY;
	private final int width;
	private final int height;
	private SimpleDateFormat customDateFormat;
	private boolean mouseOver;
	private boolean selected;

	protected FormatButton(Format format, int x, int y, int width, int height,
	                       Consumer<Format> onClick) {
		this.format = format;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.onClick = onClick;

		maxX = x + width;
		maxY = y + height;
	}

	public void render(MatrixStack matrixStack, int mouseX, int mouseY) {
		mouseOver = mouseX >= x && mouseX <= maxX && mouseY >= y && mouseY <= maxY;

		int color = selected ? BACKGROUND_COLOR_SELECTED : BACKGROUND_COLOR;
		if (mouseOver) {
			color = selected ? BACKGROUND_COLOR_SELECTED_HOVER : BACKGROUND_COLOR_HOVER;
			DRAW_UTILS.drawRectBorder(matrixStack, x - 0.5D, y - 0.5D, maxX + 0.5D, maxY + 0.5D,
					Color.WHITE.getRGB(), 0.5D);
		}

		DRAW_UTILS.drawRect(matrixStack, x, y, maxX, maxY, color);

		DRAW_UTILS.drawCenteredString(matrixStack, format.getName(), x + width / 2D,
				y + height / 4D - 2, 0.6D);
		DRAW_UTILS.drawCenteredString(matrixStack, getTime(), x + width / 2D, y + height / 2D, 0.8D);
	}

	public void mouseClicked(int button) {
		if (button == 0 && mouseOver) {
			onClick.accept(format);
			if (!selected) {
				selected = true;
			}
		}
	}

	public void setCustomDateFormat(String string) {
		try {
			this.customDateFormat = new SimpleDateFormat(string);
		} catch (IllegalArgumentException e) {
			//ignored
		}
	}

	public Format getFormat() {
		return format;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isMouseOver() {
		return mouseOver;
	}

	private String getTime() {
		SimpleDateFormat dateFormat;
		if (format == Format.CUSTOM && Objects.nonNull(customDateFormat)) {
			dateFormat = customDateFormat;
		} else {
			dateFormat = format.getDateFormat();
		}

		try {
			return dateFormat.format(System.currentTimeMillis());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return "§cERROR";
		}
	}
}

package de.jumpingpxl.labymod.chattime.util.elements;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.labymod.main.LabyMod;
import net.labymod.utils.Consumer;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;
import net.minecraft.client.gui.FontRenderer;

import java.awt.*;
import java.util.function.Function;
import java.util.regex.Pattern;

public class PlaceholderButton {

	private static final DrawUtils DRAW_UTILS = LabyMod.getInstance().getDrawUtils();
	private static final int BACKGROUND_COLOR = ModColor.toRGB(0, 0, 0, 160);
	private static final int BACKGROUND_COLOR_HOVER = ModColor.toRGB(100, 100, 100, 160);

	private final Consumer<String> onClick;
	private final String text;
	private final int x;
	private final int y;
	private final int maxX;
	private final int maxY;
	private final int width;
	private final int height;
	private Function<String, String[]> onHover;
	private boolean mouseOver;

	protected PlaceholderButton(String text, int x, int y, int width, int height,
	                            Consumer<String> onClick) {
		this.text = text;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.onClick = onClick;

		maxX = x + width;
		maxY = y + height;
	}

	public PlaceholderButton(FontRenderer fontRenderer, String text, int x, int y,
	                         Consumer<String> onClick) {
		this(text, x, y, fontRenderer.getStringWidth(text), 10, onClick);
	}

	public void render(MatrixStack matrixStack, int mouseX, int mouseY) {
		mouseOver = mouseX >= x && mouseX <= maxX && mouseY >= y && mouseY <= maxY;
		if (mouseOver) {
			DRAW_UTILS.drawRectBorder(matrixStack, x - 0.5D, y - 0.5D, maxX + 0.5D, maxY + 0.5D,
					Color.WHITE.getRGB(), 0.5D);
		}

		DRAW_UTILS.drawRect(matrixStack, x, y, maxX, maxY,
				mouseOver ? BACKGROUND_COLOR_HOVER : BACKGROUND_COLOR);
		DRAW_UTILS.drawCenteredString(matrixStack, text, x + width / 2D, y + height / 4D, 0.7D);
	}

	public boolean mouseClicked(int button) {
		if (button == 0 && mouseOver) {
			onClick.accept(stripColor(text));
			return true;
		}

		return false;
	}

	public String getText() {
		return text;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getMaxX() {
		return maxX;
	}

	public int getMaxY() {
		return maxY;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public boolean isMouseOver() {
		return mouseOver;
	}

	public Function<String, String[]> getOnHover() {
		return onHover;
	}

	public void setOnHover(Function<String, String[]> onHover) {
		this.onHover = onHover;
	}

	private String stripColor(String input) {
		return Pattern.compile("(?i)§[0-9A-FK-OR]").matcher(input).replaceAll("");
	}
}

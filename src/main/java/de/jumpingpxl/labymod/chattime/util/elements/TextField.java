package de.jumpingpxl.labymod.chattime.util.elements;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.labymod.main.LabyMod;
import net.labymod.utils.Consumer;
import net.labymod.utils.DrawUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.SharedConstants;

import java.awt.*;
import java.util.Objects;
import java.util.function.Function;

public class TextField {

	private static final DrawUtils DRAW_UTILS = LabyMod.getInstance().getDrawUtils();

	private final FontRenderer fontRenderer;
	private final Consumer<String> callback;
	private final int x;
	private final int y;
	private final int maxX;
	private final int maxY;
	private final int width;
	private final int height;
	private Function<Checker, Boolean> checker;
	private String text;
	private boolean mouseOver;
	private boolean focused;
	private int textX;

	private boolean cursorVisible;
	private int cursorIndex;
	private int markerStartIndex;

	private int tick;

	public TextField(FontRenderer fontRenderer, int x, int y, int width, int height,
	                 Consumer<String> callback) {
		this.fontRenderer = fontRenderer;
		this.callback = callback;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

		maxX = x + width;
		maxY = y + height;
		cursorVisible = true;
		cursorIndex = -1;
		markerStartIndex = -1;
	}

	public void render(MatrixStack matrixStack, int mouseX, int mouseY) {
		mouseOver = mouseX > x && mouseX < maxX && mouseY > y && mouseY < maxY;
		DRAW_UTILS.drawRect(matrixStack, x, y, maxX, maxY, Color.BLACK.getRGB());
		DRAW_UTILS.drawRectBorder(matrixStack, x, y, maxX, maxY,
				(focused ? Color.WHITE : Color.LIGHT_GRAY).getRGB(), 1);

		textX = (x + width / 2) - (fontRenderer.getStringWidth(text) / 2);
		int textY = y + height / 2 - 4;
		DRAW_UTILS.drawString(matrixStack, text, textX, textY);

		if (cursorIndex != -1 && cursorVisible && focused) {
			int cursorX = getXByIndex(cursorIndex);
			DRAW_UTILS.drawRect(matrixStack, cursorX, y + 4D, cursorX + 1D, maxY - 4D,
					Color.WHITE.getRGB());
		}

		drawMarker();
	}

	public void mouseClicked(double mouseX, int button) {
		if (markerStartIndex != -1) {
			markerStartIndex = -1;
		}

		if (mouseOver && button == 0) {
			if (!focused) {
				focused = true;
			}

			boolean cursorSet = false;
			int currentX = textX;
			for (int i = 0; i < text.length(); i++) {
				if (!cursorSet) {
					int charWidth = fontRenderer.getStringWidth(String.valueOf(text.charAt(i)));
					if (currentX + charWidth / 2D > mouseX) {
						cursorSet = true;
						setCursorIndex(i);
					} else {
						currentX += charWidth;
					}
				}
			}

			if (!cursorSet) {
				setCursorIndex(text.length());
			}
		} else if (focused) {
			focused = false;
		}
	}

	public void mouseScrolled(int delta) {
		if (focused) {
			if (Screen.hasControlDown()) {
				setCursorIndex(delta > 0 ? text.length() : 0);
			} else {
				moveCursorBy(-delta);
			}

			moveMarkerBy(-delta);
		}
	}

	public void keyPressed(int keyCode) {
		if (Screen.isSelectAll(keyCode)) {
			setCursorIndex(text.length());
			markerStartIndex = 0;
		} else if (Screen.isCopy(keyCode)) {
			if (markerStartIndex != -1) {
				Minecraft.getInstance().keyboardListener.setClipboardString(getMarkedText());
			}
		} else if (Screen.isPaste(keyCode)) {
			writeText(Minecraft.getInstance().keyboardListener.getClipboardString());
		} else if (Screen.isCut(keyCode)) {
			if (markerStartIndex != -1) {
				Minecraft.getInstance().keyboardListener.setClipboardString(getMarkedText());
				delMarkedChars();
			}
		} else {
			switch (keyCode) {
				case 259:
					if (markerStartIndex != -1) {
						delMarkedChars();
					} else {
						delChar(-1);
					}

					break;
				case 261:
					if (markerStartIndex != -1) {
						delMarkedChars();
					} else {
						delChar(1);
					}

					break;
				case 262:
					if (Screen.hasControlDown()) {
						setCursorIndex(text.length());
					} else {
						moveCursorBy(1);
					}

					moveMarkerBy(1);
					break;
				case 263:
					if (Screen.hasControlDown()) {
						setCursorIndex(0);
					} else {
						moveCursorBy(-1);
					}

					moveMarkerBy(-1);
					break;
			}
		}
	}

	public void charTyped(char codePoint) {
		if (focused && SharedConstants.isAllowedCharacter(codePoint)) {
			writeChar(codePoint);
		}
	}

	public void tick() {
		if (tick != 15) {
			tick++;
		} else {
			tick = 1;
			cursorVisible = !cursorVisible;
		}
	}

	public boolean isFocused() {
		return focused;
	}

	public void setFocused(boolean focused) {
		this.focused = focused;
	}

	public void setChecker(Function<Checker, Boolean> checker) {
		this.checker = checker;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;

		if (cursorIndex == -1 || cursorIndex > text.length()) {
			cursorIndex = text.length();
		}
	}

	public int getCursorIndex() {
		return cursorIndex;
	}

	public void setCursorIndex(int index) {
		if (index < 0) {
			this.cursorIndex = 0;
		} else {
			this.cursorIndex = Math.min(index, text.length());
		}

		tick = 5;
		cursorVisible = true;
	}

	public void writeText(String text) {
		for (char character : text.toCharArray()) {
			if (!writeChar(character)) {
				break;
			}
		}
	}

	private void moveCursorBy(int moveBy) {
		setCursorIndex(this.cursorIndex + moveBy);
	}

	private boolean writeChar(char character) {
		StringBuilder stringBuilder = new StringBuilder();
		if (text.length() == 0) {
			stringBuilder.append(character);
		} else {
			if (markerStartIndex != -1) {
				delMarkedChars();
			}

			stringBuilder.append(text, 0, cursorIndex);
			stringBuilder.append(character);
			stringBuilder.append(text.substring(cursorIndex));
		}

		if (fontRenderer.getStringWidth(stringBuilder.toString()) < width - 10 && (Objects.isNull(
				checker) || checker.apply(new Checker(stringBuilder.toString(), character)))) {
			text = stringBuilder.toString();
			callback.accept(text);
			moveCursorBy(1);
			return true;
		}

		return false;
	}

	private int getXByIndex(int index) {
		if (index == text.length()) {
			return textX + fontRenderer.getStringWidth(text);
		}

		int currentX = textX;
		for (int i = 0; i < text.length(); i++) {
			if (i == index) {
				return currentX - 1;
			}

			String character = String.valueOf(text.charAt(i));
			int charWidth = fontRenderer.getStringWidth(character);
			currentX += charWidth;
		}

		return textX;
	}

	private String getMarkedText() {
		int start = Math.min(markerStartIndex, cursorIndex);
		int end = Math.max(markerStartIndex, cursorIndex);
		return text.substring(start, end);
	}

	private void moveMarkerBy(int pos) {
		if (Screen.hasShiftDown()) {
			if (markerStartIndex == -1) {
				markerStartIndex = cursorIndex + (pos < 0 ? 1 : -1);
			}
		} else {
			if (markerStartIndex != -1) {
				markerStartIndex = -1;
			}
		}
	}

	private void delMarkedChars() {
		int start = Math.min(markerStartIndex, cursorIndex);
		int end = Math.max(markerStartIndex, cursorIndex);
		for (int i = start; i < end; i++) {
			delChar(markerStartIndex > cursorIndex ? 1 : -1);
		}

		markerStartIndex = -1;
	}

	private void delChar(int pos) {
		if (text.length() == 0) {
			return;
		}

		try {
			text = text.substring(0, cursorIndex + Math.min(pos, 0)) + text.substring(
					cursorIndex + Math.max(pos, 0));
			callback.accept(text);
			moveCursorBy(pos >= 0 ? 0 : -1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void drawMarker() {
		if (markerStartIndex == -1) {
			return;
		}

		int markerX = getXByIndex(Math.min(markerStartIndex, cursorIndex));
		int markerY = y + 4;
		int markerMaxX = getXByIndex(Math.max(markerStartIndex, cursorIndex));
		int markerMaxY = maxY - 4;

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		RenderSystem.color4f(0.0F, 0.0F, 255.0F, 255.0F);
		RenderSystem.disableTexture();
		RenderSystem.enableColorLogicOp();
		RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
		bufferbuilder.pos(markerX, markerMaxY, 0.0D).endVertex();
		bufferbuilder.pos(markerMaxX, markerMaxY, 0.0D).endVertex();
		bufferbuilder.pos(markerMaxX, markerY, 0.0D).endVertex();
		bufferbuilder.pos(markerX, markerY, 0.0D).endVertex();
		tessellator.draw();
		RenderSystem.disableColorLogicOp();
		RenderSystem.enableTexture();
	}

	public static class Checker {

		private final String text;
		private final char character;

		public Checker(String text, char character) {
			this.text = text;
			this.character = character;
		}

		public String getText() {
			return text;
		}

		public char getCharacter() {
			return character;
		}
	}
}

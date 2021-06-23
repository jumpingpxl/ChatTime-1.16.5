package de.jumpingpxl.labymod.chattime.util.elements;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.labymod.settings.elements.SettingsElement;

public class SpacerElement extends SettingsElement {

	public SpacerElement() {
		super("", null);
	}

	@Override
	public void draw(MatrixStack matrixStack, int x, int y, int maxX, int maxY, int mouseX,
	                 int mouseY) {

	}

	@Override
	public void drawDescription(int i, int i1, int i2) {

	}

	@Override
	public void mouseClicked(int i, int i1, int i2) {

	}

	@Override
	public void mouseRelease(int i, int i1, int i2) {

	}

	@Override
	public void mouseClickMove(int i, int i1, int i2) {

	}

	@Override
	public void charTyped(char c, int i) {

	}

	@Override
	public void unfocus(int i, int i1, int i2) {

	}

	@Override
	public int getEntryHeight() {
		return 10;
	}
}

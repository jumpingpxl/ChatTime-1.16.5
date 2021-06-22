package de.jumpingpxl.labymod.chattime.util.elements;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.labymod.settings.elements.BooleanElement;
import net.labymod.utils.Consumer;

public class BetterBooleanElement extends BooleanElement {

	public BetterBooleanElement(String displayName, IconData iconData,
	                            Consumer<Boolean> toggleListener, boolean currentValue) {
		super(displayName, iconData, toggleListener, currentValue);
	}

	@Override
	public void draw(MatrixStack matrixStack, int x, int y, int maxX, int maxY, int mouseX,
	                 int mouseY) {
		x -= 20;
		maxX += 20;

		super.draw(matrixStack, x, y, maxX, maxY, mouseX, mouseY);
	}
}

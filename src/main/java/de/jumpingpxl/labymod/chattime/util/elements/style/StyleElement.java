package de.jumpingpxl.labymod.chattime.util.elements.style;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.jumpingpxl.labymod.chattime.util.Settings;
import de.jumpingpxl.labymod.chattime.util.elements.BetterElement;
import net.labymod.utils.Consumer;
import net.labymod.utils.Material;

public class StyleElement extends BetterElement<String> {

	private final Settings settings;

	public StyleElement(Settings settings, String displayName, Material material, String startValue,
	                    Consumer<String> toggleListener) {
		super(displayName, material, 156, startValue, toggleListener);

		this.settings = settings;
	}

	@Override
	public void drawPreview(MatrixStack matrixStack, int x, int y, int maxX, int maxY, int mouseX,
	                        int mouseY) {
		super.drawPreview(matrixStack, x, y, maxX, maxY, mouseX, mouseY);
		int centerX = x + (maxX - x) / 2;
		int centerY = y + (maxY - y) / 2 - 4;
		getDrawUtils().drawCenteredString(matrixStack, settings.getFormattedString(getValue()),
				centerX,
				centerY);
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (isMouseOverPreview()) {
			mc.displayGuiScreen(
					new StyleScreen(settings, displayName, mc.currentScreen, getValue(), textComponent -> {
						setValue(textComponent);
						getToggleListener().accept(textComponent);
					}));
		}
	}
}

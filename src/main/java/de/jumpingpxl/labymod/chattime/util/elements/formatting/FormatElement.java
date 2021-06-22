package de.jumpingpxl.labymod.chattime.util.elements.formatting;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.jumpingpxl.labymod.chattime.util.elements.BetterElement;
import net.labymod.utils.Consumer;
import net.labymod.utils.Material;

import java.text.SimpleDateFormat;

public class FormatElement extends BetterElement<String[]> {

	private Format format;
	private String customValue;
	private SimpleDateFormat dateFormat;

	public FormatElement(String displayName, Material material, String[] startValue,
	                     Consumer<String[]> toggleListener) {
		super(displayName, material, 156, startValue, toggleListener);
	}

	@Override
	public void drawPreview(MatrixStack matrixStack, int x, int y, int maxX, int maxY, int mouseX,
	                        int mouseY) {
		super.drawPreview(matrixStack, x, y, maxX, maxY, mouseX, mouseY);
		int centerX = x + (maxX - x) / 2;
		int centerY = y + (maxY - y) / 2 - 2;
		getDrawUtils().drawCenteredString(matrixStack, format.getName(), centerX, centerY - 4D, 0.5D);
		getDrawUtils().drawCenteredString(matrixStack, dateFormat.format(System.currentTimeMillis()),
				centerX, centerY);
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (isMouseOverPreview()) {
			mc.displayGuiScreen(
					new FormatScreen(displayName, mc.currentScreen, new String[]{format.name(), customValue},
							array -> {

								setValue(array);
								getToggleListener().accept(array);
							}));
		}
	}

	@Override
	public void setValue(String[] value) {
		format = Format.getFormatByName(value[0]).orElse(Format.CUSTOM);
		this.customValue = value[1];
		dateFormat = new SimpleDateFormat(
				format == Format.CUSTOM ? customValue : format.getFormatting());
		super.setValue(value);
	}
}

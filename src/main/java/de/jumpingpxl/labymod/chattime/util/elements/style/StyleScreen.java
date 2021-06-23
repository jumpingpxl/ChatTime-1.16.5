package de.jumpingpxl.labymod.chattime.util.elements.style;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.jumpingpxl.labymod.chattime.util.Settings;
import de.jumpingpxl.labymod.chattime.util.elements.OverlayScreen;
import de.jumpingpxl.labymod.chattime.util.elements.PlaceholderButton;
import de.jumpingpxl.labymod.chattime.util.elements.TextField;
import net.labymod.utils.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;

import java.awt.*;
import java.util.Objects;
import java.util.Set;

public class StyleScreen extends OverlayScreen<String> {

	private static final String[] PLACEHOLDERS = {"0123456789", "abcdef", "mnor"};
	private static final String TIME_PLACEHOLDER = "%time%";
	private static final String STYLE_TITLE = "§eChange Style";
	private static final String USEFUL_TITLE = "§eUseful";
	private static final String PREVIEW_TITLE = "§ePreview";

	private final Settings settings;
	private final Set<PlaceholderButton> placeholderButtons;
	private TextField textField;

	public StyleScreen(Settings settings, String title, Screen backgroundScreen, String currentValue,
	                   Consumer<String> callback) {
		super(title, backgroundScreen, currentValue, callback, -105, -50, 105, 100);
		this.settings = settings;

		placeholderButtons = Sets.newHashSet();
	}

	@Override
	public void init(Minecraft minecraft, int width, int height) {
		super.init(minecraft, width, height);
		String text = getValue();
		int cursorIndex = text.length();
		boolean focused = true;
		if (Objects.nonNull(textField)) {
			text = textField.getText();
			cursorIndex = textField.getCursorIndex();
			focused = textField.isFocused();
		}

		textField = new TextField(minecraft.fontRenderer, centerX - 78, y + 25, 156, 20,
				this::setValue);
		textField.setText(text);
		textField.setCursorIndex(cursorIndex);
		textField.setFocused(focused);

		placeholderButtons.clear();
		int placeholderX = 0;
		int placeholderY = y + 43;
		for (String placeholder : PLACEHOLDERS) {
			placeholderY += 12;
			int rowWidth = -2;
			char[] charArray = placeholder.toCharArray();
			for (char character : charArray) {
				rowWidth += minecraft.fontRenderer.getStringWidth("&" + character) + 2;
			}

			if (placeholder.equals(PLACEHOLDERS[PLACEHOLDERS.length - 1])) {
				rowWidth += minecraft.fontRenderer.getStringWidth(TIME_PLACEHOLDER) + 2;
			}

			placeholderX = centerX - rowWidth / 2;
			for (char character : charArray) {
				PlaceholderButton placeholderButton = new PlaceholderButton(minecraft.fontRenderer,
						"§" + character + "&" + character, placeholderX, placeholderY,
						s -> textField.writeText(s));
				placeholderButtons.add(placeholderButton);
				placeholderX += placeholderButton.getWidth() + 2;
			}
		}

		PlaceholderButton placeholderButton = new PlaceholderButton(minecraft.fontRenderer,
				TIME_PLACEHOLDER, placeholderX, placeholderY, s -> textField.writeText(s));
		placeholderButtons.add(placeholderButton);
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		getDrawUtils().drawCenteredString(matrixStack, STYLE_TITLE, centerX, y + 18D, 0.7D);
		textField.render(matrixStack, mouseX, mouseY);

		getDrawUtils().drawCenteredString(matrixStack, USEFUL_TITLE, centerX, y + 48D, 0.7D);
		for (PlaceholderButton placeholder : placeholderButtons) {
			placeholder.render(matrixStack, mouseX, mouseY);
		}

		getDrawUtils().drawCenteredString(matrixStack, PREVIEW_TITLE, centerX, maxY - 57D, 0.7D);
		fill(matrixStack, centerX - 98, maxY - 50, centerX + 98, maxY - 30, BACKGROUND_COLOR);
		getDrawUtils().drawRect(matrixStack, centerX - 98.5D, maxY - 50.5D, centerX + 98.5D,
				maxY - 50D,
				Color.LIGHT_GRAY.getRGB());
		getDrawUtils().drawRect(matrixStack, centerX - 98.5D, maxY - 31D, centerX + 98.5D,
				maxY - 30.5D,
				Color.LIGHT_GRAY.getRGB());
		String preview = settings.getFormattedString(getValue());
		try {
			preview = preview.replace(TIME_PLACEHOLDER,
					settings.getDateFormat().format(System.currentTimeMillis()));
		} catch (Exception e) {
			preview = preview.replace(TIME_PLACEHOLDER, "§cERROR");
			e.printStackTrace();
		}

		getDrawUtils().drawCenteredString(matrixStack, preview, centerX, maxY - 45D, 1.3D);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
		textField.mouseScrolled((int) delta);
		return false;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		boolean clickedPlaceholder = false;
		for (PlaceholderButton placeholder : placeholderButtons) {
			if (placeholder.mouseClicked(button)) {
				clickedPlaceholder = true;
				break;
			}
		}

		if (!clickedPlaceholder) {
			textField.mouseClicked(mouseX, button);
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		textField.keyPressed(keyCode);
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char codePoint, int modifiers) {
		textField.charTyped(codePoint);
		return super.charTyped(codePoint, modifiers);
	}

	@Override
	public void tick() {
		textField.tick();
		super.tick();
	}
}

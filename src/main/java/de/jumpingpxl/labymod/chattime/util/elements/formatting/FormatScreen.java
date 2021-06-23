package de.jumpingpxl.labymod.chattime.util.elements.formatting;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.jumpingpxl.labymod.chattime.util.elements.OverlayScreen;
import de.jumpingpxl.labymod.chattime.util.elements.PlaceholderButton;
import de.jumpingpxl.labymod.chattime.util.elements.TextField;
import net.labymod.utils.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class FormatScreen extends OverlayScreen<String[]> {

	private static final FormatPlaceholder[][] PLACEHOLDERS = {{new FormatPlaceholder("HH",
			"Hour of day (00-23)"), new FormatPlaceholder("hh", "Hour of day (01-12)"),
			new FormatPlaceholder("mm", "Minute in hour (00-59)"), new FormatPlaceholder("ss",
			"Second in minute (00-59)"), new FormatPlaceholder("a", "Time of day (AM or PM)")},
			{new FormatPlaceholder("yyyy", "Year"), new FormatPlaceholder("MM",
					"Month in year as number (01-12)"), new FormatPlaceholder("MMMM",
					"Month in year as name"), new FormatPlaceholder("dd", "Day in month (01-31)"),
					new FormatPlaceholder("EE", "Day in week")}};
	private static final String PRESET_TITLE = "§eFormatting Presets";
	private static final String CUSTOM_FORMAT_TITLE = "§eChange Custom Format";
	private static final String SYNTAX_TITLE = "§eUseful Syntaxes";
	private static final String INFO_TEXT = "?";
	private static final String[] INFO_HOVER_TEXT = {"§6Middle-Click on a Preset to",
			"§6Insert the Format Into the", "§6Custom Format Text Field.", "",
			"§cThe Custom Format Won't", "§cChange Until You Edit Something", "§cInside the Text Field"
			+ "."};

	private final Map<Format, FormatButton> formatButtons;
	private final Set<PlaceholderButton> placeholderButtons;
	private TextField textField;
	private Format currentFormat;
	private String customValue;

	public FormatScreen(String title, Screen backgroundScreen, String[] currentValue,
	                    Consumer<String[]> callback) {
		super(title, backgroundScreen, currentValue, callback, -105, -50, 105, 104);

		formatButtons = Maps.newHashMap();
		placeholderButtons = Sets.newHashSet();
		currentFormat = Format.getFormatByName(currentValue[0]).orElse(Format.CUSTOM);
		customValue = currentValue[1];
	}

	@Override
	public void init(Minecraft minecraft, int width, int height) {
		super.init(minecraft, width, height);
		int buttonX = centerX - 75;
		int buttonY = y + 25;
		formatButtons.clear();
		for (Format format : Format.VALUES) {
			FormatButton button = new FormatButton(format, buttonX, buttonY,
					format.isHighlighted() ? 150 : 73, 15, newFormat -> {
				if (currentFormat != newFormat) {
					formatButtons.get(currentFormat).setSelected(false);
					currentFormat = newFormat;
				}
			});

			if (format == Format.CUSTOM) {
				button.setCustomDateFormat(customValue);
			}

			if (format == currentFormat) {
				button.setSelected(true);
			}

			formatButtons.put(format, button);
			if (buttonX == centerX - 75 && !format.isHighlighted()) {
				buttonX += 77;
			} else {
				buttonY += 19;
				buttonX = centerX - 75;
			}
		}

		String text = customValue;
		int cursorIndex = text.length();
		boolean focused = false;
		if (Objects.nonNull(textField)) {
			text = textField.getText();
			cursorIndex = textField.getCursorIndex();
			focused = textField.isFocused();
		}

		textField = new TextField(minecraft.fontRenderer, centerX - 75, buttonY + 8, 150, 20,
				string -> {
					customValue = string;
					formatButtons.get(Format.CUSTOM).setCustomDateFormat(string);
				});

		textField.setText(text);
		textField.setCursorIndex(cursorIndex);
		textField.setFocused(focused);
		textField.setChecker(checker -> {
			try {
				new SimpleDateFormat(checker.getText());
				return true;
			} catch (IllegalArgumentException e) {
				return false;
			}
		});

		buttonY = textField.getY() + 32;
		placeholderButtons.clear();

		for (FormatPlaceholder[] placeholders : PLACEHOLDERS) {
			int rowWidth = -2;
			for (FormatPlaceholder placeholder : placeholders) {
				rowWidth += minecraft.fontRenderer.getStringWidth(placeholder.getFormat()) + 2;
			}

			buttonX = centerX - rowWidth / 2;
			for (FormatPlaceholder placeholder : placeholders) {
				PlaceholderButton placeholderButton = placeholder.createButton(minecraft.fontRenderer,
						textField, buttonX, buttonY);
				placeholderButtons.add(placeholderButton);
				buttonX += placeholderButton.getWidth() + 2;
			}

			buttonY += 12;
		}
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		getDrawUtils().drawCenteredString(matrixStack, PRESET_TITLE, centerX, y + 18D, 0.7D);
		for (FormatButton formatButton : formatButtons.values()) {
			formatButton.render(matrixStack, mouseX, mouseY);
		}

		getDrawUtils().drawCenteredString(matrixStack, CUSTOM_FORMAT_TITLE, centerX,
				textField.getY() - 7D, 0.7D);
		textField.render(matrixStack, mouseX, mouseY);

		getDrawUtils().drawCenteredString(matrixStack, SYNTAX_TITLE, centerX, textField.getY() + 25D,
				0.7D);

		PlaceholderButton hoveredButton = null;
		for (PlaceholderButton placeholderButton : placeholderButtons) {
			placeholderButton.render(matrixStack, mouseX, mouseY);
			if (placeholderButton.isMouseOver()) {
				hoveredButton = placeholderButton;
			}
		}

		if (Objects.nonNull(hoveredButton) && Objects.nonNull(hoveredButton.getOnHover())) {
			getDrawUtils().drawHoveringText(matrixStack, mouseX, mouseY,
					hoveredButton.getOnHover().apply(hoveredButton.getText()));
		}

		drawInfo(matrixStack, mouseX, mouseY);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
		textField.mouseScrolled((int) delta);
		return false;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		for (FormatButton formatButton : formatButtons.values()) {
			formatButton.mouseClicked(button);
			if (button == 2 && formatButton.isMouseOver()) {
				textField.setText(formatButton.getFormat() == Format.CUSTOM ? customValue
						: formatButton.getFormat().getFormatting());
			}
		}

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

	@Override
	public void onDoneClick() {
		getCallback().accept(new String[]{currentFormat.name(), customValue});
	}

	private void drawInfo(MatrixStack matrixStack, int mouseX, int mouseY) {
		int infoX = maxX - 14;
		int infoY = y + 4;
		int infoMaxX = infoX + 10;
		int infoMaxY = infoY + 12;
		boolean mouseOver = mouseX >= infoX - 2 && mouseX <= infoMaxX + 2 && mouseY >= infoY - 2
				&& mouseY <= infoMaxY + 2;
		if (mouseOver) {
			getDrawUtils().drawCenteredString(matrixStack, INFO_TEXT, infoX + 5D, infoY - 2D, 2.0);
			getDrawUtils().drawHoveringText(matrixStack, mouseX, mouseY, INFO_HOVER_TEXT);
		} else {
			getDrawUtils().drawCenteredString(matrixStack, INFO_TEXT, infoX + 5D, infoY, 1.5);
		}
	}
}

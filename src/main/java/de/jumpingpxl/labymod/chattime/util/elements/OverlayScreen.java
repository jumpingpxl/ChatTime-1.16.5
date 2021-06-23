package de.jumpingpxl.labymod.chattime.util.elements;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.labymod.main.LabyMod;
import net.labymod.utils.Consumer;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;

public abstract class OverlayScreen<T> extends Screen {

	protected static final int BACKGROUND_COLOR = ModColor.toRGB(0, 0, 0, 128);
	private static final DrawUtils DRAW_UTILS = LabyMod.getInstance().getDrawUtils();
	private static final String DONE_BUTTON = "§aDone";
	private static final String CANCEL_BUTTON = "§cCancel";

	private final String title;
	private final Screen backgroundScreen;
	private final Consumer<T> callback;
	private final int defaultX;
	private final int defaultY;
	private final int defaultMaxX;
	private final int defaultMaxY;
	protected int centerX;
	protected int centerY;
	protected int x;
	protected int y;
	protected int maxX;
	protected int maxY;
	private T currentValue;
	private Button cancelButton;

	protected OverlayScreen(String title, Screen backgroundScreen, T currentValue,
	                        Consumer<T> callback, int x, int y, int maxX, int maxY) {
		super(StringTextComponent.EMPTY);

		this.title = title;
		this.backgroundScreen = backgroundScreen;
		this.currentValue = currentValue;
		this.callback = callback;

		defaultX = x;
		defaultY = y;
		defaultMaxX = maxX;
		defaultMaxY = maxY;
	}

	@Override
	public void init(Minecraft minecraft, int width, int height) {
		super.init(minecraft, width, height);
		backgroundScreen.init(minecraft, width, height);

		int tempCenterY = this.height / 3;
		centerX = this.width / 2;
		centerY = tempCenterY + (defaultY + defaultMaxY) / 3;
		x = centerX + defaultX;
		y = tempCenterY + defaultY;
		maxX = centerX + defaultMaxX;
		maxY = tempCenterY + defaultMaxY;

		int buttonLength = defaultMaxX - 7;
		cancelButton = new Button(centerX + 2, maxY - 25, buttonLength, 20,
				new StringTextComponent(CANCEL_BUTTON),
				onPress -> minecraft.displayGuiScreen(backgroundScreen));

		Button doneButton = new Button(x + 5, maxY - 25, buttonLength, 20,
				new StringTextComponent(DONE_BUTTON), onPress -> {
			onDoneClick();
			cancelButton.onPress();
		});

		addButton(cancelButton);
		addButton(doneButton);
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		backgroundScreen.render(matrixStack, 0, 0, partialTicks);
		RenderSystem.clear(256, Minecraft.IS_RUNNING_ON_MAC);

		fill(matrixStack, 0, 0, this.width, this.height, BACKGROUND_COLOR);
		fill(matrixStack, x, y, maxX, maxY, BACKGROUND_COLOR);
		DRAW_UTILS.drawCenteredString(matrixStack, title, centerX, y + 5D);

		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 256) {
			cancelButton.onPress();
			return false;
		}

		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	public void onDoneClick() {
		callback.accept(currentValue);
	}

	protected final DrawUtils getDrawUtils() {
		return DRAW_UTILS;
	}

	protected final String getOverlayTitle() {
		return title;
	}

	protected final Consumer<T> getCallback() {
		return callback;
	}

	protected final T getValue() {
		return currentValue;
	}

	protected final void setValue(T value) {
		currentValue = value;
	}
}

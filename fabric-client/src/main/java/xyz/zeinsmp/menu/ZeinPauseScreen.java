package xyz.zeinsmp.menu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public final class ZeinPauseScreen extends Screen {
    private static final String SERVER_IP = "zeinsmp.xyz";

    private static final int BUTTON_WIDTH = 140;
    private static final int BUTTON_HEIGHT = 40;
    private static final int BUTTON_GAP = 16;

    public ZeinPauseScreen() {
        super(Component.literal("ZEIN SMP"));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);

        graphics.fill(0, 0, this.width, this.height, 0xE8101014);
        graphics.fill(0, 0, this.width, 3, 0xFFB51220);
        graphics.fill(0, this.height - 3, this.width, this.height, 0xFFB51220);

        int centerX = this.width / 2;
        int titleY = Math.max(34, this.height / 4 - 12);
        drawScaledCentered(graphics, "ZEIN SMP", centerX, titleY, 3.5F, 0xFFFF303D);

        String[] labels = {"DC", "IP", "STORE"};
        int totalWidth = labels.length * BUTTON_WIDTH + (labels.length - 1) * BUTTON_GAP;
        int startX = centerX - totalWidth / 2;
        int buttonY = this.height / 2 - BUTTON_HEIGHT / 2;

        for (int i = 0; i < labels.length; i++) {
            int x = startX + i * (BUTTON_WIDTH + BUTTON_GAP);
            boolean hovered = mouseX >= x && mouseX <= x + BUTTON_WIDTH
                    && mouseY >= buttonY && mouseY <= buttonY + BUTTON_HEIGHT;

            float pulse = (float) (0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 420.0));
            int edge = hovered ? 0xFFFF4652 : 0xFF8D1822;
            int fill = hovered ? 0xFF492027 : 0xFF24171B;

            graphics.fill(x, buttonY, x + BUTTON_WIDTH, buttonY + BUTTON_HEIGHT, edge);
            graphics.fill(x + 1, buttonY + 1, x + BUTTON_WIDTH - 1,
                    buttonY + BUTTON_HEIGHT - 1, fill);
            drawScaledCentered(graphics, labels[i], x + BUTTON_WIDTH / 2,
                    buttonY + 11, 2.5F,
                    hovered ? 0xFFFFFFFF : 0xFFE8DADD);
        }

        drawScaledCentered(graphics, "ZEINSMP.XYZ", centerX,
                buttonY + 66, 2.0F, 0xFFB7A9AC);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void drawScaledCentered(GuiGraphics graphics, String text, int centerX,
                                    int y, float scale, int color) {
        graphics.pose().pushMatrix();
        graphics.pose().scale(scale, scale);
        graphics.drawCenteredString(this.font, Component.literal(text),
                (int) (centerX / scale), (int) (y / scale), color);
        graphics.pose().popMatrix();
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() != 0) {
            return super.mouseClicked(event, doubleClick);
        }

        double mouseX = event.x();
        double mouseY = event.y();
        int centerX = this.width / 2;
        int totalWidth = BUTTON_WIDTH * 3 + BUTTON_GAP * 2;
        int startX = centerX - totalWidth / 2;
        int buttonY = this.height / 2 - BUTTON_HEIGHT / 2;

        for (int i = 0; i < 3; i++) {
            int x = startX + i * (BUTTON_WIDTH + BUTTON_GAP);
            if (mouseX >= x && mouseX <= x + BUTTON_WIDTH
                    && mouseY >= buttonY && mouseY <= buttonY + BUTTON_HEIGHT) {
                if (i == 1) {
                    Minecraft.getInstance().keyboardHandler.setClipboard(SERVER_IP);
                } else {
                    Minecraft.getInstance().keyboardHandler.setClipboard(
                            i == 0 ? "Add your ZEIN SMP Discord URL" : "Add your ZEIN SMP Store URL");
                }
                return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(null);
    }
}

package io.github.kydzombie.stapitech.gui.screen.ingame;

import net.minecraft.class_583;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;
import org.lwjgl.opengl.GL11;

public abstract class ProcessingMachineScreen extends HandledScreen {

    private final String machineName;
    private final String texturePath;

    public ProcessingMachineScreen(ScreenHandler container, String machineName, String texturePath) {
        super(container);
        this.machineName = machineName;
        this.texturePath = texturePath;
    }

    protected abstract int getEnergy();
    protected abstract int getMaxEnergy();
    protected abstract int getCookProgress();

    protected void drawForeground() {
        textRenderer.draw(machineName, 96 - textRenderer.getWidth(machineName) / 2, 6, 0x404040);
        textRenderer.draw("Inventory", 8, backgroundHeight - 96 + 2, 0x404040);
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        super.render(mouseX, mouseY, delta);
        GL11.glPushMatrix();
        GL11.glDisable(32826);
        class_583.method_1927();
        GL11.glDisable(2896);
        GL11.glDisable(2929);
        int renderX = (width - backgroundWidth) / 2;
        int renderY = (height - backgroundHeight) / 2;
        if (mouseX >= renderX + 5 && mouseX <= renderX + 6 + 22 &&
                mouseY >= renderY + 5 && mouseY <= renderY + 6 + 64) {
            var text = "Energy: " + getEnergy() + "/" + getMaxEnergy();
            int length = textRenderer.getWidth(text);
            int energyTextX = mouseX - (length / 2);
            int energyTextY = mouseY - 12;

            this.fillGradient(energyTextX - 3, energyTextY - 3, energyTextX + length + 3, energyTextY + 8 + 3, -1073741824, -1073741824);
            this.textRenderer.drawWithShadow(text, energyTextX, energyTextY, -1);
        }
        GL11.glPopMatrix();
    }

    @Override
    protected void drawBackground(float tickDelta) {
        int textureId = minecraft.textureManager.getTextureId(texturePath);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.textureManager.bindTexture(textureId);
        int renderX = (width - backgroundWidth) / 2;
        int renderY = (height - backgroundHeight) / 2;
        drawTexture(renderX, renderY, 0, 0, backgroundWidth, backgroundHeight);

        int progress = getCookProgress();
        drawTexture(renderX + 79, renderY + 34, 176, 0, progress + 1, 16);

        int charge = Math.floorDiv(getEnergy() * 64, getMaxEnergy());
        drawTexture(renderX + 6, renderY + 6 + (64 - charge), 176, 81 - charge, 22, charge);
    }
}

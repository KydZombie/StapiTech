package io.github.kydzombie.stapitech.gui.screen.ingame;

import io.github.kydzombie.stapitech.block.entity.BatteryBlockEntity;
import io.github.kydzombie.stapitech.block.entity.GeneratorBlockEntity;
import io.github.kydzombie.stapitech.gui.screen.BatteryBlockScreenHandler;
import io.github.kydzombie.stapitech.gui.screen.GeneratorScreenHandler;
import net.minecraft.class_583;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerEntity;
import org.lwjgl.opengl.GL11;

public class BatteryBlockScreen extends HandledScreen {
    private final BatteryBlockEntity blockEntity;

    public BatteryBlockScreen(PlayerEntity player, BatteryBlockEntity blockEntity) {
        super(new BatteryBlockScreenHandler(player, blockEntity));
        this.blockEntity = blockEntity;
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
            var text = "Energy: " + blockEntity.getEnergy() + "/" + blockEntity.getMaxEnergy();
            int length = textRenderer.getWidth(text);
            int energyTextX = mouseX - (length / 2);
            int energyTextY = mouseY - 12;

            this.fillGradient(energyTextX - 3, energyTextY - 3, energyTextX + length + 3, energyTextY + 8 + 3, -1073741824, -1073741824);
            this.textRenderer.drawWithShadow(text, energyTextX, energyTextY, -1);
        }
        GL11.glPopMatrix();
    }

    protected void drawForeground() {
        textRenderer.draw("Battery", 89 - textRenderer.getWidth("Battery") / 2, 6, 0x404040);
        textRenderer.draw("Inventory", 8, backgroundHeight - 96 + 2, 0x404040);
    }

    @Override
    protected void drawBackground(float tickDelta) {
        int textureId = minecraft.textureManager.getTextureId("assets/stapitech/gui/battery_block.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.textureManager.bindTexture(textureId);
        int renderX = (width - backgroundWidth) / 2;
        int renderY = (height - backgroundHeight) / 2;
        drawTexture(renderX, renderY, 0, 0, backgroundWidth, backgroundHeight);

//        if (blockEntity.isBurning()) {
//            int fuelProgress = blockEntity.getFuelProgress(12);
//            drawTexture(renderX + 80, renderY + 36 + 12 - fuelProgress, 176, 12 - fuelProgress, 14, fuelProgress + 2);
//        }

        int charge = Math.floorDiv(blockEntity.getEnergy() * 64, blockEntity.getMaxEnergy());
        drawTexture(renderX + 6, renderY + 6 + (64 - charge), 176, 78 - charge, 22, charge);
    }
}

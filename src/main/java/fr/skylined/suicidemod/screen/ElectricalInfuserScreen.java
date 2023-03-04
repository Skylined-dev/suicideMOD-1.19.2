package fr.skylined.suicidemod.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import fr.skylined.suicidemod.SuicideMod;
import fr.skylined.suicidemod.screen.rendered.EnergyInfoArea;
import fr.skylined.suicidemod.util.MouseUtil;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.Optional;

public class ElectricalInfuserScreen extends AbstractContainerScreen<ElectricalInfuserMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(SuicideMod.MOD_ID,"textures/gui/electrical_infuser_gui.png");
    private EnergyInfoArea energyInfoArea;

    public ElectricalInfuserScreen(ElectricalInfuserMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
    }



    @Override
    protected void init() {

        super.init();
        assignEnergyInfoArea();

    }

    private void assignEnergyInfoArea() {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        energyInfoArea = new EnergyInfoArea(x+156,y+13,menu.blockEntity.getEnergyStorage());
    }

    @Override
    protected void renderLabels(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        font.draw(pPoseStack, title, imageWidth / 2.0F - font.width(title) / 2.0F, (float) titleLabelY, 0x404040);
        font.draw(pPoseStack, playerInventoryTitle, (float) inventoryLabelX, (float) inventoryLabelY, 0x404040);
        renderEnergyAreaTooltips(pPoseStack, pMouseX, pMouseY, x, y);
    }

    private void renderEnergyAreaTooltips(PoseStack pPoseStack, int pMouseX, int pMouseY, int x, int y) {
        if(isMouseAboveArea(pMouseX, pMouseY, x, y, 156, 13, 8, 64)) {
            renderTooltip(pPoseStack, energyInfoArea.getTooltips(),
                    Optional.empty(), pMouseX - x, pMouseY - y);
        }
    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        this.blit(pPoseStack, x, y, 0, 0, imageWidth, imageHeight);

        renderProgressArrow(pPoseStack, x, y);
        energyInfoArea.draw(pPoseStack);
    }

    private void renderProgressArrow(PoseStack pPoseStack, int x, int y) {
        if(menu.isCrafting()) {
            blit(pPoseStack, x + 79, y + 31, 176, 0, 8, menu.getScaledProgress());
        }
    }

    @Override
    public void render(PoseStack pPoseStack, int mouseX, int mouseY, float delta) {
        renderBackground(pPoseStack);
        super.render(pPoseStack, mouseX, mouseY, delta);
        renderTooltip(pPoseStack, mouseX, mouseY);
    }
    private boolean isMouseAboveArea(int pMouseX, int pMouseY, int x, int y, int offsetX, int offsetY, int width, int height) {
        return MouseUtil.isMouseOver(pMouseX, pMouseY, x + offsetX, y + offsetY, width, height);
    }
}

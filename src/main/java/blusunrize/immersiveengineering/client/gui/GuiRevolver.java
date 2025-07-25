package blusunrize.immersiveengineering.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.common.gui.ContainerRevolver;
import blusunrize.immersiveengineering.common.items.ItemRevolver;

public class GuiRevolver extends GuiContainer {

    int bullets = 0;

    public GuiRevolver(InventoryPlayer inventoryPlayer, World world) {
        super(new ContainerRevolver(inventoryPlayer, world));
        if (inventoryPlayer.player.getCurrentEquippedItem() != null && inventoryPlayer.player.getCurrentEquippedItem()
            .getItem() instanceof ItemRevolver)
            bullets = ((ItemRevolver) inventoryPlayer.player.getCurrentEquippedItem()
                .getItem()).getBulletSlotAmount(inventoryPlayer.player.getCurrentEquippedItem());
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        ClientUtils.bindTexture("immersiveengineering:textures/gui/revolver.png");
        this.drawTexturedModalRect(guiLeft, guiTop + 77, 0, 125, 176, 89);

        int w = bullets >= 18 ? 150 : bullets > 8 ? 136 : 74;
        int off = (176 - w) / 2;
        this.drawTexturedModalRect(guiLeft + off + 00, guiTop + 1, 00, 51, 74, 74);
        if (bullets >= 18) this.drawTexturedModalRect(guiLeft + off + 47, guiTop + 1, 74, 51, 103, 74);
        else if (bullets > 8) this.drawTexturedModalRect(guiLeft + off + 57, guiTop + 1, 57, 12, 79, 39);
    }
}

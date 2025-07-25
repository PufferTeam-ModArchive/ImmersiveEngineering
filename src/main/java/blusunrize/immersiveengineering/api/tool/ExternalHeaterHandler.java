package blusunrize.immersiveengineering.api.tool;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFurnace;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;

public class ExternalHeaterHandler {

    // These are set on IE loading
    public static int defaultFurnaceEnergyCost;
    public static int defaultFurnaceSpeedupCost;

    /**
     * @author BluSunrize - 09.12.2015
     *
     *         An interface to be implemented by TileEntities that want to allow direct interaction with the external
     *         heater
     */
    public static interface IExternalHeatable {

        /**
         * Called each tick<br>
         * Handle fueling as well as possible smelting speed increases here
         * 
         * @param energyAvailable the amount of RF the furnace heater has stored and can supply
         * @param redstone        whether a redstone signal is applied to the furnace heater. To keep the target warm,
         *                        but not do speed increases
         * @return the amount of RF consumed that tick. Should be lower or equal to "energyAvailable", obviously
         */
        public int doHeatTick(int energyAvailable, boolean redstone);
    }

    public static HashMap<Class<? extends TileEntity>, HeatableAdapter> adapterMap = new HashMap<Class<? extends TileEntity>, HeatableAdapter>();

    /**
     * @author BluSunrize - 09.12.2015
     *
     *         An adapter to appyl to TileEntities that can't implement the IExternalHeatable interface
     */
    public abstract static class HeatableAdapter<E extends TileEntity> {

        /**
         * Called each tick<br>
         * Handle fueling as well as possible smelting speed increases here
         * 
         * @param energyAvailable the amount of RF the furnace heater has stored and can supply
         * @param redstone        whether a redstone signal is applied to the furnace heater. To keep the target warm,
         *                        but not do speed increases
         * @return the amount of RF consumed that tick. Should be lower or equal to "energyAvailable", obviously
         */
        public abstract int doHeatTick(E tileEntity, int energyAvailable, boolean canHeat);
    }

    /**
     * registers a HeatableAdapter to a TileEnttiy class. Should really only be used when implementing the interface is
     * not an option
     */
    public static void registerHeatableAdapter(Class<? extends TileEntity> c, HeatableAdapter adapter) {
        adapterMap.put(c, adapter);
    }

    /**
     * @return a HeatableAdapter for the given TileEntity class
     */
    public static HeatableAdapter getHeatableAdapter(Class<? extends TileEntity> c) {
        HeatableAdapter adapter = (HeatableAdapter) adapterMap.get(c);
        if (adapter == null && c != TileEntity.class && c.getSuperclass() != TileEntity.class) {
            adapter = getHeatableAdapter((Class<? extends TileEntity>) c.getSuperclass());
            adapterMap.put(c, adapter);
        }
        return adapter;
    }

    public static class DefaultFurnaceAdapter extends HeatableAdapter<TileEntityFurnace> {

        boolean canCook(TileEntityFurnace tileEntity) {
            ItemStack input = tileEntity.getStackInSlot(0);
            if (input == null) return false;
            ItemStack output = FurnaceRecipes.smelting()
                .getSmeltingResult(input);
            if (output == null) return false;
            ItemStack existingOutput = tileEntity.getStackInSlot(2);
            if (existingOutput == null) return true;
            if (!existingOutput.isItemEqual(output)) return false;
            int stackSize = existingOutput.stackSize + output.stackSize;
            return stackSize <= tileEntity.getInventoryStackLimit() && stackSize <= output.getMaxStackSize();
        }

        @Override
        public int doHeatTick(TileEntityFurnace tileEntity, int energyAvailable, boolean redstone) {
            int energyConsumed = 0;
            boolean canCook = canCook(tileEntity);
            if (canCook || redstone) {
                boolean burning = tileEntity.isBurning();
                if (tileEntity.furnaceBurnTime < 200) {
                    int heatAttempt = 4;
                    int heatEnergyRatio = Math.max(1, defaultFurnaceEnergyCost);
                    int energyToUse = Math.min(energyAvailable, heatAttempt * heatEnergyRatio);
                    int heat = energyToUse / heatEnergyRatio;
                    if (heat > 0) {
                        tileEntity.furnaceBurnTime += heat;
                        energyConsumed += heat * heatEnergyRatio;
                        if (!burning) updateFurnace(tileEntity, tileEntity.furnaceBurnTime > 0);
                    }
                }
                if (canCook && tileEntity.furnaceBurnTime >= 200 && tileEntity.furnaceCookTime < 199) {
                    int energyToUse = defaultFurnaceSpeedupCost;
                    if (energyAvailable - energyConsumed > energyToUse) {
                        energyConsumed += energyToUse;
                        tileEntity.furnaceCookTime += 1;
                    }
                }
            }
            return energyConsumed;
        }

        public void updateFurnace(TileEntity tileEntity, boolean active) {
            Block containing = tileEntity.getBlockType();
            if (containing == Blocks.furnace || containing == Blocks.lit_furnace) BlockFurnace.updateFurnaceBlockState(
                active,
                tileEntity.getWorldObj(),
                tileEntity.xCoord,
                tileEntity.yCoord,
                tileEntity.zCoord);
            else {
                // Fix for Natura, might work on other furnaces that extend the vanilla one and use the variable name
                // "active". Let's hope. xD
                NBTTagCompound nbt = new NBTTagCompound();
                tileEntity.writeToNBT(nbt);
                nbt.setBoolean("active", active);
                nbt.setBoolean("Active", active);
                tileEntity.readFromNBT(nbt);
                tileEntity.getWorldObj()
                    .markBlockForUpdate(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
            }
        }
    }
}

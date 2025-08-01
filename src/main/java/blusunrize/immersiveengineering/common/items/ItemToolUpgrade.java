package blusunrize.immersiveengineering.common.items;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.google.common.collect.ImmutableSet;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import blusunrize.immersiveengineering.api.tool.IUpgrade;
import blusunrize.immersiveengineering.api.tool.IUpgradeableTool;
import blusunrize.immersiveengineering.common.util.Lib;

public class ItemToolUpgrade extends ItemIEBase implements IUpgrade {

    public ItemToolUpgrade() {
        super(
            "toolupgrade",
            1,
            "drillWaterproof",
            "drillSpeed",
            "drillDamage",
            "drillCapacity",
            "revolverBayonet",
            "revolverMagazine",
            "revolverElectro",
            "chemthrowerFocus",
            "railgunScope",
            "railgunCapacitors");
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean adv) {
        if (stack.getItemDamage() < getSubNames().length) {
            String[] flavour = ImmersiveEngineering.proxy.splitStringOnWidth(
                StatCollector
                    .translateToLocal(Lib.DESC_FLAVOUR + "toolupgrade." + this.getSubNames()[stack.getItemDamage()]),
                200);
            for (String s : flavour) list.add(s);
        }
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        if (stack.getItemDamage() == 2) return 3;
        return super.getItemStackLimit(stack);
    }

    @Override
    public Set<String> getUpgradeTypes(ItemStack upgrade) {
        if (upgrade.getItemDamage() <= 2) return ImmutableSet.of("DRILL");
        else if (upgrade.getItemDamage() == 3) return ImmutableSet.of("DRILL", "CHEMTHROWER");
        else if (upgrade.getItemDamage() <= 6) return ImmutableSet.of("REVOLVER");
        else if (upgrade.getItemDamage() == 7) return ImmutableSet.of("CHEMTHROWER");
        else return ImmutableSet.of("RAILGUN");
    }

    @Override
    public boolean canApplyUpgrades(ItemStack target, ItemStack upgrade) {
        if (upgrade.getItemDamage() == 5 && target.getItem() instanceof IUpgradeableTool)
            return !((IUpgradeableTool) target.getItem()).getUpgrades(target)
                .hasKey("bullets");
        return true;
    }

    @Override
    public void applyUpgrades(ItemStack target, ItemStack upgrade, HashMap<String, Object> modifications) {
        switch (upgrade.getItemDamage()) {
            case 0:
                modifications.put("waterproof", true);
                break;
            case 1:
                Integer mod = (Integer) modifications.get("speed");
                modifications.put("speed", (mod == null ? 0 : mod) + 1);
                break;
            case 2:
                mod = (Integer) modifications.get("damage");
                modifications.put("damage", (mod == null ? 0 : mod) + upgrade.stackSize);
                break;
            case 3:
                mod = (Integer) modifications.get("capacity");
                modifications.put("capacity", (mod == null ? 0 : mod) + 2000);
                break;

            case 4:
                Float melee = (Float) modifications.get("melee");
                modifications.put("melee", (melee == null ? 0 : melee) + 6f);
                break;
            case 5:
                mod = (Integer) modifications.get("bullets");
                modifications.put("bullets", (mod == null ? 0 : mod) + 6);
                break;
            case 6:
                modifications.put("electro", true);
                break;

            case 7:
                modifications.put("focus", true);
                break;

            case 8:
                modifications.put("scope", true);
                break;

            case 9:
                modifications.put("speed", 1f);
                modifications.put("capacity", 4000);
                break;
        }
    }
}

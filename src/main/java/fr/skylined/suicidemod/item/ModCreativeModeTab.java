package fr.skylined.suicidemod.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ModCreativeModeTab {
    public static final CreativeModeTab SUICIDEMOD_TAB = new CreativeModeTab("suicidemod_tab") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.FLY_CHARM.get());
        }
    };
}

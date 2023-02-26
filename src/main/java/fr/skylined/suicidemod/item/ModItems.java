package fr.skylined.suicidemod.item;

import fr.skylined.suicidemod.SuicideMod;
import fr.skylined.suicidemod.item.custom.FlyCharmItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SuicideMod.MOD_ID);
    
    public static final RegistryObject<Item> FLY_CHARM = ITEMS.register("fly_charm", () -> new FlyCharmItem(new Item.Properties().tab(ModCreativeModeTab.SUICIDEMOD_TAB).durability(3000)));
    public static final RegistryObject<Item> NETHERSTAR_FRAGMENT_UP = ITEMS.register("netherstar_fragment_up", () -> new Item(new Item.Properties().tab(ModCreativeModeTab.SUICIDEMOD_TAB)));
    public static final RegistryObject<Item> NETHERSTAR_FRAGMENT_DOWN = ITEMS.register("netherstar_fragment_down", () -> new Item(new Item.Properties().tab(ModCreativeModeTab.SUICIDEMOD_TAB)));
    public static final RegistryObject<Item> NETHERSTAR_FRAGMENT_LEFT = ITEMS.register("netherstar_fragment_left", () -> new Item(new Item.Properties().tab(ModCreativeModeTab.SUICIDEMOD_TAB)));
    public static final RegistryObject<Item> NETHERSTAR_FRAGMENT_RIGHT = ITEMS.register("netherstar_fragment_right", () -> new Item(new Item.Properties().tab(ModCreativeModeTab.SUICIDEMOD_TAB)));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}

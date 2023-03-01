package fr.skylined.suicidemod.block;

import fr.skylined.suicidemod.SuicideMod;
import fr.skylined.suicidemod.block.custom.ElectricalCutterBlock;
import fr.skylined.suicidemod.block.custom.EletricalInfuserBlock;
import fr.skylined.suicidemod.item.ModCreativeModeTab;
import fr.skylined.suicidemod.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StonecutterBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, SuicideMod.MOD_ID);

    public static final RegistryObject<Block> ELECTRICAL_CUTTER = registrerBlock("electrical_cutter",
            () -> new ElectricalCutterBlock(BlockBehaviour.Properties.of(Material.STONE).strength(6f).requiresCorrectToolForDrops()), ModCreativeModeTab.SUICIDEMOD_TAB);
    public static final RegistryObject<Block> ELECTRICAL_INFUSER = registrerBlock("electrical_infuser",
            () -> new EletricalInfuserBlock(BlockBehaviour.Properties.of(Material.STONE).strength(6f).requiresCorrectToolForDrops()), ModCreativeModeTab.SUICIDEMOD_TAB);
    private static <T extends Block>RegistryObject<T> registrerBlock(String name, Supplier<T> block, CreativeModeTab tab){
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn, tab);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block, CreativeModeTab tab){
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().tab(tab)));
    }

    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }
}

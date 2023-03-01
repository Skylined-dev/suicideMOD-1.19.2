package fr.skylined.suicidemod.block.entity;

import fr.skylined.suicidemod.SuicideMod;
import fr.skylined.suicidemod.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, SuicideMod.MOD_ID);

    public static final RegistryObject<BlockEntityType<ElectricalCutterBlockEntity>> ELECTRICAL_CUTTER = BLOCK_ENTITIES.register("electrical_cutter", () ->
            BlockEntityType.Builder.of(ElectricalCutterBlockEntity::new, ModBlocks.ELECTRICAL_CUTTER.get()).build(null));
    public static void register(IEventBus eventBus){
        BLOCK_ENTITIES.register(eventBus);
    }
}

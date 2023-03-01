package fr.skylined.suicidemod.recipe;

import fr.skylined.suicidemod.SuicideMod;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, SuicideMod.MOD_ID);

    public static final RegistryObject<RecipeSerializer<ElectricalCutterRecipe>> GEM_INFUSING_SERIALIZER =
            SERIALIZERS.register("electrical_cutter", () -> ElectricalCutterRecipe.Serializer.INSTANCE);

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}

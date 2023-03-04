package fr.skylined.suicidemod;

import com.mojang.logging.LogUtils;
import fr.skylined.suicidemod.block.ModBlocks;
import fr.skylined.suicidemod.block.entity.ModBlockEntities;
import fr.skylined.suicidemod.item.ModItems;
import fr.skylined.suicidemod.networking.ModMessages;
import fr.skylined.suicidemod.recipe.ModRecipes;
import fr.skylined.suicidemod.screen.ElectricalCutterScreen;
import fr.skylined.suicidemod.screen.ElectricalInfuserMenu;
import fr.skylined.suicidemod.screen.ElectricalInfuserScreen;
import fr.skylined.suicidemod.screen.ModMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.CraftingTableBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SuicideMod.MOD_ID)
public class SuicideMod
{
    public static final String MOD_ID = "suicidemod";
    private static final Logger LOGGER = LogUtils.getLogger();
    public SuicideMod()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModRecipes.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(() -> {
            ModMessages.register();
        });
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {

            MenuScreens.register(ModMenuTypes.ELECTRICAL_CUTTER_MENU.get(), ElectricalCutterScreen::new);
            MenuScreens.register(ModMenuTypes.ELECTRICAL_INFUSER_MENU.get(), ElectricalInfuserScreen::new);

        }
    }
}

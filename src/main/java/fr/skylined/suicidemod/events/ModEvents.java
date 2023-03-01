package fr.skylined.suicidemod.events;

import fr.skylined.suicidemod.SuicideMod;
import fr.skylined.suicidemod.commands.SuicideCommand;
import net.minecraft.world.item.EnderpearlItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.EnchantmentTableBlock;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.ConfigCommand;

@Mod.EventBusSubscriber(modid = SuicideMod.MOD_ID)
public class ModEvents {
    @SubscribeEvent
    public static void onCommandsRegister(RegisterCommandsEvent event){
        new SuicideCommand(event.getDispatcher());
        ConfigCommand.register(event.getDispatcher());
    }
}

package fr.skylined.suicidemod.item.custom;

import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.Random;

public class FlyCharmItem extends Item implements ICurioItem {

    Random random = new Random();
    private static final Logger LOGGER = LogUtils.getLogger();

    public FlyCharmItem(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        Player player = (Player)slotContext.entity();
        if(!player.level.isClientSide){
            if(player.getAbilities().flying){
                if(random.nextFloat() > 0.9f && !player.isCreative()){
                    stack.setDamageValue(stack.getDamageValue() + 1);
                    if (stack.getDamageValue() >= stack.getMaxDamage()){
                        stack.setCount(0);
                        if(player.getAbilities().flying)
                            player.getAbilities().flying = false;
                    }
                }
            }
        }

        ICurioItem.super.curioTick(slotContext, stack);
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        Player player = (Player) slotContext.entity();
        if(!player.level.isClientSide){
            player.getAbilities().mayfly=true;
            player.onUpdateAbilities();
        }

        ICurioItem.super.onEquip(slotContext, prevStack, stack);
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        Player player = (Player) slotContext.entity();
        if(player.level.isClientSide){
            player.getAbilities().mayfly=false;
            player.onUpdateAbilities();
        }

        ICurioItem.super.onUnequip(slotContext, newStack, stack);
    }
}

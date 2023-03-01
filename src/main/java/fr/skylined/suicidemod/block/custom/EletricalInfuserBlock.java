package fr.skylined.suicidemod.block.custom;

import fr.skylined.suicidemod.SuicideMod;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class EletricalInfuserBlock extends Block {
    public EletricalInfuserBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if(!level.isClientSide && interactionHand == InteractionHand.MAIN_HAND) return super.use(blockState, level, blockPos, player, interactionHand, blockHitResult);

        player.sendSystemMessage(Component.literal("Right clicked electrical infuser"));
        return super.use(blockState, level, blockPos, player, interactionHand, blockHitResult);
    }
}

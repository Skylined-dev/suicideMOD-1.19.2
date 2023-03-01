package fr.skylined.suicidemod.block.entity;

import com.mojang.logging.LogUtils;
import fr.skylined.suicidemod.block.ModBlocks;
import fr.skylined.suicidemod.item.ModItems;
import fr.skylined.suicidemod.recipe.ElectricalCutterRecipe;
import fr.skylined.suicidemod.screen.ElectricalCutterMenu;
import fr.skylined.suicidemod.screen.ElectricalCutterScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import javax.lang.model.util.SimpleElementVisitor6;
import java.util.Optional;

public class ElectricalCutterBlockEntity extends BlockEntity implements MenuProvider {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final Component CONTAINER_TITLE = Component.translatable("container.electrical_cutter");



    private final ItemStackHandler itemHandler = new ItemStackHandler(5){
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();


    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 78;
    public ElectricalCutterBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.ELECTRICAL_CUTTER.get(), blockPos, blockState);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch(index){
                    case 0 -> ElectricalCutterBlockEntity.this.progress;
                    case 1 -> ElectricalCutterBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index){
                    case 0 -> ElectricalCutterBlockEntity.this.progress = value;
                    case 1 -> ElectricalCutterBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }



    @Override
    public Component getDisplayName() {
        return CONTAINER_TITLE;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {

        return new ElectricalCutterMenu(id, inventory, this, this.data);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.put("inventory", itemHandler.serializeNBT());
        nbt.putInt("electrical_cutter.progress", this.progress);
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        progress = nbt.getInt("eletrical_cutter.progess");
    }

    public void drops(){
        SimpleContainer inventory = new SimpleContainer((itemHandler.getSlots()));
        for (int i = 0; i < itemHandler.getSlots(); i++){
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ElectricalCutterBlockEntity pEntity) {
        if(level.isClientSide()) return;
        //LOGGER.debug(pEntity.toString());
        if(hasRecipe(pEntity)){
            pEntity.progress++;
            setChanged(level, pos, state);

            if(pEntity.progress >= pEntity.maxProgress){
                craftItem(pEntity);
            }
        }else{
            pEntity.resetProgress();
            setChanged(level,pos,state);
        }
    }

    private void resetProgress() {
        this.progress = 0;
    }

    private static void craftItem(ElectricalCutterBlockEntity pEntity) {

        Level level = pEntity.level;
        SimpleContainer inventory = new SimpleContainer(pEntity.itemHandler.getSlots());
        for(int i = 0; i < pEntity.itemHandler.getSlots(); i++){
            inventory.setItem(i, pEntity.itemHandler.getStackInSlot(i));
        }

        Optional<ElectricalCutterRecipe> recipe = level.getRecipeManager().getRecipeFor(ElectricalCutterRecipe.Type.INSTANCE, inventory, level);

        if(hasRecipe(pEntity)){
            pEntity.itemHandler.extractItem(0,1,false);
            pEntity.itemHandler.setStackInSlot(1, new ItemStack(recipe.get().getResultItem().getItem(), pEntity.itemHandler.getStackInSlot(1).getCount() + 1));
            pEntity.itemHandler.setStackInSlot(2, new ItemStack(recipe.get().getResultItem().getItem(), pEntity.itemHandler.getStackInSlot(2).getCount() + 1));
            pEntity.itemHandler.setStackInSlot(3, new ItemStack(recipe.get().getResultItem().getItem(), pEntity.itemHandler.getStackInSlot(3).getCount() + 1));
            pEntity.itemHandler.setStackInSlot(4, new ItemStack(recipe.get().getResultItem().getItem(), pEntity.itemHandler.getStackInSlot(4).getCount() + 1));

            pEntity.resetProgress();
        }
    }

    private static boolean hasRecipe(ElectricalCutterBlockEntity entity) {
        Level level = entity.level;
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for(int i = 0; i < entity.itemHandler.getSlots(); i++){
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        Optional<ElectricalCutterRecipe> recipe = level.getRecipeManager().getRecipeFor(ElectricalCutterRecipe.Type.INSTANCE, inventory, level);

        return recipe.isPresent() && canInsertAmountIntoOutputSlot1(inventory) && canInsertAmountIntoOutputSlot2(inventory) && canInsertAmountIntoOutputSlot3(inventory) && canInsertAmountIntoOutputSlot4(inventory) &&
                canInsertItemIntoOutputSlot1(inventory, recipe.get().getResultItem()) &&
                canInsertItemIntoOutputSlot2(inventory, recipe.get().getResultItem()) &&
                canInsertItemIntoOutputSlot3(inventory, recipe.get().getResultItem()) &&
                canInsertItemIntoOutputSlot4(inventory, recipe.get().getResultItem());
    }

    private static boolean canInsertItemIntoOutputSlot1(SimpleContainer inventory, ItemStack itemStack) {
        return inventory.getItem(1).getItem() == itemStack.getItem() || inventory.getItem(1).isEmpty();
    }

    private static boolean canInsertAmountIntoOutputSlot1(SimpleContainer inventory) {

        return inventory.getItem(1).getMaxStackSize() > inventory.getItem(1).getCount();
    }

    private static boolean canInsertItemIntoOutputSlot2(SimpleContainer inventory, ItemStack itemStack) {

        return inventory.getItem(2).getItem() == itemStack.getItem() || inventory.getItem(2).isEmpty();
    }

    private static boolean canInsertAmountIntoOutputSlot2(SimpleContainer inventory) {

        return inventory.getItem(2).getMaxStackSize() > inventory.getItem(2).getCount();
    }

    private static boolean canInsertItemIntoOutputSlot3(SimpleContainer inventory, ItemStack itemStack) {
        return inventory.getItem(3).getItem() == itemStack.getItem() || inventory.getItem(3).isEmpty();
    }

    private static boolean canInsertAmountIntoOutputSlot3(SimpleContainer inventory) {

        return inventory.getItem(3).getMaxStackSize() > inventory.getItem(3).getCount();
    }

    private static boolean canInsertItemIntoOutputSlot4(SimpleContainer inventory, ItemStack itemStack) {
        return inventory.getItem(4).getItem() == itemStack.getItem() || inventory.getItem(4).isEmpty();
    }

    private static boolean canInsertAmountIntoOutputSlot4(SimpleContainer inventory) {

        return inventory.getItem(4).getMaxStackSize() > inventory.getItem(4).getCount();
    }
}

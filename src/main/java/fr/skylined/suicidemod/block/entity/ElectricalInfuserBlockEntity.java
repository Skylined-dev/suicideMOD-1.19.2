package fr.skylined.suicidemod.block.entity;

import com.mojang.logging.LogUtils;
import fr.skylined.suicidemod.SuicideMod;
import fr.skylined.suicidemod.block.custom.EletricalInfuserBlock;
import fr.skylined.suicidemod.item.ModItems;
import fr.skylined.suicidemod.networking.ModMessages;
import fr.skylined.suicidemod.networking.packets.EnergySyncS2CPacket;
import fr.skylined.suicidemod.screen.ElectricalInfuserMenu;
import fr.skylined.suicidemod.util.ModEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Map;

public class ElectricalInfuserBlockEntity  extends BlockEntity implements MenuProvider {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final Component CONTAINER_TITLE = Component.translatable("container.electrical_infuser");

    public ElectricalInfuserBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.ELECTRICAL_INFUSER.get(), blockPos, blockState);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch(index){
                    case 0 -> ElectricalInfuserBlockEntity.this.progress;
                    case 1 -> ElectricalInfuserBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index){
                    case 0 -> ElectricalInfuserBlockEntity.this.progress = value;
                    case 1 -> ElectricalInfuserBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    private final ModEnergyStorage ENERGY_STORAGE = new ModEnergyStorage(64000, 256) {
        @Override
        public void onEnergyChanged() {
            setChanged();

            ModMessages.sendToClients(new EnergySyncS2CPacket(this.energy, getBlockPos()));
        }
    };
    private static final int ENERGY_REQ = 32;


    private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();

    private final ItemStackHandler itemHandler = new ItemStackHandler(2){
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch(slot){
                case 0 -> true;
                case 1 -> false;
                default -> super.isItemValid(slot, stack);
            };
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    private final Map<Direction, LazyOptional<WrappedHandler>> directionWrappedHandlerMap =
            Map.of(Direction.DOWN, LazyOptional.of(() -> new WrappedHandler(itemHandler, (i) -> i == 1, (i, s) -> false)),
                    Direction.UP, LazyOptional.of(() -> new WrappedHandler(itemHandler, (i) -> i == null, (index, stack) -> true)),
                    Direction.NORTH, LazyOptional.of(() -> new WrappedHandler(itemHandler, (index) -> index == null, (index, stack) -> false)),
                    Direction.SOUTH, LazyOptional.of(() -> new WrappedHandler(itemHandler, (i) -> i == null, (i, s) -> false)),
                    Direction.WEST, LazyOptional.of(() -> new WrappedHandler(itemHandler, (i) -> i == 1, (i, s) -> false)),
                    Direction.EAST, LazyOptional.of(() -> new WrappedHandler(itemHandler, (i) -> i == null,
                            (index, stack) -> true)));


    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 78;

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.electrical_infuser");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new ElectricalInfuserMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if(side == null) {
                return lazyItemHandler.cast();
            }

            if(directionWrappedHandlerMap.containsKey(side)) {
                Direction localDir = this.getBlockState().getValue(EletricalInfuserBlock.FACING);

                if(side == Direction.UP || side == Direction.DOWN) {
                    return directionWrappedHandlerMap.get(side).cast();
                }

                return switch (localDir) {
                    default -> directionWrappedHandlerMap.get(side.getOpposite()).cast();
                    case EAST -> directionWrappedHandlerMap.get(side.getClockWise()).cast();
                    case SOUTH -> directionWrappedHandlerMap.get(side).cast();
                    case WEST -> directionWrappedHandlerMap.get(side.getCounterClockWise()).cast();
                };
            }
        }

        if(cap == ForgeCapabilities.ENERGY){
            return lazyEnergyHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
        lazyEnergyHandler = LazyOptional.of(() -> ENERGY_STORAGE);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        lazyEnergyHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.put("inventory", itemHandler.serializeNBT());
        nbt.putInt("electrical_infuser.progress", this.progress);
        nbt.putInt("electrical_cutter.energy", ENERGY_STORAGE.getEnergyStored());
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        progress = nbt.getInt("electrical_infuser.progress");
        ENERGY_STORAGE.setEnergy(nbt.getInt("electrical_cutter.energy"));
    }

    public void drops(){
        SimpleContainer inventory = new SimpleContainer((itemHandler.getSlots()));
        for (int i = 0; i < itemHandler.getSlots(); i++){
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ElectricalInfuserBlockEntity pEntity) {
        if(level.isClientSide()) return;
        if(hasRecipe(pEntity) && hasEnoughEnergy(pEntity)){
            pEntity.progress++;
            extractEnergy(pEntity);
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

    private static ItemStack resultItem = ItemStack.EMPTY;

    private static void craftItem(ElectricalInfuserBlockEntity pEntity) {
        if (hasRecipe(pEntity)) {
            pEntity.itemHandler.extractItem(0, 1, false);
            pEntity.itemHandler.setStackInSlot(1, new ItemStack(resultItem.getItem(), pEntity.itemHandler.getStackInSlot(1).getCount() + 1)/*new ItemStack(recipe.get().getResultItem().getItem()ModItems.NETHERSTAR_FRAGMENT_UP_FOIL.get(), pEntity.itemHandler.getStackInSlot(1).getCount() + 1)*/);

            pEntity.resetProgress();

        }
    }

    private static void extractEnergy(ElectricalInfuserBlockEntity pEntity) {
        pEntity.ENERGY_STORAGE.extractEnergy(ENERGY_REQ, false);
    }

    private static boolean hasEnoughEnergy(ElectricalInfuserBlockEntity pEntity) {
        return pEntity.ENERGY_STORAGE.getEnergyStored() >= ENERGY_REQ * pEntity.maxProgress;
    }
    public IEnergyStorage getEnergyStorage() {
        return ENERGY_STORAGE;
    }

    public void setEnergyLevel(int energy) {
        this.ENERGY_STORAGE.setEnergy(energy);
    }

    private static boolean hasRecipe(ElectricalInfuserBlockEntity entity) {
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for(int i = 0; i < entity.itemHandler.getSlots(); i++){
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        if (entity.itemHandler.getStackInSlot(0).getItem() == ModItems.NETHERSTAR_FRAGMENT_UP.get()){
            if(canInsertAmountIntoOutputSlot1(inventory) && canInsertItemIntoOutputSlot1(inventory, new ItemStack(ModItems.NETHERSTAR_FRAGMENT_UP_FOIL.get(), 1))){
                resultItem = new ItemStack(ModItems.NETHERSTAR_FRAGMENT_UP_FOIL.get(), 1);
                return true;
            }
        }else if(entity.itemHandler.getStackInSlot(0).getItem() == ModItems.NETHERSTAR_FRAGMENT_DOWN.get()){
            if(canInsertAmountIntoOutputSlot1(inventory) && canInsertItemIntoOutputSlot1(inventory, new ItemStack(ModItems.NETHERSTAR_FRAGMENT_DOWN_FOIL.get(), 1))){
                resultItem = new ItemStack(ModItems.NETHERSTAR_FRAGMENT_DOWN_FOIL.get(), 1);
                return true;
            }
        }else if(entity.itemHandler.getStackInSlot(0).getItem() == ModItems.NETHERSTAR_FRAGMENT_LEFT.get()){
            if(canInsertAmountIntoOutputSlot1(inventory) && canInsertItemIntoOutputSlot1(inventory, new ItemStack(ModItems.NETHERSTAR_FRAGMENT_LEFT_FOIL.get(), 1))){
                resultItem = new ItemStack(ModItems.NETHERSTAR_FRAGMENT_LEFT_FOIL.get(), 1);
                return true;
            }
        }else if(entity.itemHandler.getStackInSlot(0).getItem() == ModItems.NETHERSTAR_FRAGMENT_RIGHT.get()){
            if(canInsertAmountIntoOutputSlot1(inventory) && canInsertItemIntoOutputSlot1(inventory, new ItemStack(ModItems.NETHERSTAR_FRAGMENT_RIGHT_FOIL.get(), 1))){
                resultItem = new ItemStack(ModItems.NETHERSTAR_FRAGMENT_RIGHT_FOIL.get(), 1);
                return true;
            }
        }

        return false;

    }

    private static boolean canInsertItemIntoOutputSlot1(SimpleContainer inventory, ItemStack itemStack) {
        return inventory.getItem(1).getItem() == itemStack.getItem() || inventory.getItem(1).isEmpty();
    }

    private static boolean canInsertAmountIntoOutputSlot1(SimpleContainer inventory) {

        return inventory.getItem(1).getMaxStackSize() > inventory.getItem(1).getCount();
    }
}

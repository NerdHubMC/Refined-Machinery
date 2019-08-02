package abused_master.refinedmachinery.tiles.machine;

import abused_master.abusedlib.tiles.BlockEntityBase;
import abused_master.abusedlib.utils.InventoryHelper;
import abused_master.refinedmachinery.registry.ModBlockEntities;
import javafx.geometry.BoundingBox;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;

//TODO ADD FILTER TO GUI
public class BlockEntityVacuum extends BlockEntityBase implements SidedInventory {

    public DefaultedList<ItemStack> inventory = DefaultedList.ofSize(14, ItemStack.EMPTY);

    public BlockEntityVacuum() {
        super(ModBlockEntities.VACUUM);
    }

    @Override
    public void fromTag(CompoundTag nbt) {
        super.fromTag(nbt);

        inventory = DefaultedList.ofSize(14, ItemStack.EMPTY);
        Inventories.fromTag(nbt, this.inventory);
    }

    @Override
    public CompoundTag toTag(CompoundTag nbt) {
        super.toTag(nbt);
        Inventories.toTag(nbt, this.inventory);
        return nbt;
    }

    @Override
    public void tick() {
        if(!world.isReceivingRedstonePower(pos)) {
            ItemEntity target = findTarget();

            if(target != null && InventoryHelper.insertItemIfPossible(this, target.getStack(), false)) {
                target.kill();
                this.markDirty();
                world.updateListeners(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
            }
        }
    }

    public ItemEntity findTarget() {
        List<ItemEntity> items = world.getEntities(ItemEntity.class, new Box(pos.add(-3, -3, -3), pos.add(3, 3, 3)));
        if(items.isEmpty()) {
            return null;
        }

        for (ItemEntity itemEntity : items) {
            if(!itemEntity.cannotPickup() && !itemEntity.getStack().isEmpty()) {
                return itemEntity;
            }
        }

        return null;
    }

    @Override
    public int[] getInvAvailableSlots(Direction direction) {
        return new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};
    }

    @Override
    public boolean canInsertInvStack(int i, ItemStack itemStack, @Nullable Direction direction) {
        return true;
    }

    @Override
    public boolean canExtractInvStack(int i, ItemStack itemStack, Direction direction) {
        return true;
    }

    @Override
    public int getInvSize() {
        return inventory.size();
    }

    @Override
    public ItemStack getInvStack(int i) {
        return inventory.get(i);
    }

    @Override
    public ItemStack removeInvStack(int i) {
        return Inventories.removeStack(this.inventory, i);
    }

    @Override
    public boolean canPlayerUseInv(PlayerEntity playerEntity) {
        if (this.world.getBlockEntity(this.pos) != this) {
            return false;
        } else {
            return playerEntity.squaredDistanceTo((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
        }
    }

    @Override
    public boolean isInvEmpty() {
        Iterator var1 = this.inventory.iterator();

        ItemStack itemStack_1;
        do {
            if (!var1.hasNext()) {
                return true;
            }

            itemStack_1 = (ItemStack)var1.next();
        } while(itemStack_1.isEmpty());

        return false;
    }

    @Override
    public void setInvStack(int i, ItemStack itemStack) {
        inventory.set(i, itemStack);
        this.markDirty();
    }

    @Override
    public ItemStack takeInvStack(int i, int i1) {
        return Inventories.splitStack(this.inventory, i, i1);
    }

    @Override
    public void clear() {
        inventory.clear();
    }
}

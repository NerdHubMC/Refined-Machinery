package abused_master.refinedmachinery.tiles.machine;

import abused_master.abusedlib.tiles.BlockEntityBase;
import abused_master.refinedmachinery.registry.ModBlockEntities;
import abused_master.refinedmachinery.utils.ItemHelper;
import abused_master.refinedmachinery.utils.linker.ILinkerHandler;
import nerdhub.cardinalenergy.api.IEnergyHandler;
import nerdhub.cardinalenergy.impl.EnergyStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.math.Direction;

import javax.annotation.Nullable;
import java.util.Iterator;

//TODO ADD UPGRADES
public class BlockEntityEnergyFurnace extends BlockEntityBase implements IEnergyHandler, SidedInventory, ILinkerHandler {

    public EnergyStorage storage = new EnergyStorage(100000);
    public DefaultedList<ItemStack> inventory = DefaultedList.create(2, ItemStack.EMPTY);
    private int upgradeTier = 1;
    private int smeltTime = 0;
    private int baseEnergyUsage = 400;

    public BlockEntityEnergyFurnace() {
        super(ModBlockEntities.ENERGY_FURNACE);
    }

    @Override
    public void fromTag(CompoundTag nbt) {
        super.fromTag(nbt);
        this.upgradeTier = nbt.getInt("upgradeTier");
        this.smeltTime = nbt.getInt("smeltTime");
        this.storage.readEnergyFromTag(nbt);

        inventory = DefaultedList.create(2, ItemStack.EMPTY);
        Inventories.fromTag(nbt, this.inventory);
    }

    @Override
    public CompoundTag toTag(CompoundTag nbt) {
        super.toTag(nbt);
        nbt.putInt("upgradeTier", upgradeTier);
        nbt.putInt("smeltTime", this.smeltTime);
        this.storage.writeEnergyToTag(nbt);
        Inventories.toTag(nbt, this.inventory);
        return nbt;
    }

    @Override
    public void tick() {
        if(canRun()) {
            smeltTime++;
            if(smeltTime >= this.getTotalSmeltTime()) {
                smeltTime = 0;
                this.smeltItem();
                world.updateListeners(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
            }
        }else if (!canRun() && smeltTime > 0) {
            smeltTime = 0;
        }
    }

    public ItemStack getOutputStack() {
        if (!inventory.get(0).isEmpty()) {
            Recipe recipe = world.getRecipeManager().getFirstMatch(RecipeType.SMELTING, this, world).orElse(null);
            return recipe != null ? recipe.getOutput().copy() : ItemStack.EMPTY;
        }

        return ItemStack.EMPTY;
    }

    public boolean canRun() {
        ItemStack output = getOutputStack();
        if(inventory.get(0).isEmpty() || output.isEmpty() || inventory.get(1).getCount() > 64 || storage.getEnergyStored() < getEnergyUsage()) {
            return false;
        }else if(!inventory.get(1).isEmpty()) {
            if (output.getItem() != inventory.get(1).getItem()) {
                return false;
            }
        }

        return true;
    }

    public void smeltItem() {
        ItemStack output = getOutputStack();
        if(!output.isEmpty()) {
            if(!world.isClient) {
                if (inventory.get(1).isEmpty()) {
                    inventory.set(1, output);
                } else {
                    inventory.get(1).setCount(inventory.get(1).getCount() + 1);
                }

                inventory.get(0).setCount(inventory.get(0).getCount() - 1);
            }

            storage.extractEnergy(getEnergyUsage());
        }
    }

    public int getEnergyUsage() {
        return baseEnergyUsage * upgradeTier;
    }

    public int getSmeltTime() {
        return smeltTime;
    }

    public int getTotalSmeltTime() {
        return 120 / this.upgradeTier;
    }

    @Override
    public int[] getInvAvailableSlots(Direction direction) {
        return new int[] {0, 1};
    }

    @Override
    public boolean canInsertInvStack(int i, ItemStack itemStack, @Nullable Direction direction) {
        return i != 1;
    }

    @Override
    public boolean canExtractInvStack(int i, ItemStack itemStack, Direction direction) {
        return i != 0;
    }

    @Override
    public int getInvSize() {
        return inventory.size();
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
    public void clear() {
        inventory.clear();
    }

    @Override
    public void link(PlayerEntity player, CompoundTag tag) {
        ItemHelper.linkBlockPos(world, pos, player, tag);
    }
}

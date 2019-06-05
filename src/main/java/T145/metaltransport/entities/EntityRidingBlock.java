package T145.metaltransport.entities;

import T145.metaltransport.api.constants.RegistryMT;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

public class EntityRidingBlock extends Entity {

	private static final DataParameter<ItemStack> DISPLAY_STACK = EntityDataManager.createKey(EntityRidingBlock.class, DataSerializers.ITEM_STACK);
	private static final String TAG_DISPLAY_STACK = "DisplayStack";

	public EntityRidingBlock(World world) {
		super(world);
	}

	public EntityRidingBlock(World world, ItemStack stack) {
		super(world);
		this.setDisplayStack(stack);
	}

	public void setDisplayStack(ItemStack stack) {
		try {
			if (Block.getBlockFromItem(stack.getItem()) == null) {
				throw new NullPointerException(" [EntityRidingBlock] ItemStack must have a block in its contents, not an item!");
			}
		} catch (NullPointerException err) {
			RegistryMT.LOG.catching(err);
		}

		if (stack.getCount() > 1) {
			ItemStack newStack = stack.copy();
			newStack.setCount(1);
			this.dataManager.set(DISPLAY_STACK, newStack);
		} else {
			this.dataManager.set(DISPLAY_STACK, stack);
		}
	}

	public ItemStack getDisplayStack() {
		return this.dataManager.get(DISPLAY_STACK);
	}

	@Override
	protected void entityInit() {
		this.noClip = true;
		this.dataManager.register(DISPLAY_STACK, ItemStack.EMPTY);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tag) {
		NBTTagCompound stackTag = tag.getCompoundTag(TAG_DISPLAY_STACK);
		ItemStack stack = new ItemStack(stackTag);

		if (!stack.isEmpty()) {
			this.setDisplayStack(stack);
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag) {
		NBTTagCompound stackTag = new NBTTagCompound();

		this.getDisplayStack().writeToNBT(stackTag);
		tag.setTag(TAG_DISPLAY_STACK, stackTag);
	}

	@Override
	public void dismountRidingEntity() {
		super.dismountRidingEntity();

		if (!this.world.isRemote) {
			this.setDead();

			if (this.world.getGameRules().getBoolean("doEntityDrops")) {
				ItemStack stack = this.getDisplayStack();

				if (this.hasCustomName()) {
					stack.setStackDisplayName(this.getCustomNameTag());
				}

				this.entityDropItem(stack, 0.0F);
			}
		}
	}
}

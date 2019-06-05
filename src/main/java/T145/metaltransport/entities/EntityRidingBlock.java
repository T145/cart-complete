package T145.metaltransport.entities;

import javax.annotation.Nullable;

import com.google.common.base.Optional;

import T145.metaltransport.api.constants.RegistryMT;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

public class EntityRidingBlock extends Entity {

	private static final DataParameter<Optional<IBlockState>> INTERNAL_BLOCK = EntityDataManager
			.<Optional<IBlockState>>createKey(EntityRidingBlock.class, DataSerializers.OPTIONAL_BLOCK_STATE);

	public EntityRidingBlock(World world) {
		super(world);
	}

	public void setInternalBlockState(@Nullable IBlockState state) {
		RegistryMT.LOG.info(state);
		this.dataManager.set(INTERNAL_BLOCK, Optional.fromNullable(state));
	}

	@Nullable
	public IBlockState getInternalBlockState() {
		return this.dataManager.get(INTERNAL_BLOCK).orNull();
	}

	@Override
	protected void entityInit() {
		this.dataManager.register(INTERNAL_BLOCK, Optional.absent());
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound tag) {
		IBlockState iblockstate = this.getInternalBlockState();

		if (iblockstate != null) {
			tag.setShort("carried", (short) Block.getIdFromBlock(iblockstate.getBlock()));
			tag.setShort("carriedData", (short) iblockstate.getBlock().getMetaFromState(iblockstate));
		}
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound tag) {
		IBlockState iblockstate;

		if (tag.hasKey("carried", 8)) {
			iblockstate = Block.getBlockFromName(tag.getString("carried"))
					.getStateFromMeta(tag.getShort("carriedData") & 65535);
		} else {
			iblockstate = Block.getBlockById(tag.getShort("carried"))
					.getStateFromMeta(tag.getShort("carriedData") & 65535);
		}

		if (iblockstate == null || iblockstate.getBlock() == null || iblockstate.getMaterial() == Material.AIR) {
			iblockstate = null;
		}

		this.setInternalBlockState(iblockstate);
	}

	@Override
	public void setDead() {
		if (!this.world.isRemote && this.world.getGameRules().getBoolean("doEntityDrops")) {
			IBlockState state = this.getInternalBlockState();
			ItemStack stack = new ItemStack(state.getBlock());

			if (this.hasCustomName()) {
				stack.setStackDisplayName(this.getCustomNameTag());
			}

			this.entityDropItem(stack, 0.0F);
		}

		super.setDead();
	}

	@Override
	public void dismountRidingEntity() {
		super.dismountRidingEntity();
		this.setDead();
	}
}

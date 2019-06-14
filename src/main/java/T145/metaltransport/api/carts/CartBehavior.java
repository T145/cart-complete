package T145.metaltransport.api.carts;

import java.util.Optional;
import java.util.UUID;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class CartBehavior implements ICartBehavior {

	protected int dimId;
	protected UUID entityId;
	private Optional<EntityMinecart> cart = Optional.empty();

	public CartBehavior(EntityMinecart cart) {
		this.dimId = cart.world.provider.getDimension();
		this.entityId = cart.getPersistentID();
	}

	public int getDimension() {
		return dimId;
	}

	protected void setDimension(int dimId) {
		this.dimId = dimId;
	}

	public UUID getEntityId() {
		return entityId;
	}

	protected void setEntityId(UUID entityId) {
		this.entityId = entityId;
	}

	private Entity getEntityFromUUID(World world) {
		for (Entity entity : world.getLoadedEntityList()) {
			if (entity.getPersistentID().equals(entityId)) {
				return entity;
			}
		}
		return null;
	}

	public EntityMinecart getCart() {
		if (!cart.isPresent()) {
			World world = DimensionManager.getWorld(dimId);
			Entity entity = this.getEntityFromUUID(world);

			if (entity instanceof EntityMinecart) {
				cart = Optional.of((EntityMinecart) entity);
			}
		}

		return cart.get();
	}

	@Override
	public IBlockState customizeState(IBlockState state) {
		return state;
	}

	@OverridingMethodsMustInvokeSuper
	@Override
	public NBTTagCompound serialize() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("DimId", this.dimId);
		tag.setUniqueId("EntityId", this.entityId);
		return tag;
	}

	@OverridingMethodsMustInvokeSuper
	@Override
	public ICartBehavior deserialize(NBTTagCompound tag) {
		this.setDimension(tag.getInteger("DimId"));
		this.setEntityId(tag.getUniqueId("EntityId"));
		return this;
	}

	@Override
	public void tickServer(World world, BlockPos pos) {}

	@Override
	public void activate(EntityPlayer player, EnumHand hand) {}

	@Override
	public void attackCartFrom(DamageSource source, float amount) {}

	@Override
	public void killMinecart(DamageSource source, boolean dropItems) {}

	@Override
	public void onDeath() {}

	@Override
	public void fall(float distance, float damageMultiplier) {}

	@Override
	public void onActivatorRailPass(int x, int y, int z, boolean receivingPower) {}

	@Override
	public void moveAlongTrack(BlockPos pos, IBlockState rail) {}

	@Override
	public void applyDrag() {}

	@Override
	public void onDeletion() {}

	@Override
	public boolean ignoreItemEntityData() {
		// default entity value
		return false;
	}
}

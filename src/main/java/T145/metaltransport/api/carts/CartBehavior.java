package T145.metaltransport.api.carts;

import java.util.Optional;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CartBehavior implements ICartBehavior {

	protected int dimId;
	protected int entityId;
	private Optional<EntityMinecart> cart = Optional.empty();

	public CartBehavior(EntityMinecart cart) {
		this.dimId = cart.world.provider.getDimension();
		this.entityId = cart.getEntityId();
	}

	public int getDimension() {
		return dimId;
	}

	protected void setDimension(int dimId) {
		this.dimId = dimId;
	}

	public int getEntityId() {
		return entityId;
	}

	protected void setEntityId(int entityId) {
		this.entityId = entityId;
	}

	public EntityMinecart getCart() {
		if (!cart.isPresent()) {
			World world = DimensionManager.getWorld(dimId);
			Entity entity = world.getEntityByID(entityId);

			if (entity instanceof EntityMinecart) {
				cart = Optional.of((EntityMinecart) entity);
			}
		}

		return cart.get();
	}

	@OverridingMethodsMustInvokeSuper
	@Override
	public NBTTagCompound serialize() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("DimId", this.dimId);
		tag.setInteger("EntityId", this.entityId);
		return tag;
	}

	@OverridingMethodsMustInvokeSuper
	@Override
	public ICartBehavior deserialize(NBTTagCompound tag) {
		this.setDimension(tag.getInteger("DimId"));
		this.setEntityId(tag.getInteger("EntityId"));
		return this;
	}

	@Override
	public void tick() {}

	@Override
	public void tickDataManager(DataParameter<?> key) {}

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
	public boolean ignoreItemEntityData() {
		// default entity value
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void handleStatusUpdate(byte id) {}
}

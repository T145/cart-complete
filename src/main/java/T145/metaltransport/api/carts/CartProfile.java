package T145.metaltransport.api.carts;

import java.util.Optional;
import java.util.UUID;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import T145.metaltransport.api.consts.RegistryMT;
import T145.metaltransport.api.profiles.ICartProfile;
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

public class CartProfile implements ICartProfile {

	private Optional<EntityMinecart> cart = Optional.empty();
	private int dim;
	private UUID entityId;

	public CartProfile(EntityMinecart cart) {
		World world = cart.world;
		this.entityId = cart.getPersistentID();
	}

	public EntityMinecart getCart() {
		if (!cart.isPresent()) {
			World world = DimensionManager.getWorld(dim);

			for (Entity entity : world.getLoadedEntityList()) {
				if (entity instanceof EntityMinecart && !entity.isDead && entity.getPersistentID().equals(this.entityId)) {
					this.cart = Optional.of((EntityMinecart) entity);
					break;
				}
			}

			if (!cart.isPresent()) {
				RegistryMT.LOG.error("Cart entity is still unknown after reading known data!");
			}
		}

		return cart.get();
	}

	@OverridingMethodsMustInvokeSuper
	@Override
	public NBTTagCompound serialize() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("Dimension", dim);
		tag.setUniqueId("EntityId", this.entityId);
		return tag;
	}

	@OverridingMethodsMustInvokeSuper
	@Override
	public ICartProfile deserialize(NBTTagCompound tag) {
		this.dim = tag.getInteger("Dimension");
		this.entityId = tag.getUniqueId("EntityId");
		return this;
	}

	@Override
	public void tickServer(World world, BlockPos pos) {}

	@Override
	public void activate(EntityPlayer player, EnumHand hand) {}

	@Override
	public boolean attackCart(DamageSource source, float amount) {
		return false;
	}

	@Override
	public void killCart(DamageSource source, boolean dropItems) {}

	@Override
	public void onProfileDeletion() {}

	@Override
	public void onCartDeath() {}

	@Override
	public void fall(float distance, float damageMultiplier) {}

	@Override
	public void onActivatorRailPass(int x, int y, int z, boolean powered) {}

	@Override
	public void moveAlongTrack(BlockPos pos, IBlockState rail) {}

	@Override
	public void applyDrag() {}
}

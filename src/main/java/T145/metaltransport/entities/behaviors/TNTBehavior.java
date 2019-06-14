package T145.metaltransport.entities.behaviors;

import java.util.Random;

import T145.metaltransport.api.carts.CartBehavior;
import T145.metaltransport.api.carts.ICartBehavior;
import T145.metaltransport.api.carts.ICartBehaviorFactory;
import T145.metaltransport.core.MetalTransport;
import T145.metaltransport.network.client.SpawnSmokeParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TNTBehavior extends CartBehavior {

	public static class TNTBehaviorFactory implements ICartBehaviorFactory {

		@Override
		public ICartBehavior createBehavior(EntityMinecart cart) {
			return new TNTBehavior(cart);
		}
	}

	private int fuse = -1;

	public TNTBehavior(EntityMinecart cart) {
		super(cart);
	}

	protected void detonateCart(double radius) {
		EntityMinecart cart = this.getCart();
		World world = cart.world;

		if (!world.isRemote) {
			double scaledRadius = Math.sqrt(radius);

			if (scaledRadius > 5.0D) {
				scaledRadius = 5.0D;
			}

			world.createExplosion(cart, cart.posX, cart.posY, cart.posZ, (float) (4.0D + world.rand.nextDouble() * 1.5D * scaledRadius), true);
			cart.setDead();
		}
	}

	public void ignite() {
		EntityMinecart cart = this.getCart();
		World world = cart.world;
		this.fuse = 80;

		if (!cart.isSilent()) {
			world.playSound(null, cart.posX, cart.posY, cart.posZ, SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
		}
	}

	@Override
	public NBTTagCompound serialize() {
		NBTTagCompound tag = super.serialize();
		tag.setInteger("Fuse", this.fuse);
		return tag;
	}

	@Override
	public ICartBehavior deserialize(NBTTagCompound tag) {
		super.deserialize(tag);
		this.fuse = tag.getInteger("Fuse");
		return this;
	}

	private double getHorizontalMotion(EntityMinecart cart) {
		return cart.motionX * cart.motionX + cart.motionZ * cart.motionZ;
	}

	@Override
	public void tickServer(World world, BlockPos pos) {
		EntityMinecart cart = this.getCart();

		if (this.fuse > 0) {
			--this.fuse;
			MetalTransport.NETWORK.sendToAllAround(new SpawnSmokeParticles(pos), world, pos);
		} else if (this.fuse == 0) {
			this.detonateCart(this.getHorizontalMotion(cart));
		}

		if (cart.collidedHorizontally) {
			double motion = this.getHorizontalMotion(cart);

			if (motion >= 0.01D) {
				this.detonateCart(motion);
			}
		}
	}

	@Override
	public void attackCartFrom(DamageSource source, float amount) {
		Entity entity = source.getImmediateSource();

		if (entity instanceof EntityArrow) {
			EntityArrow arrow = (EntityArrow) entity;

			if (arrow.isBurning()) {
				this.detonateCart(arrow.motionX * arrow.motionX + arrow.motionY * arrow.motionY + arrow.motionZ * arrow.motionZ);
			}
		}
	}

	@Override
	public void killMinecart(DamageSource source, boolean dropItems) {
		EntityMinecart cart = this.getCart();
		double motion = this.getHorizontalMotion(cart);

		if ((source.isFireDamage() || source.isExplosion() || motion >= 0.01D) && fuse < 0) {
			World world = cart.world;
			Random rand = world.rand;

			this.ignite();
			this.fuse = rand.nextInt(20) + rand.nextInt(20);
		}
	}

	@Override
	public void fall(float distance, float damageMultiplier) {
		if (distance >= 3.0F) {
			float f = distance / 10.0F;
			this.detonateCart(f * f);
		}
	}

	@Override
	public void onActivatorRailPass(int x, int y, int z, boolean receivingPower) {
		if (receivingPower && this.fuse < 0) {
			this.ignite();
		}
	}
}

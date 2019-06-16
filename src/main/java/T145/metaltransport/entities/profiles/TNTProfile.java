package T145.metaltransport.entities.profiles;

import java.util.Random;

import T145.metaltransport.MetalTransport;
import T145.metaltransport.api.carts.CartProfile;
import T145.metaltransport.api.carts.ICartProfileFactory;
import T145.metaltransport.net.client.SpawnSmokeParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TNTProfile extends CartProfile {

	public static class TNTBehaviorFactory implements ICartProfileFactory {

		@Override
		public TNTProfile createProfile(EntityMinecart cart) {
			return new TNTProfile(cart);
		}
	}

	private int fuse = -1;

	public TNTProfile(EntityMinecart cart) {
		super(cart);
	}

	protected void detonateCart(double radius) {
		EntityMinecart cart = this.getCart();
		World world = cart.world;
		double scaledRadius = Math.sqrt(radius);

		if (scaledRadius > 5.0D) {
			scaledRadius = 5.0D;
		}

		world.createExplosion(cart, cart.posX, cart.posY, cart.posZ, (float) (4.0D + world.rand.nextDouble() * 1.5D * scaledRadius), true);
		cart.setDead();
	}

	public void ignite() {
		EntityMinecart cart = this.getCart();
		this.fuse = 80;

		if (!cart.isSilent()) {
			cart.world.playSound(null, cart.posX, cart.posY, cart.posZ, SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
		}
	}

	@Override
	public NBTTagCompound serialize() {
		NBTTagCompound tag = super.serialize();
		tag.setInteger("Fuse", this.fuse);
		return tag;
	}

	@Override
	public TNTProfile deserialize(NBTTagCompound tag) {
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
			MetalTransport.NETWORK.sendToAllAround(new SpawnSmokeParticles(pos));
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
	public boolean attackCart(DamageSource source, float amount) {
		Entity entity = source.getImmediateSource();

		if (entity instanceof EntityArrow) {
			EntityArrow arrow = (EntityArrow) entity;

			if (arrow.isBurning()) {
				this.detonateCart(arrow.motionX * arrow.motionX + arrow.motionY * arrow.motionY + arrow.motionZ * arrow.motionZ);
			}
		}
		return false;
	}

	@Override
	public void killCart(DamageSource source, boolean dropItems) {
		EntityMinecart cart = this.getCart();
		double motion = this.getHorizontalMotion(cart);

		if ((source.isFireDamage() || source.isExplosion() || motion >= 0.01D) && fuse < 0) {
			Random rand = cart.world.rand;

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
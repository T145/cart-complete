package T145.metaltransport.entities.behaviors;

import T145.metaltransport.api.carts.CartBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TNTBehavior extends CartBehavior {

	private int cartFuse = -1;

	public TNTBehavior() {
		super(Blocks.TNT);
	}

	public int getFuseTicks() {
		return this.cartFuse;
	}

	public boolean isIgnited() {
		return this.cartFuse > -1;
	}

	@Override
	public NBTTagCompound serialize() {
		NBTTagCompound tag = super.serialize();
		tag.setInteger("TNTFuse", this.cartFuse);
		return tag;
	}

	@Override
	public TNTBehavior deserialize(NBTTagCompound tag) {
		if (tag.hasKey("TNTFuse", 99)) {
			this.cartFuse = tag.getInteger("TNTFuse");
		}
		return this;
	}

	public double getHorizontalMotion(EntityMinecart cart) {
		return cart.motionX * cart.motionX + cart.motionZ * cart.motionZ;
	}

	@Override
	public void tick(EntityMinecart cart) {
		if (this.cartFuse > 0) {
			--this.cartFuse;
			cart.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, cart.posX, cart.posY + 0.5D, cart.posZ, 0.0D, 0.0D, 0.0D);
		} else if (this.cartFuse == 0) {
			this.detonateCart(cart, this.getHorizontalMotion(cart));
		}

		if (cart.collidedHorizontally) {
			double motion = this.getHorizontalMotion(cart);

			if (motion >= 0.01D) {
				this.detonateCart(cart, motion);
			}
		}
	}

	@Override
	public void attackCartFrom(EntityMinecart cart, DamageSource source, float amount) {
		Entity entity = source.getImmediateSource();

		if (entity instanceof EntityArrow) {
			EntityArrow arrow = (EntityArrow) entity;

			if (arrow.isBurning()) {
				this.detonateCart(cart, arrow.motionX * arrow.motionX + arrow.motionY * arrow.motionY + arrow.motionZ * arrow.motionZ);
			}
		}
	}

	@Override
	public void killMinecart(EntityMinecart cart, DamageSource source) {
		World world = cart.world;
		double motion = this.getHorizontalMotion(cart);

		if (!source.isFireDamage() && !source.isExplosion() && motion < 0.01D) {
			// do nothing?
		} else {
			if (this.cartFuse < 0) {
				this.ignite(cart);
				this.cartFuse = world.rand.nextInt(20) + world.rand.nextInt(20);
			}
		}
	}

	public void ignite(EntityMinecart cart) {
		World world = cart.world;

		this.cartFuse = 80;

		if (!world.isRemote) {
			world.setEntityState(cart, (byte) 10);

			if (!cart.isSilent()) {
				world.playSound(null, cart.posX, cart.posY, cart.posZ, SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
			}
		}
	}

	protected void detonateCart(EntityMinecart cart, double radius) {
		World world = cart.world;

		if (!world.isRemote) {
			double d0 = Math.sqrt(radius);

			if (d0 > 5.0D) {
				d0 = 5.0D;
			}

			world.createExplosion(cart, cart.posX, cart.posY, cart.posZ, (float) (4.0D + world.rand.nextDouble() * 1.5D * d0), true);
			cart.setDead();
		}
	}

	@Override
	public void fall(EntityMinecart cart, float distance, float damageMultiplier) {
		if (distance >= 3.0F) {
			float f = distance / 10.0F;
			this.detonateCart(cart, (double) (f * f));
		}
	}

	@Override
	public void onActivatorRailPass(EntityMinecart cart, int x, int y, int z, boolean receivingPower) {
		if (receivingPower && this.cartFuse < 0) {
			this.ignite(cart);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void handleStatusUpdate(EntityMinecart cart, byte id) {
		if (id == 10) {
			this.ignite(cart);
		}
	}
}

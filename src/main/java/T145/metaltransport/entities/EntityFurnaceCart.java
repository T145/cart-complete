package T145.metaltransport.entities;

import T145.metaltransport.api.consts.CartType;
import T145.metaltransport.api.obj.SerializersMT;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityFurnaceCart extends EntityMinecart {

	public static final float MAX_SPEED = 0.499F;
	public static final float FORCE_DAMPEN_FACTOR = 3.5F;
	public static final int BURN_TIME_CAP = 102400;

	private static final DataParameter<CartType> CART_TYPE = EntityDataManager.createKey(EntityFurnaceCart.class, SerializersMT.CART_TYPE);
	private static final DataParameter<Boolean> POWERED = EntityDataManager.createKey(EntityFurnaceCart.class, DataSerializers.BOOLEAN);
	private int fuel;

	public EntityFurnaceCart(World world) {
		super(world);
	}

	public EntityFurnaceCart(World world, double x, double y, double z) {
		super(world, x, y, z);
	}

	protected boolean isBurningFuel() {
		return this.dataManager.get(POWERED);
	}

	protected void setBurningFuel(boolean powered) {
		this.dataManager.set(POWERED, powered);
	}

	@Override
	public Type getType() {
		return Type.FURNACE;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(CART_TYPE, CartType.IRON);
		this.dataManager.register(POWERED, false);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag) {
		super.writeEntityToNBT(tag);
		tag.setInteger("Fuel", this.fuel);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tag) {
		super.readEntityFromNBT(tag);
		this.fuel = tag.getInteger("Fuel");
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (this.fuel > 0) {
			--this.fuel;
		}

		this.setBurningFuel(this.fuel > 0);

		if (this.isBurningFuel() && this.rand.nextInt(4) == 0) {
			this.world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.posX, this.posY + 0.8D, this.posZ, 0, 0, 0);
		}
	}

	@Override
	protected double getMaximumSpeed() {
		return 0.2D;
	}

	@Override
	public void killMinecart(DamageSource source) {
		super.killMinecart(source);

		if (!source.isExplosion() && this.world.getGameRules().getBoolean("doEntityDrops")) {
			this.entityDropItem(new ItemStack(Blocks.FURNACE, 1), 0.0F);
		}
	}

	private boolean isSpeeding() {
		if (Math.abs(motionX) > MAX_SPEED) {
			motionX = Math.copySign(MAX_SPEED, motionX);
			return true;
		}

		if (Math.abs(motionZ) > MAX_SPEED) {
			motionZ = Math.copySign(MAX_SPEED, motionZ);
			return true;
		}

		return false;
	}

	@Override
	protected void applyDrag() {
		motionX *= getDragAir();
		motionY *= 0.0D;
		motionZ *= getDragAir();

		if (this.isBurningFuel()) {
			float force = 0.15F;

			if (isSpeeding()) {
				force *= FORCE_DAMPEN_FACTOR;
			}

			double yaw = rotationYaw * Math.PI / 180D;
			motionX += Math.cos(yaw) * force;
			motionZ += Math.sin(yaw) * force;
		}

		float limit = 0.3F;
		motionX = Math.copySign(Math.min(Math.abs(motionX), limit), motionX);
		motionZ = Math.copySign(Math.min(Math.abs(motionZ), limit), motionZ);
	}

	@Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
		if (super.processInitialInteract(player, hand)) {
			return true;
		}

		ItemStack stack = player.getHeldItem(hand);

		if (!stack.isEmpty() && fuel < BURN_TIME_CAP) {
			if (!player.capabilities.isCreativeMode) {
				stack.shrink(1);
			}

			fuel += TileEntityFurnace.getItemBurnTime(stack);
		}

		return true;
	}

	@Override
	public IBlockState getDefaultDisplayTile() {
		return (this.isBurningFuel() ? Blocks.LIT_FURNACE : Blocks.FURNACE).getDefaultState();
	}
}
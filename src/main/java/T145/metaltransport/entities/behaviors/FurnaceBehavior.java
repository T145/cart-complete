package T145.metaltransport.entities.behaviors;

import T145.metaltransport.api.carts.CartBehavior;
import T145.metaltransport.api.carts.ICartBehavior;
import T145.metaltransport.api.carts.ICartBehaviorFactory;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class FurnaceBehavior extends CartBehavior {

	public static class FurnaceBehaviorFactory implements ICartBehaviorFactory {

		@Override
		public ICartBehavior createBehavior(EntityMinecart cart) {
			return new FurnaceBehavior(cart);
		}
	}

	private boolean powered;
	private boolean prevPowered;
	private int fuel;
	public double pushX;
	public double pushZ;

	public FurnaceBehavior(EntityMinecart cart) {
		super(cart);
	}

	public IBlockState getFurnaceState() {
		return (this.powered ? Blocks.LIT_FURNACE : Blocks.FURNACE)
				.getDefaultState().withProperty(BlockFurnace.FACING, EnumFacing.NORTH);
	}

	public void setPowered(boolean powered) {
		this.prevPowered = this.powered;
		this.powered = powered;
	}

	public double getHorizontalMotion() {
		return this.pushX * this.pushX + this.pushZ * this.pushZ;
	}

	public double getHorizontalCartMotion() {
		EntityMinecart cart = this.getCart();
		return cart.motionX * cart.motionX + cart.motionZ * cart.motionZ;
	}

	@Override
	public double getMaxCartSpeed() {
		return 0.2D;
	}

	@Override
	public NBTTagCompound serialize() {
		NBTTagCompound tag = super.serialize();
		tag.setBoolean("Powered", this.powered);
		tag.setBoolean("PrevPowered", this.prevPowered);
		tag.setDouble("PushX", this.pushX);
		tag.setDouble("PushZ", this.pushZ);
		tag.setInteger("Fuel", fuel);
		return tag;
	}

	@Override
	public ICartBehavior deserialize(NBTTagCompound tag) {
		super.deserialize(tag);
		this.powered = tag.getBoolean("Powered");
		this.prevPowered = tag.getBoolean("PrevPowered");
		this.pushX = tag.getDouble("PushX");
		this.pushZ = tag.getDouble("PushZ");
		this.fuel = tag.getInteger("Fuel");
		return this;
	}

	@Override
	public void tickServer(World world, BlockPos pos) {
		EntityMinecart cart = this.getCart();

		if (this.fuel > 0) {
			--this.fuel;
		}

		if (this.fuel <= 0) {
			this.pushX = 0.0D;
			this.pushZ = 0.0D;
		}

		this.setPowered(this.fuel > 0);

		if (powered && world.rand.nextInt(4) == 0) {
			// TODO: Make packet for handling particles
			//world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, cart.posX, cart.posY + 0.8D, cart.posZ, 0, 0, 0);
		}

		if (prevPowered != powered) {
			cart.setDisplayTile(getFurnaceState());
		}
	}

	@Override
	public void activate(EntityPlayer player, EnumHand hand) {
		EntityMinecart cart = this.getCart();
		ItemStack fuelStack = player.getHeldItem(hand);

		if (fuelStack.getItem() == Items.COAL && this.fuel + 3600 <= 32000) {
			if (!player.capabilities.isCreativeMode) {
				fuelStack.shrink(1);
			}

			this.fuel += 3600;
		}

		this.pushX = cart.posX - player.posX;
		this.pushZ = cart.posZ - player.posZ;
	}

	@Override
	public void moveAlongTrack(BlockPos pos, IBlockState rail) {
		EntityMinecart cart = this.getCart();
		double motion = getHorizontalMotion();

		if (motion > 1.0E-4D && getHorizontalCartMotion() > 0.001D) {
			motion = MathHelper.sqrt(motion);
			this.pushX /= motion;
			this.pushZ /= motion;

			if (this.pushX * cart.motionX + this.pushZ * cart.motionZ < 0.0D) {
				this.pushX = 0.0D;
				this.pushZ = 0.0D;
			} else {
				double d1 = motion / this.getMaxCartSpeed();
				this.pushX *= d1;
				this.pushZ *= d1;
			}
		}
	}

	@Override
	public void applyDrag() {
		EntityMinecart cart = this.getCart();
		double motion = getHorizontalMotion();

		if (motion > 1.0E-4D) {
			motion = MathHelper.sqrt(motion);
			this.pushX /= motion;
			this.pushZ /= motion;
			cart.motionX *= 0.8D;
			cart.motionY *= 0.0D;
			cart.motionZ *= 0.8D;
			cart.motionX += this.pushX * 1.0D;
			cart.motionZ += this.pushZ * 1.0D;
		} else {
			cart.motionX *= 0.98D;
			cart.motionY *= 0.0D;
			cart.motionZ *= 0.98D;
		}
	}
}

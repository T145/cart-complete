package T145.metaltransport.entities.behaviors;

import T145.metaltransport.api.carts.CartBehavior;
import net.minecraft.block.Block;
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

	private boolean powered;
	private boolean prevPowered;
	private int fuel;
	public double pushX;
	public double pushZ;

	public FurnaceBehavior() {
		super(new Block[] { Blocks.FURNACE, Blocks.LIT_FURNACE });
	}

	public IBlockState getDefaultDisplayTile() {
		return (this.powered ? Blocks.LIT_FURNACE : Blocks.FURNACE).getDefaultState().withProperty(BlockFurnace.FACING, EnumFacing.NORTH);
	}

	private void setPowered(boolean powered) {
		this.prevPowered = this.powered;
		this.powered = powered;
	}

	@Override
	public NBTTagCompound serialize() {
		NBTTagCompound tag = super.serialize();
		tag.setBoolean("Powered", this.powered);
		tag.setDouble("PushX", this.pushX);
		tag.setDouble("PushZ", this.pushZ);
		tag.setShort("Fuel", (short) this.fuel);
		return tag;
	}

	@Override
	public void deserialize(NBTTagCompound tag) {
		super.deserialize(tag);
		this.setPowered(powered);
		this.pushX = tag.getDouble("PushX");
		this.pushZ = tag.getDouble("PushZ");
		this.fuel = tag.getShort("Fuel");
	}

	@Override
	public void tick(EntityMinecart cart) {
		if (this.fuel > 0) {
			--this.fuel;
		}

		if (this.fuel <= 0) {
			this.pushX = 0.0D;
			this.pushZ = 0.0D;
		}

		this.setPowered(this.fuel > 0);

		World world = cart.world;

		if (this.powered && world.rand.nextInt(4) == 0) {
			world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, cart.posX, cart.posY + 0.8D, cart.posZ, 0.0D, 0.0D, 0.0D);
		}

		if (this.prevPowered != powered) {
			cart.setDisplayTile(getDefaultDisplayTile());
		}
	}

	@Override
	public double getMaxCartSpeed() {
		return 0.2D;
	}

	@Override
	public void moveAlongTrack(EntityMinecart cart, BlockPos pos, IBlockState rail) {
		double d0 = this.pushX * this.pushX + this.pushZ * this.pushZ;

		if (d0 > 1.0E-4D && cart.motionX * cart.motionX + cart.motionZ * cart.motionZ > 0.001D) {
			d0 = (double) MathHelper.sqrt(d0);
			this.pushX /= d0;
			this.pushZ /= d0;

			if (this.pushX * cart.motionX + this.pushZ * cart.motionZ < 0.0D) {
				this.pushX = 0.0D;
				this.pushZ = 0.0D;
			} else {
				double d1 = d0 / this.getMaxCartSpeed();
				this.pushX *= d1;
				this.pushZ *= d1;
			}
		}
	}

	@Override
	public void applyDrag(EntityMinecart cart) {
		double d0 = this.pushX * this.pushX + this.pushZ * this.pushZ;

		if (d0 > 1.0E-4D) {
			d0 = (double) MathHelper.sqrt(d0);
			this.pushX /= d0;
			this.pushZ /= d0;
			double d1 = 1.0D;
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

	@Override
	public void activate(EntityMinecart cart, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);

		// TODO: Add compatibility w/ all fuels, not just coal
		// The EntityMinecartFurnace only supports coal too, this is copy-pasted

		if (stack.getItem() == Items.COAL && this.fuel + 3600 <= 32000) {
			if (!player.capabilities.isCreativeMode) {
				stack.shrink(1);
			}

			this.fuel += 3600;
		}

		this.pushX = cart.posX - player.posX;
		this.pushZ = cart.posZ - player.posZ;
	}
}

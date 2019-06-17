package T145.metaltransport.entities.profiles;

import T145.metaltransport.MetalTransport;
import T145.metaltransport.api.carts.CartProfile;
import T145.metaltransport.api.profiles.ICartProfile;
import T145.metaltransport.api.profiles.ICartProfileFactory;
import T145.metaltransport.net.client.SpawnSmokeParticles;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FurnaceProfile extends CartProfile {

	public static class FurnaceProfileFactory implements ICartProfileFactory {

		@Override
		public ICartProfile create(EntityMinecart cart) {
			return new FurnaceProfile(cart);
		}
	}

	public static final float MAX_SPEED = 0.499F;
	public static final float FORCE_DAMPEN_FACTOR = 3.5F;
	public static final int BURN_TIME_CAP = 102400;

	private boolean powered;
	private boolean prevPowered;
	private short fuel;

	public IBlockState getFurnaceState() {
		return (this.powered ? Blocks.LIT_FURNACE : Blocks.FURNACE).getDefaultState();
	}

	public void setPowered(boolean powered) {
		this.prevPowered = this.powered;
		this.powered = powered;
	}

	public FurnaceProfile(EntityMinecart cart) {
		super(cart);
	}

	@Override
	public NBTTagCompound serialize() {
		NBTTagCompound tag = super.serialize();
		tag.setBoolean("Powered", this.powered);
		tag.setBoolean("PrevPowered", this.prevPowered);
		tag.setShort("Fuel", this.fuel);
		return tag;
	}

	@Override
	public ICartProfile deserialize(NBTTagCompound tag) {
		super.deserialize(tag);
		this.powered = tag.getBoolean("Powered");
		this.prevPowered = tag.getBoolean("PrevPowered");
		this.fuel = tag.getShort("Fuel");
		return this;
	}

	@Override
	public void tickServer(World world, BlockPos pos) {
		EntityMinecart cart = this.getCart();

		if (this.fuel > 0) {
			--this.fuel;
		}

		this.setPowered(this.fuel > 0);

		if (this.powered && world.rand.nextInt(4) == 0) {
			MetalTransport.NETWORK.sendToAllAround(new SpawnSmokeParticles(pos));
		}

		if (this.prevPowered != this.powered) {
			cart.setDisplayTile(getFurnaceState());
		}
	}

	@Override
	public void activate(EntityPlayer player, EnumHand hand) {
		EntityMinecart cart = this.getCart();
		ItemStack stack = player.getHeldItem(hand);

		if (!stack.isEmpty() && fuel < BURN_TIME_CAP) {
			if (!player.capabilities.isCreativeMode) {
				stack.shrink(1);
			}

			fuel += TileEntityFurnace.getItemBurnTime(stack);
		}
	}

	private boolean isSpeeding(EntityMinecart cart) {
		if (Math.abs(cart.motionX) > MAX_SPEED) {
			cart.motionX = Math.copySign(MAX_SPEED, cart.motionX);
			return true;
		}

		if (Math.abs(cart.motionZ) > MAX_SPEED) {
			cart.motionZ = Math.copySign(MAX_SPEED, cart.motionZ);
			return true;
		}

		return false;
	}

	@Override
	public void applyDrag() {
		EntityMinecart cart = this.getCart();

		cart.motionX *= cart.getDragAir();
		cart.motionY *= 0.0D;
		cart.motionZ *= cart.getDragAir();

		if (powered) {
			float force = 0.15F;

			if (isSpeeding(cart)) {
				force *= FORCE_DAMPEN_FACTOR;
			}

			double yaw = cart.rotationYaw * Math.PI / 180D;
			cart.motionX += Math.cos(yaw) * force;
			cart.motionZ += Math.sin(yaw) * force;
		}

		float limit = 0.3F;
		cart.motionX = Math.copySign(Math.min(Math.abs(cart.motionX), limit), cart.motionX);
		cart.motionZ = Math.copySign(Math.min(Math.abs(cart.motionZ), limit), cart.motionZ);
	}
}
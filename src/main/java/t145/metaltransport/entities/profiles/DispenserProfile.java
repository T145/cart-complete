package t145.metaltransport.entities.profiles;

import net.minecraft.block.BlockDispenser;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import t145.metaltransport.api.consts.RegistryMT;
import t145.metaltransport.api.profiles.IProfileFactory;
import t145.metaltransport.api.profiles.IServerProfile;

public class DispenserProfile extends TileEntityDispenser implements IServerProfile {

	public static class ProfileFactoryDispenser implements IProfileFactory {

		@Override
		public DispenserProfile create(EntityMinecart cart) {
			return new DispenserProfile(cart);
		}
	}

	protected final EntityMinecart cart;

	public DispenserProfile(EntityMinecart cart) {
		this.cart = cart;
		this.world = cart.world;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setInteger("x", pos.getX());
		tag.setInteger("y", pos.getY());
		tag.setInteger("z", pos.getZ());

		if (!this.checkLootAndWrite(tag)) {
			ItemStackHelper.saveAllItems(tag, this.stacks);
		}

		if (this.hasCustomName()) {
			tag.setString("CustomName", this.customName);
		}

		return tag;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return cart.isEntityAlive() && player.getDistanceSq(cart.posX + 0.5D, cart.posY + 0.5D, cart.posZ + 0.5D) <= 64.0D;
	}

	@Override
	public void activate(EntityPlayer player, EnumHand hand) {
		this.pos = cart.getPosition();
		player.openGui(RegistryMT.ID, cart.hashCode(), world, pos.getX(), pos.getY(), pos.getZ());

		if (this instanceof DropperProfile) {
			player.addStat(StatList.DROPPER_INSPECTED);
		} else {
			player.addStat(StatList.DISPENSER_INSPECTED);
		}
	}

	@Override
	public void onProfileDeletion() {
		InventoryHelper.dropInventoryItems(cart.world, cart, this);
	}

	private final class DispenserSource implements IBlockSource {

		@Override
		public World getWorld() {
			return cart.world;
		}

		@Override
		public double getX() {
			return this.getBlockPos().getX();
		}

		@Override
		public double getY() {
			return this.getBlockPos().getY();
		}

		@Override
		public double getZ() {
			return this.getBlockPos().getZ();
		}

		@Override
		public BlockPos getBlockPos() {
			return cart.getPosition();
		}

		@Override
		public IBlockState getBlockState() {
			return Blocks.DISPENSER.getDefaultState();
		}

		@Override
		public DispenserProfile getBlockTileEntity() {
			return DispenserProfile.this;
		}
	}

	@Override
	public void onActivatorRailPass(int x, int y, int z, boolean powered) {
		this.pos = cart.getPosition();
		int slot = this.getDispenseSlot();

		if (slot < 0) {
			world.playEvent(1001, pos, 0);
		} else {
			ItemStack stack = this.getStackInSlot(slot);
			IBehaviorDispenseItem behavior = BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.getObject(stack.getItem());

			if (behavior != IBehaviorDispenseItem.DEFAULT_BEHAVIOR) {
				this.setInventorySlotContents(slot, behavior.dispense(new DispenserSource(), stack));
			}
		}
	}
}

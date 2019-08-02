package t145.metaltransport.entities.profiles;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiDispenser;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerDispenser;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import t145.metaltransport.api.profiles.IProfileFactory;
import t145.metaltransport.api.profiles.IServerProfile;
import t145.metaltransport.core.MetalTransport;
import t145.metaltransport.entities.EntityMetalCart;

public class DispenserProfile implements IServerProfile {

	public static class ProfileFactoryDispenser implements IProfileFactory {

		@Override
		public DispenserProfile create(EntityMinecart cart) {
			return new DispenserProfile((EntityMetalCart) cart, Blocks.DISPENSER);
		}
	}

	public final TileEntityDispenser dispenser;
	protected final EntityMinecart cart;

	public DispenserProfile(EntityMetalCart cart, Block block) {
		World world = cart.world;

		this.dispenser = (TileEntityDispenser) block.createTileEntity(world, block.getDefaultState());
		this.dispenser.setWorld(world);
		this.dispenser.setPos(cart.getPosition());
		this.cart = cart;
	}

	@Nonnull
	@Override
	public Container getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return new ContainerDispenser(player.inventory, dispenser) {

			@Override
			public boolean canInteractWith(EntityPlayer player) {
				return cart.isEntityAlive() && player.getDistanceSq(cart) <= 64.0D;
			}
		};
	}

	@SideOnly(Side.CLIENT)
	@Nonnull
	@Override
	public GuiContainer getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return new GuiDispenser(player.inventory, dispenser);
	}

	@Override
	public NBTTagCompound serializeNBT() {
		return this.dispenser.writeToNBT(new NBTTagCompound());
	}

	@Override
	public void deserializeNBT(NBTTagCompound tag) {
		this.dispenser.readFromNBT(tag);
	}

	@Override
	public void activate(EntityPlayer player, EnumHand hand) {
		MetalTransport.openGui(player, cart);

		if (this instanceof DropperProfile) {
			player.addStat(StatList.DROPPER_INSPECTED);
		} else {
			player.addStat(StatList.DISPENSER_INSPECTED);
		}
	}

	@Override
	public void onProfileDeletion() {
		InventoryHelper.dropInventoryItems(cart.world, cart, dispenser);
	}

	protected final class DispenserSource implements IBlockSource {

		@Override
		public World getWorld() {
			return cart.world;
		}

		@Override
		public double getX() {
			return cart.posX;
		}

		@Override
		public double getY() {
			return cart.posY + 0.5D;
		}

		@Override
		public double getZ() {
			return cart.posZ;
		}

		@Override
		public BlockPos getBlockPos() {
			return cart.getPosition().add(0.5D, 0, 0.5D);
		}

		@Override
		public IBlockState getBlockState() {
			// TODO: Fix dispenser facing logic: Figure out a way to get the front of the cart at all times
			return Blocks.DISPENSER.getDefaultState().withProperty(BlockDispenser.FACING, cart.getHorizontalFacing());
		}

		@Override
		public TileEntityDispenser getBlockTileEntity() {
			return dispenser;
		}
	}

	protected void dispenseStack(int slot, ItemStack stack) {
		IBehaviorDispenseItem behavior = BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.getObject(stack.getItem());

		if (behavior != IBehaviorDispenseItem.DEFAULT_BEHAVIOR) {
			dispenser.setInventorySlotContents(slot, behavior.dispense(new DispenserSource(), stack));
		}
	}

	@Override
	public void onActivatorRailPass(int x, int y, int z, boolean powered) {
		World world = cart.world;

		if (!powered || world.getTotalWorldTime() % 5L != 0L) {
			return;
		}

		int slot = this.dispenser.getDispenseSlot();

		if (slot < 0) {
			world.playEvent(1001, cart.getPosition(), 0);
		} else {
			this.dispenseStack(slot, dispenser.getStackInSlot(slot));
		}
	}
}

package T145.metaltransport.items;

import T145.metaltransport.api.constants.CartType;
import T145.metaltransport.api.constants.RegistryMT;
import T145.metaltransport.entities.EntityMetalMinecartEmpty;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockRailBase.EnumRailDirection;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMetalMinecart extends ItemMod {

	private static final IBehaviorDispenseItem MINECART_DISPENSER_BEHAVIOR = new BehaviorDefaultDispenseItem() {

		private final BehaviorDefaultDispenseItem dispenseBehavior = new BehaviorDefaultDispenseItem();

		private EnumRailDirection getRailDirection(IBlockSource source, BlockPos pos) {
			IBlockState state = source.getBlockState();
			Block block = state.getBlock();

			if (block instanceof BlockRailBase) {
				BlockRailBase rail = (BlockRailBase) block;
				return rail.getRailDirection(source.getWorld(), pos, state, null);
			} else {
				return EnumRailDirection.NORTH_SOUTH;
			}
		}

		@Override
		public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
			EnumFacing front = source.getBlockState().getValue(BlockDispenser.FACING);
			BlockPos pos = source.getBlockPos().offset(front);
			World world = source.getWorld();
			IBlockState state = world.getBlockState(pos);
			double yOffset = 0.0D;

			if (BlockRailBase.isRailBlock(state)) {
				yOffset = getRailDirection(source, pos).isAscending() ? 0.6D : 0.1D;
			} else {
				if (state.getMaterial() != Material.AIR || !BlockRailBase.isRailBlock(world.getBlockState(pos.down()))) {
					return dispenseBehavior.dispense(source, stack);
				}

				yOffset -= front != EnumFacing.DOWN && getRailDirection(source, pos.down()).isAscending() ? 0.4D : 0.9D;
			}

			yOffset += Math.floor(source.getY()) + front.getYOffset();

			double xOffset = source.getX() + front.getXOffset() * 1.125D;
			double zOffset = source.getZ() + front.getZOffset() * 1.125D;
			EntityMetalMinecartEmpty cart = new EntityMetalMinecartEmpty(world, xOffset, yOffset, zOffset, CartType.byMetadata(stack.getItemDamage()));
			world.spawnEntity(cart);
			stack.shrink(1);

			return stack;
		}

		@Override
		protected void playDispenseSound(IBlockSource source) {
			source.getWorld().playEvent(1000, source.getBlockPos(), 0);
		}
	};

	public ItemMetalMinecart() {
		super(RegistryMT.RESOURCE_METAL_MINECART, CartType.values(), RegistryMT.TAB);
		this.setMaxStackSize(Items.MINECART.getItemStackLimit());
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, MINECART_DISPENSER_BEHAVIOR);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		IBlockState state = world.getBlockState(pos);

		if (!BlockRailBase.isRailBlock(state)) {
			return EnumActionResult.FAIL;
		}

		ItemStack stack = player.getHeldItem(hand);

		if (!world.isRemote) {
			BlockRailBase.EnumRailDirection dir = state.getBlock() instanceof BlockRailBase ? ((BlockRailBase) state.getBlock()).getRailDirection(world, pos, state, null) : BlockRailBase.EnumRailDirection.NORTH_SOUTH;
			double yOffset = 0.0D;

			if (dir.isAscending()) {
				yOffset = 0.5D;
			}

			EntityMetalMinecartEmpty cart = new EntityMetalMinecartEmpty(world, pos.getX() + 0.5D, pos.getY() + 0.0625D + yOffset, pos.getZ() + 0.5D, CartType.byMetadata(stack.getItemDamage()));
			world.spawnEntity(cart);
		}

		stack.shrink(1);

		return EnumActionResult.SUCCESS;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void prepareCreativeTab(NonNullList<ItemStack> items) {
		for (CartType type : CartType.values()) {
			items.add(new ItemStack(this, 1, type.ordinal()));
		}
	}
}

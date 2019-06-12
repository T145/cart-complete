package T145.metaltransport.entities.behaviors;

import T145.metaltransport.api.carts.CartBehavior;
import T145.metaltransport.api.constants.RegistryMT;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SimpleGuiBehavior extends CartBehavior {

	public SimpleGuiBehavior() {
		super(new Block[] { Blocks.CRAFTING_TABLE, Blocks.ANVIL, Blocks.ENCHANTING_TABLE });
	}

	@Override
	public void activate(EntityMinecart cart, EntityPlayer player, EnumHand hand) {
		World world = cart.world;

		if (!world.isRemote) {
			IBlockState state = cart.getDisplayTile();
			BlockPos pos = cart.getPosition();
			Block block = state.getBlock();

			player.openGui(RegistryMT.ID, cart.getEntityId(), world, pos.getX(), pos.getY(), pos.getZ());

			if (block instanceof BlockWorkbench) {
				player.addStat(StatList.CRAFTING_TABLE_INTERACTION);
			}
		}
	}
}

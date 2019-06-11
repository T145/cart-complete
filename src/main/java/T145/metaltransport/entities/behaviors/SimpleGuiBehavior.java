package T145.metaltransport.entities.behaviors;

import T145.metaltransport.api.carts.CartBehavior;
import T145.metaltransport.api.constants.RegistryMT;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class SimpleGuiBehavior extends CartBehavior {

	public SimpleGuiBehavior() {
		super(new Block[] { Blocks.CRAFTING_TABLE, Blocks.ANVIL, Blocks.ENCHANTING_TABLE });
	}

	public void openGui(EntityPlayer player, EntityMinecart cart) {
		BlockPos pos = cart.getPosition();
		player.openGui(RegistryMT.ID, cart.getEntityId(), cart.world, pos.getX(), pos.getY(), pos.getZ());
	}

	@Override
	public void activate(EntityMinecart cart, EntityPlayer player, EnumHand hand) {
		if (!player.world.isRemote) {
			this.openGui(player, cart);

			if (cart.getDisplayTile().getBlock() instanceof BlockWorkbench) {
				player.addStat(StatList.CRAFTING_TABLE_INTERACTION);
			}
		}
	}
}

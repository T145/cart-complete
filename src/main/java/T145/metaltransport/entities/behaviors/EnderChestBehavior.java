package T145.metaltransport.entities.behaviors;

import T145.metaltransport.api.carts.CartBehavior;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumHand;

public class EnderChestBehavior extends CartBehavior {

	public EnderChestBehavior() {
		super(Blocks.ENDER_CHEST);
	}

	@Override
	public void activate(EntityMinecart cart, EntityPlayer player, EnumHand hand) {
		if (!cart.world.isRemote) {
			// Ender inventory is never null, so why does the block check for it Mojang?
			player.displayGUIChest(player.getInventoryEnderChest());
			player.addStat(StatList.ENDERCHEST_OPENED);
		}
	}
}

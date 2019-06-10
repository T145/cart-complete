package T145.metaltransport.entities.behaviors;

import T145.metaltransport.api.carts.CartBehavior;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class EnderChestBehavior extends CartBehavior {

	public EnderChestBehavior() {
		super(Blocks.ENDER_CHEST);
	}

	@Override
	public void activate(EntityMinecart cart, EntityPlayer player, EnumHand hand) {
		World world = cart.world;
		InventoryEnderChest enderInv = player.getInventoryEnderChest();

		if (enderInv != null && !world.isRemote) {
			player.displayGUIChest(enderInv);
			player.addStat(StatList.ENDERCHEST_OPENED);
		}
	}
}

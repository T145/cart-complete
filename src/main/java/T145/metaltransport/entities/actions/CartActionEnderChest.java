package T145.metaltransport.entities.actions;

import T145.metaltransport.api.carts.CartAction;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class CartActionEnderChest extends CartAction {

	public CartActionEnderChest() {
		super(Blocks.ENDER_CHEST);
	}

	@Override
	public boolean activate(EntityMinecart cart, EntityPlayer player, EnumHand hand) {
		World world = cart.world;
		InventoryEnderChest enderInv = player.getInventoryEnderChest();

		if (enderInv != null && !world.isRemote) {
			player.displayGUIChest(enderInv);
			player.addStat(StatList.ENDERCHEST_OPENED);
			return true;
		}

		return true;
	}
}

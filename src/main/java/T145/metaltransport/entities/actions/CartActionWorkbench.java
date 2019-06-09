package T145.metaltransport.entities.actions;

import T145.metaltransport.api.carts.CartAction;
import T145.metaltransport.containers.CraftingTableInterface;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class CartActionWorkbench extends CartAction {

	public CartActionWorkbench() {
		super(Blocks.CRAFTING_TABLE);
	}

	@Override
	public boolean activate(EntityMinecart cart, EntityPlayer player, EnumHand hand) {
		World world = cart.world;

		if (!world.isRemote) {
			player.displayGui(new CraftingTableInterface(world, cart.getPosition()));
			player.addStat(StatList.CRAFTING_TABLE_INTERACTION);
		}

		return true;
	}
}

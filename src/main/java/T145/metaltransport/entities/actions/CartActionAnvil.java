package T145.metaltransport.entities.actions;

import T145.metaltransport.api.carts.CartAction;
import net.minecraft.block.BlockAnvil;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class CartActionAnvil extends CartAction {

	public CartActionAnvil() {
		super(Blocks.ANVIL);
	}

	@Override
	public boolean activate(EntityMinecart cart, EntityPlayer player, EnumHand hand) {
		World world = cart.world;

		if (!world.isRemote) {
			player.displayGui(new BlockAnvil.Anvil(world, cart.getPosition()));
		}

		return true;
	}
}

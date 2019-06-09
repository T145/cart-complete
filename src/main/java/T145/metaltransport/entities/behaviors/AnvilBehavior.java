package T145.metaltransport.entities.behaviors;

import T145.metaltransport.api.carts.CartBehavior;
import T145.metaltransport.containers.AnvilInterface;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class AnvilBehavior extends CartBehavior {

	public AnvilBehavior() {
		super(Blocks.ANVIL);
	}

	@Override
	public boolean activate(EntityMinecart cart, EntityPlayer player, EnumHand hand) {
		World world = cart.world;

		if (!world.isRemote) {
			player.displayGui(new AnvilInterface(world, cart.getPosition()));
		}

		return true;
	}
}

package T145.metaltransport.entities.profiles;

import T145.metaltransport.api.carts.CartProfile;
import T145.metaltransport.api.consts.RegistryMT;
import T145.metaltransport.api.profiles.ICartProfileFactory;
import T145.metaltransport.entities.EntityMetalMinecart;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class SimpleGuiProfile extends CartProfile {

	public static class SimpleGuiProfileFactory implements ICartProfileFactory {

		@Override
		public SimpleGuiProfile createProfile(EntityMinecart cart) {
			return new SimpleGuiProfile(cart);
		}
	}

	public SimpleGuiProfile(EntityMinecart cart) {
		super(cart);
	}

	@Override
	public void activate(EntityPlayer player, EnumHand hand) {
		EntityMetalMinecart cart = (EntityMetalMinecart) this.getCart();
		BlockPos pos = cart.getPosition();

		player.openGui(RegistryMT.ID, cart.getEntityId(), cart.world, pos.getX(), pos.getY(), pos.getZ());

		if (cart.getDisplayBlock() instanceof BlockWorkbench) {
			player.addStat(StatList.CRAFTING_TABLE_INTERACTION);
		}
	}
}

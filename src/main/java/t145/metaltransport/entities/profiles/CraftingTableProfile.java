package t145.metaltransport.entities.profiles;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import t145.metaltransport.api.consts.RegistryMT;
import t145.metaltransport.api.profiles.IProfileFactory;
import t145.metaltransport.api.profiles.IServerProfile;

public class CraftingTableProfile implements IServerProfile {

	public static class ProfileFactoryCraftingTable implements IProfileFactory {

		@Override
		public CraftingTableProfile create(EntityMinecart cart) {
			return new CraftingTableProfile(cart);
		}
	}

	private final EntityMinecart cart;

	public CraftingTableProfile(EntityMinecart cart) {
		this.cart = cart;
	}

	@Override
	public void activate(EntityPlayer player, EnumHand hand) {
		BlockPos pos = cart.getPosition();
		player.openGui(RegistryMT.ID, cart.hashCode(), cart.world, pos.getX(), pos.getY(), pos.getZ());
		player.addStat(StatList.CRAFTING_TABLE_INTERACTION);
	}
}

package T145.metaltransport.entities.profiles;

import T145.metaltransport.api.profiles.ICartProfileFactory;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EnchantingTableProfile extends SimpleGuiProfile {

	public static class EnchantingTableProfileFactory implements ICartProfileFactory {

		@Override
		public EnchantingTableProfile create(EntityMinecart cart) {
			return new EnchantingTableProfile(cart);
		}
	}

	public EnchantingTableProfile(EntityMinecart cart) {
		super(cart);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void render(BlockPos pos, ItemStack stack, float partialTicks) {}
}

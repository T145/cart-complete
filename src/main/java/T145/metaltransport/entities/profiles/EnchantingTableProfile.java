package T145.metaltransport.entities.profiles;

import T145.metaltransport.api.profiles.ICartProfileFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
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

	@SideOnly(Side.CLIENT)
	public final TileEntityEnchantmentTable table = new TileEntityEnchantmentTable();

	public EnchantingTableProfile(EntityMinecart cart) {
		super(cart);
		table.setWorld(Minecraft.getMinecraft().world);
		table.setPos(BlockPos.ORIGIN);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void render(BlockPos pos, ItemStack stack, float partialTicks) {
		if (table.getPos() != pos) {
			table.setPos(pos);
		}

		table.update();
		TileEntityRendererDispatcher.instance.render(table, 0, 0, 0, 0, partialTicks);
	}
}

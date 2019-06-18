package T145.metaltransport.client.render.items;

import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityEnchantmentTable;

public class EnchantingTableTEISR extends TileEntityItemStackRenderer {

	public final TileEntityEnchantmentTable table = new TileEntityEnchantmentTable();

	@Override
	public void renderByItem(ItemStack stack, float partialTicks) {
		TileEntityRendererDispatcher.instance.render(this.table, 0, 0, 0, 0, partialTicks);
	}
}

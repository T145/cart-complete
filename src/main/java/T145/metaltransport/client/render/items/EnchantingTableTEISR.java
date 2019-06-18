package T145.metaltransport.client.render.items;

import T145.metaltransport.api.client.IPositionedTEISR;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.util.math.BlockPos;

public class EnchantingTableTEISR extends TileEntityItemStackRenderer implements IPositionedTEISR {

	public final TileEntityEnchantmentTable table = new TileEntityEnchantmentTable();

	public EnchantingTableTEISR() {
		table.setWorld(Minecraft.getMinecraft().world);
		this.setPos(BlockPos.ORIGIN);
	}

	@Override
	public BlockPos getPos() {
		return table.getPos();
	}

	@Override
	public void setPos(BlockPos pos) {
		table.setPos(pos);
	}

	@Override
	public void renderByItem(ItemStack stack, float partialTicks) {
		table.update();
		TileEntityRendererDispatcher.instance.render(table, 0, 0, 0, 0, partialTicks);
	}
}

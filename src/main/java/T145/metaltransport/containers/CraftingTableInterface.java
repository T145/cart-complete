package T145.metaltransport.containers;

import net.minecraft.block.BlockWorkbench.InterfaceCraftingTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CraftingTableInterface extends InterfaceCraftingTable {

	protected final World worldIn;
	protected final BlockPos posIn;

	public CraftingTableInterface(World worldIn, BlockPos posIn) {
		super(worldIn, posIn);
		this.worldIn = worldIn;
		this.posIn = posIn;
	}

	@Override
	public Container createContainer(final InventoryPlayer playerInventory, final EntityPlayer player) {
		return new CraftingTableContainer(playerInventory, worldIn, posIn);
	}
}
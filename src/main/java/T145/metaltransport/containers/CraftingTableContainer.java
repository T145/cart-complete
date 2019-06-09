package T145.metaltransport.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CraftingTableContainer extends ContainerWorkbench {

	protected final BlockPos position;

	public CraftingTableContainer(final InventoryPlayer inventory, final World world, final BlockPos position) {
		super(inventory, world, position);
		this.position = position;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return player.getDistanceSq(position.getX() + 0.5D, position.getY(), position.getZ() + 0.5D) <= 64.0D;
	}
}

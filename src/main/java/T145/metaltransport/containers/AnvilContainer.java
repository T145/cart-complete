package T145.metaltransport.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AnvilContainer extends ContainerRepair {

	protected final BlockPos position;

	@SideOnly(Side.CLIENT)
	public AnvilContainer(InventoryPlayer inventory, World world, EntityPlayer player) {
		super(inventory, world, player);
		this.position = BlockPos.ORIGIN;
	}

	public AnvilContainer(InventoryPlayer inventory, World world, BlockPos pos, EntityPlayer player) {
		super(inventory, world, pos, player);
		this.position = pos;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return player.getDistanceSq(position.getX() + 0.5D, position.getY(), position.getZ() + 0.5D) <= 64.0D;
	}
}

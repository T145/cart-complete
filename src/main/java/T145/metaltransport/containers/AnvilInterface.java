package T145.metaltransport.containers;

import net.minecraft.block.BlockAnvil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AnvilInterface extends BlockAnvil.Anvil {

	protected final World worldIn;
	protected final BlockPos posIn;

	public AnvilInterface(World worldIn, BlockPos posIn) {
		super(worldIn, posIn);
		this.worldIn = worldIn;
		this.posIn = posIn;
	}

	@Override
	public Container createContainer(InventoryPlayer inventory, EntityPlayer player) {
		return new AnvilContainer(inventory, worldIn, posIn, player);
	}
}
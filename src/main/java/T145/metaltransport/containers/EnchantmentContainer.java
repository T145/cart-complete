package T145.metaltransport.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EnchantmentContainer extends ContainerEnchantment {

	protected final BlockPos position;

	@SideOnly(Side.CLIENT)
	public EnchantmentContainer(InventoryPlayer playerInv, World world) {
		super(playerInv, world);
		this.position = BlockPos.ORIGIN;
	}

	public EnchantmentContainer(InventoryPlayer playerInv, World world, BlockPos pos) {
		super(playerInv, world, pos);
		this.position = pos;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return player.getDistanceSq(position.getX() + 0.5D, position.getY(), position.getZ() + 0.5D) <= 64.0D;
	}
}

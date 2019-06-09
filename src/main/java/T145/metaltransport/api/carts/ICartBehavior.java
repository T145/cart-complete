package T145.metaltransport.api.carts;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ICartBehavior {

	public static final double DEFAULT_CART_SPEED = 0.4D;

	String[] getBlockNames();

	boolean hasBlockName(String blockName);

	public default boolean hasNames() {
		return getBlockNames().length > 0;
	}

	NBTTagCompound serialize();

	void deserialize(NBTTagCompound tag);

	/**
	 * Handler for {@link World#setEntityState}
	 */
	@SideOnly(Side.CLIENT)
	public void handleStatusUpdate(EntityMinecart cart, byte id);

	void tick(EntityMinecart cart);

	double getMaxCartSpeed();

	void moveAlongTrack(EntityMinecart cart, BlockPos pos, IBlockState rail);

	void applyDrag(EntityMinecart cart);

	boolean activate(EntityMinecart cart, EntityPlayer player, EnumHand hand);
}
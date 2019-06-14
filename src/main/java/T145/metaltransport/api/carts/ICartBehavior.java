package T145.metaltransport.api.carts;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ICartBehavior {

	public static final double DEFAULT_CART_SPEED = 0.4D;

	IBlockState customizeState(IBlockState state);

	NBTTagCompound serialize();

	ICartBehavior deserialize(NBTTagCompound tag);

	default double getMaxCartSpeed() {
		return DEFAULT_CART_SPEED;
	}

	void tickServer(World world, BlockPos pos);

	void activate(EntityPlayer player, EnumHand hand);

	void attackCartFrom(DamageSource source, float amount);

	void killMinecart(DamageSource source, boolean dropItems);

	void onDeath();

	void fall(float distance, float damageMultiplier);

	void onActivatorRailPass(int x, int y, int z, boolean receivingPower);

	void moveAlongTrack(BlockPos pos, IBlockState rail);

	void applyDrag();

	void onDeletion();

	boolean ignoreItemEntityData();

	/**
	 * Handler for {@link World#setEntityState}
	 */
	@SideOnly(Side.CLIENT)
	void handleStatusUpdate(byte id);
}

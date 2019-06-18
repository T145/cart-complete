package T145.metaltransport.api.profiles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ICartProfile extends ISerialProfile {

	@Override
	ICartProfile deserialize(NBTTagCompound tag);

	void tickServer(World world, BlockPos pos);

	void activate(EntityPlayer player, EnumHand hand);

	boolean attackCart(DamageSource source, float amount);

	void killCart(DamageSource source, boolean dropItems);

	void onProfileDeletion();

	void onCartDeath();

	void fall(float distance, float damageMultiplier);

	void onActivatorRailPass(int x, int y, int z, boolean powered);

	void moveAlongTrack(BlockPos pos, IBlockState rail);

	void applyDrag();

	TileEntityItemStackRenderer getStackRenderer();
}

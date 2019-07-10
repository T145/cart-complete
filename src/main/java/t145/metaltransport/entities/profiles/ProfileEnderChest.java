package t145.metaltransport.entities.profiles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import t145.metaltransport.api.profiles.IProfileFactory;
import t145.metaltransport.api.profiles.IUniversalProfile;

public class ProfileEnderChest implements IUniversalProfile {

	public static class ProfileFactoryEnderChest implements IProfileFactory {

		@Override
		public ProfileEnderChest create(EntityMinecart cart) {
			return new ProfileEnderChest(cart);
		}
	}

	public ProfileEnderChest(EntityMinecart cart) {}

	@Override
	public void tick(World world, BlockPos pos) {
		if (world.isRemote && world.getTotalWorldTime() % 5L == 0L) {
			Blocks.ENDER_CHEST.randomDisplayTick(null, world, pos, world.rand);
		}
	}

	@Override
	public void activate(EntityPlayer player, EnumHand hand) {
		if (!player.world.isRemote) {
			player.displayGUIChest(player.getInventoryEnderChest());
		}
	}

	@Override
	public boolean attackCart(DamageSource source, float amount) {
		return false;
	}

	@Override
	public void killCart(DamageSource source, boolean dropItems) {}

	@Override
	public void onProfileDeletion() {}

	@Override
	public void onCartDeath() {}

	@Override
	public void fall(float distance, float damageMultiplier) {}

	@Override
	public void onActivatorRailPass(int x, int y, int z, boolean powered) {}

	@Override
	public void moveAlongTrack(BlockPos pos, IBlockState rail) {}

	@Override
	public void applyDrag() {}

	@Override
	public NBTTagCompound serializeNBT() {
		return null;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {}

	@Override
	public void render(BlockPos pos, ItemStack stack, float partialTicks) {}

}

package t145.metaltransport.api.profiles;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IProfile extends INBTSerializable<NBTTagCompound>, IGuiHandler {

	@Nullable
	@Override
	default Container getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}

	@SideOnly(Side.CLIENT)
	@Nullable
	@Override
	default GuiContainer getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}

	@Override
	default NBTTagCompound serializeNBT() {
		return null;
	}

	@Override
	default void deserializeNBT(NBTTagCompound tag) {}

	default void tick(World world, BlockPos pos) {}

	default void activate(EntityPlayer player, EnumHand hand) {}

	default boolean attackCart(DamageSource source, float amount) {
		return false;
	}

	default void killCart(DamageSource source, boolean dropItems) {}

	default void onProfileDeletion() {}

	default void onCartDeath() {}

	default void fall(float distance, float damageMultiplier) {}

	default void onActivatorRailPass(int x, int y, int z, boolean powered) {}

	default void moveAlongTrack(BlockPos pos, IBlockState rail) {}

	default void applyDrag() {}
}

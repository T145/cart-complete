package t145.metaltransport.entities.profiles;

import me.paulf.rbeacons.server.block.TileResponsiveBeacon;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import t145.metaltransport.api.consts.RegistryMT;
import t145.metaltransport.api.profiles.IProfileFactory;
import t145.metaltransport.api.profiles.IUniversalProfile;

public class BeaconProfile extends TileResponsiveBeacon implements IUniversalProfile {

	public static class ProfileFactoryBeacon implements IProfileFactory {

		@Override
		public BeaconProfile create(EntityMinecart cart) {
			return new BeaconProfile(cart);
		}
	}

	private final EntityMinecart cart;

	public BeaconProfile(EntityMinecart cart) {
		this.cart = cart;
		this.world = cart.world;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setInteger("x", pos.getX());
		tag.setInteger("y", pos.getY());
		tag.setInteger("z", pos.getZ());
		tag.setInteger("Primary", this.getField(1));
		tag.setInteger("Secondary", this.getField(2));
		tag.setInteger("Levels", this.getField(0));
		return tag;
	}

	@Override
	public void tick(World world, BlockPos pos) {
		this.pos = cart.getPosition();
		this.update();
	}

	@Override
	public void activate(EntityPlayer player, EnumHand hand) {
		World world = cart.world;

		if (!world.isRemote) {
			BlockPos pos = cart.getPosition();
			player.openGui(RegistryMT.ID, cart.hashCode(), world, pos.getX(), pos.getY(), pos.getZ());
			player.addStat(StatList.BEACON_INTERACTION);
		}
	}

	@Override
	public void onProfileDeletion() {
		this.invalidate();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void render(Render renderer, EntityMinecart cart, ItemStack stack, float partialTicks) {
		TileEntityRendererDispatcher.instance.render(this, -0.5, 0, -0.5, partialTicks);
	}
}

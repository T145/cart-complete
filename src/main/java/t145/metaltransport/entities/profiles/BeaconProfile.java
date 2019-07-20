package t145.metaltransport.entities.profiles;

import javax.annotation.Nonnull;

import net.minecraft.client.gui.inventory.GuiBeacon;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerBeacon;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import t145.metaltransport.api.profiles.IProfileFactory;
import t145.metaltransport.api.profiles.IUniversalProfile;
import t145.metaltransport.core.MetalTransport;

public class BeaconProfile extends TileEntityBeacon implements IUniversalProfile {

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

	@Nonnull
	@Override
	public Container getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return new ContainerBeacon(player.inventory, this);
	}

	@SideOnly(Side.CLIENT)
	@Nonnull
	@Override
	public GuiContainer getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return new GuiBeacon(player.inventory, this);
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return cart.isEntityAlive() && player.getDistanceSq(cart.posX + 0.5D, cart.posY + 0.5D, cart.posZ + 0.5D) <= 64.0D;
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
		if (!world.isRemote) {
			MetalTransport.openGui(player, cart);
			player.addStat(StatList.BEACON_INTERACTION);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void render(Render renderer, EntityMinecart cart, ItemStack stack, float partialTicks) {
		TileEntityRendererDispatcher.instance.render(this, -0.5, 0, -0.5, partialTicks);
	}
}

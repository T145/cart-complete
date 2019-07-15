package t145.metaltransport.entities.profiles;

import me.paulf.rbeacons.server.block.TileResponsiveBeacon;
import net.minecraft.client.gui.inventory.GuiBeacon;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerBeacon;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import t145.metaltransport.api.consts.RegistryMT;
import t145.metaltransport.api.profiles.IProfileFactory;
import t145.metaltransport.api.profiles.IUniversalProfile;
import t145.metaltransport.entities.EntityMetalCart;

public class BeaconProfile implements IUniversalProfile {

	public static class ProfileFactoryBeacon implements IProfileFactory, IGuiHandler {

		@Override
		public BeaconProfile create(EntityMinecart cart) {
			return new BeaconProfile(cart);
		}

		@Override
		public ContainerBeacon getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
			Entity entity = world.getEntityByID(ID);

			if (entity instanceof EntityMetalCart) {
				EntityMetalCart cart = (EntityMetalCart) entity;
				return new ContainerBeacon(player.inventory, ((BeaconProfile) cart.getProfile().get()).beacon) {

					@Override
					public boolean canInteractWith(EntityPlayer player) {
						return cart.isEntityAlive() && player.getDistanceSq(cart.posX + 0.5D, cart.posY + 0.5D, cart.posZ + 0.5D) <= 64.0D;
					}
				};
			}

			return null;
		}

		@SideOnly(Side.CLIENT)
		@Override
		public GuiBeacon getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
			Entity entity = world.getEntityByID(ID);

			if (entity instanceof EntityMetalCart) {
				EntityMetalCart cart = (EntityMetalCart) entity;
				return new GuiBeacon(player.inventory, ((BeaconProfile) cart.getProfile().get()).beacon);
			}

			return null;
		}
	}

	private final TileResponsiveBeacon beacon;
	private final EntityMinecart cart;

	public BeaconProfile(EntityMinecart cart) {
		this.cart = cart;
		this.beacon = new TileResponsiveBeacon();
		beacon.setWorld(cart.world);
		beacon.setPos(cart.getPosition());
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("Levels", beacon.getField(0));
		tag.setInteger("PrimaryEffect", beacon.getField(1));
		tag.setInteger("SecondaryEffect", beacon.getField(2));
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound tag) {
		beacon.setField(0, tag.getInteger("Levels"));
		beacon.setField(1, tag.getInteger("PrimaryEffect"));
		beacon.setField(2, tag.getInteger("SecondaryEffect"));
	}

	@Override
	public void tick(World world, BlockPos pos) {
		if (!beacon.getPos().equals(pos)) {
			beacon.setPos(pos);
		}

		beacon.update();
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

	@SideOnly(Side.CLIENT)
	@Override
	public void render(Render renderer, EntityMinecart cart, ItemStack stack, float partialTicks) {
		TileEntityRendererDispatcher.instance.render(beacon, -0.5, 0, -0.5, partialTicks);
	}
}

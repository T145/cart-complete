package t145.metaltransport.entities.profiles;

import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import t145.metaltransport.api.consts.RegistryMT;
import t145.metaltransport.api.profiles.IProfileFactory;
import t145.metaltransport.api.profiles.IServerProfile;
import t145.metaltransport.entities.EntityMetalCart;

public class CraftingTableProfile implements IServerProfile {

	public static class CraftingTableProfileFactory implements IProfileFactory, IGuiHandler {

		@Override
		public CraftingTableProfile create(EntityMinecart cart) {
			return new CraftingTableProfile(cart);
		}

		@Override
		public ContainerWorkbench getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
			Entity entity = world.getEntityByID(ID);

			if (entity instanceof EntityMetalCart) {
				EntityMetalCart cart = (EntityMetalCart) entity;
				return new ContainerWorkbench(player.inventory, world, cart.getPosition()) {

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
		public GuiCrafting getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
			Entity entity = world.getEntityByID(ID);

			if (entity instanceof EntityMetalCart) {
				return new GuiCrafting(player.inventory, world, ((EntityMetalCart) entity).getPosition());
			}

			return null;
		}
	}

	private final EntityMinecart cart;

	public CraftingTableProfile(EntityMinecart cart) {
		this.cart = cart;
	}

	@Override
	public void activate(EntityPlayer player, EnumHand hand) {
		BlockPos pos = cart.getPosition();
		player.openGui(RegistryMT.ID, cart.hashCode(), cart.world, pos.getX(), pos.getY(), pos.getZ());
		player.addStat(StatList.CRAFTING_TABLE_INTERACTION);
	}
}

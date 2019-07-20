package t145.metaltransport.entities.profiles;

import javax.annotation.Nonnull;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import shadows.fastbench.gui.ContainerFastBench;
import shadows.fastbench.gui.GuiFastBench;
import t145.metaltransport.api.profiles.IProfileFactory;
import t145.metaltransport.api.profiles.IServerProfile;
import t145.metaltransport.core.MetalTransport;

public class CraftingTableProfile implements IServerProfile {

	public static class ProfileFactoryCraftingTable implements IProfileFactory {

		@Override
		public CraftingTableProfile create(EntityMinecart cart) {
			return new CraftingTableProfile(cart);
		}
	}

	private final EntityMinecart cart;

	public CraftingTableProfile(EntityMinecart cart) {
		this.cart = cart;
	}

	@Nonnull
	@Override
	public Container getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return new ContainerFastBench(player, world, cart.getPosition()) {

			@Override
			public boolean canInteractWith(EntityPlayer player) {
				return cart.isEntityAlive() && player.getDistanceSq(cart.posX - 0.5D, cart.posY + 0.5D, cart.posZ - 0.5D) <= 64.0D;
			}
		};
	}

	@SideOnly(Side.CLIENT)
	@Nonnull
	@Override
	public GuiContainer getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return new GuiFastBench(player.inventory, world, cart.getPosition());
	}

	@Override
	public void activate(EntityPlayer player, EnumHand hand) {
		MetalTransport.openGui(player, cart);
		player.addStat(StatList.CRAFTING_TABLE_INTERACTION);
	}
}

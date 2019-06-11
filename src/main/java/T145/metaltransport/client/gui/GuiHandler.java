package T145.metaltransport.client.gui;

import T145.metaltransport.containers.AnvilContainer;
import T145.metaltransport.containers.CraftingTableContainer;
import T145.metaltransport.containers.EnchantmentContainer;
import T145.metaltransport.entities.EntityMetalMinecart;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockEnchantmentTable;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiHandler implements IGuiHandler {

	@Override
	public Container getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		Entity entity = world.getEntityByID(ID);

		if (entity instanceof EntityMetalMinecart) {
			EntityMetalMinecart cart = (EntityMetalMinecart) entity;
			Block block = cart.getDisplayTile().getBlock();

			if (block instanceof BlockWorkbench) {
				return new CraftingTableContainer(player.inventory, world, cart.getPosition());
			}

			if (block instanceof BlockAnvil) {
				return new AnvilContainer(player.inventory, world, cart.getPosition(), player);
			}

			if (block instanceof BlockEnchantmentTable) {
				return new EnchantmentContainer(player.inventory, world, cart.getPosition());
			}
		}

		return null;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Gui getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		Entity entity = world.getEntityByID(ID);

		if (entity instanceof EntityMetalMinecart) {
			EntityMetalMinecart cart = (EntityMetalMinecart) entity;
			Block block = cart.getDisplayTile().getBlock();

			if (block instanceof BlockWorkbench) {
				return new GuiCrafting(player.inventory, world, cart.getPosition());
			}

			if (block instanceof BlockAnvil) {
				return new GuiRepair(player.inventory, world);
			}

			if (block instanceof BlockEnchantmentTable) {
				return new GuiEnchantment(player.inventory, world, cart);
			}
		}

		return null;
	}
}

package T145.metaltransport.client.gui;

import T145.metaltransport.entities.EntityMetalMinecart;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockEnchantmentTable;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
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
				return new ContainerWorkbench(player.inventory, world, cart.getPosition()) {

					@Override
					public boolean canInteractWith(EntityPlayer player) {
						return player.getDistanceSq(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D) <= 64.0D;
					}
				};
			}

			if (block instanceof BlockAnvil) {
				return new ContainerRepair(player.inventory, world, cart.getPosition(), player) {

					{
						this.outputSlot = new InventoryCraftResult();
						this.addSlotToContainer(new Slot(this.outputSlot, 2, 134, 47) {

							@Override
							public boolean isItemValid(ItemStack stack) {
								return false;
							}

							@Override
							public boolean canTakeStack(EntityPlayer player) {
								return (player.capabilities.isCreativeMode || player.experienceLevel >= maximumCost) && maximumCost > 0 && this.getHasStack();
							}

							@Override
							public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack) {
								if (!thePlayer.capabilities.isCreativeMode) {
									thePlayer.addExperienceLevel(-maximumCost);
								}

								float breakChance = ForgeHooks.onAnvilRepair(thePlayer, stack, inputSlots.getStackInSlot(0), inputSlots.getStackInSlot(1));

								inputSlots.setInventorySlotContents(0, ItemStack.EMPTY);

								if (materialCost > 0) {
									ItemStack itemstack = inputSlots.getStackInSlot(1);

									if (!itemstack.isEmpty() && itemstack.getCount() > materialCost) {
										itemstack.shrink(materialCost);
										inputSlots.setInventorySlotContents(1, itemstack);
									} else {
										inputSlots.setInventorySlotContents(1, ItemStack.EMPTY);
									}
								} else {
									inputSlots.setInventorySlotContents(1, ItemStack.EMPTY);
								}

								maximumCost = 0;
								IBlockState state = cart.getDisplayTile();

								if (!thePlayer.capabilities.isCreativeMode && !world.isRemote && thePlayer.getRNG().nextFloat() < breakChance) {
									int l = state.getValue(BlockAnvil.DAMAGE);
									++l;

									if (l > 2) {
										cart.setDisplayTile(cart.getDefaultDisplayTile());
										world.playEvent(1029, pos, 0);
									} else {
										cart.setDisplayTile(state.withProperty(BlockAnvil.DAMAGE, l));
										world.playEvent(1030, pos, 0);
									}
								} else if (!world.isRemote) {
									world.playEvent(1030, pos, 0);
								}

								return stack;
							}
						});
					}

					@Override
					public boolean canInteractWith(EntityPlayer player) {
						return player.getDistanceSq(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D) <= 64.0D;
					}
				};
			}

			if (block instanceof BlockEnchantmentTable) {
				return new ContainerEnchantment(player.inventory, world, cart.getPosition()) {

					@Override
					public boolean canInteractWith(EntityPlayer player) {
						return player.getDistanceSq(position.getX() + 0.5D, position.getY(), position.getZ() + 0.5D) <= 64.0D;
					}
				};
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
				return new GuiEnchantment(player.inventory, world, new EnchantmentTableNameable());
			}
		}

		return null;
	}
}
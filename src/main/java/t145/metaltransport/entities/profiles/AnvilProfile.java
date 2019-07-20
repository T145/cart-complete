package t145.metaltransport.entities.profiles;

import javax.annotation.Nonnull;

import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import t145.metaltransport.api.profiles.IProfileFactory;
import t145.metaltransport.api.profiles.IServerProfile;
import t145.metaltransport.core.MetalTransport;
import t145.metaltransport.entities.EntityMetalCart;

public class AnvilProfile implements IServerProfile {

	public static class ProfileFactoryAnvil implements IProfileFactory {

		@Override
		public AnvilProfile create(EntityMinecart cart) {
			return new AnvilProfile((EntityMetalCart) cart);
		}
	}

	private final EntityMetalCart cart;

	public AnvilProfile(EntityMetalCart cart) {
		this.cart = cart;
	}

	@Nonnull
	@Override
	public Container getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return new ContainerRepair(player.inventory, world, cart.getPosition(), player) {

			protected Slot setSlotInContainer(int index, Slot slot) {
				slot.slotNumber = index;
				this.inventorySlots.set(index, slot);
				this.inventoryItemStacks.set(index, ItemStack.EMPTY);
				return slot;
			}

			{
				this.setSlotInContainer(2, new Slot(this.outputSlot, 2, 134, 47)
				{
					@Override
					public boolean isItemValid(ItemStack stack) {
						return false;
					}

					@Override
					public boolean canTakeStack(EntityPlayer playerIn) {
						return (playerIn.capabilities.isCreativeMode
								|| playerIn.experienceLevel >= maximumCost)
								&& maximumCost > 0 && this.getHasStack();
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

						if (!world.isRemote) {
							BlockPos pos = cart.getPosition();

							if (!thePlayer.capabilities.isCreativeMode && thePlayer.getRNG().nextFloat() < breakChance) {
								int l = cart.getDisplayStack().getMetadata();
								++l;

								if (l > 2) {
									cart.removeDisplayBlock(false);
									world.playEvent(1029, pos, 0);
								} else {
									cart.setDisplayStack(new ItemStack(Blocks.ANVIL, 1, l));
									world.playEvent(1030, pos, 0);
								}
							} else {
								world.playEvent(1030, pos, 0);
							}
						}

						return stack;
					}
				});
			}

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
		return new GuiRepair(player.inventory, world);
	}

	@Override
	public void activate(EntityPlayer player, EnumHand hand) {
		MetalTransport.openGui(player, cart);
	}
}

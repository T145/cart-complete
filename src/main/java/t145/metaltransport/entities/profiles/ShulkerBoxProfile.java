package t145.metaltransport.entities.profiles;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiShulkerBox;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerShulkerBox;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import t145.metaltransport.api.profiles.IProfileFactory;
import t145.metaltransport.api.profiles.IUniversalProfile;
import t145.metaltransport.core.MetalTransport;
import t145.metaltransport.entities.EntityMetalCart;
import t145.metaltransport.net.UpdateShulkerBoxCart;

public class ShulkerBoxProfile implements IUniversalProfile {

	public static class ProfileFactoryShulkerBox implements IProfileFactory {

		@Override
		public ShulkerBoxProfile create(EntityMinecart cart) {
			return new ShulkerBoxProfile((EntityMetalCart) cart);
		}
	}

	public final TileEntityShulkerBox box;
	private final EntityMetalCart cart;

	public ShulkerBoxProfile(EntityMetalCart cart) {
		ItemStack stack = cart.getDisplayStack();
		Block block = cart.getDisplayBlock();
		World world = cart.world;

		//this.box = (TileEntityShulkerBox) block.createTileEntity(world, block.getDefaultState());
		this.box = new TileEntityShulkerBox(BlockShulkerBox.getColorFromBlock(block)) {

			@Override
			public void openInventory(EntityPlayer player) {
				if (!player.isSpectator()) {
					if (this.openCount < 0) {
						this.openCount = 0;
					}

					MetalTransport.NETWORK.sendToAllAround(new UpdateShulkerBoxCart(pos, ++this.openCount), world, pos);

					if (this.openCount == 1) {
						this.world.playSound(null, this.pos, SoundEvents.BLOCK_SHULKER_BOX_OPEN, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
					}
				}
			}

			@Override
			public void closeInventory(EntityPlayer player) {
				if (!player.isSpectator()) {
					MetalTransport.NETWORK.sendToAllAround(new UpdateShulkerBoxCart(pos, --this.openCount), world, pos);

					if (this.openCount <= 0) {
						this.world.playSound(null, this.pos, SoundEvents.BLOCK_SHULKER_BOX_CLOSE, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
					}
				}
			}
		};

		this.box.setWorld(world);
		this.box.setPos(cart.getPosition());
		this.cart = cart;

		if (stack.hasTagCompound()) {
			ItemStackHelper.loadAllItems(stack.getTagCompound().getCompoundTag("BlockEntityTag"), this.box.items);
		}
	}

	@Nonnull
	@Override
	public Container getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return new ContainerShulkerBox(player.inventory, box, player) {

			@Override
			public boolean canInteractWith(EntityPlayer player) {
				return cart.isEntityAlive() && player.getDistanceSq(cart) <= 64.0D;
			}
		};
	}

	@SideOnly(Side.CLIENT)
	@Nonnull
	@Override
	public GuiContainer getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return new GuiShulkerBox(player.inventory, box);
	}

	@Override
	public void tick(World world, BlockPos pos) {
		this.box.setPos(pos);
		this.box.update();
	}

	@Override
	public void activate(EntityPlayer player, EnumHand hand) {
		if (!player.world.isRemote) {
			MetalTransport.openGui(player, cart);
			player.addStat(StatList.OPEN_SHULKER_BOX);
		}
	}

	@Override
	public void onProfileDeletion() {
		ItemStackHelper.saveAllItems(cart.getDisplayStack().getTagCompound().getCompoundTag("BlockEntityTag"), this.box.items, true);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean renderDisplayStack(EntityMinecart cart, ItemStack stack, float partialTicks) {
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void render(Render renderer, EntityMinecart cart, ItemStack stack, float partialTicks) {
		TileEntityRendererDispatcher.instance.render(box, -0.5, 0, -0.5, partialTicks);
	}
}

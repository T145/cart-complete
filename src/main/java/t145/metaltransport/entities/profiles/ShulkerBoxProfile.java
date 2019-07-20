package t145.metaltransport.entities.profiles;

import javax.annotation.Nonnull;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiShulkerBox;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerShulkerBox;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import t145.metaltransport.api.consts.RegistryMT;
import t145.metaltransport.api.profiles.IProfileFactory;
import t145.metaltransport.api.profiles.IUniversalProfile;
import t145.metaltransport.entities.EntityMetalCart;

public class ShulkerBoxProfile extends TileEntityShulkerBox implements IUniversalProfile {

	public static class ProfileFactoryShulkerBox implements IProfileFactory {

		@Override
		public ShulkerBoxProfile create(EntityMinecart cart) {
			return new ShulkerBoxProfile((EntityMetalCart) cart);
		}
	}

	private final EntityMetalCart cart;

	public ShulkerBoxProfile(EntityMetalCart cart) {
		this.cart = cart;
		this.world = cart.world;

		ItemStack stack = cart.getDisplayStack();

		if (stack.hasTagCompound()) {
			NBTTagCompound shulkerTag = stack.getTagCompound().getCompoundTag("BlockEntityTag");

			if (shulkerTag.hasKey("Items")) {
				ItemStackHelper.loadAllItems(shulkerTag, items);
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setInteger("x", pos.getX());
		tag.setInteger("y", pos.getY());
		tag.setInteger("z", pos.getZ());
		this.saveToNbt(tag);
		return tag;
	}

	@Nonnull
	@Override
	public Container getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return new ContainerShulkerBox(player.inventory, this, player);
	}

	@SideOnly(Side.CLIENT)
	@Nonnull
	@Override
	public GuiContainer getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return new GuiShulkerBox(player.inventory, this);
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return cart.isEntityAlive() && player.getDistanceSq(cart.posX + 0.5D, cart.posY + 0.5D, cart.posZ + 0.5D) <= 64.0D;
	}

	@Override
	public void tick(World world, BlockPos pos) {
		this.pos = pos;
	}

	@Override
	public void activate(EntityPlayer player, EnumHand hand) {
		World world = cart.world;

		if (!world.isRemote) {
			BlockPos pos = cart.getPosition();
			player.openGui(RegistryMT.ID, cart.hashCode(), world, pos.getX(), pos.getY(), pos.getZ());
			player.addStat(StatList.OPEN_SHULKER_BOX);
		}
	}

	@Override
	public void openInventory(EntityPlayer player) {
		if (!player.isSpectator()) {
			if (this.openCount < 0) {
				this.openCount = 0;
			}

			++this.openCount;

			if (this.openCount == 1) {
				this.world.playSound(null, this.pos, SoundEvents.BLOCK_SHULKER_BOX_OPEN, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
			}
		}
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		if (!player.isSpectator()) {
			--this.openCount;

			if (this.openCount <= 0) {
				this.world.playSound(null, this.pos, SoundEvents.BLOCK_SHULKER_BOX_CLOSE, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
			}
		}
	}

	@Override
	public void onProfileDeletion() {
		ItemStackHelper.saveAllItems(cart.getDisplayStack().getTagCompound().getCompoundTag("BlockEntityTag"), this.items, true);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void render(Render renderer, EntityMinecart cart, ItemStack stack, float partialTicks) {
		//TileEntityRendererDispatcher.instance.render(this, -0.5, 0, -0.5, partialTicks);
	}
}

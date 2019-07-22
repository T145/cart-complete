package t145.metaltransport.entities.profiles;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import t145.metalchests.api.chests.IMetalChest;
import t145.metalchests.api.consts.ChestType;
import t145.metalchests.blocks.BlockMetalChest;
import t145.metalchests.client.gui.GuiMetalChest;
import t145.metalchests.containers.ContainerMetalChest;
import t145.metalchests.tiles.TileMetalChest;
import t145.metalchests.tiles.TileMetalHungryChest;
import t145.metalchests.tiles.TileMetalSortingHungryChest;
import t145.metaltransport.api.profiles.IProfileFactory;
import t145.metaltransport.api.profiles.IUniversalProfile;
import t145.metaltransport.core.MetalTransport;
import t145.metaltransport.entities.EntityMetalCart;
import t145.metaltransport.net.UpdateMetalChestCart;
import t145.tbone.lib.ChestAnimator;

public class MetalChestProfile implements IUniversalProfile {

	public static class ProfileFactoryMetalChest implements IProfileFactory {

		@Override
		public MetalChestProfile create(EntityMinecart cart) {
			return new MetalChestProfile((EntityMetalCart) cart);
		}
	}

	public final TileMetalChest chest;
	private final EntityMetalCart cart;

	public MetalChestProfile(EntityMetalCart cart) {
		Block block = cart.getDisplayBlock();
		World world = cart.world;

		this.chest = (TileMetalChest) block.createTileEntity(world, block.getDefaultState().withProperty(IMetalChest.VARIANT, ChestType.values()[cart.getDisplayStack().getItemDamage()]));
		this.chest.setWorld(world);
		this.chest.setPos(cart.getPosition());
		this.chest.animator = new ChestAnimator(false);
		this.cart = cart;
	}

	@Nonnull
	@Override
	public ContainerMetalChest getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return new ContainerMetalChest(chest, player.inventory) {

			{
				BlockPos pos = cart.getPosition();
				MetalTransport.NETWORK.sendToAllAround(new UpdateMetalChestCart(pos, ChestAnimator.EVENT_PLAYER_USED, chest.animator.numPlayersUsing), world, pos);
			}

			@Override
			public boolean canInteractWith(EntityPlayer player) {
				return cart.isEntityAlive() && player.getDistanceSq(cart) <= 64.0D;
			}

			@Override
			public void onContainerClosed(EntityPlayer player) {
				super.onContainerClosed(player);
				BlockPos pos = cart.getPosition();
				MetalTransport.NETWORK.sendToAllAround(new UpdateMetalChestCart(pos, ChestAnimator.EVENT_PLAYER_USED, chest.animator.numPlayersUsing), world, pos);
			}
		};
	}

	@SideOnly(Side.CLIENT)
	@Nonnull
	@Override
	public GuiMetalChest getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return new GuiMetalChest(this.getServerGuiElement(ID, player, world, x, y, z));
	}

	@Override
	public NBTTagCompound serializeNBT() {
		return chest.writeToNBT(new NBTTagCompound());
	}

	@Override
	public void deserializeNBT(NBTTagCompound tag) {
		chest.readFromNBT(tag);
	}

	@Override
	public void tick(World world, BlockPos pos) {
		chest.setPos(pos);
		chest.update();

		if (!world.isRemote && cart.isEntityAlive() && (chest instanceof TileMetalHungryChest || chest instanceof TileMetalSortingHungryChest)) {
			List<EntityItem> list = world.getEntitiesWithinAABB(EntityItem.class, cart.getEntityBoundingBox(), EntitySelectors.IS_ALIVE);

			if (!list.isEmpty()) {
				EntityItem item = list.get(0);
				item.playSound(SoundEvents.ENTITY_GENERIC_EAT, 0.25F, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F + 1.0F);
				MetalTransport.NETWORK.sendToAllAround(new UpdateMetalChestCart(pos, ChestAnimator.EVENT_CHEST_NOM, 2), world, pos);
				TileEntityHopper.putDropInInventoryAllSlots(null, chest, item);
			}
		}
	}

	@Override
	public void activate(EntityPlayer player, EnumHand hand) {
		if (!player.world.isRemote) {
			MetalTransport.openGui(player, cart);
		}
	}

	@Override
	public void onProfileDeletion() {
		BlockMetalChest.dropItems(chest, cart.world, cart.getPosition());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean renderDisplayStack(EntityMinecart cart, ItemStack stack, float partialTicks) {
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void render(Render renderer, EntityMinecart cart, ItemStack stack, float partialTicks) {
		TileEntityRendererDispatcher.instance.render(chest, -0.5, 0, -0.5, partialTicks);
	}
}

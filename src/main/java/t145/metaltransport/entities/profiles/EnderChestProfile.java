package t145.metaltransport.entities.profiles;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import t145.metaltransport.api.profiles.IProfileFactory;
import t145.metaltransport.api.profiles.IUniversalProfile;
import t145.metaltransport.core.MetalTransport;
import t145.metaltransport.net.UpdateEnderChestCart;

public class EnderChestProfile implements IUniversalProfile {

	public static class ProfileFactoryEnderChest implements IProfileFactory {

		@Override
		public EnderChestProfile create(EntityMinecart cart) {
			return new EnderChestProfile(cart);
		}
	}

	public final TileEntityEnderChest chest;
	private final EntityMinecart cart;

	public EnderChestProfile(EntityMinecart cart) {
		Block block = Blocks.ENDER_CHEST;
		World world = cart.world;

		this.chest = new TileEntityEnderChest() {

			@Override
			public boolean canBeUsed(EntityPlayer player) {
				return cart.isEntityAlive() && player.getDistanceSq(cart) <= 64.0D;
			}

			@Override
			public void openChest() {
				MetalTransport.NETWORK.sendToAllAround(new UpdateEnderChestCart(pos, ++this.numPlayersUsing), world, pos);
			}

			@Override
			public void closeChest() {
				MetalTransport.NETWORK.sendToAllAround(new UpdateEnderChestCart(pos, --this.numPlayersUsing), world, pos);
			}
		};

		this.chest.setWorld(world);
		this.chest.setPos(cart.getPosition());
		this.cart = cart;
	}

	@Override
	public void tick(World world, BlockPos pos) {
		if (!pos.equals(chest.getPos())) {
			this.chest.setPos(pos);
		}

		chest.update();

		if (world.isRemote && world.getTotalWorldTime() % 5L == 0L) {
			Blocks.ENDER_CHEST.randomDisplayTick(null, world, pos, world.rand);
		}
	}

	@Override
	public void activate(EntityPlayer player, EnumHand hand) {
		if (!player.world.isRemote) {
			InventoryEnderChest inv = player.getInventoryEnderChest();
			inv.setChestTileEntity(chest);
			player.displayGUIChest(inv);
			player.addStat(StatList.ENDERCHEST_OPENED);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean renderDisplayStack(EntityMinecart cart, ItemStack stack, float partialTicks) {
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void render(Render renderer, EntityMinecart cart, ItemStack stack, float partialTicks) {
		GlStateManager.rotate(90F, 0, 1, 0);
		TileEntityRendererDispatcher.instance.render(chest, -0.5, 0, -0.5, partialTicks);
	}
}

package t145.metaltransport.entities.profiles;

import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import t145.metaltransport.api.profiles.IProfileFactory;
import t145.metaltransport.api.profiles.IUniversalProfile;
import t145.metaltransport.core.MetalTransport;
import t145.metaltransport.entities.EntityMetalCart;

public class EnchantingTableProfile extends TileEntityEnchantmentTable implements IUniversalProfile {

	public static class ProfileFactoryEnchantingTable implements IProfileFactory {

		@Override
		public EnchantingTableProfile create(EntityMinecart cart) {
			return new EnchantingTableProfile(cart);
		}
	}

	private final EntityMinecart cart;

	public EnchantingTableProfile(EntityMinecart cart) {
		this.cart = cart;
		this.world = cart.world;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setInteger("x", pos.getX());
		tag.setInteger("y", pos.getY());
		tag.setInteger("z", pos.getZ());
		return tag;
	}

	@Nonnull
	@Override
	public Container getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return new ContainerEnchantment(player.inventory, world, cart.getPosition()) {

			@Override
			public void onCraftMatrixChanged(IInventory inventory) {
				super.onCraftMatrixChanged(inventory);
				// TODO: Tweak this to get influenced by cart contents that increase enchant power
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
		return new GuiEnchantment(player.inventory, world, this);
	}

	@Override
	public void tick(World world, BlockPos pos) {
		this.pos = cart.getPosition();
		this.update();

		if (world.isRemote && world.getTotalWorldTime() % 5L == 0L) {
			Random rand = world.rand;

			for (short i = -2; i <= 2; ++i) {
				for (short j = -2; j <= 2; ++j) {
					if (i > -2 && i < 2 && j == -1) {
						j = 2;
					}

					if (rand.nextInt(16) == 0) {
						for (short k = 0; k <= 1; ++k) {
							BlockPos blockpos = pos.add(i, k, j);
							List<EntityMinecart> carts = world.getEntitiesWithinAABB(EntityMinecart.class, new AxisAlignedBB(blockpos));
							float enchantPower = 0F;

							if (carts.isEmpty()) {
								enchantPower = ForgeHooks.getEnchantPower(world, blockpos);
							} else {
								EntityMinecart cart = carts.get(0);
								Block block = cart instanceof EntityMetalCart ? ((EntityMetalCart) cart).getDisplayBlock() : cart.getDisplayTile().getBlock();
								enchantPower = block.getEnchantPowerBonus(world, blockpos);
							}

							if (enchantPower > 0F) {
								if (carts.isEmpty() && !world.isAirBlock(pos.add(i / 2, 0, j / 2))) {
									break;
								}

								world.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, pos.getX() + 0.5D, pos.getY() + 2.0D, pos.getZ() + 0.5D, i + rand.nextFloat() - 0.5D, k - rand.nextFloat() - 1, j + rand.nextFloat() - 0.5D);
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void activate(EntityPlayer player, EnumHand hand) {
		if (!world.isRemote) {
			MetalTransport.openGui(player, cart);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void render(Render renderer, EntityMinecart cart, ItemStack stack, float partialTicks) {
		GlStateManager.rotate(90F, 0, 1, 0);
		TileEntityRendererDispatcher.instance.render(this, -0.5, -0.12, -0.5, partialTicks);
	}
}

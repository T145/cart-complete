package t145.metaltransport.entities.profiles;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.tileentity.TileEntityEnchantmentTableRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IWorldNameable;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import t145.metaltransport.api.consts.RegistryMT;
import t145.metaltransport.api.profiles.IProfileFactory;
import t145.metaltransport.api.profiles.IUniversalProfile;
import t145.metaltransport.entities.EntityMetalCart;

public class EnchantingTableProfile implements IUniversalProfile, IWorldNameable {

	public static class ProfileFactoryEnchantingTable implements IProfileFactory, IGuiHandler {

		@Override
		public EnchantingTableProfile create(EntityMinecart cart) {
			return new EnchantingTableProfile(cart);
		}

		@Override
		public ContainerEnchantment getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
			Entity entity = world.getEntityByID(ID);

			if (entity instanceof EntityMetalCart) {
				EntityMetalCart cart = (EntityMetalCart) entity;
				return new ContainerEnchantment(player.inventory, world, cart.getPosition()) {

					@Override
					public void onCraftMatrixChanged(IInventory inventory) {
						super.onCraftMatrixChanged(inventory);
						// TODO: Tweak this to get influenced by cart contents that increase enchant power
					}

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
		public GuiEnchantment getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
			Entity entity = world.getEntityByID(ID);

			if (entity instanceof EntityMetalCart) {
				EntityMetalCart cart = (EntityMetalCart) entity;
				return new GuiEnchantment(player.inventory, world, (IWorldNameable) cart.getProfile().get());
			}

			return null;
		}
	}

	private static final Random rand = new Random();
	private final ModelBook book = new ModelBook();
	private final EntityMinecart cart;
	public int tickCount;
	public float pageFlip;
	public float pageFlipPrev;
	public float flipT;
	public float flipA;
	public float bookSpread;
	public float bookSpreadPrev;
	public float bookRotation;
	public float bookRotationPrev;
	public float tRot;

	public EnchantingTableProfile(EntityMinecart cart) {
		this.cart = cart;
	}

	private float calcBookRotation(float diff) {
		float result;

		for (result = diff; result >= Math.PI; result -= (Math.PI * 2F));

		while (result < -Math.PI) {
			result += (Math.PI * 2F);
		}

		return result;
	}

	private EntityPlayer getClosePlayer(World world) {
		return world.getClosestPlayer(cart.posX, cart.posY + 0.5D, cart.posZ, 3.0D, false);
	}

	@Override
	public void tick(World world, BlockPos pos) {
		this.bookSpreadPrev = this.bookSpread;
		this.bookRotationPrev = this.bookRotation;
		EntityPlayer player = getClosePlayer(world);

		if (player != null) {
			double d0 = player.posX - cart.posX;
			double d1 = player.posZ - cart.posZ;
			this.tRot = (float) MathHelper.atan2(d1, d0);
			this.bookSpread += 0.1F;

			if (this.bookSpread < 0.5F || rand.nextInt(40) == 0) {
				float f1 = this.flipT;

				while (true) {
					this.flipT += (rand.nextInt(4) - rand.nextInt(4));

					if (f1 != this.flipT) {
						break;
					}
				}
			}
		} else {
			this.tRot += 0.02F;
			this.bookSpread -= 0.1F;
		}

		while (this.bookRotation >= Math.PI) {
			this.bookRotation -= (Math.PI * 2F);
		}

		while (this.bookRotation < -Math.PI) {
			this.bookRotation += (Math.PI * 2F);
		}

		while (this.tRot >= Math.PI) {
			this.tRot -= (Math.PI * 2F);
		}

		while (this.tRot < -Math.PI) {
			this.tRot += (Math.PI * 2F);
		}

		this.bookRotation += calcBookRotation(this.tRot - this.bookRotation) * 0.4F;
		this.bookSpread = MathHelper.clamp(this.bookSpread, 0.0F, 1.0F);
		++this.tickCount;
		this.pageFlipPrev = this.pageFlip;
		float f = (this.flipT - this.pageFlip) * 0.4F;
		float f3 = 0.2F;
		f = MathHelper.clamp(f, -0.2F, 0.2F);
		this.flipA += (f - this.flipA) * 0.9F;
		this.pageFlip += this.flipA;

		if (world.isRemote && world.getTotalWorldTime() % 5L == 0L) {
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

								world.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, pos.getX() + 0.5D,
										pos.getY() + 2.0D, pos.getZ() + 0.5D,
										i + rand.nextFloat() - 0.5D,
										k - rand.nextFloat() - 1,
										j + rand.nextFloat() - 0.5D);
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void activate(EntityPlayer player, EnumHand hand) {
		World world = cart.world;

		if (!world.isRemote) {
			BlockPos pos = cart.getPosition();
			player.openGui(RegistryMT.ID, cart.hashCode(), world, pos.getX(), pos.getY(), pos.getZ());
		}
	}

	private float computeYRotation(EntityMinecart cart, float partialTicks) {
		EntityPlayer player = getClosePlayer(cart.world);
		float rot = 180F;

		if (player != null) {
		}

		float f2 = bookRotationPrev + this.calcBookRotation(bookRotation - bookRotationPrev) * partialTicks;
		return (float) (-f2 * (rot / Math.PI));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void render(Render renderer, EntityMinecart cart, ItemStack stack, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.rotate(computeYRotation(cart, partialTicks), 0, 1, 0);
		GlStateManager.translate(0, 0.65, 0);

		float f = tickCount + partialTicks;
		GlStateManager.translate(0, 0.1F + MathHelper.sin(f * 0.1F) * 0.01F, 0);
		GlStateManager.rotate(80F, 0, 0, 1);
		renderer.bindTexture(TileEntityEnchantmentTableRenderer.TEXTURE_BOOK);

		float f3 = pageFlipPrev + (pageFlip - pageFlipPrev) * partialTicks + 0.25F;
		float f4 = pageFlipPrev + (pageFlip - pageFlipPrev) * partialTicks + 0.75F;

		f3 = (f3 - MathHelper.fastFloor(f3)) * 1.6F - 0.3F;
		f4 = (f4 - MathHelper.fastFloor(f4)) * 1.6F - 0.3F;

		if (f3 < 0.0F) {
			f3 = 0.0F;
		}

		if (f4 < 0.0F) {
			f4 = 0.0F;
		}

		if (f3 > 1.0F) {
			f3 = 1.0F;
		}

		if (f4 > 1.0F) {
			f4 = 1.0F;
		}

		float f5 = bookSpreadPrev + (bookSpread - bookSpreadPrev) * partialTicks;

		GlStateManager.enableCull();
		book.render(cart, f, f3, f4, f5, 0.0F, 0.0625F);
		GlStateManager.popMatrix();
	}

	@Override
	public String getName() {
		return "container.enchant";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentTranslation(this.getName(), new Object[0]);
	}
}

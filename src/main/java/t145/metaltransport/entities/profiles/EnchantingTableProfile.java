package t145.metaltransport.entities.profiles;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import t145.metaltransport.api.profiles.IProfileFactory;
import t145.metaltransport.api.profiles.IUniversalProfile;

public class EnchantingTableProfile implements IUniversalProfile {

	public static class ProfileFactoryEnchantingTable implements IProfileFactory {

		@Override
		public EnchantingTableProfile create(EntityMinecart cart) {
			return new EnchantingTableProfile(cart);
		}
	}

	private static final Random rand = new Random();
	private static final ResourceLocation TEXTURE_BOOK = new ResourceLocation("textures/entity/enchanting_table_book.png");
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
			Blocks.ENCHANTING_TABLE.randomDisplayTick(null, world, pos, world.rand);
		}
	}

	@Override
	public void activate(EntityPlayer player, EnumHand hand) {}

	@Override
	public boolean attackCart(DamageSource source, float amount) {
		return false;
	}

	@Override
	public void killCart(DamageSource source, boolean dropItems) {}

	@Override
	public void onProfileDeletion() {}

	@Override
	public void onCartDeath() {}

	@Override
	public void fall(float distance, float damageMultiplier) {}

	@Override
	public void onActivatorRailPass(int x, int y, int z, boolean powered) {}

	@Override
	public void moveAlongTrack(BlockPos pos, IBlockState rail) {}

	@Override
	public void applyDrag() {}

	@Override
	public NBTTagCompound serializeNBT() {
		return null;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {}

	private float computeYRotation(EntityMinecart cart, float partialTicks) {
		EntityPlayer player = getClosePlayer(cart.world);
		float rot = 180F;

		if (player != null) {
			// TODO: Fix book rotation 
		}

		float f2 = bookRotationPrev + this.calcBookRotation(bookRotation - bookRotationPrev) * partialTicks;
		return (float) (-f2 * (rot / Math.PI));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void render(Render renderer, EntityMinecart cart, ItemStack stack, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.scale(0.75F, 0.75F, 0.75F);
		GlStateManager.rotate(computeYRotation(cart, partialTicks), 0, 1, 0);
		GlStateManager.translate(0, 0.65, 0);

		float f = tickCount + partialTicks;
		GlStateManager.translate(0.0F, 0.1F + MathHelper.sin(f * 0.1F) * 0.01F, 0.0F);
		GlStateManager.rotate(80.0F, 0.0F, 0.0F, 1.0F);
		renderer.bindTexture(TEXTURE_BOOK);

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
}

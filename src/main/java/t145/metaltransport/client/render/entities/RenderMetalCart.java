package t145.metaltransport.client.render.entities;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelMinecart;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import t145.metaltransport.api.caps.CapabilityCartType;
import t145.metaltransport.api.consts.CartTier;
import t145.metaltransport.api.profiles.IProfile;
import t145.metaltransport.api.profiles.IUniversalProfile;
import t145.metaltransport.entities.EntityMetalCart;

@SideOnly(Side.CLIENT)
public class RenderMetalCart extends Render<EntityMinecart> {

	private final Minecraft mc = Minecraft.getMinecraft();
	private final ModelBase model = new ModelMinecart();

	public RenderMetalCart(RenderManager manager) {
		super(manager);
		this.shadowSize = 0.5F;
	}

	public void computeTranslationAndRotation(EntityMinecart cart, double x, double y, double z, float entityYaw, float partialTicks) {
		long i = cart.getEntityId() * 493286711L;

		i = i * i * 4392167121L + i * 98761L;

		float f = (((i >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
		float f1 = (((i >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
		float f2 = (((i >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;

		GlStateManager.translate(f, f1, f2);

		double d0 = cart.lastTickPosX + (cart.posX - cart.lastTickPosX) * (double) partialTicks;
		double d1 = cart.lastTickPosY + (cart.posY - cart.lastTickPosY) * (double) partialTicks;
		double d2 = cart.lastTickPosZ + (cart.posZ - cart.lastTickPosZ) * (double) partialTicks;
		Vec3d vec3d = cart.getPos(d0, d1, d2);
		float f3 = cart.prevRotationPitch + (cart.rotationPitch - cart.prevRotationPitch) * partialTicks;

		if (vec3d != null) {
			Vec3d vec3d1 = cart.getPosOffset(d0, d1, d2, 0.3D);
			Vec3d vec3d2 = cart.getPosOffset(d0, d1, d2, -0.3D);

			if (vec3d1 == null) {
				vec3d1 = vec3d;
			}

			if (vec3d2 == null) {
				vec3d2 = vec3d;
			}

			x += vec3d.x - d0;
			y += (vec3d1.y + vec3d2.y) / 2.0D - d1;
			z += vec3d.z - d2;
			Vec3d vec3d3 = vec3d2.add(-vec3d1.x, -vec3d1.y, -vec3d1.z);

			if (vec3d3.length() != 0.0D) {
				vec3d3 = vec3d3.normalize();
				entityYaw = (float) (Math.atan2(vec3d3.z, vec3d3.x) * 180.0D / Math.PI);
				f3 = (float) (Math.atan(vec3d3.y) * 73.0D);
			}
		}

		GlStateManager.translate(x, y + 0.375F, z);
		GlStateManager.rotate(180.0F - entityYaw, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(-f3, 0.0F, 0.0F, 1.0F);
		float f5 = cart.getRollingAmplitude() - partialTicks;

		if (f5 > 0.0F) {
			// behaves slightly better than the normal minecart:
			// f6 is always computed alongside f5, so I just do it when we do something w/ f5
			float f6 = cart.getDamage() - partialTicks;

			if (f6 < 0.0F) {
				f6 = 0.0F;
			}

			GlStateManager.rotate(MathHelper.sin(f5) * f5 * f6 / 10.0F * cart.getRollingDirection(), 1.0F, 0.0F, 0.0F);
		}
	}

	public void renderDisplayStack(ItemStack stack) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0.28, 0);
		GlStateManager.rotate(90, 0, 1, 0);
		GlStateManager.scale(1.5, 1.5, 1.5);
		mc.getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.popMatrix();
	}

	public void renderDisplayTile(EntityMinecart cart, float partialTicks) {
		IBlockState state = cart.getDisplayTile();

		if (state.getRenderType() != EnumBlockRenderType.INVISIBLE) {
			GlStateManager.pushMatrix();
			this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			GlStateManager.scale(0.75F, 0.75F, 0.75F);
			GlStateManager.translate(-0.5F, (cart.getDisplayTileOffset() - 8) / 16.0F, 0.5F);

			GlStateManager.pushMatrix();
			mc.getBlockRendererDispatcher().renderBlockBrightness(state, cart.getBrightness());
			GlStateManager.popMatrix();

			GlStateManager.color(1, 1, 1, 1);
			GlStateManager.popMatrix();
		}
	}

	@Override
	public void doRender(EntityMinecart cart, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
		this.bindEntityTexture(cart);

		computeTranslationAndRotation(cart, x, y, z, entityYaw, partialTicks);

		if (this.renderOutlines) {
			GlStateManager.enableColorMaterial();
			GlStateManager.enableOutlineMode(this.getTeamColor(cart));
		}

		// render the cart itself
		GlStateManager.pushMatrix();
		this.bindEntityTexture(cart);
		GlStateManager.scale(-1, -1, 1);
		this.model.render(cart, 0, 0, -0.1F, 0, 0, 0.0625F);
		GlStateManager.popMatrix();

		if (cart instanceof EntityMetalCart) {
			EntityMetalCart minecart = (EntityMetalCart) cart;
			boolean exec = minecart.isExecutable();

			if (!minecart.getProfile().isPresent()
					|| !(minecart.getProfile().get() instanceof IUniversalProfile)
					|| ((IUniversalProfile) minecart.getProfile().get()).renderDisplayStack(minecart, minecart.getDisplayStack(), partialTicks)) {
				// behaves slightly better than the normal minecart:
				// Normal renders air; I just don't call the code
				if (minecart.hasDisplayStack()) {
					this.renderDisplayStack(minecart.getDisplayStack());
				} else if (cart.hasDisplayTile()) {
					this.renderDisplayTile(minecart, partialTicks);
				}
			}

			if (exec) {
				IProfile profile = minecart.getProfile().get();

				if (profile instanceof IUniversalProfile) {
					IUniversalProfile universal = (IUniversalProfile) profile;
					GlStateManager.pushMatrix();
					GlStateManager.scale(0.75F, 0.75F, 0.75F);
					universal.render(this, minecart, minecart.getDisplayStack(), partialTicks);
					GlStateManager.popMatrix();
				}
			}

			if (this.renderOutlines) {
				GlStateManager.disableOutlineMode();
				GlStateManager.disableColorMaterial();
			}

			GlStateManager.popMatrix();

			if (exec) {
				IProfile profile = minecart.getProfile().get();

				if (profile instanceof IUniversalProfile) {
					IUniversalProfile universal = (IUniversalProfile) profile;

					GlStateManager.pushMatrix();
					GlStateManager.translate(x, y + 0.375F, z);
					GlStateManager.scale(0.75F, 0.75F, 0.75F);
					universal.renderIndependently(this, minecart, minecart.getDisplayStack(), partialTicks);
					GlStateManager.popMatrix();
				}
			}
		} else {
			this.renderDisplayTile(cart, partialTicks);

			if (this.renderOutlines) {
				GlStateManager.disableOutlineMode();
				GlStateManager.disableColorMaterial();
			}

			GlStateManager.popMatrix();
		}

		super.doRender(cart, x, y, z, entityYaw, partialTicks);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityMinecart cart) {
		if (cart.hasCapability(CapabilityCartType.instance, null)) {
			return cart.getCapability(CapabilityCartType.instance, null).getType().getModel();
		}
		return CartTier.IRON.getModel();
	}
}
package T145.metaltransport.client.render.entities;

import java.util.Optional;

import T145.metaltransport.api.carts.ICartBehavior;
import T145.metaltransport.entities.EntityMetalMinecart;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelMinecart;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderMetalMinecart extends Render<EntityMetalMinecart> {

	protected ModelBase modelMinecart = new ModelMinecart();

	public RenderMetalMinecart(RenderManager renderManagerIn) {
		super(renderManagerIn);
		this.shadowSize = 0.5F;
	}

	@Override
	public void doRender(EntityMetalMinecart cart, double x, double y, double z, float entityYaw, float partialTicks) {
		Optional<ICartBehavior> optBehavior = cart.getBehavior();

		GlStateManager.pushMatrix();
		this.bindEntityTexture(cart);

		long i = cart.getEntityId() * 493286711L;

		i = i * i * 4392167121L + i * 98761L;

		float f = (((i >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
		float f1 = (((i >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
		float f2 = (((i >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;

		GlStateManager.translate(f, f1, f2);

		double d0 = cart.lastTickPosX + (cart.posX - cart.lastTickPosX) * (double) partialTicks;
		double d1 = cart.lastTickPosY + (cart.posY - cart.lastTickPosY) * (double) partialTicks;
		double d2 = cart.lastTickPosZ + (cart.posZ - cart.lastTickPosZ) * (double) partialTicks;
		double d3 = 0.3D;
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

		if (this.renderOutlines) {
			GlStateManager.enableColorMaterial();
			GlStateManager.enableOutlineMode(this.getTeamColor(cart));
		}

		// behaves slightly better than the normal minecart:
		// Normal renders air; I just don't call the code
		if (cart.hasDisplayTile()) {
			GlStateManager.pushMatrix();

			ItemStack stack = cart.getDisplayStack();
			GlStateManager.translate(0, 0.3, 0);
			GlStateManager.rotate(90, 0, 1, 0);
			GlStateManager.scale(1.5, 1.5, 1.5);
			Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);

			GlStateManager.color(1, 1, 1, 1);
			GlStateManager.popMatrix();
		}

		// render the cart itself
		this.bindEntityTexture(cart);
		GlStateManager.scale(-1.0F, -1.0F, 1.0F);
		this.modelMinecart.render(cart, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		GlStateManager.popMatrix();

		if (this.renderOutlines) {
			GlStateManager.disableOutlineMode();
			GlStateManager.disableColorMaterial();
		}

		super.doRender(cart, x, y, z, entityYaw, partialTicks);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityMetalMinecart cart) {
		return cart.getCartType().getResource();
	}
}

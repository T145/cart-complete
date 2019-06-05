package T145.metaltransport.client.render.entities;

import javax.annotation.Nonnull;

import T145.metaltransport.entities.EntityRidingBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderRidingBlock extends Render<EntityRidingBlock> {

	public RenderRidingBlock(RenderManager manager) {
		super(manager);
	}

	@Override
	public void doRender(@Nonnull EntityRidingBlock rider, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(rider, x, y, z, entityYaw, partialTicks);

		if (!rider.isRiding()) {
			return;
		}

		IBlockState state = rider.getInternalBlockState();

		if (state != null) {
			BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
			GlStateManager.enableRescaleNormal();
			GlStateManager.pushMatrix();

			GlStateManager.translate(x, y, z);
			GlStateManager.scale(0.75F, 0.75F, 0.75F);
			GlStateManager.translate(-0.5F, 0.38F, 0.5F);

			float f = 0.5F;
			int i = rider.getBrightnessForRender();
			int j = i % 65536;
			int k = i / 65536;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			blockrendererdispatcher.renderBlockBrightness(state, 1.0F);
			GlStateManager.popMatrix();
			GlStateManager.disableRescaleNormal();
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityRidingBlock rider) {
		return null;
	}
}

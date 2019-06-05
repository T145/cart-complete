package T145.metaltransport.client.render.entities;

import javax.annotation.Nonnull;

import T145.metaltransport.entities.EntityRidingBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
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

		if (!rider.isRiding() || rider.getDisplayStack().isEmpty()) {
			return;
		}

		//float rot = 180F - entityYaw;

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		//GlStateManager.rotate(90F, 0F, 1F, 0F);
		GlStateManager.translate(0F, 0.7F, -0.15F);

		//if (boat.getPassengers().size() == 1)
		//GlStateManager.translate(0F, 0F, 0.6F);

		GlStateManager.scale(1.75F, 1.75F, 1.75F);

		Minecraft.getMinecraft().getRenderItem().renderItem(rider.getDisplayStack(), ItemCameraTransforms.TransformType.FIXED);
		GlStateManager.popMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityRidingBlock rider) {
		return null;
	}
}

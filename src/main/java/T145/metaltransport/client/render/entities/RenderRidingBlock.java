package T145.metaltransport.client.render.entities;

import javax.annotation.Nonnull;

import T145.metaltransport.entities.EntityRidingBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
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

		Entity mount = rider.getRidingEntity();

		if (!rider.isRiding() || mount == null) {
			return;
		}

		ItemStack displayStack = rider.getDisplayStack();

		if (displayStack.isEmpty()) {
			return;
		}

		if (mount instanceof EntityMinecart) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y, z);
			GlStateManager.rotate(90F - entityYaw, 0F, 1F, 0F);
			GlStateManager.translate(0F, 0.655F, 0F);
			GlStateManager.scale(1.5F, 1.484F, 1.5F);
			Minecraft.getMinecraft().getRenderItem().renderItem(displayStack, ItemCameraTransforms.TransformType.FIXED);
			GlStateManager.popMatrix();
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityRidingBlock rider) {
		return null;
	}
}

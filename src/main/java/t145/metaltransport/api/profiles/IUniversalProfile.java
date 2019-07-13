package t145.metaltransport.api.profiles;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IUniversalProfile extends IProfile {

	// set through the entity data manager, and is exposed to both sides
	@SideOnly(Side.CLIENT)
	default void render(Render renderer, EntityMinecart cart, ItemStack stack, float partialTicks) {}
}

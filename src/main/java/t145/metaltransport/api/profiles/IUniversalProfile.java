package t145.metaltransport.api.profiles;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IUniversalProfile extends IProfile {

	// set through the entity data manager, and is exposed to both sides
	@SideOnly(Side.CLIENT)
	void render(BlockPos pos, ItemStack stack, float partialTicks);
}

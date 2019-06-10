package T145.metaltransport.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import T145.metaltransport.api.constants.RegistryMT;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerWorkbench;

@Mixin(ContainerWorkbench.class)
public abstract class WorkbenchContainerMixin {

	@Inject(method = "explode", at = @At("HEAD"), cancellable = true)
	public boolean canInteractWith(EntityPlayer player) {
		RegistryMT.LOG.info("MIXIN SUCCESSFUL!");
		return true;
	}
	
	static {
		RegistryMT.LOG.info("HELLO WORLD");
	}
}

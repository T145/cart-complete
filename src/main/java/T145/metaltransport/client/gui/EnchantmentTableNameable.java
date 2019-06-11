package T145.metaltransport.client.gui;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IWorldNameable;

public class EnchantmentTableNameable implements IWorldNameable {

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

package T145.metaltransport.entities.behaviors;

import T145.metaltransport.api.carts.ICartBehavior;
import T145.metaltransport.api.carts.ICartBehaviorFactory;
import T145.tbone.api.IInventoryHandler;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.items.ItemStackHandler;

public class ChestBehavior extends SimpleGuiBehavior implements IInventoryHandler {

	public static class ChestBehaviorFactory implements ICartBehaviorFactory {

		@Override
		public ICartBehavior createBehavior(EntityMinecart cart) {
			return new ChestBehavior(cart);
		}
	}

	protected final ItemStackHandler handler;

	public ChestBehavior(EntityMinecart cart, int invSize) {
		super(cart);
		this.handler = new ItemStackHandler(invSize);
	}

	public ChestBehavior(EntityMinecart cart) {
		this(cart, 27);
	}

	@Override
	public NBTTagCompound serialize() {
		NBTTagCompound tag = super.serialize();
		tag.setTag(TAG_INVENTORY, handler.serializeNBT());
		return tag;
	}

	@Override
	public ICartBehavior deserialize(NBTTagCompound tag) {
		super.deserialize(tag);
		handler.deserializeNBT(tag.getCompoundTag(TAG_INVENTORY));
		return this;
	}

	@Override
	public String getName() {
		return "container.chest";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentTranslation(this.getName(), new Object[0]);
	}

	@Override
	public ItemStackHandler getInventory() {
		return handler;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		EntityMinecart cart = this.getCart();
		return !cart.isDead && player.getDistanceSq(cart) <= 64.0D;
	}

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) {}
}

package T145.metaltransport.entities.behaviors;

import T145.metaltransport.api.carts.CartBehavior;
import T145.metaltransport.api.carts.ICartBehavior;
import T145.metaltransport.api.carts.ICartBehaviorFactory;
import T145.tbone.api.IInventoryLootHandler;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;

public class ChestBehavior extends CartBehavior implements IInventoryLootHandler {

	public static class ChestBehaviorFactory implements ICartBehaviorFactory {

		@Override
		public ICartBehavior createBehavior(EntityMinecart cart) {
			return new ChestBehavior(cart);
		}
	}

	protected ItemStackHandler handler;
	private ResourceLocation lootTable;
	private long lootTableSeed;

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
	public void activate(EntityPlayer player, EnumHand hand) {
		EntityMinecart cart = this.getCart();

		if (!cart.world.isRemote) {
			player.displayGUIChest(this);
		}
	}

	@Override
	public void onDeletion() {
		EntityMinecart cart = this.getCart();
		World world = cart.world;

		for (short i = 0; i < this.getSizeInventory(); ++i) {
			ItemStack stack = this.getStackInSlot(i);

			if (!stack.isEmpty()) {
				InventoryHelper.spawnItemStack(world, cart.posX, cart.posY, cart.posZ, stack);
			}
		}
	}

	@Override
	public void onDeath() {}

	@Override
	public ItemStackHandler getInventory() {
		return handler;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		EntityMinecart cart = this.getCart();
		return !cart.isDead && player.getDistanceSq(cart) <= 64;
	}

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) {}

	@Override
	public String getName() {
		return "container.chest";
	}

	@Override
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer player) {
		return new ContainerChest(playerInventory, this, player);
	}

	@Override
	public String getGuiID() {
		return "minecraft.chest";
	}

	@Override
	public ResourceLocation getLootTable() {
		return lootTable;
	}

	@Override
	public void setLootTable(ResourceLocation lootTable, long lootTableSeed) {
		this.lootTable = lootTable;
		this.lootTableSeed = lootTableSeed;
	}
}

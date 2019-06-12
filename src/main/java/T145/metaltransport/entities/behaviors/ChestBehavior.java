package T145.metaltransport.entities.behaviors;

import java.util.Random;

import javax.annotation.Nullable;

import T145.metaltransport.api.carts.CartBehavior;
import T145.metaltransport.api.carts.ICartBehavior;
import T145.metaltransport.api.carts.ICartBehaviorFactory;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.LockCode;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.ILootContainer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;

public class ChestBehavior extends CartBehavior implements ILockableContainer, ILootContainer {

	public static class ChestBehaviorFactory implements ICartBehaviorFactory {

		@Override
		public ICartBehavior createBehavior(EntityMinecart cart) {
			return new ChestBehavior(cart);
		}
	}

	private NonNullList<ItemStack> stacks = NonNullList.<ItemStack>withSize(36, ItemStack.EMPTY);
	private ResourceLocation lootTable;
	private long lootTableSeed;

	public ChestBehavior(EntityMinecart cart) {
		super(cart);
	}

	@Override
	public NBTTagCompound serialize() {
		NBTTagCompound tag = super.serialize();
		if (this.lootTable != null) {
			tag.setString("LootTable", this.lootTable.toString());

			if (this.lootTableSeed != 0L) {
				tag.setLong("LootTableSeed", this.lootTableSeed);
			}
		} else {
			ItemStackHelper.saveAllItems(tag, this.stacks);
		}
		return tag;
	}

	@Override
	public ICartBehavior deserialize(NBTTagCompound tag) {
		super.deserialize(tag);
		this.stacks = NonNullList.<ItemStack>withSize(this.getSizeInventory(), ItemStack.EMPTY);

		if (tag.hasKey("LootTable", 8)) {
			this.lootTable = new ResourceLocation(tag.getString("LootTable"));
			this.lootTableSeed = tag.getLong("LootTableSeed");
		} else {
			ItemStackHelper.loadAllItems(tag, this.stacks);
		}
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
	public int getSizeInventory() {
		return 27;
	}

	public boolean isEmpty() {
		for (ItemStack itemstack : this.stacks) {
			if (!itemstack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		this.addLoot(null);
		return this.stacks.get(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		this.addLoot(null);
		return ItemStackHelper.getAndSplit(this.stacks, index, count);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		this.addLoot(null);
		ItemStack itemstack = this.stacks.get(index);

		if (itemstack.isEmpty()) {
			return ItemStack.EMPTY;
		} else {
			this.stacks.set(index, ItemStack.EMPTY);
			return itemstack;
		}
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		this.addLoot(null);
		this.stacks.set(index, stack);

		if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit()) {
			stack.setCount(this.getInventoryStackLimit());
		}
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void markDirty() {}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		EntityMinecart cart = this.getCart();

		if (cart.isDead) {
			return false;
		} else {
			return player.getDistanceSq(cart) <= 64.0D;
		}
	}

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) {}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return true;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		this.addLoot(null);
		this.stacks.clear();
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
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer player) {
		return new ContainerChest(playerInventory, this, player);
	}

	@Override
	public String getGuiID() {
		return "minecraft:chest";
	}

	public void addLoot(@Nullable EntityPlayer player) {
		if (this.lootTable != null) {
			EntityMinecart cart = this.getCart();
			World world = cart.world;
			LootTable loottable = world.getLootTableManager().getLootTableFromLocation(this.lootTable);
			this.lootTable = null;
			Random random;

			if (this.lootTableSeed == 0L) {
				random = new Random();
			} else {
				random = new Random(this.lootTableSeed);
			}

			LootContext.Builder lootcontext$builder = new LootContext.Builder((WorldServer) world)
					.withLootedEntity(cart); // Forge: add looted entity to LootContext

			if (player != null) {
				lootcontext$builder.withLuck(player.getLuck()).withPlayer(player); // Forge: add player to LootContext
			}

			loottable.fillInventory(this, random, lootcontext$builder.build());
		}
	}

	public void setLootTable(ResourceLocation lootTableIn, long lootTableSeedIn) {
		this.lootTable = lootTableIn;
		this.lootTableSeed = lootTableSeedIn;
	}

	@Override
	public ResourceLocation getLootTable() {
		return lootTable;
	}

	@Override
	public boolean isLocked() {
		return false;
	}

	@Override
	public void setLockCode(LockCode code) {}

	@Override
	public LockCode getLockCode() {
		return LockCode.EMPTY_CODE;
	}
}

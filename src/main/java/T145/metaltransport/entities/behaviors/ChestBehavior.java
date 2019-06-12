package T145.metaltransport.entities.behaviors;

import java.util.List;
import java.util.stream.Collectors;

import T145.metaltransport.api.carts.ICartBehavior;
import T145.metaltransport.core.MetalTransport;
import T145.tbone.api.IInventoryHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.oredict.OreDictionary;

public class ChestBehavior extends SimpleGuiBehavior implements IInventoryHandler {

	private static Block[] getAllWoodenChests() {
		NonNullList<ItemStack> chestStacks = OreDictionary.getOres("chestWood");

		if (chestStacks.size() > 1) {
			// if we have more than just the vanilla chest
			List<Block> blocks = chestStacks.stream()
					.map(chestStack -> MetalTransport.getBlockFromStack(chestStack))
					.collect(Collectors.toList());
			blocks.add(Blocks.TRAPPED_CHEST);
			return blocks.toArray(new Block[blocks.size()]);
		} else {
			return new Block[] { Blocks.CHEST, Blocks.TRAPPED_CHEST };
		}
	}

	private ItemStackHandler inv;

	public ChestBehavior(int invSize) {
		super(getAllWoodenChests());
		inv = new ItemStackHandler(invSize);
	}

	public ChestBehavior() {
		this(27);
	}
	
	@Override
	public NBTTagCompound serialize() {
		NBTTagCompound tag = super.serialize();
		tag.setTag(TAG_INVENTORY, inv.serializeNBT());
		return tag;
	}

	@Override
	public ICartBehavior deserialize(NBTTagCompound tag) {
		super.deserialize(tag);
		inv.deserializeNBT(tag.getCompoundTag(TAG_INVENTORY));
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
		return inv;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) {}
}

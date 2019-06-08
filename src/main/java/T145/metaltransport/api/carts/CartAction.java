package T145.metaltransport.api.carts;

import java.util.HashSet;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import scala.actors.threadpool.Arrays;

public class CartAction implements ICartAction {

	private HashSet<String> nameSet;
	protected String[] blockNames;

	public CartAction() {
		this.blockNames = new String[10];
		this.nameSet = new HashSet<>();
	}

	public CartAction(Block[] blocks) {
		this.blockNames = new String[blocks.length];

		for (short i = 0; i < blocks.length; ++i) {
			blockNames[i] = blocks[i].getRegistryName().toString();
		}

		this.nameSet = new HashSet<>(Arrays.asList(blockNames));
	}

	public CartAction(Block block) {
		this(new Block[] { block });
	}

	@Override
	public String[] getBlockNames() {
		return blockNames;
	}

	@Override
	public boolean hasBlockName(String blockName) {
		return nameSet.contains(blockName);
	}

	@OverridingMethodsMustInvokeSuper
	@Override
	public NBTTagCompound serialize() {
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagList names = new NBTTagList();

		for (short i = 0; i < blockNames.length; ++i) {
			names.appendTag(new NBTTagString(blockNames[i]));
		}

		tag.setInteger("Size", blockNames.length);
		tag.setTag("BlockNames", names);

		return tag;
	}

	@OverridingMethodsMustInvokeSuper
	@Override
	public void deserialize(NBTTagCompound tag) {
		blockNames = new String[tag.getInteger("Size")];
		NBTTagList names = tag.getTagList("BlockNames", Constants.NBT.TAG_STRING);

		for (short i = 0; i < names.tagCount(); ++i) {
			blockNames[i] = names.getStringTagAt(i);
		}

		this.nameSet = new HashSet<>(Arrays.asList(blockNames));
	}

	@Override
	public double getMaxCartSpeed() {
		return DEFAULT_CART_SPEED;
	}

	@Override
	public void tick(EntityMinecart cart) {}

	@Override
	public void moveAlongTrack(EntityMinecart cart, BlockPos pos, IBlockState rail) {}

	@Override
	public void applyDrag(EntityMinecart cart) {}

	@Override
	public boolean activate(EntityMinecart cart, EntityPlayer player, EnumHand hand) {
		return true;
	}
}

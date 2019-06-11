package T145.metaltransport.entities.behaviors;

import T145.metaltransport.api.carts.CartBehavior;
import T145.metaltransport.api.constants.RegistryMT;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class JukeboxBehavior extends CartBehavior {

	private ItemStack record = ItemStack.EMPTY;

	public JukeboxBehavior() {
		super(Blocks.JUKEBOX);
	}

	public ItemStack getRecord() {
		return this.record;
	}

	public void setRecord(ItemStack recordStack) {
		this.record = recordStack;
	}

	@Override
	public NBTTagCompound serialize() {
		NBTTagCompound tag = super.serialize();

		if (!this.getRecord().isEmpty()) {
			tag.setTag("RecordItem", this.getRecord().writeToNBT(new NBTTagCompound()));
		}

		return tag;
	}

	@Override
	public JukeboxBehavior deserialize(NBTTagCompound tag) {
		super.deserialize(tag);

		if (tag.hasKey("RecordItem", 10)) {
			this.setRecord(new ItemStack(tag.getCompoundTag("RecordItem")));
		} else if (tag.getInteger("Record") > 0) {
			this.setRecord(new ItemStack(Item.getItemById(tag.getInteger("Record"))));
		}

		return this;
	}

	public void insertRecord(ItemStack recordStack) {
		setRecord(recordStack.copy());
	}

	public void stopPlaying(World world, BlockPos pos) {
		world.playEvent(1010, pos, 0);
		world.playRecord(pos, null);
	}

	public void dropRecord(EntityMinecart cart) {
		this.stopPlaying(cart.world, cart.getPosition());
		cart.entityDropItem(record, 0);
	}

	@Override
	public void activate(EntityMinecart cart, EntityPlayer player, EnumHand hand) {
		World world = cart.world;

		if (!world.isRemote) {
			BlockPos pos = cart.getPosition();
			ItemStack stack = player.getHeldItem(hand);
			Item item = stack.getItem();
			RegistryMT.LOG.info(stack);

			if (item instanceof ItemRecord) {
				insertRecord(stack);
				world.playEvent(null, 1010, pos, Item.getIdFromItem(item));
				stack.shrink(1);
				player.addStat(StatList.RECORD_PLAYED);
			} else {
				this.dropRecord(cart);
			}
		}
	}

	@Override
	public void killMinecart(EntityMinecart cart, DamageSource source, boolean dropItems) {
		if (dropItems) {
			this.dropRecord(cart);
		}
	}

	@Override
	public void onDeath(EntityMinecart cart) {
		this.stopPlaying(cart.world, cart.getPosition());
	}
}

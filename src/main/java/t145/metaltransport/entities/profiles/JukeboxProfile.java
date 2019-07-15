package t145.metaltransport.entities.profiles;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import t145.metaltransport.api.profiles.IProfileFactory;
import t145.metaltransport.api.profiles.IServerProfile;

public class JukeboxProfile implements IServerProfile {

	public static class ProfileFactoryJukebox implements IProfileFactory {

		@Override
		public JukeboxProfile create(EntityMinecart cart) {
			return new JukeboxProfile(cart);
		}
	}

	private static final String RECORD_TAG = "Record";
	public final EntityMinecart cart;
	public ItemStack record = ItemStack.EMPTY;

	public JukeboxProfile(EntityMinecart cart) {
		this.cart = cart;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound tag = new NBTTagCompound();

		if (!record.isEmpty()) {
			tag.setTag(RECORD_TAG, record.serializeNBT());
		}

		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound tag) {
		if (tag.hasKey(RECORD_TAG)) {
			this.record = new ItemStack(tag.getCompoundTag(RECORD_TAG));
		}
	}

	private void dropRecord(float yOffset) {
		World world = cart.world;
		BlockPos pos = cart.getPosition();

		world.playEvent(1010, pos, 0);
		world.playRecord(pos, null);

		if (!world.isRemote) {
			cart.entityDropItem(record, yOffset);
		}

		record = ItemStack.EMPTY;
	}

	@Override
	public void activate(EntityPlayer player, EnumHand hand) {
		if (record.isEmpty()) {
			ItemStack stack = player.getHeldItem(hand);
			Item item = stack.getItem();

			if (item instanceof ItemRecord) {
				this.record = stack.copy();
				cart.world.playEvent(player, 1010, cart.getPosition(), Item.getIdFromItem(item));
				stack.shrink(1);
				player.addStat(StatList.RECORD_PLAYED);
			}
		} else {
			dropRecord(0.75F);
		}
	}

	@Override
	public void onProfileDeletion() {
		dropRecord(0F);
	}

	@Override
	public void killCart(DamageSource source, boolean dropItems) {
		if (dropItems) {
			dropRecord(0F);
		}
	}
}

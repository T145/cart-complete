package T145.metaltransport.entities.profiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Range;

import T145.metaltransport.api.profiles.ICartProfileFactory;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerBeacon;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityBeacon.BeamSegment;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;

public class BeaconProfile extends ChestProfile {

	public static class BeaconProfileFactory implements ICartProfileFactory {

		@Override
		public BeaconProfile create(EntityMinecart cart) {
			return new BeaconProfile(cart);
		}
	}

	private final Range<Double> speedRange = Range.closed(-0.14D, 0.14D);
	private final MutableBlockPos railCenter = new MutableBlockPos();

	public boolean isComplete;
	private int levels = -1;
	@Nullable private Potion primaryEffect;
	@Nullable private Potion secondaryEffect;
	private ItemStack payment = ItemStack.EMPTY;

	// CLIENT-SIDE VARS
	public final List<BeamSegment> beamSegments = new ArrayList<>();
	public long beamRenderCounter;
	public float beamRenderScale;

	public BeaconProfile(EntityMinecart cart) {
		super(cart, 1);
	}

	public float getTextureScale(World client) {
		if (!this.isComplete) {
			return 0.0F;
		} else {
			int i = (int) (client.getTotalWorldTime() - this.beamRenderCounter);
			this.beamRenderCounter = client.getTotalWorldTime();

			if (i > 1) {
				this.beamRenderScale -= (float) i / 40.0F;

				if (this.beamRenderScale < 0.0F) {
					this.beamRenderScale = 0.0F;
				}
			}

			this.beamRenderScale += 0.025F;

			if (this.beamRenderScale > 1.0F) {
				this.beamRenderScale = 1.0F;
			}

			return this.beamRenderScale;
		}
	}

	@Override
	public NBTTagCompound serialize() {
		NBTTagCompound tag = super.serialize();
		tag.setInteger("Levels", levels);

		if (primaryEffect != null) {
			tag.setInteger("PrimaryEffect", Potion.getIdFromPotion(primaryEffect));
		}

		if (secondaryEffect != null) {
			tag.setInteger("SecondaryEffect", Potion.getIdFromPotion(secondaryEffect));
		}

		return tag;
	}

	@Override
	public BeaconProfile deserialize(NBTTagCompound tag) {
		super.deserialize(tag);
		levels = tag.getInteger("Levels");

		if (tag.hasKey("PrimaryEffect")) {
			primaryEffect = Potion.getPotionById(tag.getInteger("PrimaryEffect"));
		}

		if (tag.hasKey("SecondaryEffect")) {
			secondaryEffect = Potion.getPotionById(tag.getInteger("SecondaryEffect"));
		}

		return this;
	}

	private void addEffectsToPlayers(World world, BlockPos pos) {
		if (isComplete && levels > 0 && !world.isRemote && primaryEffect != null) {
			double d0 = (levels * 10 + 10);
			int i = levels >= 4 && primaryEffect == secondaryEffect ? 1 : 0;
			int j = (9 + levels * 2) * 20;
			int k = pos.getX();
			int l = pos.getY();
			int i1 = pos.getZ();
			AxisAlignedBB box = (new AxisAlignedBB(k, l, i1, k + 1, l + 1, i1 + 1)).grow(d0).expand(0.0D, world.getHeight(), 0.0D);
			boolean hasSecondaryEffect = levels >= 4 && primaryEffect != secondaryEffect && secondaryEffect != null;

			/*
			 * Behaves slightly better than a normal beacon: Instead of iterating over the
			 * players twice to apply the secondary effect this just does it once.
			 */
			world.getEntitiesWithinAABB(EntityPlayer.class, box).forEach(player -> {
				player.addPotionEffect(new PotionEffect(primaryEffect, j, i, true, true));

				if (hasSecondaryEffect) {
					player.addPotionEffect(new PotionEffect(secondaryEffect, j, 0, true, true));
				}
			});
		}
	}

	// TODO: Optimize this vanilla code
	private void updateSegmentColors(World world, BlockPos pos) {
		int i = pos.getX();
		int j = pos.getY();
		int k = pos.getZ();
		int l = levels;
		levels = 0;
		beamSegments.clear();
		isComplete = true;
		BeamSegment seg = new BeamSegment(EnumDyeColor.WHITE.getColorComponentValues());
		beamSegments.add(seg);
		boolean flag = true;
		BlockPos.MutableBlockPos mutPos = new BlockPos.MutableBlockPos();

		for (int i1 = j + 1; i1 < 256; ++i1) {
			IBlockState state = world.getBlockState(mutPos.setPos(i, i1, k));
			float[] afloat;

			if (state.getBlock() == Blocks.STAINED_GLASS) {
				afloat = state.getValue(BlockStainedGlass.COLOR).getColorComponentValues();
			} else {
				if (state.getBlock() != Blocks.STAINED_GLASS_PANE) {
					if (state.getLightOpacity(world, mutPos) >= 15 && state.getBlock() != Blocks.BEDROCK) {
						isComplete = false;
						beamSegments.clear();
						break;
					}

					float[] customColor = state.getBlock().getBeaconColorMultiplier(state, world, mutPos, pos);

					if (customColor != null) {
						afloat = customColor;
					} else {
						seg.incrementHeight();
						continue;
					}
				} else {
					afloat = state.getValue(BlockStainedGlassPane.COLOR).getColorComponentValues();
				}
			}

			if (!flag) {
				afloat = new float[] { (seg.getColors()[0] + afloat[0]) / 2.0F, (seg.getColors()[1] + afloat[1]) / 2.0F, (seg.getColors()[2] + afloat[2]) / 2.0F };
			}

			if (Arrays.equals(afloat, seg.getColors())) {
				seg.incrementHeight();
			} else {
				seg = new BeamSegment(afloat);
				beamSegments.add(seg);
			}

			flag = false;
		}

		if (isComplete) {
			for (int l1 = 1; l1 <= 4; levels = l1++) {
				int i2 = j - l1;

				if (i2 < 0) {
					break;
				}

				boolean flag1 = true;

				for (int j1 = i - l1; j1 <= i + l1 && flag1; ++j1) {
					for (int k1 = k - l1; k1 <= k + l1; ++k1) {
						Block block = world.getBlockState(new BlockPos(j1, i2, k1)).getBlock();

						if (!block.isBeaconBase(world, new BlockPos(j1, i2, k1), pos)) {
							flag1 = false;
							break;
						}
					}
				}

				if (!flag1) {
					break;
				}
			}

			if (levels == 0) {
				isComplete = false;
			}
		}

		if (l < levels) {
			for (EntityPlayerMP player : world.getEntitiesWithinAABB(EntityPlayerMP.class, (new AxisAlignedBB(i, j, k, i, (j - 4), k)).grow(10.0D, 5.0D, 10.0D))) {
				CriteriaTriggers.CONSTRUCT_BEACON.trigger(player, new TileEntityBeacon());
			}
		}
	}

	public boolean isCartMovingSlowly() {
		EntityMinecart cart = this.getCart();
		return speedRange.contains(cart.motionX) && speedRange.contains(cart.motionZ);
	}

	@Override
	public void tickServer(World world, BlockPos pos) {
		if (this.isCartMovingSlowly() && world.getTotalWorldTime() % 80L == 0L) {
			this.updateSegmentColors(world, pos);
			this.addEffectsToPlayers(world, pos);
		}
	}

	@Override
	public void onActivatorRailPass(int x, int y, int z, boolean powered) {
		if (powered) {
			EntityMinecart cart = this.getCart();
			cart.motionX = 0;
			cart.motionY = 0;
			cart.motionZ = 0;

			if (!cart.getPosition().equals(this.railCenter.setPos(x, y, z))) {
				cart.setPosition(x, y, z);
			}
		}
	}

	@Override
	public void activate(EntityPlayer player, EnumHand hand) {
		super.activate(player, hand);
		player.addStat(StatList.BEACON_INTERACTION);
	}

	@Override
	public String getName() {
		return "container.beacon";
	}

	@Override
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer player) {
		return new ContainerBeacon(playerInventory, this);
	}

	@Override
	public String getGuiID() {
		return "minecraft:beacon";
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return stack.getItem() != null && stack.getItem().isBeaconPayment(stack);
	}

	@Override
	public int getField(int id) {
		switch (id) {
		case 0:
			return levels;
		case 1:
			return Potion.getIdFromPotion(primaryEffect);
		case 2:
			return Potion.getIdFromPotion(secondaryEffect);
		default:
			return 0;
		}
	}

	@Override
	public void setField(int id, int value) {
		switch (id) {
		case 0:
			levels = value;
			break;
		case 1:
			primaryEffect = Potion.getPotionById(value);
			break;
		case 2:
			secondaryEffect = Potion.getPotionById(value);
			break;
		}
	}

	@Override
	public int getFieldCount() {
		return 3;
	}

	@Override
	public void clear() {
		payment = ItemStack.EMPTY;
	}
}

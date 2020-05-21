package t145.metaltransport.entities;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.function.Function;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockCommandBlock;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockMobSpawner;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import t145.metaltransport.api.caps.CapabilityCartType;
import t145.metaltransport.api.carts.IMetalCart;
import t145.metaltransport.api.consts.CartTier;
import t145.metaltransport.api.consts.CartType;
import t145.metaltransport.api.consts.RegistryMT;
import t145.metaltransport.api.objs.ItemsMT;
import t145.metaltransport.api.profiles.IProfile;
import t145.metaltransport.api.profiles.IServerProfile;
import t145.metaltransport.api.profiles.IUniversalProfile;
import t145.metaltransport.api.profiles.ProfileRegistry;

public class EntityMetalCart extends EntityMinecart implements IMetalCart {

	private static final DataParameter<ItemStack> DISPLAY_STACK = EntityDataManager.createKey(EntityMetalCart.class, DataSerializers.ITEM_STACK);
	private static final DataParameter<Boolean> SHOW_STACK = EntityDataManager.createKey(EntityMetalCart.class, DataSerializers.BOOLEAN);
	private static final DataParameter<NBTTagCompound> UNIVERSAL_PROFILE_DATA = EntityDataManager.createKey(EntityMetalCart.class, DataSerializers.COMPOUND_TAG);
	private Optional<IProfile> profile = Optional.empty();

	public static final Object2ObjectOpenHashMap<Item, Function<EntityMinecart, EntityMinecart>> CARTS = new Object2ObjectOpenHashMap<>();

	static {
		CARTS.put(Item.getItemFromBlock(Blocks.CHEST), (EntityMinecart e) -> getMinecart(new ResourceLocation("chest_minecart"), e.getEntityWorld(), e.posX, e.posY, e.posZ));
		CARTS.put(Item.getItemFromBlock(Blocks.TNT), (EntityMinecart e) -> getMinecart(new ResourceLocation("tnt_minecart"), e.getEntityWorld(), e.posX, e.posY, e.posZ));
		CARTS.put(Item.getItemFromBlock(Blocks.FURNACE), (EntityMinecart e) -> getMinecart(new ResourceLocation("furnace_minecart"), e.getEntityWorld(), e.posX, e.posY, e.posZ));
		CARTS.put(Item.getItemFromBlock(Blocks.HOPPER), (EntityMinecart e) -> getMinecart(new ResourceLocation("hopper_minecart"), e.getEntityWorld(), e.posX, e.posY, e.posZ));
		CARTS.put(Item.getItemFromBlock(Blocks.COMMAND_BLOCK), (EntityMinecart e) -> getMinecart(new ResourceLocation("commandblock_minecart"), e.getEntityWorld(), e.posX, e.posY, e.posZ));
		CARTS.put(Item.getItemFromBlock(Blocks.MOB_SPAWNER), (EntityMinecart e) -> getMinecart(new ResourceLocation("spawner_minecart"), e.getEntityWorld(), e.posX, e.posY, e.posZ));
	}

	public EntityMetalCart(World world) {
		super(world);
	}

	public EntityMetalCart(World world, double x, double y, double z) {
		super(world, x, y, z);
	}

	public EntityMetalCart(EntityMinecart cart) {
		this(cart.world, cart.posX, cart.posY, cart.posZ);

		if (cart.hasCapability(CapabilityCartType.instance, null)) {
			CartTier type = cart.getCapability(CapabilityCartType.instance, null).getType();
			this.getCapability(CapabilityCartType.instance, null).setType(type);
		}

		motionX = cart.motionX;
		motionY = cart.motionY;
		motionZ = cart.motionZ;
		rotationPitch = cart.rotationPitch;
		rotationYaw = cart.rotationYaw;

		if (EntityFurnaceCart.isSpeeding(cart)) {
			lastTickPosX = cart.lastTickPosX;
			lastTickPosY = cart.lastTickPosY;
			lastTickPosZ = cart.lastTickPosZ;
		}
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(DISPLAY_STACK, ItemStack.EMPTY);
		this.dataManager.register(SHOW_STACK, false);
		this.dataManager.register(UNIVERSAL_PROFILE_DATA, new NBTTagCompound());
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tag) {
		if (tag.hasKey(TAG_DISPLAY_STACK)) {
			this.setDisplayStack(new ItemStack(tag.getCompoundTag(TAG_DISPLAY_STACK)));
		} else {
			super.readEntityFromNBT(tag);
		}

		Block block = this.getDisplayBlock();
		ResourceLocation key = block.getRegistryName();

		if (ProfileRegistry.contains(key)) {
			IProfile update = ProfileRegistry.get(key).create(this);

			if (tag.hasKey(TAG_UNIVERSAL_PROFILE)) {
				NBTTagCompound profileTag = tag.getCompoundTag(TAG_UNIVERSAL_PROFILE);
				this.dataManager.set(UNIVERSAL_PROFILE_DATA, profileTag);
				update.deserializeNBT(profileTag);
			} else if (tag.hasKey(TAG_PROFILE)) {
				update.deserializeNBT(tag.getCompoundTag(TAG_PROFILE));
				this.profile = Optional.of(update);
			} else {
				this.profile = Optional.of(update);
			}
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag) {
		if (this.hasDisplayStack()) {
			tag.setTag(TAG_DISPLAY_STACK, this.getDisplayStack().serializeNBT());
		} else {
			super.writeEntityToNBT(tag);
		}

		this.profile.ifPresent(profile -> {
			NBTTagCompound profileTag = profile.serializeNBT();

			if (profileTag != null) {
				if (!this.dataManager.get(UNIVERSAL_PROFILE_DATA).isEmpty() || profile instanceof IUniversalProfile) {
					tag.setTag(TAG_UNIVERSAL_PROFILE, profileTag);
				} else {
					tag.setTag(TAG_PROFILE, profileTag);
				}
			}
		});
	}

	@Override
	public void notifyDataManagerChange(DataParameter<?> key) {
		if (key == UNIVERSAL_PROFILE_DATA && !profile.isPresent()) {
			Block block = this.getDisplayBlock();
			ResourceLocation reg = block.getRegistryName();

			if (ProfileRegistry.contains(reg)) {
				IUniversalProfile update = (IUniversalProfile) ProfileRegistry.get(reg).create(this);
				update.deserializeNBT(this.dataManager.get(UNIVERSAL_PROFILE_DATA));
				this.profile = Optional.of(update);
			}
		}
	}

	@Override
	public EntityMinecart.Type getType() {
		if (this.hasDisplayBlock()) {
			Block block = this.getDisplayBlock();

			if (block instanceof BlockCommandBlock) {
				return EntityMinecart.Type.COMMAND_BLOCK;
			}

			if (block instanceof BlockFurnace) {
				return EntityMinecart.Type.FURNACE;
			}

			if (block instanceof BlockHopper) {
				return EntityMinecart.Type.HOPPER;
			}

			if (block instanceof BlockMobSpawner) {
				return EntityMinecart.Type.SPAWNER;
			}

			if (block instanceof BlockTNT) {
				return EntityMinecart.Type.TNT;
			}

			if (block instanceof BlockChest) {
				return EntityMinecart.Type.CHEST;
			} else {
				ItemStack chestStack = new ItemStack(block);

				if (OreDictionary.getOres("chest").contains(chestStack)) {
					return EntityMinecart.Type.CHEST;
				}
			}
		}

		return EntityMinecart.Type.RIDEABLE;
	}

	@Override
	public ItemStack getDisplayStack() {
		return this.dataManager.get(DISPLAY_STACK);
	}

	@Override
	public EntityMetalCart setDisplayStack(ItemStack stack) {
		ItemStack copyStack = stack.copy();

		if (copyStack.getCount() > 1) {
			copyStack.setCount(1);
		}

		this.dataManager.set(DISPLAY_STACK, copyStack);
		this.setHasDisplayStack(!copyStack.isEmpty());
		return this;
	}

	@Override
	public boolean hasDisplayStack() {
		return this.dataManager.get(SHOW_STACK);
	}

	@Override
	public void setHasDisplayStack(boolean hasStack) {
		if (hasStack) {
			this.setHasDisplayTile(false);
		}

		this.dataManager.set(SHOW_STACK, hasStack);
	}

	@Override
	public void setHasDisplayTile(boolean showBlock) {
		if (showBlock) {
			this.setHasDisplayStack(false);
		}

		super.setHasDisplayTile(showBlock);
	}

	@Override
	public Block getDisplayBlock() {
		if (this.hasDisplayStack()) {
			return Block.getBlockFromItem(this.getDisplayStack().getItem());
		}

		if (this.hasDisplayTile()) {
			return this.getDisplayTile().getBlock();
		}

		return Blocks.AIR;
	}

	@Override
	public boolean hasDisplayBlock() {
		return this.hasDisplayStack() || this.hasDisplayTile();
	}

	@Override
	public Optional<IProfile> getProfile() {
		return this.profile;
	}

	@Override
	public EntityMetalCart setProfile() {
		if (this.hasDisplayBlock()) {
			Block block = this.getDisplayBlock();
			ResourceLocation key = block.getRegistryName();

			if (ProfileRegistry.contains(key)) {
				this.profile = Optional.of(ProfileRegistry.get(key).create(this));
				return this;
			}
		}

		this.profile = Optional.empty();
		return this;
	}

	public boolean isExecutable() {
		Optional<IProfile> opt = this.getProfile();

		if (!opt.isPresent()) {
			return false;
		}

		IProfile profile = opt.get();

		return (profile instanceof IServerProfile && !world.isRemote) || !(profile instanceof IServerProfile && world.isRemote);
	}

	private ItemStack getDropStack() {
		if (this.hasDisplayStack()) {
			return this.getDisplayStack().copy();
		} else {
			Block block = this.getDisplayBlock();
			return new ItemStack(block, 1, block.getMetaFromState(getDisplayTile()));
		}
	}

	protected CartTier getCartType() {
		return this.getCapability(CapabilityCartType.instance, null).getType();
	}

	@Override
	public ItemStack getCartItem() {
		return new ItemStack(ItemsMT.METAL_MINECART, 1, CartType.getEmptyType(getCartType()).ordinal());
	}

	@Override
	public boolean canBeRidden() {
		return !this.hasDisplayBlock();
	}

	@Override
	public String getName() {
		if (hasCustomName()) {
			return getCustomNameTag();
		} else {
			return I18n.translateToLocal(String.format("item.metaltransport:metal_minecart.%s.name", getCartType().getName()));
		}
	}

	@Override
	public ITextComponent getDisplayName() {
		TextComponentString name = (TextComponentString) super.getDisplayName();

		if (this.hasDisplayBlock()) {
			return name.appendText(I18n.translateToLocalFormatted("%s", "com.metaltransport.info.with")).appendText(this.getDisplayStack().getDisplayName());
		}

		return name;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (this.isExecutable()) {
			this.profile.get().tick(world, getPosition());
		}
	}

	@Override
	protected void moveAlongTrack(BlockPos pos, IBlockState rail) {
		super.moveAlongTrack(pos, rail);

		if (this.isExecutable()) {
			this.profile.get().moveAlongTrack(pos, rail);
		}
	}

	@Override
	protected void applyDrag() {
		if (this.isExecutable()) {
			this.profile.get().applyDrag();
		}

		super.applyDrag();
	}

	@Override
	public void fall(float distance, float damageMultiplier) {
		if (this.isExecutable()) {
			this.profile.get().fall(distance, damageMultiplier);
		}

		super.fall(distance, damageMultiplier);
	}

	public void removeDisplayBlock(boolean dying) {
		if (this.isExecutable()) {
			IProfile profile = this.profile.get();

			profile.onProfileDeletion();

			if (dying) {
				profile.onCartDeath();
			}
		}

		this.setDisplayStack(ItemStack.EMPTY);
		this.setHasDisplayTile(false);
	}

	/**
	 * Copied from Quark:
	 * https://github.com/Vazkii/Quark/blob/master/src/main/java/vazkii/quark/tweaks/feature/MinecartInteraction.java#L57
	 */
	private static EntityMinecart getMinecart(ResourceLocation rl, World world, double x, double y, double z) {
		try {
			EntityEntry entry = ForgeRegistries.ENTITIES.getValue(rl);
			if (entry != null) {
				Class<? extends Entity> minecartClass = entry.getEntityClass();
				return (EntityMinecart) minecartClass
						.getConstructor(World.class, double.class, double.class, double.class)
						.newInstance(world, x, y, z);
			}
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException err) {
			RegistryMT.LOG.catching(err);
		}
		return null;
	}

	private static Block getBlockFromStack(ItemStack stack) {
		return Block.getBlockFromItem(stack.getItem());
	}

	private static boolean isSolidBlock(ItemStack stack) {
		return !stack.isEmpty() && getBlockFromStack(stack) != Blocks.AIR /* && block is relatively normal && in whitelist || not in blacklist */;
	}

	@Override
	public boolean processInitialInteract(final EntityPlayer player, final EnumHand hand) {
		if (MinecraftForge.EVENT_BUS.post(new MinecartInteractEvent(this, player, hand)) || this.isBeingRidden()) {
			return true;
		}

		if (this.canBeRidden() && !this.isBeingRidden()) {
			ItemStack stack = player.getHeldItem(hand);

			if (isSolidBlock(stack)) {
				if (CARTS.containsKey(stack.getItem())) {
					if (!world.isRemote) {
						EntityMinecart minecart = CARTS.get(stack.getItem()).apply(this);

						if (minecart != null) {
							minecart.getCapability(CapabilityCartType.instance, null).setType(this.getCartType());

							this.setDead();
							world.spawnEntity(minecart);

							if (!player.capabilities.isCreativeMode) {
								stack.shrink(1);

								if (stack.getCount() <= 0) {
									player.setHeldItem(hand, ItemStack.EMPTY);
								}
							}
						}
					}

					player.swingArm(hand);
				} else {
					this.setDisplayStack(stack).setProfile();

					if (!world.isRemote && !player.isCreative()) {
						stack.shrink(1);
					}

					player.swingArm(hand);
				}
			} else if (!this.world.isRemote) {
				player.startRiding(this);
			}
		} else if (this.hasDisplayBlock()) {
			if (player.isSneaking()) {
				ItemStack stack = this.getDisplayStack();
				this.removeDisplayBlock(false);

				if (!world.isRemote) {
					this.entityDropItem(stack, 0);
				}

				this.setProfile();

				return true;
			}

			if (this.isExecutable()) {
				this.profile.get().activate(player, hand);
			}
		}

		return true;
	}

	@Override
	public void onActivatorRailPass(int x, int y, int z, boolean receivingPower) {
		if (this.hasDisplayBlock() && this.isExecutable()) {
			this.profile.get().onActivatorRailPass(x, y, z, receivingPower);
		} else if (receivingPower) {
			if (this.isBeingRidden()) {
				this.removePassengers();
			}

			if (this.getRollingAmplitude() == 0) {
				this.setRollingDirection(-this.getRollingDirection());
				this.setRollingAmplitude(10);
				this.setDamage(50.0F);
				this.markVelocityChanged();
			}
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (this.profile.isPresent() && this.profile.get().attackCart(source, amount)) {
			return true;
		} // useful for the note block
		return super.attackEntityFrom(source, amount);
	}

	@Override
	public void setDead() {
		super.setDead();
		this.removeDisplayBlock(true);
	}

	@Override
	public void killMinecart(DamageSource source) {
		boolean dropItems = this.world.getGameRules().getBoolean("doEntityDrops");
		ItemStack drop = this.getDropStack();

		if (this.isExecutable()) {
			this.profile.get().killCart(source, dropItems);
		}

		this.setDead();

		if (dropItems) {
			ItemStack cart = this.getCartItem();

			if (this.hasCustomName()) {
				cart.setStackDisplayName(this.getCustomNameTag());
			}

			this.entityDropItem(cart, 0);

			if (!world.isRemote && (!source.isExplosion() || this.getCartType() == CartTier.OBSIDIAN) && !drop.isEmpty()) {
				this.entityDropItem(drop, 0);
			}
		}
	}
}

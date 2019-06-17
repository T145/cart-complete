package T145.metaltransport.entities;

import java.util.Optional;

import T145.metaltransport.MetalTransport;
import T145.metaltransport.api.carts.CartProfileRegistry;
import T145.metaltransport.api.carts.IMetalMinecart;
import T145.metaltransport.api.consts.CartType;
import T145.metaltransport.api.obj.ItemsMT;
import T145.metaltransport.api.obj.SerializersMT;
import T145.metaltransport.api.profiles.ICartProfile;
import T145.metaltransport.net.client.SyncProfileWithClient;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockCommandBlock;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockMobSpawner;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
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
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;
import net.minecraftforge.oredict.OreDictionary;

public class EntityMetalMinecart extends EntityMinecart implements IMetalMinecart {

	private static final DataParameter<CartType> CART_TYPE = EntityDataManager.createKey(EntityMetalMinecart.class, SerializersMT.CART_TYPE);
	private static final DataParameter<ItemStack> DISPLAY_STACK = EntityDataManager.createKey(EntityMetalMinecart.class, DataSerializers.ITEM_STACK);
	private static final DataParameter<Boolean> SHOW_STACK = EntityDataManager.createKey(EntityMetalMinecart.class, DataSerializers.BOOLEAN);
	private Optional<ICartProfile> profile = Optional.empty();

	public EntityMetalMinecart(World world) {
		super(world);
	}

	public EntityMetalMinecart(World world, double x, double y, double z) {
		super(world, x, y, z);
	}

	public EntityMetalMinecart(EntityMinecart cart) {
		super(cart.world, cart.posX, cart.posY, cart.posZ);

		if (cart instanceof EntityMetalMinecart) {
			this.setCartType(((EntityMetalMinecart) cart).getCartType());
		}

		this.prevPosX = cart.prevPosX;
		this.prevPosY = cart.prevPosY;
		this.prevPosZ = cart.prevPosZ;
		this.posX = cart.posX;
		this.posY = cart.posY;
		this.posZ = cart.posZ;
		this.motionX = cart.motionX;
		this.motionY = cart.motionY;
		this.motionZ = cart.motionZ;
		this.rotationYaw = cart.rotationYaw;
		this.rotationPitch = cart.rotationPitch;
		this.prevRotationYaw = cart.prevRotationYaw;
		this.prevRotationPitch = cart.prevRotationPitch;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(CART_TYPE, CartType.IRON);
		this.dataManager.register(DISPLAY_STACK, ItemStack.EMPTY);
		this.dataManager.register(SHOW_STACK, false);
	}

	@Override
	public CartType getCartType() {
		return this.dataManager.get(CART_TYPE);
	}

	@Override
	public EntityMetalMinecart setCartType(CartType type) {
		this.dataManager.set(CART_TYPE, type);
		return this;
	}

	@Override
	public ItemStack getDisplayStack() {
		return this.dataManager.get(DISPLAY_STACK);
	}

	@Override
	public EntityMetalMinecart setDisplayStack(ItemStack stack) {
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
	public Optional<ICartProfile> getCartProfile() {
		return this.profile;
	}

	@Override
	public EntityMetalMinecart setCartProfile() {
		if (this.hasDisplayBlock()) {
			Block block = this.getDisplayBlock();
			ResourceLocation key = block.getRegistryName();

			if (CartProfileRegistry.contains(key)) {
				this.profile = Optional.of(CartProfileRegistry.get(key).createProfile(this));
			} else {
				this.profile = Optional.empty();
			}
		} else {
			this.profile = Optional.empty();
		}

		MetalTransport.NETWORK.sendToAllAround(new SyncProfileWithClient(this.getPosition()));

		return this;
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

	private ItemStack getDropStack() {
		if (this.hasDisplayStack()) {
			return this.getDisplayStack().copy();
		} else {
			Block block = this.getDisplayBlock();
			return new ItemStack(block, 1, block.getMetaFromState(getDisplayTile()));
		}
	}

	@Override
	public ItemStack getCartItem() {
		return new ItemStack(ItemsMT.METAL_MINECART, 1, getCartType().ordinal());
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
			return I18n.format(String.format("item.metaltransport:metal_minecart.%s.name", getCartType().getName()));
		}
	}

	@Override
	public ITextComponent getDisplayName() {
		TextComponentString name = (TextComponentString) super.getDisplayName();

		if (this.hasDisplayBlock()) {
			return name.appendText(" With ").appendText(this.getDisplayStack().getDisplayName());
		}

		return name;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tag) {
		this.setCartType(CartType.valueOf(tag.getString(TAG_CART_TYPE)));

		if (tag.hasKey(TAG_HAS_STACK)) {
			this.setDisplayStack(new ItemStack(tag.getCompoundTag(TAG_DISPLAY_STACK)));
		} else {
			super.readEntityFromNBT(tag);
		}

		if (tag.hasKey(TAG_CART_PROFILE)) {
			Block block = this.getDisplayBlock();
			ResourceLocation key = block.getRegistryName();

			if (CartProfileRegistry.contains(key)) {
				this.profile = Optional.of(CartProfileRegistry.get(key).createProfile(this).deserialize(tag.getCompoundTag(TAG_CART_PROFILE)));
			}
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag) {
		tag.setString(TAG_CART_TYPE, this.getCartType().toString());

		if (this.hasDisplayStack()) {
			tag.setByte(TAG_HAS_STACK, (byte) 1);
			tag.setTag(TAG_DISPLAY_STACK, this.getDisplayStack().serializeNBT());
		} else {
			super.writeEntityToNBT(tag);
		}

		this.profile.ifPresent(profile -> {
			tag.setTag(TAG_CART_PROFILE, profile.serialize());
		});
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		/*
		 * == Profile Detailing
		 * 
		 * Calling this from the client will always crash the game
		 * if getCart() is called, since getPersistentId() is
		 * different for each thread. Profiles can only do things
		 * from the server anyway, so any client interaction will
		 * have to be done through packets. This removes passing
		 * a live minecart instance as a valid work-around.
		 * Luckily any client work is usually just spawning particles.
		 */

		if (!world.isRemote) {
			this.profile.ifPresent(profile -> profile.tickServer(world, this.getPosition()));
		}
	}

	@Override
	protected void moveAlongTrack(BlockPos pos, IBlockState rail) {
		super.moveAlongTrack(pos, rail);
		this.profile.ifPresent(profile -> profile.moveAlongTrack(pos, rail));
	}

	@Override
	protected void applyDrag() {
		this.profile.ifPresent(profile -> profile.applyDrag());
		super.applyDrag();
	}

	@Override
	public void fall(float distance, float damageMultiplier) {
		this.profile.ifPresent(profile -> profile.fall(distance, damageMultiplier));
		super.fall(distance, damageMultiplier);
	}

	public void removeDisplayBlock() {
		this.setDisplayStack(ItemStack.EMPTY);
		this.setHasDisplayTile(false);
		this.profile.ifPresent(profile -> profile.onProfileDeletion());
	}

	@Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
		if (MinecraftForge.EVENT_BUS.post(new MinecartInteractEvent(this, player, hand)) || this.isBeingRidden()) {
			return true;
		}

		if (this.hasDisplayBlock()) {
			if (player.isSneaking()) {
				ItemStack stack = this.getDisplayStack();
				this.removeDisplayBlock();

				if (!world.isRemote) {
					this.entityDropItem(stack, 0);
					this.profile.ifPresent(profile -> profile.onProfileDeletion());
				}

				this.setCartProfile();

				return true;
			}

			this.profile.ifPresent(profile -> profile.activate(player, hand));
		} else if (!this.world.isRemote) {
			player.startRiding(this);
		}

		return true;
	}

	@Override
	public void onActivatorRailPass(int x, int y, int z, boolean receivingPower) {
		if (this.hasDisplayBlock()) {
			this.profile.ifPresent(profile -> profile.onActivatorRailPass(x, y, z, receivingPower));
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

		this.profile.ifPresent(profile -> {
			if (!world.isRemote) {
				profile.onProfileDeletion();
			}
			profile.onCartDeath();
		});
	}

	@Override
	public void killMinecart(DamageSource source) {
		boolean dropItems = this.world.getGameRules().getBoolean("doEntityDrops");

		this.profile.ifPresent(profile -> profile.killCart(source, dropItems));
		this.setDead();

		if (dropItems) {
			ItemStack stack = this.getCartItem();

			if (this.hasCustomName()) {
				stack.setStackDisplayName(this.getCustomNameTag());
			}

			this.entityDropItem(stack, 0);

			if (!world.isRemote && (!source.isExplosion() || this.getCartType() == CartType.OBSIDIAN) && this.hasDisplayTile()) {
				this.entityDropItem(this.getDropStack(), 0);
			}
		}
	}
}

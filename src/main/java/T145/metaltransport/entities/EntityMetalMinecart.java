package T145.metaltransport.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import T145.metaltransport.api.ItemsMT;
import T145.metaltransport.api.SerializersMT;
import T145.metaltransport.api.carts.CartBehaviorRegistry;
import T145.metaltransport.api.carts.ICartBehavior;
import T145.metaltransport.api.carts.IMetalMinecart;
import T145.metaltransport.api.carts.IMetalMinecartBlock;
import T145.metaltransport.api.constants.CartType;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityMetalMinecart extends EntityMinecartEmpty implements IMetalMinecart {

	private static final DataParameter<ItemStack> DISPLAY = EntityDataManager.createKey(EntityMetalMinecart.class, DataSerializers.ITEM_STACK);
	private static final DataParameter<Optional<ICartBehavior>> BEHAVIOR = EntityDataManager.createKey(EntityMetalMinecart.class, SerializersMT.CART_BEHAVIOR);
	private static final DataParameter<CartType> CART_TYPE = EntityDataManager.createKey(EntityMetalMinecart.class, SerializersMT.CART_TYPE);

	private static final Map<String, EntityMinecart.Type> MINECART_TYPES = new HashMap() {{
		put("minecraft:chest", EntityMinecart.Type.CHEST);
		put("minecraft:trapped_chest", EntityMinecart.Type.CHEST);
		put("minecraft:ender_chest", EntityMinecart.Type.CHEST);
		put("minecraft:furnace", EntityMinecart.Type.FURNACE);
		put("minecraft:lit_furnace", EntityMinecart.Type.FURNACE);
		put("minecraft:tnt", EntityMinecart.Type.TNT);
		put("minecraft:mob_spawner", EntityMinecart.Type.SPAWNER);
		put("minecraft:hopper", EntityMinecart.Type.HOPPER);
		put("minecraft:command_block", EntityMinecart.Type.COMMAND_BLOCK);
	}};

	public EntityMetalMinecart(World world) {
		super(world);
	}

	public EntityMetalMinecart(World world, double x, double y, double z) {
		super(world, x, y, z);
	}

	public EntityMetalMinecart(EntityMinecart cart) {
		this(cart.getEntityWorld(), cart.prevPosX, cart.prevPosY, cart.prevPosZ);

		if (cart instanceof EntityMetalMinecart) {
			this.setCartType(((EntityMetalMinecart) cart).getCartType());
		} else if (cart.hasDisplayTile()) {
			this.setDisplayTile(cart.getDisplayTile());
		}

		this.posX = cart.posX;
		this.posY = cart.posY;
		this.posZ = cart.posZ;
		this.motionX = cart.motionX;
		this.motionY = cart.motionY;
		this.motionZ = cart.motionZ;
		this.rotationPitch = cart.rotationPitch;
		this.rotationYaw = cart.rotationYaw;
	}

	@Override
	public void setDisplayTile(IBlockState state) {
		ItemStack stack = this.getDisplayStack();
		Block block = state.getBlock();

		if (!stack.isEmpty()) {
			state = block.getStateFromMeta(stack.getItemDamage());
		}

		super.setDisplayTile(state);
	}

	@Override
	public ItemStack getDisplayStack() {
		return this.dataManager.get(DISPLAY);
	}

	@Override
	public EntityMetalMinecart setDisplayStack(ItemStack stack) {
		ItemStack copyStack = stack.copy();

		if (stack.getCount() > 1) {
			copyStack.setCount(1);
		}

		this.dataManager.set(DISPLAY, copyStack);
		return this.setDisplayItem(stack.getItem(), stack.getItemDamage());
	}

	protected EntityMetalMinecart setDisplayItem(Item item, int meta) {
		return this.setDisplayBlock(Block.getBlockFromItem(item), meta);
	}

	protected EntityMetalMinecart setDisplayBlock(Block block, int meta) {
		if (block instanceof IMetalMinecartBlock) {
			IMetalMinecartBlock cartBlock = (IMetalMinecartBlock) block;
			return this.setDisplayState(cartBlock.getDisplayState(this, meta), meta);
		}

		return this.setDisplayState(block.getDefaultState(), meta);
	}

	protected EntityMetalMinecart setDisplayState(IBlockState state, int meta) {
		String blockName = state.getBlock().getRegistryName().toString();
		this.setBehavior(Optional.ofNullable(CartBehaviorRegistry.get(blockName)));
		this.setDisplayTile(state);
		return this;
	}

	@Override
	public Optional<ICartBehavior> getBehavior() {
		return this.dataManager.get(BEHAVIOR);
	}

	@Override
	public void setBehavior(Optional<ICartBehavior> behavior) {
		this.dataManager.set(BEHAVIOR, behavior);
	}

	@Override
	public CartType getCartType() {
		return dataManager.get(CART_TYPE);
	}

	@Override
	public EntityMinecart setCartType(CartType type) {
		dataManager.set(CART_TYPE, type);
		return this;
	}

	@Override
	public EntityMinecart.Type getType() {
		Optional<ICartBehavior> behavior = this.getBehavior();
		if (behavior.isPresent()) {
			return MINECART_TYPES.get(behavior.get().getBlockNames()[0]);
		}
		return super.getType();
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(CART_TYPE, CartType.IRON);
		this.dataManager.register(DISPLAY, ItemStack.EMPTY);
		this.dataManager.register(BEHAVIOR, Optional.empty());
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag) {
		super.writeEntityToNBT(tag);
		tag.setString(TAG_CART_TYPE, getCartType().toString());
		NBTTagCompound stackTag = new NBTTagCompound();
		this.getDisplayStack().writeToNBT(stackTag);
		tag.setTag(TAG_DISPLAY, stackTag);
		this.getBehavior().ifPresent(behavior -> {
			tag.setByte("HasBehavior", (byte) 1);
			tag.setTag(TAG_BEHAVIOR, this.getBehavior().get().serialize());
		});
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tag) {
		super.readEntityFromNBT(tag);
		setCartType(CartType.valueOf(tag.getString(TAG_CART_TYPE)));
		this.dataManager.set(DISPLAY, new ItemStack(tag.getCompoundTag(TAG_DISPLAY)));

		if (tag.hasKey("HasBehavior")) {
			NBTTagCompound behaviorTag = tag.getCompoundTag(TAG_BEHAVIOR);
			NBTTagList names = behaviorTag.getTagList("BlockNames", Constants.NBT.TAG_STRING);

			Optional.ofNullable(CartBehaviorRegistry.get(names.getStringTagAt(0))).ifPresent(behavior -> {
				this.setBehavior(Optional.of(behavior.deserialize(behaviorTag)));
			});
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void handleStatusUpdate(byte id) {
		this.getBehavior().ifPresent(behavior -> behavior.handleStatusUpdate(this, id));
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		this.getBehavior().ifPresent(behavior -> behavior.tick(this));
	}

	@Override
	protected double getMaximumSpeed() {
		this.getBehavior().ifPresent(behavior -> behavior.getMaxCartSpeed());
		return super.getMaximumSpeed();
	}

	@Override
	protected void moveAlongTrack(BlockPos pos, IBlockState rail) {
		super.moveAlongTrack(pos, rail);
		this.getBehavior().ifPresent(behavior -> behavior.moveAlongTrack(this, pos, rail));
	}

	@Override
	protected void applyDrag() {
		this.getBehavior().ifPresent(behavior -> behavior.applyDrag(this));
		super.applyDrag();
	}

	@Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
		if (MinecraftForge.EVENT_BUS.post(new MinecartInteractEvent(this, player, hand)) || this.isBeingRidden()) {
			return true;
		}

		if (player.isSneaking()) {
			if (this.hasDisplayTile()) {
				this.dropDisplayStack();
			}
			return true;
		}

		if (this.hasDisplayTile()) {
			this.getBehavior().ifPresent(behavior -> behavior.activate(this, player, hand));
		} else if (!this.world.isRemote) {
			player.startRiding(this);
		}

		return true;
	}

	@Override
	public ItemStack getCartItem() {
		return new ItemStack(ItemsMT.METAL_MINECART, 1, getCartType().ordinal());
	}

	@Override
	public boolean canBeRidden() {
		return !this.hasDisplayTile();
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

		if (this.hasDisplayTile()) {
			return name.appendText(" With ").appendText(this.getDisplayStack().getDisplayName());
		}

		return name;
	}

	public ItemStack removeDisplayStack() {
		ItemStack stack = this.getDisplayStack();

		if (stack.isEmpty()) {
			IBlockState state = this.getDisplayTile();
			Block block = state.getBlock();
			stack = new ItemStack(block, 1, block.getMetaFromState(state));
		}

		if (this.isEntityAlive()) {
			this.setDisplayTile(getDefaultDisplayTile());
			this.setHasDisplayTile(false);
			this.dataManager.set(DISPLAY, ItemStack.EMPTY);
			this.setBehavior(Optional.empty());
			// natively synchronizes w/ the client, so no packets needed
		}

		return stack.copy();
	}

	public void dropDisplayStack() {
		if (!this.world.isRemote) {
			entityDropItem(removeDisplayStack(), 0.0F);
		}
	}

	@Override
	public void setDead() {
		super.setDead();
		this.getBehavior().ifPresent(behavior -> behavior.onDeath(this));
	}

	@Override
	public void killMinecart(DamageSource source) {
		this.setDead();

		boolean dropItems = this.world.getGameRules().getBoolean("doEntityDrops");

		if (dropItems) {
			ItemStack stack = this.getCartItem();

			if (this.hasCustomName()) {
				stack.setStackDisplayName(this.getCustomNameTag());
			}

			this.entityDropItem(stack, 0.0F);

			if (this.hasDisplayTile()) {
				this.dropDisplayStack();
			}
		}

		this.getBehavior().ifPresent(behavior -> behavior.killMinecart(this, source, dropItems));
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		this.getBehavior().ifPresent(behavior -> behavior.attackCartFrom(this, source, amount));
		return super.attackEntityFrom(source, amount);
	}

	@Override
	public void notifyDataManagerChange(DataParameter<?> key) {
		// super method is empty
		this.getBehavior().ifPresent(behavior -> behavior.tickDataManager(this, key));
	}

	@Override
	public void fall(float distance, float damageMultiplier) {
		this.getBehavior().ifPresent(behavior -> behavior.fall(this, distance, damageMultiplier));
		super.fall(distance, damageMultiplier);
	}

	@Override
	public void onActivatorRailPass(int x, int y, int z, boolean receivingPower) {
		if (this.hasDisplayTile()) {
			this.getBehavior().ifPresent(behavior -> behavior.onActivatorRailPass(this, x, y, z, receivingPower));
		} else {
			super.onActivatorRailPass(x, y, z, receivingPower);
		}
	}
}

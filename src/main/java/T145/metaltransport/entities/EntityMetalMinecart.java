package T145.metaltransport.entities;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Optional;

import T145.metaltransport.api.ItemsMT;
import T145.metaltransport.api.SerializersMT;
import T145.metaltransport.api.carts.CartActionRegistry;
import T145.metaltransport.api.carts.ICartAction;
import T145.metaltransport.api.carts.IMetalMinecart;
import T145.metaltransport.api.carts.IMinecartBlock;
import T145.metaltransport.api.constants.CartType;
import T145.metaltransport.api.constants.RegistryMT;
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

	private static final DataParameter<CartType> CART_TYPE = EntityDataManager.createKey(EntityMetalMinecart.class, SerializersMT.CART_TYPE);
	private static final DataParameter<ItemStack> DISPLAY = EntityDataManager.createKey(EntityMetalMinecart.class, DataSerializers.ITEM_STACK);
	private static final DataParameter<Optional<ICartAction>> ACTION = EntityDataManager.createKey(EntityMetalMinecart.class, SerializersMT.CART_ACTION);
	private static final Map<String, EntityMinecart.Type> MINECART_TYPES = new HashMap() {{
		//put("minecraft:air", EntityMinecart.Type.RIDEABLE);
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

	public Optional<ICartAction> getAction() {
		return this.dataManager.get(ACTION);
	}

	public void setAction(Optional<ICartAction> action) {
		this.dataManager.set(ACTION, action);
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

	public EntityMetalMinecart setDisplayState(IBlockState state) {
		Optional<ICartAction> action = this.getAction();
		String blockName = state.getBlock().getRegistryName().toString();

		RegistryMT.LOG.info(blockName);

		if (CartActionRegistry.contains(blockName)) {
			action = Optional.of(CartActionRegistry.get(blockName));
			RegistryMT.LOG.info("Setting cart action: " + action);
			this.setAction(action);
		}

		this.setDisplayTile(state);
		return this;
	}

	public EntityMetalMinecart setDisplayBlock(Block block) {
		if (block instanceof IMinecartBlock) {
			return this.setDisplayState(((IMinecartBlock) block).getDisplayState(this, this.getDisplayStack()));
		} else {
			// add any special vanilla block exceptions here
			return this.setDisplayState(block.getDefaultState());
		}
	}

	public EntityMetalMinecart setDisplayItem(Item item) {
		return this.setDisplayBlock(Block.getBlockFromItem(item));
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
		return this.setDisplayItem(stack.getItem());
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

	protected String getDisplayBlockName() {
		Optional<ICartAction> action = this.getAction();

		if (!action.isPresent() || action.get().hasNames()) {
			return this.getDisplayTile().getBlock().getRegistryName().toString();
		}

		// TODO: Implement "getActiveName" for the default return
		return action.get().getBlockNames()[0];
	}

	@Override
	public EntityMinecart.Type getType() {
		String blockName = this.getDisplayBlockName();

		if (MINECART_TYPES.containsKey(blockName)) {
			return MINECART_TYPES.get(blockName);
		}
		return super.getType();
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(CART_TYPE, CartType.IRON);
		this.dataManager.register(DISPLAY, ItemStack.EMPTY);
		this.dataManager.register(ACTION, Optional.absent());
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag) {
		super.writeEntityToNBT(tag);
		tag.setString(TAG_CART_TYPE, getCartType().toString());
		NBTTagCompound stackTag = new NBTTagCompound();
		this.getDisplayStack().writeToNBT(stackTag);
		tag.setTag(TAG_DISPLAY, stackTag);

		boolean present = this.getAction().isPresent();
		tag.setBoolean("HasAction", present);

		if (present) {
			tag.setTag(TAG_ACTION, this.getAction().get().serialize());
		}
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tag) {
		super.readEntityFromNBT(tag);
		setCartType(CartType.valueOf(tag.getString(TAG_CART_TYPE)));
		this.dataManager.set(DISPLAY, new ItemStack(tag.getCompoundTag(TAG_DISPLAY)));

		if (tag.getBoolean("HasAction")) {
			NBTTagCompound actionTag = tag.getCompoundTag(TAG_ACTION);
			NBTTagList names = actionTag.getTagList("BlockNames", Constants.NBT.TAG_STRING);
			String blockName = names.getStringTagAt(0);

			if (CartActionRegistry.contains(blockName)) {
				ICartAction action = CartActionRegistry.get(blockName);
				action.deserialize(actionTag);
				this.setAction(Optional.of(action));
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void handleStatusUpdate(byte id) {
		//super.handleStatusUpdate(id);

		Optional<ICartAction> action = this.getAction();

		if (action.isPresent()) {
			action.get().handleStatusUpdate(this, id);
		}
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		Optional<ICartAction> action = this.getAction();

		if (action.isPresent()) {
			action.get().tick(this);
		}
	}

	@Override
	protected double getMaximumSpeed() {
		Optional<ICartAction> action = this.getAction();

		if (action.isPresent()) {
			return action.get().getMaxCartSpeed();
		}

		return super.getMaximumSpeed();
	}

	@Override
	protected void moveAlongTrack(BlockPos pos, IBlockState state) {
		super.moveAlongTrack(pos, state);

		Optional<ICartAction> action = this.getAction();

		if (action.isPresent()) {
			action.get().moveAlongTrack(this, pos, state);
		}
	}

	@Override
	protected void applyDrag() {
		Optional<ICartAction> action = this.getAction();

		if (action.isPresent()) {
			action.get().applyDrag(this);
		}

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
				return true;
			}
			return false;
		}

		if (this.hasDisplayTile()) {
			Optional<ICartAction> action = this.getAction();

			if (action.isPresent()) {
				RegistryMT.LOG.info("Performing action!");
				return action.get().activate(this, player, hand);
			}
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
			this.setAction(Optional.absent());
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
	public void killMinecart(DamageSource source) {
		this.setDead();

		if (this.world.getGameRules().getBoolean("doEntityDrops")) {
			ItemStack itemstack = this.getCartItem();

			if (this.hasCustomName()) {
				itemstack.setStackDisplayName(this.getCustomNameTag());
			}

			this.entityDropItem(itemstack, 0.0F);

			if (this.hasDisplayTile()) {
				this.dropDisplayStack();
			}
		}
	}
}

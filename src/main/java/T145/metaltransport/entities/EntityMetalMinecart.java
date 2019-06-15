package T145.metaltransport.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import T145.metaltransport.api.ItemsMT;
import T145.metaltransport.api.SerializersMT;
import T145.metaltransport.api.carts.CartBehaviorRegistry;
import T145.metaltransport.api.carts.ICartBehavior;
import T145.metaltransport.api.carts.ICartBehaviorFactory;
import T145.metaltransport.api.carts.IMetalMinecart;
import T145.metaltransport.api.carts.IMetalMinecartBlock;
import T145.metaltransport.api.constants.CartType;
import T145.metaltransport.core.MetalTransport;
import T145.metaltransport.network.client.SyncBehaviorWithClient;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartEmpty;
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
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;

public class EntityMetalMinecart extends EntityMinecartEmpty implements IMetalMinecart {

	private static final DataParameter<ItemStack> DISPLAY = EntityDataManager.createKey(EntityMetalMinecart.class, DataSerializers.ITEM_STACK);
	private static final DataParameter<CartType> CART_TYPE = EntityDataManager.createKey(EntityMetalMinecart.class, SerializersMT.CART_TYPE);
	private Optional<ICartBehavior> behavior = Optional.empty();

	private static final Map<ResourceLocation, EntityMinecart.Type> MINECART_TYPES = new HashMap() {{
		put(Blocks.CHEST.getRegistryName(), EntityMinecart.Type.CHEST);
		put(Blocks.TRAPPED_CHEST.getRegistryName(), EntityMinecart.Type.CHEST);
		put(Blocks.ENDER_CHEST.getRegistryName(), EntityMinecart.Type.CHEST);
		put(Blocks.FURNACE.getRegistryName(), EntityMinecart.Type.FURNACE);
		put(Blocks.LIT_FURNACE.getRegistryName(), EntityMinecart.Type.FURNACE);
		put(Blocks.TNT.getRegistryName(), EntityMinecart.Type.TNT);
		put(Blocks.MOB_SPAWNER.getRegistryName(), EntityMinecart.Type.SPAWNER);
		put(Blocks.HOPPER.getRegistryName(), EntityMinecart.Type.HOPPER);
		put(Blocks.CHAIN_COMMAND_BLOCK.getRegistryName(), EntityMinecart.Type.COMMAND_BLOCK);
		put(Blocks.COMMAND_BLOCK.getRegistryName(), EntityMinecart.Type.COMMAND_BLOCK);
		put(Blocks.REPEATING_COMMAND_BLOCK.getRegistryName(), EntityMinecart.Type.COMMAND_BLOCK);
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
			IBlockState state = cart.getDisplayTile();
			Block block = state.getBlock();
			ItemStack stack = new ItemStack(block);
			this.setDisplayStack(stack);
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
	public CartType getCartType() {
		return dataManager.get(CART_TYPE);
	}

	@Override
	public EntityMinecart setCartType(CartType type) {
		dataManager.set(CART_TYPE, type);
		return this;
	}

	@Override
	public void setDisplayTile(IBlockState state) {}

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
		this.setHasDisplayTile(!copyStack.isEmpty());

		return this;
	}

	@Override
	public Optional<ICartBehavior> getBehavior() {
		return behavior;
	}

	@Override
	public EntityMetalMinecart setBehavior() {
		ItemStack stack = this.getDisplayStack();

		if (stack.isEmpty()) {
			this.behavior = Optional.empty();
		} else {
			Item item = stack.getItem();
			Block block = Block.getBlockFromItem(item);

			if (block instanceof IMetalMinecartBlock) {
				stack = ((IMetalMinecartBlock) block).getDisplayStack(this, stack.getItemDamage());
				stack.setCount(1); // just in case
				this.setDisplayStack(stack);
			}

			ResourceLocation key = block.getRegistryName();
			Optional<ICartBehaviorFactory> behaviorFactory = Optional.ofNullable(CartBehaviorRegistry.get(key));

			if (behaviorFactory.isPresent()) {
				ICartBehavior cartBehavior = behaviorFactory.get().createBehavior(this);
				this.behavior = Optional.of(cartBehavior);
			} else {
				this.behavior = Optional.empty();
			}
		}

		// sync the cart behavior with the client
		BlockPos pos = this.getPosition();
		MetalTransport.NETWORK.sendToAllAround(new SyncBehaviorWithClient(pos), world, pos);

		return this;
	}

	public ResourceLocation getKey() {
		ItemStack stack = this.getDisplayStack();
		Block block = MetalTransport.getBlockFromStack(stack);
		return block.getRegistryName();
	}

	@Override
	public EntityMinecart.Type getType() {
		if (this.hasDisplayTile()) {
			ResourceLocation key = this.getKey();

			if (MINECART_TYPES.containsKey(key)) {
				return MINECART_TYPES.get(key);
			}
		}
		return super.getType();
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(CART_TYPE, CartType.IRON);
		this.dataManager.register(DISPLAY, ItemStack.EMPTY);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag) {
		super.writeEntityToNBT(tag);
		tag.setString(TAG_CART_TYPE, getCartType().toString());
		NBTTagCompound stackTag = new NBTTagCompound();
		this.getDisplayStack().writeToNBT(stackTag);
		tag.setTag(TAG_DISPLAY, stackTag);

		this.behavior.ifPresent(b -> {
			tag.setByte("HasBehavior", (byte) 1);
			tag.setTag(TAG_BEHAVIOR, b.serialize());
		});
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tag) {
		super.readEntityFromNBT(tag);
		setCartType(CartType.valueOf(tag.getString(TAG_CART_TYPE)));
		this.dataManager.set(DISPLAY, new ItemStack(tag.getCompoundTag(TAG_DISPLAY)));

		if (tag.hasKey("HasBehavior")) {
			Optional.ofNullable(CartBehaviorRegistry.get(this.getKey())).ifPresent(factory -> {
				NBTTagCompound behaviorTag = tag.getCompoundTag(TAG_BEHAVIOR);
				this.behavior = Optional.of(factory.createBehavior(this).deserialize(behaviorTag));
			});
		}
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		/*
		 * == Behavior Detailing
		 * 
		 * Calling this from the client will always crash the game
		 * if getCart() is called, since getPersistentId() is
		 * different for each thread. Behaviors can only do things
		 * from the server anyway, so any client interaction will
		 * have to be done through packets. This removes passing
		 * a live minecart instance as a valid work-around.
		 * Luckily any client work is usually just spawning particles.
		 */

		if (!world.isRemote) {
			this.behavior.ifPresent(b -> b.tickServer(world, this.getPosition()));
		}
	}

	@Override
	protected double getMaximumSpeed() {
		this.behavior.ifPresent(b -> b.getMaxCartSpeed());
		return super.getMaximumSpeed();
	}

	@Override
	protected void moveAlongTrack(BlockPos pos, IBlockState rail) {
		super.moveAlongTrack(pos, rail);
		this.behavior.ifPresent(b -> b.moveAlongTrack(pos, rail));
	}

	@Override
	protected void applyDrag() {
		this.behavior.ifPresent(b -> b.applyDrag());
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
			this.behavior.ifPresent(b -> b.activate(player, hand));
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

		if (this.isEntityAlive()) {
			behavior.ifPresent(b -> b.onDeletion());
			this.setDisplayStack(ItemStack.EMPTY);
			this.setBehavior();
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

		this.behavior.ifPresent(b -> {
			if (!world.isRemote) {
				b.onDeletion();
			}
			b.onDeath();
		});
	}

	@Override
	public void killMinecart(DamageSource source) {
		boolean dropItems = this.world.getGameRules().getBoolean("doEntityDrops");

		this.behavior.ifPresent(b -> b.killMinecart(source, dropItems));
		this.setDead();

		if (dropItems) {
			ItemStack stack = this.getCartItem();

			if (this.hasCustomName()) {
				stack.setStackDisplayName(this.getCustomNameTag());
			}

			this.entityDropItem(stack, 0.0F);

			if ((!source.isExplosion() || this.getCartType() == CartType.OBSIDIAN) && this.hasDisplayTile()) {
				this.dropDisplayStack();
			}
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		this.behavior.ifPresent(b -> b.attackCartFrom(source, amount));
		return super.attackEntityFrom(source, amount);
	}

	@Override
	public void fall(float distance, float damageMultiplier) {
		this.behavior.ifPresent(b -> b.fall(distance, damageMultiplier));
		super.fall(distance, damageMultiplier);
	}

	@Override
	public void onActivatorRailPass(int x, int y, int z, boolean receivingPower) {
		if (this.hasDisplayTile()) {
			this.behavior.ifPresent(b -> b.onActivatorRailPass(x, y, z, receivingPower));
		} else {
			super.onActivatorRailPass(x, y, z, receivingPower);
		}
	}
}

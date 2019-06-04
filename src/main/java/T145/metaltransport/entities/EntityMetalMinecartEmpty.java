package T145.metaltransport.entities;

import T145.metaltransport.api.carts.IMetalMinecart;
import T145.metaltransport.api.constants.CartType;
import T145.metaltransport.core.MetalTransport;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

public class EntityMetalMinecartEmpty extends EntityMinecartEmpty implements IMetalMinecart {

	private static final DataParameter<CartType> CART_TYPE = EntityDataManager.createKey(EntityMetalMinecartEmpty.class, MetalTransport.CART_TYPE);

	public EntityMetalMinecartEmpty(World world, CartType type) {
		super(world);
		this.setCartType(type);
	}

	public EntityMetalMinecartEmpty(World world) {
		super(world);
	}

	public EntityMetalMinecartEmpty(World world, double x, double y, double z, CartType type) {
		super(world, x, y, z);
		this.setCartType(type);
	}

	public EntityMetalMinecartEmpty(World world, double x, double y, double z) {
		super(world, x, y, z);
	}

	private void setCartData(EntityMinecart cart) {
		this.posX = cart.posX;
		this.posY = cart.posY;
		this.posZ = cart.posZ;
		this.motionX = cart.motionX;
		this.motionY = cart.motionY;
		this.motionZ = cart.motionZ;
		this.rotationPitch = cart.rotationPitch;
		this.rotationYaw = cart.rotationYaw;
	}

	public EntityMetalMinecartEmpty(EntityMinecart cart, CartType type) {
		this(cart.getEntityWorld(), cart.prevPosX, cart.prevPosY, cart.prevPosZ, type);
		setCartData(cart);
	}

	public EntityMetalMinecartEmpty(EntityMinecart cart) {
		this(cart.getEntityWorld(), cart.prevPosX, cart.prevPosY, cart.prevPosZ);
		setCartData(cart);
	}

	@Override
	public CartType getCartType() {
		return this.dataManager.get(CART_TYPE);
	}

	@Override
	public void setCartType(CartType type) {
		this.dataManager.set(CART_TYPE, type);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(CART_TYPE, CartType.IRON);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag) {
		super.writeEntityToNBT(tag);
		tag.setString(TAG_CART_TYPE, getCartType().toString());
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tag) {
		super.readEntityFromNBT(tag);
		this.setCartType(CartType.valueOf(tag.getString(TAG_CART_TYPE)));
	}

	//	@Override
	//	public ItemStack getCartItem() {
	//		return new ItemStack(ItemsMC.MINECART_METAL_CHEST, 1, getChestType().ordinal());
	//	}
}

package T145.metaltransport.api.obj.caps;

import java.util.HashMap;
import java.util.Map;

import T145.metaltransport.api.consts.CartType;
import T145.metaltransport.api.obj.SerializersMT;
import T145.metaltransport.entities.EntityFurnaceCart;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.entity.item.EntityMinecartCommandBlock;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.entity.item.EntityMinecartFurnace;
import net.minecraft.entity.item.EntityMinecartHopper;
import net.minecraft.entity.item.EntityMinecartMobSpawner;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraftforge.common.util.INBTSerializable;

public class SerialCartType implements INBTSerializable<NBTTagCompound> {

	public static DataParameter<CartType> createKey(Class<? extends EntityMinecart> cartClass) {
		return EntityDataManager.createKey(cartClass, SerializersMT.CART_TYPE);
	}

	public static final Map<Class<? extends EntityMinecart>, DataParameter<CartType>> PARAMS = new HashMap() {{
		put(EntityMinecartChest.class, createKey(EntityMinecartChest.class));
		put(EntityMinecartCommandBlock.class, createKey(EntityMinecartCommandBlock.class));
		put(EntityMinecartEmpty.class, createKey(EntityMinecartEmpty.class));
		put(EntityMinecartFurnace.class, createKey(EntityMinecartFurnace.class));
		put(EntityMinecartHopper.class, createKey(EntityMinecartHopper.class));
		put(EntityMinecartMobSpawner.class, createKey(EntityMinecartMobSpawner.class));
		put(EntityMinecartTNT.class, createKey(EntityMinecartTNT.class));
		put(EntityFurnaceCart.class, createKey(EntityFurnaceCart.class));
	}};

	private final EntityMinecart cart;

	public SerialCartType(EntityMinecart cart) {
		this.cart = cart;
	}

	// not in this constructor as it needs to be loaded after other data parameters
	public static void registerTypes(EntityMinecart cart) {
		if (PARAMS.containsKey(cart.getClass())) {
			cart.getDataManager().register(PARAMS.get(cart.getClass()), CartType.IRON);
		}
	}

	public CartType getType() {
		if (PARAMS.containsKey(cart.getClass())) {
			return cart.getDataManager().get(PARAMS.get(cart.getClass()));
		} else {
			return CartType.IRON;
		}
	}

	public SerialCartType setType(CartType type) {
		if (PARAMS.containsKey(cart.getClass())) {
			cart.getDataManager().set(PARAMS.get(cart.getClass()), type);
		}
		return this;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound tag = new NBTTagCompound();

		if (PARAMS.containsKey(cart.getClass())) {
			tag.setString("CartType", getType().toString());
		}

		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound tag) {
		setType(CartType.valueOf(tag.getString("CartType")));
	}
}

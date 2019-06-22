package T145.metaltransport.api.obj.caps;

import T145.metaltransport.api.consts.CartType;
import T145.metaltransport.api.obj.SerializersMT;
import T145.metaltransport.entities.EntityFurnaceCart;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartCommandBlock;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraftforge.common.util.INBTSerializable;

public class SerialCartType implements INBTSerializable<NBTTagCompound> {

	private static final DataParameter<CartType> CART_TYPE = EntityDataManager.createKey(EntityMinecart.class, SerializersMT.CART_TYPE);
	private static final DataParameter<CartType> COMMAND_BLOCK_CART_TYPE = EntityDataManager.createKey(EntityMinecartCommandBlock.class, SerializersMT.CART_TYPE);
	private EntityMinecart cart;

	public SerialCartType(EntityMinecart cart) {
		this.cart = cart;
	}

	private static DataParameter<CartType> fetchDataType(EntityMinecart cart) {
		if (cart instanceof EntityMinecartCommandBlock) {
			return COMMAND_BLOCK_CART_TYPE;
		}
		return CART_TYPE;
	}

	// not in this constructor as it needs to be loaded after other data parameters
	public static void registerTypes(EntityMinecart cart) {
		if (!(cart instanceof EntityFurnaceCart))
			cart.getDataManager().register(fetchDataType(cart), CartType.IRON);
	}

	public CartType getType() {
		return cart.getDataManager().get(fetchDataType(cart));
	}

	public SerialCartType setType(CartType type) {
		cart.getDataManager().set(fetchDataType(cart), type);
		return this;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("CartType", getType().toString());
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound tag) {
		setType(CartType.valueOf(tag.getString("CartType")));
	}
}

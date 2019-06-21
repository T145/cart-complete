package T145.metaltransport.api.obj.caps;

import T145.metaltransport.api.consts.CartType;
import T145.metaltransport.api.obj.SerializersMT;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraftforge.common.util.INBTSerializable;

public class SerialCartType implements INBTSerializable<NBTTagCompound> {

	public static final DataParameter<CartType> CART_TYPE = EntityDataManager.createKey(EntityMinecart.class, SerializersMT.CART_TYPE);
	private EntityMinecart cart;

	public SerialCartType(EntityMinecart cart) {
		this.cart = cart;
	}

	public CartType getType() {
		return cart.getDataManager().get(CART_TYPE);
	}

	public SerialCartType setType(CartType type) {
		cart.getDataManager().set(CART_TYPE, type);
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

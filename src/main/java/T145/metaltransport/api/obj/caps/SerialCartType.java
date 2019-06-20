package T145.metaltransport.api.obj.caps;

import T145.metaltransport.api.consts.CartType;
import T145.metaltransport.api.obj.DataParamsMT;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public class SerialCartType implements INBTSerializable<NBTTagCompound> {

	private EntityMinecart cart;

	public SerialCartType(EntityMinecart cart) {
		this.cart = cart;
	}

	public CartType getType() {
		return cart.getDataManager().get(DataParamsMT.CART_TYPE);
	}

	public SerialCartType setType(CartType type) {
		cart.getDataManager().set(DataParamsMT.CART_TYPE, type);
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

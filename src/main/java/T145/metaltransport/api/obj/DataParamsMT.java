package T145.metaltransport.api.obj;

import T145.metaltransport.api.consts.CartType;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;

public class DataParamsMT {

	public static final DataParameter<CartType> CART_TYPE = EntityDataManager.createKey(EntityMinecart.class, SerializersMT.CART_TYPE);

	private DataParamsMT() {}
}

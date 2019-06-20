package T145.metaltransport.api.obj;

import T145.metaltransport.api.obj.caps.SerialCartType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class CapabilitiesMT {

	private CapabilitiesMT() {}

	@CapabilityInject(SerialCartType.class)
	public static Capability<SerialCartType> CART_TYPE;
}

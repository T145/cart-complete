package T145.metaltransport.net;

import T145.metaltransport.api.consts.RegistryMT;
import T145.metaltransport.net.client.SyncCartType;
import T145.tbone.network.TPacketHandler;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandlerMT extends TPacketHandler {

	public PacketHandlerMT() {
		super(RegistryMT.ID);
	}

	@Override
	public void registerMessages() {
		this.registerMessage(SyncCartType.class, Side.CLIENT);
	}
}

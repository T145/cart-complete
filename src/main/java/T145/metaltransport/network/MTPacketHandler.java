package T145.metaltransport.network;

import T145.metaltransport.api.constants.RegistryMT;
import T145.metaltransport.network.client.SpawnSmokeParticles;
import T145.metaltransport.network.client.SyncBehaviorWithClient;
import T145.metaltransport.network.client.SyncMobSpawnerWithClient;
import T145.tbone.network.TPacketHandler;
import net.minecraftforge.fml.relauncher.Side;

public class MTPacketHandler extends TPacketHandler {

	public MTPacketHandler() {
		super(RegistryMT.ID);
	}

	@Override
	public void registerMessages() {
		this.registerMessage(SyncBehaviorWithClient.class, Side.CLIENT);
		this.registerMessage(SyncMobSpawnerWithClient.class, Side.CLIENT);
		this.registerMessage(SpawnSmokeParticles.class, Side.CLIENT);
	}
}

package T145.metaltransport.network.client;

import T145.metaltransport.entities.EntityMetalMinecart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SyncBehaviorWithClient extends SyncMetalMinecartWithClient {

	public SyncBehaviorWithClient(BlockPos pos) {
		super(pos);
	}

	@Override
	public void processCart(World client, EntityMetalMinecart cart) {
		cart.setBehavior();
	}
}

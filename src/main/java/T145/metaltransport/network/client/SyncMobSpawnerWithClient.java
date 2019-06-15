package T145.metaltransport.network.client;

import T145.metaltransport.entities.EntityMetalMinecart;
import T145.metaltransport.entities.behaviors.MobSpawnerBehavior;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SyncMobSpawnerWithClient extends SyncMetalMinecartWithClient {

	public SyncMobSpawnerWithClient(BlockPos pos) {
		super(pos);
	}

	@Override
	public void processCart(World client, EntityMetalMinecart cart) {
		cart.getBehavior().ifPresent(behavior -> {
			((MobSpawnerBehavior) behavior).logic.updateSpawner();
		});
	}
}

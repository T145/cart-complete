package T145.metaltransport.network.client;

import T145.metaltransport.entities.EntityMetalMinecart;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpawnSmokeParticles extends SyncMetalMinecartWithClient {

	public SpawnSmokeParticles(BlockPos pos) {
		super(pos);
	}

	@Override
	public void processCart(World client, EntityMetalMinecart cart) {
		client.spawnParticle(EnumParticleTypes.SMOKE_LARGE, cart.posX, cart.posY + 0.5D, cart.posZ, 0, 0, 0);
	}
}

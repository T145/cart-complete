package T145.metaltransport.network.client;

import java.io.IOException;
import java.util.List;

import T145.metaltransport.entities.EntityMetalMinecart;
import T145.tbone.network.TMessage;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SpawnSmokeParticles extends TMessage {

	private BlockPos pos;

	public SpawnSmokeParticles() {
		// DEFAULT CONSTRUCTOR REQUIRED
	}

	public SpawnSmokeParticles(BlockPos pos) {
		this.pos = pos;
	}

	@Override
	public void serialize(PacketBuffer buf) {
		buf.writeBlockPos(pos);
	}

	@Override
	public void deserialize(PacketBuffer buf) throws IOException {
		this.pos = buf.readBlockPos();
	}

	@Override
	public void process(MessageContext buf) {
		World world = this.getClientWorld();

		if (world != null) {
			List<EntityMetalMinecart> carts = world.getEntitiesWithinAABB(EntityMetalMinecart.class, new AxisAlignedBB(pos));

			if (!carts.isEmpty()) {
				EntityMetalMinecart cart = carts.get(0);
				world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, cart.posX, cart.posY + 0.5D, cart.posZ, 0, 0, 0);
			}
		}
	}
}

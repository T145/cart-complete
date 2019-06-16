package T145.metaltransport.net.client;

import java.io.IOException;

import T145.metaltransport.entities.EntityMetalMinecart;
import T145.tbone.api.network.IWorldPositionedMessage;
import T145.tbone.network.TMessage;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SpawnSmokeParticles extends TMessage implements IWorldPositionedMessage {

	protected BlockPos pos;

	public SpawnSmokeParticles() {}

	public SpawnSmokeParticles(BlockPos pos) {
		this.pos = pos;
	}

	@Override
	public BlockPos getPos() {
		return pos;
	}

	@Override
	public World getWorld() {
		return this.getClientWorld();
	}

	@Override
	public void serialize(PacketBuffer buf) {
		buf.writeBlockPos(pos);
	}

	@Override
	public void deserialize(PacketBuffer buf) throws IOException {
		this.pos = buf.readBlockPos();
	}

	public void processCart(World client, EntityMetalMinecart cart) {}

	@Override
	public void process(MessageContext buf) {
		World client = this.getWorld();

		if (client != null) {
			client.spawnParticle(EnumParticleTypes.SMOKE_LARGE, pos.getX(), pos.getY() + 0.5D, pos.getZ(), 0, 0, 0);
		}
	}
}

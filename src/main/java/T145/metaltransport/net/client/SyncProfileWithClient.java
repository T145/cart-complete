package T145.metaltransport.net.client;

import java.io.IOException;
import java.util.List;

import T145.metaltransport.entities.EntityMetalMinecart;
import T145.tbone.api.network.IWorldPositionedMessage;
import T145.tbone.network.TMessage;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SyncProfileWithClient extends TMessage implements IWorldPositionedMessage {

	private BlockPos pos;

	public SyncProfileWithClient() {
		// DEFAULT CONSTRUCTOR REQUIRED
	}

	public SyncProfileWithClient(BlockPos pos) {
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

	@Override
	public void process(MessageContext buf) {
		World client = this.getWorld();

		if (client != null) {
			List<EntityMetalMinecart> carts = client.getEntitiesWithinAABB(EntityMetalMinecart.class, new AxisAlignedBB(pos));

			if (!carts.isEmpty()) {
				carts.get(0).setCartProfile();
			}
		}
	}
}

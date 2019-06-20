package T145.metaltransport.net.client;

import java.io.IOException;
import java.util.List;

import T145.metaltransport.MetalTransport;
import T145.metaltransport.api.consts.CartType;
import T145.tbone.api.network.IWorldPositionedMessage;
import T145.tbone.network.TMessage;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SyncCartType extends TMessage implements IWorldPositionedMessage {

	private BlockPos pos;
	private CartType type;

	public SyncCartType() {
		// DEFAULT CONSTRUCTOR REQUIRED
	}

	public SyncCartType(BlockPos pos, CartType type) {
		this.pos = pos;
		this.type = type;
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
		buf.writeEnumValue(type);
	}

	@Override
	public void deserialize(PacketBuffer buf) throws IOException {
		this.pos = buf.readBlockPos();
		this.type = buf.readEnumValue(CartType.class);
	}

	@Override
	public void process(MessageContext buf) {
		World client = this.getWorld();

		if (client != null) {
			List<EntityMinecart> carts = client.getEntitiesWithinAABB(EntityMinecart.class, new AxisAlignedBB(pos));

			if (!carts.isEmpty()) {
				carts.get(0).getCapability(MetalTransport.CAP_CART_TYPE, null).setType(type);
			}
		}
	}
}

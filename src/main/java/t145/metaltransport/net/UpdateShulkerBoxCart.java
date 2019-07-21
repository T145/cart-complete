package t145.metaltransport.net;

import java.io.IOException;
import java.util.List;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import t145.metaltransport.entities.EntityMetalCart;
import t145.metaltransport.entities.profiles.ShulkerBoxProfile;
import t145.tbone.net.TMessage;

public class UpdateShulkerBoxCart extends TMessage {

	private BlockPos pos;
	private int openCount;

	public UpdateShulkerBoxCart() {}

	public UpdateShulkerBoxCart(BlockPos pos, int openCount) {
		this.pos = pos;
		this.openCount = openCount;
	}

	@Override
	public void serialize(PacketBuffer buf) {
		buf.writeBlockPos(pos);
		buf.writeInt(openCount);
	}

	@Override
	public void deserialize(PacketBuffer buf) throws IOException {
		this.pos = buf.readBlockPos();
		this.openCount = buf.readInt();
	}

	@Override
	public void process(MessageContext ctx) {
		World world = this.getClientWorld();
		List<EntityMetalCart> carts = world.getEntitiesWithinAABB(EntityMetalCart.class, new AxisAlignedBB(pos));

		if (!carts.isEmpty()) {
			((ShulkerBoxProfile) carts.get(0).getProfile().get()).box.receiveClientEvent(1, this.openCount);
		}
	}
}

package t145.metaltransport.net;

import java.io.IOException;
import java.util.List;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import t145.metaltransport.entities.EntityMetalCart;
import t145.metaltransport.entities.profiles.MetalChestProfile;
import t145.tbone.net.TMessage;

public class UpdateMetalChestCart extends TMessage {

	private BlockPos pos;
	private int event;
	private int data;

	public UpdateMetalChestCart() {}

	public UpdateMetalChestCart(BlockPos pos, int event, int data) {
		this.pos = pos;
		this.event = event;
		this.data = data;
	}

	@Override
	public void serialize(PacketBuffer buf) {
		buf.writeBlockPos(pos);
		buf.writeInt(event);
		buf.writeInt(data);
	}

	@Override
	public void deserialize(PacketBuffer buf) throws IOException {
		this.pos = buf.readBlockPos();
		this.event = buf.readInt();
		this.data = buf.readInt();
	}

	@Override
	public void process(MessageContext ctx) {
		World world = this.getClientWorld();
		List<EntityMetalCart> carts = world.getEntitiesWithinAABB(EntityMetalCart.class, new AxisAlignedBB(pos));

		if (!carts.isEmpty()) {
			((MetalChestProfile) carts.get(0).getProfile().get()).chest.receiveClientEvent(this.event, this.data);
		}
	}
}

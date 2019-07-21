package t145.metaltransport.net;

import java.io.IOException;
import java.util.List;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import t145.metaltransport.entities.EntityMetalCart;
import t145.metaltransport.entities.profiles.EnderChestProfile;
import t145.tbone.net.TMessage;

public class UpdateEnderChestCart extends TMessage {

	private BlockPos pos;
	private int playerCount;

	public UpdateEnderChestCart() {}

	public UpdateEnderChestCart(BlockPos pos, int playerCount) {
		this.pos = pos;
		this.playerCount = playerCount;
	}

	@Override
	public void serialize(PacketBuffer buf) {
		buf.writeBlockPos(pos);
		buf.writeInt(playerCount);
	}

	@Override
	public void deserialize(PacketBuffer buf) throws IOException {
		this.pos = buf.readBlockPos();
		this.playerCount = buf.readInt();
	}

	@Override
	public void process(MessageContext ctx) {
		World world = this.getClientWorld();
		List<EntityMetalCart> carts = world.getEntitiesWithinAABB(EntityMetalCart.class, new AxisAlignedBB(pos));

		if (!carts.isEmpty()) {
			((EnderChestProfile) carts.get(0).getProfile().get()).chest.numPlayersUsing = this.playerCount;
		}
	}
}

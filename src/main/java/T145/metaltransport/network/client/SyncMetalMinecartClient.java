package T145.metaltransport.network.client;

import java.io.IOException;
import java.util.List;

import T145.metaltransport.entities.EntityMetalMinecart;
import T145.tbone.network.TMessage;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SyncMetalMinecartClient extends TMessage {

	private ItemStack displayStack;
	private BlockPos pos;

	public SyncMetalMinecartClient() {
		// DEFAULT CONSTRUCTOR REQUIRED
	}

	public SyncMetalMinecartClient(ItemStack displayStack, BlockPos pos) {
		this.displayStack = displayStack;
		this.pos = pos;
	}

	@Override
	public void serialize(PacketBuffer buf) {
		buf.writeItemStack(displayStack);
		buf.writeBlockPos(pos);
	}

	@Override
	public void deserialize(PacketBuffer buf) throws IOException {
		this.displayStack = buf.readItemStack();
		this.pos = buf.readBlockPos();
	}

	@Override
	public void process(MessageContext buf) {
		World world = this.getClientWorld();

		if (world != null) {
			List<EntityMetalMinecart> carts = world.getEntitiesWithinAABB(EntityMetalMinecart.class, new AxisAlignedBB(pos));

			if (!carts.isEmpty()) {
				carts.get(0).setDisplayStack(displayStack);
			}
		}
	}
}

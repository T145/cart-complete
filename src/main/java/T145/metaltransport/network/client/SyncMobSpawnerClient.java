package T145.metaltransport.network.client;

import java.io.IOException;
import java.util.List;

import T145.metaltransport.entities.EntityMetalMinecart;
import T145.metaltransport.entities.behaviors.MobSpawnerBehavior;
import T145.tbone.network.TMessage;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SyncMobSpawnerClient extends TMessage {

	private BlockPos pos;
	private int spawnDelay;
	private double mobRotation;
	private double prevMobRotation;

	public SyncMobSpawnerClient() {
		// DEFAULT CONSTRUCTOR REQUIRED
	}

	public SyncMobSpawnerClient(BlockPos pos, int spawnDelay, double mobRotation, double prevMobRotation) {
		this.pos = pos;
		this.spawnDelay = spawnDelay;
		this.mobRotation = mobRotation;
		this.prevMobRotation = prevMobRotation;
	}

	@Override
	public void serialize(PacketBuffer buf) {
		buf.writeBlockPos(pos);
		buf.writeInt(spawnDelay);
		buf.writeDouble(mobRotation);
		buf.writeDouble(prevMobRotation);
	}

	@Override
	public void deserialize(PacketBuffer buf) throws IOException {
		this.pos = buf.readBlockPos();
		this.spawnDelay = buf.readInt();
		this.mobRotation = buf.readDouble();
		this.prevMobRotation = buf.readDouble();
	}

	@Override
	public void process(MessageContext buf) {
		World world = this.getClientWorld();

		if (world != null) {
			List<EntityMetalMinecart> carts = world.getEntitiesWithinAABB(EntityMetalMinecart.class, new AxisAlignedBB(pos));

			if (!carts.isEmpty()) {
				carts.get(0).getBehavior().ifPresent(behavior -> {
					if (behavior instanceof MobSpawnerBehavior) {
						MobSpawnerBehavior spawner = (MobSpawnerBehavior) behavior;
						double d3 = pos.getX() + world.rand.nextFloat();
						double d4 = pos.getY() + world.rand.nextFloat();
						double d5 = pos.getZ() + world.rand.nextFloat();
						world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d3, d4, d5, 0, 0, 0);
						world.spawnParticle(EnumParticleTypes.FLAME, d3, d4, d5, 0, 0, 0);

						if (this.spawnDelay > 0) {
							--this.spawnDelay;
						}

						this.prevMobRotation = this.mobRotation;
						this.mobRotation = (this.mobRotation + (1000.0F / (this.spawnDelay + 200.0F))) % 360.0D;

						spawner.logic.spawnDelay = this.spawnDelay;
						spawner.logic.mobRotation = this.mobRotation;
						spawner.logic.prevMobRotation = this.prevMobRotation;
					}
				});
			}
		}
	}
}

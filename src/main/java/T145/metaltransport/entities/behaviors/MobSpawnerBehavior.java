package T145.metaltransport.entities.behaviors;

import T145.metaltransport.api.carts.CartBehavior;
import T145.metaltransport.api.carts.ICartBehavior;
import T145.metaltransport.api.carts.ICartBehaviorFactory;
import T145.metaltransport.core.MetalTransport;
import T145.metaltransport.network.client.SyncMobSpawnerClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MobSpawnerBehavior extends CartBehavior {

	public static class MobSpawnerBehaviorFactory implements ICartBehaviorFactory {

		@Override
		public ICartBehavior createBehavior(EntityMinecart cart) {
			return new MobSpawnerBehavior(cart);
		}
	}

	public final MobSpawnerBaseLogic logic = new MobSpawnerBaseLogic() {

		@Override
		public void broadcastEvent(int id) {
			this.getSpawnerWorld().setEntityState(this.getSpawnerEntity(), (byte) id);
		}

		@Override
		public World getSpawnerWorld() {
			return this.getSpawnerEntity().world;
		}

		@Override
		public BlockPos getSpawnerPosition() {
			return this.getSpawnerEntity().getPosition();
		}

		@Override
		public Entity getSpawnerEntity() {
			return MobSpawnerBehavior.this.getCart();
		}
	};

	public MobSpawnerBehavior(EntityMinecart cart) {
		super(cart);
	}

	@Override
	public NBTTagCompound serialize() {
		NBTTagCompound tag = super.serialize();
		this.logic.writeToNBT(tag);
		return tag;
	}

	@Override
	public ICartBehavior deserialize(NBTTagCompound tag) {
		super.deserialize(tag);
		this.logic.readFromNBT(tag);
		return this;
	}

	@Override
	public void tickServer(World world, BlockPos pos) {
		this.logic.updateSpawner();
		MetalTransport.NETWORK.sendToAllAround(new SyncMobSpawnerClient(pos), world, pos);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void handleStatusUpdate(byte id) {
		this.logic.setDelayToMin(id);
	}
}

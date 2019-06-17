package T145.metaltransport.entities.profiles;

import T145.metaltransport.MetalTransport;
import T145.metaltransport.api.carts.CartProfile;
import T145.metaltransport.api.profiles.ICartProfileFactory;
import T145.metaltransport.net.client.SyncMobSpawnerWithClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MobSpawnerProfile extends CartProfile {

	public static class MobSpawnerProfileFactory implements ICartProfileFactory {

		@Override
		public MobSpawnerProfile createProfile(EntityMinecart cart) {
			return new MobSpawnerProfile(cart);
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
			return MobSpawnerProfile.this.getCart();
		}
	};

	public MobSpawnerProfile(EntityMinecart cart) {
		super(cart);
	}

	@Override
	public NBTTagCompound serialize() {
		NBTTagCompound tag = super.serialize();
		this.logic.writeToNBT(tag);
		return tag;
	}

	@Override
	public MobSpawnerProfile deserialize(NBTTagCompound tag) {
		super.deserialize(tag);
		this.logic.readFromNBT(tag);
		return this;
	}

	@Override
	public void tickServer(World world, BlockPos pos) {
		this.logic.updateSpawner();
		MetalTransport.NETWORK.sendToAllAround(new SyncMobSpawnerWithClient(pos));
	}
}
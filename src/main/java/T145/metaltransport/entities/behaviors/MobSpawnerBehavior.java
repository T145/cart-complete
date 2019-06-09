package T145.metaltransport.entities.behaviors;

import com.google.common.base.Optional;

import T145.metaltransport.api.carts.CartBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MobSpawnerBehavior extends CartBehavior {

	/** 
	 * Dynamic programming technique so we don't need to create the logic over & over.
	 * May end up using AccessTransformers to grab EntityMinecartMobSpawner#mobSpawnerLogic.
	 */
	private Optional<MobSpawnerBaseLogic> mobSpawnerLogic = Optional.absent();

	public MobSpawnerBaseLogic getSpawnerLogic(EntityMinecart cart) {
		if (!this.mobSpawnerLogic.isPresent()) {
			this.mobSpawnerLogic = Optional.of(new MobSpawnerBaseLogic() {

				@Override
				public void broadcastEvent(int id) {
					cart.world.setEntityState(cart, (byte) id);
				}

				@Override
				public World getSpawnerWorld() {
					return cart.world;
				}

				@Override
				public BlockPos getSpawnerPosition() {
					return cart.getPosition();
				}

				@Override
				public Entity getSpawnerEntity() {
					return cart;
				}
			});
		}
		return this.mobSpawnerLogic.get();
	}

	public MobSpawnerBehavior() {
		super(Blocks.MOB_SPAWNER);
	}

	@Override
	public NBTTagCompound serialize() {
		NBTTagCompound tag = super.serialize();

		if (this.mobSpawnerLogic.isPresent()) {
			this.mobSpawnerLogic.get().writeToNBT(tag);
		}

		return tag;
	}

	@Override
	public void deserialize(NBTTagCompound tag) {
		if (this.mobSpawnerLogic.isPresent()) {
			this.mobSpawnerLogic.get().readFromNBT(tag);
		}
	}

	@Override
	public void tick(EntityMinecart cart) {
		this.getSpawnerLogic(cart).updateSpawner();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void handleStatusUpdate(EntityMinecart cart, byte id) {
		this.getSpawnerLogic(cart).setDelayToMin(id);
	}
}

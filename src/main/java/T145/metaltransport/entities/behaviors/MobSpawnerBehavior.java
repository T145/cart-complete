package T145.metaltransport.entities.behaviors;

import java.util.Optional;

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

	private Optional<MobSpawnerBaseLogic> logic = Optional.empty();

	protected MobSpawnerBaseLogic getLogic(EntityMinecart cart) {
		return this.logic.orElse(new MobSpawnerBaseLogic() {

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

	public MobSpawnerBehavior() {
		super(Blocks.MOB_SPAWNER);
	}

	@Override
	public NBTTagCompound serialize() {
		NBTTagCompound tag = super.serialize();
		this.logic.ifPresent(logic -> logic.writeToNBT(tag));
		return tag;
	}

	@Override
	public MobSpawnerBehavior deserialize(NBTTagCompound tag) {
		this.logic.ifPresent(logic -> logic.readFromNBT(tag));
		return this;
	}

	@Override
	public void tick(EntityMinecart cart) {
		this.getLogic(cart).updateSpawner();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void handleStatusUpdate(EntityMinecart cart, byte id) {
		this.getLogic(cart).setDelayToMin(id);
	}
}

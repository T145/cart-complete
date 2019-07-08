package t145.metaltransport.entities;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.world.World;

public class EntityMetalCart extends EntityMinecart {

	public EntityMetalCart(World world) {
		super(world);
	}

	public EntityMetalCart(World world, double x, double y, double z) {
		super(world, x, y, z);
	}

	@Override
	public Type getType() {
		// TODO Auto-generated method stub
		return null;
	}

}

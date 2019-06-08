package T145.metaltransport.entities.actions;

import T145.metaltransport.api.carts.CartAction;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class CartActionFurnace extends CartAction {

	private boolean powered;
	private int fuel;
	public double pushX;
	public double pushZ;

	public CartActionFurnace() {
		super(new Block[] { Blocks.FURNACE, Blocks.LIT_FURNACE });
	}

	@Override
	public CartAction serialize() {
		this.setBoolean("Powered", this.powered);
		this.setDouble("PushX", this.pushX);
		this.setDouble("PushZ", this.pushZ);
		this.setShort("Fuel", (short)this.fuel);
		return this;
	}

	@Override
	public CartAction deserialize() {
		this.powered = this.getBoolean("Powered");
		this.pushX = this.getDouble("PushX");
		this.pushZ = this.getDouble("PushZ");
		this.fuel = this.getShort("Fuel");
		return this;
	}

	@Override
	public void tick() {
	}
}

package T145.metaltransport.capabilities;

import T145.metaltransport.api.consts.CartType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public class ModuleCartType implements INBTSerializable<NBTTagCompound> {

	private CartType type;

	public ModuleCartType(CartType type) {
		this.type = type;
	}

	public ModuleCartType() {
		this(CartType.IRON);
	}

	public CartType getType() {
		return type;
	}

	public ModuleCartType setType(CartType type) {
		this.type = type;
		return this;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("CartType", getType().toString());
		return null;
	}

	@Override
	public void deserializeNBT(NBTTagCompound tag) {
		setType(CartType.valueOf(tag.getString("CartType")));
	}
}

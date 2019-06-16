package T145.metaltransport.api;

import net.minecraft.nbt.NBTTagCompound;

public interface IProfile {

	NBTTagCompound serialize();

	IProfile deserialize(NBTTagCompound tag);
}

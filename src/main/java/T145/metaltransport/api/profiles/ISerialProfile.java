package T145.metaltransport.api.profiles;

import net.minecraft.nbt.NBTTagCompound;

public interface ISerialProfile extends IProfile {

	NBTTagCompound serialize();

	ISerialProfile deserialize(NBTTagCompound tag);
}

package T145.metaltransport.api.obj.caps;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import T145.metaltransport.api.config.ConfigMT;
import T145.metaltransport.api.consts.CartType;
import T145.metaltransport.api.consts.RegistryMT;
import T145.metaltransport.api.obj.CapabilitiesMT;
import T145.metaltransport.api.obj.SerializersMT;
import mods.railcraft.common.carts.EntityCartGift;
import mods.railcraft.common.carts.EntityCartPumpkin;
import mods.railcraft.common.carts.EntityCartTNTWood;
import mods.railcraft.common.carts.EntityLocomotive;
import mods.railcraft.common.carts.EntityLocomotiveCreative;
import mods.railcraft.common.carts.EntityLocomotiveElectric;
import mods.railcraft.common.carts.EntityLocomotiveSteam;
import mods.railcraft.common.carts.EntityLocomotiveSteamSolid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;

public class SerialCartType implements INBTSerializable<NBTTagCompound> {

	public static final Set<Class<? extends EntityMinecart>> BLACKLIST = new HashSet() {{
		if (ConfigMT.hasRailcraft()) {
			add(EntityLocomotiveCreative.class);
			add(EntityLocomotiveElectric.class);
			add(EntityLocomotiveSteam.class);
			add(EntityLocomotiveSteamSolid.class);
			add(EntityLocomotive.class);
			add(EntityCartGift.class);
			add(EntityCartPumpkin.class);
			add(EntityCartTNTWood.class);
		}

		for (String className : ConfigMT.blacklist) {
			try {
				BLACKLIST.add((Class<? extends EntityMinecart>) Class.forName(className));
			} catch (ClassNotFoundException err) {
				RegistryMT.LOG.catching(err);
			}
		}
	}};

	public static final Map<Class<? extends EntityMinecart>, DataParameter<CartType>> PARAMS = new HashMap<>();

	public static final DataParameter<CartType> CART_TYPE = EntityDataManager.createKey(EntityMinecart.class, SerializersMT.CART_TYPE);

	private static DataParameter<CartType> createKey(Class<? extends EntityMinecart> cartClass) {
		return EntityDataManager.createKey(cartClass, SerializersMT.CART_TYPE);
	}

	public static void registerType(EntityMinecart cart) {
		EntityDataManager data = cart.getDataManager();
		Class<? extends EntityMinecart> cartClass = cart.getClass();

		if (data.getAll().size() == 13) {
			// the cart has no custom data parameters, so it can just get the default
			data.register(CART_TYPE, CartType.IRON);
		} else if (!BLACKLIST.contains(cartClass)) {
			if (PARAMS.containsKey(cartClass)) {
				data.register(PARAMS.get(cartClass), CartType.IRON);
			} else {
				DataParameter<CartType> param = createKey(cartClass);
				PARAMS.put(cartClass, param);
				data.register(param, CartType.IRON);
			}
		}
	}

	public static void attach(EntityMinecart cart, AttachCapabilitiesEvent<Entity> event) {
		if (!BLACKLIST.contains(cart.getClass())) {
			event.addCapability(RegistryMT.getResource(RegistryMT.KEY_CART_TYPE), new ICapabilitySerializable<NBTTagCompound>() {

				final SerialCartType type = new SerialCartType(cart);

				@Override
				public NBTTagCompound serializeNBT() {
					return type.serializeNBT();
				}

				@Override
				public void deserializeNBT(NBTTagCompound tag) {
					type.deserializeNBT(tag);
				}

				@Override
				public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
					if (capability == CapabilitiesMT.CART_TYPE) {
						return true;
					}
					return false;
				}

				@Nullable
				@Override
				public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
					if (capability == CapabilitiesMT.CART_TYPE) {
						return (T) type;
					}
					return null;
				}
			});
		}
	}

	private final EntityMinecart cart;

	public SerialCartType(EntityMinecart cart) {
		this.cart = cart;
	}

	public CartType getType() {
		EntityDataManager data = cart.getDataManager();
		Class<? extends EntityMinecart> cartClass = cart.getClass();

		if (PARAMS.containsKey(cartClass)) {
			return data.get(PARAMS.get(cartClass));
		} else {
			return data.get(CART_TYPE);
		}
	}

	public SerialCartType setType(CartType type) {
		EntityDataManager data = cart.getDataManager();
		Class<? extends EntityMinecart> cartClass = cart.getClass();

		if (PARAMS.containsKey(cartClass)) {
			data.set(PARAMS.get(cartClass), type);
		} else {
			data.set(CART_TYPE, type);
		}

		return this;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("CartType", getType().toString());
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound tag) {
		setType(CartType.valueOf(tag.getString("CartType")));
	}
}

package t145.metaltransport.api.caps;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.entity.item.EntityMinecartCommandBlock;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.entity.item.EntityMinecartFurnace;
import net.minecraft.entity.item.EntityMinecartHopper;
import net.minecraft.entity.item.EntityMinecartMobSpawner;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import t145.metaltransport.api.carts.IMetalCart;
import t145.metaltransport.api.config.ConfigMT;
import t145.metaltransport.api.consts.CartTier;
import t145.metaltransport.api.consts.RegistryMT;
import t145.metaltransport.api.objs.SerializersMT;

public class CapabilityCartType implements INBTSerializable<NBTTagCompound> {

	public static final ObjectOpenHashSet<Class<? extends EntityMinecart>> COMPATIBLE_CARTS = new ObjectOpenHashSet<>();

	static {
		COMPATIBLE_CARTS.add(EntityMinecartChest.class);
		COMPATIBLE_CARTS.add(EntityMinecartCommandBlock.class);
		COMPATIBLE_CARTS.add(EntityMinecartEmpty.class);
		COMPATIBLE_CARTS.add(EntityMinecartFurnace.class);
		COMPATIBLE_CARTS.add(EntityMinecartHopper.class);
		COMPATIBLE_CARTS.add(EntityMinecartMobSpawner.class);
		COMPATIBLE_CARTS.add(EntityMinecartTNT.class);
	}

	@CapabilityInject(CapabilityCartType.class)
	public static Capability<CapabilityCartType> instance;

	public static final DataParameter<CartTier> CART_TYPE = createKey(EntityMinecart.class);
	public static final Object2ObjectOpenHashMap<Class<? extends EntityMinecart>, DataParameter<CartTier>> PARAMS = new Object2ObjectOpenHashMap();

	private final EntityMinecart cart;

	public CapabilityCartType(final EntityMinecart cart) {
		this.cart = cart;
	}

	private static void loadFromConfig(final String[] from, final ObjectOpenHashSet<Class<? extends EntityMinecart>> to) {
		for (String className : from) {
			try {
				to.add((Class<? extends EntityMinecart>) Class.forName(className));
			} catch (ClassNotFoundException | ClassCastException err) {
				RegistryMT.LOG.catching(err);
			}
		}
	}

	private static boolean canAttach(final EntityMinecart cart) {
		return cart instanceof IMetalCart || COMPATIBLE_CARTS.contains(cart.getClass());
	}

	private static DataParameter<CartTier> createKey(final Class<? extends EntityMinecart> cartClass) {
		return EntityDataManager.createKey(cartClass, SerializersMT.CART_TYPE);
	}

	public static void register(final EntityMinecart cart) {
		EntityDataManager data = cart.getDataManager();
		Class<? extends EntityMinecart> cartClass = cart.getClass();

		if (canAttach(cart)) {
			if (!PARAMS.containsKey(cartClass)) {
				PARAMS.put(cartClass, createKey(cartClass));
			}

			data.register(PARAMS.get(cartClass), CartTier.IRON);
		} else if (ConfigMT.handleEmptyMinecarts && data.getAll().size() == 13) {
			data.register(CART_TYPE, CartTier.IRON);
		}
	}

	public static void attach(final EntityMinecart cart, final AttachCapabilitiesEvent<Entity> event) {
		if (canAttach(cart)) {
			event.addCapability(RegistryMT.getResource(RegistryMT.KEY_CART_TYPE), new ICapabilitySerializable<NBTTagCompound>() {

				final CapabilityCartType type = new CapabilityCartType(cart);

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
					if (capability == instance) {
						return true;
					}
					return false;
				}

				@Nullable
				@Override
				public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
					if (capability == instance) {
						return (T) type;
					}
					return null;
				}
			});
		}
	}

	public CartTier getType() {
		Class<? extends EntityMinecart> cartClass = cart.getClass();
		return cart.getDataManager().get(PARAMS.containsKey(cartClass) ? PARAMS.get(cartClass) : CART_TYPE);
	}

	public CapabilityCartType setType(final CartTier type) {
		Class<? extends EntityMinecart> cartClass = cart.getClass();
		cart.getDataManager().set(PARAMS.containsKey(cartClass) ? PARAMS.get(cartClass) : CART_TYPE, type);
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
		setType(CartTier.valueOf(tag.getString("CartType")));
	}
}

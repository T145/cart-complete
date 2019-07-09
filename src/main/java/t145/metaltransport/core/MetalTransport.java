package t145.metaltransport.core;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

import javax.annotation.Nullable;

import T145.tbone.core.TBone;
import T145.tbone.dispenser.BehaviorDispenseMinecart;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.entity.item.EntityMinecartMobSpawner;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTBase;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.DataSerializerEntry;
import net.minecraftforge.registries.IForgeRegistry;
import t145.metaltransport.api.caps.CapabilityCartType;
import t145.metaltransport.api.consts.CartType;
import t145.metaltransport.api.consts.ItemCartType;
import t145.metaltransport.api.consts.RegistryMT;
import t145.metaltransport.api.objs.ItemsMT;
import t145.metaltransport.api.objs.SerializersMT;
import t145.metaltransport.client.render.entities.RenderCart;
import t145.metaltransport.client.render.entities.RenderSpawnerCart;
import t145.metaltransport.client.render.entities.RenderTntCart;
import t145.metaltransport.entities.EntityFurnaceCart;
import t145.metaltransport.items.ItemCart;

@Mod(modid = RegistryMT.ID, name = RegistryMT.NAME, version = MetalTransport.VERSION, updateJSON = MetalTransport.UPDATE_JSON, dependencies = "required-after:tbone;after:metalchests")
@EventBusSubscriber
public class MetalTransport {

	public static final String VERSION = "@VERSION@";
	public static final String UPDATE_JSON = "https://raw.githubusercontent.com/T145/metaltransport/master/update.json";
	private static final Object2ObjectOpenHashMap<Item, Function<EntityMinecartEmpty, EntityMinecart>> CARTS = new Object2ObjectOpenHashMap<>();

	static {
		CARTS.put(Item.getItemFromBlock(Blocks.CHEST), (EntityMinecartEmpty e) -> getMinecart(new ResourceLocation("minecraft", "chest_minecart"), e.getEntityWorld(), e.posX, e.posY, e.posZ));
		CARTS.put(Item.getItemFromBlock(Blocks.TNT), (EntityMinecartEmpty e) -> getMinecart(new ResourceLocation("minecraft", "tnt_minecart"), e.getEntityWorld(), e.posX, e.posY, e.posZ));
		CARTS.put(Item.getItemFromBlock(Blocks.FURNACE), (EntityMinecartEmpty e) -> getMinecart(new ResourceLocation("minecraft", "furnace_minecart"), e.getEntityWorld(), e.posX, e.posY, e.posZ));
		CARTS.put(Item.getItemFromBlock(Blocks.HOPPER), (EntityMinecartEmpty e) -> getMinecart(new ResourceLocation("minecraft", "hopper_minecart"), e.getEntityWorld(), e.posX, e.posY, e.posZ));
		CARTS.put(Item.getItemFromBlock(Blocks.COMMAND_BLOCK), (EntityMinecartEmpty e) -> getMinecart(new ResourceLocation("minecraft", "commandblock_minecart"), e.getEntityWorld(), e.posX, e.posY, e.posZ));
		CARTS.put(Item.getItemFromBlock(Blocks.MOB_SPAWNER), (EntityMinecartEmpty e) -> getMinecart(new ResourceLocation("minecraft", "spawner_minecart"), e.getEntityWorld(), e.posX, e.posY, e.posZ));
	}

	public MetalTransport() {
		TBone.registerMod(RegistryMT.ID, RegistryMT.NAME);
	}

	public static boolean isDeobfuscated() {
		return VERSION.contentEquals("@VERSION@");
	}

	@Instance(RegistryMT.ID)
	public static MetalTransport instance;

	@EventHandler
	public void metaltransport$preInit(final FMLPreInitializationEvent event) {
		ModMetadata meta = event.getModMetadata();
		meta.authorList.add("T145");
		meta.autogenerated = false;
		meta.credits = "The fans!";
		meta.description = "Metal in Motion!";
		meta.logoFile = "logo.png";
		meta.modId = RegistryMT.ID;
		meta.name = RegistryMT.NAME;
		meta.url = "https://github.com/T145/metaltransport";
		meta.useDependencyInformation = false;
		meta.version = VERSION;
	}

	@EventHandler
	public void metaltransport$init(final FMLInitializationEvent event) {
		CapabilityManager.INSTANCE.register(CapabilityCartType.class, new Capability.IStorage<CapabilityCartType>() {

			@Nullable
			@Override
			public NBTBase writeNBT(Capability<CapabilityCartType> capability, CapabilityCartType instance, EnumFacing side) {
				return null;
			}

			@Override
			public void readNBT(Capability<CapabilityCartType> capability, CapabilityCartType instance, EnumFacing side, NBTBase nbt) {}

		}, () -> null);
	}

	@EventHandler
	public void metaltransport$postInit(final FMLPostInitializationEvent event) {
		BehaviorDispenseMinecart.register(ItemsMT.METAL_MINECART, ItemCart.DISPENSER_BEHAVIOR);
	}

	@SubscribeEvent
	public static void metaltransport$registerItems(final RegistryEvent.Register<Item> event) {
		final IForgeRegistry<Item> registry = event.getRegistry();

		Items.MINECART.setCreativeTab(null);
		Items.FURNACE_MINECART.setCreativeTab(null);
		Items.CHEST_MINECART.setCreativeTab(null);
		Items.HOPPER_MINECART.setCreativeTab(null);
		Items.TNT_MINECART.setCreativeTab(null);
		Items.COMMAND_BLOCK_MINECART.setCreativeTab(null);

		registry.register(ItemsMT.METAL_MINECART = new ItemCart());
	}

	@SubscribeEvent
	public static void metaltransport$registerEntities(final RegistryEvent.Register<EntityEntry> event) {
		final IForgeRegistry<EntityEntry> registry = event.getRegistry();
		registry.register(EntityEntryBuilder.create()
				.id(new ResourceLocation("furnace_minecart"), 44)
				.name(EntityMinecart.Type.FURNACE.getName())
				.entity(EntityFurnaceCart.class)
				.tracker(80, 3, true)
				.build());
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void metaltransport$registerModels(final ModelRegistryEvent event) {
		for (ItemCartType type : ItemCartType.values()) {
			TBone.registerModel(RegistryMT.ID, ItemsMT.METAL_MINECART, "item_minecart", type.ordinal(),	String.format("item=%s", type.getName()));
		}

		// TODO: Find a better way to register custom renders
		// - iterate over difference of whitelist and blacklist
		for (Class c : CapabilityCartType.WHITELIST) {
			if (c != EntityMinecartTNT.class && c != EntityMinecartMobSpawner.class) {
				RenderingRegistry.registerEntityRenderingHandler(c, manager -> new RenderCart(manager));
			}
		}

		RenderingRegistry.registerEntityRenderingHandler(EntityMinecartTNT.class, manager -> new RenderTntCart(manager));
		RenderingRegistry.registerEntityRenderingHandler(EntityMinecartMobSpawner.class, manager -> new RenderSpawnerCart(manager));
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void metalchests$registerRecipes(RegistryEvent.Register<IRecipe> event) {
		ItemCartType.registerRecipes();
	}

	@SubscribeEvent
	public static void metaltransport$registerSerializers(final RegistryEvent.Register<DataSerializerEntry> event) {
		final IForgeRegistry<DataSerializerEntry> registry = event.getRegistry();

		registry.register(SerializersMT.ENTRY_CART_TYPE = new DataSerializerEntry(
				SerializersMT.CART_TYPE = new DataSerializer<CartType>() {

					@Override
					public void write(PacketBuffer buf, CartType value) {
						buf.writeEnumValue(value);
					}

					@Override
					public CartType read(PacketBuffer buf) throws IOException {
						return buf.readEnumValue(CartType.class);
					}

					@Override
					public DataParameter<CartType> createKey(int id) {
						return new DataParameter<CartType>(id, this);
					}

					@Override
					public CartType copyValue(CartType value) {
						return value;
					}

				}).setRegistryName(RegistryMT.ID, RegistryMT.KEY_CART_TYPE));
	}

	@SubscribeEvent
	public static void metaltransport$constructEntity(final EntityEvent.EntityConstructing event) {
		Entity entity = event.getEntity();

		if (entity instanceof EntityMinecart) {
			CapabilityCartType.register((EntityMinecart) entity);
		}
	}

	@SubscribeEvent
	public static void metaltransport$attachCapabilities(final AttachCapabilitiesEvent<Entity> event) {
		Entity entity = event.getObject();

		if (entity instanceof EntityMinecart) {
			CapabilityCartType.attach((EntityMinecart) entity, event);
		}
	}

	// TODO: Compensate for death by explosions
	@SubscribeEvent
	public static void metaltransport$entityAttacked(final AttackEntityEvent event) {
		Entity target = event.getTarget();

		if (target instanceof EntityMinecart) {
			EntityMinecart cart = (EntityMinecart) target;
			World world = cart.world;

			if (!world.isRemote && !cart.getIsInvulnerable() && cart.hasCapability(CapabilityCartType.instance, null)) {
				CartType type = cart.getCapability(CapabilityCartType.instance, null).getType();

				if (type.getKillRange().contains(cart.getDamage()) && world.getGameRules().getBoolean("doEntityDrops")) {
					ItemStack cartStack = new ItemStack(ItemsMT.METAL_MINECART, 1, ItemCartType.getEmptyType(type).ordinal());

					if (cart.hasCustomName()) {
						cartStack.setStackDisplayName(cart.getCustomNameTag());
					}

					cart.entityDropItem(cartStack, 0.0F);

					if (cart.getType() != EntityMinecart.Type.RIDEABLE) {
						cart.entityDropItem(new ItemStack(cart.getDisplayTile().getBlock()), 0.0F);
					}

					cart.setDead();
					event.setCanceled(true);
				}
			}
		}
	}

	public static Block getBlockFromStack(ItemStack stack) {
		return Block.getBlockFromItem(stack.getItem());
	}

	public static boolean isSolidBlock(ItemStack stack) {
		return !stack.isEmpty() && getBlockFromStack(stack) != Blocks.AIR /* && block is relatively normal && in whitelist || not in blacklist */;
	}

	/**
	 * Copied from Quark:
	 * https://github.com/Vazkii/Quark/blob/master/src/main/java/vazkii/quark/tweaks/feature/MinecartInteraction.java#L57
	 */
	private static EntityMinecart getMinecart(ResourceLocation rl, World world, double x, double y, double z) {
		try {
			EntityEntry entry = ForgeRegistries.ENTITIES.getValue(rl);
			if (entry != null) {
				Class<? extends Entity> minecartClass = entry.getEntityClass();
				return (EntityMinecart) minecartClass
						.getConstructor(World.class, double.class, double.class, double.class)
						.newInstance(world, x, y, z);
			}
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException
				| NoSuchMethodException err) {
			RegistryMT.LOG.catching(err);
		}
		return null;
	}

	@SubscribeEvent
	public static void metaltransport$minecartInteract(MinecartInteractEvent event) {
		EntityMinecart cart = event.getMinecart();
		EntityPlayer player = event.getPlayer();
		World world = player.world;

		if (cart instanceof EntityMinecartEmpty) {
			// will be replaced w/ capability check for Display Stack
			if (!cart.isBeingRidden() && !Loader.isModLoaded("quark")) {
				EnumHand hand = EnumHand.MAIN_HAND;
				ItemStack stack = player.getHeldItemMainhand();

				if (stack.isEmpty() || !CARTS.containsKey(stack.getItem())) {
					stack = player.getHeldItemOffhand();
					hand = EnumHand.OFF_HAND;
				}

				if (!stack.isEmpty() && CARTS.containsKey(stack.getItem())) {
					if (!world.isRemote) {
						EntityMinecart minecart = CARTS.get(stack.getItem()).apply((EntityMinecartEmpty) cart);

						if (minecart != null) {
							CartType type = cart.getCapability(CapabilityCartType.instance, null).getType();
							minecart.getCapability(CapabilityCartType.instance, null).setType(type);

							cart.setDead();
							world.spawnEntity(minecart);

							if (!player.capabilities.isCreativeMode) {
								stack.shrink(1);

								if (stack.getCount() <= 0) {
									player.setHeldItem(hand, ItemStack.EMPTY);
								}
							}
						}
					}

					player.swingArm(hand);
					event.setCanceled(true);
				}
			}
		} else {
			if (player.isSneaking()) {
				EntityMinecartEmpty emptyCart = new EntityMinecartEmpty(cart.world, cart.posX, cart.posY, cart.posZ);
				CartType type = cart.getCapability(CapabilityCartType.instance, null).getType();
				emptyCart.getCapability(CapabilityCartType.instance, null).setType(type);
				emptyCart.motionX = cart.motionX;
				emptyCart.motionY = cart.motionY;
				emptyCart.motionZ = cart.motionZ;
				emptyCart.rotationPitch = cart.rotationPitch;
				emptyCart.rotationYaw = cart.rotationYaw;

				if (EntityFurnaceCart.isSpeeding(cart)) {
					emptyCart.lastTickPosX = cart.lastTickPosX;
					emptyCart.lastTickPosY = cart.lastTickPosY;
					emptyCart.lastTickPosZ = cart.lastTickPosZ;
				}

				if (!world.isRemote) {
					cart.entityDropItem(new ItemStack(cart.getDefaultDisplayTile().getBlock()), 0);
					cart.setDead();
					world.spawnEntity(emptyCart);
				}

				event.setCanceled(true);
			}
		}
	}
}
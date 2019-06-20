package T145.metaltransport;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import T145.metaltransport.api.consts.ItemCartType;
import T145.metaltransport.api.consts.RegistryMT;
import T145.metaltransport.api.obj.ItemsMT;
import T145.metaltransport.capabilities.ModuleCartType;
import T145.metaltransport.client.render.entities.RenderCart;
import T145.metaltransport.entities.EntityFurnaceCart;
import T145.metaltransport.items.ItemCart;
import T145.tbone.core.TBone;
import T145.tbone.dispenser.BehaviorDispenseMinecart;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

@Mod(modid = RegistryMT.ID, name = RegistryMT.NAME, version = MetalTransport.VERSION, updateJSON = MetalTransport.UPDATE_JSON, dependencies = "required-after:tbone;after:metalchests")
@EventBusSubscriber(modid = RegistryMT.ID)
public class MetalTransport {

	public static final String VERSION = "@VERSION@";
	public static final String UPDATE_JSON = "https://raw.githubusercontent.com/T145/metaltransport/master/update.json";

	public MetalTransport() {
		TBone.registerMod(RegistryMT.ID, RegistryMT.NAME);
	}

	public static boolean inDevMode() {
		return VERSION.contentEquals("@VERSION@");
	}

	@CapabilityInject(ModuleCartType.class)
	public static Capability<ModuleCartType> CAP_CART_TYPE;

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
	public void init(FMLInitializationEvent event) {
		CapabilityManager.INSTANCE.register(ModuleCartType.class, new Capability.IStorage<ModuleCartType>() {

			@Nullable
			@Override
			public NBTBase writeNBT(Capability<ModuleCartType> capability, ModuleCartType instance, EnumFacing side) {
				return null;
			}

			@Override
			public void readNBT(Capability<ModuleCartType> capability, ModuleCartType instance, EnumFacing side, NBTBase nbt) {}

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
		registry.register(EntityEntryBuilder.create().id(new ResourceLocation("furnace_minecart"), 44)
				.name(EntityMinecart.Type.FURNACE.getName()).entity(EntityFurnaceCart.class).tracker(80, 3, true)
				.build());
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void metaltransport$registerModels(final ModelRegistryEvent event) {
		for (ItemCartType type : ItemCartType.values()) {
			TBone.registerModel(RegistryMT.ID, ItemsMT.METAL_MINECART, "item_minecart", type.ordinal(),	String.format("item=%s", type.getName()));
		}

		RenderingRegistry.registerEntityRenderingHandler(EntityMinecart.class, manager -> new RenderCart(manager));
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void metalchests$registerRecipes(RegistryEvent.Register<IRecipe> event) {
	}

	@SubscribeEvent
	public static void metaltransport$updateConfig(OnConfigChangedEvent event) {
		if (event.getModID().equals(RegistryMT.ID)) {
			ConfigManager.sync(RegistryMT.ID, Config.Type.INSTANCE);
		}
	}

	@SubscribeEvent
	public void metaltransport$attachCapabilities(AttachCapabilitiesEvent<EntityMinecart> event) {
		EntityMinecart cart = event.getObject();

		event.addCapability(RegistryMT.getResource("cart_type"), new ICapabilitySerializable<NBTTagCompound>() {

			final ModuleCartType moduleCartType = new ModuleCartType();

			@Override
			public NBTTagCompound serializeNBT() {
				return moduleCartType.serializeNBT();
			}

			@Override
			public void deserializeNBT(NBTTagCompound tag) {
				moduleCartType.deserializeNBT(tag);
			}

			@Override
			public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
				if (capability == CAP_CART_TYPE) {
					return true;
				}
				return false;
			}

			@Nullable
			@Override
			public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
				if (capability == CAP_CART_TYPE) {
					return (T) moduleCartType;
				}
				return null;
			}
		});
	}
}

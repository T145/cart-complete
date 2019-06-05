package T145.metaltransport.core;

import java.io.IOException;

import T145.metaltransport.api.EntitiesMT;
import T145.metaltransport.api.ItemsMT;
import T145.metaltransport.api.SerializersMT;
import T145.metaltransport.api.constants.CartType;
import T145.metaltransport.api.constants.RegistryMT;
import T145.metaltransport.client.render.entities.RenderMetalMinecartEmpty;
import T145.metaltransport.client.render.entities.RenderRidingBlock;
import T145.metaltransport.entities.EntityMetalMinecartEmpty;
import T145.metaltransport.entities.EntityRidingBlock;
import T145.metaltransport.items.ItemMetalMinecart;
import T145.tbone.core.TBone;
import T145.tbone.dispenser.BehaviorDispenseMinecart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.DataSerializerEntry;
import net.minecraftforge.registries.IForgeRegistry;

@Mod(modid = RegistryMT.ID, name = RegistryMT.NAME, version = RegistryMT.VERSION, updateJSON = RegistryMT.UPDATE_JSON, dependencies = "required-after:tbone;after:metalchests")
@EventBusSubscriber(modid = RegistryMT.ID)
public class MetalTransport {

	public MetalTransport() {
		TBone.registerMod(RegistryMT.ID, RegistryMT.NAME);
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
		meta.version = RegistryMT.VERSION;
	}

	@EventHandler
	public void metaltransport$init(final FMLInitializationEvent event) {
		DataFixer fixer = FMLCommonHandler.instance().getDataFixer();
		EntityMinecart.registerFixesMinecart(fixer, EntityMetalMinecartEmpty.class);
	}

	@EventHandler
	public void metaltransport$postInit(final FMLPostInitializationEvent event) {
		BehaviorDispenseMinecart.register(ItemsMT.METAL_MINECART, ItemMetalMinecart.DISPENSER_BEHAVIOR);
	}

	@SubscribeEvent
	public static void metaltransport$updateConfig(final OnConfigChangedEvent event) {
		if (event.getModID().equals(RegistryMT.ID)) {
			ConfigManager.sync(RegistryMT.ID, Config.Type.INSTANCE);
		}
	}

	@SubscribeEvent
	public static void metaltransport$registerSerializers(final RegistryEvent.Register<DataSerializerEntry> event) {
		final IForgeRegistry<DataSerializerEntry> registry = event.getRegistry();

		registry.register(SerializersMT.CART_TYPE = new DataSerializerEntry(new DataSerializer<CartType>() {

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
	public static void metaltransport$registerItems(final RegistryEvent.Register<Item> event) {
		final IForgeRegistry<Item> registry = event.getRegistry();

		registry.register(ItemsMT.METAL_MINECART = new ItemMetalMinecart());
	}

	@SubscribeEvent
	public static void metaltransport$registerEntities(final RegistryEvent.Register<EntityEntry> event) {
		final IForgeRegistry<EntityEntry> registry = event.getRegistry();

		registry.register(EntitiesMT.METAL_MINECART = EntityEntryBuilder.create().id(RegistryMT.KEY_METAL_MINECART, 0).name(RegistryMT.KEY_METAL_MINECART).entity(EntityMetalMinecartEmpty.class).tracker(80, 3, true).build());
		registry.register(EntitiesMT.RIDING_BLOCK = EntityEntryBuilder.create().id(RegistryMT.KEY_RIDING_BLOCK, 1).name(RegistryMT.KEY_RIDING_BLOCK).entity(EntityRidingBlock.class).tracker(80, 3, true).build());
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void metaltransport$registerModels(final ModelRegistryEvent event) {
		for (CartType type : CartType.values()) {
			TBone.registerModel(RegistryMT.ID, ItemsMT.METAL_MINECART, "item_minecart", type.ordinal(), String.format("item=%s", type.getName()));
		}

		RenderingRegistry.registerEntityRenderingHandler(EntityMetalMinecartEmpty.class, manager -> new RenderMetalMinecartEmpty(manager));
		RenderingRegistry.registerEntityRenderingHandler(EntityRidingBlock.class, manager -> new RenderRidingBlock(manager));
	}

	private static boolean isValidEntity(Entity entity) {
		return entity instanceof EntityMinecart;
	}

	private static boolean isValidStack(ItemStack stack) {
		return stack.getItem() == Item.getItemFromBlock(Blocks.FURNACE);
	}

	@SubscribeEvent
	public static void metaltransport$playerInteract(PlayerInteractEvent.EntityInteractSpecific event) {
		Entity target = event.getTarget();
		EntityPlayer player = event.getEntityPlayer();

		if (isValidEntity(target) && target.getPassengers().isEmpty()) {
			EnumHand hand = EnumHand.MAIN_HAND;
			ItemStack stack = player.getHeldItemMainhand();

			if (!isValidStack(stack)) {
				stack = player.getHeldItemOffhand();
				hand = EnumHand.OFF_HAND;
			}

			if (isValidStack(stack)) {
				World world = event.getWorld();
				EntityRidingBlock passenger = new EntityRidingBlock(world, stack);
				passenger.setPosition(target.posX, target.posY, target.posZ);
				passenger.rotationYaw = target.rotationYaw;

				if (!event.getWorld().isRemote) {
					if (!player.isCreative())
						stack.shrink(1);
					world.spawnEntity(passenger);
					passenger.startRiding(target);
				}

				player.swingArm(hand);
				event.setCancellationResult(EnumActionResult.SUCCESS);
				event.setCanceled(true);
			}
		}
	}
}

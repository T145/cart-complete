package t145.metaltransport.core;

import java.io.IOException;

import javax.annotation.Nullable;

import T145.tbone.core.ClientRegistrationHelper;
import T145.tbone.core.RegistrationHelper;
import T145.tbone.dispenser.BehaviorDispenseMinecart;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockBeacon;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockEnchantmentTable;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.inventory.GuiBeacon;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiDispenser;
import net.minecraft.client.gui.inventory.GuiShulkerBox;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.entity.item.EntityMinecartMobSpawner;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerBeacon;
import net.minecraft.inventory.ContainerDispenser;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.ContainerShulkerBox;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumDyeColor;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldNameable;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.ForgeHooks;
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
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.DataSerializerEntry;
import net.minecraftforge.registries.IForgeRegistry;
import shadows.fastbench.gui.ContainerFastBench;
import shadows.fastbench.gui.GuiFastBench;
import t145.metaltransport.api.caps.CapabilityCartType;
import t145.metaltransport.api.consts.CartType;
import t145.metaltransport.api.consts.ItemCartType;
import t145.metaltransport.api.consts.RegistryMT;
import t145.metaltransport.api.objs.ItemsMT;
import t145.metaltransport.api.objs.SerializersMT;
import t145.metaltransport.api.profiles.ProfileRegistry;
import t145.metaltransport.client.render.entities.RenderCart;
import t145.metaltransport.client.render.entities.RenderMetalCart;
import t145.metaltransport.client.render.entities.RenderSpawnerCart;
import t145.metaltransport.client.render.entities.RenderTntCart;
import t145.metaltransport.entities.EntityFurnaceCart;
import t145.metaltransport.entities.EntityMetalCart;
import t145.metaltransport.entities.profiles.AnvilProfile.ProfileFactoryAnvil;
import t145.metaltransport.entities.profiles.BeaconProfile;
import t145.metaltransport.entities.profiles.BeaconProfile.ProfileFactoryBeacon;
import t145.metaltransport.entities.profiles.CraftingTableProfile.ProfileFactoryCraftingTable;
import t145.metaltransport.entities.profiles.DispenserProfile;
import t145.metaltransport.entities.profiles.DispenserProfile.ProfileFactoryDispenser;
import t145.metaltransport.entities.profiles.DropperProfile.ProfileFactoryDropper;
import t145.metaltransport.entities.profiles.EnchantingTableProfile.ProfileFactoryEnchantingTable;
import t145.metaltransport.entities.profiles.EnderChestProfile.ProfileFactoryEnderChest;
import t145.metaltransport.entities.profiles.JukeboxProfile.ProfileFactoryJukebox;
import t145.metaltransport.entities.profiles.ShulkerBoxProfile;
import t145.metaltransport.entities.profiles.ShulkerBoxProfile.ProfileFactoryShulkerBox;
import t145.metaltransport.items.ItemCart;

@Mod(modid = RegistryMT.ID, name = RegistryMT.NAME, version = MetalTransport.VERSION, updateJSON = MetalTransport.UPDATE_JSON, dependencies = "required-after:tbone;after:metalchests")
@EventBusSubscriber
public class MetalTransport implements IGuiHandler {

	public static final String VERSION = "@VERSION@";
	public static final String UPDATE_JSON = "https://raw.githubusercontent.com/T145/metaltransport/master/update.json";

	public MetalTransport() {
		RegistrationHelper.registerMod(RegistryMT.ID, RegistryMT.NAME);
	}

	public static boolean isDeobfuscated() {
		return VERSION.contentEquals("@VERSION@");
	}

	@Instance(RegistryMT.ID)
	public static MetalTransport instance;

	@Override
	public Container getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		EntityMetalCart cart = (EntityMetalCart) world.getEntityByID(ID);

		if (cart != null && cart.getProfile().isPresent()) {
			Block block = cart.getDisplayBlock();

			if (block instanceof BlockWorkbench) {
				if (Loader.isModLoaded("fastbench")) {
					return new ContainerFastBench(player, world, cart.getPosition()) {

						@Override
						public boolean canInteractWith(EntityPlayer player) {
							return cart.isEntityAlive() && player.getDistanceSq(cart.posX + 0.5D, cart.posY + 0.5D, cart.posZ + 0.5D) <= 64.0D;
						}
					};
				} else {
					return new ContainerWorkbench(player.inventory, world, cart.getPosition()) {

						@Override
						public boolean canInteractWith(EntityPlayer player) {
							return cart.isEntityAlive() && player.getDistanceSq(cart.posX + 0.5D, cart.posY + 0.5D, cart.posZ + 0.5D) <= 64.0D;
						}
					};
				}
			}

			if (block instanceof BlockEnchantmentTable) {
				return new ContainerEnchantment(player.inventory, world, cart.getPosition()) {

					@Override
					public void onCraftMatrixChanged(IInventory inventory) {
						super.onCraftMatrixChanged(inventory);
						// TODO: Tweak this to get influenced by cart contents that increase enchant power
					}

					@Override
					public boolean canInteractWith(EntityPlayer player) {
						return cart.isEntityAlive() && player.getDistanceSq(cart.posX + 0.5D, cart.posY + 0.5D, cart.posZ + 0.5D) <= 64.0D;
					}
				};
			}

			if (block instanceof BlockBeacon) {
				return new ContainerBeacon(player.inventory, (BeaconProfile) cart.getProfile().get());
			}

			if (block instanceof BlockShulkerBox) {
				return new ContainerShulkerBox(player.inventory, (ShulkerBoxProfile) cart.getProfile().get(), player);
			}

			if (block instanceof BlockDispenser) {
				return new ContainerDispenser(player.inventory, (DispenserProfile) cart.getProfile().get());
			}

			if (block instanceof BlockAnvil) {
				return new ContainerRepair(player.inventory, world, cart.getPosition(), player) {

					protected Slot setSlotInContainer(int index, Slot slot) {
						slot.slotNumber = this.inventorySlots.size();
						this.inventorySlots.set(index, slot);
						this.inventoryItemStacks.set(index, ItemStack.EMPTY);
						return slot;
					}

					{
						this.setSlotInContainer(2, new Slot(this.outputSlot, 2, 134, 47)
						{
							@Override
							public boolean isItemValid(ItemStack stack) {
								return false;
							}

							@Override
							public boolean canTakeStack(EntityPlayer playerIn) {
								return (playerIn.capabilities.isCreativeMode
										|| playerIn.experienceLevel >= maximumCost)
										&& maximumCost > 0 && this.getHasStack();
							}

							@Override
							public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack) {
								if (!thePlayer.capabilities.isCreativeMode) {
									thePlayer.addExperienceLevel(-maximumCost);
								}

								float breakChance = ForgeHooks.onAnvilRepair(thePlayer, stack, inputSlots.getStackInSlot(0), inputSlots.getStackInSlot(1));

								inputSlots.setInventorySlotContents(0, ItemStack.EMPTY);

								if (materialCost > 0) {
									ItemStack itemstack = inputSlots.getStackInSlot(1);

									if (!itemstack.isEmpty() && itemstack.getCount() > materialCost) {
										itemstack.shrink(materialCost);
										inputSlots.setInventorySlotContents(1, itemstack);
									} else {
										inputSlots.setInventorySlotContents(1, ItemStack.EMPTY);
									}
								} else {
									inputSlots.setInventorySlotContents(1, ItemStack.EMPTY);
								}

								maximumCost = 0;

								if (!world.isRemote) {
									BlockPos pos = cart.getPosition();

									if (!thePlayer.capabilities.isCreativeMode && thePlayer.getRNG().nextFloat() < breakChance) {
										int l = cart.getDisplayStack().getMetadata();
										++l;

										if (l > 2) {
											cart.removeDisplayBlock(false);
											world.playEvent(1029, pos, 0);
										} else {
											cart.setDisplayStack(new ItemStack(Blocks.ANVIL, 1, l));
											world.playEvent(1030, pos, 0);
										}
									} else {
										world.playEvent(1030, pos, 0);
									}
								}

								return stack;
							}
						});
					}

					@Override
					public boolean canInteractWith(EntityPlayer player) {
						return cart.isEntityAlive() && player.getDistanceSq(cart.posX + 0.5D, cart.posY + 0.5D, cart.posZ + 0.5D) <= 64.0D;
					}
				};
			}
		}

		return null;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public GuiContainer getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		EntityMetalCart cart = (EntityMetalCart) world.getEntityByID(ID);

		if (cart != null && cart.getProfile().isPresent()) {
			Block block = cart.getDisplayBlock();

			if (block instanceof BlockWorkbench) {
				return new GuiFastBench(player.inventory, world, cart.getPosition());
			}

			if (block instanceof BlockEnchantmentTable) {
				return new GuiEnchantment(player.inventory, world, (IWorldNameable) cart.getProfile().get());
			}

			if (block instanceof BlockBeacon) {
				return new GuiBeacon(player.inventory, (BeaconProfile) cart.getProfile().get());
			}

			if (block instanceof BlockShulkerBox) {
				return new GuiShulkerBox(player.inventory, (ShulkerBoxProfile) cart.getProfile().get());
			}

			if (block instanceof BlockDispenser) {
				return new GuiDispenser(player.inventory, (DispenserProfile) cart.getProfile().get());
			}

			if (block instanceof BlockAnvil) {
				return new GuiRepair(player.inventory, world);
			}
		}

		return null;
	}

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
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, this);
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
		ProfileRegistry.register(Blocks.ENDER_CHEST, new ProfileFactoryEnderChest());
		ProfileRegistry.register(Blocks.ENCHANTING_TABLE, new ProfileFactoryEnchantingTable());
		ProfileRegistry.register(Blocks.CRAFTING_TABLE, new ProfileFactoryCraftingTable());
		ProfileRegistry.register(Blocks.BEACON, new ProfileFactoryBeacon());
		ProfileRegistry.register(Blocks.JUKEBOX, new ProfileFactoryJukebox());

		for (EnumDyeColor color : EnumDyeColor.values()) {
			ProfileRegistry.register(BlockShulkerBox.getBlockByColor(color), new ProfileFactoryShulkerBox());
		}

		ProfileRegistry.register(Blocks.DISPENSER, new ProfileFactoryDispenser());
		ProfileRegistry.register(Blocks.DROPPER, new ProfileFactoryDropper());
		ProfileRegistry.register(Blocks.ANVIL, new ProfileFactoryAnvil());
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
		registry.register(EntityEntryBuilder.create()
				.id(RegistryMT.getResource(RegistryMT.KEY_METAL_MINECART), 0)
				.name(RegistryMT.KEY_METAL_MINECART)
				.entity(EntityMetalCart.class)
				.tracker(80, 3, true)
				.build());
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void metaltransport$registerModels(final ModelRegistryEvent event) {
		for (ItemCartType type : ItemCartType.values()) {
			ClientRegistrationHelper.registerModel(RegistryMT.ID, ItemsMT.METAL_MINECART, "item_minecart", type.ordinal(), String.format("item=%s", type.getName()));
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
		RenderingRegistry.registerEntityRenderingHandler(EntityMetalCart.class, manager -> new RenderMetalCart(manager));
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

		if (target instanceof EntityMinecart && !(target instanceof EntityMetalCart)) {
			EntityMinecart cart = (EntityMinecart) target;
			World world = cart.world;

			if (!world.isRemote && !cart.getIsInvulnerable() && cart.hasCapability(CapabilityCartType.instance, null)) {
				CartType type = cart.getCapability(CapabilityCartType.instance, null).getType();

				if (type.getDurability().contains(cart.getDamage()) && world.getGameRules().getBoolean("doEntityDrops")) {
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

	@SubscribeEvent
	public static void metaltransport$minecartInteract(MinecartInteractEvent event) {
		EntityMinecart cart = event.getMinecart();
		EntityPlayer player = event.getPlayer();
		World world = player.world;

		// Creation from EntityMetalCart to another vanilla cart is handled by itself

		if (cart instanceof EntityMinecartEmpty) {
			if (!cart.isBeingRidden() && !Loader.isModLoaded("quark")) {
				EnumHand hand = EnumHand.MAIN_HAND;
				ItemStack stack = player.getHeldItemMainhand();

				if (stack.isEmpty() || !EntityMetalCart.CARTS.containsKey(stack.getItem())) {
					stack = player.getHeldItemOffhand();
					hand = EnumHand.OFF_HAND;
				}

				if (!stack.isEmpty() && EntityMetalCart.CARTS.containsKey(stack.getItem())) {
					if (!world.isRemote) {
						EntityMinecart minecart = EntityMetalCart.CARTS.get(stack.getItem()).apply(cart);

						if (minecart != null) {
							// TODO: The cart is assumed to be registered; fix this to not do so
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
		} else if (!(cart instanceof EntityMetalCart) && player.isSneaking()) {
			EntityMetalCart emptyCart = new EntityMetalCart(cart);

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

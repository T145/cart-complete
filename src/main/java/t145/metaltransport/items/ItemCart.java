package t145.metaltransport.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import t145.metaltransport.api.caps.CapabilityCartType;
import t145.metaltransport.api.consts.CartType;
import t145.metaltransport.api.consts.RegistryMT;
import t145.metaltransport.entities.EntityFurnaceCart;
import t145.metaltransport.entities.EntityMetalCart;
import t145.tbone.dispenser.BehaviorDispenseMinecart;
import t145.tbone.items.TItem;

public class ItemCart extends TItem {

	public static final BehaviorDispenseMinecart DISPENSER_BEHAVIOR = new BehaviorDispenseMinecart() {

		private EntityMinecart getCartInstance(World world, double x, double y, double z, EntityMinecart.Type type) {
			switch (type) {
			case RIDEABLE:
				return new EntityMetalCart(world, x, y, z);
			case FURNACE:
				return new EntityFurnaceCart(world, x, y, z);
			default:
				return EntityMinecart.create(world, x, y, z, type);
			}
		}

		@Override
		public EntityMinecart getMinecartEntity(World world, double x, double y, double z, ItemStack stack) {
			CartType itemType = CartType.values()[stack.getItemDamage()];
			EntityMinecart cart = getCartInstance(world, x, y, z, itemType.getCartType());
			cart.getCapability(CapabilityCartType.instance, null).setType(itemType.getType());
			return cart;
		}
	};

	public ItemCart() {
		super(CartType.values(), RegistryMT.RESOURCE_METAL_MINECART, CreativeTabs.TRANSPORTATION);
		this.setMaxStackSize(new ItemStack(Items.MINECART).getMaxStackSize());
		BehaviorDispenseMinecart.register(this, DISPENSER_BEHAVIOR);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return DISPENSER_BEHAVIOR.placeStack(player, hand, world, pos);
	}
}
package T145.metaltransport.items;

import T145.metaltransport.api.consts.ItemCartType;
import T145.metaltransport.api.consts.RegistryMT;
import T145.metaltransport.api.obj.CapabilitiesMT;
import T145.metaltransport.entities.EntityFurnaceCart;
import T145.tbone.dispenser.BehaviorDispenseMinecart;
import T145.tbone.items.TItem;
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

public class ItemCart extends TItem {

	public static final BehaviorDispenseMinecart DISPENSER_BEHAVIOR = new BehaviorDispenseMinecart() {

		@Override
		public EntityMinecart getMinecartEntity(World world, double x, double y, double z, ItemStack stack) {
			ItemCartType itemType = ItemCartType.values()[stack.getItemDamage()];
			EntityMinecart cart = itemType.getCartType() == EntityMinecart.Type.FURNACE ? new EntityFurnaceCart(world, x, y, z) : EntityMinecart.create(world, x, y, z, itemType.getCartType());
			cart.getCapability(CapabilitiesMT.CART_TYPE, null).setType(itemType.getType());
			return cart;
		}
	};

	public ItemCart() {
		super(RegistryMT.RESOURCE_METAL_MINECART, ItemCartType.values(), CreativeTabs.TRANSPORTATION);
		this.setMaxStackSize(new ItemStack(Items.MINECART).getMaxStackSize());
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return DISPENSER_BEHAVIOR.placeStack(player, hand, world, pos);
	}
}

package T145.metaltransport.items;

import T145.metaltransport.api.constants.CartType;
import T145.metaltransport.api.constants.RegistryMT;
import T145.metaltransport.entities.EntityMetalMinecart;
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

public class ItemMetalMinecart extends TItem {

	public static final BehaviorDispenseMinecart DISPENSER_BEHAVIOR = new BehaviorDispenseMinecart() {

		@Override
		public EntityMinecart getMinecartEntity(World world, double x, double y, double z, ItemStack stack) {
			return new EntityMetalMinecart(world, x, y, z).setCartType(CartType.byMetadata(stack.getItemDamage()));
		}
	};

	public ItemMetalMinecart() {
		super(RegistryMT.RESOURCE_METAL_MINECART, CartType.values(), CreativeTabs.TRANSPORTATION);
		this.setMaxStackSize(Items.MINECART.getItemStackLimit());
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return DISPENSER_BEHAVIOR.placeStack(player, hand, world, pos);
	}
}

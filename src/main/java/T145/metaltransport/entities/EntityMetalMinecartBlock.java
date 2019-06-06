package T145.metaltransport.entities;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class EntityMetalMinecartBlock extends EntityMetalMinecartEmpty {

	public EntityMetalMinecartBlock(World world) {
		super(world);
	}

	public EntityMetalMinecartBlock(World world, double x, double y, double z) {
		super(world, x, y, z);
	}

	public EntityMetalMinecartBlock(EntityMinecart cart) {
		super(cart);
	}

	public void setDisplayTile(Block block) {
		this.setDisplayTile(block.getDefaultState());
	}

	public void setDisplayTile(Item item) {
		this.setDisplayTile(Block.getBlockFromItem(item));
	}

	public void setDisplayTile(ItemStack stack) {
		this.setDisplayTile(stack.getItem());
	}

	@Override
	public void killMinecart(DamageSource source) {
		super.killMinecart(source);

		if (world.getGameRules().getBoolean("doEntityDrops")) {
			entityDropItem(new ItemStack(this.getDisplayTile().getBlock()), 0.0F);
		}
	}

	@Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
		if (super.processInitialInteract(player, hand) || this.isBeingRidden() || this.hasDisplayTile()) {
			return true;
		}

		if (player.isSneaking()) {
			return false;
		} else {
			if (!this.world.isRemote) {
				player.startRiding(this);
			}
			return true;
		}
	}
}

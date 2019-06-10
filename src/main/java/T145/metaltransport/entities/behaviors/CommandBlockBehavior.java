package T145.metaltransport.entities.behaviors;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Optional;

import T145.metaltransport.api.carts.CartBehavior;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.CommandBlockBaseLogic;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CommandBlockBehavior extends CartBehavior {

	public String command = StringUtils.EMPTY;
	public ITextComponent lastOutput = new TextComponentString("");
	private Optional<CommandBlockBaseLogic> logic = Optional.absent();
	private int activatorRailCooldown;

	public CommandBlockBehavior() {
		super(Blocks.COMMAND_BLOCK);
	}

	public CommandBlockBaseLogic getLogic(EntityMinecart cart) {
		if (!logic.isPresent()) {
			logic = Optional.of(new CommandBlockBaseLogic() {

				@Override
				public void updateCommand() {
					CommandBlockBehavior.this.command = this.getCommand();
					CommandBlockBehavior.this.lastOutput = this.getLastOutput();
				}

				@SideOnly(Side.CLIENT)
				@Override
				public int getCommandBlockType() {
					return 1;
				}

				@SideOnly(Side.CLIENT)
				@Override
				public void fillInInfo(ByteBuf buf) {
					buf.writeInt(cart.getEntityId());
				}

				@Override
				public BlockPos getPosition() {
					return cart.getPosition();
				}

				@Override
				public Vec3d getPositionVector() {
					return cart.getPositionVector();
				}

				@Override
				public World getEntityWorld() {
					return cart.world;
				}

				@Override
				public Entity getCommandSenderEntity() {
					return cart;
				}

				@Override
				public MinecraftServer getServer() {
					return this.getEntityWorld().getMinecraftServer();
				}
			});
		}

		return logic.get();
	}

	@Override
	public NBTTagCompound serialize() {
		NBTTagCompound tag = super.serialize();

		if (this.logic.isPresent()) {
			this.logic.get().writeToNBT(tag);
		}

		return tag;
	}

	@Override
	public void deserialize(NBTTagCompound tag) {
		if (this.logic.isPresent()) {
			CommandBlockBaseLogic logic = this.logic.get();
			logic.readDataFromNBT(tag);
			this.command = logic.getCommand();
			this.lastOutput = logic.getLastOutput();
		}
	}

	@Override
	public boolean onActivatorRailPass(EntityMinecart cart, int x, int y, int z, boolean receivingPower) {
		if (receivingPower && cart.ticksExisted - this.activatorRailCooldown >= 4) {
			this.getLogic(cart).trigger(cart.world);
			this.activatorRailCooldown = cart.ticksExisted;
		}
		return false;
	}

	@Override
	public void activate(EntityMinecart cart, EntityPlayer player, EnumHand hand) {
		this.getLogic(cart).tryOpenEditCommandBlock(player);
	}

	@Override
	public void tickDataManager(EntityMinecart cart, DataParameter<?> key) {
		CommandBlockBaseLogic logic = this.getLogic(cart);

		if (logic.getLastOutput() != lastOutput) {
			logic.setLastOutput(lastOutput);
		}

		if (!logic.getCommand().equals(command)) {
			logic.setCommand(command);
		}
	}

	@Override
	public boolean ignoreItemEntityData(EntityMinecart cart) {
		return true;
	}
}

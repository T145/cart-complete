package T145.metaltransport.api.client;

import net.minecraft.util.math.BlockPos;

public interface IPositionedTEISR {

	BlockPos getPos();

	void setPos(BlockPos pos);
}

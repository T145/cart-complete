package T145.metaltransport.api.carts;

import java.util.HashMap;
import java.util.Map;

public class CartBehaviorRegistry {

	private static final Map<String, CartBehavior> BEHAVIORS = new HashMap<>();

	private CartBehaviorRegistry() {}

	public static void register(String blockName, CartBehavior action) {
		BEHAVIORS.put(blockName, action);
	}

	public static void register(CartBehavior behavior) {
		for (String name : behavior.getBlockNames()) {
			register(name, behavior);
		}
	}

	public static CartBehavior get(String blockName) {
		return BEHAVIORS.get(blockName);
	}

	public static boolean contains(String blockName) {
		return BEHAVIORS.containsKey(blockName);
	}
}

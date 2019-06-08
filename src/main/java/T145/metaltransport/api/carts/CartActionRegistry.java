package T145.metaltransport.api.carts;

import java.util.HashMap;
import java.util.Map;

public class CartActionRegistry {

	private static final Map<String, CartAction> ACTIONS = new HashMap<>();

	private CartActionRegistry() {}

	public static void register(String blockName, CartAction action) {
		ACTIONS.put(blockName, action);
	}

	public static CartAction get(String blockName) {
		return ACTIONS.get(blockName);
	}

	public static boolean contains(String blockName) {
		return ACTIONS.containsKey(blockName);
	}
}

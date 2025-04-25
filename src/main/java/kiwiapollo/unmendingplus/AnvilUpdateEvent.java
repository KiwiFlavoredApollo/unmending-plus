package kiwiapollo.unmendingplus;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

public class AnvilUpdateEvent {
    public interface AnvilUpdateCallback {
        ActionResult update(AnvilUpdateEvent event);
    }

    public static Event<AnvilUpdateCallback> EVENT = EventFactory.createArrayBacked(AnvilUpdateCallback.class, (listeners) -> (event) -> {
        for (AnvilUpdateCallback listener : listeners) {
            ActionResult result = listener.update(event);

            if (result != ActionResult.PASS) {
                return result;
            }
        }

        return ActionResult.PASS;
    });

    private final ItemStack left;
    private final ItemStack right;
    private ItemStack output;
    private int cost;

    public AnvilUpdateEvent(ItemStack left, ItemStack right, int cost) {
        this.left = left;
        this.right = right;
        this.output = ItemStack.EMPTY;
        this.cost = cost;
    }

    public ItemStack getLeft() {
        return left;
    }

    public ItemStack getRight() {
        return right;
    }

    public ItemStack getOutput() {
        return output;
    }

    public void setOutput(ItemStack output) {
        this.output = output;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
}

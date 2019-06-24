package world.bentobox.challenges.database.object.challenges;

import com.google.gson.annotations.Expose;
import org.bukkit.inventory.ItemStack;
import world.bentobox.bentobox.api.configuration.ConfigComment;
import world.bentobox.challenges.database.object.Challenge;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class InventoryChallenge extends Challenge
{
    public InventoryChallenge()
    {
        this.challengeType = ChallengeType.INVENTORY;
    }

    // ---------------------------------------------------------------------
    // Section: Abstract methods
    // ---------------------------------------------------------------------



    @Override
    public InventoryChallenge copy()
    {
        InventoryChallenge clone = new InventoryChallenge();
        this.populateClone(clone);

        clone.setRequiredItems(this.requiredItems.stream().map(ItemStack::clone).
            collect(Collectors.toCollection(() -> new ArrayList<>(this.requiredItems.size()))));
        clone.setTakeItems(this.takeItems);

        return clone;
    }


    // ---------------------------------------------------------------------
    // Section: Getters and Setters
    // ---------------------------------------------------------------------


    public List<ItemStack> getRequiredItems()
    {
        return this.requiredItems;
    }


    public void setRequiredItems(List<ItemStack> requiredItems)
    {
        this.requiredItems = requiredItems;
    }


    public boolean isTakeItems()
    {
        return this.takeItems;
    }


    public void setTakeItems(boolean takeItems)
    {
        this.takeItems = takeItems;
    }


    // ---------------------------------------------------------------------
    // Section: Variables
    // ---------------------------------------------------------------------


    @ConfigComment("")
    @ConfigComment("The items that must be in the inventory to complete the challenge. ")
    @ConfigComment("ItemStack List.")
    @Expose
    private List<ItemStack> requiredItems = new ArrayList<>();

    @ConfigComment("")
    @ConfigComment("Take the required items from the player")
    @Expose
    private boolean takeItems = true;
}

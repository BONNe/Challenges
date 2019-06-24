package world.bentobox.challenges.tasks.challengetask;

import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.util.Util;
import world.bentobox.challenges.ChallengesAddon;
import world.bentobox.challenges.database.object.Challenge;
import world.bentobox.challenges.database.object.challenges.InventoryChallenge;
import world.bentobox.challenges.tasks.CompleteTask;
import world.bentobox.challenges.tasks.TaskResults;
import world.bentobox.challenges.utils.Utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


public class CompleteInventoryTask extends CompleteTask<InventoryChallenge>
{
    /**
     * @param addon            - Challenges Addon.
     * @param user             - User who performs challenge.
     * @param challenge        - Challenge that should be completed.
     * @param world            - World where completion may occur.
     * @param topLabel         - Label of the top command.
     * @param permissionPrefix - Permission prefix for GameMode addon.
     */
    public CompleteInventoryTask(ChallengesAddon addon, User user, Challenge challenge, World world,
            String topLabel, String permissionPrefix)
    {
        super(addon, user, (InventoryChallenge) challenge, world, topLabel, permissionPrefix);
    }


    @Override
    protected TaskResults checkIfCanCompleteChallenge(int maxTimes)
    {
        TaskResults result = super.checkIfCanCompleteChallenge(maxTimes);

        if (result.isMeetsRequirements())
        {
            // Run through inventory
            List<ItemStack> requiredItems;

            // Players in creative game mode has got all items. No point to search for them.
            if (this.user.getPlayer().getGameMode() != GameMode.CREATIVE)
            {
                requiredItems = Utils.groupEqualItems(this.challenge.getRequiredItems());

                // Check if all required items are in players inventory.
                for (ItemStack required : requiredItems)
                {
                    int numInInventory;

                    if (Utils.canIgnoreMeta(required.getType()))
                    {
                        numInInventory =
                                Arrays.stream(this.user.getInventory().getContents()).
                                        filter(Objects::nonNull).
                                              filter(i -> i.getType().equals(required.getType())).
                                              mapToInt(ItemStack::getAmount).
                                              sum();
                    }
                    else
                    {
                        numInInventory =
                                Arrays.stream(this.user.getInventory().getContents()).
                                        filter(Objects::nonNull).
                                              filter(i -> i.isSimilar(required)).
                                              mapToInt(ItemStack::getAmount).
                                              sum();
                    }

                    if (numInInventory < required.getAmount())
                    {
                        this.user.sendMessage("challenges.errors.not-enough-items",
                                              "[items]",
                                              Util.prettifyText(required.getType().toString()));
                        return TaskResults.EMPTY_RESULT;
                    }

                    maxTimes = Math.min(maxTimes, numInInventory / required.getAmount());
                }
            }
            else
            {
                requiredItems = Collections.emptyList();
            }

            // Return the result
            result.setMeetsRequirements().setCompleteFactor(maxTimes).setRequiredItems(requiredItems);
        }

        return result;
    }


    protected void fullFillRequirements(TaskResults result)
    {

        // If remove items, then remove them
        if (this.challenge.isTakeItems())
        {
            int sumEverything = result.requiredItems.stream().
                    mapToInt(itemStack -> itemStack.getAmount() * result.getFactor()).
                                                            sum();

            Map<ItemStack, Integer> removedItems =
                    this.removeItems(result.requiredItems, result.getFactor());

            int removedAmount = removedItems.values().stream().mapToInt(num -> num).sum();

            // Something is not removed.
            if (sumEverything != removedAmount)
            {
                this.user.sendMessage("challenges.errors.cannot-remove-items");

                result.removedItems = removedItems;
                result.meetsRequirements = false;
            }
        }
    }


    /**
     * Removes items from a user's inventory
     * @param requiredItemList - a list of item stacks to be removed
     * @param factor - factor for required items.
     */
    private Map<ItemStack, Integer> removeItems(List<ItemStack> requiredItemList, int factor)
    {
        Map<ItemStack, Integer> removed = new HashMap<>();

        for (ItemStack required : requiredItemList)
        {
            int amountToBeRemoved = required.getAmount() * factor;

            List<ItemStack> itemsInInventory;

            if (Utils.canIgnoreMeta(required.getType()))
            {
                // Use collecting method that ignores item meta.
                itemsInInventory = Arrays.stream(user.getInventory().getContents()).
                        filter(Objects::nonNull).
                                                 filter(i -> i.getType().equals(required.getType())).
                                                 collect(Collectors.toList());
            }
            else
            {
                // Use collecting method that compares item meta.
                itemsInInventory = Arrays.stream(user.getInventory().getContents()).
                        filter(Objects::nonNull).
                                                 filter(i -> i.isSimilar(required)).
                                                 collect(Collectors.toList());
            }

            for (ItemStack itemStack : itemsInInventory)
            {
                if (amountToBeRemoved > 0)
                {
                    ItemStack dummy = itemStack.clone();
                    dummy.setAmount(1);

                    // Remove either the full amount or the remaining amount
                    if (itemStack.getAmount() >= amountToBeRemoved)
                    {
                        itemStack.setAmount(itemStack.getAmount() - amountToBeRemoved);
                        removed.merge(dummy, amountToBeRemoved, Integer::sum);
                        amountToBeRemoved = 0;
                    }
                    else
                    {
                        removed.merge(dummy, itemStack.getAmount(), Integer::sum);
                        amountToBeRemoved -= itemStack.getAmount();
                        itemStack.setAmount(0);
                    }
                }
            }

            if (amountToBeRemoved > 0)
            {
                this.addon.logError("Could not remove " + amountToBeRemoved + " of " + required.getType() +
                                    " from player's inventory!");
            }
        }

        return removed;
    }
}

package world.bentobox.challenges.tasks;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import world.bentobox.challenges.database.object.Challenge;

import java.util.List;
import java.util.Map;
import java.util.Queue;


public class TaskResults
{
    /**
     * This method sets that challenge meets all requirements at least once.
     * @return Current object.
     */
    public TaskResults setMeetsRequirements()
    {
        this.meetsRequirements = true;
        return this;
    }


    /**
     * Method sets that challenge is completed once already
     * @param completed boolean that indicate that challenge has been already completed.
     * @return Current object.
     */
    public TaskResults setCompleted(boolean completed)
    {
        this.completed = completed;
        return this;
    }


    /**
     * Method sets how many times challenge can be completed.
     * @param factor Integer that represents completion count.
     * @return Current object.
     */
    public TaskResults setCompleteFactor(int factor)
    {
        this.factor = factor;
        return this;
    }


    // ---------------------------------------------------------------------
    // Section: Requirement memory
    // ---------------------------------------------------------------------


    /**
     * Method sets requiredItems for inventory challenge.
     * @param requiredItems items that are required by inventory challenge.
     * @return Current object.
     */
    public TaskResults setRequiredItems(List<ItemStack> requiredItems)
    {
        this.requiredItems = requiredItems;
        return this;
    }


    /**
     * Method sets queue that contains all blocks with required material type.
     * @param blocks queue that contains required materials from world.
     * @return Current object.
     */
    public TaskResults setBlockQueue(Queue<Block> blocks)
    {
        this.blocks = blocks;
        return this;
    }

    /**
     * Method sets queue that contains all entities with required entity type.
     * @param entities queue that contains required entities from world.
     * @return Current object.
     */
    public TaskResults setEntityQueue(Queue<Entity> entities)
    {
        this.entities = entities;
        return this;
    }


    // ---------------------------------------------------------------------
    // Section: Getters
    // ---------------------------------------------------------------------


    /**
     * Returns value of was completed variable.
     * @return value of completed variable
     */
    public boolean wasCompleted()
    {
        return this.completed;
    }


    /**
     * This method returns how many times challenge can be completed.
     * @return completion count.
     */
    public int getFactor()
    {
        return this.factor;
    }


    /**
     * This method returns if challenge requirements has been met at least once.
     * @return value of meets requirements variable.
     */
    public boolean isMeetsRequirements()
    {
        return this.meetsRequirements;
    }


    // ---------------------------------------------------------------------
    // Section: Variables
    // ---------------------------------------------------------------------


    /**
     * Boolean that indicate that challenge has already bean completed once before.
     */
    private boolean completed;

    /**
     * Indicates that challenge can be completed.
     */
    public boolean meetsRequirements;

    /**
     * Integer that represents how many times challenge is completed
     */
    private int factor;

    /**
     * List that contains required items for Inventory Challenge
     * Necessary as it contains grouped items by type or similarity, not by limit 64.
     */
    public List<ItemStack> requiredItems;

    /**
     * Map that contains removed items and their removed count.
     */
    public Map<ItemStack, Integer> removedItems = null;

    /**
     * Queue of blocks that contains all blocks with the same type as requiredBlock from
     * challenge requirements.
     */
    public Queue<Block> blocks;

    /**
     * Queue of entities that contains all entities with the same type as requiredEntities from
     * challenge requirements.
     */
    public Queue<Entity> entities;


    /**
     * Variable that will be used to avoid multiple empty object generation.
     */
    public static final TaskResults EMPTY_RESULT = new TaskResults();
}
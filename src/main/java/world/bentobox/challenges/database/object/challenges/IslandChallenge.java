package world.bentobox.challenges.database.object.challenges;

import com.google.gson.annotations.Expose;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import world.bentobox.bentobox.api.configuration.ConfigComment;
import world.bentobox.challenges.database.object.Challenge;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;


public class IslandChallenge extends Challenge
{
    public IslandChallenge()
    {
        this.challengeType = ChallengeType.ISLAND;
    }

    // ---------------------------------------------------------------------
    // Section: Constructors
    // ---------------------------------------------------------------------


    // ---------------------------------------------------------------------
    // Section: Abstract methods
    // ---------------------------------------------------------------------


    @Override
    public IslandChallenge copy()
    {
        IslandChallenge clone = new IslandChallenge();
        this.populateClone(clone);

        clone.setRequiredBlocks(new HashMap<>(this.requiredBlocks));
        clone.setRemoveBlocks(this.removeBlocks);
        clone.setRequiredEntities(new HashMap<>(this.requiredEntities));
        clone.setRemoveEntities(this.removeEntities);
        clone.setSearchRadius(this.searchRadius);

        return clone;
    }


    // ---------------------------------------------------------------------
    // Section: Getters and Setters
    // ---------------------------------------------------------------------


    public Map<Material, Integer> getRequiredBlocks()
    {
        return this.requiredBlocks;
    }


    public void setRequiredBlocks(Map<Material, Integer> requiredBlocks)
    {
        this.requiredBlocks = requiredBlocks;
    }


    public boolean isRemoveBlocks()
    {
        return this.removeBlocks;
    }


    public void setRemoveBlocks(boolean removeBlocks)
    {
        this.removeBlocks = removeBlocks;
    }


    public Map<EntityType, Integer> getRequiredEntities()
    {
        return this.requiredEntities;
    }


    public void setRequiredEntities(Map<EntityType, Integer> requiredEntities)
    {
        this.requiredEntities = requiredEntities;
    }


    public boolean isRemoveEntities()
    {
        return this.removeEntities;
    }


    public void setRemoveEntities(boolean removeEntities)
    {
        this.removeEntities = removeEntities;
    }


    public int getSearchRadius()
    {
        return this.searchRadius;
    }


    public void setSearchRadius(int searchRadius)
    {
        this.searchRadius = searchRadius;
    }


    // ---------------------------------------------------------------------
    // Section: Variables
    // ---------------------------------------------------------------------


    @ConfigComment("")
    @ConfigComment("This is a map of the blocks required in a ISLAND challenge. Material,")
    @ConfigComment("Integer")
    @Expose
    private Map<Material, Integer> requiredBlocks = new EnumMap<>(Material.class);

    @ConfigComment("")
    @ConfigComment("Remove the required blocks from the island")
    @Expose
    private boolean removeBlocks;

    @ConfigComment("")
    @ConfigComment("Any entities that must be in the area for ISLAND type challenges. ")
    @ConfigComment("Map EntityType, Number")
    @Expose
    private Map<EntityType, Integer> requiredEntities = new EnumMap<>(EntityType.class);

    @ConfigComment("")
    @ConfigComment("Remove the entities from the island")
    @Expose
    private boolean removeEntities;

    @ConfigComment("")
    @ConfigComment("The number of blocks around the player to search for items on an island")
    @Expose
    private int searchRadius = 10;
}

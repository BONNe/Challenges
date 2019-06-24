package world.bentobox.challenges.tasks.challengetask;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.BoundingBox;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.util.Util;
import world.bentobox.challenges.ChallengesAddon;
import world.bentobox.challenges.database.object.Challenge;
import world.bentobox.challenges.database.object.challenges.IslandChallenge;
import world.bentobox.challenges.tasks.CompleteTask;
import world.bentobox.challenges.tasks.TaskResults;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;


public class CompleteIslandTask extends CompleteTask<IslandChallenge>
{
    /**
     * @param addon            - Challenges Addon.
     * @param user             - User who performs challenge.
     * @param challenge        - Challenge that should be completed.
     * @param world            - World where completion may occur.
     * @param topLabel         - Label of the top command.
     * @param permissionPrefix - Permission prefix for GameMode addon.
     */
    public CompleteIslandTask(ChallengesAddon addon, User user, Challenge challenge,
            World world,
            String topLabel, String permissionPrefix)
    {
        super(addon, user, (IslandChallenge) challenge, world, topLabel, permissionPrefix);
    }


    // check requriements


    @Override
    protected TaskResults checkIfCanCompleteChallenge(int maxTimes)
    {
        TaskResults result = super.checkIfCanCompleteChallenge(maxTimes);

        if (result.isMeetsRequirements())
        {
            // Init location in player position.
            BoundingBox boundingBox = this.user.getPlayer().getBoundingBox().clone();

            // Expand position with search radius.
            boundingBox.expand(this.challenge.getSearchRadius());

            if (ChallengesAddon.CHALLENGES_WORLD_PROTECTION.isSetForWorld(this.world))
            {
                // Players should not be able to complete challenge if they stay near island with required blocks.

                Island island = this.addon.getIslands().getIsland(this.world, this.user);

                if (boundingBox.getMinX() < island.getMinX())
                {
                    boundingBox.expand(BlockFace.EAST, Math.abs(island.getMinX() - boundingBox.getMinX()));
                }

                if (boundingBox.getMinZ() < island.getMinZ())
                {
                    boundingBox.expand(BlockFace.NORTH, Math.abs(island.getMinZ() - boundingBox.getMinZ()));
                }

                int range = island.getRange();

                int islandMaxX = island.getMinX() + range * 2;
                int islandMaxZ = island.getMinZ() + range * 2;

                if (boundingBox.getMaxX() > islandMaxX)
                {
                    boundingBox.expand(BlockFace.WEST, Math.abs(boundingBox.getMaxX() - islandMaxX));
                }

                if (boundingBox.getMaxZ() > islandMaxZ)
                {
                    boundingBox.expand(BlockFace.SOUTH, Math.abs(boundingBox.getMaxZ() - islandMaxZ));
                }
            }

            result = this.searchForEntities(this.challenge.getRequiredEntities(), maxTimes, boundingBox);

            if (result.isMeetsRequirements() && !this.challenge.getRequiredBlocks().isEmpty())
            {
                // Search for items only if entities found
                result = this.searchForBlocks(this.challenge.getRequiredBlocks(), result.getFactor(), boundingBox);
            }

        }

        return result;
    }


    /**
     * This method search required blocks in given challenge boundingBox.
     * @param requiredMap RequiredBlock Map.
     * @param factor - requirement multilayer.
     * @param boundingBox Bounding box of island challenge
     * @return TaskResults
     */
    private TaskResults searchForBlocks(Map<Material, Integer> requiredMap, int factor, BoundingBox boundingBox)
    {
        if (requiredMap.isEmpty())
        {
            return new TaskResults().setMeetsRequirements().setCompleteFactor(factor);
        }

        Map<Material, Integer> blocks = new EnumMap<>(requiredMap);
        Map<Material, Integer> blocksFound = new HashMap<>(requiredMap.size());

        // This queue will contain only blocks whit required type ordered by distance till player.
        Queue<Block> blockFromWorld = new PriorityQueue<>((o1, o2) -> {
            if (o1.getType().equals(o2.getType()))
            {
                return Double.compare(o1.getLocation().distance(this.user.getLocation()),
                                      o2.getLocation().distance(this.user.getLocation()));
            }
            else
            {
                return o1.getType().compareTo(o2.getType());
            }
        });

        for (int x = (int) boundingBox.getMinX(); x <= boundingBox.getMaxX(); x++)
        {
            for (int y = (int) boundingBox.getMinY(); y <= boundingBox.getMaxY(); y++)
            {
                for (int z = (int) boundingBox.getMinZ(); z <= boundingBox.getMaxZ(); z++)
                {
                    Block block = this.user.getWorld().getBlockAt(x, y, z);

                    if (requiredMap.containsKey(block.getType()))
                    {
                        blockFromWorld.add(block);

                        blocksFound.putIfAbsent(block.getType(), 1);
                        blocksFound.computeIfPresent(block.getType(), (reqEntity, amount) -> amount + 1);

                        // Remove one
                        blocks.computeIfPresent(block.getType(), (b, amount) -> amount - 1);
                        // Remove any that have an amount of 0
                        blocks.entrySet().removeIf(en -> en.getValue() <= 0);

                        if (blocks.isEmpty() && factor == 1)
                        {
                            // Return as soon as it s empty as no point to search more.
                            return new TaskResults().setMeetsRequirements().setCompleteFactor(factor).setBlockQueue(blockFromWorld);
                        }
                    }
                }
            }
        }

        if (blocks.isEmpty())
        {
            if (factor > 1)
            {
                // Calculate minimal completion count.

                for (Map.Entry<Material, Integer> entry : blocksFound.entrySet())
                {
                    factor = Math.min(factor,
                                      entry.getValue() / requiredMap.get(entry.getKey()));
                }
            }

            // kick garbage collector
            blocksFound.clear();

            return new TaskResults().setMeetsRequirements().setCompleteFactor(factor).setBlockQueue(blockFromWorld);
        }

        this.user.sendMessage("challenges.errors.not-close-enough", "[number]", String.valueOf(this.challenge.getSearchRadius()));

        blocks.forEach((k, v) -> user.sendMessage("challenges.errors.you-still-need",
                                                  "[amount]", String.valueOf(v),
                                                  "[item]", Util.prettifyText(k.toString())));


        // kick garbage collector
        blocks.clear();
        blocksFound.clear();
        blockFromWorld.clear();

        return TaskResults.EMPTY_RESULT;
    }


    /**
     * This method search required entities in given radius from user position and entity is inside boundingBox.
     * @param requiredMap RequiredEntities Map.
     * @param factor - requirements multiplier.
     * @param boundingBox Bounding box of island challenge
     * @return TaskResults
     */
    private TaskResults searchForEntities(Map<EntityType, Integer> requiredMap,
            int factor,
            BoundingBox boundingBox)
    {
        if (requiredMap.isEmpty())
        {
            return new TaskResults().setMeetsRequirements().setCompleteFactor(factor);
        }

        // Collect all entities that could be removed.
        Map<EntityType, Integer> entitiesFound = new HashMap<>();
        Map<EntityType, Integer> minimalRequirements = new EnumMap<>(requiredMap);

        // Create queue that contains all required entities ordered by distance till player.
        Queue<Entity> entityQueue = new PriorityQueue<>((o1, o2) -> {
            if (o1.getType().equals(o2.getType()))
            {
                return Double.compare(o1.getLocation().distance(this.user.getLocation()),
                                      o2.getLocation().distance(this.user.getLocation()));
            }
            else
            {
                return o1.getType().compareTo(o2.getType());
            }
        });

        this.world.getNearbyEntities(boundingBox).forEach(entity -> {
            // Check if entity is inside challenge bounding box
            if (requiredMap.containsKey(entity.getType()))
            {
                entityQueue.add(entity);

                entitiesFound.putIfAbsent(entity.getType(), 1);
                entitiesFound.computeIfPresent(entity.getType(), (reqEntity, amount) -> amount + 1);

                // Look through all the nearby Entities, filtering by type
                minimalRequirements.computeIfPresent(entity.getType(), (reqEntity, amount) -> amount - 1);
                minimalRequirements.entrySet().removeIf(e -> e.getValue() == 0);
            }
        });

        if (minimalRequirements.isEmpty())
        {
            if (factor > 1)
            {
                // Calculate minimal completion count.

                for (Map.Entry<EntityType, Integer> entry : entitiesFound.entrySet())
                {
                    factor = Math.min(factor,
                                      entry.getValue() / requiredMap.get(entry.getKey()));
                }
            }

            // Kick garbage collector
            entitiesFound.clear();

            return new TaskResults().setMeetsRequirements().setCompleteFactor(factor).setEntityQueue(entityQueue);
        }

        minimalRequirements.forEach((reqEnt, amount) -> this.user.sendMessage("challenges.errors.you-still-need",
                                                                              "[amount]", String.valueOf(amount),
                                                                              "[item]", Util.prettifyText(reqEnt.toString())));

        // Kick garbage collector
        entitiesFound.clear();
        minimalRequirements.clear();
        entityQueue.clear();

        return TaskResults.EMPTY_RESULT;
    }


    // Fullfill rewuairements

    protected void fullFillRequirements(TaskResults result)
    {
        if (result.isMeetsRequirements() &&
            this.challenge.isRemoveEntities() &&
            !this.challenge.getRequiredEntities().isEmpty())
        {
            this.removeEntities(result.entities, result.getFactor());
        }

        if (result.isMeetsRequirements() &&
            this.challenge.isRemoveBlocks() &&
            !this.challenge.getRequiredBlocks().isEmpty())
        {
            this.removeBlocks(result.blocks, result.getFactor());
        }
    }


    /**
     * This method removes required block and set air instead of it.
     * @param blockQueue Queue with blocks that could be removed
     * @param factor requirement factor for each block type.
     */
    private void removeBlocks(Queue<Block> blockQueue, int factor)
    {
        Map<Material, Integer>
                blocks = new EnumMap<Material, Integer>(this.challenge.getRequiredBlocks());

        // Increase required blocks by factor.
        blocks.entrySet().forEach(entry -> entry.setValue(entry.getValue() * factor));

        blockQueue.forEach(block -> {
            if (blocks.containsKey(block.getType()))
            {
                blocks.computeIfPresent(block.getType(), (b, amount) -> amount - 1);
                blocks.entrySet().removeIf(en -> en.getValue() <= 0);

                block.setType(Material.AIR);
            }
        });
    }


    /**
     * This method removes required entities.
     * @param entityQueue Queue with entities that could be removed
     * @param factor requirement factor for each entity type.
     */
    private void removeEntities(Queue<Entity> entityQueue, int factor)
    {
        Map<EntityType, Integer> entities = this.challenge.getRequiredEntities().isEmpty() ?
                new EnumMap<>(EntityType.class) : new EnumMap<EntityType, Integer>(this.challenge.getRequiredEntities());

        // Increase required entities by factor.
        entities.entrySet().forEach(entry -> entry.setValue(entry.getValue() * factor));

        // Go through entity queue and remove entities that are requried.
        entityQueue.forEach(entity -> {
            if (entities.containsKey(entity.getType()))
            {
                entities.computeIfPresent(entity.getType(), (reqEntity, amount) -> amount - 1);
                entities.entrySet().removeIf(e -> e.getValue() == 0);
                entity.remove();
            }
        });
    }
}

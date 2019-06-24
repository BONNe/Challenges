/**
 *
 */
package world.bentobox.challenges.tasks;



import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.util.Util;
import world.bentobox.challenges.ChallengesAddon;
import world.bentobox.challenges.ChallengesManager;
import world.bentobox.challenges.database.object.Challenge;
import world.bentobox.challenges.database.object.ChallengeLevel;
import world.bentobox.challenges.tasks.challengetask.CompleteInventoryTask;
import world.bentobox.challenges.tasks.challengetask.CompleteIslandTask;
import world.bentobox.challenges.tasks.challengetask.CompleteSimpleTask;


/**
 * Run when a user tries to complete a challenge
 * @author tastybento
 *
 */
public abstract class CompleteTask<T extends Challenge>
{
// ---------------------------------------------------------------------
// Section: Variables
// ---------------------------------------------------------------------

    /**
     * Challenges addon variable.
     */
    protected ChallengesAddon addon;

    /**
     * Challenges manager for addon.
     */
    protected ChallengesManager manager;

    /**
     * World where all checks are necessary.
     */
    protected World world;

    /**
     * User who is completing challenge.
     */
    protected User user;

    /**
     * Permission prefix string.
     */
    private String permissionPrefix;

    /**
     * Top command first label.
     */
    private String topLabel;

    /**
     * Challenge that should be completed.
     */
    protected T challenge;


// ---------------------------------------------------------------------
// Section: Constructor
// ---------------------------------------------------------------------


    /**
     * @param addon - Challenges Addon.
     * @param user - User who performs challenge.
     * @param challenge - Challenge that should be completed.
     * @param world - World where completion may occur.
     * @param topLabel - Label of the top command.
     * @param permissionPrefix - Permission prefix for GameMode addon.
     */
    public CompleteTask(ChallengesAddon addon,
        User user,
        T challenge,
        World world,
        String topLabel,
        String permissionPrefix)
    {
        this.addon = addon;
        this.world = world;
        this.permissionPrefix = permissionPrefix;
        this.user = user;
        this.manager = addon.getChallengesManager();
        // To avoid any modifications that may accure to challenges in current completion
        // just copy it.
        this.challenge = (T) challenge.copy();
        this.topLabel = topLabel;
    }


    /**
     * This static method allows complete challenge and get result about completion.
     * @param addon - Challenges Addon.
     * @param user - User who performs challenge.
     * @param challenge - Challenge that should be completed.
     * @param world - World where completion may occur.
     * @param topLabel - Label of the top command.
     * @param permissionPrefix - Permission prefix for GameMode addon.
     * @return true, if challenge is completed, otherwise false.
     */
    public static boolean complete(ChallengesAddon addon,
        User user,
        Challenge challenge,
        World world,
        String topLabel,
        String permissionPrefix)
    {
        return CompleteTask.complete(addon, user, challenge, world, topLabel, permissionPrefix, 1);
    }


    /**
     * This static method allows complete challenge and get result about completion.
     * @param addon - Challenges Addon.
     * @param user - User who performs challenge.
     * @param challenge - Challenge that should be completed.
     * @param world - World where completion may occur.
     * @param topLabel - Label of the top command.
     * @param permissionPrefix - Permission prefix for GameMode addon.
     * @param maxTimes - Integer that represents how many times user wants to complete challenges.
     * @return true, if challenge is completed, otherwise false.
     */
    public static boolean complete(ChallengesAddon addon,
        User user,
        Challenge challenge,
        World world,
        String topLabel,
        String permissionPrefix,
        int maxTimes)
    {
        boolean completed;

        switch (challenge.getChallengeType())
        {
            case INVENTORY:
                completed = new CompleteInventoryTask(addon, user, challenge, world, topLabel, permissionPrefix).build(maxTimes).meetsRequirements;
                break;
            case ISLAND:
                completed = new CompleteIslandTask(addon, user, challenge, world, topLabel, permissionPrefix).build(maxTimes).meetsRequirements;
                break;
            case EVENT:
                completed = true;
                break;
            case OTHER:
            case SIMPLE:
                completed = new CompleteSimpleTask(addon, user, challenge, world, topLabel, permissionPrefix).build(maxTimes).meetsRequirements;
                break;
            default:
                completed = false;
                break;
        }

        return completed;
    }


// ---------------------------------------------------------------------
// Section: Methods
// ---------------------------------------------------------------------


    /**
     * This method checks if challenge can be done, and complete it, if it is possible.
     * @return ChallengeResult object, that contains completion status.
     */
    public TaskResults build(int maxTimes)
    {
        // Check if can complete challenge
        TaskResults result = this.checkIfCanCompleteChallenge(maxTimes);

        if (!result.isMeetsRequirements())
        {
            return result;
        }

        this.fullFillRequirements(result);

        // Validation to avoid rewarding if something goes wrong in removing requirements.

        if (!result.isMeetsRequirements())
        {
            if (result.removedItems != null)
            {
                result.removedItems.forEach((item, amount) ->
                {
                    ItemStack returnItem = item.clone();
                    returnItem.setAmount(amount);

                    this.user.getInventory().addItem(returnItem).forEach((k, v) ->
                        this.user.getWorld().dropItem(this.user.getLocation(), v));
                });
            }

            // Entities and blocks will not be restored.

            return result;
        }

        // If challenge was not completed then reward items for completing it first time.
        if (!result.wasCompleted())
        {
            // Item rewards
            for (ItemStack reward : this.challenge.getRewardItems())
            {
                // Clone is necessary because otherwise it will chane reward itemstack
                // amount.
                this.user.getInventory().addItem(reward.clone()).forEach((k, v) ->
                    this.user.getWorld().dropItem(this.user.getLocation(), v));
            }

            // Money Reward
            if (this.addon.isEconomyProvided())
            {
                this.addon.getEconomyProvider().deposit(this.user, this.challenge.getRewardMoney());
            }

            // Experience Reward
            this.user.getPlayer().giveExp(this.challenge.getRewardExperience());

            // Run commands
            this.runCommands(this.challenge.getRewardCommands());

            // Send message about first completion only if it is completed only once.
            if (result.getFactor() == 1)
            {
                this.user.sendMessage("challenges.messages.you-completed-challenge", "[value]", this.challenge.getFriendlyName());
            }

            if (this.addon.getChallengesSettings().isBroadcastMessages())
            {
                for (Player player : this.addon.getServer().getOnlinePlayers())
                {
                    // Only other players should see message.
                    if (!player.getUniqueId().equals(this.user.getUniqueId()))
                    {
                        User.getInstance(player).sendMessage("challenges.messages.name-has-completed-challenge",
                            "[name]", this.user.getName(),
                            "[value]", this.challenge.getFriendlyName());
                    }
                }
            }

            // sends title to player on challenge completion
            if (this.addon.getChallengesSettings().isShowCompletionTitle())
            {
                this.user.getPlayer().sendTitle(
                    this.parseChallenge(this.user.getTranslation("challenges.titles.challenge-title"), this.challenge),
                    this.parseChallenge(this.user.getTranslation("challenges.titles.challenge-subtitle"), this.challenge),
                    10,
                    this.addon.getChallengesSettings().getTitleShowtime(),
                    20);
            }
        }

        if (result.wasCompleted() || result.getFactor() > 1)
        {
            int rewardFactor = result.getFactor() - (result.wasCompleted() ? 0 : 1);

            // Item Repeat Rewards
            for (ItemStack reward : this.challenge.getRepeatItemReward())
            {
                // Clone is necessary because otherwise it will chane reward itemstack
                // amount.

                for (int i = 0; i < rewardFactor; i++)
                {
                    this.user.getInventory().addItem(reward.clone()).forEach((k, v) ->
                        this.user.getWorld().dropItem(this.user.getLocation(), v));
                }
            }

            // Money Repeat Reward
            if (this.addon.isEconomyProvided())
            {
                this.addon.getEconomyProvider().deposit(this.user,
                    this.challenge.getRepeatMoneyReward() * rewardFactor);
            }

            // Experience Repeat Reward
            this.user.getPlayer().giveExp(
                this.challenge.getRepeatExperienceReward() * rewardFactor);

            // Run commands
            for (int i = 0; i < rewardFactor; i++)
            {
                this.runCommands(this.challenge.getRepeatRewardCommands());
            }

            if (result.getFactor() > 1)
            {
                this.user.sendMessage("challenges.messages.you-repeated-challenge-multiple",
                    "[value]", this.challenge.getFriendlyName(),
                    "[count]", Integer.toString(result.getFactor()));
            }
            else
            {
                this.user.sendMessage("challenges.messages.you-repeated-challenge", "[value]", this.challenge.getFriendlyName());
            }
        }

        // Mark as complete
        this.manager.setChallengeComplete(this.user, this.world, this.challenge, result.getFactor());

        // Check level completion for non-free challenges
        if (!result.wasCompleted() &&
            !this.challenge.getLevel().equals(ChallengesManager.FREE))
        {
            ChallengeLevel level = this.manager.getLevel(this.challenge);

            if (level != null && !this.manager.isLevelCompleted(this.user, this.world, level))
            {
                if (this.manager.validateLevelCompletion(this.user, this.world, level))
                {
                    // Item rewards
                    for (ItemStack reward : level.getRewardItems())
                    {
                        // Clone is necessary because otherwise it will chane reward itemstack
                        // amount.
                        this.user.getInventory().addItem(reward.clone()).forEach((k, v) ->
                            this.user.getWorld().dropItem(this.user.getLocation(), v));
                    }

                    // Money Reward
                    if (this.addon.isEconomyProvided())
                    {
                        this.addon.getEconomyProvider().deposit(this.user, level.getRewardMoney());
                    }

                    // Experience Reward
                    this.user.getPlayer().giveExp(level.getRewardExperience());

                    // Run commands
                    this.runCommands(level.getRewardCommands());

                    this.user.sendMessage("challenges.messages.you-completed-level", "[value]", level.getFriendlyName());

                    if (this.addon.getChallengesSettings().isBroadcastMessages())
                    {
                        for (Player player : this.addon.getServer().getOnlinePlayers())
                        {
                            // Only other players should see message.
                            if (!player.getUniqueId().equals(this.user.getUniqueId()))
                            {
                                User.getInstance(player).sendMessage("challenges.messages.name-has-completed-level",
                                    "[name]", this.user.getName(), "[value]", level.getFriendlyName());
                            }
                        }
                    }

                    this.manager.setLevelComplete(this.user, this.world, level);

                    // sends title to player on level completion
                    if (this.addon.getChallengesSettings().isShowCompletionTitle())
                    {
                        this.user.getPlayer().sendTitle(
                            this.parseLevel(this.user.getTranslation("challenges.titles.level-title"), level),
                            this.parseLevel(this.user.getTranslation("challenges.titles.level-subtitle"), level),
                            10,
                            this.addon.getChallengesSettings().getTitleShowtime(),
                            20);
                    }
                }
            }
        }

        return result;
    }


    /**
     * This method full fills all challenge type requirements, that is not full filled yet.
     * @param result Challenge Results
     */
    protected void fullFillRequirements(TaskResults result)
    {
    }


    /**
     * Checks if a challenge can be completed or not
     * It returns ChallengeResult.
     * @param maxTimes - times that user wanted to complete
     */
    protected TaskResults checkIfCanCompleteChallenge(int maxTimes)
    {
        TaskResults result;

        // Check the world
        if (!this.challenge.isDeployed())
        {
            this.user.sendMessage("challenges.errors.not-deployed");
            result = TaskResults.EMPTY_RESULT;
        }
        else if (maxTimes < 1)
        {
            this.user.sendMessage("challenges.errors.not-valid-integer");
            result = TaskResults.EMPTY_RESULT;
        }
        else if (Util.getWorld(this.world) != Util.getWorld(this.user.getWorld()) ||
            !this.challenge.getUniqueId().startsWith(Util.getWorld(this.world).getName()))
        {
            this.user.sendMessage("general.errors.wrong-world");
            result = TaskResults.EMPTY_RESULT;
        }
        // Player is not on island
        else if (ChallengesAddon.CHALLENGES_WORLD_PROTECTION.isSetForWorld(this.world) &&
            !this.addon.getIslands().userIsOnIsland(this.user.getWorld(), this.user))
        {
            this.user.sendMessage("challenges.errors.not-on-island");
            result = TaskResults.EMPTY_RESULT;
        }
        // Check player permission
        else if (!this.addon.getIslands().getIslandAt(this.user.getLocation()).
            map(i -> i.isAllowed(this.user, ChallengesAddon.CHALLENGES_ISLAND_PROTECTION)).
            orElse(false))
        {
            this.user.sendMessage("challenges.errors.no-rank");
            result = TaskResults.EMPTY_RESULT;
        }
        // Check if user has unlocked challenges level.
        else if (!this.challenge.getLevel().equals(ChallengesManager.FREE) &&
            !this.manager.isLevelUnlocked(this.user, this.world, this.manager.getLevel(this.challenge.getLevel())))
        {
            this.user.sendMessage("challenges.errors.challenge-level-not-available");
            result = TaskResults.EMPTY_RESULT;
        }
        // Check max times
        else if (this.challenge.isRepeatable() && this.challenge.getMaxTimes() > 0 &&
            this.manager.getChallengeTimes(this.user, this.world, this.challenge) >= this.challenge.getMaxTimes())
        {
            this.user.sendMessage("challenges.errors.not-repeatable");
            result = TaskResults.EMPTY_RESULT;
        }
        // Check repeatability
        else if (!this.challenge.isRepeatable() && this.manager.isChallengeComplete(this.user, this.world, this.challenge))
        {
            this.user.sendMessage("challenges.errors.not-repeatable");
            result = TaskResults.EMPTY_RESULT;
        }
        // Check environment
        else if (!this.challenge.getEnvironment().isEmpty() &&
            !this.challenge.getEnvironment().contains(this.user.getWorld().getEnvironment()))
        {
            this.user.sendMessage("challenges.errors.wrong-environment");
            result = TaskResults.EMPTY_RESULT;
        }
        // Check permission
        else if (!this.checkPermissions())
        {
            this.user.sendMessage("general.errors.no-permission");
            result = TaskResults.EMPTY_RESULT;
        }
        else
        {
            result = new TaskResults();
            result.meetsRequirements = true;
            result.setCompleted(this.manager.isChallengeComplete(this.user, this.world, this.challenge));
        }

        // Everything fails till this point.
        return result;
    }


    /**
     * This method checks if user has all required permissions.
     * @return true if user has all required permissions, otherwise false.
     */
    private boolean checkPermissions()
    {
        return this.challenge.getRequiredPermissions().isEmpty() ||
            this.challenge.getRequiredPermissions().stream().allMatch(s -> this.user.hasPermission(s));
    }


    /**
     * This method runs all commands from command list.
     * @param commands List of commands that must be performed.
     */
    private void runCommands(List<String> commands)
    {
        // Ignore commands with this perm
        if (user.hasPermission(this.permissionPrefix + "command.challengeexempt") && !user.isOp())
        {
            return;
        }
        for (String cmd : commands)
        {
            if (cmd.startsWith("[SELF]"))
            {
                String alert = "Running command '" + cmd + "' as " + this.user.getName();
                this.addon.getLogger().info(alert);
                cmd = cmd.substring(6, cmd.length()).replace("[player]", this.user.getName()).trim();
                try
                {
                    if (!user.performCommand(cmd))
                    {
                        this.showError(cmd);
                    }
                }
                catch (Exception e)
                {
                    this.showError(cmd);
                }

                continue;
            }
            // Substitute in any references to player
            try
            {
                if (!this.addon.getServer().dispatchCommand(this.addon.getServer().getConsoleSender(),
                    cmd.replace("[player]", this.user.getName())))
                {
                    this.showError(cmd);
                }
            }
            catch (Exception e)
            {
                this.showError(cmd);
            }
        }
    }


    /**
     * Throws error message.
     * @param cmd Error message that appear after failing some command.
     */
    private void showError(final String cmd)
    {
        this.addon.getLogger().severe("Problem executing command executed by player - skipping!");
        this.addon.getLogger().severe(() -> "Command was : " + cmd);
    }


// ---------------------------------------------------------------------
// Section: Title parsings
// ---------------------------------------------------------------------


    /**
     * This method pareses input message by replacing all challenge variables in [] with their values.
     * @param inputMessage inputMessage string
     * @param challenge Challenge from which these values should be taken
     * @return new String that replaces [VALUE] with correct value from challenge.
     */
    private String parseChallenge(String inputMessage, Challenge challenge)
    {
        String outputMessage = inputMessage;

        if (inputMessage.contains("[") && inputMessage.contains("]"))
        {
            outputMessage = outputMessage.replace("[friendlyName]", challenge.getFriendlyName());
            outputMessage = outputMessage.replace("[level]", challenge.getLevel().isEmpty() ? "" : this.manager.getLevel(challenge.getLevel()).getFriendlyName());
            outputMessage = outputMessage.replace("[rewardText]", challenge.getRewardText());
        }

        return ChatColor.translateAlternateColorCodes('&', outputMessage);
    }


    /**
     * This method pareses input message by replacing all level variables in [] with their values.
     * @param inputMessage inputMessage string
     * @param level level from which these values should be taken
     * @return new String that replaces [VALUE] with correct value from level.
     */
    private String parseLevel(String inputMessage, ChallengeLevel level)
    {
        String outputMessage = inputMessage;

        if (inputMessage.contains("[") && inputMessage.contains("]"))
        {
            outputMessage = outputMessage.replace("[friendlyName]", level.getFriendlyName());
            outputMessage = outputMessage.replace("[rewardText]", level.getRewardText());
        }

        return ChatColor.translateAlternateColorCodes('&', outputMessage);
    }
}

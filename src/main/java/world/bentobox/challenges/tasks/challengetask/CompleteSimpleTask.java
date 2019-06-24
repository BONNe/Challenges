package world.bentobox.challenges.tasks.challengetask;

import org.bukkit.GameMode;
import org.bukkit.World;
import world.bentobox.bentobox.api.localization.TextVariables;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.challenges.ChallengesAddon;
import world.bentobox.challenges.database.object.Challenge;
import world.bentobox.challenges.database.object.challenges.SimpleChallenge;
import world.bentobox.challenges.tasks.CompleteTask;
import world.bentobox.challenges.tasks.TaskResults;


public class CompleteSimpleTask extends CompleteTask<SimpleChallenge>
{
    /**
     * @param addon            - Challenges Addon.
     * @param user             - User who performs challenge.
     * @param challenge        - Challenge that should be completed.
     * @param world            - World where completion may occur.
     * @param topLabel         - Label of the top command.
     * @param permissionPrefix - Permission prefix for GameMode addon.
     */
    public CompleteSimpleTask(ChallengesAddon addon, User user, Challenge challenge, World world,
            String topLabel, String permissionPrefix)
    {
        super(addon, user, (SimpleChallenge) challenge, world, topLabel, permissionPrefix);
    }


    protected void fullFillRequirements(TaskResults result)
    {
        if (this.addon.isEconomyProvided() && this.challenge.isTakeMoney())
        {
            this.addon.getEconomyProvider().withdraw(this.user, this.challenge.getRequiredMoney());
        }

        if (this.challenge.isTakeExperience() &&
            this.user.getPlayer().getGameMode() != GameMode.CREATIVE)
        {
            // Cannot take anything from creative game mode.
            this.user.getPlayer().setTotalExperience(
                    this.user.getPlayer().getTotalExperience() - this.challenge.getRequiredExperience());
        }
    }


    protected TaskResults checkIfCanCompleteChallenge(int maxTime)
    {
        TaskResults result = super.checkIfCanCompleteChallenge(maxTime);

        if (result.isMeetsRequirements())
        {
            if (!this.addon.isLevelProvided() &&
                this.challenge.getRequiredIslandLevel() != 0)
            {
                this.user.sendMessage("challenges.errors.missing-addon");
                result = TaskResults.EMPTY_RESULT;
            }
            else if (!this.addon.isEconomyProvided() && this.challenge.getRequiredMoney() != 0)
            {
                this.user.sendMessage("challenges.errors.missing-addon");
                result = TaskResults.EMPTY_RESULT;
            }
            else if (this.addon.isEconomyProvided() && this.challenge.getRequiredMoney() < 0)
            {
                this.user.sendMessage("challenges.errors.incorrect");
                result = TaskResults.EMPTY_RESULT;
            }
            else if (this.addon.isEconomyProvided() &&
                     !this.addon.getEconomyProvider().has(this.user, this.challenge.getRequiredMoney()))
            {
                this.user.sendMessage("challenges.errors.not-enough-money",
                                      "[value]",
                                      Integer.toString(this.challenge.getRequiredMoney()));
                result = TaskResults.EMPTY_RESULT;
            }
            else if (this.challenge.getRequiredExperience() < 0)
            {
                this.user.sendMessage("challenges.errors.incorrect");
                result = TaskResults.EMPTY_RESULT;
            }
            else if (this.user.getPlayer().getTotalExperience() < this.challenge.getRequiredExperience() &&
                     this.user.getPlayer().getGameMode() != GameMode.CREATIVE)
            {
                // Players in creative gamemode has infinite amount of EXP.

                this.user.sendMessage("challenges.errors.not-enough-experience",
                                      "[value]",
                                      Integer.toString(this.challenge.getRequiredExperience()));
                result = TaskResults.EMPTY_RESULT;
            }
            else if (this.addon.isLevelProvided() &&
                     this.addon.getLevelAddon().getIslandLevel(this.world, this.user.getUniqueId()) < this.challenge.getRequiredIslandLevel())
            {
                this.user.sendMessage("challenges.errors.island-level",
                                      TextVariables.NUMBER,
                                      String.valueOf(this.challenge.getRequiredIslandLevel()));
                result = TaskResults.EMPTY_RESULT;
            }
            else
            {
                // calculate factor
                int factor = this.getAvailableCompletionTimes(maxTime);

                if (this.addon.isEconomyProvided() && this.challenge.isTakeMoney())
                {
                    factor = Math.min(factor, (int) this.addon.getEconomyProvider().getBalance(this.user) / this.challenge.getRequiredMoney());
                }

                if (this.challenge.getRequiredExperience() > 0 && this.challenge.isTakeExperience())
                {
                    factor = Math.min(factor, this.user.getPlayer().getTotalExperience() / this.challenge.getRequiredExperience());
                }

                return result.setCompleteFactor(factor);
            }
        }

        return result;
    }



    /**
     * This method checks if it is possible to complete maxTimes current challenge by
     * challenge constraints and already completed times.
     * @param vantedTimes How many times user wants to complete challenge
     * @return how many times user is able complete challenge by its constraints.
     */
    private int getAvailableCompletionTimes(int vantedTimes)
    {
        if (!this.challenge.isRepeatable())
        {
            // Challenge is not repeatable
            vantedTimes = 1;
        }
        else if (this.challenge.getMaxTimes() != 0)
        {
            // Challenge has limitations
            long availableTimes = this.challenge.getMaxTimes() - this.manager.getChallengeTimes(this.user, this.world, this.challenge);

            if (availableTimes < vantedTimes)
            {
                vantedTimes = (int) availableTimes;
            }
        }

        return vantedTimes;
    }
}

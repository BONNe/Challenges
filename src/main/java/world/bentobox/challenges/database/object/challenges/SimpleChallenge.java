package world.bentobox.challenges.database.object.challenges;

import com.google.gson.annotations.Expose;
import world.bentobox.bentobox.api.configuration.ConfigComment;
import world.bentobox.challenges.database.object.Challenge;


public class SimpleChallenge extends Challenge
{
    public SimpleChallenge()
    {
        this.challengeType = ChallengeType.SIMPLE;
    }

    // ---------------------------------------------------------------------
    // Section: Constructors
    // ---------------------------------------------------------------------

    // ---------------------------------------------------------------------
    // Section: Abstract methods
    // ---------------------------------------------------------------------


    @Override
    public SimpleChallenge copy()
    {
        SimpleChallenge clone;

        try
        {
            clone = (SimpleChallenge) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            clone = new SimpleChallenge();
            this.populateClone(clone);

            clone.setRequiredExperience(this.requiredExperience);
            clone.setTakeExperience(this.takeExperience);
            clone.setRequiredMoney(this.requiredMoney);
            clone.setTakeMoney(this.takeMoney);
            clone.setRequiredIslandLevel(this.requiredIslandLevel);
        }

        return clone;
    }


    // ---------------------------------------------------------------------
    // Section: Getters and Setters
    // ---------------------------------------------------------------------


    public int getRequiredExperience()
    {
        return requiredExperience;
    }


    public void setRequiredExperience(int requiredExperience)
    {
        this.requiredExperience = requiredExperience;
    }


    public boolean isTakeExperience()
    {
        return takeExperience;
    }


    public void setTakeExperience(boolean takeExperience)
    {
        this.takeExperience = takeExperience;
    }


    public int getRequiredMoney()
    {
        return requiredMoney;
    }


    public void setRequiredMoney(int requiredMoney)
    {
        this.requiredMoney = requiredMoney;
    }


    public boolean isTakeMoney()
    {
        return takeMoney;
    }


    public void setTakeMoney(boolean takeMoney)
    {
        this.takeMoney = takeMoney;
    }


    public long getRequiredIslandLevel()
    {
        return requiredIslandLevel;
    }


    public void setRequiredIslandLevel(long requiredIslandLevel)
    {
        this.requiredIslandLevel = requiredIslandLevel;
    }


    // ---------------------------------------------------------------------
    // Section: Variables
    // ---------------------------------------------------------------------


    @ConfigComment("")
    @ConfigComment("Required experience for challenge completion.")
    @Expose
    private int requiredExperience = 0;

    @ConfigComment("")
    @ConfigComment("Take the experience from the player")
    @Expose
    private boolean takeExperience;

    @ConfigComment("")
    @ConfigComment("Required money for challenge completion. Economy plugins or addons")
    @ConfigComment("is required for this option.")
    @Expose
    private int requiredMoney = 0;

    @ConfigComment("")
    @ConfigComment("Take the money from the player")
    @Expose
    private boolean takeMoney;

    @ConfigComment("")
    @ConfigComment("Required island level for challenge completion. Plugin or Addon that")
    @ConfigComment("calculates island level is required for this option.")
    @Expose
    private long requiredIslandLevel;
}

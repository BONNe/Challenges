package world.bentobox.challenges.database.object;


import java.util.*;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import com.google.gson.annotations.Expose;

import world.bentobox.bentobox.api.configuration.ConfigComment;
import world.bentobox.bentobox.database.objects.DataObject;


/**
 * Data object for challenges
 * @author tastybento
 *
 */
public abstract class Challenge implements Cloneable, DataObject
{
    /**
     * Empty constructor
     */
    public Challenge()
    {
    }

    /**
     * @return the challengeType
     */
    public ChallengeType getChallengeType()
    {
        return this.challengeType;
    }


    public abstract Challenge copy();


    public void setChallengeType(ChallengeType type)
    {
    }


    /**
     * This enum holds all Challenge Types.
     */
    public enum ChallengeType
    {
        /**
         * The player must have the items on them.
         */
        INVENTORY,

        /**
         * Items or required entities have to be within x blocks of the player.
         */
        ISLAND,

        /**
         * Challenge that requires event to be processed.
         */
        EVENT,

        /**
         * Simple type, like required money / experience or island level. This my request
         * other plugins to be setup before it could work.
         */
        SIMPLE,

        // Use SIMPLE
        @Deprecated
        OTHER,
    }


    // ---------------------------------------------------------------------
    // Section: Variables
    // ---------------------------------------------------------------------

    @ConfigComment("")
    @ConfigComment("Unique name of the challenge")
    @Expose
    private String uniqueId = "";

    @ConfigComment("")
    @ConfigComment("The name of the challenge. May include color codes. Single line.")
    @Expose
    private String friendlyName = "";

    @ConfigComment("")
    @ConfigComment("Whether this challenge is deployed or not.")
    @Expose
    private boolean deployed;

    @ConfigComment("")
    @ConfigComment("Description of the challenge. Will become the lore on the icon. Can ")
    @ConfigComment("include & color codes. String List.")
    @Expose
    private List<String> description = new ArrayList<>();

    @ConfigComment("")
    @ConfigComment("The icon in the GUI for this challenge. ItemStack.")
    @Expose
    private ItemStack icon = new ItemStack(Material.PAPER);

    @ConfigComment("")
    @ConfigComment("Order of this challenge. It allows define order for challenges in")
    @ConfigComment("single level. If order for challenges are equal, it will order by")
    @ConfigComment("challenge unique id.")
    @Expose
    private int order = -1;

    @ConfigComment("")
    @ConfigComment("List of environments where this challenge will occur: NETHER, NORMAL,")
    @ConfigComment("THE_END. Leave blank for all.")
    @Expose
    private Set<World.Environment> environment = new HashSet<>();

    @ConfigComment("")
    @ConfigComment("If true, the challenge will disappear from the GUI when completed")
    @Expose
    private boolean removeWhenCompleted;

    @ConfigComment("")
    @ConfigComment("Unique challenge ID. Empty means that challenge is in free challenge list.")
    @Expose
    private String level = "";

    @Expose
    protected ChallengeType challengeType;

    // ---------------------------------------------------------------------
    // Section: Requirement related
    // ---------------------------------------------------------------------

    @ConfigComment("")
    @ConfigComment("")
    @ConfigComment("The required permissions to see this challenge. String list.")
    @Expose
    private Set<String> requiredPermissions = new HashSet<>();


    // ---------------------------------------------------------------------
    // Section: Rewards
    // ---------------------------------------------------------------------

    @ConfigComment("")
    @ConfigComment("")
    @ConfigComment("If this is blank, the reward text will be auto-generated, otherwise")
    @ConfigComment("this will be used.")
    @Expose
    private String rewardText = "";


    @ConfigComment("")
    @ConfigComment("List of items the player will receive first time. ItemStack List.")
    @Expose
    private List<ItemStack> rewardItems = new ArrayList<>();

    @ConfigComment("")
    @ConfigComment("Experience point reward")
    @Expose
    private int rewardExperience = 0;

    @ConfigComment("")
    @ConfigComment("Money reward. Economy plugin or addon required for this option.")
    @Expose
    private int rewardMoney = 0;

    @ConfigComment("")
    @ConfigComment("Commands to run when the player completes the challenge for the first")
    @ConfigComment("time. String List")
    @Expose
    private List<String> rewardCommands = new ArrayList<>();


    // ---------------------------------------------------------------------
    // Section: Repeat Rewards
    // ---------------------------------------------------------------------


    @ConfigComment("")
    @ConfigComment("")
    @ConfigComment("True if the challenge is repeatable")
    @Expose
    private boolean repeatable;

    @ConfigComment("")
    @ConfigComment("Description of the repeat rewards. If blank, it will be autogenerated.")
    @Expose
    private String repeatRewardText = "";

    @ConfigComment("")
    @ConfigComment("Maximum number of times the challenge can be repeated. 0 or less")
    @ConfigComment("will mean infinite times.")
    @Expose
    private int maxTimes = 1;

    @ConfigComment("")
    @ConfigComment("Repeat experience reward")
    @Expose
    private int repeatExperienceReward = 0;

    @ConfigComment("")
    @ConfigComment("Reward items for repeating the challenge. List of ItemStacks.")
    @Expose
    private List<ItemStack> repeatItemReward = new ArrayList<>();

    @ConfigComment("")
    @ConfigComment("Repeat money reward. Economy plugin or addon required for this option.")
    @Expose
    private int repeatMoneyReward;

    @ConfigComment("")
    @ConfigComment("Commands to run when challenge is repeated. String List.")
    @Expose
    private List<String> repeatRewardCommands = new ArrayList<>();


    // ---------------------------------------------------------------------
    // Section: Getters
    // ---------------------------------------------------------------------


    /**
     * @return the uniqueId
     */
    @Override
    public String getUniqueId()
    {
        return uniqueId;
    }


    /**
     * @return the friendlyName
     */
    public String getFriendlyName()
    {
        return friendlyName.isEmpty() ? uniqueId : friendlyName;
    }


    /**
     * @return the deployed
     */
    public boolean isDeployed()
    {
        return deployed;
    }


    /**
     * @return the description
     */
    public List<String> getDescription()
    {
        return description;
    }


    /**
     * @return the icon
     */
    public ItemStack getIcon()
    {
        return icon !=null ? icon.clone() : new ItemStack(Material.PAPER);
    }


    /**
     * @return the order
     */
    public int getOrder()
    {
        return order;
    }


    /**
     * @return the environment
     */
    public Set<World.Environment> getEnvironment()
    {
        return environment;
    }


    /**
     * @return the level
     */
    public String getLevel()
    {
        return level;
    }


    /**
     * @return the removeWhenCompleted
     */
    public boolean isRemoveWhenCompleted()
    {
        return removeWhenCompleted;
    }


    /**
     * @return the requiredPermissions
     */
    public Set<String> getRequiredPermissions()
    {
        return requiredPermissions;
    }


    /**
     * @return the rewardText
     */
    public String getRewardText()
    {
        return rewardText;
    }


    /**
     * @return the rewardItems
     */
    public List<ItemStack> getRewardItems()
    {
        return rewardItems;
    }


    /**
     * @return the rewardExperience
     */
    public int getRewardExperience()
    {
        return rewardExperience;
    }


    /**
     * @return the rewardMoney
     */
    public int getRewardMoney()
    {
        return rewardMoney;
    }


    /**
     * @return the rewardCommands
     */
    public List<String> getRewardCommands()
    {
        return rewardCommands;
    }


    /**
     * @return the repeatable
     */
    public boolean isRepeatable()
    {
        return repeatable;
    }


    /**
     * @return the repeatRewardText
     */
    public String getRepeatRewardText()
    {
        return repeatRewardText;
    }


    /**
     * @return the maxTimes
     */
    public int getMaxTimes()
    {
        return maxTimes;
    }


    /**
     * @return the repeatExperienceReward
     */
    public int getRepeatExperienceReward()
    {
        return repeatExperienceReward;
    }


    /**
     * @return the repeatItemReward
     */
    public List<ItemStack> getRepeatItemReward()
    {
        return repeatItemReward;
    }


    /**
     * @return the repeatMoneyReward
     */
    public int getRepeatMoneyReward()
    {
        return repeatMoneyReward;
    }


    /**
     * @return the repeatRewardCommands
     */
    public List<String> getRepeatRewardCommands()
    {
        return repeatRewardCommands;
    }


    // ---------------------------------------------------------------------
    // Section: Setters
    // ---------------------------------------------------------------------


    /**
     * @param uniqueId the uniqueId to set
     */
    @Override
    public void setUniqueId(String uniqueId)
    {
        this.uniqueId = uniqueId;
    }


    /**
     * This method sets the friendlyName value.
     * @param friendlyName the friendlyName new value.
     *
     */
    public void setFriendlyName(String friendlyName)
    {
        this.friendlyName = friendlyName;
    }


    /**
     * This method sets the deployed value.
     * @param deployed the deployed new value.
     *
     */
    public void setDeployed(boolean deployed)
    {
        this.deployed = deployed;
    }


    /**
     * This method sets the description value.
     * @param description the description new value.
     *
     */
    public void setDescription(List<String> description)
    {
        this.description = description;
    }


    /**
     * This method sets the icon value.
     * @param icon the icon new value.
     *
     */
    public void setIcon(ItemStack icon)
    {
        this.icon = icon;
    }


    /**
     * This method sets the order value.
     * @param order the order new value.
     *
     */
    public void setOrder(int order)
    {
        this.order = order;
    }


    /**
     * This method sets the environment value.
     * @param environment the environment new value.
     *
     */
    public void setEnvironment(Set<World.Environment> environment)
    {
        this.environment = environment;
    }


    /**
     * This method sets the level value.
     * @param level the level new value.
     */
    public void setLevel(String level)
    {
        this.level = level;
    }


    /**
     * This method sets the removeWhenCompleted value.
     * @param removeWhenCompleted the removeWhenCompleted new value.
     *
     */
    public void setRemoveWhenCompleted(boolean removeWhenCompleted)
    {
        this.removeWhenCompleted = removeWhenCompleted;
    }


    /**
     * This method sets the requiredPermissions value.
     * @param requiredPermissions the requiredPermissions new value.
     *
     */
    public void setRequiredPermissions(Set<String> requiredPermissions)
    {
        this.requiredPermissions = requiredPermissions;
    }


    /**
     * This method sets the rewardText value.
     * @param rewardText the rewardText new value.
     *
     */
    public void setRewardText(String rewardText)
    {
        this.rewardText = rewardText;
    }


    /**
     * This method sets the rewardItems value.
     * @param rewardItems the rewardItems new value.
     *
     */
    public void setRewardItems(List<ItemStack> rewardItems)
    {
        this.rewardItems = rewardItems;
    }


    /**
     * This method sets the rewardExperience value.
     * @param rewardExperience the rewardExperience new value.
     *
     */
    public void setRewardExperience(int rewardExperience)
    {
        this.rewardExperience = rewardExperience;
    }


    /**
     * This method sets the rewardMoney value.
     * @param rewardMoney the rewardMoney new value.
     *
     */
    public void setRewardMoney(int rewardMoney)
    {
        this.rewardMoney = rewardMoney;
    }


    /**
     * This method sets the rewardCommands value.
     * @param rewardCommands the rewardCommands new value.
     *
     */
    public void setRewardCommands(List<String> rewardCommands)
    {
        this.rewardCommands = rewardCommands;
    }


    /**
     * This method sets the repeatable value.
     * @param repeatable the repeatable new value.
     *
     */
    public void setRepeatable(boolean repeatable)
    {
        this.repeatable = repeatable;
    }


    /**
     * This method sets the repeatRewardText value.
     * @param repeatRewardText the repeatRewardText new value.
     *
     */
    public void setRepeatRewardText(String repeatRewardText)
    {
        this.repeatRewardText = repeatRewardText;
    }


    /**
     * This method sets the maxTimes value.
     * @param maxTimes the maxTimes new value.
     *
     */
    public void setMaxTimes(int maxTimes)
    {
        this.maxTimes = maxTimes;
    }


    /**
     * This method sets the repeatExperienceReward value.
     * @param repeatExperienceReward the repeatExperienceReward new value.
     *
     */
    public void setRepeatExperienceReward(int repeatExperienceReward)
    {
        this.repeatExperienceReward = repeatExperienceReward;
    }


    /**
     * This method sets the repeatItemReward value.
     * @param repeatItemReward the repeatItemReward new value.
     *
     */
    public void setRepeatItemReward(List<ItemStack> repeatItemReward)
    {
        this.repeatItemReward = repeatItemReward;
    }


    /**
     * This method sets the repeatMoneyReward value.
     * @param repeatMoneyReward the repeatMoneyReward new value.
     *
     */
    public void setRepeatMoneyReward(int repeatMoneyReward)
    {
        this.repeatMoneyReward = repeatMoneyReward;
    }


    /**
     * This method sets the repeatRewardCommands value.
     * @param repeatRewardCommands the repeatRewardCommands new value.
     *
     */
    public void setRepeatRewardCommands(List<String> repeatRewardCommands)
    {
        this.repeatRewardCommands = repeatRewardCommands;
    }


    /**
     * @see java.lang.Object#hashCode()
     * @return int
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((uniqueId == null) ? 0 : uniqueId.hashCode());
        return result;
    }


    /**
     * @see java.lang.Object#equals(Object) ()
     * @param obj of type Object
     * @return boolean
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof Challenge))
        {
            return false;
        }

        Challenge other = (Challenge) obj;

        if (uniqueId == null)
        {
            return other.uniqueId == null;
        }
        else
        {
            return uniqueId.equals(other.uniqueId);
        }
    }


    /**
     * This method populates cloned object with Challenge variables.
     * @param clone Cloned object that must be populated.
     */
    protected void populateClone(Challenge clone)
    {
        // Global settings
        clone.setUniqueId(this.uniqueId);
        clone.setFriendlyName(this.friendlyName);
        clone.setDescription(new ArrayList<>(this.description));
        clone.setIcon(this.icon.clone());

        clone.setOrder(this.order);
        clone.setDeployed(this.deployed);
        clone.setEnvironment(new HashSet<>(this.environment));
        clone.setLevel(this.level);
        clone.setRemoveWhenCompleted(this.removeWhenCompleted);

        // Requirements
        clone.setRequiredPermissions(new HashSet<>(this.requiredPermissions));

        // Rewards
        clone.setRewardText(this.rewardText);
        clone.setRewardItems(this.rewardItems.stream().
            map(ItemStack::clone).
            collect(Collectors.toCollection(() -> new ArrayList<>(this.rewardItems.size()))));
        clone.setRewardExperience(this.rewardExperience);
        clone.setRewardMoney(this.rewardMoney);
        clone.setRewardCommands(new ArrayList<>(this.rewardCommands));

        // Repeat
        clone.setRepeatable(this.repeatable);
        clone.setMaxTimes(this.maxTimes);

        // Repeat rewards
        clone.setRepeatRewardText(this.repeatRewardText);
        clone.setRepeatExperienceReward(this.repeatExperienceReward);
        clone.setRepeatItemReward(this.repeatItemReward.stream().
            map(ItemStack::clone).
            collect(Collectors.toCollection(() -> new ArrayList<>(this.repeatItemReward.size()))));
        clone.setRepeatMoneyReward(this.repeatMoneyReward);
        clone.setRepeatRewardCommands(new ArrayList<>(this.repeatRewardCommands));
    }
}
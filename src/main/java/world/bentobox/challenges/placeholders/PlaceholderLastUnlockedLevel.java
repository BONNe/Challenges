package world.bentobox.challenges.placeholders;


import java.util.Iterator;
import java.util.List;

import world.bentobox.bentobox.api.addons.GameModeAddon;
import world.bentobox.bentobox.api.placeholders.PlaceholderReplacer;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.challenges.ChallengesAddon;
import world.bentobox.challenges.utils.LevelStatus;


/**
 * This class defines Last Unlocked Level placeholder. If last unlocked level is not found
 * then return EmptyString.
 */
public class PlaceholderLastUnlockedLevel implements PlaceholderReplacer
{
	/**
	 * Constructor creates a new PlaceholderLastUnlockedLevel instance.
	 *
	 * @param addon of type ChallengesAddon
	 * @param gameModeAddon of type GameModeAddon
	 */
	public PlaceholderLastUnlockedLevel(ChallengesAddon addon, GameModeAddon gameModeAddon)
	{
		this.addon = addon;
		this.gameModeAddon = gameModeAddon;
	}


	/**
	 /* (non-Javadoc)
	 * @see world.bentobox.bentobox.api.placeholders.PlaceholderReplacer#onReplace(world.bentobox.bentobox.api.user.User)
	 */
	@Override
	public String onReplace(User user)
	{
		List<LevelStatus> levelStatusList =
			this.addon.getChallengesManager().getAllChallengeLevelStatus(user,
				this.gameModeAddon.getOverWorld());

		String returnValue = "";

		Iterator<LevelStatus> iterator = levelStatusList.iterator();

		if (iterator.hasNext())
		{
			LevelStatus last = iterator.next();

			if (last.isUnlocked())
			{
				returnValue = last.getLevel().getFriendlyName();
			}

			while (iterator.hasNext() && last.isUnlocked())
			{
				last = iterator.next();

				if (last.isUnlocked())
				{
					returnValue = last.getLevel().getFriendlyName();
				}
			}
		}

		return returnValue;
	}


// ---------------------------------------------------------------------
// Section: Variables
// ---------------------------------------------------------------------


	/**
	 * Variable stores challenges addon.
	 */
	private ChallengesAddon addon;

	/**
	 * Variable stores gameModeAddon.
	 */
	private GameModeAddon gameModeAddon;
}

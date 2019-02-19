package world.bentobox.challenges.placeholders;


import java.util.List;

import world.bentobox.bentobox.api.addons.GameModeAddon;
import world.bentobox.bentobox.api.placeholders.PlaceholderReplacer;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.challenges.ChallengesAddon;
import world.bentobox.challenges.database.object.ChallengeLevel;


/**
 * This class defines completed level count in challenges addon.
 */
public class PlaceholderCompletedLevelCount implements PlaceholderReplacer
{
	/**
	 * Constructor creates a new PlaceholderCompletedLevelCount instance.
	 *
	 * @param addon of type ChallengesAddon
	 * @param gameModeAddon of type GameModeAddon
	 */
	public PlaceholderCompletedLevelCount(ChallengesAddon addon, GameModeAddon gameModeAddon)
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
		List<ChallengeLevel> levels =
			this.addon.getChallengesManager().getLevels(this.gameModeAddon.getOverWorld());

		long count = levels.stream().
			filter(level -> this.addon.getChallengesManager().isLevelUnlocked(user,
				this.gameModeAddon.getOverWorld(),
				level)).
			count();

		return Long.toString(count);
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

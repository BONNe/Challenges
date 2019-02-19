package world.bentobox.challenges.placeholders;


import java.util.List;

import world.bentobox.bentobox.api.addons.GameModeAddon;
import world.bentobox.bentobox.api.placeholders.PlaceholderReplacer;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.challenges.ChallengesAddon;
import world.bentobox.challenges.database.object.Challenge;


/**
 * Allows to define number of completed challenges.
 */
public class PlaceholderCompletedChallengeCount implements PlaceholderReplacer
{
	/**
	 * Constructor creates a new PlaceholderCompletedChallengeCount instance.
	 *
	 * @param addon of type ChallengesAddon
	 * @param gameModeAddon of type GameModeAddon
	 */
	public PlaceholderCompletedChallengeCount(ChallengesAddon addon,
		GameModeAddon gameModeAddon)
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
		List<Challenge> challenges =
			this.addon.getChallengesManager().getAllChallenges(this.gameModeAddon.getOverWorld());

		long count = challenges.stream().
			filter(challenge -> this.addon.getChallengesManager().isChallengeComplete(user,
				this.gameModeAddon.getOverWorld(),
				challenge)).
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

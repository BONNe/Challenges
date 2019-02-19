package world.bentobox.challenges.placeholders;


import java.util.List;

import world.bentobox.bentobox.api.addons.GameModeAddon;
import world.bentobox.bentobox.api.placeholders.PlaceholderReplacer;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.challenges.ChallengesAddon;
import world.bentobox.challenges.database.object.Challenge;


/**
 * This class defines placeholder that returns how much challenges are completed in
 * specified level.
 */
public class PlaceholderCompletedChallengeCountPerLevel implements PlaceholderReplacer
{
	/**
	 * Constructor creates a new PlaceholderCompletedChallengeCountPerLevel instance.
	 *
	 * @param addon of type ChallengesAddon
	 * @param gameModeAddon of type GameModeAddon
	 * @param levelID of type String
	 */
	public PlaceholderCompletedChallengeCountPerLevel(ChallengesAddon addon,
		GameModeAddon gameModeAddon,
		String levelID)
	{
		this.addon = addon;
		this.gameModeAddon = gameModeAddon;
		this.levelID = levelID;
	}


	/**
	 /* (non-Javadoc)
	 * @see world.bentobox.bentobox.api.placeholders.PlaceholderReplacer#onReplace(world.bentobox.bentobox.api.user.User)
	 */
	@Override
	public String onReplace(User user)
	{
		List<Challenge> challenges = this.addon.getChallengesManager().getLevelChallenges(
			this.addon.getChallengesManager().getLevel(this.levelID));

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
	 * This stores levelID.
	 */
	private String levelID;

	/**
	 * Variable stores challenges addon.
	 */
	private ChallengesAddon addon;

	/**
	 * Variable stores gameModeAddon.
	 */
	private GameModeAddon gameModeAddon;
}

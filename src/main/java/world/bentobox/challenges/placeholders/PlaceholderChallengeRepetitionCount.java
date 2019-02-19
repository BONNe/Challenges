package world.bentobox.challenges.placeholders;



import world.bentobox.bentobox.api.addons.GameModeAddon;
import world.bentobox.bentobox.api.placeholders.PlaceholderReplacer;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.challenges.ChallengesAddon;


/**
 * This class allows to define placeholder that returns repetition count for each challenge.
 */
public class PlaceholderChallengeRepetitionCount implements PlaceholderReplacer
{
	/**
	 * Constructor creates a new PlaceholderChallengeRepetitionCount instance.
	 *
	 * @param addon of type ChallengesAddon
	 * @param gameModeAddon of type GameModeAddon
	 * @param challenge of type String
	 */
	public PlaceholderChallengeRepetitionCount(ChallengesAddon addon,
		GameModeAddon gameModeAddon,
		String challenge)
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
		return Long.toString(this.addon.getChallengesManager().getChallengeTimes(user,
			this.gameModeAddon.getOverWorld(),
			this.challengeID));
	}


// ---------------------------------------------------------------------
// Section: Variables
// ---------------------------------------------------------------------


	/**
	 * This stores challenges unique ID.
	 */
	private String challengeID;

	/**
	 * Variable stores challenges addon.
	 */
	private ChallengesAddon addon;

	/**
	 * Variable stores gameModeAddon.
	 */
	private GameModeAddon gameModeAddon;
}

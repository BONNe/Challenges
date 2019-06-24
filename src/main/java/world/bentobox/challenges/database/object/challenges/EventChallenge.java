package world.bentobox.challenges.database.object.challenges;


import world.bentobox.challenges.database.object.Challenge;


public class EventChallenge extends Challenge
{
    // ---------------------------------------------------------------------
    // Section: Constructor
    // ---------------------------------------------------------------------

    public EventChallenge()
    {
        this.challengeType = ChallengeType.EVENT;
    }

    // ---------------------------------------------------------------------
    // Section: Getters and Setters
    // ---------------------------------------------------------------------



    // ---------------------------------------------------------------------
    // Section: Abstract methods
    // ---------------------------------------------------------------------


    @Override
    public EventChallenge copy()
    {
        EventChallenge clone = new EventChallenge();
        this.populateClone(clone);

        return clone;
    }
}

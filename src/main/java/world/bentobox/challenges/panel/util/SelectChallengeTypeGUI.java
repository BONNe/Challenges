package world.bentobox.challenges.panel.util;


import org.bukkit.Material;
import org.bukkit.World;
import world.bentobox.bentobox.api.panels.builders.PanelBuilder;
import world.bentobox.bentobox.api.panels.builders.PanelItemBuilder;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.challenges.database.object.Challenge;
import world.bentobox.challenges.utils.GuiUtils;

import java.util.function.BiConsumer;


/**
 * This class creates panel that allows to select and deselect World Environments. On save it runs
 * input consumer with true and selected values.
 */
public class SelectChallengeTypeGUI
{
	public SelectChallengeTypeGUI(User user, BiConsumer<Boolean, Challenge.ChallengeType> consumer)
	{
		this.user = user;
		this.consumer = consumer;

		this.build();
	}


	/**
	 * This method builds environment select panel.
	 */
	private void build()
	{
		PanelBuilder panelBuilder = new PanelBuilder().user(this.user).name(this.user.getTranslation("challenges.gui.title.admin.toggle-environment"));

		GuiUtils.fillBorder(panelBuilder, Material.BLUE_STAINED_GLASS_PANE);

		panelBuilder.item(20, new PanelItemBuilder().
			name(World.Environment.NETHER.name()).
			icon(Material.NETHERRACK).
			clickHandler((panel, user1, clickType, i) -> {
				this.consumer.accept(true, Challenge.ChallengeType.INVENTORY);
				return true;
			}).
			build());
		panelBuilder.item(22, new PanelItemBuilder().
			name(World.Environment.NORMAL.name()).
			icon(Material.DIRT).
			clickHandler((panel, user1, clickType, i) -> {
				this.consumer.accept(true, Challenge.ChallengeType.ISLAND);
				return true;
			}).
			build());
		panelBuilder.item(24, new PanelItemBuilder().
			name(World.Environment.THE_END.name()).
			icon(Material.END_STONE).
			clickHandler((panel, user1, clickType, i) -> {
				this.consumer.accept(true, Challenge.ChallengeType.SIMPLE);
				return true;
			}).
			build());


		panelBuilder.item(44, new PanelItemBuilder().
			name(this.user.getTranslation("challenges.gui.buttons.return")).
			icon(Material.OAK_DOOR).
			clickHandler((panel, user1, clickType, i) -> {
				this.consumer.accept(false, Challenge.ChallengeType.EVENT);
				return true;
			}).
			build());

		panelBuilder.build();
	}


// ---------------------------------------------------------------------
// Section: Variables
// ---------------------------------------------------------------------

	/**
	 * User who wants to run command.
	 */
	private User user;

	/**
	 * Stores current Consumer
	 */
	private BiConsumer<Boolean, Challenge.ChallengeType> consumer;

}

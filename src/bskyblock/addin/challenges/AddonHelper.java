package bskyblock.addin.challenges;

import java.util.logging.Logger;

import org.bukkit.command.CommandSender;

import us.tastybento.bskyblock.BSkyBlock;
import us.tastybento.bskyblock.config.BSBLocale;
import us.tastybento.bskyblock.util.Util;

/**
 * Makes code look nicer
 * @author ben
 *
 */
public abstract class AddonHelper {
    protected final Challenges plugin;
    protected final BSkyBlock bSkyBlock;

    public AddonHelper(Challenges plugin) {
        this.plugin = plugin;
        this.bSkyBlock = BSkyBlock.getPlugin();
    }
    
    public final Logger getLogger() {
        return plugin.getLogger();
    }
    
    public final void sendMessage(CommandSender sender, String message) {
        Util.sendMessage(sender, message);
    }
    
    public final BSBLocale getLocale(CommandSender sender) {
        return plugin.getLocale(sender);
    }
}

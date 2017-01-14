package me.armar.plugins.autorank.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.pathbuilder.Path;
import me.armar.plugins.autorank.util.AutorankTools;

public class ChooseCommand extends AutorankCommand {

	private final Autorank plugin;

	public ChooseCommand(final Autorank instance) {
		this.setUsage("/ar choose <path name>");
		this.setDesc("Choose a certain ranking path");
		this.setPermission("autorank.choose");

		plugin = instance;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

		// This command will give a preview of a certain path of ranking.
		if (!plugin.getCommandsManager().hasPermission("autorank.choose", sender)) {
			return true;
		}

		if (args.length < 2) {
			sender.sendMessage(Lang.INVALID_FORMAT.getConfigValue("/ar choose <path name>"));
			return true;
		}

		if (!(sender instanceof Player)) {
			sender.sendMessage(Lang.YOU_ARE_A_ROBOT.getConfigValue("you can't choose ranking paths, silly.."));
			return true;
		}

		final Player player = (Player) sender;

		final String pathName = AutorankTools.getStringFromArgs(args, 1);

		// Get current path
		Path activePath = plugin.getPathManager().getCurrentPath(player.getUniqueId());

		if (activePath != null && activePath.getDisplayName().equalsIgnoreCase(pathName)) {
			sender.sendMessage(Lang.ALREADY_ON_THIS_PATH.getConfigValue());
			return true;
		}

		// Try to find path that matches given name
		Path targetPath = plugin.getPathManager().matchPath(pathName, false);

		if (targetPath == null) {
			sender.sendMessage(Lang.NO_PATH_FOUND_WITH_THAT_NAME.getConfigValue());
			return true;
		}
		
		// TODO: currently, if you change the display name, the user can retake a path even though the internal name did not change.
		
		if (plugin.getPlayerDataConfig().hasCompletedPath(player.getUniqueId(), targetPath.getDisplayName()) && !plugin.getPathsConfig().allowInfinitePathing(targetPath.getDisplayName())) {
			sender.sendMessage(ChatColor.RED + "You already completed this path before. You are not allowed to retake it!");
			return true;
		}
		
		if (!targetPath.meetsPrerequisites(player)) {
			sender.sendMessage(ChatColor.RED + "You do not meet the prerequisites of this path!");
			sender.sendMessage(ChatColor.RED + "Type " + ChatColor.GOLD + "/ar view prereq " + targetPath.getDisplayName() + ChatColor.RED + " to see a list of prerequisites.");
			return true;
		}

		// Set chosen path to target path
		plugin.getPlayerDataConfig().setChosenPath(player.getUniqueId(), targetPath.getDisplayName());

		// Reset progress
		plugin.getPlayerDataConfig().setCompletedRequirements(player.getUniqueId(), new ArrayList<Integer>());

		// Give player confirmation message.
		sender.sendMessage(Lang.CHOSEN_PATH.getConfigValue(targetPath.getDisplayName()));
		sender.sendMessage(Lang.PROGRESS_RESET.getConfigValue());

		return true;
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.commands.manager.AutorankCommand#onTabComplete(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public List<String> onTabComplete(final CommandSender sender, final Command cmd, final String commandLabel,
			final String[] args) {
		// TODO Auto-generated method stub

		final Player player = (Player) sender;

		final List<String> possibilities = new ArrayList<String>();

		final List<Path> possiblePaths = plugin.getPathManager().getPossiblePaths(player);

		for (Path possiblePath : possiblePaths) {
			possibilities.add(possiblePath.getDisplayName());
		}

		return possibilities;
	}

}

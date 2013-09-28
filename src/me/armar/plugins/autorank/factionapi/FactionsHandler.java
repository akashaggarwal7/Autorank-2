package me.armar.plugins.autorank.factionapi;

import me.armar.plugins.autorank.Autorank;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.entity.UPlayer;

public class FactionsHandler {

	private Autorank plugin;
	private Factions factions;
	
	public FactionsHandler(Autorank instance) {
		plugin = instance;
	}
	
	public boolean isEnabled() {
		return factions != null;
	}
	
	public boolean setupFactions() {
		Plugin factionPlugin = plugin.getServer().getPluginManager().getPlugin("Factions");
		
		if (factionPlugin == null) return false;
		
		factions = (Factions) factionPlugin;
		
		return factions != null;
	}
	
	public double getFactionPower(Player player) {
		UPlayer uPlayer = UPlayer.get(player);
		
		return uPlayer.getFaction().getPower();
	}
}

package de.example.playeradmin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PlayerAdminPlugin extends JavaPlugin implements Listener {
    private final Set<UUID> frozen = ConcurrentHashMap.newKeySet();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("PlayerAdminPlugin enabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("playeradmin.admin")) {
            sender.sendMessage(color("&cDazu hast du keine Berechtigung."));
            return true;
        }
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            help(sender);
            return true;
        }

        String sub = args[0].toLowerCase();
        if (sub.equals("info") || sub.equals("spieler")) {
            if (args.length < 2) { sender.sendMessage(color("&cVerwendung: /player info <Spieler>")); return true; }
            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null) { sender.sendMessage(color("&cSpieler ist nicht online.")); return true; }
            sender.sendMessage(color("&6Spieler: &f" + target.getName()));
            sender.sendMessage(color("&6Anzeige-Name: &f" + target.getDisplayName()));
            sender.sendMessage(color("&6Welt: &f" + target.getWorld().getName()));
            sender.sendMessage(color("&6Gefreezt: &f" + (frozen.contains(target.getUniqueId()) ? "Ja" : "Nein")));
            sender.sendMessage(color("&6Unsterblich: &f" + target.isInvulnerable()));
            return true;
        }

        if (sub.equals("name")) {
            if (args.length < 3) { sender.sendMessage(color("&cVerwendung: /player name <Spieler> <AnzeigeName>")); return true; }
            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null) { sender.sendMessage(color("&cSpieler ist nicht online.")); return true; }
            String display = String.join(" ", java.util.Arrays.copyOfRange(args, 2, args.length)).replace('&', '§');
            target.setDisplayName(display);
            target.setPlayerListName(display.length() > 16 ? display.substring(0, 16) : display);
            sender.sendMessage(color("&aAnzeige-Name von &f" + target.getName() + " &awurde auf &f" + display + " &agesetzt."));
            target.sendMessage(color("&aDein Anzeige-Name wurde geändert."));
            return true;
        }

        if (sub.equals("freeze") || sub.equals("unfreeze")) {
            if (args.length < 2) { sender.sendMessage(color("&cVerwendung: /player " + sub + " <Spieler>")); return true; }
            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null) { sender.sendMessage(color("&cSpieler ist nicht online.")); return true; }
            if (sub.equals("freeze")) {
                frozen.add(target.getUniqueId());
                sender.sendMessage(color("&a" + target.getName() + " wurde eingefroren."));
                target.sendMessage(color("&cDu wurdest von einem Admin eingefroren."));
            } else {
                frozen.remove(target.getUniqueId());
                sender.sendMessage(color("&a" + target.getName() + " wurde aufgetaut."));
                target.sendMessage(color("&aDu bist nicht mehr eingefroren."));
            }
            return true;
        }

        if (sub.equals("god") || sub.equals("invulnerable")) {
            if (args.length < 3 || !(args[2].equalsIgnoreCase("on") || args[2].equalsIgnoreCase("off"))) {
                sender.sendMessage(color("&cVerwendung: /player god <Spieler> <on|off>")); return true;
            }
            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null) { sender.sendMessage(color("&cSpieler ist nicht online.")); return true; }
            boolean enabled = args[2].equalsIgnoreCase("on");
            target.setInvulnerable(enabled);
            sender.sendMessage(color("&aUnsterblichkeit für " + target.getName() + ": " + (enabled ? "&aan" : "&caus")));
            return true;
        }

        if (sub.equals("skin")) {
            if (args.length < 3) { sender.sendMessage(color("&cVerwendung: /player skin <Spieler> <Skin-Spielername>")); return true; }
            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null) { sender.sendMessage(color("&cZielspieler ist nicht online.")); return true; }
            if (Bukkit.getPluginManager().getPlugin("SkinsRestorer") == null) {
                sender.sendMessage(color("&cSkin ändern benötigt das Zusatzplugin SkinsRestorer."));
                sender.sendMessage(color("&7Installiere SkinsRestorer und starte den Server neu."));
                return true;
            }
            // Delegate skin handling to SkinsRestorer; the target may need to reconnect for the skin to refresh.
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "skin set " + target.getName() + " " + args[2]);
            sender.sendMessage(color("&aSkin-Befehl für " + target.getName() + " ausgeführt. Falls der Skin nicht sofort erscheint, erneut verbinden."));
            return true;
        }

        help(sender);
        return true;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (!frozen.contains(event.getPlayer().getUniqueId()) || event.getTo() == null) return;
        if (event.getFrom().getX() != event.getTo().getX() || event.getFrom().getY() != event.getTo().getY() || event.getFrom().getZ() != event.getTo().getZ()) {
            event.setTo(event.getFrom());
        }
    }

    @Override
    public void onDisable() { frozen.clear(); }

    private void help(CommandSender sender) {
        sender.sendMessage(color("&6--- PlayerAdmin ---"));
        sender.sendMessage(color("&e/player info <Spieler> &7- Spieler anzeigen"));
        sender.sendMessage(color("&e/player name <Spieler> <AnzeigeName> &7- Anzeige-Name setzen"));
        sender.sendMessage(color("&e/player skin <Spieler> <SkinName> &7- Skin setzen (SkinsRestorer nötig)"));
        sender.sendMessage(color("&e/player freeze <Spieler> &7- Spieler einfrieren"));
        sender.sendMessage(color("&e/player unfreeze <Spieler> &7- Spieler auftauen"));
        sender.sendMessage(color("&e/player god <Spieler> <on|off> &7- Unsterblichkeit"));
    }

    private String color(String text) { return ChatColor.translateAlternateColorCodes('&', text); }
}

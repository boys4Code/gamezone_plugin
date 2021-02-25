package com.totoka28.plugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.io.BukkitObjectInputStream;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Main extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        System.out.println("boys4code's plugin is enable!");
        this.getConfig().options().copyDefaults();
        saveDefaultConfig();
        try {
            adatfile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bukkit.getPluginManager().registerEvents(this, this);
        fentvane.addAll(Bukkit.getOnlinePlayers());
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            public void run() {
                uptime();
            }
        },0L, 20L);
    }
    private File file;
    private YamlConfiguration config2;

    public YamlConfiguration getfile() {return config2;}
    public File getFile() {return file;}

    public void adatfile() throws IOException {
        file = new File(Bukkit.getServer().getPluginManager().getPlugin("GameZonePl").getDataFolder(), "adatok.yml");
        if (!file.exists()) {
            file.createNewFile();
        }
        config2 = YamlConfiguration.loadConfiguration(file);
    }
    public void mentesfile() {
        try {
            this.getfile().save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String eleres1 = "Joinmsg.players.";
    public String eleres2 = "Leavemsg.players.";
    public String eleres3 = "Uptime.players.";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (cmd.getName().equalsIgnoreCase("joinmsg")) {
                if (args.length <= 0) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("CommandDescrptionJoin")));
                } else {
                    String szöveg = "";
                    for (int i=0; i < args.length; i++) {
                        szöveg = szöveg + args[i] + " ";
                    }
                    this.getfile().set(eleres1+player.getUniqueId(), szöveg);
                    mentesfile();
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("JoinmsgisSaved")));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("NewJoinmsg") + this.getfile().getString(eleres1+player.getUniqueId())));
                }
            } else if (cmd.getName().equalsIgnoreCase("leavemsg")) {
                if (args.length <= 0) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("CommandDescrptionLeave")));
                } else {
                    String szöveg = "";
                    for (int i=0; i < args.length; i++) {
                        szöveg = szöveg + args[i] + " ";
                    }
                    this.getfile().set(eleres2+player.getUniqueId(), szöveg);
                    mentesfile();
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("LevaemsgisSaved")));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("NewLeavemsg") + this.getfile().getString(eleres2+player.getUniqueId())));
                }
            }



            if (cmd.getName().equals("uptime")) {
                if (args.length <= 0) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("UptimeDescription")));
                } else if (args.length <= 1) {
                    if (args[0].equals("del")) {
                        if (player.hasPermission("gamezone.uptime.delete")) {
                            this.getfile().set("Uptime.players", null);
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("RemovedUptime")));
                        } else {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("NoPermission")));
                        }
                    } else {
                        if (player.hasPermission("gamezone.uptime.check")) {
                            if (Bukkit.getOfflinePlayer(args[0]).hasPlayedBefore()) {
                                OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
                                int s = 0, p = 0, h = 0;
                                s = this.getfile().getInt(eleres3 + target.getUniqueId());
                                p = s / 60;
                                s = s - p * 60;
                                h = p / 60;
                                p = p - h * 60;
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("Sec") + s +
                                        this.getConfig().getString("Minute") + p + this.getConfig().getString("Hour") + h));
                            } else {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("PlayerIsNotPlayedBefore")));
                            }
                        } else {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("NoPermission")));
                        }
                    }
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("BadUsage")));
                }
            }
        }
        return false;
    }

    List<Player> fentvane = new ArrayList<>();
    @EventHandler
    public void Join(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (this.getfile().isSet(eleres1+player.getUniqueId())) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', player.getDisplayName()+ " " + this.getfile().getString(eleres1+player.getUniqueId())));
        }  else {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("DefaultJoin")+player.getDisplayName()+this.getConfig().getString("DefaultJoin2")));
        }
        fentvane.add(player);
    }

    @EventHandler
    public void Leave(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        if (this.getfile().isSet(eleres2+player.getUniqueId())) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', player.getDisplayName()+ " " + this.getfile().getString(eleres2+player.getUniqueId())));
        }  else {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("DefaultLeave")+player.getDisplayName()+this.getConfig().getString("DefaultLeave2")));
        }
        fentvane.remove(player);
    }

    public void uptime() {
        for (int i = 0; i < fentvane.size(); i++) {
            if (this.getfile().isSet(eleres3 + fentvane.get(i).getUniqueId())) {
                this.getfile().set(eleres3 + fentvane.get(i).getUniqueId(), this.getfile().getInt(eleres3 + fentvane.get(i).getUniqueId()) + 1);
                mentesfile();
            } else {
                this.getfile().set(eleres3 + fentvane.get(i).getUniqueId(), 0);
                mentesfile();
            }
        }
    }
}

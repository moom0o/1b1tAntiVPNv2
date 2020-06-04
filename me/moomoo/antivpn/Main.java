package me.moomoo.antivpn;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;

import static java.lang.Thread.sleep;

public class Main extends JavaPlugin implements Listener {
    private HashSet<Player> allowed = new HashSet<Player>();
    public void onEnable() {
        System.out.println("[ENABLED] moomoo's antivpn plugin");
        System.out.println("                                                                   ");
        System.out.println("            bbbbbbbb                                               ");
        System.out.println("  1111111   b::::::b              1111111            tttt          ");
        System.out.println(" 1::::::1   b::::::b             1::::::1         ttt:::t          ");
        System.out.println("1:::::::1   b::::::b            1:::::::1         t:::::t          ");
        System.out.println("111:::::1    b:::::b            111:::::1         t:::::t          ");
        System.out.println("   1::::1    b:::::bbbbbbbbb       1::::1   ttttttt:::::ttttttt    ");
        System.out.println("   1::::1    b::::::::::::::bb     1::::1   t:::::::::::::::::t    ");
        System.out.println("   1::::1    b::::::::::::::::b    1::::1   t:::::::::::::::::t    ");
        System.out.println("   1::::l    b:::::bbbbb:::::::b   1::::l   tttttt:::::::tttttt    ");
        System.out.println("   1::::l    b:::::b    b::::::b   1::::l         t:::::t          ");
        System.out.println("   1::::l    b:::::b     b:::::b   1::::l         t:::::t          ");
        System.out.println("   1::::l    b:::::b     b:::::b   1::::l         t:::::t          ");
        System.out.println("   1::::l    b:::::b     b:::::b   1::::l         t:::::t    tttttt");
        System.out.println("111::::::111 b:::::bbbbbb::::::b111::::::111      t::::::tttt:::::t");
        System.out.println("1::::::::::1 b::::::::::::::::b 1::::::::::1      tt::::::::::::::t");
        System.out.println("1::::::::::1 b:::::::::::::::b  1::::::::::1        tt:::::::::::tt");
        System.out.println("111111111111 bbbbbbbbbbbbbbbb   111111111111          ttttttttttt  ");
        System.out.println("                                                                   ");

        Bukkit.getServer().getPluginManager().registerEvents(this, this);


    }
    public void onDisable() {
        System.out.println("[DISABLED] moomoo's antivpn plugin");
    }
    @EventHandler
    public void onMessage(AsyncPlayerChatEvent evt){
        Player player = evt.getPlayer();
        if(!allowed.contains(player)){
            player.sendMessage("§7You can not chat because you are using a §cvpn.");
            player.sendMessage("§7In order to chat you must §cwhitelist yourself at ");
            player.sendMessage("§6https://whitelist.1b1t.tk/");
            player.sendMessage("§cAfter adding your name make sure to relog.");
            evt.setCancelled(true);
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent evt){
        Player player = evt.getPlayer();
        if(!allowed.contains(player)){
            player.sendMessage("§7You can not chat because you are using a §cvpn.");
            player.sendMessage("§7In order to chat you must §cwhitelist yourself at ");
            player.sendMessage("§6https://whitelist.1b1t.tk/");
            player.sendMessage("§cAfter adding your name make sure to relog.");
            evt.setCancelled(true);
        } else {
            if(evt.getMessage().equalsIgnoreCase("/kill")){
                evt.getPlayer().setHealth(0.0D);
                evt.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent evt){
        evt.setQuitMessage("");
        if(allowed.contains(evt.getPlayer())){
            Bukkit.broadcastMessage("§7" + evt.getPlayer().getName() + " left the game.");
        }
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent evt) throws IOException, ParseException {
        evt.setJoinMessage("");
        allowed.add(evt.getPlayer());
        Player craftplayer = evt.getPlayer();

        String ip = evt.getPlayer().getAddress().toString().replace("/", "").replaceAll(":(.*)", "");
        String player = evt.getPlayer().getName();


        System.out.println(ip);
        System.out.println(evt.getPlayer().getAddress());
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + player + " joined");
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    URL url = new URL("https://whitelist.1b1t.tk/proxycheck?key=key&ip=" + ip + "&player=" + player);
                    URLConnection connection = url.openConnection();
                    HttpURLConnection httpConn = (HttpURLConnection) connection;
                    BufferedReader in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    JSONParser parser = new JSONParser();
                    JSONObject json = (JSONObject) parser.parse(response.toString());
                    System.out.println(response.toString());
                    System.out.println(json);
                    if(json.get("proxy").toString().equals("yes")) {
                        System.out.println(player + " is not allowed to talk because " + ip + " is a vpn.");
                        System.out.println(response.toString());
                        allowed.remove(evt.getPlayer());
                    }
                    if(json.get("proxy").toString().equals("no")){
                        Bukkit.broadcastMessage("§7" + evt.getPlayer().getName() + " joined the game.");
                    }
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }

            }
        });

        t.start();


    }

}

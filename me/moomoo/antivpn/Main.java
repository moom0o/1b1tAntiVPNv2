package me.moomoo.antivpn;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;

public class Main extends JavaPlugin implements Listener {
    private HashSet<Player> allowed = new HashSet<Player>();
    public void onEnable() {
        System.out.println("[ENABLED] moomoo's antivpn plugin, originally made for 1b1t");
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
    }
    public void onDisable() {
        System.out.println("[DISABLED] moomoo's antivpn plugin, originally made for 1b1t. Goodnight.");
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
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent evt) throws IOException, ParseException {
        allowed.add(evt.getPlayer());
        Player craftplayer = evt.getPlayer();
        String ip = evt.getPlayer().getAddress().toString().replace("/", "").replaceAll(":[0-9][0-9][0-9][0-9][0-9]", "");
        String player = evt.getPlayer().getName();
        System.out.println(ip);
        System.out.println(evt.getPlayer().getAddress());
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + player + " joined");
        URL url = new URL("https://whitelist.1b1t.tk/proxycheck?key=yyyyyyyyyyyyy&ip=" + ip + "&player=" + player);
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

    }

}
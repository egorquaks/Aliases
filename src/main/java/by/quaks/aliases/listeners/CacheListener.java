package by.quaks.aliases.listeners;

import by.quaks.aliases.data.AliasesTable;
import by.quaks.aliases.data.AsyncDao;
import by.quaks.aliases.utils.StaticStuff;
import com.destroystokyo.paper.event.player.PlayerConnectionCloseEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class CacheListener implements Listener {
    AsyncDao<AliasesTable> asyncDao;
    public CacheListener(AsyncDao<AliasesTable> asyncDao){
        this.asyncDao = asyncDao;
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        asyncDao.createCacheForUUID(e.getPlayer().getUniqueId());
    }
    @EventHandler
    public void onPlayerLeave(PlayerConnectionCloseEvent e){
        UUID uuid = e.getPlayerUniqueId();
        StaticStuff.removeStatus(uuid);
        StaticStuff.removeAliasName(uuid);
        asyncDao.evictCacheForUUID(uuid);
    }
}

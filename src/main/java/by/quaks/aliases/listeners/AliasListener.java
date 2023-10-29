package by.quaks.aliases.listeners;

import by.quaks.aliases.config.MainConfig;
import by.quaks.aliases.data.AliasesTable;
import by.quaks.aliases.data.AsyncDao;
import by.quaks.aliases.utils.StaticStuff;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.UUID;

public class AliasListener implements Listener {
    private AsyncDao<AliasesTable> aliasesTableAsyncDao;
    private MainConfig mainConfig;
    public AliasListener(AsyncDao<AliasesTable> aliasesTableAsyncDao, MainConfig mainConfig){
        this.aliasesTableAsyncDao = aliasesTableAsyncDao;
        this.mainConfig = mainConfig;
    }
    @EventHandler
    public void playerCommandPreprocessEvent(PlayerCommandPreprocessEvent e){
        if(StaticStuff.getStatus(e.getPlayer().getUniqueId())){
            UUID uuid = e.getPlayer().getUniqueId();
            e.setCancelled(true);
            if(e.getMessage().contains("/alias ")||e.getMessage().contains("/a ")){
                StaticStuff.removeStatus(uuid);
                StaticStuff.removeAliasName(uuid);
                return;
            }
            String aliasName = StaticStuff.getAliasName(uuid);
            aliasesTableAsyncDao.getEntryByUUIDAndAlias(uuid,aliasName).thenAccept(entry->{
                if(entry == null) {
                    aliasesTableAsyncDao.addEntity(new AliasesTable(uuid, aliasName, e.getMessage())).thenRunAsync(() -> {
                        StaticStuff.removeStatus(uuid);
                        StaticStuff.removeAliasName(uuid);
                        aliasesTableAsyncDao.updateCacheForUUID(uuid);
                    });
                }else{
                    entry.setSource(e.getMessage());
                    aliasesTableAsyncDao.updateEntry(entry).thenRunAsync(() -> {
                        StaticStuff.removeStatus(uuid);
                        StaticStuff.removeAliasName(uuid);
                        aliasesTableAsyncDao.updateCacheForUUID(uuid);
                    });
                }
                e.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize(mainConfig.getString("aliasAssigned")
                        .replaceAll("%alias%",aliasName)
                        .replaceAll("%source%",e.getMessage())));
            });
        }
    }
}

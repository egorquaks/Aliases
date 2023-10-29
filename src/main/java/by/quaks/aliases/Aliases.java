package by.quaks.aliases;

import by.quaks.aliases.commands.AliasCommand;
import by.quaks.aliases.config.MainConfig;
import by.quaks.aliases.data.*;
import by.quaks.aliases.listeners.AliasListener;
import by.quaks.aliases.listeners.CacheListener;
//import dev.jorel.commandapi.CommandAPI;
//import dev.jorel.commandapi.CommandAPIBukkitConfig;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;


public final class Aliases extends JavaPlugin {
    private DatabaseManager databaseManager;
    private AsyncDao<AliasesTable> aliasesTableAsyncDao;
    MainConfig config;
    @Override
    public void onLoad() {
//        CommandAPI.onLoad(new CommandAPIBukkitConfig(this).verboseOutput(true).initializeNBTAPI(NBTContainer.class,NBTContainer::new));
    }
    @Override
    public void onEnable() {
        config = new MainConfig(getDataFolder());

//        CommandAPI.onEnable();

        databaseManager = new DatabaseManager("jdbc:h2:./"+getDataFolder()+"/aliases");
        databaseManager.initializeTables(AliasesTable.class);
        aliasesTableAsyncDao = new AsyncDao<>(databaseManager.getDao(AliasesTable.class));
        if(!Bukkit.getOnlinePlayers().isEmpty()){aliasesTableAsyncDao.createCacheForPlayers(Bukkit.getOnlinePlayers());}

        this.getCommand("alias").setExecutor(new AliasCommand(aliasesTableAsyncDao, config));

        getServer().getPluginManager().registerEvents(new AliasListener(aliasesTableAsyncDao, config),this);
        getServer().getPluginManager().registerEvents(new CacheListener(aliasesTableAsyncDao), this);
    }

    @Override
    public void onDisable() {
        aliasesTableAsyncDao.clearCache();
//        CommandAPI.onDisable();
        if (databaseManager != null) {
            try {
                databaseManager.getConnectionSource().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

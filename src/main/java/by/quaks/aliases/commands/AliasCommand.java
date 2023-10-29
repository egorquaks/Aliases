package by.quaks.aliases.commands;

import by.quaks.aliases.config.MainConfig;
import by.quaks.aliases.data.AliasesTable;
import by.quaks.aliases.data.AsyncDao;
import by.quaks.aliases.utils.StaticStuff;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class AliasCommand implements CommandExecutor, TabExecutor {
    private AsyncDao<AliasesTable> aliasesTableAsyncDao;
    private MainConfig mainConfig;
    MiniMessage miniMessage;
    public AliasCommand(AsyncDao<AliasesTable> aliasesTableAsyncDao, MainConfig mainConfig){
        this.aliasesTableAsyncDao = aliasesTableAsyncDao;
        this.mainConfig = mainConfig;
        miniMessage = MiniMessage.miniMessage();
    }
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length==0){
            commandSender.sendMessage(miniMessage.deserialize(mainConfig.getString("description")));
        }
        UUID uuid;
        if(commandSender instanceof Player player){
            uuid = player.getUniqueId();
        }else{
            commandSender.sendMessage(miniMessage.deserialize(mainConfig.getString("description")));
            return true;
        }
        if(args.length>1){
            if(args[0].equals("record")){
                if(args[1].equals("record")){
                    player.sendMessage(miniMessage.deserialize(mainConfig.getString("recordWord")));
                    return true;
                }
                String aliasName = args[1];
                if(StaticStuff.getStatus(uuid)){
                    StaticStuff.removeStatus(uuid);
                    return true;
                }
                player.sendMessage(miniMessage.deserialize(mainConfig.getString("enterCommand").replaceAll("%alias%",aliasName)));
                StaticStuff.setStatus(uuid,true);
                StaticStuff.setAliasName(uuid,aliasName);
                return true;
            }
        }
        if(args.length==1){
            if(args[0].equals("record")){
                player.sendMessage(miniMessage.deserialize(mainConfig.getString("description")));
                return true;
            }
            List<AliasesTable> tables = aliasesTableAsyncDao.getAllEntriesByUUID(uuid).join();
            for (AliasesTable table : tables){
                if(args[0].equals(table.getAlias())){
                    Bukkit.dispatchCommand(commandSender,table.getSource().substring(1));
                    return true;
                }
            }
            player.sendMessage(miniMessage.deserialize(mainConfig.getString("noSuchAlias").replaceAll("%alias%",args[0])));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length<=1){
            List<String> aliases = new ArrayList<>(Collections.singletonList("record"));
            UUID uuid;
            if(commandSender instanceof Player player){
                uuid = player.getUniqueId();
            }else{
                return Collections.emptyList();
            }
            List<AliasesTable> list = aliasesTableAsyncDao.getAllEntriesByUUID(uuid).join();
            if(list!=null){
                for (AliasesTable table :list) {
                    aliases.add(table.getAlias());
                }
            }
            return aliases;
        }
        return Collections.emptyList();
    }
}

package by.quaks.aliases.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class MainConfig {
    private File configFile;
    private FileConfiguration config;

    public MainConfig(File dataFolder) {
        configFile = new File(dataFolder, "config.yml");

        if (!configFile.exists()) {
            dataFolder.mkdirs();
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(configFile);

        setDefaultIfNotSet("description","<u><click:open_url:'https://github.com/qu4ks/Aliases'><gradient:gold:yellow:gold>Aliases_v1.0</gradient></click></u> <click:open_url:'https://www.spigotmc.org/members/quaks.1008503/'><u>by Quaks</u></click>" +
                "\n\nPlugin for creating aliases for commands.<newline>Usage:" +
                "\nType the command /alias record [AliasName]<newline>Next, simply enter the command into the chat for which you want to set an alias;" +
                "\nNow you can simply use /alias [AliasName].");
        setDefaultIfNotSet("enterCommand","<green>Now you can enter a command into the chat for which the alias \"%alias%\" will be assigned.</green>");
        setDefaultIfNotSet("aliasAssigned","<green>Great! You can now use \"/a %alias%\" instead of \"%source%\".</green>");
        setDefaultIfNotSet("noSuchAlias","<red>There is no such alias called \"%alias%\".</red>");
        setDefaultIfNotSet("recordWord","<red>The word \"record\" is reserved by the plugin, do not use it.</red>");
        saveConfig();
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setDefaultIfNotSet(String path, Object defaultValue) {
        if (!config.contains(path)) {
            config.set(path, defaultValue);
        }
    }
    public int getInt(String path){
        return config.getInt(path);
    }
    public String getString(String path){
        return config.getString(path);
    }
}

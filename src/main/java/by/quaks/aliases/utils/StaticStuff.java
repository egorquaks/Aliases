package by.quaks.aliases.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StaticStuff {
    private static Map<UUID, Boolean> statuses = new HashMap<>();
    public static void setStatus(UUID uuid, boolean status){statuses.put(uuid, status);}
    public static boolean getStatus(UUID uuid){return statuses.getOrDefault(uuid,false);}
    public static void removeStatus(UUID uuid){statuses.remove(uuid);}

    private static Map<UUID, String> aliasName = new HashMap<>();
    public static void setAliasName(UUID uuid, String command){aliasName.put(uuid,command);}
    public static String getAliasName(UUID uuid){return aliasName.getOrDefault(uuid,"/alias");}
    public static void removeAliasName(UUID uuid){aliasName.remove(uuid);}
}

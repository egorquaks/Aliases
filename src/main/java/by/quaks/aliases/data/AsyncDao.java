package by.quaks.aliases.data;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.j256.ormlite.dao.Dao;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class AsyncDao<T> {
    private Dao<T, Integer> dao;
    public AsyncDao(Dao<T,Integer> dao){
        this.dao = dao;
    }
    private Cache<UUID, List<T>> uuidCache = Caffeine.newBuilder().build();
    public CompletableFuture<Void> createCacheForUUID(UUID uuid) {
        return CompletableFuture.runAsync(()->{
            uuidCache.get(uuid, key -> {
                Bukkit.getLogger().info("Загружен в кэш "+uuid);
                try {
                    return dao.queryForEq("UUID", uuid);
                } catch (Exception e) {
                    return Collections.emptyList();
                }
            });
        });
    }
    public CompletableFuture<Void> updateCacheForUUID(UUID uuid) {
        return CompletableFuture.runAsync(()->{
            List<T> updatedResult;
            try {
                updatedResult = dao.queryForEq("UUID", uuid);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            if (updatedResult != null) {
                uuidCache.put(uuid, updatedResult);
                Bukkit.getLogger().info("Обновлён кэш "+uuid);
            }
        });
    }
    public CompletableFuture<Void> evictCacheForUUID(UUID uuid) {
        return CompletableFuture.runAsync(()->{
            uuidCache.invalidate(uuid);
            Bukkit.getLogger().info("Выгружено из кэша "+uuid);
        });
    }

    public void clearCache() {
        uuidCache.invalidateAll();
        Bukkit.getLogger().info("Кэш очищен");
    }

    public CompletableFuture<Void> createCacheForPlayers(Collection<? extends Player> players) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (Player player : players) {
            CompletableFuture<Void> future = createCacheForUUID(player.getUniqueId());
            futures.add(future);
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }


    public CompletableFuture<List<T>> getAllEntities() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return dao.queryForAll();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
    }
    public CompletableFuture<Boolean> addEntity(T entity) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                int rowCount = dao.create(entity);
                return rowCount > 0;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        });
    }
    public CompletableFuture<List<T>> getAllEntriesByUUID(UUID uuid) {
        return CompletableFuture.completedFuture(uuidCache.getIfPresent(uuid));
    }
    public CompletableFuture<T> getEntryByUUIDAndAlias(UUID uuid, String name) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Map<String, Object> fieldValues = new HashMap<>();
                fieldValues.put("UUID", uuid);
                fieldValues.put("ALIAS", name);

                List<T> result = dao.queryForFieldValues(fieldValues);

                if (!result.isEmpty()) {
                    return result.get(0);
                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
    }
    public CompletableFuture<Boolean> updateEntry(T updatedEntity) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                int rowCount = dao.update(updatedEntity);
                return rowCount > 0;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        });
    }

}

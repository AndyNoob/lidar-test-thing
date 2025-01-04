package comfortable_andy.lidar;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.RayTraceResult;

public final class LidarMain extends JavaPlugin {

    static final int RES = 32;

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void onEnable() {
        // Plugin startup logic
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, h -> {
            Commands commands = h.registrar();
            commands.register(Commands.literal("lidar").executes(c -> {
                Entity entity = c.getSource().getExecutor();
                if (!(entity instanceof LivingEntity living)) return 0;
                Location location = living.getLocation();
                var dir = location.getDirection();
                location.setPitch(location.getPitch() + 90);
                final double distBtn = 8d / RES;
                var down = location.getDirection().multiply(distBtn);
                location.setPitch(location.getPitch() - 90);
                location.setYaw(location.getYaw() + 90);
                var right = location.getDirection();
                right.setY(0);
                right.normalize().multiply(distBtn);
                for (int x = -RES / 2; x < RES / 2 - 1; x++) {
                    for (int y = -RES / 2; y < RES / 2 - 1; y++) {
                        var loc = living.getEyeLocation()
                                .add(down.clone().multiply(y))
                                .add(right.clone().multiply(x));
                        // skip the rest if origin is inside block
                        if (loc.getBlock().isSolid()) continue;
                        var curDir = dir.clone();
                        curDir.rotateAroundAxis(
                                down.clone().multiply(-1),
                                Math.toRadians(-x * 5)
                        );
                        curDir.rotateAroundAxis(
                                right,
                                Math.toRadians(-y * 5)
                        );
                        RayTraceResult result = living.getWorld().rayTraceBlocks(loc, curDir, 10);
                        Location display = result == null ? curDir.multiply(10).toLocation(living.getWorld()).add(living.getEyeLocation()) : result.getHitPosition().toLocation(living.getWorld());
                        living.getWorld().spawn(
                                display,
                                ShulkerBullet.class,
                                s -> {
                                    s.setGravity(false);
                                    Bukkit.getScheduler().runTaskLater(this, s::remove, 20 * 2);
                                }
                        );
                    }
                }
                return 1;
            }).build());
        });
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

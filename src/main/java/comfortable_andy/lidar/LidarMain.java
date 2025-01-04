package comfortable_andy.lidar;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;

public final class LidarMain extends JavaPlugin {

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
                final double distBtn = 0.2;
                var down = location.getDirection().multiply(distBtn);
                location.setPitch(location.getPitch() - 90);
                location.setYaw(location.getYaw() + 90);
                var right = location.getDirection();
                right.setY(0);
                right.normalize().multiply(distBtn);
                for (int x = -25; x < 25; x++) {
                    for (int y = -10; y < 10; y++) {
                        var loc = location.clone()
                                .add(down.clone().multiply(y))
                                .add(right.clone().multiply(x));
                        living.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, loc, 1);
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

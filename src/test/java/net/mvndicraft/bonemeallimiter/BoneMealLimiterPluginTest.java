package net.mvndicraft.bonemeallimiter;

import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BoneMealLimiterPluginTest {

    private ServerMock server;
    private World world;
    private BoneMealLimiterPlugin plugin;
    private PlayerMock player;

    @BeforeEach
    public void setUp() {
        // server = MockBukkit.mock();
        // server.addSimpleWorld("world");
        // world = server.getWorld("world");
        // plugin = MockBukkit.load(BoneMealLimiterPlugin.class);

        // player = server.addPlayer();

        // player.setGameMode(GameMode.SURVIVAL);
    }

    // example test
    @Test
    void testPlace() {
        // Block block = server.getWorld("world").getBlockAt(0, 0, 0);
        // block.setType(Material.CARROTS);
        // player.simulateBlockPlace(Material.BONE_MEAL, new Location(world, 0, 0, 0));
        // assertEquals(Material.CARROTS, block.getType());
    }
}

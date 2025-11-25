package lucas.games.brogue.backend.entities;


import lucas.games.brogue.backend.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreatureTest {

    @Test
    void testPlayerInitialization() {
        Position start = new Position(10, 10);
        Player player = new Player(start);

        assertEquals(100, player.getCurrentHp());
        assertEquals("You", player.getName());
        assertEquals('@', player.getSymbol());
        assertEquals(start, player.getPosition());
        assertFalse(player.isDead());
    }

    @Test
    void testDamageAndDeath() {
        Player p = new Player(new Position(0, 0));

        p.takeDamage(30);
        assertEquals(70, p.getCurrentHp());
        assertFalse(p.isDead());

        p.takeDamage(70);
        assertEquals(0, p.getCurrentHp(), "HP should not go below 0");
        assertTrue(p.isDead());

        p.takeDamage(10);
        assertEquals(0, p.getCurrentHp(), "HP should remain at 0 after death");
    }

    @Test
    void testHealing() {
        Player p = new Player(new Position(0, 0));
        p.takeDamage(50);

        p.heal(25);
        assertEquals(75, p.getCurrentHp());

        p.heal(100);
        assertEquals(100, p.getCurrentHp(), "Should not exceed max HP");
    }
}
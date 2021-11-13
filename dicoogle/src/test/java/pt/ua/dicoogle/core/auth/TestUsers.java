/**
 * Copyright (C) 2014  Universidade de Aveiro, DETI/IEETA, Bioinformatics Group - http://bioinformatics.ua.pt/
 *
 * This file is part of Dicoogle/dicoogle.
 *
 * Dicoogle/dicoogle is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dicoogle/dicoogle is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Dicoogle.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ua.dicoogle.core.auth;

import org.junit.*;
import org.junit.rules.Stopwatch;
import pt.ua.dicoogle.server.users.User;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

/**
 * Created by bastiao on 23/01/16.
 */
public class TestUsers {
    @BeforeClass
    public static void setUpClass() {}

    @AfterClass
    public static void tearDownClass() {}

    @Before
    public void setUp() {}

    @After
    public void tearDown() {}

    @Test
    public void authentication() {
        String passwd = "123";
        User u = User.create("nat", false, passwd);
        assertTrue(u.verifyPassword(passwd));

        passwd = "correctHorseBatteryStaple";
        assertTrue(u.changePassword("123", passwd));
        assertTrue(u.verifyPassword(passwd));

        passwd = "VeryStrongPassword!!11";
        assertTrue(u.resetPassword(passwd));
        assertTrue(u.verifyPassword(passwd));
    }

    @Test
    public void meranie() {
        String password = "test long password...";
        User u = User.create("User", false, password);
        long start = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            u.verifyPassword(password.toCharArray());
        }
        long end = System.nanoTime();
        long nano = end - start;
        long millis = TimeUnit.NANOSECONDS.toMillis(nano);
        long sec = TimeUnit.NANOSECONDS.toSeconds(nano);
        long millisOnly = millis - TimeUnit.SECONDS.toMillis(sec);
        long nanoOnly = nano - TimeUnit.MILLISECONDS.toNanos(millisOnly) - TimeUnit.SECONDS.toNanos(sec);
        System.out.printf("Time = %ds, %dms, %dns.", sec, millisOnly, nanoOnly);
        // 100x:
        // OLD: Time = 5s, 540ms, 555900ns.
        // NEW: Time = 0s,   1ms, 333600ns.
        // Speedup: 4 154x

        // 1000x:
        // OLD: Time = 53s, 950ms, 984500ns.
        // NEW: Time = 0s, 6ms, 804600ns.
        // Speedup: 7928x
    }

}

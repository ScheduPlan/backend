package de.hofuniversity.assemblyplanner.util;

import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

public class JpaUtil {
    public static void reset(TestEntityManager em) {
        em.flush();
        em.clear();
    }
}

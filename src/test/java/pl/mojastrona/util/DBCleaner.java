package pl.mojastrona.util;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DBCleaner {

    private final EntityManager entityManager;

    @Transactional
    void clean() {
        //wylaczyc integralnosc bazy
        // odkryc jakie sa tabelki
        // usunac dane ze wszystkich tabelek
        // właczyc
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

        List<String> tables = ((List<Object[]>) entityManager.createNativeQuery("SHOW tables").getResultList())
                .stream()
                .map(row -> row[0].toString())
                .toList();

        tables.forEach(table -> entityManager.createNativeQuery(String.format("DELETE FROM %s", table)).executeUpdate());
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }
}

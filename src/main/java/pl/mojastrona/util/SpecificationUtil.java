package pl.mojastrona.util;

import jakarta.persistence.criteria.CriteriaQuery;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SpecificationUtil {

    public boolean isABoolean(CriteriaQuery<?> query) {
        return query.getResultType() == Long.class || query.getResultType() == long.class;
    }
}

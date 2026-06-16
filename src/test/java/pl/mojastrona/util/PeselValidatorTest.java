package pl.mojastrona.util;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;
class PeselValidatorTest {

    @ParameterizedTest
    @CsvSource({
            "0123456895, false",
            "894645135456, false",
            "pakskfjsodf, false",
            "91102584149, true",
            "51091084589, true",
            "11111111111, false"
    })
    void givenPesel_whenIsPeselValid_thenExpectedResult(String pesel, boolean expectedIsValid) {

        boolean peselValid = PeselValidator.isPeselValid(pesel);


        Assertions.assertThat(peselValid).isEqualTo(expectedIsValid);
    }
}
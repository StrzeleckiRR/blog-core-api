package pl.mojastrona.bmi;

import org.assertj.core.api.Assertions;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import pl.mojastrona.BaseUnitTest;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class BmiCalculatorTest extends BaseUnitTest {

    @InjectMocks
    private BmiCalculator underTest;


    @ParameterizedTest
    @CsvSource({
            "50, 190, 13.85, NIEDOWAGA",
            "80, 180, 24.69, OK",
            "120, 200, 30, NADWAGA"
    })
    void givenWeightAndHeight_whenCalculate_thenExpectedBmi(Integer weight, Integer height, double expectedBmi, BmiNote expectedBmiNote ) {


        BmiCalculation bmiCalculation = underTest.calculate(weight, height);

        assertThat(bmiCalculation).isNotNull();
        assertThat(bmiCalculation.getBmi()).isEqualTo(expectedBmi, Offset.offset(0.005));
        assertThat(bmiCalculation.getBmiNote()).isEqualTo(expectedBmiNote);
    }
}
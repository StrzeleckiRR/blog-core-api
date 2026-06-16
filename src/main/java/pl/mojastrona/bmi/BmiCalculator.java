package pl.mojastrona.bmi;

import org.springframework.stereotype.Component;


@Component
class BmiCalculator {

    BmiCalculation calculate(Integer weight, Integer heightInCm){

        //1. Oblicz BMI i kasyfikację
        double heightInMeters = heightInCm / 100.0;
        double bmi = weight / heightInMeters / heightInMeters;


        BmiNote bmiNote;
        if(bmi>25){
            bmiNote = BmiNote.NADWAGA;
        }else if (bmi<25 && bmi>=20 ){
            bmiNote = BmiNote.OK;
        }else {
            bmiNote = BmiNote.NIEDOWAGA;
        }

        return new BmiCalculation(bmi, bmiNote);
    }
}
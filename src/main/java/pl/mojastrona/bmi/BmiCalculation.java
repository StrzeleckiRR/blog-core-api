package pl.mojastrona.bmi;

class BmiCalculation {
    private final double bmi;

    private final BmiNote bmiNote;

    public BmiCalculation(double bmi, BmiNote bmiNote) {
        this.bmi = bmi;
        this.bmiNote = bmiNote;
    }

    public double getBmi() {
        return bmi;
    }

    public BmiNote getBmiNote() {
        return bmiNote;
    }
}

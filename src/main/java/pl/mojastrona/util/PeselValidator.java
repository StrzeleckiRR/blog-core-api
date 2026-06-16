package pl.mojastrona.util;

public class PeselValidator {

    public static boolean isPeselValid(String pesel){
        boolean peselLengthValid = isPeselLengthValid(pesel);
        return peselLengthValid && onlyNumbers(pesel) && isCrtValid(pesel);
    }
    private static boolean isPeselLengthValid(String pesel){
        boolean b = pesel != null && pesel.length() == 11;
        return b;
    }

    private static boolean onlyNumbers(String pesel){
        for(int i=0;i<pesel.length();i++){
            char c = pesel.charAt(i);
            //not true = false ,  not false = true
            if(!Character.isDigit(c)){
                return false;
            }
        }
        return true;
    }
    private static boolean isCrtValid(String pesel) {
        int sum = 0;
        int[] weights = {1,3,7,9,1,3,7,9,1,3};

        //  Wydobyc cyfry
        for(int i=0;i<10;i++){
            sum += multiplyByWeight(pesel, i, weights[i]);
        }

        // suma kontrolna
        char c = pesel.charAt(10);
        String charAsStr = String.valueOf(c);
        int digit = Integer.parseInt(charAsStr);

        String sumAsStr = String.valueOf(sum);
        String jednosc = sumAsStr.substring(sumAsStr.length() - 1);
        int jednoscAsNumber = Integer.parseInt(jednosc);

        return (jednoscAsNumber == 0 && digit == 0)
                || (jednoscAsNumber > 0 && 10 - jednoscAsNumber == digit);
    }

    private static int multiplyByWeight(char c, int weight){
        String charAsStr = String.valueOf(c);
        int digit = Integer.parseInt(charAsStr);
        int multiply = digit * weight;
        return multiply;
    }

    //overload przeciążenie
    private static int multiplyByWeight(String pesel, int index, int weight){
        char c = pesel.charAt(index);
        return multiplyByWeight(c,weight);
    }
}


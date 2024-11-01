package itstep.learning.android_first_project;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class CalculatorModel {

    private double firstArg;
    private double secondArg;


    private StringBuilder inputStr = new StringBuilder();
    private String action = "";

    private State state;

    private enum State {
        firstArgInput,
        operationSelected,
        secondArgInput,
        resultShow
    }

    public CalculatorModel() {
        state = State.firstArgInput;
    }


    public void onNumPressed(String buttonText) {

        if (state == State.resultShow) {
            state = State.firstArgInput;
            inputStr.setLength(0);
        }
        if (state == State.operationSelected) {
            state = State.secondArgInput;
            inputStr = new StringBuilder();
        }

        if (inputStr.length() < 9) {
            switch (buttonText) {

                case "0":
                    if (inputStr.length() != 0) inputStr.append("0");
                    break;
                case "1":
                    inputStr.append("1");
                    break;
                case "2":
                    inputStr.append("2");
                    break;
                case "3":
                    inputStr.append("3");
                    break;
                case "4":
                    inputStr.append("4");
                    break;
                case "5":
                    inputStr.append("5");
                    break;
                case "6":
                    inputStr.append("6");
                    break;
                case "7":
                    inputStr.append("7");
                    break;
                case "8":
                    inputStr.append("8");
                    break;
                case "9":
                    inputStr.append("9");
                    break;
            }
        }

    }

    public void onActionPressed(String actionText) throws ParseException {

        if (state == State.secondArgInput && inputStr.length() > 0) {
            secondArg = Integer.parseInt((inputStr.toString()));
            state = State.resultShow;
            inputStr.setLength(0);
            switch (action) {
                case "AC":
                    System.out.println("ac");
                    break;
                case "\u232B":
                    System.out.println("\u232B");
                    break;

                case "C":
                    inputStr.deleteCharAt(inputStr.length() - 1);
                    state = State.firstArgInput;
                    break;
                case "%":
                    System.out.println("%");
                    break;
                case "/": {
                    inputStr = new StringBuilder();
                    inputStr.append(firstArg / secondArg);

                }
                break;
                case "x": {
                    inputStr = new StringBuilder();
                    inputStr.append(firstArg * secondArg);

                }
                break;
                case "-": {
                    inputStr = new StringBuilder();
                    inputStr.append(firstArg - secondArg);

                }
                break;
                case "+": {
                    inputStr = new StringBuilder();
                    inputStr.append(firstArg + secondArg);

                }
                break;
                case "√": {
                    inputStr = new StringBuilder();
                    inputStr.append(Math.pow(firstArg, 1 / secondArg));

                }
                break;
                case "^": {
                    inputStr = new StringBuilder();
                    inputStr.append(Math.pow(firstArg, secondArg));

                }
                break;


                case "=":
                    System.out.println("=");
                    break;

                case "log": {
                    inputStr = new StringBuilder();
                    inputStr.append(Math.log(firstArg));

                }
                break;
                case "+-": {
                    int num = Integer.parseInt(inputStr.toString());
                    if (num < 0 && inputStr.indexOf("-") > -1) {
                        inputStr.deleteCharAt(0);
                    } else if (num > 0 && inputStr.indexOf("-") == -1) {
                        inputStr.insert(0, '-');
                    }
                }
                return;
                case ".": {
                    inputStr.append('.');
                }
                return;


            }

        } else if (inputStr.length() > 0 && state == State.firstArgInput) {

            switch (actionText) {

                case "AC": {
                    firstArg = 0;
                    secondArg = 0;
                    inputStr = new StringBuilder();
                    inputStr.append(0);
                    state = State.firstArgInput;
                    return;
                }
                case "\u232B": {

                    if (secondArg != 0) {
                        secondArg = 0;

                    } else if (firstArg != 0) {
                        firstArg = 0;
                    }
                    inputStr = new StringBuilder();
                    inputStr.append(0);
                    state = State.firstArgInput;
                    return;
                }

                case "C": {
                    if (inputStr.length() <= 1) {
                        inputStr.setCharAt(0, '0');
                    } else {
                        inputStr.deleteCharAt(inputStr.length() - 1);
                    }
                    state = State.firstArgInput;

                    return;
                }
                case "+": {
                    firstArg =  NumberFormat.getInstance(Locale.ROOT).parse(inputStr.toString()).doubleValue();

                    inputStr = new StringBuilder();
                    inputStr.append("+");
                    state = State.operationSelected;
                    action = "+";
                }
                return;
                case "-": {
                    firstArg =  NumberFormat.getInstance(Locale.ROOT).parse(inputStr.toString()).doubleValue();
                    inputStr = new StringBuilder();
                    inputStr.append("-");
                    state = State.operationSelected;
                    action = "-";
                }
                return;
                case "/": {
                    firstArg =  NumberFormat.getInstance(Locale.ROOT).parse(inputStr.toString()).doubleValue();
                    inputStr = new StringBuilder();
                    inputStr.append("/");
                    state = State.operationSelected;
                    action = "/";
                }
                return;
                case "x": {
                    firstArg =  NumberFormat.getInstance(Locale.ROOT).parse(inputStr.toString()).doubleValue();
                    inputStr = new StringBuilder();
                    inputStr.append("x");
                    state = State.operationSelected;
                    action = "x";
                }
                return;
                case "√": {
                    firstArg =  NumberFormat.getInstance(Locale.ROOT).parse(inputStr.toString()).doubleValue();
                    inputStr = new StringBuilder();
                    inputStr.append("√");
                    state = State.operationSelected;
                    action = "√";
                }
                return;
                case "log": {
                    inputStr = new StringBuilder();
                    inputStr.append(Math.log(firstArg));
                    state = State.resultShow;
                    action = "log";

                }
                break;
                case "%": {
                    firstArg = NumberFormat.getInstance(Locale.ROOT).parse(inputStr.toString()).doubleValue() / 100;
                    inputStr = new StringBuilder();
                    inputStr.append(firstArg);
                    action = "x";
                    state = State.operationSelected;

                }
                return;

                case "+-": {
                    double num =   firstArg =  NumberFormat.getInstance(Locale.ROOT).parse(inputStr.toString()).doubleValue();
                    if (num < 0 && inputStr.indexOf("-") > -1) {
                        inputStr.deleteCharAt(0);
                    } else if (num > 0 && inputStr.indexOf("-") == -1) {
                        inputStr.insert(0, '-');
                    }
                }
                return;

                case ".": {
                    inputStr.append('.');
                    action = ".";

                }
                return;


            }

            firstArg =  NumberFormat.getInstance(Locale.ROOT).parse(inputStr.toString()).doubleValue();
            state = State.operationSelected;
            action = actionText;

        }
    }


    public String getText() {
        while (inputStr.toString().startsWith("0") && inputStr.length() > 1 && inputStr.indexOf(".") != 1) {
            inputStr.deleteCharAt(0);
        }
        if(action.equals(".")) {
            return inputStr.toString();
        }
        int dotIndex = inputStr.indexOf(".");
        if (dotIndex != -1) {
            String strTemp = inputStr.substring(0, dotIndex);
            try {
                double numTemp = NumberFormat.getInstance(Locale.ROOT).parse(inputStr.toString()).doubleValue();
                double numTempRes = NumberFormat.getInstance(Locale.ROOT).parse(strTemp).doubleValue();
                if (numTemp == numTempRes) {
                    inputStr.delete(dotIndex, inputStr.length());
                }


            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }


        switch (state) {
            case resultShow:
                String res = inputStr.toString();
                state = State.firstArgInput;
                inputStr = new StringBuilder();
                firstArg = 0;
                secondArg = 0;
                return res;
            default:
                return inputStr.toString();
        }
    }


}



package com.dal.group24.inputs;

import java.util.Scanner;

public class UserInput implements IInputs {
    @Override
    public String getStringInput() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }
}

package test.brava.main;

import test.brava.logic.BravaConnector;

public class Main {

    public static void main(String[] args) {
        BravaConnector bra = new BravaConnector("http://ricksoft-brava-test.westus.cloudapp.azure.com:8080");

        bra.run();
    }

}

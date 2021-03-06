package com.simulation;


import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;


/**
 * Simulation back-end logic
 * @version 2.0.7
 * @author Patryk Lasoń, Hubert Bełkot
 *
 *
 *<p>Simple war simulation. There are infantry, tank, mobile base, base and trap units which can interact with others. Each unit
 * has it's own features. The program sets the startup parameters from user input and runs simulation by initializing each unit's
 * movement. The simulation ends within set number of iterations. </p>
 */
public class Simulation{
    /**
     * size of the side of the square map
     */
    static int size = 100;
    /**
     * number of iterations
     */
    static int iterations = 1000;
    /**
     * number of infantry for both teams
     */
    static int infantry = 8000;
    /**
     * number of tanks for both teams
     */
    static int tanks = 2000;
    /**
     * number of mobile bases
     */
    static int mobiles = 3000;
    /**
     * number of bases
     */
    static int bases = 1000;
    /**
     * number of traps
     */
    static int traps = 3000;
    /**
     * array of file saving user choices
     */
    static boolean[] uFileChoice = new boolean[5];
    static {
        uFileChoice[0] = true;
        uFileChoice[4] = true;
    }
    //0 - infantry
    //1 - tanks
    //2 - traps
    //3 - mobiles
    //4 - stats


    /**
     * start consoleApp interface
     * @param args default Java argument
     */
    public static void main(String[] args) {
        recalculateParams();
        ConsoleApp.menu();
    }

    /**
     * runs simulation by initializing creation of map and units, and then initializing unit's movement
     * saves important stats to file
     */
    public static void simulation(){

        File file = new File("Results.txt");
        if(!file.exists()){
            try{
                if(file.createNewFile())
                    System.out.println("Plik został utworzony");
            }catch (Exception exception){
                System.out.println(exception.getMessage());
            }
        }
        if (file.canWrite()){
            try {
                FileWriter fileWriter = new FileWriter(file,false);
                Formatter formatter = new Formatter(fileWriter);
                formatter.format("%s \r\n", "Symulacja wykonana dla danych początkowych:");

                Map map = new Map(size);

                UnitCreator unitCreator = new UnitCreator(map, infantry/2, tanks/2, mobiles,bases,traps);
                List<Unit> unitList = unitCreator.getUnitList();

                int createdInfantry = ArmyUnit.getNumOfALiveInfantry();
                int createdTanks = ArmyUnit.getNumOfALiveTanks();

                formatter.format("%s \r\n", "Czas rozpoczęcia: " + LocalDateTime.now());
                formatter.format("%s \r\n", "Rozmiar boku mapy: " + size);
                formatter.format("%s \r\n", "Ilość iteracji: " + iterations);
                formatter.format("%s \r\n", "Liczba jednostek piechoty: " + createdInfantry);
                formatter.format("%s \r\n", "Liczba czołgów: " + createdTanks);
                formatter.format("%s \r\n", "Liczba mobilnych baz: " + mobiles);
                formatter.format("%s \r\n", "Liczba głównych baz: " + bases);
                formatter.format("%s \r\n\r\n", "Ilość pułapek: " + traps);

                ArmyUnit.setDeadArmy(0);
                Trap.setDeadTrap(0);
                MovingBase.setDeadMovingBase(0);

                for(int i = 0; i < iterations; ++i) {
                    for (Unit unit : unitList) {
                        if (unit instanceof ArmyUnit) {
                            ((ArmyUnit) unit).move(map, unitCreator);
                        }
                        else if (unit instanceof MovingBase) {
                            ((MovingBase) unit).move(map, unitCreator);
                        }

                        if(ArmyUnit.getDeadArmy()==1 && uFileChoice[0]) {
                            formatter.format("%s \r\n", "Jednostka "+ unit.id+" piechoty została zabita w " + (i + 1)+" iteracji");
                            ArmyUnit.setDeadArmy(0);
                        }
                        else if(ArmyUnit.getDeadArmy()==2 && uFileChoice[1]){
                            formatter.format("%s \r\n", "Czołg "+ unit.id+" został zniszczony w " + (i + 1)+" iteracji");
                            ArmyUnit.setDeadArmy(0);
                        }
                        else if(Trap.getDeadTrap()==3 && uFileChoice[2]){
                            formatter.format("%s \r\n", "Pułapka "+ unit.id+" została zniszczona w " + (i + 1)+" iteracji");
                            Trap.setDeadTrap(0);
                        }
                        else if(MovingBase.getDeadMovingBase()==4 && uFileChoice[3]){
                            formatter.format("%s \r\n", "Baza poruszająca się "+ unit.id+" została zniszczona w " + (i + 1) + " iteracji");
                            MovingBase.setDeadMovingBase(0);
                        }
                    }
                }

                int team1 = ArmyUnit.getNumOfAliveTeam1();
                int team2 = ArmyUnit.getNumOfAliveTeam2();
                String winner;

                if(team1 > team2){
                    winner="Wygrała drużyna 1";}
                else if(team2 > team1){
                    winner="Wygrała drużyna 2";}
                else{
                    winner="Remis";}

                formatter.format("%s \r\n"," ");

                if(uFileChoice[4]){
                    formatter.format("%s \r\n", "Wynik symulacji dla powyższych danych:");
                    formatter.format("%s \r\n","Jednostek żywych: " + ArmyUnit.getNumOfAliveUnits());
                    formatter.format("%s \r\n", "Żywej piechoty: " + ArmyUnit.getNumOfALiveInfantry());
                    formatter.format("%s \r\n", "Działających czołgów: " + ArmyUnit.getNumOfALiveTanks());
                    formatter.format("%s \r\n", "Śmierci jednostek: " + (createdInfantry + createdTanks  - ArmyUnit.getNumOfAliveUnits()));
                    formatter.format("%s \r\n", "Stoczonych bitw: " + ArmyUnit.getNumOfBattles());
                    formatter.format("%s \r\n", "Wykonanych ataków: " + ArmyUnit.getNumOfAttacks());
                }
                formatter.format("%s \r\n", "Rezultat symulacji: " + winner + " (" + Math.max(team1, team2) + " do " + Math.min(team1, team2) + ")");

                formatter.close();
                fileWriter.close();

            }catch (Exception exception){
                System.out.println(exception.getMessage());
            }
        }
        Desktop desktop = Desktop.getDesktop();
        if(Desktop.isDesktopSupported()) {
            try {
                desktop.open(file);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Błąd automatycznego otwierania pliku wynikowego!");
            }
        }
        else
            System.out.println("Twój system nie wspiera automatycznego otwarcia pliku! Możesz go znaleźć w folderze" +
                    " programu.");

        ArmyUnit.resetArmyStats();
    }

    /**
     * displays menu for changing params in console interface
     */
    static void printParams(){
        int uChoice;
        Scanner scanner = new Scanner(System.in);

        do {
            System.out.println("=============== PARAMETRY ===============");
            System.out.println("wybierz odpowiednią cyfrę aby edytować: ");
            System.out.println("[0] Wróć do menu");
            System.out.println("[1] Mapa: " + size + "x" + size + " = " + (size * size) + ". Uwaga: zmianie ulegnie ilość jednostek!");
            System.out.println("[2] Iteracje: " + iterations);
            System.out.println("[3] Piechota: " + infantry);
            System.out.println("[4] Czołgi: " + tanks);
            System.out.println("[5] Mobilne bazy: " + mobiles);
            System.out.println("[6] Bazy: " + bases);
            System.out.println("[7] Pułapki: " + traps);
            do {
                System.out.print("Twój wybór: ");
                while (!scanner.hasNextInt()) {
                    scanner = new Scanner(System.in);
                    System.out.print("Podano niepoprawną wartość, spróbuj ponownie: ");
                }
                uChoice = scanner.nextInt();
                if (uChoice < 0 || uChoice > 7)
                    System.out.println("niepoprawny wybór ");
            } while (uChoice < 0 || uChoice > 7);

            setStartupParams(uChoice);
        }while(uChoice != 0);
    }

    /**
     * allows user to set startup params in console interface
     * @param choice which parameter will be changed
     */
    static void setStartupParams(int choice) {
        Scanner scanner = new Scanner(System.in);
        boolean success = false;

        switch (choice) {
            case 1 -> {
                do{
                    System.out.println("Aktualny rozmiar mapy: " + size + "x" + size + " = " + size*size);
                    System.out.print("Proszę podać rozmiar boku mapy (zakres 10-1000): ");
                    while (!scanner.hasNextInt()) {
                        scanner = new Scanner(System.in);    //clears scanner buffer
                        System.out.println("Wprowadzono nieprawidłową wartość!");
                        System.out.print("Proszę podać rozmiar boku mapy (zakres 10-1000): ");
                    }
                    int x = scanner.nextInt();
                    if (x >= 10 && x <= 1000) {
                        size = x;
                        recalculateParams();
                        success = true;
                    } else
                        System.out.println("Podano nieprawdiłowy rozmiar (10 - 1000)!");
                }while(!success);
            }
            case 2-> {
                do {
                    System.out.println("Aktualnie iteracji: " + iterations);
                    System.out.print("Proszę podać ilość iteracji (max 100 000): ");
                    while (!scanner.hasNextInt()) {
                        scanner = new Scanner(System.in);
                        System.out.println("Wprowadzono nieprawidłową wartość!");
                        System.out.print("Proszę podać ilość iteracji (max 100 000): ");
                    }
                    int x = scanner.nextInt();
                    if (x > 0 && x <= 100000) {
                        iterations = x;
                        success = true;
                    } else
                        System.out.println("Podano nieprawidłową liczbę iteracji (1-100 000)!");
                } while (!success);
            }
            case 3 -> {
                System.out.println("Pierwotna wartość: " + infantry);
                infantry = 0;
                do{
                    System.out.println("wolne miejsca w aktualnym stanie: " + freeArmyFields());
                    System.out.print("Proszę podać ilość jednostek piechoty: ");
                    while (!scanner.hasNextInt()) {
                        scanner = new Scanner(System.in);
                        System.out.println("Wprowadzono nieprawidłową wartość!");
                        System.out.print("Proszę podać ilość jednostek piechoty: ");
                    }
                    int x = scanner.nextInt();
                    if(x>=0 && x <= freeArmyFields()){
                        infantry = x;
                        success = true;
                    }
                    else if(x > freeArmyFields())
                        System.out.println(x - freeArmyFields() + " jednostek nie ma miejsca, podaj mniejszą ilość");
                }while(!success);
            }
            case 4-> {
                System.out.println("Pierwotna wartość: " + tanks);
                tanks = 0;
                do{
                    System.out.println("wolne miejsca w aktualnym stanie: " + freeArmyFields());
                    System.out.print("Proszę podać ilość czołgów: ");
                    while (!scanner.hasNextInt()) {
                        scanner = new Scanner(System.in);
                        System.out.println("Wprowadzono nieprawidłową wartość!");
                        System.out.print("Proszę podać ilość czołgów: ");
                    }
                    int x = scanner.nextInt();
                    if(x>=0 && x <= freeArmyFields()){
                        tanks = x;
                        success = true;
                    }
                    else if(x > freeArmyFields())
                        System.out.println(x - freeArmyFields() + " jednostek nie ma miejsca, podaj mniejszą ilość");
                }while(!success);
            }
            case 5 -> {
                System.out.println("Pierwotna wartość: " + mobiles);
                mobiles = 0;
                do{
                    System.out.println("wolne miejsca w aktualnym stanie: " + freeNeutralFields());
                    System.out.print("Proszę podać ilość mobilnych baz: ");
                    while (!scanner.hasNextInt()) {
                        scanner = new Scanner(System.in);
                        System.out.println("Wprowadzono nieprawidłową wartość!");
                        System.out.print("Proszę podać ilość mobilnych baz: ");
                    }
                    int x = scanner.nextInt();
                    if(x>=0 && x <= freeNeutralFields()){
                        mobiles = x;
                        success = true;
                    }
                    else if(x > freeNeutralFields())
                        System.out.println(x - freeNeutralFields() + " jednostek nie ma miejsca, podaj mniejszą ilość");
                }while(!success);
            }
            case 6 -> {
                System.out.println("Pierwotna wartość: " + bases);
                bases = 0;
                do{
                    System.out.println("wolne miejsca w aktualnym stanie: " + freeNeutralFields());
                    System.out.print("Proszę podać ilość baz: ");
                    while (!scanner.hasNextInt()) {
                        scanner = new Scanner(System.in);
                        System.out.println("Wprowadzono nieprawidłową wartość!");
                        System.out.print("Proszę podać ilość baz: ");
                    }
                    int x = scanner.nextInt();
                    if(x>=0 && x  <= freeNeutralFields()){
                        bases = x;
                        success = true;
                    }
                    else if(x > freeNeutralFields())
                        System.out.println(x - freeNeutralFields() + " jednostek nie ma miejsca, podaj mniejszą ilość");
                }while(!success);
            }
            case 7 -> {
                System.out.println("Pierwotna wartość: " + traps);
                traps = 0;
                do{
                    System.out.println("wolne miejsca w aktualnym stanie: " + freeNeutralFields());
                    System.out.print("Proszę podać ilość pułapek: ");
                    while (!scanner.hasNextInt()) {
                        scanner = new Scanner(System.in);
                        System.out.println("Wprowadzono nieprawidłową wartość!");
                        System.out.print("Proszę podać ilość pułapek: ");
                    }
                    int x = scanner.nextInt();
                    if(x>=0 && x  <= freeNeutralFields()){
                        traps = x;
                        success = true;
                    }
                    else if(x > freeNeutralFields())
                        System.out.println(x - freeNeutralFields() + " jednostek nie ma miejsca, podaj mniejszą ilość");
                }while(!success);
            }
        }

    }

    /**
     *
     * @return number of free fields for army units
     */
    static int freeArmyFields(){
        return size*size - infantry - tanks;
    }

    /**
     *
     * @return number of free fields for neutral units
     */
    static int freeNeutralFields(){
        return size*size - mobiles - bases - traps;
    }

    /**
     * recalculates quantities of units
     */
    public static void recalculateParams(){
        infantry = (int)(size*size * 0.75);
        tanks = (int)(size*size * 0.25);
        mobiles = (int)(size*size * 0.3);
        bases = (int)(size*size * 0.1);
        traps = (int)(size*size * 0.3);
    }

    /**
     * recalculates amount of specific units based on default proportions. Used when user
     * lowers map size and army units cant fit in. Tries to fill as many fields as possible and
     * takes care of army units quantities being even numbers.
     */
    public static void recalculateArmyParams() {
        int fields = size * size;
        if((0.75 * fields) % 2 == 0 && (0.25 * fields) % 2 == 0) {
            infantry = (int) (fields * 0.75);
            tanks = (int) (fields * 0.25);
        }
        else if((0.75 * fields) % 2 == 1 && (0.25 * fields) % 2 == 0){
            infantry = (int) ((fields - 1) * 0.75);
            tanks = (int) (fields * 0.25);
        }
        else if((0.75 * fields) % 2 == 0 && (0.25 * fields) % 2 == 1){
            infantry = (int) (fields * 0.75);
            tanks = (int) ((fields - 1) * 0.25);
        }
        else if((0.75 * fields) % 2 == 1 && (0.25 * fields) % 2 == 1){
            infantry = (int) ((fields - 1) * 0.75);
            tanks = (int) ((fields - 1) * 0.25);
        }

        if(fields - infantry - tanks == 2)
            infantry+=2;
    }

    /**
     * recalculates amount of specific units based on default proportions. Used when user
     * lowers map size and neutral units cant fit in.
     */
    public static void recalculateNeutralParams(){
        mobiles = (int)(size*size * 0.3);
        bases = (int)(size*size * 0.1);
        traps = (int)(size*size * 0.3);
    }

    public static int getSize() {
        return size;
    }

    public static void setSize(int size) {
        Simulation.size = size;
    }

    public static int getIterations() {
        return iterations;
    }

    public static void setIterations(int iterations) {
        Simulation.iterations = iterations;
    }

    public static int getInfantry() {
        return infantry;
    }

    public static void setInfantry(int infantry) {
        Simulation.infantry = infantry;
    }

    public static int getTanks() {
        return tanks;
    }

    public static void setTanks(int tanks) {
        Simulation.tanks = tanks;
    }

    public static int getMobiles() {
        return mobiles;
    }

    public static void setMobiles(int mobiles) {
        Simulation.mobiles = mobiles;
    }

    public static int getBases() {
        return bases;
    }

    public static void setBases(int bases) {
        Simulation.bases = bases;
    }

    public static int getTraps() {
        return traps;
    }

    public static void setTraps(int traps) {
        Simulation.traps = traps;
    }

    public static void setuFileChoice(boolean value, int i) {
        uFileChoice[i] = value;
    }

    public static boolean getuFileChoice(int i){
        return uFileChoice[i];
    }

}
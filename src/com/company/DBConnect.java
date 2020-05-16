package com.company;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class DBConnect {
    private static DBUtils utils = null;
    private void insertCandidate() throws SQLException {
        String name, lastName, phone;
        Scanner scan = new Scanner(System.in);
        System.out.println("Įveskite kandidato vardą");
        name  = scan.nextLine();
        System.out.println("Įveskite kandidato pavardę");
        lastName = scan.nextLine();
        System.out.println("Įveskite kandidato telefono numerį");
        phone = scan.nextLine();
        while (!phone.matches("\\+[0-9]+$")){
            System.out.println("Telefono numeris turi prasidėti + ženklu ir toliau būti sudarytas tik iš skaičių!");
            System.out.println("Įveskite telefono numerį iš naujo");
            phone = scan.nextLine();
        }
        utils.insertCandidate(name, lastName, phone);
    }
    private void deleteName() throws SQLException {
        String name, lastName;
        Scanner scan = new Scanner(System.in);
        System.out.println("Įveskite norimo ištrinti kandidato vardą");
        name  = scan.nextLine();
        System.out.println("Įveskite norimo ištrinti kandidato pavardę");
        lastName = scan.nextLine();
        utils.deleteCandidate(name,lastName);
    }
    private void insertExperience () throws SQLException {
        String choice, id = null;
        Scanner scan = new Scanner(System.in);
        utils.searchAll();
        System.out.println("Pasirinkite kandidato id, kuriam norite priskirti patirtį");
        id = scan.nextLine();
        while (utils.idExists(Integer.parseInt(id)) == false)  {
            System.out.println("Toks id neegzistuoja! Pasirinkite id iš lentelės");
            id = scan.nextLine();
        }
        System.out.println("Įveskite patirtį ir jos trukmę (atskirti tarpu)");
        do {
            choice = scan.nextLine();
        }while(!choice.contains(" "));
        utils.insertExperience(Integer.parseInt(id),choice);

    }
    private void insertCandidacy() throws SQLException {
        String choice, id = null;
        Scanner scan = new Scanner(System.in);
        utils.searchAll();
        System.out.println("Pasirinkite kandidato id, kuriam norite priskirti kandidatavimą");
        id = scan.nextLine();
        while (utils.idExists(Integer.parseInt(id)) == false)  {
            System.out.println("Toks id neegzistuoja! Pasirinkite id iš lentelės");
            id = scan.nextLine();
        }
        System.out.println("Įveskite kandidatuojamą poziciją");
        choice = scan.nextLine();
        utils.insertCandidacy(Integer.parseInt(id),choice);

    }
    private void newCandidate() throws SQLException {
        Scanner scan = new Scanner(System.in);
        String choice = null;

        do {
        System.out.println("Pasirinkite norimą atlikti operaciją:");
        System.out.println("    1 - Įterpti kandidatą");
        System.out.println("    2 - Įterpti kandidato patirtį");
        System.out.println("    3 - Įterpti kandidato kandidatavimą");
        System.out.println("    q - Išeiti iš kandidatų duomenų įterpimo");

        do {
            choice = scan.nextLine();
            switch (choice) {
                case "1":
                    insertCandidate();
                    break;
                case "2":
                    insertExperience();
                    break;
                case "3":
                    insertCandidacy();
            }
        } while (!choice.matches("[123q]")); // end of loop
        } while (!choice.equals("q"));
    }
    private void deleteCandidate() throws SQLException {
        Scanner scan = new Scanner(System.in);
        String choice, id = null;

        do {
            System.out.println("Pasirinkite norimą atlikti operaciją:");
            System.out.println("    1 - Ištrinti kandidatą pagal vardą ir pavardę");
            System.out.println("    2 - Ištrinti kandidatą pagal id");
            System.out.println("    q - Išeiti iš kandidatų duomenų trynimo");

            do {
                choice = scan.nextLine();
                switch (choice) {
                    case "1":
                        deleteName();
                        break;
                    case "2":
                        utils.searchAll();
                        System.out.println("Pasirinkite norimo ištrinti kandidato id");
                        id = scan.nextLine();
                        while (utils.idExists(Integer.parseInt(id)) == false)  {
                            System.out.println("Toks id neegzistuoja! Pasirinkite id iš lentelės");
                            id = scan.nextLine();
                        }
                        utils.deleteCandidateId(Integer.parseInt(id));

                }
            } while (!choice.matches("[123q]")); // end of loop
        } while (!choice.equals("q"));

    }
    private void findCandidate() throws SQLException {
        String answers = null;
        Scanner scan = new Scanner(System.in);
        String choice = null;
        System.out.println("Pasirinkite norimą atlikti operaciją:");
        System.out.println("    1 - Surasti pagal vardą ir pavardę");
        System.out.println("    2 - Surasti pagal kandidatuojamą poziciją");
        System.out.println("    3 - Surasti pagal turimą patirtį");
        System.out.println("    q - Išeiti iš kandidatų paieškos");
        do {
            choice = scan.nextLine();
            switch (choice) {
                case "1":
                    System.out.println("Įveskite vardą ir pavarde (atskirtus tarpais)");
                    answers = scan.nextLine();
                    utils.searchByName(answers);
                   break;
                case "2":
                    System.out.println("Įveskite kandidatuojamą poziciją");
                    answers = scan.nextLine();
                    utils.searchByCandidacy(answers);
                    break;
                case "3":
                    System.out.println("Įveskite kandidato turimą patirtį");
                    answers = scan.nextLine();
                    utils.searchByExperience(answers);
                    break;
                case "q":
                    break;
            }
        }while(!choice.matches("[123q]"));

    }
    private void updateCandidate() throws SQLException {String answers = null;
        Scanner scan = new Scanner(System.in);
        String choice , id, candidateId, position = null;
        Boolean wrongChoice = false;
        System.out.println("Pasirinkite norimą atlikti operaciją:");
        System.out.println("    1 - Pakeisti kandidato kandidatuojamą poziciją");
        System.out.println("    q - Išeiti iš kandidatų paieškos");
        do {
            choice = scan.nextLine();
            switch (choice) {
                case "1":
                    utils.searchAll();
                    System.out.println("Pasirinkite kandidato id, kurio kandidatuojamą poziciją norite pakeisti");
                    do {
                        candidateId = scan.nextLine();
                        while (utils.idExists(Integer.parseInt(candidateId)) == false)  {
                            System.out.println("Toks id neegzistuoja! Pasirinkite id iš lentelės");
                            candidateId = scan.nextLine();
                        }
                        if (utils.candidacyExists(Integer.parseInt(candidateId)) == false)  {
                            System.out.println("Šis kandidatas nekandidatuoją į jokias pozicijas");
                            System.out.println("Pasirinkite kitą kandidatą");
                            wrongChoice = true;
                        }
                        else{
                            wrongChoice = false;
                        }
                    }while(wrongChoice);
                    utils.searchCandidacyById(Integer.parseInt(candidateId));
                    System.out.println("Pasirinkite kandidatavimo id, kurio poziciją norėsite pakeisti");
                    id = scan.nextLine();
                    while (utils.candidateCandidacyExists(Integer.parseInt(candidateId), Integer.parseInt(id)) == false) {
                        System.out.println("Toks id neegzistuoja! Pasirinkite id iš lentelės");
                        id = scan.nextLine();
                    }
                    System.out.println("Įveskite keičiamos pozicijos pavadinimą");
                    position = scan.nextLine();
                    utils.updateCandidacy(Integer.parseInt(candidateId), Integer.parseInt(id), position);
            }
        }while(!choice.matches("[1q]"));
    }
    public static void main(String[] args) throws SQLException {
        DBConnect db = new DBConnect();
        utils = new DBUtils();
        utils.connect();
        utils.prepareStatements();
        String choice = null;
        do {
            System.out.println("Pasirinkite skaičių atitinkantį norimą komandą:");
            System.out.println("    1 - Įteprti naują kandidatą");
            System.out.println("    2 - Ištrinti kandidatą");
            System.out.println("    3 - Rasti kandidatą");
            System.out.println("    4 - Atnaujinti duomenis apie kandidatą");
            System.out.println("    q - Išeiti iš programos");

            Scanner scan = new Scanner(System.in);
            do {
                choice = scan.nextLine();
                switch (choice) {
                    case "1":
                        db.newCandidate();
                        break;
                    case "2":
                        db.deleteCandidate();
                        break;
                    case "3":
                        db.findCandidate();
                        break;
                    case "4":
                        db.updateCandidate();
                }
            } while (!choice.matches("[1234q]")); // end of loop
        } while (!choice.equals("q"));
    }
}

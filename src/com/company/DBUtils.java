package com.company;

import java.io.BufferedReader;
import java.sql.*;

public class DBUtils {

    public static String host = "host address here";
    Connection con = null;
    PreparedStatement searchNameLastName = null;
    PreparedStatement searchCandidacy = null;
    PreparedStatement searchExperience = null;
    PreparedStatement insertCandidate = null;
    PreparedStatement idExists = null;
    PreparedStatement insertExperience = null;
    PreparedStatement conCanExp = null;
    PreparedStatement idExp = null;
    PreparedStatement idCandidacy = null;
    PreparedStatement insertCandidacy = null;
    PreparedStatement conCanCan = null;
    PreparedStatement deleteCandidate = null;
    PreparedStatement deleteCandidateId = null;
    PreparedStatement searchCandidacyById = null;
    PreparedStatement candidacyExists = null;
    PreparedStatement canExists = null;
    PreparedStatement candidacyUpdate = null;
    PreparedStatement deleteCanCan = null;
    ResultSet rs = null;

    public Connection connect (){
        try {
            con = DriverManager.getConnection(host);
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return con;
    }
    public void prepareStatements () throws SQLException {
        //search statements
        searchNameLastName = con.prepareStatement("select * from kandidatas where vardas = ? and pavarde = ?");
        searchCandidacy= con.prepareStatement("SELECT kandidatas.id, kandidatas.vardas, kandidatas.pavarde, kandidatavimas.pozicija "  +
                "FROM kandidatas " +
                "JOIN kandidatas_kandidatavimas ON kandidatas.id = kandidatas_kandidatavimas.kandidatasId " +
                "JOIN kandidatavimas ON kandidatas_kandidatavimas.kandidatavimasId = kandidatavimas.kandidatavimasid " +
                "WHERE kandidatavimas.pozicija = ?");
        searchExperience =  con.prepareStatement("select * from kandidatas_patirtis_view where pareigos = ?");
        searchCandidacyById = con.prepareStatement(
                "SELECT kandidatavimas.kandidatavimasid, kandidatas.vardas, kandidatas.pavarde, kandidatavimas.pozicija "  +
                        "FROM kandidatas " +
                        "JOIN kandidatas_kandidatavimas ON kandidatas.id = kandidatas_kandidatavimas.kandidatasId " +
                        "JOIN kandidatavimas ON kandidatas_kandidatavimas.kandidatavimasId = kandidatavimas.kandidatavimasid " +
                        "WHERE kandidatas.id = ?");

        //exist statements
        idExists = con.prepareStatement("select exists(select 1 from kandidatas where id = ?)");
        canExists = con.prepareStatement("select exists(select 1 from kandidatas_kandidatavimas where kandidatasid = ? and kandidatavimasid = ?)");
        candidacyExists = con.prepareStatement("select exists(select 1 from kandidatas_kandidatavimas where kandidatasid = ?)");

        //get id statements
        idExp = con.prepareStatement("Select patirtisid from patirtis where pareigos = ? and patirtiesTrukme = ?");
        idCandidacy = con.prepareStatement("Select kandidatavimasid from kandidatavimas where pozicija = ?");

        //insert statements
        insertCandidate = con.prepareStatement("INSERT INTO kandidatas (vardas, pavarde, telefonas) VALUES (?,?,?)");
        insertExperience = con.prepareStatement("INSERT INTO patirtis(pareigos, patirtiesTrukme) VALUES (? ,?)");
        insertCandidacy = con.prepareStatement("INSERT INTO kandidatavimas(pozicija) VALUES (?)");

        //insert into "foreign-key" table statements
        conCanExp = con.prepareStatement("INSERT INTO kandidatas_patirtis (kandidatasid, patirtisId) VALUES (?, ?)");
        conCanCan = con.prepareStatement("INSERT INTO kandidatas_kandidatavimas(kandidatasid, kandidatavimasid) VALUES (?, ?)");

        //delete statements
        deleteCandidate = con.prepareStatement("DELETE FROM kandidatas WHERE vardas = ? and pavarde = ?");
        deleteCandidateId = con.prepareStatement("DELETE FROM kandidatas WHERE id = ?");
        deleteCanCan = con.prepareStatement("DELETE FROM kandidatas_kandidatavimas where kandidatasid = ? and kandidatavimasid = ?");

        //update statements
        candidacyUpdate = con.prepareStatement("update kandidatas_kandidatavimas set kandidatavimasid = ? where kandidatavimasid = ?");
    }
    public void searchByName(String str) throws SQLException {
        searchNameLastName.setString(1, str.substring(0 ,str.indexOf(" ")));
        searchNameLastName.setString(2, str.substring(str.indexOf(" ") + 1));
        rs = searchNameLastName.executeQuery();
        printResultSet(rs);
    }
    public void searchByCandidacy(String str) throws SQLException {
        searchCandidacy.setString(1, str);
        rs = searchCandidacy.executeQuery();
        printResultSet(rs);
    }
    public void searchByExperience(String str) throws SQLException {
        searchExperience.setString(1, str);
        rs = searchExperience.executeQuery();
        printResultSet(rs);
    }
    private void printResultSet(ResultSet rs) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        while (rs.next()) {
            for (int i = 1; i <= columnsNumber ;i++) {
                if (i > 1) System.out.print(" | ");
                System.out.print(rs.getString(i));
            }
            System.out.println("");
        }
        System.out.println();
    }
    public void insertCandidate(String name, String lastname,String phone) throws SQLException {
        insertCandidate.setString(1, name);
        insertCandidate.setString(2, lastname);
        insertCandidate.setString(3, phone);
        int n  = insertCandidate.executeUpdate();
        System.out.println("Rows affected: " + n);
    }
    public void searchAll () throws SQLException {
        Statement st = con.createStatement();
        String selectString = "SELECT * FROM kandidatas";
        rs = st.executeQuery(selectString);
        printResultSet(rs);
    }
    public boolean idExists (Integer id) throws SQLException {

        idExists.setInt(1, id);
        rs = idExists.executeQuery();
        rs.next();
        return rs.getBoolean(1);
    }
    private int getExperienceId (String str) throws SQLException {
        idExp.setString(1, str.substring(0, str.indexOf(" ")));
        idExp.setInt(2, Integer.parseInt(str.substring(str.indexOf(" ") + 1)));
        rs = idExp.executeQuery();
        if (rs.next())
        {
            return rs.getInt(1);
        }
        else{
            return 0;
        }

    }
    private int getCandidacyId (String str) throws SQLException {
        idCandidacy.setString(1, str);
        rs = idCandidacy.executeQuery();
        if (rs.next())
        {
            return rs.getInt(1);
        }
        else{
            return 0;
        }
    }
    private int maxIdExperience () throws SQLException {
        Statement st = con.createStatement();
        String max = "SELECT MAX(patirtisID) FROM patirtis";
        rs = st.executeQuery(max);
        rs.next();
        return rs.getInt(1);
    }
    private int maxIdCandidacy () throws SQLException {
        Statement st = con.createStatement();
        String max = "SELECT MAX(kandidatavimasID) FROM kandidatavimas";
        rs = st.executeQuery(max);
        rs.next();
        return rs.getInt(1);
    }
    public void insertExperience (Integer id, String str) throws SQLException {
        int expId = getExperienceId(str);
        try {
            con.setAutoCommit(false);
            if (expId == 0) {
                insertExperience.setString(1, str.substring(0, str.indexOf(" ")));
                insertExperience.setInt(2, Integer.parseInt(str.substring(str.indexOf(" ") + 1)));
                insertExperience.executeUpdate();
                conCanExp.setInt(1, id);
                conCanExp.setInt(2, maxIdExperience());
                conCanExp.executeUpdate();
            }
            else{
                conCanExp.setInt(1, id);
                conCanExp.setInt(2, expId);
                conCanExp.executeUpdate();
            }

            con.commit();
        } catch (SQLException e) {
            if (con != null) {
                try {
                    System.err.print("Transaction is being rolled back");
                    con.rollback();
                } catch (SQLException excep) {
                    excep.printStackTrace();
                }
            }
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        finally {
            con.setAutoCommit(true);
        }
    }
    public void insertCandidacy(int id, String str) throws SQLException {
        try {
            con.setAutoCommit(false);
            int canId = getCandidacyId(str);
            if (canId == 0) {
                insertCandidacy.setString(1, str);
                insertCandidacy.executeUpdate();
                conCanCan.setInt(1, id);
                conCanCan.setInt(2, maxIdCandidacy());
                conCanCan.executeUpdate();
            }
            else{
                conCanCan.setInt(1, id);
                conCanCan.setInt(2, canId);
                conCanCan.executeUpdate();
            }
            con.commit();
        } catch (SQLException e) {
            if (con != null) {
                try {
                    System.err.print("Transaction is being rolled back");
                    con.rollback();
                } catch (SQLException excep) {
                    excep.getErrorCode();
                }
            }
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        finally {
            con.setAutoCommit(true);
        }
    }
    public void deleteCandidate(String name, String lastName) throws SQLException {
        deleteCandidate.setString(1, name);
        deleteCandidate.setString(2, lastName);
        int effectedRows = deleteCandidate.executeUpdate();
        System.out.println("Candidate deleted " +  effectedRows);
    }
    public void deleteCandidateId (Integer id) throws SQLException {
        deleteCandidateId.setInt(1, id);
        int effectedRows = deleteCandidateId.executeUpdate();
        System.out.println("Candidate deleted: " +  effectedRows);
    }
    public void searchCandidacyById (Integer id) throws SQLException {
        searchCandidacyById.setInt(1, id);
        rs = searchCandidacyById.executeQuery();
        printResultSet(rs);
    }
    public boolean candidacyExists (Integer id) throws SQLException {
        candidacyExists.setInt(1, id);
        rs = candidacyExists.executeQuery();
        rs.next();
        return rs.getBoolean(1);
    }
    public boolean candidateCandidacyExists (Integer candidateId, Integer candidacyId) throws SQLException {
        canExists.setInt(1, candidateId);
        canExists.setInt(2, candidacyId);
        rs = canExists.executeQuery();
        rs.next();
        return rs.getBoolean(1);
    }
    public void updateCandidacy(Integer candidateId, Integer candidacyId, String position) throws SQLException {
        int newCandidacyId = getCandidacyId(position);
        if (newCandidacyId > 0){
            if (candidateCandidacyExists(candidateId,newCandidacyId)){
                System.out.println("Tokia kandidatuojama pozicija jau buvo priskirta kandidatui");
                deleteCandidate_Candidacy(candidateId, candidacyId);
                System.out.println("Kandidatavimas pašalintas");
            }
            else{
                candidacyUpdate.setInt(1, newCandidacyId);
                candidacyUpdate.setInt(2, candidacyId);
                candidacyUpdate.executeUpdate();
            }
        }
        else{
            try{
                deleteCandidate_Candidacy(candidateId, candidacyId);
                insertCandidacy.setString(1, position);
                insertCandidacy.executeUpdate();
                conCanCan.setInt(1, candidateId);
                conCanCan.setInt(2, maxIdCandidacy());
                conCanCan.executeUpdate();
            } catch (SQLException e) {
                if (con != null) {
                    try {
                        System.err.print("Transaction is being rolled back");
                        con.rollback();
                    } catch (SQLException excep) {
                        excep.getErrorCode();
                    }
                }
                e.printStackTrace();
            }
            finally {
                con.setAutoCommit(true);
            }
        }
    }
    private void deleteCandidate_Candidacy(Integer candidateId, Integer candidacyId) throws SQLException {
        deleteCanCan.setInt(1, candidateId);
        deleteCanCan.setInt(2, candidacyId);
        deleteCanCan.executeUpdate();
    }
}

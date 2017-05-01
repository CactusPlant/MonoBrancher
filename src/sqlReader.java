import java.sql.*;
import java.util.ArrayList;

/**
 * Created by Cactus on 4/29/2017.
 */
public class sqlReader {
    private static sqlReader ourInstance = new sqlReader();

    PreparedStatement stmt = null;

    public static sqlReader getInstance() {
        return ourInstance;
    }

    private sqlReader() {
    }

    public ArrayList<String> getDefinition(Connection con, String word){

        ArrayList<String> result = new ArrayList<String>();
        ResultSet rs = null;
        try{

            String sql = "SELECT WORD,READING,DEFINITION FROM entry WHERE WORD = ? OR READING = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1,word);
            stmt.setString(2, word);
            rs = stmt.executeQuery();
            while (rs.next() != false) {
                System.out.println(rs.getString("reading"));
               result.add(rs.getString("definition"));
            }

        }catch(Exception e) {e.printStackTrace();}
        finally {try{
            stmt.close();
            con.close();
        }catch(Exception e) {e.printStackTrace();}}
        return result;
    }

    public boolean isKnown(Connection con, String string){
        Boolean known = false;
        PreparedStatement stmt = null;
        String sql = "SELECT * FROM 'knownwords' WHERE word = ?" ;
        ResultSet rs = null;
        try{
            stmt = con.prepareStatement(sql);
            stmt.setString(1, string);
            rs = stmt.executeQuery();
            while (rs.next() != false) {

                if (rs.getString("word").equals(string)){
                    System.out.println(rs.getString("word"));
                    System.out.println(string);
                    known = true;
                }
            }

        }catch(Exception e){e.printStackTrace();}
        finally{try{
            rs.close();
            stmt.close();
            con.close();
        }catch(Exception e){e.printStackTrace();}}

        return known;
    }

    public void toggleKnown(Connection con, String string){
        PreparedStatement stmt = null;
        String sql = "" ;
        ResultSet rs = null;
        try{Connection con2 = DriverManager.getConnection("jdbc:sqlite:res\\db\\known.db");
            if (isKnown(con2,string)){
                System.out.println("REMOVING WORD FROM DATABASE");
                sql = "DELETE FROM knownwords WHERE word = ?";
            } else{
                System.out.println("ADDING WORD TO DATABASE");
                sql = "INSERT INTO knownwords(word) VALUES (?)";
            }

            stmt = con.prepareStatement(sql);
            stmt.setString(1, string);
            stmt.executeUpdate();
        }catch(Exception e){e.printStackTrace();}
        finally{try{
            stmt.close();
            con.close();
        }catch(Exception e){
            System.out.println("Closed during finally|try");
        }}

    }
}

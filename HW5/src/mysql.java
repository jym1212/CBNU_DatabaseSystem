import java.sql.*;

public class mysql {
    public static void main(String[] args) {
        try {

            //모든 데이터 검색 
            selectAllBook();

            //데이터 삽입
            insertBook(11, "통증과 교정 운동", "파브스포츠", 20000);
            //특정 데이터 검색
            selectBook(11);
            
            //데이터 삭제
            deleteBook(11);
            //모든 데이터 검색
            selectAllBook();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void selectAllBook() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://192.168.56.107:4567/madang",
                    "yunmin", "1212");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Book");

            System.out.println("SELECT * FROM Book;\n");
            while (rs.next()) {
                System.out
                        .println(rs.getInt(1) + " " + rs.getString(2) + " " + rs.getString(3) + " " + rs.getString(4));
            }
            System.out.println("\n");

            con.close();

        } catch (Exception e) {
            System.out.println("Select Error : " + e);
        }
    }
    
    public static void selectBook(int bookid) {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://192.168.56.107:4567/madang",
                    "yunmin", "1212");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Book WHERE bookid = " + bookid + ";\n");

            System.out.println("SELECT * FROM Book WHERE bookid = " + bookid + ";");
            while (rs.next()) {
                System.out
                        .println(rs.getInt(1) + " " + rs.getString(2) + " " + rs.getString(3) + " " + rs.getString(4));
            }
            System.out.println("\n");
            
            con.close();

        } catch(Exception e) {
            System.out.println("Select Error : " + e);
        }
    }
    
    public static void insertBook(int bookid, String bookname, String publisher, int price) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://192.168.56.107:4567/madang",
                    "yunmin", "1212");

            String query = "INSERT INTO Book (bookid, bookname, publisher, price) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, bookid);
            pstmt.setString(2, bookname);
            pstmt.setString(3, publisher);
            pstmt.setInt(4, price);
            
            System.out.println("INSERT INTO Book (bookid, bookname, publisher, price)\nVALUES (" + bookid + ", " + bookname + ", " + publisher + ", " + price + ");");
            int rowsCount = pstmt.executeUpdate();
            System.out.println(rowsCount + " row(s) in set\n");

        } catch (Exception e) {
            System.out.println("Insert Error : " + e);
        }
    }
    
    public static void deleteBook(int bookid) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://192.168.56.107:4567/madang",
                    "yunmin", "1212");

            String query = "DELETE FROM Book WHERE bookid = ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, bookid);

            System.out.println("DELETE FROM Book WHERE bookid = " + bookid + ";");
            int rowsCount = pstmt.executeUpdate();
            System.out.println(rowsCount + " row(s) in set\n");

        } catch (Exception e) {
            System.out.println("Delete Error : " + e);
        }
    }
}

import java.util.Scanner;
import java.sql.*;

public class main_menu {
    public static void main(String[] args) throws Exception {
        int menu = 0;
        Connection con = null;
        Scanner sc = new Scanner(System.in);
        
        while (true) {
            System.out.print("\n\n=====================================================\n");
            System.out.println("    충북대학교 소프트웨어학과 동아리 관리 프로그램     ");
            System.out.println("          소프트웨어학과 2021041078 정윤민           ");
            System.out.println("=====================================================");
            System.out.println("1. 동아리 관리 DB 연결           2. 전체 동아리 목록");
            System.out.println("3. 동아리 담당 교수 목록         4. 동아리 회원 관리");
            System.out.println("5. 동아리 임원 관리              6. 동아리 행사 관리");
            System.out.println("7. 동아리 프로젝트 관리          8. 동아리 비품 관리");
            System.out.println("9. 동아리 관리 DB 해제           10. 프로그램 종료"); 
            System.out.println("=====================================================");
            System.out.print("메뉴 선택 : ");

            menu = sc.nextInt();

            switch (menu) {
                case 1:
                    con = getConnection(con);
                    if (con != null) {
                        System.out.println("동아리 관리 DB 연결에 성공했습니다.");
                    } else {
                        System.out.println("동아리 관리 DB 연결에 실패했습니다.");
                    }
                    break;
                case 9:
                    con = closeConnection(con);
                    if (con == null) {
                        System.out.println("동아리 관리 DB 연결을 해제했습니다.");
                    } else {
                        System.out.println("동아리 관리 DB 연결 해제에 실패했습니다.");
                    }
                    break;
                case 10:
                    System.out.println("프로그램을 종료합니다.");
                    sc.close();
                    System.exit(0);
                    break;
            }
        }
    }

    // DB 연결 함수
    public static Connection getConnection(Connection con) {
        try {
            if (con != null && !con.isClosed()) {
                System.out.println("이미 동아리 관리 DB와 연결되어 있습니다.");
                return con;
            }

            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                    "jdbc:mysql://192.168.56.107:4567/",
                    "yunmin", "1212");
            init_clubDB(con);
            Statement stmt = con.createStatement();
            stmt.executeUpdate("USE ClubManagement");
            stmt.close();
        } catch (Exception e) {
            System.out.println("Database Connection Error: " + e);
        }
        return con;
    }

    public static void init_clubDB(Connection con) {
        try{
            Statement stmt = con.createStatement();
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS ClubManagement");
            stmt.close();
        } catch (Exception e) {
            System.out.println("Database Connection Error: " + e);
        }
    }

    // DB 연결 해제 함수
    public static Connection closeConnection(Connection con) {
        try {
            if (con != null) {
                con.close();
                con = null;
            }
        } catch (SQLException e) {
            System.out.println("Database Connection Close Error: " + e);
        }
        return con;
    }
}
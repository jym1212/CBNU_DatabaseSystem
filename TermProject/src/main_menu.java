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
            System.out.println("1. 동아리 관리 DB 연결           2. 동아리 관리");
            System.out.println("3. 동아리 담당 교수 관리         4. 동아리 회원 관리");
            System.out.println("5. 동아리 임원 관리              6. 동아리 행사 관리");
            System.out.println("7. 동아리 프로젝트 관리          8. 동아리 비품 관리");
            System.out.println("9. 동아리 관리 DB 해제           10. 프로그램 종료"); 
            System.out.println("=====================================================");
            System.out.print("메뉴 선택 : ");

            menu = sc.nextInt();

            switch (menu) {
                case 1:
                    System.out.println("동아리 관리 DB 연결을 시작합니다.");
                    con = getConnection(con);
                    if (con != null) {
                        System.out.println("동아리 관리 DB 연결에 성공했습니다.");
                    } else {
                        System.out.println("동아리 관리 DB 연결에 실패했습니다.");
                    }
                    break;
                case 2:
                    if (con != null) {
                        club.club_menu(con, sc);
                    } else {
                        System.out.println("동아리 관리 DB와 연결되어 있지 않습니다.");
                        System.out.println("동아리 관리 DB와 먼저 연결해주세요.");
                    }
                    break;
                case 3:
                    if (con != null) {
                        professor.professor_menu(con, sc);
                    } else {
                        System.out.println("동아리 관리 DB와 연결되어 있지 않습니다.");
                        System.out.println("동아리 관리 DB와 먼저 연결해주세요.");
                    }
                    break;
                case 4:
                    if (con != null) {
                        student.student_menu(con, sc);
                    } else {
                        System.out.println("동아리 관리 DB와 연결되어 있지 않습니다.");
                        System.out.println("동아리 관리 DB와 먼저 연결해주세요.");
                    }
                    break;
                case 5:
                    if (con != null) {
                        manager.manager_menu(con, sc);
                    } else {
                        System.out.println("동아리 관리 DB와 연결되어 있지 않습니다.");
                        System.out.println("동아리 관리 DB와 먼저 연결해주세요.");
                    }
                    break;
                case 6:
                    if (con != null) {
                        event.event_menu(con, sc);
                    } else {
                        System.out.println("동아리 관리 DB와 연결되어 있지 않습니다.");
                        System.out.println("동아리 관리 DB와 먼저 연결해주세요.");
                    }
                    break;
                case 8:
                    if (con != null) {
                        item.item_menu(con, sc);
                    } else {
                        System.out.println("동아리 관리 DB와 연결되어 있지 않습니다.");
                        System.out.println("동아리 관리 DB와 먼저 연결해주세요.");
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
                default:
                    System.out.println("잘못된 메뉴 선택입니다.");
                    System.out.println("다시 선택해주세요.");
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
                    "jdbc:mysql://192.168.56.108:4568/",
                    "yunmin", "1212");
            init_clubDB(con);
        } catch (Exception e) {
            System.out.println("Database Connection Error: " + e);
        }
        return con;
    }

    public static void init_clubDB(Connection con) {
        try{
            Statement stmt = con.createStatement();
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS ClubManagement");
            stmt.executeUpdate("USE ClubManagement");
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS Professor (prof_id VARCHAR(12) NOT NULL PRIMARY KEY, prof_name VARCHAR(10) NOT NULL, email VARCHAR(30), lab_name VARCHAR(20), lab_num INT)");
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS Club (club_id INT NOT NULL PRIMARY KEY, club_name VARCHAR(20) NOT NULL, room_num INT, total_num INT, prof_id VARCHAR(10), FOREIGN KEY (prof_id) REFERENCES Professor(prof_id))");
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS Student (stu_id VARCHAR(12) NOT NULL PRIMARY KEY, stu_name VARCHAR(10) NOT NULL, stu_phone VARCHAR(15), stu_grade INT, stu_dept VARCHAR(20), stu_state VARCHAR(10), club_id INT, FOREIGN KEY (club_id) REFERENCES Club(club_id))");
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS Manager (man_id VARCHAR(12) NOT NULL PRIMARY KEY, man_name VARCHAR(10) NOT NULL, man_phone VARCHAR(15), man_grade INT, man_dept VARCHAR(20), position VARCHAR(10), club_id INT, FOREIGN KEY (club_id) REFERENCES Club(club_id))");
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS Event (event_id INT NOT NULL PRIMARY KEY, event_name VARCHAR(20) NOT NULL, event_date DATE, club_id INT, FOREIGN KEY (club_id) REFERENCES Club(club_id))");
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS Project (project_id INT NOT NULL PRIMARY KEY, project_name VARCHAR(30) NOT NULL, project_date DATE, club_id INT, FOREIGN KEY (club_id) REFERENCES Club(club_id))");
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS Item (item_id INT NOT NULL PRIMARY KEY, item_name VARCHAR(20) NOT NULL, item_date DATE, total_num INT, man_id VARCHAR(10), FOREIGN KEY (man_id) REFERENCES Manager(man_id))");
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS Participate (event_id INT NOT NULL, stu_id VARCHAR(10) NOT NULL, PRIMARY KEY (event_id, stu_id), total_num INT, FOREIGN KEY (event_id) REFERENCES Event(event_id), FOREIGN KEY (stu_id) REFERENCES Student(stu_id))");
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS Work_On (project_id INT NOT NULL, stu_id VARCHAR(10) NOT NULL, PRIMARY KEY (project_id, stu_id), total_num INT, FOREIGN KEY (project_id) REFERENCES Project(project_id), FOREIGN KEY (stu_id) REFERENCES Student(stu_id))");
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
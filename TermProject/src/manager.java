import java.util.Scanner;
import java.sql.*;

public class manager {
    public static void manager_menu(Connection con, Scanner sc) {
        int menu = 0;

        while (true) {
            System.out.print("\n\n===============================\n");
            System.out.println("     동아리 임원 관리 메뉴        ");
            System.out.println("===============================");
            System.out.println("1. 임원 목록 출력");
            System.out.println("2. 임원 추가");
            System.out.println("3. 임원 수정");
            System.out.println("4. 임원 삭제");
            System.out.println("5. 임원 검색");
            System.out.println("6. 메인 메뉴로 돌아가기");
            System.out.print(">> 메뉴 선택 : ");

            menu = sc.nextInt();

            switch (menu) {
                case 2:
                    insertManager(con, sc);
                    break;
                case 6:
                    return;
            }
        }
    }

    // 출력 형식 맞추기 위한 함수
    public static String formatString(String str, int length) {
        int realLength = 0;
        for (char c : str.toCharArray()) {
            realLength += (c > 0x7F) ? 2 : 1;
        }
        int padding = length - realLength;
        return str + " ".repeat(Math.max(0, padding));
    }

    // 임원 추가 함수
    public static void insertManager(Connection con, Scanner sc) {
        try {
            System.out.print("\n임원 번호 : ");
            String man_id = sc.next();
            System.out.print("임원 이름 : ");
            String man_name = sc.next();
            System.out.print("전화번호 : ");
            String man_phone = sc.next();
            System.out.print("임원 학년 : ");
            int man_grade = sc.nextInt();
            System.out.print("임원 학과 : ");
            String man_dept = sc.next();
            System.out.print("임원 직책 : ");
            String position = sc.next();

            System.out.print("동아리 코드 : ");
            int club_id = sc.nextInt();
            Integer valid_id = null;

            String query = "SELECT club_id FROM Club WHERE club_id = ?";
            try (PreparedStatement pstmt = con.prepareStatement(query)) {
                pstmt.setInt(1, club_id);
                ResultSet rs = pstmt.executeQuery();
                if (!rs.next()) {
                    System.out.println(">> 데이터 삽입 실패 : 해당 동아리가 존재하지 않습니다.");
                    pstmt.close();
                    return;
                } else {
                    valid_id = club_id;
                    pstmt.close();
                }
            }

            String insertQuery = "INSERT INTO Manager (man_id, man_name, man_phone, man_grade, man_dept, position, club_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement insertPstmt = con.prepareStatement(insertQuery);
            insertPstmt.setString(1, man_id);
            insertPstmt.setString(2, man_name);
            insertPstmt.setString(3, man_phone);
            insertPstmt.setInt(4, man_grade);
            insertPstmt.setString(5, man_dept);
            insertPstmt.setString(6, position);
            insertPstmt.setInt(7, valid_id);

            insertPstmt.executeUpdate();
            System.out.println(">> Manager 테이블에 데이터를 성공적으로 삽입했습니다.");

            insertPstmt.close();

    
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println(">> 데이터 삽입 실패 : 임원 번호가 이미 존재합니다.");

        } catch (SQLException e) {
            System.out.println(">> 데이터 삽입 실패 : " + e.getMessage());

        } catch (Exception e) {
            System.out.println(">> 예상치 못한 오류 : " + e.getMessage());
        }
    }
}

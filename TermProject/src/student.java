import java.util.Scanner;
import java.sql.*;

public class student {
    static int invalid = 0;

    public static void student_menu(Connection con, Scanner sc) {
        int menu = 0;

        while (true) {
            System.out.print("\n\n===============================\n");
            System.out.println("        동아리 회원 관리 메뉴        ");
            System.out.println("===============================");
            System.out.println("1. 회원 목록 출력");
            System.out.println("2. 회원 추가");
            System.out.println("3. 회원 수정");
            System.out.println("4. 회원 삭제");
            System.out.println("5. 회원 검색");
            System.out.println("6. 메인 메뉴로 돌아가기");
            System.out.print(">> 메뉴 선택 : ");

            menu = sc.nextInt();

            switch (menu) {
                case 2:
                    insertStudent(con, sc);
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

    // 회원 추가 함수
    public static void insertStudent(Connection con, Scanner sc) {
        try {
            System.out.print("\n학생 번호 : ");
            String stu_id = sc.next();
            System.out.print("학생 이름 : ");
            String stu_name = sc.next();
            System.out.print("학생 전화번호 : ");
            String stu_phone = sc.next();
            System.out.print("학생 학년 : ");
            int stu_grade = sc.nextInt();
            System.out.print("학생 상태 : ");
            String stu_state = sc.next();

            System.out.print("동아리 코드 : ");
            int club_id = sc.nextInt();
            Integer valid_id = null;

            String checkQuery = "SELECT club_id FROM Club WHERE club_id = ?";
            try (PreparedStatement checkPstmt = con.prepareStatement(checkQuery)) {
                checkPstmt.setInt(1, club_id);
                ResultSet check_rs = checkPstmt.executeQuery();
                if (!check_rs.next()) {
                    System.out.println(">> 데이터 삽입 실패 : 동아리 코드가 존재하지 않습니다.");
                    checkPstmt.close();
                    return;                     
                } else {
                    valid_id = club_id;
                    checkPstmt.close();
                }
            }

            String query = "INSERT INTO Student (stu_id, stu_name, stu_phone, stu_grade, stu_state, club_id) VALUES (?, ?, ?, ?, ?, ?)"; 
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, stu_id);
            pstmt.setString(2, stu_name);
            pstmt.setString(3, stu_phone);
            pstmt.setInt(4, stu_grade);
            pstmt.setString(5, stu_state);
            pstmt.setInt(6, valid_id);

            pstmt.executeUpdate();
            System.out.println(">> Student 테이블에 데이터를 성공적으로 삽입했습니다.");

            pstmt.close();

        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println(">> 데이터 삽입 실패 : 동아리 코드가 이미 존재합니다.");

        } catch (SQLException e) {
            System.out.println(">> 데이터 삽입 실패 : " + e.getMessage());

        } catch (Exception e) {
            System.out.println(">> 예상치 못한 오류 : " + e.getMessage());
        }
    }
    
}

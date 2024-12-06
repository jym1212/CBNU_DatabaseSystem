import java.util.Scanner;
import java.sql.*;

public class student {
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
            System.out.print("\n동아리 코드 : ");
            int club_id = sc.nextInt();
            System.out.print("동아리 이름 : ");
            String club_name = sc.next();
            System.out.print("동아리 호수 : ");
            int room_num = sc.nextInt();
            System.out.print("동아리 인원 : ");
            int total_num = sc.nextInt();

            String query = "INSERT INTO Club (club_id, club_name, room_num, total_num) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, club_id);
            pstmt.setString(2, club_name);
            pstmt.setInt(3, room_num);
            pstmt.setInt(4, total_num);

            pstmt.executeUpdate();
            System.out.println(">> Club 테이블에 데이터를 성공적으로 삽입했습니다.");

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

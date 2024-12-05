import java.util.Scanner;
import java.sql.*;

public class club {
    public static void club_menu(Connection con, Scanner sc) {
        int menu = 0;

        while (true) {
            System.out.print("\n\n===============================\n");
            System.out.println("      전체 동아리 목록 관리      ");
            System.out.println("===============================");
            System.out.println("1. 동아리 목록 출력");
            System.out.println("2. 동아리 추가");
            System.out.println("3. 동아리 수정");
            System.out.println("4. 동아리 삭제");
            System.out.println("5. 동아리 검색");
            System.out.println("6. 동아리 정렬");
            System.out.println("7. 메인 메뉴로 돌아가기");
            System.out.print("메뉴 선택 : ");

            menu = sc.nextInt();

            switch (menu) {
                case 1:
                    break;
                case 2:
                    insertClub(con, sc);
                    break;
                case 7:
                    return;
            }
        }
    }

    // 동아리 추가 함수
    public static void insertClub(Connection con, Scanner sc) {
        try {
            System.out.print("\n동아리 코드 : ");
            int club_id = sc.nextInt();
            System.out.print("동아리 이름 : ");
            String club_name = sc.next();
            System.out.print("동아리 호수 : ");
            int room_num = sc.nextInt();
            System.out.print("동아리 총 인원 : ");
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
            System.out.println(">> 데이터 삽입 실패: " + e.getMessage());
            if (e.getSQLState() != null) {
                System.out.println(">> SQL State: " + e.getSQLState());
                System.out.println(">> Error Code: " + e.getErrorCode());
            }

        } catch (Exception e) {
            System.out.println(">> 예상치 못한 오류 : " + e.getMessage());
        }
    }
}

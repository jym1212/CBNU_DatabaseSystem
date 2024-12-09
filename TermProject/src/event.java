import java.util.Scanner;
import java.sql.*;

public class event {
    public static void event_menu(Connection con, Scanner sc) {
        int menu = 0;

        while (true) {
            System.out.print("\n\n===============================\n");
            System.out.println("     동아리 행사 관리 메뉴        ");
            System.out.println("===============================");
            System.out.println("1. 행사 목록 출력");
            System.out.println("2. 행사 추가");
            System.out.println("3. 행사 수정");
            System.out.println("4. 행사 삭제");
            System.out.println("5. 행사 검색");
            System.out.println("6. 메인 메뉴로 돌아가기");
            System.out.print(">> 메뉴 선택 : ");

            menu = sc.nextInt();

            switch (menu) {
                case 1:
                    selectALLEvent(con);
                    break;
                case 6:
                    return;
                default:
                    System.out.println("잘못된 메뉴 선택입니다.");
                    System.out.println("다시 선택해주세요.");
                    break;
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

    public static void selectALLEvent(Connection con) {
        try{
            String query = "SELECT e.event_id, e.event_name, e.event_date, p.total_num, e.club_id " +
                            "FROM Event e, Participate p " + "WHERE e.event_id = p.event_id";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if (!rs.next()) {
                System.out.print("\n>> 행사 목록이 존재하지 않습니다.\n");
                return;
            }

            System.out.print(
                    "\n------------------------------------------------------------------------------------\n");
            System.out.printf("| %s | %s | %s | %s | %s |\n",
                    formatString("행사 번호", 10),
                    formatString("행사 이름", 20),
                    formatString("행사 날짜", 15),
                    formatString("인원수", 5),
                    formatString("동아리 번호", 13));
            System.out.println(
                    "------------------------------------------------------------------------------------");

            do {
                String event_id = formatString(rs.getString(1), 10);
                String event_name = formatString(rs.getString(2), 20);
                String event_date = formatString(rs.getString(3), 15);
                String total_num = formatString(rs.getString(4), 5);
                String club_id = formatString(rs.getString(5), 13);
                System.out.printf("| %s | %s | %s | %s | %s |\n", event_id, event_name, event_date, total_num, club_id);
            } while (rs.next());
            System.out.println(
                    "------------------------------------------------------------------------------------");

            stmt.close();

        } catch (SQLSyntaxErrorException e) {
            System.out.println(">> SQL 문법 오류 : " + e.getMessage());
            
        } catch (SQLException e) {
            System.out.println(">> 데이터베이스 조회 오류 : " + e.getMessage());
            
        } catch (Exception e) {
            System.out.println(">> 예상치 못한 오류 : " + e.getMessage());
        }
    }
}

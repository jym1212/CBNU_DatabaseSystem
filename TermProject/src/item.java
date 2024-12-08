import java.util.Scanner;
import java.sql.*;

public class item {
    public static void item_menu(Connection con, Scanner sc) {
        int menu = 0;

        while (true) {
            System.out.print("\n\n===============================\n");
            System.out.println("     동아리 비품 관리 메뉴        ");
            System.out.println("===============================");
            System.out.println("1. 비품 목록 출력");
            System.out.println("2. 비품 추가");
            System.out.println("3. 비품 수정");
            System.out.println("4. 비품 삭제");
            System.out.println("5. 비품 검색");
            System.out.println("6. 메인 메뉴로 돌아가기");
            System.out.print(">> 메뉴 선택 : ");

            menu = sc.nextInt();

            switch (menu) {
                case 1:
                    selectALLItem(con);
                    break;
                case 6:
                    return;
                default:
                    System.out.println("잘못된 입력입니다.");
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

    // 전체 비품 목록 출력
    public static void selectALLItem(Connection con) {
        try {
            String query = "SELECT * FROM Item ORDER BY item_id, man_id";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if (!rs.next()) {
                System.out.print("\n>> 비품이 존재하지 않습니다.\n");
                return;
            }

            System.out.print("\n------------------------------------------------------------------------------------------------------\n");
            System.out.printf("| %s | %s | %s | %s | %s |\n",
                        formatString("비품 번호", 10),
                        formatString("비품 이름", 20),
                        formatString("비품 날짜", 15),
                        formatString("비품 개수", 10),
                        formatString("임원 번호", 13));
            System.out.println(
                    "------------------------------------------------------------------------------------------------------");

            do {
                String item_id = formatString(rs.getString(1), 10);
                String item_name = formatString(rs.getString(2), 20);
                String item_date = formatString(rs.getString(3), 15);
                String total_num = formatString(rs.getString(4), 10);
                String man_id = formatString(rs.getString(5), 13);
                System.out.printf("| %s | %s | %s | %s | %s |\n", item_id, item_name, item_date, total_num, man_id);
            } while (rs.next());
            System.out.println(
                    "------------------------------------------------------------------------------------------------------");
            
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

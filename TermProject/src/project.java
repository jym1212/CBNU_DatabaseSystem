import java.util.Scanner;
import java.sql.*;

public class project {
    public static void project_menu(Connection con, Scanner sc) {
        int menu = 0;

        while (true) {
            System.out.print("\n\n==================================\n");
            System.out.println("     동아리 프로젝트 관리 메뉴        ");
            System.out.println("==================================");
            System.out.println("1. 프로젝트 목록 출력");
            System.out.println("2. 프로젝트 추가");
            System.out.println("3. 프로젝트 수정");
            System.out.println("4. 프로젝트 삭제");
            System.out.println("5. 프로젝트 검색");
            System.out.println("==================================");
            System.out.println("6. 프로젝트 참여 학생 목록 출력");
            System.out.println("7. 프로젝트 참여 학생 추가");
            System.out.println("8. 프로젝트 참여 학생 삭제");
            System.out.println("9. 프로젝트 참여 학생 검색");
            System.out.println("10. 메인 메뉴로 돌아가기");
            System.out.print(">> 메뉴 선택 : ");

            menu = sc.nextInt();

            switch (menu) {
                case 1:
                    selectALLProject(con);
                    break;
                case 10:
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
    
    // 전체 프로젝트 목록 출력 함수
    public static void selectALLProject(Connection con) {
        try{
            String query = "SELECT * FROM Project ORDER BY club_id, project_id";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if(!rs.next()){
                System.out.print("\n>> 프로젝트 목록이 존재하지 않습니다.\n");
                stmt.close();
                return;
            }

            System.out.print(
                    "\n------------------------------------------------------------------------------------------\n");
            System.out.printf("| %s | %s | %s | %s | %s |\n",
                    formatString("프로젝트 번호", 10),
                    formatString("프로젝트 이름", 30),
                    formatString("프로젝트 날짜", 15),
                    formatString("인원수", 5),
                    formatString("동아리 번호", 13));
            System.out.println(
                    "------------------------------------------------------------------------------------------");

            do {
                String project_id = formatString(rs.getString(1), 10);
                String project_name = formatString(rs.getString(2), 30);
                String project_date = formatString(rs.getString(3), 15);
                String total_num = formatString(rs.getString(4), 6);
                String club_id = formatString(rs.getString(5), 13);
                System.out.printf("| %s | %s | %s | %s | %s |\n", project_id, project_name, project_date, total_num, club_id);
            } while (rs.next());
            System.out.println(
                    "------------------------------------------------------------------------------------------");
            
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

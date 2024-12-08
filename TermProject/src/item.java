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
                case 2:
                    insertItem(con, sc);
                    break;
                case 3:
                    updateItem(con, sc);
                    break;
                case 4:
                    deleteItem(con, sc);
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

            System.out.print(
                    "\n------------------------------------------------------------------------------------\n");
            System.out.printf("| %s | %s | %s | %s | %s |\n",
                    formatString("비품 번호", 10),
                    formatString("비품 이름", 20),
                    formatString("비품 날짜", 15),
                    formatString("비품 개수", 10),
                    formatString("임원 번호", 13));
            System.out.println(
                    "------------------------------------------------------------------------------------");

            do {
                String item_id = formatString(rs.getString(1), 10);
                String item_name = formatString(rs.getString(2), 20);
                String item_date = formatString(rs.getString(3), 15);
                String total_num = formatString(rs.getString(4), 10);
                String man_id = formatString(rs.getString(5), 13);
                System.out.printf("| %s | %s | %s | %s | %s |\n", item_id, item_name, item_date, total_num, man_id);
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
    
    // 비품 추가 함수
    public static void insertItem(Connection con, Scanner sc) {
        try {

            System.out.print("\n비품 번호 : ");
            int item_id = sc.nextInt();
            System.out.print("비품 이름 : ");
            String item_name = sc.next();
            System.out.print("비품 날짜(YYYY-MM-DD) : ");
            String item_date = sc.next();
            java.sql.Date sql_date = java.sql.Date.valueOf(item_date);
            System.out.print("비품 개수 : ");
            int total_num = sc.nextInt();

            System.out.print("임원 번호 : ");
            String man_id = sc.next();
            String valid_id = null;

            String query = "SELECT man_id FROM Manager WHERE man_id = ?;";
            try (PreparedStatement pstmt = con.prepareStatement(query)) {
                pstmt.setString(1, man_id);
                ResultSet rs = pstmt.executeQuery();
                if (!rs.next()) {
                    System.out.println(">> 데이터 삽입 실패 : 임원 번호가 존재하지 않습니다.");
                    pstmt.close();
                    return;
                } else {
                    valid_id = man_id;
                    pstmt.close();
                }
            }

            String insertQuery = "INSERT INTO Item (item_id, item_name, item_date, total_num, man_id) VALUES (?, ?, ?, ?, ?);";
            PreparedStatement insertPstmt = con.prepareStatement(insertQuery);
            insertPstmt.setInt(1, item_id);
            insertPstmt.setString(2, item_name);
            insertPstmt.setDate(3, sql_date);
            insertPstmt.setInt(4, total_num);
            insertPstmt.setString(5, valid_id);

            insertPstmt.executeUpdate();
            System.out.println(">> Item 테이블에 데이터를 성공적으로 삽입했습니다.");

            insertPstmt.close();

        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println(">> 데이터 삽입 실패 : 비품 번호가 이미 존재합니다.");

        } catch (SQLSyntaxErrorException e) {
            System.out.println(">> SQL 문법 오류 : " + e.getMessage());

        } catch (SQLException e) {
            System.out.println(">> 데이터베이스 입력 오류 : " + e.getMessage());

        } catch (Exception e) {
            System.out.println(">> 예상치 못한 오류 : " + e.getMessage());
        }
    }
    
    // 비품 수정 함수
    public static void updateItem(Connection con, Scanner sc) {
        try {
            System.out.print("\n수정할 비품 번호 : ");
            int item_id = sc.nextInt();

            String query = "SELECT * FROM Item WHERE item_id = ?;";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, item_id);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) {
                System.out.println(">> 데이터 수정 실패 : 해당 비품 번호가 존재하지 않습니다.");
                return;
            }

            System.out.print("비품 이름 : ");
            String item_name = sc.next();
            System.out.print("비품 날짜(YYYY-MM-DD) : ");
            String item_date = sc.next();
            java.sql.Date sql_date = java.sql.Date.valueOf(item_date);
            System.out.print("비품 개수 : ");
            int total_num = sc.nextInt();

            String updateQuery = "UPDATE Item SET item_name = ?, item_date = ?, total_num = ? WHERE item_id = ?;";
            PreparedStatement updatePstmt = con.prepareStatement(updateQuery);
            updatePstmt.setString(1, item_name);
            updatePstmt.setDate(2, sql_date);
            updatePstmt.setInt(3, total_num);
            updatePstmt.setInt(4, item_id);

            updatePstmt.executeUpdate();
            System.out.println(">> Item 테이블의 데이터를 성공적으로 수정했습니다.");

            pstmt.close();
            updatePstmt.close();

        } catch (SQLException e) {
            System.out.println(">> 데이터 수정 실패 : " + e.getMessage());

        } catch (Exception e) {
            System.out.println(">> 예상치 못한 오류 : " + e.getMessage());
        }
    }
    
    // 비품 삭제 함수
    public static void deleteItem(Connection con, Scanner sc) {
        try{
            System.out.print("\n삭제할 비품 번호 : ");
            int item_id = sc.nextInt();

            String query = "SELECT * FROM Item WHERE item_id = ?;";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, item_id);
            ResultSet rs = pstmt.executeQuery();

            if(!rs.next()){
                System.out.println(">> 데이터 삭제 실패 : 해당 비품 번호가 존재하지 않습니다.");
                return;
            }

            String deleteQuery = "DELETE FROM Item WHERE item_id = ?;";
            PreparedStatement deletePstmt = con.prepareStatement(deleteQuery);
            deletePstmt.setInt(1, item_id);

            deletePstmt.executeUpdate();
            System.out.println(">> Item 테이블의 데이터를 성공적으로 삭제했습니다.");

            pstmt.close();
            deletePstmt.close();
            
        } catch (SQLException e) {
            System.out.println(">> 데이터 삭제 실패 : " + e.getMessage());

        } catch (Exception e) {
            System.out.println(">> 예상치 못한 오류 : " + e.getMessage());
        }
    }
}

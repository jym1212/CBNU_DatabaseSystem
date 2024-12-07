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
                case 1:
                    selectALLManager(con);
                    break;
                case 2:
                    insertManager(con, sc);
                    break;
                case 3:
                    updateManager(con, sc);
                    break;
                case 4:
                    deleteManager(con, sc);
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

    // 전체 임원 목록 출력 함수
    public static void selectALLManager(Connection con) {
        try {
            String query = "SELECT * FROM Manager ORDER BY club_id, man_id";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if (!rs.next()) {
                System.out.print("\n>> 임원이 존재하지 않습니다.\n");
                return;
            }

            System.out.print(
                    "\n-----------------------------------------------------------------------------------------------------------\n");
            System.out.printf("| %s | %s | %s | %s | %s | %s | %s |\n",
                    formatString("임원 번호", 13),
                    formatString("임원 이름", 10),
                    formatString("전화번호", 15),
                    formatString("학년", 5),
                    formatString("학과", 20),
                    formatString("직책", 10),
                    formatString("동아리 코드", 12));
            System.out.println(
                    "-----------------------------------------------------------------------------------------------------------");

            do {
                String man_id = formatString(rs.getString(1), 13);
                String man_name = formatString(rs.getString(2), 10);
                String man_phone = formatString(rs.getString(3), 15);
                String man_grade = formatString(rs.getString(4), 5);
                String man_dept = formatString(rs.getString(5), 20);
                String position = formatString(rs.getString(6), 10);
                String club_id = formatString(rs.getString(7), 12);
                System.out.printf("| %s | %s | %s | %s | %s | %s | %s |\n", man_id, man_name, man_phone, man_grade,
                        man_dept, position, club_id);
            } while (rs.next());
            System.out.println(
                    "-----------------------------------------------------------------------------------------------------------");

            stmt.close();

        } catch (SQLSyntaxErrorException e) {
            System.out.println(">> SQL 문법 오류 : " + e.getMessage());

        } catch (SQLException e) {
            System.out.println(">> 데이터베이스 조회 오류 : " + e.getMessage());

        } catch (Exception e) {
            System.out.println(">> 예상치 못한 오류 : " + e.getMessage());
        }
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
                    System.out.println(">> 데이터 삽입 실패 : 동아리 번호가 존재하지 않습니다.");
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

    // 임원 수정 함수
    public static void updateManager(Connection con, Scanner sc) {
        try {
            System.out.print("\n수정할 임원 번호 : ");
            String man_id = sc.next();

            String query = "SELECT * FROM Manager WHERE man_id = ?;";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, man_id);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) {
                System.out.println(">> 데이터 수정 실패 : 해당 임원 번호가 존재하지 않습니다.");
                pstmt.close();
                return;
            }

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

            String updateQuery = "UPDATE Manager SET man_name = ?, man_phone = ?, man_grade = ?, man_dept = ?, position = ? WHERE man_id = ?;";
            PreparedStatement updatePstmt = con.prepareStatement(updateQuery);
            updatePstmt.setString(1, man_name);
            updatePstmt.setString(2, man_phone);
            updatePstmt.setInt(3, man_grade);
            updatePstmt.setString(4, man_dept);
            updatePstmt.setString(5, position);
            updatePstmt.setString(6, man_id);

            updatePstmt.executeUpdate();
            System.out.println(">> Manager 테이블의 데이터를 성공적으로 수정했습니다.");

            pstmt.close();
            updatePstmt.close();
        } catch (SQLException e) {
            System.out.println(">> 데이터 수정 실패 : " + e.getMessage());

        } catch (Exception e) {
            System.out.println(">> 예상치 못한 오류 : " + e.getMessage());
        }
    }

    // 임원 삭제 함수
    public static void deleteManager(Connection con, Scanner sc) {
        try {
            System.out.print("\n삭제할 임원 번호 : ");
            String man_id = sc.next();

            String query = "SELECT * FROM Manager WHERE man_id = ?;";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, man_id);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) {
                System.out.println(">> 데이터 수정 실패 : 해당 임원 번호가 존재하지 않습니다.");
                pstmt.close();
                return;
            }

            String deleteQuery = "DELETE FROM Manager WHERE man_id = ?;";
            PreparedStatement deletePstmt = con.prepareStatement(deleteQuery);
            deletePstmt.setString(1, man_id);

            deletePstmt.executeUpdate();
            System.out.println(">> Manager 테이블의 데이터를 성공적으로 삭제했습니다.");

            pstmt.close();
            deletePstmt.close();
            
        } catch (SQLException e) {
            System.out.println(">> 데이터 삭제 실패 : " + e.getMessage());

        } catch (Exception e) {
            System.out.println(">> 예상치 못한 오류 : " + e.getMessage());
        }
    }
}
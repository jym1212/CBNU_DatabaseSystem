import java.util.Scanner;
import java.sql.*;

public class professor {
    public static void professor_menu(Connection con, Scanner sc) {
        int menu = 0;

        while (true) {
            System.out.print("\n\n===============================\n");
            System.out.println("     전체 교수님 목록 관리      ");
            System.out.println("===============================");
            System.out.println("1. 교수님 목록 출력");
            System.out.println("2. 교수님 추가");
            System.out.println("3. 교수님 수정");
            System.out.println("4. 교수님 삭제");
            System.out.println("5. 교수님 검색");
            System.out.println("6. 메인 메뉴로 돌아가기");
            System.out.print(">> 메뉴 선택 : ");

            menu = sc.nextInt();

            switch (menu) {
                case 1:
                    selectALLProfessor(con);
                    break;
                case 2:
                    insertProfessor(con, sc);
                    break;
                case 4:
                    deleteProfessor(con, sc);
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

    // 전체 교수님 목록 출력 함수
    public static void selectALLProfessor(Connection con) {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Professor ORDER BY prof_id ASC;");

            System.out.print("\n---------------------------------------------------------------------------------------------------\n");
            System.out.printf("| %s | %s | %s | %s | %s |\n",
                        formatString("교수님 아이디", 12),
                        formatString("교수님 성함", 10),
                        formatString("이메일", 30),
                        formatString("랩실 이름", 20),
                        formatString("랩실 호수", 6));
            System.out.println("---------------------------------------------------------------------------------------------------");
            while (rs.next()) {
                String prof_id = formatString(rs.getString(1), 13);
                String prof_name = formatString(rs.getString(2), 11);
                String email = formatString(rs.getString(3), 30);
                String lab_name = formatString(rs.getString(4), 20);
                String lab_num = formatString(rs.getString(5), 9);
                System.out.printf("| %s | %s | %s | %s | %s |\n", prof_id, prof_name, email, lab_name, lab_num);
            }
            System.out.println("---------------------------------------------------------------------------------------------------");

            stmt.close();

        } catch (SQLSyntaxErrorException e) {
            System.out.println(">> SQL 문법 오류 : " + e.getMessage());
            
        } catch (SQLException e) {
            System.out.println(">> 데이터베이스 조회 오류 : " + e.getMessage());
            System.out.println(">> SQL State : " + e.getSQLState());
            System.out.println(">> Error Code : " + e.getErrorCode());
            
        } catch (Exception e) {
            System.out.println(">> 예상치 못한 오류 : " + e.getMessage());
        }
    }
    
    // 교수님 추가 함수
    public static void insertProfessor(Connection con, Scanner sc) {
        try {
            System.out.print("교수님 아이디 : ");
            String prof_id = sc.next();
            System.out.print("교수님 성함 : ");
            String prof_name = sc.next();
            System.out.print("교수님 이메일 : ");
            String email = sc.next();
            System.out.print("랩실 이름 : ");
            String lab_name = sc.next();
            System.out.print("랩실 호수 : ");
            int lab_num = sc.nextInt();

            String query = "INSERT INTO Professor (prof_id, prof_name, email, lab_name, lab_num) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, prof_id);
            pstmt.setString(2, prof_name);
            pstmt.setString(3, email);
            pstmt.setString(4, lab_name);
            pstmt.setInt(5, lab_num);

            pstmt.executeUpdate();
            System.out.println(">> Professor 테이블에 데이터를 성공적으로 삽입했습니다.");

            pstmt.close();

        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println(">> 데이터 삽입 실패 : 교수님 아이디가 이미 존재합니다.");

        } catch (SQLException e) {
            System.out.println(">> 데이터 삽입 실패 : " + e.getMessage());
            if (e.getSQLState() != null) {
                System.out.println(">> SQL State : " + e.getSQLState());
                System.out.println(">> Error Code : " + e.getErrorCode());
            }

        } catch (Exception e) {
            System.out.println(">> 예상치 못한 오류 : " + e.getMessage());
        }
    }

    // 교수님 삭제 함수
    public static void deleteProfessor(Connection con, Scanner sc) {     
        try {
            System.out.print("\n삭제할 교수님 아이디 : ");
            int prof_id = sc.nextInt();

            String query = "DELETE FROM Professor WHERE prof_id = ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, prof_id);

            pstmt.executeUpdate();
            System.out.println(">> Professor 테이블에 데이터를 성공적으로 삭제했습니다.");

            pstmt.close();

        } catch (SQLException e) {
            System.out.println(">> 데이터 삭제 실패 : " + e.getMessage());
            if (e.getSQLState() != null) {
                System.out.println(">> SQL State : " + e.getSQLState());
                System.out.println(">> Error Code : " + e.getErrorCode());
            }

        } catch (Exception e) {
            System.out.println(">> 예상치 못한 오류 : " + e.getMessage());
        }
    }
}

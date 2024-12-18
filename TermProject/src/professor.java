import java.util.Scanner;
import java.sql.*;

public class professor {
    public static void professor_menu(Connection con, Scanner sc) {
        int menu = 0;

        while (true) {
            System.out.print("\n\n===============================\n");
            System.out.println("     담당 교수님 관리 메뉴   ");
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
                case 3:
                    updateProfessor(con, sc);
                    break;
                case 4:
                    deleteProfessor(con, sc);
                    break;
                case 5:
                    selectProfessor(con, sc);
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

    // 전체 교수님 목록 출력 함수
    public static void selectALLProfessor(Connection con) {
        try {
            String query = "SELECT * FROM Professor ORDER BY prof_id ASC;";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if (!rs.next()) {
                System.out.print("\n>> 교수님이 존재하지 않습니다.\n");
                return;
            }
            
            System.out.print("\n---------------------------------------------------------------------------------------------------\n");
            System.out.printf("| %s | %s | %s | %s | %s |\n",
                        formatString("교수님 번호", 13),
                        formatString("교수님 성함", 10),
                        formatString("이메일", 30),
                        formatString("랩실 이름", 20),
                        formatString("랩실 호수", 6));
            System.out.println("---------------------------------------------------------------------------------------------------");

            do {
                String prof_id = formatString(rs.getString(1), 13);
                String prof_name = formatString(rs.getString(2), 11);
                String email = formatString(rs.getString(3), 30);
                String lab_name = formatString(rs.getString(4), 20);
                String lab_num = formatString(rs.getString(5), 9);
                System.out.printf("| %s | %s | %s | %s | %s |\n", prof_id, prof_name, email, lab_name, lab_num);
            } while (rs.next());
            System.out.println("---------------------------------------------------------------------------------------------------");

            stmt.close();

        } catch (SQLSyntaxErrorException e) {
            System.out.println(">> SQL 문법 오류 : " + e.getMessage());
            
        } catch (SQLException e) {
            System.out.println(">> 데이터베이스 조회 오류 : " + e.getMessage());
            
        } catch (Exception e) {
            System.out.println(">> 예상치 못한 오류 : " + e.getMessage());
        }
    }
    
    // 교수님 추가 함수
    public static void insertProfessor(Connection con, Scanner sc) {
        try {
            System.out.print("\n교수님 번호 : ");
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
            System.out.println(">> 데이터 삽입 실패 : 교수님 번호가 이미 존재합니다.");

        } catch (SQLException e) {
            System.out.println(">> 데이터 삽입 실패 : " + e.getMessage());

        } catch (Exception e) {
            System.out.println(">> 예상치 못한 오류 : " + e.getMessage());
        }
    }

    // 교수님 수정 함수
    public static void updateProfessor(Connection con, Scanner sc) {
        try {
            System.out.print("\n수정할 교수님 번호 : ");
            String prof_id = sc.next();

            String query = "SELECT * FROM Professor WHERE prof_id = ?;";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, prof_id);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) {
                System.out.println(">> 해당 교수님 번호가 존재하지 않습니다.");
                return;
            }

            System.out.print("교수님 성함 : ");
            String prof_name = sc.next();
            System.out.print("교수님 이메일 : ");
            String email = sc.next();
            System.out.print("랩실 이름 : ");
            String lab_name = sc.next();
            System.out.print("랩실 호수 : ");
            int lab_num = sc.nextInt();

            String updateQuery = "UPDATE Professor SET prof_name = ?, email = ?, lab_name = ?, lab_num = ? WHERE prof_id = ?";
            PreparedStatement updatePstmt = con.prepareStatement(updateQuery);
            updatePstmt.setString(1, prof_name);
            updatePstmt.setString(2, email);
            updatePstmt.setString(3, lab_name);
            updatePstmt.setInt(4, lab_num);
            updatePstmt.setString(5, prof_id);

            updatePstmt.executeUpdate();
            System.out.println(">> Professor 테이블에 데이터를 성공적으로 수정했습니다.");

            pstmt.close();
            updatePstmt.close();

        } catch (SQLException e) {
            System.out.println(">> 데이터 수정 실패 : " + e.getMessage());
            
        } catch (Exception e) {
            System.out.println(">> 예상치 못한 오류 : " + e.getMessage());
        }
    }

    // 교수님 삭제 함수
    public static void deleteProfessor(Connection con, Scanner sc) {
        try {
            System.out.print("\n삭제할 교수님 번호 : ");
            String prof_id = sc.next();

            String query = "SELECT * FROM Professor WHERE prof_id = ?;";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, prof_id);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) {
                System.out.println(">> 해당 교수님 번호가 존재하지 않습니다.");
                return;
            }

            String deleteQuery = "DELETE FROM Professor WHERE prof_id = ?";
            PreparedStatement deletePstmt = con.prepareStatement(deleteQuery);
            deletePstmt.setString(1, prof_id);

            deletePstmt.executeUpdate();
            System.out.println(">> Professor 테이블에 데이터를 성공적으로 삭제했습니다.");

            pstmt.close();
            deletePstmt.close();

        } catch (SQLException e) {
            System.out.println(">> 데이터 삭제 실패 : " + e.getMessage());

        } catch (Exception e) {
            System.out.println(">> 예상치 못한 오류 : " + e.getMessage());
        }
    }

    // 교수님 검색 함수
    public static void selectProfessor(Connection con, Scanner sc) {
        try {
            System.out.print("\n검색할 교수님 번호 : ");
            String search_prof_id = sc.next();

            String query = "SELECT * FROM Professor WHERE prof_id = ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, search_prof_id);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) {
                System.out.println(">> 해당 교수님 번호가 존재하지 않습니다.");
                return;
            }

            System.out.print("\n---------------------------------------------------------------------------------------------------\n");
            System.out.printf("| %s | %s | %s | %s | %s |\n",
                        formatString("교수님 번호", 13),
                        formatString("교수님 성함", 10),
                        formatString("이메일", 30),
                        formatString("랩실 이름", 20),
                        formatString("랩실 호수", 6));
            System.out.println("---------------------------------------------------------------------------------------------------");
            do {
                String prof_id = formatString(rs.getString(1), 13);
                String prof_name = formatString(rs.getString(2), 11);
                String email = formatString(rs.getString(3), 30);
                String lab_name = formatString(rs.getString(4), 20);
                String lab_num = formatString(rs.getString(5), 9);
                System.out.printf("| %s | %s | %s | %s | %s |\n", prof_id, prof_name, email, lab_name, lab_num);
            } while (rs.next());
            System.out.println("---------------------------------------------------------------------------------------------------");

            pstmt.close();

        } catch (SQLSyntaxErrorException e) {
            System.out.println(">> SQL 문법 오류 : " + e.getMessage());
            
        } catch (SQLException e) {
            System.out.println(">> 데이터베이스 조회 오류 : " + e.getMessage());

        } catch (Exception e) {
            System.out.println(">> 예상치 못한 오류 : " + e.getMessage());
        }
    }
}

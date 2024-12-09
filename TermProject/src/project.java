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
                case 2:
                    insertProject(con, sc);
                    break;
                case 3:
                    updateProject(con, sc);
                    break;
                case 4:
                    deleteProject(con, sc);
                    break;
                case 5:
                    selectProject(con, sc);
                    break;
                case 6:
                    selectALLWorkOn(con, sc);
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
        try {
            String query = "SELECT * FROM Project ORDER BY club_id, project_id";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if (!rs.next()) {
                System.out.print("\n>> 프로젝트 목록이 존재하지 않습니다.\n");
                stmt.close();
                return;
            }

            System.out.print(
                    "\n---------------------------------------------------------------------------------------------\n");
            System.out.printf("| %s | %s | %s | %s | %s |\n",
                    formatString("프로젝트 번호", 10),
                    formatString("프로젝트 이름", 30),
                    formatString("프로젝트 날짜", 15),
                    formatString("인원수", 5),
                    formatString("동아리 번호", 13));
            System.out.println(
                    "---------------------------------------------------------------------------------------------");

            do {
                String project_id = formatString(rs.getString(1), 13);
                String project_name = formatString(rs.getString(2), 30);
                String project_date = formatString(rs.getString(3), 15);
                String total_num = formatString(rs.getString(4), 6);
                String club_id = formatString(rs.getString(5), 13);
                System.out.printf("| %s | %s | %s | %s | %s |\n", project_id, project_name, project_date, total_num,
                        club_id);
            } while (rs.next());
            System.out.println(
                    "---------------------------------------------------------------------------------------------");

            stmt.close();

        } catch (SQLSyntaxErrorException e) {
            System.out.println(">> SQL 문법 오류 : " + e.getMessage());

        } catch (SQLException e) {
            System.out.println(">> 데이터베이스 조회 오류 : " + e.getMessage());

        } catch (Exception e) {
            System.out.println(">> 예상치 못한 오류 : " + e.getMessage());
        }
    }
    
    // 프로젝트 추가 함수
    public static void insertProject(Connection con, Scanner sc) {
        try {
            Integer valid_club_id = null;

            System.out.print("\n동아리 코드 : ");
            int club_id = sc.nextInt();

            String query = "SELECT club_id FROM Club WHERE club_id = ?;";
            try (PreparedStatement pstmt = con.prepareStatement(query)) {
                pstmt.setInt(1, club_id);
                ResultSet rs = pstmt.executeQuery();
                if (!rs.next()) {
                    System.out.print("\n>> 데이터 삽입 실패 : 동아리 번호가 존재하지 않습니다.\n");
                    pstmt.close();
                    return;
                } else {
                    valid_club_id = club_id;
                    pstmt.close();
                }
            }

            System.out.print("\n프로젝트 번호 : ");
            int project_id = sc.nextInt();
            System.out.print("프로젝트 이름 : ");
            String project_name = sc.next();
            System.out.print("프로젝트 날짜(YYYY-MM-DD) : ");
            String project_date = sc.next();
            java.sql.Date sql_date = java.sql.Date.valueOf(project_date);
            System.out.print("인원수 : ");
            int total_num = sc.nextInt();

            String insertQuery = "INSERT INTO Project (project_id, project_name, project_date, total_num, club_id) VALUES(?, ?, ?, ?, ?);";
            PreparedStatement insertPstmt = con.prepareStatement(insertQuery);
            insertPstmt.setInt(1, project_id);
            insertPstmt.setString(2, project_name);
            insertPstmt.setDate(3, sql_date);
            insertPstmt.setInt(4, total_num);
            insertPstmt.setInt(5, valid_club_id);

            insertPstmt.executeUpdate();
            System.out.println(">> Project 테이블에 데이터를 성공적으로 삽입했습니다.");

            insertPstmt.close();

        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println(">> 데이터 삽입 실패 : 프로젝트 번호가 이미 존재합니다.");

        } catch (SQLSyntaxErrorException e) {
            System.out.println(">> SQL 문법 오류 : " + e.getMessage());

        } catch (SQLException e) {
            System.out.println(">> 데이터베이스 입력 오류 : " + e.getMessage());

        } catch (Exception e) {
            System.out.println(">> 예상치 못한 오류 : " + e.getMessage());
        }
    }
    
    // 프로젝트 수정 함수
    public static void updateProject(Connection con, Scanner sc) {
        try {
            System.out.print("\n수정할 프로젝트 번호 : ");
            int project_id = sc.nextInt();

            String query = "SELECT * FROM Project WHERE project_id = ?;";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, project_id);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) {
                System.out.println(">> 데이터 수정 실패 : 프로젝트 번호가 존재하지 않습니다.");
                pstmt.close();
                return;
            }

            System.out.print("프로젝트 이름 : ");
            String project_name = sc.next();
            System.out.print("프로젝트 날짜(YYYY-MM-DD) : ");
            String project_date = sc.next();
            java.sql.Date sql_date = java.sql.Date.valueOf(project_date);
            System.out.print("인원수 : ");
            int total_num = sc.nextInt();

            String updateQuery = "UPDATE Project SET project_name = ?, project_date = ?, total_num = ? WHERE project_id = ?;";
            PreparedStatement updatePstmt = con.prepareStatement(updateQuery);
            updatePstmt.setString(1, project_name);
            updatePstmt.setDate(2, sql_date);
            updatePstmt.setInt(3, total_num);
            updatePstmt.setInt(4, project_id);

            updatePstmt.executeUpdate();
            System.out.println(">> Project 테이블의 데이터를 성공적으로 수정했습니다.");

            updatePstmt.close();

        } catch (SQLSyntaxErrorException e) {
            System.out.println(">> SQL 문법 오류 : " + e.getMessage());

        } catch (SQLException e) {
            System.out.println(">> 데이터베이스 수정 오류 : " + e.getMessage());

        } catch (Exception e) {
            System.out.println(">> 예상치 못한 오류 : " + e.getMessage());
        }
    }
    
    // 프로젝트 삭제 함수
    public static void deleteProject(Connection con, Scanner sc) {
        try {
            System.out.print("\n삭제할 프로젝트 번호 : ");
            int project_id = sc.nextInt();

            String query = "SELECT * FROM Project WHERE project_id = ?;";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, project_id);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) {
                System.out.print("\n>> 데이터 삭제 실패 : 프로젝트 번호가 존재하지 않습니다.\n");
                pstmt.close();
                return;
            }

            try {
                String deleteQuery1 = "DELETE FROM Work_on WHERE project_id = ?;";
                PreparedStatement deletePstmt1 = con.prepareStatement(deleteQuery1);
                deletePstmt1.setInt(1, project_id);

                deletePstmt1.executeUpdate();
                deletePstmt1.close();

                System.out.print("\n>> Work_on 테이블에서 관련 데이터를 삭제했습니다.\n");

            } catch (SQLException e) {
                if (e.getMessage().contains("doesn't exist")) {
                    System.out.print("\n>> Work_on 테이블이 존재하지 않아 삭제를 하지 않습니다.\n");
                } else {
                    throw e;
                }
            }

            String deleteQuery2 = "DELETE FROM Project WHERE project_id = ?;";
            PreparedStatement deletePstmt2 = con.prepareStatement(deleteQuery2);
            deletePstmt2.setInt(1, project_id);
            deletePstmt2.executeUpdate();

            System.out.println(">> Project 테이블에서 데이터를 성공적으로 삭제했습니다.");

            pstmt.close();
            deletePstmt2.close();

        } catch (SQLSyntaxErrorException e) {
            System.out.println(">> SQL 문법 오류 : " + e.getMessage());

        } catch (SQLException e) {
            System.out.println(">> 데이터베이스 삭제 오류 : " + e.getMessage());

        } catch (Exception e) {
            System.out.println(">> 예상치 못한 오류 : " + e.getMessage());
        }
    }
    
    // 프로젝트 검색 함수  
    public static void selectProject(Connection con, Scanner sc) {
        try {
            System.out.print("\n검색할 프로젝트 번호 : ");
            int project_id = sc.nextInt();

            String query = "SELECT * FROM Project WHERE project_id = ?;";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, project_id);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) {
                System.out.println(">> 데이터 검색 실패 : 프로젝트 번호가 존재하지 않습니다.");
                pstmt.close();
                return;
            }

            System.out.print(
                    "\n---------------------------------------------------------------------------------------------\n");
            System.out.printf("| %s | %s | %s | %s | %s |\n",
                    formatString("프로젝트 번호", 10),
                    formatString("프로젝트 이름", 30),
                    formatString("프로젝트 날짜", 15),
                    formatString("인원수", 5),
                    formatString("동아리 번호", 13));
            System.out.println(
                    "---------------------------------------------------------------------------------------------");

            do {
                String project_id_str = formatString(rs.getString(1), 13);
                String project_name = formatString(rs.getString(2), 30);
                String project_date = formatString(rs.getString(3), 15);
                String total_num = formatString(rs.getString(4), 6);
                String club_id = formatString(rs.getString(5), 13);
                System.out.printf("| %s | %s | %s | %s | %s |\n", project_id_str, project_name, project_date, total_num,
                        club_id);
            } while (rs.next());
            System.out.println(
                    "---------------------------------------------------------------------------------------------");

            pstmt.close();

        } catch (SQLSyntaxErrorException e) {
            System.out.println(">> SQL 문법 오류 : " + e.getMessage());

        } catch (SQLException e) {
            System.out.println(">> 데이터베이스 조회 오류 : " + e.getMessage());

        } catch (Exception e) {
            System.out.println(">> 예상치 못한 오류 : " + e.getMessage());
        }
    }
    
    // 프로젝트 참여 학생 목록 출력 함수
    public static void selectALLWorkOn(Connection con, Scanner sc) {
        try{
            System.out.print("\n검색할 프로젝트 번호 : ");
            int search_project_id = sc.nextInt();

            String query = "SELECT * FROM Work_on WHERE project_id = ?;";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, search_project_id);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) {
                System.out.print("\n>> 프로젝트 참여 학생 목록이 존재하지 않습니다.\n");
                pstmt.close();
                return;
            }

            System.out.print("\n---------------------------------\n");
            System.out.printf("| %s | %s |\n",
                    formatString("프로젝트 번호", 10),
                    formatString("학생 번호", 13));
            System.out.println(
                    "---------------------------------");

            do {
                String project_id = formatString(rs.getString(1), 13);
                String stu_id = formatString(rs.getString(2), 13);
                System.out.printf("| %s | %s |n", project_id, stu_id);
            } while (rs.next());
            System.out.println(
                    "---------------------------------");

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

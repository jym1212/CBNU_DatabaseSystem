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
            System.out.println("===============================");
            System.out.println("6. 행사 참여 학생 목록 출력");
            System.out.println("7. 행사 참여 학생 추가");
            System.out.println("8. 행사 참여 학생 삭제");
            System.out.println("9. 행사 참여 학생 검색");
            System.out.println("10. 메인 메뉴로 돌아가기");
            System.out.print(">> 메뉴 선택 : ");

            menu = sc.nextInt();

            switch (menu) {
                case 1:
                    selectALLEvent(con);
                    break;
                case 2:
                    insertEvent(con, sc);
                    break;
                case 3:
                    updateEvent(con, sc);
                    break;
                case 4:
                    deleteEvent(con, sc);
                    break;
                case 5:
                    selectEvent(con, sc);
                    break;
                case 6:
                    selectALLParticipate(con, sc);
                    break;
                case 7:
                    insertParticipate(con, sc);
                    break;
                case 8:
                    deleteParticipate(con, sc);
                    break;
                case 9:
                    selectParticipate(con, sc);
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

    // 전체 행사 목록 출력 함수
    public static void selectALLEvent(Connection con) {
        try {
            String query = "SELECT * FROM Event ORDER BY club_id, event_id ASC;";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if (!rs.next()) {
                System.out.print("\n>> 행사 목록이 존재하지 않습니다.\n");
                return;
            }

            System.out.print(
                    "\n--------------------------------------------------------------------------------\n");
            System.out.printf("| %s | %s | %s | %s | %s |\n",
                    formatString("행사 번호", 10),
                    formatString("행사 이름", 20),
                    formatString("행사 날짜", 15),
                    formatString("인원수", 5),
                    formatString("동아리 번호", 13));
            System.out.println(
                    "--------------------------------------------------------------------------------");

            do {
                String event_id = formatString(rs.getString(1), 10);
                String event_name = formatString(rs.getString(2), 20);
                String event_date = formatString(rs.getString(3), 15);
                String total_num = formatString(rs.getString(4), 6);
                String club_id = formatString(rs.getString(5), 13);
                System.out.printf("| %s | %s | %s | %s | %s |\n", event_id, event_name, event_date, total_num, club_id);
            } while (rs.next());
            System.out.println(
                    "--------------------------------------------------------------------------------");

            stmt.close();

        } catch (SQLSyntaxErrorException e) {
            System.out.println(">> SQL 문법 오류 : " + e.getMessage());

        } catch (SQLException e) {
            System.out.println(">> 데이터베이스 조회 오류 : " + e.getMessage());

        } catch (Exception e) {
            System.out.println(">> 예상치 못한 오류 : " + e.getMessage());
        }
    }
    
    // 행사 추가 함수 
    public static void insertEvent(Connection con, Scanner sc) {
        try {
            Integer valid_club_id = null;
            Integer valid_event_id = null;

            System.out.print("\n동아리 코드 : ");
            int club_id = sc.nextInt();

            String query1 = "SELECT club_id FROM Club WHERE club_id = ?;";
            try (PreparedStatement pstmt1 = con.prepareStatement(query1)) {
                pstmt1.setInt(1, club_id);
                ResultSet rs1 = pstmt1.executeQuery();
                if (!rs1.next()) {
                    System.out.print("\n>> 동아리 번호가 존재하지 않습니다.\n");
                    pstmt1.close();
                    return;
                } else {
                    valid_club_id = club_id;
                    pstmt1.close();

                }
            }

            System.out.print("\n행사 번호 : ");
            int event_id = sc.nextInt();

            String query2 = "SELECT * FROM Event WHERE event_id = ?;";
            PreparedStatement pstmt2 = con.prepareStatement(query2);
            pstmt2.setInt(1, event_id);
            ResultSet rs2 = pstmt2.executeQuery();

            if (rs2.next()) {
                System.out.print("\n>> 이미 존재하는 행사 번호입니다.\n");
                pstmt2.close();
                return;
            } else {
                valid_event_id = event_id;
                pstmt2.close();
            }
            
            System.out.print("행사 이름 : ");
            String event_name = sc.next();
            System.out.print("행사 날짜(YYYY-MM-DD) : ");
            String event_date = sc.next();
            java.sql.Date sql_date = java.sql.Date.valueOf(event_date);
            System.out.print("인원수 : ");
            int total_num = sc.nextInt();

            String insertQuery = "INSERT INTO Event (event_id, event_name, event_date, total_num, club_id) VALUES (?, ?, ?, ?, ?);";
            PreparedStatement insertPstmt = con.prepareStatement(insertQuery);
            insertPstmt.setInt(1, valid_event_id);
            insertPstmt.setString(2, event_name);
            insertPstmt.setDate(3, sql_date);
            insertPstmt.setInt(4, total_num);
            insertPstmt.setInt(5, valid_club_id);

            insertPstmt.executeUpdate();
            System.out.println(">> Event 테이블에 데이터를 성공적으로 삽입했습니다.");

            insertPstmt.close();

        } catch (SQLSyntaxErrorException e) {
            System.out.println(">> SQL 문법 오류 : " + e.getMessage());

        } catch (SQLException e) {
            System.out.println(">> 데이터베이스 입력 오류 : " + e.getMessage());

        } catch (Exception e) {
            System.out.println(">> 예상치 못한 오류 : " + e.getMessage());
        }
    }
    
    // 행사 수정 함수
    public static void updateEvent(Connection con, Scanner sc) {
        try {
            System.out.print("\n수정할 행사 번호 : ");
            int event_id = sc.nextInt();

            String query = "SELECT * FROM Event WHERE event_id = ?;";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, event_id);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) {
                System.out.print("\n>> 해당 행사 번호가 존재하지 않습니다.\n");
                pstmt.close();
                return;
            }

            System.out.print("행사 이름 : ");
            String event_name = sc.next();
            System.out.print("행사 날짜(YYYY-MM-DD) : ");
            String event_date = sc.next();
            java.sql.Date sql_date = java.sql.Date.valueOf(event_date);
            System.out.print("인원수 : ");
            int total_num = sc.nextInt();

            String updateQuery = "UPDATE Event SET event_name = ?, event_date = ?, total_num = ? WHERE event_id = ?;";
            PreparedStatement updatePstmt = con.prepareStatement(updateQuery);
            updatePstmt.setString(1, event_name);
            updatePstmt.setDate(2, sql_date);
            updatePstmt.setInt(3, total_num);
            updatePstmt.setInt(4, event_id);

            updatePstmt.executeUpdate();
            System.out.println(">> Event, Participate 테이블에 데이터를 성공적으로 수정했습니다.");

            pstmt.close();
            updatePstmt.close();

        } catch (SQLSyntaxErrorException e) {
            System.out.println(">> SQL 문법 오류 : " + e.getMessage());

        } catch (SQLException e) {
            System.out.println(">> 데이터베이스 수정 오류 : " + e.getMessage());

        } catch (Exception e) {
            System.out.println(">> 예상치 못한 오류 : " + e.getMessage());
        }
    }
    
    // 행사 삭제 함수
    public static void deleteEvent(Connection con, Scanner sc) {
        try {
            System.out.print("\n삭제할 행사 번호 : ");
            int event_id = sc.nextInt();

            String query = "SELECT * FROM Event WHERE event_id = ?;";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, event_id);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) {
                System.out.print("\n>> 해당 행사 번호가 존재하지 않습니다.\n");
                pstmt.close();
                return;
            }

            try{
                String deleteQuery1 = "DELETE FROM Participate WHERE event_id = ?;";
                PreparedStatement deletePstmt1 = con.prepareStatement(deleteQuery1);
                deletePstmt1.setInt(1, event_id);

                deletePstmt1.executeUpdate();
                deletePstmt1.close();

                System.out.print("\n>> Participate 테이블에서 관련 데이터를 삭제했습니다.\n");
            
            } catch (SQLException e) {
                if (e.getMessage().contains("doesn't exist")) {
                    System.out.print("\n>> Participate 테이블이 존재하지 않아 삭제를 하지 않습니다.\n");
                } else {
                    throw e; 
                }
            }

            String deleteQuery2 = "DELETE FROM Event WHERE event_id = ?;";
            PreparedStatement deletePstmt2 = con.prepareStatement(deleteQuery2);
            deletePstmt2.setInt(1, event_id);

            deletePstmt2.executeUpdate();

            System.out.println(">> Event 테이블에 데이터를 성공적으로 삭제했습니다.");

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

    // 행사 검색 함수
    public static void selectEvent(Connection con, Scanner sc) {
        try{
            System.out.print("\n검색할 행사 번호 : ");
            int search_event_id = sc.nextInt();

            String query = "SELECT * FROM Event WHERE event_id = ?;";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, search_event_id);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) {
                System.out.print("\n>> 해당 행사 번호가 존재하지 않습니다.\n");
                pstmt.close();
                return;
            }

            System.out.print("\n--------------------------------------------------------------------------------\n");
            System.out.printf("| %s | %s | %s | %s | %s |\n",
                    formatString("행사 번호", 10),
                    formatString("행사 이름", 20),
                    formatString("행사 날짜", 15),
                    formatString("인원수", 5),
                    formatString("동아리 번호", 13));
            System.out.println("--------------------------------------------------------------------------------");

            do {
                String event_id = formatString(rs.getString(1), 10);
                String event_name = formatString(rs.getString(2), 20);
                String event_date = formatString(rs.getString(3), 15);
                String total_num = formatString(rs.getString(4), 6);
                String club_id = formatString(rs.getString(5), 13);
                System.out.printf("| %s | %s | %s | %s | %s |\n", event_id, event_name, event_date, total_num, club_id);
            } while (rs.next());
            System.out.println("--------------------------------------------------------------------------------");

            pstmt.close();

        } catch (SQLSyntaxErrorException e) {
            System.out.println(">> SQL 문법 오류 : " + e.getMessage());

        } catch (SQLException e) {
            System.out.println(">> 데이터베이스 조회 오류 : " + e.getMessage());

        } catch (Exception e) {
            System.out.println(">> 예상치 못한 오류 : " + e.getMessage());
        }
    }

    // 전체 행사 참여 학생 목록 출력 함수
    public static void selectALLParticipate(Connection con, Scanner sc) {
        try {
            System.out.print("\n출력할 행사 번호 : ");
            int search_event_id = sc.nextInt();

            String query = "SELECT * FROM Participate WHERE event_id = ?;";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, search_event_id);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) {
                System.out.print("\n>> 해당 행사 번호에 참여 학생이 존재하지 않습니다.\n");
                pstmt.close();
                return;
            }

            System.out.print("\n------------------------------\n");
            System.out.printf("| %s | %s |\n",
                    formatString("행사 번호", 10),
                    formatString("학생 번호", 13));
            System.out.println("------------------------------");

            do {
                String event_id = formatString(rs.getString(1), 10);
                String stu_id = formatString(rs.getString(2), 13);
                System.out.printf("| %s | %s |\n", event_id, stu_id);
            } while (rs.next());
            System.out.println(
                    "------------------------------");

            pstmt.close();

        } catch (SQLSyntaxErrorException e) {
            System.out.println(">> SQL 문법 오류 : " + e.getMessage());

        } catch (SQLException e) {
            System.out.println(">> 데이터베이스 조회 오류 : " + e.getMessage());

        } catch (Exception e) {
            System.out.println(">> 예상치 못한 오류 : " + e.getMessage());
        }
    }
    
    // 행사 참여 학생 추가 함수
    public static void insertParticipate(Connection con, Scanner sc) {
        try {
            System.out.print("\n행사 번호 : ");
            int event_id = sc.nextInt();

            String query1 = "SELECT * FROM Event WHERE event_id = ?;";
            PreparedStatement pstmt1 = con.prepareStatement(query1);
            pstmt1.setInt(1, event_id);
            ResultSet rs = pstmt1.executeQuery();

            if (!rs.next()) {
                System.out.print("\n>> 해당 행사 번호가 존재하지 않습니다.\n");
                pstmt1.close();
                return;
            }

            System.out.print("학생 번호 : ");
            String stu_id = sc.next();

            String query2 = "SELECT * FROM Student WHERE stu_id = ?;";
            PreparedStatement pstmt2 = con.prepareStatement(query2);
            pstmt2.setString(1, stu_id);
            ResultSet rs2 = pstmt2.executeQuery();

            if (!rs2.next()) {
                System.out.print("\n>> 해당 학생 번호가 존재하지 않습니다.\n");
                pstmt2.close();
                return;
            }

            String insertQuery = "INSERT INTO Participate (event_id, stu_id) VALUES (?, ?);";
            PreparedStatement insertPstmt = con.prepareStatement(insertQuery);
            insertPstmt.setInt(1, event_id);
            insertPstmt.setString(2, stu_id);

            insertPstmt.executeUpdate();
            System.out.println(">> Participate 테이블에 데이터를 성공적으로 삽입했습니다.");

            pstmt1.close();
            pstmt2.close();
            insertPstmt.close();

        } catch (SQLSyntaxErrorException e) {
            System.out.println(">> SQL 문법 오류 : " + e.getMessage());

        } catch (SQLException e) {
            System.out.println(">> 데이터베이스 입력 오류 : " + e.getMessage());

        } catch (Exception e) {
            System.out.println(">> 예상치 못한 오류 : " + e.getMessage());
        }

    }

    // 행사 참여 학생 삭제 함수
    public static void deleteParticipate(Connection con, Scanner sc) {
        try {
            System.out.print("\n삭제할 학생 번호 : ");
            String stu_id = sc.next();

            String query1 = "SELECT * FROM Participate WHERE stu_id = ?;";
            PreparedStatement pstmt1 = con.prepareStatement(query1);
            pstmt1.setString(1, stu_id);
            ResultSet rs1 = pstmt1.executeQuery();

            if (!rs1.next()) {
                System.out.print("\n>> 해당 학생 번호가 존재하지 않습니다.\n");
                pstmt1.close();
                return;
            }

            System.out.print("삭제할 행사 번호 : ");
            int event_id = sc.nextInt();

            String query2 = "SELECT * FROM Participate WHERE event_id = ? AND stu_id = ?;";
            PreparedStatement pstmt2 = con.prepareStatement(query2);
            pstmt2.setInt(1, event_id);
            pstmt2.setString(2, stu_id);
            ResultSet rs2 = pstmt2.executeQuery();

            if (!rs2.next()) {
                System.out.print("\n>> 해당 학생이 해당 행사에 참여하지 않았습니다.\n");
                pstmt1.close();
                pstmt2.close();
                return;
            }

            String deleteQuery = "DELETE FROM Participate WHERE event_id = ? AND stu_id = ?;";
            PreparedStatement deletePstmt = con.prepareStatement(deleteQuery);
            deletePstmt.setInt(1, event_id);
            deletePstmt.setString(2, stu_id);

            deletePstmt.executeUpdate();
            System.out.println(">> Participate 테이블에 데이터를 성공적으로 삭제했습니다.");

            pstmt1.close();
            pstmt2.close();
            deletePstmt.close();

        } catch (SQLSyntaxErrorException e) {
            System.out.println(">> SQL 문법 오류 : " + e.getMessage());

        } catch (SQLException e) {
            System.out.println(">> 데이터베이스 삭제 오류 : " + e.getMessage());

        } catch (Exception e) {
            System.out.println(">> 예상치 못한 오류 : " + e.getMessage());
        }
    }

    // 행사 참여 학생 검색 함수
    public static void selectParticipate(Connection con, Scanner sc) {
        try {
            System.out.print("\n검색할 학생 번호 : ");
            String search_stu_id = sc.next();

            String query = "SELECT * FROM Participate WHERE stu_id = ?;";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, search_stu_id);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) {
                System.out.print("\n>> 해당 학생 번호가 존재하지 않습니다.\n");
                pstmt.close();
                return;
            }

            System.out.print("\n------------------------------\n");
            System.out.printf("| %s | %s |\n",
                    formatString("행사 번호", 10),
                    formatString("학생 번호", 13));
            System.out.println("------------------------------");

            do {
                String event_id = formatString(rs.getString(1), 10);
                String stu_id = formatString(rs.getString(2), 13);
                System.out.printf("| %s | %s |\n", event_id, stu_id);
            } while (rs.next());
            System.out.println(
                    "------------------------------");

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

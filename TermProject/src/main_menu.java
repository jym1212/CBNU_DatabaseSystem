import java.util.Scanner;
import java.sql.*;

public class main_menu {
    public static void main(String[] args) throws Exception {
        int menu = 0;

        //메인 메뉴 출력
        System.out.println("======================================================");
        System.out.println("    충북대학교 소프트웨어학과 동아리 관리 프로그램     ");
        System.out.println("          소프트웨어학과 2021041078 정윤민           ");
        System.out.println("======================================================");
        System.out.println("1. 동아리 관리 DB 연결          2. 전체 동아리 목록");
        System.out.println("3. 동아리 회원 관리             4. 동아리 임원 관리");
        System.out.println("5. 동아리 행사 관리             6. 동아리 프로젝트 관리");
        System.out.println("7. 동아리 비품 관리             8. 동아리 관리 DB 해제");
        System.out.println("======================================================");
        System.out.print("메뉴 선택 : ");

        Scanner sc = new Scanner(System.in);
        menu = sc.nextInt();

        sc.close();
    }
}
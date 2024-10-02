import java.sql.*;
import java.util.Scanner;

public class Main {
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver"; // MySQL JDBC driver
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        Class.forName(JDBC_DRIVER);
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", "forgot");
        Statement statement = connection.createStatement();
//        statement.executeUpdate("use java_db");
//        ResultSet resultset = statement.executeQuery("SELECT * FROM person");
//        while (resultset.next()) {
//            System.out.println(resultset.getString("id") + " " + resultset.getString("Firstname") + " ");
//        }
        String query = "CREATE DATABASE IF NOT EXISTS CourseManagement";
        statement.executeUpdate(query);
        statement.executeUpdate("USE CourseManagement");
////        create tables student, subject, student_subject

        createTables();
//
//        statement.executeUpdate(query);
        choice(statement);
    }

    public static void choice(Statement statement) throws SQLException {
        Scanner sc = new Scanner(System.in);
        int choice = 0;
        do{
        System.out.println("1. Add new Student\n 2. Add new subject \n 3. Select Student\n 4. Exit");
        choice = sc.nextInt();
        switch (choice) {
            case 1:
                addStudent();
                break;
            case 2:
                addSubject();
                break;
            case 3:
                  selectStudent(statement);
                  break;
            case 4:
                break;
            default:
                System.out.println("Invalid Choice");
        }
        }while(choice!=4);
    }

    public static void createTables() {
        // SQL query to create the student table
        String createStudentTableQuery = "CREATE TABLE IF NOT EXISTS student (id INT PRIMARY KEY, name VARCHAR(100))";
        Thread createStudentTableThread = new Thread(new DatabaseUpdate(createStudentTableQuery));
        createStudentTableThread.start();

        // SQL query to create the subject table
        String createSubjectTableQuery = "CREATE TABLE IF NOT EXISTS subject (code INT PRIMARY KEY, name VARCHAR(100))";
        Thread createSubjectTableThread = new Thread(new DatabaseUpdate(createSubjectTableQuery));
        createSubjectTableThread.start();

        // SQL query to create the student_subject table
        String createStudentSubjectTableQuery = "CREATE TABLE IF NOT EXISTS student_subject (student_id INT, subject_code INT, PRIMARY KEY (student_id, subject_code))";
        Thread createStudentSubjectTableThread = new Thread(new DatabaseUpdate(createStudentSubjectTableQuery));
        createStudentSubjectTableThread.start();
    }
    public static void addStudent(){
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Student ID");
        int id = sc.nextInt();
        System.out.println("Enter Student Name");
        String name = sc.next();
        String query = "INSERT INTO student VALUES (" + id + ", '" + name + "')";
        Thread thread = new Thread(new DatabaseUpdate(query));
        thread.start();
    }

    public static void addSubject(){
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Subject Code");
        int code = sc.nextInt();
        System.out.println("Enter Subject Name");
        String name = sc.next();
        String query = "INSERT INTO subject VALUES (" + code + ", '" + name + "')";
        Thread thread = new Thread(new DatabaseUpdate(query));
        thread.start();
    }
    public static void selectStudent(Statement statement) throws SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Student ID");
        int id = sc.nextInt();
        String query = "SELECT * FROM student WHERE id = " + id;
        statement.executeUpdate("USE CourseManagement");
        try {
            ResultSet resultSet = statement.executeQuery(query);
            String name = "";
            if (!resultSet.next()) {
                System.out.println("Student not found");
                return;
            } else {
                name = resultSet.getString("name");
                System.out.println(name);
                int choice = 0;
                do {
                    System.out.println("Welcome " + name);
                    System.out.println("1. Add new course\n 2. Remove course \n 3. Display courses \n 4. Exit");
                    choice = sc.nextInt();
                    String query1 = "SELECT * FROM subject";
                    String query2;
                    int code;
                    switch (choice) {
                        case 1:
                            query1 = "SELECT code,name FROM subject ";
                            Thread thread_subject = new Thread(new DatabaseQuery(query1, "code", "name"));

                            thread_subject.start();
                            System.out.println("Enter Subject Code");
                            code = sc.nextInt();
                            String checkSubject = "SELECT * FROM subject WHERE code = '" + code+"'";
                            ResultSet rs =statement.executeQuery(checkSubject);
                            if(!rs.next()){
                                System.out.println("Subject not found");
                                break;
                            }
                            query2 = "INSERT INTO student_subject VALUES (" + id + ", " + code + ")";
                            Thread thread = new Thread(new DatabaseUpdate(query2));
                            thread.start();
                            break;
                        case 2:
                            System.out.println("Enter Subject Code");
                            code = sc.nextInt();
                            query2 = "DELETE FROM student_subject WHERE student_id = " + id + " AND subject_code = " + code;
                            Thread thread1 = new Thread(new DatabaseUpdate(query2));
                            thread1.start();
                            break;
                        case 3:
                            String query3 = "SELECT * FROM student_subject WHERE student_id = " + id;
                            Thread thread2 = new Thread(new DatabaseQuery(query3, "subject_code", "student_id"));
                            thread2.start();
                            break;
                        case 4:
                            break;
                        default:
                            System.out.println("Invalid Choice");
                    }
                } while (choice != 4);
            }} catch(SQLException e){
                System.out.println(e);
            }


    }
}




/*
INSERT INTO student_subject (student_id, subject_code)
SELECT s.student_id, su.subject_code
FROM student s
JOIN student_subject su ON s.student_id = su.student_id
WHERE s.student_id = ? AND su.subject_code = ?
 */

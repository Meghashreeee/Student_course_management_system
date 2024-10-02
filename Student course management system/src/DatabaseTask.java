import java.sql.*;

class DatabaseQuery implements Runnable {
    private String query;
    private String params[];

    public DatabaseQuery(String query, String ... args) {
        this.query = query;
        this.params = args;
    }

    @Override
    public void run() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "forgot");
            Statement statement = connection.createStatement();
            statement.executeUpdate("USE CourseManagement");
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                for(String param : params) {
                    System.out.print(param+": "+resultSet.getString(param) + " ,");
                }
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


//class to run update queries
class DatabaseUpdate implements Runnable{
    private String query;

    DatabaseUpdate(String query)
    {
        this.query = query;
    }
    @Override
    public void run() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "forgot");
            Statement statement = connection.createStatement();
            statement.executeUpdate("USE CourseManagement");
            statement.executeUpdate(query);
            System.out.println("Update successful");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



}
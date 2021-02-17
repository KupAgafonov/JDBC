import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.sql.*;

public class Main {

    private static final Logger LOGGER = LogManager.getLogger(Main.class);
    private static final Marker EXCEPTION_MARKER = MarkerManager.getMarker("EXCEPTION");
    private static final Marker PRINT_CONSOLE = MarkerManager.getMarker("PRINT");
    private static final Marker RESULT_FILE = MarkerManager.getMarker("RESULT");


    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/workbench?useSSL=false&serverTimezone=Europe/Moscow";
        String user = "root";
        String pass = "c3e077i11d22";

        try (Connection connection = DriverManager.getConnection(url, user, pass)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT course_name , " +
                    "COUNT(*) / (MAX(month(subscription_date)) - MIN(month(subscription_date)) + 1) AS avg " +
                    "FROM workbench.purchaselist " +
                    "GROUP BY course_name ;\n");    // для расчета функциями по каждому курсу

            while (resultSet.next()){
                String name = resultSet.getString("course_name");
                String avg = resultSet.getString("avg");
                LOGGER.info(PRINT_CONSOLE, getFormatStringInfo(name, avg));
                LOGGER.info(RESULT_FILE, getFormatStringInfo(name, avg));
            }
        } catch (SQLException exception) {
            LOGGER.error(EXCEPTION_MARKER, (Object) exception);
        }
    }

    static String getFormatStringInfo(String courseName, String avgValue) {
        return String.format("| %-50.50s | %7.7s |", courseName, avgValue);
    }

}

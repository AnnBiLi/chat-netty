package chat.utils;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class C3p0Util {
    private static ComboPooledDataSource dataSource =
            new ComboPooledDataSource("mysql");
    static Connection cn = null;

    static {
        if (cn == null) {
            try {
                cn = dataSource.getConnection();
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("读取MySQL配置异常");
            }
        }
    }

    //获取连接
    public static Connection getConnection() {
        return cn;
    }

    /**
     * 关闭连接
     */
    public static void close(Connection connection, PreparedStatement statement, ResultSet resultSet) {

        try {
            if (connection != null) {
                connection.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


//    public static void main(String[] args) {
//        Connection connection = C3p0Util.getConnection();
//        String sql = "select * from user where id = ?";
//        try {
//            PreparedStatement preparedStatement = connection.prepareStatement(sql);
//            preparedStatement.setString(1, "2");
//            ResultSet resultSet = preparedStatement.executeQuery();
//            while (resultSet.next()) {
//                System.out.println(resultSet.getString("name"));
//            }
//            preparedStatement.execute();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//    }

    public static void main(String[] args) {
        int id = 0;
        String name = "离线";
        //获取connect实例
        Connection connection = C3p0Util.getConnection();
        String sql = "select id from userState where name = ? ";


        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1,name);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()){
                id = resultSet.getInt("id");
                System.out.println("kkkk");
            }
            //关闭资源
            statement.close();
            resultSet.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        System.out.println(id);
    }

}

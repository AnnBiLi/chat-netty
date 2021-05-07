package chat.mapper;

import chat.bean.User;
import chat.utils.C3p0Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Auther:WYL
 * @Date:2021/4/21 - 04 - 21
 * @Time:17:07
 * @version:1.0
 */
public class UserMapper {

    //将对数据库的查询写在mapper中

    /**
     * 根据用户名查找用户对象
     */
    public User getUserByName(String name){
        //获取数据库连接
        Connection connection = C3p0Util.getConnection();
        String sql = "select * from user where name = ?";
        User user = null;
        try {
            //获取statement
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1,name);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                user = new User();
                //将数据库获取的结果集交给复制给自定义的user对象
                user.setId(resultSet.getInt("id"));
                user.setName(resultSet.getString("name"));
                user.setPwd(resultSet.getString("pwd"));
                user.setEmail(resultSet.getString("email"));

            }
            //关闭资源
            statement.close();
            resultSet.close();
            //        C3p0Util.close(connection,statement,resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    /**
     * 根据用户名和密码查找判断用户是否存在
     */
    public boolean isExits(String name,String pwd){
        boolean result = false;

        //获取数据库连接
        Connection connection = C3p0Util.getConnection();
        String sql = "select * from user where name = ? and pwd = ?";
        User user = null;
        try {
            user = new User();
            //获取statement
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1,name);
            statement.setString(2,pwd);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                //存在该用户
                result = true;

            }
            //关闭资源
            statement.close();
            resultSet.close();
            //  C3p0Util.close(connection,statement,resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 根据用户名和邮箱获取密码
     */
    public String getPwd(String name,String email){
        String pwd = null;
        //获取数据库连接
        Connection connection = C3p0Util.getConnection();
        String sql = "select * from user where name = ? and email = ?";
        User user = null;
        try {
            user = new User();
            //获取statement
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1,name);
            statement.setString(2,email);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                //存在该用户
                pwd = resultSet.getString("pwd");
            }
            //关闭资源
            statement.close();
            resultSet.close();
            //  C3p0Util.close(connection,statement,resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pwd;
    }

    public User getUserByEmail(String email){
        //获取数据库连接
        Connection connection = C3p0Util.getConnection();
        String sql = "select * from user where emial = ?";
        User user = null;
        try {
            //获取statement
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1,email);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                user = new User();
                //将数据库获取的结果集交给复制给自定义的user对象
                user.setId(resultSet.getInt("id"));
                user.setName(resultSet.getString("name"));
                user.setPwd(resultSet.getString("pwd"));
                user.setEmail(resultSet.getString("email"));

            }
            //关闭资源
            statement.close();
            resultSet.close();
            //        C3p0Util.close(connection,statement,resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public int insertRegisterByUser(String name, String pwd, String email) {
        //获取数据库连接
        Connection connection = C3p0Util.getConnection();
        String sql = "insert into user(name,pwd,email) values(?,?,?)";
        try {
            //获取Statement实例
            PreparedStatement statement = connection.prepareStatement(sql);

            //参数赋值
            statement.setString(1, name);
            statement.setString(2, pwd);
            statement.setString(3, email);

            //执行插入操作
            int res = statement.executeUpdate();
            if (res == 1){
                return 1;//1表示插入成功
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean updateModifyPwdByName(String name, String oldPwd, String newPwd) {
        //获取connect实例
        Connection connection = C3p0Util.getConnection();
        String sql = "update user set pwd=? where name=? and pwd=?";

        try {
            //获取statement实例
            PreparedStatement statement = connection.prepareStatement(sql);

            //参数赋值
            statement.setString(1,newPwd);
            statement.setString(2,name);
            statement.setString(3,oldPwd);
            //执行更新操作
            int res = statement.executeUpdate();

            if (res == 1){
                //1表示更新成功
                return true;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public void updateUserStateToOnline(String name) {
        int id = searchStateIdByName("在线");
        //获取connect实例
        Connection connection = C3p0Util.getConnection();
        String sql = "update user set user_state_id=? where name=? ";
        try {
            //获取statement实例
            PreparedStatement statement = connection.prepareStatement(sql);

            //参数赋值
            statement.setInt(1,id);
            statement.setString(2,name);
            //执行更新操作
            statement.executeUpdate();


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public static void main(String[] args) {
        UserMapper userMapper = new UserMapper();
        int id = userMapper.searchStateIdByName("在线");
        System.out.println(id);
    }

    public int searchStateIdByName(String name){
        int id = 0;
        //获取connect实例
        Connection connection = C3p0Util.getConnection();
        String sql = "select id from userState where name = ? ";


        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1,name);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                id = resultSet.getInt("id");

            }
            //关闭资源
            statement.close();
            resultSet.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return id;
    }

    public int getUserStateByName(String name) {
        int id = 0;
        //获取connect实例
        Connection connection = C3p0Util.getConnection();
        String sql = "select user_state_id from user where name = ? ";

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1,name);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                id = resultSet.getInt("user_state_id");
            }
            //关闭资源
            statement.close();
            resultSet.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return id;
    }


    public void updateUserStateOffline(String name) {
        int id = searchStateIdByName("离线");
        //获取connect实例
        Connection connection = C3p0Util.getConnection();
        String sql = "update user set user_state_id=? where name=? ";
        try {
            //获取statement实例
            PreparedStatement statement = connection.prepareStatement(sql);

            //参数赋值
            statement.setInt(1,id);
            statement.setString(2,name);
            //执行更新操作
            statement.executeUpdate();


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public String getStateNameById(int userState) {
        String name = null;
        //获取connect实例
        Connection connection = C3p0Util.getConnection();
        String sql = "select name from userState where id = ? ";

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1,userState);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                name = resultSet.getString("id");
            }
            //关闭资源
            statement.close();
            resultSet.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return name;
    }
}

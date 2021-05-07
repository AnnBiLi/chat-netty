package chat.bean;

/**
 * User类和数据库对应
 */
public class User {
    private int id;
    private String name;
    private String pwd;
    private String email;
    private int user_state_id;

    public int getUser_state_id() {
        return user_state_id;
    }

    public void setUser_state_id(int user_state_id) {
        this.user_state_id = user_state_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", pwd='" + pwd + '\'' +
                ", email='" + email + '\'' +
                ", user_state_id=" + user_state_id +
                '}';
    }
}

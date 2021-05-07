package chat.bean;

/**
 * @Auther:WYL
 * @Date:2021/4/24 - 04 - 24
 * @Time:18:22
 * @version:1.0
 */
public class UserState {

    private int id;

    private String name;

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

    @Override
    public String toString() {
        return "UserState{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}

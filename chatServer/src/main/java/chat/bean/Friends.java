package chat.bean;

/**
 * @Auther:WYL
 * @Date:2021/4/24 - 04 - 24
 * @Time:22:18
 * @version:1.0
 */
public class Friends {

    private int id;
    //备注昵称
    private String name;
    //朋友的id
    private int friend_id;
    //朋友类型id
    private int friend_type_id;
    //朋友分组id
    private int friend_group_id;

    @Override
    public String toString() {
        return "Friends{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", friend_id=" + friend_id +
                ", friend_type_id=" + friend_type_id +
                ", friend_group_id=" + friend_group_id +
                '}';
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

    public int getFriend_id() {
        return friend_id;
    }

    public void setFriend_id(int friend_id) {
        this.friend_id = friend_id;
    }

    public int getFriend_type_id() {
        return friend_type_id;
    }

    public void setFriend_type_id(int friend_type_id) {
        this.friend_type_id = friend_type_id;
    }

    public int getFriend_group_id() {
        return friend_group_id;
    }

    public void setFriend_group_id(int friend_group_id) {
        this.friend_group_id = friend_group_id;
    }
}

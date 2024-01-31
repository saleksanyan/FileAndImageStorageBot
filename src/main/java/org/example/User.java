package org.example;


import jakarta.persistence.*;

import java.util.Objects;

/**
 * users table
 */
@Entity
@Table(name = "users")
@NamedQueries({
        //gives users
        @NamedQuery(
                name = "get_users",
                query = "SELECT u FROM User u "
        ),
        @NamedQuery(
                name = "get_user_by_id",
                query = "SELECT u FROM User u WHERE u.userID = :userID "
        )


})
public class User {


    @Id
    @Column(name = "user_id", nullable=false)
    private Long userID;

    @Column(name = "username")
    private String username;


    public User(Long userID,String username) {
        this.userID = userID;
        this.username = username;
    }

    public User() {

    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    @Override
    public String toString() {
        return "User{" +
                "userID=" + userID +
                ", username='" + username + '\'' + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userID, user.userID) && Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userID, username);
    }
}

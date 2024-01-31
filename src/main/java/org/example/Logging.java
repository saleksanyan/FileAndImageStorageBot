package org.example;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * provides logging and checking of the user
 */
public class Logging {

    static String check(Session session, Long user_id, String username) {

        User u = getById(session, user_id);
        if (u == null) {
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setUserID(user_id);
            session.persist(newUser);
            session.flush();

            System.out.println("User not exists in database. Written.");
            return "no_exists";
        } else {
            System.out.println("User exists in database.");
            return "exists";
        }


    }


    //gives user that have the given id
    public static User getById(Session session,long id) {
        return session.get(User.class, id);
    }
}

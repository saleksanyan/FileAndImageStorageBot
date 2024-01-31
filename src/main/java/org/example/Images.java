package org.example;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.sql.Blob;


/**
 * images table
 */
@Entity
@Table(name = "images")
@NamedQueries({
        //gives images
        @NamedQuery(
                name = "get_images",
                query = "SELECT i FROM Images i WHERE i.user.userID = :userID"
        ),
        @NamedQuery(
                name = "get_image_by_id",
                query = "SELECT i FROM Images i WHERE i.user.userID = :userID AND i.imageID = :imageID"
        )


})
public class Images {


    @Id
    @Column(name = "image_id", nullable=false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long imageID;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Column(name = "image_name", nullable=false)
    private String imageName;


    @Column(name = "image_data", nullable=false)
    private byte[] imageData;



    public Long getImageID() {
        return imageID;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getImageName() {
        return imageName;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public void setImageName(String fileName) {
        this.imageName = fileName;
    }

    @Override
    public String toString() {
        return imageID +
                ": "+ imageName +"\n";
    }
}

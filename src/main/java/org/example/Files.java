package org.example;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * files table
 */
@Entity
@Table(name = "files")
@NamedQueries({
        //gives files
        @NamedQuery(
                name = "get_files",
                query = "SELECT f FROM Files f WHERE f.user.userID = :userID"
        ),
        @NamedQuery(
                name = "get_file_by_id",
                query = "SELECT f FROM Files f WHERE f.user.userID = :userID AND f.fileID = :fileID "
        )
})
public class Files {


    @Id
    @Column(name = "file_id", nullable=false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long fileID;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Column(name = "file_name", nullable=false)
    private String fileName;


    @Column(name = "file_data", nullable = false)
    private byte[] fileData;

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    public Long getFileID() {
        return fileID;
    }


    public User getUser() {
        return user;
    }


    public String getFileName() {
        return fileName;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return fileID +": "+
                fileName+"\n";
    }
}

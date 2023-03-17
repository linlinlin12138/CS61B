package gitlet;

// TODO: any imports you need here
import java.io.File;
import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.TreeMap;

import static gitlet.Utils.*;


/** Represents a gitlet commit object.
 *
 *  does at a high level.
 *
 *  @author
 */
public class Commit implements Serializable {
    /**
     *
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */
    //Folder to save commits
    static final File COMMIT_DIR=join(".gitlet","commit");
    /** The message of this Commit. */
    private String message;
    /** To record when the commit is created.**/
    private Date timestamp;
    /** Reference to the files.*/

    //Commits should point to its previous commit.
    private String parent;

    //Commit should contain the snapshot of the files and their status.
    private TreeMap<String,String> files;
    public Commit(String m,Date t,String p,TreeMap f){
        message=m;
        timestamp=t;
        parent=p;
        files=f;
    }

    public String getMessage() {
        return message;
    }

    public String getParent() {
        return parent;
    }


    public Date getTimestamp() {
        return timestamp;
    }

    public String getHashCode(){
        String f=null;
        if(getFiles()!=null){
            f=getFiles().toString();
        }
        return sha1(getTimestamp().toString(),getMessage(),getParent(),f);
    }

    public TreeMap getFiles(){
        return files;
    }

    //save the commit to a file. What file????
    public void saveCommit(){
        String name=getHashCode();
        File com=join(COMMIT_DIR,name);
        File master=join(COMMIT_DIR,"master");
        File HEAD=join(COMMIT_DIR,"head");
        writeObject(com,this);
        //Create separate files to save master and head
        writeContents(master,name);
        writeContents(HEAD,name);
    }

    public static Commit findCommit(String commitName){
        File commitFile=join(COMMIT_DIR,commitName);
        String CommitName=readContentsAsString(commitFile);
        File targetCommit=join(COMMIT_DIR,CommitName);
        Commit tCommit=readObject(targetCommit,Commit.class);
        return tCommit;
    }

    /* TODO: fill in the rest of this class. */
}

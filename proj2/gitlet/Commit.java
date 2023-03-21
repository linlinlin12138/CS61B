package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.*;


/**
 * Represents a gitlet commit object.
 * <p>
 * does at a high level.
 *
 * @author
 */
public class Commit implements Serializable {
    /**
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */
    //Folder to save commits
    static final File COMMIT_DIR = join(".gitlet", "commit");
    //Folder to save the information of branched
    /**
     * The message of this Commit.
     */
    private String message;
    /**
     * To record when the commit is created.
     **/
    private Date timestamp;
    /**
     * Reference to the files.
     */

    //Commits should point to its previous commit.
    private String parent;

    //Commit should contain the snapshot of the files and their status.
    private TreeMap<String, String> files;

    public Commit(String m, Date t, String p, TreeMap f) {
        message = m;
        timestamp = t;
        parent = p;
        files = f;
    }

    public String getMessage() {
        return message;
    }

    public String getParent() {
        return parent;
    }

    public Commit getParentCommit() {
        String parentName = getParent();
        if (parentName == null) {
            return null;
        }
        return findCommit(parentName);
    }


    public Date getTimestamp() {
        return timestamp;
    }

    public String getHashCode() {
        String f;
        if (getFiles() != null) {
            f = getFiles().toString();
            return sha1(getTimestamp().toString(), getMessage(), getParent(), f);
        }
        return sha1(getTimestamp().toString(), getMessage());
    }

    public TreeMap getFiles() {
        return files;
    }

    public static void changeHead(String id) {
        File headRecording = join(COMMIT_DIR, "head");
        writeContents(headRecording, id);
    }

    public void saveBranchInfo(String branchName, String id) {
        File branchInfo = join(COMMIT_DIR, "branchInfo");
        HashMap<String, String> hm;
        if (!branchInfo.exists()) {
            hm = new HashMap<>();
        } else {
            hm = readObject(branchInfo, HashMap.class);
        }
        hm.put(branchName, id);
        writeObject(branchInfo, hm);
    }

    public static String findBranchName(String id) {
        File branchInfo = join(COMMIT_DIR, "branchInfo");
        HashMap<String, String> hm = readObject(branchInfo, HashMap.class);
        for (String branchName : hm.keySet()) {
            if (hm.get(branchName) == id) {
                return branchName;
            }
        }
        return null;
    }


    //save the commit to a file. What file????
    public void saveCommit(String branchName) {
        String name = getHashCode();
        File com = join(COMMIT_DIR, name);
        File head = join(COMMIT_DIR, "head");
        writeObject(com, this);
        //Create separate files to save master and head
        writeContents(head, name);
        saveBranchInfo(branchName, getHashCode());
    }

    public static Commit findCommit(String commitName) {
        if (commitName.equals("head")) {
            File head = join(COMMIT_DIR, "head");
            commitName = readContentsAsString(head);
        }
        File commitFile = join(COMMIT_DIR, commitName);
        if (!commitFile.exists()) {
            File branchInfo = join(COMMIT_DIR, "branchInfo");
            HashMap<String, String> hs = readObject(branchInfo, HashMap.class);
            if (!hs.containsKey(commitName)) {
                return null;
            }
            commitName = hs.get(commitName);
            commitFile = join(COMMIT_DIR, commitName);
        }
        Commit tCommit = readObject(commitFile, Commit.class);
        return tCommit;
    }

    public static void findbyMessage(String message) {
        List<String> allCommits = Utils.plainFilenamesIn(COMMIT_DIR);
        ArrayList<String> targets = new ArrayList<>();
        for (String s : allCommits) {
            Commit c = findCommit(s);
            if (c.message == message) {
                targets.add(s);
            }
        }
        if (targets.isEmpty()) {
            System.out.println("Found no commit with that message.");
            System.exit(0);
        } else {
            for (String s : targets) {
                System.out.println(s);
            }
        }
    }

    public static void createNewBranch(String branchName) {
        File branchInfo = join(COMMIT_DIR, "branchInfo");
        HashMap<String, String> hm = readObject(branchInfo, HashMap.class);
        if (hm.containsKey(branchName)) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        hm.put(branchName, findCommit("head").getHashCode());
        writeObject(branchInfo, hm);
    }

    public static void removeBranch(String branchName) {
        File branchInfo = join(COMMIT_DIR, "branchInfo");
        HashMap<String, String> hm = readObject(branchInfo, HashMap.class);
        if (!hm.containsKey(branchName)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        String id = hm.get(branchName);
        if (findCommit("head").getHashCode() == id) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        hm.remove(branchName);
        writeObject(branchInfo, hm);
    }

}

package gitlet;

import java.io.File;

import static gitlet.Utils.*;

import java.nio.file.Files;
import java.util.*;
import java.text.SimpleDateFormat;

/**
 * Represents a gitlet repository.
 */
public class Repository {
    /**
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    //The directory to put all the commits.
    public static final File COMMIT_DIR = join(".gitlet", "commit");

    public static void intializeRepository() {
        if (Files.exists(GITLET_DIR.toPath())) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        GITLET_DIR.mkdir();
        COMMIT_DIR.mkdir();
        Date time = new Date(0);
        Commit init = new Commit("initial commit", time, null, null);
        init.saveCommit("master");
    }

    public static String createBlob(String fileName) {
        File contentFile = join(CWD, fileName);
        if (!contentFile.exists()) {
            System.out.println("File does not exist");
            System.exit(0);
        }
        String content = readContentsAsString(contentFile);
        String blobName = sha1(content);
        File blob = join(GITLET_DIR, blobName);
        //Can we create duplicate blob?
        writeContents(blob, content);
        return blobName;
    }

    public static void addtoStagingArea(String fileName, String blobName) {
        TreeMap s = findStagingArea();
        if (s.containsKey(fileName) && s.get(fileName) == blobName) {
            return;
        }
        s.put(fileName, blobName);
        writeObject(join(GITLET_DIR, "stagedforadd"), s);
    }

    public static void clearStagingArea() {
        File stage = join(GITLET_DIR, "stagedforadd");
        TreeMap<String, String> addStaging = new TreeMap<>();
        writeObject(stage, addStaging);
    }


    public static TreeMap findStagingArea() {
        File stage = join(GITLET_DIR, "stagedforadd");
        if (!stage.exists()) {
            TreeMap<String, String> addStaging = new TreeMap<>();
            writeObject(stage, addStaging);
        }
        TreeMap s = readObject(stage, TreeMap.class);
        return s;
    }


    public static void createNewCommit(String message) {
        Commit parent = Commit.findCommit("head");
        String branchName = Commit.findBranchName(parent.getHashCode());
        Date presentTime = new Date();
        TreeMap<String, String> files;
        if (parent.getFiles() != null) {
            files = parent.getFiles();
        } else {
            files = new TreeMap<>();
        }
        Commit c = new Commit(message, presentTime, parent.getHashCode(), files);
        TreeMap s = findStagingArea();
        if (s.isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        } else {
            files.putAll(s);
        }
        s.clear();
        File stage = join(GITLET_DIR, "stagedforadd");
        writeObject(stage, s);
        c.saveCommit(branchName);
    }

    public static void removeFile(String fileName) {
        TreeMap s = findStagingArea();
        Boolean needtobeRemoved = false;
        if (!s.isEmpty()) {
            if (s.containsKey(fileName)) {
                needtobeRemoved = true;
                s.remove(fileName);
            }
        }
        Commit hCommit = Commit.findCommit("head");
        if (hCommit.getFiles()!=null&&hCommit.getFiles().containsKey(fileName)) {
            needtobeRemoved = true;
            hCommit.getFiles().remove(fileName);
        }
        if (needtobeRemoved == false) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
        File removed = join(CWD, fileName);
        if (removed.exists()) {
            removed.delete();
        }
    }

    public static void printLogs() {
        Commit hCommit = Commit.findCommit("head");
        Commit cur = hCommit;
        while (true) {
            System.out.println("===");
            System.out.print("commit ");
            System.out.println(cur.getHashCode());
            System.out.print("Date: ");
            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
            Date d = cur.getTimestamp();
            System.out.println(sdf.format(d));
            System.out.println(cur.getMessage());
            System.out.println();
            String name = cur.getParent();
            if (name == null) {
                return;
            }
            cur = Commit.findCommit(name);
        }
    }

    public static File findBlob(String blobName) {
        File blob = join(GITLET_DIR, blobName);
        return blob;
    }

    public static void checkOutForBranch(String branchname) {
        Commit branch = Commit.findCommit(branchname);
        if (branch == null) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        Commit head = Commit.findCommit("head");
        if (head.equals(branch)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        TreeMap<String, String> files = branch.getFiles();
        TreeMap<String, String> headFiles = head.getFiles();
        for (String name : files.keySet()) {
            if (!headFiles.containsKey(name)) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
            File f = join(CWD, name);
            writeContents(f, files.get(name));
        }
        for (String name : headFiles.keySet()) {
            if (!files.containsKey(name)) {
                removeFile(name);
            }
        }

        Commit.changeHead(branch.getHashCode());
        clearStagingArea();
    }


    public static void checkoutforHead(String fileName) {
        checkoutforID("head", fileName);
    }

    public static void checkoutforID(String id, String fileName) {
        Commit t = Commit.findCommit(id);
        TreeMap<String, String> f = t.getFiles();
        if (f.containsKey(fileName)) {
            String blobName = f.get(fileName);
            File b = findBlob(blobName);
            String content = readContentsAsString(b);
            File targetFile = join(CWD, fileName);
            writeContents(targetFile, content);
        }
        TreeMap s = findStagingArea();
        if (!s.isEmpty()) {
            if (s.containsKey(fileName)) {
                s.remove(fileName);
            }
        }
    }

    public static void printGlobalLog() {
        List<String> allCommits = Utils.plainFilenamesIn(COMMIT_DIR);
        for (String s : allCommits) {
            if(s!="head"&&s!="branchInfo"){
                Commit cur = Commit.findCommit(s);
                System.out.println("===");
                System.out.print("commit ");
                System.out.println(cur.getHashCode());
                System.out.print("Date: ");
                SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
                Date d = cur.getTimestamp();
                System.out.println(sdf.format(d));
                System.out.println(cur.getMessage());
                System.out.println();
            }
        }
    }

    public static void reset(String id) {
        Commit c = Commit.findCommit(id);
        if (c == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        Commit cur = Commit.findCommit("head");
        TreeMap<String, String> t = c.getFiles();
        TreeMap<String, String> headFile = cur.getFiles();
        for (String name : t.keySet()) {
            if (!headFile.containsKey(name)) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }
        for (String name : t.keySet()) {
            checkoutforID(id, name);
        }
        for (String name : headFile.keySet()) {
            if (!t.containsKey(name)) {
                removeFile(name);
            }
        }
        Commit.changeHead(id);
        clearStagingArea();
    }


    public static Commit findCommonAncestor(Commit branch, Commit master) {
        HashSet<Commit> parentOfBranch = new HashSet<>();
        Commit cur = branch;
        while (cur != null) {
            parentOfBranch.add(cur);
            cur = cur.getParentCommit();
        }
        Commit m = master;
        while (m != null) {
            if (parentOfBranch.contains(m)) {
                return m;
            }
            m = m.getParentCommit();
        }
        return null;
    }

    public static void treatConflicts(String fileName, File b1, File b2) {
        File f = join(CWD, fileName);
        String contentOfBranch = readContentsAsString(b1);
        String contentOfHead;
        if (b2 == null) {
            contentOfHead = "";
        } else {
            contentOfHead = readContentsAsString(b2);
        }
        String content = "<<<<<<< HEAD" + "\n" + contentOfHead + "\n" + "=======" + "\n" + contentOfBranch + "\n" + ">>>>>>>";
        writeContents(f, content);
        String blobName = createBlob(fileName);
        addtoStagingArea(fileName, blobName);
        System.out.println("Encountered a merge conflict.");
    }

    public static void merge(String branchName) {
        TreeMap<String, String> sta = findStagingArea();
        if (!sta.isEmpty()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }

        Commit branch = Commit.findCommit(branchName);
        if (branch == null) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        Commit master = Commit.findCommit("head");
        Commit br = branch;
        while (br != null) {
            if (br.equals(master)) {
                System.out.println("Cannot merge a branch with itself.");
                System.exit(0);
            }
            br = br.getParentCommit();
        }
        Commit splitPoint = findCommonAncestor(branch, master);
        TreeMap<String, String> b = branch.getFiles();
        TreeMap<String, String> m = master.getFiles();
        TreeMap<String, String> s = splitPoint.getFiles();
        for (String name : s.keySet()) {
            //modify in branch, but not in master,in master, it will stay the same as in the split point
            if (b.containsKey(name) && !b.get(name).equals(s.get(name)) && s.get(name).equals(m.get(name))) {
                File f = join(CWD, name);
                writeContents(f, findBlob(b.get(name)));
                addtoStagingArea(name, b.get(name));
            }
            //modified in branch, deleted in master
            if (!b.get(name).equals(s.get(name)) && !m.containsKey(name)) {
                File b1 = findBlob(b.get(name));
                treatConflicts(name, b1, null);
            }
            if (!m.get(name).equals(s.get(name)) && !b.containsKey(name)) {
                File b1 = findBlob(m.get(name));
                treatConflicts(name, b1, null);
            }
            //modify in master, but not in branch
            //stay the same
            //modified in both master and branch, have the same content, stay the same.
            if (!b.containsKey(name)) {
                //When a file exists in the split point, but not in the branch.
                //also unmodified in the master.
                //means it is removed in the branch.
                if (m.get(name).equals(s.get(name))) {
                    removeFile(name);
                    m.remove(name);
                    writeObject(join(COMMIT_DIR, master.getHashCode()), m);
                }
                //If it is modified in the master, it should stay at they are.
            }
            if (!m.containsKey(name)) {
                //Do we need to modify the branch commit?
                if (b.get(name).equals(s.get(name))) {
                    removeFile(name);
                    b.remove(name);
                    writeObject(join(COMMIT_DIR, branch.getHashCode()), b);
                }
            }
        }
        for (String name : b.keySet()) {
            if (!s.containsKey(name) && !m.containsKey(name)) {
                checkoutforID(branch.getHashCode(), name);
                addtoStagingArea(name, b.get(name));
            }
            if (m.containsKey(name) && !m.get(name).equals(b.get(name))) {
                File b1 = findBlob(b.get(name));
                File b2 = findBlob(m.get(name));
                treatConflicts(name, b1, b2);
            }
        }
        createNewCommit("Merged " + branchName + " into " + "master");

    }


}

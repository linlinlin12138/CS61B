package gitlet;

import java.io.File;

import static gitlet.Commit.*;
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
        Commit.changeCurBranch("master");
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
        writeContents(blob, content);
        return blobName;
    }

    public static void addtoStagingArea(String fileName, String blobName) {
        TreeMap<String, String> s = findStagingArea();
        TreeMap<String, String> r = findRemovedArea();
        Commit h = getCurHead();
        TreeMap<String, String> files = h.getFiles();
        if (s.containsKey(fileName) && blobName.equals(s.get(fileName))) {
            return;
        }
        if (r != null && r.containsKey(fileName)) {
            r.remove(fileName);
            writeObject(join(GITLET_DIR, "removedArea"), r);
            return;
        }
        if (files != null && blobName.equals(files.get(fileName))) {
            s.remove(fileName);
            writeObject(join(GITLET_DIR, "stagedforadd"), s);
            return;
        }
        s.put(fileName, blobName);
        writeObject(join(GITLET_DIR, "stagedforadd"), s);
    }

    public static void clearStagingArea() {
        File stage = join(GITLET_DIR, "stagedforadd");
        File rstage = join(GITLET_DIR, "removedArea");
        TreeMap<String, String> addStaging = new TreeMap<>();
        writeObject(stage, addStaging);
        writeObject(rstage, addStaging);
    }


    public static TreeMap<String, String> findStagingArea() {
        File stage = join(GITLET_DIR, "stagedforadd");

        if (!stage.exists()) {
            TreeMap<String, String> addStaging = new TreeMap<>();
            writeObject(stage, addStaging);
        }
        return readObject(stage, TreeMap.class);
    }


    public static void createNewCommit(String message) {
        String branchName = readContentsAsString(join(COMMIT_DIR, "curBranch"));
        Commit parent = getCurHead();
        Date presentTime = new Date();
        TreeMap<String, String> files;
        if (parent.getFiles() != null) {
            files = parent.getFiles();
        } else {
            files = new TreeMap<>();
        }
        Commit c = new Commit(message, presentTime, parent.getHashCode(), files);
        TreeMap<String, String> s = findStagingArea();
        TreeMap<String, String> r = findRemovedArea();
        TreeMap<String,String> f=c.getFiles();
        if (!s.isEmpty()) {
            f.putAll(s);
        }
        if (r != null && !r.isEmpty()) {
            for (String name : r.keySet()){
               // System.out.println(1);
                f.remove(name);
            }
        }
        else if(s.isEmpty()){
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        clearStagingArea();
        c.saveCommit(branchName);
    }

    public static TreeMap<String, String> findRemovedArea() {
        File r = join(GITLET_DIR, "removedArea");
        if (!r.exists()) {
            TreeMap<String, String> n = new TreeMap<>();
            writeObject(r, n);
            return n;
        }
        return readObject(r, TreeMap.class);
    }

    public static void removeFile(String fileName) {
        TreeMap<String, String> s = findStagingArea();
        TreeMap<String, String> removedArea = findRemovedArea();
        boolean needtobeRemoved = false;
        if (!s.isEmpty()) {
            if (s.containsKey(fileName)) {
                s.remove(fileName);
                writeObject(join(GITLET_DIR, "stagedforadd"), s);
                return;
            }
        }
        Commit hCommit = getCurHead();
        TreeMap<String, String> tm = hCommit.getFiles();
        if (tm != null && tm.containsKey(fileName)) {
            //System.out.println(1);
            needtobeRemoved = true;
            removedArea.put(fileName, tm.get(fileName));
            writeObject(join(GITLET_DIR, "removedArea"), removedArea);
            /*File c=join(COMMIT_DIR,hCommit.getHashCode());
            c.delete();
            hCommit.getFiles().remove(fileName);
            writeObject(c,hCommit.getHashCode());*/
        }
        if (!needtobeRemoved) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
        File removed = join(CWD, fileName);
        if (removed.exists()) {
            removed.delete();
        }
    }

    public static void printLogs() {
        Commit hCommit = getCurHead();
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
        return join(GITLET_DIR, blobName);
    }

    public static void checkOutForBranch(String branchname) {
        Commit branch = Commit.findCommit(branchname);
        File curBranch = join(COMMIT_DIR, "curBranch");
        String cur = readContentsAsString(curBranch);
        if (branch == null) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        if (cur.equals(branchname)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        TreeMap<String, String> files = branch.getFiles();
        TreeMap<String, String> headFiles = findCommit(cur).getFiles();
        if (files != null) {
            for (String name : files.keySet()) {
                File f = join(CWD, name);
                if (headFiles == null || !headFiles.containsKey(name)){
                    if(f.exists()&&!sha1(readContentsAsString(f)).equals(files.get(name))) {
                        System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                        System.exit(0);
                    }
                }
                String content=readContentsAsString(findBlob(files.get(name)));
                writeContents(f, content);
            }
        }
        if (headFiles != null) {
            for (String name : headFiles.keySet()) {
                if (files == null || !files.containsKey(name)) {
                    File f=join(CWD,name);
                    f.delete();
                }
            }
        }
        Commit.changeCurBranch(branchname);
        clearStagingArea();
    }


    public static void checkoutforHead(String fileName) {
        String branchName = readContentsAsString(join(COMMIT_DIR, "curBranch"));
        checkoutforID(getBranchHead(branchName), fileName);
    }

    public static void checkoutforID(String id, String fileName) {
        Commit t = Commit.findCommit(id);
        if (t == null) {
            t=Commit.findWithShortid(id);
            if(t==null){
                System.out.println("No commit with that id exists.");
                System.exit(0);
            }
        }
        TreeMap<String, String> f = t.getFiles();
        if (f.containsKey(fileName)) {
            String blobName = f.get(fileName);
            File b = findBlob(blobName);
            String content = readContentsAsString(b);
            File targetFile = join(CWD, fileName);
            writeContents(targetFile, content);
        } else {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        TreeMap<String, String> s = findStagingArea();
        if (!s.isEmpty()) {
            s.remove(fileName);
        }
    }

    public static void printStatus() {
        System.out.println("=== Branches ===");
        File branchInfo = join(COMMIT_DIR, "branchInfo");
        HashMap<String, String> branch = readObject(branchInfo, HashMap.class);
        String[] branches = new String[branch.size()];
        File curBranch = join(COMMIT_DIR, "curBranch");
        String c=readContentsAsString(curBranch);
        int i = 0;
        if (branchInfo.exists() && !branch.isEmpty()) {
            for (String name : branch.keySet()) {
                branches[i] = name;
                i++;
            }
            Arrays.sort(branches);
            for (String name : branches) {
                if (name.equals(c)) {
                    System.out.println("*" + name);
                } else {
                    System.out.println(name);
                }
            }
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        TreeMap<String, String> stage = findStagingArea();
        String[] stagedFiles = new String[stage.size()];
        i = 0;
        if (!stage.isEmpty()) {
            for (String name : stage.keySet()) {
                stagedFiles[i] = name;
                i++;
            }
            Arrays.sort(stagedFiles);
            for (String name : stagedFiles) {
                System.out.println(name);
            }
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        TreeMap<String, String> removed = findRemovedArea();
        String[] removedFiles = new String[removed.size()];
        i = 0;
        if (!removed.isEmpty()) {
            for (String name : removed.keySet()) {
                removedFiles[i] = name;
                i++;
            }
            Arrays.sort(removedFiles);
            for (String name : removedFiles) {
                System.out.println(name);
            }
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        System.out.println("=== Untracked Files ===");
        System.out.println();
    }

    public static void printGlobalLog() {
        List<String> allCommits = Utils.plainFilenamesIn(COMMIT_DIR);
        for (String s : allCommits) {
            if (!s.equals("head") && !s.equals("branchInfo") && !s.equals("curBranch")) {
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
            c=Commit.findWithShortid(id);
            if(c==null){
                System.out.println("No commit with that id exists.");
                System.exit(0);
            }
        }
        Commit cur = Commit.getCurHead();
        TreeMap<String, String> t = c.getFiles();
        TreeMap<String, String> headFile = cur.getFiles();
        if (t != null) {
            for (String name : t.keySet()) {
                if (headFile == null || !headFile.containsKey(name)){
                    File f=join(CWD,name);
                    if(f.exists()&&!sha1(readContentsAsString(f)).equals(t.get(name))){
                        System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                        System.exit(0);
                    }
                }
            }
            for (String name : t.keySet()) {
                checkoutforID(id, name);
            }
        }
        if (headFile != null) {
            for (String name : headFile.keySet()) {
                if (t != null && !t.containsKey(name)) {
                    removeFile(name);
                }
            }
        }
        File curBranch = join(COMMIT_DIR, "curBranch");
        String name = readContentsAsString(curBranch);
        Commit.changeHead(name, id);
        clearStagingArea();
    }


    public static Commit findCommonAncestor(Commit branch, Commit master) {
        HashSet<String> parentOfBranch = new HashSet<>();
        Commit cur = branch;
        while (cur != null) {
            parentOfBranch.add(cur.getHashCode());
            //System.out.println(cur.getMessage());
            cur = cur.getParentCommit();
        }
        Commit m = master;
        while (m != null) {
            if (parentOfBranch.contains(m.getHashCode())) {
                return m;
            }
            //System.out.println(m.getMessage());
            m = m.getParentCommit();
        }
        return null;
    }

    public static void treatConflicts(String fileName, File b1, File b2) {
        File f = join(CWD, fileName);
        String contentOfBranch;
        String contentOfHead;
        if (b2 == null) {
            contentOfHead = "";
        } else {
            contentOfHead = readContentsAsString(b2);
        }
        if (b1 == null) {
            contentOfBranch = "";
        } else {
            contentOfBranch = readContentsAsString(b1);
        }
        String content = "<<<<<<< HEAD" + "\n" + contentOfHead + "=======" +"\n"+ contentOfBranch + ">>>>>>>"+"\n";
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
        Commit master = Commit.getCurHead();
        File curBranch = join(COMMIT_DIR, "curBranch");
        String curName=readContentsAsString(curBranch);
        if (branchName.equals(curName)) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }
        Commit splitPoint = findCommonAncestor(branch, master);
        if(splitPoint.getHashCode().equals(master.getHashCode())){
            checkOutForBranch(branchName);
            System.out.println("Current branch fast-forwarded.");
            System.exit(0);
        }
        if(splitPoint.getHashCode().equals(branch.getHashCode())){
            System.out.println("Given branch is an ancestor of the current branch.");
            System.exit(0);
        }
        TreeMap<String, String> b = branch.getFiles();
        TreeMap<String, String> m = master.getFiles();
        TreeMap<String, String> s = splitPoint.getFiles();
        if(s!=null) {
            for (String name : s.keySet()) {
                //modify in branch, but not in master,in master, it will stay the same as in the split point
                if (b != null && b.containsKey(name) && !s.get(name).equals(b.get(name)) && s.get(name).equals(m.get(name))) {
                    File f = join(CWD, name);
                    writeContents(f, readContents(findBlob(b.get(name))));
                    addtoStagingArea(name, b.get(name));
                }
                //modified in branch, deleted in master
                if (!s.get(name).equals(b.get(name)) && (m == null || !m.containsKey(name))) {
                    if(b.get(name)!=null) {
                        File b1 = findBlob(b.get(name));
                        treatConflicts(name, b1, null);
                    }
                }
                if (!s.get(name).equals(m.get(name)) && (b == null || !b.containsKey(name))) {
                    if(m.get(name)!=null) {
                        File b1 = findBlob(m.get(name));
                        treatConflicts(name, null, b1);
                    }
                }
                //modify in master, but not in branch
                //stay the same
                //modified in both master and branch, have the same content, stay the same.
                if (b==null || !b.containsKey(name)) {
                    //When a file exists in the split point, but not in the branch.
                    //also unmodified in the master.
                    //means it is removed in the branch.
                    if (s.get(name).equals(m.get(name))) {
                        //System.out.println("1");
                        removeFile(name);
                    }
                    //If it is modified in the master, it should stay at they are.
                }
            }
        }
        if(b!=null) {
            for (String name : b.keySet()) {
                if ((s==null || !s.containsKey(name)) && (m == null || !m.containsKey(name))) {
                    checkoutforID(branch.getHashCode(), name);
                    addtoStagingArea(name, b.get(name));
                }
                if (m!=null && m.containsKey(name) && !b.get(name).equals(m.get(name))) {
                    File b1 = findBlob(b.get(name));
                    File b2 = findBlob(m.get(name));
                    treatConflicts(name, b1, b2);
                }
            }
        }
        createNewCommit("Merged " + branchName + " into " + curName + ".");

    }


}

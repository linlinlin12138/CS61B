package gitlet;



import java.io.File;
import static gitlet.Utils.*;

import java.nio.file.Files;
import java.util.Date;
import java.util.TreeMap;
import java.text.SimpleDateFormat;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File COMMIT_DIR=join(".gitlet","commit");

    public static void intializeRepository(){
        if(Files.exists(GITLET_DIR.toPath())){
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        GITLET_DIR.mkdir();
        COMMIT_DIR.mkdir();
        Date time=new Date(0);
        Commit init=new Commit("initial commit",time,null,null);
        init.saveCommit();
    }

    public static String createBlob(String fileName){
        File contentFile=join(CWD,fileName);
        if(!contentFile.exists()){
            System.out.println("File does not exist");
            System.exit(0);
        }
        String content=readContentsAsString(contentFile);
        String blobName=sha1(content);
        File blob=join(GITLET_DIR,blobName);
        //Can we create duplicate blob?
        writeContents(blob,content);
        return blobName;
    }

    public static void addtoStagingArea(String fileName,String blobName) {
        TreeMap s=findStagingArea();
        if (s.containsKey(fileName) && s.get(fileName) == blobName) {
                return;
            }
        s.put(fileName, blobName);
        writeObject(join(GITLET_DIR,"stagedforadd"),s);
        }


    public static TreeMap findStagingArea() {
        File stage = join(GITLET_DIR, "stagedforadd");
        if (!stage.exists()) {
            TreeMap<String, String> addStaging = new TreeMap<>();
            writeObject(stage, addStaging);
        }
        TreeMap s=readObject(stage,TreeMap.class);
        return s;
    }


        public static void createNewCommit(String message){
            Commit parent = Commit.findCommit("head");
            Date presentTime = new Date();
            TreeMap<String,String> files;
            if(parent.getFiles()!=null){
               files=parent.getFiles();
            }
            else{
                files=new TreeMap<>();
            }
            Commit c = new Commit(message, presentTime,parent.getHashCode(),files);
            TreeMap s=findStagingArea();
            if(s.isEmpty()){
                System.out.println("No changes added to the commit.");
                System.exit(0);
            }
            else{
                files.putAll(s);
            }
            s.clear();
            File stage=join(GITLET_DIR,"stagedforadd");
            writeObject(stage,s);
            c.saveCommit();
        }

        public static void removeFile(String fileName){
            TreeMap s=findStagingArea();
            Boolean needtobeRemoved=false;
            if (!s.isEmpty()) {
                if (s.containsKey(fileName)) {
                    needtobeRemoved=true;
                    s.remove(fileName);
                }
            }
            Commit hCommit= Commit.findCommit("head");
            if(hCommit.getFiles().containsKey(fileName)){
                needtobeRemoved=true;
                hCommit.getFiles().remove(fileName);
            }
            if(needtobeRemoved==false){
                System.out.println("No reason to remove the file.");
                System.exit(0);
            }
            File removed=join(CWD,fileName);
            if(removed.exists()){
                removed.delete();
            }
        }

        public static void printLogs() {
            Commit hCommit = Commit.findCommit("head");
            Commit cur = hCommit;
            while (cur != null) {
                System.out.println("===");
                System.out.print("commit ");
                System.out.println(cur.getHashCode());
                System.out.print("Date: ");
                SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
                Date d = cur.getTimestamp();
                System.out.println(sdf.format(d));
                System.out.println(cur.getMessage());
                System.out.println();
                String name = cur.getParent();
                cur = Commit.findCommit(name);
            }
        }

            public static File findBlob(String blobName){
                File blob=join(GITLET_DIR,blobName);
                return blob;
            }


            public static void checkoutforHead(String fileName){
                checkoutforID("head",fileName);
        }

        public static void checkoutforID(String id,String fileName){
            Commit t=Commit.findCommit(id);
            if(t.getFiles().containsKey(fileName)) {
                String blobName = (String) t.getFiles().get(fileName);
                File b = findBlob(blobName);
                String content = readContentsAsString(b);
                File targetFile = join(GITLET_DIR, fileName);
                writeContents(targetFile, content);
            }
            TreeMap s=findStagingArea();
            if(!s.isEmpty()){
                if(s.containsKey(fileName)){
                    s.remove(fileName);
                }
            }
        }






}

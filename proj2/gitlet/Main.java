package gitlet;

import java.io.File;

import static gitlet.Repository.GITLET_DIR;
import static gitlet.Utils.join;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if(args[0]==null){
            throw new GitletException("Please enter a command.");
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                Repository.intializeRepository();
                break;
            case "add":
                String fileName=args[1];
                String blobName=Repository.createBlob(fileName);
                Repository.addtoStagingArea(fileName,blobName);
                break;
            case "commit":
                if(args[1]==null){
                    System.out.println("Please enter a commit message.");
                    System.exit(0);
                }
                String message=args[1];
                break;
            case "rm":
                Repository.removeFile(args[1]);
                break;
            case "log":
                Repository.printLogs();
                break;
            case "checkout":
                if(args[1]=="--"){
                    Repository.checkoutforHead(args[2]);
                }
                if(args[2]=="--"){
                    Repository.checkoutforID(args[1],args[3]);
                }
                break;

        }
    }
}

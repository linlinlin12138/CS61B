package gitlet;

import java.nio.file.Files;

import static gitlet.Repository.GITLET_DIR;

/**
 * Driver class for Gitlet, a subset of the Git version-control system.
 *
 * @author
 */
public class Main {

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND1> <OPERAND2> ...
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        if (!firstArg.equals("init") && !Files.exists(GITLET_DIR.toPath())) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
        switch (firstArg) {
            case "init":
                Repository.intializeRepository();
                break;
            case "add":
                String fileName = args[1];
                String blobName = Repository.createBlob(fileName);
                Repository.addtoStagingArea(fileName, blobName);
                break;
            case "commit":
                if (args.length == 1 || args[1].equals("")) {
                    System.out.println("Please enter a commit message.");
                    System.exit(0);
                }
                String message = args[1];
                Repository.createNewCommit(message);
                break;
            case "status":
                Repository.printStatus();
                break;
            case "rm":
                Repository.removeFile(args[1]);
                break;
            case "log":
                Repository.printLogs();
                break;
            case "checkout":
                if (args[1].equals("--")) {
                    Repository.checkoutforHead(args[2]);
                } else if (args.length == 2) {
                    Repository.checkOutForBranch(args[1]);
                } else if (args.length == 3 && args[2].equals("--")) {
                    Repository.checkoutforID(args[1], args[3]);
                }
                else{
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                break;
            case "branch":
                Commit.createNewBranch(args[1]);
                break;
            case "global-log":
                Repository.printGlobalLog();
                break;
            case "find":
                Commit.findbyMessage(args[1]);
                break;
            case "rm-branch":
                Commit.removeBranch(args[1]);
                break;
            case "reset":
                Repository.reset(args[1]);
                break;
            case "merge":
                Repository.merge(args[1]);
                break;
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);

        }
    }
}

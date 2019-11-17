import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class JavaBall {

    public static ArrayList<String> teamList = new ArrayList<>();
    public static ArrayList<String> resultList = new ArrayList<>();

    public static void main(String[] args) {

        JavaBallForm form = new JavaBallForm();

        try {
            readFile(teamList, "TeamsIn.txt", true);
        } catch (FileNotFoundException e) {
            form.showTerminatingDialog("File not found");
        }

        form.setFrame();

    }

    public static void readFile(ArrayList<String> lineList, String pathName, boolean sort) throws FileNotFoundException {
        File file = new File(pathName);

        Scanner sc = new Scanner(file);

        while (sc.hasNextLine()) {
            lineList.add(sc.nextLine());
        }
        sc.close();

        if (sort) {
            lineList.sort(String::compareToIgnoreCase);
        }

    }

    public static ArrayList<String> createMatchList(ArrayList<String> list) {
        ArrayList<String> matchList = new ArrayList<>();

        ArrayList<String> tempList = new ArrayList<>(list);

        while (tempList.size() > 1) {
            String guest = tempList.get(0);
            tempList.remove(0);

            for (String team : tempList) {
                matchList.add(guest + " v " + team);
            }
        }
        return matchList;
    }

    public static void removeTeam(String name) {

        teamList.remove(name);

    }
}

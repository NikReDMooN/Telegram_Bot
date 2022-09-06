import java.util.HashMap;

public class Main {
    public static void main (String[] args){
        CreateHelpData mydata = new CreateHelpData();
        HashMap<String, Integer> myhash = mydata.ParcDatesAndKol();
        System.out.println(myhash.get("9_10") + myhash.get("13_10"));
        mydata.write_new_info(myhash);
        myhash = mydata.ParcDatesAndKol();
        System.out.println(myhash.get("9_10") + myhash.get("13_10"));
    }
}

package models.account;

import com.gilecode.yagson.YaGson;

import java.io.*;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Scanner;

public class Account implements Serializable {
    private String name, password;
    private int missionPassed;

    public Account(String name, String password) {
        this.missionPassed = 0;
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public int getMissionsPassed() {
        return missionPassed;
    }

    public void setNextMission() {
        this.missionPassed++;
    }

    public static Account loadJson(String name) throws FileNotFoundException {
        YaGson yaGson = new YaGson();
        FileReader fileReader = new FileReader("res/users/" + name + ".json");
        String json = new Scanner(fileReader).nextLine();
        //TODO close filereader in this method and methods like this
        return yaGson.fromJson(json, Account.class);
    }

    public static void addAccount(String name, String password) throws IOException
    {
        if (getAllAccounts().contains(name))
        {
            throw new IOException("This name is already taken");
        }
        if (password.isEmpty() || name.isEmpty()){
            throw new IOException("You didn't choose any username or password");
        }
        toJson(new Account(name, password));
    }

    public static void toJson(Account account) {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter("res/users/" + account.name + ".json");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Formatter formatter = new Formatter(fileWriter);
        YaGson yaGson = new YaGson();
        String json = yaGson.toJson(account);
        formatter.format(json).flush();
        formatter.close();
    }
    public static ArrayList<String> getAllAccounts(){
        File folder = new File("res/users");
        File[] listOfFiles = folder.listFiles();
        ArrayList<String> usersName = new ArrayList<>();
        for (File listOfFile : listOfFiles)
        {
            String name = listOfFile.getName();
            name = name.substring(0, name.indexOf('.'));
            usersName.add(name);
        }
        return usersName;
    }
}

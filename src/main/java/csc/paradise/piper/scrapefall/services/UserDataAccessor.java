package csc.paradise.piper.scrapefall.services;

import com.google.gson.Gson;
import csc.paradise.piper.scrapefall.models.User;
import net.dzultra.jsa.cards.enums.OrderMode;
import net.dzultra.jsa.cards.enums.SortMode;

import java.io.*;

public class UserDataAccessor {
    private static final String USERS_FILEPATH =
            System.getProperty("user.home") + File.separator +
                    ".scrapefall" + File.separator + "UserInfo.json";

    private static final UserDataAccessor INSTANCE = new UserDataAccessor();
    private static final Gson GSON = new Gson();

    private User currentUser;

    private UserDataAccessor() {
        File userDataFile = new File(USERS_FILEPATH);
        File parentDir = userDataFile.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }

        if (!userDataFile.exists() || userDataFile.length() == 0) {
            // Nothing saved yet — no user to load
            currentUser = null;
            return;
        }

        try (FileInputStream fis = new FileInputStream(userDataFile);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            currentUser = (User) ois.readObject();
        } catch (IOException e) {
            System.err.println("WARNING: UserData has not been received");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.err.println("WARNING: UserData cannot be cast to User");
            e.printStackTrace();
        }
    }

    public static UserDataAccessor getInstance() {
        return INSTANCE;
    }

    protected void saveUser() {
        File userDataFile = new File(USERS_FILEPATH);
        try {
            File parentDir = userDataFile.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }

            try (FileOutputStream fos = new FileOutputStream(userDataFile);
                 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                oos.writeObject(currentUser);
            }
        } catch (IOException e) {
            System.err.println("WARNING: UserData has not been saved");
            e.printStackTrace();
        }
    }

    public void persistUser() {
        saveUser();
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean updateUser(OrderMode lastUsedOrderMode, SortMode lastUsedSortMode) {
        currentUser = new User(lastUsedOrderMode,lastUsedSortMode);
        saveUser();
        return true;
    }
}
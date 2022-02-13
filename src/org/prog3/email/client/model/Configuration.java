package org.prog3.email.client.model;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;

public class Configuration implements Serializable {
    public static Configuration instance = new Configuration();
    public static Path path = Path.of("./email.conf");
    ArrayList<String> accounts = new ArrayList<>();
    int currentAccount = 0;

    public void addAccount(String account) {
        accounts.add(account);
    }

    public void setDefaultAccount(int index) {
        currentAccount = index;
    }

    public int getCurrentAccount() {
        return currentAccount;
    }

    public ArrayList<String> getAccounts() {
        return accounts;
    }

}

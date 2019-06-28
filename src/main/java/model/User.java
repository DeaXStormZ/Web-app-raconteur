package model;

import java.util.ArrayList;
import java.util.HashMap;

public class User {
    private String login, password;
    private ArrayList<Story> contributions = null;
    private ArrayList<Story> authored = null;
    private HashMap<Story, History> histories = null;

    public User(String login, String password, ArrayList<Story> contributions, ArrayList<Story> authored, HashMap<Story, History> histories) {
        this.login = login;
        this.password = password;
        this.contributions = contributions;
        this.authored = authored;
        this.histories = histories;
    }

    public User(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public void saveProgress() {
        throw new UnsupportedOperationException("not yet implemented");
    }

    public ArrayList<Story> getEditableStories() {
        throw new UnsupportedOperationException("not yet implemented");
    }

    public ArrayList<Story> seeAuthoredStories() {
        throw new UnsupportedOperationException("not yet implemented");
    }

    public void addStory(String storyTitle, Paragraph headParagraph, User author) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    public String getPassword() {
        return password;
    }

    public ArrayList<Story> getContributions() {
        return contributions;
    }

    public ArrayList<Story> getAuthored() {
        return authored;
    }

    public HashMap<Story, History> getHistories() {
        return histories;
    }

    @Override
    public String toString() {
        return "User{" +
                "login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", contributions=" + contributions +
                ", authored=" + authored +
                ", histories=" + histories +
                '}';
    }
}

package model;

import java.util.ArrayList;

public class History {
    private Story story;
    private int idStory;
    private ArrayList<Choice> history;
    private User user;

    public History(Story story, ArrayList<Choice> history) {
        this.story = story;
        this.history = history;
    }

    public History(int idStory, ArrayList<Choice> history) {
        this.idStory = idStory;
        this.history = history;
    }

    public int getIdStory() {
        return story.getIdStory();
    }

    public Story getStory() {
        return story;
    }

    public ArrayList<Choice> getHistory() {
        return history;
    }

    public Choice getLastChoice() {
        throw new UnsupportedOperationException("not yet implemented");
    }

    public void modifyChoice(Choice oldChoice, Choice newChoice) {
        //TODO
    }

    public void addChoice(Choice choice) {
        //TODO
    }
}

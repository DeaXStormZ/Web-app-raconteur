package model;

import java.util.ArrayList;

public class Story {
    private int idStory;
    private String storyTitle;
    private User author;
    private ArrayList<User> contributors = null;
    private boolean isPublished = false;
    private Paragraph headParagraph;

    public Story(int idStory, String storyTitle, User author, ArrayList<User> contributors, boolean isPublished, Paragraph headParagraph) {
        this.idStory = idStory;
        this.storyTitle = storyTitle;
        this.author = author;
        this.contributors = contributors;
        this.isPublished = isPublished;
        this.headParagraph = headParagraph;
    }

    @Override
    public int hashCode() {
        return this.idStory;
    }

    public int getIdStory() {
        return idStory;
    }

    public String getStoryTitle() {
        return storyTitle;
    }

    public User getAuthor() {
        return author;
    }

    public ArrayList<User> getContributors() {
        return contributors;
    }

    public boolean isPublished() {
        return isPublished;
    }

    public Paragraph getHeadParagraph() {
        return headParagraph;
    }

    public void setIdStory(int idStory) {
        this.idStory = idStory;
    }

    public void setHeadParagraph(Paragraph headParagraph) {
        this.headParagraph = headParagraph;
    }

    public void publish() {
        this.isPublished = true;
        //TODO
    }

    public void unpublish() {
        this.isPublished = false;
        //TODO
    }

    public void addContributor(User contributor) {

    }

    public void removeContributor(User contributor) {

    }
}

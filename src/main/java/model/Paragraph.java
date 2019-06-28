package model;

import java.util.List;

public class Paragraph {
    private int idParagraph;
    private boolean isAConclusion, isValidated;
    private String text;
    private String title;
    private User author;
    private List<Choice> choices;

    public Paragraph(int idParagraph, boolean isAConclusion, boolean isValidated, String text, String title, User author, List<Choice> choices) {
        this.idParagraph = idParagraph;
        this.isAConclusion = isAConclusion;
        this.isValidated = isValidated;
        this.text = text;
        this.title = title;
        this.author = author;
        this.choices = choices;
    }

    public int getIdParagraph() {
        return idParagraph;
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public boolean isAConclusion() {
        return isAConclusion;
    }

    public String getText() {
        return text;
    }

    public boolean isValidated() {
        return isValidated;
    }

    public User getAuthor() {
        return author;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    public void setAsConclusion() {
        isAConclusion = true;
    }

    public void unsetAsConclusion() {
        isAConclusion = false;
    }

    public void validate() {
        isValidated = true;
    }

    public void unvalidate() {
        isValidated = false;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public void addChoice(Choice choice) {
        //TODO
    }

    public void removeChoice(Choice choice) {
        //TODO
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void delete() {

    }
}

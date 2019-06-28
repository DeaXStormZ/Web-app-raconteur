package model;

public class Choice {
    private int idStory;
    private int idChoice, condition;
    private int idNextParagraph;
    private int idCurrentParagraph;
    private boolean isLocked;
    private Paragraph currentParagraph;
    private Paragraph nextParagraph;

    public Choice(int idStory, int idChoice, int condition, int idNextParagraph, int idCurrentParagraph, boolean isLocked) {
        this.idStory = idStory;
        this.idChoice = idChoice;
        this.condition = condition;
        this.idNextParagraph = idNextParagraph;
        this.idCurrentParagraph = idCurrentParagraph;
        this.isLocked = isLocked;
    }

    public int getIdChoice() {
        return idChoice;
    }

    public int getCondition() {
        return condition;
    }

    public int getIdNextParagraph() {
        return idNextParagraph;
    }
}

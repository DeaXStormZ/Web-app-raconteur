ALTER TABLE Story
    DROP CONSTRAINT authorStory;
ALTER TABLE PublishedStory
    DROP CONSTRAINT idStoryPublishedStory;

ALTER TABLE Paragraph
    DROP CONSTRAINT idStoryParagraph;
ALTER TABLE Paragraph
    DROP CONSTRAINT authorParagraph;
ALTER TABLE ValidatedParagraph
    DROP CONSTRAINT idStoryValidatedParagraph;
ALTER TABLE ConclusionParagraph
    DROP CONSTRAINT idStoryConclusionParagraph;

ALTER TABLE Choice
    DROP CONSTRAINT idStoryChoice;
ALTER TABLE LockedChoice
    DROP CONSTRAINT idStoryLockedChoice;

--ALTER TABLE History
--    DROP CONSTRAINT loginHistory;
--ALTER TABLE History
--    DROP CONSTRAINT idStoryHistory;

--ALTER TABLE Author
--    DROP CONSTRAINT loginAuthor;

ALTER TABLE isAContributor
    DROP CONSTRAINT idStoryIsAContributor;
ALTER TABLE isAContributor
    DROP CONSTRAINT loginIsAContributor;

ALTER TABLE HistoryElement
    DROP CONSTRAINT idChoiceHistoryElement;
ALTER TABLE HistoryElement
    DROP CONSTRAINT idHistoryHistoryElement;

DROP TABLE RegisteredUser;
--DROP TABLE Author;
DROP TABLE Story;
DROP TABLE PublishedStory;
DROP TABLE Paragraph;
DROP TABLE ValidatedParagraph;
DROP TABLE ConclusionParagraph;
DROP TABLE Choice;
DROP TABLE LockedChoice;
--DROP TABLE History;
DROP TABLE HistoryElement;
DROP TABLE isAContributor;

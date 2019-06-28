-- Création des tables
CREATE SEQUENCE id_story
    START WITH 100
    INCREMENT BY 1;

CREATE SEQUENCE id_paragraph
    START WITH 100
    INCREMENT BY 1;

CREATE SEQUENCE id_choice
    START WITH 100
    INCREMENT BY 1;

CREATE TABLE RegisteredUser
(
    login    VARCHAR(30) NOT NULL,
    password VARCHAR(30) NOT NULL,
    PRIMARY KEY (login)
);

CREATE TABLE Story
(
    idStory       number(6) DEFAULT id_story.nextval,
    storyTitle    VARCHAR(300),
    author        VARCHAR(30),
    headParagraph number(6)
);

CREATE TABLE PublishedStory
(
    idStory number(6) PRIMARY KEY
);

CREATE TABLE Paragraph
(
    idStory        number(6),
    idParagraph    number(6) DEFAULT id_paragraph.nextval,
    paragraphTitle VARCHAR(300),
    text           CLOB,
    author         VARCHAR(30),
    PRIMARY KEY (idStory, idParagraph)
);

CREATE TABLE ValidatedParagraph
(
    idStory     number(6),
    idParagraph number(6),
    PRIMARY KEY (idStory, idParagraph)
);

CREATE TABLE ConclusionParagraph
(
    idStory     number(6),
    idParagraph number(6),
    PRIMARY KEY (idStory, idParagraph)
);

CREATE TABLE Choice
(
    idStory       number(6),
    idParagraph   number(6),
    idChoice      number(6) DEFAULT id_choice.nextval,
    condition     INTEGER not null,
    nextParagraph number(6),
    PRIMARY KEY (idStory, idParagraph, idChoice)
);

CREATE TABLE LockedChoice
(
    idStory     number(6),
    idParagraph number(6),
    idChoice    number(6),
    PRIMARY KEY (idStory, idParagraph, idChoice)
);

CREATE TABLE isAContributor
(
    login   VARCHAR(30),
    idStory number(6),
    PRIMARY KEY (login, idStory)
);

CREATE TABLE HistoryElement
(
    idStory     number(6),
    idParagraph number(6),
    idChoice    number(6),
    login       VARCHAR(30),
    position    INTEGER NOT NULL,
    PRIMARY KEY (idParagraph, idStory, login, position),
    CONSTRAINT positionPos CHECK ( position > 0 )
);

-- Clés étrangères

ALTER TABLE Story
    ADD CONSTRAINT authorStory FOREIGN KEY (author) REFERENCES RegisteredUser (login) ON DELETE CASCADE;
ALTER TABLE PublishedStory
    ADD CONSTRAINT idStoryPublishedStory FOREIGN KEY (idStory) REFERENCES Story (idStory) ON DELETE CASCADE;

ALTER TABLE Paragraph
    ADD CONSTRAINT idStoryParagraph FOREIGN KEY (idStory) REFERENCES Story (idStory) ON DELETE CASCADE;
ALTER TABLE Paragraph
    ADD CONSTRAINT authorParagraph FOREIGN KEY (author) REFERENCES RegisteredUser (login) ON DELETE CASCADE;
ALTER TABLE ValidatedParagraph
    ADD CONSTRAINT idStoryValidatedParagraph FOREIGN KEY (idStory, idParagraph) REFERENCES Paragraph (idStory, idParagraph) ON DELETE CASCADE;
ALTER TABLE ConclusionParagraph
    ADD CONSTRAINT idStoryConclusionParagraph FOREIGN KEY (idStory, idParagraph) REFERENCES Paragraph (idStory, idParagraph) ON DELETE CASCADE;

ALTER TABLE Choice
    ADD CONSTRAINT idStoryChoice FOREIGN KEY (idStory, idParagraph) REFERENCES Paragraph (idStory, idParagraph) ON DELETE CASCADE;
ALTER TABLE LockedChoice
    ADD CONSTRAINT idStoryLockedChoice FOREIGN KEY (idStory, idParagraph, idChoice) REFERENCES Choice (idStory, idParagraph, idChoice) ON DELETE CASCADE;

--ALTER TABLE History
--    ADD CONSTRAINT loginHistory FOREIGN KEY (login) REFERENCES RegisteredUser (login);
--ALTER TABLE History
--    ADD CONSTRAINT idStoryHistory FOREIGN KEY (idStory) REFERENCES Story (idStory);

ALTER TABLE isAContributor
    ADD CONSTRAINT loginIsAContributor FOREIGN KEY (login) REFERENCES RegisteredUser (login);
ALTER TABLE isAContributor
    ADD CONSTRAINT idStoryIsAContributor FOREIGN KEY (idStory) REFERENCES Story (idStory);

ALTER TABLE HistoryElement
    ADD CONSTRAINT idChoiceHistoryElement FOREIGN KEY (idChoice, idParagraph, idStory) REFERENCES Choice (idChoice, idParagraph, idStory);
ALTER TABLE HistoryElement
    ADD CONSTRAINT idHistoryHistoryElement FOREIGN KEY (login) REFERENCES RegisteredUser (login);
package controller;

import dao.*;
import model.Choice;
import model.Paragraph;
import model.Story;
import model.User;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.max;


@WebServlet(name = "controller", urlPatterns = {"/controller"})
public class Controller extends HttpServlet {


    public static final String STORY = "story";
    public static final String ID_STORY = "id_story";
    public static final String PARAGRAPHS_LIST = "paragraphs_list";
    public static final String ID_PARAGRAPH = "id_paragraph";
    public static final String STORIES_LIST = "stories_list";
    public static final String EDIT_STORY = "edit_story";
    public static final String CHOICE_TITLES = "choice_titles";
    public static final String EDIT_PARAGRAPH = "edit_paragraph";
    public static final String PARAGRAPH = "paragraph";
    public static final String REMOVE_STORY = "remove_story";
    public static final String ADD_STORY = "add_story";
    public static final String STORY_TITLE = "story_title";
    public static final String PARAGRAPH_TEXT = "paragraph_text";
    public static final String REMOVE_PARAGRAPH = "remove_paragraph";
    public static final String INSERT_AFTER_PARAGRAPH = "insert_after_paragraph";
    public static final String CONDITION = "condition";
    public static final String SUBMIT_STORY = "create_story";
    public static final String SUBMIT_PARAGRAPH = "submit_paragraph";
    private static final String ACTION = "action", REASON = "reason", VIEW_ALL = "view_all",
            LOGIN = "login", PASSWORD = "password", PASSWORD_CONFIRM = "password_confirm",
            VIEW_STORY = "view_story", REGISTER = "register";
    @Resource(name = "jdbc/data")
    DataSource ds;


    static private void printRequest(HttpServletRequest request, HttpServletResponse response) {
        try (PrintWriter out = response.getWriter()) {
            request.setCharacterEncoding("UTF-8");
            Map<String, String[]> map = request.getParameterMap();

            StringBuilder sb = new StringBuilder();

            for (String s : map.keySet()) {
                sb.append(s).append(": ");
                for (String ss : map.get(s))
                    sb.append(ss).append(", ");
            }

            out.println(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter(ACTION);
        StoryDAO storyDAO = new StoryDAO(ds);

        if (action == null) {
            throw new UnsupportedOperationException("get without an action");
        }

        switch (action) {
            case VIEW_ALL:
                actionBrowseAllPublishedStories(request, response);
                break;
            case VIEW_STORY:
                HttpSession current_session = request.getSession();
                String jspFile;
                if (current_session.getAttribute(LOGIN) == null)
                    jspFile = "/WEB-INF/view_story.jsp";
                else
                    jspFile = "/WEB-INF/view_story_logged_in.jsp";
                actionGetStory(request, response, storyDAO, jspFile);


                break;
            default:
                throw new UnsupportedOperationException("get called on invalid action\"" + action + "\"");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {

        String action = request.getParameter(ACTION);
        UserDAO userDAO = new UserDAO(ds);
        StoryDAO storyDAO = new StoryDAO(ds);
        ParagraphDAO paragraphDAO = new ParagraphDAO(ds);
        HistoryDAO historyDAO = new HistoryDAO(ds);
        ChoiceDAO choiceDAO = new ChoiceDAO(ds);

        if (action == null) throw new UnsupportedOperationException("Post called for no action");

        switch (action) {
            case REGISTER:
                actionRegisterUser(request, response, userDAO);
                break;
            case LOGIN:
                actionLogin(request, response, userDAO, storyDAO);
                break;
            case REMOVE_STORY:
                actionRemoveStory(request, response, userDAO, storyDAO);
                break;
            case EDIT_STORY:
                actionEditStory(request, response, storyDAO, paragraphDAO, choiceDAO);
                break;
            case ADD_STORY:
                actionAddStory(request, response);
                break;
            case SUBMIT_STORY:
                actionSubmitStory(request, response, storyDAO, paragraphDAO);
                break;
            case EDIT_PARAGRAPH:
                actionEditParagraph(request, response, storyDAO, paragraphDAO);
                break;
            case REMOVE_PARAGRAPH:
                actionRemoveParagraph(request, response, storyDAO, paragraphDAO);
                break;
            case "submit_choice":
                actionSubmitChoice(request, response, storyDAO, paragraphDAO, choiceDAO);
                break;
            case INSERT_AFTER_PARAGRAPH:
                actionAddChoice(request, response, storyDAO, paragraphDAO);
                break;
            case SUBMIT_PARAGRAPH:
                actionSubmitParagraph(request, response, storyDAO, paragraphDAO, choiceDAO);
                break;
            case "reach_home":
                prepareUserHomepage(request, response, storyDAO);
                break;
            case "disconnect":
                request.getRequestDispatcher("index.html").forward(request, response);
            default:
                throw new UnsupportedOperationException("post called on invalid action\"" + action + "\"");
        }

    }

    private void actionAddChoice(HttpServletRequest request, HttpServletResponse response, StoryDAO storyDAO, ParagraphDAO paragraphDAO) throws ServletException, IOException {
        int idStory = Integer.parseInt(request.getParameter(ID_STORY));
        Story story = storyDAO.getStory(idStory);
        int idParagraph = Integer.parseInt(request.getParameter(ID_PARAGRAPH));
        Paragraph paragraph = paragraphDAO.getParagraph(idStory, idParagraph);
        request.setAttribute(STORY, story);
        request.setAttribute(PARAGRAPH, paragraph);
        request.getRequestDispatcher("WEB-INF/insert_after_paragraph.jsp").forward(request, response);
    }

    private void actionAddStory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("WEB-INF/create_story.jsp").forward(request, response);
    }

    private void actionEditParagraph(HttpServletRequest request, HttpServletResponse response, StoryDAO storyDAO, ParagraphDAO paragraphDAO) throws ServletException, IOException {
        int idStory = Integer.parseInt(request.getParameter(ID_STORY));
        Story story = storyDAO.getStory(idStory);
        int idParagraph = Integer.parseInt(request.getParameter(ID_PARAGRAPH));
        Paragraph paragraph = paragraphDAO.getParagraph(idStory, idParagraph);
        request.setAttribute(STORY, story);
        request.setAttribute(PARAGRAPH, paragraph);
        request.getRequestDispatcher("WEB-INF/edit_paragraph.jsp").forward(request, response);
    }

    private void actionSubmitChoice(HttpServletRequest request, HttpServletResponse response, StoryDAO storyDAO, ParagraphDAO paragraphDAO, ChoiceDAO choiceDAO) throws ServletException, IOException {
        int idStory = Integer.parseInt(request.getParameter(ID_STORY));
        Story story = storyDAO.getStory(idStory);
        int idParagraph = Integer.parseInt(request.getParameter(ID_PARAGRAPH));
        request.setAttribute(STORY, story);
        String requestParameter = request.getParameter(CONDITION);
        int condition = requestParameter == null ? 0 : Integer.parseInt(requestParameter);
        HttpSession session = request.getSession();
        String author = session.getAttribute(LOGIN).toString();
        //goto edit_paragraph
        String text = request.getParameter(PARAGRAPH_TEXT);
//        String text = request.getParameter(PARAGRAPH_TEXT);
//        String headParagraphTitle = text.substring(0, max(text.indexOf(". "), text.length() - 1));
        String headParagraphTitle = text.substring(0, max(text.indexOf(". "), text.length()));
        int newParagraphId = paragraphDAO.addParagraph(idStory, headParagraphTitle, text, author);
        choiceDAO.addChoice(idStory, idParagraph, condition, newParagraphId);

        Paragraph newParagraph = paragraphDAO.getParagraph(idStory, newParagraphId);
        request.setAttribute(PARAGRAPH, newParagraph);

        List<Paragraph> paragraphs = paragraphDAO.getParagraphs(idStory);
        request.setAttribute(PARAGRAPHS_LIST, paragraphs);

        request.getRequestDispatcher("WEB-INF/edit_story.jsp").forward(request, response);
    }

    private void actionRemoveParagraph(HttpServletRequest request, HttpServletResponse response, StoryDAO storyDAO, ParagraphDAO paragraphDAO) throws ServletException, IOException {
        int idStory = Integer.parseInt(request.getParameter(ID_STORY));
        int idParagraph = Integer.parseInt(request.getParameter(ID_PARAGRAPH));
        Story story = storyDAO.getStory(idStory);
        request.setAttribute(STORY, story);
        HttpSession session = request.getSession();
        String login = session.getAttribute(LOGIN).toString();
        request.setAttribute(LOGIN, login);
        Paragraph paragraph = paragraphDAO.getParagraph(idStory, idParagraph);

        if (!login.equals(paragraph.getAuthor().getLogin())) {
            throw new UnsupportedOperationException("A user should not be able to remove a paragraph from another user");
        }

        paragraphDAO.removeParagraph(idStory, idParagraph);
        List<Paragraph> paragraphs = paragraphDAO.getParagraphs(idStory);
        request.setAttribute(PARAGRAPHS_LIST, paragraphs);
        request.getRequestDispatcher("WEB-INF/edit_story.jsp").forward(request, response);
    }

    private void actionSubmitStory(HttpServletRequest request, HttpServletResponse response, StoryDAO storyDAO, ParagraphDAO paragraphDAO) throws ServletException, IOException {
        String title = request.getParameter(STORY_TITLE);
//        String title = "title_story";
        HttpSession session = request.getSession();
        String author = session.getAttribute(LOGIN).toString();
        request.setAttribute(LOGIN, author);


//        String headParagraphTitle = "title_paragraph";
        String text = request.getParameter(PARAGRAPH_TEXT);
        String headParagraphTitle = text.substring(0, max(text.indexOf(". "), text.length()));
//        String headParagraphTitle = "headparagrpahtitle";
        int idStory = storyDAO.addStory(title, author, headParagraphTitle, text);
        Story story = storyDAO.getStory(idStory);

        storyDAO.publish(story);//TODO put a button publish
        request.setAttribute(STORY, story);

        prepareUserHomepage(request, response, storyDAO);
    }

    private void prepareUserHomepage(HttpServletRequest request, HttpServletResponse response, StoryDAO storyDAO) throws ServletException, IOException {
        HttpSession current_session = request.getSession();
        String login = current_session.getAttribute(LOGIN).toString();
        request.setAttribute(LOGIN, login);
        request.setAttribute("readable_stories", storyDAO.getPublishedStories(true));

        //liste des histoire dont on est l auteur
        request.setAttribute("authored_stories", storyDAO.getAuthored(login));

        //liste des histoires editables
        request.setAttribute("editable_stories", storyDAO.getAllStories());
        request.getRequestDispatcher("WEB-INF/user_homepage.jsp").forward(request, response);
    }

    /**
     * Visitor view of all stories
     */
    private void actionBrowseAllPublishedStories(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        StoryDAO storyDAO = new StoryDAO(ds);
        ArrayList<Story> stories = storyDAO.getPublishedStories(true);
        request.setAttribute(STORIES_LIST, stories);
        request.getRequestDispatcher("WEB-INF/browse_stories.jsp").forward(request, response);
    }

    /**
     * Récupère les informations sur un user donné par son identifiant.
     * Ajoute cet ouvrage comme attribut à la requête puis appelle la vue demandée.
     * La requête doit comprendre les paramètres :
     * -- login, le login du user à récupérer
     * -- view, le nom de la vue à afficher ("modifier" ou "supprimer")
     * Sinon, on appelle invalidParameters.
     */
    private void actionLogin(HttpServletRequest request, HttpServletResponse response, UserDAO userDAO, StoryDAO storyDAO)
            throws ServletException, IOException {

        String login = request.getParameter(LOGIN),
                password = request.getParameter(PASSWORD);

        HttpSession session = request.getSession(true);

        if (!userDAO.isLoginValid(login, password)) {
            request.setAttribute(LOGIN, login);
            request.getRequestDispatcher("WEB-INF/cant_login.jsp").forward(request, response);
        }

        session.setAttribute(LOGIN, login);
        request.setAttribute(LOGIN, login);

        prepareUserHomepage(request, response, storyDAO);
    }

    /**
     * add a User
     */
    private void actionRegisterUser(HttpServletRequest request, HttpServletResponse response, UserDAO userDAO)
            throws IOException, ServletException {
        String login = request.getParameter(LOGIN),
                password = request.getParameter(PASSWORD),
                confirmation = request.getParameter(PASSWORD_CONFIRM);

        if (!password.equals(confirmation)) {
            request.setAttribute(LOGIN, login);
            request.setAttribute(REASON, "les deux mots de passe donnés sont différents");
            request.getRequestDispatcher("WEB-INF/cant_create.jsp").forward(request, response);
        } else if (userDAO.isLoginExisting(login)) {
            request.setAttribute(LOGIN, login);
            request.setAttribute(REASON, "ce pseudonyme est déjà attribué ou n'est pas conforme");
            request.getRequestDispatcher("WEB-INF/cant_create.jsp").forward(request, response);
        }

        userDAO.addUser(new User(login, password, null, null, null));
        response.sendRedirect("login.html");
    }


    private void actionGetStory(HttpServletRequest request, HttpServletResponse response, StoryDAO storyDAO, String jspFile)
            throws ServletException, IOException {
        int idStory = Integer.parseInt(request.getParameter(ID_STORY));
        Story story = storyDAO.getStory(idStory);
        ParagraphDAO paragraphDAO = new ParagraphDAO(ds);
        ChoiceDAO choiceDAO = new ChoiceDAO(ds);
        request.setAttribute(STORY, story);

        //read the paragraphs from the ID_PARAGRAPH to the paragraph that contains more than one choice
        String requestParameter = request.getParameter(ID_PARAGRAPH);
        int parameter = requestParameter == null ? story.getHeadParagraph().getIdParagraph() : Integer.parseInt(requestParameter);
        Paragraph headParagraph = paragraphDAO.getParagraph(idStory, parameter);

        List<Paragraph> paragraphs = paragraphDAO.getParagraphsWithOneChoice(idStory, headParagraph);
        request.setAttribute(PARAGRAPHS_LIST, paragraphs);

        List<Choice> choices = choiceDAO.getAvailableChoices(idStory, paragraphs.get(paragraphs.size() - 1).getIdParagraph(), null);//FIXME put History instead of null
        Map<Choice, String> choice_titles = getChoiceStringMap(idStory, paragraphDAO, choices);
        request.setAttribute(CHOICE_TITLES, choice_titles);

        getServletContext().getRequestDispatcher(jspFile).forward(request, response);
    }

    private Map<Choice, String> getChoiceStringMap(int idStory, ParagraphDAO paragraphDAO, List<Choice> choices) {
        Map<Choice, String> choice_titles = new HashMap<>();
        for (Choice choice : choices) {
            choice_titles.put(choice, paragraphDAO.getParagraph(idStory, choice.getIdNextParagraph()).getTitle());
        }
        return choice_titles;
    }

    private void actionEditStory(HttpServletRequest request, HttpServletResponse response, StoryDAO storyDAO, ParagraphDAO paragraphDAO, ChoiceDAO choiceDAO) throws ServletException, IOException {

        int idStory = Integer.parseInt(request.getParameter(ID_STORY));
        Story story = storyDAO.getStory(idStory);
        request.setAttribute(STORY, story);

//        //liste des choices unlocked
//        List<Choice> unlockedChoices = choiceDAO.getUnlockedChoices(idStory);
//        Map<Choice, String> choice_titles = getChoiceStringMap(idStory, paragraphDAO, unlockedChoices);
//        request.setAttribute(CHOICE_TITLES, choice_titles);

        //liste des paragraphes modifiables
        List<Paragraph> paragraphs = paragraphDAO.getParagraphs(idStory);
        request.setAttribute(PARAGRAPHS_LIST, paragraphs);

        //TODO add the contributor in the table isacontributor
        getServletContext().getRequestDispatcher("/WEB-INF/edit_story.jsp").forward(request, response);
    }

    private void actionSubmitParagraph(HttpServletRequest request, HttpServletResponse response, StoryDAO storyDAO, ParagraphDAO paragraphDAO, ChoiceDAO choiceDAO) throws ServletException, IOException {
        int idStory = Integer.parseInt(request.getParameter(ID_STORY));
        Story story = storyDAO.getStory(idStory);
        int idParagraph = Integer.parseInt(request.getParameter(ID_PARAGRAPH));
        Paragraph paragraph = paragraphDAO.getParagraph(idStory, idParagraph);
        request.setAttribute(STORY, story);
        request.setAttribute(PARAGRAPH, paragraph);

        String text = request.getParameter(PARAGRAPH_TEXT);
        paragraphDAO.setText(idStory, idParagraph, text);

        List<Choice> choices = choiceDAO.getChoices(idStory, idParagraph);
        Map<Choice, String> choice_titles = getChoiceStringMap(idStory, paragraphDAO, choices);
        request.setAttribute(CHOICE_TITLES, choice_titles);

        List<Paragraph> paragraphs = paragraphDAO.getParagraphs(idStory);
        request.setAttribute(PARAGRAPHS_LIST, paragraphs);

        getServletContext().getRequestDispatcher("/WEB-INF/edit_story.jsp").forward(request, response);
    }

    private void actionRemoveStory(HttpServletRequest request, HttpServletResponse response, UserDAO userDAO, StoryDAO storyDAO) throws ServletException, IOException {
        int idStory = Integer.parseInt(request.getParameter(ID_STORY));

        HttpSession session = request.getSession();
        String login = session.getAttribute(LOGIN).toString();
        request.setAttribute(LOGIN, login);

        Story story = storyDAO.getStory(idStory);
        request.setAttribute(STORY, story);

        if (!login.equals(story.getAuthor().getLogin())) {
            throw new UnsupportedOperationException("A user should not be able to remove a story from another user");
        }

        storyDAO.removeStory(idStory);
        prepareUserHomepage(request, response, storyDAO);
    }
}
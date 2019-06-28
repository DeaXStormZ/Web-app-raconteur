<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="login" scope="request" type="java.lang.String"/>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <link href="<%=request.getContextPath()%>/styles/styles.css" rel="stylesheet">
    <title>Accueil : ${login}</title>
</head>
<body>
<form accept-charset="UTF-8" action="controller" method="post" class="home_button">
    <button name="action" value="disconnect" class="mid_sized_button">Déconnexion</button>
</form>
<div id="page_block">

    <header>
        <h1>Bonjour, <b>${login}</b></h1>
    </header>

    <h2>Vos histoires</h2>

    <%--    stories created, taken part in--%>
    <jsp:useBean id="authored_stories" scope="request" type="java.util.List<model.Story>"/>
    <c:forEach items="${authored_stories}" var="story">
        <div class="framed">
            <h1>${story.storyTitle}</h1>
            <h3>${story.author.login}</h3>

            <form accept-charset="UTF-8" action="controller" method="post">

                <input type="hidden" name="login" value="${login}">
                <input type="hidden" name="id_story" value="${story.idStory}">


                <button name="action" value="edit_story" class="sober_button">Editer</button>
                <button name="action" value="remove_story" class="sober_button">Supprimer</button>
            </form>
        </div>
        <br/>
    </c:forEach>

    <div class="center">
        <form accept-charset="UTF-8" action="controller" method="post">
            <input type="hidden" name="login" value="${login}">
            <button name="action" value="add_story" class="mid_sized_button">Ecrire une histoire</button>
        </form>
    </div>

    <h2>Toutes les histoires modifiables</h2>

    <jsp:useBean id="editable_stories" scope="request" type="java.util.List<model.Story>"/>
    <c:forEach items="${editable_stories}" var="story">
        <div class="framed">
            <h1>${story.storyTitle}</h1>
            <h3>${story.author.login}</h3>

            <form accept-charset="UTF-8" action="controller" method="post">

                <input type="hidden" name="login" value="${login}">
                <input type="hidden" name="id_story" value="${story.idStory}">


                <button name="action" value="edit_story" class="sober_button">Editer</button>
                <button name="action" value="remove_story" class="sober_button">Supprimer</button>
            </form>
        </div>
        <br/>
    </c:forEach>

    <h2>Toutes les histoires publiées</h2>

    <jsp:useBean id="readable_stories" scope="request" type="java.util.List<model.Story>"/>
    <c:forEach items="${readable_stories}" var="story">
        <div class="framed">
            <h1>${story.storyTitle}</h1>
            <h3>${story.author.login}</h3>

            <form accept-charset="UTF-8" action="controller" method="get">

                <input type="hidden" name="action" value="view_story">
                <input type="hidden" name="id_story" value="${story.idStory}">

                <input name="connect_button" type="submit" value="Lire cette histoire" class="sober_button"/>
            </form>
        </div>
        <br/>
    </c:forEach>

</div>
</body>
</html>

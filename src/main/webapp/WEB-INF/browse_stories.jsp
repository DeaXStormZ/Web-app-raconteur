<%@ page contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="stories_list" scope="request" type="java.util.List<model.Story>"/>
<html>
<head>
    <link href="<%=request.getContextPath()%>/styles/styles.css" rel="stylesheet">
    <title>Raconteur</title>
</head>
<body>
<form accept-charset="UTF-8" action="controller" method="post" class="home_button">
    <button name="action" value="disconnect" class="mid_sized_button">Accueil</button>
</form>
<div id="page_block">

    <header>
        <h1>Toutes les histoires</h1>
        <h3>Vous n'êtes pas connecté(e).</h3>
    </header>

    <% if (stories_list.isEmpty()) { %>
    <h4>Aucune histoire à lire.</h4>
    <% } else { %>
    <c:forEach items="${stories_list}" var="story">
        <div class="framed">

            <h1>${story.storyTitle}</h1>
            <h3>${story.author.login}</h3>

            <form accept-charset="UTF-8" action="controller" method="get">
                <input type="hidden" name="id_story" value="${story.idStory}">
                <button name="action" value="view_story" class="sober_button">Lire cette histoire</button>
            </form>

        </div>
        <br/>
    </c:forEach>
    <% }%>

</div>
</body>
</html>

<jsp:useBean id="story" scope="request" type="model.Story"/>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <link href="<%=request.getContextPath()%>/styles/styles.css" rel="stylesheet">
    <title>${story.storyTitle}</title>
</head>
<form accept-charset="UTF-8" action="controller" method="post" class="home_button">
    <button name="action" value="reach_home" class="mid_sized_button">Profil</button>
</form>
<body>

<div id="page_block">
    <header>
        <h1 id="title">${story.storyTitle}</h1>

        <h2 id="author"><b>${story.author.login}</b></h2>
    </header>

    <section class="story_body">
        <jsp:useBean id="paragraphs_list" scope="request" type="java.util.List<model.Paragraph>"/>
        <c:forEach items="${paragraphs_list}" var="paragraph">
            <p>
                    ${paragraph.text}
            </p>
        </c:forEach>
    </section>

    <jsp:useBean id="choice_titles" scope="request" type="java.util.Map<model.Choice, java.lang.String>"/>

    <% if (!choice_titles.isEmpty()) {%>
    <h6>✶ ✶ ✶</h6>
    <h2>Choix possibles</h2>
    <section class="story_body">
        <c:forEach items="${choice_titles}" var="choice">
            <div class="framed">
                    ${choice.value}
                <form accept-charset="UTF-8" action="controller" method="get">
                    <input type="hidden" name="id_story" value="${story.idStory}">
                    <input type="hidden" name="id_paragraph" value="${choice.key.idNextParagraph}">
                    <button name="action" value="view_story" class="sober_button">Faire ce choix</button>
                </form>
            </div>
            <br/>
        </c:forEach>
    </section>
    <%} %>
</div>


</body>
</html>
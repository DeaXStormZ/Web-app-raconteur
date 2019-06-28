<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="story" scope="request" type="model.Story"/>
<html>
<head>
    <link href="<%=request.getContextPath()%>/styles/styles.css" rel="stylesheet">
    <title>Edition de ${story.storyTitle}</title>
</head>
<body>
<form accept-charset="UTF-8" action="controller" method="post" class="home_button">
    <button name="action" value="reach_home" class="mid_sized_button">Profil</button>
</form>
<div id="page_block">

    <h1>Editeur "${story.storyTitle}"</h1>

    <section class="story_body">
        <jsp:useBean id="paragraphs_list" scope="request" type="java.util.List<model.Paragraph>"/>
        <c:forEach items="${paragraphs_list}" var="paragraph">
            <div class="framed">
                <p>${paragraph.text}</p>

                <form accept-charset="UTF-8" action="controller" method="post">
                    <input type="hidden" name="id_story" value="${story.idStory}">
                    <input type="hidden" name="id_paragraph" value="${paragraph.idParagraph}">
                    <button name="action" value="edit_paragraph" class="sober_button">Editer §</button>
                    <button name="action" value="insert_after_paragraph" class="sober_button">Insérer § ensuite</button>
                    <button name="action" value="remove_paragraph" class="sober_button">Supprimer §</button>
                </form>
            </div>
            <br>
        </c:forEach>
    </section>
</div>
</body>
</html>

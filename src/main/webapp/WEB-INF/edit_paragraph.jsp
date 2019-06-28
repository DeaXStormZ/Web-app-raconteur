<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="story" scope="request" type="model.Story"/>
<jsp:useBean id="paragraph" scope="request" type="model.Paragraph"/>
<html>
<head>
    <link href="<%=request.getContextPath()%>/styles/styles.css" rel="stylesheet">
    <title>Edition de paragraphe</title>
</head>
<body>
<form accept-charset="UTF-8" action="controller" method="post" class="home_button">
    <button name="action" value="reach_home" class="mid_sized_button">Profil</button>
</form>
<div id="page_block">

    <h1>Editeur de paragraphe</h1>

    <div class="center all_width">
        <form accept-charset="UTF-8" action="controller" method="post" class="all_width">
            <input type="hidden" name="id_story" value="${story.idStory}">
            <input type="hidden" name="id_paragraph" value="${paragraph.idParagraph}">

            <label><textarea name="paragraph_text" class="paragraph_edit_field">${paragraph.text}</textarea></label>
            <br>
            <button name="action" value="submit_paragraph" class="mid_sized_button shift_down">Valider</button>
        </form>
    </div>

</div>
</body>
</html>

<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <link href="<%=request.getContextPath()%>/styles/styles.css" rel="stylesheet">
    <title>Création d'une histoire</title>
</head>
<body>
<form accept-charset="UTF-8" action="controller" method="post" class="home_button">
    <button name="action" value="reach_home" class="mid_sized_button">Profil</button>
</form>
<div id="page_block">

    <h1>Création d'une histoire</h1>

    <div class="center all_width">
        <form accept-charset="UTF-8" action="controller" method="post" class="all_width">


            <label><input name="story_title" type="text" class="field" placeholder="Titre"></label>
            <br>

            <label><textarea name="paragraph_text"
                             class="paragraph_edit_field"
                             placeholder="Texte"></textarea></label>
            <br>
            <button name="action" value="create_story" class="mid_sized_button shift_down">Valider</button>
        </form>
    </div>

</div>
</body>
</html>

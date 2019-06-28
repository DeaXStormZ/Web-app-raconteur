<jsp:useBean id="reason" scope="request" type="java.lang.String"/>
<jsp:useBean id="login" scope="request" type="java.lang.String"/>
<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <link href="<%=request.getContextPath()%>/styles/styles.css" rel="stylesheet">
    <meta charset="UTF-8">
    <title>Création impossible</title>
</head>
<body>

<div class="failure">
    Impossible de créer l'utilisateur "<b>${login}</b>", car ${reason}.
    <br/>
    <br/>
    <a href="<%=request.getContextPath()%>/register.html">Inscription</a><br/>
    <a href="<%=request.getContextPath()%>/index.html">Accueil</a>
</div>

</body>
</html>
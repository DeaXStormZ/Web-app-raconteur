<jsp:useBean id="login" scope="request" type="java.lang.String"/>
<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <link href="<%=request.getContextPath()%>/styles/styles.css" rel="stylesheet">
    <meta charset="UTF-8">
    <title>Connexion impossible</title>
</head>
<body>

<div class="failure">
    Impossible de se connecter; utilisateur "<b>${login}</b>" inexistant ou mot de passe
    invalide.
    <br/>
    <br/>
    <a href="<%=request.getContextPath()%>/login.html">Connexion</a><br/>
    <a href="<%=request.getContextPath()%>/index.html">Accueil</a>
</div>

</body>
</html>
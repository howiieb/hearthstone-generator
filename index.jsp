<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Hearthstone Card Generator</title>
    <link rel="stylesheet" type="text/css" href="static/style/hs_generator.css">
</head>
<body>
<%@page import="hs_generator.CardBuilder" %>
<%@page import="hs_generator.Card" %>
<%@page import="java.util.Random" %>
<% CardBuilder builder = new CardBuilder();
    Random rng = new Random();
    Card card = rng.nextBoolean()? builder.makeBattlecryCard() : builder.makeDeathrattleCard();
%>
<div id="textArea"><span id="sideText">This is an algorithm that generates cards for Hearthstone.
  It understands the rules of the game, and randomly generates values based on conditionals it has been given.
  The base algorithm is by no means perfect, but it can produce some cool stuff sometimes! <br>
    You can find the source code for the algorithm used, and give feedback <a
            href="https://github.com/howiieb/hearthstone-generator">here.</a> <br> <br>
    If there's interest, I can expand the functionality of this page - think being able to save your favourite cards!
    Let me know what you think and see more of my stuff at <a
            href="http://www.howiieb.com">www.howiieb.com <br></a></span>
    <a href="${pageContext.request.contextPath}/"><img src="static/button.png" alt="Reload button"
                                                       style="margin-top: 10px; width:220px;"></a>

    <br> <br> (All art property of Blizzard Entertainment)
</div>
<div id="cardArea">
    <div id="card"><img src="static/template.png" alt="card template">
        <span class="resourceValue" id="mana"><%=card.getMana()%></span>
        <span class="resourceValue" id="health"><%=card.getHealth()%></span>
        <span class="resourceValue" id="attack"><%=card.getAttack()%></span>
        <div id="descriptionBox"><p class="descriptionText"><%=card.getText()%>
        </p></div>
        <div id="minionType"><p class="descriptionText"><%=card.getTypeStr()%>
        </p></div>
        <script src="static/boldKeywords.js"></script>
        <script src="static/fineTune.js"></script>
    </div>
</div>
</body>
</html>

function replace() {

    let text = document.getElementsByClassName("descriptionText")[0];

    const words = ["Battlecry:", "Deathrattle:", "Taunt.", "Poisonous.", "Divine Shield."];

    for (let i = 0; i < words.length; i++) {
        text.innerHTML = text.innerHTML.replace(words[i], "<span class='keyword'>" + words[i] + "</span>");
    }
}

replace();
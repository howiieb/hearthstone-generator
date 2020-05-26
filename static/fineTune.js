function fitValues() {
    for (const value of document.getElementsByClassName("resourceValue")) {
        if (value.innerHTML.length > 1) {
            if (value.id === "mana") {
                value.style.left = "15px";
            } else if (value.id === "health") {
                value.style.left = "215px";
            } else if (value.id === "attack") {
                value.style.left = "24px";
            }
        }
    }
}

function fitDescription() {
    let description = document.getElementsByClassName("descriptionText")[0];

    if (description.innerText.length > 72) {
        description.style.fontSize = "14px";
    }

    let minionType = document.getElementById("minionType");
    if (minionType.innerText === "Elemental") {
        minionType.style.left = "106px"
    }
}

fitValues();
fitDescription();
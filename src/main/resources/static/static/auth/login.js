const profileCards = document.querySelectorAll(".profile-card");

let selectProfile = "frentista";

profileCards.forEach(card => {
    card.addEventListener("click", () => {

        profileCards.forEach(item => {
            item.classList.remove("selected");
        });

        card.classList.add("selected");

        selectProfile = card.dataset.profile;

        console.log("Perfil selecionado:", selectProfile);
    });
});

const loginForm = document.getElementById("loginForm");

loginForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const email = document.getElementById("email").value;
    const senha = document.getElementById("senha").value;

    console.log("Email:", email);
    console.log("Senha:", senha);
    console.log("Perfil:", selectProfile);


    /*
        Aqui posteriormente vamos fazer:

        fetch("http://localhost:8080/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                email: email,
                senha: senha,
                perfil: selectedProfile
            })
        });

    */

        alert(
            "Login enviado!\n\n" +
            "Perfil: " + selectProfile +
            "\nEmail: " + email
        );
});
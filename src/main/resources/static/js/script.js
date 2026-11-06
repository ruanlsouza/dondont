document.addEventListener("DOMContentLoaded", () => {
    const cadastroForm = document.querySelector("#formCadastro");

    if (cadastroForm) {
        cadastroForm.addEventListener("submit", (event) => {
            const senha = document.querySelector("#senha");
            const confirmarSenha = document.querySelector("#confirmarSenha");

            if (senha.value !== confirmarSenha.value) {
                event.preventDefault();
                confirmarSenha.setCustomValidity("As senhas não coincidem.");
                confirmarSenha.reportValidity();
            } else {
                confirmarSenha.setCustomValidity("");
            }
        });

        document.querySelector("#confirmarSenha").addEventListener("input", (event) => {
            event.target.setCustomValidity("");
        });
    }

    const dataConsulta = document.querySelector("#data");
    if (dataConsulta) {
        const hoje = new Date();
        const ano = hoje.getFullYear();
        const mes = String(hoje.getMonth() + 1).padStart(2, "0");
        const dia = String(hoje.getDate()).padStart(2, "0");
        dataConsulta.min = ano + "-" + mes + "-" + dia;
    }

    document.querySelectorAll("[data-confirm-logout]").forEach((form) => {
        form.addEventListener("submit", (event) => {
            if (!confirm("Deseja realmente sair da sua conta?")) {
                event.preventDefault();
            }
        });
    });
});
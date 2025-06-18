document.addEventListener("DOMContentLoaded", async () => {
    const container = document.getElementById("medicamentosDestaque");
    const token = localStorage.getItem("jwtToken");

    try {
        const response = await fetch("/api/medicamentos", {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                ...(token && { "Authorization": `Bearer ${token}` })
            }
        });

        if (!response.ok) throw new Error("Erro ao buscar medicamentos.");

        const medicamentos = await response.json();
        const top4 = medicamentos.slice(0, 4);

        top4.forEach(medicamento => {
            const card = document.createElement("div");
            card.className = "col-md-6 col-lg-3 mb-4";

            card.innerHTML = `
                <div class="card product-card h-100">
                    <img src="img/medicamento${medicamento.id}.png" class="card-img-top" alt="${medicamento.nome}">
                    <div class="card-body d-flex flex-column">
                        <h5 class="card-title">${medicamento.nome}</h5>
                        <p class="card-text small text-muted">${medicamento.descricao || "Sem descrição."}</p>
                        <p class="card-text fs-5 fw-bold mt-auto">
                          R$ ${typeof medicamento.precoVenda === 'number' ? medicamento.precoVenda.toFixed(2) : "Preço indisponível"}
                        </p>
                        <a href="detalhes.html?id=${medicamento.id}" class="btn btn-outline-primary mt-2">Ver Detalhes</a>
                    </div>
                </div>
            `;

            container.appendChild(card);
        });

    } catch (error) {
        console.error("Erro ao carregar medicamentos:", error);
        container.innerHTML = `<div class="alert alert-danger">Não foi possível carregar os medicamentos.</div>`;
    }
});

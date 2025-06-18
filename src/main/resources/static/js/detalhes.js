function getIdFromURL() {
    const params = new URLSearchParams(window.location.search);
    return params.get('id');
}
async function carregarDetalhesMedicamento() {
    const id = getIdFromURL();
    const token = localStorage.getItem('jwtToken');
    const loading = document.getElementById('loadingDetalhes');
    const erro = document.getElementById('erroDetalhes');
    const container = document.getElementById('detalhesContainer');

    if (!id) {
        erro.classList.remove('d-none');
        erro.textContent = 'ID do medicamento não fornecido.';
        return;
    }

    loading.style.display = 'block';

    try {
        const res = await fetch(`/api/medicamentos/${id}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (!res.ok) throw new Error('Erro ao buscar medicamento');

        const med = await res.json();

        document.getElementById('nomeMedicamento').textContent = med.nome;
        document.getElementById('principioAtivo').textContent = med.principioAtivo.nome;
        document.getElementById('industria').textContent = med.industria.nome;
        document.getElementById('validade').textContent = med.validade;
        document.getElementById('precoVenda').textContent = med.precoVenda.toFixed(2);

        if (med.promocao && med.precoPromocional) {
            document.getElementById('precoPromocionalGroup').style.display = 'block';
            document.getElementById('precoPromocional').textContent = med.precoPromocional.toFixed(2);
        } else {
            document.getElementById('precoPromocionalGroup').style.display = 'none';
        }

        // Escolhe imagem random
        const imagens = [1, 24, 25, 26];
        const numeroAleatorio = imagens[Math.floor(Math.random() * imagens.length)];
        document.getElementById('imagemMedicamento').src = `img/medicamento${numeroAleatorio}.png`;

        container.style.display = 'block';

    } catch (err) {
        console.error(err);
        erro.classList.remove('d-none');
    } finally {
        loading.style.display = 'none';
    }
}
carregarDetalhesMedicamento();

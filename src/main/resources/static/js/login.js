// Em src/main/resources/static/js/login.js

document.addEventListener('DOMContentLoaded', function () {
    const loginForm = document.getElementById('loginForm'); // Certifique-se que seu formulário de login tem id="loginForm"

    if (loginForm) {
        loginForm.addEventListener('submit', async function (event) {
            event.preventDefault(); // Previne o comportamento padrão de submissão do formulário

            const usernameInput = document.getElementById('username'); // Seu input de username
            const passwordInput = document.getElementById('password'); // Seu input de senha

            const username = usernameInput.value;
            const password = passwordInput.value;

            // Limpa mensagens de erro anteriores (se houver)
            const errorDiv = document.getElementById('loginError'); // Adicione um <div id="loginError"> no seu HTML para mostrar erros
            if (errorDiv) {
                errorDiv.textContent = '';
                errorDiv.style.display = 'none';
            }


            if (!username || !password) {
                if (errorDiv) {
                    errorDiv.textContent = 'Por favor, preencha o nome de usuário e a senha.';
                    errorDiv.style.display = 'block';
                } else {
                    alert('Por favor, preencha o nome de usuário e a senha.');
                }
                return;
            }

            try {
                const response = await fetch('http://localhost:8080/api/auth/login', { // Ou apenas '/api/auth/login'
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ username: username, password: password })
                });

                if (response.ok) { // Status 200-299
                    const data = await response.json(); // Espera { "token": "...", "tipo": "Bearer" }

                    if (data.token) {
                        localStorage.setItem('jwtToken', data.token); // SALVA O TOKEN!
                        console.log('Login bem-sucedido, token salvo:', data.token);
                        window.location.href = 'dashboard.html'; // Redireciona para o dashboard ou página principal
                    } else {
                        throw new Error('Token não recebido do servidor.');
                    }
                } else {
                    // Tenta pegar a mensagem de erro do corpo da resposta
                    let errorMessage = `Erro de login: ${response.status}`;
                    try {
                        const errorData = await response.json(); // Se o backend envia JSON no erro
                        errorMessage = errorData.message || (typeof errorData === 'string' ? errorData : `Erro ${response.status}`);
                    } catch (e) {
                        // Se o corpo do erro não for JSON, use o statusText
                        errorMessage = `Erro de login: ${response.status} - ${response.statusText}`;
                    }

                    if (errorDiv) {
                        errorDiv.textContent = errorMessage;
                        errorDiv.style.display = 'block';
                    } else {
                        alert(errorMessage);
                    }
                    console.error('Falha no login:', errorMessage);
                }

            } catch (error) {
                console.error('Erro ao tentar fazer login:', error);
                if (errorDiv) {
                    errorDiv.textContent = 'Erro ao tentar fazer login. Verifique o console para detalhes.';
                    errorDiv.style.display = 'block';
                } else {
                    alert('Erro ao tentar fazer login. Verifique o console para detalhes.');
                }
            }
        });
    } else {
        console.error('Elemento #loginForm não encontrado na página.');
    }
});
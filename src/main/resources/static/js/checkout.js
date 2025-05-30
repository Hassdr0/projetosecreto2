document.addEventListener('DOMContentLoaded', function() {
    console.log('SCRIPT: checkout.js - DOM carregado.');

    const paymentMethodRadios = document.querySelectorAll('input[name="paymentMethod"]');

    // Array com as seções de formulário de pagamento e seus IDs correspondentes
    const paymentFormSections = [
        { id: 'creditCardForm', radioValue: 'creditCard' },
        { id: 'paypalForm', radioValue: 'paypal' },
        { id: 'boletoForm', radioValue: 'boleto' },
        { id: 'pixForm', radioValue: 'pix' }
    ];

    const mainPaymentButton = document.getElementById('btnPagarAgora');
    const cardPaymentFormElement = document.getElementById('cardPaymentForm'); // O formulário <form> do cartão
    const paymentErrorDiv = document.getElementById('paymentError');

    function togglePaymentForms() {
        const selectedMethodRadio = document.querySelector('input[name="paymentMethod"]:checked');

        let activeFormId = null;
        if (selectedMethodRadio) {
            activeFormId = selectedMethodRadio.value;
            console.log('Método de pagamento selecionado:', activeFormId);
        } else {
            console.log('Nenhum método de pagamento selecionado.');
        }

        paymentFormSections.forEach(section => {
            const sectionElement = document.getElementById(section.id);
            if (sectionElement) {
                if (section.radioValue === activeFormId) {
                    sectionElement.style.display = 'block';
                } else {
                    sectionElement.style.display = 'none';
                }
            } else {
                console.warn(`Elemento com ID '${section.id}' não encontrado.`);
            }
        });

        // Ajusta o texto do botão principal e o atributo 'form'
        if (mainPaymentButton) {
            if (activeFormId === 'creditCard') {
                mainPaymentButton.textContent = 'Pagar com Cartão';
                if (cardPaymentFormElement) {
                    mainPaymentButton.setAttribute('form', cardPaymentFormElement.id);
                }
            } else if (activeFormId === 'paypal') {
                mainPaymentButton.textContent = 'Pagar com PayPal';
                mainPaymentButton.removeAttribute('form');
            } else if (activeFormId === 'boleto') {
                mainPaymentButton.textContent = 'Gerar Boleto';
                mainPaymentButton.removeAttribute('form');
            } else if (activeFormId === 'pix') {
                mainPaymentButton.textContent = 'Gerar QR Code Pix';
                mainPaymentButton.removeAttribute('form');
            } else {
                mainPaymentButton.textContent = 'Finalizar Pedido'; // Genérico se nada estiver claro
                mainPaymentButton.removeAttribute('form');
            }
        }
    }

    if (paymentMethodRadios.length > 0) {
        paymentMethodRadios.forEach(radio => {
            radio.addEventListener('change', togglePaymentForms);
        });
        // Chama a função na carga da página para configurar a visibilidade inicial
        togglePaymentForms();
    } else {
        console.warn('Nenhum radio button para método de pagamento (name="paymentMethod") encontrado.');
    }

    // Event listener para o botão principal de pagamento/finalizar
    if (mainPaymentButton) {
        mainPaymentButton.addEventListener('click', function(event) {
            const selectedMethodRadio = document.querySelector('input[name="paymentMethod"]:checked');
            if (paymentErrorDiv) paymentErrorDiv.style.display = 'none';

            if (selectedMethodRadio) {
                const selectedMethodValue = selectedMethodRadio.value;
                console.log(`Botão principal ('${mainPaymentButton.textContent}') clicado para método: ${selectedMethodValue}`);

                // Se o método não for cartão, prevenimos o default (que seria submeter o form de cartão)
                // e executamos a lógica específica.
                if (selectedMethodValue !== 'creditCard') {
                    event.preventDefault();
                    if (selectedMethodValue === 'paypal') {
                        alert('Simulação: Redirecionando para o PayPal...');
                    } else if (selectedMethodValue === 'boleto') {
                        alert('Simulação: Gerando boleto...');
                    } else if (selectedMethodValue === 'pix') {
                        alert('Simulação: Gerando QR Code Pix...');
                    }
                } else {
                    // Para 'creditCard', o botão tem form="cardPaymentForm", então ele tentará submeter.
                    // A submissão real do formulário de cartão é tratada abaixo.
                    console.log('Botão "Pagar com Cartão" clicado, submissão será tratada pelo formulário.');
                }
            } else {
                event.preventDefault(); // Nenhum método selecionado
                 if(paymentErrorDiv){
                    paymentErrorDiv.textContent = 'Por favor, selecione um método de pagamento.';
                    paymentErrorDiv.style.display = 'block';
                } else {
                    alert('Por favor, selecione um método de pagamento.');
                }
            }
        });
    }

    // Event listener para a submissão DO FORMULÁRIO DE CARTÃO DE CRÉDITO
    if (cardPaymentFormElement) {
        cardPaymentFormElement.addEventListener('submit', async function(event) {
            event.preventDefault(); // Sempre previne a submissão padrão para tratar com JS/AJAX
            if (paymentErrorDiv) paymentErrorDiv.style.display = 'none';

            const token = localStorage.getItem('jwtToken'); // Assume que o token está salvo
            if (!token && paymentErrorDiv) { // Apenas um exemplo, autenticação para pagamento é mais complexa
                paymentErrorDiv.textContent = 'Erro de autenticação. Por favor, faça login novamente.';
                paymentErrorDiv.style.display = 'block';
                // window.location.href = 'login.html';
                return;
            }

            console.log('Formulário de cartão submetido via JavaScript.');
            const cardNumber = document.getElementById('cardNumber').value;
            const cardName = document.getElementById('cardName').value;
            const cardExpiry = document.getElementById('cardExpiry').value;
            const cardCvv = document.getElementById('cardCvv').value;

            if (!cardNumber || !cardName || !cardExpiry || !cardCvv) {
                if(paymentErrorDiv) {
                    paymentErrorDiv.textContent = 'Por favor, preencha todos os dados do cartão.';
                    paymentErrorDiv.style.display = 'block';
                }
                return;
            }

            console.log('Dados do Cartão (SIMULAÇÃO - NÃO ENVIE ISSO DIRETAMENTE EM PRODUÇÃO):', { cardNumber, cardName, cardExpiry, cardCvv });
            alert('Simulação: Processando pagamento com cartão...\nEm um sistema real, os dados do cartão seriam enviados de forma segura para um gateway de pagamento.');

            // Aqui viria a lógica de chamar o backend com um token de pagamento gerado por um gateway,
            // ou uma simulação de sucesso/erro para sua apresentação.
            // Ex:
            // setTimeout(() => {
            //    alert("Pagamento com cartão aprovado (simulação)!");
            //    window.location.href = "confirmacao_pedido.html"; // Redireciona para página de sucesso
            // }, 2000);
        });
    } else {
        console.warn("Elemento do formulário de cartão #cardPaymentForm não encontrado.")
    }
});
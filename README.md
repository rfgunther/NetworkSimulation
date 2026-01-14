\# Simulador de Camadas de Rede (OSI/TCP/IP) em Java



Projeto simples criado para estudar e visualizar o funcionamento das camadas de rede dos modelos \*\*OSI\*\* e \*\*TCP/IP\*\*, com ênfase em encapsulamento e desencapsulamento de dados.



Ideal para quem está começando em Redes de Computadores e quer entender na prática como os dados "viajam" pelas camadas.



\## Objetivo do projeto



\- Aprender \*\*Programação Orientada a Objetos\*\* em Java

\- Entender o fluxo de dados nas camadas de rede

\- Visualizar encapsulamento e desencapsulamento em tempo real

\- Introduzir conceitos básicos de \*\*criptografia\*\* na camada de apresentação (AES)



\## Cenário simulado



Uma rede doméstica com dispositivos típicos:

\- PC1 e PC2

\- Celular

\- SmartTV

\- Geladeira inteligente

\- Câmera de segurança



\## Camadas implementadas (até o momento)



1\. \*\*Application\*\* → mensagem do usuário

2\. \*\*Presentation\*\* → formatação + criptografia AES (opcional)

3\. \*\*Session\*\* → simulação de ID de sessão

4\. \*\*Transport\*\* → portas de origem/destino

5\. \*\*Network\*\* → endereços IP

6\. \*\*Data Link\*\* → enquadramento simples

7\. \*\*Physical\*\* → transmissão de bits (simulada)



\## Como executar



1\. Certifique-se de ter o \*\*JDK 8+\*\* instalado

2\. Clone o repositório:

&nbsp;  ```bash

&nbsp;  git clone https://github.com/rfgunther/NetworkSimulation.git

&nbsp;  cd NetworkSimulation

Compile:Bashjavac NetworkSimulation.java
Execute:Bashjava NetworkSimulation

Comandos disponíveis
textsend <origem> <destino> <mensagem>     → envia mensagem entre dispositivos
encrypt <dispositivo>                   → ativa criptografia AES naquele dispositivo
exit                                    → sai do programa
Exemplos:
Bashsend PC1 Geladeira Temperatura alta!
encrypt SmartTV
send Celular SmartTV Filme hoje à noite?
Estrutura do projeto (atual)
textNetworkSimulation/
├── NetworkSimulation.java     ← tudo em um arquivo (fácil para iniciantes)
├── .gitignore
└── README.md
Futuramente pretendo separar em arquivos por classe e adicionar:

Comunicação real via sockets
Perda de pacotes simulada
Latência e jitter
Interface gráfica simples (Swing ou JavaFX)

Tecnologias / Conceitos praticados

Java puro (sem frameworks)
Programação Orientada a Objetos (herança, polimorfismo, abstração)
Camadas OSI e TCP/IP
Encapsulamento / desencapsulamento
Criptografia simétrica (AES com ECB/PKCS5Padding – apenas didático)

Licença
MIT License – sinta-se à vontade para usar, modificar e aprender!
Feito com 💻 para estudar Redes e Java
Rudolf – 2025/2026

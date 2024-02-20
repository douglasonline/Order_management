# Gerenciamento de pedidos

# Sobre o projeto

 Esse projeto é um sistema de gestão de pedidos, onde os usuários podem cadastrar produtos e serviços, criar pedidos e adicionar itens a esses pedidos.
 
# Nível de prova do projeto
## PROVA NÍVEL III  

# O projeto segue os seguintes padrões

- Foi desenvolvido um cadastro (Create/Read/Update/Delete/List com paginação) para as seguintes entidades: produto/serviço, pedido e itens de pedido.
- E possível aplicar filtros na listagem
- As entidades utilizam Bean Validation
- Foi implementado um ControllerAdvice para customizar os HTTP Response das requisições (mínimo BAD REQUEST)
- Todos as entidades tem um ID único do tipo UUID gerado automaticamente
- No cadastro de produto/serviço tem uma indicação para diferenciar um produto de um serviço
- É possível aplicar um percentual de desconto no pedido, porém apenas para os itens que sejam produto (não serviço); o desconto será sobre o valor total dos produtos
- Somente é possível aplicar desconto no pedido se ele estiver na situação Aberto (Fechado bloqueia)
- Não é possível excluir um produto/serviço se ele estiver associado a algum pedido
- Não é possível adicionar um produto desativado em um pedido 

# Tecnologias utilizadas
## Back end
- Banco de dados PostgreSQL
- Java 8+ 
- Maven
- Spring
- JPA
- Bean Validation
- QueryDSL
- REST com JSON
- Swagger


## Banco de Dados Utilizado
- PostgreSQL

## Banco de Dados Utilizado para os Testes Unitários
- Embedded PostgreSQL

# Como executar o projeto

## Back end
Pré-requisitos: PostgreSQL, Java, Maven 

```bash
# Clonar repositório

# Na pasta raiz do projeto se necessário execute o codigo mvn clean compile para gerar as variáveis do QueryDSL  

```
- Para iniciar o projeto deve-se configurar o Banco de Dados PostgreSQL no application.properties passando o usuário (username) e a senha (password) conforme mostra na imagem abaixo:

![Configurar Banco De Dados](https://github.com/douglasonline/Imagens/blob/master/Configurar_%20Banco_De_Dados.png) 

```bash

# Após essas configurações do Banco de Dados pode-se executar o projeto que o Database e as tabelas serão criados automaticamentes 

```

# Como acessar o Swagger do Back end 

- Coloque no navegador o endereço: http://localhost:8080/swagger-ui/index.html#/

## Com o Swagger podemos ver as requisições que podemos realizar 

![Requisicoes Gestao de Pedidos Parte1](https://github.com/douglasonline/Imagens/blob/master/Requisicoes_Gestao_de_Pedidos_Parte1.png) 

![Requisicoes Gestao de Pedidos Parte2](https://github.com/douglasonline/Imagens/blob/master/Requisicoes_Gestao_de_Pedidos_Parte2.png) 

![Requisicoes Gestao de Pedidos Parte3](https://github.com/douglasonline/Imagens/blob/master/Requisicoes_Gestao_de_Pedidos_Parte3.png) 
  

## Como consumir o projeto

- O primeiro passo e colocar dados no nosso Banco de Dados

Para colocar os dados no nosso Banco de Dados estou utilizando o Postman 

- Temos que começar criando um produto ou serviço (productService)

- A nossa URL para adicionar um produto ou serviço fica assim

http://localhost:8080/api/productService 

![Criar Produto ou Servico](https://github.com/douglasonline/Imagens/blob/master/Criar_Produto_ou_Servico.png)

- Depois podemos criar um pedido (orderRequest), sem itens 

- A nossa URL para adicionar um pedido sem itens fica assim

http://localhost:8080/api/orderRequest

![Pedido Sem Itens](https://github.com/douglasonline/Imagens/blob/master/Pedido_Sem_Itens.png)

- Também podemos criar um pedido (orderRequest), com itens para isso precisamos passar o id do productService que criamos anteriormente 

- A nossa URL para adicionar um pedido com itens fica a mesma

http://localhost:8080/api/orderRequest

![Pedido Com Itens](https://github.com/douglasonline/Imagens/blob/master/Pedido_Com_Itens.png)

- Também podemos criar só o item (orderRequestItem), mas para isso precisamos do id do pedido (orderRequest), e do id do produto ou serviço (productService) 

- A nossa URL para adicionar um item fica assim

http://localhost:8080/api/orderRequestItem    

![Item Pedido](https://github.com/douglasonline/Imagens/blob/master/Item_Pedido.png)

- Também podemos adicionar desconto em um pedido 

- A nossa URL para adicionar um desconto em um pedido fica assim  

http://localhost:8080/api/orderRequest/ID_DO_PEDIDO/apply-discount

![Desconto Pedido](https://github.com/douglasonline/Imagens/blob/master/Desconto_Pedido.png)

# Autor

Douglas

https://www.linkedin.com/in/douglas-j-b2194a232/


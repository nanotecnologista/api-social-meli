**API Social MELI** 

**![][image1]Backend JAVA / GO** 

**Objetivo** 

O objetivo deste sprint é aplicar os conteúdos ministrados até o momento durante o BOOTCAMP do MeLi, para que você possa implementar uma API REST a partir de uma declaração proposta, uma especificação de requisitos e a documentação. 

**Consignas** 

O desafio a seguir consiste em: 

**Desenvolva uma API para um determinado cenário.** No ponto A da seção a seguir, você encontrará uma descrição detalhada do cenário e de cada um dos requisitos solicitados. 

**Bônus:** Caso você tenha conseguido resolver a seção A e ainda tenha tempo sobrando, nós o convidamos a propor suas próprias melhorias ou requisitos que julgar necessários e implementá-los como um extra. Para isso, você terá um requisito de referência para saber que tipo de melhorias pode buscar. Não se esqueça de que esta seção é 100% OPCIONAL e NÃO OBRIGATÓRIA. 

**Observe que:** 

Todo o desafio deve ser desenvolvido em **um único projeto.** 

A API precisa de documentação **Swagger.** 

Desenvolvimento individual 

**A. Cenário e requisitos iniciais** 

O Mercado Livre continua crescendo e no próximo ano pretende começar a implementar uma série de ferramentas que permitirão que compradores e vendedores tenham uma experiência totalmente inovadora, onde o vínculo que os une seja muito mais próximo.  
Como a data de lançamento está se aproximando, é necessária uma versão Beta do que será conhecido como **"SocialMeli"**, na qual os compradores poderão seguir seus vendedores favoritos e saber todas as notícias que eles publicam. 

Para isso, um analista funcional identificou uma série de requisitos que precisam ser executados. Você verá os detalhes a seguir: 

**A criação de uma API Rest SocialMeli está prevista para permitir:** 

Poder "**seguir**" um determinado **usuário** 

Obtenha o resultado do número de usuários que seguem um determinado vendedor**. (quantos me seguem)** 

Obter uma lista de todos os usuários que seguem um determinado vendedor (**Quem me segue?**) 

Obtenha uma lista de todos os vendedores seguidos por um determinado usuário (**Quem estou seguindo?**) 

Registre uma nova publicação. 

Obtenha uma lista das **publicações feitas nas últimas duas semanas** pelos vendedores que um usuário segue (para isso, leve em consideração a classificação por data, com as publicações mais recentes primeiro). 

Para que você possa "**deixar de seguir**" um determinado vendedor. 

Por outro lado, como se pretende uma boa experiência do usuário em relação à forma como os resultados de cada consulta são apresentados, é necessário que os resultados possam ser classificados por qualquer um dos seguintes critérios: 

Alfabético Ascendente e Descendente 

Data crescente e decrescente 

**SocialMeli** tem como objetivo permitir que os vendedores publiquem novos produtos com ofertas ou descontos especiais exclusivos para seus seguidores por um determinado período de tempo. Para isso, propõe os seguintes requisitos: 

Realizar a publicação de um novo produto em promoção. 

Obter a quantidade de produtos em promoção de um determinado vendedor.  
**B. Bônus (Desenvolvimento EXTRA)** 

Finalmente, o Mercado Livre anunciou que está aberto a qualquer nova funcionalidade ou proposta de melhoria para o "**SocialMeli**". Caso isso seja realizado, será necessário, além de desenvolver a funcionalidade, **apresentar a documentação técnica associada.** 

Abaixo está **um exemplo de requisito** para você usar como referência para a criação de possíveis novos requisitos. 

Obtenha uma lista de todos os produtos em promoção de um vendedor específico. 

![][image2]**Requisitos técnicos funcionais A. Requisitos iniciais** 

**US 0001:** Poder "seguir" um vendedor específico 

**Sign:** 

| Método  | SINAL |
| ----- | :---- |
| POST  | /users/{userId}/follow/{userIdToFollow} |
| **Exemplo**: /users/123/follow/234 |  |
| **Resposta**  | Código de status 200 ( OK) \- sem corpo ou dto  Código de status 400 (Bad Request) \- bodyless ou dto |

**Filtros/Parâmetros:** 

| Parâmetros  | Tipo  | Descrição/exemplo |
| :---- | :---: | :---- |

userId int Número que identifica o usuário atual userIdToFollow int Número que identifica o usuário a ser rastreado  
**US 0002:** Obtenha o resultado do número de usuários que seguem um determinado vendedor. 

**Sign:** 

| Método  | SINAL |
| :---- | :---- |
| GET /users/{userId}/followers/count  **Exemplo:** /users/234/followers/count/ |  |
| **Resposta**  | {  "userId": 234,  "userName": "vendedor1",  "followersCount": 35  } |

**Filtros/Parâmetros:** 

| Parâmetros  | Tipo  | Descrição/exemplo |
| :---- | :---: | :---- |
| userId  | int  | Número que identifica cada usuário |

**US 0003:** Obter uma lista de todos os usuários que seguem um determinado vendedor (Quem me segue?) 

**Sign:**

| Método  | SINAL |
| ----- | :---- |
| GET  | /users/{userId}/followers/list |
| **Exemplo:** /users/234/followers/list |  |

| Resposta  | {  "userId": 234,  "userName": "vendedor1",  "followers": \[  {  "userId": 4698,  "userName": "usuario1"  },  {  "userId": 1536,  "userName": "usuario2"  },  {  "userId": 2236,  "userName": "usuario3"  }  \]  } |
| :---- | :---- |

**Filtros/Parâmetros:** 

| Parâmetros  | Tipo  | Descrição/exemplo |
| :---- | :---: | ----- |
| userId  | int  | Número que identifica cada usuário |

**US 0004:** Obter uma lista de todos os vendedores seguidos por um determinado usuário (Quem estou seguindo?) 

**Sign:** 

| Método  | SINAL |
| :---- | :---- |

GET /users/{userId}/followed/list 

**Exemplo:** /users/4698/followed/list

| Resposta  | {  "user\_id": 4698,  "user\_name": "usuario1",  "followed": \[  {  "user\_id": 234,  "user\_name": "vendedor1"  },  {  "user\_id": 6932,  "user\_name": "vendedor2"  },  {  "user\_id": 6631,  "user\_name": "vendedor3"  }  \]  } |
| :---- | :---- |

**Filtros/Parâmetros:** 

| Parâmetros  | Tipo  | Descrição/exemplo |
| :---- | :---: | ----- |
| userId  | int  | Número que identifica cada usuário |

**US 0005:** Registro de uma nova publicação 

**Sign:**

| Método  | SINAL |
| ----- | :---- |
| POST  | /products/publish |

| PAYLOAD:  | {  "user\_id": 123,  "date": "29-04-2021",  "product": {  "product\_id": 1,  "product\_name": "Cadeira Gamer",  "type": "Gamer",  "brand": "Racer",  "color": "Red & Black",  "notes": "Special Edition"  },  "category": 100,  "price": 1500.50  } |
| :---- | :---- |
| **RESPONDER**  | Código de status 200 (tudo OK)  Código de status 400 (Bad request) |

**Filtros/Parâmetros:**

| Parâmetros  | Tipo  | Descrição/exemplo |
| :---- | :---: | :---- |
| user\_id  | int  | Número que identifica cada usuário |
| date  | LocalDate  | Data de publicação no formato dd-MM-aaaa |
| product\_id  | int  | Número de identificação de um produto associado a uma publicação |
| product\_name  | String  | Cadeia de caracteres que representa o nome de um produto |
| type  brand  color  | String  String  String  | Cadeia de caracteres que representa o tipo de um produto  Cadeia de caracteres que representa o nome da marca de um produto  Uma cadeia de caracteres que representa a cor de um produto. |
| notes  | String  | Cadeia de caracteres para colocar notas ou observações em um produto |

| category  | int  | Identificador usado para identificar a categoria à qual um produto pertence. Por exemplo: 100: Cadeiras, 58: Teclados |
| :---- | :---: | :---- |
| price  | double  | Preço do produto |

**US 0006:** Obtenha uma lista das publicações feitas pelos fornecedores que um usuário segue nas últimas duas semanas (para isso, leve em consideração a classificação por data, com as publicações mais recentes primeiro). 

**Sign:**

| Método  | SINAL |
| ----- | :---- |
| GET  | /products/followed/{userId}/list |
| **Exemplo:** /products/followed/4698/list |  |

| RESPONDER:  | {  "user\_id": 4698,  "posts": \[ {  “user\_id”: 123,El  "post\_id": 32,  "date": "01-05-2021",  "product": {  "product\_id": 62,  "product\_name": "Headset RGB Inalámbrico",  "type": "Gamer",  "brand": "Razer",  "color": "Green with RGB",  "notes": "Sem bateria"  },  "category": 120,  "price": 2800.69  },  {  “user\_id”: 234,  "post\_id": 18,  "date": "29-04-2021",  "product": {  "product\_id": 1,  "productName": "Cadeira Gamer",  "type": "Gamer",  "brand": "Racer",  "color": "Red & Black",  "notes": "Special Edition"  },  "category": 100,  "price": 15000.50  }  \]  } |
| :---- | :---- |
|  |  |

**Filtros/Parâmetros:**

| Parâmetros  | Tipo  | Descrição/exemplo |
| :---- | :---: | :---- |

| userId  | int  | Número que identifica cada usuário |
| :---- | :---: | :---- |

**US 0007:** Para que você possa "Unfollow" um determinado vendedor. 

**Sign:** 

| Método  | SINAL |
| ----- | :---- |
| POST  | /users/{users/{userId}/unfollow/{userIdToUnfollow} |
| **Exemplo:** /users/234/unfollow/123 |  |

**Filtros/Parâmetros:** 

| Parâmetros  | Tipo  | Descrição/exemplo |
| :---- | :---: | :---- |
| userId  | int  | Número que identifica o usuário atual |
| userIdToUnfollow  | int  | Número que identifica o usuário a ser deixado de seguir |

**US 0008:** Classificação alfabética em ordem crescente e decrescente 

**Sign:** 

| Método  | SINAL |
| ----- | :---- |
| GET  | **Exemplos:**  /users/{UserID}/followers/list?order=name\_asc  /users/{UserID}/followers/list?order=name\_desc  /users/{UserID}/followed/list?order=name\_asc  /users/{UserID}/followed/list?order=name\_desc |

| ordem  | Descrição |
| :---- | :---- |
| nome\_asc  | Alfabeticamente em ordem crescente. |
| nome\_desc  | Em ordem alfabética decrescente. |

\*Observação**:** este pedido se aplica somente a US-003 e US-004.  
**US 0009:** Classificação por data crescente e decrescente 

**Sign:** 

| Método  | SINAL |
| ----- | :---- |
| GET  | **Exemplos:**  /products/followed/{userId}/list?order=date\_asc  /products/followed/{userId}/list?order=date\_desc |

| ordem  | Descrição |
| :---- | :---- |
| data\_asc  | Data crescente (da mais antiga para a mais recente) |
| data\_desc  | Data decrescente (da mais recente para a mais antiga) |

\*Observação**:** esta portaria se aplica somente à US-006. 

**US 0010:** Realização da publicação de um novo produto promocional 

**Sign:**

| Método  | SINAL |
| ----- | :---- |
| POST  | /products/promo-pub |
| **PAYLOAD:**  | {  "user\_id": 234,  "date": "29-04-2021",  "product": {  "product\_id": 1,  "product\_name": "Cadeira Gamer",  "type": "Gamer",  "brand": "Racer",  "color": "Red & Black",  "notes": "Special Edition"  },  "category": 100,  "price": 1500.50,  "has\_promo": true, |

|  | "discount": 0.25  } |
| ----- | :---- |
| **Resposta**  | Código de status 200 (OK)  Código de status 400 (Bad Request) |

**Filtros/Parâmetros:**

| Parâmetros  | Tipo  | Descrição/exemplo |
| :---- | :---: | :---- |
| user\_id  | int  | Número que identifica cada usuário |
| date  | LocalDate  | Data de publicação no formato dd-MM-aaaa |
| product\_id  | int  | Número de identificação de um produto associado a uma publicação |
| product\_name  | String  | Cadeia de caracteres que representa o nome de um produto |
| type  | String  | Cadeia de caracteres que representa o tipo de um produto |
| brand  | String  | Cadeia de caracteres que representa o nome da marca de um produto |
| color  | String  | Uma cadeia de caracteres que representa a cor de um produto. |
| notes  | String  | Cadeia de caracteres para colocar notas ou observações em um produto |
| category  | int  | Identificador usado para identificar a categoria à qual um produto pertence. Por exemplo: 100: Cadeiras, 58: Teclados |
| price  | double  | Preço do produto |
| has\_promo  | boolean  | Campo verdadeiro ou falso para determinar se um produto está em promoção ou não. |
| discount  | double  | Caso um produto esteja em promoção, defina o valor do desconto. |

**US 0011:** Obter a quantidade de produtos em promoção para um determinado vendedor. 

**Sign:** 

| Método  | SINAL |
| ----- | :---- |
| GET  | /products/promo-pub/count?user\_id={userId} |
| **Resposta**  | {  "user\_id" : 234,  "user\_name": "vendedor1",  "promo\_products\_count": 23  } |

**Filtros/Parâmetros:** 

| Parâmetros  | Tipo  | Descrição/exemplo |
| :---- | :---: | :---- |
| user\_id  | int  | Número que identifica cada usuário |
| user\_name  | String  | Cadeia de caracteres que representa o nome do usuário |
| promo\_products\_count  | int  | Quantidade numérica de produtos em promoção para um determinado usuário. |

**B. Exemplo de requisito de bônus (OPCIONAL)** 

**US 0012:** Obter uma lista de todos os produtos em promoção de um determinado vendedor. 

**Sign:** 

| Método  | SINAL |
| :---- | :---- |

GET /products/promo-pub/list?user\_id={userId}

| RESPONDER:  | {  "user\_id": 234,  "user\_name": "vendedor1",  "posts": \[  {  “user\_id”: 234  "post\_id": 18,  "date": "29-04-2021",  "product": {  "product\_id": 1,  "product\_name": "Cadeira Gamer",  "type": "Gamer",  "brand": "Racer",  "color": "Red & Black",  "notes": "Special Edition"  },  "category": "100",  "price": 15000.50,  "has\_promo": true,  "discount": 0.25  }  \]  } |
| :---- | :---- |

**Filtros/Parâmetros:** 

| Parâmetros  | Tipo  | Descrição/exemplo |
| :---- | :---: | :---- |
| user\_id  | int  | Número que identifica cada usuário |
| user\_name  | String  | Cadeia de caracteres que representa o nome do usuário |
| post\_id  | int  | Número que identifica cada uma das publicações |
| date  | LocalDate  | Data de publicação no formato dd-MM-aaaa |
| product\_id  | int  | Número de identificação de um produto associado a uma publicação |

product\_name String Cadeia de caracteres que representa o nome de um produto

| type  | String  | Cadeia de caracteres que representa o tipo de um produto |
| :---- | :---: | :---- |
| brand  | String  | Cadeia de caracteres que representa o nome da marca de um produto |
| color  | String  | Uma cadeia de caracteres que representa a cor de um produto. |
| notes  | String  | Cadeia de caracteres para colocar notas ou observações em um produto |
| category  | int  | Identificador usado para identificar a categoria à qual um produto pertence. Por exemplo: 100: Cadeiras, 58: Teclados |
| price  | double  | Preço do produto |
| has\_promo  | boolean  | Campo verdadeiro ou falso para determinar se um produto está em promoção ou não. |
| discount  | double  | Caso um produto esteja em promoção, defina o valor do desconto. |

![][image3]**Backend \- Testing** 

**Diretrizes de atividades** 

O desafio a seguir consiste em duas partes: 

**Implementar validações e diferentes testes para um determinado cenário:** a partir de um primeiro incremento de um cenário conhecido, devem ser estabelecidos diferentes processos de validação de dados e os testes unitários necessários. Após a implementação bem-sucedida dos testes de unidade, vocês terão de implementar pelo menos um teste de integração para cada ***“User-Story”***. 

**Bônus:** Caso você tenha conseguido resolver a seção "A" e ainda tenha tempo, convidamos você a propor outros testes de integração (diferentes do que você  
implementou no ponto A). Não se esqueça de que esta seção é **100% OPCIONAL e NÃO OBRIGATÓRIA.** 

**Cenário** 

**SocialMeli**, A nova implementação do MercadoLibre, realizada pela equipe de desenvolvimento do "Bootcamp", tornou-se um sucesso\! Diante disso e do fato de que o MeLi tem padrões de qualidade muito altos em relação aos produtos de software que utiliza, estabeleceu uma série de validações que considera necessárias levar em conta ao incorporar dados, bem como diferentes testes unitários para garantir o funcionamento correto de cada uma das funcionalidades que inclui. 

Para realizar essas implementações, deve-se tomar como base o projeto desenvolvido. A partir dele, cada uma das validações e testes unitários correspondentes será realizada. Seguindo o princípio de que o MeLi possui padrões de qualidade muito altos, um especialista sugeriu a possibilidade de implementar pelo menos um teste de integração para cada User Story da primeira versão, com o objetivo de alcançar uma cobertura de código maior do que a obtida com os testes unitários. 

Como documentação de apoio, um analista funcional anexará o seguinte documento de requisitos técnicos e funcionais: Documentação 

**Requisitos incrementais (BÔNUS OPCIONAL)** 

O mesmo especialista em qualidade que sugeriu a implementação de pelo menos um teste de integração acima sugere que seria ideal ter testes de integração adicionais implementados para tentar alcançar **uma cobertura maior ou igual a 80%.** 

Deve-se observar que a base de desenvolvimento alcançada em uma equipe deve ser respeitada para que seja possível realizar esse novo incremento individualmente. O especialista sabe que o tempo de desenvolvimento do bootcamp é limitado e, por isso, sugere que essa implementação seja feita **somente se os prazos forem atingidos e a data de entrega** estimada puder ser cumprida.  
![][image4]**Requisitos técnicos funcionais (Ponto A)** 

**User Stories** 

A SocialMeli tinha anteriormente as seguintes histórias de usuário e requisitos técnicos: **US-0001:** Ser capaz de "Follow" um vendedor específico 

**US-0002:** Obtenha o resultado do número de usuários que seguem um determinado vendedor. 

**US-0003:** Obter uma lista de todos os usuários que seguem um determinado vendedor (Quem me segue?)   
**US-0004:** Obtenha uma lista de todos os vendedores seguidos por um determinado usuário (Quem estou seguindo?)   
**US-0005:** Registre uma nova publicação. 

**US-0006:** Obtenha uma lista das postagens feitas pelos fornecedores que um usuário segue nas últimas duas semanas (para isso, leve em consideração a classificação por data, com as postagens mais recentes primeiro). 

**US-0007:** Para poder fazer "Unfollow" um determinado vendedor. 

**US-0008:** Classificação alfabética em ordem crescente e decrescente. 

**US-0009:** Classificação por data ascendente e descendente.

| Dados/Parâmetros  | Tipo  | Longitude  | Descrição |
| :---- | :---: | ----- | :---- |
| user\_id  | Integer  |  | Número que identifica o usuário atual |
| user\_id\_to\_follow  | Integer  |  | Número que identifica o usuário a ser rastreado |
| user\_name  | String  | 15  | Nome de usuário associado ao user\_id |
| followers\_count  | Integer  |  | Número de seguidores |

| id\_post  | Integer  |  | Número que identifica cada uma das publicações |
| :---- | :---: | ----- | :---- |
| date  | LocalDate  |  | Data de publicação no formato dd-MM-aaaa |
| product\_id  | Integer  |  | Número de identificação de cada um dos produtos associados a uma publicação |
| product\_name  | String  | 40  | Cadeia de caracteres que representa o nome de um produto |
| type  | String  | 15  | Cadeia de caracteres que representa o tipo de um produto |
| brand  | String  | 25  | Cadeia de caracteres que representa o nome da marca de um produto |
| color  | String  | 15  | Uma cadeia de caracteres que representa a cor de um produto. |
| notes  | String  | 80  | Cadeia de caracteres para colocar notas ou observações em um produto |
| category  | Integer  |  | Identificador usado para identificar a categoria à qual um produto pertence. Por exemplo: 100: Cadeiras, 58: Teclados |
| price  | Double  | 10.000.000 (Máx) | Preço do produto |
| user\_id\_to\_unfollow  | Integer  |  | Número que identifica o usuário a ser deixado de seguir |
| order  | String  |  | Define a ordenação. Pode ter os valores: name\_asc, name\_desc, date\_asc, date\_desc |

**Resumo dos dados de entrada (todos os US):** 

**Validações de campo (todos os US):**

| Dados/Parâmetros  | Obrigatório?  | Validação  | Mensagem de erro |
| :---- | :---: | ----- | ----- |
| user\_id  | SIM  | Que o  campo não  esteja  vazio.  Maior 0 | O id não pode  estar vazio.  id deve ser  maior que  zero |
| date  | SIM  | Que o  campo não  esteja  vazio. | A data não  pode estar  vazia. |
| product\_id  **product\_name**  | SIM  SIM  | Que o  campo não  esteja vazio.  Maior 0  Que o  campo não  esteja vazio.  Compriment  o máximo de  40  caracteres.  Que não  tem  caracteres  especiais (%,  &, $, etc.),  permite  espaços | O id não pode  estar vazio.  id deve ser  maior que  zero  O campo não  pode estar  vazio.  O  comprimento  não pode  exceder 40  caracteres.  O campo não  pode conter  caracteres  especiais. |
| type  | SIM  | Que o  campo não  esteja vazio.  Compriment  o máximo de  15  caracteres. | O campo não  pode estar  vazio.  O  comprimento  não pode  exceder 15  caracteres. |

|  |  | Nãocontém  caracteres  especiais(%,  &, $, etc.) | Ocamponão  podeconter  caracteres  especiais.  |
| :---- | ----- | ----- | ----- |
| brand  | SIM  | Que o  campo não  esteja  vazio.  Comprimen  to máximo  de 25  caracteres.  Não contém  caracteres  especiais (%,  &, $, etc.)  | O comprimento  nãopode  exceder25  caracteres.  O campo não  podeestar  vazio.  O camponão  podeconter  caracteres  especiais.  |
| color  | SIM  | Que o  campo não  esteja vazio.  Compriment  o máximo de  15 caracteres.  Não contém  caracteres  especiais (%,  &, $, etc.)  | O campo não  podeestar  vazio.  O comprimento  nãopode  exceder15  caracteres.  O campo não  podeconter  caracteres  especiais.  |
| **notes**  | NÃO  | Compriment  o máximo de  80 caracteres.  Que não  tem caracteres  especiais (%,  &, $, etc.),  permite  espaços  | O comprimento  nãopode  exceder80  caracteres.  O campo não  podeconter  caracteres  especiais. |

| category  | SIM  | Que o  campo não  esteja vazio. | O campo não  pode estar  vazio. |
| :---- | :---: | ----: | ----: |
| price  | SIM  | Que o  campo não  esteja vazio  O preço  máximo  pode ser de  10.000.000. | O campo não  pode estar  vazio.  O preço  máximo por  produto é de  10.000.000 |

**\*Observação:** Observe que, para o retorno de mensagens de erro, é recomendável usar os códigos de status correspondentes. 

**Testes unitários:** 

Uma série de testes unitários a serem executados é solicitada abaixo; no entanto, se for considerado necessário implementar outros, isso é totalmente viável.

|  | Situações de entrada  | Comportamento Esperado |
| :---- | ----- | ----- |
| **T-0001**  | Verifique se o usuário a ser rastreado existe. **(US-0001)** | **Ela foi cumprida:**  Isso permite que você continue normalmente.  **Ela não é cumprida:**  Notifica a inexistência por meio de uma exceção. |
| **T-0002  T-0003**  | Verifique se o usuário a ser deixado de seguir existe. **(US-0007)**  Verifique se o tipo de ordem de classificação alfabética existe **(US-0008)** | **Ela foi cumprida:**  Isso permite que você continue normalmente.  **Ela não é cumprida:**  Notifica a inexistência por meio de uma exceção.  **Ela foi cumprida:**  Isso permite que você continue normalmente.  **Ela não é cumprida:**  Notifica a inexistência por meio de uma exceção. |

| T-0004  | Verifique a ordem ascendente e descendente correta por nome. (US-0008) | Devuelve la lista ordenada según el criterio solicitado |
| :---- | :---- | :---- |
| **T-0005**  | Verifique se a classificação por data existe. **(US-0009)** | **Ela foi cumprida:**  Isso permite que você continue normalmente.  **Ela não é cumprida:**  Notifica a inexistência por meio de uma exceção. |
| **T-0006**  | Verificar a ordem ascendente e descendente correta por data. **(US-0009)** | Verificar a ordem ascendente e descendente correta por data. **(US-0009)** |
| **T-0007**  | Verifique se o número de seguidores de um determinado usuário está correto.. **(US-0002)** | Retorna o cálculo correto do número total de seguidores que um usuário tem. |
| **T-0008**  | Verifique se a consulta de publicações feitas nas duas últimas semanas de um determinado vendedor são de fato das duas últimas semanas.. **(US-0006)** | Retorna apenas dados de publicações com data de publicação nas duas últimas semanas a partir do dia da data. |

[image1]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABQAAAAUCAYAAACNiR0NAAABDElEQVR4XsXUPWqEQBTAce+hlZ1iYSMKFkIK2RN4goBNsLX3FBZewco72Gvhml4L2WJBEowBfZl5rFvM7G7MfuXBD8R5/JEpFERRfFEUZXvwcaV3SpKkjaCqakPAPZDo7iFB7uAWF4NFUcAwDFCWJdJ1ndthXQxGUQTzPMMycRxzO6znBjVNgzzPj8FxHMHzPG5vdZByXRf6vkd06rrGuzx3n78GqSRJ0DJhGCJ273+Ctm1D13WITtu2YFkWYndXBbMsO37ZNE3g+z63szoYBAFGlknTlNthPTdYVRUGm6ZB5+5tddA0TXAcBwzDQOz5KQL55ezZl9circ/7B2VZfiUPXxS78AffFGm9/QBPysE1tiKFpQAAAABJRU5ErkJggg==>

[image2]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABQAAAAUCAYAAACNiR0NAAABDElEQVR4XsXUPWqEQBTAce+hlZ1iYSMKFkIK2RN4goBNsLX3FBZewco72Gvhml4L2WJBEowBfZl5rFvM7G7MfuXBD8R5/JEpFERRfFEUZXvwcaV3SpKkjaCqakPAPZDo7iFB7uAWF4NFUcAwDFCWJdJ1ndthXQxGUQTzPMMycRxzO6znBjVNgzzPj8FxHMHzPG5vdZByXRf6vkd06rrGuzx3n78GqSRJ0DJhGCJ273+Ctm1D13WITtu2YFkWYndXBbMsO37ZNE3g+z63szoYBAFGlknTlNthPTdYVRUGm6ZB5+5tddA0TXAcBwzDQOz5KQL55ezZl9circ/7B2VZfiUPXxS78AffFGm9/QBPysE1tiKFpQAAAABJRU5ErkJggg==>

[image3]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABQAAAAUCAYAAACNiR0NAAABDElEQVR4XsXUPWqEQBTAce+hlZ1iYSMKFkIK2RN4goBNsLX3FBZewco72Gvhml4L2WJBEowBfZl5rFvM7G7MfuXBD8R5/JEpFERRfFEUZXvwcaV3SpKkjaCqakPAPZDo7iFB7uAWF4NFUcAwDFCWJdJ1ndthXQxGUQTzPMMycRxzO6znBjVNgzzPj8FxHMHzPG5vdZByXRf6vkd06rrGuzx3n78GqSRJ0DJhGCJ273+Ctm1D13WITtu2YFkWYndXBbMsO37ZNE3g+z63szoYBAFGlknTlNthPTdYVRUGm6ZB5+5tddA0TXAcBwzDQOz5KQL55ezZl9circ/7B2VZfiUPXxS78AffFGm9/QBPysE1tiKFpQAAAABJRU5ErkJggg==>

[image4]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABQAAAAUCAYAAACNiR0NAAABDElEQVR4XsXUPWqEQBTAce+hlZ1iYSMKFkIK2RN4goBNsLX3FBZewco72Gvhml4L2WJBEowBfZl5rFvM7G7MfuXBD8R5/JEpFERRfFEUZXvwcaV3SpKkjaCqakPAPZDo7iFB7uAWF4NFUcAwDFCWJdJ1ndthXQxGUQTzPMMycRxzO6znBjVNgzzPj8FxHMHzPG5vdZByXRf6vkd06rrGuzx3n78GqSRJ0DJhGCJ273+Ctm1D13WITtu2YFkWYndXBbMsO37ZNE3g+z63szoYBAFGlknTlNthPTdYVRUGm6ZB5+5tddA0TXAcBwzDQOz5KQL55ezZl9circ/7B2VZfiUPXxS78AffFGm9/QBPysE1tiKFpQAAAABJRU5ErkJggg==>
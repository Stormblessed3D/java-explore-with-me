# Explore – афиша событий
📌 **Общее описание приложения** <br /> 
Микросервисное REST-приложение, которое позволяет пользователям делиться информацией о мероприятиях и находить компанию для участия в них. 
Включает в себя основной сервис и сервис статистики, каждый со своей БД. Предусмотрена возможность поиска и фильтрации мероприятий, их добавления / изменения, настроена подача заявок на участие в мероприятиях и их подтверждение, комментарии. 
Предусмотрена модерация событий, размещённых пользователями.<br /> 
<br /> 
Приложение состоит из двух сервисов:     
* ***Основной сервис:***           
API основного сервиса разделен на три части:    
    * публичная часть - доступна любому пользователю без регистрации     
    * закрытая часть - доступна только авторизованным пользователям     
    * административная часть - доступна только для администраторов сервиса    
* ***Сервис статистики***       
Сохраняет статистику о количестве обращений пользователей к спискам событий ```GET /events``` или конкретному событию ```GET /events/{id}```. Сервис фиксирует эндпоинт, IP-адрес пользователя, отправившего запрос, и дату запроса.     

📌 **Стек технологий:** <br /> 
Java 11, Spring Boot 2.7.5, Maven, Hibernate, Lombok, MapStruct, QueryDSL, PostgreSQL, Docker. <br />
<br />
📌 **Инструкция по развертыванию:** <br /> 
Версия JDK: Amazon Corretto 11      
Приложение запускается через Docker (основной сервис, сервис статистики и базы данных каждого из сервисов запускаются в отдельном Docker-контейнере каждый).         
   
1. ```mvn package``` - создать jar-файлы       
2. ```docker-compose up``` - команда для запуска приложения (запускать из папки проекта). Конфигурация описана в файле [docker-compose.yml ](https://github.com/Stormblessed3D/java-explore-with-me/blob/main/docker-compose.yml)             

📌 **Функциональность**:      
[Swagger спецификация основного сервиса](https://github.com/Stormblessed3D/java-explore-with-me/blob/main/ewm-main-service-spec.json)       
[Swagger спецификация сервиса статистики](https://github.com/Stormblessed3D/java-explore-with-me/blob/main/ewm-stats-service-spec.json)      

📌 **Планы по развитию приложения**:      
* добавить Spring Security для авторизации, аутентификации пользователей       
* использовать брокер сообщений (например, Apache Kafka) для общения основного сервиса и сервиса статистики     

### ER диаграмма основного сервиса
![ER diagram](https://github.com/Stormblessed3D/java-explore-with-me/blob/main/ewm-main-service/ER%20diagram_Explore%20with%20me_v2.png)

# Explore – афиша событий
**Общее описание приложения** <br /> 
Микросервисное REST-приложение, которое позволяет пользователям делиться информацией о мероприятиях и находить компанию для участия в них. 
Включает в себя основной сервис и сервис статистики, каждый со своей БД. Предусмотрена возможность поиска и фильтрации мероприятий, их добавления / изменения, настроена подача заявок на участие в мероприятиях и их подтверждение, комментарии. 
Предусмотрена модерация событий, размещённых пользователями. Запросы для получения списка событий фиксируются сервисом статистики.<br /> 
<br /> 
**Стек технологий:** <br /> 
Java 11, Spring Boot 2.7.5, Maven, Hibernate, Lombok, MapStruct, QueryDSL, PostgreSQL, Docker. <br />
<br />
**Инструкция по развертыванию:** <br /> 
Версия JDK: Amazon Corretto 11      
Приложение запускается через Docker (основной сервис, сервис статистики и базы данных каждого из сервисов запускаются в отдельном Docker-контейнере каждый).         
   
1. ```mvn package``` - создать jar-файлы       
2. ```docker-compose up``` - команда для запуска приложения (запускать из папки проекта). Конфигурация описана в файле [docker-compose.yml ](https://github.com/Stormblessed3D/java-explore-with-me/blob/main/docker-compose.yml)             

**Функциональность**:      
[Swagger спецификация основного сервиса](https://github.com/Stormblessed3D/java-explore-with-me/blob/main/ewm-main-service-spec.json)       
[Swagger спецификация сервиса статистики](https://github.com/Stormblessed3D/java-explore-with-me/blob/main/ewm-stats-service-spec.json)      


### ER diagram of main service
![ER diagram](https://github.com/Stormblessed3D/java-explore-with-me/blob/main/ewm-main-service/ER%20diagram_Explore%20with%20me_v2.png)

Bidding System

Backend Architecture: Developed a scalable bidding system with Spring Boot, allowing users to place bids and determine the winner through a defined strategy.

Design Pattern: Combined the Strategy and Observer Patterns to identify the auction winner based on customizable criteria while providing real-time notifications to all bidders.

Responsiveness and Scalability:  Integrated rate limiting with Apache Kafka to enhance bid management, control concurrent bids, and facilitate real-time updates through asynchronous communication.

To run
1. create bidding_system Database in mysql
2. start the zookeper
3. start apace kafka
4. start the spring boot application
5. now you are good to go to do you own bidding 


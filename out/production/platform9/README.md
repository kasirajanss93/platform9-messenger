# platform9-messenger

This is a Messaging system where different clients can communicate with each other. The design is done to handle the asynchrous request similar to ACTOR model implementation i.e each client will have its own mailbox where the message sits.s

Assumptions

1. Client A will have list of Clients to which it can communicate and vice versa. It is hardcoded for now

2. Clients and Servers are in different machine - used socket programming to communicate between the client and the server

3. Made use of RabbitMQ to handle multiple client request in a FIFO manner. If multiple client tries to communicate at the same time then the requests are handled in a FIFO manner

4. Assumptions has to made to handle clients request asynchronously (similar to ACTOR model). Each client will have its own MailBox in this case it is a Queue. Once the request comes to the queue then the message is displayed.

5. Multiple clients can send message to a particular client say A. A will receive those message but A should mention the client name before it has to send message

6. Client can press Enter without typing anything to choose the client it wants to communicate

7. If the client name entered is wrong, then it will prompt again to select the client


System Explanation

1. A Client can choose the other client to which it can communcation

2. if the choosen client is in the list of user the client can communicate then it can start communicating

3. Client type the message and when enter is pressed the message is sent to servers port (1234)

4. The server receives the message for the port (1234) and adds the message to its FIFO RabbitMQ

5. The listener of the RabbitMQ then reads the content of the message and starts processing the message in Queue (from,to,message,port). The message is extracted and it is send to the client using the client port in the message. The client port is stored in a hashmap which recides in the server

6. Each client listens for the message in its port and once it receives the message. It places it in its own Queue which the listner listens and displays it.

7. At a point, a client can view message from multiple clients (similar to whatsapp) but it can communicate with only one other client at an instance


Steps to Setup

1. Clone the repository from https://github.com/kasirajanss93/platform9-messenger.git

2. Follow steps to install RabbitMQ https://www.rabbitmq.com/install-standalone-mac.html

3. Start RabbitMQ as a backgroud process using rabbitmq-server 

4. Download Intellij Idea from https://www.jetbrains.com/idea/

5. Open Intellij - File->Open-> select the folder platform-messenger from the clone folder

6. Run Server.java

7. Run Multiple Clients - Client1.java,Client2.java,Client3.java,Client4.java

Client1 
[*] Waiting for messages. To exit press CTRL+C
Client List
Client1
Client2
Client3
Enter Client Name:
Client2
Start Typing......
Client2: Hi
Hello
You: Hello
How are you ?
You: How are you ?
Client2: I am good



Client2

[*] Waiting for messages. To exit press CTRL+C
Client List
Client1
Client2
Client3
Client4
Enter Client Name:
Client1
Start Typing......
Hi
You: Hi
Client1: Hello
Client1: How are you ?
I am good
You: I am good
You: 
Client List
Client1
Client2
Client3
Client4
Enter Client Name:
Client3
Start Typing......
Hi Hello
You: Hi Hello

Client3
 [*] Waiting for messages. To exit press CTRL+C
Client List
Client1
Client2
Client3
Enter Client Name:
Client2: Hi Hello


8. Giving enter which typing anything will allow to choose next client

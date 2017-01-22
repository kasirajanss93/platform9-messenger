# platform9-messenger

This is a Messaging system where different clients can communicate with each other. The design is done to handle the asynchronous request similar to ACTOR model implementation i.e each client will have its own mailbox where the message sits.

Assumptions

1. Client A will have a list of Clients to which it can communicate and vice versa. It is hard coded for now

2. Client A needs to know the username of Client B for the communication to happen, for now as mentioned before it is hard coded

3. Clients and Servers are in different machine - used socket programming to communicate between the client and the server. 

4. Made use of RabbitMQ to handle multiple client requests in a FIFO manner. If multiple clients tries to communicate at the same time to the server then the requests are handled in a FIFO manner

5. Assumptions have made to handle clients request asynchronously (similar to ACTOR model). Each client will have its own MailBox, in this case, it is a Queue. Once the request comes into the queue then the message is displayed on the client

6. Multiple clients can send message to a particular client, say A. A will receive those message but for A to communicate it should estabish a connection to the particular client it needs to communicate

7. Client can press Enter without typing anything to choose the client it wants to communicate

8. If the client name entered is wrong, then it will prompt again to select the client

9. The ports of each client are stored in the server in a hashmap. This can be enhanced in the future, allowing clients to register to the server


System Explanation

1. A Client A can choose the other client (B,C,D) to which it can communication

2. If the chosen client is in the list of user the client can communicate, then it can start communicating

3. Client type the message and when enter is pressed the message is sent to servers port (1234)

4. The server receives the message for the port (1234) and adds the message to its FIFO RabbitMQ

5. The listener of the RabbitMQ then reads the content of the message and starts processing the message in Queue (from,to,message,port). The message is extracted and it is sent to the client using the client port in the message (present in the server currently). The client port is stored in a hashmap which resides in the server

6. Each client listens for the message in its port and once it receives the message. It places it in its own Queue which the listener listens and displays it.

7. At a point, a client can view message from multiple clients (similar to WhatsApp) but it can communicate with only one other client at an instance


Steps to Setup

1. Clone the repository from https://github.com/kasirajanss93/platform9-messenger.git

2. Follow steps to install RabbitMQ https://www.rabbitmq.com/install-standalone-mac.html

3. Start RabbitMQ as a background process using rabbitmq-server 

4. Download IntelliJ Idea from https://www.jetbrains.com/idea/

5. Open Intellij - File->Open-> select the folder platform-messenger from the clone folder

6. Run Server.java

7. Run Multiple Clients - Client1.java,Client2.java,Client3.java,Client4.java

8. Giving enter without typing anything will allow choosing next client

9. The following are the example of the client communication in action 

- Client1 
- [*] Waiting for messages. To exit press CTRL+C
- Client List
- Client1
- Client2
- Client3
- Enter Client Name:
- Client2
- Start Typing......
- Client2: Hi
- Hello
- You: Hello
- How are you ?
- You: How are you ?
- Client2: I am good
.
.
.
- Client2

- [*] Waiting for messages. To exit press CTRL+C
- Client List
- Client1
- Client2
- Client3
- Client4
- Enter Client Name:
- Client1
- Start Typing......
- Hi
- You: Hi
- Client1: Hello
- Client1: How are you ?
- I am good
- You: I am good
- You: 
- Client List
- Client1
- Client2
- Client3
- Client4
- Enter Client Name:
- Client3
- Start Typing......
- Hi Hello
- You: Hi Hello

- Client3
-  [*] Waiting for messages. To exit press CTRL+C
- Client List
- Client1
- Client2
- Client3
- Enter Client Name:
- Client2: Hi Hello



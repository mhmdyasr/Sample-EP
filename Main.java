import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import org.glassfish.tyrus.server.Server;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

// User class for Signup
class User {
    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }
}

// Transaction class
class Transaction {
    private String transactionId;
    private String username;
    private double amount;

    public Transaction(String transactionId, String username, double amount) {
        this.transactionId = transactionId;
        this.username = username;
        this.amount = amount;
    }

    public String getTransactionId() {
        return transactionId;
    }
}

// Admin class
class Admin {
    public void viewUsers(ArrayList<User> users) {
        System.out.println("Registered Users:");
        for (User  user : users) {
            System.out.println(user.getUsername());
        }
    }
}

// WebSocket Server
@ServerEndpoint("/styles")
class StyleWebSocketServer {
    private static List<Session> clients = Collections.synchronizedList(new ArrayList<>());

    @OnOpen
    public void onOpen(Session session) {
        clients.add(session);
        System.out.println("New client connected: " + session.getId());
    }

    @OnClose
    public void onClose(Session session) {
        clients.remove(session);
        System.out.println("Client disconnected: " + session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Received message: " + message);
        // Broadcast the message to all connected clients
        synchronized (clients) {
            for (Session client : clients) {
                if (client.isOpen()) {
                    try {
                        client.getBasicRemote().sendText(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

// Main class
public class Main {
    public static void main(String[] args) {
        ArrayList<User> users = new ArrayList<>();
        ArrayList<Transaction> transactions = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);

        // Start the WebSocket server
        new Thread(() -> {
            Server server = new Server("localhost", 8080, "/ws", null, StyleWebSocketServer.class);
            try {
                server.start();
                System.out.println("WebSocket server started on ws://localhost:8080/ws/styles");
                // Keep the server running
                Thread.currentThread().join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // Add a shutdown hook to close the scanner
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            scanner.close();
            System.out.println("Scanner closed.");
        }));

        // Signup Module
        System.out.println("Signup Module");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        users.add(new User(username, password));
        System.out.println("User  registered successfully!");

        // Transaction Module
        System.out.println("Transaction Module");
        System.out.print("Enter transaction ID: ");
        String transactionId = scanner.nextLine();
        System.out.print("Enter amount: ");
        double amount = scanner.nextDouble();
        transactions.add(new Transaction(transactionId, username, amount));
        System.out.println("Transaction recorded successfully!");

        // Admin Module
        Admin admin = new Admin();
        admin.viewUsers(users);

        // Change CSS properties (simulated)
        System.out.println("Changing CSS properties...");
        System.out.print("Enter new background color (e.g., lightblue): ");
        String newBackgroundColor = scanner.next();
        System.out.print("Enter new text color (e.g., white): ");
        String newTextColor = scanner.next();
        System.out.print("Enter new border (e.g., 2px solid #007BFF): ");
        String newBorder = scanner.next();
        System.out.print("Enter new border radius (e.g., 15px): ");
        String newBorderRadius = scanner.next();
        System.out.print("Enter new box shadow (e.g., 0 4px 8px rgba(0, 0, 0, 0.2)): ");
        String newBoxShadow = scanner.next();

        // Simulate sending these styles to the HTML page
        String stylesToSend = String.format("{\"backgroundColor\":\"%s\",\"color\":\"%s\",\"border\":\"%s\",\"borderRadius\":\"%s\",\"boxShadow\":\"%s\"}",
                newBackgroundColor, newTextColor, newBorder, newBorderRadius, newBoxShadow);
        synchronized (StyleWebSocketServer.clients) {
            for (Session client : StyleWebSocketServer.clients) {
                if (client.isOpen()) {
                    try {
                        client.getBasicRemote().sendText(stylesToSend);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

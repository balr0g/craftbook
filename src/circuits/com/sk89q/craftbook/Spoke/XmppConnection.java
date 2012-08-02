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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class XmppConnection {
	private Socket socket = null;
	private static ServerSocket server = null;
	private BufferedReader streamIn = null;
	private DataOutputStream streamOut = null;
	protected RunnableParam Finished;
	private static String LoadPage;

	public XmppConnection() {

		Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					if (CurMessage != null) {
						CurMessage = null;
					}

				}
			}
		});
		th.run();
	}

	public interface InterperateRequest {
		public String requestRecieved(String request);
	}

	static InterperateRequest requestRecieved;

	public static void Start(final int port, InterperateRequest req,
			String loadPage) {
		requestRecieved = req;
		LoadPage = loadPage;
		Thread df = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					System.out.println("Debugger running on " + port
							+ ", please wait  ...");
					server = new ServerSocket(port);
					while (true) {

						final Socket socket = server.accept();
						BufferedReader streamIn = new BufferedReader(
								new InputStreamReader(socket.getInputStream()));
						final DataOutputStream streamOut = new DataOutputStream(
								new BufferedOutputStream(socket
										.getOutputStream()));

						String request = streamIn.readLine();
						if (request == null)
							continue;
						request = request.replace("GET /", "").replace(
								" HTTP/1.1", "");

						if (request.toLowerCase().equals("favicon.ico")) {
							return;
						} else if (request.toLowerCase().equals("world")) {
							streamOut.writeBytes(makeMessage(LoadPage,
									"text/html"));
							streamOut.flush();
							socket.close();
							return;
						} else if (request.toLowerCase().startsWith(
								"beginconnection")) {
							String gc = UUID.randomUUID().toString();
							socks.put(gc, new XmppConnection());
							streamOut.writeBytes(makeMessage(gc, "text/plain"));
							streamOut.flush();
							socket.close();
							return;
						} else if (request.toLowerCase().startsWith(
								"maintainconnection/")) {
							String uid = request.toLowerCase().replace(
									"maintainconnection/", "");
							XmppConnection d = socks.get(uid);

							d.Finished = new RunnableParam() {
								@Override
								public void run(String m) {
									try {
										streamOut.writeBytes(makeMessage(m,
												"text/plain"));
										streamOut.flush();
										socket.close();
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							};

							return;
						} else if (request.toLowerCase().startsWith(
								"messagerecieved/")) {
							String gr = request.replace("messageRecieved/", "");

							String uid = gr.split("\\")[0] ;
							String msg = gr.split("\\")[1] ;

							socks.get(uid).SendMessage(msg);

							socket.close();
							return;
						}
					}
				} catch (IOException ioe) {
					System.out.println(ioe);
				}
			}
		});

		df.start();
	}

	String CurMessage;

	protected void SendMessage(String msg) {
		CurMessage = msg;
	}

	public interface RunnableParam {
		public void run(String g);
	}

	static HashMap<String, XmppConnection> socks = new HashMap<String, XmppConnection>();

	public static void CloseServer() {
		try {
			if (server != null)
				server.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static String makeMessage(String message, String type) {

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
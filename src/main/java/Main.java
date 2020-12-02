import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Main {
    static JDA api;

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/discord", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Started web server");

        startBot();
    }

    public static boolean startBot() throws InterruptedException {
        //TODO: Token removed for github
        JDABuilder preBuild = JDABuilder.createDefault("TOKEN HERE");
        try {
            api = preBuild.build();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        api.awaitReady();
        return true;
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String response = "Received";
            httpExchange.sendResponseHeaders(200, response.length());

            //Get params
            String channelName = httpExchange.getRequestHeaders().get("channel").get(0);
            String messageToSend = httpExchange.getRequestHeaders().get("message").get(0);
            String username = httpExchange.getRequestHeaders().get("username").get(0);
            String avatarurl = httpExchange.getRequestHeaders().get("avatarurl").get(0);

            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes());
            os.close();

            sendDiscordMessage(channelName, messageToSend, username, avatarurl);
        }
    }

    public static void sendDiscordMessage(String channelName, String messageToSend, String username, String avatarurl) throws IOException {
        //TODO: Webhook removed for github
        URL url = new URL("WEBHOOK HERE");

        HttpURLConnection con;

        con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        //If you don't put this, it doesn't allow you to get any output/input
        con.setRequestProperty("User-Agent", "Literally Java lol");
        con.setRequestProperty("Content-Type", "application/json");
        //So that we can write to it
        con.setDoOutput(true);

        //Send request
        DataOutputStream wr = new DataOutputStream (
                con.getOutputStream());

        //Construct the webhook response, discord likes it very specific
        wr.writeBytes("{\"content\":\""+ messageToSend + "\",\"embeds\":null,\"username\":\"" + username + "\", \"avatar_url\": \"" + avatarurl + "\"}");
        wr.close();

        //Response
        int status = con.getResponseCode();

        BufferedReader in;
        if (status > 299) {
            in = new BufferedReader(
                    new InputStreamReader(con.getErrorStream()));
        } else {
            in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
        }

        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        in.close();
        con.disconnect();

        System.out.println("[POST Response] " + content.toString());


//        Guild server = api.getGuildById("685606700929384489");
//        TextChannel channel = server.getTextChannelsByName(channelName, true).get(0);
//        channel.sendMessage(messageToSend).queue();
    }

}
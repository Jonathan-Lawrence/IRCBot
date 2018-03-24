package api_ircbot;

public class IRCBotAPIMain {
	public static void main(String[] args) throws Exception{
		String server = "irc.freenode.net";
		String channel = "#maps_weather";
			
		// Now start our bot up.
	        IRCBot bot = new IRCBot();
	        
	    // Enable debugging output.
	        bot.setVerbose(true);
	        
	    // Connect to the IRC server.
	        bot.connect(server);
	        
	    // Join the #spotify_bot channel.
	        bot.joinChannel(channel);
	        bot.setTopic(channel, "Maps and Weather!");
	     
	        
	    //bot.disconnect();
	}
	

	
	
}


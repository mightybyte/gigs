package net.mightybyte.gigs.commands;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import net.mightybyte.gigs.ClientConnection;

public class HelpCommand implements Command {

	@Override
	public void execute(ClientConnection connection, String args) {
		System.out.println(args);
		System.out.println(args.length());
		if (args.length() == 0) {
			writeHelpFile(connection, "help/main");
		} else {
			writeHelpFile(connection, "help/"+args+".txt");
		}
	}

	private void writeHelpFile(ClientConnection connection, String args) {
		try {
			BufferedReader fis = new BufferedReader(new InputStreamReader(
					new FileInputStream(args)));
			String line;
			while ((line = fis.readLine()) != null) {
				connection.writelnToClient(line);
			}
		} catch (FileNotFoundException e) {
			connection.writelnToClient("No help available for that command");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		connection.writeToClientPrompt("");
	}

}

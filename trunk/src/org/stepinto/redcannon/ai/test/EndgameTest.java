package org.stepinto.redcannon.ai.test;

import java.io.*;
import org.stepinto.redcannon.common.*;
import org.stepinto.redcannon.ai.*;
import org.stepinto.redcannon.ai.log.*;

public class EndgameTest {
	public void run(File file) throws InvalidFenFormatException, IOException {
		GameState state = FenParser.parseFile(file);
		SearchEngine engine = new SearchEngine(state);
		engine.addEvaluator(new NaiveEvaluator());
		engine.addSelector(new NaiveSelector());
		
		// engine.setLogger(new SearchLogger(System.out));
		
		System.out.println(file.getAbsolutePath());
		state.getBoard().dump(System.out);
		
		SearchResult result = engine.search();
		result.dump(System.out);
		engine.getStatistics().dump(System.out);
		
		System.exit(0);
	}
	
	public static void main(String args[]) throws Exception {
		File dir = new File("test/endgames");
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith("fen");
			}};
		
		for (File file : dir.listFiles(filter))
			new EndgameTest().run(file);
	}
}

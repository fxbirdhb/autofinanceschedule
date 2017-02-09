package autofinanceschedule.base;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

public class ConfigFile {
	
	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	static public Document getConfig() throws IOException {
		
		Document doc = null;

		Charset charset = Charset.forName("US-ASCII");
		
		Path path = Paths.get(ConfigConstant.CONFIGFILE_DIR);
		
		List<String> lins = Files.readAllLines(path, charset);
		
		if (lins.size() > 0) {
			
			doc = Document.parse(lins.get(0));
			
		}
				
		return doc;
	}
	
	/**
	 * 
	 * @param doc
	 * @throws IOException
	 */
	static public void setConfig(Document doc) throws IOException {
		
		Charset charset = Charset.forName("US-ASCII");
		
		OpenOption[] options = new OpenOption[] {CREATE, APPEND };
		
		Path path = Paths.get(ConfigConstant.CONFIGFILE_DIR);
		
		List<String> lines = new ArrayList<String>();
		
		String line = doc.toJson();
		
		lines.add(line);
		
		Files.write(path, lines, options);
	}

}

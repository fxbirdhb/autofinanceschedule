package autofinanceschedule.log;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import autofinanceschedule.log.logbase;

public class SimpleLog extends logbase {

	private BufferedWriter writer;
	
	public SimpleLog(String dir) throws IOException {
		
		Charset charset = Charset.forName("US-ASCII");
		
		OpenOption[] options = new OpenOption[] {CREATE, APPEND };
		
		Path path = Paths.get(dir);
		
		writer = Files.newBufferedWriter(path, charset, options);
		
	}

	@Override
	public int InsertLog(String log) throws Exception {
		
		int intResult = 0;

		writer.write(log, 0, log.length());
		
		writer.flush();
	
		return intResult;
	}

}

package org.stepinto.redcannon.ucci;

import java.io.IOException;
import java.io.OutputStream;

public class UcciOutputStream extends OutputStream {
	private boolean newLineStart = true; 
	private OutputStream out;
	
	public UcciOutputStream(OutputStream out) {
		this.out = out;
	}
	
	@Override
	public void write(int ch) throws IOException {
		if (newLineStart) {
			out.write('i');
			out.write('n');
			out.write('f');
			out.write('o');
			out.write(' ');
			
			newLineStart = false;
		}
		
		if (ch == '\n')
			newLineStart = true;
		out.write(ch);
	}

}

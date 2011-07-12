package gov.nasa.pds.imaging.generation.generate.elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Checksum implements Element {
	
	private File file;
	
	public Md5Checksum() { }
	
	@Override
	public String getValue(){
		byte[] b = createChecksum();
		String checksum = "";
		for (int i=0; i < b.length; i++) {
			checksum += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
		}
		return checksum;
	}

	private byte[] createChecksum()
	{
		InputStream fis;
		MessageDigest complete = null;
		try {
			fis = new FileInputStream(this.file);


			byte[] buffer = new byte[1024];
			complete = MessageDigest.getInstance("MD5");
			int numRead;
			do {
				numRead = fis.read(buffer);
				if (numRead > 0) {
					complete.update(buffer, 0, numRead);
				}
			} while (numRead != -1);
			fis.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {}
		return complete.digest();
	}
	
	@Override
	public String getUnits() {
		return null;
	}
	
	@Override
	public void setParameters(String filePath) {
		this.file = new File(filePath);
	}
}

package services.filetransfer;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import services.filetransfer.ZipUtils;

public class ZipUtilsTest {
	private Path zipPath;
	private Path dstPath;
	private File dstDir;
	private Path file1;
	private Path file2;
	private Path file3;

	@Before
	public void setUp() {
		zipPath = Paths.get("test/test.zip");
		file1 = Paths.get("test/tmp/test/1.txt");
		file2 = Paths.get("test/tmp/test/2.txt");
		file3 = Paths.get("test/tmp/test/3.txt");

		dstPath = Paths.get("test/tmp");
		dstDir = dstPath.toFile();
		dstDir.mkdirs();
	}

	@After
	public void tearDown() throws IOException {
		zipPath = null;
		FileUtils.deleteDirectory(dstDir);
		dstDir = null;
	}

	@Test
	public void testExtractZip_Ok() throws IOException {
		ZipUtils.extractZip(zipPath, dstPath);

		assertTrue(file1.toFile().exists());
		assertTrue(file2.toFile().exists());
		assertTrue(file3.toFile().exists());
		assertEquals("toto\n", getFileContent(file1));
		assertEquals("toto\n", getFileContent(file2));
		assertEquals("toto\n", getFileContent(file3));
	}
	
	@Test(expected=NullPointerException.class)
	public void testExtractZip_NullDst() throws IOException {
		ZipUtils.extractZip(zipPath, null);
	}

	@Test(expected=NullPointerException.class)
	public void testExtractZip_NullSrc() throws IOException {
		ZipUtils.extractZip(null, dstPath);
	}

	private String getFileContent(Path path) throws IOException{
		byte[] encoded = Files.readAllBytes(path);
		return new String(encoded, Charset.forName("UTF8"));
	}
}

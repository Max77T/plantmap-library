package services.filetransfer;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import services.filetransfer.PathLink;

public class PathLinkTest {
	private Path dst;
	private Path src;
	private Path file1;
	private Path file2;
	private Path md1;
	private Path md2;
	
	@Before
	public void setUp() throws IOException {
		dst = Paths.get("test/dst");
		src = Paths.get("test/src");
		file1 = Paths.get("test/src/1.txt");
		file2 = Paths.get("test/src/2.txt");
		md1 = src.resolve(PathLink.metadataDirectoryName).resolve("1.txt" + PathLink.metadataFileExtension);
		md2 = src.resolve(PathLink.metadataDirectoryName).resolve("2.txt" + PathLink.metadataFileExtension);
		
		md1.getParent().toFile().mkdirs();
		file1.getParent().toFile().mkdirs();
		try(OutputStream os = Files.newOutputStream(file1, 
				StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)){
			os.write("file1".getBytes());
		}
		
		try(OutputStream os = Files.newOutputStream(file2, 
				StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)){
			os.write("file2".getBytes());
		}
		
		try(OutputStream os = Files.newOutputStream(md1, 
				StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)){
			os.write("md1".getBytes());
		}
		
		try(OutputStream os = Files.newOutputStream(md2, 
				StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)){
			os.write("md2".getBytes());
		}
	}

	@After
	public void tearDown() throws IOException {
		FileUtils.deleteDirectory(dst.toFile());
		FileUtils.deleteDirectory(src.toFile());
		dst = null;
		src = null;
		file1 = null;
		file2 = null;
		md1 = null;
		md2 = null;
	}

	@Test(expected=NullPointerException.class)
	public void testCreate_NullMapPath() {
		PathLink.create(null);
	}
	
	@Test
	public void testCreate_Equals() {
		PathLink pl1 = PathLink.create(Paths.get("/test/img.png"));
		PathLink pl2 = PathLink.create(Paths.get("/test/img.png"));
		
		assertEquals(pl1, pl2);
	}
	
	@Test
	public void testCreate_NotEquals() {
		PathLink pl1 = PathLink.create(Paths.get("/test/img.png"));
		PathLink pl2 = PathLink.create(Paths.get("/test/img2.png"));
		
		assertNotEquals(pl1, pl2);
	}
	
	@Test
	public void testCreate_MetadataPath() {
		PathLink pl1 = PathLink.create(Paths.get("/test/img.png"));
		Path metadata = Paths.get("/test/" + PathLink.metadataDirectoryName + "/img.png" + PathLink.metadataFileExtension);
		
		assertEquals(metadata, pl1.getMetadata());
	}
	
	@Test(expected=NullPointerException.class)
	public void testMoveFiles_PathLinksNull() throws IOException {
		PathLink.moveFiles(null, Paths.get("/tmp"));
	}
	
	@Test(expected=NullPointerException.class)
	public void testMoveFiles_DstPathNull() throws IOException {
		PathLink.moveFiles(new ArrayList<PathLink>(), null);
	}
	
	@Test
	public void testMoveFiles_FilesDontExistsInSrc() throws IOException {
		PathLink.moveFiles(Arrays.asList(PathLink.create(file1), PathLink.create(file2)), dst);
		
		assertFalse(file1.toFile().exists());
		assertFalse(file2.toFile().exists());
		assertFalse(md1.toFile().exists());
		assertFalse(md2.toFile().exists());
	}
	
	@Test
	public void testMoveFiles_FilesExistInDst() throws IOException {
		Path file1Dst = dst.resolve("1.txt");
		Path file2Dst = dst.resolve("2.txt");
		Path md1Dst = dst.resolve(PathLink.metadataDirectoryName).resolve("1.txt" + PathLink.metadataFileExtension);
		Path md2Dst = dst.resolve(PathLink.metadataDirectoryName).resolve("2.txt" + PathLink.metadataFileExtension);
		
		PathLink.moveFiles(Arrays.asList(PathLink.create(file1), PathLink.create(file2)), dst);
		
		assertTrue(file1Dst.toFile().exists());
		assertTrue(file2Dst.toFile().exists());
		assertTrue(md1Dst.toFile().exists());
		assertTrue(md2Dst.toFile().exists());
	}
	
	@Test
	public void testMoveFiles_ContentIsOk() throws IOException {
		Path file1Dst = dst.resolve("1.txt");
		Path file2Dst = dst.resolve("2.txt");
		Path md1Dst = dst.resolve(PathLink.metadataDirectoryName).resolve("1.txt" + PathLink.metadataFileExtension);
		Path md2Dst = dst.resolve(PathLink.metadataDirectoryName).resolve("2.txt" + PathLink.metadataFileExtension);
		
		PathLink.moveFiles(Arrays.asList(PathLink.create(file1), PathLink.create(file2)), dst);
		
		assertEquals("file1", getFileContent(file1Dst));
		assertEquals("file2", getFileContent(file2Dst));
		assertEquals("md1", getFileContent(md1Dst));
		assertEquals("md2", getFileContent(md2Dst));
	}
	
	@Test
	public void testMoveFiles_SrcMissing() throws IOException {
		Path file = dst.resolve("3.txt");
		Path md = dst.resolve(PathLink.metadataDirectoryName).resolve("3.txt" + PathLink.metadataFileExtension);
		
		PathLink.moveFiles(Arrays.asList(PathLink.create(file1), PathLink.create(file2)), dst);
		
		assertFalse(file.toFile().exists());
		assertFalse(md.toFile().exists());
	}
	
	private String getFileContent(Path path) throws IOException{
		byte[] encoded = Files.readAllBytes(path);
		return new String(encoded, Charset.defaultCharset());
	}
}

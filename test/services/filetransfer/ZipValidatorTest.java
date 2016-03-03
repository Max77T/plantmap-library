package services.filetransfer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import conf.UploadConfig;
import services.filetransfer.PathLink;
import services.filetransfer.ZipValidator;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ZipValidatorTest {
	private final Path baseDir = Paths.get("test/ZipContentValidator-TestData");

	@Test(expected=NullPointerException.class)
	public void testGetValidZipContent_NullConfig() {
		new ZipValidator(null);
	}
	
	@Test(expected=NullPointerException.class)
	public void testGetValidZipContent_NullPath() throws IOException {
		UploadConfig config = mock(UploadConfig.class);
		new ZipValidator(config).getValidZipContent(null);
	}

	@Test
	public void testGetValidZipContent_Ok() throws IOException {
		// Prepare
		UploadConfig config = mock(UploadConfig.class);
		when(config.getAllowedImgExtensions()).thenReturn(
				Arrays.asList(new String[]{".jpeg", ".png"}));

		ZipValidator zcv = new ZipValidator(config);
		Path pathToCheck = baseDir.resolve("valid");
		
		// Act
		List<PathLink> pathLinks = zcv.getValidZipContent(pathToCheck);

		// Assert
		PathLink expected1 = PathLink.create(pathToCheck.resolve("1.PNG"));
		PathLink expected2 = PathLink.create(pathToCheck.resolve("2.JPEG"));
		assertTrue(pathLinks.contains(expected1));
		assertTrue(pathLinks.contains(expected2));
		assertTrue(pathLinks.size() == 2);
	}
	
	@Test
	public void testGetValidZipContent_NotAllowedExtension() throws IOException {
		// Prepare
		UploadConfig config = mock(UploadConfig.class);
		when(config.getAllowedImgExtensions()).thenReturn(
				Arrays.asList(new String[]{".jpeg"}));

		ZipValidator zcv = new ZipValidator(config);
		Path pathToCheck = baseDir.resolve("valid");
		
		// Act
		List<PathLink> pathLinks = zcv.getValidZipContent(pathToCheck);

		// Assert
		PathLink expected1 = PathLink.create(pathToCheck.resolve("1.PNG"));
		PathLink expected2 = PathLink.create(pathToCheck.resolve("2.JPEG"));
		assertFalse(pathLinks.contains(expected1));
		assertTrue(pathLinks.contains(expected2));
		assertTrue(pathLinks.size() == 1);
	}
	
	@Test
	public void testGetValidZipContent_NoMaps() throws IOException {
		// Prepare
		UploadConfig config = mock(UploadConfig.class);
		when(config.getAllowedImgExtensions()).thenReturn(
				Arrays.asList(new String[]{".jpeg"}));

		ZipValidator zcv = new ZipValidator(config);
		Path pathToCheck = baseDir.resolve("no-maps");
		
		// Act
		List<PathLink> pathLinks = zcv.getValidZipContent(pathToCheck);

		// Assert
		assertTrue(pathLinks.isEmpty());
	}
	
	@Test
	public void testGetValidZipContent_NoMetadataFile() throws IOException {
		// Prepare
		UploadConfig config = mock(UploadConfig.class);
		when(config.getAllowedImgExtensions()).thenReturn(
				Arrays.asList(new String[]{".jpeg"}));

		ZipValidator zcv = new ZipValidator(config);
		Path pathToCheck = baseDir.resolve("no-metadata-file");
		
		// Act
		List<PathLink> pathLinks = zcv.getValidZipContent(pathToCheck);

		// Assert
		assertTrue(pathLinks.isEmpty());
	}

	@Test
	public void testGetValidZipContent_NoMetadataFolder() throws IOException {
		// Prepare
		UploadConfig config = mock(UploadConfig.class);
		when(config.getAllowedImgExtensions()).thenReturn(
				Arrays.asList(new String[]{".jpeg"}));

		ZipValidator zcv = new ZipValidator(config);
		Path pathToCheck = baseDir.resolve("no-metadata-file");
		
		// Act
		List<PathLink> pathLinks = zcv.getValidZipContent(pathToCheck);

		// Assert
		assertTrue(pathLinks.isEmpty());
	}

	@Test
	public void testGetValidZipContent_OneMetadata() throws IOException {
		// Prepare
		UploadConfig config = mock(UploadConfig.class);
		when(config.getAllowedImgExtensions()).thenReturn(
				Arrays.asList(new String[]{".jpeg"}));

		ZipValidator zcv = new ZipValidator(config);
		Path pathToCheck = baseDir.resolve("only-one-metadata");
		
		// Act
		List<PathLink> pathLinks = zcv.getValidZipContent(pathToCheck);

		// Assert
		PathLink expected1 = PathLink.create(pathToCheck.resolve("1.JPEG"));
		PathLink expected2 = PathLink.create(pathToCheck.resolve("2.JPEG"));
		assertTrue(pathLinks.contains(expected1));
		assertFalse(pathLinks.contains(expected2));
		assertTrue(pathLinks.size() == 1);
	}
}

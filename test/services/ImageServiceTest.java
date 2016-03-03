package services;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import conf.UploadConfig;

public class ImageServiceTest {
	private final Path baseDir = Paths.get("test/ImageService-TestData");
	private final Path thumbnailDir = baseDir.resolve("thumb");
	
	@Before
	public void setup(){
		thumbnailDir.toFile().mkdirs();
	}
	
	@After
	public void tearDown(){
		try{
			FileUtils.deleteDirectory(thumbnailDir.toFile());
		}catch(IOException ex){
			// Do nothing on purpose
		}
		
	}
	
	@Test(expected=NullPointerException.class)
	public void testImageService_NullConfig() {
		new ImageServiceImpl(null);
	}
	
	@Test(expected=NullPointerException.class)
	public void testGetThumbnail_NullPattern() throws IOException {
		UploadConfig config = mock(UploadConfig.class);
		new ImageServiceImpl(config).getThumbnail(ThumbnailSize.BIG, null);
	}
	
	@Test
	public void testGetThumbnail_BigThumbnailCreation_Ok() throws IOException {
		// Prepare
		UploadConfig config = mock(UploadConfig.class);
		when(config.getLibraryThumbnailDir()).thenReturn(thumbnailDir);
		when(config.getLibraryStoreDir()).thenReturn(baseDir);
		
		String pattern = "no-thumbnail/1.JPEG";
		
		// Act
		new ImageServiceImpl(config).getThumbnail(ThumbnailSize.BIG, pattern);
		
		// Assert
		assertTrue(thumbnailDir.resolve("BIG/no-thumbnail/1.JPEG").toFile().exists());
	}
	
	@Test
	public void testGetThumbnail_SmallThumbnailCreation_Ok() throws IOException {
		// Prepare
		UploadConfig config = mock(UploadConfig.class);
		when(config.getLibraryThumbnailDir()).thenReturn(thumbnailDir);
		when(config.getLibraryStoreDir()).thenReturn(baseDir);
		
		String pattern = "no-thumbnail/1.JPEG";
		
		// Act
		new ImageServiceImpl(config).getThumbnail(ThumbnailSize.SMALL, pattern);
		
		// Assert
		assertTrue(thumbnailDir.resolve("SMALL/no-thumbnail/1.JPEG").toFile().exists());
	}
}

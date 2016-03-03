package models.users;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import javax.persistence.PersistenceException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import play.test.FakeApplication;
import play.test.Helpers;

public class UserTest {
	private static FakeApplication app;

	@BeforeClass
	public static void startApp() {
		app = Helpers.fakeApplication(Helpers.inMemoryDatabase());
		Helpers.start(app);
		
		UserEntity.find.all().stream().forEach(u -> u.delete());
		RoleEntity.find.all().stream().forEach(r -> r.delete());
		
		RoleEntity roleUser = new RoleEntity("User");
		roleUser.save();
		
		RoleEntity roleGeom = new RoleEntity("Geomatician");
		roleGeom.save();
		
		RoleEntity roleAdmin = new RoleEntity("Admin");
		roleAdmin.save();
	}

	@AfterClass
	public static void stopApp() {
		UserEntity.find.all().stream().forEach(u -> u.delete());
		RoleEntity.find.all().stream().forEach(r -> r.delete());
		
		Helpers.stop(app);
	}
	
	@After
	public void tearDown(){
		UserEntity.find.all().stream().forEach(u -> u.delete());
	}
	
	@Test
	public void testCreate_Ok(){
		UserEntity user = UserEntity.create("rom", "1234", "e@mail.com", Arrays.asList(Role.User));
	
		assertTrue(user.id > 0);
		assertTrue("rom".equals(user.login));
		assertFalse(user.password.isEmpty());
		assertTrue("e@mail.com".equals(user.email));
		assertTrue(user.roles.size() == 1);
	}
	
	@Test
	public void testCreate_Ok_MultiRoles(){
		UserEntity user = UserEntity.create("rom", "1234", "e@mail.com", Arrays.asList(Role.User, Role.Geomatician));
	
		assertTrue(user.id > 0);
		assertTrue("rom".equals(user.login));
		assertFalse(user.password.isEmpty());
		assertTrue("e@mail.com".equals(user.email));
		assertTrue(user.roles.size() == 2);
	}
	
	@Test
	public void testCreate_Ok_NoRoles(){
		UserEntity user = UserEntity.create("rom", "1234", "e@mail.com", new ArrayList<>());
	
		assertTrue(user.id > 0);
		assertTrue("rom".equals(user.login));
		assertFalse(user.password.isEmpty());
		assertTrue("e@mail.com".equals(user.email));
		assertTrue(user.roles.size() == 0);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testCreate_Nok_EmptyLogin(){
		UserEntity.create("", "1234", "e@mail.com", Arrays.asList(Role.User));
	}
	
	@Test(expected=NullPointerException.class)
	public void testCreate_Nok_NullLogin(){
		UserEntity.create(null, "1234", "e@mail.com", Arrays.asList(Role.User));
	}
	
	@Test(expected=NullPointerException.class)
	public void testCreate_Nok_NullPassword(){
		UserEntity.create("rom", null, "e@mail.com", Arrays.asList(Role.User));
	}
	
	@Test(expected=NullPointerException.class)
	public void testCreate_Nok_NullEmail(){
		UserEntity.create("rom", "rrr", null, Arrays.asList(Role.User));
	}
	
	@Test(expected=NullPointerException.class)
	public void testCreate_Nok_NullRoles(){
		UserEntity.create("rom", "rrr", "fzegz", null);
	}
	
	@Test
	public void testCreate_PasswordEncryption(){
		UserEntity user = UserEntity.create("rom", "1234", "e@mail.com", Arrays.asList(Role.User));
		
		assertFalse(user.password.isEmpty());
		assertFalse("1234".equals(user.password));
	}
	
	@Test(expected=PersistenceException.class)
	public void testCreate_Nok_SameLogin(){
		UserEntity.create("rom", "1234", "e@mail.com", Arrays.asList(Role.User));
		UserEntity.create("rom", "1235", "u@mail.com", Arrays.asList(Role.User));
	}
	
	@Test
	public void testGetUser_Ok(){
		UserEntity.create("rom", "1234", "rom@mail.com", Arrays.asList(Role.User));
		UserEntity.create("toto", "1234", "toto@mail.com", Arrays.asList(Role.User));
		
		Optional<UserEntity> userOpt = UserEntity.getUser("rom");
		
		assertTrue(userOpt.isPresent());
		assertTrue("rom".equals(userOpt.get().login));
		assertTrue("rom@mail.com".equals(userOpt.get().email));
	}
	
	@Test
	public void testGetUser_Nok_UnexistentLogin(){
		UserEntity.create("rom", "1234", "rom@mail.com", Arrays.asList(Role.User));
		UserEntity.create("toto", "1234", "toto@mail.com", Arrays.asList(Role.User));
		
		Optional<UserEntity> userOpt = UserEntity.getUser("jo");
		
		assertFalse(userOpt.isPresent());
	}

	@Test
	public void testGetUser_Nok_NullLogin(){
		UserEntity.create("rom", "1234", "rom@mail.com", Arrays.asList(Role.User));
		UserEntity.create("toto", "1234", "toto@mail.com", Arrays.asList(Role.User));
		
		Optional<UserEntity> userOpt = UserEntity.getUser(null);
		
		assertFalse(userOpt.isPresent());
	}
	
	@Test
	public void testAuthenticate_Nok_UnexistentLogin(){
		UserEntity.create("rom", "1234", "rom@mail.com", Arrays.asList(Role.User));
		UserEntity.create("toto", "1234", "toto@mail.com", Arrays.asList(Role.User));
		
		Optional<UserEntity> userOpt = UserEntity.authenticate("jo", "1234");
		
		assertFalse(userOpt.isPresent());
	}
	
	@Test
	public void testAuthenticate_Nok_NullLogin(){
		UserEntity.create("rom", "1234", "rom@mail.com", Arrays.asList(Role.User));
		UserEntity.create("toto", "1234", "toto@mail.com", Arrays.asList(Role.User));
		
		Optional<UserEntity> userOpt = UserEntity.authenticate(null, "1234");
		
		assertFalse(userOpt.isPresent());
	}

	@Test
	public void testAuthenticate_Nok_NullPassword(){
		UserEntity.create("rom", "1234", "rom@mail.com", Arrays.asList(Role.User));
		UserEntity.create("toto", "1234", "toto@mail.com", Arrays.asList(Role.User));
		
		Optional<UserEntity> userOpt = UserEntity.authenticate("rom", null);
		
		assertFalse(userOpt.isPresent());
	}
	
	@Test
	public void testAuthenticate_Nok_BadPassword(){
		UserEntity.create("rom", "1234", "rom@mail.com", Arrays.asList(Role.User));
		UserEntity.create("toto", "1234", "toto@mail.com", Arrays.asList(Role.User));
		
		Optional<UserEntity> userOpt = UserEntity.authenticate("rom", "1235");
		
		assertFalse(userOpt.isPresent());
	}
	
	@Test
	public void testAuthenticate_Ok(){
		UserEntity.create("rom", "1234", "rom@mail.com", Arrays.asList(Role.User));
		UserEntity.create("toto", "1234", "toto@mail.com", Arrays.asList(Role.User));
		
		Optional<UserEntity> userOpt = UserEntity.authenticate("rom", "1234");
		
		assertTrue(userOpt.isPresent());
		assertTrue("rom".equals(userOpt.get().login));
		assertTrue("rom@mail.com".equals(userOpt.get().email));
	}
	
	@Test
	public void testIsUserInRole_Ok_TwoRoles(){
		UserEntity.create("rom", "1234", "rom@mail.com", Arrays.asList(Role.User, Role.Admin));
		
		assertTrue(UserEntity.isInRole("rom", "Admin"));
		assertTrue(UserEntity.isInRole("rom", "User"));
		assertFalse(UserEntity.isInRole("rom", "Geomatician"));
	}
	
	@Test
	public void testIsUserInRole_Ok_OneRole(){
		UserEntity.create("rom", "1234", "rom@mail.com", Arrays.asList(Role.User));
		
		assertFalse(UserEntity.isInRole("rom", "Admin"));
		assertTrue(UserEntity.isInRole("rom", "User"));
		assertFalse(UserEntity.isInRole("rom", "Geomatician"));
	}
	
	@Test
	public void testIsUserInRole_Nok_NoRole(){
		UserEntity.create("rom", "1234", "rom@mail.com", new ArrayList<>());
		
		assertFalse(UserEntity.isInRole("rom", "Admin"));
		assertFalse(UserEntity.isInRole("rom", "User"));
		assertFalse(UserEntity.isInRole("rom", "Geomatician"));
	}

	@Test
	public void testIsUserInRole_Nok_NullRole(){
		UserEntity.create("rom", "1234", "rom@mail.com", Arrays.asList(Role.User));
		
		assertFalse(UserEntity.isInRole("rom", null));
	}
	
	@Test
	public void testIsUserInRole_Nok_NullLogin(){
		UserEntity.create("rom", "1234", "rom@mail.com", Arrays.asList(Role.User));
		
		assertFalse(UserEntity.isInRole(null, "Admin"));
	}
}

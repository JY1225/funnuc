package eu.robojob.irscw.db;

import org.junit.Before;
import org.junit.Test;

public class DatabaseMapperTest {

	private DatabaseMapper dbMapper;
	
	@Before
	public void setup() {
		dbMapper = DatabaseMapper.getInstance();
	}
	
	@Test
	public void testConnection() {
		dbMapper.logTestContent();
	}
}

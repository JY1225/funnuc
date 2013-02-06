package eu.robojob.irscw.db.external.device;

import java.sql.SQLException;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import eu.robojob.irscw.db.GeneralMapper;
import eu.robojob.irscw.db.external.util.ConnectionMapper;
import eu.robojob.irscw.external.device.AbstractDevice;

public class DeviceMapperTest {

	private DeviceMapper deviceMapper;
	
	@Before
	public void setup() {
		deviceMapper = new DeviceMapper(new GeneralMapper(), new ConnectionMapper());
	}
	
	@Test
	public void testGetAllDevices() {
		try {
			Set<AbstractDevice> devices = deviceMapper.getAllDevices();
			for (AbstractDevice device : devices) {
				System.out.println(device.toString());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}

package com.asvkin.ajira.service.graphs;

import com.asvkin.ajira.beans.Device;
import com.asvkin.ajira.exception.AlreadyExistsException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({DeviceGraph.class, GraphService.class,})
public class GraphServiceTest {
	@Mock
	DeviceGraph deviceGraph;
	@InjectMocks
	private GraphService graphService;
	
	@Before
	public void setup() {
		Whitebox.setInternalState(graphService, "graph", deviceGraph);
	}
	
	@Test(expected = AlreadyExistsException.class)
	public void whenAddNewDeviceThenhasVertexTrueReturnAlreadyExistsException() {
		Mockito.when(deviceGraph.hasVertex(createNewDevice())).thenReturn(true);
		graphService.addNewDevice(createNewDevice());
	}
	
	@Test
	public void whenGraphNewDevice() {
		Mockito.when(deviceGraph.hasVertex(createNewDevice())).thenReturn(false);
		graphService.addNewDevice(createNewDevice());
		Mockito.verify(deviceGraph).addVertex(createNewDevice());
	}
	
	private Device createNewDevice() {
		Device device = new Device();
		device.setName("A1");
		device.setType("COMPUTER");
		return device;
	}
	
}
package eu.robojob.irscw.ui.configure.process;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javafx.application.Platform;

import org.apache.log4j.Logger;

import eu.robojob.irscw.PropertiesProcessFlowFactory;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.ui.configure.AbstractFormPresenter;
import eu.robojob.irscw.ui.configure.ConfigurePresenter;

public class ProcessOpenPresenter extends AbstractFormPresenter<ProcessOpenView, ProcessMenuPresenter> {
	
	private static final Logger logger = Logger.getLogger(ProcessOpenPresenter.class);
	private ProcessFlow processFlow;
	private PropertiesProcessFlowFactory propertiesProcessFlowFactory;
	
	private static final String DEMO_1_URL = "C:\\RoboJob\\demo1.properties";
	private static final String DEMO_2A_URL = "C:\\RoboJob\\demo2a.properties";
	private static final String DEMO_2B_URL = "C:\\RoboJob\\demo2b.properties";
		
	public ProcessOpenPresenter(ProcessOpenView view, ProcessFlow propcessFlow, PropertiesProcessFlowFactory propertiesProcessFlowFactory) {
		super(view);
		this.processFlow = propcessFlow;
		this.propertiesProcessFlowFactory = propertiesProcessFlowFactory;
	}

	@Override
	public void setPresenter() {
		view.setPresenter(this);
	}

	@Override
	public boolean isConfigured() {
		return false;
	}
	
	public void openProcess(final String processId) {
		logger.info("loading process: " + processId);
		if (processId.equals(ProcessOpenView.DEMO_1)) {
			Properties properties = new Properties();
			try {
				properties.load(new FileInputStream(new File(DEMO_1_URL)));
				processFlow.loadFromOtherProcessFlow(propertiesProcessFlowFactory.loadProcessFlow(properties));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (processId.equals(ProcessOpenView.DEMO_2_A)) {
			Properties properties = new Properties();
			try {
				properties.load(new FileInputStream(new File(DEMO_2A_URL)));
				processFlow.loadFromOtherProcessFlow(propertiesProcessFlowFactory.loadProcessFlow(properties));
			}catch (IOException e) {
				e.printStackTrace();
			}
		} else if (processId.equals(ProcessOpenView.DEMO_2_B)) {
			Properties properties = new Properties();
			try {
				properties.load(new FileInputStream(new File(DEMO_2B_URL)));
				processFlow.loadFromOtherProcessFlow(propertiesProcessFlowFactory.loadProcessFlow(properties));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		menuPresenter.processOpened();
	}
}

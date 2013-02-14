package com.mindscapehq.raygun4java.messages;

import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.Locale;

import javax.activity.ActivityCompletedException;

public class RaygunEnvironmentMessage {

	private String cpu;
	private int processorCount;
	private String osVersion;
	private int windowBoundsWidth;
	private int windowBoundsHeight;
	private String currentOrientation;
	private String locale;
	private long totalPhysicalMemory;
	private long availablePhysicalMemory;
	private long totalVirtualMemory;
	private long availableVirtualMemory;
	private int diskSpaceFree;
	private String architecture;
	
	public RaygunEnvironmentMessage()
	{
		try {
			OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();
			architecture = osMXBean.getArch();		
			osVersion = osMXBean.getVersion();		
			processorCount = Runtime.getRuntime().availableProcessors();
			totalVirtualMemory = Runtime.getRuntime().totalMemory();
			availableVirtualMemory = Runtime.getRuntime().freeMemory();
			windowBoundsWidth = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width;
			windowBoundsHeight = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;
			locale = Locale.getDefault().getLanguage() + "-" + Locale.getDefault().getCountry();	
		} catch (Exception e) { 
		}
			
	}
	
}

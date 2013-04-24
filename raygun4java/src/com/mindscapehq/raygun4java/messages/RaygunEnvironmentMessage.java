package com.mindscapehq.raygun4java.messages;

import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.TimeZone;
import java.util.Locale;

import javax.activity.ActivityCompletedException;

public class RaygunEnvironmentMessage {

	private String cpu;
	private String architecture;
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
	private double utcOffset;
	
	public RaygunEnvironmentMessage()
	{
		try {			
			OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();
			com.sun.management.OperatingSystemMXBean sunMxBean = (com.sun.management.OperatingSystemMXBean) osMXBean;
			
			architecture = sunMxBean.getArch();				
			processorCount = Runtime.getRuntime().availableProcessors();
			
			windowBoundsWidth = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width;
			windowBoundsHeight = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;
			locale = Locale.getDefault().getLanguage() + "-" + Locale.getDefault().getCountry();	
			
			utcOffset = TimeZone.getDefault().getRawOffset() / 3600000.0;
			
			availablePhysicalMemory = sunMxBean.getFreePhysicalMemorySize();
			totalPhysicalMemory = sunMxBean.getTotalPhysicalMemorySize();
			totalVirtualMemory = sunMxBean.getTotalSwapSpaceSize();
			availableVirtualMemory = sunMxBean.getFreeSwapSpaceSize();
			
			processorCount = sunMxBean.getAvailableProcessors();
			
			// This to be refactored when we have a Map to put the info into
			osVersion = sunMxBean.getName() + " - " +sunMxBean.getVersion();
			
		} catch (Exception e) { 
		}
			
	}
	
}

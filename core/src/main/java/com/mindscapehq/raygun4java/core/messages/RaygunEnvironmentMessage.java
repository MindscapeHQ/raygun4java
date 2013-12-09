package com.mindscapehq.raygun4java.core.messages;

import java.awt.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Logger;

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
    try
    {
      utcOffset = TimeZone.getDefault().getRawOffset() / 3600000.0;
      locale = Locale.getDefault().getLanguage() + "-" + Locale.getDefault().getCountry();
      windowBoundsWidth = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width;
      windowBoundsHeight = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;

      processorCount = Runtime.getRuntime().availableProcessors();

      MemoryMXBean memBean = ManagementFactory.getMemoryMXBean();

      totalVirtualMemory = memBean.getHeapMemoryUsage().getMax() + memBean.getNonHeapMemoryUsage().getMax();
      availableVirtualMemory = memBean.getHeapMemoryUsage().getUsed() + memBean.getNonHeapMemoryUsage().getUsed();

      OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();
      architecture = osMXBean.getArch();
      osVersion = osMXBean.getName() + " - " + osMXBean.getVersion();
    } catch (Throwable t)
    {
      Logger.getLogger("Raygun4Java").warning("Couldn't access all environment data. If you are running in GAE or a restricted environment this is expected");
    }
	}
}

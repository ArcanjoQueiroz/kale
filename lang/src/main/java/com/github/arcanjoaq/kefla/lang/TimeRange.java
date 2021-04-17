package com.github.arcanjoaq.kefla.lang;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Preconditions;

public class TimeRange implements Serializable {

  private static final long serialVersionUID = -5361842668003969130L;
 
  private final LocalTime startTime;
  private final LocalTime endTime;
 
  private final Logger logger = LoggerFactory.getLogger(TimeRange.class);
 
  private TimeRange(final LocalTime startTime, final LocalTime endTime) {
    this.startTime = startTime;
    this.endTime = endTime;
  }
  
  public static TimeRange of(final LocalTime startTime, final LocalTime endTime) {
    Preconditions.checkArgument(startTime != null);
    Preconditions.checkArgument(endTime != null);
    return new TimeRange(startTime, endTime);
  }
  
  public boolean isBetween(final LocalTime time) {
    if (time.equals(startTime) || time.equals(endTime)) {
      logger.debug("Time {} is between [{}...{}]", time, startTime, endTime);  
      return true;
    }
    
    final LocalDateTime betweenDateTime = LocalDate.now().atTime(time);
    
    final LocalDateTime startDateTime = LocalDate.now().atTime(startTime);
    
    LocalDateTime endDateTime;
    if (endTime.isBefore(startTime)) {
      endDateTime = LocalDate.now().plusDays(1).atTime(endTime);  
    } else {
      endDateTime = LocalDate.now().atTime(endTime);
    }    
    
    boolean isBetween;
    if (startTime.isBefore(endTime)) {
      isBetween = betweenDateTime.isAfter(startDateTime) 
          && betweenDateTime.isBefore(endDateTime);
    } else {
      isBetween = betweenDateTime.isAfter(startDateTime) 
          && betweenDateTime.isBefore(endDateTime)
          ||
          betweenDateTime.plusDays(1).isAfter(startDateTime) 
          && betweenDateTime.plusDays(1).isBefore(endDateTime);
    }
    if (isBetween) {
      logger.debug("Time {} is between [{}...{}]", time, startTime, endTime);  
    } else {
      logger.debug("Time {} is not between [{}...{}]", time, startTime, endTime);
    }    
    return isBetween;
    
  }
}


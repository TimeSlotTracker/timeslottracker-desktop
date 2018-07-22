package net.sf.timeslottracker.idledetector;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.win32.StdCallLibrary;

/*
 * JPPF.
 * Copyright (C) 2005-2014 JPPF Team.
 * http://www.jppf.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * Instances of this class provide the computer idle time on a Windows system.
 * 
 * @author Laurent Cohen
 */
/**
 * Source file changed for using in TST
 * 
 * @author cnitsa
 */
public class Win32IdleTime {

  public interface Kernel32n extends StdCallLibrary {
    Kernel32 INSTANCE = (Kernel32) Native.loadLibrary("kernel32",
        Kernel32.class);

    /**
     * Retrieves the number of milliseconds that have elapsed since the system
     * was started.
     * 
     * @see http://msdn2.microsoft.com/en-us/library/ms724408.aspx
     * @return number of milliseconds that have elapsed since the system was
     *         started.
     */
    public int GetTickCount();
  };

  /**
   * Get the amount of milliseconds that have elapsed since the last input event
   * (mouse or keyboard)
   * 
   * @return idle time in milliseconds
   */
  public static int getIdleTimeMillis() {

    User32.LASTINPUTINFO lastInputInfo = new User32.LASTINPUTINFO();
    User32.INSTANCE.GetLastInputInfo(lastInputInfo);
    return Kernel32.INSTANCE.GetTickCount() - lastInputInfo.dwTime;
  }

  enum State {
    UNKNOWN, ONLINE, IDLE, AWAY
  };

}

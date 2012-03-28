/*
 *  Aprof - Java Memory Allocation Profiler
 *  Copyright (C) 2002-2012  Devexperts LLC
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.devexperts.aprof;

import com.devexperts.aprof.util.Log;

/**
 * @author Roman Elizarov
 */
class DumpPeriodicThread extends Thread {
	private static final long SLEEP_TIME = 1000;

	private final Dumper dumper;
	private final long time;

	public DumpPeriodicThread(Dumper dumper, long time) {
		super("AProfDump-Periodic");
		setDaemon(true);
		setPriority(Thread.MAX_PRIORITY);
		this.dumper = dumper;
		this.time = time;
	}

	@Override
	public void run() {
        while (true) {
            try {
                long wait_dump = time;
                //noinspection InfiniteLoopStatement
                while (true) {
                    Thread.sleep(SLEEP_TIME);
                    if (time > 0 && (wait_dump -= SLEEP_TIME) <= 0) {
                        dumper.makeDump(false);
                        wait_dump = time;
                    } else if (AProfRegistry.isOverflowThreshold()) {
                        dumper.makeOverflowSnapshot();
                    }
                }
            } catch (InterruptedException e) {
                // thread dies
                e.printStackTrace();
                Log.out.print(getName() + " was interrupted");
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}
}
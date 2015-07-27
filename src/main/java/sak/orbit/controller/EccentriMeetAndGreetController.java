/**
 *
 */
package sak.orbit.controller;

import java.io.File;

import sak.orbit.core.Memory;

/**
 * @author andreas
 */
public class EccentriMeetAndGreetController extends MeetAndGreetController {

	/**
	 * @param configuration
	 *            supports 3001-3004
	 */
	public EccentriMeetAndGreetController(final int configuration) {
		super(new File("src/vms/bin3.obf"), configuration);
	}

	@Override
	void writeInput(final Memory memory) {
		memory.setInputPort(2, 0);
		memory.setInputPort(3, 0);
	}
}

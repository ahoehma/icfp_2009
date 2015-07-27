package sak.orbit.ui;

import sak.orbit.controller.EccentriMeetAndGreetController;

/**
 * @author andreas
 */
public class EccentriMeetAndGreetOrbitAnimator extends
		MeetAndGreetOrbitAnimator {

	/**
	 * The application entry point
	 * 
	 * @param args
	 *            the command line arguments
	 */
	public static void main(final String[] args) {
		new EccentriMeetAndGreetOrbitAnimator().run();
	}

	/**
	 * 
	 */
	public EccentriMeetAndGreetOrbitAnimator() {
		super(new EccentriMeetAndGreetController(3001), new int[] { 3001, 3002,
				3003, 3004 });
	}
}
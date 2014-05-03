package nachos.threads;

import nachos.machine.*;

/**
 * A <i>communicator</i> allows threads to synchronously exchange 32-bit
 * messages. Multiple threads can be waiting to <i>speak</i>, and multiple
 * threads can be waiting to <i>listen</i>. But there should never be a time
 * when both a speakerCount and a listenerCountCount are waiting, because the two threads can
 * be paired off at this point.
 */
public class Communicator {
	/**
	 * Allocate a new communicator.
	 */
	
	private static final char flag = 'f';  	// used for debugging
	private Condition speakerDoorman;			// Lock mechanism for the speakerCount.
	private Condition listenerDoorMan;			// Lock mechanism for the listenerCountCount
	private int message;										
	private Lock doorLock; 						// we need the lock mechanism
	private int speakerCount;					// count the number of speakers waiting for this lock. 
	private int listenerCount;				// count the number of listenerCountCounts waiting for the lock
	
	/*Class Constructor*/ 
	public Communicator() {
		Lib.debug(flag, "Declaring communicator object");
		speakerCount = 0;		// reset counts							
		listenerCount = 0;					
		doorLock = new Lock();	// instantiate lock 
		listenerDoorMan = new Condition(doorLock);		//  Allocate a new condition variables.
		speakerDoorman = new Condition(doorLock);
	}

	/**
	 * Wait for a thread to listen through this communicator, and then transfer
	 * <i>word</i> to the listener.
	 * 
	 * <p>
	 * Does not return until this thread is paired up with a listening thread.
	 * Exactly one listener should receive <i>word</i>.
	 * 
	 * @param word the integer to transfer.
	 */

	/*Description: when a process calls the speak method:
	 * 					1. waits till it gets a lock.
	 * 					2. sleeps until a listener shows up
	 * 					3. sends out the 
	 * */
	public void speak(int word) {
		Lib.debug(flag, "speak called");
			
		speakerCount++;				// increment speaker
		doorLock.acquire();			// try to acquire lock, gets blocked if the lock is busy
		message = word;	
		
		
		if(listenerCount==0){	// if there are no listeners go to sleep.
			Lib.debug(flag, "waiting for ");
			listenerDoorMan.sleep();		
		}
		
		listenerCount--;			// we pick up a listener, check him off
				
		speakerDoorman.wake();		//Wake up at most one thread sleeping on this condition variable.
		doorLock.release(); 		// Atomically release this lock, allowing other threads to acquire it.
	}

	/**
	 * Wait for a thread to speak through this communicator, and then return the
	 * <i>word</i> that thread passed to <tt>speak()</tt>.
	 * 
	 * @return the integer transferred.
	 */
	
	public int listen() {
		Lib.debug(flag, "listen called");
		doorLock.acquire();			// try to acquire lock, gets blocked if the lock is busy
		listenerCount++;
		listenerDoorMan.wake();			//Wake up at most one thread sleeping on this condition variable.

		if(speakerCount==0){			// while there isnt any speakers,  
			speakerDoorman.sleep();		// Atomically release the associated lock and go to 
			//sleep on this condition variable until another thread wakes it using wake().
		}
		speakerCount--;
		doorLock.release();				// release the lock for next process
		return message;					// return the word that the speaker stored.
	}
}

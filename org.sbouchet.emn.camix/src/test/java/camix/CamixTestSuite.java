package camix;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Classe de suite de tests unitaire pour Camix.
 *
 * @version 3.0
 * @author Matthias Brun
 *
 */
public class CamixTestSuite
{

	/**
	 * Suite de tests pour Camix.
	 *
	 * @return la suite de tests.
	 *
	 * @see junit.framework.TestSuite
	 *
	 */	
	public static Test suite() 
	{
		final Class<?>[] classesTest = {
			// TODO : insérer ici les classes de test. 
			CanalChatTestSimple.class,
			CanalChatTest.class,
			ServiceChatTest.class
		};

		final TestSuite suite = new TestSuite(classesTest);

		return suite;
	}

}


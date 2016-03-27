package camix;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Classe de tests unitaires de la classe CanalChat.
 *
 * @version 3.0
 * @author Matthias Brun
 *
 */
public class CanalChatTest extends TestCase
{
	/**
	 * Canal à tester.
	 */
	private CanalChat canal;

	/**
	 * Crée le client nécessaire aux tests.
	 *
	 * <p>Code exécuté avant les tests.</p>
	 *
	 * @throws Exception toute exception.
	 *
	 */
	@Before
	public void setUp() throws Exception
	{
		/* Création du canal à tester. */
		this.canal = new CanalChat("canal test");
	}

	/**
	 * Non implanté.
	 *
	 * <p>Code exécuté après les tests.</p>
	 *
	 * @throws Exception toute exception.
	 *
	 */
	@After
	public void tearDown() throws Exception 
	{
		/* rien à faire */
	}

	/**
	 * Pré-condition d'existence du canal à tester.
	 */
	@Test
	public void testPreconditions() 
	{
		/* Assertions. */
		assertNotNull(this.canal);
	}

	/**
	 * Création d'un mock de client de chat.
	 * 
	 * <p>Le mock possède en sortie le comportement attendu pour une insertion dans un canal.</p>
	 * 
	 * @param id un identifiant du client.
	 * @return le mock d'un client du chat prêt pour une insertion dans un canal.
	 */
	private ClientChat creerMockClientChat(String id)
	{
		/* Création d'un mock de client du chat. */
		final ClientChat clientMock = EasyMock.createMock(ClientChat.class);

		/* Description du comportement du mock du client.                           */
		/* Le mock du client est sollicité trois fois pour en connaître la socket : */
		/* - deux fois pour l'insertion ;                                           */ 
		/* - une fois pour l'assert de présence dans le canal.                      */
		final int repetitions = 3;
		
		EasyMock.expect(
				clientMock.donneId()
				).andReturn(
						id
			);
		
		EasyMock.expectLastCall().times(repetitions);

		/* Chargement du comportement du mock du client. */
		EasyMock.replay(clientMock);
		
		return clientMock;
	}
	
	/**
	 * Insertion d'un mock d'un client dans le canal.
	 * 
	 * @param clientMock le mock du client à insérer dans le canal.
	 * @param nombreClients nombre de clients dans le canal <i>avant</i> insertion.
	 * 
	 */
	private void insertionCanalMockClientChat(ClientChat clientMock, int nombreClients)
	{
		/* Exécution de la méthode d'ajout d'un client dans le canal. */
		this.canal.ajouteClient(clientMock);

		/* Assertions. */
		assertEquals("Nombre de clients incompatible", nombreClients + 1, (int) this.canal.donneNombreClients());
		assertTrue("Le client n'est pas dans le canal", this.canal.estPresent(clientMock));

		/* Vérification du comportement du mock. */
		EasyMock.verify(clientMock);
	}
	
	/**
	 * Teste l'ajout d'un client non présent dans le canal.
	 * 
	 * <p>Méthode concernée : public void ajouteClient(IClientChat client)</p>
	 *
	 */
	@Test
	public void testAjouteClient_nonpresent()
	{	
		/* Création d'un identifiant pour le client. */
		final String id = "clientIdAjouteClientNonPresent";
		
		/* Création du mock du client prêt pour une insertion dans un canal. */
		final ClientChat clientMock = this.creerMockClientChat(id);

		/* Ajout d'un client non-présent dans le canal. */
		this.insertionCanalMockClientChat(clientMock, 0);
	}

	/**
	 * Teste l'ajout d'un client déjà présent dans le canal.
	 * 
	 * <p>Méthode concernée : public void ajouteClient(IClientChat client)</p>
	 *
	 */
	@Test
	public void testAjouteClient_present()
	{
		/* Création d'un identifiant pour le client. */
		final String id = "clientIdAjouteClientPresent";
		
		/* Création du mock du client prêt pour une insertion dans un canal. */
		final ClientChat clientMock = this.creerMockClientChat(id);

		/* 
		 * Séquence de mise en précondition du test. 
		 */
		this.insertionCanalMockClientChat(clientMock, 0);

		/* Réinitalisation du mock du client. */
		EasyMock.reset(clientMock);
		
		/* 
		 * Séquence de test. 
		 */
		
		/* Description du comportement du mock pour une seconde insertion dans le canal. */
		/* Le mock du client est sollicité deux fois pour en connaître la socket :       */
		/* - une fois pour l'insertion ;                                                 */ 
		/* - une fois pour l'assert de présence dans le canal.                           */
		final int repetitions = 2;
		
		EasyMock.expect(
				clientMock.donneId()
				).andReturn(
						id
			);
		
		EasyMock.expectLastCall().times(repetitions);

		/* Chargement du comportement du mock du client. */
		EasyMock.replay(clientMock);
    	
		/* Seconde insertion du client dans le canal (méthode à tester). */
		this.canal.ajouteClient(clientMock);

		/* Assertions. */
		assertEquals("Nombre de client incompatible après seconde insertion", 1, (int) this.canal.donneNombreClients());
		assertTrue("Le client n'est pas dans le canal après seconde insertion", this.canal.estPresent(clientMock));

		/* Vérification du comportement du mock. */
		EasyMock.verify(clientMock);
	}
	
	/**
	 * Test la méthode d'envoie de messages aux clients du canal.
	 * 
	 * <p>Méthode concernée : public void envoieClients(String message) </p>
	 */
	@Test
	public void testEnvoieClients()
	{
		/* Le message de test. */
		final String message = "Message de test.";
		
		/* Le nombre de clients dans le canal. */
		final Integer nbClients = 15;
		
		/* Ensemble des identifiants client pour les mocks des clients. */
		final String[] id = new String[nbClients];
		
		/* Ensemble de mocks de clients prêt pour une insertion dans un canal. */
		final ClientChat[] clientsMock = new ClientChat[nbClients];

		/* 
		 * Séquence de mise en précondition du test. 
		 */
		
		/* Création des mocks des clients prêt pour une insertion dans un canal       */
		/* et insertion des mocks des clients dans le canal (pré-conditions au test) .*/
		for (int i = 0; i < nbClients; i++) {
			id[i] = "clientIdEnvoieClient" + i;
			clientsMock[i] = this.creerMockClientChat(id[i]);
			this.insertionCanalMockClientChat(clientsMock[i], i);
			
			/* Réinitalisation du mock du client inséré dans le canal (pour la suite des tests). */
			EasyMock.reset(clientsMock[i]);
		}

		/* 
		 * Séquence de test. 
		 */
		
		/* Description des comportements des mocks des clients pour l'envoie d'un message */
		/* (les mocks sont sollicités une fois chacun pour envoyer un message au client)  */
		/* et chargement des mocks.                                                       */
		for (int i = 0; i < nbClients; i++) {
			clientsMock[i].envoieMessage(message);
			EasyMock.replay(clientsMock[i]);
		}
		
		/* Méthode à tester. */
		this.canal.envoieClients(message);
		
		/* Vérification du comportement des mocks. */
		for (int i = 0; i < nbClients; i++) {
			EasyMock.verify(clientsMock[i]);
		}
	}

}


